/*
 * Copyright (c) 1994, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 only, as published by
 * the Free Software Foundation. Oracle designates this particular file as
 * subject to the "Classpath" exception as provided by Oracle in the LICENSE
 * file that accompanied this code.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License version 2 for more
 * details (a copy is included in the LICENSE file that accompanied this code).
 * 
 * You should have received a copy of the GNU General Public License version 2
 * along with this work; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA or
 * visit www.oracle.com if you need additional information or have any
 * questions.
 */

package from.java.io.forr.rebound.io.iio;

import java.io.IOException;
import java.util.Arrays;
import rebound.io.iio.OutputByteStream;

/**
 * This class implements an output stream in which the data is written into a
 * byte array. The buffer automatically grows as data is written to it. The data
 * can be retrieved using <code>toByteArray()</code> and <code>toString()</code>
 * .
 * <p>
 * Closing a <tt>ByteArrayOutputStream</tt> has no effect. The methods in this
 * class can be called after the stream has been closed without generating an
 * <tt>IOException</tt>.
 *
 * @author Arthur van Hoff
 * @since JDK1.0
 */
public class ByteArrayOutputByteStream
implements OutputByteStream   //TODO also implement RandomAccessOutputByteStream!
{
	
	/**
	 * The buffer where data is stored.
	 */
	protected byte buf[];
	
	/**
	 * The number of valid bytes in the buffer.
	 */
	protected int count;
	
	/**
	 * Creates a new byte array output stream. The buffer capacity is initially
	 * 32 bytes, though its size increases if necessary.
	 */
	public ByteArrayOutputByteStream()
	{
		this(32);
	}
	
	/**
	 * Creates a new byte array output stream, with a buffer capacity of the
	 * specified size, in bytes.
	 *
	 * @param size
	 *            the initial size.
	 * @exception IllegalArgumentException
	 *                if size is negative.
	 */
	public ByteArrayOutputByteStream(int size)
	{
		if (size < 0)
		{
			throw new IllegalArgumentException("Negative initial size: " + size);
		}
		this.buf = new byte[size];
	}
	
	/**
	 * Increases the capacity if necessary to ensure that it can hold at least
	 * the number of elements specified by the minimum capacity argument.
	 *
	 * @param minCapacity
	 *            the desired minimum capacity
	 * @throws OutOfMemoryError
	 *             if {@code minCapacity < 0}. This is interpreted as a request
	 *             for the unsatisfiably large capacity
	 *             {@code (long) Integer.MAX_VALUE + (minCapacity - Integer.MAX_VALUE)}
	 *             .
	 */
	private void ensureCapacity(int minCapacity)
	{
		// overflow-conscious code
		if (minCapacity - this.buf.length > 0)
			grow(minCapacity);
	}
	
	/**
	 * The maximum size of array to allocate. Some VMs reserve some header words
	 * in an array. Attempts to allocate larger arrays may result in
	 * OutOfMemoryError: Requested array size exceeds VM limit
	 */
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
	
	/**
	 * Increases the capacity to ensure that it can hold at least the number of
	 * elements specified by the minimum capacity argument.
	 *
	 * @param minCapacity
	 *            the desired minimum capacity
	 */
	private void grow(int minCapacity)
	{
		// overflow-conscious code
		int oldCapacity = this.buf.length;
		int newCapacity = oldCapacity << 1;
		if (newCapacity - minCapacity < 0)
			newCapacity = minCapacity;
		if (newCapacity - MAX_ARRAY_SIZE > 0)
			newCapacity = hugeCapacity(minCapacity);
		this.buf = Arrays.copyOf(this.buf, newCapacity);
	}
	
	private static int hugeCapacity(int minCapacity)
	{
		if (minCapacity < 0) // overflow
			throw new OutOfMemoryError();
		return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
	}
	
	/**
	 * Writes the specified byte to this byte array output stream.
	 *
	 * @param b
	 *            the byte to be written.
	 */
	@Override
	public void write(int b)
	{
		ensureCapacity(this.count + 1);
		this.buf[this.count] = (byte) b;
		this.count += 1;
	}
	
	/**
	 * Writes <code>len</code> bytes from the specified byte array starting at
	 * offset <code>off</code> to this byte array output stream.
	 *
	 * @param b
	 *            the data.
	 * @param off
	 *            the start offset in the data.
	 * @param len
	 *            the number of bytes to write.
	 */
	@Override
	public void write(byte b[], int off, int len)
	{
		if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) - b.length > 0))
		{
			throw new IndexOutOfBoundsException();
		}
		ensureCapacity(this.count + len);
		System.arraycopy(b, off, this.buf, this.count, len);
		this.count += len;
	}
	
	/**
	 * Writes the complete contents of this byte array output stream to the
	 * specified output stream argument, as if by calling the output stream's
	 * write method using <code>out.write(buf, 0, count)</code>.
	 *
	 * @param out
	 *            the output stream to which to write the data.
	 * @exception IOException
	 *                if an I/O error occurs.
	 */
	public void writeTo(OutputByteStream out) throws IOException
	{
		out.write(this.buf, 0, this.count);
	}
	
	/**
	 * Resets the <code>count</code> field of this byte array output stream to
	 * zero, so that all currently accumulated output in the output stream is
	 * discarded. The output stream can be used again, reusing the already
	 * allocated buffer space.
	 */
	public void reset()
	{
		this.count = 0;
	}
	
	/**
	 * Creates a newly allocated byte array. Its size is the current size of
	 * this output stream and the valid contents of the buffer have been copied
	 * into it.
	 *
	 * @return the current contents of this output stream, as a byte array.
	 * @see java.io.ByteArrayOutputStream#size()
	 */
	public byte toByteArray()[]
	{
		return Arrays.copyOf(this.buf, this.count);
	}
	
	/**
	 * Returns the current size of the buffer.
	 *
	 * @return the value of the <code>count</code> field, which is the number of
	 *         valid bytes in this output stream.
	 */
	public int size()
	{
		return this.count;
	}
	
	/**
	 * Closing a <tt>ByteArrayOutputStream</tt> has no effect. The methods in
	 * this class can be called after the stream has been closed without
	 * generating an <tt>IOException</tt>.
	 */
	public void close() throws IOException
	{
	}
	
	
	
	
	
	@Override
	public void flush() throws IOException
	{
	}
}
