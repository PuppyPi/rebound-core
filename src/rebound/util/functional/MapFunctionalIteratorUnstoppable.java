package rebound.util.functional;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface MapFunctionalIteratorUnstoppable<K, V>
extends MapFunctionalIterator<K, V>
{
	@Nonnull
	public void observeUnstoppable(K key, V value);
	
	
	@Override
	public default ContinueSignal observe(K key, V value)
	{
		observeUnstoppable(key, value);
		return ContinueSignal.Continue;
	}
}
