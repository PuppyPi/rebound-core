package rebound.io.iio;

import java.io.IOException;
import rebound.util.collections.Slice;

/**
 * A Java Interface abstracting the essence of input in {@link java.io.InputStream}, without closing or etc.  :3
 * @author Puppy Pie ^w^
 */
public interface BasicInputByteStream
{
	public int read() throws IOException;
	
	
	
	public default int read(byte[] b) throws IOException
	{
		return read(b, 0, b.length);
	}
	
	public default int read(Slice<byte[]> b) throws IOException
	{
		return read(b.getUnderlying(), b.getOffset(), b.getLength());
	}
	
	public int read(byte[] b, int off, int len) throws IOException;
	
	
	
	
	
	public long skip(long numberOfBytesToSkip) throws IOException;
}
