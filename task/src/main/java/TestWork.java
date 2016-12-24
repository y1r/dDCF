import dDCF.lib.Task;
import dDCF.lib.TaskDeque;
import dDCF.lib.Tasks;
import dDCF.lib.Work;

import java.io.Serializable;

public class TestWork implements Work {

	public static Serializable work(Serializable i) {
		System.out.println(i);

		if ((Integer) i == 5) return "finished";

		Tasks tasks = new Tasks();
		tasks.appendTask(new Task(TestWork::work, (Integer) i + 1));

		try {
			Thread.sleep(3000);
		} catch (Exception e) {
		}

		tasks.join();

		return tasks.getTask(0).result;
	}

	@Override
	public void starter() {
		TaskDeque.appendTask(new Task(TestWork::work, 0));
		TaskDeque.appendTask(new Task(TestWork::work, 0));
	}

	@Override
	public void ender() {

	}
}
