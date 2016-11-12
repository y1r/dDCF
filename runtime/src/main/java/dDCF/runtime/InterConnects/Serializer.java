package dDCF.runtime.InterConnects;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Serializer {
	private static ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());

	public Serializer() {
	}

	void writeMessageToStream(DataOutputStream stream, Message msg) throws IOException {
		byte[] packedMsg = objectMapper.writeValueAsBytes(msg);

		stream.writeInt(packedMsg.length);
		stream.write(packedMsg);
	}

	Message readMessageFromStream(DataInputStream stream) throws IOException {
		int msgLen = stream.readInt();
		byte[] packedMsg = new byte[msgLen];

		int ret;
		while ((ret = stream.read(packedMsg, packedMsg.length - msgLen, msgLen)) >= 0 && msgLen > 0) {
			if (ret == -1) throw new IOException();
			msgLen -= ret;
		}

		return objectMapper.readValue(packedMsg, Message.class);
	}
}
