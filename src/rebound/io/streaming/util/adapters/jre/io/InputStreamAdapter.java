/*
 * Created on Jun 13, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.util.adapters.jre.io;

import static rebound.bits.Unsigned.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.ByteBlockReadStream;
import rebound.io.streaming.util.implhelp.StreamImplUtilities.IndolentByteBlockReadStream;
import rebound.io.streaming.util.usagehelp.StreamUsageUtilities;

/**
 * Given a {@link ByteBlockReadStream}, this exposes a Java Standard IO interface to it, as an {@link InputStream}.
 * <br>Note: {@link StreamUsageUtilities#getAsJREInputStream(ByteBlockReadStream)} should normally be used to instantiate this class for performance reasons.
 * @author RProgrammer
 */
public class InputStreamAdapter
extends InputStream
{
	protected ByteBlockReadStream in;
	
	public InputStreamAdapter()
	{
		super();
	}
	
	public InputStreamAdapter(ByteBlockReadStream in)
	{
		super();
		this.in = in;
	}
	
	
	
	public void resetUnderlying(ByteBlockReadStream in)
	{
		this.in = in;
	}
	
	public ByteBlockReadStream getUnderlying()
	{
		return this.in;
	}
	
	
	
	
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof InputStreamAdapter)
			return this.in.equals(((InputStreamAdapter)obj).in);
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return this.in.hashCode() ^ 0x23FFE097; //Chosen by fair dice roll--guaranteed to be random!
	}
	
	
	
	
	@Override
	public int available() throws IOException
	{
		//We don't support this capability (yet ;)
		return 0;
	}
	
	@Override
	public void close() throws IOException
	{
		this.in.close();
	}
	
	@Override
	public boolean markSupported()
	{
		//Todo when Mark/Reset is added as a capability interface, check instanceof and delegate to it
		return false;
	}
	
	@Override
	public synchronized void mark(int readlimit)
	{
	}
	
	@Override
	public synchronized void reset() throws IOException
	{
		super.reset();
	}
	
	
	
	@Override
	public int read(byte[] b) throws IOException
	{
		return read(b, 0, b.length);
	}
	
	@Override
	public int read() throws IOException
	{
		try
		{
			if (this.in.isEOF())
				return -1;
			
			try
			{
				return upcast(this.in.read());  //DON'T FORGET THE UPCAST OR BYTES OVER 127 WILL BE CORRUPTED AND MAY SIGNAL EOF! XD!!
			}
			catch (EOFException exc)
			{
				return -1;
			}
		}
		catch (ClosedStreamException exc)
		{
			throw new IOException("The stream has been closed.");
		}
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		if (this.in.isEOF())
			return -1;
		
		try
		{
			if (this.in instanceof IndolentByteBlockReadStream)
				return ((IndolentByteBlockReadStream)this.in).readIndolent(b, off, len);
			else
				return this.in.read(b, off, len);
		}
		catch (ClosedStreamException exc)
		{
			throw new IOException("The stream has been closed.");
		}
	}
	
	@Override
	public long skip(long n) throws IOException
	{
		try
		{
			if (this.in.isEOF())
				return 0; //I'll stick with InputStream.class's implementation (returning 0, not -1 like read()) just to be safe
			
			return this.in.skip(n);
		}
		catch (ClosedStreamException exc)
		{
			throw new IOException("The stream has been closed.");
		}
	}
}
