package rebound.util.collections;

import java.util.Collection;

public interface DefaultCollection<E>
extends Collection<E>
{
	@Override
	public default boolean addAll(Collection<? extends E> c)
	{
		return CollectionUtilities.defaultAddAll(this, c);
	}
	
	@Override
	public default boolean containsAll(Collection<?> c)
	{
		return CollectionUtilities.defaultContainsAll(this, c);
	}
	
	@Override
	public default boolean retainAll(Collection<?> c)
	{
		return CollectionUtilities.defaultRetainAll(this, c);
	}
	
	@Override
	public default boolean removeAll(Collection<?> c)
	{
		return CollectionUtilities.defaultRemoveAll(this, c);
	}
	
	@Override
	public default boolean isEmpty()
	{
		return size() == 0;
	}
	
	@Override
	public default Object[] toArray()
	{
		return CollectionUtilities.defaultToArray(this);
	}
	
	@Override
	public default <T> T[] toArray(T[] a)
	{
		return CollectionUtilities.defaultToArray(this, a);
	}
}