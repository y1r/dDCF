package dDCF.runtime;

import dDCF.lib.Work;
import dDCF.lib.internal.Worker;
import dDCF.runtime.Utils.CmdLineParser;
import dDCF.runtime.Utils.Config;
import dDCF.runtime.Utils.Reflection;
import org.apache.commons.cli.ParseException;

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

		/*
		try {
			InterConnects cons = new InterConnects(cfg);
		} catch (IOException e) {
		}
		*/

		if (cfg.isMaster) {
			// TODO: read jar and execute

			Work work = Reflection.getWork(cfg.jarName);

			if (work == null) {
				System.out.println("Couldn't find valid class");
				return;
			} else {
				work.starter();

				Worker.startWorkers();

				work.ender();
			}
		} else {
			Worker.startWorkers();
		}

		return;
	}
}
