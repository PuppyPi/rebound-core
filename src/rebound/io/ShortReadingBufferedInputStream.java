package rebound.io;

import static rebound.math.SmallIntegerMathUtilities.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import rebound.annotations.semantic.simpledata.Positive;

/**
 * This is useful for, say, tunnelling where you MUST NOT use {@link BufferedInputStream} because that can TOTALLY mess up the flush information by waiting for an indefinite
 * amount of time before it fills up its buffer.  The sender of the data might NEED that data to be flushed straight to the other side!  Possibly because the
 * other side is waiting for it and won't send its data until it gets it!!  And if we're waiting on it to send more to fill up the buffer, and its waiting on the
 * other side, and the other side is waiting on us, that's a deadlock!!!
 * 
 * Again, for clarity,
 * Say you have Side A and Side B, and there's full-duplex communication between them (information can pass in both directions completely arbitrarily, in a multithreaded
 * way!).  Now say Side B is like a Web Server, just responding to what Side A sends and sending something as soon as it receives a request.  And say you're a
 * tunnel/relay/proxy between Side A and B, doing nothing but forwarding the traffic in both direction (eg, to get around a NAT router on Side A or something perhaps).
 * So look at this possible situation!!:
 * • Side A sends a request that's say, 100 bytes.
 * • Your {@link BufferedInputStream} blocks, read()ing and waiting for Side A to send enough bytes to fill up its buffer.
 * • So you're not forwarding it to Side B!
 * • Side B blocks, sends nothing until it receives some request message!
 * • Side A *might* send more data—perhaps even enough to fill up your {@link BufferedInputStream} buffer!  *But not until it gets a response from Side B*
 * • So you're stuck waiting on Side A, Side A is stuck waiting on Side B, Side B is stuck waiting on you!!  **Deadlock!!!*
 * 
 * • {@link ShortReadingBufferedInputStream} breaks this deadlock by returning from its {@link #read(byte[], int, int) read()} method with no more than a single read
 * from the underlying pipe from Side A!  In other words, it doesn't try to *fill* its own buffer, it just offers the underlying stream/pipe the opportunity to fill
 * the buffer in a single read() call, and *if* it gets more than you asked this class for, *then* it will buffer, and so you can make a thousand tiny read() calls
 * (like for single bytes or 16/32/64-bit integers or such) and this class will handle it performantly, reading, say, chewing on say, single TCP packet from the
 * underlying stream, *but it will never try to read more than one TCP packet!!* (technically, more than one read() call to the underlying stream!).
 * 
 * So ***absolutely only*** use {@link ShortReadingBufferedInputStream}, not {@link BufferedInputStream} for forwarding duplex traffic, because it will never make
 * more than one read() call to the underlying stream, it will only allow it to be extra big, and rely on the underlying stream to return a short read rather than
 * block, and then buffer any extra data so that future calls will just use that up directly.
 * 
 * Ie, a read() call to this class only makes 0 or 1 read() calls to the underlying stream, never more than 1! (possibly of different parameters, but still)
 * 
 * 
 * The only time your can safely use a buffer-filling buffering decorator like {@link BufferedInputStream} is when you know the data is just going to be flushed right
 * through like a download from a file on a hard drive, and never pause or wait on a cycle of data flow!  (Then it is (slightly??) more efficient to use that kind!)
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
				int r = underlying.read(buffer, 0, buffer.length);
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
