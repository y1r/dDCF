package dDCF.lib.internal;

import java.net.InetAddress;

public class Config {
	private static Config _instance;

	// Master Configuration
	public boolean isMaster;
	public String jarName = null;
	public double connectProb;

	// Worker Configuration
	public InetAddress host;
	public int remotePort;

	// Common
	public byte[] jarByteCode = null;
	public int localPort;
	public boolean isDebug;
	public int threads;

	private Config() {
	}

	public static synchronized Config getInstance() {
		if (_instance == null) _instance = new Config();

		return _instance;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		if (isMaster) {
			stringBuilder.append("Master-mode ");
			stringBuilder.append("jarName:" + jarName + " ");
			stringBuilder.append("connect-probability:" + connectProb + " ");
		} else {
			stringBuilder.append("Worker-mode ");
			stringBuilder.append("remoteHost:" + host.toString() + " ");
			stringBuilder.append("remotePort:" + remotePort + " ");
		}

		stringBuilder.append("localPort:" + localPort + " ");
		stringBuilder.append("threads:" + threads + " ");
		stringBuilder.append("isDebug:" + isDebug);

		return stringBuilder.toString();
	}
}