package rebound.util.collections;

import java.util.Map.Entry;
import rebound.annotations.semantic.temporal.ConstantReturnValue;

/**
 * Like {@link Entry} but supports {@link #remove()}!
 */
public interface LiveMapEntry<K, V>
{
	@ConstantReturnValue
	public K getKey();
	
	public V getValue();
	
	public void setValue(V value);
	public V getAndSetValue(V value);
	
	/**
	 * Once this is called, all the other methods throw {@link IllegalStateException}.
	 */
	public void remove();
}
