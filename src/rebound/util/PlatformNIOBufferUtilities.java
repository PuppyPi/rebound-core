package rebound.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import javax.annotation.Nonnull;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.NondirectBufferException;
import rebound.exceptions.NullEnumValueIllegalArgumentException;
import rebound.exceptions.UnexpectedHardcodedEnumValueException;
import rebound.exceptions.WrappedThrowableRuntimeException;
import rebound.util.AngryReflectionUtility.JavaVisibility;
import rebound.util.classhacking.jre.ClasshackingSunNIOUtilitiesOpportunisticHardlinked;

public class PlatformNIOBufferUtilities
{
	/**
	 * NOTE: this differs from the specification in this:
	 * The initial order of the returned buffer is always native byte-order; NOT generally big-endian!  (so many headaches XD)
	 */
	@Nonnull
	public static ByteBuffer allocateByteBuffer(int capacity, BufferAllocationType allocationType)
	{
		if (capacity < 0)
			throw new IllegalArgumentException("buffers can't have a negative capacity! XD *slaps knee melodramatically*");
		
		try
		{
			ByteBuffer rv = null;
			{
				if (allocationType == BufferAllocationType.JAVAHEAP)
					rv = ByteBuffer.allocate(capacity);
				else if (allocationType == BufferAllocationType.PREFERABLY_DIRECT)
					rv = ByteBuffer.allocateDirect(capacity);
				else if (allocationType == BufferAllocationType.NECESSARILY_DIRECT_POSSIBLYEXCESSSIZE_PAGEALIGNED_GARBAGECOLLECTED)
				{
					ByteBuffer buffer = ByteBuffer.allocateDirect(capacity);
					if (!buffer.isDirect())
						throw new NondirectBufferException();
					rv = buffer;
				}
				else if (allocationType == BufferAllocationType.NECESSARILY_DIRECT_EXACTSIZE_NOTPAGEALIGNED_NOTGARBAGECOLLECTED)
				{
					rv = PlatformNIOBufferUtilities.allocateDirectNonGarbageCollectedBufferViaOpportunisticSoftLinkCall(capacity);
				}
				
				else if (allocationType == BufferAllocationType.NECESSARILY_DIRECT_EXACTSIZE_NOTPAGEALIGNED_GARBAGECOLLECTED)
				{
					ByteBuffer exactUntrackedMallocAllocatedBuffer = allocateByteBuffer(capacity, BufferAllocationType.NECESSARILY_DIRECT_EXACTSIZE_NOTPAGEALIGNED_NOTGARBAGECOLLECTED);
					
					//We don't use ClasshackingSunNIOUtilitiesOpportunisticHardlinked.makeDirectMemoryDeallocatedOnJavaBufferGarbageCollection(exactMallocAllocatedBuffer); because we DON'T want it to decrement the reserved-memory-allocated count, because it was never incremented!  (this type of memory is untracked by that system!)
					PlatformNIOBufferUtilities.makeUntrackedBufferGarbageCollectedViaOpportunisticSoftLinkCall(exactUntrackedMallocAllocatedBuffer);
					
					rv = exactUntrackedMallocAllocatedBuffer;
				}
				
				else if (allocationType == null) throw new NullEnumValueIllegalArgumentException();
				else throw new UnexpectedHardcodedEnumValueException();
			}
			
			rv.order(ByteOrder.nativeOrder());
			
			return rv;
		}
		catch (OutOfMemoryError exc)
		{
			System.err.println("OutOfMemoryError occurred attempting to allocate "+capacity+" bytes, with type "+allocationType+"; max memory="+Runtime.getRuntime().maxMemory()+", total memory="+Runtime.getRuntime().totalMemory()+", free memory="+Runtime.getRuntime().freeMemory());
			//Note: Accessing amounts of currently allocated *direct* memory would require access-override, classhacked accessings of java.nio.Bits.totalCapacity, java.nio.Bits.reservedMemory :>
			throw exc;
		}
	}
	
