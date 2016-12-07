package dDCF.runtime;

import dDCF.lib.Work;
import dDCF.lib.internal.Config;
import dDCF.lib.internal.Worker;
import dDCF.runtime.Utils.CmdLineParser;
import dDCF.runtime.Utils.Reflection;
import dDCF.runtime.Utils.Utils;
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

		// cache jar file
		if (cfg.isMaster) {
			try {
				cfg.jarByteCode = Utils.ReadFile(cfg.jarName);
			} catch (IOException e) {
				System.out.println("Jar File Reading Error: " + e.getMessage());
			}
		}

		/*
		try {
			InterConnects cons = new InterConnects(cfg);
		} catch (IOException e) {
		}
		*/

		if (cfg.isMaster) {
			try {
				Object[] works = Reflection.getWork(cfg.jarByteCode).toArray();

				if (works.length != 0) {
					for (Object obj : works) {
						Work work = (Work) obj;
						work.starter();
						Worker.startWorkers();
						work.ender();
					}
				} else {
					System.out.println("Couldn't find valid work.");
				}
			} catch (IOException e) {
				System.out.println(e.toString());
			}
		} else {
			Worker.startWorkers();
		}

		return;
	}
}
