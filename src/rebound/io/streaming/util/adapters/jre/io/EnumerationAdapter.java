/*
 * Created on Mar 6, 2011
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.util.adapters.jre.io;

import java.io.EOFException;
import java.io.IOException;
import java.util.Enumeration;
import rebound.exceptions.ImpossibleException;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.ReferenceUnitReadStream;

/**
 * The exposed {@link Enumeration} will return <code>null</code> after EOF, regardless as to whether <code>null</code>s were valid elements.
 * {@link #hasMoreElements()} will always work.
 * Note that all thrown exceptions except for {@link EOFException} on read() are assumed to be {@link ImpossibleException supposedly impossible}.
 * @author RProgrammer
 */
public class EnumerationAdapter<E>
implements Enumeration<E>
{
	protected ReferenceUnitReadStream<E> base;
	
	public EnumerationAdapter()
	{
		super();
	}
	
	public EnumerationAdapter(ReferenceUnitReadStream<E> base)
	{
		super();
		this.base = base;
	}
	
	
	
	@Override
	public boolean hasMoreElements()
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
	public E nextElement()
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
	
	
	public ReferenceUnitReadStream<E> getBase()
	{
		return this.base;
	}
	
	public void setBase(ReferenceUnitReadStream<E> base)
	{
		this.base = base;
	}
}
