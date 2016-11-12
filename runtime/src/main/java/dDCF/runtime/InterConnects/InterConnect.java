package dDCF.runtime.InterConnects;

import dDCF.runtime.Utils.Utils;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;

public class InterConnect {
	public DataInputStream dataInputStream;
	public DataOutputStream dataOutputStream;
	Thread thread;
	InputStream inputStream;
	OutputStream outputStream;
	LinkedBlockingDeque<Message> linkedBlockingDeque;
	boolean active;
	Serializer serializer = new Serializer();

	public InterConnect(Socket sock, LinkedBlockingDeque<Message> deque) throws IOException {
		inputStream = sock.getInputStream();
		outputStream = sock.getOutputStream();
		linkedBlockingDeque = deque;

		dataInputStream = new DataInputStream(inputStream);
		dataOutputStream = new DataOutputStream(outputStream);

		active = true;

		thread = new Thread(() ->
		{
			while (active) {
				try {
					Message msg = serializer.readMessageFromStream(dataInputStream);
					linkedBlockingDeque.add(msg);
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
}
