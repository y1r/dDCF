package dDCF.runtime.Utils;

import dDCF.lib.internal.Config;
import dDCF.lib.internal.Utils;
import org.apache.commons.cli.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Comparator;

public class CmdLineParser {
	Options opts;

	public CmdLineParser() {
		opts = new Options();

		// for master mode
		opts.addOption("m", "master", true, "Task jar file (Master-Mode)");

		// for worker mode
		opts.addOption("w", "worker", true, "Master (remote) address (Worker-Mode)");
		opts.addOption("r", "remote-port", true, "Master (remote) port number");

		// common options
		opts.addOption("l", "local-port", true, "Local port number");
		opts.addOption("t", "threads", true, "Number of available threads");
		opts.addOption("h", "help", false, "Show usage");
		opts.addOption("d", "debug", false, "Debug mode");
	}

	public void showUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setOptionComparator(new Comparator<Option>() {
			// http://stackoverflow.com/questions/11741625/apache-commons-cli-ordering-help-options
			private static final String OPTS_ORDER = "mwrlthd"; // short option names

			@Override
			public int compare(Option o1, Option o2) {
				return OPTS_ORDER.indexOf(o1.getOpt()) - OPTS_ORDER.indexOf(o2.getOpt());
			}
		});
		formatter.printHelp("runtime.jar", opts);
	}

	public Config Parse(String[] args) throws ParseException {
		Config cfg = Config.getInstance();
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(opts, args);

		if (cmd.hasOption('h')) {
			showUsage();
		}

		if (cmd.hasOption('m') && cmd.hasOption('w'))
			throw new ParseException("Select -m OR -w");

		// for master mode
		if (cmd.hasOption('m')) {
			cfg.isMaster = true;
			cfg.jarName = cmd.getOptionValue('m');
		}
		// for worker mode
		else if (cmd.hasOption('w')) {
			cfg.isMaster = false;
			String ipaddr = cmd.getOptionValue('w');
			try {
				cfg.host = InetAddress.getByName(ipaddr);
			} catch (UnknownHostException e) {
				throw new ParseException("Check your master IP address");
			}
		} else {
			throw new ParseException("Select -m OR -w");
		}

		if (!cfg.isMaster) {
			cfg.remote_port = Integer.parseInt(cmd.getOptionValue("r", Integer.toString(Constants.PORT)));
		}

		cfg.local_port = Integer.parseInt(cmd.getOptionValue("l", Integer.toString(Constants.PORT)));
		cfg.threads = Integer.parseInt(cmd.getOptionValue("t", Integer.toString(Runtime.getRuntime().availableProcessors())));
		cfg.isDebug = cmd.hasOption("d");

		Utils.debugPrint(cfg.toString());

		return cfg;
	}
}
