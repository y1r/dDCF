package dDCF.runtime;

import dDCF.lib.Work;
import dDCF.lib.internal.Worker;
import dDCF.runtime.InterConnects.InterConnects;
import dDCF.runtime.Utils.CmdLineParser;
import dDCF.runtime.Utils.Config;
import org.apache.commons.cli.ParseException;

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

public class main {
	public static void main(String[] args) {
		Config cfg = null;
		CmdLineParser parser = new CmdLineParser();

		try {
			cfg = parser.Parse(args);
		} catch (ParseException e) {
			System.out.println("Parse Error: " + e.getMessage());
			parser.showUsage();
			return;
		}

		try {
			InterConnects cons = new InterConnects(cfg);
		} catch (IOException e) {
		}

		if (cfg.isMaster) {
			// TODO: read jar and execute
			JarFile jarFile = null;
			try {
				jarFile = new JarFile(cfg.jarName);
			} catch (IOException e) {
				System.out.println(e.toString() + "jar error");
				return;
			}

			List<String> classNames = new ArrayList<>();
			for (Enumeration e = jarFile.entries(); e.hasMoreElements(); ) {
				JarEntry entry = (JarEntry) e.nextElement();
				classNames.add(entry.getName());
			}

			URLClassLoader loader = null;
			try {
				File jar = new File(cfg.jarName);
				loader = new URLClassLoader(new URL[]{jar.toURI().toURL()});
			} catch (MalformedURLException e) {
				System.out.println(e.toString() + "URL error");
			}

			Work work = null;
			for (String className : classNames) {
				try {
					Work test = (Work) loader.loadClass(className.substring(0, className.length() - 6).replace('/', '.')).newInstance();
					if (test != null) work = test;
				} catch (Exception e) {
				}
			}
			if (work == null) {
				System.out.println("Couldn't find valid class");
				return;
			} else {
				work.main();
			}
		} else {
			// # of Workers should be changed when this node supports SMP.
			Thread worker = Worker.getInfiniteWorker();
			worker.start();
		}

		return;
	}
}
