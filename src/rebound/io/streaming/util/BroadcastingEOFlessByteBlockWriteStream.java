package rebound.io.streaming.util;

import java.io.EOFException;
import java.io.IOException;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.exceptions.UnreachableCodeException;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.ByteBlockWriteStream;
import rebound.io.streaming.util.implhelp.AbstractStream;
import rebound.util.BasicExceptionUtilities;

/**
 * EOFless-only because if they reach EOF at different times they're no longer identical!  \o/
 */
public class BroadcastingEOFlessByteBlockWriteStream
extends AbstractStream
implements ByteBlockWriteStream
{
	protected ByteBlockWriteStream[] underlyings;
	
	public BroadcastingEOFlessByteBlockWriteStream()
	{
	}
	
	public BroadcastingEOFlessByteBlockWriteStream(@LiveValue ByteBlockWriteStream... underlyings)
	{
		this.underlyings = underlyings;
	}
	
	
	
	@Override
	public boolean isEOF() throws IOException, ClosedStreamException
	{
		return false;
	}
	
	@Override
	public void flush() throws IOException, ClosedStreamException
	{
		for (ByteBlockWriteStream u : this.underlyings)
			u.flush();
	}
	
	@Override
	public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
	{
		for (ByteBlockWriteStream u : this.underlyings)
		{
			long r = u.skip(amount);
			if (r != amount)
				throw new IllegalStateException("An EOF-ful stream was given to a broadcaster!!");
		}
		
		return amount;
	}
	
	@Override
	public void write(byte unit) throws EOFException, IOException, ClosedStreamException
	{
		for (ByteBlockWriteStream u : this.underlyings)
		{
			try
			{
				u.write(unit);
			}
			catch (EOFException exc)
			{
				throw new IllegalStateException("An EOF-ful stream was given to a broadcaster!!");
			}
		}
	}
	
	@Override
	public int write(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
	{
		for (ByteBlockWriteStream u : this.underlyings)
		{
			long r = u.write(buffer, offset, length);
			if (r != length)
				throw new IllegalStateException("An EOF-ful stream was given to a broadcaster!!");
		}
		
		return length;
	}
	
	@Override
	protected void close0() throws IOException
	{
		Throwable tt = null;
		
		for (ByteBlockWriteStream u : this.underlyings)
		{
			try
			{
				u.close();
			}
			catch (Throwable t)
			{
				if (tt == null)
					tt = t;
				else
					tt.addSuppressed(t);
			}
		}
		
		if (tt != null)
		{
			BasicExceptionUtilities.throwGeneralThrowableIfPossible(tt);
			throw new UnreachableCodeException();
		}
	}
	
	
	
	
	
	@LiveValue
	public ByteBlockWriteStream[] getUnderlyings()
	{
		return this.underlyings;
	}
	
	public void setUnderlyings(@LiveValue ByteBlockWriteStream[] underlyings)
	{
		this.underlyings = underlyings;
	}
}
