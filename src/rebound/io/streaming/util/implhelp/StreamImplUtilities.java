/*
 * Created on May 26, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.util.implhelp;

import java.io.IOException;
import java.io.InputStream;
import rebound.exceptions.ImpossibleException;
import rebound.io.streaming.api.BlockReadStream;
import rebound.io.streaming.api.BlockWriteStream;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.Stream;
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
import rebound.io.streaming.api.StreamAPIs.ShortBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.ShortBlockWriteStream;

//Todo update the indolents and etc. for primitives!

/**
 * Utilities for implementations of streams are housed here.
 * @author RProgrammer
 */
public class StreamImplUtilities
{
	protected static final int DefaultSkipBufferSize = 1024;
	
	
	
	
	
	
	/**
	 * Suitable for parameter-validation for read(<i>any</i>[], int offset, int length) and write(<i>any</i>[], int offset, int length).
	 * @param bufferCapacity (ie, buffer.length)
	 */
	public static void checkBlockMethodParameters(Object buffer, int bufferCapacity, int offset, int length)
	{
		if (buffer == null)
			//Todo throw new NullPointerException("The buffer provided was null.");
			throw new IllegalArgumentException();
		
		if (offset < 0 || offset > bufferCapacity)
			//Todo throw new IllegalArgumentException("The offset provided ("+offset+") is invalid.  (buffer capacity was "+bufferCapacity+")");
			throw new IllegalArgumentException();
		
		if (length < 0 || length > bufferCapacity)
			//Todo throw new IllegalArgumentException("The length provided ("+offset+") is invalid.  (buffer capacity was "+bufferCapacity+")");
			throw new IllegalArgumentException();
		
		if (offset+length > bufferCapacity)
			//Todo throw new IllegalArgumentException(...);
			throw new IllegalArgumentException();
	}
	
	
	//	* Suitable for parameter-validation of {@link Stream#skip(long)} and {@link PrecoupledStream#drive(int)} (don't worry about the cast)
	/**
	 * Suitable for parameter-validation of {@link Stream#skip(long)} (don't worry about the cast)
	 */
	public static void checkDriveMethodParameters(long amount)
	{
		if (amount < 0)
			//Todo throw new IllegalArgumentException("The amount provided ("+amount+") is negative and thus invalid.");
			throw new IllegalArgumentException();
	}
	
	
	/**
	 * Use this to get a standard error message for the case where an invalid amount is returned by a block operation (eg, {@link ByteBlockReadStream#read(byte[], int, int)} &lt; 0 || &gt; <code>length</code>), if you're nice enough to check for such cases.
	 * <p>The only cases where this can happen are when the returned amount is negative, or greater than the requested amount.  0 and less-than-requested are indications of EOF.
	 */
	public static ImpossibleException getExceptionForInvalidBlockAmount(int amount, Stream offender)
	{
		if (amount < 0)
			//Todo return new ImpossibleException("The stream ("+offender.getClass().getName()+") returned a negative length for a block operation.");
			throw new ImpossibleException();
		
		else
			//Todo return new ImpossibleException("The stream ("+offender.getClass().getName()+") returned a greater length for a block operation than requested.");
			throw new ImpossibleException();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//OH, this kills the stacktrace!!  x"DDD
	//Todo bring these back, but in method form!
	
	//	/**
	//	 * This is for performance.
	//	 * Most uses of {@link ClosedStreamException} use the default state, which is immutable anyway,
	//	 * so this provides a single instance that won't be garbage collected and reallocated.
	//	 * <br>
	//	 * Users should not rely on any {@link ClosedStreamException}s being == this instance, it is here purely for performance reasons.
	//	 */
	//	public static final ClosedStreamException ClosedStreamException_Instance = rebound.io.streaming.ClosedStreamException.getInstance();
	//
	//	/**
	//	 * See {@link #ClosedStreamException_Instance}
	//	 */
	//	public static final EOFException EOFException_Instance = new java.io.EOFException();
	//
	//	/**
	//	 * In cases where a ClosedStreamException is not expected (such as SIO), this can be used instead.
	//	 */
	//	public static final IOException IOExceptionForClosedStreamException_Instance = new java.io.IOException("The stream has been closed.");
	//
	//	/**
	//	 * If the length provided in a parameter is negative.
	//	 * Eg, length in read([], int, int) is negative.
	//	 */
	//	public static final IllegalArgumentException IllegalArgumentExceptionForNegativeLength_Instance = new IllegalArgumentException("Provided length is negative.");
	//
	//	/**
	//	 * If the offset provided in a parameter is invalid.
	//	 * Eg, offset in read([], int, int) is invalid.
	//	 */
	//	public static final IllegalArgumentException IllegalArgumentExceptionForInvalidOffset_Instance = new IllegalArgumentException("Provided offset is invalid.");
	//
	//	/**
	//	 * If the cursor provided to {@link SeekableStream#setCursor(long)} is negative.
	//	 */
	//	public static final IllegalArgumentException IllegalArgumentExceptionForNegativeCursor_Instance = new IllegalArgumentException("Provided cursor is negative.");
	//
	//	/**
	//	 * When a {@link NullPointerException} is needed with no message.
	//	 */
	//	public static final NullPointerException NullPointerException_Instance = new NullPointerException();
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * An indolent stream is one in whom a particular method (skip() in this case) is allowed to do less than asked (eg, like {@link InputStream}).
	 * The public API forces an implementation to perform the task to completion, but if this is difficult, an Indolent algorithm can be implemented and passed to {@link StreamImplUtilities} to force completion.
	 * 
	 * <p>In other words, the forceRead(InputStream, ...) utility that practically everyone puts in their 'IOUtils' is put into {@link StreamImplUtilities} for RIO, and the onus to call forceRead() is on the stream implementations, rather than the API user.
	 * <br>
	 * <br>
	 * Note: The Indolent API, while intended for use by {@link StreamImplUtilities}, must be capable of being used by the API user in lieu of the normal (forced) method.
	 * @author RProgrammer
	 */
	public static interface IndolentStream
	extends Stream
	{
		public long skipIndolent(long requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return StreamImplUtilities.forceSkip(this, amount);
		}
	}
	
	
	
	public static interface IndolentBlockReadStream<B, A>
	extends IndolentStream, BlockReadStream<B, A>
	{
	}
	
	public static interface IndolentBlockWriteStream<B, A>
	extends IndolentStream, BlockWriteStream<B, A>
	{
	}
	
	
	
	//	public static interface IndolentPrecoupledStream
	//	extends IndolentStream, PrecoupledStream
	//	{
	//		public int driveIndolent(int requestedAmount) throws IOException, ClosedStreamException;
	//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static long forceSkip(IndolentStream indolentStream, long amount) throws ClosedStreamException, IOException
	{
		checkDriveMethodParameters(amount);
		
		long amt = 0;
		long total = 0;
		
		while (true)
		{
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			amt = indolentStream.skipIndolent(amount-total);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for skip()");
				throw new ImpossibleException();
			
			if (amt > amount)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for skip() than requested.");
				throw new ImpossibleException();
			
			
			total += amt;
			
			if (total == amount)
				return total;
			
			if (indolentStream.isEOF())
				return total;
		}
	}
	
	
	//	public static int forceDrive(IndolentPrecoupledStream indolentStream, int amount) throws ClosedStreamException, IOException
	//	{
	//		checkDriveMethodParameters(amount);
	//
	//		int amt = 0;
	//		int total = 0;
	//
	//		while (true)
	//		{
	//			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
	//			amt = indolentStream.driveIndolent(amount-total);
	//
	//			if (amt < 0)
	//				throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for skip()");
	//			if (amt > amount)
	//				throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for skip() than requested.");
	//
	//			total += amt;
	//
	//			if (total == amount)
	//				return total;
	//
	//			if (indolentStream.isEOF())
	//				return total;
	//		}
	//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static interface IndolentReferenceBlockReadStream<D>
	extends IndolentBlockReadStream<D, D[]>, ReferenceBlockReadStream<D>
	{
		public int readIndolent(D[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default int read(D[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return StreamImplUtilities.forceRead(this, buffer, offset, length);
		}
	}
	
	
	public static interface IndolentReferenceBlockWriteStream<D>
	extends IndolentBlockWriteStream<D, D[]>, ReferenceBlockWriteStream<D>
	{
		public int writeIndolent(D[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default int write(D[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return StreamImplUtilities.forceWrite(this, buffer, offset, length);
		}
	}
	
	
	
	
	
	
	
	public static <D> int forceRead(IndolentReferenceBlockReadStream<D> indolentStream, D[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		checkBlockMethodParameters(buffer, buffer.length, offset, length);
		
		int amt = 0;
		int total = 0;
		
		while (true)
		{
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			amt = indolentStream.readIndolent(buffer, offset, length-total);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for read([], int, int)");
				throw new ImpossibleException();
			
			if (amt > length)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for read([], int, int) than requested.");
				throw new ImpossibleException();
			
			
			
			total += amt;
			
			if (total == length)
				return total;
			
			if (indolentStream.isEOF())
				return total;
		}
	}
	
	
	public static <D> int forceWrite(IndolentReferenceBlockWriteStream<D> indolentStream, D[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		checkBlockMethodParameters(buffer, buffer.length, offset, length);
		
		int amt = 0;
		int total = 0;
		
		while (true)
		{
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			amt = indolentStream.writeIndolent(buffer, offset, length-total);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for read([], int, int)");
				throw new ImpossibleException();
			
			if (amt > length)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for read([], int, int) than requested.");
				throw new ImpossibleException();
			
			
			total += amt;
			
			if (total == length)
				return total;
			
			if (indolentStream.isEOF())
				return total;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	//This might be concurrently accessed, but it is raw memory (a JVM array) that is only written to, NEVER read from, so it's fine to not synchronize!  :D
	// (the variable is read from and written to, but the worst that could happen is duplicate allocations, all but one of which will subsequently garbage collected, with no harm done.  And that's highly unlikely anyway :3  )
	protected static Object[] GlobalDirtySkipBufferForReferences;
	public static long skipByDiscarding(ReferenceBlockReadStream readstream, long amount) throws ClosedStreamException, IOException
	{
		Object[] discardBuffer;
		{
			discardBuffer = GlobalDirtySkipBufferForReferences;
			
			if (discardBuffer == null)
			{
				discardBuffer = new Object[DefaultSkipBufferSize];
				GlobalDirtySkipBufferForReferences = discardBuffer;
			}
		}
		
		
		return skipByDiscarding(readstream, amount, GlobalDirtySkipBufferForReferences);
	}
	
	
	
	public static long skipByDiscarding(ReferenceBlockReadStream readstream, long amount, Object[] discardBuffer) throws ClosedStreamException, IOException
	{
		if (amount < 0)
			throw new IllegalArgumentException("skip amount cannot be < 0");
		
		if (readstream.isClosed())
			throw new ClosedStreamException();
		
		if (amount == 0 || readstream.isEOF())
			return 0;
		
		
		int discardBufferSize = discardBuffer.length;
		
		long readSoFar = 0;
		while (true)
		{
			long remaining = amount - readSoFar;
			int remaining32 = remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
			int requested = remaining32 > discardBufferSize ? discardBufferSize : remaining32;
			
			int amountReadThisTime = readstream.read(discardBuffer, 0, requested);
			readSoFar += amountReadThisTime;
			
			if (amountReadThisTime < requested)
				break;
		}
		
		assert readSoFar <= amount;
		
		return readSoFar;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	primxp
	
	
	
	
	
	
	
	public static interface Indolent_$$Prim$$_BlockReadStream
	extends _$$Prim$$_BlockReadStream, IndolentBlockReadStream<_$$Primitive$$_, _$$prim$$_[]>
	{
		public int readIndolent(_$$prim$$_[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default int read(_$$prim$$_[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return StreamImplUtilities.forceRead(this, buffer, offset, length);
		}
	}
	
	public static interface Indolent_$$Prim$$_BlockWriteStream
	extends _$$Prim$$_BlockWriteStream, IndolentBlockWriteStream<_$$Primitive$$_, _$$prim$$_[]>
	{
		public int writeIndolent(_$$prim$$_[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default int write(_$$prim$$_[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return StreamImplUtilities.forceWrite(this, buffer, offset, length);
		}
	}
	
	
	
	
	
	public static int forceRead(Indolent_$$Prim$$_BlockReadStream indolentStream, _$$prim$$_[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		checkBlockMethodParameters(buffer, buffer.length, offset, length);
		
		int original = length;
		
		while (true)
		{
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			
			int amt = indolentStream.readIndolent(buffer, offset, length);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for read(_$$prim$$_[], int, int)");
				throw new ImpossibleException();
			
			if (amt > length)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for read(_$$prim$$_[], int, int) than requested.");
				throw new ImpossibleException();
			
			
			offset += amt;
			length -= amt;
			
			if (length == 0)
				return original;
			
			if (indolentStream.isEOF())
				return original - length;
		}
	}
	
	
	
	public static int forceWrite(Indolent_$$Prim$$_BlockWriteStream indolentStream, _$$prim$$_[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		checkBlockMethodParameters(buffer, buffer.length, offset, length);
		
		int original = length;
		
		while (true)
		{
			if (length == 0)
				return original;
			
			if (indolentStream.isEOF())
				return original - length;
			
			
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			
			int amt = indolentStream.writeIndolent(buffer, offset, length);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for write(_$$prim$$_[], int, int)");
				throw new ImpossibleException();
			
			if (amt > length)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for write(_$$prim$$_[], int, int) than requested.");
				throw new ImpossibleException();
			
			
			offset += amt;
			length -= amt;
		}
	}
	
	
	
	
	
	
	//This might be concurrently accessed, but it is raw memory (a JVM array) that is only written to, NEVER read from, so it's fine to not synchronize!  :D
	// (the variable is read from and written to, but the worst that could happen is duplicate allocations, all but one of which will subsequently garbage collected, with no harm done.  And that's highly unlikely anyway :3  )
	protected static _$$prim$$_[] GlobalDirtySkipBufferFor_$$Prim$$_s;
	public static long skipByDiscarding(_$$Prim$$_BlockReadStream readstream, long amount) throws ClosedStreamException, IOException
	{
		_$$prim$$_[] discardBuffer;
		{
			discardBuffer = GlobalDirtySkipBufferFor_$$Prim$$_s;
			
			if (discardBuffer == null)
			{
				discardBuffer = new _$$prim$$_[DefaultSkipBufferSize];
				GlobalDirtySkipBufferFor_$$Prim$$_s = discardBuffer;
			}
		}
		
		
		return skipByDiscarding(readstream, amount, GlobalDirtySkipBufferFor_$$Prim$$_s);
	}
	
	
	
	public static long skipByDiscarding(_$$Prim$$_BlockReadStream readstream, long amount, _$$prim$$_[] discardBuffer) throws ClosedStreamException, IOException
	{
		if (amount < 0)
			throw new IllegalArgumentException("skip amount cannot be < 0");
		
		if (readstream.isClosed())
			throw new ClosedStreamException();
		
		if (amount == 0 || readstream.isEOF())
			return 0;
		
		
		int discardBufferSize = discardBuffer.length;
		
		long readSoFar = 0;
		while (true)
		{
			long remaining = amount - readSoFar;
			
			if (remaining <= 0 || readstream.isEOF())
				break;
			
			int remaining32 = remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
			int requested = remaining32 > discardBufferSize ? discardBufferSize : remaining32;
			
			int amountReadThisTime = readstream.read(discardBuffer, 0, requested);
			readSoFar += amountReadThisTime;
			
			if (amountReadThisTime < requested)  //eof!
				break;
		}
		
		assert readSoFar <= amount;
		
		return readSoFar;
	}
	
	
	
	
	
	
	
	
	
	 */
	
	
	
	
	
	
	
	public static interface IndolentBooleanBlockReadStream
	extends BooleanBlockReadStream, IndolentBlockReadStream<Boolean, boolean[]>
	{
		public int readIndolent(boolean[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default int read(boolean[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return StreamImplUtilities.forceRead(this, buffer, offset, length);
		}
	}
	
	public static interface IndolentBooleanBlockWriteStream
	extends BooleanBlockWriteStream, IndolentBlockWriteStream<Boolean, boolean[]>
	{
		public int writeIndolent(boolean[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default int write(boolean[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return StreamImplUtilities.forceWrite(this, buffer, offset, length);
		}
	}
	
	
	
	
	
	public static int forceRead(IndolentBooleanBlockReadStream indolentStream, boolean[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		checkBlockMethodParameters(buffer, buffer.length, offset, length);
		
		int original = length;
		
		while (true)
		{
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			
			int amt = indolentStream.readIndolent(buffer, offset, length);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for read(boolean[], int, int)");
				throw new ImpossibleException();
			
			if (amt > length)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for read(boolean[], int, int) than requested.");
				throw new ImpossibleException();
			
			
			offset += amt;
			length -= amt;
			
			if (length == 0)
				return original;
			
			if (indolentStream.isEOF())
				return original - length;
		}
	}
	
	
	
	public static int forceWrite(IndolentBooleanBlockWriteStream indolentStream, boolean[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		checkBlockMethodParameters(buffer, buffer.length, offset, length);
		
		int original = length;
		
		while (true)
		{
			if (length == 0)
				return original;
			
			if (indolentStream.isEOF())
				return original - length;
			
			
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			
			int amt = indolentStream.writeIndolent(buffer, offset, length);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for write(boolean[], int, int)");
				throw new ImpossibleException();
			
			if (amt > length)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for write(boolean[], int, int) than requested.");
				throw new ImpossibleException();
			
			
			offset += amt;
			length -= amt;
		}
	}
	
	
	
	
	
	
	//This might be concurrently accessed, but it is raw memory (a JVM array) that is only written to, NEVER read from, so it's fine to not synchronize!  :D
	// (the variable is read from and written to, but the worst that could happen is duplicate allocations, all but one of which will subsequently garbage collected, with no harm done.  And that's highly unlikely anyway :3  )
	protected static boolean[] GlobalDirtySkipBufferForBooleans;
	public static long skipByDiscarding(BooleanBlockReadStream readstream, long amount) throws ClosedStreamException, IOException
	{
		boolean[] discardBuffer;
		{
			discardBuffer = GlobalDirtySkipBufferForBooleans;
			
			if (discardBuffer == null)
			{
				discardBuffer = new boolean[DefaultSkipBufferSize];
				GlobalDirtySkipBufferForBooleans = discardBuffer;
			}
		}
		
		
		return skipByDiscarding(readstream, amount, GlobalDirtySkipBufferForBooleans);
	}
	
	
	
	public static long skipByDiscarding(BooleanBlockReadStream readstream, long amount, boolean[] discardBuffer) throws ClosedStreamException, IOException
	{
		if (amount < 0)
			throw new IllegalArgumentException("skip amount cannot be < 0");
		
		if (readstream.isClosed())
			throw new ClosedStreamException();
		
		if (amount == 0 || readstream.isEOF())
			return 0;
		
		
		int discardBufferSize = discardBuffer.length;
		
		long readSoFar = 0;
		while (true)
		{
			long remaining = amount - readSoFar;
			
			if (remaining <= 0 || readstream.isEOF())
				break;
			
			int remaining32 = remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
			int requested = remaining32 > discardBufferSize ? discardBufferSize : remaining32;
			
			int amountReadThisTime = readstream.read(discardBuffer, 0, requested);
			readSoFar += amountReadThisTime;
			
			if (amountReadThisTime < requested)  //eof!
				break;
		}
		
		assert readSoFar <= amount;
		
		return readSoFar;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static interface IndolentByteBlockReadStream
	extends ByteBlockReadStream, IndolentBlockReadStream<Byte, byte[]>
	{
		public int readIndolent(byte[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return StreamImplUtilities.forceRead(this, buffer, offset, length);
		}
	}
	
	public static interface IndolentByteBlockWriteStream
	extends ByteBlockWriteStream, IndolentBlockWriteStream<Byte, byte[]>
	{
		public int writeIndolent(byte[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default int write(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return StreamImplUtilities.forceWrite(this, buffer, offset, length);
		}
	}
	
	
	
	
	
	public static int forceRead(IndolentByteBlockReadStream indolentStream, byte[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		checkBlockMethodParameters(buffer, buffer.length, offset, length);
		
		int original = length;
		
		while (true)
		{
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			
			int amt = indolentStream.readIndolent(buffer, offset, length);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for read(byte[], int, int)");
				throw new ImpossibleException();
			
			if (amt > length)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for read(byte[], int, int) than requested.");
				throw new ImpossibleException();
			
			
			offset += amt;
			length -= amt;
			
			if (length == 0)
				return original;
			
			if (indolentStream.isEOF())
				return original - length;
		}
	}
	
	
	
	public static int forceWrite(IndolentByteBlockWriteStream indolentStream, byte[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		checkBlockMethodParameters(buffer, buffer.length, offset, length);
		
		int original = length;
		
		while (true)
		{
			if (length == 0)
				return original;
			
			if (indolentStream.isEOF())
				return original - length;
			
			
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			
			int amt = indolentStream.writeIndolent(buffer, offset, length);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for write(byte[], int, int)");
				throw new ImpossibleException();
			
			if (amt > length)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for write(byte[], int, int) than requested.");
				throw new ImpossibleException();
			
			
			offset += amt;
			length -= amt;
		}
	}
	
	
	
	
	
	
	//This might be concurrently accessed, but it is raw memory (a JVM array) that is only written to, NEVER read from, so it's fine to not synchronize!  :D
	// (the variable is read from and written to, but the worst that could happen is duplicate allocations, all but one of which will subsequently garbage collected, with no harm done.  And that's highly unlikely anyway :3  )
	protected static byte[] GlobalDirtySkipBufferForBytes;
	public static long skipByDiscarding(ByteBlockReadStream readstream, long amount) throws ClosedStreamException, IOException
	{
		byte[] discardBuffer;
		{
			discardBuffer = GlobalDirtySkipBufferForBytes;
			
			if (discardBuffer == null)
			{
				discardBuffer = new byte[DefaultSkipBufferSize];
				GlobalDirtySkipBufferForBytes = discardBuffer;
			}
		}
		
		
		return skipByDiscarding(readstream, amount, GlobalDirtySkipBufferForBytes);
	}
	
	
	
	public static long skipByDiscarding(ByteBlockReadStream readstream, long amount, byte[] discardBuffer) throws ClosedStreamException, IOException
	{
		if (amount < 0)
			throw new IllegalArgumentException("skip amount cannot be < 0");
		
		if (readstream.isClosed())
			throw new ClosedStreamException();
		
		if (amount == 0 || readstream.isEOF())
			return 0;
		
		
		int discardBufferSize = discardBuffer.length;
		
		long readSoFar = 0;
		while (true)
		{
			long remaining = amount - readSoFar;
			
			if (remaining <= 0 || readstream.isEOF())
				break;
			
			int remaining32 = remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
			int requested = remaining32 > discardBufferSize ? discardBufferSize : remaining32;
			
			int amountReadThisTime = readstream.read(discardBuffer, 0, requested);
			readSoFar += amountReadThisTime;
			
			if (amountReadThisTime < requested)  //eof!
				break;
		}
		
		assert readSoFar <= amount;
		
		return readSoFar;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static interface IndolentCharBlockReadStream
	extends CharBlockReadStream, IndolentBlockReadStream<Character, char[]>
	{
		public int readIndolent(char[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default int read(char[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return StreamImplUtilities.forceRead(this, buffer, offset, length);
		}
	}
	
	public static interface IndolentCharBlockWriteStream
	extends CharBlockWriteStream, IndolentBlockWriteStream<Character, char[]>
	{
		public int writeIndolent(char[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default int write(char[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return StreamImplUtilities.forceWrite(this, buffer, offset, length);
		}
	}
	
	
	
	
	
	public static int forceRead(IndolentCharBlockReadStream indolentStream, char[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		checkBlockMethodParameters(buffer, buffer.length, offset, length);
		
		int original = length;
		
		while (true)
		{
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			
			int amt = indolentStream.readIndolent(buffer, offset, length);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for read(char[], int, int)");
				throw new ImpossibleException();
			
			if (amt > length)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for read(char[], int, int) than requested.");
				throw new ImpossibleException();
			
			
			offset += amt;
			length -= amt;
			
			if (length == 0)
				return original;
			
			if (indolentStream.isEOF())
				return original - length;
		}
	}
	
	
	
	public static int forceWrite(IndolentCharBlockWriteStream indolentStream, char[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		checkBlockMethodParameters(buffer, buffer.length, offset, length);
		
		int original = length;
		
		while (true)
		{
			if (length == 0)
				return original;
			
			if (indolentStream.isEOF())
				return original - length;
			
			
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			
			int amt = indolentStream.writeIndolent(buffer, offset, length);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for write(char[], int, int)");
				throw new ImpossibleException();
			
			if (amt > length)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for write(char[], int, int) than requested.");
				throw new ImpossibleException();
			
			
			offset += amt;
			length -= amt;
		}
	}
	
	
	
	
	
	
	//This might be concurrently accessed, but it is raw memory (a JVM array) that is only written to, NEVER read from, so it's fine to not synchronize!  :D
	// (the variable is read from and written to, but the worst that could happen is duplicate allocations, all but one of which will subsequently garbage collected, with no harm done.  And that's highly unlikely anyway :3  )
	protected static char[] GlobalDirtySkipBufferForChars;
	public static long skipByDiscarding(CharBlockReadStream readstream, long amount) throws ClosedStreamException, IOException
	{
		char[] discardBuffer;
		{
			discardBuffer = GlobalDirtySkipBufferForChars;
			
			if (discardBuffer == null)
			{
				discardBuffer = new char[DefaultSkipBufferSize];
				GlobalDirtySkipBufferForChars = discardBuffer;
			}
		}
		
		
		return skipByDiscarding(readstream, amount, GlobalDirtySkipBufferForChars);
	}
	
	
	
	public static long skipByDiscarding(CharBlockReadStream readstream, long amount, char[] discardBuffer) throws ClosedStreamException, IOException
	{
		if (amount < 0)
			throw new IllegalArgumentException("skip amount cannot be < 0");
		
		if (readstream.isClosed())
			throw new ClosedStreamException();
		
		if (amount == 0 || readstream.isEOF())
			return 0;
		
		
		int discardBufferSize = discardBuffer.length;
		
		long readSoFar = 0;
		while (true)
		{
			long remaining = amount - readSoFar;
			
			if (remaining <= 0 || readstream.isEOF())
				break;
			
			int remaining32 = remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
			int requested = remaining32 > discardBufferSize ? discardBufferSize : remaining32;
			
			int amountReadThisTime = readstream.read(discardBuffer, 0, requested);
			readSoFar += amountReadThisTime;
			
			if (amountReadThisTime < requested)  //eof!
				break;
		}
		
		assert readSoFar <= amount;
		
		return readSoFar;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static interface IndolentShortBlockReadStream
	extends ShortBlockReadStream, IndolentBlockReadStream<Short, short[]>
	{
		public int readIndolent(short[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default int read(short[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return StreamImplUtilities.forceRead(this, buffer, offset, length);
		}
	}
	
	public static interface IndolentShortBlockWriteStream
	extends ShortBlockWriteStream, IndolentBlockWriteStream<Short, short[]>
	{
		public int writeIndolent(short[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default int write(short[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return StreamImplUtilities.forceWrite(this, buffer, offset, length);
		}
	}
	
	
	
	
	
	public static int forceRead(IndolentShortBlockReadStream indolentStream, short[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		checkBlockMethodParameters(buffer, buffer.length, offset, length);
		
		int original = length;
		
		while (true)
		{
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			
			int amt = indolentStream.readIndolent(buffer, offset, length);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for read(short[], int, int)");
				throw new ImpossibleException();
			
			if (amt > length)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for read(short[], int, int) than requested.");
				throw new ImpossibleException();
			
			
			offset += amt;
			length -= amt;
			
			if (length == 0)
				return original;
			
			if (indolentStream.isEOF())
				return original - length;
		}
	}
	
	
	
	public static int forceWrite(IndolentShortBlockWriteStream indolentStream, short[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		checkBlockMethodParameters(buffer, buffer.length, offset, length);
		
		int original = length;
		
		while (true)
		{
			if (length == 0)
				return original;
			
			if (indolentStream.isEOF())
				return original - length;
			
			
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			
			int amt = indolentStream.writeIndolent(buffer, offset, length);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for write(short[], int, int)");
				throw new ImpossibleException();
			
			if (amt > length)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for write(short[], int, int) than requested.");
				throw new ImpossibleException();
			
			
			offset += amt;
			length -= amt;
		}
	}
	
	
	
	
	
	
	//This might be concurrently accessed, but it is raw memory (a JVM array) that is only written to, NEVER read from, so it's fine to not synchronize!  :D
	// (the variable is read from and written to, but the worst that could happen is duplicate allocations, all but one of which will subsequently garbage collected, with no harm done.  And that's highly unlikely anyway :3  )
	protected static short[] GlobalDirtySkipBufferForShorts;
	public static long skipByDiscarding(ShortBlockReadStream readstream, long amount) throws ClosedStreamException, IOException
	{
		short[] discardBuffer;
		{
			discardBuffer = GlobalDirtySkipBufferForShorts;
			
			if (discardBuffer == null)
			{
				discardBuffer = new short[DefaultSkipBufferSize];
				GlobalDirtySkipBufferForShorts = discardBuffer;
			}
		}
		
		
		return skipByDiscarding(readstream, amount, GlobalDirtySkipBufferForShorts);
	}
	
	
	
	public static long skipByDiscarding(ShortBlockReadStream readstream, long amount, short[] discardBuffer) throws ClosedStreamException, IOException
	{
		if (amount < 0)
			throw new IllegalArgumentException("skip amount cannot be < 0");
		
		if (readstream.isClosed())
			throw new ClosedStreamException();
		
		if (amount == 0 || readstream.isEOF())
			return 0;
		
		
		int discardBufferSize = discardBuffer.length;
		
		long readSoFar = 0;
		while (true)
		{
			long remaining = amount - readSoFar;
			
			if (remaining <= 0 || readstream.isEOF())
				break;
			
			int remaining32 = remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
			int requested = remaining32 > discardBufferSize ? discardBufferSize : remaining32;
			
			int amountReadThisTime = readstream.read(discardBuffer, 0, requested);
			readSoFar += amountReadThisTime;
			
			if (amountReadThisTime < requested)  //eof!
				break;
		}
		
		assert readSoFar <= amount;
		
		return readSoFar;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static interface IndolentFloatBlockReadStream
	extends FloatBlockReadStream, IndolentBlockReadStream<Float, float[]>
	{
		public int readIndolent(float[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return StreamImplUtilities.forceRead(this, buffer, offset, length);
		}
	}
	
	public static interface IndolentFloatBlockWriteStream
	extends FloatBlockWriteStream, IndolentBlockWriteStream<Float, float[]>
	{
		public int writeIndolent(float[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default int write(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return StreamImplUtilities.forceWrite(this, buffer, offset, length);
		}
	}
	
	
	
	
	
	public static int forceRead(IndolentFloatBlockReadStream indolentStream, float[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		checkBlockMethodParameters(buffer, buffer.length, offset, length);
		
		int original = length;
		
		while (true)
		{
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			
			int amt = indolentStream.readIndolent(buffer, offset, length);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for read(float[], int, int)");
				throw new ImpossibleException();
			
			if (amt > length)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for read(float[], int, int) than requested.");
				throw new ImpossibleException();
			
			
			offset += amt;
			length -= amt;
			
			if (length == 0)
				return original;
			
			if (indolentStream.isEOF())
				return original - length;
		}
	}
	
	
	
	public static int forceWrite(IndolentFloatBlockWriteStream indolentStream, float[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		checkBlockMethodParameters(buffer, buffer.length, offset, length);
		
		int original = length;
		
		while (true)
		{
			if (length == 0)
				return original;
			
			if (indolentStream.isEOF())
				return original - length;
			
			
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			
			int amt = indolentStream.writeIndolent(buffer, offset, length);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for write(float[], int, int)");
				throw new ImpossibleException();
			
			if (amt > length)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for write(float[], int, int) than requested.");
				throw new ImpossibleException();
			
			
			offset += amt;
			length -= amt;
		}
	}
	
	
	
	
	
	
	//This might be concurrently accessed, but it is raw memory (a JVM array) that is only written to, NEVER read from, so it's fine to not synchronize!  :D
	// (the variable is read from and written to, but the worst that could happen is duplicate allocations, all but one of which will subsequently garbage collected, with no harm done.  And that's highly unlikely anyway :3  )
	protected static float[] GlobalDirtySkipBufferForFloats;
	public static long skipByDiscarding(FloatBlockReadStream readstream, long amount) throws ClosedStreamException, IOException
	{
		float[] discardBuffer;
		{
			discardBuffer = GlobalDirtySkipBufferForFloats;
			
			if (discardBuffer == null)
			{
				discardBuffer = new float[DefaultSkipBufferSize];
				GlobalDirtySkipBufferForFloats = discardBuffer;
			}
		}
		
		
		return skipByDiscarding(readstream, amount, GlobalDirtySkipBufferForFloats);
	}
	
	
	
	public static long skipByDiscarding(FloatBlockReadStream readstream, long amount, float[] discardBuffer) throws ClosedStreamException, IOException
	{
		if (amount < 0)
			throw new IllegalArgumentException("skip amount cannot be < 0");
		
		if (readstream.isClosed())
			throw new ClosedStreamException();
		
		if (amount == 0 || readstream.isEOF())
			return 0;
		
		
		int discardBufferSize = discardBuffer.length;
		
		long readSoFar = 0;
		while (true)
		{
			long remaining = amount - readSoFar;
			
			if (remaining <= 0 || readstream.isEOF())
				break;
			
			int remaining32 = remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
			int requested = remaining32 > discardBufferSize ? discardBufferSize : remaining32;
			
			int amountReadThisTime = readstream.read(discardBuffer, 0, requested);
			readSoFar += amountReadThisTime;
			
			if (amountReadThisTime < requested)  //eof!
				break;
		}
		
		assert readSoFar <= amount;
		
		return readSoFar;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static interface IndolentIntBlockReadStream
	extends IntBlockReadStream, IndolentBlockReadStream<Integer, int[]>
	{
		public int readIndolent(int[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default int read(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return StreamImplUtilities.forceRead(this, buffer, offset, length);
		}
	}
	
	public static interface IndolentIntBlockWriteStream
	extends IntBlockWriteStream, IndolentBlockWriteStream<Integer, int[]>
	{
		public int writeIndolent(int[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default int write(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return StreamImplUtilities.forceWrite(this, buffer, offset, length);
		}
	}
	
	
	
	
	
	public static int forceRead(IndolentIntBlockReadStream indolentStream, int[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		checkBlockMethodParameters(buffer, buffer.length, offset, length);
		
		int original = length;
		
		while (true)
		{
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			
			int amt = indolentStream.readIndolent(buffer, offset, length);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for read(int[], int, int)");
				throw new ImpossibleException();
			
			if (amt > length)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for read(int[], int, int) than requested.");
				throw new ImpossibleException();
			
			
			offset += amt;
			length -= amt;
			
			if (length == 0)
				return original;
			
			if (indolentStream.isEOF())
				return original - length;
		}
	}
	
	
	
	public static int forceWrite(IndolentIntBlockWriteStream indolentStream, int[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		checkBlockMethodParameters(buffer, buffer.length, offset, length);
		
		int original = length;
		
		while (true)
		{
			if (length == 0)
				return original;
			
			if (indolentStream.isEOF())
				return original - length;
			
			
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			
			int amt = indolentStream.writeIndolent(buffer, offset, length);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for write(int[], int, int)");
				throw new ImpossibleException();
			
			if (amt > length)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for write(int[], int, int) than requested.");
				throw new ImpossibleException();
			
			
			offset += amt;
			length -= amt;
		}
	}
	
	
	
	
	
	
	//This might be concurrently accessed, but it is raw memory (a JVM array) that is only written to, NEVER read from, so it's fine to not synchronize!  :D
	// (the variable is read from and written to, but the worst that could happen is duplicate allocations, all but one of which will subsequently garbage collected, with no harm done.  And that's highly unlikely anyway :3  )
	protected static int[] GlobalDirtySkipBufferForInts;
	public static long skipByDiscarding(IntBlockReadStream readstream, long amount) throws ClosedStreamException, IOException
	{
		int[] discardBuffer;
		{
			discardBuffer = GlobalDirtySkipBufferForInts;
			
			if (discardBuffer == null)
			{
				discardBuffer = new int[DefaultSkipBufferSize];
				GlobalDirtySkipBufferForInts = discardBuffer;
			}
		}
		
		
		return skipByDiscarding(readstream, amount, GlobalDirtySkipBufferForInts);
	}
	
	
	
	public static long skipByDiscarding(IntBlockReadStream readstream, long amount, int[] discardBuffer) throws ClosedStreamException, IOException
	{
		if (amount < 0)
			throw new IllegalArgumentException("skip amount cannot be < 0");
		
		if (readstream.isClosed())
			throw new ClosedStreamException();
		
		if (amount == 0 || readstream.isEOF())
			return 0;
		
		
		int discardBufferSize = discardBuffer.length;
		
		long readSoFar = 0;
		while (true)
		{
			long remaining = amount - readSoFar;
			
			if (remaining <= 0 || readstream.isEOF())
				break;
			
			int remaining32 = remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
			int requested = remaining32 > discardBufferSize ? discardBufferSize : remaining32;
			
			int amountReadThisTime = readstream.read(discardBuffer, 0, requested);
			readSoFar += amountReadThisTime;
			
			if (amountReadThisTime < requested)  //eof!
				break;
		}
		
		assert readSoFar <= amount;
		
		return readSoFar;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static interface IndolentDoubleBlockReadStream
	extends DoubleBlockReadStream, IndolentBlockReadStream<Double, double[]>
	{
		public int readIndolent(double[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return StreamImplUtilities.forceRead(this, buffer, offset, length);
		}
	}
	
	public static interface IndolentDoubleBlockWriteStream
	extends DoubleBlockWriteStream, IndolentBlockWriteStream<Double, double[]>
	{
		public int writeIndolent(double[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default int write(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return StreamImplUtilities.forceWrite(this, buffer, offset, length);
		}
	}
	
	
	
	
	
	public static int forceRead(IndolentDoubleBlockReadStream indolentStream, double[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		checkBlockMethodParameters(buffer, buffer.length, offset, length);
		
		int original = length;
		
		while (true)
		{
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			
			int amt = indolentStream.readIndolent(buffer, offset, length);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for read(double[], int, int)");
				throw new ImpossibleException();
			
			if (amt > length)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for read(double[], int, int) than requested.");
				throw new ImpossibleException();
			
			
			offset += amt;
			length -= amt;
			
			if (length == 0)
				return original;
			
			if (indolentStream.isEOF())
				return original - length;
		}
	}
	
	
	
	public static int forceWrite(IndolentDoubleBlockWriteStream indolentStream, double[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		checkBlockMethodParameters(buffer, buffer.length, offset, length);
		
		int original = length;
		
		while (true)
		{
			if (length == 0)
				return original;
			
			if (indolentStream.isEOF())
				return original - length;
			
			
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			
			int amt = indolentStream.writeIndolent(buffer, offset, length);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for write(double[], int, int)");
				throw new ImpossibleException();
			
			if (amt > length)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for write(double[], int, int) than requested.");
				throw new ImpossibleException();
			
			
			offset += amt;
			length -= amt;
		}
	}
	
	
	
	
	
	
	//This might be concurrently accessed, but it is raw memory (a JVM array) that is only written to, NEVER read from, so it's fine to not synchronize!  :D
	// (the variable is read from and written to, but the worst that could happen is duplicate allocations, all but one of which will subsequently garbage collected, with no harm done.  And that's highly unlikely anyway :3  )
	protected static double[] GlobalDirtySkipBufferForDoubles;
	public static long skipByDiscarding(DoubleBlockReadStream readstream, long amount) throws ClosedStreamException, IOException
	{
		double[] discardBuffer;
		{
			discardBuffer = GlobalDirtySkipBufferForDoubles;
			
			if (discardBuffer == null)
			{
				discardBuffer = new double[DefaultSkipBufferSize];
				GlobalDirtySkipBufferForDoubles = discardBuffer;
			}
		}
		
		
		return skipByDiscarding(readstream, amount, GlobalDirtySkipBufferForDoubles);
	}
	
	
	
	public static long skipByDiscarding(DoubleBlockReadStream readstream, long amount, double[] discardBuffer) throws ClosedStreamException, IOException
	{
		if (amount < 0)
			throw new IllegalArgumentException("skip amount cannot be < 0");
		
		if (readstream.isClosed())
			throw new ClosedStreamException();
		
		if (amount == 0 || readstream.isEOF())
			return 0;
		
		
		int discardBufferSize = discardBuffer.length;
		
		long readSoFar = 0;
		while (true)
		{
			long remaining = amount - readSoFar;
			
			if (remaining <= 0 || readstream.isEOF())
				break;
			
			int remaining32 = remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
			int requested = remaining32 > discardBufferSize ? discardBufferSize : remaining32;
			
			int amountReadThisTime = readstream.read(discardBuffer, 0, requested);
			readSoFar += amountReadThisTime;
			
			if (amountReadThisTime < requested)  //eof!
				break;
		}
		
		assert readSoFar <= amount;
		
		return readSoFar;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static interface IndolentLongBlockReadStream
	extends LongBlockReadStream, IndolentBlockReadStream<Long, long[]>
	{
		public int readIndolent(long[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return StreamImplUtilities.forceRead(this, buffer, offset, length);
		}
	}
	
	public static interface IndolentLongBlockWriteStream
	extends LongBlockWriteStream, IndolentBlockWriteStream<Long, long[]>
	{
		public int writeIndolent(long[] buffer, int offset, int requestedLength) throws IOException, ClosedStreamException;
		
		
		@Override
		public default int write(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return StreamImplUtilities.forceWrite(this, buffer, offset, length);
		}
	}
	
	
	
	
	
	public static int forceRead(IndolentLongBlockReadStream indolentStream, long[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		checkBlockMethodParameters(buffer, buffer.length, offset, length);
		
		int original = length;
		
		while (true)
		{
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			
			int amt = indolentStream.readIndolent(buffer, offset, length);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for read(long[], int, int)");
				throw new ImpossibleException();
			
			if (amt > length)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for read(long[], int, int) than requested.");
				throw new ImpossibleException();
			
			
			offset += amt;
			length -= amt;
			
			if (length == 0)
				return original;
			
			if (indolentStream.isEOF())
				return original - length;
		}
	}
	
	
	
	public static int forceWrite(IndolentLongBlockWriteStream indolentStream, long[] buffer, int offset, int length) throws ClosedStreamException, IOException
	{
		checkBlockMethodParameters(buffer, buffer.length, offset, length);
		
		int original = length;
		
		while (true)
		{
			if (length == 0)
				return original;
			
			if (indolentStream.isEOF())
				return original - length;
			
			
			//Make sure read() always gets called at least once, so that unforseen behaviors are perpetuated.
			
			int amt = indolentStream.writeIndolent(buffer, offset, length);
			
			
			if (amt < 0)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a negative length for write(long[], int, int)");
				throw new ImpossibleException();
			
			if (amt > length)
				//Todo throw new ImpossibleException("The Indolent implementation ("+indolentStream.getClass().getName()+") returned a greater length for write(long[], int, int) than requested.");
				throw new ImpossibleException();
			
			
			offset += amt;
			length -= amt;
		}
	}
	
	
	
	
	
	
	//This might be concurrently accessed, but it is raw memory (a JVM array) that is only written to, NEVER read from, so it's fine to not synchronize!  :D
	// (the variable is read from and written to, but the worst that could happen is duplicate allocations, all but one of which will subsequently garbage collected, with no harm done.  And that's highly unlikely anyway :3  )
	protected static long[] GlobalDirtySkipBufferForLongs;
	public static long skipByDiscarding(LongBlockReadStream readstream, long amount) throws ClosedStreamException, IOException
	{
		long[] discardBuffer;
		{
			discardBuffer = GlobalDirtySkipBufferForLongs;
			
			if (discardBuffer == null)
			{
				discardBuffer = new long[DefaultSkipBufferSize];
				GlobalDirtySkipBufferForLongs = discardBuffer;
			}
		}
		
		
		return skipByDiscarding(readstream, amount, GlobalDirtySkipBufferForLongs);
	}
	
	
	
	public static long skipByDiscarding(LongBlockReadStream readstream, long amount, long[] discardBuffer) throws ClosedStreamException, IOException
	{
		if (amount < 0)
			throw new IllegalArgumentException("skip amount cannot be < 0");
		
		if (readstream.isClosed())
			throw new ClosedStreamException();
		
		if (amount == 0 || readstream.isEOF())
			return 0;
		
		
		int discardBufferSize = discardBuffer.length;
		
		long readSoFar = 0;
		while (true)
		{
			long remaining = amount - readSoFar;
			
			if (remaining <= 0 || readstream.isEOF())
				break;
			
			int remaining32 = remaining > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)remaining;
			int requested = remaining32 > discardBufferSize ? discardBufferSize : remaining32;
			
			int amountReadThisTime = readstream.read(discardBuffer, 0, requested);
			readSoFar += amountReadThisTime;
			
			if (amountReadThisTime < requested)  //eof!
				break;
		}
		
		assert readSoFar <= amount;
		
		return readSoFar;
	}
	
	
	
	
	
	
	
	
	
	
	//>>>
	
	
	
	
	
	private StreamImplUtilities() {}
}
