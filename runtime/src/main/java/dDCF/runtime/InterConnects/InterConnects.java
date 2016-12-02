package dDCF.runtime.InterConnects;

import dDCF.lib.internal.Config;
import dDCF.runtime.Utils.Utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

public class InterConnects {
	LinkedBlockingDeque<Message> linkedBlockingDeque;
	Thread messageListener;
	boolean messageListenerWorking;

	List<InterConnect> interConnectList;

	ServerSocket receiver;
	Thread receiveThread;
	boolean receiverWorking;

	public InterConnects(Config cfg) throws IOException {
		linkedBlockingDeque = new LinkedBlockingDeque<>();
		interConnectList = new ArrayList<>();

		if (!cfg.isMaster) {
			appendAddress(cfg.host, cfg.remote_port);
		}

		startReceiver(cfg.local_port);
		startMessageListener();

		if (!cfg.isMaster) {
			Utils.debugPrint("SEND_START");
			Message msg = new Message();
			try {
				for (int i = 0; i < interConnectList.size(); i++)
					interConnectList.get(0).sendMessage(msg);
			} catch (Exception e) {
				System.out.println("write error" + e.toString());
			}
			Utils.debugPrint("SEND_DONE");
		}
	}

	private void startReceiver(int port) throws IOException {
		receiver = new ServerSocket(port);

		receiverWorking = true;
		receiveThread = new Thread(() ->
		{
			while (receiverWorking) {
				try {
					Utils.debugPrint("Accept Loop!");
					Socket socket = receiver.accept();
					Utils.debugPrint("Accepted[" + interConnectList.size() + "]:" + socket.getInetAddress().toString());
					appendSocket(socket);
				} catch (IOException e) {
				}
			}
		}
		);
		receiveThread.start();
	}

	private void stopReceiver() {
		receiverWorking = false;
	}

	private void appendAddress(InetAddress inetAddress, int port) throws IOException {
		Socket sock = new Socket(inetAddress, port);
		appendSocket(sock);
	}

	private void appendSocket(Socket sock) throws IOException {
		InterConnect interConnect = new InterConnect(sock, linkedBlockingDeque);
		interConnectList.add(interConnect);
	}

	private void startMessageListener() {
		messageListenerWorking = true;
		messageListener = new Thread(() ->
		{
			while (messageListenerWorking) {
				try {
					Utils.debugPrint("Listener Loop!");
					Message msg = linkedBlockingDeque.takeFirst();
					Utils.debugPrint("Listened:" + msg.toString());
				} catch (InterruptedException e) {
				}
			}
		}
		);
		messageListener.start();
	}

	private void stopMessageListener() {
		messageListenerWorking = false;
	}
}
