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
import java.io.OutputStream;
import javax.annotation.concurrent.NotThreadSafe;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.io.iio.OutputByteStream;
import rebound.io.iio.unions.CloseableFlushableOutputByteStreamInterface;
import rebound.util.collections.prim.PrimitiveCollections.ByteArrayList;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;

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
@NotThreadSafe
public class ByteListOutputByteStream
extends OutputStream
implements CloseableFlushableOutputByteStreamInterface   //TODO also implement RandomAccessOutputByteStream!
{
	/**
	 * The buffer where data is stored.
	 */
	protected ByteList buf;
	
	/**
	 * Creates a new byte array output stream. The buffer capacity is initially
	 * 32 bytes, though its size increases if necessary.
	 */
	public ByteListOutputByteStream()
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
	public ByteListOutputByteStream(int size)
	{
		if (size < 0)
		{
			throw new IllegalArgumentException("Negative initial size: " + size);
		}
		this.buf = new ByteArrayList(size);
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
		this.buf.addByte((byte) b);
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
			throw new IndexOutOfBoundsException();
		
		this.buf.addAllBytes(b, off, len);
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
		out.write(this.buf);
	}
	
	/**
	 * Resets the <code>count</code> field of this byte array output stream to
	 * zero, so that all currently accumulated output in the output stream is
	 * discarded. The output stream can be used again, reusing the already
	 * allocated buffer space.
	 */
	public void reset()
	{
		this.buf.clear();
	}
	
	/**
	 * Creates a newly allocated byte array. Its size is the current size of
	 * this output stream and the valid contents of the buffer have been copied
	 * into it.
	 *
	 * @return the current contents of this output stream, as a byte array.
	 * @see java.io.ByteArrayOutputStream#size()
	 */
	@ThrowAwayValue
	public byte[] toByteArray()
	{
		return this.buf.toByteArray();
	}
	
	@ThrowAwayValue
	public ByteList toByteList()
	{
		return buf.clone();
	}
	
	@LiveValue
	public ByteList getByteListLive()
	{
		return buf;
	}
	
	/**
	 * Returns the current size of the buffer.
	 *
	 * @return the value of the <code>count</code> field, which is the number of
	 *         valid bytes in this output stream.
	 */
	public int size()
	{
		return this.buf.size();
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
