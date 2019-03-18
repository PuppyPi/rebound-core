package rebound.util.functional;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface MapFunctionalIterator<K, V>
{
	@Nonnull
	public ContinueSignal observe(K key, V value);
}
