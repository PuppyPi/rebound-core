/*
 * Created on Jul 9, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.classhacking.jre;

import static rebound.util.classhacking.jre.ClasshackingSunNIOUtilitiesOpportunisticHardlinked.BufferType.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import rebound.exceptions.ImPrettySureThisNeverActuallyHappensRuntimeException;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.UnreachableCodeException;
import rebound.util.AngryReflectionUtility;
import rebound.util.AngryReflectionUtility.JavaVisibility;
import rebound.util.ExceptionUtilities;
import rebound.util.classhacking.ClasshackingUtilities;
import rebound.util.classhacking.HackedClassOrMemberUnavailableException;
import rebound.util.objectutil.JavaNamespace;
import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

/*
 * Some notes:
 * 		+ If you didn't know, direct (byte) buffers' capacities are rounded (above!) the memory page size (eg, 4096 bytes!)    (see java.nio.DirectByteBuffer(int))
 * 			+ This only applies to java.nio.ByteBuffer.allocateDirect(int); I dunno about the JNI call
 * 
 * 		+ Deallocation occurs on garbage collection for direct buffers allocated in Java (ie, java.nio.ByteBuffer.allocateDirect(int)),
 * 			But NOT for direct byte buffers created from JNI (ie, NewDirectByteBuffer)!
 * 			+ GC-driven deallocation is performed with java.lang.ref things (spfc. sun.misc.Cleaner's which subclass java.lang.ref.PhantomReferences), and java.nio.DirectByteBuffer.Deallocator
 * 				Excerpt from java.nio.DirectByteBuffer(int)
 * 					cleaner = Cleaner.create(this, new Deallocator(base, size, cap));
 * 				^_^
 * 			+ The java.nio.DirectByteBuffer.cleaner variable is final and can't be set, but
 * 			+ I *THINK* there is nothing stopping you from creating a random Cleaner (they are public! :D ) which has a Deallocator or equivalent as it's cleanup function ^_^
 * 
 * 		+ Note on sun.misc.Cleaner's: they are specifically integrated to the JVM; it's an actual extension to java.lang.ref, not just a utility wrapper around it!  (ie, you can't recreate their functionality without modifying&recompiling the JVM source code!)
 */



