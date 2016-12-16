package dDCF.runtime.InterConnects;

import dDCF.lib.internal.Config;
import dDCF.runtime.Utils.Utils;

public class MessageHandler {
	public Message handle(Message msg) {
		Utils.debugPrint("Received" + msg.toString());

		Message reply = null;

		switch (msg.messageType) {
			case NODE_REQUEST: {
				// returns NODE_OFFER
				// TODO: implement

				break;
			}

			case NODE_OFFER: {
				// DO NOT implement here.
				// NODE_OFFER must deal in caller. (sendMessageAndWaitReply)

				break;
			}

			case EXECUTE_REQUEST: {
				// returns EXECUTE_OFFER

				reply = MessageFactory.newMessage(MESSAGE_TYPE.EXECUTE_OFFER, msg.sequenceCode + 1);
				reply.jarByteCode = Config.getInstance().jarByteCode;

				break;
			}

			case EXECUTE_OFFER: {
				// DO NOT implement here.
				// EXECUTE_OFFER must deal in caller. (sendMessageAndWaitReply)

				break;
			}

			case JOB_REQUEST: {
				// returns JOB_OFFER
				// TODO: implement

				break;
			}

			case JOB_OFFER: {
				// DO NOT implement here.
				// JOB_OFFER must deal in caller. (sendMessageAndWaitReply)

				break;
			}

			case JOB_RECEIVED: {
				// returns nothing
				// TODO: implement

				break;
			}

			case JOB_DONE: {
				// returns nothing
				// TODO: implement

				break;
			}

		}

		return reply;
	}
}
