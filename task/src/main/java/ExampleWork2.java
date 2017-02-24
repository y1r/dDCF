/* ***** EXAMPLE ***** */
/*
public class ExampleWork2 implements Work {
	static final int[] testArray = {1, 2, 3, 4, 5, 6, 7, 8, 9};
	Task _main = null;

	@Override
	public void starter() {
		// setup Tasks
		_main = new Task(ExampleWork2::main, null);
		TaskDeque.appendTask(_main);
	}

	static Serializable main(Serializable args) {
		System.out.println("result:" + getMax(testArray));
		return null;
	}

	static Serializable getMax(Serializable args) {
		int[] array = (int[]) args;

		// 再帰の終了条件
		if (array.length == 1) return array[0];
		else {
			// 新しい配列の作成
			int[] array1 = new int[array.length / 2];
			int[] array2 = new int[array.length - array1.length];

			// 値のコピー
			for (int i = 0; i < array.length; i++) {
				if (i < array1.length)
					array1[i] = array[i];
				else
					array2[i - array1.length] = array[i];
			}

			// fork
			Tasks tasks = new Tasks();
			tasks.appendTask(new Task(ExampleWork2::getMax, (Serializable) array1));
			tasks.appendTask(new Task(ExampleWork2::getMax, (Serializable) array2));

			tasks.join(); // join

			int max = Integer.MIN_VALUE;
			for (Task task : tasks.tasks)
				max = Integer.max(max, (int) task.getResult());
			return max;
		}
	}
}
*/