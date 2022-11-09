package rebound.util.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class RandomAccessIterator<E>
implements Iterator<E>
{
	protected int nextIndex = 0;
	protected boolean removeCalled = false;
	
	@Override
	public boolean hasNext()
	{
		int realNextIndex = removeCalled ? (nextIndex - 1) : nextIndex;
		return realNextIndex < getUnderlyingSize();
	}
	
	@Override
	public E next()
	{
		int realNextIndex = removeCalled ? (nextIndex - 1) : nextIndex;
		
		if (realNextIndex < getUnderlyingSize())
		{
			E element = getFromUnderlying(realNextIndex);
			
			if (removeCalled)
				removeCalled = false;
			else
				nextIndex++;
			
			return element;
		}
		else
		{
			throw new NoSuchElementException();
		}
	}
	
	@Override
	public void remove()
	{
		if (nextIndex == 0)
			throw new IllegalStateException();
		
		if (removeCalled)
			throw new IllegalStateException();
		
		removeFromUnderlying(nextIndex - 1);
		removeCalled = true;
	}
	
	
	
	
	protected abstract int getUnderlyingSize();
	protected abstract void removeFromUnderlying(int index);
	protected abstract E getFromUnderlying(int index);
}
