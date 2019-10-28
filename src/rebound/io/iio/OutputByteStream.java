package rebound.io.iio;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;

/**
 * An Java Interface abstracting the essence of {@link java.io.OutputStream} :3
 * @author Puppy Pie ^w^
 */
public interface OutputByteStream
extends Flushable, Closeable
{
	public void write(int b) throws IOException;
	
	
	
	public default void write(byte[] b) throws IOException
	{
		write(b, 0, b.length);
	}
	
	public default void write(Slice<byte[]> b) throws IOException
	{
		write(b.getUnderlying(), b.getOffset(), b.getLength());
	}
	
	public default void write(ByteList b) throws IOException
	{
		write(b.toByteArraySlicePossiblyLive());
	}
	
	public void write(byte[] b, int off, int len) throws IOException;
}
