/*
 * Created on Feb 20, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Collection;
import rebound.exceptions.ReadonlyUnsupportedOperationException;

/**
 * Note! You probably want to override {@link #contains(Object)}!!
 * (as well as {@link #iterator()} and {@link #size()} :> )
 * (the {@link AbstractCollection#contains(Object)} implementation is O(n)  0,0  )
 * 
 * @author Puppy Pie ^_^
 */
public abstract class AbstractReadonlyList<E>
extends AbstractList<E>
//implements StaticallyReadableCollection, StaticallyUnwriteableCollection
{
	@Override
	public boolean add(E e)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public void clear()
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public boolean remove(Object o)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public boolean retainAll(Collection<?> c)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public boolean removeAll(Collection<?> c)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	
	
	@Override
	public void add(int index, E element)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends E> c)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public E remove(int index)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
}
