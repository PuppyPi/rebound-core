package rebound.util.collections.maps;

import java.util.Iterator;
import javax.annotation.Nonnull;
import rebound.annotations.hints.IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.reachability.PossiblySnapshotPossiblyLiveValue;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.util.collections.CollectionUtilities;
import rebound.util.collections.RandomAccessIterator;

public abstract class AbstractValueCollectionOfIndexableMapView<V>
extends AbstractValueCollectionOfMapView<V>
implements IndexableCollection<V>
{
	//Easy peasy if, say, they're an inner/anonymous/local class! :D
	@Override
	public abstract IndexableMap<?, V> getOwningMap();
	
	
	@Override
	public boolean isIndexableCollection()
	{
		return getOwningMap().isIndexableMap();
	}
	
	
	@Override
	public V getByIndex(int index) throws IndexOutOfBoundsException
	{
		if (isIndexableCollection())
			return getOwningMap().getValueByIndex(index);
		else
			throw new UnsupportedOperationException();
	}
	
	@Override
	public V removeByIndex(int index) throws IndexOutOfBoundsException
	{
		if (isIndexableCollection())
			return getOwningMap().removeByIndex(index).getValue();
		else
			throw new UnsupportedOperationException();
	}
	
	
	
	
	@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
	@Override
	@Nonnull
	@PossiblySnapshotPossiblyLiveValue
	public Iterator<V> iterator()
	{
		if (isIndexableCollection())
		{
			return new RandomAccessIterator<V>()
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
				protected V getFromUnderlying(int index)
				{
					return getOwningMap().getValueByIndex(index);
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
}
