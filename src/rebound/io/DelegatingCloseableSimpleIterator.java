package rebound.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import rebound.exceptions.StopIterationReturnPath;
import rebound.util.collections.SimpleIterator;

public class DelegatingCloseableSimpleIterator<E>
implements CloseableSimpleIterator<E>
{
	protected final Closeable closeable;
	protected final SimpleIterator<E> underlying;
	
	public DelegatingCloseableSimpleIterator(Closeable closeable, SimpleIterator<E> underlying)
	{
		this.closeable = closeable;
		this.underlying = underlying;
	}
	
	public void close() throws IOException
	{
		closeable.close();
	}
	
	
	
	public boolean equals(Object o)
	{
		return underlying.equals(o);
	}
	
	public int hashCode()
	{
		return underlying.hashCode();
	}
	
	@Override
	public String toString()
	{
		return underlying.toString();
	}
	
	
	
	
	public E nextrp() throws StopIterationReturnPath
	{
		return underlying.nextrp();
	}
	
	public void drain()
	{
		underlying.drain();
	}
	
	public <C> C[] drainToNewArray(Class<C> componentType)
	{
		return underlying.drainToNewArray(componentType);
	}
	
	public Object[] drainToNewArray()
	{
		return underlying.drainToNewArray();
	}
	
	public int drainTo(Collection<? super E> sink)
	{
		return underlying.drainTo(sink);
	}
	
	public Iterator<E> toIterator()
	{
		return underlying.toIterator();  //Todo wrap in a DelegatingCloseableIterator if it's not already an instanceof CloseableIterator   (once we make CloseableIterator XD')
	}
}
