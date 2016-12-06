package dDCF.runtime.InterConnects;

import dDCF.lib.internal.Config;
import dDCF.runtime.Utils.Utils;

public class MessageHandler {

	byte[] jarCache = null;

	public void handle(Message msg) {

		Utils.debugPrint("Received" + msg.toString());

		switch (msg.messageType) {
			case EXECUTE_REQUEST: {
				if (Config.getInstance().isMaster) {
					if (jarCache == null) {
						getJarCache();
					}

				} else {

				}
			}
			break;

			case EXECUTE_OFFER:
				break;
		}

	}

	private void getJarCache() {

	}
}
