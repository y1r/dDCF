package dDCF.lib.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class CustomObjectInputStream extends ObjectInputStream {
	ClassLoader loader;

	CustomObjectInputStream(InputStream inputStream, ClassLoader l) throws IOException {
		super(inputStream);
		loader = l;
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
		return Class.forName(desc.getName(), false, loader);
	}
}
