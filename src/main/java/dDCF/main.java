package dDCF;

import dDCF.Utils.CmdLineParser;
import dDCF.Utils.Config;
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


        return;
	}
}
