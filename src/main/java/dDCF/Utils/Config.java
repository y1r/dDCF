package dDCF.Utils;

import java.net.InetAddress;

public class Config {
	private static Config _instance;
	boolean isMaster;
	String jarName = null;
	InetAddress host;
	int remote_port;
	int local_port;
	boolean isDebug;

	private Config() {
	}

	public static Config getInstance() {
		if (_instance == null) _instance = new Config();

		return _instance;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		if (isMaster) {
			stringBuilder.append("Master-mode ");
			stringBuilder.append("jarName:" + jarName + " ");
		} else {
			stringBuilder.append("Worker-mode ");
			stringBuilder.append("host:" + host.toString() + " ");
		}

		stringBuilder.append("remote_port:" + remote_port + " ");
		stringBuilder.append("local_port:" + local_port + " ");
		stringBuilder.append("isDebug:" + isDebug);

		return stringBuilder.toString();
	}
}