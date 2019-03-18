/*
 * Created on Jan 12, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;
import rebound.exceptions.ImpossibleException;

public class ConcatenatingIterator<E>
implements Iterator<E>
{
	protected Iterator<Iterator<E>> underlying;
	
	protected boolean bof = true;
	protected boolean alreadyRemoved = false;
	
	protected Iterator<E> previous;
	protected Iterator<E> current;
	protected boolean bofInCurrent;
	
	public ConcatenatingIterator(Iterator<Iterator<E>> underlying)
	{
		this.underlying = underlying;
		
		//Initialize :>
		advanceIterators();
		this.bofInCurrent = true;
	}
	
	//Important because it skips empty iterators!  (which is super-important for hasNext!! :O )
	protected void advanceIterators()
	{
		while (true)
		{
			if (!this.underlying.hasNext())
			{
				this.current = null;
				return;
			}
			else
			{
				this.current = this.underlying.next();
				
				if (this.current.hasNext())
					return;
				else
					continue;
			}
		}
	}
	
	
	@Override
	public boolean hasNext()
	{
		if (this.current == null)
		{
			return false;
		}
		else
		{
			if (!this.current.hasNext())
				throw new ImpossibleException();
			
			return true;
		}
	}
	
	@Override
	public E next()
	{
		if (!hasNext())
			throw new NoSuchElementException();
		
		if (this.current == null)
			throw new ImpossibleException();
		
		this.bof = false;
		this.alreadyRemoved = false;
		
		while (true)
		{
			if (this.current.hasNext())
			{
				this.bofInCurrent = false;
				E e = this.current.next();
				
				if (!this.current.hasNext())
					advanceIterators();
				
				return e;
			}
			else
			{
				this.previous = this.current;
				this.bofInCurrent = true;
				
				advanceIterators();
			}
		}
	}
	
	
	//TODO unit-test the removing feature!!!
	@Override
	public void remove()
	{
		if (this.bof || this.alreadyRemoved)
			throw new IllegalStateException();
		
		if (this.bofInCurrent)
		{
			if (this.previous == null)
				throw new ImpossibleException();
			
			this.previous.remove();
		}
		else
		{
			this.current.remove();
		}
		
		this.alreadyRemoved = true;
	}
}
