package dDCF.Utils;

import org.apache.commons.cli.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CmdLineParser {
	public static Config Parse(String[] args) throws ParseException {
		Options opts = new Options();

		// for master mode
		opts.addOption("m", "master", false, "Set this option when you use this computer as a master-node.");
		opts.addOption("j", "job", true, "Select your job jar file.");

		// for worker mode
		opts.addOption("w", "worker", false, "Set this option when you use this computer as a worker-node.");
		opts.addOption("h", "host", false, "Select your master IP addr.");

		// common options
		opts.addOption("p", "port", true, "Set this option when you use user-defined TCP port number.");

		Config cfg = new Config();
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(opts, args);

		// for master mode
		if (cmd.hasOption('m')) {
			cfg.isMaster = true;
			if (cmd.hasOption('j')) {
				cfg.jarName = cmd.getOptionValue('j');
			} else {
				throw new ParseException("You must select your job jar file.");
			}
		}
		// for worker mode
		else if (cmd.hasOption('w')) {
			cfg.isMaster = false;
			if (cmd.hasOption('h')) {
				String ipaddr = cmd.getOptionValue('h');
				try {
					InetAddress addr = InetAddress.getByName(ipaddr);
				} catch (UnknownHostException e) {
					throw new ParseException("Please check your master IP addr.");
				}
			} else {
				throw new ParseException("Select your master IP addr.");
			}
		} else {
			throw new ParseException("You must select master or worker mode.");
		}

		if (cmd.hasOption('p')) {
			cfg.port = Integer.parseInt(cmd.getOptionValue('p', Integer.toString(Constants.PORT)));
		}

		return cfg;
	}
}
