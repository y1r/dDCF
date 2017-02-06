package dDCF.lib;

import dDCF.lib.internal.Worker;

import java.util.ArrayList;
import java.util.List;

public class Tasks {
	public List<Task> tasks = new ArrayList<>();

	public int appendTask(Task task) {
		tasks.add(task);
		TaskDeque.appendTask(task); // add to last

		return getLength();
	}

	public void join() {
		/*
		while (tasks.stream().anyMatch(value -> !value.isEnded())) {
			Worker.work();
		}
		*/

		while (needWorking()) {
			Worker.work();
		}
	}

	private boolean needWorking() {
		for (int i = 0; i < tasks.size(); i++)
			if (!tasks.get(i).isEnded()) return true;
		return false;
	}

	int getLength() {
		return tasks.size() - 1;
	}

	public Task getTask(int index) {
		return tasks.get(index);
	}
}
