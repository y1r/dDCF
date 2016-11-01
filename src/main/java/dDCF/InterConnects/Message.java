package dDCF.InterConnects;

public class Message {
	// ALL VARIABLES MUST BE 'PUBLIC'

	public String str;

	public Message() {
	}

	public Message(String string) {
		str = string;
	}

	@Override
	public String toString() {
		return str;
	}
}
