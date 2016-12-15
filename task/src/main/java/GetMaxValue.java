import dDCF.lib.Task;
import dDCF.lib.TaskDeque;
import dDCF.lib.Tasks;

import java.util.Random;
import java.util.Scanner;
import java.util.function.Function;

public class GetMaxValue {
	Task t = null;
	Integer[] value = null;
	private int length;

	//@Override
	public void starter() {
		t = new Task<>(null, this::start, null);
		TaskDeque.appendTask(t);
	}

	public void ender() {
		System.out.println("ender");
	}

	Object start(Object t) {
		try {
			Scanner scanner = new Scanner(System.in);
			length = scanner.nextInt();
			value = new Integer[length];
			Random random = new Random();

			for (int i = 0; i < value.length; i++)
				value[i] = random.nextInt();

		} catch (Exception e) {

		}

		System.out.println("optimize start");
		for (int i = 0; i < 3; i++) {
//			TaskDeque.reset();
			main(this::getMax2);
			main(this::getMax3);
			System.out.print('.');
		}
		System.out.println();
		System.out.println("optimize finish");

		long time = 0;
		for (int i = 0; i < 100; i++) {
//			TaskDeque.reset();
			time += main(this::getMax3);
			System.out.print('.');
		}
		System.out.println();
		System.out.println("getMax3:" + time / 100.0);

		time = 0;
		for (int i = 0; i < 100; i++) {
//			TaskDeque.reset();
			time += main(this::getMax2);
			System.out.print('.');
		}
		System.out.println();
		System.out.println("getMax2:" + time / 100.0);

		return null;
	}

	public long main(Function<Data, Integer> func) {
		long start = System.currentTimeMillis();
		int result = func.apply(new Data(value, 0, value.length - 1));
		long end = System.currentTimeMillis();

		return end - start;
	}

	Integer getMax2(Data data) {
		if (data.start - data.end == 0) return data.array[data.start];

		int mid = data.start + (data.end - data.start) / 2;

		int val1 = getMax2(new Data(data.array, data.start, mid));
		int val2 = getMax2(new Data(data.array, mid + 1, data.end));

		if (val1 < val2) return val2;
		else
			return val1;
	}

	Integer getMax3(Data data) {
		if (data.start - data.end == 0) return data.array[data.start];

		if (data.end - data.start <= 10000) return getMax2(data);

		int mid = data.start + (data.end - data.start) / 2;
//		System.out.flush();

		Tasks<Data, Integer> tasks = new Tasks<>();
		Task<Data, Integer> task1 = new Task<>(tasks, this::getMax3, new Data(data.array, data.start, mid));
		Task<Data, Integer> task2 = new Task<>(tasks, this::getMax3, new Data(data.array, mid + 1, data.end));

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

		if (val1 < val2)
			return val2;
		else
			return val1;
	}

	class Data {
		Integer[] array;
		int start;
		int end;

		Data(Integer[] a, int s, int e) {
			array = a;
			start = s;
			end = e;
		}
	}
}