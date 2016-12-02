package dDCF.lib;

import dDCF.lib.internal.Worker;

import java.util.ArrayList;
import java.util.List;

public class Tasks<InputType, ResultType> {
	List<Task> tasks = new ArrayList<>();

	public int appendTask(Task<InputType, ResultType> task) {
		tasks.add(task);
		TaskDeque.appendTask(task); // add to last

		return getLength();
	}

	public void join() {
		while (tasks.stream().anyMatch(value -> !value.isEnded())) {
			Worker.work();
		}
	}

	int getLength() {
		return tasks.size() - 1;
	}

	public Task getTask(int index) {
		return tasks.get(index);
	}
}