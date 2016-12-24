package dDCF.lib.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class CustomObjectInputStream extends ObjectInputStream {
	ClassLoader loader;

	/*
	private CustomObjectInputStream() throws IOException {
		super();
	}
	*/

	CustomObjectInputStream(ClassLoader l) throws IOException {
		super();
		loader = l;
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
		return Class.forName(desc.getName(), false, loader);
	}
}
