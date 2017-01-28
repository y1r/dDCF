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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class InterConnect {
	static ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
	static MessageSerializer messageSerializer = new MessageSerializer();
	public DataInputStream dataInputStream;
	public DataOutputStream dataOutputStream;
	Thread thread;
	InputStream inputStream;
	OutputStream outputStream;
	boolean active;
	MessageHandler messageHandler = new MessageHandler();

	ConcurrentHashMap<Long, Message> returnedMessages = new ConcurrentHashMap<>();
	ConcurrentHashMap<Long, CountDownLatch> waitingReplies = new ConcurrentHashMap<>();

	public InterConnect(Socket sock) throws IOException {
		inputStream = sock.getInputStream();
		outputStream = sock.getOutputStream();
		dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));
		dataOutputStream = new DataOutputStream(new BufferedOutputStream(outputStream));

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
	}

	private void sendMessage(Message msg) throws IOException {
		byte[] buf = null;

		if (Config.getInstance().usePacket)
			buf = messageSerializer.serialize(msg);
		else
			buf = objectMapper.writeValueAsBytes(msg);

		while (true) {
			try {
				Utils.debugPrint(() -> "send" + msg.toString());
				synchronized (this) {
					dataOutputStream.writeInt(buf.length);
					dataOutputStream.write(buf);
				}
				dataOutputStream.flush();
				Utils.debugPrint(() -> "send" + msg.toString());
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Message readMessage() throws IOException {
		int msgLen = dataInputStream.readInt();
		Utils.debugPrint(() -> "read" + msgLen);
		byte[] packedMsg = Utils.readInputStream(dataInputStream, msgLen);

		Utils.debugPrint(() -> "read" + packedMsg.toString());

		if (Config.getInstance().usePacket) {
			return messageSerializer.deserialize(packedMsg);
		} else {
			return objectMapper.readValue(packedMsg, Message.class);
		}
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

		Utils.debugPrint(() -> "regist");
		msg.serverPort = Config.getInstance().localPort;
		msg.serverIPv4Addr = Config.getInstance().localHost.getAddress();
		Utils.debugPrint(() -> "registed");

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
