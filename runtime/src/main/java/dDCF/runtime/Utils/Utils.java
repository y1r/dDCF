package dDCF.runtime.Utils;

import dDCF.lib.internal.Config;

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
}
