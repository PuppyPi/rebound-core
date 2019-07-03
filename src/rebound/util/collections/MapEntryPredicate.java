package rebound.util.collections;

public interface MapEntryPredicate<K, V>
{
	public boolean test(K key, V value);
}
