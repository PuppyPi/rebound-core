/*
 * Created on Jun 13, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.util.adapters.jre.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.ByteBlockWriteStream;
import rebound.io.streaming.util.usagehelp.StreamUsageUtilities;

//TODO Propagate Indolent reads through from InputStream in one and force reads from RawWriteStream

/**
 * Complementary to a {@link OutputStreamWrapper Wrapper}, an Adapter exposes an eponymous interface.
 * So, an OutputStreamAdapter exposes an {@link OutputStream} (sio) interface given a {@link ByteBlockWriteStream} instance.
 * <br>Note: {@link StreamUsageUtilities#getAsJREOutputStream(ByteBlockWriteStream)} should normally be used to instantiate this class for performance reasons.
 * @author RProgrammer
 */
public class OutputStreamAdapter
extends OutputStream
{
	protected ByteBlockWriteStream out;
	
	public OutputStreamAdapter()
	{
		super();
	}
	
	public OutputStreamAdapter(ByteBlockWriteStream out)
	{
		super();
		this.out = out;
	}
	
	public ByteBlockWriteStream getWriteStream()
	{
		return this.out;
	}
	
	public void setWriteStream(ByteBlockWriteStream out)
	{
		this.out = out;
	}
	
	
	
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof OutputStreamAdapter)
			return this.out.equals(((OutputStreamAdapter)obj).out);
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return this.out.hashCode() ^ 0x23FFE097; //Chosen by fair dice roll--guaranteed to be random!
	}
	
	
	
	
	@Override
	public void close() throws IOException
	{
		this.out.close();
	}
	
	
	
	
	
	@Override
	public void flush() throws IOException
	{
		try
		{
			this.out.flush();
		}
		catch (ClosedStreamException exc)
		{
			throw new IOException("The stream has been closed.");
		}
	}
	
	@Override
	public void write(int b) throws IOException
	{
		if (this.out.isEOF())
			throw new EOFException();
		
		try
		{
			this.out.write((byte)b);
		}
		catch (ClosedStreamException exc)
		{
			throw new IOException("The stream has been closed.");
		}
		//EOFException is already correct
	}
	
	
	@Override
	public void write(byte[] buffer, int offset, int length) throws IOException
	{
		if (this.out.isEOF())
			throw new EOFException();
		
		try
		{
			int amt = this.out.write(buffer, offset, length);
			if (length != amt)
				throw new EOFException();
		}
		catch (ClosedStreamException exc)
		{
			throw new IOException("The stream has been closed.");
		}
	}
}
