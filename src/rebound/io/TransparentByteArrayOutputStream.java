package rebound.io;

import java.util.Arrays;
import java.util.List;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.io.iio.GuaranteedBasicOutputByteStream;
import rebound.io.iio.OutputByteStream;
import rebound.util.collections.Slice;

//Todo a version that uses rebound.util.growth.Grower instead of our hardcoded algorithm XD''

public class TransparentByteArrayOutputStream
extends GuaranteedOutputStream
implements GuaranteedBasicOutputByteStream, OutputByteStream
{
	protected byte[] buff;
	protected int count;
	
	public TransparentByteArrayOutputStream()
	{
		this(32);
	}
	
	public TransparentByteArrayOutputStream(int initialCapacity)
	{
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Negative initial size: " + initialCapacity);
		buff = new byte[initialCapacity];
	}
	
	public void ensureCapacity(int minsize)
	{
		int oldsize = buff != null ? buff.length : 0;
		
		if (oldsize >= minsize)
		{
			if (buff == null)
				buff = new byte[0];
			return;
		}
		
		//Hardcoded minimum of +64 bytes, +10%, and minsize
		int newsize = 0;
		if (oldsize / 10 < 64)
			newsize = oldsize + 64;
		else
			newsize = oldsize + oldsize / 10;
		if (newsize < minsize)
			newsize = minsize;
		
		if (buff != null)
		{
			byte[] newbuff = new byte[newsize];
			System.arraycopy(buff, 0, newbuff, 0, oldsize);
			buff = newbuff;
		}
		else
		{
			buff = new byte[newsize];
		}
	}
	
	public void write(int b)
	{
		int oldcount = count;
		ensureCapacity(oldcount+1);
		buff[oldcount] = (byte)b;
		count = oldcount + 1;
	}
	
	public void write(byte b[], int off, int len)
	{
		if (off < 0 || len < 0 || off + len > b.length)
			throw new IndexOutOfBoundsException();
		else if (len == 0)
			return;
		
		int oldcount = count;
		ensureCapacity(oldcount+len);
		System.arraycopy(b, off, buff, oldcount, len);
		count = oldcount + len;
	}
	
	/**
	 * Use this with <i>caution</i> as it can be easily invalidated by the {@link #ensureCapacity(int)} method.
	 * Note also that it probably has a longer length than the number of logical bytes as given by {@link #getSize()} (ie, the difference between a {@link List}'s capacity and size)
	 */
	@LiveValue
	public byte[] getRawByteArray()
	{
		return buff;
	}
	
	public void copyInto(byte[] buffer, int offset)
	{
		if (buffer.length - offset < count)
			throw new IllegalArgumentException("provided buffer is too small!");
		
		System.arraycopy(this.buff, 0, buffer, offset, count);
	}
	
	
	/**
	 * Gets the logical number of bytes--the number of bytes written.
	 */
	public int getSize()
	{
		return count;
	}
	
	/**
	 * Create a copy of the buffer that is also trimmed to size.
	 */
	@SnapshotValue
	public byte[] toByteArray()
	{
		return Arrays.copyOf(buff, count);
	}
	
	/**
	 * This is just like {@link #toByteArray()} except it doesn't make an extra copy!!
	 */
	@LiveValue
	public Slice<byte[]> toLiveByteArraySlice()
	{
		return new Slice<>(buff, 0, count);
	}
	
	
	
	/**
	 * Deletes all the data written!!  Not resets the cursor to overwrite data!!
	 */
	public void reset()
	{
		count = 0;
	}
	
	
	
	public void flush()
	{
	}
	
	public void close()
	{
		freeThings();
	}
	
	
	public void freeThings()
	{
		this.buff = null;
	}
}
