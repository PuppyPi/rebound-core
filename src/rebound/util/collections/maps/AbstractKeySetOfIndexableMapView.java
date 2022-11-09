package rebound.util.collections.maps;

import java.util.Iterator;
import javax.annotation.Nonnull;
import rebound.annotations.hints.IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.reachability.PossiblySnapshotPossiblyLiveValue;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.util.collections.CollectionUtilities;
import rebound.util.collections.PolymorphicCollectionUtilities;
import rebound.util.collections.RandomAccessIterator;

public abstract class AbstractKeySetOfIndexableMapView<K>
extends AbstractKeySetOfMapView<K>
implements IndexableCollection<K>
{
	//Easy peasy if, say, they're an inner/anonymous/local class! :D
	@Override
	public abstract IndexableMap<K, ?> getOwningMap();
	
	
	@Override
	public boolean isIndexableCollection()
	{
		return getOwningMap().isIndexableMap();
	}
	
	
	@Override
	public K getByIndex(int index) throws IndexOutOfBoundsException
	{
		if (isIndexableCollection())
			return getOwningMap().getEntryByIndex(index).getKey();
		else
			throw new UnsupportedOperationException();
	}
	
	@Override
	public K removeByIndex(int index) throws IndexOutOfBoundsException
	{
		if (isIndexableCollection())
			return getOwningMap().removeByIndex(index).getKey();
		else
			throw new UnsupportedOperationException();
	}
	
	
	
	
	/**
	 * Be sure to implement this method if <code>this</code> isn't always an {@link #isIndexableCollection() IndexableCollection}!
	 * 
	 * Can be array, list, set, collection, iterable, etc. :3
	 * 
	 * @see PolymorphicCollectionUtilities#anyToObjectArray(Object)
	 * @see PolymorphicCollectionUtilities#anyToIterator(Object)
	 */
	@ReadonlyValue
	@PossiblySnapshotPossiblyLiveValue
	public Object getActualKeysOrConstructSnapshotOfKeysForReadingOnly()
	{
		if (isIndexableCollection())
			return CollectionUtilities.defaultToArray(this);
		else
			throw new UnsupportedOperationException();
	}
	
	
	
	@Override
	@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
	@Nonnull
	@PossiblySnapshotPossiblyLiveValue
	public Iterator<K> iterator()
	{
		if (isIndexableCollection())
		{
			return new RandomAccessIterator<K>()
			{
				@Override
				protected int getUnderlyingSize()
				{
					return getOwningMap().size();
				}
				
				@Override
				protected void removeFromUnderlying(int index)
				{
					getOwningMap().removeByIndex(index);
				}
				
				@Override
				protected K getFromUnderlying(int index)
				{
					return getOwningMap().getKeyByIndex(index);
				}
			};
		}
		else
		{
			return super.iterator();
		}
	}
	
	
	
	
	
	@Override
	@Nonnull
	@SnapshotValue
	@ThrowAwayValue //specified by java.util collections framework API  ^_^
	public Object[] toArray()
	{
		if (isIndexableCollection())
			return CollectionUtilities.defaultToArray(this);
		else
			return super.toArray();
	}
	
	@Override
	@Nonnull
	@SnapshotValue
	@ThrowAwayValue //not explicitly specified by java.util collections framework API, but prolly is like toArray() xD''
	public <T> T[] toArray(@Nonnull T[] a)
	{
		if (isIndexableCollection())
			return CollectionUtilities.defaultToArray(this, a);
		else
			return super.toArray(a);
	}
	
	
	
	
	
	
	
	
	
	//// Use default impl's :3 ////
	//public boolean equals(Object o);
	//public int hashCode();
	//public boolean containsAll(Collection<?> c);
	//public boolean retainAll(Collection<?> c);
	//public boolean removeAll(Collection<?> c);
}
