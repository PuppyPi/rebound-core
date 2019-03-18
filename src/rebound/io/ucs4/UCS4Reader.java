package rebound.io.ucs4;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.nio.IntBuffer;

/**
 * Exactly analogous to {@link Reader}, but using 4-byte int's instead of 2-byte char's to represent Unicode characters!
 * ( also it's an interface! :D )
 * 
 * @author Puppy Pie ^w^
 */
public interface UCS4Reader
extends Closeable
{
	public long read() throws IOException;
	
	
	
	public default int read(int[] cbuf) throws IOException
	{
		return read(cbuf, 0, cbuf.length);
	}
	
	public int read(int[] cbuf, int off, int len) throws IOException;
	
	
	public default int read(IntBuffer target) throws IOException
	{
		if (target.hasArray())
		{
			//Why doesn't the JRE do this???
			//(oh, because non-byte buffer probably are almost always made from byte buffers instead of heap arrays of their actual type!)
			//(but then..is that really a good thing??)
			//(apart from direct IO, buffers can nicely represent an array plus the "offset,length" information we're *constantly* passing along with them! XDD )
			return this.read(target.array(), target.arrayOffset() + target.position(), target.remaining());
		}
		else
		{
			int len = target.remaining();
			int[] cbuf = new int[len];
			int n = read(cbuf, 0, len);
			if (n > 0)
				target.put(cbuf, 0, n);
			return n;
		}
	}
	
	
	
	
	
	
	
	
	public long skip(long n) throws IOException;
	
	
	
	
	
	
	
	
	
	public default boolean ready() throws IOException
	{
		return false;
	}
	
	
	
	public default boolean markSupported()
	{
		return false;
	}
	
	public default void mark(int readAheadLimit) throws IOException
	{
		throw new IOException("mark() not supported");
	}
	
	public default void reset() throws IOException
	{
		throw new IOException("reset() not supported");
	}
	
	
	
	@Override
	public void close() throws IOException;
}
