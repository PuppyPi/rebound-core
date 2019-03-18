/*
 * Created on Jun 12, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.util.adapters.jre.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import rebound.io.streaming.api.BlockWriteStream;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.ByteBlockWriteStream;
import rebound.io.streaming.util.implhelp.AbstractStream;
import rebound.io.streaming.util.usagehelp.StreamUsageUtilities;

/**
 * This wraps a {@link InputStream java.io.OutputStream} as a {@link ByteBlockWriteStream rebound.io.streaming.RawWriteStream}.
 * Since {@link OutputStream} doesn't support fixed-size data stores, this will never recognize EOF (ie, even if EOF is reached, {@link #isEOF()} will never be <code>true</code>; {@link IOException}s will just be thrown every <code>write()</code>).
 * <br>Note: {@link StreamUsageUtilities#getAsRIOWriteStream(OutputStream)} should normally be used to instantiate this class for performance reasons.
 * <br>Note: This class must violate the {@link BlockWriteStream} spec because the {@link OutputStream} interface doesn't provide information on the partial write in the case that it illegally extended past EOF. (it just throws an {@link EOFException})
 * @author RProgrammer
 */
public class OutputStreamWrapper
extends AbstractStream
implements ByteBlockWriteStream
{
	protected OutputStream out;
	protected boolean pastEOF = false;
	
	public OutputStreamWrapper()
	{
		super();
	}
	
	public OutputStreamWrapper(OutputStream out)
	{
		super();
		setOutputStream(out);
	}
	
	
	/**
	 * Resets the {@link #isClosed() closed state} and the {@link #getOutputStream() underlying OutputStream}.
	 */
	public void setOutputStream(OutputStream out)
	{
		this.out = out;
		this.closed = false;
		this.pastEOF = false;
	}
	
	
	
	@Override
	protected void close0() throws IOException
	{
		this.out.close();
	}
	
	@Override
	public boolean isEOF() throws IOException, ClosedStreamException
	{
		requireOpen();
		return this.pastEOF;
	}
	
	@Override
	public void write(byte unit) throws EOFException, IOException, ClosedStreamException
	{
		requireOpen();
		try
		{
			this.out.write(unit);
		}
		catch (EOFException exc)
		{
			this.pastEOF = true;
			throw exc;
		}
	}
	
	@Override
	public int write(byte[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException
	{
		requireOpen();
		try
		{
			this.out.write(buffer, offset, requestedLength);
		}
		catch (EOFException exc)
		{
			this.pastEOF = true;
			throw exc; //We can't return a number since we don't know how much was written
		}
		return requestedLength;
	}
	
	@Override
	public void flush() throws IOException, ClosedStreamException
	{
		requireOpen();
		this.out.flush();
	}
	
	
	
	private static final byte[] SKIP_BUFFER = new byte[4096];
	
	@Override
	public long skip(long maxLength) throws IOException, ClosedStreamException
	{
		requireOpen();
		
		try
		{
			long original = maxLength;
			
			while (maxLength > 0)
			{
				this.out.write(SKIP_BUFFER, 0, maxLength < SKIP_BUFFER.length ? (int)maxLength : SKIP_BUFFER.length);
				maxLength -= maxLength < SKIP_BUFFER.length ? maxLength : SKIP_BUFFER.length;
			}
			
			return original;
		}
		catch (EOFException exc)
		{
			this.pastEOF = true;
			throw exc; //We can't return a number since we don't know how much was written
		}
	}
	
	
	
	public OutputStream getOutputStream()
	{
		return this.out;
	}
}