	protected static ByteBuffer allocateDirectNonGarbageCollectedBufferViaOpportunisticSoftLinkCall(int capacity)
	{
		//Opportunistic soft linking!
		
		String targetClassName = "rebound.util.NonGarbageCollectedDirectByteBufferAllocator";
		String targetMethodName = "allocateDirectNonGarbageCollectedExactSizeNotPageAligned";
		
		Class targetClass = AngryReflectionUtility.forName(targetClassName);
		
		if (targetClass == null)
			throw new UnsupportedOperationException(new ClassNotFoundException(targetClassName));
		
		Method targetMethod = AngryReflectionUtility.getMethod(targetClass, targetMethodName, new Class[]{int.class}, JavaVisibility.PUBLIC, ByteBuffer.class, true, true);
		
		if (targetMethod == null)
			throw new UnsupportedOperationException(new NoSuchMethodError(targetClassName+"."+targetMethodName));
		
		
		Object rv = null;
		{
			try
			{
				rv = targetMethod.invoke(null, capacity);
			}
			catch (IllegalAccessException exc)
			{
				throw new ImpossibleException();
			}
			catch (IllegalArgumentException exc)
			{
				throw new ImpossibleException();
			}
			catch (InvocationTargetException exc)
			{
				throw new WrappedThrowableRuntimeException(exc.getCause());
			}
		}
		
		if (!(rv == null || rv instanceof ByteBuffer))
			throw new ImpossibleException();
		
		return (ByteBuffer)rv;
	}
	
	protected static void freeDirectNonGarbageCollectedBufferViaOpportunisticSoftLinkCall(ByteBuffer buffer)
	{
		//Opportunistic soft linking!
		
		String targetClassName = "rebound.util.NonGarbageCollectedDirectByteBufferAllocator";
		String targetMethodName = "freeNonGarbageCollectedBuffer";
		
		Class targetClass = AngryReflectionUtility.forName(targetClassName);
		
		if (targetClass == null)
			throw new UnsupportedOperationException(new ClassNotFoundException(targetClassName));
		
		Method targetMethod = AngryReflectionUtility.getMethod(targetClass, targetMethodName, new Class[]{ByteBuffer.class}, JavaVisibility.PUBLIC, void.class, true, true);
		
		if (targetMethod == null)
			throw new UnsupportedOperationException(new NoSuchMethodError(targetClassName+"."+targetMethodName));
		
		
		try
		{
			targetMethod.invoke(null, buffer);
		}
		catch (IllegalAccessException exc)
		{
			throw new ImpossibleException();
		}
		catch (IllegalArgumentException exc)
		{
			throw new ImpossibleException();
		}
		catch (InvocationTargetException exc)
		{
			throw ExceptionUtilities.rewrapToUnchecked(exc);
		}
	}
	
	protected static void makeUntrackedBufferGarbageCollectedViaOpportunisticSoftLinkCall(ByteBuffer buffer)
	{
		//Opportunistic soft linking!
		
		String targetClassName = "rebound.util.NonGarbageCollectedDirectByteBufferAllocator";
		String targetMethodName = "makeUntrackedBufferGarbageCollected";
		
		Class targetClass = AngryReflectionUtility.forName(targetClassName);
		
		if (targetClass == null)
			throw new UnsupportedOperationException(new ClassNotFoundException(targetClassName));
		
		Method targetMethod = AngryReflectionUtility.getMethod(targetClass, targetMethodName, new Class[]{ByteBuffer.class}, JavaVisibility.PUBLIC, null, true, true);
		
		if (targetMethod == null)
			throw new UnsupportedOperationException(new NoSuchMethodError(targetClassName+"."+targetMethodName));
		
		
		//Invoke!
		{
			try
			{
				targetMethod.invoke(null, buffer);
			}
			catch (IllegalAccessException exc)
			{
				throw new ImpossibleException();
			}
			catch (IllegalArgumentException exc)
			{
				throw new ImpossibleException();
			}
			catch (InvocationTargetException exc)
			{
				throw ExceptionUtilities.rewrapToUnchecked(exc);
			}
		}
	}
	
