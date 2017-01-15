package dDCF.lib.internal.InterConnects;

import dDCF.lib.internal.Pair;
import dDCF.lib.internal.SerializedTask;
import dDCF.lib.internal.Utils;

import java.io.*;
import java.util.ArrayList;

/* ***** PACKET-ORDER ***** */
/*
	// common
	public MESSAGE_TYPE messageType;
	public long sequenceCode;

	// NODE_REGISTER
	public byte[] serverIPv4Addr;
	public int serverPort;

	// NODE_OFFER
	public List<Pair<byte[], Integer>> nodesOffer;

	// EXECUTE_OFFER
	public byte[] jarByteCode;

	// JOB_OFFER, JOB_DONE
	public SerializedTask task;
*/

public class MessageSerializer {
	public byte[] serialize(Message msg) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteArrayOutputStream);

		// public MESSAGE_TYPE messageType;
		stream.writeByte(msg.messageType.toOrd());

		// public long sequenceCode;
		stream.writeLong(msg.sequenceCode);

		// public byte[] serverIPv4Addr;
		writeByteArray(stream, msg.serverIPv4Addr);

		// public int serverPort;
		stream.writeInt(msg.serverPort);

		// public List<Pair<byte[], Integer>> nodesOffer;
		stream.writeInt((msg.nodesOffer == null) ? 0 : msg.nodesOffer.size());
		if (msg.nodesOffer != null) {
			for (Pair<byte[], Integer> node : msg.nodesOffer) {
				writeByteArray(stream, node.first);
				stream.writeInt(node.second);
			}
		}

		// public byte[] jarByteCode;
		writeByteArray(stream, msg.jarByteCode);

		// public SerializedTask task;
		// function, input, result, ended
		stream.writeBoolean(msg.task != null);
		if (msg.task != null) {
			writeByteArray(stream, msg.task.function);
			writeByteArray(stream, msg.task.input);
			writeByteArray(stream, msg.task.result);
			stream.writeBoolean(msg.task.ended);
		}
		stream.flush();

		return byteArrayOutputStream.toByteArray();
	}

	public Message deserialize(byte[] byteArray) throws IOException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
		DataInputStream stream = new DataInputStream(byteArrayInputStream);
		Message msg = new Message();

		// public MESSAGE_TYPE messageType;
		msg.messageType = MESSAGE_TYPE.fromOrd(stream.readByte());

		// public long sequenceCode;
		msg.sequenceCode = stream.readLong();

		// public byte[] serverIPv4Addr;
		msg.serverIPv4Addr = readByteArray(stream);

		// public int serverPort;
		msg.serverPort = stream.readInt();

		// public List<Pair<byte[], Integer>> nodesOffer;
		int nodesOfferLen = stream.readInt();
		msg.nodesOffer = new ArrayList<>();
		for (int i = 0; i < nodesOfferLen; i++)
			msg.nodesOffer.add(new Pair<>(
					readByteArray(stream), stream.readInt())
			);

		// public byte[] jarByteCode;
		msg.jarByteCode = readByteArray(stream);

		// public SerializedTask task;
		// function, input, result, ended
		if (stream.readBoolean()) {
			msg.task = new SerializedTask();
			msg.task.function = readByteArray(stream);
			msg.task.input = readByteArray(stream);
			msg.task.result = readByteArray(stream);
			msg.task.ended = stream.readBoolean();
		}

		return msg;
	}

	void writeByteArray(DataOutputStream stream, byte[] input) throws IOException {
		stream.writeInt(input == null ? 0 : input.length);
		if (input != null) {
			stream.write(input);
		}
	}

	byte[] readByteArray(DataInputStream stream) throws IOException {
		int len = stream.readInt();
		if (len == 0) return null;

		return Utils.readInputStream(stream, len);
	}
}
