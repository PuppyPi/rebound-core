/*
 * Created on May 26, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.util.usagehelp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import rebound.io.ShortWriteIOException;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.BooleanBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.BooleanBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.ByteBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.ByteBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.CharBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.CharBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.DoubleBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.DoubleBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.FloatBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.FloatBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.IntBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.IntBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.LongBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.LongBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.ReferenceBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.ReferenceBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.ReferenceUnitReadStream;
import rebound.io.streaming.api.StreamAPIs.ReferenceUnitWriteStream;
import rebound.io.streaming.api.StreamAPIs.ShortBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.ShortBlockWriteStream;
import rebound.io.streaming.util.adapters.ReferenceBlockToUnitReadStream;
import rebound.io.streaming.util.adapters.ReferenceBlockToUnitWriteStream;
import rebound.io.streaming.util.adapters.ReferenceUnitToBlockReadStream;
import rebound.io.streaming.util.adapters.ReferenceUnitToBlockWriteStream;
import rebound.io.streaming.util.adapters.jre.io.InputStreamAdapter;
import rebound.io.streaming.util.adapters.jre.io.InputStreamWrapper;
import rebound.io.streaming.util.adapters.jre.io.OutputStreamAdapter;
import rebound.io.streaming.util.adapters.jre.io.OutputStreamWrapper;
import rebound.util.collections.Slice;

/**
 * Utilities for usage of streams are housed here.
 * @author RProgrammer
 */