public class ClasshackingSunNIOUtilitiesOpportunisticHardlinked
implements JavaNamespace
{
	public static enum BufferType
	{
		BYTE_HEAP,
		BYTE_DIRECT,
		
		/**
		 * Named java.nio.ByteBufferAs{Short,Char,Int,Float,Long,Double}Buffer{B,L},
		 * these classes implement the non-byte to byte conversions in Java ("softly").
		 * So they are slower, but they work when native direct accesses aren't allowed
		 * (such as unaligned access on platforms that don't support it, which is the
		 * only time they're used in direct byte buffers :>
		 * see java.nio.DirectByteBuffer.as<i>Foo<i>Buffer() ^_^ )
		 */
		SOFT_HEAP,
		SOFT_DIRECT,
		
		/**
		 * Named Direct{Short,Char,Int,Float,Long,Double}BufferS,
		 * these classes are native-access, save for needing to swap the byte-endianness
		 * from/to the host's byte-endianness  (especially overhead-inducing for floats ><! )
		 */
		SWAPPED_DIRECT,
		
		/**
		 * Named Direct{Short,Char,Int,Float,Long,Double}BufferS,
		 * these classes
		 * ARE DA BOMB!
		 * XD
		 * Ie, exactly what C would do, using sun.misc.Unsafe >;)
		 */
		NATIVE_DIRECT,
	}
	
	
	/**
	 * @return <code>null</code> if not a supported (Sun JDK) primitive NIO buffer, and always ATOMIC_HEAP or SOFT_HEAP if a heap buffer :>
	 */
	public static BufferType getBufferType(Buffer buffer)
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			
			if (buffer == null)
				return null; // >,>
			else if (buffer instanceof ByteBuffer)
				return buffer.isDirect() ? BYTE_DIRECT : BYTE_HEAP;
			else
			{
				Class bufferClass = buffer.getClass();
				
				
				if (bufferClass == DirectByteBuffer) throw new ImpossibleException();
				if (bufferClass == DirectByteBufferR) throw new ImpossibleException();
				
				if (bufferClass == DirectCharBufferU) return NATIVE_DIRECT;
				if (bufferClass == DirectCharBufferRU) return NATIVE_DIRECT;
				if (bufferClass == DirectIntBufferU) return NATIVE_DIRECT;
				if (bufferClass == DirectIntBufferRU) return NATIVE_DIRECT;
				if (bufferClass == DirectFloatBufferU) return NATIVE_DIRECT;
				if (bufferClass == DirectFloatBufferRU) return NATIVE_DIRECT;
				if (bufferClass == DirectLongBufferU) return NATIVE_DIRECT;
				if (bufferClass == DirectLongBufferRU) return NATIVE_DIRECT;
				if (bufferClass == DirectDoubleBufferU) return NATIVE_DIRECT;
				if (bufferClass == DirectDoubleBufferRU) return NATIVE_DIRECT;
				
				if (bufferClass == DirectShortBufferS) return SWAPPED_DIRECT;
				if (bufferClass == DirectShortBufferRS) return SWAPPED_DIRECT;
				if (bufferClass == DirectCharBufferS) return SWAPPED_DIRECT;
				if (bufferClass == DirectCharBufferRS) return SWAPPED_DIRECT;
				if (bufferClass == DirectIntBufferS) return SWAPPED_DIRECT;
				if (bufferClass == DirectIntBufferRS) return SWAPPED_DIRECT;
				if (bufferClass == DirectFloatBufferS) return SWAPPED_DIRECT;
				if (bufferClass == DirectFloatBufferRS) return SWAPPED_DIRECT;
				if (bufferClass == DirectLongBufferS) return SWAPPED_DIRECT;
				if (bufferClass == DirectLongBufferRS) return SWAPPED_DIRECT;
				if (bufferClass == DirectDoubleBufferS) return SWAPPED_DIRECT;
				if (bufferClass == DirectDoubleBufferRS) return SWAPPED_DIRECT;
				
				if (bufferClass == ByteBufferAsShortBufferB) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsShortBufferRB) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsShortBufferL) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsShortBufferRL) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsCharBufferB) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsCharBufferRB) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsCharBufferL) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsCharBufferRL) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsIntBufferB) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsIntBufferRB) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsIntBufferL) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsIntBufferRL) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsFloatBufferB) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsFloatBufferRB) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsFloatBufferL) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsFloatBufferRL) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsLongBufferB) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsLongBufferRB) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsLongBufferL) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsLongBufferRL) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsDoubleBufferB) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsDoubleBufferRB) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsDoubleBufferL) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				if (bufferClass == ByteBufferAsDoubleBufferRL) return buffer.isDirect() ? SOFT_DIRECT : SOFT_HEAP;
				
				
				return null; // /shrugs; I dunno!
			}
		}
	}
	
	//isDirect is already a thing
	
	/*
	 * Every buffer implementation class (eg, DirectShortS, ByteBufferAsDoubleBufferB)
	 * has a corresponding read-only subclass/wrapper class that exposes it read-only.
	 * + Direct...R's have their read-write buffer as their attachment
	 * + Soft ByteBufferAs...R's don't have a general attachment, just a type-specific reference to their ByteBuffer
	 * + Slices and duplicates can have such attachments as well!
	 * 
	 * also, isReadOnly is already a thing x>
	 */
	
	
	/**
	 * @return <code>null</code> if there is none! ^_^
	 */
	public static ByteBuffer getUnderlyingByteBuffer(Buffer buffer)
	{
		if (buffer == null)
			return null; // >,>
		
		else if (buffer instanceof DirectBuffer)
		{
			Object attachment = ((DirectBuffer)buffer).attachment();
			if (attachment == null)
			{
				return buffer instanceof ByteBuffer ? (ByteBuffer)buffer : null;
			}
			else
			{
				return getUnderlyingByteBuffer((Buffer)attachment);
			}
		}
		
		else
		{
			//sloooooow ><   (but prolly more robust if implementations change!)
			Field bb = AngryReflectionUtility.getField(buffer.getClass(), "bb", null, ByteBuffer.class, false, true);
			
			if (bb == null)
			{
				return null;
			}
			else
			{
				try
				{
					if (AngryReflectionUtility.getVisibility(bb) != JavaVisibility.PUBLIC) //avoid causing SecurityExceptions if unnecessary
						bb.setAccessible(true);
					
					return (ByteBuffer)bb.get(buffer);
				}
				catch (IllegalArgumentException exc)
				{
					throw new ImpossibleException(exc); //that was part of our search criteria for the field! ;_;
				}
				catch (IllegalAccessException exc)
				{
					throw new ImpossibleException(exc); //we called .setAccessible(true), so a SecurityException might happen, but never this :)
				}
			}
		}
	}
	
	public static ByteBuffer getUnderlyingByteBufferEnforced(Buffer buffer) throws IllegalArgumentException
	{
		ByteBuffer underlyingBuffer = getUnderlyingByteBuffer(buffer);
		if (underlyingBuffer == null)
			throw new IllegalArgumentException("Can't find underlying ByteBuffer for: "+buffer);
		else
			return underlyingBuffer;
	}
	
	
	/**
	 * Note: this returns false negatives (but not false positives) for non-standard-ly garabge collected buffers (eg, with {@link #makeDirectMemoryDeallocatedOnJavaBufferGarbageCollection(ByteBuffer)})
	 * (cleaner field is final uwu )
	 */
	public static boolean isStandardlyGarbageCollected(Buffer buffer) throws IllegalArgumentException
	{
		if (!buffer.isDirect())
			return true;
		else
		{
			if (buffer instanceof DirectBuffer)
			{
				return ((DirectBuffer)buffer).cleaner() != null;
			}
			else
			{
				throw new ImPrettySureThisNeverActuallyHappensRuntimeException("A direct buffer that is not a sun.nio.ch.DirectBuffer! o_O!");
			}
		}
	}
	
	
	
	public static void makeDirectMemoryDeallocatedOnJavaBufferGarbageCollection(ByteBuffer buffer)
	{
		makeDirectMemoryDeallocatedOnJavaBufferGarbageCollection(buffer, getUnderlyingBufferCapacity(buffer));
	}
	
	public static void makeDirectMemoryDeallocatedOnJavaBufferGarbageCollection(ByteBuffer buffer, long actualSizeOfMemory)
	{
		makeDirectMemoryDeallocatedOnJavaObjectGarbageCollection(buffer, getUnderlyingBufferBaseAddress(buffer), actualSizeOfMemory, getUnderlyingBufferCapacity(buffer));
	}
	
	/**
	 * The difference between this and {@link #makeUntrackedCHeapMemoryDeallocatedOnJavaObjectGarbageCollection(Object, long)} is that this deregisters the memory in the java.nio tracking
	 * Ie, making sure direct memory doesn't exceed "-XX:MaxDirectMemorySize=<size>"
	 * :>
	 */
	public static void makeDirectMemoryDeallocatedOnJavaObjectGarbageCollection(Object thing, long memoryAddress, long actualSizeOfMemory, int capacity)
	{
		Cleaner.create(thing, newTrackedDirectMemoryDeallocator(memoryAddress, actualSizeOfMemory, capacity));
	}
	
	public static void makeUntrackedCHeapMemoryDeallocatedOnJavaObjectGarbageCollection(Object thing, long memoryAddress)
	{
		Cleaner.create(thing, newUntrackedCHeapMemoryDeallocator(memoryAddress));
	}
	
	
	public static Runnable newTrackedDirectMemoryDeallocator(long address, long sizeOfActualAllocatedMemory, int capacityUsed)
	{
		try
		{
			getDeallocator_init().setAccessible(true);
			return (Runnable)getDeallocator_init().newInstance(address, sizeOfActualAllocatedMemory, capacityUsed);
		}
		catch (InvocationTargetException exc)
		{
			ExceptionUtilities.throwGeneralThrowableAttemptingUnverifiedThrow(exc.getCause());
			throw new UnreachableCodeException();
		}
		catch (InstantiationException exc)
		{
			throw new HackedClassOrMemberUnavailableException("InstantiationException while instantiating a "+Deallocator.getName(), exc);
		}
		catch (IllegalArgumentException exc)
		{
			throw new HackedClassOrMemberUnavailableException("IllegalArgumentException while instantiating a "+Deallocator.getName()+" ._.", exc);
		}
		catch (IllegalAccessException exc)
		{
			throw new ImpossibleException(exc); //we called .setAccessible(true), so a SecurityException might happen, but never this :)
		}
	}
	
	public static Runnable newUntrackedCHeapMemoryDeallocator(final long address)
	{
		return new Runnable()
		{
			@Override
			public void run()
			{
				((Unsafe)ClasshackingSunUnsafe.getTheUnsafe()).freeMemory(address);  //FIXME THIS IS NOT GOOD; THEY CHANGED THE FQN AFTER JAVA 8!!
			}
		};
	}
	
	
	
	/**
	 * Ie, the address of position 0 in this buffer; NOT the address of its backing buffer! (eg, slices and views!)
	 * See {@link #getUnderlyingBufferBaseAddress(Buffer)} for that ^^
	 */
	public static long getBufferBaseAddress(Buffer buffer) throws IllegalArgumentException
	{
		if (buffer instanceof DirectBuffer)
			return ((DirectBuffer)buffer).address();
		else
		{
			if (!buffer.isDirect())
				throw new IllegalArgumentException("Buffer is not a direct buffer!");
			else
				throw new ImPrettySureThisNeverActuallyHappensRuntimeException("A direct buffer that is not a sun.nio.ch.DirectBuffer! o_O!");
		}
	}
	
	public static long getUnderlyingBufferBaseAddress(Buffer buffer) throws IllegalArgumentException
	{
		return getBufferBaseAddress(getUnderlyingByteBufferEnforced(buffer));
	}
	
	public static int getUnderlyingBufferCapacity(Buffer buffer) throws IllegalArgumentException
	{
		return getUnderlyingByteBufferEnforced(buffer).capacity();
	}
	
	
	
	
	
	
	
	/*
		pkg = java.nio :>
		
		ByteBufferAs{Short,Char,Int,Float,Long,Double}Buffer{B,L}{,R}
		DirectByteBuffer{,R}
		Direct{Short,Char,Int,Float,Long,Double}Buffer{U,S}{,R}
		
		DirectByteBuffer$Deallocator
	 */
	
	protected static Object opportunisticSoftLinkingLock = new Object();
	protected static boolean opportunisticSoftLinkingCompleted = false;
	
	protected static Class Deallocator;
	protected static Constructor Deallocator_init;
	
	protected static Class ByteBufferAsShortBufferB;
	protected static Class ByteBufferAsShortBufferRB;
	protected static Class ByteBufferAsShortBufferL;
	protected static Class ByteBufferAsShortBufferRL;
	protected static Class ByteBufferAsCharBufferB;
	protected static Class ByteBufferAsCharBufferRB;
	protected static Class ByteBufferAsCharBufferL;
	protected static Class ByteBufferAsCharBufferRL;
	protected static Class ByteBufferAsIntBufferB;
	protected static Class ByteBufferAsIntBufferRB;
	protected static Class ByteBufferAsIntBufferL;
	protected static Class ByteBufferAsIntBufferRL;
	protected static Class ByteBufferAsFloatBufferB;
	protected static Class ByteBufferAsFloatBufferRB;
	protected static Class ByteBufferAsFloatBufferL;
	protected static Class ByteBufferAsFloatBufferRL;
	protected static Class ByteBufferAsLongBufferB;
	protected static Class ByteBufferAsLongBufferRB;
	protected static Class ByteBufferAsLongBufferL;
	protected static Class ByteBufferAsLongBufferRL;
	protected static Class ByteBufferAsDoubleBufferB;
	protected static Class ByteBufferAsDoubleBufferRB;
	protected static Class ByteBufferAsDoubleBufferL;
	protected static Class ByteBufferAsDoubleBufferRL;
	
	
	protected static Class DirectByteBuffer;
	protected static Class DirectByteBufferR;
	
	protected static Class DirectShortBufferU;
	protected static Class DirectShortBufferRU;
	protected static Class DirectShortBufferS;
	protected static Class DirectShortBufferRS;
	protected static Class DirectCharBufferU;
	protected static Class DirectCharBufferRU;
	protected static Class DirectCharBufferS;
	protected static Class DirectCharBufferRS;
	protected static Class DirectIntBufferU;
	protected static Class DirectIntBufferRU;
	protected static Class DirectIntBufferS;
	protected static Class DirectIntBufferRS;
	protected static Class DirectFloatBufferU;
	protected static Class DirectFloatBufferRU;
	protected static Class DirectFloatBufferS;
	protected static Class DirectFloatBufferRS;
	protected static Class DirectLongBufferU;
	protected static Class DirectLongBufferRU;
	protected static Class DirectLongBufferS;
	protected static Class DirectLongBufferRS;
	protected static Class DirectDoubleBufferU;
	protected static Class DirectDoubleBufferRU;
	protected static Class DirectDoubleBufferS;
	protected static Class DirectDoubleBufferRS;
	
	
	public static void doOpportunisticSoftLinking()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			if (!opportunisticSoftLinkingCompleted)
			{
				Deallocator = ClasshackingUtilities.classhackingForName("java.nio.DirectByteBuffer$Deallocator");
				Deallocator_init = ClasshackingUtilities.ensureHackedClassThingNonNull(AngryReflectionUtility.getConstructor(Deallocator, new Class[]{long.class, long.class, int.class}, null), Deallocator.getName()+".<init>(long, long, int)");
				
				ByteBufferAsShortBufferB = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsShortBufferB");
				ByteBufferAsShortBufferRB = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsShortBufferRB");
				ByteBufferAsShortBufferL = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsShortBufferL");
				ByteBufferAsShortBufferRL = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsShortBufferRL");
				ByteBufferAsCharBufferB = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsCharBufferB");
				ByteBufferAsCharBufferRB = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsCharBufferRB");
				ByteBufferAsCharBufferL = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsCharBufferL");
				ByteBufferAsCharBufferRL = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsCharBufferRL");
				ByteBufferAsIntBufferB = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsIntBufferB");
				ByteBufferAsIntBufferRB = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsIntBufferRB");
				ByteBufferAsIntBufferL = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsIntBufferL");
				ByteBufferAsIntBufferRL = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsIntBufferRL");
				ByteBufferAsFloatBufferB = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsFloatBufferB");
				ByteBufferAsFloatBufferRB = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsFloatBufferRB");
				ByteBufferAsFloatBufferL = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsFloatBufferL");
				ByteBufferAsFloatBufferRL = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsFloatBufferRL");
				ByteBufferAsLongBufferB = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsLongBufferB");
				ByteBufferAsLongBufferRB = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsLongBufferRB");
				ByteBufferAsLongBufferL = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsLongBufferL");
				ByteBufferAsLongBufferRL = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsLongBufferRL");
				ByteBufferAsDoubleBufferB = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsDoubleBufferB");
				ByteBufferAsDoubleBufferRB = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsDoubleBufferRB");
				ByteBufferAsDoubleBufferL = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsDoubleBufferL");
				ByteBufferAsDoubleBufferRL = ClasshackingUtilities.classhackingForName("java.nio.ByteBufferAsDoubleBufferRL");
				
				
				DirectByteBuffer = ClasshackingUtilities.classhackingForName("java.nio.DirectByteBuffer");
				DirectByteBufferR = ClasshackingUtilities.classhackingForName("java.nio.DirectByteBufferR");
				
				DirectShortBufferU = ClasshackingUtilities.classhackingForName("java.nio.DirectShortBufferU");
				DirectShortBufferRU = ClasshackingUtilities.classhackingForName("java.nio.DirectShortBufferRU");
				DirectShortBufferS = ClasshackingUtilities.classhackingForName("java.nio.DirectShortBufferS");
				DirectShortBufferRS = ClasshackingUtilities.classhackingForName("java.nio.DirectShortBufferRS");
				DirectCharBufferU = ClasshackingUtilities.classhackingForName("java.nio.DirectCharBufferU");
				DirectCharBufferRU = ClasshackingUtilities.classhackingForName("java.nio.DirectCharBufferRU");
				DirectCharBufferS = ClasshackingUtilities.classhackingForName("java.nio.DirectCharBufferS");
				DirectCharBufferRS = ClasshackingUtilities.classhackingForName("java.nio.DirectCharBufferRS");
				DirectIntBufferU = ClasshackingUtilities.classhackingForName("java.nio.DirectIntBufferU");
				DirectIntBufferRU = ClasshackingUtilities.classhackingForName("java.nio.DirectIntBufferRU");
				DirectIntBufferS = ClasshackingUtilities.classhackingForName("java.nio.DirectIntBufferS");
				DirectIntBufferRS = ClasshackingUtilities.classhackingForName("java.nio.DirectIntBufferRS");
				DirectFloatBufferU = ClasshackingUtilities.classhackingForName("java.nio.DirectFloatBufferU");
				DirectFloatBufferRU = ClasshackingUtilities.classhackingForName("java.nio.DirectFloatBufferRU");
				DirectFloatBufferS = ClasshackingUtilities.classhackingForName("java.nio.DirectFloatBufferS");
				DirectFloatBufferRS = ClasshackingUtilities.classhackingForName("java.nio.DirectFloatBufferRS");
				DirectLongBufferU = ClasshackingUtilities.classhackingForName("java.nio.DirectLongBufferU");
				DirectLongBufferRU = ClasshackingUtilities.classhackingForName("java.nio.DirectLongBufferRU");
				DirectLongBufferS = ClasshackingUtilities.classhackingForName("java.nio.DirectLongBufferS");
				DirectLongBufferRS = ClasshackingUtilities.classhackingForName("java.nio.DirectLongBufferRS");
				DirectDoubleBufferU = ClasshackingUtilities.classhackingForName("java.nio.DirectDoubleBufferU");
				DirectDoubleBufferRU = ClasshackingUtilities.classhackingForName("java.nio.DirectDoubleBufferRU");
				DirectDoubleBufferS = ClasshackingUtilities.classhackingForName("java.nio.DirectDoubleBufferS");
				DirectDoubleBufferRS = ClasshackingUtilities.classhackingForName("java.nio.DirectDoubleBufferRS");
				
				opportunisticSoftLinkingCompleted = true;
			}
		}
	}
	
	
	
	
	protected static Class getDeallocator()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return Deallocator;
		}
	}
	
	protected static Constructor getDeallocator_init()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return Deallocator_init;
		}
	}
	
	
	protected static Class getByteBufferAsShortBufferB()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsShortBufferB;
		}
	}
	
	protected static Class getByteBufferAsShortBufferRB()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsShortBufferRB;
		}
	}
	
	protected static Class getByteBufferAsShortBufferL()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsShortBufferL;
		}
	}
	
	protected static Class getByteBufferAsShortBufferRL()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsShortBufferRL;
		}
	}
	
	protected static Class getByteBufferAsCharBufferB()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsCharBufferB;
		}
	}
	
	protected static Class getByteBufferAsCharBufferRB()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsCharBufferRB;
		}
	}
	
	protected static Class getByteBufferAsCharBufferL()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsCharBufferL;
		}
	}
	
	protected static Class getByteBufferAsCharBufferRL()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsCharBufferRL;
		}
	}
	
	protected static Class getByteBufferAsIntBufferB()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsIntBufferB;
		}
	}
	
	protected static Class getByteBufferAsIntBufferRB()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsIntBufferRB;
		}
	}
	
	protected static Class getByteBufferAsIntBufferL()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsIntBufferL;
		}
	}
	
	protected static Class getByteBufferAsIntBufferRL()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsIntBufferRL;
		}
	}
	
	protected static Class getByteBufferAsFloatBufferB()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsFloatBufferB;
		}
	}
	
	protected static Class getByteBufferAsFloatBufferRB()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsFloatBufferRB;
		}
	}
	
	protected static Class getByteBufferAsFloatBufferL()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsFloatBufferL;
		}
	}
	
	protected static Class getByteBufferAsFloatBufferRL()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsFloatBufferRL;
		}
	}
	
	protected static Class getByteBufferAsLongBufferB()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsLongBufferB;
		}
	}
	
	protected static Class getByteBufferAsLongBufferRB()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsLongBufferRB;
		}
	}
	
	protected static Class getByteBufferAsLongBufferL()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsLongBufferL;
		}
	}
	
	protected static Class getByteBufferAsLongBufferRL()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsLongBufferRL;
		}
	}
	
	protected static Class getByteBufferAsDoubleBufferB()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsDoubleBufferB;
		}
	}
	
	protected static Class getByteBufferAsDoubleBufferRB()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsDoubleBufferRB;
		}
	}
	
	protected static Class getByteBufferAsDoubleBufferL()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsDoubleBufferL;
		}
	}
	
	protected static Class getByteBufferAsDoubleBufferRL()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return ByteBufferAsDoubleBufferRL;
		}
	}
	
	
	
	protected static Class getDirectByteBuffer()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectByteBuffer;
		}
	}
	
	protected static Class getDirectByteBufferR()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectByteBufferR;
		}
	}
	
	
	protected static Class getDirectShortBufferU()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectShortBufferU;
		}
	}
	
	protected static Class getDirectShortBufferRU()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectShortBufferRU;
		}
	}
	
	protected static Class getDirectShortBufferS()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectShortBufferS;
		}
	}
	
	protected static Class getDirectShortBufferRS()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectShortBufferRS;
		}
	}
	
	protected static Class getDirectCharBufferU()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectCharBufferU;
		}
	}
	
	protected static Class getDirectCharBufferRU()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectCharBufferRU;
		}
	}
	
	protected static Class getDirectCharBufferS()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectCharBufferS;
		}
	}
	
	protected static Class getDirectCharBufferRS()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectCharBufferRS;
		}
	}
	
	protected static Class getDirectIntBufferU()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectIntBufferU;
		}
	}
	
	protected static Class getDirectIntBufferRU()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectIntBufferRU;
		}
	}
	
	protected static Class getDirectIntBufferS()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectIntBufferS;
		}
	}
	
	protected static Class getDirectIntBufferRS()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectIntBufferRS;
		}
	}
	
	protected static Class getDirectFloatBufferU()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectFloatBufferU;
		}
	}
	
	protected static Class getDirectFloatBufferRU()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectFloatBufferRU;
		}
	}
	
	protected static Class getDirectFloatBufferS()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectFloatBufferS;
		}
	}
	
	protected static Class getDirectFloatBufferRS()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectFloatBufferRS;
		}
	}
	
	protected static Class getDirectLongBufferU()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectLongBufferU;
		}
	}
	
	protected static Class getDirectLongBufferRU()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectLongBufferRU;
		}
	}
	
	protected static Class getDirectLongBufferS()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectLongBufferS;
		}
	}
	
	protected static Class getDirectLongBufferRS()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectLongBufferRS;
		}
	}
	
	protected static Class getDirectDoubleBufferU()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectDoubleBufferU;
		}
	}
	
	protected static Class getDirectDoubleBufferRU()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectDoubleBufferRU;
		}
	}
	
	protected static Class getDirectDoubleBufferS()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectDoubleBufferS;
		}
	}
	
	protected static Class getDirectDoubleBufferRS()
	{
		synchronized (opportunisticSoftLinkingLock)
		{
			doOpportunisticSoftLinking();
			return DirectDoubleBufferRS;
		}
	}
}
