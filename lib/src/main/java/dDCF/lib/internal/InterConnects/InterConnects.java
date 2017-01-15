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
	InterConnect master;

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

		startAccepter(cfg.localPort);

		if (cfg.isMaster)
			master = null;
		else {
			// is worker
			registerMaster(cfg.host, cfg.remotePort);
			List<Pair<String, Integer>> offers = master.registerNode();
			for (Pair<String, Integer> cur : offers) {
				Utils.debugPrint("connect to " + cur.toString());
				appendAddress(InetAddress.getByName(cur.first), cur.second);
			}
		}
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
		InterConnect interConnect = new InterConnect(sock, false);
		interConnectList.add(interConnect);
	}

	private void registerMaster(InetAddress inetAddress, int port) throws IOException {
		Socket sock = new Socket(inetAddress, port);
		master = new InterConnect(sock, true);
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

	public byte[] getJarCode() {
		return master.getJarCode();
	}
}
