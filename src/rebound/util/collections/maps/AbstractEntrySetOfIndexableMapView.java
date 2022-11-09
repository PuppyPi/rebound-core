package rebound.util.collections.maps;

import java.util.Map.Entry;
import javax.annotation.Nonnull;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.util.collections.CollectionUtilities;

public abstract class AbstractEntrySetOfIndexableMapView<K, V>
extends AbstractEntrySetOfMapView<K, V>
implements IndexableCollection<Entry<K, V>>
{
	//Easy peasy if, say, they're an inner/anonymous/local class! :D
	@Override
	public abstract IndexableMap<K, V> getOwningMap();
	
	
	@Override
	public boolean isIndexableCollection()
	{
		return getOwningMap().isIndexableMap();
	}
	
	
	@Override
	public Entry<K, V> getByIndex(int index) throws IndexOutOfBoundsException
	{
		if (isIndexableCollection())
			return getOwningMap().getEntryByIndex(index);
		else
			throw new UnsupportedOperationException();
	}
	
	@Override
	public Entry<K, V> removeByIndex(int index) throws IndexOutOfBoundsException
	{
		if (isIndexableCollection())
			return getOwningMap().removeByIndex(index);
		else
			throw new UnsupportedOperationException();
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
