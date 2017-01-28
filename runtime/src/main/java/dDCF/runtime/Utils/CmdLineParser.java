package dDCF.runtime.Utils;

import dDCF.lib.internal.Config;
import dDCF.lib.internal.Pair;
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
		opts.addOption("p", "prob-of-cons", true, "Probability Of Connection (Default: 1.0)");

		// for worker mode
		opts.addOption("w", "worker", true, "Master (remote) address:port (Worker-Mode)");

		// common options
		opts.addOption("l", "local", true, "Local address:port");
		opts.addOption("t", "threads", true, "Number of available threads");
		opts.addOption("c", "custom-packet", false, "Use custom packet serializer");
		opts.addOption("h", "help", false, "Show usage");
		opts.addOption("d", "debug", false, "Debug mode");
	}

	public void showUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setOptionComparator(new Comparator<Option>() {
			// http://stackoverflow.com/questions/11741625/apache-commons-cli-ordering-help-options
			private static final String OPTS_ORDER = "mpwltchd"; // short option names

			@Override
			public int compare(Option o1, Option o2) {
				return OPTS_ORDER.indexOf(o1.getOpt()) - OPTS_ORDER.indexOf(o2.getOpt());
			}
		});
		formatter.printHelp("runtime.jar", opts);
	}

	public Config parse(String[] args) throws ParseException {
		Config cfg = Config.getInstance();
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(opts, args);

		if (cmd.hasOption('h')) {
			showUsage();
		}

		if (cmd.hasOption('m') == cmd.hasOption('w'))
			throw new ParseException("Select -m OR -w");

		// for master mode
		if (cmd.hasOption('m')) {
			cfg.isMaster = true;
			cfg.jarName = cmd.getOptionValue('m');
			cfg.connectProb = Double.parseDouble(cmd.getOptionValue('p', "1.0"));
		}

		// for worker mode
		if (cmd.hasOption('w')) {
			cfg.isMaster = false;
			Pair<InetAddress, Integer> remote = null;
			try {
				remote = parseAddress(cmd.getOptionValue('w'));
				cfg.remoteHost = remote.first;
				cfg.remotePort = remote.second;
			} catch (UnknownHostException e) {
				throw new ParseException("Check your master IP address");
			}
		}

		if (!cmd.hasOption("l"))
			throw new ParseException("-l: not found");
		Pair<InetAddress, Integer> local = null;
		try {
			local = parseAddress(cmd.getOptionValue('l'));
			cfg.localHost = local.first;
			cfg.localPort = local.second;
		} catch (UnknownHostException e) {
			throw new ParseException("Check your local IP address");
		}

		cfg.threads = Integer.parseInt(cmd.getOptionValue("t", Integer.toString(Runtime.getRuntime().availableProcessors())));
		cfg.isDebug = cmd.hasOption("d");
		cfg.usePacket = cmd.hasOption("c");

		Utils.debugPrint(cfg::toString);

		return cfg;
	}

	Pair<InetAddress, Integer> parseAddress(String addrStr) throws UnknownHostException {
		String[] splited = addrStr.split(":");
		InetAddress address = null;

		address = InetAddress.getByName(splited[0]);

		int port = Constants.PORT;
		if (splited.length >= 2)
			port = Integer.parseInt(splited[1]);

		return new Pair<>(address, port);
	}
}
