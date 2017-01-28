package dDCF.lib.internal;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.function.Supplier;

public class Utils {
	public static void debugPrint(Supplier<String> msg) {
		Config cfg = Config.getInstance();

		if (cfg.isDebug) {
			Date date = new Date();

			System.out.println("[debug " + date.toString() + "]: " + msg.get());
		}
	}

	public static String getSizeOrNull(Collection<?> collection) {
		if (collection == null)
			return "null";
		else
			return Integer.toString(collection.size());
	}

	public static byte[] readFile(String name) throws IOException {
		return readInputStream(new FileInputStream(name));
	}

	public static byte[] readInputStream(InputStream stream) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		byte[] buffer = new byte[1000];

		int count;
		while ((count = stream.read(buffer)) > 0) {
			byteArrayOutputStream.write(buffer, 0, count);
		}

		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] readInputStream(InputStream stream, int len) throws IOException {
		byte[] buffer = new byte[len];

		int ret;
		while ((ret = stream.read(buffer, buffer.length - len, len)) >= 0 && len > 0) {
			if (ret == -1) throw new IOException();
			len -= ret;
		}

		return buffer;
	}
}
