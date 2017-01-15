package dDCF.lib.internal.InterConnects;

import com.fasterxml.jackson.databind.ObjectMapper;
import dDCF.lib.Task;
import dDCF.lib.internal.Config;
import dDCF.lib.internal.Pair;
import dDCF.lib.internal.SerializedTask;
import dDCF.lib.internal.Utils;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class InterConnect {
	static ObjectMapper objectMapper;
	public DataInputStream dataInputStream;
	public DataOutputStream dataOutputStream;
	Thread thread;
	InputStream inputStream;
	OutputStream outputStream;
	boolean active;
	MessageHandler messageHandler = new MessageHandler();

	ConcurrentHashMap<Long, Message> returnedMessages = new ConcurrentHashMap<>();
	ConcurrentHashMap<Long, CountDownLatch> waitingReplies = new ConcurrentHashMap<>();

	BlockingQueue<byte[]> sendQueue = new LinkedBlockingQueue<>();
	Thread sendThread;

	public InterConnect(Socket sock, boolean isBaseCon) throws IOException {
		inputStream = sock.getInputStream();
		outputStream = sock.getOutputStream();

		dataInputStream = new DataInputStream(inputStream);
		dataOutputStream = new DataOutputStream(outputStream);

		objectMapper = new ObjectMapper(new MessagePackFactory());
//		objectMapper.registerModule(new AfterburnerModule());

		// TODO: benchmarking
/*
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		objectMapper.configure(WRITE_ENUMS_USING_INDEX, true);
		*/


		active = true;

		thread = new Thread(() ->
		{
			while (active) {
				try {
					Message msg = readMessage();

					if (msg == null) continue;

					if (waitingReplies.containsKey(msg.sequenceCode)) {
						returnedMessages.put(msg.sequenceCode, msg);
						waitingReplies.remove(msg.sequenceCode).countDown();
						continue;
					}

					Message reply = messageHandler.handle(msg);
					if (reply != null)
						sendMessage(reply);

				} catch (IOException e) {
					e.printStackTrace();
					active = false;
				}
			}
		});
		thread.start();

		sendThread = new Thread(() ->
		{
			while (active) {
				try {
					byte[] packedMsg = sendQueue.take();

					dataOutputStream.writeInt(packedMsg.length);
					dataOutputStream.write(packedMsg);

				} catch (InterruptedException e) {
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		sendThread.start();
	}

	private void sendMessage(Message msg) throws IOException {
		while (true) {
			try {
				sendQueue.put(objectMapper.writeValueAsBytes(msg));
				Utils.debugPrint("send" + msg.toString());
				break;
			} catch (InterruptedException e) {
			}
		}
	}

	private Message readMessage() throws IOException {
		int msgLen = dataInputStream.readInt();
		byte[] packedMsg = new byte[msgLen];

		int ret;
		while ((ret = dataInputStream.read(packedMsg, packedMsg.length - msgLen, msgLen)) >= 0 && msgLen > 0) {
			if (ret == -1) throw new IOException();
			msgLen -= ret;
		}

		Utils.debugPrint("read" + packedMsg.toString());

		return objectMapper.readValue(packedMsg, Message.class);
	}

	private Message sendMessageAndWaitReply(Message msg) throws IOException {
		long seqCodeOfReply = msg.sequenceCode + 1;

		CountDownLatch countDownLatch = new CountDownLatch(1);
		waitingReplies.put(seqCodeOfReply, countDownLatch);
		sendMessage(msg);
		while (true) {
			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			if (returnedMessages.containsKey(seqCodeOfReply)) {
				return returnedMessages.remove(seqCodeOfReply);
			}
		}
		return null;
	}

	public byte[] getJarCode() {
		Message msg = MessageFactory.newMessage(MESSAGE_TYPE.EXECUTE_REQUEST);
		Message reply = null;

		try {
			reply = sendMessageAndWaitReply(msg);
		} catch (IOException e) {
		}

		if (reply == null) return null;

		return reply.jarByteCode;
	}

	public List<Pair<byte[], Integer>> registerNode() {
		Message msg = MessageFactory.newMessage(MESSAGE_TYPE.NODE_REGISTER);


		msg.serverPort = Config.getInstance().localPort;
		msg.serverIPv4Addr = Utils.getLocalHostAddress();

		Message reply = null;
		try {
			reply = sendMessageAndWaitReply(msg);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return reply.nodesOffer;
	}

	public Pair<Long, Task> stealTask() {
		Message msg = MessageFactory.newMessage(MESSAGE_TYPE.JOB_REQUEST);
		Message reply = null;

		try {
			reply = sendMessageAndWaitReply(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (reply == null) return null;

		if (reply.task == null) return null;

		return new Pair<>(reply.sequenceCode, SerializedTask.deserialize(reply.task));
	}

	public void returnTask(Pair<Long, Task> stolen) {
		Message msg = MessageFactory.newMessage(MESSAGE_TYPE.JOB_DONE, stolen.first + 1);
		msg.task = SerializedTask.serialize(stolen.second);

		try {
			sendMessage(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}
}
