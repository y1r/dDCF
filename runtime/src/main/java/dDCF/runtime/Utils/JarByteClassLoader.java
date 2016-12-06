package dDCF.runtime.Utils;

import dDCF.lib.internal.Pair;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Stream;

public class JarByteClassLoader extends ClassLoader {
	public byte[] jarByte;
	public Map<String, Pair<Class, byte[]>> byteCodeClasses = new HashMap<>();

	public JarByteClassLoader(InputStream inputStream) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		byte[] buffer = new byte[1000];

		int count;
		while ((count = inputStream.read(buffer)) > 0) {
			byteArrayOutputStream.write(buffer, 0, count);
		}

		jarByte = byteArrayOutputStream.toByteArray();
		LoadClassesToMap(jarByte);
	}

	public JarByteClassLoader(byte[] b) throws IOException {
		jarByte = b;
		LoadClassesToMap(jarByte);
	}

	private void LoadClassesToMap(byte[] b) throws IOException {
		JarInputStream jarInputStream = new JarInputStream(new ByteArrayInputStream(b));
		JarEntry entry;
		while ((entry = jarInputStream.getNextJarEntry()) != null) {
			if (entry.getName().endsWith(".class")) {
				byte buffer[] = new byte[1000];
				int count;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				while ((count = jarInputStream.read(buffer, 0, buffer.length)) > 0) {
					byteArrayOutputStream.write(buffer, 0, count);
				}
				byteCodeClasses.put(entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.'), new Pair<>(null, byteArrayOutputStream.toByteArray()));
			}
		}
	}

	public Stream<String> ClassNameStream() {
		return byteCodeClasses.keySet().stream();
	}

	public Class findClass(String name) {
		Pair<Class, byte[]> classes = byteCodeClasses.get(name);

		// check system loader
		Class loaded = null;
		try {
			loaded = findSystemClass(name);
		} catch (ClassNotFoundException e) {
		}
		if (loaded != null) return loaded;

		// check already name is already loaded by this class
		if (classes.first != null) return classes.first;

		// define class
		classes.first = defineClass(name, classes.second, 0, classes.second.length);

		return classes.first;
	}
}
