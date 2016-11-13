package dDCF.runtime.Utils;

import dDCF.lib.Work;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Reflection {
	public static Work getWork(String jarName) {
		List<String> classNames = getClassNamesFromJar(jarName);
		if (classNames == null) return null;

		URLClassLoader loader = null;
		try {
			File jar = new File(jarName);
			loader = new URLClassLoader(new URL[]{jar.toURI().toURL()});
		} catch (MalformedURLException e) {
			System.out.println("Converting jar-path to URL Error:" + e.toString());
		}

		Work work = null;
		for (String className : classNames) {
			try {
				Work test = (Work) loader.loadClass(className.substring(0, className.length() - 6).replace('/', '.')).newInstance();
				if (test != null) work = test;
			} catch (Exception e) {
			}
		}

		return work;
	}

	static List<String> getClassNamesFromJar(String jar) {
		JarFile jarFile = null;

		try {
			jarFile = new JarFile(jar);
		} catch (IOException e) {
			System.out.println("Opening Jar File Error:" + e.toString());
			return null;
		}

		List<String> classNames = new ArrayList<>();
		for (Enumeration e = jarFile.entries(); e.hasMoreElements(); ) {
			JarEntry entry = (JarEntry) e.nextElement();
			classNames.add(entry.getName());
		}

		return classNames;
	}
}
