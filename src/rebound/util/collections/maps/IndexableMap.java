package rebound.util.collections.maps;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nonnull;
import rebound.annotations.semantic.FunctionalityType;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionIntToInt;

/**
 * This doesn't imply that the ordering is consistent across method invocations or meaningful in any way!
 * Or that it's even the same ordering the {@link #keySet()}, {@link #entrySet()}, {@link #values()} views' {@link Collection#iterator() iterator}s will return in! (though it probably is)
 * But it's useful in many of the same ways that the arbitrary ordering of those iterators are and in ways it's not!—particularly pseudorandom random-access sampling such as used in unit testing of various things!
 */
@FunctionalityType
public interface IndexableMap<K, V>
extends Map<K, V>, MapWithGetRandomEntry<K, V>
{
	/**
	 * @throws IndexOutOfBoundsException if index < 0 or index >= {@link #size()}
	 */
	public Entry<K, V> getEntryByIndex(int index) throws IndexOutOfBoundsException;
	
	/**
	 * @throws IndexOutOfBoundsException if index < 0 or index >= {@link #size()}
	 */
	public K getKeyByIndex(int index) throws IndexOutOfBoundsException;
	
	/**
	 * @throws IndexOutOfBoundsException if index < 0 or index >= {@link #size()}
	 */
	public V getValueByIndex(int index) throws IndexOutOfBoundsException;
	
	
	public default @Nonnull Entry<K, V> putByIndex(int index, V newValue) throws IndexOutOfBoundsException
	{
		K k = getKeyByIndex(index);
		V oldValue = put(k, newValue);
		return new SimpleEntry<>(k, oldValue);
	}
	
	public default @Nonnull Entry<K, V> removeByIndex(int index) throws IndexOutOfBoundsException
	{
		K k = getKeyByIndex(index);
		V v = remove(k);
		return new SimpleEntry<>(k, v);
	}
	
	
	
	
	//<<< tp IndexableMap
	@TraitPredicate
	public default boolean isIndexableMap()
	{
		return true;
	}
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof IndexableMap && ((IndexableMap)x).isIndexableMap();
	}
	//>>>
	
	
	
	
	@Override
	public default Entry<K, V> getRandomEntry(UnaryFunctionIntToInt pullIntegerZeroToExclusiveHighBound)
	{
		return getEntryByIndex(pullIntegerZeroToExclusiveHighBound.f(this.size()));
	}
	
	@Override
	public default K getRandomKey(UnaryFunctionIntToInt pullIntegerZeroToExclusiveHighBound) throws IllegalStateException
	{
		return getKeyByIndex(pullIntegerZeroToExclusiveHighBound.f(this.size()));
	}
	
	@Override
	public default V getRandomValue(UnaryFunctionIntToInt pullIntegerZeroToExclusiveHighBound) throws IllegalStateException
	{
		return getValueByIndex(pullIntegerZeroToExclusiveHighBound.f(this.size()));
	}
	
	@Override
	public default boolean isMapWithGetRandomEntry()
	{
		return true;
	}
}
