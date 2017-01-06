package dDCF.lib.internal.InterConnects;

import dDCF.lib.internal.Config;
import dDCF.lib.internal.Pair;
import dDCF.lib.internal.Utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConnectionManager {
	// Singleton Pattern
	private static ConnectionManager _instance = null;
	Random rnd = new Random();
	double p;
	List<Pair<InetAddress, Integer>> nodeList = new ArrayList<>();

	private ConnectionManager() {
		// load probability configuration from Config
		p = Config.getInstance().connectProb;

		// add master
		try {
			nodeList.add(new Pair<>(InetAddress.getLocalHost(), Config.getInstance().localPort));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public static synchronized ConnectionManager getInstance() {
		if (_instance == null) {
			_instance = new ConnectionManager();
		}

		return _instance;
	}

	synchronized List<Pair<InetAddress, Integer>> registerNodeAndOffer(InetAddress addr, int port) {
		List<Pair<InetAddress, Integer>> remain = new ArrayList<>(nodeList);
		List<Pair<InetAddress, Integer>> offers = new ArrayList<>();

		// force inserting
		offers.add(remain.remove(rnd.nextInt(remain.size())));

		double p_mod = p - 1.0 / nodeList.size();

		// stochastic inserting
		for (Pair<InetAddress, Integer> cur : remain) {
			double pp = rnd.nextDouble();

			if (p == 1.0 || pp < p_mod) {
				offers.add(cur);
			}
		}

		nodeList.add(new Pair<>(addr, port));

		Utils.debugPrint("offers:");
		for (Pair<InetAddress, Integer> hoge : offers)
			Utils.debugPrint(hoge.toString());

		Utils.debugPrint("nodeList:");
		for (Pair<InetAddress, Integer> hoge : nodeList)
			Utils.debugPrint(hoge.toString());

		return offers;
	}
}
