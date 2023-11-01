package rebound.io.iio;

import java.io.IOException;
import rebound.io.TransparentByteArrayInputStream;
import rebound.util.collections.Slice;

/**
 * Just an {@link InputByteStream} that doesn't throw {@link IOException}s
 * @see TransparentByteArrayInputStream
 */
public interface GuaranteedBasicInputByteStream
extends BasicInputByteStream
{
	public int read();
	
	
	
	public default int read(byte[] b)
	{
		return read(b, 0, b.length);
	}
	
	public default int read(Slice<byte[]> b)
	{
		return read(b.getUnderlying(), b.getOffset(), b.getLength());
	}
	
	public int read(byte[] b, int off, int len);
	
	
	
	
	
	public long skip(long numberOfBytesToSkip);
}
