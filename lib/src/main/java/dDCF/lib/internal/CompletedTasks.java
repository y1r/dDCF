package dDCF.lib.internal;

import dDCF.lib.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompletedTasks {
	private static List<Task> completed = Collections.synchronizedList(new ArrayList<>());

	public static void completeTask(Task task) {

	}
}
