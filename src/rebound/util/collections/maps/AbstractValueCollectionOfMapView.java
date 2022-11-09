/*
 * Created on Apr 15, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections.maps;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import rebound.annotations.hints.IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.reachability.LiveMethod;
import rebound.annotations.semantic.reachability.PossiblySnapshotPossiblyLiveValue;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.util.collections.CollectionUtilities;
import rebound.util.collections.PolymorphicCollectionUtilities;

public abstract class AbstractValueCollectionOfMapView<V>
implements Collection<V>, MapValueCollectionView<V>
{
	//Easy peasy if, say, they're an inner/anonymous/local class! :D
	@Override
	public abstract Map<?, V> getOwningMap();
	
	
	
	
	
	/**
	 * Can be array, list, set, collection, iterable, etc. :3
	 *
	 * + Comes with a default implementation that constructs a new snapshot each invocation, but can definitely be overridden! ^_^
	 *
	 * @see PolymorphicCollectionUtilities#anyToObjectArray(Object)
	 * @see PolymorphicCollectionUtilities#anyToIterator(Object)
	 */
	@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
	@PossiblySnapshotPossiblyLiveValue
	public Object getActualValuesOrConstructSnapshotOfValues()
	{
		//Default snapshotting impl! :D
		
		Set keys = getOwningMap().keySet();
		
		Object[] values = new Object[keys.size()]; //faster for toArray() and not too terribly slow for iterator()  ^_^
		
		int i = 0;
		for (Object key : keys)
			values[i++] = getOwningMap().get(key);
		
		return values;
	}
	
	@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
	@Override
	@Nonnull
	@PossiblySnapshotPossiblyLiveValue
	public Iterator<V> iterator()
	{
		return CollectionUtilities.getRemoveCallbackIteratorDecorator(PolymorphicCollectionUtilities.anyToIterator(getActualValuesOrConstructSnapshotOfValues()), this);
	}
	
	
	
	
	
	
	@Override
	@Nonnull
	@SnapshotValue
	@ThrowAwayValue //specified by java.util collections framework API  ^_^
	public Object[] toArray()
	{
		return PolymorphicCollectionUtilities.anyToNewObjectArray(getActualValuesOrConstructSnapshotOfValues());
	}
	
	@Override
	@Nonnull
	@SnapshotValue
	@ThrowAwayValue //not explicitly specified by java.util collections framework API, but prolly is like toArray() xD''
	public <T> T[] toArray(@Nonnull T[] a)
	{
		return (T[])PolymorphicCollectionUtilities.anyToNewArray(getActualValuesOrConstructSnapshotOfValues(), a.getClass().getComponentType());
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
		return getOwningMap().containsValue(o);
	}
	
	@LiveMethod
	public boolean containsAll(Collection c)
	{
		for (Object e : c)
			if (!contains(e))
				return false;
		return true;
	}
	
	
	
	//Borrowed from AbstractSet, since that works decently for non-set unordered collections as well :3
	public boolean equals(Object o)
	{
		if (o == this)
			return true;
		
		if (!(o instanceof Set))
			return false;
		Collection c = (Collection)o;
		if (c.size() != size())
			return false;
		try
		{
			return containsAll(c);
		}
		catch (ClassCastException unused)
		{
			return false;
		}
		catch (NullPointerException unused)
		{
			return false;
		}
	}
	
	
	//Borrowed from AbstractSet, since that works decently for non-set unordered collections as well :3
	public int hashCode()
	{
		int h = 0;
		Iterator<V> i = iterator();
		while (i.hasNext())
		{
			V obj = i.next();
			if (obj != null)
				h += obj.hashCode();
		}
		return h;
	}
	
	
	
	
	
	
	
	
	@Override
	public boolean add(Object e)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException();
	}
	
	public boolean addAll(Collection c)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean removeAll(Collection c)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean retainAll(Collection c)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void clear()
	{
		getOwningMap().clear();
	}
}
