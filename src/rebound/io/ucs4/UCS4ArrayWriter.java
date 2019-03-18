package rebound.io.ucs4;

import java.io.IOException;
import java.util.Arrays;

public class UCS4ArrayWriter
implements UCS4Writer
{
	/**
	 * The buffer where data is stored.
	 */
	protected int[] buf;
	
	/**
	 * The number of chars in the buffer.
	 */
	protected int count;
	
	
	
	/**
	 * Creates a new CharArrayWriter.
	 */
	public UCS4ArrayWriter()
	{
		this(32);
	}
	
	/**
	 * Creates a new CharArrayWriter with the specified initial size.
	 *
	 * @param initialSize
	 *            an int specifying the initial buffer size.
	 * @exception IllegalArgumentException
	 *                if initialSize is negative
	 */
	public UCS4ArrayWriter(int initialSize)
	{
		if (initialSize < 0)
		{
			throw new IllegalArgumentException("Negative initial size: " + initialSize);
		}
		this.buf = new int[initialSize];
	}
	
	
	
	
	/**
	 * Writes a character to the buffer.
	 */
	@Override
	public void write(int c)
	{
		int newcount = this.count + 1;
		if (newcount > this.buf.length)
		{
			this.buf = Arrays.copyOf(this.buf, Math.max(this.buf.length << 1, newcount));
		}
		this.buf[this.count] = (char) c;
		this.count = newcount;
	}
	
	/**
	 * Writes characters to the buffer.
	 * 
	 * @param c
	 *            the data to be written
	 * @param off
	 *            the start offset in the data
	 * @param len
	 *            the number of chars that are written
	 */
	@Override
	public void write(int[] c, int off, int len)
	{
		if ((off < 0) || (off > c.length) || (len < 0) || ((off + len) > c.length) || ((off + len) < 0))
		{
			throw new IndexOutOfBoundsException();
		}
		else if (len == 0)
		{
			return;
		}
		
		int newcount = this.count + len;
		if (newcount > this.buf.length)
		{
			this.buf = Arrays.copyOf(this.buf, Math.max(this.buf.length << 1, newcount));
		}
		System.arraycopy(c, off, this.buf, this.count, len);
		this.count = newcount;
	}
	
	
	
	/**
	 * Writes the contents of the buffer to another character stream.
	 *
	 * @param out
	 *            the output stream to write to
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public void writeTo(UCS4Writer out) throws IOException
	{
		out.write(this.buf, 0, this.count);
	}
	
	
	
	
	/**
	 * Resets the buffer so that you can use it again without throwing away the
	 * already allocated buffer.
	 */
	public void reset()
	{
		this.count = 0;
	}
	
	/**
	 * Returns a copy of the input data.
	 *
	 * @return an array of chars copied from the input data.
	 */
	public int[] toIntArray()
	{
		return Arrays.copyOf(this.buf, this.count);
	}
	
	/**
	 * Returns the current size of the buffer.
	 *
	 * @return an int representing the current size of the buffer.
	 */
	public int size()
	{
		return this.count;
	}
	
	
	
	
	//TODO! :D
	//	/**
	//	 * Converts input data to a string.
	//	 *
	//	 * @return the string.
	//	 */
	//	public String toString()
	//	{
	//		return new String(buf, 0, count);
	//	}
	
	
	
	/**
	 * Flush the stream.
	 */
	@Override
	public void flush()
	{
	}
	
	/**
	 * Close the stream. This method does not release the buffer, since its
	 * contents might still be required. Note: Invoking this method in this
	 * class will have no effect.
	 */
	@Override
	public void close()
	{
	}
}
