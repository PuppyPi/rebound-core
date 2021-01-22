package rebound.io.util;

import static java.util.Objects.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.util.collections.CollectionUtilities.*;
import java.io.IOException;
import java.io.InputStream;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.exceptions.ClosedIOException;
import rebound.exceptions.ImpossibleException;
import rebound.io.AbstractOutputStream;
import rebound.util.collections.ArrayUtilities;
import rebound.util.functional.FunctionInterfaces.UnaryProcedureBoolean;

public class EquivalenceComparingOutputStream
extends AbstractOutputStream
{
	protected final InputStream reference;
	protected final UnaryProcedureBoolean handleResult;
	
	protected boolean result = true;
	protected boolean closed = false;
	
	
	public EquivalenceComparingOutputStream(InputStream reference, UnaryProcedureBoolean handleResult)
	{
		this.reference = requireNonNull(reference);
		this.handleResult = requireNonNull(handleResult);
	}
	
	/**
	 * Handles a Comparison Mismatch (false) result by making the write()'s throw {@link EquivalenceComparisonFailureIOException}
	 * (you can still use {@link #getResult()} though, to be sure :3 )
	 */
	public EquivalenceComparingOutputStream(InputStream reference)
	{
		this.reference = requireNonNull(reference);
		
		this.handleResult = r ->
		{
			if (!r)
				close();
		};
	}
	
	
	
	
	
	final byte[] buff = new byte[4096];
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		rangeCheckIntervalByLength(b.length, off, len);
		
		if (closed)
			throw new ClosedIOException();
		
		while (len > 0)
		{
			int a = least(buff.length, len);
			
			int amt = reference.read(buff, 0, a);
			
			if (amt != -1 && amt > a)
				throw new ImpossibleException();
			
			if (amt == -1 || !ArrayUtilities.equals(buff, 0, b, off, amt))
			{
				if (result)  //only call handleResult once ^^'
				{
					result = false;
					handleResult.f(false);
					
					if (closed)  //this is how they cause it to stop/abort prematurely :3
						throw new EquivalenceComparisonFailureIOException();
				}
			}
			
			
			off += amt;
			len -= amt;
		}
	}
	
	
	@Override
	public void close()
	{
		if (!closed)
		{
			closed = true;
			
			if (result)  //only call handleResult once ^^'
			{
				handleResult.f(true);
			}
		}
	}
	
	public boolean isClosed()
	{
		return closed;
	}
	
	@Override
	public void flush()
	{
		//nothing X3
	}
	
	
	
	
	public boolean getResult()
	{
		return result;
	}
	
	
	@ImplementationTransparency
	public InputStream getReference()
	{
		return reference;
	}
}
