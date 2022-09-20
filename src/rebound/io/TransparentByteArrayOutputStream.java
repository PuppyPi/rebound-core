package rebound.io;

import java.util.Arrays;
import java.util.List;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.io.iio.GuaranteedBasicOutputByteStream;
import rebound.io.iio.OutputByteStream;
import rebound.util.collections.ArrayUtilities;
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
		this.buff = initialCapacity == 0 ? ArrayUtilities.EmptyByteArray : new byte[initialCapacity];
	}
	
	public void ensureCapacity(int minsize)
	{
		int oldsize = this.buff != null ? this.buff.length : 0;
		
		if (oldsize >= minsize)
		{
			if (this.buff == null)
				this.buff = new byte[0];
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
		
		if (this.buff != null)
		{
			byte[] newbuff = new byte[newsize];
			System.arraycopy(this.buff, 0, newbuff, 0, oldsize);
			this.buff = newbuff;
		}
		else
		{
			this.buff = new byte[newsize];
		}
	}
	
	@Override
	public void write(int b)
	{
		int oldcount = this.count;
		ensureCapacity(oldcount+1);
		this.buff[oldcount] = (byte)b;
		this.count = oldcount + 1;
	}
	
	@Override
	public void write(byte b[], int off, int len)
	{
		if (off < 0 || len < 0 || off + len > b.length)
			throw new IndexOutOfBoundsException();
		else if (len == 0)
			return;
		
		int oldcount = this.count;
		ensureCapacity(oldcount+len);
		System.arraycopy(b, off, this.buff, oldcount, len);
		this.count = oldcount + len;
	}
	
	/**
	 * Use this with <i>caution</i> as it can be easily invalidated by the {@link #ensureCapacity(int)} method.
	 * Note also that it probably has a longer length than the number of logical bytes as given by {@link #getSize()} (ie, the difference between a {@link List}'s capacity and size)
	 */
	@LiveValue
	public byte[] getRawByteArray()
	{
		return this.buff;
	}
	
	/**
	 * @return how much was copied (the same as {@link #getSize()})
	 * @throws IllegalArgumentException  if the provided byte[] was too small.
	 */
	public int copyInto(byte[] buffer, int offset)
	{
		if (buffer.length - offset < this.count)
			throw new IllegalArgumentException("provided buffer is too small!");
		
		System.arraycopy(this.buff, 0, buffer, offset, this.count);
		
		return this.count;
	}
	
	
	/**
	 * Gets the logical number of bytes--the number of bytes written.
	 */
	public int getSize()
	{
		return this.count;
	}
	
	/**
	 * Create a copy of the buffer that is also trimmed to size.
	 */
	@SnapshotValue
	public byte[] toByteArray()
	{
		return Arrays.copyOf(this.buff, this.count);
	}
	
	/**
	 * This is just like {@link #toByteArray()} except it doesn't make an extra copy!!
	 */
	@LiveValue
	public Slice<byte[]> toLiveByteArraySlice()
	{
		return new Slice<>(this.buff, 0, this.count);
	}
	
	
	
	/**
	 * Deletes all the data written!!  Not resets the cursor to overwrite data!!
	 */
	public void reset()
	{
		this.count = 0;
	}
	
	
	
	@Override
	public void flush()
	{
	}
	
	@Override
	public void close()
	{
		freeThings();
	}
	
	
	/**
	 * The stream can still be used after this is called!  It just frees up memory by releasing the underlying byte[] :3
	 */
	public void freeThings()
	{
		this.buff = ArrayUtilities.EmptyByteArray;
		this.count = 0;
	}
}