public class StreamUsageUtilities
{
	protected static final int DefaultPumpBufferSize = 4096;
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!
	 * @return the number of units transferred!
	 */
	public static <D> long pumpToEitherEOF(ReferenceBlockReadStream<D> in, ReferenceBlockWriteStream<D> out) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		Object[] buffer = new Object[DefaultPumpBufferSize];
		return pumpToInputEOF(in, out, (D[])buffer);
	}
	
	
	/**
	 * Note that the output stream MUST never EOF!!
	 * This is NOT indolent—the only reason this would ever return less than requested is due to EOF on the input!
	 * @return the number of units transferred!
	 */
	public static <D> long pumpFixed(ReferenceBlockReadStream<D> in, ReferenceBlockWriteStream<D> out, long amount) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		Object[] buffer = new Object[DefaultPumpBufferSize];
		return pumpFixed(in, out, amount, (D[])buffer);
	}
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!
	 * @return the number of units transferred!
	 */
	public static <D> long pumpToInputEOF(ReferenceBlockReadStream<D> in, ReferenceBlockWriteStream<D> out, D[] buffer) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		assert buffer != null;
		
		
		final int bufferLength = buffer.length;
		
		
		long amountSoFar = 0;
		
		while (true)
		{
			int amountRequestedThisTime = bufferLength;
			
			int amountReadThisTime = in.read(buffer, 0, amountRequestedThisTime);
			assert amountReadThisTime >= 0 && amountReadThisTime <= amountRequestedThisTime;
			
			int amountWrittenThisTime = out.write(buffer, 0, amountReadThisTime);
			assert amountWrittenThisTime >= 0 && amountWrittenThisTime <= amountReadThisTime;
			
			if (amountWrittenThisTime < amountReadThisTime)
				throw new ShortWriteIOException(amountReadThisTime, amountWrittenThisTime);
			
			amountSoFar += amountWrittenThisTime;
			
			if (amountReadThisTime < amountRequestedThisTime)
				break;
		}
		
		
		return amountSoFar;
	}
	
	
	/**
	 * Note that the output stream MUST never EOF!!
	 * This is NOT indolent—the only reason this would ever return less than requested is due to EOF on the input!
	 * @return the number of units transferred!
	 */
	public static <D> long pumpFixed(ReferenceBlockReadStream<D> in, ReferenceBlockWriteStream<D> out, long amountRequested, D[] buffer) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		assert amountRequested > 0;
		assert buffer != null;
		
		
		final int bufferLength = buffer.length;
		
		
		long amountSoFar = 0;
		
		while (amountSoFar < amountRequested)
		{
			long remaining = amountRequested - amountSoFar;
			int remaining32 = remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
			int amountRequestedThisTime = remaining32 > bufferLength ? bufferLength : remaining32;
			
			int amountReadThisTime = in.read(buffer, 0, amountRequestedThisTime);
			assert amountReadThisTime >= 0 && amountReadThisTime <= amountRequestedThisTime;
			
			int amountWrittenThisTime = out.write(buffer, 0, amountReadThisTime);
			assert amountWrittenThisTime >= 0 && amountWrittenThisTime <= amountReadThisTime;
			
			if (amountWrittenThisTime < amountReadThisTime)
				throw new ShortWriteIOException(amountReadThisTime, amountWrittenThisTime);
			
			amountSoFar += amountWrittenThisTime;
			
			if (amountReadThisTime < amountRequestedThisTime)
				break;
		}
		
		
		return amountSoFar;
	}
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	primxp
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteException} if it does!)
	 * This is NOT indolent—the only reason this would ever return less than requested is due to EOF on the input!
	 * @return the number of units transferred!
	 ⎋a/
	public static long pumpFixed(_$$Prim$$_BlockReadStream in, _$$Prim$$_BlockWriteStream out, long amount) throws ClosedStreamException, IOException, ShortWriteException
	{
		_$$prim$$_[] buffer = new _$$prim$$_[DefaultPumpBufferSize];
		return pumpFixed(in, out, amount, buffer);
	}
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteException} if it does!)
	 * @return the number of units transferred!
	 ⎋a/
	public static long pumpToInputEOF(_$$Prim$$_BlockReadStream in, _$$Prim$$_BlockWriteStream out) throws ClosedStreamException, IOException, ShortWriteException
	{
		_$$prim$$_[] buffer = new _$$prim$$_[DefaultPumpBufferSize];
		return pumpToInputEOF(in, out, buffer);
	}
	
	
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteException} if it does!)
	 * @return the number of units transferred!
	 ⎋a/
	public static long pumpToInputEOF(_$$Prim$$_BlockReadStream in, _$$Prim$$_BlockWriteStream out, _$$prim$$_[] buffer) throws ClosedStreamException, IOException, ShortWriteException
	{
		assert buffer != null;
		
		
		final int bufferLength = buffer.length;
		
		
		long amountSoFar = 0;
		
		while (true)
		{
			int amountRequestedThisTime = bufferLength;
			
			int amountReadThisTime = in.read(buffer, 0, amountRequestedThisTime);
			assert amountReadThisTime >= 0 && amountReadThisTime <= amountRequestedThisTime;
			
			int amountWrittenThisTime = out.write(buffer, 0, amountReadThisTime);
			assert amountWrittenThisTime >= 0 && amountWrittenThisTime <= amountReadThisTime;
			
			if (amountWrittenThisTime < amountReadThisTime)
				throw new ShortWriteException(amountReadThisTime, amountWrittenThisTime);
			
			amountSoFar += amountWrittenThisTime;
			
			if (amountReadThisTime < amountRequestedThisTime)
				break;
		}
		
		
		return amountSoFar;
	}
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteException} if it does!)
	 * This is NOT indolent—the only reason this would ever return less than requested is due to EOF on the input!
	 * @return the number of units transferred!
	 ⎋a/
	public static long pumpFixed(_$$Prim$$_BlockReadStream in, _$$Prim$$_BlockWriteStream out, long amountRequested, _$$prim$$_[] buffer) throws ClosedStreamException, IOException, ShortWriteException
	{
		assert amountRequested > 0;
		assert buffer != null;
		
		
		final int bufferLength = buffer.length;
		
		
		long amountSoFar = 0;
		
		while (amountSoFar < amountRequested)
		{
			long remaining = amountRequested - amountSoFar;
			int remaining32 = remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
			int amountRequestedThisTime = remaining32 > bufferLength ? bufferLength : remaining32;
			
			int amountReadThisTime = in.read(buffer, 0, amountRequestedThisTime);
			assert amountReadThisTime >= 0 && amountReadThisTime <= amountRequestedThisTime;
			
			int amountWrittenThisTime = out.write(buffer, 0, amountReadThisTime);
			assert amountWrittenThisTime >= 0 && amountWrittenThisTime <= amountReadThisTime;
			
			if (amountWrittenThisTime < amountReadThisTime)
				throw new ShortWriteException(amountReadThisTime, amountWrittenThisTime);
			
			amountSoFar += amountWrittenThisTime;
			
			if (amountReadThisTime < amountRequestedThisTime)
				break;
		}
		
		
		return amountSoFar;
	}
	
	
	
	
	
	
	
	
	
	public static void writeMandatory(_$$Prim$$_BlockWriteStream stream, _$$prim$$_[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		int amountActuallyWritten = stream.write(buffer, offset, length);
		if (amountActuallyWritten != length)
			throw new ShortWriteException(length, amountActuallyWritten);
	}
	
	public static void writeMandatory(_$$Prim$$_BlockWriteStream stream, _$$prim$$_[] buffer) throws IOException, ClosedStreamException
	{
		writeMandatory(stream, buffer, 0, buffer.length);
	}
	
	public static void writeMandatory(_$$Prim$$_BlockWriteStream stream, Slice<_$$prim$$_[]> buffer) throws IOException, ClosedStreamException
	{
		writeMandatory(stream, buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	 */
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * This is NOT indolent—the only reason this would ever return less than requested is due to EOF on the input!
	 * @return the number of units transferred!
	 */
	public static long pumpFixed(BooleanBlockReadStream in, BooleanBlockWriteStream out, long amount) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		boolean[] buffer = new boolean[DefaultPumpBufferSize];
		return pumpFixed(in, out, amount, buffer);
	}
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * @return the number of units transferred!
	 */
	public static long pumpToInputEOF(BooleanBlockReadStream in, BooleanBlockWriteStream out) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		boolean[] buffer = new boolean[DefaultPumpBufferSize];
		return pumpToInputEOF(in, out, buffer);
	}
	
	
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * @return the number of units transferred!
	 */
	public static long pumpToInputEOF(BooleanBlockReadStream in, BooleanBlockWriteStream out, boolean[] buffer) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		assert buffer != null;
		
		
		final int bufferLength = buffer.length;
		
		
		long amountSoFar = 0;
		
		while (true)
		{
			int amountRequestedThisTime = bufferLength;
			
			int amountReadThisTime = in.read(buffer, 0, amountRequestedThisTime);
			assert amountReadThisTime >= 0 && amountReadThisTime <= amountRequestedThisTime;
			
			int amountWrittenThisTime = out.write(buffer, 0, amountReadThisTime);
			assert amountWrittenThisTime >= 0 && amountWrittenThisTime <= amountReadThisTime;
			
			if (amountWrittenThisTime < amountReadThisTime)
				throw new ShortWriteIOException(amountReadThisTime, amountWrittenThisTime);
			
			amountSoFar += amountWrittenThisTime;
			
			if (amountReadThisTime < amountRequestedThisTime)
				break;
		}
		
		
		return amountSoFar;
	}
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * This is NOT indolent—the only reason this would ever return less than requested is due to EOF on the input!
	 * @return the number of units transferred!
	 */
	public static long pumpFixed(BooleanBlockReadStream in, BooleanBlockWriteStream out, long amountRequested, boolean[] buffer) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		assert amountRequested > 0;
		assert buffer != null;
		
		
		final int bufferLength = buffer.length;
		
		
		long amountSoFar = 0;
		
		while (amountSoFar < amountRequested)
		{
			long remaining = amountRequested - amountSoFar;
			int remaining32 = remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
			int amountRequestedThisTime = remaining32 > bufferLength ? bufferLength : remaining32;
			
			int amountReadThisTime = in.read(buffer, 0, amountRequestedThisTime);
			assert amountReadThisTime >= 0 && amountReadThisTime <= amountRequestedThisTime;
			
			int amountWrittenThisTime = out.write(buffer, 0, amountReadThisTime);
			assert amountWrittenThisTime >= 0 && amountWrittenThisTime <= amountReadThisTime;
			
			if (amountWrittenThisTime < amountReadThisTime)
				throw new ShortWriteIOException(amountReadThisTime, amountWrittenThisTime);
			
			amountSoFar += amountWrittenThisTime;
			
			if (amountReadThisTime < amountRequestedThisTime)
				break;
		}
		
		
		return amountSoFar;
	}
	
	
	
	
	
	
	
	
	
	public static void writeMandatory(BooleanBlockWriteStream stream, boolean[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		int amountActuallyWritten = stream.write(buffer, offset, length);
		if (amountActuallyWritten != length)
			throw new ShortWriteIOException(length, amountActuallyWritten);
	}
	
	public static void writeMandatory(BooleanBlockWriteStream stream, boolean[] buffer) throws IOException, ClosedStreamException
	{
		writeMandatory(stream, buffer, 0, buffer.length);
	}
	
	public static void writeMandatory(BooleanBlockWriteStream stream, Slice<boolean[]> buffer) throws IOException, ClosedStreamException
	{
		writeMandatory(stream, buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * This is NOT indolent—the only reason this would ever return less than requested is due to EOF on the input!
	 * @return the number of units transferred!
	 */
	public static long pumpFixed(ByteBlockReadStream in, ByteBlockWriteStream out, long amount) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		byte[] buffer = new byte[DefaultPumpBufferSize];
		return pumpFixed(in, out, amount, buffer);
	}
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * @return the number of units transferred!
	 */
	public static long pumpToInputEOF(ByteBlockReadStream in, ByteBlockWriteStream out) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		byte[] buffer = new byte[DefaultPumpBufferSize];
		return pumpToInputEOF(in, out, buffer);
	}
	
	
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * @return the number of units transferred!
	 */
	public static long pumpToInputEOF(ByteBlockReadStream in, ByteBlockWriteStream out, byte[] buffer) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		assert buffer != null;
		
		
		final int bufferLength = buffer.length;
		
		
		long amountSoFar = 0;
		
		while (true)
		{
			int amountRequestedThisTime = bufferLength;
			
			int amountReadThisTime = in.read(buffer, 0, amountRequestedThisTime);
			assert amountReadThisTime >= 0 && amountReadThisTime <= amountRequestedThisTime;
			
			int amountWrittenThisTime = out.write(buffer, 0, amountReadThisTime);
			assert amountWrittenThisTime >= 0 && amountWrittenThisTime <= amountReadThisTime;
			
			if (amountWrittenThisTime < amountReadThisTime)
				throw new ShortWriteIOException(amountReadThisTime, amountWrittenThisTime);
			
			amountSoFar += amountWrittenThisTime;
			
			if (amountReadThisTime < amountRequestedThisTime)
				break;
		}
		
		
		return amountSoFar;
	}
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * This is NOT indolent—the only reason this would ever return less than requested is due to EOF on the input!
	 * @return the number of units transferred!
	 */
	public static long pumpFixed(ByteBlockReadStream in, ByteBlockWriteStream out, long amountRequested, byte[] buffer) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		assert amountRequested > 0;
		assert buffer != null;
		
		
		final int bufferLength = buffer.length;
		
		
		long amountSoFar = 0;
		
		while (amountSoFar < amountRequested)
		{
			long remaining = amountRequested - amountSoFar;
			int remaining32 = remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
			int amountRequestedThisTime = remaining32 > bufferLength ? bufferLength : remaining32;
			
			int amountReadThisTime = in.read(buffer, 0, amountRequestedThisTime);
			assert amountReadThisTime >= 0 && amountReadThisTime <= amountRequestedThisTime;
			
			int amountWrittenThisTime = out.write(buffer, 0, amountReadThisTime);
			assert amountWrittenThisTime >= 0 && amountWrittenThisTime <= amountReadThisTime;
			
			if (amountWrittenThisTime < amountReadThisTime)
				throw new ShortWriteIOException(amountReadThisTime, amountWrittenThisTime);
			
			amountSoFar += amountWrittenThisTime;
			
			if (amountReadThisTime < amountRequestedThisTime)
				break;
		}
		
		
		return amountSoFar;
	}
	
	
	
	
	
	
	
	
	
	public static void writeMandatory(ByteBlockWriteStream stream, byte[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		int amountActuallyWritten = stream.write(buffer, offset, length);
		if (amountActuallyWritten != length)
			throw new ShortWriteIOException(length, amountActuallyWritten);
	}
	
	public static void writeMandatory(ByteBlockWriteStream stream, byte[] buffer) throws IOException, ClosedStreamException
	{
		writeMandatory(stream, buffer, 0, buffer.length);
	}
	
	public static void writeMandatory(ByteBlockWriteStream stream, Slice<byte[]> buffer) throws IOException, ClosedStreamException
	{
		writeMandatory(stream, buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * This is NOT indolent—the only reason this would ever return less than requested is due to EOF on the input!
	 * @return the number of units transferred!
	 */
	public static long pumpFixed(CharBlockReadStream in, CharBlockWriteStream out, long amount) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		char[] buffer = new char[DefaultPumpBufferSize];
		return pumpFixed(in, out, amount, buffer);
	}
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * @return the number of units transferred!
	 */
	public static long pumpToInputEOF(CharBlockReadStream in, CharBlockWriteStream out) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		char[] buffer = new char[DefaultPumpBufferSize];
		return pumpToInputEOF(in, out, buffer);
	}
	
	
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * @return the number of units transferred!
	 */
	public static long pumpToInputEOF(CharBlockReadStream in, CharBlockWriteStream out, char[] buffer) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		assert buffer != null;
		
		
		final int bufferLength = buffer.length;
		
		
		long amountSoFar = 0;
		
		while (true)
		{
			int amountRequestedThisTime = bufferLength;
			
			int amountReadThisTime = in.read(buffer, 0, amountRequestedThisTime);
			assert amountReadThisTime >= 0 && amountReadThisTime <= amountRequestedThisTime;
			
			int amountWrittenThisTime = out.write(buffer, 0, amountReadThisTime);
			assert amountWrittenThisTime >= 0 && amountWrittenThisTime <= amountReadThisTime;
			
			if (amountWrittenThisTime < amountReadThisTime)
				throw new ShortWriteIOException(amountReadThisTime, amountWrittenThisTime);
			
			amountSoFar += amountWrittenThisTime;
			
			if (amountReadThisTime < amountRequestedThisTime)
				break;
		}
		
		
		return amountSoFar;
	}
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * This is NOT indolent—the only reason this would ever return less than requested is due to EOF on the input!
	 * @return the number of units transferred!
	 */
	public static long pumpFixed(CharBlockReadStream in, CharBlockWriteStream out, long amountRequested, char[] buffer) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		assert amountRequested > 0;
		assert buffer != null;
		
		
		final int bufferLength = buffer.length;
		
		
		long amountSoFar = 0;
		
		while (amountSoFar < amountRequested)
		{
			long remaining = amountRequested - amountSoFar;
			int remaining32 = remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
			int amountRequestedThisTime = remaining32 > bufferLength ? bufferLength : remaining32;
			
			int amountReadThisTime = in.read(buffer, 0, amountRequestedThisTime);
			assert amountReadThisTime >= 0 && amountReadThisTime <= amountRequestedThisTime;
			
			int amountWrittenThisTime = out.write(buffer, 0, amountReadThisTime);
			assert amountWrittenThisTime >= 0 && amountWrittenThisTime <= amountReadThisTime;
			
			if (amountWrittenThisTime < amountReadThisTime)
				throw new ShortWriteIOException(amountReadThisTime, amountWrittenThisTime);
			
			amountSoFar += amountWrittenThisTime;
			
			if (amountReadThisTime < amountRequestedThisTime)
				break;
		}
		
		
		return amountSoFar;
	}
	
	
	
	
	
	
	
	
	
	public static void writeMandatory(CharBlockWriteStream stream, char[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		int amountActuallyWritten = stream.write(buffer, offset, length);
		if (amountActuallyWritten != length)
			throw new ShortWriteIOException(length, amountActuallyWritten);
	}
	
	public static void writeMandatory(CharBlockWriteStream stream, char[] buffer) throws IOException, ClosedStreamException
	{
		writeMandatory(stream, buffer, 0, buffer.length);
	}
	
	public static void writeMandatory(CharBlockWriteStream stream, Slice<char[]> buffer) throws IOException, ClosedStreamException
	{
		writeMandatory(stream, buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * This is NOT indolent—the only reason this would ever return less than requested is due to EOF on the input!
	 * @return the number of units transferred!
	 */
	public static long pumpFixed(ShortBlockReadStream in, ShortBlockWriteStream out, long amount) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		short[] buffer = new short[DefaultPumpBufferSize];
		return pumpFixed(in, out, amount, buffer);
	}
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * @return the number of units transferred!
	 */
	public static long pumpToInputEOF(ShortBlockReadStream in, ShortBlockWriteStream out) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		short[] buffer = new short[DefaultPumpBufferSize];
		return pumpToInputEOF(in, out, buffer);
	}
	
	
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * @return the number of units transferred!
	 */
	public static long pumpToInputEOF(ShortBlockReadStream in, ShortBlockWriteStream out, short[] buffer) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		assert buffer != null;
		
		
		final int bufferLength = buffer.length;
		
		
		long amountSoFar = 0;
		
		while (true)
		{
			int amountRequestedThisTime = bufferLength;
			
			int amountReadThisTime = in.read(buffer, 0, amountRequestedThisTime);
			assert amountReadThisTime >= 0 && amountReadThisTime <= amountRequestedThisTime;
			
			int amountWrittenThisTime = out.write(buffer, 0, amountReadThisTime);
			assert amountWrittenThisTime >= 0 && amountWrittenThisTime <= amountReadThisTime;
			
			if (amountWrittenThisTime < amountReadThisTime)
				throw new ShortWriteIOException(amountReadThisTime, amountWrittenThisTime);
			
			amountSoFar += amountWrittenThisTime;
			
			if (amountReadThisTime < amountRequestedThisTime)
				break;
		}
		
		
		return amountSoFar;
	}
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * This is NOT indolent—the only reason this would ever return less than requested is due to EOF on the input!
	 * @return the number of units transferred!
	 */
	public static long pumpFixed(ShortBlockReadStream in, ShortBlockWriteStream out, long amountRequested, short[] buffer) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		assert amountRequested > 0;
		assert buffer != null;
		
		
		final int bufferLength = buffer.length;
		
		
		long amountSoFar = 0;
		
		while (amountSoFar < amountRequested)
		{
			long remaining = amountRequested - amountSoFar;
			int remaining32 = remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
			int amountRequestedThisTime = remaining32 > bufferLength ? bufferLength : remaining32;
			
			int amountReadThisTime = in.read(buffer, 0, amountRequestedThisTime);
			assert amountReadThisTime >= 0 && amountReadThisTime <= amountRequestedThisTime;
			
			int amountWrittenThisTime = out.write(buffer, 0, amountReadThisTime);
			assert amountWrittenThisTime >= 0 && amountWrittenThisTime <= amountReadThisTime;
			
			if (amountWrittenThisTime < amountReadThisTime)
				throw new ShortWriteIOException(amountReadThisTime, amountWrittenThisTime);
			
			amountSoFar += amountWrittenThisTime;
			
			if (amountReadThisTime < amountRequestedThisTime)
				break;
		}
		
		
		return amountSoFar;
	}
	
	
	
	
	
	
	
	
	
	public static void writeMandatory(ShortBlockWriteStream stream, short[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		int amountActuallyWritten = stream.write(buffer, offset, length);
		if (amountActuallyWritten != length)
			throw new ShortWriteIOException(length, amountActuallyWritten);
	}
	
	public static void writeMandatory(ShortBlockWriteStream stream, short[] buffer) throws IOException, ClosedStreamException
	{
		writeMandatory(stream, buffer, 0, buffer.length);
	}
	
	public static void writeMandatory(ShortBlockWriteStream stream, Slice<short[]> buffer) throws IOException, ClosedStreamException
	{
		writeMandatory(stream, buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * This is NOT indolent—the only reason this would ever return less than requested is due to EOF on the input!
	 * @return the number of units transferred!
	 */
	public static long pumpFixed(FloatBlockReadStream in, FloatBlockWriteStream out, long amount) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		float[] buffer = new float[DefaultPumpBufferSize];
		return pumpFixed(in, out, amount, buffer);
	}
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * @return the number of units transferred!
	 */
	public static long pumpToInputEOF(FloatBlockReadStream in, FloatBlockWriteStream out) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		float[] buffer = new float[DefaultPumpBufferSize];
		return pumpToInputEOF(in, out, buffer);
	}
	
	
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * @return the number of units transferred!
	 */
	public static long pumpToInputEOF(FloatBlockReadStream in, FloatBlockWriteStream out, float[] buffer) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		assert buffer != null;
		
		
		final int bufferLength = buffer.length;
		
		
		long amountSoFar = 0;
		
		while (true)
		{
			int amountRequestedThisTime = bufferLength;
			
			int amountReadThisTime = in.read(buffer, 0, amountRequestedThisTime);
			assert amountReadThisTime >= 0 && amountReadThisTime <= amountRequestedThisTime;
			
			int amountWrittenThisTime = out.write(buffer, 0, amountReadThisTime);
			assert amountWrittenThisTime >= 0 && amountWrittenThisTime <= amountReadThisTime;
			
			if (amountWrittenThisTime < amountReadThisTime)
				throw new ShortWriteIOException(amountReadThisTime, amountWrittenThisTime);
			
			amountSoFar += amountWrittenThisTime;
			
			if (amountReadThisTime < amountRequestedThisTime)
				break;
		}
		
		
		return amountSoFar;
	}
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * This is NOT indolent—the only reason this would ever return less than requested is due to EOF on the input!
	 * @return the number of units transferred!
	 */
	public static long pumpFixed(FloatBlockReadStream in, FloatBlockWriteStream out, long amountRequested, float[] buffer) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		assert amountRequested > 0;
		assert buffer != null;
		
		
		final int bufferLength = buffer.length;
		
		
		long amountSoFar = 0;
		
		while (amountSoFar < amountRequested)
		{
			long remaining = amountRequested - amountSoFar;
			int remaining32 = remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
			int amountRequestedThisTime = remaining32 > bufferLength ? bufferLength : remaining32;
			
			int amountReadThisTime = in.read(buffer, 0, amountRequestedThisTime);
			assert amountReadThisTime >= 0 && amountReadThisTime <= amountRequestedThisTime;
			
			int amountWrittenThisTime = out.write(buffer, 0, amountReadThisTime);
			assert amountWrittenThisTime >= 0 && amountWrittenThisTime <= amountReadThisTime;
			
			if (amountWrittenThisTime < amountReadThisTime)
				throw new ShortWriteIOException(amountReadThisTime, amountWrittenThisTime);
			
			amountSoFar += amountWrittenThisTime;
			
			if (amountReadThisTime < amountRequestedThisTime)
				break;
		}
		
		
		return amountSoFar;
	}
	
	
	
	
	
	
	
	
	
	public static void writeMandatory(FloatBlockWriteStream stream, float[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		int amountActuallyWritten = stream.write(buffer, offset, length);
		if (amountActuallyWritten != length)
			throw new ShortWriteIOException(length, amountActuallyWritten);
	}
	
	public static void writeMandatory(FloatBlockWriteStream stream, float[] buffer) throws IOException, ClosedStreamException
	{
		writeMandatory(stream, buffer, 0, buffer.length);
	}
	
	public static void writeMandatory(FloatBlockWriteStream stream, Slice<float[]> buffer) throws IOException, ClosedStreamException
	{
		writeMandatory(stream, buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * This is NOT indolent—the only reason this would ever return less than requested is due to EOF on the input!
	 * @return the number of units transferred!
	 */
	public static long pumpFixed(IntBlockReadStream in, IntBlockWriteStream out, long amount) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		int[] buffer = new int[DefaultPumpBufferSize];
		return pumpFixed(in, out, amount, buffer);
	}
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * @return the number of units transferred!
	 */
	public static long pumpToInputEOF(IntBlockReadStream in, IntBlockWriteStream out) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		int[] buffer = new int[DefaultPumpBufferSize];
		return pumpToInputEOF(in, out, buffer);
	}
	
	
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * @return the number of units transferred!
	 */
	public static long pumpToInputEOF(IntBlockReadStream in, IntBlockWriteStream out, int[] buffer) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		assert buffer != null;
		
		
		final int bufferLength = buffer.length;
		
		
		long amountSoFar = 0;
		
		while (true)
		{
			int amountRequestedThisTime = bufferLength;
			
			int amountReadThisTime = in.read(buffer, 0, amountRequestedThisTime);
			assert amountReadThisTime >= 0 && amountReadThisTime <= amountRequestedThisTime;
			
			int amountWrittenThisTime = out.write(buffer, 0, amountReadThisTime);
			assert amountWrittenThisTime >= 0 && amountWrittenThisTime <= amountReadThisTime;
			
			if (amountWrittenThisTime < amountReadThisTime)
				throw new ShortWriteIOException(amountReadThisTime, amountWrittenThisTime);
			
			amountSoFar += amountWrittenThisTime;
			
			if (amountReadThisTime < amountRequestedThisTime)
				break;
		}
		
		
		return amountSoFar;
	}
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * This is NOT indolent—the only reason this would ever return less than requested is due to EOF on the input!
	 * @return the number of units transferred!
	 */
	public static long pumpFixed(IntBlockReadStream in, IntBlockWriteStream out, long amountRequested, int[] buffer) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		assert amountRequested > 0;
		assert buffer != null;
		
		
		final int bufferLength = buffer.length;
		
		
		long amountSoFar = 0;
		
		while (amountSoFar < amountRequested)
		{
			long remaining = amountRequested - amountSoFar;
			int remaining32 = remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
			int amountRequestedThisTime = remaining32 > bufferLength ? bufferLength : remaining32;
			
			int amountReadThisTime = in.read(buffer, 0, amountRequestedThisTime);
			assert amountReadThisTime >= 0 && amountReadThisTime <= amountRequestedThisTime;
			
			int amountWrittenThisTime = out.write(buffer, 0, amountReadThisTime);
			assert amountWrittenThisTime >= 0 && amountWrittenThisTime <= amountReadThisTime;
			
			if (amountWrittenThisTime < amountReadThisTime)
				throw new ShortWriteIOException(amountReadThisTime, amountWrittenThisTime);
			
			amountSoFar += amountWrittenThisTime;
			
			if (amountReadThisTime < amountRequestedThisTime)
				break;
		}
		
		
		return amountSoFar;
	}
	
	
	
	
	
	
	
	
	
	public static void writeMandatory(IntBlockWriteStream stream, int[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		int amountActuallyWritten = stream.write(buffer, offset, length);
		if (amountActuallyWritten != length)
			throw new ShortWriteIOException(length, amountActuallyWritten);
	}
	
	public static void writeMandatory(IntBlockWriteStream stream, int[] buffer) throws IOException, ClosedStreamException
	{
		writeMandatory(stream, buffer, 0, buffer.length);
	}
	
	public static void writeMandatory(IntBlockWriteStream stream, Slice<int[]> buffer) throws IOException, ClosedStreamException
	{
		writeMandatory(stream, buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * This is NOT indolent—the only reason this would ever return less than requested is due to EOF on the input!
	 * @return the number of units transferred!
	 */
	public static long pumpFixed(DoubleBlockReadStream in, DoubleBlockWriteStream out, long amount) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		double[] buffer = new double[DefaultPumpBufferSize];
		return pumpFixed(in, out, amount, buffer);
	}
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * @return the number of units transferred!
	 */
	public static long pumpToInputEOF(DoubleBlockReadStream in, DoubleBlockWriteStream out) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		double[] buffer = new double[DefaultPumpBufferSize];
		return pumpToInputEOF(in, out, buffer);
	}
	
	
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * @return the number of units transferred!
	 */
	public static long pumpToInputEOF(DoubleBlockReadStream in, DoubleBlockWriteStream out, double[] buffer) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		assert buffer != null;
		
		
		final int bufferLength = buffer.length;
		
		
		long amountSoFar = 0;
		
		while (true)
		{
			int amountRequestedThisTime = bufferLength;
			
			int amountReadThisTime = in.read(buffer, 0, amountRequestedThisTime);
			assert amountReadThisTime >= 0 && amountReadThisTime <= amountRequestedThisTime;
			
			int amountWrittenThisTime = out.write(buffer, 0, amountReadThisTime);
			assert amountWrittenThisTime >= 0 && amountWrittenThisTime <= amountReadThisTime;
			
			if (amountWrittenThisTime < amountReadThisTime)
				throw new ShortWriteIOException(amountReadThisTime, amountWrittenThisTime);
			
			amountSoFar += amountWrittenThisTime;
			
			if (amountReadThisTime < amountRequestedThisTime)
				break;
		}
		
		
		return amountSoFar;
	}
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * This is NOT indolent—the only reason this would ever return less than requested is due to EOF on the input!
	 * @return the number of units transferred!
	 */
	public static long pumpFixed(DoubleBlockReadStream in, DoubleBlockWriteStream out, long amountRequested, double[] buffer) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		assert amountRequested > 0;
		assert buffer != null;
		
		
		final int bufferLength = buffer.length;
		
		
		long amountSoFar = 0;
		
		while (amountSoFar < amountRequested)
		{
			long remaining = amountRequested - amountSoFar;
			int remaining32 = remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
			int amountRequestedThisTime = remaining32 > bufferLength ? bufferLength : remaining32;
			
			int amountReadThisTime = in.read(buffer, 0, amountRequestedThisTime);
			assert amountReadThisTime >= 0 && amountReadThisTime <= amountRequestedThisTime;
			
			int amountWrittenThisTime = out.write(buffer, 0, amountReadThisTime);
			assert amountWrittenThisTime >= 0 && amountWrittenThisTime <= amountReadThisTime;
			
			if (amountWrittenThisTime < amountReadThisTime)
				throw new ShortWriteIOException(amountReadThisTime, amountWrittenThisTime);
			
			amountSoFar += amountWrittenThisTime;
			
			if (amountReadThisTime < amountRequestedThisTime)
				break;
		}
		
		
		return amountSoFar;
	}
	
	
	
	
	
	
	
	
	
	public static void writeMandatory(DoubleBlockWriteStream stream, double[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		int amountActuallyWritten = stream.write(buffer, offset, length);
		if (amountActuallyWritten != length)
			throw new ShortWriteIOException(length, amountActuallyWritten);
	}
	
	public static void writeMandatory(DoubleBlockWriteStream stream, double[] buffer) throws IOException, ClosedStreamException
	{
		writeMandatory(stream, buffer, 0, buffer.length);
	}
	
	public static void writeMandatory(DoubleBlockWriteStream stream, Slice<double[]> buffer) throws IOException, ClosedStreamException
	{
		writeMandatory(stream, buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * This is NOT indolent—the only reason this would ever return less than requested is due to EOF on the input!
	 * @return the number of units transferred!
	 */
	public static long pumpFixed(LongBlockReadStream in, LongBlockWriteStream out, long amount) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		long[] buffer = new long[DefaultPumpBufferSize];
		return pumpFixed(in, out, amount, buffer);
	}
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * @return the number of units transferred!
	 */
	public static long pumpToInputEOF(LongBlockReadStream in, LongBlockWriteStream out) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		long[] buffer = new long[DefaultPumpBufferSize];
		return pumpToInputEOF(in, out, buffer);
	}
	
	
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * @return the number of units transferred!
	 */
	public static long pumpToInputEOF(LongBlockReadStream in, LongBlockWriteStream out, long[] buffer) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		assert buffer != null;
		
		
		final int bufferLength = buffer.length;
		
		
		long amountSoFar = 0;
		
		while (true)
		{
			int amountRequestedThisTime = bufferLength;
			
			int amountReadThisTime = in.read(buffer, 0, amountRequestedThisTime);
			assert amountReadThisTime >= 0 && amountReadThisTime <= amountRequestedThisTime;
			
			int amountWrittenThisTime = out.write(buffer, 0, amountReadThisTime);
			assert amountWrittenThisTime >= 0 && amountWrittenThisTime <= amountReadThisTime;
			
			if (amountWrittenThisTime < amountReadThisTime)
				throw new ShortWriteIOException(amountReadThisTime, amountWrittenThisTime);
			
			amountSoFar += amountWrittenThisTime;
			
			if (amountReadThisTime < amountRequestedThisTime)
				break;
		}
		
		
		return amountSoFar;
	}
	
	
	
	
	/**
	 * Note that the output stream MUST never EOF!!  (This will throw a {@link ShortWriteIOException} if it does!)
	 * This is NOT indolent—the only reason this would ever return less than requested is due to EOF on the input!
	 * @return the number of units transferred!
	 */
	public static long pumpFixed(LongBlockReadStream in, LongBlockWriteStream out, long amountRequested, long[] buffer) throws ClosedStreamException, IOException, ShortWriteIOException
	{
		assert amountRequested > 0;
		assert buffer != null;
		
		
		final int bufferLength = buffer.length;
		
		
		long amountSoFar = 0;
		
		while (amountSoFar < amountRequested)
		{
			long remaining = amountRequested - amountSoFar;
			int remaining32 = remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
			int amountRequestedThisTime = remaining32 > bufferLength ? bufferLength : remaining32;
			
			int amountReadThisTime = in.read(buffer, 0, amountRequestedThisTime);
			assert amountReadThisTime >= 0 && amountReadThisTime <= amountRequestedThisTime;
			
			int amountWrittenThisTime = out.write(buffer, 0, amountReadThisTime);
			assert amountWrittenThisTime >= 0 && amountWrittenThisTime <= amountReadThisTime;
			
			if (amountWrittenThisTime < amountReadThisTime)
				throw new ShortWriteIOException(amountReadThisTime, amountWrittenThisTime);
			
			amountSoFar += amountWrittenThisTime;
			
			if (amountReadThisTime < amountRequestedThisTime)
				break;
		}
		
		
		return amountSoFar;
	}
	
	
	
	
	
	
	
	
	
	public static void writeMandatory(LongBlockWriteStream stream, long[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		int amountActuallyWritten = stream.write(buffer, offset, length);
		if (amountActuallyWritten != length)
			throw new ShortWriteIOException(length, amountActuallyWritten);
	}
	
	public static void writeMandatory(LongBlockWriteStream stream, long[] buffer) throws IOException, ClosedStreamException
	{
		writeMandatory(stream, buffer, 0, buffer.length);
	}
	
	public static void writeMandatory(LongBlockWriteStream stream, Slice<long[]> buffer) throws IOException, ClosedStreamException
	{
		writeMandatory(stream, buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<Adapters
	//<SIO
	public static InputStream getAsJREInputStream(ByteBlockReadStream stream)
	{
		if (stream instanceof InputStreamWrapper)
			return ((InputStreamWrapper)stream).getUnderlying();
		return new InputStreamAdapter(stream);
	}
	
	public static OutputStream getAsJREOutputStream(ByteBlockWriteStream stream)
	{
		if (stream instanceof OutputStreamWrapper)
			return ((OutputStreamWrapper)stream).getOutputStream();
		return new OutputStreamAdapter(stream);
	}
	
	public static ByteBlockReadStream getAsRIOReadStream(InputStream stream)
	{
		if (stream instanceof InputStreamAdapter)
			return ((InputStreamAdapter)stream).getUnderlying();
		return new InputStreamWrapper(stream);
	}
	
	public static ByteBlockWriteStream getAsRIOWriteStream(OutputStream stream)
	{
		if (stream instanceof OutputStreamAdapter)
			return ((OutputStreamAdapter)stream).getWriteStream();
		return new OutputStreamWrapper(stream);
	}
	//SIO>
	
	
	//<Unit/Block
	public static <D> ReferenceUnitReadStream<D> getAsUnitReadStream(ReferenceBlockReadStream<D> stream)
	{
		if (stream instanceof ReferenceUnitToBlockReadStream)
			return ((ReferenceUnitToBlockReadStream<D>)stream).getUnderlying();
		return new ReferenceBlockToUnitReadStream(stream);
	}
	
	public static <D> ReferenceUnitWriteStream<D> getAsUnitWriteStream(ReferenceBlockWriteStream<D> stream)
	{
		if (stream instanceof ReferenceUnitToBlockWriteStream)
			return ((ReferenceUnitToBlockWriteStream<D>)stream).getUnderlying();
		return new ReferenceBlockToUnitWriteStream(stream);
	}
	
	public static <D> ReferenceBlockReadStream<D> getAsBlockReadStream(ReferenceUnitReadStream<D> stream)
	{
		if (stream instanceof ReferenceBlockToUnitReadStream)
			return ((ReferenceBlockToUnitReadStream<D>)stream).getUnderlying();
		return new ReferenceUnitToBlockReadStream(stream);
	}
	
	public static <D> ReferenceBlockWriteStream<D> getAsBlockWriteStream(ReferenceUnitWriteStream<D> stream)
	{
		if (stream instanceof ReferenceBlockToUnitWriteStream)
			return ((ReferenceBlockToUnitWriteStream<D>)stream).getUnderlying();
		return new ReferenceUnitToBlockWriteStream(stream);
	}
	//Unit/Block>
	//Adapters>
	
	
	
	
	
	
	
	
	private StreamUsageUtilities() {}
}
