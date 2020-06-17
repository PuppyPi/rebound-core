/*
 * Created on Nov 17, 2005
 * 	by the wonderful Eclipse(c)
 */
package rebound.io.util;

import static java.util.Objects.*;
import static rebound.bits.Unsigned.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.util.BasicExceptionUtilities.*;
import static rebound.util.collections.PolymorphicCollectionUtilities.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Nonnull;
import rebound.annotations.hints.Inline;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.simpledata.ActuallySigned;
import rebound.annotations.semantic.simpledata.ActuallyUnsigned;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.SlowVersionUnsupportedException;
import rebound.io.iio.InputByteStream;
import rebound.io.streaming.api.StreamAPIs.ByteBlockReadStream;
import rebound.util.collections.ArrayUtilities;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.functional.FunctionInterfaces.UnaryProcedure;
import rebound.util.functional.throwing.FunctionalInterfacesThrowingCheckedExceptionsStandard.UnaryProcedureThrowingIOException;
import rebound.util.objectutil.JavaNamespace;

//Todo requirePositive(), requireNonNull(), @ActuallySigned, etc. as needed

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
	
	
	
	@Inline  //inlining is necessary for the immediate optimization of We-Know-The-Runtime-Type-And-So-We-Don't-Need-To-Do-Virtual-Method-Invocation-and-Now-We-Can-Inline-Object-Method-Calls!  :>>
	public static BufferedInputStream ensureBufferedInputStream(InputStream in)
	{
		return in.getClass() == BufferedInputStream.class ? (BufferedInputStream)in : new BufferedInputStream(in);
	}
	
	@Inline  //inlining is necessary for the immediate optimization of We-Know-The-Runtime-Type-And-So-We-Don't-Need-To-Do-Virtual-Method-Invocation-and-Now-We-Can-Inline-Object-Method-Calls!  :>>
	public static BufferedOutputStream ensureBufferedOutputStream(OutputStream in)
	{
		return in.getClass() == BufferedOutputStream.class ? (BufferedOutputStream)in : new BufferedOutputStream(in);
	}
	
	
	
	
	
	
	
	
	//<Pump
	/**
	 * @return the number of bytes we actually pumped! \o/
	 */
	public static @ActuallySigned long pumpObservingErrors(InputStream in, OutputStream out, int bufferSize, UnaryProcedure<IOException> ioerrorInInput, UnaryProcedure<IOException> ioerrorInOutput)
	{
		@ActuallySigned long total = 0;
		byte[] buffer = new byte[bufferSize];
		
		while (true)
		{
			int amt;
			try
			{
				amt = in.read(buffer);
			}
			catch (IOException exc)
			{
				ioerrorInInput.f(exc);
				break;
			}
			
			if (amt < 0)
				break;
			
			try
			{
				out.write(buffer, 0, amt);
			}
			catch (IOException exc)
			{
				ioerrorInOutput.f(exc);
				break;
			}
			
			total = safe_add_s64(total, amt);
		}
		
		return total;
	}
	
	/**
	 * @return the number of bytes we actually pumped! \o/
	 */
	public static long pumpObservingErrors(InputStream in, OutputStream out, UnaryProcedure<IOException> ioerrorInInput, UnaryProcedure<IOException> ioerrorInOutput)
	{
		return pumpObservingErrors(in, out, 4096, ioerrorInInput, ioerrorInOutput);
	}
	
	public static long pumpThenCloseBothObservingErrors(InputStream in, OutputStream out, UnaryProcedure<IOException> ioerrorInInput, UnaryProcedure<IOException> ioerrorInOutput) throws IOException
	{
		try
		{
			return pumpObservingErrors(in, out, ioerrorInInput, ioerrorInOutput);
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
	
	
	
	public static @ActuallyUnsigned long pumpFixedObservingErrors(InputStream in, OutputStream out, @ActuallyUnsigned long length, int bufferSize, UnaryProcedure<IOException> ioerrorInInput, UnaryProcedure<IOException> ioerrorInOutput)
	{
		requirePositive(bufferSize);
		
		if (length > 0)
		{
			byte[] buffer = new byte[bufferSize];
			@ActuallyUnsigned long curr = 0;
			while (Long.compareUnsigned(curr, length) < 0)
			{
				int amt;
				try
				{
					amt = in.read(buffer, 0, Math.min(bufferSize, (int)(length - curr)));
				}
				catch (IOException exc)
				{
					ioerrorInInput.f(exc);
					break;
				}
				
				if (amt < 0)
					return curr;
				
				curr = safe_add_u64(curr, amt);
				
				try
				{
					out.write(buffer, 0, amt);
				}
				catch (IOException exc)
				{
					ioerrorInOutput.f(exc);
					break;
				}
			}
			return curr;
		}
		else
		{
			return 0;
		}
	}
	
	public static @ActuallyUnsigned long pumpFixedObservingErrors(InputStream in, OutputStream out, @ActuallyUnsigned long length, UnaryProcedure<IOException> ioerrorInInput, UnaryProcedure<IOException> ioerrorInOutput)
	{
		return pumpFixedObservingErrors(in, out, length, 4096, ioerrorInInput, ioerrorInOutput);
	}
	
	
	
	
	
	
	
	
	
	/**
	 * @return the number of bytes we actually pumped! \o/
	 */
	public static @ActuallySigned long pump(InputStream in, OutputStream out, int bufferSize) throws IOException
	{
		@ActuallySigned long total = 0;
		byte[] buffer = new byte[bufferSize];
		
		while (true)
		{
			int amt = in.read(buffer);
			if (amt < 0)
				break;
			
			out.write(buffer, 0, amt);
			total = safe_add_s64(total, amt);
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
	
	
	
	public static @ActuallyUnsigned long pumpFixed(InputStream in, OutputStream out, @ActuallyUnsigned long length, int bufferSize) throws IOException
	{
		requirePositive(bufferSize);
		
		if (length > 0)
		{
			byte[] buffer = new byte[bufferSize];
			@ActuallyUnsigned long curr = 0;
			while (Long.compareUnsigned(curr, length) < 0)
			{
				int amt = in.read(buffer, 0, Math.min(bufferSize, (int)(length - curr)));
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
	
	public static @ActuallyUnsigned long pumpFixed(InputStream in, OutputStream out, @ActuallyUnsigned long length) throws IOException
	{
		return pumpFixed(in, out, length, 4096);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @return the number of bytes we actually pumped! \o/
	 */
	public static @ActuallySigned long pump(RandomAccessFile in, OutputStream out, int bufferSize) throws IOException
	{
		@ActuallySigned long total = 0;
		byte[] buffer = new byte[bufferSize];
		
		while (true)
		{
			int amt = in.read(buffer);
			if (amt < 0)
				break;
			
			out.write(buffer, 0, amt);
			total = safe_add_s64(total, amt);
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
	
	
	public static @ActuallyUnsigned long pumpFixed(RandomAccessFile in, OutputStream out, long length, int bufferSize) throws IOException
	{
		if (length > 0)
		{
			byte[] buffer = new byte[bufferSize];
			@ActuallyUnsigned long curr = 0;
			while (Long.compareUnsigned(curr, length) < 0)
			{
				int amt = in.read(buffer, 0, Math.min(bufferSize, (int)(length - curr)));
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
	
	public static long pumpFixed(RandomAccessFile in, OutputStream out, long length) throws IOException
	{
		return pumpFixed(in, out, length, 4096);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @return the number of bytes we actually pumped! \o/
	 */
	public static @ActuallySigned long pump(InputStream in, RandomAccessFile out, int bufferSize) throws IOException
	{
		@ActuallySigned long total = 0;
		byte[] buffer = new byte[bufferSize];
		
		while (true)
		{
			int amt = in.read(buffer);
			if (amt < 0)
				break;
			
			out.write(buffer, 0, amt);
			total = safe_add_s64(total, amt);
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
	
	
	public static @ActuallyUnsigned long pumpFixed(InputStream in, RandomAccessFile out, long length, int bufferSize) throws IOException
	{
		if (length > 0)
		{
			byte[] buffer = new byte[bufferSize];
			@ActuallyUnsigned long curr = 0;
			while (Long.compareUnsigned(curr, length) < 0)
			{
				int amt = in.read(buffer, 0, Math.min(bufferSize, (int)(length - curr)));
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static @ActuallySigned long skipFullyByDiscarding(InputStream in, long amount, byte[] dummyBuffer) throws IOException
	{
		@ActuallySigned long total = 0;
		int amt = 0;
		
		while (true)
		{
			if (total >= amount)
				return total; //We're finished
			
			amt = in.read(dummyBuffer, 0, (int)Math.min(amount - total, dummyBuffer.length));
			
			if (amt < 0) //Only read does this; skip() does not support EOF indication
				return total; //We're finished
			
			total = safe_add_s64(total, amt);
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
	
	
	
	
	
	public static int read1viaReadBlock(InputStream in, byte[] buf1) throws EOFException, IOException
	{
		return read1viaReadBlock(in, buf1, 0);
	}
	
	public static int read1viaReadBlock(InputStream in, byte[] buf1, int offset) throws EOFException, IOException
	{
		while (true)
		{
			int r = in.read(buf1, offset, 1);
			
			if (r == -1)
				return -1;
			else if (r == 1)
				return upcast(buf1[offset]);
			else if (r == 0)
				continue;
			else
				throw new ImpossibleException("an InputStream implementation's read(byte[], int, int) method returned more bytes (+r+) than were requested! (1)");
		}
	}
	
	
	
	
	
	
	
	public static void writeSlice(OutputStream out, @ReadonlyValue Slice<byte[]> data) throws IOException
	{
		out.write(data.getUnderlying(), data.getOffset(), data.getLength());
	}
	
	public static int readSlice(InputStream in, @WritableValue Slice<byte[]> data) throws IOException
	{
		return in.read(data.getUnderlying(), data.getOffset(), data.getLength());
	}
	
	public static void readSliceFully(InputStream in, @WritableValue Slice<byte[]> data) throws IOException
	{
		readFully(in, data.getUnderlying(), data.getOffset(), data.getLength());
	}
	
	
	
	public static void writeList(OutputStream out, @ReadonlyValue ByteList data) throws IOException
	{
		writeSlice(out, data.toByteArraySlicePossiblyLive());
	}
	
	public static int readList(InputStream in, @WritableValue ByteList data) throws IOException
	{
		//		IntegerContainer rv = new SimpleIntegerContainer();
		//		readWriteArraySliceDefinitelyLiveThrowingIOException(data, arraySlice -> rv.set(readSlice(in, arraySlice)));
		//		return rv.get();
		
		ensureWritableCollection(data);
		
		Slice<byte[]> s = data.toByteArraySliceLiveOrNull();
		if (s == null)
			throw new SlowVersionUnsupportedException();
		return readSlice(in, s);
	}
	
	public static void readListFully(InputStream in, @WritableValue ByteList data) throws IOException
	{
		//		readWriteArraySliceDefinitelyLiveThrowingIOException(data, arraySlice -> readSliceFully(in, arraySlice));
		
		ensureWritableCollection(data);
		
		Slice<byte[]> s = data.toByteArraySliceLiveOrNull();
		if (s == null)
			throw new SlowVersionUnsupportedException();
		readSliceFully(in, s);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//TODO Resolve these duplicates XD''
	
	
	public static boolean compare(InputStream a, InputStream b, int bufferSize) throws IOException
	{
		Object rv = ExtraIOUtilities.consumeInSync(a, b, ExtraIOUtilities.CONSUMER_COMPARISON, bufferSize);
		return (Boolean)rv; //null is never returned by the encompassing method
	}
	
	public static boolean compare(InputStream a, InputStream b) throws IOException
	{
		return compare(a, b, 4096);
	}
	
	
	
	
	
	public static boolean streameq(InputStream a, InputStream b) throws IOException
	{
		return streamcmp(a, b) == 0;
	}
	
	public static int streamcmp(InputStream a, InputStream b) throws IOException
	{
		return streamcmp(a, false, b, false);
	}
	
	public static int streamcmp(InputStream a, boolean okayIfAStopsEarly, InputStream b, boolean okayIfBStopsEarly) throws IOException
	{
		//// yay JIT compiling! :D ////
		BufferedInputStream ab = ensureBufferedInputStream(a);
		BufferedInputStream bb = ensureBufferedInputStream(b);
		///////////////////////////////
		
		while (true)
		{
			int ac = ab.read();
			
			if (ac < 0 && okayIfAStopsEarly)
				return 0;
			
			int bc = bb.read();
			
			if (ac < 0)
			{
				if (bc < 0)
				{
					return 0;
				}
				else
				{
					return -1;
				}
			}
			else
			{
				if (bc < 0)
				{
					return okayIfBStopsEarly ? 0 : 1;
				}
				else
				{
					if (ac == bc)
					{
						//continue;
					}
					else if (ac < bc)
					{
						return -1;
					}
					else //if (ac > bc)
					{
						return 1;
					}
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	public static byte[] writeAllToMemory(UnaryProcedureThrowingIOException<OutputStream> write)
	{
		try (ByteArrayOutputStream buff = new ByteArrayOutputStream())
		{
			write.f(buff);
			return buff.toByteArray();
		}
		catch (IOException exc)
		{
			throw new ImpossibleException(exc);
		}
	}
	
	
	public static String writeAllTextToMemory(UnaryProcedureThrowingIOException<Writer> write)
	{
		try (StringWriter buff = new StringWriter())
		{
			write.f(buff);
			return buff.toString();
		}
		catch (IOException exc)
		{
			throw new ImpossibleException(exc);
		}
	}
}
