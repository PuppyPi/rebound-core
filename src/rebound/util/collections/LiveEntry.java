package rebound.util.collections;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import rebound.annotations.semantic.FunctionalityType;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;

/**
 * Like {@link Entry} but supports {@link #remove()}!  And is guaranteed to be Live, unlike {@link SimpleImmutableEntry}!
 */
@FunctionalityType
public interface LiveEntry<K, V>
extends Entry<K, V>
{
	public void setValueWithoutGetting(V value);
	
	/**
	 * Once this is called, all the other methods throw {@link IllegalStateException}.
	 */
	public void remove();
	
	
	
	
	
	// <<< tp LiveEntry
	@TraitPredicate
	public default boolean isLiveEntry()
	{
		return true;
	}
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof LiveEntry && ((LiveEntry)x).isLiveEntry();
	}
	// >>>
}
