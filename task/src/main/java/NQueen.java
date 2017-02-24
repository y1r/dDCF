import dDCF.lib.Task;
import dDCF.lib.TaskDeque;
import dDCF.lib.Tasks;
import dDCF.lib.Work;
import dDCF.lib.internal.InterConnects.ConnectionManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NQueen implements Work {
	//public class NQueen {
	Task t = null;

	static Serializable main(Serializable t) {
		Scanner scan = new Scanner(System.in);
		int N = scan.nextInt();

		System.out.println(N);

		int[] init = new int[0];

		System.out.println("optimize start");
		for (int i = 0; i < 10; i++) {
			NQueen1(8, init);
			NQueen2(new NQueenData(8, init));
			System.out.print('.');
		}
		System.out.println();
		System.out.println("optimize finish");
/*
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
*/
		long time = 0;
		for (int i = 0; i < 10; i++) {
			long start = System.currentTimeMillis();
			NQueen2(new NQueenData(N, init));
			long end = System.currentTimeMillis();
			time += end - start;
			System.out.print('.');
		}
		System.out.println();
		System.err.println(ConnectionManager.getInstance().getConnectionCounts() + "," + time / 10.0);
//		System.out.println("NQueen2:" + time / 100.0);

		System.exit(0);

		return null;
	}

	static private boolean NQueenCheck(int N, int[] map) {
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

	static Integer NQueen1(Integer N, int[] map) {
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

		// slow
		//int currentMax = Arrays.stream(map).max().orElse(-1);

		// fast
		int currentMax = -1;
		for (int i = 0; i < map.length; i++)
			if (map[i] > currentMax) currentMax = map[i];

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

	static Serializable NQueen2(Serializable d) {
		NQueenData data = (NQueenData) d;
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

		Tasks tasks = new Tasks();
		List<Task> taskList = new ArrayList<>();

		for (int i = currentMax + 1; i < data.N * data.N; i++) {
			int[] newmap = new int[data.map.length + 1];
			System.arraycopy(data.map, 0, newmap, 0, data.map.length);
			newmap[data.map.length] = i;
			if (data.map.length > 1)
				res += NQueen1(data.N, newmap);
			else {
				Task task = new Task(NQueen::NQueen2, new NQueenData(data.N, newmap));
				taskList.add(task);
				tasks.appendTask(task);
			}
		}

		tasks.join();

		// slow
//		int sum = taskList.stream().mapToInt(value -> (Integer) value.getResult()).sum();

		// fast
		int sum = 0;
		for (int i = 0; i < taskList.size(); i++)
			sum += (int) taskList.get(i).getResult();


		if (res != 0) return res;
		return sum;
	}

	@Override
	public void starter() {
		try {
			Thread.sleep(15000);
		} catch (Exception e) {

		}
		t = new Task(NQueen::main, null);
		TaskDeque.appendTask(t);
	}
}

class NQueenData implements Serializable {
	public Integer N;
	public int[] map;

	public NQueenData() {
	}

	public NQueenData(Integer n, int[] m) {
		N = n;
		map = m;
	}
}