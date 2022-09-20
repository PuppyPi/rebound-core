package rebound.io.iio;

import java.io.IOException;
import rebound.io.TransparentByteArrayOutputStream;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;

/**
 * Just an {@link OutputByteStream} that doesn't throw {@link IOException}s
 * @see TransparentByteArrayOutputStream
 */
public interface GuaranteedBasicOutputByteStream
extends BasicOutputByteStream
{
	public void write(int b);
	
	
	
	public default void write(byte[] b)
	{
		write(b, 0, b.length);
	}
	
	public default void write(Slice<byte[]> b)
	{
		write(b.getUnderlying(), b.getOffset(), b.getLength());
	}
	
	public default void write(ByteList b)
	{
		write(b.toByteArraySlicePossiblyLive());
	}
	
	public void write(byte[] b, int off, int len);
}
