package dDCF.runtime.Utils;

import dDCF.lib.Work;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

public class Reflection {
	public static Stream<Work> getWork(String jarName) throws IOException {
		FileInputStream fileInputStream;

		fileInputStream = new FileInputStream(jarName);

		JarByteClassLoader jarByteClassLoader = new JarByteClassLoader(fileInputStream);
		Stream<String> stringStream = jarByteClassLoader.ClassNameStream();

		Stream<String> works = stringStream.filter(s ->
				{
					Class testClass = jarByteClassLoader.findClass(s);
					Class[] interfaces = testClass.getInterfaces();

					return Arrays.stream(interfaces).anyMatch(cls -> cls == Work.class);
				}
		);

		return works.map(str ->
				{
					Work workInstance = null;
					try {
						Class work = jarByteClassLoader.findClass(str);
						workInstance = (Work) work.newInstance();
					} catch (Exception e) {
						System.out.println(e.toString());
					}
					return workInstance;
				}
		);
	}
}
