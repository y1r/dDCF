package dDCF.lib.internal;

import dDCF.lib.Task;
import dDCF.lib.TaskDeque;

public class Worker extends Thread {
	public Worker() {
	}

	public static Thread getInfiniteWorker() {
		Thread th = new Thread(() ->
		{
			TaskDeque taskDeque = new TaskDeque();
			workInfinitely();
		});

		return th;
	}

	public static void startWorkers() {
		int node = Config.getInstance().threads;
		System.out.println("nodes:" + node);
		Thread[] workers = new Thread[node];

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
		while (true) {
			Task t = TaskDeque.pollLast();
			if (t == null) {
				t = TaskDeque.steal();
				if (t == null) return;
			}
			t.execute();
		}
	}

	private static void workInfinitely() {
		while (true) {
			Task t = TaskDeque.pollLast();
			if (t == null) {
				t = TaskDeque.steal();
				if (t == null) continue;
			}
			t.execute();
		}
	}
}
