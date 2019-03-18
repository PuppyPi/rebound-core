/*
 * Created on Mar 6, 2011
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.util.adapters.jre.jcf;

import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import rebound.exceptions.ImpossibleException;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.ReferenceUnitReadStream;

/**
 * The exposed {@link Iterator} will return <code>null</code> after EOF, regardless as to whether <code>null</code>s were valid elements.
 * {@link #hasNext()} will always work.
 * {@link #remove()} always throws {@link UnsupportedOperationException}.
 * Note that all thrown exceptions except for {@link EOFException} on read() are assumed to be {@link ImpossibleException supposedly impossible}.
 * @author RProgrammer
 */
public class IteratorAdapter<E>
implements Iterator<E>
{
	protected ReferenceUnitReadStream<E> base;
	
	public IteratorAdapter()
	{
		super();
	}
	
	public IteratorAdapter(ReferenceUnitReadStream<E> base)
	{
		super();
		this.base = base;
	}
	
	
	
	@Override
	public boolean hasNext()
	{
		try
		{
			return !this.base.isEOF();
		}
		catch (ClosedStreamException exc)
		{
			throw new ImpossibleException(exc);
		}
		catch (IOException exc)
		{
			throw new ImpossibleException(exc);
		}
	}
	
	@Override
	public E next()
	{
		try
		{
			return this.base.read();
		}
		catch (EOFException exc)
		{
			return null;
		}
		catch (ClosedStreamException exc)
		{
			throw new ImpossibleException(exc);
		}
		catch (IOException exc)
		{
			throw new ImpossibleException(exc);
		}
	}
	
	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}
	
	
	
	public ReferenceUnitReadStream<E> getBase()
	{
		return this.base;
	}
	
	public void setBase(ReferenceUnitReadStream<E> base)
	{
		this.base = base;
	}
}
