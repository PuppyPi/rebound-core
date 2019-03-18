/*
 * Created on Dec 11, 2010
 * 	by the great Eclipse(c)
 */
package rebound.util.collections;

import java.util.Collection;
import java.util.Iterator;

public class SingletonIterator<E>
implements Iterator<E>
{
	protected boolean consumed;
	protected E element;
	protected Collection<E> removeReceiver;
	
	public SingletonIterator(E element, Collection<E> removeReceiver)
	{
		super();
		init(element, removeReceiver);
	}
	
	public SingletonIterator(E element)
	{
		super();
		init(element);
	}
	
	public void init(E element, Collection<E> removeReceiver)
	{
		this.element = element;
		this.consumed = false;
		this.removeReceiver = removeReceiver;
	}
	
	public void init(E element)
	{
		init(element, null);
	}
	
	
	
	@Override
	public boolean hasNext()
	{
		return !this.consumed;
	}
	
	@Override
	public E next()
	{
		if (this.consumed)
		{
			return null;
		}
		else
		{
			this.consumed = true;
			return this.element;
		}
	}
	
	
	
	@Override
	public void remove()
	{
		if (this.removeReceiver == null)
			throw new UnsupportedOperationException();
		else
		{
			if (!this.consumed)
				throw new IllegalStateException();
			else
				this.removeReceiver.remove(this.element);
		}
	}
}
