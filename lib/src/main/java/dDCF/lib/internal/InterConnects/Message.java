package dDCF.lib.internal.InterConnects;

import dDCF.lib.internal.Pair;
import dDCF.lib.internal.SerializedTask;
import dDCF.lib.internal.Utils;

import java.util.List;

enum MESSAGE_TYPE {
	UNDEFINED,
	NODE_REGISTER,
	NODE_OFFER,
	EXECUTE_REQUEST,
	EXECUTE_OFFER,
	JOB_REQUEST,
	JOB_OFFER,
	JOB_RECEIVED,
	JOB_DONE
}

public class Message {
	// ALL VARIABLES MUST BE 'PUBLIC'

	// common
	public MESSAGE_TYPE messageType;
	public long sequenceCode;

	// NODE_REGISTER
	public String serverAddr;
	public int serverPort;

	// NODE_OFFER
	public List<Pair<String, Integer>> nodesOffer;

	// EXECUTE_OFFER
	public byte[] jarByteCode;

	// JOB_OFFER
	public SerializedTask task;

	// JOB_RECEIVED

	// JOB_DONE
	// public Task task;


	public Message() {
		messageType = MESSAGE_TYPE.UNDEFINED;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("type: " + messageType.name() + "\n");

		switch (messageType) {
			case NODE_REGISTER:
				stringBuilder.append("server port to regist: " + serverPort + "\n");
				break;

			case NODE_OFFER:
				stringBuilder.append("Offers: " + Utils.getSizeOrNull(nodesOffer) + "\n");
				break;

			case EXECUTE_OFFER:
				stringBuilder.append("Executes: " + jarByteCode.length);
				break;

			case JOB_OFFER:
				stringBuilder.append("Task: " + ((task == null) ? "null" : "non-null"));
				break;

			case JOB_RECEIVED:
				break;

			case JOB_DONE:
				stringBuilder.append("Task: " + ((task == null) ? "null" : "non-null"));
				break;
		}

		return stringBuilder.toString();
	}
}
