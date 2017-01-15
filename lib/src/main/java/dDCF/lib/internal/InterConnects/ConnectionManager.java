package dDCF.lib.internal.InterConnects;

import dDCF.lib.internal.Config;
import dDCF.lib.internal.Pair;
import dDCF.lib.internal.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConnectionManager {
	// Singleton Pattern
	private static ConnectionManager _instance = null;
	Random rnd = new Random();
	double p;
	List<Pair<byte[], Integer>> nodeList = new ArrayList<>();

	private ConnectionManager() {
		// load probability configuration from Config
		p = Config.getInstance().connectProb;

		// add master
		nodeList.add(new Pair<>(Utils.getLocalHostAddress(), Config.getInstance().localPort));
	}

	public static synchronized ConnectionManager getInstance() {
		if (_instance == null) {
			_instance = new ConnectionManager();
		}

		return _instance;
	}

	synchronized List<Pair<byte[], Integer>> registerNodeAndOffer(byte[] addr, int port) {
		List<Pair<byte[], Integer>> remain = new ArrayList<>(nodeList);
		List<Pair<byte[], Integer>> offers = new ArrayList<>();

		// force inserting
		offers.add(remain.remove(rnd.nextInt(remain.size())));

		double p_mod = p - 1.0 / nodeList.size();

		// stochastic inserting
		for (Pair<byte[], Integer> cur : remain) {
			double pp = rnd.nextDouble();

			if (p == 1.0 || pp < p_mod) {
				offers.add(cur);
			}
		}

		nodeList.add(new Pair<>(addr, port));

		Utils.debugPrint("offers:");
		for (Pair<byte[], Integer> hoge : offers)
			Utils.debugPrint(hoge.toString());

		Utils.debugPrint("nodeList:");
		for (Pair<byte[], Integer> hoge : nodeList)
			Utils.debugPrint(hoge.toString());

		return offers;
	}

	public int getConnectionCounts() {
		return nodeList.size();
	}
}
