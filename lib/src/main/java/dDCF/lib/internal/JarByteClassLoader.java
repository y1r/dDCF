package dDCF.lib.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Stream;

public class JarByteClassLoader extends ClassLoader {
	private static JarByteClassLoader loader = null;
	public byte[] jarByte;
	public Map<String, Pair<Class, byte[]>> byteCodeClasses = new HashMap<>();

	private JarByteClassLoader() {
	}

	public static void loadJarFile(byte[] b) throws IOException {
		if (loader == null) {
			loader = new JarByteClassLoader();
			loader.jarByte = b;
			loader.LoadClassesToMap(loader.jarByte);
		}
	}

	public static JarByteClassLoader getInstance() {
		return loader;
	}

	private void LoadClassesToMap(byte[] b) throws IOException {
		JarInputStream jarInputStream = new JarInputStream(new ByteArrayInputStream(b));
		JarEntry entry;
		while ((entry = jarInputStream.getNextJarEntry()) != null) {
			// exclude directories
			if (entry.getName().endsWith(".class")) {
				// convert foo/bar/sample.class to foo.bar.sample
				byteCodeClasses.put(entry.getName().substring(0, entry.getName().length() - 6).replace('/', '.'), new Pair<>(null, Utils.readInputStream(jarInputStream)));
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

		resolveClass(classes.first);

		return classes.first;
	}
}
