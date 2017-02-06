package dDCF.lib.internal;

import java.net.InetAddress;

public class Config {
	private static Config _instance;

	// Master Configuration
	public boolean isMaster;
	public String jarName = null;
	public double connectProb;

	// Worker Configuration
	public InetAddress remoteHost;
	public int remotePort;

	// Common
	public byte[] jarByteCode = null;
	public InetAddress localHost;
	public int localPort;
	public boolean isDebug;
	public int threads;
	public double stealProb;

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
			stringBuilder.append("remoteHost:" + remoteHost.toString() + " ");
			stringBuilder.append("remotePort:" + remotePort + " ");
		}

		stringBuilder.append("localHost:" + localHost.toString() + " ");
		stringBuilder.append("localPort:" + localPort + " ");
		stringBuilder.append("threads:" + threads + " ");
		stringBuilder.append("isDebug:" + isDebug + " ");
		stringBuilder.append("stealProb:" + stealProb + " ");

		return stringBuilder.toString();
	}
}