package dDCF.runtime.InterConnects;

import java.util.Random;

public class MessageFactory {
	static Random random = new Random();

	public static Message newMessage(MESSAGE_TYPE type) {
		Message msg = new Message();

		msg.messageType = type;
		msg.sequenceCode = random.nextLong();

		return msg;
	}

	public static Message newMessage(MESSAGE_TYPE type, long nextSeqCode) {
		Message msg = new Message();

		msg.messageType = type;
		msg.sequenceCode = nextSeqCode;

		return msg;
	}
}
