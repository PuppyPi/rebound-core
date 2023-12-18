package rebound.io;

import static rebound.math.SmallIntegerMathUtilities.*;
import java.io.IOException;
import java.io.InputStream;
import rebound.annotations.semantic.simpledata.Positive;

/**
 * This is useful for, say, tunnelling where you MUST NOT use BufferedInputStream because that can TOTALLY mess up the flush information by waiting for an indefinite
 * amount of time before it fills up its buffer.  The sender of the data might NEED that data to be flushed straight to the other side!  Possibly because the
 * other side is waiting for it and won't send its data until it gets it!!  And if we're waiting on it to send more to fill up the buffer, and its waiting on the
 * other side, and the other side is waiting on us, that's a deadlock!!!
 * 
 * So ***absolutely only*** use ShortReadingBufferedInputStream, not BufferedInputStream because it will never make more than one read() call to the underlying
 * stream, it will only allow it to be extra big, and rely on the underlying stream to return a short read rather than block, and then buffer any extra data
 * so that future calls will just use that up directly.
 * 
 * Ie, a read() call to it only makes 0 or 1 read() calls to the underlying (possibly of different parameters, but still), never more than 1!
 */
public class ShortReadingBufferedInputStream
extends InputStream
{
	protected final InputStream underlying;
	protected byte[] buffer;
	protected int bufferPosition, bufferSize;
	
	public ShortReadingBufferedInputStream(InputStream underlying, @Positive int bufferSize)
	{
		this.underlying = underlying;
		this.buffer = new byte[requirePositive(bufferSize)];
	}
	
	public ShortReadingBufferedInputStream(InputStream underlying)
	{
		this(underlying, 4096);
	}
	
	
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		if (bufferSize == 0)
		{
			if (len >= buffer.length)
				return underlying.read(b, off, len);
			else
			{
				int r = underlying.read(buffer, 0, buffer.length);
				if (r == -1)
					return r;
				else
				{
					int n = least(r, len);
					System.arraycopy(buffer, 0, b, off, n);
					bufferPosition = n;
					bufferSize = r - n;
					return n;
				}
			}
		}
		else
		{
			int n = least(bufferSize, len);
			System.arraycopy(buffer, bufferPosition, b, off, n);
			bufferPosition += n;
			bufferSize -= n;
			return n;
		}
	}
	
	
	@Override
	public int read() throws IOException
	{
		if (bufferSize == 0)
		{
			if (1 >= buffer.length)
				return underlying.read();
			else
			{
				int r = underlying.read();
				if (r == -1)
					return r;
				else
				{
					bufferPosition = 1;
					bufferSize = r - 1;
					return buffer[0];
				}
			}
		}
		else
		{
			int p = bufferPosition;
			bufferPosition = p+1;
			bufferSize--;
			return buffer[p];
		}
	}
	
	
	@Override
	public long skip(long n) throws IOException
	{
		if (n < 0)
			return 0;
		
		int s = n <= bufferSize ? (int)n : bufferSize;
		
		bufferPosition += s;
		bufferSize -= s;
		
		return s + underlying.skip(n - s);
	}
	
	
	@Override
	public int available() throws IOException
	{
		return bufferSize + underlying.available();
	}
	
	
	@Override
	public void close() throws IOException
	{
		underlying.close();
	}
}
