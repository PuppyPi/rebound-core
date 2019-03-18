/*
 * Created on Jun 12, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.util.adapters.jre.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.ByteBlockReadStream;
import rebound.io.streaming.util.implhelp.AbstractStream;
import rebound.io.streaming.util.implhelp.StreamImplUtilities.IndolentByteBlockReadStream;
import rebound.io.streaming.util.usagehelp.StreamUsageUtilities;

/**
 * This wraps a {@link InputStream java.io.InputStream} as a {@link ByteBlockReadStream rebound.io.streaming.RawReadStream}.
 * <br>Note: {@link StreamUsageUtilities#getAsRIOReadStream(InputStream)} should normally be used to instantiate this class for performance reasons.
 * <br>Note: In order to preserve compatibility with the
 * @author RProgrammer
 */
public class InputStreamWrapper
extends AbstractStream
implements IndolentByteBlockReadStream
{
	protected InputStream in;
	protected boolean pastEOF;
	//Todo actually implement this..somehow?? X'DD   protected byte pushBuff; //used for the dummy read to test if skip has hit EOF (since only InputStream.read can test for EOF, not InputStream.skip)
	
	
	public InputStreamWrapper()
	{
		super();
	}
	
	public InputStreamWrapper(InputStream in)
	{
		super();
		resetUnderlying(in);
	}
	
	
	
	
	/**
	 * Resets the {@link #isClosed() closed state}, {@link #isEOF() EOF state}, and the {@link #getUnderlying() underlying InputStream}.
	 */
	public void resetUnderlying(InputStream in)
	{
		this.in = in;
		this.closed = false;
		this.pastEOF = false;
	}
	
	public InputStream getUnderlying()
	{
		return this.in;
	}
	
	
	
	
	
	@Override
	public void close0() throws IOException
	{
		this.in.close();
	}
	
	@Override
	public boolean isEOF() throws IOException, ClosedStreamException
	{
		requireOpen();
		return this.pastEOF;
	}
	
	
	@Override
	public byte read() throws EOFException, IOException, ClosedStreamException
	{
		requireOpen();
		if (this.pastEOF)
			throw new EOFException();
		
		int b = this.in.read();
		
		if (b < 0)
		{
			this.pastEOF = true;
			throw new EOFException();
		}
		
		return (byte)b;  //no analog to upcast() in the Adapter needed here; downcasting works the same for signed and unsigned bytes ^www^
	}
	
	@Override
	public int readIndolent(byte[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException
	{
		requireOpen();
		if (this.pastEOF)
			return 0;
		
		int amt = this.in.read(buffer, offset, requestedLength);
		
		if (amt < 0)
		{
			this.pastEOF = true;
			return 0;
		}
		else
		{
			return amt;
		}
	}
	
	
	
	@Override
	public long skipIndolent(long maxLength) throws IOException, ClosedStreamException
	{
		requireOpen();
		
		long amt = this.in.skip(maxLength);
		
		//Even though InputStream.class doesn't implement this behavior, I can dream can't I? ;)
		if (amt < 0)
		{
			this.pastEOF = true;
			return 0;
		}
		else
		{
			return amt;
		}
	}
}
