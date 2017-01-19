package dDCF.lib;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskDeque {
	private final static Object hashMapLock = new Object();
	private static LinkedBlockingQueue<Task> queue = new LinkedBlockingQueue<>();
	private static HashMap<Long, TaskDeque> taskDequeHashMap = new HashMap<>();
	private LinkedBlockingDeque<Task> deque = new LinkedBlockingDeque<>();

	public TaskDeque() {
		List<Task> tmpTaskList = new ArrayList<>();
		queue.drainTo(tmpTaskList);
		deque.addAll(tmpTaskList);

		synchronized (hashMapLock) {
			taskDequeHashMap.put(Thread.currentThread().getId(), this);
		}
	}

	public static void appendTask(Task t) {
		if (getCurrentTaskDeque() != null) {
			getCurrentTaskDeque().append(t);
		} else
			queue.add(t);
	}

	private static TaskDeque getCurrentTaskDeque() {
		return taskDequeHashMap.get(Thread.currentThread().getId());
	}

	public static Task pollLast() {
		return getCurrentTaskDeque()._pollLast();
	}

	public static Task pollFirst() {
		return getCurrentTaskDeque()._pollFirst();
	}

	public static Task steal() {
		List<TaskDeque> list = new ArrayList<>();

		long thId = Thread.currentThread().getId();

		for (Map.Entry<Long, TaskDeque> t : taskDequeHashMap.entrySet()) {
			long key = t.getKey();
			TaskDeque deq = t.getValue();

			if (key != thId && deq.deque.size() != 0)
				list.add(deq);
		}

		Collections.shuffle(list);

		for (int i = 0; i < list.size(); i++) {
			Task t = list.get(i)._pollFirst();
			if (t != null) return t;
		}

		return null;
	}

	private void append(Task t) {
		deque.add(t);
	}

	private Task _pollLast() {
		return deque.pollLast();
	}

	private Task _pollFirst() {
		return deque.pollFirst();
	}
}
