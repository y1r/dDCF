package dDCF.runtime.InterConnects;

import dDCF.lib.internal.Pair;
import dDCF.runtime.Utils.Utils;

import java.net.InetAddress;
import java.util.List;

enum MESSAGE_TYPE {
	UNDEFINED,
	NODE_REQUEST,
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

	public MESSAGE_TYPE messageType;

	// NODE_OFFER
	public List<Pair<InetAddress, Integer>> nodesOffer;

	// EXECUTE_OFFER
	public byte[] jarByteCode;

	// JOB_OFFER
	// TODO

	// JOB_RECEIVED
	// TODO

	// JOB_DONE
	// TODO

	public Message() {
		messageType = MESSAGE_TYPE.UNDEFINED;
	}

	public Message(MESSAGE_TYPE type) {
		messageType = type;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("type: " + messageType.name() + "\n");

		switch (messageType) {
			case NODE_OFFER:
				stringBuilder.append("Offers: " + Utils.getSizeOrNull(nodesOffer) + "\n");
				break;

			case EXECUTE_OFFER:
				stringBuilder.append("Executes: " + jarByteCode.length);
				break;

			case JOB_OFFER:
				// TODO
				break;

			case JOB_RECEIVED:
				// TODO
				break;

			case JOB_DONE:
				// TODO
				break;
		}

		return stringBuilder.toString();
	}
}
