package dDCF.lib.internal.InterConnects;

public enum MESSAGE_TYPE {
	UNDEFINED,
	NODE_REGISTER,
	NODE_OFFER,
	EXECUTE_REQUEST,
	EXECUTE_OFFER,
	JOB_REQUEST,
	JOB_OFFER,
	JOB_RECEIVED,
	JOB_DONE;

	public static MESSAGE_TYPE fromOrd(byte ord) {
		return values()[ord];
	}

	public byte toOrd() {
		return (byte) ordinal();
	}
}