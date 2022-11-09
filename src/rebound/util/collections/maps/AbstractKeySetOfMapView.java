/*
 * Created on Apr 15, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections.maps;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import rebound.annotations.hints.IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.reachability.LiveMethod;
import rebound.annotations.semantic.reachability.PossiblySnapshotPossiblyLiveValue;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.util.collections.CollectionUtilities;
import rebound.util.collections.PolymorphicCollectionUtilities;

public abstract class AbstractKeySetOfMapView<K>
extends AbstractSet<K>
implements Set<K>, MapKeySetView<K>
{
	//Easy peasy if, say, they're an inner/anonymous/local class! :D
	@Override
	public abstract Map<K, ?> getOwningMap();
	
	
	
	
	/**
	 * Can be array, list, set, collection, iterable, etc. :3
	 * 
	 * @see PolymorphicCollectionUtilities#anyToObjectArray(Object)
	 * @see PolymorphicCollectionUtilities#anyToIterator(Object)
	 */
	@ReadonlyValue
	@PossiblySnapshotPossiblyLiveValue
	public abstract Object getActualKeysOrConstructSnapshotOfKeysForReadingOnly();
	
	
	
	@Override
	@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
	@Nonnull
	@PossiblySnapshotPossiblyLiveValue
	public Iterator<K> iterator()
	{
		return CollectionUtilities.getRemoveCallbackIteratorDecorator(PolymorphicCollectionUtilities.anyToIterator(getActualKeysOrConstructSnapshotOfKeysForReadingOnly()), this);
	}
	
	
	
	
	
	@Override
	@Nonnull
	@SnapshotValue
	@ThrowAwayValue //specified by java.util collections framework API  ^_^
	public Object[] toArray()
	{
		return PolymorphicCollectionUtilities.anyToNewObjectArray(getActualKeysOrConstructSnapshotOfKeysForReadingOnly());
	}
	
	@Override
	@Nonnull
	@SnapshotValue
	@ThrowAwayValue //not explicitly specified by java.util collections framework API, but prolly is like toArray() xD''
	public <T> T[] toArray(@Nonnull T[] a)
	{
		return (T[])PolymorphicCollectionUtilities.anyToNewArray(getActualKeysOrConstructSnapshotOfKeysForReadingOnly(), a.getClass().getComponentType());
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
		return getOwningMap().containsKey(o);
	}
	
	
	
	
	
	
	@Override
	public boolean add(K e)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	@LiveMethod
	public boolean remove(Object o)
	{
		boolean contained = getOwningMap().containsKey(o);
		if (contained)
			getOwningMap().remove(o);
		return contained;
	}
	
	@Override
	public boolean addAll(Collection<? extends K> c)
	{
		throw new UnsupportedOperationException();
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
	//public boolean retainAll(Collection<?> c);
	//public boolean removeAll(Collection<?> c);
}
