/*
 * Created on Apr 15, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections.maps;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nonnull;
import rebound.annotations.hints.IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.reachability.LiveMethod;
import rebound.annotations.semantic.reachability.PossiblySnapshotPossiblyLiveValue;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.util.collections.PolymorphicCollectionUtilities;
import rebound.util.objectutil.BasicObjectUtilities;

public abstract class AbstractEntrySetOfMapView<K, V>
extends AbstractSet<Entry<K, V>>
implements Set<Entry<K, V>>, MapEntrySetView<K, V>
{
	//Easy peasy if, say, they're an inner/anonymous/local class! :D
	@Override
	public abstract Map<K, V> getOwningMap();
	
	
	
	/**
	 * Can be array, list, set, collection, iterable, etc. :3
	 * 
	 * + Comes with a default implementation that *possibly wastefully!* constructs a new snapshot each invocation, but can definitely be overridden! ^_^
	 * 
	 * @see PolymorphicCollectionUtilities#anyToObjectArray(Object)
	 * @see PolymorphicCollectionUtilities#anyToIterator(Object)
	 */
	@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
	@PossiblySnapshotPossiblyLiveValue
	public Object getActualEntriesOrConstructSnapshotOfEntries()
	{
		//Default snapshotting impl! :D
		
		Set<K> keys = getOwningMap().keySet();
		
		Entry[] entries = new Entry[keys.size()]; //faster for toArray() and not too terribly slow for iterator()  ^_^
		
		int i = 0;
		for (K key : keys)
			entries[i++] = new AbstractEntryOfMap<K, V>(getOwningMap(), key);
		
		return entries;
	}
	
	
	
	@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
	@Override
	@Nonnull
	@PossiblySnapshotPossiblyLiveValue
	public Iterator<Entry<K, V>> iterator()
	{
		//return CollectionUtilities.iterator(getActualEntriesOrConstructSnapshotOfEntries());
		
		Iterator<K> k = getOwningMap().keySet().iterator();
		
		return new Iterator<Map.Entry<K,V>>()
		{
			@Override
			public boolean hasNext()
			{
				return k.hasNext();
			}
			
			@Override
			public Entry<K, V> next()
			{
				K key = k.next();
				return new AbstractEntryOfMap<K, V>(getOwningMap(), key);
			}
			
			@Override
			public void remove()
			{
				k.remove();
			}
		};
	}
	
	
	
	
	
	
	
	@Override
	@Nonnull
	@SnapshotValue
	@ThrowAwayValue //specified by java.util collections framework API  ^_^
	public Object[] toArray()
	{
		return PolymorphicCollectionUtilities.anyToNewObjectArray(getActualEntriesOrConstructSnapshotOfEntries());
	}
	
	@Override
	@Nonnull
	@SnapshotValue
	@ThrowAwayValue //not explicitly specified by java.util collections framework API, but prolly is like toArray() xD''
	public <T> T[] toArray(@Nonnull T[] a)
	{
		return (T[])PolymorphicCollectionUtilities.anyToNewArray(getActualEntriesOrConstructSnapshotOfEntries(), a.getClass().getComponentType());
	}
	
	
	
	
	
	@Override
	@LiveMethod
	public int size()
	{
		return getOwningMap().size();
	}
	
	@Override
	@LiveMethod
	public boolean isEmpty()
	{
		return getOwningMap().isEmpty();
	}
	
	@Override
	@LiveMethod
	public boolean contains(Object o)
	{
		if (!(o instanceof Entry))
			return false;
		
		Entry e = (Entry)o;
		
		V v = getOwningMap().get(e.getKey());
		
		return BasicObjectUtilities.eq(e.getValue(), v);
	}
	
	
	
	
	
	@Override
	@LiveMethod
	public boolean add(Entry<K, V> e)
	{
		K k = e.getKey();
		V v = e.getValue();
		
		if (getOwningMap().containsKey(k))
		{
			V previousValue = getOwningMap().put(k, v);
			return BasicObjectUtilities.eq(previousValue, v);
		}
		else
		{
			getOwningMap().put(k, v);
			return true;
		}
	}
	
	@Override
	@LiveMethod
	public boolean remove(Object o)
	{
		if (!(o instanceof Entry))
			return false;
		
		Entry e = (Entry) o;
		
		Object k = e.getKey();
		Object v = e.getValue();
		
		if (getOwningMap().containsKey(k))
		{
			V previousValue = getOwningMap().remove(k);
			return BasicObjectUtilities.eq(previousValue, v);
		}
		else
		{
			return false;
		}
	}
	
	@Override
	@LiveMethod
	public void clear()
	{
		getOwningMap().clear();
	}
	
	
	
	
	
	
	
	
	//// Use default impl's :3 ////
	//public boolean equals(Object o);
	//public int hashCode();
	//public boolean containsAll(Collection<?> c);
	//public boolean addAll(Collection<? extends Entry<K, V>> c);
	//public boolean retainAll(Collection<?> c);
	//public boolean removeAll(Collection<?> c);
}