	public static void freeNonGarbageCollectedBuffer(ByteBuffer buffer)
	{
		if (!buffer.isDirect())
			throw new IllegalArgumentException("Cannot free() a Java heap buffer!!! X\"DD");
		
		freeDirectNonGarbageCollectedBufferViaOpportunisticSoftLinkCall(buffer);
	}
	
	public static boolean isGarbageCollected(ByteBuffer buffer)
	{
		return ClasshackingSunNIOUtilitiesOpportunisticHardlinked.isStandardlyGarbageCollected(buffer);  //Hey, so sue me; this should be in there anyways! XD
	}
	
	/**
	 * Returns one of:
	 * {@link BufferAllocationType#JAVAHEAP}
	 * {@link BufferAllocationType#NECESSARILY_DIRECT_POSSIBLYEXCESSSIZE_PAGEALIGNED_GARBAGECOLLECTED}
	 * {@link BufferAllocationType#NECESSARILY_DIRECT_EXACTSIZE_NOTPAGEALIGNED_NOTGARBAGECOLLECTED}
	 * ^_^
	 */
	public static BufferAllocationType getBufferAllocationType(Buffer buffer)
	{
		if (!buffer.isDirect())
			return BufferAllocationType.JAVAHEAP;
		else if (isGarbageCollected(ClasshackingSunNIOUtilitiesOpportunisticHardlinked.getUnderlyingByteBuffer(buffer)))
			return BufferAllocationType.NECESSARILY_DIRECT_POSSIBLYEXCESSSIZE_PAGEALIGNED_GARBAGECOLLECTED;
		else
			return BufferAllocationType.NECESSARILY_DIRECT_EXACTSIZE_NOTPAGEALIGNED_NOTGARBAGECOLLECTED;
	}
	
	
	
	
	
	
	/**
	 * @return <code>null</code> if there is none! ^_^
	 */
	public static ByteBuffer getUnderlyingByteBuffer(Buffer buffer)
	{
		return ClasshackingSunNIOUtilitiesOpportunisticHardlinked.getUnderlyingByteBuffer(buffer);
	}
	
	public static <E extends Buffer> E allocateCompatibleBuffer(E originalBuffer, BufferAllocationType allocationType)
	{
		int originalBitlength = NIOBufferUtilities.getBufferBitlength(originalBuffer);
		ByteBuffer newByteBuffer = allocateByteBuffer(originalBuffer.capacity() * originalBitlength / 8, allocationType);
		
		//Set byte order
		{
			ByteBuffer originalByteBuffer = getUnderlyingByteBuffer(originalBuffer);
			if (originalByteBuffer != null)
				newByteBuffer.order(originalByteBuffer.order());
		}
		
		E newBuffer = null;
		{
			if (originalBuffer instanceof ByteBuffer) newBuffer = (E)newByteBuffer;
			else if (originalBuffer instanceof ShortBuffer) newBuffer = (E)newByteBuffer.asShortBuffer();
			else if (originalBuffer instanceof CharBuffer) newBuffer = (E)newByteBuffer.asCharBuffer();
			else if (originalBuffer instanceof IntBuffer) newBuffer = (E)newByteBuffer.asIntBuffer();
			else if (originalBuffer instanceof FloatBuffer) newBuffer = (E)newByteBuffer.asFloatBuffer();
			else if (originalBuffer instanceof LongBuffer) newBuffer = (E)newByteBuffer.asLongBuffer();
			else if (originalBuffer instanceof DoubleBuffer) newBuffer = (E)newByteBuffer.asDoubleBuffer();
			else throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(originalBuffer);
		}
		
		newBuffer.limit(originalBuffer.limit());
		newBuffer.position(originalBuffer.position());
		
		return newBuffer;
	}
	
	public static <E extends Buffer> E allocateCompatibleBuffer(E originalBuffer)
	{
		return allocateCompatibleBuffer(originalBuffer, getBufferAllocationType(originalBuffer));
	}
}
