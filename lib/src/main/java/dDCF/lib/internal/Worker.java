package dDCF.lib.internal;

import dDCF.lib.Task;
import dDCF.lib.TaskDeque;
import dDCF.lib.internal.InterConnects.InterConnects;

import java.util.Random;

public class Worker extends Thread {
	private static InterConnects interConnects;
	private static Thread[] workers;
	private static Random rnd = new Random();
	private static double stealP;

	public Worker() {
	}

	public static Thread getInfiniteWorker() {
		Thread th = new Thread(() ->
		{
			// identify by Thread-Id
			TaskDeque taskDeque = new TaskDeque();
			workInfinitely();
		});

		return th;
	}

	public static void startWorkers(InterConnects ics) {
		interConnects = ics;
		stealP = Config.getInstance().stealProb;

		int node = Config.getInstance().threads;
		System.out.println("threads:" + node);
		workers = new Thread[node];

		TaskDeque.registerWantedWorkers(node);
		for (int i = 0; i < workers.length; i++) {
			workers[i] = Worker.getInfiniteWorker();
			workers[i].start();
		}

		for (int i = 0; i < workers.length; i++) {
			try {
				workers[i].join();
			} catch (InterruptedException e) {
			}
		}
	}

	public static void work() {
		Task t = null;

		while ((t = TaskDeque.pollLast()) != null)
			t.execute();

		while ((t = TaskDeque.steal()) != null)
			t.execute();

	}

	private static void workInfinitely() {
		while (true) {
			work();

			Pair<Long, Task> t = interConnects.stealTask();
			if (t != null && t.second != null) {
				t.second.execute();
				interConnects.returnTask(t);
			}
		}
	}
}
