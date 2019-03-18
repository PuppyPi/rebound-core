package rebound.util.collections;

import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import rebound.util.objectutil.UnderlyingInstanceAccessible;

public class SimpleRandomAccessBasedListIterator<E>
implements ListIterator<E>, UnderlyingInstanceAccessible<List<E>>
{
	protected final List<E> underlyingList;
	
	protected int position;
	protected int last = -1;
	
	public SimpleRandomAccessBasedListIterator(List<E> underlyingList, int initialPosition)
	{
		this.underlyingList = underlyingList;
		this.position = initialPosition;
	}
	
	public SimpleRandomAccessBasedListIterator(List<E> underlyingList)
	{
		this(underlyingList, 0);
	}
	
	@Override
	public List<E> getUnderlying()
	{
		return this.underlyingList;
	}
	
	
	
	@Override
	public int nextIndex()
	{
		return this.position;
	}
	
	@Override
	public int previousIndex()
	{
		return this.position-1;
	}
	
	@Override
	public boolean hasNext()
	{
		return this.position < this.underlyingList.size();
	}
	
	@Override
	public boolean hasPrevious()
	{
		return this.position > 0;
	}
	
	
	@Override
	public E next()
	{
		if (!hasNext())
			throw new NoSuchElementException();
		
		this.last = this.position;
		return this.underlyingList.get(this.position++);
	}
	
	@Override
	public E previous()
	{
		if (!hasPrevious())
			throw new NoSuchElementException();
		
		this.last = this.position-1;
		return this.underlyingList.get(--this.position);
	}
	
	
	
	@Override
	public void add(E e)
	{
		this.underlyingList.add(this.position, e);
		this.position++;
	}
	
	@Override
	public void set(E e)
	{
		if (this.last == -1)
			throw new IllegalStateException();
		
		this.underlyingList.set(this.last, e);
	}
	
	@Override
	public void remove()
	{
		if (this.last == -1)
			throw new IllegalStateException();
		
		this.underlyingList.remove(this.last);
		this.last = -1;
	}
	
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + this.last;
		result = prime * result + this.position;
		result = prime * result + ((this.underlyingList == null) ? 0 : this.underlyingList.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleRandomAccessBasedListIterator other = (SimpleRandomAccessBasedListIterator)obj;
		if (this.last != other.last)
			return false;
		if (this.position != other.position)
			return false;
		if (this.underlyingList == null)
		{
			if (other.underlyingList != null)
				return false;
		}
		else if (!this.underlyingList.equals(other.underlyingList))
			return false;
		return true;
	}
}