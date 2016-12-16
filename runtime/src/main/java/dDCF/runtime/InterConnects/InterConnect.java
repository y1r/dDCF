package dDCF.runtime.InterConnects;

import dDCF.runtime.Utils.Utils;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class InterConnect {
	public DataInputStream dataInputStream;
	public DataOutputStream dataOutputStream;
	Thread thread;
	InputStream inputStream;
	OutputStream outputStream;
	boolean active;
	Serializer serializer = new Serializer();
	MessageHandler messageHandler = new MessageHandler();

	ConcurrentHashMap<Long, Message> returnedMessages = new ConcurrentHashMap<>();
	ConcurrentHashMap<Long, CountDownLatch> waitingReplies = new ConcurrentHashMap<>();

	public InterConnect(Socket sock) throws IOException {
		inputStream = sock.getInputStream();
		outputStream = sock.getOutputStream();

		dataInputStream = new DataInputStream(inputStream);
		dataOutputStream = new DataOutputStream(outputStream);

		active = true;

		thread = new Thread(() ->
		{
			while (active) {
				try {
					Message msg = serializer.readMessageFromStream(dataInputStream);

					if (waitingReplies.containsKey(msg.sequenceCode)) {
						returnedMessages.put(msg.sequenceCode, msg);
						waitingReplies.remove(msg.sequenceCode).countDown();
						continue;
					}

					Message reply = messageHandler.handle(msg);
					if (reply != null)
						serializer.writeMessageToStream(dataOutputStream, reply);

				} catch (IOException e) {
					active = false;
					Utils.debugPrint("Listener Stopped.");
				}
			}
		}
		);
		thread.start();
	}

	public void sendMessage(Message msg) throws IOException {
		serializer.writeMessageToStream(dataOutputStream, msg);
	}

	public Message sendMessageAndWaitReply(Message msg) throws IOException {
		long seqCodeOfReply = msg.sequenceCode + 1;

		CountDownLatch countDownLatch = new CountDownLatch(1);
		waitingReplies.put(seqCodeOfReply, countDownLatch);
		sendMessage(msg);
		while (true) {
			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				Utils.debugPrint("Interrupted Exception in sendMessageAndWaitReply." + e.getMessage());
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

		if (reply != null)
			return reply.jarByteCode;

		return null;
	}
}
