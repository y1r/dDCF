package dDCF.lib;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskDeque {
	private final static Object lock = new Object();
	private static BlockingQueue<Task> queue = new LinkedBlockingQueue<>();
	private static HashMap<Long, TaskDeque> taskDequeHashMap = new HashMap<>();
	private static volatile int remainingDeques;
	//private static ConcurrentHashMap<Long, TaskDeque> taskDequeHashMap = new ConcurrentHashMap<>();
	private Deque<Task> deque = new LinkedBlockingDeque<>();

	public TaskDeque() {
		List<Task> tmpTaskList = new ArrayList<>();
		queue.drainTo(tmpTaskList);
		deque.addAll(tmpTaskList);

		synchronized (lock) {
			taskDequeHashMap.put(Thread.currentThread().getId(), this);
			remainingDeques--;
		}
	}

	public static void registerWantedWorkers(int num) {
		remainingDeques = num;
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
		TaskDeque deque = getCurrentTaskDeque();
		if (deque == null) {
			return null;
		}

		return deque._pollLast();
	}

	public static Task pollFirst() {
		TaskDeque deque = getCurrentTaskDeque();
		if (deque == null) {
			return null;
		}

		return deque._pollFirst();
	}

	/*
	public static Task steal() {
		long thId = Thread.currentThread().getId();
		Set<Map.Entry<Long, TaskDeque>> taskDeques = taskDequeHashMap.entrySet();

		for (Map.Entry<Long, TaskDeque> e : taskDeques) {
			if (e.getKey() != thId) {
				Task t = e.getValue()._pollFirst();
				if (t != null) return t;
			}
		}

		return null;
	}
	*/

	public static Task steal() {
		if (remainingDeques != 0) return null;

		List<TaskDeque> list = new ArrayList<>();
		TaskDeque cur = getCurrentTaskDeque();

		Collection<TaskDeque> taskDeques = taskDequeHashMap.values();

		for (TaskDeque deque : taskDeques) {
			if (deque != cur)
				list.add(deque);
		}

		Collections.shuffle(list);

		for (int i = 0; i < list.size(); i++) {
			Task t = list.get(i)._pollFirst();
			if (t != null) return t;
		}
/*
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {

		}
*/
		return null;
	}

/*
	static Random rnd = new Random();

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
	*/

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
