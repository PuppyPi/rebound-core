package rebound.io.util;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import javax.annotation.Nonnull;
import rebound.io.iio.InputByteStream;
import rebound.io.iio.OutputByteStream;
import rebound.io.iio.unions.CloseableFlushableOutputByteStreamInterface;
import from.java.io.forr.rebound.io.iio.ByteArrayOutputByteStream;

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
			total += amt;
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
	
	
	public static long pumpFixed(InputByteStream in, OutputByteStream out, long length, int bufferSize) throws IOException
	{
		if (length > 0)
		{
			byte[] buffer = new byte[bufferSize];
			int amt = 0;
			long curr = 0;
			while (curr < length)
			{
				amt = in.read(buffer, 0, Math.min(bufferSize, (int)(length - curr)));
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
		int r = 0;
		while (true)
		{
			r = in.read(buff, offset + read, len - read);
			if (r < 0)
				throw new EOFException("Premature EOF");
			read += r;
			if (read >= len)
				return;
		}
	}
	
	public static void readFully(InputByteStream in, byte[] buff) throws EOFException, IOException
	{
		readFully(in, buff, 0, buff.length);
	}
	
	public static byte[] readFullyToNew(InputByteStream in, int length) throws EOFException, IOException
	{
		byte[] b = new byte[length];
		readFully(in, b);
		return b;
	}
	
	
	
	
	/**
	 * Tries until the specified number of bytes are actually skipped.
	 * @throws EOFException If the underlying stream returns -1 bytes read
	 * @throws IOException If the underlying stream throws one
	 */
	public static void skipFully(InputByteStream in, long len, byte[] dummyBuffer) throws EOFException, IOException
	{
		int total = 0;
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
			
			total += amt;
			
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
