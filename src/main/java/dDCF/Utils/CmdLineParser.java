package dDCF.Utils;

import org.apache.commons.cli.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CmdLineParser {
	Options opts;

	public CmdLineParser() {
		opts = new Options();

		// for master mode
		opts.addOption("m", "master", true, "Use this node as a master. Select your job jar file.");

		// for worker mode
		opts.addOption("w", "worker", true, "Use this as a worker. Select your master address.");
		opts.addOption("r", "remote-port", true, "Select your remote(master) port number.");

		// common options
		opts.addOption("l", "local-port", true, "Select your local port number.");
		opts.addOption("h", "help", false, "Show usage.");
		opts.addOption("d", "debug", false, "Debug mode");
	}

	public void showUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("dDCF.jar", opts);
	}

	public Config Parse(String[] args) throws ParseException {
		Config cfg = Config.getInstance();
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(opts, args);

		if (cmd.hasOption('h')) {
			showUsage();
		}

		if (cmd.hasOption('m') && cmd.hasOption('w'))
			throw new ParseException("You must select master-mode OR worker-mode.");

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
				throw new ParseException("Please check your master IP address/port.");
			}
		} else {
			throw new ParseException("You must select master or worker mode.");
		}

		if (!cfg.isMaster) {
			cfg.remote_port = Integer.parseInt(cmd.getOptionValue("r", Integer.toString(Constants.PORT)));
		}

		cfg.local_port = Integer.parseInt(cmd.getOptionValue("l", Integer.toString(Constants.PORT)));
		cfg.isDebug = cmd.hasOption("d");

		Utils.debugPrint(cfg.toString());

		return cfg;
	}
}
