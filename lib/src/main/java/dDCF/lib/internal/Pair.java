package dDCF.lib.internal;

public class Pair<T, U> {
	public T first;
	public U second;

	public Pair(T _first, U _second) {
		first = _first;
		second = _second;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pair) {
			Pair<?, ?> pair = (Pair<?, ?>) obj;
			return first.equals(pair.first) && second.equals(pair.second);
		}
		return false;
	}
}
