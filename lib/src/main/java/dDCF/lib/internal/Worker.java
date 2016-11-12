package dDCF.lib.internal;

import dDCF.lib.Task;

public class Worker extends Thread {
	private Worker() {
	}

	public static Thread getInfiniteWorker() {
		Thread th = new Thread(() ->
		{
			while (true) {
				try {
					Task task = TaskDeque.deque.takeFirst(); // TODO: first or last?
					work(task);
				} catch (InterruptedException e) {
				}
			}
		});

		return th;
	}

	public static void work(Task task) {
		task.execute();
		CompletedTasks.completeTask(task);
	}
}
