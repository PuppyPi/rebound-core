package rebound.util.collections;

import rebound.exceptions.NoSuchMappingReturnPath;
import rebound.exceptions.ReturnPath;

public interface MapWithGetRP<K, V>
{
	/**
	 * Throws {@link NoSuchMappingReturnPath} if no mapping for the given key (but remember, {@link ReturnPath}, no stacktrace)
	 * So if it returns <code>null</code>, that really means <code>null</code> was the value! :D
	 */
	public V getrp(K key) throws NoSuchMappingReturnPath;
}