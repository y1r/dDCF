package dDCF.lib;

import dDCF.lib.internal.TaskDeque;
import dDCF.lib.internal.Worker;

import java.util.ArrayList;
import java.util.List;

import static dDCF.lib.internal.TaskDeque.deque;

public class Tasks<InputType, ResultType> {
	List<Task> tasks = new ArrayList<>();

	public int appendTask(Task<InputType, ResultType> task) {
		tasks.add(task);
		deque.add(task); // add to Last

		return getLength();
	}

	public void join() {
		while (tasks.stream().anyMatch(value -> !value.isEnded())) {
			Task task = TaskDeque.deque.pollLast(); // stack-like
			if (task != null)
				Worker.work(task);
			else {
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}
		}
	}

	int getLength() {
		return tasks.size() - 1;
	}

	public Task getTask(int index) {
		return tasks.get(index);
	}
}
