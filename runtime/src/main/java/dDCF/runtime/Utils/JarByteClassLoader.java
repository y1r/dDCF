package dDCF.runtime.Utils;

import dDCF.lib.internal.Pair;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Stream;

public class JarByteClassLoader extends ClassLoader {
	public byte[] jarByte;
	public Map<String, Pair<Class, byte[]>> byteCodeClasses = new HashMap<>();

	public JarByteClassLoader(byte[] b) throws IOException {
		jarByte = b;
		LoadClassesToMap(jarByte);
	}

	private void LoadClassesToMap(byte[] b) throws IOException {
		JarInputStream jarInputStream = new JarInputStream(new ByteArrayInputStream(b));
		JarEntry entry;
		while ((entry = jarInputStream.getNextJarEntry()) != null) {
			if (entry.getName().endsWith(".class")) {
				byteCodeClasses.put(entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.'), new Pair<>(null, Utils.ReadInputStream(jarInputStream)));
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

		// check already "name".class is already loaded by this loader
		if (classes.first != null) return classes.first;

		// define class
		classes.first = defineClass(name, classes.second, 0, classes.second.length);

		return classes.first;
	}
}
