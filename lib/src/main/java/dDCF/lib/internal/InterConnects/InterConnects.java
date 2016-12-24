package dDCF.lib.internal.InterConnects;

import dDCF.lib.Task;
import dDCF.lib.internal.Config;
import dDCF.lib.internal.Pair;
import dDCF.lib.internal.Utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class InterConnects {
	List<InterConnect> interConnectList;

	// accepter : accepter is an accept loop, it handles NEW connection.
	ServerSocket accepter;
	Thread acceptThread;
	boolean accepterWorking;

	// steal
	Random rnd = new Random();
	ConcurrentHashMap<Long, Pair<Integer, Long>> taskKeyToInterConnectKeyPair = new ConcurrentHashMap<>();

	public InterConnects(Config cfg) throws IOException {
		interConnectList = new ArrayList<>();

		if (!cfg.isMaster) {
			appendAddress(cfg.host, cfg.remote_port);
		}

		startAccepter(cfg.local_port);

		/* send test message */
		/*
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
		*/
	}

	private void startAccepter(int port) throws IOException {
		accepter = new ServerSocket(port);

		accepterWorking = true;
		acceptThread = new Thread(() ->
		{
			while (accepterWorking) {
				try {
					Utils.debugPrint("Accept Loop!");
					Socket socket = accepter.accept();
					Utils.debugPrint("Accepted[" + interConnectList.size() + "]:" + socket.getInetAddress().toString());
					appendSocket(socket);
				} catch (IOException e) {
				}
			}
		}
		);
		acceptThread.start();
	}

	private void stopAccepter() {
		accepterWorking = false;
	}

	private void appendAddress(InetAddress inetAddress, int port) throws IOException {
		Socket sock = new Socket(inetAddress, port);
		appendSocket(sock);
	}

	private void appendSocket(Socket sock) throws IOException {
		InterConnect interConnect = new InterConnect(sock);
		interConnectList.add(interConnect);
	}

	public byte[] getJarCode() {
		for (InterConnect interConnect : interConnectList) {
			byte[] byteCode = interConnect.getJarCode();

			if (byteCode != null)
				return byteCode;
		}

		return null;
	}

	public Pair<Long, Task> stealTask() {
//		Utils.debugPrint("stealTask");
		for (int i = 0; i < interConnectList.size(); i++) {
			Pair<Long, Task> taskPair = interConnectList.get(i).stealTask();
			if (taskPair == null) continue;

			long key = rnd.nextLong();

			taskKeyToInterConnectKeyPair.put(key, new Pair<>(i, taskPair.first));

			taskPair.first = key;

//			Utils.debugPrint("return");
			return taskPair;
		}
//		Utils.debugPrint("null");

		return null;
	}

	public void returnTask(Pair<Long, Task> task) {
		Pair<Integer, Long> keyPair = taskKeyToInterConnectKeyPair.remove(task.first);

		interConnectList.get(keyPair.first).returnTask(new Pair<>(keyPair.second, task.second));
	}

}