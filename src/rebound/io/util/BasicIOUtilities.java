package rebound.io.util;

import static java.util.Objects.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.util.BasicExceptionUtilities.*;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import javax.annotation.Nonnull;
import rebound.annotations.semantic.simpledata.ActuallyUnsigned;
import rebound.io.iio.InputByteStream;
import rebound.io.iio.OutputByteStream;
import rebound.io.iio.ResettableInputByteStream;
import rebound.io.iio.unions.CloseableFlushableOutputByteStreamInterface;
import rebound.io.iio.unions.CloseableInputByteStreamInterface;
import rebound.util.collections.ArrayUtilities;
import from.java.io.forr.rebound.io.iio.ByteArrayOutputByteStream;

//Todo requirePositive(), requireNonNull(), @ActuallySigned, etc. as needed

public class BasicIOUtilities
{
	public static byte read1(InputByteStream in) throws EOFException, IOException
	{
		int v = in.read();
		if (v == -1)
			throw new EOFException();
		return (byte)v;
	}
	
	
	
	
	
	
	//<Pump
	/**
	 * @return the number of bytes we actually pumped! \o/
	 */
	public static long pump(InputByteStream in, OutputByteStream out, int bufferSize) throws IOException
	{
		long total = 0;
		byte[] buffer = new byte[bufferSize];
		int amt = in.read(buffer);
		while (amt >= 0)
		{
			total = safe_add_s64(total, amt);
			out.write(buffer, 0, amt);
			amt = in.read(buffer);
		}
		return total;
	}
	
	/**
	 * @return the number of bytes we actually pumped! \o/
	 */
	public static long pump(InputByteStream in, OutputByteStream out) throws IOException
	{
		return pump(in, out, 4096);
	}
	
	
	public static @ActuallyUnsigned long pumpFixed(InputByteStream in, OutputByteStream out, @ActuallyUnsigned long length, int bufferSize) throws IOException
	{
		if (length > 0)
		{
			byte[] buffer = new byte[bufferSize];
			int amt = 0;
			@ActuallyUnsigned long curr = 0;
			while (Long.compareUnsigned(curr, length) < 0)
			{
				amt = in.read(buffer, 0, Math.min(bufferSize, (int)(length - curr)));
				if (amt < 0)
					return curr;
				curr = safe_add_u64(curr, amt);
				out.write(buffer, 0, amt);
			}
			return curr;
		}
		else
		{
			return 0;
		}
	}
	
