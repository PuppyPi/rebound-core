/*
 * Created on Mar 18, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections;

import static rebound.util.collections.CollectionUtilities.*;
import java.util.Iterator;

public class ConcatenatingIterable<E>
implements Iterable<E>
{
	protected Iterable<Iterable<E>> underlying;
	
	
	public ConcatenatingIterable()
	{
	}
	
	public ConcatenatingIterable(Iterable<Iterable<E>> underlying)
	{
		this.underlying = underlying;
	}
	
	
	public Iterable<Iterable<E>> getUnderlying()
	{
		return this.underlying;
	}
	
	public void setUnderlying(Iterable<Iterable<E>> underlying)
	{
		this.underlying = underlying;
	}
	
	
	@Override
	public Iterator<E> iterator()
	{
		return new ConcatenatingIterator<E>(map((Mapper<Iterable<E>, Iterator<E>>)Iterable::iterator, this.underlying.iterator()).toIterator());
	}
}
