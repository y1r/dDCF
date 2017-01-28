package dDCF.lib.internal.InterConnects;

import dDCF.lib.Task;
import dDCF.lib.TaskDeque;
import dDCF.lib.internal.Config;
import dDCF.lib.internal.SerializedTask;
import dDCF.lib.internal.Utils;

import java.util.concurrent.ConcurrentHashMap;

public class MessageHandler {
	ConcurrentHashMap<Long, Task> delegatedTasks = new ConcurrentHashMap<>();

	public Message handle(Message msg) {
//		Utils.debugPrint("Received" + msg.toString());

		Message reply = null;

		switch (msg.messageType) {
			case NODE_REGISTER: {
				// returns NODE_OFFER

				reply = MessageFactory.newMessage(MESSAGE_TYPE.NODE_OFFER, msg.sequenceCode + 1);

				Utils.debugPrint(() -> "serverIPv4Addr:" + msg.serverIPv4Addr);
				Utils.debugPrint(() -> "serverPort:" + msg.serverPort);

				reply.nodesOffer = ConnectionManager.getInstance().registerNodeAndOffer(msg.serverIPv4Addr, msg.serverPort);

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

				reply = MessageFactory.newMessage(MESSAGE_TYPE.JOB_OFFER, msg.sequenceCode + 1);

				Task stolen = TaskDeque.steal();

				reply.task = SerializedTask.serialize(stolen);

				if (reply.task != null) {
					// seqcode : REQ is msg.seq, OFFER is msg.seq + 1 therefore DONE is msg.seq + 2.
					delegatedTasks.put(msg.sequenceCode + 2, stolen);
				}

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
				// TODO: IS IT REQUIRE?

				break;
			}

			case JOB_DONE: {
				// returns nothing

				Task original = delegatedTasks.remove(msg.sequenceCode);
				Task delegated = SerializedTask.deserialize(msg.task);

				original.setResult(delegated.getResult());

				break;
			}
		}

//		Utils.debugPrint("return: " + reply.toString());

		return reply;
	}
}
