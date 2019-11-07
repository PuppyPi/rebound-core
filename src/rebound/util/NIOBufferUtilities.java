/*
 * Created on Jul 19, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util;

import static rebound.util.BasicExceptionUtilities.*;
import static rebound.util.PlatformNIOBufferUtilities.*;
import static rebound.util.collections.prim.PrimitiveCollections.*;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.annotations.semantic.simpledata.DirectBuffer;
import rebound.annotations.semantic.temporal.PossiblySnapshotPossiblyLiveValue;
import rebound.exceptions.DirectBufferException;
import rebound.exceptions.NotYetImplementedException;
import rebound.exceptions.StructuredClassCastException;
import rebound.util.collections.ArrayUtilities;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.collections.prim.PrimitiveCollections.CharacterList;
import rebound.util.collections.prim.PrimitiveCollections.DoubleList;
import rebound.util.collections.prim.PrimitiveCollections.FloatList;
import rebound.util.collections.prim.PrimitiveCollections.IntegerList;
import rebound.util.collections.prim.PrimitiveCollections.LongList;
import rebound.util.collections.prim.PrimitiveCollections.ShortList;
import rebound.util.objectutil.JavaNamespace;
import rebound.util.objectutil.PubliclyCloneable;

//TODO Refactor-Rename to remove method overloading ^^'

public class NIOBufferUtilities
implements JavaNamespace
{
	public static void advance(Buffer b, int amount)
	{
		b.position(b.position() + amount);
	}
	
	
	public static @DirectBuffer ByteBuffer allocateDirectNativeEndianness(int capacityInBytes)
	{
		ByteBuffer b = ByteBuffer.allocateDirect(capacityInBytes);
		b.order(ByteOrder.nativeOrder());
		return b;
	}
	
	
	
	// I noticed these only being useful in a programming paradigm where I don't necessarily guarantee that the buffer won't be garbage collected and its underlying pointer become invalid after the pointer has been extracted from the Buffer and given to native code X'D
	//  withMallocXyz() using @UnmanagedBuffer and handling the free()ing explicitly is a safer way, I think, for JVM's that might garbage-collect things you think are on the stack but have been elided away and nulled out after the last use! :3
	//	
	//	@ThrowAwayValue
	//	@ManagedBuffer
	//	public static ByteBuffer toNewDirect(@ReadonlyValue ByteList heap)
	//	{
	//		return toNewDirect(heap.toByteArraySlicePossiblyLive());
	//	}
	//	
	//	@ThrowAwayValue
	//	@ManagedBuffer
	//	public static ByteBuffer toNewDirect(@ReadonlyValue Slice<byte[]> heap)
	//	{
	//		return toNewDirect(heap.getUnderlying(), heap.getOffset(), heap.getLength());
	//	}
	//	
	//	@ThrowAwayValue
	//	@ManagedBuffer
	//	public static ByteBuffer toNewDirect(@ReadonlyValue byte[] heap)
	//	{
	//		return toNewDirect(heap, 0, heap.length);
	//	}
	//	
	//	@ThrowAwayValue
	//	@ManagedBuffer
	//	public static ByteBuffer toNewDirect(@ReadonlyValue byte[] heap, int offset, int length)
	//	{
	//		ByteBuffer buffer = ByteBuffer.allocateDirect(length);
	//		buffer.put(heap, offset, length);
	//		buffer.rewind();
	//		return buffer;
	//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int getBufferBitlength(Buffer buffer) throws IllegalArgumentException
	{
		if (buffer instanceof ByteBuffer) return 8;
		if (buffer instanceof ShortBuffer) return 16;
		if (buffer instanceof CharBuffer) return 16;
		if (buffer instanceof IntBuffer) return 32;
		if (buffer instanceof FloatBuffer) return 32;
		if (buffer instanceof LongBuffer) return 64;
		if (buffer instanceof DoubleBuffer) return 64;
		
		if (buffer == null)
			throw new NullPointerException();
		else
			throw new StructuredClassCastException(buffer.getClass());
	}
	
	public static <E extends Buffer> E cloneBuffer(E originalBuffer)
	{
		if (originalBuffer instanceof PubliclyCloneable)
		{
			return (E)((PubliclyCloneable)originalBuffer).clone();
		}
		else
		{
			E newBuffer = allocateCompatibleBuffer(originalBuffer);
			assert newBuffer.capacity() == originalBuffer.capacity();
			NIOBufferUtilities.copyBuffers(originalBuffer, 0, newBuffer, 0, originalBuffer.capacity());
			return newBuffer;
		}
	}
	
	
	
	
	
	
	public static interface ExtendedBuffer<BufferBulkCopyConjugateType extends Buffer, ArrayType>
	{
		/**
		 * Should increment this.position and sourceBuffer.position :>
		 */
		public void put(BufferBulkCopyConjugateType sourceBuffer);
		
		/**
		 * Should *not* increment positions :>
		 */
		public void put(int destIndex, BufferBulkCopyConjugateType sourceBuffer, int sourceIndex, int numberOfElementsToCopy);
		
		
		
		public Class getComponentBaseType();
		
		
		
		
		
		/**
		 * Should increment position by array.length :>
		 */
		public void get(ArrayType array);
		
		/**
		 * Should increment position by length :>
		 */
		public void get(ArrayType array, int arrayOffset, int length);
		
		/**
		 * Should *not* increment the position :>
		 */
		public void get(int index, ArrayType array);
		
		/**
		 * Should *not* increment the position :>
		 */
		public void get(int index, ArrayType array, int arrayOffset, int length);
		
		
		
		/**
		 * Should increment position by array.length :>
		 */
		public void put(ArrayType array);
		
		/**
		 * Should increment position by length :>
		 */
		public void put(ArrayType array, int arrayOffset, int length);
		
		/**
		 * Should *not* increment the position :>
		 */
		public void put(int index, ArrayType array);
		
		/**
		 * Should *not* increment the position :>
		 */
		public void put(int index, ArrayType array, int arrayOffset, int length);
	}
	
	
	public static void copyBuffers(Buffer sourceBuffer, Buffer destBuffer)
	{
		if (destBuffer.remaining() < sourceBuffer.remaining()) throw new IndexOutOfBoundsException("Dest buffer ("+destBuffer.remaining()+" remaining) cannot hold as much as source buffer is providing! ("+sourceBuffer.remaining()+" remaining)");
		
		if (destBuffer instanceof ExtendedBuffer)
			((ExtendedBuffer)destBuffer).put(sourceBuffer);
		//else if (destBuffer instanceof _$$Prim$$_Buffer)
		//	((_$$Prim$$_Buffer)destBuffer).put((_$$Prim$$_Buffer)sourceBuffer);
		else if (destBuffer instanceof ByteBuffer)
			((ByteBuffer)destBuffer).put((ByteBuffer)sourceBuffer);
		else if (destBuffer instanceof CharBuffer)
			((CharBuffer)destBuffer).put((CharBuffer)sourceBuffer);
		else if (destBuffer instanceof ShortBuffer)
			((ShortBuffer)destBuffer).put((ShortBuffer)sourceBuffer);
		else if (destBuffer instanceof FloatBuffer)
			((FloatBuffer)destBuffer).put((FloatBuffer)sourceBuffer);
		else if (destBuffer instanceof IntBuffer)
			((IntBuffer)destBuffer).put((IntBuffer)sourceBuffer);
		else if (destBuffer instanceof DoubleBuffer)
			((DoubleBuffer)destBuffer).put((DoubleBuffer)sourceBuffer);
		else if (destBuffer instanceof LongBuffer)
			((LongBuffer)destBuffer).put((LongBuffer)sourceBuffer);
		else
			throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(destBuffer);
	}
	
	public static void copyBuffers(Buffer sourceBuffer, int sourceOffset, Buffer destBuffer, int destOffset, int numberOfElementsToCopy)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("Source start ("+sourceOffset+") negative!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("Destination start ("+destOffset+") negative!");
		if (sourceOffset > sourceBuffer.capacity()) throw new IndexOutOfBoundsException("Source end ("+sourceOffset+") beyond source capacity ("+sourceBuffer.capacity()+")!");
		if (destOffset > destBuffer.capacity()) throw new IndexOutOfBoundsException("Destination end ("+destOffset+") beyond destination capacity ("+destBuffer.capacity()+")!");
		if (sourceOffset+numberOfElementsToCopy > sourceBuffer.capacity()) throw new IndexOutOfBoundsException("Source end ("+(sourceOffset+numberOfElementsToCopy)+") beyond source capacity ("+sourceBuffer.capacity()+")!");
		if (destOffset+numberOfElementsToCopy > destBuffer.capacity()) throw new IndexOutOfBoundsException("Destination end ("+(destOffset+numberOfElementsToCopy)+") beyond destination capacity ("+destBuffer.capacity()+")!");
		
		if (destBuffer instanceof ExtendedBuffer)
			((ExtendedBuffer)destBuffer).put(destOffset, sourceBuffer, sourceOffset, numberOfElementsToCopy);
		
		else if (destBuffer instanceof ByteBuffer || destBuffer instanceof CharBuffer || destBuffer instanceof ShortBuffer || destBuffer instanceof FloatBuffer || destBuffer instanceof IntBuffer || destBuffer instanceof DoubleBuffer || destBuffer instanceof LongBuffer)  //destBuffer instanceof _$$Prim$$_Buffer ||
		{
			int originalSourcePosition = sourceBuffer.position();
			int originalSourceLimit = sourceBuffer.limit();
			int originalDestPosition = destBuffer.position();
			int originalDestLimit = destBuffer.limit();
			
			setPositionAndLimit(sourceBuffer, sourceOffset, sourceBuffer.capacity());
			setPositionAndLimit(destBuffer, destOffset, destBuffer.capacity());
			
			//else if (destBuffer instanceof _$$Prim$$_Buffer)
			//	((_$$Prim$$_Buffer)destBuffer).put((_$$Prim$$_Buffer)sourceBuffer);
			if (destBuffer instanceof ByteBuffer)
				((ByteBuffer)destBuffer).put((ByteBuffer)sourceBuffer);
			else if (destBuffer instanceof CharBuffer)
				((CharBuffer)destBuffer).put((CharBuffer)sourceBuffer);
			else if (destBuffer instanceof ShortBuffer)
				((ShortBuffer)destBuffer).put((ShortBuffer)sourceBuffer);
			else if (destBuffer instanceof FloatBuffer)
				((FloatBuffer)destBuffer).put((FloatBuffer)sourceBuffer);
			else if (destBuffer instanceof IntBuffer)
				((IntBuffer)destBuffer).put((IntBuffer)sourceBuffer);
			else if (destBuffer instanceof DoubleBuffer)
				((DoubleBuffer)destBuffer).put((DoubleBuffer)sourceBuffer);
			else if (destBuffer instanceof LongBuffer)
				((LongBuffer)destBuffer).put((LongBuffer)sourceBuffer);
			else
				throw new StructuredClassCastException(destBuffer.getClass());
			
			setPositionAndLimit(sourceBuffer, originalSourcePosition, originalSourceLimit);
			setPositionAndLimit(destBuffer, originalDestPosition, originalDestLimit);
		}
		
		else
			throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(destBuffer);
	}
	
	public static void setPositionAndLimit(Buffer buffer, int position, int limit)
	{
		//Important we check before [partially] modifying anything!
		if (position < 0) throw new IndexOutOfBoundsException("Position ("+position+") is negative!");
		if (limit < 0) throw new IndexOutOfBoundsException("Limit ("+limit+") is negative!");
		if (position > limit) throw new IndexOutOfBoundsException("Position ("+position+") beyond limit ("+limit+")!");
		
		//The order is SO IMPORTANT! XD
		buffer.limit(limit);
		buffer.position(position);
	}
	
	
	
	/**
	 * Either returns the same buffer, or allocates a new one and returns that,
	 * as needed depending on capacity, and if the original one is <code>null</code> :>
	 * Note: it may allocate more than the minimumCapacity!
	 */
	public static ByteBuffer insureCapacity(ByteBuffer extant, int minimumCapacity)
	{
		ByteBuffer rv = null;
		
		if (extant.capacity() < minimumCapacity)
		{
			//Todo use a LogisticLinear growth pattern somehow ._.   (perhaps retrofit to allow for non-index based growers; ie, perform inverse function to find x = if(y)  :>  )
			int newCapacity = (int)(minimumCapacity * 1.2d) + 256;
			rv = PlatformNIOBufferUtilities.allocateByteBuffer(newCapacity, PlatformNIOBufferUtilities.getBufferAllocationType(extant));
			
			//Copy le state!
			rv.limit(extant.limit());
			rv.position(extant.position());
			rv.order(extant.order());
		}
		else
		{
			rv = extant;
		}
		
		return rv;
	}
	
	
	
	
	
	
	
	
	
	
	@LiveValue
	public static Slice getUnderlyingArrayOrThrowIfDirect(Buffer buffer) throws DirectBufferException
	{
		if (buffer == null)
			throw new NullPointerException();
		
		if (buffer.hasArray())
		{
			Object array = buffer.array();
			return new Slice(array, buffer.arrayOffset(), buffer.remaining());
		}
		else
		{
			throw new DirectBufferException();
		}
	}
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static Slice getUnderlyingArrayOrCopyIfDirect(Buffer buffer) throws DirectBufferException
	{
		if (buffer == null)
			throw new NullPointerException();
		
		if (buffer.hasArray())
		{
			Object array = buffer.array();
			return new Slice(array, buffer.arrayOffset(), buffer.remaining());
		}
		else
		{
			return new Slice(copyToNewArray(buffer), 0, buffer.remaining());
		}
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Either returns {@link Buffer#array()} if possible,
	 * or a {@link System#arraycopy(Object, int, Object, int, int) subset} of that array if its {@link Buffer#position()} and/or {@link Buffer#limit()} aren't 0 and array.length respectively,
	 * or creates an array and copies the buffer into it if it's a direct buffer (really, if {@link Buffer#hasArray()} returns false :3 )
	 * 
	 * Ie, gets the buffer as an array (which starts at its start and ends at its end, possibly requiring a copy) ^_^
	 */
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static Object getArray(Buffer buffer)
	{
		if (buffer == null)
			throw new NullPointerException();
		
		if (buffer.hasArray())
		{
			Object array = buffer.array();
			
			if (buffer.position() == 0 && buffer.limit() == buffer.capacity())
				return array; //live value!
			else
			{
				Object subarray = Array.newInstance(array.getClass().getComponentType(), buffer.remaining());
				System.arraycopy(array, buffer.position(), subarray, 0, buffer.remaining());
				return subarray; //snapshot value!
			}
		}
		else
		{
			return copyToNewArray(buffer); //snapshot value!
		}
	}
	
	
	
	/*
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static _$$prim$$_[] getArray(_$$Prim$$_Buffer buffer)
	{
		if (buffer == null)
			throw new NullPointerException();
		
		if (buffer.hasArray())
		{
			_$$prim$$_[] array = buffer.array();
			
			if (buffer.position() == 0 && buffer.limit() == buffer.capacity())
				return array; //live value!
			else
			{
				_$$prim$$_[] subarray = new _$$prim$$_[buffer.remaining()];
				System.arraycopy(array, buffer.position(), subarray, 0, subarray.length);
				return subarray; //snapshot value!
			}
		}
		else
		{
			return copyToNewArray(buffer); //snapshot value!
		}
	}
	
	 */
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static byte[] getArray(ByteBuffer buffer)
	{
		if (buffer == null)
			throw new NullPointerException();
		
		if (buffer.hasArray())
		{
			byte[] array = buffer.array();
			
			if (buffer.position() == 0 && buffer.limit() == buffer.capacity())
				return array; //live value!
			else
			{
				byte[] subarray = new byte[buffer.remaining()];
				System.arraycopy(array, buffer.position(), subarray, 0, subarray.length);
				return subarray; //snapshot value!
			}
		}
		else
		{
			return copyToNewArray(buffer); //snapshot value!
		}
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static char[] getArray(CharBuffer buffer)
	{
		if (buffer == null)
			throw new NullPointerException();
		
		if (buffer.hasArray())
		{
			char[] array = buffer.array();
			
			if (buffer.position() == 0 && buffer.limit() == buffer.capacity())
				return array; //live value!
			else
			{
				char[] subarray = new char[buffer.remaining()];
				System.arraycopy(array, buffer.position(), subarray, 0, subarray.length);
				return subarray; //snapshot value!
			}
		}
		else
		{
			return copyToNewArray(buffer); //snapshot value!
		}
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static short[] getArray(ShortBuffer buffer)
	{
		if (buffer == null)
			throw new NullPointerException();
		
		if (buffer.hasArray())
		{
			short[] array = buffer.array();
			
			if (buffer.position() == 0 && buffer.limit() == buffer.capacity())
				return array; //live value!
			else
			{
				short[] subarray = new short[buffer.remaining()];
				System.arraycopy(array, buffer.position(), subarray, 0, subarray.length);
				return subarray; //snapshot value!
			}
		}
		else
		{
			return copyToNewArray(buffer); //snapshot value!
		}
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static float[] getArray(FloatBuffer buffer)
	{
		if (buffer == null)
			throw new NullPointerException();
		
		if (buffer.hasArray())
		{
			float[] array = buffer.array();
			
			if (buffer.position() == 0 && buffer.limit() == buffer.capacity())
				return array; //live value!
			else
			{
				float[] subarray = new float[buffer.remaining()];
				System.arraycopy(array, buffer.position(), subarray, 0, subarray.length);
				return subarray; //snapshot value!
			}
		}
		else
		{
			return copyToNewArray(buffer); //snapshot value!
		}
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static int[] getArray(IntBuffer buffer)
	{
		if (buffer == null)
			throw new NullPointerException();
		
		if (buffer.hasArray())
		{
			int[] array = buffer.array();
			
			if (buffer.position() == 0 && buffer.limit() == buffer.capacity())
				return array; //live value!
			else
			{
				int[] subarray = new int[buffer.remaining()];
				System.arraycopy(array, buffer.position(), subarray, 0, subarray.length);
				return subarray; //snapshot value!
			}
		}
		else
		{
			return copyToNewArray(buffer); //snapshot value!
		}
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static double[] getArray(DoubleBuffer buffer)
	{
		if (buffer == null)
			throw new NullPointerException();
		
		if (buffer.hasArray())
		{
			double[] array = buffer.array();
			
			if (buffer.position() == 0 && buffer.limit() == buffer.capacity())
				return array; //live value!
			else
			{
				double[] subarray = new double[buffer.remaining()];
				System.arraycopy(array, buffer.position(), subarray, 0, subarray.length);
				return subarray; //snapshot value!
			}
		}
		else
		{
			return copyToNewArray(buffer); //snapshot value!
		}
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@ReadonlyValue
	public static long[] getArray(LongBuffer buffer)
	{
		if (buffer == null)
			throw new NullPointerException();
		
		if (buffer.hasArray())
		{
			long[] array = buffer.array();
			
			if (buffer.position() == 0 && buffer.limit() == buffer.capacity())
				return array; //live value!
			else
			{
				long[] subarray = new long[buffer.remaining()];
				System.arraycopy(array, buffer.position(), subarray, 0, subarray.length);
				return subarray; //snapshot value!
			}
		}
		else
		{
			return copyToNewArray(buffer); //snapshot value!
		}
	}
	
	
	
	
	
	
	
	
	@SnapshotValue
	@ThrowAwayValue
	public static Object copyToNewArray(Buffer buffer)
	{
		if (buffer instanceof ExtendedBuffer)
		{
			Object array = Array.newInstance(((ExtendedBuffer)buffer).getComponentBaseType(), buffer.remaining());
			((ExtendedBuffer)buffer).get(buffer.position(), array);
			return array;
		}
		
		//else if (buffer instanceof _$$Prim$$_Buffer)
		//	return copyToNewArray((_$$Prim$$_Buffer)buffer);
		else if (buffer instanceof ByteBuffer)
			return copyToNewArray((ByteBuffer)buffer);
		else if (buffer instanceof CharBuffer)
			return copyToNewArray((CharBuffer)buffer);
		else if (buffer instanceof ShortBuffer)
			return copyToNewArray((ShortBuffer)buffer);
		else if (buffer instanceof FloatBuffer)
			return copyToNewArray((FloatBuffer)buffer);
		else if (buffer instanceof IntBuffer)
			return copyToNewArray((IntBuffer)buffer);
		else if (buffer instanceof DoubleBuffer)
			return copyToNewArray((DoubleBuffer)buffer);
		else if (buffer instanceof LongBuffer)
			return copyToNewArray((LongBuffer)buffer);
		
		else
			throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(buffer);
	}
	
	
	
	/*
	@SnapshotValue
	@ThrowAwayValue
	public static _$$prim$$_[] copyToNewArray(_$$Prim$$_Buffer buffer)
	{
		_$$prim$$_[] array = new _$$prim$$_[buffer.remaining()];
		buffer.get(array);
		advance(buffer, -array.length);
		return array;
	}
	
	 */
	
	@SnapshotValue
	@ThrowAwayValue
	public static byte[] copyToNewArray(ByteBuffer buffer)
	{
		byte[] array = new byte[buffer.remaining()];
		buffer.get(array);
		advance(buffer, -array.length);
		return array;
	}
	
	@SnapshotValue
	@ThrowAwayValue
	public static char[] copyToNewArray(CharBuffer buffer)
	{
		char[] array = new char[buffer.remaining()];
		buffer.get(array);
		advance(buffer, -array.length);
		return array;
	}
	
	@SnapshotValue
	@ThrowAwayValue
	public static short[] copyToNewArray(ShortBuffer buffer)
	{
		short[] array = new short[buffer.remaining()];
		buffer.get(array);
		advance(buffer, -array.length);
		return array;
	}
	
	@SnapshotValue
	@ThrowAwayValue
	public static float[] copyToNewArray(FloatBuffer buffer)
	{
		float[] array = new float[buffer.remaining()];
		buffer.get(array);
		advance(buffer, -array.length);
		return array;
	}
	
	@SnapshotValue
	@ThrowAwayValue
	public static int[] copyToNewArray(IntBuffer buffer)
	{
		int[] array = new int[buffer.remaining()];
		buffer.get(array);
		advance(buffer, -array.length);
		return array;
	}
	
	@SnapshotValue
	@ThrowAwayValue
	public static double[] copyToNewArray(DoubleBuffer buffer)
	{
		double[] array = new double[buffer.remaining()];
		buffer.get(array);
		advance(buffer, -array.length);
		return array;
	}
	
	@SnapshotValue
	@ThrowAwayValue
	public static long[] copyToNewArray(LongBuffer buffer)
	{
		long[] array = new long[buffer.remaining()];
		buffer.get(array);
		advance(buffer, -array.length);
		return array;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	@SnapshotValue
	@ThrowAwayValue
	public static Object copyToNewArray(Buffer buffer, int absoluteOffset, int amount)
	{
		if (buffer instanceof ExtendedBuffer)
		{
			Object array = null;
			
			int originalPosition = buffer.position(); int originalLimit = buffer.limit();
			setPositionAndLimit(buffer, absoluteOffset, absoluteOffset+amount);
			
			try
			{
				array = Array.newInstance(((ExtendedBuffer)buffer).getComponentBaseType(), buffer.remaining());
				
				((ExtendedBuffer)buffer).get(buffer.position(), array);
			}
			finally
			{
				setPositionAndLimit(buffer, originalPosition, originalLimit);
			}
			
			return array;
		}
		
		//		else if (buffer instanceof _$$Prim$$_Buffer)
		//			return copyToNewArray((_$$Prim$$_Buffer)buffer, absoluteOffset, amount);
		else if (buffer instanceof ByteBuffer)
			return copyToNewArray((ByteBuffer)buffer, absoluteOffset, amount);
		else if (buffer instanceof CharBuffer)
			return copyToNewArray((CharBuffer)buffer, absoluteOffset, amount);
		else if (buffer instanceof ShortBuffer)
			return copyToNewArray((ShortBuffer)buffer, absoluteOffset, amount);
		else if (buffer instanceof FloatBuffer)
			return copyToNewArray((FloatBuffer)buffer, absoluteOffset, amount);
		else if (buffer instanceof IntBuffer)
			return copyToNewArray((IntBuffer)buffer, absoluteOffset, amount);
		else if (buffer instanceof DoubleBuffer)
			return copyToNewArray((DoubleBuffer)buffer, absoluteOffset, amount);
		else if (buffer instanceof LongBuffer)
			return copyToNewArray((LongBuffer)buffer, absoluteOffset, amount);
		
		else
			throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(buffer);
	}
	
	
	
	/*
	@SnapshotValue
	@ThrowAwayValue
	public static _$$prim$$_[] copyToNewArray(_$$Prim$$_Buffer buffer, int absoluteOffset, int amount)
	{
		_$$prim$$_[] array = null;
		
		int originalPosition = buffer.position(); int originalLimit = buffer.limit();
		setPositionAndLimit(buffer, absoluteOffset, absoluteOffset+amount);
		
		try
		{
			array = new _$$prim$$_[amount];
			
			buffer.get(array);
		}
		finally
		{
			setPositionAndLimit(buffer, originalPosition, originalLimit);
		}
		
		return array;
	}
	
	 */
	
	
	
	@SnapshotValue
	@ThrowAwayValue
	public static byte[] copyToNewArray(ByteBuffer buffer, int absoluteOffset, int amount)
	{
		byte[] array = null;
		
		int originalPosition = buffer.position(); int originalLimit = buffer.limit();
		setPositionAndLimit(buffer, absoluteOffset, absoluteOffset+amount);
		
		try
		{
			array = new byte[amount];
			
			buffer.get(array);
		}
		finally
		{
			setPositionAndLimit(buffer, originalPosition, originalLimit);
		}
		
		return array;
	}
	
	@SnapshotValue
	@ThrowAwayValue
	public static char[] copyToNewArray(CharBuffer buffer, int absoluteOffset, int amount)
	{
		char[] array = null;
		
		int originalPosition = buffer.position(); int originalLimit = buffer.limit();
		setPositionAndLimit(buffer, absoluteOffset, absoluteOffset+amount);
		
		try
		{
			array = new char[amount];
			
			buffer.get(array);
		}
		finally
		{
			setPositionAndLimit(buffer, originalPosition, originalLimit);
		}
		
		return array;
	}
	
	@SnapshotValue
	@ThrowAwayValue
	public static short[] copyToNewArray(ShortBuffer buffer, int absoluteOffset, int amount)
	{
		short[] array = null;
		
		int originalPosition = buffer.position(); int originalLimit = buffer.limit();
		setPositionAndLimit(buffer, absoluteOffset, absoluteOffset+amount);
		
		try
		{
			array = new short[amount];
			
			buffer.get(array);
		}
		finally
		{
			setPositionAndLimit(buffer, originalPosition, originalLimit);
		}
		
		return array;
	}
	
	@SnapshotValue
	@ThrowAwayValue
	public static float[] copyToNewArray(FloatBuffer buffer, int absoluteOffset, int amount)
	{
		float[] array = null;
		
		int originalPosition = buffer.position(); int originalLimit = buffer.limit();
		setPositionAndLimit(buffer, absoluteOffset, absoluteOffset+amount);
		
		try
		{
			array = new float[amount];
			
			buffer.get(array);
		}
		finally
		{
			setPositionAndLimit(buffer, originalPosition, originalLimit);
		}
		
		return array;
	}
	
	@SnapshotValue
	@ThrowAwayValue
	public static int[] copyToNewArray(IntBuffer buffer, int absoluteOffset, int amount)
	{
		int[] array = null;
		
		int originalPosition = buffer.position(); int originalLimit = buffer.limit();
		setPositionAndLimit(buffer, absoluteOffset, absoluteOffset+amount);
		
		try
		{
			array = new int[amount];
			
			buffer.get(array);
		}
		finally
		{
			setPositionAndLimit(buffer, originalPosition, originalLimit);
		}
		
		return array;
	}
	
	@SnapshotValue
	@ThrowAwayValue
	public static double[] copyToNewArray(DoubleBuffer buffer, int absoluteOffset, int amount)
	{
		double[] array = null;
		
		int originalPosition = buffer.position(); int originalLimit = buffer.limit();
		setPositionAndLimit(buffer, absoluteOffset, absoluteOffset+amount);
		
		try
		{
			array = new double[amount];
			
			buffer.get(array);
		}
		finally
		{
			setPositionAndLimit(buffer, originalPosition, originalLimit);
		}
		
		return array;
	}
	
	@SnapshotValue
	@ThrowAwayValue
	public static long[] copyToNewArray(LongBuffer buffer, int absoluteOffset, int amount)
	{
		long[] array = null;
		
		int originalPosition = buffer.position(); int originalLimit = buffer.limit();
		setPositionAndLimit(buffer, absoluteOffset, absoluteOffset+amount);
		
		try
		{
			array = new long[amount];
			
			buffer.get(array);
		}
		finally
		{
			setPositionAndLimit(buffer, originalPosition, originalLimit);
		}
		
		return array;
	}
	
	
	
	
	
	
	
	
	
	
	
	public static Object wrapInBuffer(Object array)
	{
		//if (array instanceof _$$prim$$_[])
		//	return wrapInBuffer((_$$prim$$_[])array);
		if (array instanceof byte[])
			return wrapInBuffer((byte[])array);
		else if (array instanceof char[])
			return wrapInBuffer((char[])array);
		else if (array instanceof short[])
			return wrapInBuffer((short[])array);
		else if (array instanceof float[])
			return wrapInBuffer((float[])array);
		else if (array instanceof int[])
			return wrapInBuffer((int[])array);
		else if (array instanceof double[])
			return wrapInBuffer((double[])array);
		else if (array instanceof long[])
			return wrapInBuffer((long[])array);
		
		else if (array == null)
			return null;
		else
			throw new StructuredClassCastException("(Currently) Unsupported array type", array.getClass());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static Object wrapInBuffer(Object array, int offset, int length)
	{
		/*
		else if (array instanceof _$$prim$$_[])
			return wrapInBuffer((_$$prim$$_[])array, offset, length);
		 */
		if (array instanceof byte[])
			return wrapInBuffer((byte[])array, offset, length);
		else if (array instanceof char[])
			return wrapInBuffer((char[])array, offset, length);
		else if (array instanceof short[])
			return wrapInBuffer((short[])array, offset, length);
		else if (array instanceof float[])
			return wrapInBuffer((float[])array, offset, length);
		else if (array instanceof int[])
			return wrapInBuffer((int[])array, offset, length);
		else if (array instanceof double[])
			return wrapInBuffer((double[])array, offset, length);
		else if (array instanceof long[])
			return wrapInBuffer((long[])array, offset, length);
		
		else if (array == null)
			return null;
		else
			throw new StructuredClassCastException("(Currently) Unsupported array type", array.getClass());
	}
	
	
	
	
	
	
	/* <<<
	primxp
	_$$primxpconf:noboolean$$_
	
	public static _$$Prim$$_Buffer wrapInBuffer(_$$prim$$_[] array)
	{
		return _$$Prim$$_Buffer.wrap(array);
	}
	
	public static _$$Prim$$_Buffer wrapInBufferBySlice_$$Prim$$_(Slice<_$$prim$$_[]> arraySlice)
	{
		return wrapInBuffer(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static _$$Prim$$_Buffer wrapInBuffer(_$$prim$$_[] array, int offset, int length)
	{
		return _$$Prim$$_Buffer.wrap(array, offset, length);
	}
	
	 */
	
	
	public static ByteBuffer wrapInBuffer(byte[] array)
	{
		return ByteBuffer.wrap(array);
	}
	
	public static ByteBuffer wrapInBufferBySliceByte(Slice<byte[]> arraySlice)
	{
		return wrapInBuffer(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static ByteBuffer wrapInBuffer(byte[] array, int offset, int length)
	{
		return ByteBuffer.wrap(array, offset, length);
	}
	
	
	
	public static CharBuffer wrapInBuffer(char[] array)
	{
		return CharBuffer.wrap(array);
	}
	
	public static CharBuffer wrapInBufferBySliceChar(Slice<char[]> arraySlice)
	{
		return wrapInBuffer(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static CharBuffer wrapInBuffer(char[] array, int offset, int length)
	{
		return CharBuffer.wrap(array, offset, length);
	}
	
	
	
	public static ShortBuffer wrapInBuffer(short[] array)
	{
		return ShortBuffer.wrap(array);
	}
	
	public static ShortBuffer wrapInBufferBySliceShort(Slice<short[]> arraySlice)
	{
		return wrapInBuffer(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static ShortBuffer wrapInBuffer(short[] array, int offset, int length)
	{
		return ShortBuffer.wrap(array, offset, length);
	}
	
	
	
	public static FloatBuffer wrapInBuffer(float[] array)
	{
		return FloatBuffer.wrap(array);
	}
	
	public static FloatBuffer wrapInBufferBySliceFloat(Slice<float[]> arraySlice)
	{
		return wrapInBuffer(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static FloatBuffer wrapInBuffer(float[] array, int offset, int length)
	{
		return FloatBuffer.wrap(array, offset, length);
	}
	
	
	
	public static IntBuffer wrapInBuffer(int[] array)
	{
		return IntBuffer.wrap(array);
	}
	
	public static IntBuffer wrapInBufferBySliceInt(Slice<int[]> arraySlice)
	{
		return wrapInBuffer(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static IntBuffer wrapInBuffer(int[] array, int offset, int length)
	{
		return IntBuffer.wrap(array, offset, length);
	}
	
	
	
	public static DoubleBuffer wrapInBuffer(double[] array)
	{
		return DoubleBuffer.wrap(array);
	}
	
	public static DoubleBuffer wrapInBufferBySliceDouble(Slice<double[]> arraySlice)
	{
		return wrapInBuffer(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static DoubleBuffer wrapInBuffer(double[] array, int offset, int length)
	{
		return DoubleBuffer.wrap(array, offset, length);
	}
	
	
	
	public static LongBuffer wrapInBuffer(long[] array)
	{
		return LongBuffer.wrap(array);
	}
	
	public static LongBuffer wrapInBufferBySliceLong(Slice<long[]> arraySlice)
	{
		return wrapInBuffer(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static LongBuffer wrapInBuffer(long[] array, int offset, int length)
	{
		return LongBuffer.wrap(array, offset, length);
	}
	
	// >>>
	
	
	
	
	
	
	public static void fillBytes(ByteBuffer buffer, int numberOfBytes, byte value)
	{
		//Shortcut!  (why bitfields; why are you so confusing? ._. )
		for (int i = 0; i < numberOfBytes / 8; i++)
			buffer.putLong(0);
		
		for (int i = 0; i < numberOfBytes % 8; i++)
			buffer.put((byte)0);
	}
	
	public static void fillBytes(ByteBuffer buffer, int numberOfBytes)
	{
		fillBytes(buffer, numberOfBytes, (byte)0x00);
	}
	
	public static void fillBytes(ByteBuffer buffer)
	{
		fillBytes(buffer, buffer.remaining());
	}
	
	
	public static void fillBytes(byte[] buffer, int offset, int numberOfBytes, byte value)
	{
		for (int i = 0; i < numberOfBytes; i++)
			buffer[offset+i] = value;
	}
	
	public static void fillBytes(byte[] buffer, int numberOfBytes, byte value)
	{
		fillBytes(buffer, 0, numberOfBytes, value);
	}
	
	public static void fillBytes(byte[] buffer, int numberOfBytes)
	{
		fillBytes(buffer, numberOfBytes, (byte)0x00);
	}
	
	public static void fillBytes(byte[] buffer)
	{
		fillBytes(buffer, buffer.length);
	}
	
	
	
	
	
	
	public static void boundsCheckBuffer(Buffer buffer, int absolutePosition, int numberOfElements)
	{
		if (absolutePosition < 0)
			throw new IndexOutOfBoundsException("Negative offset!");
		
		if (numberOfElements < 0)
			throw new IllegalArgumentException("Negative length!");
		
		if (absolutePosition > buffer.capacity())
			throw new IndexOutOfBoundsException("Starting position ("+absolutePosition+") is beyond capacity of buffer ("+buffer.capacity()+")!");
		
		if (absolutePosition+numberOfElements > buffer.capacity())
			throw new IndexOutOfBoundsException("Terminating position ("+(absolutePosition+numberOfElements)+") is beyond capacity of buffer ("+buffer.capacity()+")!");
	}
	
	
	
	
	
	
	
	
	public static interface SlicableBuffer
	{
		public Buffer slice();
	}
	
	public static <B extends Buffer> B slice(B buffer)
	{
		//		else if (buffer instanceof _$$Prim$$_Buffer)
		//			return (B)((_$$Prim$$_Buffer)buffer).slice();
		
		if (buffer instanceof ByteBuffer)
			return (B)((ByteBuffer)buffer).slice();
		else if (buffer instanceof CharBuffer)
			return (B)((CharBuffer)buffer).slice();
		else if (buffer instanceof ShortBuffer)
			return (B)((ShortBuffer)buffer).slice();
		else if (buffer instanceof FloatBuffer)
			return (B)((FloatBuffer)buffer).slice();
		else if (buffer instanceof IntBuffer)
			return (B)((IntBuffer)buffer).slice();
		else if (buffer instanceof DoubleBuffer)
			return (B)((DoubleBuffer)buffer).slice();
		else if (buffer instanceof LongBuffer)
			return (B)((LongBuffer)buffer).slice();
		
		else if (buffer instanceof SlicableBuffer)
			return (B)((SlicableBuffer)buffer).slice();
		
		else
			throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(buffer);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * {@link Buffer#capacity()} not {@link Buffer#remaining()} :3
	 */
	public static int arrayOrBufferLength(Object source)
	{
		if (source instanceof Buffer)
			return ((Buffer)source).capacity();
		else
			return Array.getLength(source);
	}
	
	
	
	
	
	/**
	 * Doesn't make use of {@link Buffer#position()} or {@link Buffer#limit()} ^^''', like eg, {@link ByteBuffer#get(int)}!
	 */
	public static void arrayOrBufferCopy(Object source, int sourceOffsetInElements, Object dest, int destOffsetInElements, int lengthInElements)
	{
		if (source instanceof Buffer)
		{
			if (dest instanceof Buffer)
			{
				//TODO
				throw new NotYetImplementedException();
			}
			else
			{
				if (dest.getClass().isArray())
					//TODO
					throw new NotYetImplementedException();
				else
					throw newClassCastExceptionOrNullPointerException(dest);
			}
		}
		else
		{
			if (dest instanceof Buffer)
			{
				if (source.getClass().isArray())
					//TODO
					throw new NotYetImplementedException();
				else
					throw newClassCastExceptionOrNullPointerException(source);
			}
			else
			{
				//Already exists XDD
				//this will throw its own class cast exceptions as necessary :333
				System.arraycopy(source, sourceOffsetInElements, dest, destOffsetInElements, lengthInElements);
			}
		}
	}
	
	
	
	
	
	/**
	 * NOTE: floating point equality works according to {@link Primitives#eqSane(double, double)} / {@link Primitives#eqSane(float, float)}, so that all NaN's are considered equal, as well as +0 and -0 X"D  :DDD!!
	 */
	public static boolean arraysOrBuffersEqual(Object a, int aOffsetInElements, Object b, int bOffsetInElements, int lengthInElements)
	{
		/*
		else if (a instanceof _$$prim$$_[] && b instanceof _$$prim$$_[])
				return ArrayUtilities.arrayEquals((_$$prim$$_[])a, aOffsetInElements, (_$$prim$$_[])b, bOffsetInElements, lengthInElements);
		 */
		if (a instanceof boolean[] && b instanceof boolean[])
			return ArrayUtilities.arrayEquals((boolean[])a, aOffsetInElements, (boolean[])b, bOffsetInElements, lengthInElements);
		else if (a instanceof byte[] && b instanceof byte[])
			return ArrayUtilities.arrayEquals((byte[])a, aOffsetInElements, (byte[])b, bOffsetInElements, lengthInElements);
		else if (a instanceof char[] && b instanceof char[])
			return ArrayUtilities.arrayEquals((char[])a, aOffsetInElements, (char[])b, bOffsetInElements, lengthInElements);
		else if (a instanceof short[] && b instanceof short[])
			return ArrayUtilities.arrayEquals((short[])a, aOffsetInElements, (short[])b, bOffsetInElements, lengthInElements);
		else if (a instanceof float[] && b instanceof float[])
			return ArrayUtilities.arrayEquals((float[])a, aOffsetInElements, (float[])b, bOffsetInElements, lengthInElements);
		else if (a instanceof int[] && b instanceof int[])
			return ArrayUtilities.arrayEquals((int[])a, aOffsetInElements, (int[])b, bOffsetInElements, lengthInElements);
		else if (a instanceof double[] && b instanceof double[])
			return ArrayUtilities.arrayEquals((double[])a, aOffsetInElements, (double[])b, bOffsetInElements, lengthInElements);
		else if (a instanceof long[] && b instanceof long[])
			return ArrayUtilities.arrayEquals((long[])a, aOffsetInElements, (long[])b, bOffsetInElements, lengthInElements);
		
		if (a instanceof Object[])
		{
			if (b instanceof Object[])
				Arrays.equals((Object[])a, (Object[])b);
			else
				throw newClassCastExceptionOrNullPointerException(b);
		}
		else if (b instanceof Object[])
		{
			throw newClassCastExceptionOrNullPointerException(a);
		}
		
		
		
		if (!(a.getClass().isArray() || a instanceof Buffer))
			throw newClassCastExceptionOrNullPointerException(a);
		if (!(b.getClass().isArray() || b instanceof Buffer))
			throw newClassCastExceptionOrNullPointerException(b);
		
		//TODO!
		throw new NotYetImplementedException();
	}
	
	
	
	public static boolean arraysOrBuffersEqual(Object a, Object b)
	{
		//Iirc, Arrays.class methods (or just Array.class'??) were inlined as intrinsics in the Sun/Oracle JVM!!
		/*
		else if (a instanceof _$$prim$$_[] && b instanceof _$$prim$$_[])
				return Arrays.equals((_$$prim$$_[])a, (_$$prim$$_[])b);
		 */
		if (a instanceof boolean[] && b instanceof boolean[])
			return Arrays.equals((boolean[])a, (boolean[])b);
		else if (a instanceof byte[] && b instanceof byte[])
			return Arrays.equals((byte[])a, (byte[])b);
		else if (a instanceof char[] && b instanceof char[])
			return Arrays.equals((char[])a, (char[])b);
		else if (a instanceof short[] && b instanceof short[])
			return Arrays.equals((short[])a, (short[])b);
		else if (a instanceof float[] && b instanceof float[])
			return Arrays.equals((float[])a, (float[])b);
		else if (a instanceof int[] && b instanceof int[])
			return Arrays.equals((int[])a, (int[])b);
		else if (a instanceof double[] && b instanceof double[])
			return Arrays.equals((double[])a, (double[])b);
		else if (a instanceof long[] && b instanceof long[])
			return Arrays.equals((long[])a, (long[])b);
		
		if (a instanceof Object[])
		{
			if (b instanceof Object[])
				Arrays.equals((Object[])a, (Object[])b);
			else
				throw newClassCastExceptionOrNullPointerException(b);
		}
		else if (b instanceof Object[])
		{
			throw newClassCastExceptionOrNullPointerException(a);
		}
		
		
		
		if (!(a.getClass().isArray() || a instanceof Buffer))
			throw newClassCastExceptionOrNullPointerException(a);
		if (!(b.getClass().isArray() || b instanceof Buffer))
			throw newClassCastExceptionOrNullPointerException(b);
		
		//TODO!
		throw new NotYetImplementedException();
	}
	
	
	
	
	
	//Todolp
	//	public static int arrayOrBufferHashCode(Object x, int offsetInElements, int lengthInElements)
	//	{
	//
	//	}
	//
	//	public static int arrayOrBufferHashCode(Object x)
	//	{
	//
	//	}
	
	
	
	
	
	
	
	
	
	public static Object getBoxing(Buffer buffer, int absoluteIndex)
	{
		//		/* <<<
		//		 * primxp
		//		 * else if (buffer instanceof _$$Prim$$_Buffer)
		//		 * 	return ((_$$Prim$$_Buffer)buffer).get(absoluteIndex);
		//		 */
		//		//>>>
		
		if (buffer instanceof ByteBuffer)
			return ((ByteBuffer)buffer).get(absoluteIndex);
		else if (buffer instanceof CharBuffer)
			return ((CharBuffer)buffer).get(absoluteIndex);
		else if (buffer instanceof ShortBuffer)
			return ((ShortBuffer)buffer).get(absoluteIndex);
		else if (buffer instanceof FloatBuffer)
			return ((FloatBuffer)buffer).get(absoluteIndex);
		else if (buffer instanceof IntBuffer)
			return ((IntBuffer)buffer).get(absoluteIndex);
		else if (buffer instanceof DoubleBuffer)
			return ((DoubleBuffer)buffer).get(absoluteIndex);
		else if (buffer instanceof LongBuffer)
			return ((LongBuffer)buffer).get(absoluteIndex);
		else
			throw newClassCastExceptionOrNullPointerException(buffer);
	}
	
	
	
	
	public static void putUnboxing(Buffer buffer, int absoluteIndex, Object newValue)
	{
		//		/* <<<
		//		 * primxp
		//		 * else if (buffer instanceof _$$Prim$$_Buffer)
		//		 * 	((_$$Prim$$_Buffer)buffer).put(absoluteIndex, (_$$prim$$_)(_$$Primitive$$_)newValue);
		//		 */
		//		//>>>
		
		if (buffer instanceof ByteBuffer)
			((ByteBuffer)buffer).put(absoluteIndex, (Byte)newValue);
		else if (buffer instanceof CharBuffer)
			((CharBuffer)buffer).put(absoluteIndex, (Character)newValue);
		else if (buffer instanceof ShortBuffer)
			((ShortBuffer)buffer).put(absoluteIndex, (Short)newValue);
		else if (buffer instanceof FloatBuffer)
			((FloatBuffer)buffer).put(absoluteIndex, (Float)newValue);
		else if (buffer instanceof IntBuffer)
			((IntBuffer)buffer).put(absoluteIndex, (Integer)newValue);
		else if (buffer instanceof DoubleBuffer)
			((DoubleBuffer)buffer).put(absoluteIndex, (Double)newValue);
		else if (buffer instanceof LongBuffer)
			((LongBuffer)buffer).put(absoluteIndex, (Long)newValue);
		else
			throw newClassCastExceptionOrNullPointerException(buffer);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	python
	
	
	p(primxp.primxp(prims=primxp.AllPrimsButBoolean, source="""
	
	
	
	public static void getWithoutMoving(@ReadonlyValue _$$Prim$$_Buffer buffer, int bufferOffset, @WritableValue _$$prim$$_[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.get(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue _$$Prim$$_Buffer buffer, int bufferOffset, @WritableValue _$$prim$$_[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.get(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	
	
	
	public static void getWithoutMoving(@ReadonlyValue _$$Prim$$_Buffer buffer, @WritableValue _$$prim$$_[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.get(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue _$$Prim$$_Buffer buffer, @WritableValue _$$prim$$_[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.get(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue _$$Prim$$_Buffer buffer, @WritableValue Slice<_$$prim$$_[]> arraySlice)
	{
		getWithoutMoving(buffer, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void getWithoutMoving_$$Prim$$_(@WritableValue _$$Prim$$_Buffer buffer, @ReadonlyValue _$$Primitive$$_List list)
	{
		getWithoutMoving(buffer, list.to_$$Prim$$_ArraySlicePossiblyLive());
	}
	
	public static void getMoving_$$Prim$$_(@ReadonlyValue _$$Prim$$_Buffer buffer, @WritableValue Slice<_$$prim$$_[]> arraySlice)
	{
		buffer.get(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void getMoving_$$Prim$$_(@WritableValue _$$Prim$$_Buffer buffer, @ReadonlyValue _$$Primitive$$_List list)
	{
		getMoving_$$Prim$$_(buffer, list.to_$$Prim$$_ArraySlicePossiblyLive());
	}
	
	
	
	public static _$$prim$$_[] getToNewArrayWithoutMoving(@ReadonlyValue _$$Prim$$_Buffer buffer, int length)
	{
		_$$prim$$_[] array = new _$$prim$$_[length];
		getWithoutMoving(buffer, array);
		return array;
	}
	
	public static _$$prim$$_[] getToNewArrayMoving(@ReadonlyValue _$$Prim$$_Buffer buffer, int length)
	{
		_$$prim$$_[] array = new _$$prim$$_[length];
		buffer.get(array);
		return array;
	}
	
	
	public static _$$Primitive$$_List getToNewListWithoutMoving_$$Prim$$_(@ReadonlyValue _$$Prim$$_Buffer buffer, int length)
	{
		return _$$prim$$_ArrayAsList(getToNewArrayWithoutMoving(buffer, length));
	}
	
	public static _$$Primitive$$_List getToNewListMoving_$$Prim$$_(@ReadonlyValue _$$Prim$$_Buffer buffer, int length)
	{
		return _$$prim$$_ArrayAsList(getToNewArrayMoving(buffer, length));
	}
	
	
	
	
	
	
	
	
	
	
	public static void putWithoutMoving(@WritableValue _$$Prim$$_Buffer buffer, int bufferOffset, @ReadonlyValue _$$prim$$_[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.put(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMoving(@WritableValue _$$Prim$$_Buffer buffer, int bufferOffset, @ReadonlyValue _$$prim$$_[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.put(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	
	
	public static void putWithoutMoving(@WritableValue _$$Prim$$_Buffer buffer, @ReadonlyValue _$$prim$$_[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.put(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMoving(@WritableValue _$$Prim$$_Buffer buffer, @ReadonlyValue _$$prim$$_[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.put(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMoving_$$Prim$$_(@WritableValue _$$Prim$$_Buffer buffer, @ReadonlyValue Slice<_$$prim$$_[]> arraySlice)
	{
		putWithoutMoving(buffer, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void putWithoutMoving_$$Prim$$_(@WritableValue _$$Prim$$_Buffer buffer, @ReadonlyValue _$$Primitive$$_List list)
	{
		putWithoutMoving_$$Prim$$_(buffer, list.to_$$Prim$$_ArraySlicePossiblyLive());
	}
	
	public static void putMoving_$$Prim$$_(@WritableValue _$$Prim$$_Buffer buffer, @ReadonlyValue Slice<_$$prim$$_[]> arraySlice)
	{
		buffer.put(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void putMoving_$$Prim$$_(@WritableValue _$$Prim$$_Buffer buffer, @ReadonlyValue _$$Primitive$$_List list)
	{
		putMoving_$$Prim$$_(buffer, list.to_$$Prim$$_ArraySlicePossiblyLive());
	}
	
	
	
	
	
	public static _$$Prim$$_Buffer sliceToNIOBuffer_$$Prim$$_(Slice<_$$prim$$_[]> slice)
	{
		return _$$Prim$$_Buffer.wrap(slice.getUnderlying(), slice.getOffset(), slice.getLength());
	}
	
	
	
	
	
	
	
	
	"""));
	 */
	
	
	
	
	public static void getWithoutMoving(@ReadonlyValue ByteBuffer buffer, int bufferOffset, @WritableValue byte[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.get(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue ByteBuffer buffer, int bufferOffset, @WritableValue byte[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.get(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	
	
	
	public static void getWithoutMoving(@ReadonlyValue ByteBuffer buffer, @WritableValue byte[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.get(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue ByteBuffer buffer, @WritableValue byte[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.get(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue ByteBuffer buffer, @WritableValue Slice<byte[]> arraySlice)
	{
		getWithoutMoving(buffer, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void getWithoutMovingByte(@WritableValue ByteBuffer buffer, @ReadonlyValue ByteList list)
	{
		getWithoutMoving(buffer, list.toByteArraySlicePossiblyLive());
	}
	
	public static void getMovingByte(@ReadonlyValue ByteBuffer buffer, @WritableValue Slice<byte[]> arraySlice)
	{
		buffer.get(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void getMovingByte(@WritableValue ByteBuffer buffer, @ReadonlyValue ByteList list)
	{
		getMovingByte(buffer, list.toByteArraySlicePossiblyLive());
	}
	
	
	
	public static byte[] getToNewArrayWithoutMoving(@ReadonlyValue ByteBuffer buffer, int length)
	{
		byte[] array = new byte[length];
		getWithoutMoving(buffer, array);
		return array;
	}
	
	public static byte[] getToNewArrayMoving(@ReadonlyValue ByteBuffer buffer, int length)
	{
		byte[] array = new byte[length];
		buffer.get(array);
		return array;
	}
	
	
	public static ByteList getToNewListWithoutMovingByte(@ReadonlyValue ByteBuffer buffer, int length)
	{
		return byteArrayAsList(getToNewArrayWithoutMoving(buffer, length));
	}
	
	public static ByteList getToNewListMovingByte(@ReadonlyValue ByteBuffer buffer, int length)
	{
		return byteArrayAsList(getToNewArrayMoving(buffer, length));
	}
	
	
	
	
	
	
	
	
	
	
	public static void putWithoutMoving(@WritableValue ByteBuffer buffer, int bufferOffset, @ReadonlyValue byte[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.put(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMoving(@WritableValue ByteBuffer buffer, int bufferOffset, @ReadonlyValue byte[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.put(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	
	
	public static void putWithoutMoving(@WritableValue ByteBuffer buffer, @ReadonlyValue byte[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.put(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMoving(@WritableValue ByteBuffer buffer, @ReadonlyValue byte[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.put(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMovingByte(@WritableValue ByteBuffer buffer, @ReadonlyValue Slice<byte[]> arraySlice)
	{
		putWithoutMoving(buffer, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void putWithoutMovingByte(@WritableValue ByteBuffer buffer, @ReadonlyValue ByteList list)
	{
		putWithoutMovingByte(buffer, list.toByteArraySlicePossiblyLive());
	}
	
	public static void putMovingByte(@WritableValue ByteBuffer buffer, @ReadonlyValue Slice<byte[]> arraySlice)
	{
		buffer.put(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void putMovingByte(@WritableValue ByteBuffer buffer, @ReadonlyValue ByteList list)
	{
		putMovingByte(buffer, list.toByteArraySlicePossiblyLive());
	}
	
	
	
	
	
	public static ByteBuffer sliceToNIOBufferByte(Slice<byte[]> slice)
	{
		return ByteBuffer.wrap(slice.getUnderlying(), slice.getOffset(), slice.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static void getWithoutMoving(@ReadonlyValue CharBuffer buffer, int bufferOffset, @WritableValue char[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.get(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue CharBuffer buffer, int bufferOffset, @WritableValue char[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.get(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	
	
	
	public static void getWithoutMoving(@ReadonlyValue CharBuffer buffer, @WritableValue char[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.get(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue CharBuffer buffer, @WritableValue char[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.get(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue CharBuffer buffer, @WritableValue Slice<char[]> arraySlice)
	{
		getWithoutMoving(buffer, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void getWithoutMovingChar(@WritableValue CharBuffer buffer, @ReadonlyValue CharacterList list)
	{
		getWithoutMoving(buffer, list.toCharArraySlicePossiblyLive());
	}
	
	public static void getMovingChar(@ReadonlyValue CharBuffer buffer, @WritableValue Slice<char[]> arraySlice)
	{
		buffer.get(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void getMovingChar(@WritableValue CharBuffer buffer, @ReadonlyValue CharacterList list)
	{
		getMovingChar(buffer, list.toCharArraySlicePossiblyLive());
	}
	
	
	
	public static char[] getToNewArrayWithoutMoving(@ReadonlyValue CharBuffer buffer, int length)
	{
		char[] array = new char[length];
		getWithoutMoving(buffer, array);
		return array;
	}
	
	public static char[] getToNewArrayMoving(@ReadonlyValue CharBuffer buffer, int length)
	{
		char[] array = new char[length];
		buffer.get(array);
		return array;
	}
	
	
	public static CharacterList getToNewListWithoutMovingChar(@ReadonlyValue CharBuffer buffer, int length)
	{
		return charArrayAsList(getToNewArrayWithoutMoving(buffer, length));
	}
	
	public static CharacterList getToNewListMovingChar(@ReadonlyValue CharBuffer buffer, int length)
	{
		return charArrayAsList(getToNewArrayMoving(buffer, length));
	}
	
	
	
	
	
	
	
	
	
	
	public static void putWithoutMoving(@WritableValue CharBuffer buffer, int bufferOffset, @ReadonlyValue char[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.put(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMoving(@WritableValue CharBuffer buffer, int bufferOffset, @ReadonlyValue char[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.put(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	
	
	public static void putWithoutMoving(@WritableValue CharBuffer buffer, @ReadonlyValue char[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.put(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMoving(@WritableValue CharBuffer buffer, @ReadonlyValue char[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.put(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMovingChar(@WritableValue CharBuffer buffer, @ReadonlyValue Slice<char[]> arraySlice)
	{
		putWithoutMoving(buffer, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void putWithoutMovingChar(@WritableValue CharBuffer buffer, @ReadonlyValue CharacterList list)
	{
		putWithoutMovingChar(buffer, list.toCharArraySlicePossiblyLive());
	}
	
	public static void putMovingChar(@WritableValue CharBuffer buffer, @ReadonlyValue Slice<char[]> arraySlice)
	{
		buffer.put(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void putMovingChar(@WritableValue CharBuffer buffer, @ReadonlyValue CharacterList list)
	{
		putMovingChar(buffer, list.toCharArraySlicePossiblyLive());
	}
	
	
	
	
	
	public static CharBuffer sliceToNIOBufferChar(Slice<char[]> slice)
	{
		return CharBuffer.wrap(slice.getUnderlying(), slice.getOffset(), slice.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static void getWithoutMoving(@ReadonlyValue ShortBuffer buffer, int bufferOffset, @WritableValue short[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.get(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue ShortBuffer buffer, int bufferOffset, @WritableValue short[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.get(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	
	
	
	public static void getWithoutMoving(@ReadonlyValue ShortBuffer buffer, @WritableValue short[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.get(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue ShortBuffer buffer, @WritableValue short[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.get(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue ShortBuffer buffer, @WritableValue Slice<short[]> arraySlice)
	{
		getWithoutMoving(buffer, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void getWithoutMovingShort(@WritableValue ShortBuffer buffer, @ReadonlyValue ShortList list)
	{
		getWithoutMoving(buffer, list.toShortArraySlicePossiblyLive());
	}
	
	public static void getMovingShort(@ReadonlyValue ShortBuffer buffer, @WritableValue Slice<short[]> arraySlice)
	{
		buffer.get(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void getMovingShort(@WritableValue ShortBuffer buffer, @ReadonlyValue ShortList list)
	{
		getMovingShort(buffer, list.toShortArraySlicePossiblyLive());
	}
	
	
	
	public static short[] getToNewArrayWithoutMoving(@ReadonlyValue ShortBuffer buffer, int length)
	{
		short[] array = new short[length];
		getWithoutMoving(buffer, array);
		return array;
	}
	
	public static short[] getToNewArrayMoving(@ReadonlyValue ShortBuffer buffer, int length)
	{
		short[] array = new short[length];
		buffer.get(array);
		return array;
	}
	
	
	public static ShortList getToNewListWithoutMovingShort(@ReadonlyValue ShortBuffer buffer, int length)
	{
		return shortArrayAsList(getToNewArrayWithoutMoving(buffer, length));
	}
	
	public static ShortList getToNewListMovingShort(@ReadonlyValue ShortBuffer buffer, int length)
	{
		return shortArrayAsList(getToNewArrayMoving(buffer, length));
	}
	
	
	
	
	
	
	
	
	
	
	public static void putWithoutMoving(@WritableValue ShortBuffer buffer, int bufferOffset, @ReadonlyValue short[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.put(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMoving(@WritableValue ShortBuffer buffer, int bufferOffset, @ReadonlyValue short[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.put(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	
	
	public static void putWithoutMoving(@WritableValue ShortBuffer buffer, @ReadonlyValue short[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.put(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMoving(@WritableValue ShortBuffer buffer, @ReadonlyValue short[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.put(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMovingShort(@WritableValue ShortBuffer buffer, @ReadonlyValue Slice<short[]> arraySlice)
	{
		putWithoutMoving(buffer, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void putWithoutMovingShort(@WritableValue ShortBuffer buffer, @ReadonlyValue ShortList list)
	{
		putWithoutMovingShort(buffer, list.toShortArraySlicePossiblyLive());
	}
	
	public static void putMovingShort(@WritableValue ShortBuffer buffer, @ReadonlyValue Slice<short[]> arraySlice)
	{
		buffer.put(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void putMovingShort(@WritableValue ShortBuffer buffer, @ReadonlyValue ShortList list)
	{
		putMovingShort(buffer, list.toShortArraySlicePossiblyLive());
	}
	
	
	
	
	
	public static ShortBuffer sliceToNIOBufferShort(Slice<short[]> slice)
	{
		return ShortBuffer.wrap(slice.getUnderlying(), slice.getOffset(), slice.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static void getWithoutMoving(@ReadonlyValue FloatBuffer buffer, int bufferOffset, @WritableValue float[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.get(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue FloatBuffer buffer, int bufferOffset, @WritableValue float[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.get(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	
	
	
	public static void getWithoutMoving(@ReadonlyValue FloatBuffer buffer, @WritableValue float[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.get(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue FloatBuffer buffer, @WritableValue float[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.get(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue FloatBuffer buffer, @WritableValue Slice<float[]> arraySlice)
	{
		getWithoutMoving(buffer, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void getWithoutMovingFloat(@WritableValue FloatBuffer buffer, @ReadonlyValue FloatList list)
	{
		getWithoutMoving(buffer, list.toFloatArraySlicePossiblyLive());
	}
	
	public static void getMovingFloat(@ReadonlyValue FloatBuffer buffer, @WritableValue Slice<float[]> arraySlice)
	{
		buffer.get(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void getMovingFloat(@WritableValue FloatBuffer buffer, @ReadonlyValue FloatList list)
	{
		getMovingFloat(buffer, list.toFloatArraySlicePossiblyLive());
	}
	
	
	
	public static float[] getToNewArrayWithoutMoving(@ReadonlyValue FloatBuffer buffer, int length)
	{
		float[] array = new float[length];
		getWithoutMoving(buffer, array);
		return array;
	}
	
	public static float[] getToNewArrayMoving(@ReadonlyValue FloatBuffer buffer, int length)
	{
		float[] array = new float[length];
		buffer.get(array);
		return array;
	}
	
	
	public static FloatList getToNewListWithoutMovingFloat(@ReadonlyValue FloatBuffer buffer, int length)
	{
		return floatArrayAsList(getToNewArrayWithoutMoving(buffer, length));
	}
	
	public static FloatList getToNewListMovingFloat(@ReadonlyValue FloatBuffer buffer, int length)
	{
		return floatArrayAsList(getToNewArrayMoving(buffer, length));
	}
	
	
	
	
	
	
	
	
	
	
	public static void putWithoutMoving(@WritableValue FloatBuffer buffer, int bufferOffset, @ReadonlyValue float[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.put(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMoving(@WritableValue FloatBuffer buffer, int bufferOffset, @ReadonlyValue float[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.put(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	
	
	public static void putWithoutMoving(@WritableValue FloatBuffer buffer, @ReadonlyValue float[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.put(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMoving(@WritableValue FloatBuffer buffer, @ReadonlyValue float[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.put(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMovingFloat(@WritableValue FloatBuffer buffer, @ReadonlyValue Slice<float[]> arraySlice)
	{
		putWithoutMoving(buffer, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void putWithoutMovingFloat(@WritableValue FloatBuffer buffer, @ReadonlyValue FloatList list)
	{
		putWithoutMovingFloat(buffer, list.toFloatArraySlicePossiblyLive());
	}
	
	public static void putMovingFloat(@WritableValue FloatBuffer buffer, @ReadonlyValue Slice<float[]> arraySlice)
	{
		buffer.put(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void putMovingFloat(@WritableValue FloatBuffer buffer, @ReadonlyValue FloatList list)
	{
		putMovingFloat(buffer, list.toFloatArraySlicePossiblyLive());
	}
	
	
	
	
	
	public static FloatBuffer sliceToNIOBufferFloat(Slice<float[]> slice)
	{
		return FloatBuffer.wrap(slice.getUnderlying(), slice.getOffset(), slice.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static void getWithoutMoving(@ReadonlyValue IntBuffer buffer, int bufferOffset, @WritableValue int[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.get(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue IntBuffer buffer, int bufferOffset, @WritableValue int[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.get(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	
	
	
	public static void getWithoutMoving(@ReadonlyValue IntBuffer buffer, @WritableValue int[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.get(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue IntBuffer buffer, @WritableValue int[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.get(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue IntBuffer buffer, @WritableValue Slice<int[]> arraySlice)
	{
		getWithoutMoving(buffer, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void getWithoutMovingInt(@WritableValue IntBuffer buffer, @ReadonlyValue IntegerList list)
	{
		getWithoutMoving(buffer, list.toIntArraySlicePossiblyLive());
	}
	
	public static void getMovingInt(@ReadonlyValue IntBuffer buffer, @WritableValue Slice<int[]> arraySlice)
	{
		buffer.get(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void getMovingInt(@WritableValue IntBuffer buffer, @ReadonlyValue IntegerList list)
	{
		getMovingInt(buffer, list.toIntArraySlicePossiblyLive());
	}
	
	
	
	public static int[] getToNewArrayWithoutMoving(@ReadonlyValue IntBuffer buffer, int length)
	{
		int[] array = new int[length];
		getWithoutMoving(buffer, array);
		return array;
	}
	
	public static int[] getToNewArrayMoving(@ReadonlyValue IntBuffer buffer, int length)
	{
		int[] array = new int[length];
		buffer.get(array);
		return array;
	}
	
	
	public static IntegerList getToNewListWithoutMovingInt(@ReadonlyValue IntBuffer buffer, int length)
	{
		return intArrayAsList(getToNewArrayWithoutMoving(buffer, length));
	}
	
	public static IntegerList getToNewListMovingInt(@ReadonlyValue IntBuffer buffer, int length)
	{
		return intArrayAsList(getToNewArrayMoving(buffer, length));
	}
	
	
	
	
	
	
	
	
	
	
	public static void putWithoutMoving(@WritableValue IntBuffer buffer, int bufferOffset, @ReadonlyValue int[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.put(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMoving(@WritableValue IntBuffer buffer, int bufferOffset, @ReadonlyValue int[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.put(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	
	
	public static void putWithoutMoving(@WritableValue IntBuffer buffer, @ReadonlyValue int[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.put(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMoving(@WritableValue IntBuffer buffer, @ReadonlyValue int[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.put(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMovingInt(@WritableValue IntBuffer buffer, @ReadonlyValue Slice<int[]> arraySlice)
	{
		putWithoutMoving(buffer, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void putWithoutMovingInt(@WritableValue IntBuffer buffer, @ReadonlyValue IntegerList list)
	{
		putWithoutMovingInt(buffer, list.toIntArraySlicePossiblyLive());
	}
	
	public static void putMovingInt(@WritableValue IntBuffer buffer, @ReadonlyValue Slice<int[]> arraySlice)
	{
		buffer.put(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void putMovingInt(@WritableValue IntBuffer buffer, @ReadonlyValue IntegerList list)
	{
		putMovingInt(buffer, list.toIntArraySlicePossiblyLive());
	}
	
	
	
	
	
	public static IntBuffer sliceToNIOBufferInt(Slice<int[]> slice)
	{
		return IntBuffer.wrap(slice.getUnderlying(), slice.getOffset(), slice.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static void getWithoutMoving(@ReadonlyValue DoubleBuffer buffer, int bufferOffset, @WritableValue double[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.get(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue DoubleBuffer buffer, int bufferOffset, @WritableValue double[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.get(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	
	
	
	public static void getWithoutMoving(@ReadonlyValue DoubleBuffer buffer, @WritableValue double[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.get(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue DoubleBuffer buffer, @WritableValue double[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.get(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue DoubleBuffer buffer, @WritableValue Slice<double[]> arraySlice)
	{
		getWithoutMoving(buffer, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void getWithoutMovingDouble(@WritableValue DoubleBuffer buffer, @ReadonlyValue DoubleList list)
	{
		getWithoutMoving(buffer, list.toDoubleArraySlicePossiblyLive());
	}
	
	public static void getMovingDouble(@ReadonlyValue DoubleBuffer buffer, @WritableValue Slice<double[]> arraySlice)
	{
		buffer.get(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void getMovingDouble(@WritableValue DoubleBuffer buffer, @ReadonlyValue DoubleList list)
	{
		getMovingDouble(buffer, list.toDoubleArraySlicePossiblyLive());
	}
	
	
	
	public static double[] getToNewArrayWithoutMoving(@ReadonlyValue DoubleBuffer buffer, int length)
	{
		double[] array = new double[length];
		getWithoutMoving(buffer, array);
		return array;
	}
	
	public static double[] getToNewArrayMoving(@ReadonlyValue DoubleBuffer buffer, int length)
	{
		double[] array = new double[length];
		buffer.get(array);
		return array;
	}
	
	
	public static DoubleList getToNewListWithoutMovingDouble(@ReadonlyValue DoubleBuffer buffer, int length)
	{
		return doubleArrayAsList(getToNewArrayWithoutMoving(buffer, length));
	}
	
	public static DoubleList getToNewListMovingDouble(@ReadonlyValue DoubleBuffer buffer, int length)
	{
		return doubleArrayAsList(getToNewArrayMoving(buffer, length));
	}
	
	
	
	
	
	
	
	
	
	
	public static void putWithoutMoving(@WritableValue DoubleBuffer buffer, int bufferOffset, @ReadonlyValue double[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.put(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMoving(@WritableValue DoubleBuffer buffer, int bufferOffset, @ReadonlyValue double[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.put(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	
	
	public static void putWithoutMoving(@WritableValue DoubleBuffer buffer, @ReadonlyValue double[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.put(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMoving(@WritableValue DoubleBuffer buffer, @ReadonlyValue double[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.put(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMovingDouble(@WritableValue DoubleBuffer buffer, @ReadonlyValue Slice<double[]> arraySlice)
	{
		putWithoutMoving(buffer, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void putWithoutMovingDouble(@WritableValue DoubleBuffer buffer, @ReadonlyValue DoubleList list)
	{
		putWithoutMovingDouble(buffer, list.toDoubleArraySlicePossiblyLive());
	}
	
	public static void putMovingDouble(@WritableValue DoubleBuffer buffer, @ReadonlyValue Slice<double[]> arraySlice)
	{
		buffer.put(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void putMovingDouble(@WritableValue DoubleBuffer buffer, @ReadonlyValue DoubleList list)
	{
		putMovingDouble(buffer, list.toDoubleArraySlicePossiblyLive());
	}
	
	
	
	
	
	public static DoubleBuffer sliceToNIOBufferDouble(Slice<double[]> slice)
	{
		return DoubleBuffer.wrap(slice.getUnderlying(), slice.getOffset(), slice.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static void getWithoutMoving(@ReadonlyValue LongBuffer buffer, int bufferOffset, @WritableValue long[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.get(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue LongBuffer buffer, int bufferOffset, @WritableValue long[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.get(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	
	
	
	public static void getWithoutMoving(@ReadonlyValue LongBuffer buffer, @WritableValue long[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.get(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue LongBuffer buffer, @WritableValue long[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.get(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue LongBuffer buffer, @WritableValue Slice<long[]> arraySlice)
	{
		getWithoutMoving(buffer, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void getWithoutMovingLong(@WritableValue LongBuffer buffer, @ReadonlyValue LongList list)
	{
		getWithoutMoving(buffer, list.toLongArraySlicePossiblyLive());
	}
	
	public static void getMovingLong(@ReadonlyValue LongBuffer buffer, @WritableValue Slice<long[]> arraySlice)
	{
		buffer.get(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void getMovingLong(@WritableValue LongBuffer buffer, @ReadonlyValue LongList list)
	{
		getMovingLong(buffer, list.toLongArraySlicePossiblyLive());
	}
	
	
	
	public static long[] getToNewArrayWithoutMoving(@ReadonlyValue LongBuffer buffer, int length)
	{
		long[] array = new long[length];
		getWithoutMoving(buffer, array);
		return array;
	}
	
	public static long[] getToNewArrayMoving(@ReadonlyValue LongBuffer buffer, int length)
	{
		long[] array = new long[length];
		buffer.get(array);
		return array;
	}
	
	
	public static LongList getToNewListWithoutMovingLong(@ReadonlyValue LongBuffer buffer, int length)
	{
		return longArrayAsList(getToNewArrayWithoutMoving(buffer, length));
	}
	
	public static LongList getToNewListMovingLong(@ReadonlyValue LongBuffer buffer, int length)
	{
		return longArrayAsList(getToNewArrayMoving(buffer, length));
	}
	
	
	
	
	
	
	
	
	
	
	public static void putWithoutMoving(@WritableValue LongBuffer buffer, int bufferOffset, @ReadonlyValue long[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.put(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMoving(@WritableValue LongBuffer buffer, int bufferOffset, @ReadonlyValue long[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.position(bufferOffset);
			buffer.put(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	
	
	public static void putWithoutMoving(@WritableValue LongBuffer buffer, @ReadonlyValue long[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.put(array, arrayOffset, length);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMoving(@WritableValue LongBuffer buffer, @ReadonlyValue long[] array)
	{
		int originalPosition = buffer.position();
		
		try
		{
			buffer.put(array);
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMovingLong(@WritableValue LongBuffer buffer, @ReadonlyValue Slice<long[]> arraySlice)
	{
		putWithoutMoving(buffer, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void putWithoutMovingLong(@WritableValue LongBuffer buffer, @ReadonlyValue LongList list)
	{
		putWithoutMovingLong(buffer, list.toLongArraySlicePossiblyLive());
	}
	
	public static void putMovingLong(@WritableValue LongBuffer buffer, @ReadonlyValue Slice<long[]> arraySlice)
	{
		buffer.put(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void putMovingLong(@WritableValue LongBuffer buffer, @ReadonlyValue LongList list)
	{
		putMovingLong(buffer, list.toLongArraySlicePossiblyLive());
	}
	
	
	
	
	
	public static LongBuffer sliceToNIOBufferLong(Slice<long[]> slice)
	{
		return LongBuffer.wrap(slice.getUnderlying(), slice.getOffset(), slice.getLength());
	}
	
	
	
	
	
	
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void getWithoutMoving(@ReadonlyValue FloatBuffer buffer, @WritableValue double[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			for (int i = 0; i < length; i++)
			{
				array[arrayOffset+i] = buffer.get();
			}
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void getWithoutMoving(@ReadonlyValue FloatBuffer buffer, @WritableValue double[] array)
	{
		getWithoutMoving(buffer, array, 0, array.length);
	}
	
	public static double[] getToNewDoubleArrayWithoutMoving(@ReadonlyValue FloatBuffer buffer, int length)
	{
		double[] array = new double[length];
		getWithoutMoving(buffer, array);
		return array;
	}
	
	
	public static void putWithoutMovingDoubleIntoFloat(@WritableValue FloatBuffer buffer, @ReadonlyValue double[] array, int arrayOffset, int length)
	{
		int originalPosition = buffer.position();
		
		try
		{
			for (int i = 0; i < length; i++)
			{
				buffer.put((float)array[arrayOffset+i]);
			}
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	public static void putWithoutMovingDoubleIntoFloat(@WritableValue FloatBuffer buffer, @ReadonlyValue double[] array)
	{
		putWithoutMovingDoubleIntoFloat(buffer, array, 0, array.length);
	}
	
	public static void putWithoutMovingDoubleIntoFloat(@WritableValue FloatBuffer buffer, @ReadonlyValue Slice<double[]> array)
	{
		putWithoutMovingDoubleIntoFloat(buffer, array.getUnderlying(), array.getOffset(), array.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void withoutMovingBuffer(Buffer buffer, Runnable r)
	{
		int originalPosition = buffer.position();
		
		try
		{
			r.run();
		}
		finally
		{
			buffer.position(originalPosition);
		}
	}
	
	
	
	
	
	
	/**
	 * Does NOT alter position()!
	 */
	public static void setAllToZero(ByteBuffer b)
	{
		setAll(b, (byte)0);
	}
	
	/**
	 * Does NOT alter position()!
	 */
	public static void setAll(ByteBuffer b, byte value)
	{
		//This should really have been included in the api, then DirectByteBuffer could include a call to Unsafe.setMemory()  X'D
		
		//Todo detect directness and do the Unsafe.setMemory() thing ourselves ^^'
		
		withoutMovingBuffer(b, () ->
		{
			while (b.hasRemaining())
				b.put((byte)0);
		});
	}
	
	
	
	
	
	
	
	
	public static <B extends Buffer> B sliceNonmodifying(B buffer)
	{
		/* <<<
primxp
_$$primxpconf:noboolean$$_
		if (buffer instanceof _$$Prim$$_Buffer)
			return (B)((_$$Prim$$_Buffer)buffer).slice();
		 */
		
		if (buffer instanceof ByteBuffer)
			return (B)((ByteBuffer)buffer).slice();
		
		if (buffer instanceof CharBuffer)
			return (B)((CharBuffer)buffer).slice();
		
		if (buffer instanceof ShortBuffer)
			return (B)((ShortBuffer)buffer).slice();
		
		if (buffer instanceof FloatBuffer)
			return (B)((FloatBuffer)buffer).slice();
		
		if (buffer instanceof IntBuffer)
			return (B)((IntBuffer)buffer).slice();
		
		if (buffer instanceof DoubleBuffer)
			return (B)((DoubleBuffer)buffer).slice();
		
		if (buffer instanceof LongBuffer)
			return (B)((LongBuffer)buffer).slice();
		// >>>
		
		throw newClassCastExceptionOrNullPointerException(buffer);
	}
	
	
	
	
	
	//@NotThreadSafe
	public static <B extends Buffer> B sliceAbsoluteNonmodifying(B buffer, int offset, int length)
	{
		int p = buffer.position();
		int l = buffer.limit();
		
		Buffer u;
		
		if (offset != p || length != l - p)
		{
			if (offset + p + length > l)
				throw new IndexOutOfBoundsException();
			
			buffer.position(p+offset);
			buffer.limit(p+offset+length);
			u = sliceNonmodifying(buffer);
			buffer.position(p);
			buffer.limit(l);
		}
		else
		{
			u = sliceNonmodifying(buffer);
		}
		
		return (B)u;
	}
	
	/**
	 * Offset is relative to the buffer's current position
	 * And this call advances that position
	 */
	//@NotThreadSafe
	public static <B extends Buffer> B sliceRelativeAdvancing(B buffer, int offset, int length)
	{
		B u = sliceNonmodifying(buffer);
		u.position(offset);
		u.limit(offset+length);
		advance(buffer, length);
		return u;
	}
	
	
	
	
	//@NotThreadSafe
	public static <B extends Buffer> B sliceAbsoluteNonmodifying(Slice<B> bufferImmutablepositionslice)
	{
		return sliceAbsoluteNonmodifying(bufferImmutablepositionslice.getUnderlying(), bufferImmutablepositionslice.getOffset(), bufferImmutablepositionslice.getLength());
	}
	
	/**
	 * Offset is relative to the buffer's current position
	 * And this call advances that position
	 */
	//@NotThreadSafe
	public static <B extends Buffer> B sliceRelativeAdvancing(Slice<B> bufferImmutablepositionslice)
	{
		return sliceRelativeAdvancing(bufferImmutablepositionslice.getUnderlying(), bufferImmutablepositionslice.getOffset(), bufferImmutablepositionslice.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
primxp
_$$primxpconf:noboolean$$_
	public static _$$Prim$$_Buffer sliceNonmodifying(_$$Prim$$_Buffer buffer)
	{
		return buffer.slice();
	}
	 */
	
	public static ByteBuffer sliceNonmodifying(ByteBuffer buffer)
	{
		return buffer.slice();
	}
	
	public static CharBuffer sliceNonmodifying(CharBuffer buffer)
	{
		return buffer.slice();
	}
	
	public static ShortBuffer sliceNonmodifying(ShortBuffer buffer)
	{
		return buffer.slice();
	}
	
	public static FloatBuffer sliceNonmodifying(FloatBuffer buffer)
	{
		return buffer.slice();
	}
	
	public static IntBuffer sliceNonmodifying(IntBuffer buffer)
	{
		return buffer.slice();
	}
	
	public static DoubleBuffer sliceNonmodifying(DoubleBuffer buffer)
	{
		return buffer.slice();
	}
	
	public static LongBuffer sliceNonmodifying(LongBuffer buffer)
	{
		return buffer.slice();
	}
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void slicecopyBuffers(Slice source, Slice dest)
	{
		if (source.getLength() != dest.getLength())
			throw new IndexOutOfBoundsException();
		
		Object sourceUnderlying = source.getUnderlying();
		Object destUnderlying = dest.getUnderlying();
		
		if (sourceUnderlying instanceof Buffer)
		{
			if (destUnderlying instanceof Buffer)
			{
				
			}
			else
			{
				
			}
		}
		else
		{
			if (destUnderlying instanceof Buffer)
			{
				
			}
			else
			{
				ArrayUtilities.slicecopy(source, dest);
			}
		}
	}
}
