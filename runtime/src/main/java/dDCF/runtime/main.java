package dDCF.runtime;

import dDCF.lib.Work;
import dDCF.lib.internal.Config;
import dDCF.lib.internal.InterConnects.InterConnects;
import dDCF.lib.internal.JarByteClassLoader;
import dDCF.lib.internal.Utils;
import dDCF.lib.internal.Worker;
import dDCF.runtime.Utils.CmdLineParser;
import dDCF.runtime.Utils.Reflection;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

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

		// cache jar file (master)
		if (cfg.isMaster) {
			try {
				cfg.jarByteCode = Utils.ReadFile(cfg.jarName);
			} catch (IOException e) {
				System.out.println("Jar File Reading Error: " + e.getMessage());
			}
		}


		InterConnects cons;
		try {
			cons = new InterConnects(cfg);
		} catch (IOException e) {
			Utils.debugPrint("InterConnects IOException." + e.getMessage());
			return;
		}

		// master's work
		if (cfg.isMaster) {
			try {
				Object[] works = Reflection.getWork(cfg.jarByteCode).toArray();

				if (works.length != 0) {
					for (Object obj : works) {
						Work work = (Work) obj;
						work.starter();
						Worker.startWorkers(cons);
						work.ender();
					}
				} else {
					System.out.println("Couldn't find valid work.");
				}

			} catch (IOException e) {
				System.out.println(e.toString());
			}
		}

		// worker's work
		if (!cfg.isMaster) {
			byte[] jarCode = cons.getJarCode();
			cfg.jarByteCode = jarCode;

			if (jarCode == null) System.out.println("failed getJarCode");
			else System.out.println("jarCode:" + jarCode.length);
			try {
				JarByteClassLoader.loadJarFile(jarCode);
			} catch (IOException e) {
				Utils.debugPrint(e.toString());
			}

			Worker.startWorkers(cons);
		}

		return;
	}
}
