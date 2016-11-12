import dDCF.lib.Task;
import dDCF.lib.Tasks;
import dDCF.lib.Work;

import java.util.Random;

public class GetMaxValue implements Work {
	@Override
	public void main() {

		Integer[] value = new Integer[10000000];

		Random random = new Random();

		/*
		Tasks<Integer[], Integer> tasks = new Tasks<>();
		Task<Integer[], Integer> task1 = new Task<>(tasks, this::getMax, array1);
		Task<Integer[], Integer> task2 = new Task<>(tasks, this::getMax, array2);

		tasks.appendTask(task1);
		tasks.appendTask(task2);

		tasks.join();
		*/

		for (int i = 0; i < value.length; i++)
			value[i] = random.nextInt();

//		for (Integer val : value) {
//			val = new Integer(random.nextInt());
//		}

		Hoge hoge = new Hoge();

		long start = System.currentTimeMillis();
		hoge.show("Max: " + getMax3(new data(value, 0, value.length - 1)));
		long end = System.currentTimeMillis();
		System.out.println((end - start) + "ms");

	}

	Integer getMax2(data data) {
		if (data.start - data.end == 0) return data.array[data.start];

		int mid = data.start + (data.end - data.start) / 2;

		int val1 = getMax2(new data(data.array, data.start, mid));
		int val2 = getMax2(new data(data.array, mid + 1, data.end));

		if (val1 < val2) return val2;
		else
			return val1;
	}

	Integer getMax3(data data) {
		if (data.start - data.end == 0) return data.array[data.start];

		int mid = data.start + (data.end - data.start) / 2;

		Tasks<data, Integer> tasks = new Tasks<>();
		Task<data, Integer> task1 = new Task<>(tasks, this::getMax3, new data(data.array, data.start, mid));
		Task<data, Integer> task2 = new Task<>(tasks, this::getMax3, new data(data.array, mid + 1, data.end));

		tasks.appendTask(task1);
		tasks.appendTask(task2);

		tasks.join();

		int val1 = task1.getResult();
		int val2 = task2.getResult();

		if (val1 < val2) return val2;
		else
			return val1;
	}

	Integer getMax(Integer[] array) {
		if (array.length == 1)
			return array[0];

		Integer[] array1 = new Integer[array.length / 2];
		Integer[] array2 = new Integer[array.length - array1.length];

		for (int i = 0; i < array.length; i++) {
			if (i < array1.length) array1[i] = array[i];
			else
				array2[i - array1.length] = array[i];
		}

		Tasks<Integer[], Integer> tasks = new Tasks<>();
		Task<Integer[], Integer> task1 = new Task<>(tasks, this::getMax, array1);
		Task<Integer[], Integer> task2 = new Task<>(tasks, this::getMax, array2);

		tasks.appendTask(task1);
		tasks.appendTask(task2);

		tasks.join();

		int val1 = task1.getResult();
		int val2 = task2.getResult();

		if (val1 < val2) return val2;
		else
			return val1;
	}

	class data {
		Integer[] array;
		int start;
		int end;

		data(Integer[] a, int s, int e) {
			array = a;
			start = s;
			end = e;
		}
	}
}
