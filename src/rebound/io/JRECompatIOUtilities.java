/*
 * Created on Nov 17, 2005
 * 	by the wonderful Eclipse(c)
 */
package rebound.io;

import static java.util.Objects.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.util.BasicExceptionUtilities.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Nonnull;
import rebound.io.iio.InputByteStream;
import rebound.io.streaming.api.StreamAPIs.ByteBlockReadStream;
import rebound.util.collections.ArrayUtilities;
import rebound.util.collections.Slice;
import rebound.util.objectutil.JavaNamespace;

public class JRECompatIOUtilities
implements JavaNamespace
{
	public static InputStream getInputFromStreamOrBuffer(Object o)
	{
		if (o instanceof InputStream)
			return (InputStream) o;
		//TODO else if (o instanceof InputByteStream)
		//TODO else if (o instanceof ByteBlockReadStream)
		else if (o instanceof byte[])
			return new ByteArrayInputStream((byte[])o);
		//		TODO else if (o instanceof ByteBuffer)
		//			return new com.totallynotsun.pdfview.ByteBufferInputStream((ByteBuffer)o);
		else if (o == null)
			return new ByteArrayInputStream(ArrayUtilities.EmptyByteArray);
		else
			throw newClassCastExceptionOrNullPointerException(o);
	}
	
	public static boolean isBufferAndEmpty(Object o)
	{
		if (o instanceof InputStream)
			return false;
		else if (o instanceof InputByteStream)
			return false;
		else if (o instanceof ByteBlockReadStream)
			return false;
		
		else if (o instanceof byte[])
			return ((byte[])o).length == 0;
		else if (o instanceof ByteBuffer)
			return ((ByteBuffer)o).remaining() == 0;
		else if (o == null)
			return true;
		else
			throw newClassCastExceptionOrNullPointerException(o);
	}
	
	
	
	public static BufferedInputStream ensureBufferedInputStream(InputStream in)
	{
		return in instanceof BufferedInputStream ? (BufferedInputStream)in : new BufferedInputStream(in);
	}
	
	public static BufferedOutputStream ensureBufferedOutputStream(OutputStream in)
	{
		return in instanceof BufferedOutputStream ? (BufferedOutputStream)in : new BufferedOutputStream(in);
	}
	
	
	
	
	
	
	
	
	//<Pump
	/**
	 * @return the number of bytes we actually pumped! \o/
	 */
	public static long pump(InputStream in, OutputStream out, int bufferSize) throws IOException
	{
		long total = 0;
		byte[] buffer = new byte[bufferSize];
		
		while (true)
		{
			int amt = in.read(buffer);
			if (amt < 0)
				break;
			
			out.write(buffer, 0, amt);
			total += amt;
		}
		
		return total;
	}
	
	/**
	 * @return the number of bytes we actually pumped! \o/
	 */
	public static long pump(InputStream in, OutputStream out) throws IOException
	{
		return pump(in, out, 4096);
	}
	
	public static long pumpThenCloseBoth(InputStream in, OutputStream out) throws IOException
	{
		try
		{
			return pump(in, out);
		}
		finally
		{
			try
			{
				in.close();
			}
			finally
			{
				out.close();
			}
		}
	}
	
	
	
	public static long pumpFixed(InputStream in, OutputStream out, long length, int bufferSize) throws IOException
	{
		if (length > 0)
		{
			byte[] buffer = new byte[bufferSize];
			long curr = 0;
			while (curr < length)
			{
				int amt = in.read(buffer, 0, Math.min(bufferSize, (int)(length - curr)));
				if (amt < 0)
					return curr;
				curr += amt;
				out.write(buffer, 0, amt);
			}
			return curr;
		}
		else
		{
			return 0;
		}
	}
	
	public static long pumpFixed(InputStream in, OutputStream out, long length) throws IOException
	{
		return pumpFixed(in, out, length, 4096);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @return the number of bytes we actually pumped! \o/
	 */
	public static long pump(RandomAccessFile in, OutputStream out, int bufferSize) throws IOException
	{
		long total = 0;
		byte[] buffer = new byte[bufferSize];
		
		while (true)
		{
			int amt = in.read(buffer);
			if (amt < 0)
				break;
			
			out.write(buffer, 0, amt);
			total += amt;
		}
		
		return total;
	}
	
	/**
	 * @return the number of bytes we actually pumped! \o/
	 */
	public static long pump(RandomAccessFile in, OutputStream out) throws IOException
	{
		return pump(in, out, 4096);
	}
	
	
	public static long pumpFixed(RandomAccessFile in, OutputStream out, long length, int bufferSize) throws IOException
	{
		if (length > 0)
		{
			byte[] buffer = new byte[bufferSize];
			long curr = 0;
			while (curr < length)
			{
				int amt = in.read(buffer, 0, Math.min(bufferSize, (int)(length - curr)));
				if (amt < 0)
					return curr;
				curr += amt;
				out.write(buffer, 0, amt);
			}
			return curr;
		}
		else
		{
			return 0;
		}
	}
	
	public static long pumpFixed(RandomAccessFile in, OutputStream out, long length) throws IOException
	{
		return pumpFixed(in, out, length, 4096);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @return the number of bytes we actually pumped! \o/
	 */
	public static long pump(InputStream in, RandomAccessFile out, int bufferSize) throws IOException
	{
		long total = 0;
		byte[] buffer = new byte[bufferSize];
		
		while (true)
		{
			int amt = in.read(buffer);
			if (amt < 0)
				break;
			
			out.write(buffer, 0, amt);
			total += amt;
		}
		
		return total;
	}
	
	/**
	 * @return the number of bytes we actually pumped! \o/
	 */
	public static long pump(InputStream in, RandomAccessFile out) throws IOException
	{
		return pump(in, out, 4096);
	}
	
	
	public static long pumpFixed(InputStream in, RandomAccessFile out, long length, int bufferSize) throws IOException
	{
		if (length > 0)
		{
			byte[] buffer = new byte[bufferSize];
			long curr = 0;
			while (curr < length)
			{
				int amt = in.read(buffer, 0, Math.min(bufferSize, (int)(length - curr)));
				if (amt < 0)
					return curr;
				curr += amt;
				out.write(buffer, 0, amt);
			}
			return curr;
		}
		else
		{
			return 0;
		}
	}
	
	public static long pumpFixed(InputStream in, RandomAccessFile out, long length) throws IOException
	{
		return pumpFixed(in, out, length, 4096);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void writeZeros(OutputStream out, long amount) throws IOException
	{
		writeRepeatingBytes(out, amount, new byte[(int)least(4096, amount)]);
	}
	
	public static void writeRepeatingBytes(OutputStream out, long amount, byte[] zeroBuffer) throws IOException
	{
		while (amount > 0)
		{
			int amountThisTime = (int)least(amount, zeroBuffer.length);
			out.write(zeroBuffer, 0, amountThisTime);
			amount -= amountThisTime;
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * Discards no more than a given amount of data from an InputStream.<br>
	 * The only reason any less can be skipped is because of EOF (determined by InputStream.skip(long) == 0).<br>
	 */
	public static long discard(InputStream in) throws IOException
	{
		long skipped = 0;
		while (true)
		{
			long c = in.skip(8388608);  //Seems like a good value that won't cause overflows XD
			if (c <= 0)
				return skipped;
			else
				skipped += c;
		}
	}
	
	public static long discardByReading(InputStream in) throws IOException
	{
		byte[] dummyBuff = new byte[1024];
		
		long skipped = 0;
		while (true)
		{
			long c = in.read(dummyBuff, 0, dummyBuff.length);
			if (c < 0)
				return skipped;
			else
				skipped += c;
		}
	}
	
	
	
	
	
	
	
	public static boolean compare(InputStream a, InputStream b, int bufferSize) throws IOException
	{
		Object rv = ExtraIOUtilities.consumeInSync(a, b, ExtraIOUtilities.CONSUMER_COMPARISON, bufferSize);
		return (Boolean)rv; //null is never returned by the encompassing method
	}
	
	public static boolean compare(InputStream a, InputStream b) throws IOException
	{
		return compare(a, b, 4096);
	}
	
	
	
	//	public static long scan(InputStream in, byte[] tag) throws IOException
	//	{
	//		return scan(in, tag, 0, tag.length);
	//	}
	//
	//	/**
	//	 * Scans for <code>tag</code> in the inputstream and stops immediately after the end of its occurance.<br>
	//	 * Returns the position at which it was first discovered, or -1.<br>
	//	 */
	//	public static long scan(InputStream in, byte[] tag, int tagOffset, int tagLength) throws IOException
	//	{
	//		NYI XD'
	//	}
	
	
	
	
	
	/**
	 * Tries until the specified number of bytes are actually read.
	 * @throws EOFException If the underlying stream eof's
	 * @throws IOException If the underlying stream throws one
	 */
	public static void readFully(InputStream in, byte[] buff, int offset, int len) throws EOFException, IOException
	{
		int read = 0;
		while (true)
		{
			if (read >= len)
				return;
			int r = in.read(buff, offset + read, len - read);
			if (r < 0)
				throw new EOFException("Premature EOF");
			read += r;
		}
	}
	
	public static void readFully(InputStream in, byte[] buff) throws EOFException, IOException
	{
		readFully(in, buff, 0, buff.length);
	}
	
	public static byte[] readFullyToNew(InputStream in, int length) throws EOFException, IOException
	{
		if (length == 0)
			return ArrayUtilities.EmptyByteArray;
		else
		{
			byte[] b = new byte[length];
			readFully(in, b);
			return b;
		}
	}
	
	
	
	/**
	 * Discards no more than a given amount of data from an InputStream.<br>
	 * The only reason any less can be skipped is because of EOF (determined by InputStream.skip(long) < 0).<br>
	 */
	public static long skipFully(InputStream in, long amount) throws IOException
	{
		long skipped = 0;
		while (skipped < amount)
		{
			long c = in.skip(amount-skipped);
			if (c < 0)
				return skipped;
			else
				skipped += c;
		}
		return skipped;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static long skipFullyByDiscarding(InputStream in, long amount, byte[] dummyBuffer) throws IOException
	{
		long total = 0;
		int amt = 0;
		
		while (true)
		{
			if (total >= amount)
				return total; //We're finished
			
			amt = in.read(dummyBuffer, 0, (int)Math.min(amount - total, dummyBuffer.length));
			
			if (amt < 0) //Only read does this; skip() does not support EOF indication
				return total; //We're finished
			
			total += amt;
		}
	}
	
	public static long skipFullyByDiscarding(InputStream in, long amount) throws IOException
	{
		return skipFullyByDiscarding(in, amount, new byte[2048]);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * This turns an interface with short reads into one that only reads less than requested due to EOF.
	 * Reads until the amount specified is read or EOF is reached.
	 * Returns the amount actually read.
	 */
	public static int readAsMuchAsPossible(InputStream in, byte[] buff, int offset, int len) throws IOException
	{
		int read = 0;
		
		while (true)
		{
			if (read >= len)
				break;
			int r = in.read(buff, offset + read, len - read);
			if (r < 0)
				break;
			read += r;
		}
		
		return read;
	}
	
	public static int readAsMuchAsPossible(InputStream in, byte[] buff) throws IOException
	{
		return readAsMuchAsPossible(in, buff, 0, buff.length);
	}
	
	public static byte[] readAsMuchAsPossibleToNew(InputStream in, int length) throws IOException
	{
		if (length == 0)
			return ArrayUtilities.EmptyByteArray;
		else
		{
			byte[] b = new byte[length];
			int amt = readAsMuchAsPossible(in, b);
			
			assert amt >= 0;
			
			if (amt != length)
				b = Arrays.copyOf(b, amt);
			
			return b;
		}
	}
	
	
	
	
	
	public static byte[] readAll(@Nonnull InputStream in) throws IOException
	{
		requireNonNull(in);
		
		ByteArrayOutputStream buff = new ByteArrayOutputStream();
		pump(in, buff);
		return buff.toByteArray();
	}
	
	/**
	 * This reads up to <code>magic.length</code> bytes and compares them with the given bytes (<code>magic</code>).
	 * Iff they differ, or EOF is encountered, <code>false</code> is returned.
	 */
	public static boolean checkMagic(InputStream in, byte[] magic) throws IOException
	{
		int c = 0;
		for (int i = 0; i < magic.length; i++)
		{
			c = in.read();
			
			if (c < 0)
				return false;
			
			if ((byte)c != magic[i])
				return false;
		}
		
		return true;
	}
	
	
	
	//Todo debug ^^'
	//	/**
	//	 * This is like {@link RandomAccessBytesInterface#skip(long)}, except that if you try to jump past the EOF, it extends the "file" as needed.
	//	 */
	//	public static int jumpBytes(RandomAccessBytesInterface file, int toSkip) throws IOException, EOFException
	//	{
	//		if (file.getFilePointer() + toSkip + 1 > file.length())
	//		{
	//			long newLength = file.getFilePointer() + toSkip + 1;
	//			file.setLength(newLength);
	//		}
	//
	//		int skipped = 0;
	//		while (true)
	//		{
	//			long s = file.skip(toSkip - skipped);
	//
	//			if (s < 0)
	//				throw new EOFException("Skip failed");
	//
	//			skipped += s;
	//
	//			if (skipped >= toSkip)
	//				return skipped;
	//		}
	//	}
	
	
	
	
	
	
	
	public static OutputStream getNullOutputStream()
	{
		return new OutputStream()
		{
			@Override
			public void write(byte[] b) throws IOException
			{
				//no op.
			}
			
			@Override
			public void write(int b) throws IOException
			{
				//no op.
			}
			
			@Override
			public void write(byte[] b, int off, int len) throws IOException
			{
				//no op.
			}
			
			
			@Override
			public void close() throws IOException
			{
				//no op.
			}
			
			@Override
			public void flush() throws IOException
			{
				//no op.
			}
		};
	}
	
	
	
	
	
	
	public static byte read1(InputStream in) throws EOFException, IOException
	{
		int v = in.read();
		if (v < 0)
			throw new EOFException();
		return (byte)v;
	}
	
	public static byte read1(ByteArrayInputStream in) throws EOFException
	{
		int v = in.read();
		if (v < 0)
			throw new EOFException();
		return (byte)v;
	}
	
	
	
	
	
	
	
	
	public static void writeSlice(OutputStream out, Slice<byte[]> data) throws IOException
	{
		out.write(data.getUnderlying(), data.getOffset(), data.getLength());
	}
	
	public static int readSlice(InputStream in, Slice<byte[]> data) throws IOException
	{
		return in.read(data.getUnderlying(), data.getOffset(), data.getLength());
	}
}
