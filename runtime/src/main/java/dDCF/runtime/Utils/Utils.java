package dDCF.runtime.Utils;

import dDCF.lib.internal.Config;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

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
}
