package dDCF.lib.internal;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

public class Utils {
	public static void debugPrint(String msg) {
		Config cfg = Config.getInstance();

		if (cfg.isDebug) {
			Date date = new Date();

			System.out.println("[debug " + date.toString() + "]: " + msg);
		}
	}

	public static String getSizeOrNull(Collection<?> collection) {
		if (collection == null)
			return "null";
		else
			return Integer.toString(collection.size());
	}

	public static byte[] ReadFile(String name) throws IOException {
		return ReadInputStream(new FileInputStream(name));
	}

	public static byte[] ReadInputStream(InputStream stream) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		byte[] buffer = new byte[1000];

		int count;
		while ((count = stream.read(buffer)) > 0) {
			byteArrayOutputStream.write(buffer, 0, count);
		}

		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] getLocalHostAddress() {
		List<byte[]> inetAddressList = new ArrayList<>();

		Enumeration<NetworkInterface> networkInterfaces = null;
		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			e.printStackTrace();
			return null;
		}

		InetAddress master = Config.getInstance().host;

		for (NetworkInterface ni = networkInterfaces.nextElement(); networkInterfaces.hasMoreElements(); ) {
			// check if master is reachable from ni
			try {
				if (!master.isReachable(ni, 0, 0))
					continue;
			} catch (IOException e) {
				continue;
			}

			Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
			for (InetAddress inetAddress = inetAddresses.nextElement(); inetAddresses.hasMoreElements(); ) {
				// ipv4 is only ok( 32[bit] / 8[bit/byte] = 4[byte] )
				if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress().length == 4) {
					inetAddressList.add(inetAddress.getAddress());
				}
			}
		}

		assert inetAddressList.size() > 1;

		return inetAddressList.get(0);
	}
}
