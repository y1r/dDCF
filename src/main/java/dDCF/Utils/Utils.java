package dDCF.Utils;

import java.util.Date;

public class Utils {
	static void debugPrint(String msg) {
		Config cfg = Config.getInstance();

		if (cfg.isDebug) {
			Date date = new Date();

			System.out.println("[debug " + date.toString() + "]: " + msg);
		}
	}
}