	public static long pumpFixed(InputByteStream in, OutputByteStream out, long length) throws IOException
	{
		return pumpFixed(in, out, length, 4096);
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Discards no more than a given amount of data from an InputByteStreamInterface.<br>
	 * The only reason any less can be skipped is because of EOF (determined by InputByteStreamInterface.skip(long) < 0).<br>
	 */
	public static long discard(InputByteStream in, long amount) throws IOException
	{
		long skipped = 0;
		long c = 0;
		while (skipped < amount)
		{
			c = in.skip(amount-skipped);
			if (c < 0)
				return skipped;
			else
				skipped += c;
		}
		return skipped;
	}
	
	/**
	 * Discards no more than a given amount of data from an InputByteStreamInterface.<br>
	 * The only reason any less can be skipped is because of EOF (determined by InputByteStreamInterface.skip(long) == 0).<br>
	 */
	public static long discard(InputByteStream in) throws IOException
	{
		long skipped = 0;
		long c = 0;
		while (true)
		{
			c = in.skip(65536);
			if (c <= 0)
				return skipped;
			else
				skipped += c;
		}
	}
	
	
	/**
	 * Discards no more than a given amount of data from an InputByteStreamInterface.<br>
	 * The only reason any less can be skipped is because of EOF.<br>
	 * It does this by using read() rather than skip() on the given input stream :>
	 */
	public static long discardByReading(InputByteStream in, long amount) throws IOException
	{
		byte[] dummyBuff = new byte[1024];
		
		long skipped = 0;
		long c = 0;
		while (skipped < amount)
		{
			c = in.read(dummyBuff, 0, (int)Math.min(dummyBuff.length, amount-skipped));
			if (c == -1)
				return skipped;
			else
				skipped += c;
		}
		return skipped;
	}
	
	public static long discardByReading(InputByteStream in) throws IOException
	{
		byte[] dummyBuff = new byte[1024];
		
		long skipped = 0;
		long c = 0;
		while (true)
		{
			c = in.read(dummyBuff, 0, dummyBuff.length);
			if (c == -1)
				return skipped;
			else
				skipped += c;
		}
	}
	
	
	//	public static long scan(InputByteStreamInterface in, byte[] tag) throws IOException
	//	{
	//		return scan(in, tag, 0, tag.length);
	//	}
	//
	//	/**
	//	 * Scans for <code>tag</code> in the inputstream and stops immediately after the end of its occurance.<br>
	//	 * Returns the position at which it was first discovered, or -1.<br>
	//	 */
	//	public static long scan(InputByteStreamInterface in, byte[] tag, int tagOffset, int tagLength) throws IOException
	//	{
	//		NYI XD'
	//	}
	
	
	
	
	
	/**
	 * Tries until the specified number of bytes are actually read.
	 * @throws EOFException If the underlying stream returns -1 bytes read
	 * @throws IOException If the underlying stream throws one
	 */
	public static void readFully(InputByteStream in, byte[] buff, int offset, int len) throws EOFException, IOException
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
	
	public static void readFully(InputByteStream in, byte[] buff) throws EOFException, IOException
	{
		readFully(in, buff, 0, buff.length);
	}
	
	public static byte[] readFullyToNew(InputByteStream in, int length) throws EOFException, IOException
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
	 * Tries until the specified number of bytes are actually skipped.
	 * @throws EOFException If the underlying stream returns -1 bytes read
	 * @throws IOException If the underlying stream throws one
	 */
	public static void skipFully(InputByteStream in, @ActuallyUnsigned long len, byte[] dummyBuffer) throws EOFException, IOException
	{
		requireNonNull(dummyBuffer);
		
		@ActuallyUnsigned long total = 0;
		int amt = 0;
		int toRead = 0;
		while (true)
		{
			if (len - total > dummyBuffer.length)
				toRead = dummyBuffer.length;
			else
				toRead = (int)(len - total);
			
			amt = in.read(dummyBuffer, 0, toRead);
			
			if (amt < 0) //Only read does this; skip() does not support EOF indication
				throw new EOFException("Premature EOF");
			
			total = safe_add_u64(total, amt);
			
			if (total >= len)
				return; //We're finished
		}
	}
	
	public static void skipFully(InputByteStream in, long len) throws EOFException, IOException
	{
		skipFully(in, len, new byte[4096]);
	}
	
	
	
	/**
	 * This turns an interface with short reads into one that only reads less than requested due to EOF.
	 * Reads until the amount specified is read or EOF is reached.
	 * Returns the amount actually read.
	 */
	public static int readAsMuchAsPossible(InputByteStream in, byte[] buff, int offset, int len) throws IOException
	{
		int read = 0;
		
		int r = 0;
		while (true)
		{
			r = in.read(buff, offset + read, len - read);
			if (r < 0)
				break;
			read += r;
			if (read >= len)
				break;
		}
		
		return read;
	}
	
	public static int readAsMuchAsPossible(InputByteStream in, byte[] buff) throws IOException
	{
		return readAsMuchAsPossible(in, buff, 0, buff.length);
	}
	
	
	
	
	public static byte[] readAll(@Nonnull InputByteStream in) throws IOException
	{
		ByteArrayOutputByteStream buff = new ByteArrayOutputByteStream();
		pump(in, buff);
		return buff.toByteArray();
	}
	
	/**
	 * This reads up to <code>magic.length</code> bytes and compares them with the given bytes (<code>magic</code>).
	 * Iff they differ, or EOF is encountered, <code>false</code> is returned.
	 */
	public static boolean checkMagic(InputByteStream in, byte[] magic) throws IOException
	{
		int c = 0;
		for (int i = 0; i < magic.length; i++)
		{
			c = in.read();
			
			if (c == -1)
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
	
	
	
	public static void closeWithoutError(Closeable closeableThing)
	{
		try
		{
			closeableThing.close();
		}
		catch (IOException exc)
		{
		}
	}
	
	public static void closeAllWithoutError(Closeable... ss)
	{
		if (ss != null)
		{
			for (Closeable s : ss)
			{
				closeWithoutError(s);
			}
		}
	}
	
	public static void closeAll(Closeable... ss) throws IOException
	{
		if (ss != null)
		{
			Throwable exc = null;
			
			for (Closeable s : ss)
			{
				try
				{
					s.close();
				}
				catch (Throwable e)
				{
					if (exc == null)
						exc = e;
					else
						exc.addSuppressed(e);
				}
			}
			
			if (exc != null)
			{
				if (exc instanceof IOException)
					throw (IOException)exc;
				else
					rethrowSafe(exc);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static enum EmptyInputByteStream
	implements CloseableInputByteStreamInterface, ResettableInputByteStream
	{
		I;
		
		
		@Override
		public void close() {}
		
		@Override
		public int read() {return -1;}
		@Override
		public int read(byte[] b) {return -1;}
		@Override
		public int read(byte[] b, int off, int len) {return -1;}
		@Override
		public long skip(long n) {return 0;}
		
		@Override
		public void seekToStart() throws IOException {}
	}
	
	
	
	
	
	public static enum NullOutputStream
	implements CloseableFlushableOutputByteStreamInterface
	{
		I;
		
		
		@Override
		public void write(byte[] b)
		{
			//no op.
		}
		
		@Override
		public void write(int b)
		{
			//no op.
		}
		
		@Override
		public void write(byte[] b, int off, int len)
		{
			//no op.
		}
		
		
		@Override
		public void close()
		{
			//no op.
		}
		
		@Override
		public void flush()
		{
			//no op.
		}
	}
}
