package rebound.util.collections;

import java.util.List;
import java.util.ListIterator;

public class DelegatingListIterator<E>
implements ListIterator<E>
{
	protected final List<E> underlying;
	protected int cursor;
	protected boolean forwardLooking = true;
	protected boolean modificationUsedUpOrUninitialized = true;
	
	public DelegatingListIterator(List<E> underlying, int cursor)
	{
		this.underlying = underlying;
		this.cursor = cursor;
	}
	
	public DelegatingListIterator(List<E> underlying)
	{
		this(underlying, 0);
	}
	
	
	
	
	
	@Override
	public boolean hasNext()
	{
		return cursor < underlying.size();
	}
	
	@Override
	public E next()
	{
		E e = underlying.get(cursor);
		cursor++;
		forwardLooking = true;
		modificationUsedUpOrUninitialized = false;
		return e;
	}
	
	@Override
	public boolean hasPrevious()
	{
		return cursor > 0;
	}
	
	@Override
	public E previous()
	{
		E e = underlying.get(cursor-1);
		cursor--;
		forwardLooking = false;
		modificationUsedUpOrUninitialized = false;
		return e;
	}
	
	@Override
	public int nextIndex()
	{
		return cursor;
	}
	
	@Override
	public int previousIndex()
	{
		return cursor-1;
	}
	
	@Override
	public void remove()
	{
		if (modificationUsedUpOrUninitialized)
			throw new IllegalStateException();
		underlying.remove(forwardLooking ? cursor - 1 : cursor);
		if (forwardLooking)
			cursor--;
		modificationUsedUpOrUninitialized = true;
	}
	
	@Override
	public void set(E e)
	{
		if (modificationUsedUpOrUninitialized)
			throw new IllegalStateException();
		underlying.set(forwardLooking ? cursor - 1 : cursor, e);
		modificationUsedUpOrUninitialized = true;
	}
	
	@Override
	public void add(E e)
	{
		if (modificationUsedUpOrUninitialized)
			throw new IllegalStateException();
		underlying.add(forwardLooking ? cursor - 1 : cursor, e);
		if (forwardLooking)
			cursor++;
		modificationUsedUpOrUninitialized = true;
	}
}
