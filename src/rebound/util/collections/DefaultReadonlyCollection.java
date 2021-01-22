package rebound.util.collections;

import java.util.Collection;
import rebound.exceptions.ReadonlyUnsupportedOperationException;

public interface DefaultReadonlyCollection<E>
extends DefaultCollection<E>
{
	@Override
	public default boolean add(E e)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public default boolean addAll(Collection<? extends E> c)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public default void clear()
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public default boolean remove(Object o)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public default boolean retainAll(Collection<?> c)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public default boolean removeAll(Collection<?> c)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
}
