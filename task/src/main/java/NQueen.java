import dDCF.lib.Task;
import dDCF.lib.TaskDeque;
import dDCF.lib.Tasks;
import dDCF.lib.Work;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class NQueen implements Work {
	Task t = null;

	@Override
	public void starter() {
		t = new Task<>(null, this::main, null);
		TaskDeque.appendTask(t);
	}

	@Override
	public void ender() {

	}

	/*
	Object main(Object t) {
		int[] init = new int[0];

		System.out.println(NQueen1(5, init));
		return null;
	}
	*/

	/*
	Object main(Object t) {
		int[] init = new int[0];

		System.err.println("optimize start");
		for (int i = 0; i < 3; i++) {
			NQueen2(new NQueenData(i, init));
			System.err.print('.');
		}
		System.err.println("\noptimize end");

		for (int i = 1; i <= 32; i++) {
			{
				long time = 0;
				long start = System.currentTimeMillis();
				int res = 0;
				for (int j = 0; j < 10; j++) {
					res = NQueen1(i, init);
					System.err.print(".");
				}
				System.err.print("\n");
				long end = System.currentTimeMillis();

				time += (end - start) / 10;
				System.out.println("1," + i + "," + time + "," + res);
			}
			{
				long time = 0;
				long start = System.currentTimeMillis();
				int res = 0;
				for (int j = 0; j < 10; j++) {
					res = NQueen2(new NQueenData(i, init));
					System.err.print(".");
				}
				System.err.print("\n");
				long end = System.currentTimeMillis();

				time += (end - start) / 10;
				System.out.println("2," + i + "," + time + "," + res);
			}
		}
		return null;
	}
	*/

	Object main(Object t) {
		Scanner scan = new Scanner(System.in);
		int N = scan.nextInt();

		int[] init = new int[0];

		System.out.println("optimize start");
		for (int i = 1; i < N; i++) {
			NQueen1(i, init);
			NQueen2(new NQueenData(i, init));
			System.out.print('.');
		}
		System.out.println();
		System.out.println("optimize finish");

		long time = 0;
		for (int i = 0; i < 10; i++) {
			long start = System.currentTimeMillis();
			NQueen1(N, init);
			long end = System.currentTimeMillis();
			time += end - start;
			System.out.print('.');
		}
		System.out.println();
		System.out.println("NQueen1:" + time / 10.0);

		time = 0;
		for (int i = 0; i < 10; i++) {
			long start = System.currentTimeMillis();
			NQueen2(new NQueenData(N, init));
			long end = System.currentTimeMillis();
			time += end - start;
			System.out.print('.');
		}
		System.out.println();
		System.out.println("NQueen2:" + time / 10.0);

		return null;
	}

	private boolean NQueenCheck(int N, int[] map) {
		for (int i = 0; i < map.length; i++) {
			int pos = map[i];
			for (int j = i + 1; j < map.length && map[j] != -1; j++) {
				int row = pos / N;
				int crow = map[j] / N;

				int column = pos % N;
				int ccolumn = map[j] % N;

				int drow = crow - row;
				int dcol = ccolumn - column;


				// to - right
				if (drow == 0) return false;

				// to - down
				if (dcol == 0) return false;

				// left-down
				if (drow == dcol) return false;

				// to - right-down
				if (drow == -dcol) return false;
			}
		}

		return true;
	}

	Integer NQueen1(Integer N, int[] map) {
		// fail-check
		boolean ok = NQueenCheck(N, map);
		if (ok && map.length == N && map[map.length - 1] != -1)
			return 1;
		else if (!ok) {
			int pos = map.length - 1;

			// want to remove
			for (; pos >= 0 && map[pos] == -1; pos--) ;
			map[pos] = -1;

			return 0;
		}

		// length-check
		if (map.length != N) {
			// expanding
			int[] newmap = new int[N];
			System.arraycopy(map, 0, newmap, 0, map.length);
			for (int i = map.length; i < newmap.length; i++)
				newmap[i] = -1;
			return NQueen1(N, newmap);
		}

		int currentMax = Arrays.stream(map).max().orElse(-1);

		// want to remove
		int pos = 0;
		for (pos = 0; pos < map.length && map[pos] != -1; pos++) ;

		int res = 0;
		for (int i = currentMax + 1; i < N * N; i++) {
			map[pos] = i;
			res += NQueen1(N, map);
		}
		map[pos] = -1;

		return res;
	}

	Integer NQueen2(NQueenData data) {
		// fail-check
		if (!NQueenCheck(data.N, data.map)) return 0;

		// finish-check
		if (data.map.length == data.N) return 1;

		int currentMax;
		if (data.map.length == 0)
			currentMax = -1;
		else
			currentMax = data.map[data.map.length - 1];

		int res = 0;

		Tasks<NQueenData, Integer> tasks = new Tasks<>();
		List<Task<NQueenData, Integer>> taskList = new ArrayList<>();

		for (int i = currentMax + 1; i < data.N * data.N; i++) {
			int[] newmap = new int[data.map.length + 1];
			System.arraycopy(data.map, 0, newmap, 0, data.map.length);
			newmap[data.map.length] = i;
			if (data.map.length > 1)
				res += NQueen1(data.N, newmap);
			else {
				Task<NQueenData, Integer> task = new Task<>(tasks, this::NQueen2, new NQueenData(data.N, newmap));
				taskList.add(task);
				tasks.appendTask(task);
			}
		}

		tasks.join();

		int sum = taskList.stream().mapToInt(value -> value.getResult()).sum();

		if (res != 0) return res;
		return sum;
	}

	class NQueenData {
		Integer N;
		int[] map;

		NQueenData(Integer n, int[] m) {
			N = n;
			map = m;
		}
	}
}