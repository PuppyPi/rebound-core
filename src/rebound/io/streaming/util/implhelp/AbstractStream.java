/*
 * Created on May 17, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.util.implhelp;

import java.io.IOException;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.Stream;

/**
 * This utility class implements the {@link Stream#isClosed()} logic, delegating the actual closing operation to {@link #close0()}
 * Note that you still need to check if the stream is closed before doing things, a requirement for which a {@link #requireOpen() utility} is provided.
 * @author RProgrammer
 */
public abstract class AbstractStream
implements Stream
{
	protected boolean closed = false;
	
	
	@Override
	public void close() throws IOException
	{
		//Only do anything if the closed flag is not already set (which is why we must be careful in case we're a decorator stream)
		if (!this.closed)
		{
			try
			{
				close0();
			}
			catch (IOException exc)
			{
				//If IOException if thrown, set the closed flag
				this.closed = true;
				throw exc;
			}
			//If something else is thrown, don't set the closed flag (so the next close() will still attempt to perform a close operation)
			
			//If nothing is thrown, then set the closed flag
			this.closed = true;
		}
	}
	
	@Override
	public boolean isClosed()
	{
		return this.closed;
	}
	
	
	/**
	 * Unsets the closed flag.
	 */
	protected void reset()
	{
		this.closed = false;
	}
	
	/**
	 * This should perform the actual flushing and closing operation on the stream, without worrying about {@link #isClosed()}.
	 */
	protected abstract void close0() throws IOException;
	
	
	/**
	 * @throws ClosedStreamException If {@link #isClosed()}
	 */
	protected void requireOpen() throws ClosedStreamException
	{
		if (this.closed)
			throw new ClosedStreamException();
	}
}
