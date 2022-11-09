package rebound.util.collections.maps;

import java.util.Map;
import javax.annotation.Nullable;
import rebound.annotations.semantic.FunctionalityType;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionIntToInt;

@FunctionalityType
public interface MapWithGetRandomEntry<K, V>
extends Map<K, V>
{
	/**
	 * @return null  ifF {@link #isEmpty()}
	 */
	public @Nullable Entry<K, V> getRandomEntry(UnaryFunctionIntToInt pullIntegerZeroToExclusiveHighBound);
	
	/**
	 * @throws IllegalStateException  ifF {@link #isEmpty()}
	 */
	public K getRandomKey(UnaryFunctionIntToInt pullIntegerZeroToExclusiveHighBound) throws IllegalStateException;
	
	/**
	 * @throws IllegalStateException  ifF {@link #isEmpty()}
	 */
	public V getRandomValue(UnaryFunctionIntToInt pullIntegerZeroToExclusiveHighBound) throws IllegalStateException;
	
	
	
	
	
	//<<< tp MapWithGetRandomEntry
	@TraitPredicate
	public default boolean isMapWithGetRandomEntry()
	{
		return true;
	}
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof MapWithGetRandomEntry && ((MapWithGetRandomEntry)x).isMapWithGetRandomEntry();
	}
	//>>>
}
