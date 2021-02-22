package phase.generation.history;

public interface HistoryValueComparator<V> {
	public boolean valueEquals(V v1, V v2);
}
