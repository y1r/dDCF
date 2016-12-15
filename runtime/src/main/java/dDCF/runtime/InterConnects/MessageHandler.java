package dDCF.runtime.InterConnects;

import dDCF.lib.internal.Config;
import dDCF.runtime.Utils.Utils;

public class MessageHandler {
	public Message handle(Message msg) {

		Utils.debugPrint("Received" + msg.toString());

		switch (msg.messageType) {
			case EXECUTE_REQUEST: {
				Message reply = new Message();
				reply.messageType = MESSAGE_TYPE.EXECUTE_OFFER;
				reply.jarByteCode = Config.getInstance().jarByteCode;
			}
			break;

			case EXECUTE_OFFER:
				break;
		}

		return null;

	}

	private void getJarCache() {

	}
}
