package dDCF.lib;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskDeque {
	private static LinkedBlockingQueue<Task> queue = new LinkedBlockingQueue<>();
	private static ConcurrentHashMap<Long, TaskDeque> taskDequeHashMap = new ConcurrentHashMap<>();
	private static Random rnd = new Random();
	private LinkedBlockingDeque<Task> deque = new LinkedBlockingDeque<>();

	public TaskDeque() {
		List<Task> tmpTaskList = new ArrayList<>();
		queue.drainTo(tmpTaskList);
		deque.addAll(tmpTaskList);

		taskDequeHashMap.put(Thread.currentThread().getId(), this);
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
		Enumeration<TaskDeque> all = taskDequeHashMap.elements();

		while (all.hasMoreElements()) {
			TaskDeque tmp = all.nextElement();
			if (tmp != getCurrentTaskDeque() && tmp.deque.size() != 0)
				list.add(tmp);
		}

		while (list.size() != 0) {
			TaskDeque tmp = list.remove(rnd.nextInt(list.size()));
			Task t = tmp._pollFirst();
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
