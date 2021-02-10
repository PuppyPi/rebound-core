/*
 * Created on Aug 30, 2008
 * 	by the great Eclipse(c)
 */
package rebound.util.collections;

import static rebound.bits.BitUtilities.*;
import static rebound.bits.BitfieldSafeCasts.*;
import static rebound.math.SmallFloatMathUtilities.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.util.CodeHinting.*;
import static rebound.util.Primitives.*;
import static rebound.util.collections.prim.PrimitiveCollections.*;
import static rebound.util.objectutil.BasicObjectUtilities.*;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.reachability.PossiblySnapshotPossiblyLiveValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.bits.Bytes;
import rebound.exceptions.NotYetImplementedException;
import rebound.math.SmallIntegerMathUtilities;
import rebound.util.BasicExceptionUtilities;
import rebound.util.Primitives;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.collections.prim.PrimitiveCollections.CharacterList;
import rebound.util.collections.prim.PrimitiveCollections.DoubleList;
import rebound.util.collections.prim.PrimitiveCollections.FloatList;
import rebound.util.collections.prim.PrimitiveCollections.IntegerList;
import rebound.util.collections.prim.PrimitiveCollections.LongList;
import rebound.util.collections.prim.PrimitiveCollections.ShortList;
import rebound.util.container.ContainerInterfaces.ObjectContainer;
import rebound.util.functional.EqualityComparator;
import rebound.util.functional.FunctionInterfaces.BooleanEqualityComparator;
import rebound.util.functional.FunctionInterfaces.ByteEqualityComparator;
import rebound.util.functional.FunctionInterfaces.CharEqualityComparator;
import rebound.util.functional.FunctionInterfaces.DoubleEqualityComparator;
import rebound.util.functional.FunctionInterfaces.FloatEqualityComparator;
import rebound.util.functional.FunctionInterfaces.IntEqualityComparator;
import rebound.util.functional.FunctionInterfaces.LongEqualityComparator;
import rebound.util.functional.FunctionInterfaces.NullaryFunction;
import rebound.util.functional.FunctionInterfaces.ShortEqualityComparator;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionBooleanToBoolean;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionByteToBoolean;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionCharToBoolean;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionDoubleToBoolean;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionFloatToBoolean;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionIntToBoolean;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionIntToObject;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionLongToBoolean;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionShortToBoolean;
import rebound.util.objectutil.JavaNamespace;
import rebound.util.objectutil.PubliclyCloneable;

public class ArrayUtilities
implements JavaNamespace
{
	//Static immutable empty arrays (in case JVM doesn't actually optimize new XYZ[0] to something equivalent)
	public static final boolean[] EmptyBooleanArray = new boolean[0];
	public static final char[] EmptyCharArray = new char[0];
	public static final byte[] EmptyByteArray = new byte[0];
	public static final short[] EmptyShortArray = new short[0];
	public static final int[] EmptyIntArray = new int[0];
	public static final long[] EmptyLongArray = new long[0];
	public static final float[] EmptyFloatArray = new float[0];
	public static final double[] EmptyDoubleArray = new double[0];
	public static final Object[] EmptyObjectArray = new Object[0];
	
	public static final Slice<boolean[]> EmptyBooleanSlice = new Slice<>(EmptyBooleanArray, 0, 0);
	public static final Slice<char[]> EmptyCharSlice = new Slice<>(EmptyCharArray, 0, 0);
	public static final Slice<byte[]> EmptyByteSlice = new Slice<>(EmptyByteArray, 0, 0);
	public static final Slice<short[]> EmptyShortSlice = new Slice<>(EmptyShortArray, 0, 0);
	public static final Slice<int[]> EmptyIntSlice = new Slice<>(EmptyIntArray, 0, 0);
	public static final Slice<long[]> EmptyLongSlice = new Slice<>(EmptyLongArray, 0, 0);
	public static final Slice<float[]> EmptyFloatSlice = new Slice<>(EmptyFloatArray, 0, 0);
	public static final Slice<double[]> EmptyDoubleSlice = new Slice<>(EmptyDoubleArray, 0, 0);
	public static final Slice<Object[]> EmptyObjectSlice = new Slice<>(EmptyObjectArray, 0, 0);
	
	
	
	
	public static boolean eqa(Object a, Object b)
	{
		if (a == b)
			return true;
		
		else if (a == null || b == null)  //&& a != b
			return false;
		
		else if (a.equals(b))
			return true;
		
		else if (a instanceof Object[])
			return b instanceof Object[] && arrayDeepEquals(a, b);
		
		/* <<<
			primxp
			
			else if (a instanceof _$$prim$$_[])
				return b instanceof _$$prim$$_[] && arrayEquals((_$$prim$$_[])a, (_$$prim$$_[])b);
		 */
		
		else if (a instanceof boolean[])
			return b instanceof boolean[] && arrayEquals((boolean[])a, (boolean[])b);
		
		else if (a instanceof byte[])
			return b instanceof byte[] && arrayEquals((byte[])a, (byte[])b);
		
		else if (a instanceof char[])
			return b instanceof char[] && arrayEquals((char[])a, (char[])b);
		
		else if (a instanceof short[])
			return b instanceof short[] && arrayEquals((short[])a, (short[])b);
		
		else if (a instanceof float[])
			return b instanceof float[] && arrayEquals((float[])a, (float[])b);
		
		else if (a instanceof int[])
			return b instanceof int[] && arrayEquals((int[])a, (int[])b);
		
		else if (a instanceof double[])
			return b instanceof double[] && arrayEquals((double[])a, (double[])b);
		
		else if (a instanceof long[])
			return b instanceof long[] && arrayEquals((long[])a, (long[])b);
		// >>>
		
		
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void slicecopy(Slice source, int offsetSource, Slice dest, int offsetDest, int length)
	{
		System.arraycopy(source.getUnderlying(), source.getOffset()+offsetSource, dest.getUnderlying(), dest.getOffset()+offsetDest, length);
	}
	
	public static void slicecopy(Slice source, Slice dest, int length)
	{
		slicecopy(source, 0, dest, 0, length);
	}
	
	public static void slicecopy(Slice source, Slice dest)
	{
		int n = source.getLength();
		if (n != dest.getLength())
			throw new IllegalArgumentException("Lengths don't match!  "+source.getLength()+" != "+dest.getLength());
		
		slicecopy(source, dest, n);
	}
	
	
	
	
	
	
	
	
	public static boolean arrayShallowEquals(Object[] a, int aOffset, Object[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (a[aOffset+i] != b[bOffset+i])
				return false;
		return true;
	}
	
	public static boolean arrayShallowEquals(Object[] a, Object[] b)
	{
		int length = a.length;
		if (b.length != length)
			return false;
		return arrayShallowEquals(a, 0, b, 0, length);
	}
	
	
	//Todo public static boolean arrayDeepEquals(Object[] a, int aOffset, Object[] b, int bOffset, int length)
	//Todo public static boolean arrayDeepEquals(Object[] a, Object[] b)
	
	//Todo public static boolean arrayShallowEquals(Object a, int aOffset, Object b, int bOffset, int length)
	//Todo public static boolean arrayShallowEquals(Object a, Object b)
	//Todo public static boolean arrayDeepEquals(Object a, int aOffset, Object b, int bOffset, int length)
	//Todo public static boolean arrayDeepEquals(Object a, Object b)
	
	
	
	/* <<<
	primxp
	public static boolean arrayEquals(_$$prim$$_[] a, int aOffset, _$$prim$$_[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (!eqSane(a[aOffset+i], b[bOffset+i]))
				return false;
		return true;
	}
	
	public static boolean arrayEquals(_$$prim$$_[] a, _$$prim$$_[] b)
	{
		int length = a.length;
		if (b.length != length)
			return false;
		return arrayEquals(a, 0, b, 0, length);
	}
	
	public static boolean arrayEquals_$$Prim$$_(Slice<_$$prim$$_[]> a, Slice<_$$prim$$_[]> b)
	{
		int length = a.getLength();
		if (b.getLength() != length)
			return false;
		return arrayEquals(a.getUnderlying(), a.getOffset(), b.getUnderlying(), b.getOffset(), length);
	}
	
	
	 */
	public static boolean arrayEquals(boolean[] a, int aOffset, boolean[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (!eqSane(a[aOffset+i], b[bOffset+i]))
				return false;
		return true;
	}
	
	public static boolean arrayEquals(boolean[] a, boolean[] b)
	{
		int length = a.length;
		if (b.length != length)
			return false;
		return arrayEquals(a, 0, b, 0, length);
	}
	
	public static boolean arrayEqualsBoolean(Slice<boolean[]> a, Slice<boolean[]> b)
	{
		int length = a.getLength();
		if (b.getLength() != length)
			return false;
		return arrayEquals(a.getUnderlying(), a.getOffset(), b.getUnderlying(), b.getOffset(), length);
	}
	
	
	public static boolean arrayEquals(byte[] a, int aOffset, byte[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (!eqSane(a[aOffset+i], b[bOffset+i]))
				return false;
		return true;
	}
	
	public static boolean arrayEquals(byte[] a, byte[] b)
	{
		int length = a.length;
		if (b.length != length)
			return false;
		return arrayEquals(a, 0, b, 0, length);
	}
	
	public static boolean arrayEqualsByte(Slice<byte[]> a, Slice<byte[]> b)
	{
		int length = a.getLength();
		if (b.getLength() != length)
			return false;
		return arrayEquals(a.getUnderlying(), a.getOffset(), b.getUnderlying(), b.getOffset(), length);
	}
	
	
	public static boolean arrayEquals(char[] a, int aOffset, char[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (!eqSane(a[aOffset+i], b[bOffset+i]))
				return false;
		return true;
	}
	
	public static boolean arrayEquals(char[] a, char[] b)
	{
		int length = a.length;
		if (b.length != length)
			return false;
		return arrayEquals(a, 0, b, 0, length);
	}
	
	public static boolean arrayEqualsChar(Slice<char[]> a, Slice<char[]> b)
	{
		int length = a.getLength();
		if (b.getLength() != length)
			return false;
		return arrayEquals(a.getUnderlying(), a.getOffset(), b.getUnderlying(), b.getOffset(), length);
	}
	
	
	public static boolean arrayEquals(short[] a, int aOffset, short[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (!eqSane(a[aOffset+i], b[bOffset+i]))
				return false;
		return true;
	}
	
	public static boolean arrayEquals(short[] a, short[] b)
	{
		int length = a.length;
		if (b.length != length)
			return false;
		return arrayEquals(a, 0, b, 0, length);
	}
	
	public static boolean arrayEqualsShort(Slice<short[]> a, Slice<short[]> b)
	{
		int length = a.getLength();
		if (b.getLength() != length)
			return false;
		return arrayEquals(a.getUnderlying(), a.getOffset(), b.getUnderlying(), b.getOffset(), length);
	}
	
	
	public static boolean arrayEquals(float[] a, int aOffset, float[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (!eqSane(a[aOffset+i], b[bOffset+i]))
				return false;
		return true;
	}
	
	public static boolean arrayEquals(float[] a, float[] b)
	{
		int length = a.length;
		if (b.length != length)
			return false;
		return arrayEquals(a, 0, b, 0, length);
	}
	
	public static boolean arrayEqualsFloat(Slice<float[]> a, Slice<float[]> b)
	{
		int length = a.getLength();
		if (b.getLength() != length)
			return false;
		return arrayEquals(a.getUnderlying(), a.getOffset(), b.getUnderlying(), b.getOffset(), length);
	}
	
	
	public static boolean arrayEquals(int[] a, int aOffset, int[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (!eqSane(a[aOffset+i], b[bOffset+i]))
				return false;
		return true;
	}
	
	public static boolean arrayEquals(int[] a, int[] b)
	{
		int length = a.length;
		if (b.length != length)
			return false;
		return arrayEquals(a, 0, b, 0, length);
	}
	
	public static boolean arrayEqualsInt(Slice<int[]> a, Slice<int[]> b)
	{
		int length = a.getLength();
		if (b.getLength() != length)
			return false;
		return arrayEquals(a.getUnderlying(), a.getOffset(), b.getUnderlying(), b.getOffset(), length);
	}
	
	
	public static boolean arrayEquals(double[] a, int aOffset, double[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (!eqSane(a[aOffset+i], b[bOffset+i]))
				return false;
		return true;
	}
	
	public static boolean arrayEquals(double[] a, double[] b)
	{
		int length = a.length;
		if (b.length != length)
			return false;
		return arrayEquals(a, 0, b, 0, length);
	}
	
	public static boolean arrayEqualsDouble(Slice<double[]> a, Slice<double[]> b)
	{
		int length = a.getLength();
		if (b.getLength() != length)
			return false;
		return arrayEquals(a.getUnderlying(), a.getOffset(), b.getUnderlying(), b.getOffset(), length);
	}
	
	
	public static boolean arrayEquals(long[] a, int aOffset, long[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (!eqSane(a[aOffset+i], b[bOffset+i]))
				return false;
		return true;
	}
	
	public static boolean arrayEquals(long[] a, long[] b)
	{
		int length = a.length;
		if (b.length != length)
			return false;
		return arrayEquals(a, 0, b, 0, length);
	}
	
	public static boolean arrayEqualsLong(Slice<long[]> a, Slice<long[]> b)
	{
		int length = a.getLength();
		if (b.getLength() != length)
			return false;
		return arrayEquals(a.getUnderlying(), a.getOffset(), b.getUnderlying(), b.getOffset(), length);
	}
	
	
	// >>>
	
	
	
	public static boolean arrayEqualsPolymorphic(Object arrayA, Object arrayB)
	{
		if (arrayA == arrayB) return true;
		if (arrayA == null || arrayB == null) return false;
		if (arrayA.getClass() != arrayB.getClass()) return false;
		
		Object tokenArray = arrayA; //arbitrary since their runtime types are equal!
		
		if (tokenArray instanceof boolean[]) return Arrays.equals((boolean[])arrayA, (boolean[])arrayB);
		else if (tokenArray instanceof byte[]) return Arrays.equals((byte[])arrayA, (byte[])arrayB);
		else if (tokenArray instanceof short[]) return Arrays.equals((short[])arrayA, (short[])arrayB);
		else if (tokenArray instanceof char[]) return Arrays.equals((char[])arrayA, (char[])arrayB);
		else if (tokenArray instanceof int[]) return Arrays.equals((int[])arrayA, (int[])arrayB);
		else if (tokenArray instanceof float[]) return Arrays.equals((float[])arrayA, (float[])arrayB);
		else if (tokenArray instanceof long[]) return Arrays.equals((long[])arrayA, (long[])arrayB);
		else if (tokenArray instanceof double[]) return Arrays.equals((double[])arrayA, (double[])arrayB);
		else if (tokenArray instanceof Object[]) return Arrays.equals((Object[])arrayA, (Object[])arrayB);
		else throw new IllegalArgumentException("Provided objects are not arrays!");
	}
	
	public static boolean arrayDeepEquals(Object arrayA, Object arrayB)
	{
		if (arrayA == arrayB) return true;
		if (arrayA == null || arrayB == null) return false;
		if (arrayA.getClass() != arrayB.getClass()) return false;
		
		Object tokenArray = arrayA; //arbitrary since their runtime types are equal!
		
		if (tokenArray instanceof boolean[]) return Arrays.equals((boolean[])arrayA, (boolean[])arrayB);
		else if (tokenArray instanceof byte[]) return Arrays.equals((byte[])arrayA, (byte[])arrayB);
		else if (tokenArray instanceof short[]) return Arrays.equals((short[])arrayA, (short[])arrayB);
		else if (tokenArray instanceof char[]) return Arrays.equals((char[])arrayA, (char[])arrayB);
		else if (tokenArray instanceof int[]) return Arrays.equals((int[])arrayA, (int[])arrayB);
		else if (tokenArray instanceof float[]) return Arrays.equals((float[])arrayA, (float[])arrayB);
		else if (tokenArray instanceof long[]) return Arrays.equals((long[])arrayA, (long[])arrayB);
		else if (tokenArray instanceof double[]) return Arrays.equals((double[])arrayA, (double[])arrayB);
		else if (tokenArray instanceof Object[]) return Arrays.deepEquals((Object[])arrayA, (Object[])arrayB);
		else throw new IllegalArgumentException("Provided objects are not arrays!");
	}
	
	
	
	
	
	
	
	public static int arrayHashCode(Object[] array, int offset, int length)
	{
		if (array == null)
			return 0;
		
		int result = 1;
		for (int i = 0; i < offset+length; i++)
			result = 31 * result + (array[i] == null ? 0 : array[i].hashCode());
		return result;
	}
	
	public static int arrayHashCode(Object[] array)
	{
		return arrayHashCode(array, 0, array.length);
	}
	
	
	
	/* <<<
primxp

	public static int arrayHashCode(_$$prim$$_[] array, int offset, int length)
	{
		if (array == null)
			return 0;
		
		int result = 1;
		for (int i = 0; i < offset+length; i++)
			result = 31 * result + hashprim(array[i]);
		return result;
	}

	public static int arrayHashCode(_$$prim$$_[] array)
	{
		return arrayHashCode(array, 0, array.length);
	}
	
	
	 */
	
	public static int arrayHashCode(boolean[] array, int offset, int length)
	{
		if (array == null)
			return 0;
		
		int result = 1;
		for (int i = 0; i < offset+length; i++)
			result = 31 * result + hashprim(array[i]);
		return result;
	}
	
	public static int arrayHashCode(boolean[] array)
	{
		return arrayHashCode(array, 0, array.length);
	}
	
	
	
	public static int arrayHashCode(byte[] array, int offset, int length)
	{
		if (array == null)
			return 0;
		
		int result = 1;
		for (int i = 0; i < offset+length; i++)
			result = 31 * result + hashprim(array[i]);
		return result;
	}
	
	public static int arrayHashCode(byte[] array)
	{
		return arrayHashCode(array, 0, array.length);
	}
	
	
	
	public static int arrayHashCode(char[] array, int offset, int length)
	{
		if (array == null)
			return 0;
		
		int result = 1;
		for (int i = 0; i < offset+length; i++)
			result = 31 * result + hashprim(array[i]);
		return result;
	}
	
	public static int arrayHashCode(char[] array)
	{
		return arrayHashCode(array, 0, array.length);
	}
	
	
	
	public static int arrayHashCode(short[] array, int offset, int length)
	{
		if (array == null)
			return 0;
		
		int result = 1;
		for (int i = 0; i < offset+length; i++)
			result = 31 * result + hashprim(array[i]);
		return result;
	}
	
	public static int arrayHashCode(short[] array)
	{
		return arrayHashCode(array, 0, array.length);
	}
	
	
	
	public static int arrayHashCode(float[] array, int offset, int length)
	{
		if (array == null)
			return 0;
		
		int result = 1;
		for (int i = 0; i < offset+length; i++)
			result = 31 * result + hashprim(array[i]);
		return result;
	}
	
	public static int arrayHashCode(float[] array)
	{
		return arrayHashCode(array, 0, array.length);
	}
	
	
	
	public static int arrayHashCode(int[] array, int offset, int length)
	{
		if (array == null)
			return 0;
		
		int result = 1;
		for (int i = 0; i < offset+length; i++)
			result = 31 * result + hashprim(array[i]);
		return result;
	}
	
	public static int arrayHashCode(int[] array)
	{
		return arrayHashCode(array, 0, array.length);
	}
	
	
	
	public static int arrayHashCode(double[] array, int offset, int length)
	{
		if (array == null)
			return 0;
		
		int result = 1;
		for (int i = 0; i < offset+length; i++)
			result = 31 * result + hashprim(array[i]);
		return result;
	}
	
	public static int arrayHashCode(double[] array)
	{
		return arrayHashCode(array, 0, array.length);
	}
	
	
	
	public static int arrayHashCode(long[] array, int offset, int length)
	{
		if (array == null)
			return 0;
		
		int result = 1;
		for (int i = 0; i < offset+length; i++)
			result = 31 * result + hashprim(array[i]);
		return result;
	}
	
	public static int arrayHashCode(long[] array)
	{
		return arrayHashCode(array, 0, array.length);
	}
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	public static boolean arrayAllIs(Object[] array, int offset, int length, Object value)
	{
		int pastEnd = offset+length;
		for (int i = offset; i < pastEnd; i++)
			if (array[i] != value)
				return false;
		return true;
	}
	
	/*
	public static boolean arrayAllIs(_$$prim$$_[] array, int offset, int length, _$$prim$$_ value)
	{
		int pastEnd = offset+length;
		for (int i = offset; i < pastEnd; i++)
			if (array[i] != value)
				return false;
		return true;
	}
	
	 */
	
	public static boolean arrayAllIs(boolean[] array, int offset, int length, boolean value)
	{
		int pastEnd = offset+length;
		for (int i = offset; i < pastEnd; i++)
			if (array[i] != value)
				return false;
		return true;
	}
	
	public static boolean arrayAllIs(byte[] array, int offset, int length, byte value)
	{
		int pastEnd = offset+length;
		for (int i = offset; i < pastEnd; i++)
			if (array[i] != value)
				return false;
		return true;
	}
	
	public static boolean arrayAllIs(char[] array, int offset, int length, char value)
	{
		int pastEnd = offset+length;
		for (int i = offset; i < pastEnd; i++)
			if (array[i] != value)
				return false;
		return true;
	}
	
	public static boolean arrayAllIs(short[] array, int offset, int length, short value)
	{
		int pastEnd = offset+length;
		for (int i = offset; i < pastEnd; i++)
			if (array[i] != value)
				return false;
		return true;
	}
	
	public static boolean arrayAllIs(float[] array, int offset, int length, float value)
	{
		int pastEnd = offset+length;
		for (int i = offset; i < pastEnd; i++)
			if (array[i] != value)
				return false;
		return true;
	}
	
	public static boolean arrayAllIs(int[] array, int offset, int length, int value)
	{
		int pastEnd = offset+length;
		for (int i = offset; i < pastEnd; i++)
			if (array[i] != value)
				return false;
		return true;
	}
	
	public static boolean arrayAllIs(double[] array, int offset, int length, double value)
	{
		int pastEnd = offset+length;
		for (int i = offset; i < pastEnd; i++)
			if (array[i] != value)
				return false;
		return true;
	}
	
	public static boolean arrayAllIs(long[] array, int offset, int length, long value)
	{
		int pastEnd = offset+length;
		for (int i = offset; i < pastEnd; i++)
			if (array[i] != value)
				return false;
		return true;
	}
	
	
	
	public static <E> boolean arrayAllIs(E[] array, int offset, int length, E value, EqualityComparator<E> comparator)
	{
		int pastEnd = offset+length;
		for (int i = offset; i < pastEnd; i++)
			if (comparator.equals(array[i], value))
				return false;
		return true;
	}
	
	
	
	
	
	public static boolean arrayAllIs(Object[] array, Object value)
	{
		return arrayAllIs(array, 0, array.length, value);
	}
	
	/*
	public static boolean arrayAllIs(_$$prim$$_[] array, _$$prim$$_ value)
	{
		return arrayAllIs(array, 0, array.length, value);
	}
	
	 */
	
	public static boolean arrayAllIs(boolean[] array, boolean value)
	{
		return arrayAllIs(array, 0, array.length, value);
	}
	
	public static boolean arrayAllIs(byte[] array, byte value)
	{
		return arrayAllIs(array, 0, array.length, value);
	}
	
	public static boolean arrayAllIs(char[] array, char value)
	{
		return arrayAllIs(array, 0, array.length, value);
	}
	
	public static boolean arrayAllIs(short[] array, short value)
	{
		return arrayAllIs(array, 0, array.length, value);
	}
	
	public static boolean arrayAllIs(float[] array, float value)
	{
		return arrayAllIs(array, 0, array.length, value);
	}
	
	public static boolean arrayAllIs(int[] array, int value)
	{
		return arrayAllIs(array, 0, array.length, value);
	}
	
	public static boolean arrayAllIs(double[] array, double value)
	{
		return arrayAllIs(array, 0, array.length, value);
	}
	
	public static boolean arrayAllIs(long[] array, long value)
	{
		return arrayAllIs(array, 0, array.length, value);
	}
	
	
	
	public static <E> boolean arrayAllIs(E[] array, E value, EqualityComparator<E> comparator)
	{
		return arrayAllIs(array, 0, array.length, value, comparator);
	}
	
	
	
	
	
	
	
	
	
	
	
	public static boolean arrayContains(Object[] array, Object value)
	{
		//return arrayContains(array, value, ObjectUtilities.getNaturalEqualityComparator());
		for (Object e : array)
			if (Objects.equals(e, value))
				return true;
		return false;
	}
	
	public static boolean arrayContains(Object[] array, Object value, EqualityComparator comparator)
	{
		for (Object e : array)
			if (comparator.equals(e, value))
				return true;
		return false;
	}
	
	/*
	public static boolean arrayContains(_$$prim$$_[] array, _$$prim$$_ value)
	{
		for (_$$prim$$_ e : array)
			if (e == value)
				return true;
		return false;
	}
	
	 */
	
	
	
	public static boolean arrayContains(float[] array, float value)
	{
		for (float e : array)
			if (Primitives.eqSane(e, value))
				return true;
		return false;
	}
	
	public static boolean arrayContains(double[] array, double value)
	{
		for (double e : array)
			if (Primitives.eqSane(e, value))
				return true;
		return false;
	}
	
	
	
	public static boolean arrayContains(boolean[] array, boolean value)
	{
		for (boolean e : array)
			if (e == value)
				return true;
		return false;
	}
	
	public static boolean arrayContains(byte[] array, byte value)
	{
		for (byte e : array)
			if (e == value)
				return true;
		return false;
	}
	
	public static boolean arrayContains(char[] array, char value)
	{
		for (char e : array)
			if (e == value)
				return true;
		return false;
	}
	
	public static boolean arrayContains(short[] array, short value)
	{
		for (short e : array)
			if (e == value)
				return true;
		return false;
	}
	
	public static boolean arrayContains(int[] array, int value)
	{
		for (int e : array)
			if (e == value)
				return true;
		return false;
	}
	
	public static boolean arrayContains(long[] array, long value)
	{
		for (long e : array)
			if (e == value)
				return true;
		return false;
	}
	
	
	
	
	
	
	
	
	public static boolean arrayMatchesSilent(Object[] a, int aOffset, Object[] b, int bOffset, int length, EqualityComparator comparator)
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = SmallIntegerMathUtilities.progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = SmallIntegerMathUtilities.progmod(bOffset, b.length);
		
		int amtInA = Math.min(a.length, aOffset+length);
		int amtInB = Math.min(b.length, bOffset+length);
		
		if (amtInA != amtInB)
			return false;
		else
			//Then only compare the available regions
			length = amtInA; // = amtInB;  :>
		
		for (int i = 0; i < length; i++)
			if (!comparator.equals(a[aOffset+i], b[bOffset+i]))
				return false;
		return true;
	}
	
	/*
	public static boolean arrayMatchesSilent(_$$prim$$_[] a, int aOffset, _$$prim$$_[] b, int bOffset, int length)
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = progmod(bOffset, b.length);
		
		int amtInA = Math.min(a.length, aOffset+length);
		int amtInB = Math.min(b.length, bOffset+length);
		
		if (amtInA != amtInB)
			return false;
		else
			//Then only compare the available regions
			length = amtInA; // = amtInB;  :>
		
		for (int i = 0; i < length; i++)
			if (a[aOffset+i] != b[bOffset+i])
				return false;
		return true;
	}
	
	 */
	
	public static boolean arrayMatchesSilent(boolean[] a, int aOffset, boolean[] b, int bOffset, int length)
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = SmallIntegerMathUtilities.progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = SmallIntegerMathUtilities.progmod(bOffset, b.length);
		
		int amtInA = Math.min(a.length, aOffset+length);
		int amtInB = Math.min(b.length, bOffset+length);
		
		if (amtInA != amtInB)
			return false;
		else
			//Then only compare the available regions
			length = amtInA; // = amtInB;  :>
		
		for (int i = 0; i < length; i++)
			if (a[aOffset+i] != b[bOffset+i])
				return false;
		return true;
	}
	
	public static boolean arrayMatchesSilent(byte[] a, int aOffset, byte[] b, int bOffset, int length)
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = SmallIntegerMathUtilities.progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = SmallIntegerMathUtilities.progmod(bOffset, b.length);
		
		int amtInA = Math.min(a.length, aOffset+length);
		int amtInB = Math.min(b.length, bOffset+length);
		
		if (amtInA != amtInB)
			return false;
		else
			//Then only compare the available regions
			length = amtInA; // = amtInB;  :>
		
		for (int i = 0; i < length; i++)
			if (a[aOffset+i] != b[bOffset+i])
				return false;
		return true;
	}
	
	public static boolean arrayMatchesSilent(char[] a, int aOffset, char[] b, int bOffset, int length)
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = SmallIntegerMathUtilities.progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = SmallIntegerMathUtilities.progmod(bOffset, b.length);
		
		int amtInA = Math.min(a.length, aOffset+length);
		int amtInB = Math.min(b.length, bOffset+length);
		
		if (amtInA != amtInB)
			return false;
		else
			//Then only compare the available regions
			length = amtInA; // = amtInB;  :>
		
		for (int i = 0; i < length; i++)
			if (a[aOffset+i] != b[bOffset+i])
				return false;
		return true;
	}
	
	public static boolean arrayMatchesSilent(short[] a, int aOffset, short[] b, int bOffset, int length)
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = SmallIntegerMathUtilities.progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = SmallIntegerMathUtilities.progmod(bOffset, b.length);
		
		int amtInA = Math.min(a.length, aOffset+length);
		int amtInB = Math.min(b.length, bOffset+length);
		
		if (amtInA != amtInB)
			return false;
		else
			//Then only compare the available regions
			length = amtInA; // = amtInB;  :>
		
		for (int i = 0; i < length; i++)
			if (a[aOffset+i] != b[bOffset+i])
				return false;
		return true;
	}
	
	public static boolean arrayMatchesSilent(float[] a, int aOffset, float[] b, int bOffset, int length)
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = SmallIntegerMathUtilities.progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = SmallIntegerMathUtilities.progmod(bOffset, b.length);
		
		int amtInA = Math.min(a.length, aOffset+length);
		int amtInB = Math.min(b.length, bOffset+length);
		
		if (amtInA != amtInB)
			return false;
		else
			//Then only compare the available regions
			length = amtInA; // = amtInB;  :>
		
		for (int i = 0; i < length; i++)
			if (a[aOffset+i] != b[bOffset+i])
				return false;
		return true;
	}
	
	public static boolean arrayMatchesSilent(int[] a, int aOffset, int[] b, int bOffset, int length)
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = SmallIntegerMathUtilities.progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = SmallIntegerMathUtilities.progmod(bOffset, b.length);
		
		int amtInA = Math.min(a.length, aOffset+length);
		int amtInB = Math.min(b.length, bOffset+length);
		
		if (amtInA != amtInB)
			return false;
		else
			//Then only compare the available regions
			length = amtInA; // = amtInB;  :>
		
		for (int i = 0; i < length; i++)
			if (a[aOffset+i] != b[bOffset+i])
				return false;
		return true;
	}
	
	public static boolean arrayMatchesSilent(double[] a, int aOffset, double[] b, int bOffset, int length)
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = SmallIntegerMathUtilities.progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = SmallIntegerMathUtilities.progmod(bOffset, b.length);
		
		int amtInA = Math.min(a.length, aOffset+length);
		int amtInB = Math.min(b.length, bOffset+length);
		
		if (amtInA != amtInB)
			return false;
		else
			//Then only compare the available regions
			length = amtInA; // = amtInB;  :>
		
		for (int i = 0; i < length; i++)
			if (a[aOffset+i] != b[bOffset+i])
				return false;
		return true;
	}
	
	public static boolean arrayMatchesSilent(long[] a, int aOffset, long[] b, int bOffset, int length)
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = SmallIntegerMathUtilities.progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = SmallIntegerMathUtilities.progmod(bOffset, b.length);
		
		int amtInA = Math.min(a.length, aOffset+length);
		int amtInB = Math.min(b.length, bOffset+length);
		
		if (amtInA != amtInB)
			return false;
		else
			//Then only compare the available regions
			length = amtInA; // = amtInB;  :>
		
		for (int i = 0; i < length; i++)
			if (a[aOffset+i] != b[bOffset+i])
				return false;
		return true;
	}
	
	
	
	
	
	
	public static boolean arrayMatches(Object[] a, int aOffset, Object[] b, int bOffset, int length, EqualityComparator comparator) throws IndexOutOfBoundsException
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = SmallIntegerMathUtilities.progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = SmallIntegerMathUtilities.progmod(bOffset, b.length);
		
		if (aOffset+length > a.length)
			throw new IndexOutOfBoundsException();
		if (bOffset+length > b.length)
			throw new IndexOutOfBoundsException();
		
		for (int i = 0; i < length; i++)
			if (!comparator.equals(a[aOffset+i], b[bOffset+i]))
				return false;
		return true;
	}
	
	
	/*
	public static boolean arrayMatches(_$$prim$$_[] a, int aOffset, _$$prim$$_[] b, int bOffset, int length) throws IndexOutOfBoundsException
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = progmod(bOffset, b.length);
		
		if (aOffset+length > a.length)
			throw new IndexOutOfBoundsException();
		if (bOffset+length > b.length)
			throw new IndexOutOfBoundsException();
		
		for (int i = 0; i < length; i++)
			if (a[aOffset+i] != b[bOffset+i])
				return false;
		return true;
	}
	
	 */
	
	public static boolean arrayMatches(boolean[] a, int aOffset, boolean[] b, int bOffset, int length) throws IndexOutOfBoundsException
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = SmallIntegerMathUtilities.progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = SmallIntegerMathUtilities.progmod(bOffset, b.length);
		
		if (aOffset+length > a.length)
			throw new IndexOutOfBoundsException();
		if (bOffset+length > b.length)
			throw new IndexOutOfBoundsException();
		
		for (int i = 0; i < length; i++)
			if (a[aOffset+i] != b[bOffset+i])
				return false;
		return true;
	}
	
	public static boolean arrayMatches(byte[] a, int aOffset, byte[] b, int bOffset, int length) throws IndexOutOfBoundsException
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = SmallIntegerMathUtilities.progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = SmallIntegerMathUtilities.progmod(bOffset, b.length);
		
		if (aOffset+length > a.length)
			throw new IndexOutOfBoundsException();
		if (bOffset+length > b.length)
			throw new IndexOutOfBoundsException();
		
		for (int i = 0; i < length; i++)
			if (a[aOffset+i] != b[bOffset+i])
				return false;
		return true;
	}
	
	public static boolean arrayMatches(char[] a, int aOffset, char[] b, int bOffset, int length) throws IndexOutOfBoundsException
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = SmallIntegerMathUtilities.progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = SmallIntegerMathUtilities.progmod(bOffset, b.length);
		
		if (aOffset+length > a.length)
			throw new IndexOutOfBoundsException();
		if (bOffset+length > b.length)
			throw new IndexOutOfBoundsException();
		
		for (int i = 0; i < length; i++)
			if (a[aOffset+i] != b[bOffset+i])
				return false;
		return true;
	}
	
	public static boolean arrayMatches(short[] a, int aOffset, short[] b, int bOffset, int length) throws IndexOutOfBoundsException
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = SmallIntegerMathUtilities.progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = SmallIntegerMathUtilities.progmod(bOffset, b.length);
		
		if (aOffset+length > a.length)
			throw new IndexOutOfBoundsException();
		if (bOffset+length > b.length)
			throw new IndexOutOfBoundsException();
		
		for (int i = 0; i < length; i++)
			if (a[aOffset+i] != b[bOffset+i])
				return false;
		return true;
	}
	
	public static boolean arrayMatches(float[] a, int aOffset, float[] b, int bOffset, int length) throws IndexOutOfBoundsException
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = SmallIntegerMathUtilities.progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = SmallIntegerMathUtilities.progmod(bOffset, b.length);
		
		if (aOffset+length > a.length)
			throw new IndexOutOfBoundsException();
		if (bOffset+length > b.length)
			throw new IndexOutOfBoundsException();
		
		for (int i = 0; i < length; i++)
			if (a[aOffset+i] != b[bOffset+i])
				return false;
		return true;
	}
	
	public static boolean arrayMatches(int[] a, int aOffset, int[] b, int bOffset, int length) throws IndexOutOfBoundsException
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = SmallIntegerMathUtilities.progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = SmallIntegerMathUtilities.progmod(bOffset, b.length);
		
		if (aOffset+length > a.length)
			throw new IndexOutOfBoundsException();
		if (bOffset+length > b.length)
			throw new IndexOutOfBoundsException();
		
		for (int i = 0; i < length; i++)
			if (a[aOffset+i] != b[bOffset+i])
				return false;
		return true;
	}
	
	public static boolean arrayMatches(double[] a, int aOffset, double[] b, int bOffset, int length) throws IndexOutOfBoundsException
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = SmallIntegerMathUtilities.progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = SmallIntegerMathUtilities.progmod(bOffset, b.length);
		
		if (aOffset+length > a.length)
			throw new IndexOutOfBoundsException();
		if (bOffset+length > b.length)
			throw new IndexOutOfBoundsException();
		
		for (int i = 0; i < length; i++)
			if (a[aOffset+i] != b[bOffset+i])
				return false;
		return true;
	}
	
	public static boolean arrayMatches(long[] a, int aOffset, long[] b, int bOffset, int length) throws IndexOutOfBoundsException
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = SmallIntegerMathUtilities.progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = SmallIntegerMathUtilities.progmod(bOffset, b.length);
		
		if (aOffset+length > a.length)
			throw new IndexOutOfBoundsException();
		if (bOffset+length > b.length)
			throw new IndexOutOfBoundsException();
		
		for (int i = 0; i < length; i++)
			if (a[aOffset+i] != b[bOffset+i])
				return false;
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static byte[] splitElements16to8LE(short[] source, int offset, int length)
	{
		byte[] dest = new byte[length*2];
		for (int i = 0; i < length; i++)
			Bytes.putLittleShort(dest, i*2, source[offset+i]);
		return dest;
	}
	
	public static byte[] splitElements16to8BE(short[] source, int offset, int length)
	{
		byte[] dest = new byte[length*2];
		for (int i = 0; i < length; i++)
			Bytes.putBigShort(dest, i*2, source[offset+i]);
		return dest;
	}
	
	
	
	
	public static byte[] splitElements32to8LE(int[] source, int offset, int length)
	{
		byte[] dest = new byte[length*4];
		for (int i = 0; i < length; i++)
			Bytes.putLittleInt(dest, i*4, source[offset+i]);
		return dest;
	}
	
	public static byte[] splitElements32to8BE(int[] source, int offset, int length)
	{
		byte[] dest = new byte[length*4];
		for (int i = 0; i < length; i++)
			Bytes.putBigInt(dest, i*4, source[offset+i]);
		return dest;
	}
	
	
	
	
	
	
	
	
	
	
	public static short[] mergeElements8to16LE(byte[] source, int offset, int length)
	{
		if ((length % 2) != 0)
			throw new IllegalArgumentException("Only multiples of 2 bytes (8 bits) can be merged into ints (16 bits)!");
		
		int nout = length / 2;
		
		
		short[] dest = new short[nout];
		for (int i = 0; i < nout; i++)
			dest[i] = Bytes.getLittleShort(source, offset + (i * 2));
		return dest;
	}
	
	public static short[] mergeElements8to16BE(byte[] source, int offset, int length)
	{
		if ((length % 2) != 0)
			throw new IllegalArgumentException("Only multiples of 2 bytes (8 bits) can be merged into ints (16 bits)!");
		
		int nout = length / 2;
		
		
		short[] dest = new short[nout];
		for (int i = 0; i < nout; i++)
			dest[i] = Bytes.getBigShort(source, offset + (i * 2));
		return dest;
	}
	
	
	
	
	public static int[] mergeElements8to32LE(byte[] source, int offset, int length)
	{
		if ((length % 4) != 0)
			throw new IllegalArgumentException("Only multiples of 4 bytes (8 bits) can be merged into ints (32 bits)!");
		
		int nout = length / 4;
		
		
		int[] dest = new int[nout];
		for (int i = 0; i < nout; i++)
			dest[i] = Bytes.getLittleInt(source, offset + (i * 4));
		return dest;
	}
	
	public static int[] mergeElements8to32BE(byte[] source, int offset, int length)
	{
		if ((length % 4) != 0)
			throw new IllegalArgumentException("Only multiples of 4 bytes (8 bits) can be merged into ints (32 bits)!");
		
		int nout = length / 4;
		
		
		int[] dest = new int[nout];
		for (int i = 0; i < nout; i++)
			dest[i] = Bytes.getBigInt(source, offset + (i * 4));
		return dest;
	}
	
	
	
	
	
	
	
	public static int[] mergeElements16to32LE(short[] source, int offset, int length)
	{
		if ((length % 2) != 0)
			throw new IllegalArgumentException("Only multiples of 4 bytes (8 bits) can be merged into ints (32 bits)!");
		
		int nout = length / 2;
		
		
		int[] dest = new int[nout];
		for (int i = 0; i < nout; i++)
		{
			int v = 0;
			int o = offset + (i << 1);
			v |= source[o] & 0x0000FFFF; o++;
			v |= source[o] << 16;
			dest[i] = v;
		}
		return dest;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static byte[] splitElements16to8LE(short[] source)
	{
		return splitElements16to8LE(source, 0, source.length);
	}
	
	public static byte[] splitElements16to8BE(short[] source)
	{
		return splitElements16to8BE(source, 0, source.length);
	}
	
	public static byte[] splitElements32to8LE(int[] source)
	{
		return splitElements32to8LE(source, 0, source.length);
	}
	
	public static byte[] splitElements32to8BE(int[] source)
	{
		return splitElements32to8BE(source, 0, source.length);
	}
	
	public static short[] mergeElements8to16LE(byte[] source)
	{
		return mergeElements8to16LE(source, 0, source.length);
	}
	
	public static short[] mergeElements8to16BE(byte[] source)
	{
		return mergeElements8to16BE(source, 0, source.length);
	}
	
	public static int[] mergeElements8to32LE(byte[] source)
	{
		return mergeElements8to32LE(source, 0, source.length);
	}
	
	public static int[] mergeElements8to32BE(byte[] source)
	{
		return mergeElements8to32BE(source, 0, source.length);
	}
	
	public static int[] mergeElements16to32LE(short[] source)
	{
		return mergeElements16to32LE(source, 0, source.length);
	}
	
	
	
	
	
	
	public static byte[] splitElements16to8LE(Slice<short[]> source)
	{
		return splitElements16to8LE(source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public static byte[] splitElements16to8BE(Slice<short[]> source)
	{
		return splitElements16to8BE(source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public static byte[] splitElements32to8LE(Slice<int[]> source)
	{
		return splitElements32to8LE(source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public static byte[] splitElements32to8BE(Slice<int[]> source)
	{
		return splitElements32to8BE(source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public static short[] mergeElements8to16LE(Slice<byte[]> source)
	{
		return mergeElements8to16LE(source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public static short[] mergeElements8to16BE(Slice<byte[]> source)
	{
		return mergeElements8to16BE(source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public static int[] mergeElements8to32LE(Slice<byte[]> source)
	{
		return mergeElements8to32LE(source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public static int[] mergeElements8to32BE(Slice<byte[]> source)
	{
		return mergeElements8to32BE(source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public static int[] mergeElements16to32LE(Slice<short[]> source)
	{
		return mergeElements16to32LE(source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int[] mergeElementsBytesToIntsLE(ByteBuffer source, int offset, int length)
	{
		if ((length % 4) != 0)
			throw new IllegalArgumentException("Only multiples of 4 bytes (8 bits) can be merged into ints (32 bits)!");
		
		int nout = length / 4;
		
		
		int[] dest = new int[nout];
		for (int i = 0; i < nout; i++)
			dest[i] = Bytes.getLittleInt(source, offset + (i << 2));
		return dest;
	}
	
	
	public static int[] mergeElementsBytesToIntsLE(ByteBuffer source)
	{
		return mergeElementsBytesToIntsLE(source, 0, source.capacity());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static <E> int indexOf(E[] array, E value, EqualityComparator<E> equalityComparator)
	{
		if (array == null)
			return -1;
		for (int i = 0; i < array.length; i++)
			if (equalityComparator.equals(value, array[i]))
				return i;
		return -1;
	}
	
	public static <E> int indexOf(E[] array, int subArrayStart, E value, EqualityComparator<E> equalityComparator)
	{
		if (array == null) return -1;
		for (int i = subArrayStart; i < array.length; i++)
			if (equalityComparator.equals(value, array[i]))
				return i;
		return -1;
	}
	
	public static <E> int indexOf(E[] array, int subArrayStart, int subArrayLength, E value, EqualityComparator<E> equalityComparator)
	{
		if (array == null) return -1;
		if (subArrayLength < 0) throw new IndexOutOfBoundsException("negative length! D:");
		int subArrayEnd = subArrayStart+subArrayLength;
		for (int i = subArrayStart; i < subArrayEnd; i++)
			if (equalityComparator.equals(value, array[i]))
				return i;
		return -1;
	}
	
	
	public static <E> int indexOf(E[] array, E value)
	{
		return indexOf(array, value, getNaturalEqualityComparator());
	}
	
	public static <E> int indexOf(E[] array, int subArrayStart, E value)
	{
		return indexOf(array, subArrayStart, value, getNaturalEqualityComparator());
	}
	
	public static <E> int indexOf(E[] array, int subArrayStart, int subArrayLength, E value)
	{
		return indexOf(array, subArrayStart, subArrayLength, value, getNaturalEqualityComparator());
	}
	
	
	
	public static <E> boolean contains(E[] array, E value, EqualityComparator<E> equalityComparator)
	{
		if (array == null)
			return false;
		for (int i = 0; i < array.length; i++)
			if (equalityComparator.equals(value, array[i]))
				return true;
		return false;
	}
	
	public static <E> boolean contains(E[] array, int subArrayStart, E value, EqualityComparator<E> equalityComparator)
	{
		if (array == null) return false;
		for (int i = subArrayStart; i < array.length; i++)
			if (equalityComparator.equals(value, array[i]))
				return true;
		return false;
	}
	
	public static <E> boolean contains(E[] array, int subArrayStart, int subArrayLength, E value, EqualityComparator<E> equalityComparator)
	{
		if (array == null) return false;
		if (subArrayLength < 0) throw new IndexOutOfBoundsException("negative length! D:");
		int subArrayEnd = subArrayStart+subArrayLength;
		for (int i = subArrayStart; i < subArrayEnd; i++)
			if (equalityComparator.equals(value, array[i]))
				return true;
		return false;
	}
	
	
	
	public static <E> boolean contains(E[] array, E value)
	{
		return contains(array, value, getNaturalEqualityComparator());
	}
	
	public static <E> boolean contains(E[] array, int subArrayStart, E value)
	{
		return contains(array, subArrayStart, value, getNaturalEqualityComparator());
	}
	
	public static <E> boolean contains(E[] array, int subArrayStart, int subArrayLength, E value)
	{
		return contains(array, subArrayStart, subArrayLength, value, getNaturalEqualityComparator());
	}
	
	
	
	
	
	
	
	
	
	
	/* <<<
	primxp
	
	public static int indexOf(_$$prim$$_[] array, _$$prim$$_ value)
	{
		if (array == null)
			return -1;
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	public static int indexOf(_$$prim$$_[] array, int subArrayStart, _$$prim$$_ value)
	{
		if (array == null) return -1;
		for (int i = subArrayStart; i < array.length; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	public static int indexOf(_$$prim$$_[] array, int subArrayStart, int subArrayLength, _$$prim$$_ value)
	{
		if (array == null) return -1;
		if (subArrayLength < 0) throw new IndexOutOfBoundsException("negative length! D:");
		int subArrayEnd = subArrayStart+subArrayLength;
		for (int i = subArrayStart; i < subArrayEnd; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	
	public static boolean contains(_$$prim$$_[] array, _$$prim$$_ value)
	{
		if (array == null)
			return false;
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	public static boolean contains(_$$prim$$_[] array, int subArrayStart, _$$prim$$_ value)
	{
		if (array == null) return false;
		for (int i = subArrayStart; i < array.length; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	public static boolean contains(_$$prim$$_[] array, int subArrayStart, int subArrayLength, _$$prim$$_ value)
	{
		if (array == null) return false;
		if (subArrayLength < 0) throw new IndexOutOfBoundsException("negative length! D:");
		int subArrayEnd = subArrayStart+subArrayLength;
		for (int i = subArrayStart; i < subArrayEnd; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	
	
	
	
	
	
	public static int count(UnaryFunction_$$Prim$$_ToBoolean predicate, _$$prim$$_[] array)
	{
		return count(predicate, array, 0, array.length);
	}
	
	public static int count(_$$prim$$_ value, _$$prim$$_[] array)
	{
		return count(value, array, 0, array.length);
	}
	
	
	public static int count(UnaryFunction_$$Prim$$_ToBoolean predicate, _$$prim$$_[] array, int offset, int length)
	{
		int e = length + offset;
		int count = 0;
		for (int i = offset; i < e; i++)
			if (predicate.f(array[i]))
				count++;
		return count;
	}
	
	public static int count(_$$prim$$_ value, _$$prim$$_[] array, int offset, int length)
	{
		int e = length + offset;
		int count = 0;
		for (int i = offset; i < e; i++)
			if (array[i] == value)
				count++;
		return count;
	}
	
	
	
	
	
	 */
	
	public static int indexOf(boolean[] array, boolean value)
	{
		if (array == null)
			return -1;
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	public static int indexOf(boolean[] array, int subArrayStart, boolean value)
	{
		if (array == null) return -1;
		for (int i = subArrayStart; i < array.length; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	public static int indexOf(boolean[] array, int subArrayStart, int subArrayLength, boolean value)
	{
		if (array == null) return -1;
		if (subArrayLength < 0) throw new IndexOutOfBoundsException("negative length! D:");
		int subArrayEnd = subArrayStart+subArrayLength;
		for (int i = subArrayStart; i < subArrayEnd; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	
	public static boolean contains(boolean[] array, boolean value)
	{
		if (array == null)
			return false;
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	public static boolean contains(boolean[] array, int subArrayStart, boolean value)
	{
		if (array == null) return false;
		for (int i = subArrayStart; i < array.length; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	public static boolean contains(boolean[] array, int subArrayStart, int subArrayLength, boolean value)
	{
		if (array == null) return false;
		if (subArrayLength < 0) throw new IndexOutOfBoundsException("negative length! D:");
		int subArrayEnd = subArrayStart+subArrayLength;
		for (int i = subArrayStart; i < subArrayEnd; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	
	
	
	
	
	
	public static int count(UnaryFunctionBooleanToBoolean predicate, boolean[] array)
	{
		return count(predicate, array, 0, array.length);
	}
	
	public static int count(boolean value, boolean[] array)
	{
		return count(value, array, 0, array.length);
	}
	
	
	public static int count(UnaryFunctionBooleanToBoolean predicate, boolean[] array, int offset, int length)
	{
		int e = length + offset;
		int count = 0;
		for (int i = offset; i < e; i++)
			if (predicate.f(array[i]))
				count++;
		return count;
	}
	
	public static int count(boolean value, boolean[] array, int offset, int length)
	{
		int e = length + offset;
		int count = 0;
		for (int i = offset; i < e; i++)
			if (array[i] == value)
				count++;
		return count;
	}
	
	
	
	
	
	
	public static int indexOf(byte[] array, byte value)
	{
		if (array == null)
			return -1;
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	public static int indexOf(byte[] array, int subArrayStart, byte value)
	{
		if (array == null) return -1;
		for (int i = subArrayStart; i < array.length; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	public static int indexOf(byte[] array, int subArrayStart, int subArrayLength, byte value)
	{
		if (array == null) return -1;
		if (subArrayLength < 0) throw new IndexOutOfBoundsException("negative length! D:");
		int subArrayEnd = subArrayStart+subArrayLength;
		for (int i = subArrayStart; i < subArrayEnd; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	
	public static boolean contains(byte[] array, byte value)
	{
		if (array == null)
			return false;
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	public static boolean contains(byte[] array, int subArrayStart, byte value)
	{
		if (array == null) return false;
		for (int i = subArrayStart; i < array.length; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	public static boolean contains(byte[] array, int subArrayStart, int subArrayLength, byte value)
	{
		if (array == null) return false;
		if (subArrayLength < 0) throw new IndexOutOfBoundsException("negative length! D:");
		int subArrayEnd = subArrayStart+subArrayLength;
		for (int i = subArrayStart; i < subArrayEnd; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	
	
	
	
	
	
	public static int count(UnaryFunctionByteToBoolean predicate, byte[] array)
	{
		return count(predicate, array, 0, array.length);
	}
	
	public static int count(byte value, byte[] array)
	{
		return count(value, array, 0, array.length);
	}
	
	
	public static int count(UnaryFunctionByteToBoolean predicate, byte[] array, int offset, int length)
	{
		int e = length + offset;
		int count = 0;
		for (int i = offset; i < e; i++)
			if (predicate.f(array[i]))
				count++;
		return count;
	}
	
	public static int count(byte value, byte[] array, int offset, int length)
	{
		int e = length + offset;
		int count = 0;
		for (int i = offset; i < e; i++)
			if (array[i] == value)
				count++;
		return count;
	}
	
	
	
	
	
	
	public static int indexOf(char[] array, char value)
	{
		if (array == null)
			return -1;
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	public static int indexOf(char[] array, int subArrayStart, char value)
	{
		if (array == null) return -1;
		for (int i = subArrayStart; i < array.length; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	public static int indexOf(char[] array, int subArrayStart, int subArrayLength, char value)
	{
		if (array == null) return -1;
		if (subArrayLength < 0) throw new IndexOutOfBoundsException("negative length! D:");
		int subArrayEnd = subArrayStart+subArrayLength;
		for (int i = subArrayStart; i < subArrayEnd; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	
	public static boolean contains(char[] array, char value)
	{
		if (array == null)
			return false;
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	public static boolean contains(char[] array, int subArrayStart, char value)
	{
		if (array == null) return false;
		for (int i = subArrayStart; i < array.length; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	public static boolean contains(char[] array, int subArrayStart, int subArrayLength, char value)
	{
		if (array == null) return false;
		if (subArrayLength < 0) throw new IndexOutOfBoundsException("negative length! D:");
		int subArrayEnd = subArrayStart+subArrayLength;
		for (int i = subArrayStart; i < subArrayEnd; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	
	
	
	
	
	
	public static int count(UnaryFunctionCharToBoolean predicate, char[] array)
	{
		return count(predicate, array, 0, array.length);
	}
	
	public static int count(char value, char[] array)
	{
		return count(value, array, 0, array.length);
	}
	
	
	public static int count(UnaryFunctionCharToBoolean predicate, char[] array, int offset, int length)
	{
		int e = length + offset;
		int count = 0;
		for (int i = offset; i < e; i++)
			if (predicate.f(array[i]))
				count++;
		return count;
	}
	
	public static int count(char value, char[] array, int offset, int length)
	{
		int e = length + offset;
		int count = 0;
		for (int i = offset; i < e; i++)
			if (array[i] == value)
				count++;
		return count;
	}
	
	
	
	
	
	
	public static int indexOf(short[] array, short value)
	{
		if (array == null)
			return -1;
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	public static int indexOf(short[] array, int subArrayStart, short value)
	{
		if (array == null) return -1;
		for (int i = subArrayStart; i < array.length; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	public static int indexOf(short[] array, int subArrayStart, int subArrayLength, short value)
	{
		if (array == null) return -1;
		if (subArrayLength < 0) throw new IndexOutOfBoundsException("negative length! D:");
		int subArrayEnd = subArrayStart+subArrayLength;
		for (int i = subArrayStart; i < subArrayEnd; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	
	public static boolean contains(short[] array, short value)
	{
		if (array == null)
			return false;
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	public static boolean contains(short[] array, int subArrayStart, short value)
	{
		if (array == null) return false;
		for (int i = subArrayStart; i < array.length; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	public static boolean contains(short[] array, int subArrayStart, int subArrayLength, short value)
	{
		if (array == null) return false;
		if (subArrayLength < 0) throw new IndexOutOfBoundsException("negative length! D:");
		int subArrayEnd = subArrayStart+subArrayLength;
		for (int i = subArrayStart; i < subArrayEnd; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	
	
	
	
	
	
	public static int count(UnaryFunctionShortToBoolean predicate, short[] array)
	{
		return count(predicate, array, 0, array.length);
	}
	
	public static int count(short value, short[] array)
	{
		return count(value, array, 0, array.length);
	}
	
	
	public static int count(UnaryFunctionShortToBoolean predicate, short[] array, int offset, int length)
	{
		int e = length + offset;
		int count = 0;
		for (int i = offset; i < e; i++)
			if (predicate.f(array[i]))
				count++;
		return count;
	}
	
	public static int count(short value, short[] array, int offset, int length)
	{
		int e = length + offset;
		int count = 0;
		for (int i = offset; i < e; i++)
			if (array[i] == value)
				count++;
		return count;
	}
	
	
	
	
	
	
	public static int indexOf(float[] array, float value)
	{
		if (array == null)
			return -1;
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	public static int indexOf(float[] array, int subArrayStart, float value)
	{
		if (array == null) return -1;
		for (int i = subArrayStart; i < array.length; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	public static int indexOf(float[] array, int subArrayStart, int subArrayLength, float value)
	{
		if (array == null) return -1;
		if (subArrayLength < 0) throw new IndexOutOfBoundsException("negative length! D:");
		int subArrayEnd = subArrayStart+subArrayLength;
		for (int i = subArrayStart; i < subArrayEnd; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	
	public static boolean contains(float[] array, float value)
	{
		if (array == null)
			return false;
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	public static boolean contains(float[] array, int subArrayStart, float value)
	{
		if (array == null) return false;
		for (int i = subArrayStart; i < array.length; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	public static boolean contains(float[] array, int subArrayStart, int subArrayLength, float value)
	{
		if (array == null) return false;
		if (subArrayLength < 0) throw new IndexOutOfBoundsException("negative length! D:");
		int subArrayEnd = subArrayStart+subArrayLength;
		for (int i = subArrayStart; i < subArrayEnd; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	
	
	
	
	
	
	public static int count(UnaryFunctionFloatToBoolean predicate, float[] array)
	{
		return count(predicate, array, 0, array.length);
	}
	
	public static int count(float value, float[] array)
	{
		return count(value, array, 0, array.length);
	}
	
	
	public static int count(UnaryFunctionFloatToBoolean predicate, float[] array, int offset, int length)
	{
		int e = length + offset;
		int count = 0;
		for (int i = offset; i < e; i++)
			if (predicate.f(array[i]))
				count++;
		return count;
	}
	
	public static int count(float value, float[] array, int offset, int length)
	{
		int e = length + offset;
		int count = 0;
		for (int i = offset; i < e; i++)
			if (array[i] == value)
				count++;
		return count;
	}
	
	
	
	
	
	
	public static int indexOf(int[] array, int value)
	{
		if (array == null)
			return -1;
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	public static int indexOf(int[] array, int subArrayStart, int value)
	{
		if (array == null) return -1;
		for (int i = subArrayStart; i < array.length; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	public static int indexOf(int[] array, int subArrayStart, int subArrayLength, int value)
	{
		if (array == null) return -1;
		if (subArrayLength < 0) throw new IndexOutOfBoundsException("negative length! D:");
		int subArrayEnd = subArrayStart+subArrayLength;
		for (int i = subArrayStart; i < subArrayEnd; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	
	public static boolean contains(int[] array, int value)
	{
		if (array == null)
			return false;
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	public static boolean contains(int[] array, int subArrayStart, int value)
	{
		if (array == null) return false;
		for (int i = subArrayStart; i < array.length; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	public static boolean contains(int[] array, int subArrayStart, int subArrayLength, int value)
	{
		if (array == null) return false;
		if (subArrayLength < 0) throw new IndexOutOfBoundsException("negative length! D:");
		int subArrayEnd = subArrayStart+subArrayLength;
		for (int i = subArrayStart; i < subArrayEnd; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	
	
	
	
	
	
	public static int count(UnaryFunctionIntToBoolean predicate, int[] array)
	{
		return count(predicate, array, 0, array.length);
	}
	
	public static int count(int value, int[] array)
	{
		return count(value, array, 0, array.length);
	}
	
	
	public static int count(UnaryFunctionIntToBoolean predicate, int[] array, int offset, int length)
	{
		int e = length + offset;
		int count = 0;
		for (int i = offset; i < e; i++)
			if (predicate.f(array[i]))
				count++;
		return count;
	}
	
	public static int count(int value, int[] array, int offset, int length)
	{
		int e = length + offset;
		int count = 0;
		for (int i = offset; i < e; i++)
			if (array[i] == value)
				count++;
		return count;
	}
	
	
	
	
	
	
	public static int indexOf(double[] array, double value)
	{
		if (array == null)
			return -1;
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	public static int indexOf(double[] array, int subArrayStart, double value)
	{
		if (array == null) return -1;
		for (int i = subArrayStart; i < array.length; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	public static int indexOf(double[] array, int subArrayStart, int subArrayLength, double value)
	{
		if (array == null) return -1;
		if (subArrayLength < 0) throw new IndexOutOfBoundsException("negative length! D:");
		int subArrayEnd = subArrayStart+subArrayLength;
		for (int i = subArrayStart; i < subArrayEnd; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	
	public static boolean contains(double[] array, double value)
	{
		if (array == null)
			return false;
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	public static boolean contains(double[] array, int subArrayStart, double value)
	{
		if (array == null) return false;
		for (int i = subArrayStart; i < array.length; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	public static boolean contains(double[] array, int subArrayStart, int subArrayLength, double value)
	{
		if (array == null) return false;
		if (subArrayLength < 0) throw new IndexOutOfBoundsException("negative length! D:");
		int subArrayEnd = subArrayStart+subArrayLength;
		for (int i = subArrayStart; i < subArrayEnd; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	
	
	
	
	
	
	public static int count(UnaryFunctionDoubleToBoolean predicate, double[] array)
	{
		return count(predicate, array, 0, array.length);
	}
	
	public static int count(double value, double[] array)
	{
		return count(value, array, 0, array.length);
	}
	
	
	public static int count(UnaryFunctionDoubleToBoolean predicate, double[] array, int offset, int length)
	{
		int e = length + offset;
		int count = 0;
		for (int i = offset; i < e; i++)
			if (predicate.f(array[i]))
				count++;
		return count;
	}
	
	public static int count(double value, double[] array, int offset, int length)
	{
		int e = length + offset;
		int count = 0;
		for (int i = offset; i < e; i++)
			if (array[i] == value)
				count++;
		return count;
	}
	
	
	
	
	
	
	public static int indexOf(long[] array, long value)
	{
		if (array == null)
			return -1;
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	public static int indexOf(long[] array, int subArrayStart, long value)
	{
		if (array == null) return -1;
		for (int i = subArrayStart; i < array.length; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	public static int indexOf(long[] array, int subArrayStart, int subArrayLength, long value)
	{
		if (array == null) return -1;
		if (subArrayLength < 0) throw new IndexOutOfBoundsException("negative length! D:");
		int subArrayEnd = subArrayStart+subArrayLength;
		for (int i = subArrayStart; i < subArrayEnd; i++)
			if (array[i] == value)
				return i;
		return -1;
	}
	
	
	public static boolean contains(long[] array, long value)
	{
		if (array == null)
			return false;
		for (int i = 0; i < array.length; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	public static boolean contains(long[] array, int subArrayStart, long value)
	{
		if (array == null) return false;
		for (int i = subArrayStart; i < array.length; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	public static boolean contains(long[] array, int subArrayStart, int subArrayLength, long value)
	{
		if (array == null) return false;
		if (subArrayLength < 0) throw new IndexOutOfBoundsException("negative length! D:");
		int subArrayEnd = subArrayStart+subArrayLength;
		for (int i = subArrayStart; i < subArrayEnd; i++)
			if (array[i] == value)
				return true;
		return false;
	}
	
	
	
	
	
	
	
	public static int count(UnaryFunctionLongToBoolean predicate, long[] array)
	{
		return count(predicate, array, 0, array.length);
	}
	
	public static int count(long value, long[] array)
	{
		return count(value, array, 0, array.length);
	}
	
	
	public static int count(UnaryFunctionLongToBoolean predicate, long[] array, int offset, int length)
	{
		int e = length + offset;
		int count = 0;
		for (int i = offset; i < e; i++)
			if (predicate.f(array[i]))
				count++;
		return count;
	}
	
	public static int count(long value, long[] array, int offset, int length)
	{
		int e = length + offset;
		int count = 0;
		for (int i = offset; i < e; i++)
			if (array[i] == value)
				count++;
		return count;
	}
	
	
	
	
	
	//>>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Creates a new array without the given element, all the elements to the right shifted down, and with a length 1 less than the original.<br>
	 * @param deletionIndex The element to 'delete'
	 * @return The new array
	 */
	public static <E> E[] pop(E[] array, int deletionIndex)
	{
		if (array.length == 0)
			throw new IllegalArgumentException();
		
		try
		{
			E[] excluser = (E[])Array.newInstance(array.getClass().getComponentType(), array.length-1);
			
			if (deletionIndex > 0)
				System.arraycopy(array, 0, excluser, 0, deletionIndex);
			
			if (deletionIndex < array.length-1)
				System.arraycopy(array, deletionIndex+1, excluser, deletionIndex, array.length-deletionIndex-1);
			
			return excluser;
		}
		catch (NegativeArraySizeException exc)
		{
			throw new AssertionError();
		}
	}
	
	
	
	/**
	 * Clones an array by cloning each element and placing it in the corresponding index of <code>newArray</code>.
	 * @param cloneableArray The source array
	 * @param newArray The destination array; must be the same length as the source array (but not necessarily the same component-type)
	 * @return <code>newArray</code>
	 */
	public static <E> E[] deepClone(PubliclyCloneable[] cloneableArray, E[] newArray)
	{
		for (int i = 0; i < cloneableArray.length; i++)
			newArray[i] = (E)cloneableArray[i].clone();
		return newArray;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<Reversing
	public static void reverse(Object[] array, int offset, int length)
	{
		Object swap = null;
		
		for (int offing = 0; offing < length / 2; offing++)
		{
			swap = array[offset + offing];
			array[offset + offing] = array[offset + length - 1 - offing];
			array[offset + length - 1 - offing] = swap;
		}
	}
	
	public static void reverse(Object[] array)
	{
		reverse(array, 0, array.length);
	}
	
	
	
	
	/* <<<
	primxp

	public static void reverse(_$$prim$$_[] array, int offset, int length)
	{
		_$$prim$$_ swap = _$$primdef$$_;
		
		for (int offing = 0; offing < length / 2; offing++)
		{
			swap = array[offset + offing];
			array[offset + offing] = array[offset + length - 1 - offing];
			array[offset + length - 1 - offing] = swap;
		}
	}

	public static void reverse(_$$prim$$_[] array)
	{
		reverse(array, 0, array.length);
	}


	public static _$$prim$$_[] reversed(_$$prim$$_[] input, int offset, int length)
	{
		_$$prim$$_[] output = new _$$prim$$_[input.length];
		
		for (int offing = 0; offing < length / 2; offing++)
		{
			output[offset + offing] = input[offset + length - 1 - offing];
			output[offset + length - 1 - offing] = input[offset + offing];
		}
		
		return output;
	}

	public static _$$prim$$_[] reversed(_$$prim$$_[] array)
	{
		return reversed(array, 0, array.length);
	}
	 */
	
	public static void reverse(boolean[] array, int offset, int length)
	{
		boolean swap = false;
		
		for (int offing = 0; offing < length / 2; offing++)
		{
			swap = array[offset + offing];
			array[offset + offing] = array[offset + length - 1 - offing];
			array[offset + length - 1 - offing] = swap;
		}
	}
	
	public static void reverse(boolean[] array)
	{
		reverse(array, 0, array.length);
	}
	
	
	public static boolean[] reversed(boolean[] input, int offset, int length)
	{
		boolean[] output = new boolean[input.length];
		
		for (int offing = 0; offing < length / 2; offing++)
		{
			output[offset + offing] = input[offset + length - 1 - offing];
			output[offset + length - 1 - offing] = input[offset + offing];
		}
		
		return output;
	}
	
	public static boolean[] reversed(boolean[] array)
	{
		return reversed(array, 0, array.length);
	}
	
	public static void reverse(byte[] array, int offset, int length)
	{
		byte swap = ((byte)0);
		
		for (int offing = 0; offing < length / 2; offing++)
		{
			swap = array[offset + offing];
			array[offset + offing] = array[offset + length - 1 - offing];
			array[offset + length - 1 - offing] = swap;
		}
	}
	
	public static void reverse(byte[] array)
	{
		reverse(array, 0, array.length);
	}
	
	
	public static byte[] reversed(byte[] input, int offset, int length)
	{
		byte[] output = new byte[input.length];
		
		for (int offing = 0; offing < length / 2; offing++)
		{
			output[offset + offing] = input[offset + length - 1 - offing];
			output[offset + length - 1 - offing] = input[offset + offing];
		}
		
		return output;
	}
	
	public static byte[] reversed(byte[] array)
	{
		return reversed(array, 0, array.length);
	}
	
	public static void reverse(char[] array, int offset, int length)
	{
		char swap = ((char)0);
		
		for (int offing = 0; offing < length / 2; offing++)
		{
			swap = array[offset + offing];
			array[offset + offing] = array[offset + length - 1 - offing];
			array[offset + length - 1 - offing] = swap;
		}
	}
	
	public static void reverse(char[] array)
	{
		reverse(array, 0, array.length);
	}
	
	
	public static char[] reversed(char[] input, int offset, int length)
	{
		char[] output = new char[input.length];
		
		for (int offing = 0; offing < length / 2; offing++)
		{
			output[offset + offing] = input[offset + length - 1 - offing];
			output[offset + length - 1 - offing] = input[offset + offing];
		}
		
		return output;
	}
	
	public static char[] reversed(char[] array)
	{
		return reversed(array, 0, array.length);
	}
	
	public static void reverse(short[] array, int offset, int length)
	{
		short swap = ((short)0);
		
		for (int offing = 0; offing < length / 2; offing++)
		{
			swap = array[offset + offing];
			array[offset + offing] = array[offset + length - 1 - offing];
			array[offset + length - 1 - offing] = swap;
		}
	}
	
	public static void reverse(short[] array)
	{
		reverse(array, 0, array.length);
	}
	
	
	public static short[] reversed(short[] input, int offset, int length)
	{
		short[] output = new short[input.length];
		
		for (int offing = 0; offing < length / 2; offing++)
		{
			output[offset + offing] = input[offset + length - 1 - offing];
			output[offset + length - 1 - offing] = input[offset + offing];
		}
		
		return output;
	}
	
	public static short[] reversed(short[] array)
	{
		return reversed(array, 0, array.length);
	}
	
	public static void reverse(float[] array, int offset, int length)
	{
		float swap = 0.0f;
		
		for (int offing = 0; offing < length / 2; offing++)
		{
			swap = array[offset + offing];
			array[offset + offing] = array[offset + length - 1 - offing];
			array[offset + length - 1 - offing] = swap;
		}
	}
	
	public static void reverse(float[] array)
	{
		reverse(array, 0, array.length);
	}
	
	
	public static float[] reversed(float[] input, int offset, int length)
	{
		float[] output = new float[input.length];
		
		for (int offing = 0; offing < length / 2; offing++)
		{
			output[offset + offing] = input[offset + length - 1 - offing];
			output[offset + length - 1 - offing] = input[offset + offing];
		}
		
		return output;
	}
	
	public static float[] reversed(float[] array)
	{
		return reversed(array, 0, array.length);
	}
	
	public static void reverse(int[] array, int offset, int length)
	{
		int swap = 0;
		
		for (int offing = 0; offing < length / 2; offing++)
		{
			swap = array[offset + offing];
			array[offset + offing] = array[offset + length - 1 - offing];
			array[offset + length - 1 - offing] = swap;
		}
	}
	
	public static void reverse(int[] array)
	{
		reverse(array, 0, array.length);
	}
	
	
	public static int[] reversed(int[] input, int offset, int length)
	{
		int[] output = new int[input.length];
		
		for (int offing = 0; offing < length / 2; offing++)
		{
			output[offset + offing] = input[offset + length - 1 - offing];
			output[offset + length - 1 - offing] = input[offset + offing];
		}
		
		return output;
	}
	
	public static int[] reversed(int[] array)
	{
		return reversed(array, 0, array.length);
	}
	
	public static void reverse(double[] array, int offset, int length)
	{
		double swap = 0.0d;
		
		for (int offing = 0; offing < length / 2; offing++)
		{
			swap = array[offset + offing];
			array[offset + offing] = array[offset + length - 1 - offing];
			array[offset + length - 1 - offing] = swap;
		}
	}
	
	public static void reverse(double[] array)
	{
		reverse(array, 0, array.length);
	}
	
	
	public static double[] reversed(double[] input, int offset, int length)
	{
		double[] output = new double[input.length];
		
		for (int offing = 0; offing < length / 2; offing++)
		{
			output[offset + offing] = input[offset + length - 1 - offing];
			output[offset + length - 1 - offing] = input[offset + offing];
		}
		
		return output;
	}
	
	public static double[] reversed(double[] array)
	{
		return reversed(array, 0, array.length);
	}
	
	public static void reverse(long[] array, int offset, int length)
	{
		long swap = 0l;
		
		for (int offing = 0; offing < length / 2; offing++)
		{
			swap = array[offset + offing];
			array[offset + offing] = array[offset + length - 1 - offing];
			array[offset + length - 1 - offing] = swap;
		}
	}
	
	public static void reverse(long[] array)
	{
		reverse(array, 0, array.length);
	}
	
	
	public static long[] reversed(long[] input, int offset, int length)
	{
		long[] output = new long[input.length];
		
		for (int offing = 0; offing < length / 2; offing++)
		{
			output[offset + offing] = input[offset + length - 1 - offing];
			output[offset + length - 1 - offing] = input[offset + offing];
		}
		
		return output;
	}
	
	public static long[] reversed(long[] array)
	{
		return reversed(array, 0, array.length);
	}
	//>>>
	
	//Reversing>
	
	
	
	
	//<Creation and setting
	//TODO more
	public static boolean[] getPrepopulatedArrayBoolean(int size, boolean value)
	{
		boolean[] a = new boolean[size];
		if (value != false)
			Arrays.fill(a, value);
		return a;
	}
	
	public static long[] getPrepopulatedArrayLong(int size, long value)
	{
		long[] a = new long[size];
		if (value != 0)
			Arrays.fill(a, value);
		return a;
	}
	//Creation and setting>
	
	
	
	
	
	
	//Todo interleave()   (the inverse! XD)
	
	
	
	//Todo-lp one for individually specifiable component types XP
	
	
	/**
	 * Note!  The returned arrays' component types will all be the <i>same</i> component type: that of the interleaved array!
	 */
	public static <E> E[][] deinterleaveToObjectArrays(E[] array, int interleavedBanks)
	{
		if ((array.length % interleavedBanks) != 0)
			throw new NotYetImplementedException("interleaved array (which was length "+array.length+") must be a multiple of number of interleaved banks! (which was "+interleavedBanks+")  :[");
		
		E[][] deinterleaved = (E[][])Array.newInstance(array.getClass().getComponentType(), interleavedBanks, array.length / interleavedBanks);
		
		int currentInterleave = 0;
		for (int i = 0; i < array.length; i++)
		{
			deinterleaved[currentInterleave][i / interleavedBanks] = array[i];
			currentInterleave = (currentInterleave + 1) % interleavedBanks;
		}
		
		return deinterleaved;
	}
	
	
	/*
	public static _$$prim$$_[][] deinterleave(_$$prim$$_[] array, int interleavedBanks)
	{
		if ((array.length % interleavedBanks) != 0)
			throw new NotYetImplementedException("interleaved array (which was length "+array.length+") must be a multiple of number of interleaved banks! (which was "+interleavedBanks+")  :[");
		
		_$$prim$$_[][] deinterleaved = new _$$prim$$_[interleavedBanks][array.length / interleavedBanks];
		
		int currentInterleave = 0;
		for (int i = 0; i < array.length; i++)
		{
			deinterleaved[currentInterleave][i / interleavedBanks] = array[i];
			currentInterleave = (currentInterleave + 1) % interleavedBanks;
		}
		
		return deinterleaved;
	}
	
	 */
	public static boolean[][] deinterleave(boolean[] array, int interleavedBanks)
	{
		if ((array.length % interleavedBanks) != 0)
			throw new NotYetImplementedException("interleaved array (which was length "+array.length+") must be a multiple of number of interleaved banks! (which was "+interleavedBanks+")  :[");
		
		boolean[][] deinterleaved = new boolean[interleavedBanks][array.length / interleavedBanks];
		
		int currentInterleave = 0;
		for (int i = 0; i < array.length; i++)
		{
			deinterleaved[currentInterleave][i / interleavedBanks] = array[i];
			currentInterleave = (currentInterleave + 1) % interleavedBanks;
		}
		
		return deinterleaved;
	}
	
	public static byte[][] deinterleave(byte[] array, int interleavedBanks)
	{
		if ((array.length % interleavedBanks) != 0)
			throw new NotYetImplementedException("interleaved array (which was length "+array.length+") must be a multiple of number of interleaved banks! (which was "+interleavedBanks+")  :[");
		
		byte[][] deinterleaved = new byte[interleavedBanks][array.length / interleavedBanks];
		
		int currentInterleave = 0;
		for (int i = 0; i < array.length; i++)
		{
			deinterleaved[currentInterleave][i / interleavedBanks] = array[i];
			currentInterleave = (currentInterleave + 1) % interleavedBanks;
		}
		
		return deinterleaved;
	}
	
	public static char[][] deinterleave(char[] array, int interleavedBanks)
	{
		if ((array.length % interleavedBanks) != 0)
			throw new NotYetImplementedException("interleaved array (which was length "+array.length+") must be a multiple of number of interleaved banks! (which was "+interleavedBanks+")  :[");
		
		char[][] deinterleaved = new char[interleavedBanks][array.length / interleavedBanks];
		
		int currentInterleave = 0;
		for (int i = 0; i < array.length; i++)
		{
			deinterleaved[currentInterleave][i / interleavedBanks] = array[i];
			currentInterleave = (currentInterleave + 1) % interleavedBanks;
		}
		
		return deinterleaved;
	}
	
	public static short[][] deinterleave(short[] array, int interleavedBanks)
	{
		if ((array.length % interleavedBanks) != 0)
			throw new NotYetImplementedException("interleaved array (which was length "+array.length+") must be a multiple of number of interleaved banks! (which was "+interleavedBanks+")  :[");
		
		short[][] deinterleaved = new short[interleavedBanks][array.length / interleavedBanks];
		
		int currentInterleave = 0;
		for (int i = 0; i < array.length; i++)
		{
			deinterleaved[currentInterleave][i / interleavedBanks] = array[i];
			currentInterleave = (currentInterleave + 1) % interleavedBanks;
		}
		
		return deinterleaved;
	}
	
	public static float[][] deinterleave(float[] array, int interleavedBanks)
	{
		if ((array.length % interleavedBanks) != 0)
			throw new NotYetImplementedException("interleaved array (which was length "+array.length+") must be a multiple of number of interleaved banks! (which was "+interleavedBanks+")  :[");
		
		float[][] deinterleaved = new float[interleavedBanks][array.length / interleavedBanks];
		
		int currentInterleave = 0;
		for (int i = 0; i < array.length; i++)
		{
			deinterleaved[currentInterleave][i / interleavedBanks] = array[i];
			currentInterleave = (currentInterleave + 1) % interleavedBanks;
		}
		
		return deinterleaved;
	}
	
	public static int[][] deinterleave(int[] array, int interleavedBanks)
	{
		if ((array.length % interleavedBanks) != 0)
			throw new NotYetImplementedException("interleaved array (which was length "+array.length+") must be a multiple of number of interleaved banks! (which was "+interleavedBanks+")  :[");
		
		int[][] deinterleaved = new int[interleavedBanks][array.length / interleavedBanks];
		
		int currentInterleave = 0;
		for (int i = 0; i < array.length; i++)
		{
			deinterleaved[currentInterleave][i / interleavedBanks] = array[i];
			currentInterleave = (currentInterleave + 1) % interleavedBanks;
		}
		
		return deinterleaved;
	}
	
	public static double[][] deinterleave(double[] array, int interleavedBanks)
	{
		if ((array.length % interleavedBanks) != 0)
			throw new NotYetImplementedException("interleaved array (which was length "+array.length+") must be a multiple of number of interleaved banks! (which was "+interleavedBanks+")  :[");
		
		double[][] deinterleaved = new double[interleavedBanks][array.length / interleavedBanks];
		
		int currentInterleave = 0;
		for (int i = 0; i < array.length; i++)
		{
			deinterleaved[currentInterleave][i / interleavedBanks] = array[i];
			currentInterleave = (currentInterleave + 1) % interleavedBanks;
		}
		
		return deinterleaved;
	}
	
	public static long[][] deinterleave(long[] array, int interleavedBanks)
	{
		if ((array.length % interleavedBanks) != 0)
			throw new NotYetImplementedException("interleaved array (which was length "+array.length+") must be a multiple of number of interleaved banks! (which was "+interleavedBanks+")  :[");
		
		long[][] deinterleaved = new long[interleavedBanks][array.length / interleavedBanks];
		
		int currentInterleave = 0;
		for (int i = 0; i < array.length; i++)
		{
			deinterleaved[currentInterleave][i / interleavedBanks] = array[i];
			currentInterleave = (currentInterleave + 1) % interleavedBanks;
		}
		
		return deinterleaved;
	}
	
	
	
	
	/* <<<
	primxp
	
	
	
	/**
	 * Example!:
	 * 
	 * 		interleaveGeneral({ {a,b, c,d, e,f}, {0,1,2, 3,4,5, 6,7,8} }, {2,3})  -->   {a,b,0,1,2, c,d,3,4,5, e,f,6,7,8}
	 ⎋a/
	public static _$$prim$$_[] interleaveGeneral(_$$prim$$_[][] banks, int[] fieldSizes)
	{
		int nFields = banks.length;
		if (fieldSizes.length != nFields)  throw new IllegalArgumentException();
		
		if (nFields == 0)
			return Empty_$$Prim$$_Array;
		
		
		
		//Calculate struct size/count and do some vallllidaaation~  :333
		int nStructs;
		int structSize;
		{
			nStructs = 0;  //init to anything, just something XD'
			structSize = 0;
			
			for (int fieldIndex = 0; fieldIndex < nFields; fieldIndex++)
			{
				_$$prim$$_[] bank = banks[fieldIndex];
				int bL = bank.length;
				
				int fieldSize = fieldSizes[fieldIndex];
				
				
				if (fieldIndex == 0)
				{
					if ((bL % fieldSize) != 0)
						throw new IllegalArgumentException();
					
					nStructs = bL / fieldSize;
				}
				else
				{
					if (fieldSize * nStructs != bL)
						throw new IllegalArgumentException();
				}
				
				structSize += fieldSize;
			}
		}
		
		
		
		_$$prim$$_[] interleaved = new _$$prim$$_[nStructs * structSize];
		
		//Actually do the copying! \:DD/
		
		
		int fieldOffset = 0;
		
		for (int fieldIndex = 0; fieldIndex < nFields; fieldIndex++)
		{
			_$$prim$$_[] bank = banks[fieldIndex];
			int fieldSize = fieldSizes[fieldIndex];
			
			for (int structIndex = 0; structIndex < nStructs; structIndex++)
			{
				System.arraycopy(bank, structIndex * fieldSize, interleaved, structIndex * structSize + fieldOffset, fieldSize);
			}
			
			fieldOffset += fieldSize;
		}
		
		
		return interleaved;
	}
	
	
	
	
	
	
	 */
	
	
	
	/**
	 * Example!:
	 * 
	 * 		interleaveGeneral({ {a,b, c,d, e,f}, {0,1,2, 3,4,5, 6,7,8} }, {2,3})  -->   {a,b,0,1,2, c,d,3,4,5, e,f,6,7,8}
	 */
	public static boolean[] interleaveGeneral(boolean[][] banks, int[] fieldSizes)
	{
		int nFields = banks.length;
		if (fieldSizes.length != nFields)  throw new IllegalArgumentException();
		
		if (nFields == 0)
			return EmptyBooleanArray;
		
		
		
		//Calculate struct size/count and do some vallllidaaation~  :333
		int nStructs;
		int structSize;
		{
			nStructs = 0;  //init to anything, just something XD'
			structSize = 0;
			
			for (int fieldIndex = 0; fieldIndex < nFields; fieldIndex++)
			{
				boolean[] bank = banks[fieldIndex];
				int bL = bank.length;
				
				int fieldSize = fieldSizes[fieldIndex];
				
				
				if (fieldIndex == 0)
				{
					if ((bL % fieldSize) != 0)
						throw new IllegalArgumentException();
					
					nStructs = bL / fieldSize;
				}
				else
				{
					if (fieldSize * nStructs != bL)
						throw new IllegalArgumentException();
				}
				
				structSize += fieldSize;
			}
		}
		
		
		
		boolean[] interleaved = new boolean[nStructs * structSize];
		
		//Actually do the copying! \:DD/
		
		
		int fieldOffset = 0;
		
		for (int fieldIndex = 0; fieldIndex < nFields; fieldIndex++)
		{
			boolean[] bank = banks[fieldIndex];
			int fieldSize = fieldSizes[fieldIndex];
			
			for (int structIndex = 0; structIndex < nStructs; structIndex++)
			{
				System.arraycopy(bank, structIndex * fieldSize, interleaved, structIndex * structSize + fieldOffset, fieldSize);
			}
			
			fieldOffset += fieldSize;
		}
		
		
		return interleaved;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Example!:
	 * 
	 * 		interleaveGeneral({ {a,b, c,d, e,f}, {0,1,2, 3,4,5, 6,7,8} }, {2,3})  -->   {a,b,0,1,2, c,d,3,4,5, e,f,6,7,8}
	 */
	public static byte[] interleaveGeneral(byte[][] banks, int[] fieldSizes)
	{
		int nFields = banks.length;
		if (fieldSizes.length != nFields)  throw new IllegalArgumentException();
		
		if (nFields == 0)
			return EmptyByteArray;
		
		
		
		//Calculate struct size/count and do some vallllidaaation~  :333
		int nStructs;
		int structSize;
		{
			nStructs = 0;  //init to anything, just something XD'
			structSize = 0;
			
			for (int fieldIndex = 0; fieldIndex < nFields; fieldIndex++)
			{
				byte[] bank = banks[fieldIndex];
				int bL = bank.length;
				
				int fieldSize = fieldSizes[fieldIndex];
				
				
				if (fieldIndex == 0)
				{
					if ((bL % fieldSize) != 0)
						throw new IllegalArgumentException();
					
					nStructs = bL / fieldSize;
				}
				else
				{
					if (fieldSize * nStructs != bL)
						throw new IllegalArgumentException();
				}
				
				structSize += fieldSize;
			}
		}
		
		
		
		byte[] interleaved = new byte[nStructs * structSize];
		
		//Actually do the copying! \:DD/
		
		
		int fieldOffset = 0;
		
		for (int fieldIndex = 0; fieldIndex < nFields; fieldIndex++)
		{
			byte[] bank = banks[fieldIndex];
			int fieldSize = fieldSizes[fieldIndex];
			
			for (int structIndex = 0; structIndex < nStructs; structIndex++)
			{
				System.arraycopy(bank, structIndex * fieldSize, interleaved, structIndex * structSize + fieldOffset, fieldSize);
			}
			
			fieldOffset += fieldSize;
		}
		
		
		return interleaved;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Example!:
	 * 
	 * 		interleaveGeneral({ {a,b, c,d, e,f}, {0,1,2, 3,4,5, 6,7,8} }, {2,3})  -->   {a,b,0,1,2, c,d,3,4,5, e,f,6,7,8}
	 */
	public static char[] interleaveGeneral(char[][] banks, int[] fieldSizes)
	{
		int nFields = banks.length;
		if (fieldSizes.length != nFields)  throw new IllegalArgumentException();
		
		if (nFields == 0)
			return EmptyCharArray;
		
		
		
		//Calculate struct size/count and do some vallllidaaation~  :333
		int nStructs;
		int structSize;
		{
			nStructs = 0;  //init to anything, just something XD'
			structSize = 0;
			
			for (int fieldIndex = 0; fieldIndex < nFields; fieldIndex++)
			{
				char[] bank = banks[fieldIndex];
				int bL = bank.length;
				
				int fieldSize = fieldSizes[fieldIndex];
				
				
				if (fieldIndex == 0)
				{
					if ((bL % fieldSize) != 0)
						throw new IllegalArgumentException();
					
					nStructs = bL / fieldSize;
				}
				else
				{
					if (fieldSize * nStructs != bL)
						throw new IllegalArgumentException();
				}
				
				structSize += fieldSize;
			}
		}
		
		
		
		char[] interleaved = new char[nStructs * structSize];
		
		//Actually do the copying! \:DD/
		
		
		int fieldOffset = 0;
		
		for (int fieldIndex = 0; fieldIndex < nFields; fieldIndex++)
		{
			char[] bank = banks[fieldIndex];
			int fieldSize = fieldSizes[fieldIndex];
			
			for (int structIndex = 0; structIndex < nStructs; structIndex++)
			{
				System.arraycopy(bank, structIndex * fieldSize, interleaved, structIndex * structSize + fieldOffset, fieldSize);
			}
			
			fieldOffset += fieldSize;
		}
		
		
		return interleaved;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Example!:
	 * 
	 * 		interleaveGeneral({ {a,b, c,d, e,f}, {0,1,2, 3,4,5, 6,7,8} }, {2,3})  -->   {a,b,0,1,2, c,d,3,4,5, e,f,6,7,8}
	 */
	public static short[] interleaveGeneral(short[][] banks, int[] fieldSizes)
	{
		int nFields = banks.length;
		if (fieldSizes.length != nFields)  throw new IllegalArgumentException();
		
		if (nFields == 0)
			return EmptyShortArray;
		
		
		
		//Calculate struct size/count and do some vallllidaaation~  :333
		int nStructs;
		int structSize;
		{
			nStructs = 0;  //init to anything, just something XD'
			structSize = 0;
			
			for (int fieldIndex = 0; fieldIndex < nFields; fieldIndex++)
			{
				short[] bank = banks[fieldIndex];
				int bL = bank.length;
				
				int fieldSize = fieldSizes[fieldIndex];
				
				
				if (fieldIndex == 0)
				{
					if ((bL % fieldSize) != 0)
						throw new IllegalArgumentException();
					
					nStructs = bL / fieldSize;
				}
				else
				{
					if (fieldSize * nStructs != bL)
						throw new IllegalArgumentException();
				}
				
				structSize += fieldSize;
			}
		}
		
		
		
		short[] interleaved = new short[nStructs * structSize];
		
		//Actually do the copying! \:DD/
		
		
		int fieldOffset = 0;
		
		for (int fieldIndex = 0; fieldIndex < nFields; fieldIndex++)
		{
			short[] bank = banks[fieldIndex];
			int fieldSize = fieldSizes[fieldIndex];
			
			for (int structIndex = 0; structIndex < nStructs; structIndex++)
			{
				System.arraycopy(bank, structIndex * fieldSize, interleaved, structIndex * structSize + fieldOffset, fieldSize);
			}
			
			fieldOffset += fieldSize;
		}
		
		
		return interleaved;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Example!:
	 * 
	 * 		interleaveGeneral({ {a,b, c,d, e,f}, {0,1,2, 3,4,5, 6,7,8} }, {2,3})  -->   {a,b,0,1,2, c,d,3,4,5, e,f,6,7,8}
	 */
	public static float[] interleaveGeneral(float[][] banks, int[] fieldSizes)
	{
		int nFields = banks.length;
		if (fieldSizes.length != nFields)  throw new IllegalArgumentException();
		
		if (nFields == 0)
			return EmptyFloatArray;
		
		
		
		//Calculate struct size/count and do some vallllidaaation~  :333
		int nStructs;
		int structSize;
		{
			nStructs = 0;  //init to anything, just something XD'
			structSize = 0;
			
			for (int fieldIndex = 0; fieldIndex < nFields; fieldIndex++)
			{
				float[] bank = banks[fieldIndex];
				int bL = bank.length;
				
				int fieldSize = fieldSizes[fieldIndex];
				
				
				if (fieldIndex == 0)
				{
					if ((bL % fieldSize) != 0)
						throw new IllegalArgumentException();
					
					nStructs = bL / fieldSize;
				}
				else
				{
					if (fieldSize * nStructs != bL)
						throw new IllegalArgumentException();
				}
				
				structSize += fieldSize;
			}
		}
		
		
		
		float[] interleaved = new float[nStructs * structSize];
		
		//Actually do the copying! \:DD/
		
		
		int fieldOffset = 0;
		
		for (int fieldIndex = 0; fieldIndex < nFields; fieldIndex++)
		{
			float[] bank = banks[fieldIndex];
			int fieldSize = fieldSizes[fieldIndex];
			
			for (int structIndex = 0; structIndex < nStructs; structIndex++)
			{
				System.arraycopy(bank, structIndex * fieldSize, interleaved, structIndex * structSize + fieldOffset, fieldSize);
			}
			
			fieldOffset += fieldSize;
		}
		
		
		return interleaved;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Example!:
	 * 
	 * 		interleaveGeneral({ {a,b, c,d, e,f}, {0,1,2, 3,4,5, 6,7,8} }, {2,3})  -->   {a,b,0,1,2, c,d,3,4,5, e,f,6,7,8}
	 */
	public static int[] interleaveGeneral(int[][] banks, int[] fieldSizes)
	{
		int nFields = banks.length;
		if (fieldSizes.length != nFields)  throw new IllegalArgumentException();
		
		if (nFields == 0)
			return EmptyIntArray;
		
		
		
		//Calculate struct size/count and do some vallllidaaation~  :333
		int nStructs;
		int structSize;
		{
			nStructs = 0;  //init to anything, just something XD'
			structSize = 0;
			
			for (int fieldIndex = 0; fieldIndex < nFields; fieldIndex++)
			{
				int[] bank = banks[fieldIndex];
				int bL = bank.length;
				
				int fieldSize = fieldSizes[fieldIndex];
				
				
				if (fieldIndex == 0)
				{
					if ((bL % fieldSize) != 0)
						throw new IllegalArgumentException();
					
					nStructs = bL / fieldSize;
				}
				else
				{
					if (fieldSize * nStructs != bL)
						throw new IllegalArgumentException();
				}
				
				structSize += fieldSize;
			}
		}
		
		
		
		int[] interleaved = new int[nStructs * structSize];
		
		//Actually do the copying! \:DD/
		
		
		int fieldOffset = 0;
		
		for (int fieldIndex = 0; fieldIndex < nFields; fieldIndex++)
		{
			int[] bank = banks[fieldIndex];
			int fieldSize = fieldSizes[fieldIndex];
			
			for (int structIndex = 0; structIndex < nStructs; structIndex++)
			{
				System.arraycopy(bank, structIndex * fieldSize, interleaved, structIndex * structSize + fieldOffset, fieldSize);
			}
			
			fieldOffset += fieldSize;
		}
		
		
		return interleaved;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Example!:
	 * 
	 * 		interleaveGeneral({ {a,b, c,d, e,f}, {0,1,2, 3,4,5, 6,7,8} }, {2,3})  -->   {a,b,0,1,2, c,d,3,4,5, e,f,6,7,8}
	 */
	public static double[] interleaveGeneral(double[][] banks, int[] fieldSizes)
	{
		int nFields = banks.length;
		if (fieldSizes.length != nFields)  throw new IllegalArgumentException();
		
		if (nFields == 0)
			return EmptyDoubleArray;
		
		
		
		//Calculate struct size/count and do some vallllidaaation~  :333
		int nStructs;
		int structSize;
		{
			nStructs = 0;  //init to anything, just something XD'
			structSize = 0;
			
			for (int fieldIndex = 0; fieldIndex < nFields; fieldIndex++)
			{
				double[] bank = banks[fieldIndex];
				int bL = bank.length;
				
				int fieldSize = fieldSizes[fieldIndex];
				
				
				if (fieldIndex == 0)
				{
					if ((bL % fieldSize) != 0)
						throw new IllegalArgumentException();
					
					nStructs = bL / fieldSize;
				}
				else
				{
					if (fieldSize * nStructs != bL)
						throw new IllegalArgumentException();
				}
				
				structSize += fieldSize;
			}
		}
		
		
		
		double[] interleaved = new double[nStructs * structSize];
		
		//Actually do the copying! \:DD/
		
		
		int fieldOffset = 0;
		
		for (int fieldIndex = 0; fieldIndex < nFields; fieldIndex++)
		{
			double[] bank = banks[fieldIndex];
			int fieldSize = fieldSizes[fieldIndex];
			
			for (int structIndex = 0; structIndex < nStructs; structIndex++)
			{
				System.arraycopy(bank, structIndex * fieldSize, interleaved, structIndex * structSize + fieldOffset, fieldSize);
			}
			
			fieldOffset += fieldSize;
		}
		
		
		return interleaved;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Example!:
	 * 
	 * 		interleaveGeneral({ {a,b, c,d, e,f}, {0,1,2, 3,4,5, 6,7,8} }, {2,3})  -->   {a,b,0,1,2, c,d,3,4,5, e,f,6,7,8}
	 */
	public static long[] interleaveGeneral(long[][] banks, int[] fieldSizes)
	{
		int nFields = banks.length;
		if (fieldSizes.length != nFields)  throw new IllegalArgumentException();
		
		if (nFields == 0)
			return EmptyLongArray;
		
		
		
		//Calculate struct size/count and do some vallllidaaation~  :333
		int nStructs;
		int structSize;
		{
			nStructs = 0;  //init to anything, just something XD'
			structSize = 0;
			
			for (int fieldIndex = 0; fieldIndex < nFields; fieldIndex++)
			{
				long[] bank = banks[fieldIndex];
				int bL = bank.length;
				
				int fieldSize = fieldSizes[fieldIndex];
				
				
				if (fieldIndex == 0)
				{
					if ((bL % fieldSize) != 0)
						throw new IllegalArgumentException();
					
					nStructs = bL / fieldSize;
				}
				else
				{
					if (fieldSize * nStructs != bL)
						throw new IllegalArgumentException();
				}
				
				structSize += fieldSize;
			}
		}
		
		
		
		long[] interleaved = new long[nStructs * structSize];
		
		//Actually do the copying! \:DD/
		
		
		int fieldOffset = 0;
		
		for (int fieldIndex = 0; fieldIndex < nFields; fieldIndex++)
		{
			long[] bank = banks[fieldIndex];
			int fieldSize = fieldSizes[fieldIndex];
			
			for (int structIndex = 0; structIndex < nStructs; structIndex++)
			{
				System.arraycopy(bank, structIndex * fieldSize, interleaved, structIndex * structSize + fieldOffset, fieldSize);
			}
			
			fieldOffset += fieldSize;
		}
		
		
		return interleaved;
	}
	
	
	
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	/* <<<
	primxp
	
	
	/**
	 * Basically an inverse function of {@link #concatArrays(_$$prim$$_[][])}!! \:DD/
	 ⎋a/
	public static _$$prim$$_[][] splitPartitions(_$$prim$$_[] concattedFlatArray, int partitionSize)
	{
		return splitPartitions(concattedFlatArray, partitionSize, true);
	}
	
	public static _$$prim$$_[][] splitPartitionsLenient(_$$prim$$_[] concattedFlatArray, int partitionSize)
	{
		return splitPartitions(concattedFlatArray, partitionSize, false);
	}
	
	public static Slice<_$$prim$$_[]>[] splitPartitionsLiveSlices_$$Prim$$_(Slice<_$$prim$$_[]> concattedFlatArraySlice, int partitionSize)
	{
		return splitPartitionsLiveSlices_$$Prim$$_(concattedFlatArraySlice, partitionSize, true);
	}
	
	
	
	
	public static _$$prim$$_[][] splitPartitions(_$$prim$$_[] concattedFlatArray, int partitionSize, boolean requireExact)
	{
		return splitPartitions_$$Prim$$_(concattedFlatArray, 0, concattedFlatArray.length, partitionSize, requireExact);
	}
	
	public static Slice<_$$prim$$_[]>[] splitPartitionsLiveSlices_$$Prim$$_(_$$prim$$_[] concattedFlatArray, int partitionSize, boolean requireExact)
	{
		return splitPartitionsLiveSlices_$$Prim$$_(concattedFlatArray, 0, concattedFlatArray.length, partitionSize, requireExact);
	}
	
	public static _$$prim$$_[][] splitPartitions_$$Prim$$_(Slice<_$$prim$$_[]> concattedFlatArraySlice, int partitionSize, boolean requireExact)
	{
		return splitPartitions_$$Prim$$_(concattedFlatArraySlice.getUnderlying(), concattedFlatArraySlice.getOffset(), concattedFlatArraySlice.getLength(), partitionSize, requireExact);
	}
	
	public static Slice<_$$prim$$_[]>[] splitPartitionsLiveSlices_$$Prim$$_(Slice<_$$prim$$_[]> concattedFlatArraySlice, int partitionSize, boolean requireExact)
	{
		return splitPartitionsLiveSlices_$$Prim$$_(concattedFlatArraySlice.getUnderlying(), concattedFlatArraySlice.getOffset(), concattedFlatArraySlice.getLength(), partitionSize, requireExact);
	}
	
	
	
	
	public static _$$prim$$_[][] splitPartitions_$$Prim$$_(_$$prim$$_[] concattedFlatArray, int concattedFlatArrayOffset, int concattedFlatArrayLength, int partitionSize, boolean requireExact)
	{
		if (partitionSize < 1)
			throw new IllegalArgumentException();
		
		int nElements = concattedFlatArrayLength;
		
		if (requireExact && (nElements % partitionSize) != 0)
			throw new IllegalArgumentException("Tried to split an array "+nElements+" elements long into equal partitions each "+partitionSize+" elements long!!");
		
		int nPartitions = ceilingDivision(nElements, partitionSize);  //ceiling division is important only if it happend to be inexact!  (which *may* be the case if requireExact is not set!)
		
		_$$prim$$_[][] partitions = new _$$prim$$_[nPartitions][];
		
		int baseInFlatArray = 0;
		for (int partitionIndex = 0; partitionIndex < nPartitions; partitionIndex++)
		{
			// baseInFlatArray == partitionIndex * partitionSize
			
			int partitionSizeThisIteration = least(partitionSize, nElements - baseInFlatArray);
			
			_$$prim$$_[] partition = new _$$prim$$_[partitionSizeThisIteration];
			
			System.arraycopy(concattedFlatArray, concattedFlatArrayOffset + baseInFlatArray, partition, 0, partitionSizeThisIteration);
			//	for (int elementIndexInPartition = 0; elementIndexInPartition < partitionSize; elementIndexInPartition++)
			//		partition[elementIndexInPartition] = concattedFlatArray[concattedFlatArrayOffset + baseInFlatArray+elementIndexInPartition];
			
			partitions[partitionIndex] = partition;
			
			baseInFlatArray += partitionSize;
		}
		
		return partitions;
	}
	
	public static Slice<_$$prim$$_[]>[] splitPartitionsLiveSlices_$$Prim$$_(_$$prim$$_[] concattedFlatArray, int concattedFlatArrayOffset, int concattedFlatArrayLength, int partitionSize, boolean requireExact)
	{
		if (partitionSize < 1)
			throw new IllegalArgumentException();
		
		int nElements = concattedFlatArrayLength;
		
		if (requireExact && (nElements % partitionSize) != 0)
			throw new IllegalArgumentException("Tried to split an array "+nElements+" elements long into equal partitions each "+partitionSize+" elements long!!");
		
		int nPartitions = ceilingDivision(nElements, partitionSize);  //ceiling division is important only if it happend to be inexact!  (which *may* be the case if requireExact is not set!)
		
		Slice<_$$prim$$_[]>[] partitions = new Slice[nPartitions];
		
		int baseInFlatArray = 0;
		for (int partitionIndex = 0; partitionIndex < nPartitions; partitionIndex++)
		{
			// baseInFlatArray == partitionIndex * partitionSize
			
			int partitionSizeThisIteration = least(partitionSize, nElements - baseInFlatArray);
			
			Slice<_$$prim$$_[]> partition = new Slice<>(concattedFlatArray, concattedFlatArrayOffset + baseInFlatArray, partitionSizeThisIteration);
			
			partitions[partitionIndex] = partition;
			
			baseInFlatArray += partitionSize;
		}
		
		return partitions;
	}
	
	
	
	
	
	
	public static _$$prim$$_[] concatArrays(_$$prim$$_[]... arrays)
	{
		int resultingLength = 0;
		{
			for (_$$prim$$_[] a : arrays)
			{
				resultingLength += a != null ? a.length : 0;
			}
		}
		
		
		_$$prim$$_[] newArray = new _$$prim$$_[resultingLength];
		
		
		int cursor = 0;
		
		for (_$$prim$$_[] a : arrays)
		{
			if (a != null)
			{
				int l = a.length;
				if (l != 0)
				{
					System.arraycopy(a, 0, newArray, cursor, l);
				}
			}
			
			cursor += a != null ? a.length : 0;
		}
		
		
		return newArray;
	}
	
	
	
	public static _$$prim$$_[] concatArraySlices_$$Prim$$_(Slice<_$$prim$$_[]>... arraySlices)
	{
		int resultingLength = 0;
		{
			for (Slice<_$$prim$$_[]> a : arraySlices)
			{
				resultingLength += a != null ? a.getLength() : 0;
			}
		}
		
		
		_$$prim$$_[] newArray = new _$$prim$$_[resultingLength];
		
		
		int cursor = 0;
		
		for (Slice<_$$prim$$_[]> a : arraySlices)
		{
			if (a != null)
			{
				int l = a.getLength();
				if (l != 0)
				{
					System.arraycopy(a.getUnderlying(), a.getOffset(), newArray, cursor, l);
				}
			}
			
			cursor += a != null ? a.length : 0;
		}
		
		
		return newArray;
	}
	
	
	
	
	
	
	 */
	
	
	/**
	 * Basically an inverse function of {@link #concatArrays(boolean[][])}!! \:DD/
	 */
	public static boolean[][] splitPartitions(boolean[] concattedFlatArray, int partitionSize)
	{
		return splitPartitions(concattedFlatArray, partitionSize, true);
	}
	
	public static boolean[][] splitPartitionsLenient(boolean[] concattedFlatArray, int partitionSize)
	{
		return splitPartitions(concattedFlatArray, partitionSize, false);
	}
	
	public static Slice<boolean[]>[] splitPartitionsLiveSlicesBoolean(Slice<boolean[]> concattedFlatArraySlice, int partitionSize)
	{
		return splitPartitionsLiveSlicesBoolean(concattedFlatArraySlice, partitionSize, true);
	}
	
	
	
	
	public static boolean[][] splitPartitions(boolean[] concattedFlatArray, int partitionSize, boolean requireExact)
	{
		return splitPartitionsBoolean(concattedFlatArray, 0, concattedFlatArray.length, partitionSize, requireExact);
	}
	
	public static Slice<boolean[]>[] splitPartitionsLiveSlicesBoolean(boolean[] concattedFlatArray, int partitionSize, boolean requireExact)
	{
		return splitPartitionsLiveSlicesBoolean(concattedFlatArray, 0, concattedFlatArray.length, partitionSize, requireExact);
	}
	
	public static boolean[][] splitPartitionsBoolean(Slice<boolean[]> concattedFlatArraySlice, int partitionSize, boolean requireExact)
	{
		return splitPartitionsBoolean(concattedFlatArraySlice.getUnderlying(), concattedFlatArraySlice.getOffset(), concattedFlatArraySlice.getLength(), partitionSize, requireExact);
	}
	
	public static Slice<boolean[]>[] splitPartitionsLiveSlicesBoolean(Slice<boolean[]> concattedFlatArraySlice, int partitionSize, boolean requireExact)
	{
		return splitPartitionsLiveSlicesBoolean(concattedFlatArraySlice.getUnderlying(), concattedFlatArraySlice.getOffset(), concattedFlatArraySlice.getLength(), partitionSize, requireExact);
	}
	
	
	
	
	public static boolean[][] splitPartitionsBoolean(boolean[] concattedFlatArray, int concattedFlatArrayOffset, int concattedFlatArrayLength, int partitionSize, boolean requireExact)
	{
		if (partitionSize < 1)
			throw new IllegalArgumentException();
		
		int nElements = concattedFlatArrayLength;
		
		if (requireExact && (nElements % partitionSize) != 0)
			throw new IllegalArgumentException("Tried to split an array "+nElements+" elements long into equal partitions each "+partitionSize+" elements long!!");
		
		int nPartitions = ceilingDivision(nElements, partitionSize);  //ceiling division is important only if it happend to be inexact!  (which *may* be the case if requireExact is not set!)
		
		boolean[][] partitions = new boolean[nPartitions][];
		
		int baseInFlatArray = 0;
		for (int partitionIndex = 0; partitionIndex < nPartitions; partitionIndex++)
		{
			// baseInFlatArray == partitionIndex * partitionSize
			
			int partitionSizeThisIteration = least(partitionSize, nElements - baseInFlatArray);
			
			boolean[] partition = new boolean[partitionSizeThisIteration];
			
			System.arraycopy(concattedFlatArray, concattedFlatArrayOffset + baseInFlatArray, partition, 0, partitionSizeThisIteration);
			//	for (int elementIndexInPartition = 0; elementIndexInPartition < partitionSize; elementIndexInPartition++)
			//		partition[elementIndexInPartition] = concattedFlatArray[concattedFlatArrayOffset + baseInFlatArray+elementIndexInPartition];
			
			partitions[partitionIndex] = partition;
			
			baseInFlatArray += partitionSize;
		}
		
		return partitions;
	}
	
	public static Slice<boolean[]>[] splitPartitionsLiveSlicesBoolean(boolean[] concattedFlatArray, int concattedFlatArrayOffset, int concattedFlatArrayLength, int partitionSize, boolean requireExact)
	{
		if (partitionSize < 1)
			throw new IllegalArgumentException();
		
		int nElements = concattedFlatArrayLength;
		
		if (requireExact && (nElements % partitionSize) != 0)
			throw new IllegalArgumentException("Tried to split an array "+nElements+" elements long into equal partitions each "+partitionSize+" elements long!!");
		
		int nPartitions = ceilingDivision(nElements, partitionSize);  //ceiling division is important only if it happend to be inexact!  (which *may* be the case if requireExact is not set!)
		
		Slice<boolean[]>[] partitions = new Slice[nPartitions];
		
		int baseInFlatArray = 0;
		for (int partitionIndex = 0; partitionIndex < nPartitions; partitionIndex++)
		{
			// baseInFlatArray == partitionIndex * partitionSize
			
			int partitionSizeThisIteration = least(partitionSize, nElements - baseInFlatArray);
			
			Slice<boolean[]> partition = new Slice<>(concattedFlatArray, concattedFlatArrayOffset + baseInFlatArray, partitionSizeThisIteration);
			
			partitions[partitionIndex] = partition;
			
			baseInFlatArray += partitionSize;
		}
		
		return partitions;
	}
	
	
	
	
	
	
	public static boolean[] concatArrays(boolean[]... arrays)
	{
		int resultingLength = 0;
		{
			for (boolean[] a : arrays)
			{
				resultingLength += a != null ? a.length : 0;
			}
		}
		
		
		boolean[] newArray = new boolean[resultingLength];
		
		
		int cursor = 0;
		
		for (boolean[] a : arrays)
		{
			if (a != null)
			{
				int l = a.length;
				if (l != 0)
				{
					System.arraycopy(a, 0, newArray, cursor, l);
				}
			}
			
			cursor += a != null ? a.length : 0;
		}
		
		
		return newArray;
	}
	
	
	
	public static boolean[] concatArraySlicesBoolean(Slice<boolean[]>... arraySlices)
	{
		int resultingLength = 0;
		{
			for (Slice<boolean[]> a : arraySlices)
			{
				resultingLength += a != null ? a.getLength() : 0;
			}
		}
		
		
		boolean[] newArray = new boolean[resultingLength];
		
		
		int cursor = 0;
		
		for (Slice<boolean[]> a : arraySlices)
		{
			if (a != null)
			{
				int l = a.getLength();
				if (l != 0)
				{
					System.arraycopy(a.getUnderlying(), a.getOffset(), newArray, cursor, l);
				}
			}
			
			cursor += a != null ? a.length : 0;
		}
		
		
		return newArray;
	}
	
	
	
	
	
	
	
	
	/**
	 * Basically an inverse function of {@link #concatArrays(byte[][])}!! \:DD/
	 */
	public static byte[][] splitPartitions(byte[] concattedFlatArray, int partitionSize)
	{
		return splitPartitions(concattedFlatArray, partitionSize, true);
	}
	
	public static byte[][] splitPartitionsLenient(byte[] concattedFlatArray, int partitionSize)
	{
		return splitPartitions(concattedFlatArray, partitionSize, false);
	}
	
	public static Slice<byte[]>[] splitPartitionsLiveSlicesByte(Slice<byte[]> concattedFlatArraySlice, int partitionSize)
	{
		return splitPartitionsLiveSlicesByte(concattedFlatArraySlice, partitionSize, true);
	}
	
	
	
	
	public static byte[][] splitPartitions(byte[] concattedFlatArray, int partitionSize, boolean requireExact)
	{
		return splitPartitionsByte(concattedFlatArray, 0, concattedFlatArray.length, partitionSize, requireExact);
	}
	
	public static Slice<byte[]>[] splitPartitionsLiveSlicesByte(byte[] concattedFlatArray, int partitionSize, boolean requireExact)
	{
		return splitPartitionsLiveSlicesByte(concattedFlatArray, 0, concattedFlatArray.length, partitionSize, requireExact);
	}
	
	public static byte[][] splitPartitionsByte(Slice<byte[]> concattedFlatArraySlice, int partitionSize, boolean requireExact)
	{
		return splitPartitionsByte(concattedFlatArraySlice.getUnderlying(), concattedFlatArraySlice.getOffset(), concattedFlatArraySlice.getLength(), partitionSize, requireExact);
	}
	
	public static Slice<byte[]>[] splitPartitionsLiveSlicesByte(Slice<byte[]> concattedFlatArraySlice, int partitionSize, boolean requireExact)
	{
		return splitPartitionsLiveSlicesByte(concattedFlatArraySlice.getUnderlying(), concattedFlatArraySlice.getOffset(), concattedFlatArraySlice.getLength(), partitionSize, requireExact);
	}
	
	
	
	
	public static byte[][] splitPartitionsByte(byte[] concattedFlatArray, int concattedFlatArrayOffset, int concattedFlatArrayLength, int partitionSize, boolean requireExact)
	{
		if (partitionSize < 1)
			throw new IllegalArgumentException();
		
		int nElements = concattedFlatArrayLength;
		
		if (requireExact && (nElements % partitionSize) != 0)
			throw new IllegalArgumentException("Tried to split an array "+nElements+" elements long into equal partitions each "+partitionSize+" elements long!!");
		
		int nPartitions = ceilingDivision(nElements, partitionSize);  //ceiling division is important only if it happend to be inexact!  (which *may* be the case if requireExact is not set!)
		
		byte[][] partitions = new byte[nPartitions][];
		
		int baseInFlatArray = 0;
		for (int partitionIndex = 0; partitionIndex < nPartitions; partitionIndex++)
		{
			// baseInFlatArray == partitionIndex * partitionSize
			
			int partitionSizeThisIteration = least(partitionSize, nElements - baseInFlatArray);
			
			byte[] partition = new byte[partitionSizeThisIteration];
			
			System.arraycopy(concattedFlatArray, concattedFlatArrayOffset + baseInFlatArray, partition, 0, partitionSizeThisIteration);
			//	for (int elementIndexInPartition = 0; elementIndexInPartition < partitionSize; elementIndexInPartition++)
			//		partition[elementIndexInPartition] = concattedFlatArray[concattedFlatArrayOffset + baseInFlatArray+elementIndexInPartition];
			
			partitions[partitionIndex] = partition;
			
			baseInFlatArray += partitionSize;
		}
		
		return partitions;
	}
	
	public static Slice<byte[]>[] splitPartitionsLiveSlicesByte(byte[] concattedFlatArray, int concattedFlatArrayOffset, int concattedFlatArrayLength, int partitionSize, boolean requireExact)
	{
		if (partitionSize < 1)
			throw new IllegalArgumentException();
		
		int nElements = concattedFlatArrayLength;
		
		if (requireExact && (nElements % partitionSize) != 0)
			throw new IllegalArgumentException("Tried to split an array "+nElements+" elements long into equal partitions each "+partitionSize+" elements long!!");
		
		int nPartitions = ceilingDivision(nElements, partitionSize);  //ceiling division is important only if it happend to be inexact!  (which *may* be the case if requireExact is not set!)
		
		Slice<byte[]>[] partitions = new Slice[nPartitions];
		
		int baseInFlatArray = 0;
		for (int partitionIndex = 0; partitionIndex < nPartitions; partitionIndex++)
		{
			// baseInFlatArray == partitionIndex * partitionSize
			
			int partitionSizeThisIteration = least(partitionSize, nElements - baseInFlatArray);
			
			Slice<byte[]> partition = new Slice<>(concattedFlatArray, concattedFlatArrayOffset + baseInFlatArray, partitionSizeThisIteration);
			
			partitions[partitionIndex] = partition;
			
			baseInFlatArray += partitionSize;
		}
		
		return partitions;
	}
	
	
	
	
	
	
	public static byte[] concatArrays(byte[]... arrays)
	{
		int resultingLength = 0;
		{
			for (byte[] a : arrays)
			{
				resultingLength += a != null ? a.length : 0;
			}
		}
		
		
		byte[] newArray = new byte[resultingLength];
		
		
		int cursor = 0;
		
		for (byte[] a : arrays)
		{
			if (a != null)
			{
				int l = a.length;
				if (l != 0)
				{
					System.arraycopy(a, 0, newArray, cursor, l);
				}
			}
			
			cursor += a != null ? a.length : 0;
		}
		
		
		return newArray;
	}
	
	
	
	public static byte[] concatArraySlicesByte(Slice<byte[]>... arraySlices)
	{
		int resultingLength = 0;
		{
			for (Slice<byte[]> a : arraySlices)
			{
				resultingLength += a != null ? a.getLength() : 0;
			}
		}
		
		
		byte[] newArray = new byte[resultingLength];
		
		
		int cursor = 0;
		
		for (Slice<byte[]> a : arraySlices)
		{
			if (a != null)
			{
				int l = a.getLength();
				if (l != 0)
				{
					System.arraycopy(a.getUnderlying(), a.getOffset(), newArray, cursor, l);
				}
			}
			
			cursor += a != null ? a.length : 0;
		}
		
		
		return newArray;
	}
	
	
	
	
	
	
	
	
	/**
	 * Basically an inverse function of {@link #concatArrays(char[][])}!! \:DD/
	 */
	public static char[][] splitPartitions(char[] concattedFlatArray, int partitionSize)
	{
		return splitPartitions(concattedFlatArray, partitionSize, true);
	}
	
	public static char[][] splitPartitionsLenient(char[] concattedFlatArray, int partitionSize)
	{
		return splitPartitions(concattedFlatArray, partitionSize, false);
	}
	
	public static Slice<char[]>[] splitPartitionsLiveSlicesChar(Slice<char[]> concattedFlatArraySlice, int partitionSize)
	{
		return splitPartitionsLiveSlicesChar(concattedFlatArraySlice, partitionSize, true);
	}
	
	
	
	
	public static char[][] splitPartitions(char[] concattedFlatArray, int partitionSize, boolean requireExact)
	{
		return splitPartitionsChar(concattedFlatArray, 0, concattedFlatArray.length, partitionSize, requireExact);
	}
	
	public static Slice<char[]>[] splitPartitionsLiveSlicesChar(char[] concattedFlatArray, int partitionSize, boolean requireExact)
	{
		return splitPartitionsLiveSlicesChar(concattedFlatArray, 0, concattedFlatArray.length, partitionSize, requireExact);
	}
	
	public static char[][] splitPartitionsChar(Slice<char[]> concattedFlatArraySlice, int partitionSize, boolean requireExact)
	{
		return splitPartitionsChar(concattedFlatArraySlice.getUnderlying(), concattedFlatArraySlice.getOffset(), concattedFlatArraySlice.getLength(), partitionSize, requireExact);
	}
	
	public static Slice<char[]>[] splitPartitionsLiveSlicesChar(Slice<char[]> concattedFlatArraySlice, int partitionSize, boolean requireExact)
	{
		return splitPartitionsLiveSlicesChar(concattedFlatArraySlice.getUnderlying(), concattedFlatArraySlice.getOffset(), concattedFlatArraySlice.getLength(), partitionSize, requireExact);
	}
	
	
	
	
	public static char[][] splitPartitionsChar(char[] concattedFlatArray, int concattedFlatArrayOffset, int concattedFlatArrayLength, int partitionSize, boolean requireExact)
	{
		if (partitionSize < 1)
			throw new IllegalArgumentException();
		
		int nElements = concattedFlatArrayLength;
		
		if (requireExact && (nElements % partitionSize) != 0)
			throw new IllegalArgumentException("Tried to split an array "+nElements+" elements long into equal partitions each "+partitionSize+" elements long!!");
		
		int nPartitions = ceilingDivision(nElements, partitionSize);  //ceiling division is important only if it happend to be inexact!  (which *may* be the case if requireExact is not set!)
		
		char[][] partitions = new char[nPartitions][];
		
		int baseInFlatArray = 0;
		for (int partitionIndex = 0; partitionIndex < nPartitions; partitionIndex++)
		{
			// baseInFlatArray == partitionIndex * partitionSize
			
			int partitionSizeThisIteration = least(partitionSize, nElements - baseInFlatArray);
			
			char[] partition = new char[partitionSizeThisIteration];
			
			System.arraycopy(concattedFlatArray, concattedFlatArrayOffset + baseInFlatArray, partition, 0, partitionSizeThisIteration);
			//	for (int elementIndexInPartition = 0; elementIndexInPartition < partitionSize; elementIndexInPartition++)
			//		partition[elementIndexInPartition] = concattedFlatArray[concattedFlatArrayOffset + baseInFlatArray+elementIndexInPartition];
			
			partitions[partitionIndex] = partition;
			
			baseInFlatArray += partitionSize;
		}
		
		return partitions;
	}
	
	public static Slice<char[]>[] splitPartitionsLiveSlicesChar(char[] concattedFlatArray, int concattedFlatArrayOffset, int concattedFlatArrayLength, int partitionSize, boolean requireExact)
	{
		if (partitionSize < 1)
			throw new IllegalArgumentException();
		
		int nElements = concattedFlatArrayLength;
		
		if (requireExact && (nElements % partitionSize) != 0)
			throw new IllegalArgumentException("Tried to split an array "+nElements+" elements long into equal partitions each "+partitionSize+" elements long!!");
		
		int nPartitions = ceilingDivision(nElements, partitionSize);  //ceiling division is important only if it happend to be inexact!  (which *may* be the case if requireExact is not set!)
		
		Slice<char[]>[] partitions = new Slice[nPartitions];
		
		int baseInFlatArray = 0;
		for (int partitionIndex = 0; partitionIndex < nPartitions; partitionIndex++)
		{
			// baseInFlatArray == partitionIndex * partitionSize
			
			int partitionSizeThisIteration = least(partitionSize, nElements - baseInFlatArray);
			
			Slice<char[]> partition = new Slice<>(concattedFlatArray, concattedFlatArrayOffset + baseInFlatArray, partitionSizeThisIteration);
			
			partitions[partitionIndex] = partition;
			
			baseInFlatArray += partitionSize;
		}
		
		return partitions;
	}
	
	
	
	
	
	
	public static char[] concatArrays(char[]... arrays)
	{
		int resultingLength = 0;
		{
			for (char[] a : arrays)
			{
				resultingLength += a != null ? a.length : 0;
			}
		}
		
		
		char[] newArray = new char[resultingLength];
		
		
		int cursor = 0;
		
		for (char[] a : arrays)
		{
			if (a != null)
			{
				int l = a.length;
				if (l != 0)
				{
					System.arraycopy(a, 0, newArray, cursor, l);
				}
			}
			
			cursor += a != null ? a.length : 0;
		}
		
		
		return newArray;
	}
	
	
	
	public static char[] concatArraySlicesChar(Slice<char[]>... arraySlices)
	{
		int resultingLength = 0;
		{
			for (Slice<char[]> a : arraySlices)
			{
				resultingLength += a != null ? a.getLength() : 0;
			}
		}
		
		
		char[] newArray = new char[resultingLength];
		
		
		int cursor = 0;
		
		for (Slice<char[]> a : arraySlices)
		{
			if (a != null)
			{
				int l = a.getLength();
				if (l != 0)
				{
					System.arraycopy(a.getUnderlying(), a.getOffset(), newArray, cursor, l);
				}
			}
			
			cursor += a != null ? a.length : 0;
		}
		
		
		return newArray;
	}
	
	
	
	
	
	
	
	
	/**
	 * Basically an inverse function of {@link #concatArrays(short[][])}!! \:DD/
	 */
	public static short[][] splitPartitions(short[] concattedFlatArray, int partitionSize)
	{
		return splitPartitions(concattedFlatArray, partitionSize, true);
	}
	
	public static short[][] splitPartitionsLenient(short[] concattedFlatArray, int partitionSize)
	{
		return splitPartitions(concattedFlatArray, partitionSize, false);
	}
	
	public static Slice<short[]>[] splitPartitionsLiveSlicesShort(Slice<short[]> concattedFlatArraySlice, int partitionSize)
	{
		return splitPartitionsLiveSlicesShort(concattedFlatArraySlice, partitionSize, true);
	}
	
	
	
	
	public static short[][] splitPartitions(short[] concattedFlatArray, int partitionSize, boolean requireExact)
	{
		return splitPartitionsShort(concattedFlatArray, 0, concattedFlatArray.length, partitionSize, requireExact);
	}
	
	public static Slice<short[]>[] splitPartitionsLiveSlicesShort(short[] concattedFlatArray, int partitionSize, boolean requireExact)
	{
		return splitPartitionsLiveSlicesShort(concattedFlatArray, 0, concattedFlatArray.length, partitionSize, requireExact);
	}
	
	public static short[][] splitPartitionsShort(Slice<short[]> concattedFlatArraySlice, int partitionSize, boolean requireExact)
	{
		return splitPartitionsShort(concattedFlatArraySlice.getUnderlying(), concattedFlatArraySlice.getOffset(), concattedFlatArraySlice.getLength(), partitionSize, requireExact);
	}
	
	public static Slice<short[]>[] splitPartitionsLiveSlicesShort(Slice<short[]> concattedFlatArraySlice, int partitionSize, boolean requireExact)
	{
		return splitPartitionsLiveSlicesShort(concattedFlatArraySlice.getUnderlying(), concattedFlatArraySlice.getOffset(), concattedFlatArraySlice.getLength(), partitionSize, requireExact);
	}
	
	
	
	
	public static short[][] splitPartitionsShort(short[] concattedFlatArray, int concattedFlatArrayOffset, int concattedFlatArrayLength, int partitionSize, boolean requireExact)
	{
		if (partitionSize < 1)
			throw new IllegalArgumentException();
		
		int nElements = concattedFlatArrayLength;
		
		if (requireExact && (nElements % partitionSize) != 0)
			throw new IllegalArgumentException("Tried to split an array "+nElements+" elements long into equal partitions each "+partitionSize+" elements long!!");
		
		int nPartitions = ceilingDivision(nElements, partitionSize);  //ceiling division is important only if it happend to be inexact!  (which *may* be the case if requireExact is not set!)
		
		short[][] partitions = new short[nPartitions][];
		
		int baseInFlatArray = 0;
		for (int partitionIndex = 0; partitionIndex < nPartitions; partitionIndex++)
		{
			// baseInFlatArray == partitionIndex * partitionSize
			
			int partitionSizeThisIteration = least(partitionSize, nElements - baseInFlatArray);
			
			short[] partition = new short[partitionSizeThisIteration];
			
			System.arraycopy(concattedFlatArray, concattedFlatArrayOffset + baseInFlatArray, partition, 0, partitionSizeThisIteration);
			//	for (int elementIndexInPartition = 0; elementIndexInPartition < partitionSize; elementIndexInPartition++)
			//		partition[elementIndexInPartition] = concattedFlatArray[concattedFlatArrayOffset + baseInFlatArray+elementIndexInPartition];
			
			partitions[partitionIndex] = partition;
			
			baseInFlatArray += partitionSize;
		}
		
		return partitions;
	}
	
	public static Slice<short[]>[] splitPartitionsLiveSlicesShort(short[] concattedFlatArray, int concattedFlatArrayOffset, int concattedFlatArrayLength, int partitionSize, boolean requireExact)
	{
		if (partitionSize < 1)
			throw new IllegalArgumentException();
		
		int nElements = concattedFlatArrayLength;
		
		if (requireExact && (nElements % partitionSize) != 0)
			throw new IllegalArgumentException("Tried to split an array "+nElements+" elements long into equal partitions each "+partitionSize+" elements long!!");
		
		int nPartitions = ceilingDivision(nElements, partitionSize);  //ceiling division is important only if it happend to be inexact!  (which *may* be the case if requireExact is not set!)
		
		Slice<short[]>[] partitions = new Slice[nPartitions];
		
		int baseInFlatArray = 0;
		for (int partitionIndex = 0; partitionIndex < nPartitions; partitionIndex++)
		{
			// baseInFlatArray == partitionIndex * partitionSize
			
			int partitionSizeThisIteration = least(partitionSize, nElements - baseInFlatArray);
			
			Slice<short[]> partition = new Slice<>(concattedFlatArray, concattedFlatArrayOffset + baseInFlatArray, partitionSizeThisIteration);
			
			partitions[partitionIndex] = partition;
			
			baseInFlatArray += partitionSize;
		}
		
		return partitions;
	}
	
	
	
	
	
	
	public static short[] concatArrays(short[]... arrays)
	{
		int resultingLength = 0;
		{
			for (short[] a : arrays)
			{
				resultingLength += a != null ? a.length : 0;
			}
		}
		
		
		short[] newArray = new short[resultingLength];
		
		
		int cursor = 0;
		
		for (short[] a : arrays)
		{
			if (a != null)
			{
				int l = a.length;
				if (l != 0)
				{
					System.arraycopy(a, 0, newArray, cursor, l);
				}
			}
			
			cursor += a != null ? a.length : 0;
		}
		
		
		return newArray;
	}
	
	
	
	public static short[] concatArraySlicesShort(Slice<short[]>... arraySlices)
	{
		int resultingLength = 0;
		{
			for (Slice<short[]> a : arraySlices)
			{
				resultingLength += a != null ? a.getLength() : 0;
			}
		}
		
		
		short[] newArray = new short[resultingLength];
		
		
		int cursor = 0;
		
		for (Slice<short[]> a : arraySlices)
		{
			if (a != null)
			{
				int l = a.getLength();
				if (l != 0)
				{
					System.arraycopy(a.getUnderlying(), a.getOffset(), newArray, cursor, l);
				}
			}
			
			cursor += a != null ? a.length : 0;
		}
		
		
		return newArray;
	}
	
	
	
	
	
	
	
	
	/**
	 * Basically an inverse function of {@link #concatArrays(float[][])}!! \:DD/
	 */
	public static float[][] splitPartitions(float[] concattedFlatArray, int partitionSize)
	{
		return splitPartitions(concattedFlatArray, partitionSize, true);
	}
	
	public static float[][] splitPartitionsLenient(float[] concattedFlatArray, int partitionSize)
	{
		return splitPartitions(concattedFlatArray, partitionSize, false);
	}
	
	public static Slice<float[]>[] splitPartitionsLiveSlicesFloat(Slice<float[]> concattedFlatArraySlice, int partitionSize)
	{
		return splitPartitionsLiveSlicesFloat(concattedFlatArraySlice, partitionSize, true);
	}
	
	
	
	
	public static float[][] splitPartitions(float[] concattedFlatArray, int partitionSize, boolean requireExact)
	{
		return splitPartitionsFloat(concattedFlatArray, 0, concattedFlatArray.length, partitionSize, requireExact);
	}
	
	public static Slice<float[]>[] splitPartitionsLiveSlicesFloat(float[] concattedFlatArray, int partitionSize, boolean requireExact)
	{
		return splitPartitionsLiveSlicesFloat(concattedFlatArray, 0, concattedFlatArray.length, partitionSize, requireExact);
	}
	
	public static float[][] splitPartitionsFloat(Slice<float[]> concattedFlatArraySlice, int partitionSize, boolean requireExact)
	{
		return splitPartitionsFloat(concattedFlatArraySlice.getUnderlying(), concattedFlatArraySlice.getOffset(), concattedFlatArraySlice.getLength(), partitionSize, requireExact);
	}
	
	public static Slice<float[]>[] splitPartitionsLiveSlicesFloat(Slice<float[]> concattedFlatArraySlice, int partitionSize, boolean requireExact)
	{
		return splitPartitionsLiveSlicesFloat(concattedFlatArraySlice.getUnderlying(), concattedFlatArraySlice.getOffset(), concattedFlatArraySlice.getLength(), partitionSize, requireExact);
	}
	
	
	
	
	public static float[][] splitPartitionsFloat(float[] concattedFlatArray, int concattedFlatArrayOffset, int concattedFlatArrayLength, int partitionSize, boolean requireExact)
	{
		if (partitionSize < 1)
			throw new IllegalArgumentException();
		
		int nElements = concattedFlatArrayLength;
		
		if (requireExact && (nElements % partitionSize) != 0)
			throw new IllegalArgumentException("Tried to split an array "+nElements+" elements long into equal partitions each "+partitionSize+" elements long!!");
		
		int nPartitions = ceilingDivision(nElements, partitionSize);  //ceiling division is important only if it happend to be inexact!  (which *may* be the case if requireExact is not set!)
		
		float[][] partitions = new float[nPartitions][];
		
		int baseInFlatArray = 0;
		for (int partitionIndex = 0; partitionIndex < nPartitions; partitionIndex++)
		{
			// baseInFlatArray == partitionIndex * partitionSize
			
			int partitionSizeThisIteration = least(partitionSize, nElements - baseInFlatArray);
			
			float[] partition = new float[partitionSizeThisIteration];
			
			System.arraycopy(concattedFlatArray, concattedFlatArrayOffset + baseInFlatArray, partition, 0, partitionSizeThisIteration);
			//	for (int elementIndexInPartition = 0; elementIndexInPartition < partitionSize; elementIndexInPartition++)
			//		partition[elementIndexInPartition] = concattedFlatArray[concattedFlatArrayOffset + baseInFlatArray+elementIndexInPartition];
			
			partitions[partitionIndex] = partition;
			
			baseInFlatArray += partitionSize;
		}
		
		return partitions;
	}
	
	public static Slice<float[]>[] splitPartitionsLiveSlicesFloat(float[] concattedFlatArray, int concattedFlatArrayOffset, int concattedFlatArrayLength, int partitionSize, boolean requireExact)
	{
		if (partitionSize < 1)
			throw new IllegalArgumentException();
		
		int nElements = concattedFlatArrayLength;
		
		if (requireExact && (nElements % partitionSize) != 0)
			throw new IllegalArgumentException("Tried to split an array "+nElements+" elements long into equal partitions each "+partitionSize+" elements long!!");
		
		int nPartitions = ceilingDivision(nElements, partitionSize);  //ceiling division is important only if it happend to be inexact!  (which *may* be the case if requireExact is not set!)
		
		Slice<float[]>[] partitions = new Slice[nPartitions];
		
		int baseInFlatArray = 0;
		for (int partitionIndex = 0; partitionIndex < nPartitions; partitionIndex++)
		{
			// baseInFlatArray == partitionIndex * partitionSize
			
			int partitionSizeThisIteration = least(partitionSize, nElements - baseInFlatArray);
			
			Slice<float[]> partition = new Slice<>(concattedFlatArray, concattedFlatArrayOffset + baseInFlatArray, partitionSizeThisIteration);
			
			partitions[partitionIndex] = partition;
			
			baseInFlatArray += partitionSize;
		}
		
		return partitions;
	}
	
	
	
	
	
	
	public static float[] concatArrays(float[]... arrays)
	{
		int resultingLength = 0;
		{
			for (float[] a : arrays)
			{
				resultingLength += a != null ? a.length : 0;
			}
		}
		
		
		float[] newArray = new float[resultingLength];
		
		
		int cursor = 0;
		
		for (float[] a : arrays)
		{
			if (a != null)
			{
				int l = a.length;
				if (l != 0)
				{
					System.arraycopy(a, 0, newArray, cursor, l);
				}
			}
			
			cursor += a != null ? a.length : 0;
		}
		
		
		return newArray;
	}
	
	
	
	public static float[] concatArraySlicesFloat(Slice<float[]>... arraySlices)
	{
		int resultingLength = 0;
		{
			for (Slice<float[]> a : arraySlices)
			{
				resultingLength += a != null ? a.getLength() : 0;
			}
		}
		
		
		float[] newArray = new float[resultingLength];
		
		
		int cursor = 0;
		
		for (Slice<float[]> a : arraySlices)
		{
			if (a != null)
			{
				int l = a.getLength();
				if (l != 0)
				{
					System.arraycopy(a.getUnderlying(), a.getOffset(), newArray, cursor, l);
				}
			}
			
			cursor += a != null ? a.length : 0;
		}
		
		
		return newArray;
	}
	
	
	
	
	
	
	
	
	/**
	 * Basically an inverse function of {@link #concatArrays(int[][])}!! \:DD/
	 */
	public static int[][] splitPartitions(int[] concattedFlatArray, int partitionSize)
	{
		return splitPartitions(concattedFlatArray, partitionSize, true);
	}
	
	public static int[][] splitPartitionsLenient(int[] concattedFlatArray, int partitionSize)
	{
		return splitPartitions(concattedFlatArray, partitionSize, false);
	}
	
	public static Slice<int[]>[] splitPartitionsLiveSlicesInt(Slice<int[]> concattedFlatArraySlice, int partitionSize)
	{
		return splitPartitionsLiveSlicesInt(concattedFlatArraySlice, partitionSize, true);
	}
	
	
	
	
	public static int[][] splitPartitions(int[] concattedFlatArray, int partitionSize, boolean requireExact)
	{
		return splitPartitionsInt(concattedFlatArray, 0, concattedFlatArray.length, partitionSize, requireExact);
	}
	
	public static Slice<int[]>[] splitPartitionsLiveSlicesInt(int[] concattedFlatArray, int partitionSize, boolean requireExact)
	{
		return splitPartitionsLiveSlicesInt(concattedFlatArray, 0, concattedFlatArray.length, partitionSize, requireExact);
	}
	
	public static int[][] splitPartitionsInt(Slice<int[]> concattedFlatArraySlice, int partitionSize, boolean requireExact)
	{
		return splitPartitionsInt(concattedFlatArraySlice.getUnderlying(), concattedFlatArraySlice.getOffset(), concattedFlatArraySlice.getLength(), partitionSize, requireExact);
	}
	
	public static Slice<int[]>[] splitPartitionsLiveSlicesInt(Slice<int[]> concattedFlatArraySlice, int partitionSize, boolean requireExact)
	{
		return splitPartitionsLiveSlicesInt(concattedFlatArraySlice.getUnderlying(), concattedFlatArraySlice.getOffset(), concattedFlatArraySlice.getLength(), partitionSize, requireExact);
	}
	
	
	
	
	public static int[][] splitPartitionsInt(int[] concattedFlatArray, int concattedFlatArrayOffset, int concattedFlatArrayLength, int partitionSize, boolean requireExact)
	{
		if (partitionSize < 1)
			throw new IllegalArgumentException();
		
		int nElements = concattedFlatArrayLength;
		
		if (requireExact && (nElements % partitionSize) != 0)
			throw new IllegalArgumentException("Tried to split an array "+nElements+" elements long into equal partitions each "+partitionSize+" elements long!!");
		
		int nPartitions = ceilingDivision(nElements, partitionSize);  //ceiling division is important only if it happend to be inexact!  (which *may* be the case if requireExact is not set!)
		
		int[][] partitions = new int[nPartitions][];
		
		int baseInFlatArray = 0;
		for (int partitionIndex = 0; partitionIndex < nPartitions; partitionIndex++)
		{
			// baseInFlatArray == partitionIndex * partitionSize
			
			int partitionSizeThisIteration = least(partitionSize, nElements - baseInFlatArray);
			
			int[] partition = new int[partitionSizeThisIteration];
			
			System.arraycopy(concattedFlatArray, concattedFlatArrayOffset + baseInFlatArray, partition, 0, partitionSizeThisIteration);
			//	for (int elementIndexInPartition = 0; elementIndexInPartition < partitionSize; elementIndexInPartition++)
			//		partition[elementIndexInPartition] = concattedFlatArray[concattedFlatArrayOffset + baseInFlatArray+elementIndexInPartition];
			
			partitions[partitionIndex] = partition;
			
			baseInFlatArray += partitionSize;
		}
		
		return partitions;
	}
	
	public static Slice<int[]>[] splitPartitionsLiveSlicesInt(int[] concattedFlatArray, int concattedFlatArrayOffset, int concattedFlatArrayLength, int partitionSize, boolean requireExact)
	{
		if (partitionSize < 1)
			throw new IllegalArgumentException();
		
		int nElements = concattedFlatArrayLength;
		
		if (requireExact && (nElements % partitionSize) != 0)
			throw new IllegalArgumentException("Tried to split an array "+nElements+" elements long into equal partitions each "+partitionSize+" elements long!!");
		
		int nPartitions = ceilingDivision(nElements, partitionSize);  //ceiling division is important only if it happend to be inexact!  (which *may* be the case if requireExact is not set!)
		
		Slice<int[]>[] partitions = new Slice[nPartitions];
		
		int baseInFlatArray = 0;
		for (int partitionIndex = 0; partitionIndex < nPartitions; partitionIndex++)
		{
			// baseInFlatArray == partitionIndex * partitionSize
			
			int partitionSizeThisIteration = least(partitionSize, nElements - baseInFlatArray);
			
			Slice<int[]> partition = new Slice<>(concattedFlatArray, concattedFlatArrayOffset + baseInFlatArray, partitionSizeThisIteration);
			
			partitions[partitionIndex] = partition;
			
			baseInFlatArray += partitionSize;
		}
		
		return partitions;
	}
	
	
	
	
	
	
	public static int[] concatArrays(int[]... arrays)
	{
		int resultingLength = 0;
		{
			for (int[] a : arrays)
			{
				resultingLength += a != null ? a.length : 0;
			}
		}
		
		
		int[] newArray = new int[resultingLength];
		
		
		int cursor = 0;
		
		for (int[] a : arrays)
		{
			if (a != null)
			{
				int l = a.length;
				if (l != 0)
				{
					System.arraycopy(a, 0, newArray, cursor, l);
				}
			}
			
			cursor += a != null ? a.length : 0;
		}
		
		
		return newArray;
	}
	
	
	
	public static int[] concatArraySlicesInt(Slice<int[]>... arraySlices)
	{
		int resultingLength = 0;
		{
			for (Slice<int[]> a : arraySlices)
			{
				resultingLength += a != null ? a.getLength() : 0;
			}
		}
		
		
		int[] newArray = new int[resultingLength];
		
		
		int cursor = 0;
		
		for (Slice<int[]> a : arraySlices)
		{
			if (a != null)
			{
				int l = a.getLength();
				if (l != 0)
				{
					System.arraycopy(a.getUnderlying(), a.getOffset(), newArray, cursor, l);
				}
			}
			
			cursor += a != null ? a.length : 0;
		}
		
		
		return newArray;
	}
	
	
	
	
	
	
	
	
	/**
	 * Basically an inverse function of {@link #concatArrays(double[][])}!! \:DD/
	 */
	public static double[][] splitPartitions(double[] concattedFlatArray, int partitionSize)
	{
		return splitPartitions(concattedFlatArray, partitionSize, true);
	}
	
	public static double[][] splitPartitionsLenient(double[] concattedFlatArray, int partitionSize)
	{
		return splitPartitions(concattedFlatArray, partitionSize, false);
	}
	
	public static Slice<double[]>[] splitPartitionsLiveSlicesDouble(Slice<double[]> concattedFlatArraySlice, int partitionSize)
	{
		return splitPartitionsLiveSlicesDouble(concattedFlatArraySlice, partitionSize, true);
	}
	
	
	
	
	public static double[][] splitPartitions(double[] concattedFlatArray, int partitionSize, boolean requireExact)
	{
		return splitPartitionsDouble(concattedFlatArray, 0, concattedFlatArray.length, partitionSize, requireExact);
	}
	
	public static Slice<double[]>[] splitPartitionsLiveSlicesDouble(double[] concattedFlatArray, int partitionSize, boolean requireExact)
	{
		return splitPartitionsLiveSlicesDouble(concattedFlatArray, 0, concattedFlatArray.length, partitionSize, requireExact);
	}
	
	public static double[][] splitPartitionsDouble(Slice<double[]> concattedFlatArraySlice, int partitionSize, boolean requireExact)
	{
		return splitPartitionsDouble(concattedFlatArraySlice.getUnderlying(), concattedFlatArraySlice.getOffset(), concattedFlatArraySlice.getLength(), partitionSize, requireExact);
	}
	
	public static Slice<double[]>[] splitPartitionsLiveSlicesDouble(Slice<double[]> concattedFlatArraySlice, int partitionSize, boolean requireExact)
	{
		return splitPartitionsLiveSlicesDouble(concattedFlatArraySlice.getUnderlying(), concattedFlatArraySlice.getOffset(), concattedFlatArraySlice.getLength(), partitionSize, requireExact);
	}
	
	
	
	
	public static double[][] splitPartitionsDouble(double[] concattedFlatArray, int concattedFlatArrayOffset, int concattedFlatArrayLength, int partitionSize, boolean requireExact)
	{
		if (partitionSize < 1)
			throw new IllegalArgumentException();
		
		int nElements = concattedFlatArrayLength;
		
		if (requireExact && (nElements % partitionSize) != 0)
			throw new IllegalArgumentException("Tried to split an array "+nElements+" elements long into equal partitions each "+partitionSize+" elements long!!");
		
		int nPartitions = ceilingDivision(nElements, partitionSize);  //ceiling division is important only if it happend to be inexact!  (which *may* be the case if requireExact is not set!)
		
		double[][] partitions = new double[nPartitions][];
		
		int baseInFlatArray = 0;
		for (int partitionIndex = 0; partitionIndex < nPartitions; partitionIndex++)
		{
			// baseInFlatArray == partitionIndex * partitionSize
			
			int partitionSizeThisIteration = least(partitionSize, nElements - baseInFlatArray);
			
			double[] partition = new double[partitionSizeThisIteration];
			
			System.arraycopy(concattedFlatArray, concattedFlatArrayOffset + baseInFlatArray, partition, 0, partitionSizeThisIteration);
			//	for (int elementIndexInPartition = 0; elementIndexInPartition < partitionSize; elementIndexInPartition++)
			//		partition[elementIndexInPartition] = concattedFlatArray[concattedFlatArrayOffset + baseInFlatArray+elementIndexInPartition];
			
			partitions[partitionIndex] = partition;
			
			baseInFlatArray += partitionSize;
		}
		
		return partitions;
	}
	
	public static Slice<double[]>[] splitPartitionsLiveSlicesDouble(double[] concattedFlatArray, int concattedFlatArrayOffset, int concattedFlatArrayLength, int partitionSize, boolean requireExact)
	{
		if (partitionSize < 1)
			throw new IllegalArgumentException();
		
		int nElements = concattedFlatArrayLength;
		
		if (requireExact && (nElements % partitionSize) != 0)
			throw new IllegalArgumentException("Tried to split an array "+nElements+" elements long into equal partitions each "+partitionSize+" elements long!!");
		
		int nPartitions = ceilingDivision(nElements, partitionSize);  //ceiling division is important only if it happend to be inexact!  (which *may* be the case if requireExact is not set!)
		
		Slice<double[]>[] partitions = new Slice[nPartitions];
		
		int baseInFlatArray = 0;
		for (int partitionIndex = 0; partitionIndex < nPartitions; partitionIndex++)
		{
			// baseInFlatArray == partitionIndex * partitionSize
			
			int partitionSizeThisIteration = least(partitionSize, nElements - baseInFlatArray);
			
			Slice<double[]> partition = new Slice<>(concattedFlatArray, concattedFlatArrayOffset + baseInFlatArray, partitionSizeThisIteration);
			
			partitions[partitionIndex] = partition;
			
			baseInFlatArray += partitionSize;
		}
		
		return partitions;
	}
	
	
	
	
	
	
	public static double[] concatArrays(double[]... arrays)
	{
		int resultingLength = 0;
		{
			for (double[] a : arrays)
			{
				resultingLength += a != null ? a.length : 0;
			}
		}
		
		
		double[] newArray = new double[resultingLength];
		
		
		int cursor = 0;
		
		for (double[] a : arrays)
		{
			if (a != null)
			{
				int l = a.length;
				if (l != 0)
				{
					System.arraycopy(a, 0, newArray, cursor, l);
				}
			}
			
			cursor += a != null ? a.length : 0;
		}
		
		
		return newArray;
	}
	
	
	
	public static double[] concatArraySlicesDouble(Slice<double[]>... arraySlices)
	{
		int resultingLength = 0;
		{
			for (Slice<double[]> a : arraySlices)
			{
				resultingLength += a != null ? a.getLength() : 0;
			}
		}
		
		
		double[] newArray = new double[resultingLength];
		
		
		int cursor = 0;
		
		for (Slice<double[]> a : arraySlices)
		{
			if (a != null)
			{
				int l = a.getLength();
				if (l != 0)
				{
					System.arraycopy(a.getUnderlying(), a.getOffset(), newArray, cursor, l);
				}
			}
			
			cursor += a != null ? a.length : 0;
		}
		
		
		return newArray;
	}
	
	
	
	
	
	
	
	
	/**
	 * Basically an inverse function of {@link #concatArrays(long[][])}!! \:DD/
	 */
	public static long[][] splitPartitions(long[] concattedFlatArray, int partitionSize)
	{
		return splitPartitions(concattedFlatArray, partitionSize, true);
	}
	
	public static long[][] splitPartitionsLenient(long[] concattedFlatArray, int partitionSize)
	{
		return splitPartitions(concattedFlatArray, partitionSize, false);
	}
	
	public static Slice<long[]>[] splitPartitionsLiveSlicesLong(Slice<long[]> concattedFlatArraySlice, int partitionSize)
	{
		return splitPartitionsLiveSlicesLong(concattedFlatArraySlice, partitionSize, true);
	}
	
	
	
	
	public static long[][] splitPartitions(long[] concattedFlatArray, int partitionSize, boolean requireExact)
	{
		return splitPartitionsLong(concattedFlatArray, 0, concattedFlatArray.length, partitionSize, requireExact);
	}
	
	public static Slice<long[]>[] splitPartitionsLiveSlicesLong(long[] concattedFlatArray, int partitionSize, boolean requireExact)
	{
		return splitPartitionsLiveSlicesLong(concattedFlatArray, 0, concattedFlatArray.length, partitionSize, requireExact);
	}
	
	public static long[][] splitPartitionsLong(Slice<long[]> concattedFlatArraySlice, int partitionSize, boolean requireExact)
	{
		return splitPartitionsLong(concattedFlatArraySlice.getUnderlying(), concattedFlatArraySlice.getOffset(), concattedFlatArraySlice.getLength(), partitionSize, requireExact);
	}
	
	public static Slice<long[]>[] splitPartitionsLiveSlicesLong(Slice<long[]> concattedFlatArraySlice, int partitionSize, boolean requireExact)
	{
		return splitPartitionsLiveSlicesLong(concattedFlatArraySlice.getUnderlying(), concattedFlatArraySlice.getOffset(), concattedFlatArraySlice.getLength(), partitionSize, requireExact);
	}
	
	
	
	
	public static long[][] splitPartitionsLong(long[] concattedFlatArray, int concattedFlatArrayOffset, int concattedFlatArrayLength, int partitionSize, boolean requireExact)
	{
		if (partitionSize < 1)
			throw new IllegalArgumentException();
		
		int nElements = concattedFlatArrayLength;
		
		if (requireExact && (nElements % partitionSize) != 0)
			throw new IllegalArgumentException("Tried to split an array "+nElements+" elements long into equal partitions each "+partitionSize+" elements long!!");
		
		int nPartitions = ceilingDivision(nElements, partitionSize);  //ceiling division is important only if it happend to be inexact!  (which *may* be the case if requireExact is not set!)
		
		long[][] partitions = new long[nPartitions][];
		
		int baseInFlatArray = 0;
		for (int partitionIndex = 0; partitionIndex < nPartitions; partitionIndex++)
		{
			// baseInFlatArray == partitionIndex * partitionSize
			
			int partitionSizeThisIteration = least(partitionSize, nElements - baseInFlatArray);
			
			long[] partition = new long[partitionSizeThisIteration];
			
			System.arraycopy(concattedFlatArray, concattedFlatArrayOffset + baseInFlatArray, partition, 0, partitionSizeThisIteration);
			//	for (int elementIndexInPartition = 0; elementIndexInPartition < partitionSize; elementIndexInPartition++)
			//		partition[elementIndexInPartition] = concattedFlatArray[concattedFlatArrayOffset + baseInFlatArray+elementIndexInPartition];
			
			partitions[partitionIndex] = partition;
			
			baseInFlatArray += partitionSize;
		}
		
		return partitions;
	}
	
	public static Slice<long[]>[] splitPartitionsLiveSlicesLong(long[] concattedFlatArray, int concattedFlatArrayOffset, int concattedFlatArrayLength, int partitionSize, boolean requireExact)
	{
		if (partitionSize < 1)
			throw new IllegalArgumentException();
		
		int nElements = concattedFlatArrayLength;
		
		if (requireExact && (nElements % partitionSize) != 0)
			throw new IllegalArgumentException("Tried to split an array "+nElements+" elements long into equal partitions each "+partitionSize+" elements long!!");
		
		int nPartitions = ceilingDivision(nElements, partitionSize);  //ceiling division is important only if it happend to be inexact!  (which *may* be the case if requireExact is not set!)
		
		Slice<long[]>[] partitions = new Slice[nPartitions];
		
		int baseInFlatArray = 0;
		for (int partitionIndex = 0; partitionIndex < nPartitions; partitionIndex++)
		{
			// baseInFlatArray == partitionIndex * partitionSize
			
			int partitionSizeThisIteration = least(partitionSize, nElements - baseInFlatArray);
			
			Slice<long[]> partition = new Slice<>(concattedFlatArray, concattedFlatArrayOffset + baseInFlatArray, partitionSizeThisIteration);
			
			partitions[partitionIndex] = partition;
			
			baseInFlatArray += partitionSize;
		}
		
		return partitions;
	}
	
	
	
	
	
	
	public static long[] concatArrays(long[]... arrays)
	{
		int resultingLength = 0;
		{
			for (long[] a : arrays)
			{
				resultingLength += a != null ? a.length : 0;
			}
		}
		
		
		long[] newArray = new long[resultingLength];
		
		
		int cursor = 0;
		
		for (long[] a : arrays)
		{
			if (a != null)
			{
				int l = a.length;
				if (l != 0)
				{
					System.arraycopy(a, 0, newArray, cursor, l);
				}
			}
			
			cursor += a != null ? a.length : 0;
		}
		
		
		return newArray;
	}
	
	
	
	public static long[] concatArraySlicesLong(Slice<long[]>... arraySlices)
	{
		int resultingLength = 0;
		{
			for (Slice<long[]> a : arraySlices)
			{
				resultingLength += a != null ? a.getLength() : 0;
			}
		}
		
		
		long[] newArray = new long[resultingLength];
		
		
		int cursor = 0;
		
		for (Slice<long[]> a : arraySlices)
		{
			if (a != null)
			{
				int l = a.getLength();
				if (l != 0)
				{
					System.arraycopy(a.getUnderlying(), a.getOffset(), newArray, cursor, l);
				}
			}
			
			cursor += a != null ? a.length : 0;
		}
		
		
		return newArray;
	}
	
	
	
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Sorts the given array, then removes duplicate values, returning a sorted array of all the same unique values or the same length or shorter.
	 * + Note: this does NOT modify the provided array
	 */
	public static int[] sortAndUniqueify(int[] raw)
	{
		int[] sorted = null;
		{
			sorted = new int[raw.length];
			System.arraycopy(raw, 0, sorted, 0, sorted.length); //yeah, don't forget this part.
			Arrays.sort(sorted);
		}
		
		int[] uniques = new int[sorted.length]; //we know it won't be longer than the original array!
		int uniqueCount = 0;
		
		boolean hasLast = false;
		int last = 0;
		for (int currentValue : sorted)
		{
			if (hasLast && last == currentValue)
			{
				//skip the duplicate.
			}
			else
			{
				uniques[uniqueCount] = currentValue;
				uniqueCount++;
			}
			
			hasLast = true;
			last = currentValue;
		}
		
		int[] trimmed = new int[uniqueCount];
		System.arraycopy(uniques, 0, trimmed, 0, uniqueCount);
		
		return trimmed;
	}
	
	
	public static double[] sortAndUniqueify(double[] raw)
	{
		double[] sorted = null;
		{
			sorted = new double[raw.length];
			System.arraycopy(raw, 0, sorted, 0, sorted.length); //yeah, don't forget this part.  XD
			Arrays.sort(sorted);
		}
		
		double[] uniques = new double[sorted.length]; //we know it won't be longer than the original array!
		int uniqueCount = 0;
		
		boolean hasLast = false;
		double last = 0;
		for (double currentValue : sorted)
		{
			if (hasLast && last == currentValue)
			{
				//skip the duplicate.
			}
			else
			{
				uniques[uniqueCount] = currentValue;
				uniqueCount++;
			}
			
			hasLast = true;
			last = currentValue;
		}
		
		double[] trimmed = new double[uniqueCount];
		System.arraycopy(uniques, 0, trimmed, 0, uniqueCount);
		
		return trimmed;
	}
	
	
	
	
	
	public static <E> E[] sortedArray(E[] array)
	{
		E[] newArray = array.clone();
		Arrays.sort(newArray);
		return newArray;
	}
	
	public static <E> E[] sortedArray(E[] array, Comparator<? super E> comparator)
	{
		E[] newArray = array.clone();
		Arrays.sort(newArray, comparator);
		return newArray;
	}
	
	
	/*
	public static _$$prim$$_[] sorted(_$$prim$$_[] src)
	{
		_$$prim$$_[] newArray = new _$$prim$$_[src.length];
		System.arraycopy(src, 0, newArray, 0, src.length);
		Arrays.sort(newArray);
		return newArray;
	}
	
	 */
	
	public static void sort(boolean[] array)
	{
		int srclen = array.length;
		
		int trueCount = 0;
		{
			for (boolean e : array)
				if (e == true)
					trueCount++;
		}
		
		
		
		if (srclen-trueCount > 0)
		{
			for (int i = 0; i < srclen-trueCount; i++)
				array[i] = false;
		}
		
		if (trueCount > 0)
		{
			for (int i = trueCount; i < srclen; i++)
				array[i] = true;
		}
	}
	
	public static boolean[] sorted(boolean[] src)
	{
		int srclen = src.length;
		
		int trueCount = 0;
		{
			for (boolean e : src)
				if (e == true)
					trueCount++;
		}
		
		boolean[] newArray = new boolean[srclen];
		
		if (trueCount > 0)
		{
			for (int i = trueCount; i < srclen; i++)
				newArray[i] = true;
		}
		
		return newArray;
	}
	
	public static byte[] sorted(byte[] src)
	{
		byte[] newArray = new byte[src.length];
		System.arraycopy(src, 0, newArray, 0, src.length);
		Arrays.sort(newArray);
		return newArray;
	}
	
	public static char[] sorted(char[] src)
	{
		char[] newArray = new char[src.length];
		System.arraycopy(src, 0, newArray, 0, src.length);
		Arrays.sort(newArray);
		return newArray;
	}
	
	public static short[] sorted(short[] src)
	{
		short[] newArray = new short[src.length];
		System.arraycopy(src, 0, newArray, 0, src.length);
		Arrays.sort(newArray);
		return newArray;
	}
	
	public static float[] sorted(float[] src)
	{
		float[] newArray = new float[src.length];
		System.arraycopy(src, 0, newArray, 0, src.length);
		Arrays.sort(newArray);
		return newArray;
	}
	
	public static int[] sorted(int[] src)
	{
		int[] newArray = new int[src.length];
		System.arraycopy(src, 0, newArray, 0, src.length);
		Arrays.sort(newArray);
		return newArray;
	}
	
	public static double[] sorted(double[] src)
	{
		double[] newArray = new double[src.length];
		System.arraycopy(src, 0, newArray, 0, src.length);
		Arrays.sort(newArray);
		return newArray;
	}
	
	public static long[] sorted(long[] src)
	{
		long[] newArray = new long[src.length];
		System.arraycopy(src, 0, newArray, 0, src.length);
		Arrays.sort(newArray);
		return newArray;
	}
	
	
	
	
	
	
	
	public static <E> E[] uniqed(E[] input, EqualityComparator<E> equalityComparator)
	{
		if (input == null)
			return null;
		
		E[] outputUntrimmed = (E[])Array.newInstance(input.getClass().getComponentType(), input.length);
		int outputSize = 0;
		
		E last = null;
		boolean hasLast = false;
		
		for (int i = 0; i < input.length; i++)
		{
			if (!hasLast)
			{
				outputUntrimmed[outputSize] = input[i];
				outputSize++;
				
				last = input[i];
				hasLast = true;
			}
			else
			{
				if (equalityComparator.equals(input[i], last))
				{
					//skip it :>
				}
				else
				{
					outputUntrimmed[outputSize] = input[i];
					outputSize++;
					
					last = input[i];
				}
			}
		}
		
		
		//Trim output :>
		E[] outputTrimmed = outputSize == outputUntrimmed.length ? outputUntrimmed : (E[])Arrays.copyOf(outputUntrimmed, outputSize);
		
		return outputTrimmed;
	}
	
	
	public static <E> E[] uniqed(E[] input)
	{
		return uniqed(input, (EqualityComparator<E>)getNaturalEqualityComparator());
	}
	
	
	/*
	public static _$$prim$$_[] uniqed(_$$prim$$_[] input)
	{
		if (input == null)
			return null;
		
		_$$prim$$_[] outputUntrimmed = new _$$prim$$_[input.length];
		int outputSize = 0;
		
		_$$prim$$_ last = _$$primdef$$_;
		boolean hasLast = false;
		
		for (int i = 0; i < input.length; i++)
		{
			if (!hasLast)
			{
				outputUntrimmed[outputSize] = input[i];
				outputSize++;
				
				last = input[i];
				hasLast = true;
			}
			else
			{
				if (input[i] == last)
				{
					//skip it :>
				}
				else
				{
					outputUntrimmed[outputSize] = input[i];
					outputSize++;
					
					last = input[i];
				}
			}
		}
		
		
		//Trim output :>
		_$$prim$$_[] outputTrimmed = outputSize == outputUntrimmed.length ? outputUntrimmed : Arrays.copyOf(outputUntrimmed, outputSize);
		
		return outputTrimmed;
	}
	
	
	 */
	
	public static boolean[] uniqed(boolean[] input)
	{
		if (input == null)
			return null;
		
		boolean[] outputUntrimmed = new boolean[input.length];
		int outputSize = 0;
		
		boolean last = false;
		boolean hasLast = false;
		
		for (int i = 0; i < input.length; i++)
		{
			if (!hasLast)
			{
				outputUntrimmed[outputSize] = input[i];
				outputSize++;
				
				last = input[i];
				hasLast = true;
			}
			else
			{
				if (input[i] == last)
				{
					//skip it :>
				}
				else
				{
					outputUntrimmed[outputSize] = input[i];
					outputSize++;
					
					last = input[i];
				}
			}
		}
		
		
		//Trim output :>
		boolean[] outputTrimmed = outputSize == outputUntrimmed.length ? outputUntrimmed : Arrays.copyOf(outputUntrimmed, outputSize);
		
		return outputTrimmed;
	}
	
	
	public static byte[] uniqed(byte[] input)
	{
		if (input == null)
			return null;
		
		byte[] outputUntrimmed = new byte[input.length];
		int outputSize = 0;
		
		byte last = ((byte)0);
		boolean hasLast = false;
		
		for (int i = 0; i < input.length; i++)
		{
			if (!hasLast)
			{
				outputUntrimmed[outputSize] = input[i];
				outputSize++;
				
				last = input[i];
				hasLast = true;
			}
			else
			{
				if (input[i] == last)
				{
					//skip it :>
				}
				else
				{
					outputUntrimmed[outputSize] = input[i];
					outputSize++;
					
					last = input[i];
				}
			}
		}
		
		
		//Trim output :>
		byte[] outputTrimmed = outputSize == outputUntrimmed.length ? outputUntrimmed : Arrays.copyOf(outputUntrimmed, outputSize);
		
		return outputTrimmed;
	}
	
	
	public static char[] uniqed(char[] input)
	{
		if (input == null)
			return null;
		
		char[] outputUntrimmed = new char[input.length];
		int outputSize = 0;
		
		char last = ((char)0);
		boolean hasLast = false;
		
		for (int i = 0; i < input.length; i++)
		{
			if (!hasLast)
			{
				outputUntrimmed[outputSize] = input[i];
				outputSize++;
				
				last = input[i];
				hasLast = true;
			}
			else
			{
				if (input[i] == last)
				{
					//skip it :>
				}
				else
				{
					outputUntrimmed[outputSize] = input[i];
					outputSize++;
					
					last = input[i];
				}
			}
		}
		
		
		//Trim output :>
		char[] outputTrimmed = outputSize == outputUntrimmed.length ? outputUntrimmed : Arrays.copyOf(outputUntrimmed, outputSize);
		
		return outputTrimmed;
	}
	
	
	public static short[] uniqed(short[] input)
	{
		if (input == null)
			return null;
		
		short[] outputUntrimmed = new short[input.length];
		int outputSize = 0;
		
		short last = ((short)0);
		boolean hasLast = false;
		
		for (int i = 0; i < input.length; i++)
		{
			if (!hasLast)
			{
				outputUntrimmed[outputSize] = input[i];
				outputSize++;
				
				last = input[i];
				hasLast = true;
			}
			else
			{
				if (input[i] == last)
				{
					//skip it :>
				}
				else
				{
					outputUntrimmed[outputSize] = input[i];
					outputSize++;
					
					last = input[i];
				}
			}
		}
		
		
		//Trim output :>
		short[] outputTrimmed = outputSize == outputUntrimmed.length ? outputUntrimmed : Arrays.copyOf(outputUntrimmed, outputSize);
		
		return outputTrimmed;
	}
	
	
	public static float[] uniqed(float[] input)
	{
		if (input == null)
			return null;
		
		float[] outputUntrimmed = new float[input.length];
		int outputSize = 0;
		
		float last = 0.0f;
		boolean hasLast = false;
		
		for (int i = 0; i < input.length; i++)
		{
			if (!hasLast)
			{
				outputUntrimmed[outputSize] = input[i];
				outputSize++;
				
				last = input[i];
				hasLast = true;
			}
			else
			{
				if (input[i] == last)
				{
					//skip it :>
				}
				else
				{
					outputUntrimmed[outputSize] = input[i];
					outputSize++;
					
					last = input[i];
				}
			}
		}
		
		
		//Trim output :>
		float[] outputTrimmed = outputSize == outputUntrimmed.length ? outputUntrimmed : Arrays.copyOf(outputUntrimmed, outputSize);
		
		return outputTrimmed;
	}
	
	
	public static int[] uniqed(int[] input)
	{
		if (input == null)
			return null;
		
		int[] outputUntrimmed = new int[input.length];
		int outputSize = 0;
		
		int last = 0;
		boolean hasLast = false;
		
		for (int i = 0; i < input.length; i++)
		{
			if (!hasLast)
			{
				outputUntrimmed[outputSize] = input[i];
				outputSize++;
				
				last = input[i];
				hasLast = true;
			}
			else
			{
				if (input[i] == last)
				{
					//skip it :>
				}
				else
				{
					outputUntrimmed[outputSize] = input[i];
					outputSize++;
					
					last = input[i];
				}
			}
		}
		
		
		//Trim output :>
		int[] outputTrimmed = outputSize == outputUntrimmed.length ? outputUntrimmed : Arrays.copyOf(outputUntrimmed, outputSize);
		
		return outputTrimmed;
	}
	
	
	public static double[] uniqed(double[] input)
	{
		if (input == null)
			return null;
		
		double[] outputUntrimmed = new double[input.length];
		int outputSize = 0;
		
		double last = 0.0d;
		boolean hasLast = false;
		
		for (int i = 0; i < input.length; i++)
		{
			if (!hasLast)
			{
				outputUntrimmed[outputSize] = input[i];
				outputSize++;
				
				last = input[i];
				hasLast = true;
			}
			else
			{
				if (input[i] == last)
				{
					//skip it :>
				}
				else
				{
					outputUntrimmed[outputSize] = input[i];
					outputSize++;
					
					last = input[i];
				}
			}
		}
		
		
		//Trim output :>
		double[] outputTrimmed = outputSize == outputUntrimmed.length ? outputUntrimmed : Arrays.copyOf(outputUntrimmed, outputSize);
		
		return outputTrimmed;
	}
	
	
	public static long[] uniqed(long[] input)
	{
		if (input == null)
			return null;
		
		long[] outputUntrimmed = new long[input.length];
		int outputSize = 0;
		
		long last = 0l;
		boolean hasLast = false;
		
		for (int i = 0; i < input.length; i++)
		{
			if (!hasLast)
			{
				outputUntrimmed[outputSize] = input[i];
				outputSize++;
				
				last = input[i];
				hasLast = true;
			}
			else
			{
				if (input[i] == last)
				{
					//skip it :>
				}
				else
				{
					outputUntrimmed[outputSize] = input[i];
					outputSize++;
					
					last = input[i];
				}
			}
		}
		
		
		//Trim output :>
		long[] outputTrimmed = outputSize == outputUntrimmed.length ? outputUntrimmed : Arrays.copyOf(outputUntrimmed, outputSize);
		
		return outputTrimmed;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * NOTE: when creating arrays, if the value is null, the array will be or type Object[], not any other expected type
	 * @param array if <code>null</code>, one will be created ^_^
	 */
	public static <E> void fillArrayReference(E[] array, int offset, int length, E value)
	{
		int e = offset + length;
		for (int i = offset; i < e; i++)
			array[i] = value;
	}
	
	public static <E> void fillArrayReference(E[] array, E value)
	{
		fillArrayReference(array, 0, array.length, value); //it makes sense that array can't be null here since we wouldn't know how big to make it!
	}
	
	public static Object[] newfillArrayObject(int length, Object value)
	{
		Object[] array = new Object[length];
		
		if (value != null)
			fillArrayReference(array, value);
		
		return array;
	}
	
	public static <T> T[] newfillArray(int length, T value, Class<T> componentType)
	{
		T[] array = (T[]) Array.newInstance(componentType, length);
		
		//Todo notice null, 0, or false, and elide this step accordingly ^^'
		for (int i = 0; i < length; i++)
			Array.set(array, i, value);
		
		return array;
	}
	
	
	
	
	
	/* <<<
	primxp
	
	public static void fillArray_$$Prim$$_(_$$prim$$_[] array, int offset, int length, _$$prim$$_ value)
	{
		int e = offset + length;
		for (int i = offset; i < e; i++)
			array[i] = value;
	}
	
	public static void fillArray_$$Prim$$_(_$$prim$$_[] array, _$$prim$$_ value)
	{
		fillArray_$$Prim$$_(array, 0, array.length, value);
	}
	
	public static _$$prim$$_[] newfillArray_$$Prim$$_(int length, _$$prim$$_ value)
	{
		_$$prim$$_[] array = new _$$prim$$_[length];
		
		if (value != _$$primdef$$_)
			fillArray_$$Prim$$_(array, 0, length, value);
		
		return array;
	}
	
	
	
	public static void fillArraySlice_$$Prim$$_(@Nonnull Slice<_$$prim$$_[]> arraySlice, _$$prim$$_ value)
	{
		fillArray_$$Prim$$_(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength(), value);
	}
	
	@Nonnull
	public static Slice<_$$prim$$_[]> newfillArraySlice_$$Prim$$_(int length, _$$prim$$_ value)
	{
		return wholeArraySlice_$$Prim$$_(newfillArray_$$Prim$$_(length, value));
	}
	
	
	
	
	
	
	 */
	
	public static void fillArrayBoolean(boolean[] array, int offset, int length, boolean value)
	{
		int e = offset + length;
		for (int i = offset; i < e; i++)
			array[i] = value;
	}
	
	public static void fillArrayBoolean(boolean[] array, boolean value)
	{
		fillArrayBoolean(array, 0, array.length, value);
	}
	
	public static boolean[] newfillArrayBoolean(int length, boolean value)
	{
		boolean[] array = new boolean[length];
		
		if (value != false)
			fillArrayBoolean(array, 0, length, value);
		
		return array;
	}
	
	
	
	public static void fillArraySliceBoolean(@Nonnull Slice<boolean[]> arraySlice, boolean value)
	{
		fillArrayBoolean(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength(), value);
	}
	
	@Nonnull
	public static Slice<boolean[]> newfillArraySliceBoolean(int length, boolean value)
	{
		return wholeArraySliceBoolean(newfillArrayBoolean(length, value));
	}
	
	
	
	
	
	
	
	public static void fillArrayByte(byte[] array, int offset, int length, byte value)
	{
		int e = offset + length;
		for (int i = offset; i < e; i++)
			array[i] = value;
	}
	
	public static void fillArrayByte(byte[] array, byte value)
	{
		fillArrayByte(array, 0, array.length, value);
	}
	
	public static byte[] newfillArrayByte(int length, byte value)
	{
		byte[] array = new byte[length];
		
		if (value != ((byte)0))
			fillArrayByte(array, 0, length, value);
		
		return array;
	}
	
	
	
	public static void fillArraySliceByte(@Nonnull Slice<byte[]> arraySlice, byte value)
	{
		fillArrayByte(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength(), value);
	}
	
	@Nonnull
	public static Slice<byte[]> newfillArraySliceByte(int length, byte value)
	{
		return wholeArraySliceByte(newfillArrayByte(length, value));
	}
	
	
	
	
	
	
	
	public static void fillArrayChar(char[] array, int offset, int length, char value)
	{
		int e = offset + length;
		for (int i = offset; i < e; i++)
			array[i] = value;
	}
	
	public static void fillArrayChar(char[] array, char value)
	{
		fillArrayChar(array, 0, array.length, value);
	}
	
	public static char[] newfillArrayChar(int length, char value)
	{
		char[] array = new char[length];
		
		if (value != ((char)0))
			fillArrayChar(array, 0, length, value);
		
		return array;
	}
	
	
	
	public static void fillArraySliceChar(@Nonnull Slice<char[]> arraySlice, char value)
	{
		fillArrayChar(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength(), value);
	}
	
	@Nonnull
	public static Slice<char[]> newfillArraySliceChar(int length, char value)
	{
		return wholeArraySliceChar(newfillArrayChar(length, value));
	}
	
	
	
	
	
	
	
	public static void fillArrayShort(short[] array, int offset, int length, short value)
	{
		int e = offset + length;
		for (int i = offset; i < e; i++)
			array[i] = value;
	}
	
	public static void fillArrayShort(short[] array, short value)
	{
		fillArrayShort(array, 0, array.length, value);
	}
	
	public static short[] newfillArrayShort(int length, short value)
	{
		short[] array = new short[length];
		
		if (value != ((short)0))
			fillArrayShort(array, 0, length, value);
		
		return array;
	}
	
	
	
	public static void fillArraySliceShort(@Nonnull Slice<short[]> arraySlice, short value)
	{
		fillArrayShort(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength(), value);
	}
	
	@Nonnull
	public static Slice<short[]> newfillArraySliceShort(int length, short value)
	{
		return wholeArraySliceShort(newfillArrayShort(length, value));
	}
	
	
	
	
	
	
	
	public static void fillArrayFloat(float[] array, int offset, int length, float value)
	{
		int e = offset + length;
		for (int i = offset; i < e; i++)
			array[i] = value;
	}
	
	public static void fillArrayFloat(float[] array, float value)
	{
		fillArrayFloat(array, 0, array.length, value);
	}
	
	public static float[] newfillArrayFloat(int length, float value)
	{
		float[] array = new float[length];
		
		if (value != 0.0f)
			fillArrayFloat(array, 0, length, value);
		
		return array;
	}
	
	
	
	public static void fillArraySliceFloat(@Nonnull Slice<float[]> arraySlice, float value)
	{
		fillArrayFloat(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength(), value);
	}
	
	@Nonnull
	public static Slice<float[]> newfillArraySliceFloat(int length, float value)
	{
		return wholeArraySliceFloat(newfillArrayFloat(length, value));
	}
	
	
	
	
	
	
	
	public static void fillArrayInt(int[] array, int offset, int length, int value)
	{
		int e = offset + length;
		for (int i = offset; i < e; i++)
			array[i] = value;
	}
	
	public static void fillArrayInt(int[] array, int value)
	{
		fillArrayInt(array, 0, array.length, value);
	}
	
	public static int[] newfillArrayInt(int length, int value)
	{
		int[] array = new int[length];
		
		if (value != 0)
			fillArrayInt(array, 0, length, value);
		
		return array;
	}
	
	
	
	public static void fillArraySliceInt(@Nonnull Slice<int[]> arraySlice, int value)
	{
		fillArrayInt(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength(), value);
	}
	
	@Nonnull
	public static Slice<int[]> newfillArraySliceInt(int length, int value)
	{
		return wholeArraySliceInt(newfillArrayInt(length, value));
	}
	
	
	
	
	
	
	
	public static void fillArrayDouble(double[] array, int offset, int length, double value)
	{
		int e = offset + length;
		for (int i = offset; i < e; i++)
			array[i] = value;
	}
	
	public static void fillArrayDouble(double[] array, double value)
	{
		fillArrayDouble(array, 0, array.length, value);
	}
	
	public static double[] newfillArrayDouble(int length, double value)
	{
		double[] array = new double[length];
		
		if (value != 0.0d)
			fillArrayDouble(array, 0, length, value);
		
		return array;
	}
	
	
	
	public static void fillArraySliceDouble(@Nonnull Slice<double[]> arraySlice, double value)
	{
		fillArrayDouble(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength(), value);
	}
	
	@Nonnull
	public static Slice<double[]> newfillArraySliceDouble(int length, double value)
	{
		return wholeArraySliceDouble(newfillArrayDouble(length, value));
	}
	
	
	
	
	
	
	
	public static void fillArrayLong(long[] array, int offset, int length, long value)
	{
		int e = offset + length;
		for (int i = offset; i < e; i++)
			array[i] = value;
	}
	
	public static void fillArrayLong(long[] array, long value)
	{
		fillArrayLong(array, 0, array.length, value);
	}
	
	public static long[] newfillArrayLong(int length, long value)
	{
		long[] array = new long[length];
		
		if (value != 0l)
			fillArrayLong(array, 0, length, value);
		
		return array;
	}
	
	
	
	public static void fillArraySliceLong(@Nonnull Slice<long[]> arraySlice, long value)
	{
		fillArrayLong(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength(), value);
	}
	
	@Nonnull
	public static Slice<long[]> newfillArraySliceLong(int length, long value)
	{
		return wholeArraySliceLong(newfillArrayLong(length, value));
	}
	
	
	
	
	
	
	// >>>
	
	
	
	
	
	//<Array equality comparators!
	
	
	//Man, Eclipse has some funky needs when it comes to indentation! XD
	
	
	public static EqualityComparator<boolean[]> getBooleanArrayEqualityComparator()
	{
		return BooleanArrayEqualityComparator;
	}
	
	protected static final EqualityComparator<boolean[]> BooleanArrayEqualityComparator = new EqualityComparator<boolean[]>
	()
	{
		@Override
		public boolean equals(boolean[] a, boolean[] b)
		{
			return Arrays.equals(a, b);
		}
	};
	
	
	
	public static EqualityComparator<byte[]> getByteArrayEqualityComparator()
	{
		return ByteArrayEqualityComparator;
	}
	
	protected static final EqualityComparator<byte[]> ByteArrayEqualityComparator = new EqualityComparator<byte[]>
	()
	{
		@Override
		public boolean equals(byte[] a, byte[] b)
		{
			return Arrays.equals(a, b);
		}
	};
	
	
	
	public static EqualityComparator<short[]> getShortArrayEqualityComparator()
	{
		return ShortArrayEqualityComparator;
	}
	
	protected static final EqualityComparator<short[]> ShortArrayEqualityComparator = new EqualityComparator<short[]>
	()
	{
		@Override
		public boolean equals(short[] a, short[] b)
		{
			return Arrays.equals(a, b);
		}
	};
	
	
	
	public static EqualityComparator<char[]> getCharArrayEqualityComparator()
	{
		return CharArrayEqualityComparator;
	}
	
	protected static final EqualityComparator<char[]> CharArrayEqualityComparator = new EqualityComparator<char[]>
	()
	{
		@Override
		public boolean equals(char[] a, char[] b)
		{
			return Arrays.equals(a, b);
		}
	};
	
	
	
	public static EqualityComparator<int[]> getIntArrayEqualityComparator()
	{
		return IntArrayEqualityComparator;
	}
	
	protected static final EqualityComparator<int[]> IntArrayEqualityComparator = new EqualityComparator<int[]>
	()
	{
		@Override
		public boolean equals(int[] a, int[] b)
		{
			return Arrays.equals(a, b);
		}
	};
	
	
	
	public static EqualityComparator<float[]> getFloatArrayEqualityComparator()
	{
		return FloatArrayEqualityComparator;
	}
	
	protected static final EqualityComparator<float[]> FloatArrayEqualityComparator = new EqualityComparator<float[]>
	()
	{
		@Override
		public boolean equals(float[] a, float[] b)
		{
			return Arrays.equals(a, b);
		}
	};
	
	
	
	public static EqualityComparator<long[]> getLongArrayEqualityComparator()
	{
		return LongArrayEqualityComparator;
	}
	
	protected static final EqualityComparator<long[]> LongArrayEqualityComparator = new EqualityComparator<long[]>
	()
	{
		@Override
		public boolean equals(long[] a, long[] b)
		{
			return Arrays.equals(a, b);
		}
	};
	
	
	
	public static EqualityComparator<double[]> getDoubleArrayEqualityComparator()
	{
		return DoubleArrayEqualityComparator;
	}
	
	protected static final EqualityComparator<double[]> DoubleArrayEqualityComparator = new EqualityComparator<double[]>
	()
	{
		@Override
		public boolean equals(double[] a, double[] b)
		{
			return Arrays.equals(a, b);
		}
	};
	
	
	
	
	
	public static EqualityComparator<Object[]> getObjectArrayShallowEqualityComparator()
	{
		return ObjectArrayShallowEqualityComparator;
	}
	
	protected static final EqualityComparator<Object[]> ObjectArrayShallowEqualityComparator = new EqualityComparator<Object[]>
	()
	{
		@Override
		public boolean equals(Object[] a, Object[] b)
		{
			return Arrays.equals(a, b);
		}
	};
	
	
	
	public static EqualityComparator<Object[]> getObjectArrayDeepEqualityComparator()
	{
		return ObjectArrayDeepEqualityComparator;
	}
	
	protected static final EqualityComparator<Object[]> ObjectArrayDeepEqualityComparator = new EqualityComparator<Object[]>
	()
	{
		@Override
		public boolean equals(Object[] a, Object[] b)
		{
			return Arrays.deepEquals(a, b);
		}
	};
	
	
	
	
	
	
	public static EqualityComparator getDynamicArrayShallowEqualityComparator()
	{
		return DynamicArrayShallowEqualityComparator;
	}
	
	protected static final EqualityComparator DynamicArrayShallowEqualityComparator = new EqualityComparator<Object>
	()
	{
		@Override
		public boolean equals(Object a, Object b)
		{
			return arrayEqualsPolymorphic(a, b);
		}
	};
	
	
	
	public static EqualityComparator getDynamicArrayDeepEqualityComparator()
	{
		return DynamicArrayShallowEqualityComparator;
	}
	
	protected static final EqualityComparator DynamicArrayDeepEqualityComparator = new EqualityComparator<Object>
	()
	{
		@Override
		public boolean equals(Object a, Object b)
		{
			return arrayDeepEquals(a, b);
		}
	};
	
	
	
	
	
	
	public static EqualityComparator getPrimitiveOrShallowEqualityComparator(Class componentType)
	{
		if (componentType == null)
			throw new NullPointerException();
		if (componentType == Void.class) //there is something that's not a primitive or an Object! XD
			throw new IllegalArgumentException();
		
		if (componentType == boolean.class) return getBooleanArrayEqualityComparator();
		else if (componentType == byte.class) return getByteArrayEqualityComparator();
		else if (componentType == short.class) return getShortArrayEqualityComparator();
		else if (componentType == char.class) return getCharArrayEqualityComparator();
		else if (componentType == int.class) return getIntArrayEqualityComparator();
		else if (componentType == float.class) return getFloatArrayEqualityComparator();
		else if (componentType == long.class) return getLongArrayEqualityComparator();
		else if (componentType == double.class) return getDoubleArrayEqualityComparator();
		else return getObjectArrayShallowEqualityComparator();
	}
	
	public static EqualityComparator getPrimitiveOrDeepEqualityComparator(Class componentType)
	{
		if (componentType == null)
			throw new NullPointerException();
		if (componentType == Void.class) //there is something that's not a primitive or an Object! XD
			throw new IllegalArgumentException();
		
		if (componentType == boolean.class) return getBooleanArrayEqualityComparator();
		else if (componentType == byte.class) return getByteArrayEqualityComparator();
		else if (componentType == short.class) return getShortArrayEqualityComparator();
		else if (componentType == char.class) return getCharArrayEqualityComparator();
		else if (componentType == int.class) return getIntArrayEqualityComparator();
		else if (componentType == float.class) return getFloatArrayEqualityComparator();
		else if (componentType == long.class) return getLongArrayEqualityComparator();
		else if (componentType == double.class) return getDoubleArrayEqualityComparator();
		else return getObjectArrayDeepEqualityComparator();
	}
	
	//Array equality comparators!>
	
	
	
	
	
	
	
	
	
	
	/**
	 * Ie, is fasters, but component type[s] won't be preserved :P
	 * Runtime type of returned array *will actually be*  Object[].class
	 * 
	 * + Null arrays are taken to mean empty arrays :3
	 * 		+ But this will never return null, just a (globally cached!) emtpy array ^w^
	 * 
	 * Note: if there is only one array not empty/null, that is simply returned ^_^
	 * (which is super fast and great if you're reading, but means this isn't guaranteed to return a {@link ThrowAwayValue} :P )
	 */
	@Nonnull
	@PossiblySnapshotPossiblyLiveValue
	public static Object[] concatArraysToObjectArray(Object[]... arrays)
	{
		if (arrays == null || arrays.length == 0)
			return EmptyObjectArray;
		
		
		int totalSize = 0;
		int indexOfFirstNonempty = -1;
		int indexOfLastNonempty = -1;
		{
			for (int i = 0; i < arrays.length; i++)
			{
				Object[] a = arrays[i];
				
				if (a != null && a.length != 0)
				{
					if (indexOfFirstNonempty == -1)
						indexOfFirstNonempty = i;
					
					indexOfLastNonempty = i;
					
					totalSize += a.length;
				}
				else
				{
					//totalSize += 0;  XD
				}
			}
		}
		
		
		if (totalSize == 0)
			return EmptyObjectArray;
		
		
		assert indexOfFirstNonempty != -1 && indexOfLastNonempty != -1; //since totalSize != 0  !!
		
		
		
		assert arrays[indexOfFirstNonempty].length <= totalSize;
		
		if (arrays[indexOfFirstNonempty].length == totalSize)
			return arrays[indexOfFirstNonempty];
		else
		{
			Object[] mergedArray = new Object[totalSize];
			
			int position = 0;
			
			for (int i = indexOfFirstNonempty; i <= indexOfLastNonempty; i++)
			{
				Object[] a = arrays[i];
				System.arraycopy(a, 0, mergedArray, position, a.length);
				position += a.length;
			}
			
			return mergedArray;
		}
	}
	
	
	
	/**
	 * Concatenate the given arrays!
	 */
	public static <E> E concatGeneralArrays(E... arrays)
	{
		Class componentType = null;
		{
			for (E a : arrays)
			{
				if (a != null)
				{
					if (!a.getClass().isArray())
						throw new IllegalArgumentException();
					
					componentType = a.getClass().getComponentType();
					break;
				}
			}
			
			if (componentType == null) //ie, they were all null! ><
				return null; //we can't know which type to make a zero-length array of; so just pass out what they passed in! xD
		}
		
		return concatGeneralArraysExplicitReturnComponentType(componentType, arrays);
	}
	
	/**
	 * Concatenate the given arrays!
	 */
	public static <E> E concatGeneralArraysExplicitReturnComponentType(Class componentType, E... arrays)
	{
		/*
		else if (componentType == _$$prim$$_.class)
			return (E)concatArrays((_$$prim$$_[][])arrays);
		 */
		if (componentType == boolean.class)
			return (E)concatArrays((boolean[][])arrays);
		else if (componentType == byte.class)
			return (E)concatArrays((byte[][])arrays);
		else if (componentType == char.class)
			return (E)concatArrays((char[][])arrays);
		else if (componentType == short.class)
			return (E)concatArrays((short[][])arrays);
		else if (componentType == float.class)
			return (E)concatArrays((float[][])arrays);
		else if (componentType == int.class)
			return (E)concatArrays((int[][])arrays);
		else if (componentType == double.class)
			return (E)concatArrays((double[][])arrays);
		else if (componentType == long.class)
			return (E)concatArrays((long[][])arrays);
		else
		{
			int resultingLength = 0;
			{
				for (Object[] a : (Object[][])arrays)
				{
					resultingLength += a != null ? a.length : 0;
				}
			}
			
			
			Object[] newArray = (Object[])Array.newInstance(componentType, resultingLength);
			
			
			int cursor = 0;
			
			for (Object[] a : (Object[][])arrays)
			{
				if (a != null && a.length != 0)
				{
					System.arraycopy(a, 0, newArray, cursor, a.length);
				}
				
				cursor += a != null ? a.length : 0;
			}
			
			
			return (E)newArray;
		}
	}
	
	
	
	
	
	
	
	
	public static <E> E[] concatArrays(E[]... arrays)
	{
		return (E[])concatGeneralArrays((Object[])arrays);
	}
	
	public static <E> E[] concatArraysExplicitReturnComponentType(Class componentType, E[]... arrays)
	{
		return (E[])concatGeneralArraysExplicitReturnComponentType(componentType, (Object[])arrays);
	}
	
	
	
	
	public static <E> E[] concatArrayWith1(E[] array, E singleton)
	{
		E[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = singleton;
		return newArray;
	}
	
	/*
	public static _$$prim$$_[] concatArrayWith1(_$$prim$$_[] array, _$$prim$$_ singleton)
	{
		_$$prim$$_[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = singleton;
		return newArray;
	}
	
	 */
	
	public static boolean[] concatArrayWith1(boolean[] array, boolean singleton)
	{
		boolean[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = singleton;
		return newArray;
	}
	
	public static byte[] concatArrayWith1(byte[] array, byte singleton)
	{
		byte[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = singleton;
		return newArray;
	}
	
	public static char[] concatArrayWith1(char[] array, char singleton)
	{
		char[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = singleton;
		return newArray;
	}
	
	public static short[] concatArrayWith1(short[] array, short singleton)
	{
		short[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = singleton;
		return newArray;
	}
	
	public static float[] concatArrayWith1(float[] array, float singleton)
	{
		float[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = singleton;
		return newArray;
	}
	
	public static int[] concatArrayWith1(int[] array, int singleton)
	{
		int[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = singleton;
		return newArray;
	}
	
	public static double[] concatArrayWith1(double[] array, double singleton)
	{
		double[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = singleton;
		return newArray;
	}
	
	public static long[] concatArrayWith1(long[] array, long singleton)
	{
		long[] newArray = Arrays.copyOf(array, array.length + 1);
		newArray[array.length] = singleton;
		return newArray;
	}
	
	
	
	
	
	public static <E> E[] concat1WithArray(E singleton, E[] array)
	{
		E[] newArray = (E[])Array.newInstance(array.getClass().getComponentType(), array.length+1);
		newArray[0] = singleton;
		System.arraycopy(array, 0, newArray, 1, array.length);
		return newArray;
	}
	
	/*
	public static _$$prim$$_[] concat1WithArray(_$$prim$$_ singleton, _$$prim$$_[] array)
	{
		_$$prim$$_[] newArray = new _$$prim$$_[array.length+1];
		newArray[0] = singleton;
		System.arraycopy(array, 0, newArray, 1, array.length);
		return newArray;
	}
	
	 */
	
	public static boolean[] concat1WithArray(boolean singleton, boolean[] array)
	{
		boolean[] newArray = new boolean[array.length+1];
		newArray[0] = singleton;
		System.arraycopy(array, 0, newArray, 1, array.length);
		return newArray;
	}
	
	public static byte[] concat1WithArray(byte singleton, byte[] array)
	{
		byte[] newArray = new byte[array.length+1];
		newArray[0] = singleton;
		System.arraycopy(array, 0, newArray, 1, array.length);
		return newArray;
	}
	
	public static short[] concat1WithArray(short singleton, short[] array)
	{
		short[] newArray = new short[array.length+1];
		newArray[0] = singleton;
		System.arraycopy(array, 0, newArray, 1, array.length);
		return newArray;
	}
	
	public static char[] concat1WithArray(char singleton, char[] array)
	{
		char[] newArray = new char[array.length+1];
		newArray[0] = singleton;
		System.arraycopy(array, 0, newArray, 1, array.length);
		return newArray;
	}
	
	public static int[] concat1WithArray(int singleton, int[] array)
	{
		int[] newArray = new int[array.length+1];
		newArray[0] = singleton;
		System.arraycopy(array, 0, newArray, 1, array.length);
		return newArray;
	}
	
	public static float[] concat1WithArray(float singleton, float[] array)
	{
		float[] newArray = new float[array.length+1];
		newArray[0] = singleton;
		System.arraycopy(array, 0, newArray, 1, array.length);
		return newArray;
	}
	
	public static long[] concat1WithArray(long singleton, long[] array)
	{
		long[] newArray = new long[array.length+1];
		newArray[0] = singleton;
		System.arraycopy(array, 0, newArray, 1, array.length);
		return newArray;
	}
	
	public static double[] concat1WithArray(double singleton, double[] array)
	{
		double[] newArray = new double[array.length+1];
		newArray[0] = singleton;
		System.arraycopy(array, 0, newArray, 1, array.length);
		return newArray;
	}
	
	
	
	
	
	public static <E> E[] concatArrayWith2(E[] array, E singletonNp0, E singletonNp1)
	{
		E[] newArray = (E[])Array.newInstance(array.getClass().getComponentType(), array.length+2);
		System.arraycopy(array, 0, newArray, 0, array.length);
		newArray[array.length+0] = singletonNp0;
		newArray[array.length+1] = singletonNp1;
		return newArray;
	}
	
	public static <E> E[] concat2WithArray(E singleton0, E singleton1, E[] array)
	{
		E[] newArray = (E[])Array.newInstance(array.getClass().getComponentType(), array.length+2);
		newArray[0] = singleton0;
		newArray[1] = singleton1;
		System.arraycopy(array, 0, newArray, 2, array.length);
		return newArray;
	}
	
	
	
	
	
	
	
	/**
	 * Out-of-place!
	 */
	public static <E> E[] uniqueifyArray(E[] array)
	{
		if (array == null)
			return null;
		
		Set<E> set = new HashSet<E>(Arrays.asList(array));
		E[] newArray = set.toArray((E[])Array.newInstance(array.getClass().getComponentType(), set.size()));
		Arrays.sort(newArray);
		return newArray;
	}
	
	//Todo uniqueifyArray for the primitives!
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void swapPairs(Object[] array, int start, int lengthInIndividualElements)
	{
		if ((lengthInIndividualElements % 2) != 0)
			throw new IllegalArgumentException("length is not a multiple of cycle size (2, since it's pairs!) ><    length="+lengthInIndividualElements);
		
		if (lengthInIndividualElements < 0)
			throw new IndexOutOfBoundsException("negative length! ><   "+lengthInIndividualElements);
		
		if (start < 0)
			throw new ArrayIndexOutOfBoundsException("negative index! ><   "+start);
		
		if (start + lengthInIndividualElements > array.length)
			throw new ArrayIndexOutOfBoundsException("excessive ending index! ><   "+(start+lengthInIndividualElements)+" > "+array.length);
		
		int numberOfCycles = lengthInIndividualElements / 2;
		
		Object t = null;
		for (int i = 0; i < numberOfCycles; i++)
		{
			t = array[start+i*2+0];
			array[start+i*2+0] = array[start+i*2+1];
			array[start+i*2+1] = t;
		}
	}
	
	public static void swapPairs(Object[] array)
	{
		swapPairs(array, 0, array.length);
	}
	
	
	
	
	
	
	
	public static <E> void swapArrayReferences(E[] array, int a, int b)
	{
		E s = array[a];
		array[a] = array[b];
		array[b] = s;
	}
	
	/* <<<
	primxp
	
	public static void swapArray_$$Prim$$_s(_$$prim$$_[] array, int a, int b)
	{
		_$$prim$$_ s = array[a];
		array[a] = array[b];
		array[b] = s;
	}
	
	 */
	
	public static void swapArrayBooleans(boolean[] array, int a, int b)
	{
		boolean s = array[a];
		array[a] = array[b];
		array[b] = s;
	}
	
	
	public static void swapArrayBytes(byte[] array, int a, int b)
	{
		byte s = array[a];
		array[a] = array[b];
		array[b] = s;
	}
	
	
	public static void swapArrayChars(char[] array, int a, int b)
	{
		char s = array[a];
		array[a] = array[b];
		array[b] = s;
	}
	
	
	public static void swapArrayShorts(short[] array, int a, int b)
	{
		short s = array[a];
		array[a] = array[b];
		array[b] = s;
	}
	
	
	public static void swapArrayFloats(float[] array, int a, int b)
	{
		float s = array[a];
		array[a] = array[b];
		array[b] = s;
	}
	
	
	public static void swapArrayInts(int[] array, int a, int b)
	{
		int s = array[a];
		array[a] = array[b];
		array[b] = s;
	}
	
	
	public static void swapArrayDoubles(double[] array, int a, int b)
	{
		double s = array[a];
		array[a] = array[b];
		array[b] = s;
	}
	
	
	public static void swapArrayLongs(long[] array, int a, int b)
	{
		long s = array[a];
		array[a] = array[b];
		array[b] = s;
	}
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> E[] sliceR(E[] array, int inclusiveLowBound, int exclusiveHighBound)
	{
		int len = array.length;
		
		if (inclusiveLowBound == 0 && exclusiveHighBound == len)
			return array;  //Hence the PossiblyLiveValue part ;>
		
		if (inclusiveLowBound != len) //leave it if == length!!
			inclusiveLowBound = SmallIntegerMathUtilities.progmod(inclusiveLowBound, len);
		
		if (exclusiveHighBound != len) //leave it if == length!!
			exclusiveHighBound = SmallIntegerMathUtilities.progmod(exclusiveHighBound, len);
		
		//Swap if incorrect order, to be nice :3
		//Todo should we reverse the order??? :>?
		if (exclusiveHighBound < inclusiveLowBound)
		{
			int t = exclusiveHighBound;
			exclusiveHighBound = inclusiveLowBound;
			inclusiveLowBound = t;
		}
		
		assert inclusiveLowBound >= 0;
		assert inclusiveLowBound <= len;
		assert exclusiveHighBound >= 0;
		assert exclusiveHighBound <= len;
		
		E[] slice = (E[])Array.newInstance(array.getClass().getComponentType(), exclusiveHighBound - inclusiveLowBound);
		
		System.arraycopy(array, inclusiveLowBound, slice, 0, slice.length);
		
		return slice;
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static <E> E[] sliceR(E[] array, int inclusiveLowBound)
	{
		return sliceR(array, inclusiveLowBound, array.length);
	}
	
	
	
	@ThrowAwayValue
	public static <E> E[] slice(E[] array, int inclusiveLowBound, int exclusiveHighBound)
	{
		if (inclusiveLowBound == 0 && exclusiveHighBound == array.length)
			return array.clone();
		else
			return sliceR(array, inclusiveLowBound, exclusiveHighBound);
	}
	
	@ThrowAwayValue
	public static <E> E[] slice(E[] array, int inclusiveLowBound)
	{
		return slice(array, inclusiveLowBound, array.length);
	}
	
	
	
	
	/*<<<
primxp
	@PossiblySnapshotPossiblyLiveValue
	public static _$$prim$$_[] sliceR(_$$prim$$_[] array, int inclusiveLowBound, int exclusiveHighBound)
	{
		int len = array.length;
		
		if (inclusiveLowBound == 0 && exclusiveHighBound == len)
			return array;  //Hence the PossiblyLiveValue part ;>
		
		if (inclusiveLowBound != len) //leave it if == length!!
			inclusiveLowBound = progmod(inclusiveLowBound, len);
		
		if (exclusiveHighBound != len) //leave it if == length!!
			exclusiveHighBound = progmod(exclusiveHighBound, len);
		
		//Swap if incorrect order, to be nice :3
		if (exclusiveHighBound < inclusiveLowBound)
		{
			int t = exclusiveHighBound;
			exclusiveHighBound = inclusiveLowBound;
			inclusiveLowBound = t;
		}
		
		assert inclusiveLowBound >= 0;
		assert inclusiveLowBound <= len;
		assert exclusiveHighBound >= 0;
		assert exclusiveHighBound <= len;
		
		_$$prim$$_[] slice = new _$$prim$$_[exclusiveHighBound - inclusiveLowBound];
		
		System.arraycopy(array, inclusiveLowBound, slice, 0, slice.length);
		
		return slice;
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static _$$prim$$_[] sliceR(_$$prim$$_[] array, int inclusiveLowBound)
	{
		return sliceR(array, inclusiveLowBound, array.length);
	}
	
	
	
	@ThrowAwayValue
	public static _$$prim$$_[] slice(_$$prim$$_[] array, int inclusiveLowBound, int exclusiveHighBound)
	{
		if (inclusiveLowBound == 0 && exclusiveHighBound == array.length)
			return array.clone();
		else
			return sliceR(array, inclusiveLowBound, exclusiveHighBound);
	}
	
	@ThrowAwayValue
	public static _$$prim$$_[] slice(_$$prim$$_[] array, int inclusiveLowBound)
	{
		return slice(array, inclusiveLowBound, array.length);
	}
	
	
	
	
	
	 */
	@PossiblySnapshotPossiblyLiveValue
	public static boolean[] sliceR(boolean[] array, int inclusiveLowBound, int exclusiveHighBound)
	{
		int len = array.length;
		
		if (inclusiveLowBound == 0 && exclusiveHighBound == len)
			return array;  //Hence the PossiblyLiveValue part ;>
		
		if (inclusiveLowBound != len) //leave it if == length!!
			inclusiveLowBound = progmod(inclusiveLowBound, len);
		
		if (exclusiveHighBound != len) //leave it if == length!!
			exclusiveHighBound = progmod(exclusiveHighBound, len);
		
		//Swap if incorrect order, to be nice :3
		if (exclusiveHighBound < inclusiveLowBound)
		{
			int t = exclusiveHighBound;
			exclusiveHighBound = inclusiveLowBound;
			inclusiveLowBound = t;
		}
		
		assert inclusiveLowBound >= 0;
		assert inclusiveLowBound <= len;
		assert exclusiveHighBound >= 0;
		assert exclusiveHighBound <= len;
		
		boolean[] slice = new boolean[exclusiveHighBound - inclusiveLowBound];
		
		System.arraycopy(array, inclusiveLowBound, slice, 0, slice.length);
		
		return slice;
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static boolean[] sliceR(boolean[] array, int inclusiveLowBound)
	{
		return sliceR(array, inclusiveLowBound, array.length);
	}
	
	
	
	@ThrowAwayValue
	public static boolean[] slice(boolean[] array, int inclusiveLowBound, int exclusiveHighBound)
	{
		if (inclusiveLowBound == 0 && exclusiveHighBound == array.length)
			return array.clone();
		else
			return sliceR(array, inclusiveLowBound, exclusiveHighBound);
	}
	
	@ThrowAwayValue
	public static boolean[] slice(boolean[] array, int inclusiveLowBound)
	{
		return slice(array, inclusiveLowBound, array.length);
	}
	
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static byte[] sliceR(byte[] array, int inclusiveLowBound, int exclusiveHighBound)
	{
		int len = array.length;
		
		if (inclusiveLowBound == 0 && exclusiveHighBound == len)
			return array;  //Hence the PossiblyLiveValue part ;>
		
		if (inclusiveLowBound != len) //leave it if == length!!
			inclusiveLowBound = progmod(inclusiveLowBound, len);
		
		if (exclusiveHighBound != len) //leave it if == length!!
			exclusiveHighBound = progmod(exclusiveHighBound, len);
		
		//Swap if incorrect order, to be nice :3
		if (exclusiveHighBound < inclusiveLowBound)
		{
			int t = exclusiveHighBound;
			exclusiveHighBound = inclusiveLowBound;
			inclusiveLowBound = t;
		}
		
		assert inclusiveLowBound >= 0;
		assert inclusiveLowBound <= len;
		assert exclusiveHighBound >= 0;
		assert exclusiveHighBound <= len;
		
		byte[] slice = new byte[exclusiveHighBound - inclusiveLowBound];
		
		System.arraycopy(array, inclusiveLowBound, slice, 0, slice.length);
		
		return slice;
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static byte[] sliceR(byte[] array, int inclusiveLowBound)
	{
		return sliceR(array, inclusiveLowBound, array.length);
	}
	
	
	
	@ThrowAwayValue
	public static byte[] slice(byte[] array, int inclusiveLowBound, int exclusiveHighBound)
	{
		if (inclusiveLowBound == 0 && exclusiveHighBound == array.length)
			return array.clone();
		else
			return sliceR(array, inclusiveLowBound, exclusiveHighBound);
	}
	
	@ThrowAwayValue
	public static byte[] slice(byte[] array, int inclusiveLowBound)
	{
		return slice(array, inclusiveLowBound, array.length);
	}
	
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static char[] sliceR(char[] array, int inclusiveLowBound, int exclusiveHighBound)
	{
		int len = array.length;
		
		if (inclusiveLowBound == 0 && exclusiveHighBound == len)
			return array;  //Hence the PossiblyLiveValue part ;>
		
		if (inclusiveLowBound != len) //leave it if == length!!
			inclusiveLowBound = progmod(inclusiveLowBound, len);
		
		if (exclusiveHighBound != len) //leave it if == length!!
			exclusiveHighBound = progmod(exclusiveHighBound, len);
		
		//Swap if incorrect order, to be nice :3
		if (exclusiveHighBound < inclusiveLowBound)
		{
			int t = exclusiveHighBound;
			exclusiveHighBound = inclusiveLowBound;
			inclusiveLowBound = t;
		}
		
		assert inclusiveLowBound >= 0;
		assert inclusiveLowBound <= len;
		assert exclusiveHighBound >= 0;
		assert exclusiveHighBound <= len;
		
		char[] slice = new char[exclusiveHighBound - inclusiveLowBound];
		
		System.arraycopy(array, inclusiveLowBound, slice, 0, slice.length);
		
		return slice;
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static char[] sliceR(char[] array, int inclusiveLowBound)
	{
		return sliceR(array, inclusiveLowBound, array.length);
	}
	
	
	
	@ThrowAwayValue
	public static char[] slice(char[] array, int inclusiveLowBound, int exclusiveHighBound)
	{
		if (inclusiveLowBound == 0 && exclusiveHighBound == array.length)
			return array.clone();
		else
			return sliceR(array, inclusiveLowBound, exclusiveHighBound);
	}
	
	@ThrowAwayValue
	public static char[] slice(char[] array, int inclusiveLowBound)
	{
		return slice(array, inclusiveLowBound, array.length);
	}
	
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static short[] sliceR(short[] array, int inclusiveLowBound, int exclusiveHighBound)
	{
		int len = array.length;
		
		if (inclusiveLowBound == 0 && exclusiveHighBound == len)
			return array;  //Hence the PossiblyLiveValue part ;>
		
		if (inclusiveLowBound != len) //leave it if == length!!
			inclusiveLowBound = progmod(inclusiveLowBound, len);
		
		if (exclusiveHighBound != len) //leave it if == length!!
			exclusiveHighBound = progmod(exclusiveHighBound, len);
		
		//Swap if incorrect order, to be nice :3
		if (exclusiveHighBound < inclusiveLowBound)
		{
			int t = exclusiveHighBound;
			exclusiveHighBound = inclusiveLowBound;
			inclusiveLowBound = t;
		}
		
		assert inclusiveLowBound >= 0;
		assert inclusiveLowBound <= len;
		assert exclusiveHighBound >= 0;
		assert exclusiveHighBound <= len;
		
		short[] slice = new short[exclusiveHighBound - inclusiveLowBound];
		
		System.arraycopy(array, inclusiveLowBound, slice, 0, slice.length);
		
		return slice;
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static short[] sliceR(short[] array, int inclusiveLowBound)
	{
		return sliceR(array, inclusiveLowBound, array.length);
	}
	
	
	
	@ThrowAwayValue
	public static short[] slice(short[] array, int inclusiveLowBound, int exclusiveHighBound)
	{
		if (inclusiveLowBound == 0 && exclusiveHighBound == array.length)
			return array.clone();
		else
			return sliceR(array, inclusiveLowBound, exclusiveHighBound);
	}
	
	@ThrowAwayValue
	public static short[] slice(short[] array, int inclusiveLowBound)
	{
		return slice(array, inclusiveLowBound, array.length);
	}
	
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static float[] sliceR(float[] array, int inclusiveLowBound, int exclusiveHighBound)
	{
		int len = array.length;
		
		if (inclusiveLowBound == 0 && exclusiveHighBound == len)
			return array;  //Hence the PossiblyLiveValue part ;>
		
		if (inclusiveLowBound != len) //leave it if == length!!
			inclusiveLowBound = progmod(inclusiveLowBound, len);
		
		if (exclusiveHighBound != len) //leave it if == length!!
			exclusiveHighBound = progmod(exclusiveHighBound, len);
		
		//Swap if incorrect order, to be nice :3
		if (exclusiveHighBound < inclusiveLowBound)
		{
			int t = exclusiveHighBound;
			exclusiveHighBound = inclusiveLowBound;
			inclusiveLowBound = t;
		}
		
		assert inclusiveLowBound >= 0;
		assert inclusiveLowBound <= len;
		assert exclusiveHighBound >= 0;
		assert exclusiveHighBound <= len;
		
		float[] slice = new float[exclusiveHighBound - inclusiveLowBound];
		
		System.arraycopy(array, inclusiveLowBound, slice, 0, slice.length);
		
		return slice;
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static float[] sliceR(float[] array, int inclusiveLowBound)
	{
		return sliceR(array, inclusiveLowBound, array.length);
	}
	
	
	
	@ThrowAwayValue
	public static float[] slice(float[] array, int inclusiveLowBound, int exclusiveHighBound)
	{
		if (inclusiveLowBound == 0 && exclusiveHighBound == array.length)
			return array.clone();
		else
			return sliceR(array, inclusiveLowBound, exclusiveHighBound);
	}
	
	@ThrowAwayValue
	public static float[] slice(float[] array, int inclusiveLowBound)
	{
		return slice(array, inclusiveLowBound, array.length);
	}
	
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static int[] sliceR(int[] array, int inclusiveLowBound, int exclusiveHighBound)
	{
		int len = array.length;
		
		if (inclusiveLowBound == 0 && exclusiveHighBound == len)
			return array;  //Hence the PossiblyLiveValue part ;>
		
		if (inclusiveLowBound != len) //leave it if == length!!
			inclusiveLowBound = progmod(inclusiveLowBound, len);
		
		if (exclusiveHighBound != len) //leave it if == length!!
			exclusiveHighBound = progmod(exclusiveHighBound, len);
		
		//Swap if incorrect order, to be nice :3
		if (exclusiveHighBound < inclusiveLowBound)
		{
			int t = exclusiveHighBound;
			exclusiveHighBound = inclusiveLowBound;
			inclusiveLowBound = t;
		}
		
		assert inclusiveLowBound >= 0;
		assert inclusiveLowBound <= len;
		assert exclusiveHighBound >= 0;
		assert exclusiveHighBound <= len;
		
		int[] slice = new int[exclusiveHighBound - inclusiveLowBound];
		
		System.arraycopy(array, inclusiveLowBound, slice, 0, slice.length);
		
		return slice;
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static int[] sliceR(int[] array, int inclusiveLowBound)
	{
		return sliceR(array, inclusiveLowBound, array.length);
	}
	
	
	
	@ThrowAwayValue
	public static int[] slice(int[] array, int inclusiveLowBound, int exclusiveHighBound)
	{
		if (inclusiveLowBound == 0 && exclusiveHighBound == array.length)
			return array.clone();
		else
			return sliceR(array, inclusiveLowBound, exclusiveHighBound);
	}
	
	@ThrowAwayValue
	public static int[] slice(int[] array, int inclusiveLowBound)
	{
		return slice(array, inclusiveLowBound, array.length);
	}
	
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static double[] sliceR(double[] array, int inclusiveLowBound, int exclusiveHighBound)
	{
		int len = array.length;
		
		if (inclusiveLowBound == 0 && exclusiveHighBound == len)
			return array;  //Hence the PossiblyLiveValue part ;>
		
		if (inclusiveLowBound != len) //leave it if == length!!
			inclusiveLowBound = progmod(inclusiveLowBound, len);
		
		if (exclusiveHighBound != len) //leave it if == length!!
			exclusiveHighBound = progmod(exclusiveHighBound, len);
		
		//Swap if incorrect order, to be nice :3
		if (exclusiveHighBound < inclusiveLowBound)
		{
			int t = exclusiveHighBound;
			exclusiveHighBound = inclusiveLowBound;
			inclusiveLowBound = t;
		}
		
		assert inclusiveLowBound >= 0;
		assert inclusiveLowBound <= len;
		assert exclusiveHighBound >= 0;
		assert exclusiveHighBound <= len;
		
		double[] slice = new double[exclusiveHighBound - inclusiveLowBound];
		
		System.arraycopy(array, inclusiveLowBound, slice, 0, slice.length);
		
		return slice;
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static double[] sliceR(double[] array, int inclusiveLowBound)
	{
		return sliceR(array, inclusiveLowBound, array.length);
	}
	
	
	
	@ThrowAwayValue
	public static double[] slice(double[] array, int inclusiveLowBound, int exclusiveHighBound)
	{
		if (inclusiveLowBound == 0 && exclusiveHighBound == array.length)
			return array.clone();
		else
			return sliceR(array, inclusiveLowBound, exclusiveHighBound);
	}
	
	@ThrowAwayValue
	public static double[] slice(double[] array, int inclusiveLowBound)
	{
		return slice(array, inclusiveLowBound, array.length);
	}
	
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static long[] sliceR(long[] array, int inclusiveLowBound, int exclusiveHighBound)
	{
		int len = array.length;
		
		if (inclusiveLowBound == 0 && exclusiveHighBound == len)
			return array;  //Hence the PossiblyLiveValue part ;>
		
		if (inclusiveLowBound != len) //leave it if == length!!
			inclusiveLowBound = progmod(inclusiveLowBound, len);
		
		if (exclusiveHighBound != len) //leave it if == length!!
			exclusiveHighBound = progmod(exclusiveHighBound, len);
		
		//Swap if incorrect order, to be nice :3
		if (exclusiveHighBound < inclusiveLowBound)
		{
			int t = exclusiveHighBound;
			exclusiveHighBound = inclusiveLowBound;
			inclusiveLowBound = t;
		}
		
		assert inclusiveLowBound >= 0;
		assert inclusiveLowBound <= len;
		assert exclusiveHighBound >= 0;
		assert exclusiveHighBound <= len;
		
		long[] slice = new long[exclusiveHighBound - inclusiveLowBound];
		
		System.arraycopy(array, inclusiveLowBound, slice, 0, slice.length);
		
		return slice;
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static long[] sliceR(long[] array, int inclusiveLowBound)
	{
		return sliceR(array, inclusiveLowBound, array.length);
	}
	
	
	
	@ThrowAwayValue
	public static long[] slice(long[] array, int inclusiveLowBound, int exclusiveHighBound)
	{
		if (inclusiveLowBound == 0 && exclusiveHighBound == array.length)
			return array.clone();
		else
			return sliceR(array, inclusiveLowBound, exclusiveHighBound);
	}
	
	@ThrowAwayValue
	public static long[] slice(long[] array, int inclusiveLowBound)
	{
		return slice(array, inclusiveLowBound, array.length);
	}
	
	
	
	
	
	//>>>
	
	
	
	
	
	
	
	
	
	
	
	
	public static <T> T cloneArray(T array)
	{
		/*
		else if (array instanceof _$$prim$$_[])
			return (T)((_$$prim$$_[])array).clone();
		 */
		if (array == null)
			return null;
		else if (array instanceof Object[])
			return (T)((Object[])array).clone();
		else if (array instanceof boolean[])
			return (T)((boolean[])array).clone();
		else if (array instanceof byte[])
			return (T)((byte[])array).clone();
		else if (array instanceof char[])
			return (T)((char[])array).clone();
		else if (array instanceof short[])
			return (T)((short[])array).clone();
		else if (array instanceof float[])
			return (T)((float[])array).clone();
		else if (array instanceof int[])
			return (T)((int[])array).clone();
		else if (array instanceof double[])
			return (T)((double[])array).clone();
		else if (array instanceof long[])
			return (T)((long[])array).clone();
		else
			throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(array);
	}
	
	
	
	
	
	
	
	
	public static int getAndValidateAllSameLength(Object[] arrayOfArrays)
	{
		if (arrayOfArrays == null)
			throw new NullPointerException();
		if (arrayOfArrays.length == 0)
			throw new IllegalArgumentException("No sub-arrays to extract second dimension's length from ;_;");
		
		int first = Array.getLength(arrayOfArrays[0]);
		
		for (int i = 1; i < arrayOfArrays.length; i++)
			if (Array.getLength(arrayOfArrays[i]) != first)
				throw new IllegalArgumentException("Second dimensions' lengths are not all equal! D:");
		
		return first;
	}
	
	
	/**
	 * @return the length they all are (not counting nonarrays), or -1 if they are all nonarrays! 0,0
	 */
	public static int getAndValidateAllSameLengthSkippingNonarrays(Object[] arrayOfArrays)
	{
		if (arrayOfArrays == null)
			throw new NullPointerException();
		if (arrayOfArrays.length == 0)
			throw new IllegalArgumentException("No sub-arrays to extract second dimension's length from ;_;");
		
		
		int firstIndex = -1;
		{
			for (int i = 0; i < arrayOfArrays.length; i++)
			{
				if (arrayOfArrays[i] != null && arrayOfArrays[i].getClass().isArray())
				{
					firstIndex = i;
					break;
				}
			}
		}
		
		
		if (firstIndex == -1)
			return -1;
		
		
		int first = Array.getLength(arrayOfArrays[firstIndex]);
		
		for (int i = firstIndex+1; i < arrayOfArrays.length; i++)
			if (arrayOfArrays[i] != null && arrayOfArrays[i].getClass().isArray() && Array.getLength(arrayOfArrays[i]) != first)
				throw new IllegalArgumentException("Second dimensions' lengths are not all equal! D:");
		
		return first;
	}
	
	
	//Todo public static int getAndValidateHyperRectangular(Object[] multidimensionalArray, int dimension) ??
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * Copy element-bounded/exact regions of primitive arrays as if they were bitfields :>
	 * 
	 * OBVIOUSLY THIS WAS GENERATED WITH CODE
	 * I DON'T HAVE *THAT* MUCH TIME X'D
	 */
	
	//Todo do we need different-endian versions?? no, right?  (for byte-endianness? for bit-endianness??)
	
	
	
	
	public static void copyBitfields(boolean[] source, boolean[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(boolean[] source, int sourceOffset, int sourceLength, boolean[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//1 == 1
		int length = 0;
		{
			if (sourceLength == -1 && destLength == -1)
				throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
			else if (sourceLength == -1)
				length = destLength = sourceLength;
			else if (destLength == -1)
				length = sourceLength = destLength;
			else
			{
				//Minimize them to the overlap betweens them :>
				length = sourceLength = destLength = SmallIntegerMathUtilities.least(sourceLength, destLength);
			}
		}
		
		//boolean == boolean
		System.arraycopy(source, sourceOffset, dest, destOffset, length);
	}
	
	
	
	public static void copyBitfields(boolean[] source, byte[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(boolean[] source, int sourceOffset, int sourceLength, byte[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//1 != 8
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 1, 8);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 8, 1);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 1;
			long destLengthInBits = destLength * 8;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 1));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 8));
		}
		
		
		//8 > 1  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*1)/8;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 8/1; e++)
			{
				boolean s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 1 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 1;
			}
			
			byte d = bitcastFromLongToByte(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(boolean[] source, char[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(boolean[] source, int sourceOffset, int sourceLength, char[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//1 != 16
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 1, 16);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 16, 1);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 1;
			long destLengthInBits = destLength * 16;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 1));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
		}
		
		
		//16 > 1  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*1)/16;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 16/1; e++)
			{
				boolean s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 1 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 1;
			}
			
			char d = bitcastFromLongToChar(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(boolean[] source, short[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(boolean[] source, int sourceOffset, int sourceLength, short[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//1 != 16
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 1, 16);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 16, 1);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 1;
			long destLengthInBits = destLength * 16;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 1));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
		}
		
		
		//16 > 1  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*1)/16;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 16/1; e++)
			{
				boolean s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 1 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 1;
			}
			
			short d = bitcastFromLongToShort(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(boolean[] source, float[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(boolean[] source, int sourceOffset, int sourceLength, float[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//1 != 32
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 1, 32);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 32, 1);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 1;
			long destLengthInBits = destLength * 32;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 1));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
		}
		
		
		//32 > 1  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*1)/32;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 32/1; e++)
			{
				boolean s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 1 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 1;
			}
			
			float d = bitcastFromLongToFloat(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(boolean[] source, int[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(boolean[] source, int sourceOffset, int sourceLength, int[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//1 != 32
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 1, 32);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 32, 1);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 1;
			long destLengthInBits = destLength * 32;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 1));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
		}
		
		
		//32 > 1  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*1)/32;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 32/1; e++)
			{
				boolean s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 1 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 1;
			}
			
			int d = bitcastFromLongToInt(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(boolean[] source, double[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(boolean[] source, int sourceOffset, int sourceLength, double[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//1 != 64
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 1, 64);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 64, 1);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 1;
			long destLengthInBits = destLength * 64;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 1));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
		}
		
		
		//64 > 1  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*1)/64;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 64/1; e++)
			{
				boolean s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 1 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 1;
			}
			
			double d = bitcastFromLongToDouble(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(boolean[] source, long[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(boolean[] source, int sourceOffset, int sourceLength, long[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//1 != 64
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 1, 64);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 64, 1);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 1;
			long destLengthInBits = destLength * 64;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 1));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
		}
		
		
		//64 > 1  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*1)/64;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 64/1; e++)
			{
				boolean s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 1 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 1;
			}
			
			long d = bitcastFromLongToLong(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(byte[] source, boolean[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(byte[] source, int sourceOffset, int sourceLength, boolean[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//8 != 1
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 8, 1);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 1, 8);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 8;
			long destLengthInBits = destLength * 1;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 8));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 1));
		}
		
		
		//8 > 1  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*1)/8;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			byte s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 8/1; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 1)) & ((1L << 1) - 1));
				boolean d = bitcastFromLongToBoolean(shiftedAndMasked);
				dest[i*(8/1)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(byte[] source, byte[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(byte[] source, int sourceOffset, int sourceLength, byte[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//8 == 8
		int length = 0;
		{
			if (sourceLength == -1 && destLength == -1)
				throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
			else if (sourceLength == -1)
				length = destLength = sourceLength;
			else if (destLength == -1)
				length = sourceLength = destLength;
			else
			{
				//Minimize them to the overlap betweens them :>
				length = sourceLength = destLength = SmallIntegerMathUtilities.least(sourceLength, destLength);
			}
		}
		
		//byte == byte
		System.arraycopy(source, sourceOffset, dest, destOffset, length);
	}
	
	
	
	public static void copyBitfields(byte[] source, char[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(byte[] source, int sourceOffset, int sourceLength, char[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//8 != 16
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 8, 16);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 16, 8);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 8;
			long destLengthInBits = destLength * 16;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 8));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
		}
		
		
		//16 > 8  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*8)/16;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 16/8; e++)
			{
				byte s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 8 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 8;
			}
			
			char d = bitcastFromLongToChar(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(byte[] source, short[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(byte[] source, int sourceOffset, int sourceLength, short[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//8 != 16
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 8, 16);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 16, 8);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 8;
			long destLengthInBits = destLength * 16;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 8));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
		}
		
		
		//16 > 8  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*8)/16;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 16/8; e++)
			{
				byte s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 8 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 8;
			}
			
			short d = bitcastFromLongToShort(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(byte[] source, float[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(byte[] source, int sourceOffset, int sourceLength, float[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//8 != 32
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 8, 32);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 32, 8);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 8;
			long destLengthInBits = destLength * 32;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 8));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
		}
		
		
		//32 > 8  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*8)/32;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 32/8; e++)
			{
				byte s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 8 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 8;
			}
			
			float d = bitcastFromLongToFloat(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(byte[] source, int[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(byte[] source, int sourceOffset, int sourceLength, int[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//8 != 32
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 8, 32);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 32, 8);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 8;
			long destLengthInBits = destLength * 32;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 8));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
		}
		
		
		//32 > 8  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*8)/32;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 32/8; e++)
			{
				byte s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 8 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 8;
			}
			
			int d = bitcastFromLongToInt(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(byte[] source, double[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(byte[] source, int sourceOffset, int sourceLength, double[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//8 != 64
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 8, 64);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 64, 8);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 8;
			long destLengthInBits = destLength * 64;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 8));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
		}
		
		
		//64 > 8  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*8)/64;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 64/8; e++)
			{
				byte s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 8 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 8;
			}
			
			double d = bitcastFromLongToDouble(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(byte[] source, long[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(byte[] source, int sourceOffset, int sourceLength, long[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//8 != 64
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 8, 64);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 64, 8);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 8;
			long destLengthInBits = destLength * 64;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 8));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
		}
		
		
		//64 > 8  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*8)/64;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 64/8; e++)
			{
				byte s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 8 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 8;
			}
			
			long d = bitcastFromLongToLong(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(char[] source, boolean[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(char[] source, int sourceOffset, int sourceLength, boolean[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//16 != 1
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 16, 1);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 1, 16);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 16;
			long destLengthInBits = destLength * 1;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 1));
		}
		
		
		//16 > 1  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*1)/16;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			char s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 16/1; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 1)) & ((1L << 1) - 1));
				boolean d = bitcastFromLongToBoolean(shiftedAndMasked);
				dest[i*(16/1)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(char[] source, byte[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(char[] source, int sourceOffset, int sourceLength, byte[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//16 != 8
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 16, 8);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 8, 16);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 16;
			long destLengthInBits = destLength * 8;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 8));
		}
		
		
		//16 > 8  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*8)/16;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			char s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 16/8; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 8)) & ((1L << 8) - 1));
				byte d = bitcastFromLongToByte(shiftedAndMasked);
				dest[i*(16/8)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(char[] source, char[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(char[] source, int sourceOffset, int sourceLength, char[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//16 == 16
		int length = 0;
		{
			if (sourceLength == -1 && destLength == -1)
				throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
			else if (sourceLength == -1)
				length = destLength = sourceLength;
			else if (destLength == -1)
				length = sourceLength = destLength;
			else
			{
				//Minimize them to the overlap betweens them :>
				length = sourceLength = destLength = SmallIntegerMathUtilities.least(sourceLength, destLength);
			}
		}
		
		//char == char
		System.arraycopy(source, sourceOffset, dest, destOffset, length);
	}
	
	
	
	public static void copyBitfields(char[] source, short[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(char[] source, int sourceOffset, int sourceLength, short[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//16 == 16
		int length = 0;
		{
			if (sourceLength == -1 && destLength == -1)
				throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
			else if (sourceLength == -1)
				length = destLength = sourceLength;
			else if (destLength == -1)
				length = sourceLength = destLength;
			else
			{
				//Minimize them to the overlap betweens them :>
				length = sourceLength = destLength = SmallIntegerMathUtilities.least(sourceLength, destLength);
			}
		}
		
		//char != short
		for (int i = 0; i < length; i++)
		{
			char s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			short d = bitcastFromLongToShort(sbits);
			dest[i+destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(char[] source, float[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(char[] source, int sourceOffset, int sourceLength, float[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//16 != 32
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 16, 32);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 32, 16);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 16;
			long destLengthInBits = destLength * 32;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
		}
		
		
		//32 > 16  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*16)/32;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 32/16; e++)
			{
				char s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 16 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 16;
			}
			
			float d = bitcastFromLongToFloat(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(char[] source, int[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(char[] source, int sourceOffset, int sourceLength, int[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//16 != 32
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 16, 32);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 32, 16);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 16;
			long destLengthInBits = destLength * 32;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
		}
		
		
		//32 > 16  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*16)/32;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 32/16; e++)
			{
				char s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 16 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 16;
			}
			
			int d = bitcastFromLongToInt(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(char[] source, double[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(char[] source, int sourceOffset, int sourceLength, double[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//16 != 64
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 16, 64);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 64, 16);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 16;
			long destLengthInBits = destLength * 64;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
		}
		
		
		//64 > 16  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*16)/64;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 64/16; e++)
			{
				char s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 16 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 16;
			}
			
			double d = bitcastFromLongToDouble(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(char[] source, long[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(char[] source, int sourceOffset, int sourceLength, long[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//16 != 64
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 16, 64);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 64, 16);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 16;
			long destLengthInBits = destLength * 64;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
		}
		
		
		//64 > 16  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*16)/64;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 64/16; e++)
			{
				char s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 16 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 16;
			}
			
			long d = bitcastFromLongToLong(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(short[] source, boolean[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(short[] source, int sourceOffset, int sourceLength, boolean[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//16 != 1
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 16, 1);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 1, 16);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 16;
			long destLengthInBits = destLength * 1;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 1));
		}
		
		
		//16 > 1  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*1)/16;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			short s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 16/1; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 1)) & ((1L << 1) - 1));
				boolean d = bitcastFromLongToBoolean(shiftedAndMasked);
				dest[i*(16/1)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(short[] source, byte[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(short[] source, int sourceOffset, int sourceLength, byte[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//16 != 8
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 16, 8);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 8, 16);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 16;
			long destLengthInBits = destLength * 8;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 8));
		}
		
		
		//16 > 8  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*8)/16;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			short s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 16/8; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 8)) & ((1L << 8) - 1));
				byte d = bitcastFromLongToByte(shiftedAndMasked);
				dest[i*(16/8)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(short[] source, char[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(short[] source, int sourceOffset, int sourceLength, char[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//16 == 16
		int length = 0;
		{
			if (sourceLength == -1 && destLength == -1)
				throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
			else if (sourceLength == -1)
				length = destLength = sourceLength;
			else if (destLength == -1)
				length = sourceLength = destLength;
			else
			{
				//Minimize them to the overlap betweens them :>
				length = sourceLength = destLength = SmallIntegerMathUtilities.least(sourceLength, destLength);
			}
		}
		
		//short != char
		for (int i = 0; i < length; i++)
		{
			short s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			char d = bitcastFromLongToChar(sbits);
			dest[i+destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(short[] source, short[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(short[] source, int sourceOffset, int sourceLength, short[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//16 == 16
		int length = 0;
		{
			if (sourceLength == -1 && destLength == -1)
				throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
			else if (sourceLength == -1)
				length = destLength = sourceLength;
			else if (destLength == -1)
				length = sourceLength = destLength;
			else
			{
				//Minimize them to the overlap betweens them :>
				length = sourceLength = destLength = SmallIntegerMathUtilities.least(sourceLength, destLength);
			}
		}
		
		//short == short
		System.arraycopy(source, sourceOffset, dest, destOffset, length);
	}
	
	
	
	public static void copyBitfields(short[] source, float[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(short[] source, int sourceOffset, int sourceLength, float[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//16 != 32
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 16, 32);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 32, 16);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 16;
			long destLengthInBits = destLength * 32;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
		}
		
		
		//32 > 16  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*16)/32;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 32/16; e++)
			{
				short s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 16 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 16;
			}
			
			float d = bitcastFromLongToFloat(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(short[] source, int[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(short[] source, int sourceOffset, int sourceLength, int[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//16 != 32
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 16, 32);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 32, 16);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 16;
			long destLengthInBits = destLength * 32;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
		}
		
		
		//32 > 16  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*16)/32;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 32/16; e++)
			{
				short s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 16 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 16;
			}
			
			int d = bitcastFromLongToInt(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(short[] source, double[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(short[] source, int sourceOffset, int sourceLength, double[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//16 != 64
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 16, 64);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 64, 16);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 16;
			long destLengthInBits = destLength * 64;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
		}
		
		
		//64 > 16  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*16)/64;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 64/16; e++)
			{
				short s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 16 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 16;
			}
			
			double d = bitcastFromLongToDouble(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(short[] source, long[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(short[] source, int sourceOffset, int sourceLength, long[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//16 != 64
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 16, 64);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 64, 16);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 16;
			long destLengthInBits = destLength * 64;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
		}
		
		
		//64 > 16  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*16)/64;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 64/16; e++)
			{
				short s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 16 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 16;
			}
			
			long d = bitcastFromLongToLong(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(float[] source, boolean[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(float[] source, int sourceOffset, int sourceLength, boolean[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//32 != 1
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 32, 1);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 1, 32);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 32;
			long destLengthInBits = destLength * 1;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 1));
		}
		
		
		//32 > 1  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*1)/32;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			float s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 32/1; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 1)) & ((1L << 1) - 1));
				boolean d = bitcastFromLongToBoolean(shiftedAndMasked);
				dest[i*(32/1)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(float[] source, byte[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(float[] source, int sourceOffset, int sourceLength, byte[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//32 != 8
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 32, 8);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 8, 32);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 32;
			long destLengthInBits = destLength * 8;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 8));
		}
		
		
		//32 > 8  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*8)/32;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			float s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 32/8; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 8)) & ((1L << 8) - 1));
				byte d = bitcastFromLongToByte(shiftedAndMasked);
				dest[i*(32/8)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(float[] source, char[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(float[] source, int sourceOffset, int sourceLength, char[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//32 != 16
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 32, 16);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 16, 32);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 32;
			long destLengthInBits = destLength * 16;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
		}
		
		
		//32 > 16  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*16)/32;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			float s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 32/16; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 16)) & ((1L << 16) - 1));
				char d = bitcastFromLongToChar(shiftedAndMasked);
				dest[i*(32/16)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(float[] source, short[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(float[] source, int sourceOffset, int sourceLength, short[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//32 != 16
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 32, 16);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 16, 32);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 32;
			long destLengthInBits = destLength * 16;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
		}
		
		
		//32 > 16  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*16)/32;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			float s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 32/16; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 16)) & ((1L << 16) - 1));
				short d = bitcastFromLongToShort(shiftedAndMasked);
				dest[i*(32/16)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(float[] source, float[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(float[] source, int sourceOffset, int sourceLength, float[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//32 == 32
		int length = 0;
		{
			if (sourceLength == -1 && destLength == -1)
				throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
			else if (sourceLength == -1)
				length = destLength = sourceLength;
			else if (destLength == -1)
				length = sourceLength = destLength;
			else
			{
				//Minimize them to the overlap betweens them :>
				length = sourceLength = destLength = SmallIntegerMathUtilities.least(sourceLength, destLength);
			}
		}
		
		//float == float
		System.arraycopy(source, sourceOffset, dest, destOffset, length);
	}
	
	
	
	public static void copyBitfields(float[] source, int[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(float[] source, int sourceOffset, int sourceLength, int[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//32 == 32
		int length = 0;
		{
			if (sourceLength == -1 && destLength == -1)
				throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
			else if (sourceLength == -1)
				length = destLength = sourceLength;
			else if (destLength == -1)
				length = sourceLength = destLength;
			else
			{
				//Minimize them to the overlap betweens them :>
				length = sourceLength = destLength = SmallIntegerMathUtilities.least(sourceLength, destLength);
			}
		}
		
		//float != int
		for (int i = 0; i < length; i++)
		{
			float s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			int d = bitcastFromLongToInt(sbits);
			dest[i+destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(float[] source, double[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(float[] source, int sourceOffset, int sourceLength, double[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//32 != 64
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 32, 64);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 64, 32);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 32;
			long destLengthInBits = destLength * 64;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
		}
		
		
		//64 > 32  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*32)/64;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 64/32; e++)
			{
				float s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 32 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 32;
			}
			
			double d = bitcastFromLongToDouble(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(float[] source, long[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(float[] source, int sourceOffset, int sourceLength, long[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//32 != 64
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 32, 64);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 64, 32);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 32;
			long destLengthInBits = destLength * 64;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
		}
		
		
		//64 > 32  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*32)/64;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 64/32; e++)
			{
				float s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 32 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 32;
			}
			
			long d = bitcastFromLongToLong(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(int[] source, boolean[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(int[] source, int sourceOffset, int sourceLength, boolean[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//32 != 1
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 32, 1);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 1, 32);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 32;
			long destLengthInBits = destLength * 1;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 1));
		}
		
		
		//32 > 1  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*1)/32;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			int s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 32/1; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 1)) & ((1L << 1) - 1));
				boolean d = bitcastFromLongToBoolean(shiftedAndMasked);
				dest[i*(32/1)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(int[] source, byte[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(int[] source, int sourceOffset, int sourceLength, byte[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//32 != 8
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 32, 8);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 8, 32);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 32;
			long destLengthInBits = destLength * 8;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 8));
		}
		
		
		//32 > 8  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*8)/32;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			int s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 32/8; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 8)) & ((1L << 8) - 1));
				byte d = bitcastFromLongToByte(shiftedAndMasked);
				dest[i*(32/8)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(int[] source, char[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(int[] source, int sourceOffset, int sourceLength, char[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//32 != 16
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 32, 16);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 16, 32);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 32;
			long destLengthInBits = destLength * 16;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
		}
		
		
		//32 > 16  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*16)/32;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			int s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 32/16; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 16)) & ((1L << 16) - 1));
				char d = bitcastFromLongToChar(shiftedAndMasked);
				dest[i*(32/16)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(int[] source, short[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(int[] source, int sourceOffset, int sourceLength, short[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//32 != 16
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 32, 16);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 16, 32);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 32;
			long destLengthInBits = destLength * 16;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
		}
		
		
		//32 > 16  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*16)/32;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			int s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 32/16; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 16)) & ((1L << 16) - 1));
				short d = bitcastFromLongToShort(shiftedAndMasked);
				dest[i*(32/16)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(int[] source, float[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(int[] source, int sourceOffset, int sourceLength, float[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//32 == 32
		int length = 0;
		{
			if (sourceLength == -1 && destLength == -1)
				throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
			else if (sourceLength == -1)
				length = destLength = sourceLength;
			else if (destLength == -1)
				length = sourceLength = destLength;
			else
			{
				//Minimize them to the overlap betweens them :>
				length = sourceLength = destLength = SmallIntegerMathUtilities.least(sourceLength, destLength);
			}
		}
		
		//int != float
		for (int i = 0; i < length; i++)
		{
			int s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			float d = bitcastFromLongToFloat(sbits);
			dest[i+destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(int[] source, int[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(int[] source, int sourceOffset, int sourceLength, int[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//32 == 32
		int length = 0;
		{
			if (sourceLength == -1 && destLength == -1)
				throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
			else if (sourceLength == -1)
				length = destLength = sourceLength;
			else if (destLength == -1)
				length = sourceLength = destLength;
			else
			{
				//Minimize them to the overlap betweens them :>
				length = sourceLength = destLength = SmallIntegerMathUtilities.least(sourceLength, destLength);
			}
		}
		
		//int == int
		System.arraycopy(source, sourceOffset, dest, destOffset, length);
	}
	
	
	
	public static void copyBitfields(int[] source, double[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(int[] source, int sourceOffset, int sourceLength, double[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//32 != 64
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 32, 64);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 64, 32);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 32;
			long destLengthInBits = destLength * 64;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
		}
		
		
		//64 > 32  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*32)/64;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 64/32; e++)
			{
				int s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 32 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 32;
			}
			
			double d = bitcastFromLongToDouble(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(int[] source, long[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(int[] source, int sourceOffset, int sourceLength, long[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//32 != 64
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 32, 64);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 64, 32);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 32;
			long destLengthInBits = destLength * 64;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
		}
		
		
		//64 > 32  (dest primitive > source primitive)
		int destLengthFloor = (sourceLength*32)/64;
		for (int i = 0; i < destLengthFloor; i++)
		{
			long shiftedAndMasked = 0;
			
			for (int e = 0; e < 64/32; e++)
			{
				int s = source[i+sourceOffset];
				long sbits = bitcastToLongUnsigned(s); //"unsigned" means no sign extension; ie, the high-order bits (beyond primitive length, 32 ;D) will allllll be happily zeros! yay! ^,^
				shiftedAndMasked |= sbits;
				shiftedAndMasked <<= 32;
			}
			
			long d = bitcastFromLongToLong(shiftedAndMasked);
			dest[i + destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(double[] source, boolean[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(double[] source, int sourceOffset, int sourceLength, boolean[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//64 != 1
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 64, 1);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 1, 64);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 64;
			long destLengthInBits = destLength * 1;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 1));
		}
		
		
		//64 > 1  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*1)/64;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			double s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 64/1; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 1)) & ((1L << 1) - 1));
				boolean d = bitcastFromLongToBoolean(shiftedAndMasked);
				dest[i*(64/1)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(double[] source, byte[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(double[] source, int sourceOffset, int sourceLength, byte[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//64 != 8
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 64, 8);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 8, 64);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 64;
			long destLengthInBits = destLength * 8;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 8));
		}
		
		
		//64 > 8  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*8)/64;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			double s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 64/8; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 8)) & ((1L << 8) - 1));
				byte d = bitcastFromLongToByte(shiftedAndMasked);
				dest[i*(64/8)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(double[] source, char[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(double[] source, int sourceOffset, int sourceLength, char[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//64 != 16
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 64, 16);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 16, 64);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 64;
			long destLengthInBits = destLength * 16;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
		}
		
		
		//64 > 16  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*16)/64;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			double s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 64/16; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 16)) & ((1L << 16) - 1));
				char d = bitcastFromLongToChar(shiftedAndMasked);
				dest[i*(64/16)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(double[] source, short[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(double[] source, int sourceOffset, int sourceLength, short[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//64 != 16
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 64, 16);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 16, 64);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 64;
			long destLengthInBits = destLength * 16;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
		}
		
		
		//64 > 16  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*16)/64;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			double s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 64/16; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 16)) & ((1L << 16) - 1));
				short d = bitcastFromLongToShort(shiftedAndMasked);
				dest[i*(64/16)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(double[] source, float[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(double[] source, int sourceOffset, int sourceLength, float[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//64 != 32
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 64, 32);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 32, 64);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 64;
			long destLengthInBits = destLength * 32;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
		}
		
		
		//64 > 32  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*32)/64;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			double s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 64/32; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 32)) & ((1L << 32) - 1));
				float d = bitcastFromLongToFloat(shiftedAndMasked);
				dest[i*(64/32)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(double[] source, int[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(double[] source, int sourceOffset, int sourceLength, int[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//64 != 32
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 64, 32);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 32, 64);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 64;
			long destLengthInBits = destLength * 32;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
		}
		
		
		//64 > 32  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*32)/64;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			double s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 64/32; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 32)) & ((1L << 32) - 1));
				int d = bitcastFromLongToInt(shiftedAndMasked);
				dest[i*(64/32)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(double[] source, double[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(double[] source, int sourceOffset, int sourceLength, double[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//64 == 64
		int length = 0;
		{
			if (sourceLength == -1 && destLength == -1)
				throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
			else if (sourceLength == -1)
				length = destLength = sourceLength;
			else if (destLength == -1)
				length = sourceLength = destLength;
			else
			{
				//Minimize them to the overlap betweens them :>
				length = sourceLength = destLength = SmallIntegerMathUtilities.least(sourceLength, destLength);
			}
		}
		
		//double == double
		System.arraycopy(source, sourceOffset, dest, destOffset, length);
	}
	
	
	
	public static void copyBitfields(double[] source, long[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(double[] source, int sourceOffset, int sourceLength, long[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//64 == 64
		int length = 0;
		{
			if (sourceLength == -1 && destLength == -1)
				throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
			else if (sourceLength == -1)
				length = destLength = sourceLength;
			else if (destLength == -1)
				length = sourceLength = destLength;
			else
			{
				//Minimize them to the overlap betweens them :>
				length = sourceLength = destLength = SmallIntegerMathUtilities.least(sourceLength, destLength);
			}
		}
		
		//double != long
		for (int i = 0; i < length; i++)
		{
			double s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			long d = bitcastFromLongToLong(sbits);
			dest[i+destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(long[] source, boolean[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(long[] source, int sourceOffset, int sourceLength, boolean[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//64 != 1
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 64, 1);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 1, 64);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 64;
			long destLengthInBits = destLength * 1;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 1));
		}
		
		
		//64 > 1  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*1)/64;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			long s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 64/1; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 1)) & ((1L << 1) - 1));
				boolean d = bitcastFromLongToBoolean(shiftedAndMasked);
				dest[i*(64/1)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(long[] source, byte[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(long[] source, int sourceOffset, int sourceLength, byte[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//64 != 8
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 64, 8);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 8, 64);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 64;
			long destLengthInBits = destLength * 8;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 8));
		}
		
		
		//64 > 8  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*8)/64;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			long s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 64/8; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 8)) & ((1L << 8) - 1));
				byte d = bitcastFromLongToByte(shiftedAndMasked);
				dest[i*(64/8)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(long[] source, char[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(long[] source, int sourceOffset, int sourceLength, char[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//64 != 16
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 64, 16);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 16, 64);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 64;
			long destLengthInBits = destLength * 16;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
		}
		
		
		//64 > 16  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*16)/64;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			long s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 64/16; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 16)) & ((1L << 16) - 1));
				char d = bitcastFromLongToChar(shiftedAndMasked);
				dest[i*(64/16)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(long[] source, short[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(long[] source, int sourceOffset, int sourceLength, short[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//64 != 16
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 64, 16);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 16, 64);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 64;
			long destLengthInBits = destLength * 16;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 16));
		}
		
		
		//64 > 16  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*16)/64;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			long s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 64/16; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 16)) & ((1L << 16) - 1));
				short d = bitcastFromLongToShort(shiftedAndMasked);
				dest[i*(64/16)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(long[] source, float[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(long[] source, int sourceOffset, int sourceLength, float[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//64 != 32
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 64, 32);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 32, 64);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 64;
			long destLengthInBits = destLength * 32;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
		}
		
		
		//64 > 32  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*32)/64;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			long s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 64/32; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 32)) & ((1L << 32) - 1));
				float d = bitcastFromLongToFloat(shiftedAndMasked);
				dest[i*(64/32)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(long[] source, int[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(long[] source, int sourceOffset, int sourceLength, int[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//64 != 32
		if (sourceLength == -1 && destLength == -1)
			throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
		else if (sourceLength == -1)
			destLength = SmallIntegerMathUtilities.ceilingDivision(sourceLength * 64, 32);
		else if (destLength == -1)
			sourceLength = SmallIntegerMathUtilities.ceilingDivision(destLength * 32, 64);
		else
		{
			//Minimize them to the overlap betweens them :>
			long sourceLengthInBits = sourceLength * 64;
			long destLengthInBits = destLength * 32;
			long minLengthInBits = SmallIntegerMathUtilities.least(sourceLengthInBits, destLengthInBits);
			sourceLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 64));
			destLength = safeCastS64toS32(SmallIntegerMathUtilities.ceilingDivision(minLengthInBits, 32));
		}
		
		
		//64 > 32  (source primitive > dest primitive)
		int sourceLengthFloor = (destLength*32)/64;
		for (int i = 0; i < sourceLengthFloor; i++)
		{
			long s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			
			for (int e = 0; e < 64/32; e++)
			{
				long shiftedAndMasked = ((sbits >>> (e * 32)) & ((1L << 32) - 1));
				int d = bitcastFromLongToInt(shiftedAndMasked);
				dest[i*(64/32)+e + destOffset] = d;
			}
		}
	}
	
	
	
	public static void copyBitfields(long[] source, double[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(long[] source, int sourceOffset, int sourceLength, double[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//64 == 64
		int length = 0;
		{
			if (sourceLength == -1 && destLength == -1)
				throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
			else if (sourceLength == -1)
				length = destLength = sourceLength;
			else if (destLength == -1)
				length = sourceLength = destLength;
			else
			{
				//Minimize them to the overlap betweens them :>
				length = sourceLength = destLength = SmallIntegerMathUtilities.least(sourceLength, destLength);
			}
		}
		
		//long != double
		for (int i = 0; i < length; i++)
		{
			long s = source[i+sourceOffset];
			long sbits = bitcastToLongUnsigned(s);
			double d = bitcastFromLongToDouble(sbits);
			dest[i+destOffset] = d;
		}
	}
	
	
	
	public static void copyBitfields(long[] source, long[] dest)
	{
		copyBitfields(source, 0, source.length, dest, 0, dest.length);
	}
	
	public static void copyBitfields(long[] source, int sourceOffset, int sourceLength, long[] dest, int destOffset, int destLength)
	{
		if (sourceOffset < 0) throw new IndexOutOfBoundsException("negative source offset!");
		if (sourceLength < 0) throw new IndexOutOfBoundsException("negative source length!");
		if (destOffset < 0) throw new IndexOutOfBoundsException("negative destination offset!");
		if (destLength < 0) throw new IndexOutOfBoundsException("negative destination length!");
		if (sourceOffset+sourceLength > source.length) throw new IndexOutOfBoundsException("overflowing source range! :o");
		if (destOffset+destLength > dest.length) throw new IndexOutOfBoundsException("overflowing destination range! :o");
		
		
		//64 == 64
		int length = 0;
		{
			if (sourceLength == -1 && destLength == -1)
				throw new IllegalArgumentException("Both lengths can't be 'determine-from-other-length'! :o");
			else if (sourceLength == -1)
				length = destLength = sourceLength;
			else if (destLength == -1)
				length = sourceLength = destLength;
			else
			{
				//Minimize them to the overlap betweens them :>
				length = sourceLength = destLength = SmallIntegerMathUtilities.least(sourceLength, destLength);
			}
		}
		
		//long == long
		System.arraycopy(source, sourceOffset, dest, destOffset, length);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static <E> Object[] loseComponentType(E[] typedArray)
	{
		Object[] untypedArray = new Object[typedArray.length];
		System.arraycopy(typedArray, 0, untypedArray, 0, typedArray.length);
		return untypedArray;
	}
	
	public static <Pre, Post> Post[] changeComponentType(Pre[] originalTypedArray, Class<Post> newComponentType)
	{
		Post[] newTypedArray = (Post[])Array.newInstance(newComponentType, originalTypedArray.length);
		System.arraycopy(originalTypedArray, 0, newTypedArray, 0, originalTypedArray.length);
		return newTypedArray;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static <E> E[] makeArray(UnaryFunctionIntToObject<? extends E> filler, int length, Class<E> componentType)
	{
		E[] array = (E[])Array.newInstance(componentType, length);
		
		for (int i = 0; i < length; i++)
			array[i] = filler.f(i);
		
		return array;
	}
	
	public static <E> E[] makeArrayIndexless(NullaryFunction<? extends E> filler, int length, Class<E> componentType)
	{
		E[] array = (E[])Array.newInstance(componentType, length);
		
		for (int i = 0; i < length; i++)
			array[i] = filler.f();
		
		return array;
	}
	
	
	public static Object[] makeObjectArray(UnaryFunctionIntToObject<?> filler, int length)
	{
		Object[] array = new Object[length];
		
		for (int i = 0; i < length; i++)
			array[i] = filler.f(i);
		
		return array;
	}
	
	public static Object[] makeObjectArrayIndexless(NullaryFunction<?> filler, int length)
	{
		Object[] array = new Object[length];
		
		for (int i = 0; i < length; i++)
			array[i] = filler.f();
		
		return array;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	python
	
	
	s = """
	public static void checkSmallRangeValuedArrayIsDuplicateless(_$$prim$$_[] array)
	{
		checkSmallRangeValuedArrayIsDuplicateless(array, least(array));
	}
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(_$$prim$$_[] array, _$$prim$$_ valueBase)
	{
		BitSet has = new BitSet();
		
		for (_$$prim$$_ value : array)
		{
			int zValue = safeCast_$$bitabbv$$_toS32(value - valueBase);
			
			if (zValue < 0)
				throw new IllegalArgumentException();
			
			if (has.get(zValue))
				throw new IllegalArgumentException("Duplicates detected in array!:  Multiple occurrences of "+((long)value));
			else
				has.set(zValue);
		}
	}
	
	
	
	public static void checkSmallRangeValuedArraysAreDuplicatelessAndCoverSameValuesInPossiblyDifferentOrders(_$$prim$$_[] arrayA, _$$prim$$_[] arrayB, _$$prim$$_ valueBase)
	{
		if (arrayA.length != arrayB.length)
			throw new IllegalArgumentException("They differ even in length as well!! \\\\o/");
		
		
		BitSet hasA = new BitSet();
		{
			for (_$$prim$$_ value : arrayA)
			{
				int zValue = safeCast_$$bitabbv$$_toS32(value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasA.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array A!:  Multiple occurrences of "+((_$$upcast$$_)value));
				else
					hasA.set(zValue);
			}
		}
		
		
		BitSet hasB = new BitSet();
		{
			for (_$$prim$$_ value : arrayB)
			{
				int zValue = safeCast_$$bitabbv$$_toS32(value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasB.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array B!:  Multiple occurrences of "+((_$$upcast$$_)value));
				else
					hasB.set(zValue);
			}
		}
		
		
		
		if (!hasA.equals(hasB))
			throw new IllegalArgumentException("They differ even as sets in which values they contain, not just in the order of them!!");
	}
	
	
	
	
	
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(_$$Primitive$$_List array)
	{
		checkSmallRangeValuedArrayIsDuplicateless(array, least(array));
	}
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(_$$Primitive$$_List array, _$$prim$$_ valueBase)
	{
		BitSet has = new BitSet();
		
		for (_$$prim$$_ value : array)
		{
			int zValue = safeCast_$$bitabbv$$_toS32(value - valueBase);
			
			if (zValue < 0)
				throw new IllegalArgumentException();
			
			if (has.get(zValue))
				throw new IllegalArgumentException("Duplicates detected in array!:  Multiple occurrences of "+((long)value));
			else
				has.set(zValue);
		}
	}
	
	
	
	public static void checkSmallRangeValuedArraysAreDuplicatelessAndCoverSameValuesInPossiblyDifferentOrders(_$$Primitive$$_List arrayA, _$$Primitive$$_List arrayB, _$$prim$$_ valueBase)
	{
		if (arrayA.size() != arrayB.size())
			throw new IllegalArgumentException("They differ even in length as well!! \\\\o/");
		
		
		BitSet hasA = new BitSet();
		{
			for (_$$prim$$_ value : arrayA)
			{
				int zValue = safeCast_$$bitabbv$$_toS32(value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasA.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array A!:  Multiple occurrences of "+((_$$upcast$$_)value));
				else
					hasA.set(zValue);
			}
		}
		
		
		BitSet hasB = new BitSet();
		{
			for (_$$prim$$_ value : arrayB)
			{
				int zValue = safeCast_$$bitabbv$$_toS32(value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasB.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array B!:  Multiple occurrences of "+((_$$upcast$$_)value));
				else
					hasB.set(zValue);
			}
		}
		
		
		
		if (!hasA.equals(hasB))
			throw new IllegalArgumentException("They differ even as sets in which values they contain, not just in the order of them!!");
	}
	
	
	
	
	
	
	"""
	
	
	
	
	p(primxp.primxp(prims=newSubdict(primxp.NumPrims, ["byte", "short", "char", "int"]), source=s.replace("safeCast_$$bitabbv$$_toS32", "")))
	
	p(primxp.primxp(prims=newSubdict(primxp.NumPrims, ["long"]), source=s))
	
	
	 */
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(byte[] array)
	{
		checkSmallRangeValuedArrayIsDuplicateless(array, least(array));
	}
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(byte[] array, byte valueBase)
	{
		BitSet has = new BitSet();
		
		for (byte value : array)
		{
			int zValue = (value - valueBase);
			
			if (zValue < 0)
				throw new IllegalArgumentException();
			
			if (has.get(zValue))
				throw new IllegalArgumentException("Duplicates detected in array!:  Multiple occurrences of "+((long)value));
			else
				has.set(zValue);
		}
	}
	
	
	
	public static void checkSmallRangeValuedArraysAreDuplicatelessAndCoverSameValuesInPossiblyDifferentOrders(byte[] arrayA, byte[] arrayB, byte valueBase)
	{
		if (arrayA.length != arrayB.length)
			throw new IllegalArgumentException("They differ even in length as well!! \\o/");
		
		
		BitSet hasA = new BitSet();
		{
			for (byte value : arrayA)
			{
				int zValue = (value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasA.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array A!:  Multiple occurrences of "+((int)value));
				else
					hasA.set(zValue);
			}
		}
		
		
		BitSet hasB = new BitSet();
		{
			for (byte value : arrayB)
			{
				int zValue = (value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasB.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array B!:  Multiple occurrences of "+((int)value));
				else
					hasB.set(zValue);
			}
		}
		
		
		
		if (!hasA.equals(hasB))
			throw new IllegalArgumentException("They differ even as sets in which values they contain, not just in the order of them!!");
	}
	
	
	
	
	
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(ByteList array)
	{
		checkSmallRangeValuedArrayIsDuplicateless(array, least(array));
	}
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(ByteList array, byte valueBase)
	{
		BitSet has = new BitSet();
		
		for (byte value : array)
		{
			int zValue = (value - valueBase);
			
			if (zValue < 0)
				throw new IllegalArgumentException();
			
			if (has.get(zValue))
				throw new IllegalArgumentException("Duplicates detected in array!:  Multiple occurrences of "+((long)value));
			else
				has.set(zValue);
		}
	}
	
	
	
	public static void checkSmallRangeValuedArraysAreDuplicatelessAndCoverSameValuesInPossiblyDifferentOrders(ByteList arrayA, ByteList arrayB, byte valueBase)
	{
		if (arrayA.size() != arrayB.size())
			throw new IllegalArgumentException("They differ even in length as well!! \\o/");
		
		
		BitSet hasA = new BitSet();
		{
			for (byte value : arrayA)
			{
				int zValue = (value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasA.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array A!:  Multiple occurrences of "+((int)value));
				else
					hasA.set(zValue);
			}
		}
		
		
		BitSet hasB = new BitSet();
		{
			for (byte value : arrayB)
			{
				int zValue = (value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasB.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array B!:  Multiple occurrences of "+((int)value));
				else
					hasB.set(zValue);
			}
		}
		
		
		
		if (!hasA.equals(hasB))
			throw new IllegalArgumentException("They differ even as sets in which values they contain, not just in the order of them!!");
	}
	
	
	
	
	
	
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(char[] array)
	{
		checkSmallRangeValuedArrayIsDuplicateless(array, least(array));
	}
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(char[] array, char valueBase)
	{
		BitSet has = new BitSet();
		
		for (char value : array)
		{
			int zValue = (value - valueBase);
			
			if (zValue < 0)
				throw new IllegalArgumentException();
			
			if (has.get(zValue))
				throw new IllegalArgumentException("Duplicates detected in array!:  Multiple occurrences of "+((long)value));
			else
				has.set(zValue);
		}
	}
	
	
	
	public static void checkSmallRangeValuedArraysAreDuplicatelessAndCoverSameValuesInPossiblyDifferentOrders(char[] arrayA, char[] arrayB, char valueBase)
	{
		if (arrayA.length != arrayB.length)
			throw new IllegalArgumentException("They differ even in length as well!! \\o/");
		
		
		BitSet hasA = new BitSet();
		{
			for (char value : arrayA)
			{
				int zValue = (value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasA.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array A!:  Multiple occurrences of "+((int)value));
				else
					hasA.set(zValue);
			}
		}
		
		
		BitSet hasB = new BitSet();
		{
			for (char value : arrayB)
			{
				int zValue = (value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasB.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array B!:  Multiple occurrences of "+((int)value));
				else
					hasB.set(zValue);
			}
		}
		
		
		
		if (!hasA.equals(hasB))
			throw new IllegalArgumentException("They differ even as sets in which values they contain, not just in the order of them!!");
	}
	
	
	
	
	
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(CharacterList array)
	{
		checkSmallRangeValuedArrayIsDuplicateless(array, least(array));
	}
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(CharacterList array, char valueBase)
	{
		BitSet has = new BitSet();
		
		for (char value : array)
		{
			int zValue = (value - valueBase);
			
			if (zValue < 0)
				throw new IllegalArgumentException();
			
			if (has.get(zValue))
				throw new IllegalArgumentException("Duplicates detected in array!:  Multiple occurrences of "+((long)value));
			else
				has.set(zValue);
		}
	}
	
	
	
	public static void checkSmallRangeValuedArraysAreDuplicatelessAndCoverSameValuesInPossiblyDifferentOrders(CharacterList arrayA, CharacterList arrayB, char valueBase)
	{
		if (arrayA.size() != arrayB.size())
			throw new IllegalArgumentException("They differ even in length as well!! \\o/");
		
		
		BitSet hasA = new BitSet();
		{
			for (char value : arrayA)
			{
				int zValue = (value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasA.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array A!:  Multiple occurrences of "+((int)value));
				else
					hasA.set(zValue);
			}
		}
		
		
		BitSet hasB = new BitSet();
		{
			for (char value : arrayB)
			{
				int zValue = (value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasB.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array B!:  Multiple occurrences of "+((int)value));
				else
					hasB.set(zValue);
			}
		}
		
		
		
		if (!hasA.equals(hasB))
			throw new IllegalArgumentException("They differ even as sets in which values they contain, not just in the order of them!!");
	}
	
	
	
	
	
	
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(short[] array)
	{
		checkSmallRangeValuedArrayIsDuplicateless(array, least(array));
	}
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(short[] array, short valueBase)
	{
		BitSet has = new BitSet();
		
		for (short value : array)
		{
			int zValue = (value - valueBase);
			
			if (zValue < 0)
				throw new IllegalArgumentException();
			
			if (has.get(zValue))
				throw new IllegalArgumentException("Duplicates detected in array!:  Multiple occurrences of "+((long)value));
			else
				has.set(zValue);
		}
	}
	
	
	
	public static void checkSmallRangeValuedArraysAreDuplicatelessAndCoverSameValuesInPossiblyDifferentOrders(short[] arrayA, short[] arrayB, short valueBase)
	{
		if (arrayA.length != arrayB.length)
			throw new IllegalArgumentException("They differ even in length as well!! \\o/");
		
		
		BitSet hasA = new BitSet();
		{
			for (short value : arrayA)
			{
				int zValue = (value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasA.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array A!:  Multiple occurrences of "+((int)value));
				else
					hasA.set(zValue);
			}
		}
		
		
		BitSet hasB = new BitSet();
		{
			for (short value : arrayB)
			{
				int zValue = (value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasB.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array B!:  Multiple occurrences of "+((int)value));
				else
					hasB.set(zValue);
			}
		}
		
		
		
		if (!hasA.equals(hasB))
			throw new IllegalArgumentException("They differ even as sets in which values they contain, not just in the order of them!!");
	}
	
	
	
	
	
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(ShortList array)
	{
		checkSmallRangeValuedArrayIsDuplicateless(array, least(array));
	}
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(ShortList array, short valueBase)
	{
		BitSet has = new BitSet();
		
		for (short value : array)
		{
			int zValue = (value - valueBase);
			
			if (zValue < 0)
				throw new IllegalArgumentException();
			
			if (has.get(zValue))
				throw new IllegalArgumentException("Duplicates detected in array!:  Multiple occurrences of "+((long)value));
			else
				has.set(zValue);
		}
	}
	
	
	
	public static void checkSmallRangeValuedArraysAreDuplicatelessAndCoverSameValuesInPossiblyDifferentOrders(ShortList arrayA, ShortList arrayB, short valueBase)
	{
		if (arrayA.size() != arrayB.size())
			throw new IllegalArgumentException("They differ even in length as well!! \\o/");
		
		
		BitSet hasA = new BitSet();
		{
			for (short value : arrayA)
			{
				int zValue = (value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasA.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array A!:  Multiple occurrences of "+((int)value));
				else
					hasA.set(zValue);
			}
		}
		
		
		BitSet hasB = new BitSet();
		{
			for (short value : arrayB)
			{
				int zValue = (value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasB.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array B!:  Multiple occurrences of "+((int)value));
				else
					hasB.set(zValue);
			}
		}
		
		
		
		if (!hasA.equals(hasB))
			throw new IllegalArgumentException("They differ even as sets in which values they contain, not just in the order of them!!");
	}
	
	
	
	
	
	
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(int[] array)
	{
		checkSmallRangeValuedArrayIsDuplicateless(array, least(array));
	}
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(int[] array, int valueBase)
	{
		BitSet has = new BitSet();
		
		for (int value : array)
		{
			int zValue = (value - valueBase);
			
			if (zValue < 0)
				throw new IllegalArgumentException();
			
			if (has.get(zValue))
				throw new IllegalArgumentException("Duplicates detected in array!:  Multiple occurrences of "+((long)value));
			else
				has.set(zValue);
		}
	}
	
	
	
	public static void checkSmallRangeValuedArraysAreDuplicatelessAndCoverSameValuesInPossiblyDifferentOrders(int[] arrayA, int[] arrayB, int valueBase)
	{
		if (arrayA.length != arrayB.length)
			throw new IllegalArgumentException("They differ even in length as well!! \\o/");
		
		
		BitSet hasA = new BitSet();
		{
			for (int value : arrayA)
			{
				int zValue = (value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasA.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array A!:  Multiple occurrences of "+((long)value));
				else
					hasA.set(zValue);
			}
		}
		
		
		BitSet hasB = new BitSet();
		{
			for (int value : arrayB)
			{
				int zValue = (value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasB.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array B!:  Multiple occurrences of "+((long)value));
				else
					hasB.set(zValue);
			}
		}
		
		
		
		if (!hasA.equals(hasB))
			throw new IllegalArgumentException("They differ even as sets in which values they contain, not just in the order of them!!");
	}
	
	
	
	
	
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(IntegerList array)
	{
		checkSmallRangeValuedArrayIsDuplicateless(array, least(array));
	}
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(IntegerList array, int valueBase)
	{
		BitSet has = new BitSet();
		
		for (int value : array)
		{
			int zValue = (value - valueBase);
			
			if (zValue < 0)
				throw new IllegalArgumentException();
			
			if (has.get(zValue))
				throw new IllegalArgumentException("Duplicates detected in array!:  Multiple occurrences of "+((long)value));
			else
				has.set(zValue);
		}
	}
	
	
	
	public static void checkSmallRangeValuedArraysAreDuplicatelessAndCoverSameValuesInPossiblyDifferentOrders(IntegerList arrayA, IntegerList arrayB, int valueBase)
	{
		if (arrayA.size() != arrayB.size())
			throw new IllegalArgumentException("They differ even in length as well!! \\o/");
		
		
		BitSet hasA = new BitSet();
		{
			for (int value : arrayA)
			{
				int zValue = (value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasA.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array A!:  Multiple occurrences of "+((long)value));
				else
					hasA.set(zValue);
			}
		}
		
		
		BitSet hasB = new BitSet();
		{
			for (int value : arrayB)
			{
				int zValue = (value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasB.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array B!:  Multiple occurrences of "+((long)value));
				else
					hasB.set(zValue);
			}
		}
		
		
		
		if (!hasA.equals(hasB))
			throw new IllegalArgumentException("They differ even as sets in which values they contain, not just in the order of them!!");
	}
	
	
	
	
	
	
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(long[] array)
	{
		checkSmallRangeValuedArrayIsDuplicateless(array, least(array));
	}
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(long[] array, long valueBase)
	{
		BitSet has = new BitSet();
		
		for (long value : array)
		{
			int zValue = safeCastS64toS32(value - valueBase);
			
			if (zValue < 0)
				throw new IllegalArgumentException();
			
			if (has.get(zValue))
				throw new IllegalArgumentException("Duplicates detected in array!:  Multiple occurrences of "+((long)value));
			else
				has.set(zValue);
		}
	}
	
	
	
	public static void checkSmallRangeValuedArraysAreDuplicatelessAndCoverSameValuesInPossiblyDifferentOrders(long[] arrayA, long[] arrayB, long valueBase)
	{
		if (arrayA.length != arrayB.length)
			throw new IllegalArgumentException("They differ even in length as well!! \\o/");
		
		
		BitSet hasA = new BitSet();
		{
			for (long value : arrayA)
			{
				int zValue = safeCastS64toS32(value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasA.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array A!:  Multiple occurrences of "+((long)value));
				else
					hasA.set(zValue);
			}
		}
		
		
		BitSet hasB = new BitSet();
		{
			for (long value : arrayB)
			{
				int zValue = safeCastS64toS32(value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasB.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array B!:  Multiple occurrences of "+((long)value));
				else
					hasB.set(zValue);
			}
		}
		
		
		
		if (!hasA.equals(hasB))
			throw new IllegalArgumentException("They differ even as sets in which values they contain, not just in the order of them!!");
	}
	
	
	
	
	
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(LongList array)
	{
		checkSmallRangeValuedArrayIsDuplicateless(array, least(array));
	}
	
	public static void checkSmallRangeValuedArrayIsDuplicateless(LongList array, long valueBase)
	{
		BitSet has = new BitSet();
		
		for (long value : array)
		{
			int zValue = safeCastS64toS32(value - valueBase);
			
			if (zValue < 0)
				throw new IllegalArgumentException();
			
			if (has.get(zValue))
				throw new IllegalArgumentException("Duplicates detected in array!:  Multiple occurrences of "+((long)value));
			else
				has.set(zValue);
		}
	}
	
	
	
	public static void checkSmallRangeValuedArraysAreDuplicatelessAndCoverSameValuesInPossiblyDifferentOrders(LongList arrayA, LongList arrayB, long valueBase)
	{
		if (arrayA.size() != arrayB.size())
			throw new IllegalArgumentException("They differ even in length as well!! \\o/");
		
		
		BitSet hasA = new BitSet();
		{
			for (long value : arrayA)
			{
				int zValue = safeCastS64toS32(value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasA.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array A!:  Multiple occurrences of "+((long)value));
				else
					hasA.set(zValue);
			}
		}
		
		
		BitSet hasB = new BitSet();
		{
			for (long value : arrayB)
			{
				int zValue = safeCastS64toS32(value - valueBase);
				
				if (zValue < 0)
					throw new IllegalArgumentException();
				
				if (hasB.get(zValue))
					throw new IllegalArgumentException("Duplicates detected in array B!:  Multiple occurrences of "+((long)value));
				else
					hasB.set(zValue);
			}
		}
		
		
		
		if (!hasA.equals(hasB))
			throw new IllegalArgumentException("They differ even as sets in which values they contain, not just in the order of them!!");
	}
	
	
	
	
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	python
	
	p(primxp.primxp(prims=primxp.AllPrimsAndObject, source="""
	
	public static void rotateInPlaceBy1(_$$prim$$_[] array, int offset, int length)
	{
		if (length < 0 || offset < 0 || offset + length > array.length)
			throw new IndexOutOfBoundsException();
		
		
		
		if (length == 0)
			return;
		
		_$$prim$$_ last = array[length-1+offset];
		
		for (int i = length-1; i >= 1; i--)
		{
			int prev = i == 0 ? length - 1 : i;
			
			array[i+offset] = array[prev+offset];
		}
		
		array[offset] = last;
	}
	
	public static void rotateInPlaceBy1(_$$prim$$_[] array)
	{
		rotateInPlaceBy1(array, 0, array.length);
	}
	
	
	
	
	public static void rotateIntoBy1(_$$prim$$_[] source, int sourceOffset, int length, @WritableValue _$$prim$$_[] dest, int destOffset)
	{
		if (source == dest)
		{
			if (sourceOffset == destOffset)
				rotateInPlaceBy1(source, sourceOffset, length);
			else
				throw new NotYetImplementedException();
		}
		
		if (length < 0 || sourceOffset < 0 || sourceOffset + length > source.length)
			throw new IndexOutOfBoundsException();
		
		
		
		if (length == 0)
			return;
		
		for (int i = length-1; i >= 0; i--)
		{
			int prev = i == 0 ? length - 1 : i;
			
			source[i+sourceOffset] = source[prev+sourceOffset];
		}
	}
	
	
	
	@ThrowAwayValue
	public static _$$prim$$_[] rotateOutOfPlaceBy1(_$$prim$$_[] array, int offset, int length)
	{
		_$$prim$$_[] output = new _$$prim$$_[length];
		rotateIntoBy1(array, offset, length, output, 0);
		return output;
	}
	
	
	@ThrowAwayValue
	public static _$$prim$$_[] rotateOutOfPlaceBy1(_$$prim$$_[] array)
	{
		return rotateOutOfPlaceBy1(array, 0, array.length);
	}
	
	"""))
	 */
	
	
	public static void rotateInPlaceBy1(boolean[] array, int offset, int length)
	{
		if (length < 0 || offset < 0 || offset + length > array.length)
			throw new IndexOutOfBoundsException();
		
		
		
		if (length == 0)
			return;
		
		boolean last = array[length-1+offset];
		
		for (int i = length-1; i >= 1; i--)
		{
			int prev = i == 0 ? length - 1 : i;
			
			array[i+offset] = array[prev+offset];
		}
		
		array[offset] = last;
	}
	
	public static void rotateInPlaceBy1(boolean[] array)
	{
		rotateInPlaceBy1(array, 0, array.length);
	}
	
	
	
	
	public static void rotateIntoBy1(boolean[] source, int sourceOffset, int length, @WritableValue boolean[] dest, int destOffset)
	{
		if (source == dest)
		{
			if (sourceOffset == destOffset)
				rotateInPlaceBy1(source, sourceOffset, length);
			else
				throw new NotYetImplementedException();
		}
		
		if (length < 0 || sourceOffset < 0 || sourceOffset + length > source.length)
			throw new IndexOutOfBoundsException();
		
		
		
		if (length == 0)
			return;
		
		for (int i = length-1; i >= 0; i--)
		{
			int prev = i == 0 ? length - 1 : i;
			
			source[i+sourceOffset] = source[prev+sourceOffset];
		}
	}
	
	
	
	@ThrowAwayValue
	public static boolean[] rotateOutOfPlaceBy1(boolean[] array, int offset, int length)
	{
		boolean[] output = new boolean[length];
		rotateIntoBy1(array, offset, length, output, 0);
		return output;
	}
	
	
	@ThrowAwayValue
	public static boolean[] rotateOutOfPlaceBy1(boolean[] array)
	{
		return rotateOutOfPlaceBy1(array, 0, array.length);
	}
	
	
	
	public static void rotateInPlaceBy1(byte[] array, int offset, int length)
	{
		if (length < 0 || offset < 0 || offset + length > array.length)
			throw new IndexOutOfBoundsException();
		
		
		
		if (length == 0)
			return;
		
		byte last = array[length-1+offset];
		
		for (int i = length-1; i >= 1; i--)
		{
			int prev = i == 0 ? length - 1 : i;
			
			array[i+offset] = array[prev+offset];
		}
		
		array[offset] = last;
	}
	
	public static void rotateInPlaceBy1(byte[] array)
	{
		rotateInPlaceBy1(array, 0, array.length);
	}
	
	
	
	
	public static void rotateIntoBy1(byte[] source, int sourceOffset, int length, @WritableValue byte[] dest, int destOffset)
	{
		if (source == dest)
		{
			if (sourceOffset == destOffset)
				rotateInPlaceBy1(source, sourceOffset, length);
			else
				throw new NotYetImplementedException();
		}
		
		if (length < 0 || sourceOffset < 0 || sourceOffset + length > source.length)
			throw new IndexOutOfBoundsException();
		
		
		
		if (length == 0)
			return;
		
		for (int i = length-1; i >= 0; i--)
		{
			int prev = i == 0 ? length - 1 : i;
			
			source[i+sourceOffset] = source[prev+sourceOffset];
		}
	}
	
	
	
	@ThrowAwayValue
	public static byte[] rotateOutOfPlaceBy1(byte[] array, int offset, int length)
	{
		byte[] output = new byte[length];
		rotateIntoBy1(array, offset, length, output, 0);
		return output;
	}
	
	
	@ThrowAwayValue
	public static byte[] rotateOutOfPlaceBy1(byte[] array)
	{
		return rotateOutOfPlaceBy1(array, 0, array.length);
	}
	
	
	
	public static void rotateInPlaceBy1(char[] array, int offset, int length)
	{
		if (length < 0 || offset < 0 || offset + length > array.length)
			throw new IndexOutOfBoundsException();
		
		
		
		if (length == 0)
			return;
		
		char last = array[length-1+offset];
		
		for (int i = length-1; i >= 1; i--)
		{
			int prev = i == 0 ? length - 1 : i;
			
			array[i+offset] = array[prev+offset];
		}
		
		array[offset] = last;
	}
	
	public static void rotateInPlaceBy1(char[] array)
	{
		rotateInPlaceBy1(array, 0, array.length);
	}
	
	
	
	
	public static void rotateIntoBy1(char[] source, int sourceOffset, int length, @WritableValue char[] dest, int destOffset)
	{
		if (source == dest)
		{
			if (sourceOffset == destOffset)
				rotateInPlaceBy1(source, sourceOffset, length);
			else
				throw new NotYetImplementedException();
		}
		
		if (length < 0 || sourceOffset < 0 || sourceOffset + length > source.length)
			throw new IndexOutOfBoundsException();
		
		
		
		if (length == 0)
			return;
		
		for (int i = length-1; i >= 0; i--)
		{
			int prev = i == 0 ? length - 1 : i;
			
			source[i+sourceOffset] = source[prev+sourceOffset];
		}
	}
	
	
	
	@ThrowAwayValue
	public static char[] rotateOutOfPlaceBy1(char[] array, int offset, int length)
	{
		char[] output = new char[length];
		rotateIntoBy1(array, offset, length, output, 0);
		return output;
	}
	
	
	@ThrowAwayValue
	public static char[] rotateOutOfPlaceBy1(char[] array)
	{
		return rotateOutOfPlaceBy1(array, 0, array.length);
	}
	
	
	
	public static void rotateInPlaceBy1(short[] array, int offset, int length)
	{
		if (length < 0 || offset < 0 || offset + length > array.length)
			throw new IndexOutOfBoundsException();
		
		
		
		if (length == 0)
			return;
		
		short last = array[length-1+offset];
		
		for (int i = length-1; i >= 1; i--)
		{
			int prev = i == 0 ? length - 1 : i;
			
			array[i+offset] = array[prev+offset];
		}
		
		array[offset] = last;
	}
	
	public static void rotateInPlaceBy1(short[] array)
	{
		rotateInPlaceBy1(array, 0, array.length);
	}
	
	
	
	
	public static void rotateIntoBy1(short[] source, int sourceOffset, int length, @WritableValue short[] dest, int destOffset)
	{
		if (source == dest)
		{
			if (sourceOffset == destOffset)
				rotateInPlaceBy1(source, sourceOffset, length);
			else
				throw new NotYetImplementedException();
		}
		
		if (length < 0 || sourceOffset < 0 || sourceOffset + length > source.length)
			throw new IndexOutOfBoundsException();
		
		
		
		if (length == 0)
			return;
		
		for (int i = length-1; i >= 0; i--)
		{
			int prev = i == 0 ? length - 1 : i;
			
			source[i+sourceOffset] = source[prev+sourceOffset];
		}
	}
	
	
	
	@ThrowAwayValue
	public static short[] rotateOutOfPlaceBy1(short[] array, int offset, int length)
	{
		short[] output = new short[length];
		rotateIntoBy1(array, offset, length, output, 0);
		return output;
	}
	
	
	@ThrowAwayValue
	public static short[] rotateOutOfPlaceBy1(short[] array)
	{
		return rotateOutOfPlaceBy1(array, 0, array.length);
	}
	
	
	
	public static void rotateInPlaceBy1(float[] array, int offset, int length)
	{
		if (length < 0 || offset < 0 || offset + length > array.length)
			throw new IndexOutOfBoundsException();
		
		
		
		if (length == 0)
			return;
		
		float last = array[length-1+offset];
		
		for (int i = length-1; i >= 1; i--)
		{
			int prev = i == 0 ? length - 1 : i;
			
			array[i+offset] = array[prev+offset];
		}
		
		array[offset] = last;
	}
	
	public static void rotateInPlaceBy1(float[] array)
	{
		rotateInPlaceBy1(array, 0, array.length);
	}
	
	
	
	
	public static void rotateIntoBy1(float[] source, int sourceOffset, int length, @WritableValue float[] dest, int destOffset)
	{
		if (source == dest)
		{
			if (sourceOffset == destOffset)
				rotateInPlaceBy1(source, sourceOffset, length);
			else
				throw new NotYetImplementedException();
		}
		
		if (length < 0 || sourceOffset < 0 || sourceOffset + length > source.length)
			throw new IndexOutOfBoundsException();
		
		
		
		if (length == 0)
			return;
		
		for (int i = length-1; i >= 0; i--)
		{
			int prev = i == 0 ? length - 1 : i;
			
			source[i+sourceOffset] = source[prev+sourceOffset];
		}
	}
	
	
	
	@ThrowAwayValue
	public static float[] rotateOutOfPlaceBy1(float[] array, int offset, int length)
	{
		float[] output = new float[length];
		rotateIntoBy1(array, offset, length, output, 0);
		return output;
	}
	
	
	@ThrowAwayValue
	public static float[] rotateOutOfPlaceBy1(float[] array)
	{
		return rotateOutOfPlaceBy1(array, 0, array.length);
	}
	
	
	
	public static void rotateInPlaceBy1(int[] array, int offset, int length)
	{
		if (length < 0 || offset < 0 || offset + length > array.length)
			throw new IndexOutOfBoundsException();
		
		
		
		if (length == 0)
			return;
		
		int last = array[length-1+offset];
		
		for (int i = length-1; i >= 1; i--)
		{
			int prev = i == 0 ? length - 1 : i;
			
			array[i+offset] = array[prev+offset];
		}
		
		array[offset] = last;
	}
	
	public static void rotateInPlaceBy1(int[] array)
	{
		rotateInPlaceBy1(array, 0, array.length);
	}
	
	
	
	
	public static void rotateIntoBy1(int[] source, int sourceOffset, int length, @WritableValue int[] dest, int destOffset)
	{
		if (source == dest)
		{
			if (sourceOffset == destOffset)
				rotateInPlaceBy1(source, sourceOffset, length);
			else
				throw new NotYetImplementedException();
		}
		
		if (length < 0 || sourceOffset < 0 || sourceOffset + length > source.length)
			throw new IndexOutOfBoundsException();
		
		
		
		if (length == 0)
			return;
		
		for (int i = length-1; i >= 0; i--)
		{
			int prev = i == 0 ? length - 1 : i;
			
			source[i+sourceOffset] = source[prev+sourceOffset];
		}
	}
	
	
	
	@ThrowAwayValue
	public static int[] rotateOutOfPlaceBy1(int[] array, int offset, int length)
	{
		int[] output = new int[length];
		rotateIntoBy1(array, offset, length, output, 0);
		return output;
	}
	
	
	@ThrowAwayValue
	public static int[] rotateOutOfPlaceBy1(int[] array)
	{
		return rotateOutOfPlaceBy1(array, 0, array.length);
	}
	
	
	
	public static void rotateInPlaceBy1(double[] array, int offset, int length)
	{
		if (length < 0 || offset < 0 || offset + length > array.length)
			throw new IndexOutOfBoundsException();
		
		
		
		if (length == 0)
			return;
		
		double last = array[length-1+offset];
		
		for (int i = length-1; i >= 1; i--)
		{
			int prev = i == 0 ? length - 1 : i;
			
			array[i+offset] = array[prev+offset];
		}
		
		array[offset] = last;
	}
	
	public static void rotateInPlaceBy1(double[] array)
	{
		rotateInPlaceBy1(array, 0, array.length);
	}
	
	
	
	
	public static void rotateIntoBy1(double[] source, int sourceOffset, int length, @WritableValue double[] dest, int destOffset)
	{
		if (source == dest)
		{
			if (sourceOffset == destOffset)
				rotateInPlaceBy1(source, sourceOffset, length);
			else
				throw new NotYetImplementedException();
		}
		
		if (length < 0 || sourceOffset < 0 || sourceOffset + length > source.length)
			throw new IndexOutOfBoundsException();
		
		
		
		if (length == 0)
			return;
		
		for (int i = length-1; i >= 0; i--)
		{
			int prev = i == 0 ? length - 1 : i;
			
			source[i+sourceOffset] = source[prev+sourceOffset];
		}
	}
	
	
	
	@ThrowAwayValue
	public static double[] rotateOutOfPlaceBy1(double[] array, int offset, int length)
	{
		double[] output = new double[length];
		rotateIntoBy1(array, offset, length, output, 0);
		return output;
	}
	
	
	@ThrowAwayValue
	public static double[] rotateOutOfPlaceBy1(double[] array)
	{
		return rotateOutOfPlaceBy1(array, 0, array.length);
	}
	
	
	
	public static void rotateInPlaceBy1(long[] array, int offset, int length)
	{
		if (length < 0 || offset < 0 || offset + length > array.length)
			throw new IndexOutOfBoundsException();
		
		
		
		if (length == 0)
			return;
		
		long last = array[length-1+offset];
		
		for (int i = length-1; i >= 1; i--)
		{
			int prev = i == 0 ? length - 1 : i;
			
			array[i+offset] = array[prev+offset];
		}
		
		array[offset] = last;
	}
	
	public static void rotateInPlaceBy1(long[] array)
	{
		rotateInPlaceBy1(array, 0, array.length);
	}
	
	
	
	
	public static void rotateIntoBy1(long[] source, int sourceOffset, int length, @WritableValue long[] dest, int destOffset)
	{
		if (source == dest)
		{
			if (sourceOffset == destOffset)
				rotateInPlaceBy1(source, sourceOffset, length);
			else
				throw new NotYetImplementedException();
		}
		
		if (length < 0 || sourceOffset < 0 || sourceOffset + length > source.length)
			throw new IndexOutOfBoundsException();
		
		
		
		if (length == 0)
			return;
		
		for (int i = length-1; i >= 0; i--)
		{
			int prev = i == 0 ? length - 1 : i;
			
			source[i+sourceOffset] = source[prev+sourceOffset];
		}
	}
	
	
	
	@ThrowAwayValue
	public static long[] rotateOutOfPlaceBy1(long[] array, int offset, int length)
	{
		long[] output = new long[length];
		rotateIntoBy1(array, offset, length, output, 0);
		return output;
	}
	
	
	@ThrowAwayValue
	public static long[] rotateOutOfPlaceBy1(long[] array)
	{
		return rotateOutOfPlaceBy1(array, 0, array.length);
	}
	
	
	
	public static void rotateInPlaceBy1(Object[] array, int offset, int length)
	{
		if (length < 0 || offset < 0 || offset + length > array.length)
			throw new IndexOutOfBoundsException();
		
		
		
		if (length == 0)
			return;
		
		Object last = array[length-1+offset];
		
		for (int i = length-1; i >= 1; i--)
		{
			int prev = i == 0 ? length - 1 : i;
			
			array[i+offset] = array[prev+offset];
		}
		
		array[offset] = last;
	}
	
	public static void rotateInPlaceBy1(Object[] array)
	{
		rotateInPlaceBy1(array, 0, array.length);
	}
	
	
	
	
	public static void rotateIntoBy1(Object[] source, int sourceOffset, int length, @WritableValue Object[] dest, int destOffset)
	{
		if (source == dest)
		{
			if (sourceOffset == destOffset)
				rotateInPlaceBy1(source, sourceOffset, length);
			else
				throw new NotYetImplementedException();
		}
		
		if (length < 0 || sourceOffset < 0 || sourceOffset + length > source.length)
			throw new IndexOutOfBoundsException();
		
		
		
		if (length == 0)
			return;
		
		for (int i = length-1; i >= 0; i--)
		{
			int prev = i == 0 ? length - 1 : i;
			
			source[i+sourceOffset] = source[prev+sourceOffset];
		}
	}
	
	
	
	@ThrowAwayValue
	public static Object[] rotateOutOfPlaceBy1(Object[] array, int offset, int length)
	{
		Object[] output = new Object[length];
		rotateIntoBy1(array, offset, length, output, 0);
		return output;
	}
	
	
	@ThrowAwayValue
	public static Object[] rotateOutOfPlaceBy1(Object[] array)
	{
		return rotateOutOfPlaceBy1(array, 0, array.length);
	}
	
	
	//>>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	python
	
	
	s = """
	
	public static void fillRangeByLength(@WritableValue _$$prim$$_[] array, int offset, _$$prim$$_ inclusiveStart, int length, _$$prim$$_ step)
	{
		for (int i = 0; i < length; i++)
			array[offset+i] = (_$$prim$$_)(inclusiveStart + (step * i));
	}
	
	public static void fillRangeByLength(@WritableValue _$$prim$$_[] array, int offset, _$$prim$$_ inclusiveStart, int length)
	{
		fillRangeByLength(array, offset, inclusiveStart, length, (_$$prim$$_)1);
	}
	
	@ThrowAwayValue
	public static _$$prim$$_[] rangeByLength(_$$prim$$_ inclusiveStart, int length, _$$prim$$_ step)
	{
		_$$prim$$_[] array = new _$$prim$$_[length];
		fillRangeByLength(array, 0, inclusiveStart, length, step);
		return array;
	}
	
	@ThrowAwayValue
	public static _$$prim$$_[] rangeByLength(_$$prim$$_ inclusiveStart, int length)
	{
		return rangeByLength(inclusiveStart, length, (_$$prim$$_)1);
	}
	
	
	
	public static _$$prim$$_[] range(_$$prim$$_ inclusiveStart, _$$prim$$_ exclusiveEnd)
	{
		int length = safeCast_$$bitabbv$$_toS32(exclusiveEnd - inclusiveStart);
		return rangeByLength(inclusiveStart, length);
	}
	
	public static _$$prim$$_[] range(_$$prim$$_ exclusiveEnd)
	{
		return range((_$$prim$$_)0, exclusiveEnd);
	}
	
	
	
	
	
	
	
	public static void fillRangeListByLength(@WritableValue _$$Primitive$$_List array, int offset, _$$prim$$_ inclusiveStart, int length, _$$prim$$_ step)
	{
		for (int i = 0; i < length; i++)
			array.set_$$Prim$$_(offset+i, (_$$prim$$_)(inclusiveStart + (step * i)));
	}
	
	public static void fillRangeListByLength(@WritableValue _$$Primitive$$_List array, int offset, _$$prim$$_ inclusiveStart, int length)
	{
		fillRangeListByLength(array, offset, inclusiveStart, length, (_$$prim$$_)1);
	}
	
	@ThrowAwayValue
	@WritableValue
	public static _$$Primitive$$_List rangeListByLength(_$$prim$$_ inclusiveStart, int length, _$$prim$$_ step)
	{
		_$$Primitive$$_List array = new_$$Primitive$$_ListZerofilled(length);
		fillRangeListByLength(array, 0, inclusiveStart, length, step);
		return array;
	}
	
	@ThrowAwayValue
	@WritableValue
	public static _$$Primitive$$_List rangeListByLength(_$$prim$$_ inclusiveStart, int length)
	{
		return rangeListByLength(inclusiveStart, length, (_$$prim$$_)1);
	}
	
	
	
	@WritableValue
	public static _$$Primitive$$_List rangeList(_$$prim$$_ inclusiveStart, _$$prim$$_ exclusiveEnd)
	{
		int length = safeCast_$$bitabbv$$_toS32(exclusiveEnd - inclusiveStart);
		return rangeListByLength(inclusiveStart, length);
	}
	
	@WritableValue
	public static _$$Primitive$$_List rangeList(_$$prim$$_ exclusiveEnd)
	{
		return rangeList((_$$prim$$_)0, exclusiveEnd);
	}
	
	
	
	"""
	
	
	
	p(primxp.primxp(prims=newSubdict(primxp.NumPrims, ["byte", "short", "char", "int"]), source=s.replace("safeCast_$$bitabbv$$_toS32", "")))
	
	p(primxp.primxp(prims=newSubdict(primxp.NumPrims, ["float", "double"]), source=s.replace("safeCast_$$bitabbv$$_toS32", "safeCastIntegerValuedFloatingPoint_$$bitabbv$$_toS32")))
	
	p(primxp.primxp(prims=newSubdict(primxp.NumPrims, ["long"]), source=s))
	
	
	 */
	
	
	public static void fillRangeByLength(@WritableValue byte[] array, int offset, byte inclusiveStart, int length, byte step)
	{
		for (int i = 0; i < length; i++)
			array[offset+i] = (byte)(inclusiveStart + (step * i));
	}
	
	public static void fillRangeByLength(@WritableValue byte[] array, int offset, byte inclusiveStart, int length)
	{
		fillRangeByLength(array, offset, inclusiveStart, length, (byte)1);
	}
	
	@ThrowAwayValue
	public static byte[] rangeByLength(byte inclusiveStart, int length, byte step)
	{
		byte[] array = new byte[length];
		fillRangeByLength(array, 0, inclusiveStart, length, step);
		return array;
	}
	
	@ThrowAwayValue
	public static byte[] rangeByLength(byte inclusiveStart, int length)
	{
		return rangeByLength(inclusiveStart, length, (byte)1);
	}
	
	
	
	public static byte[] range(byte inclusiveStart, byte exclusiveEnd)
	{
		int length = (exclusiveEnd - inclusiveStart);
		return rangeByLength(inclusiveStart, length);
	}
	
	public static byte[] range(byte exclusiveEnd)
	{
		return range((byte)0, exclusiveEnd);
	}
	
	
	
	
	
	
	
	public static void fillRangeListByLength(@WritableValue ByteList array, int offset, byte inclusiveStart, int length, byte step)
	{
		for (int i = 0; i < length; i++)
			array.setByte(offset+i, (byte)(inclusiveStart + (step * i)));
	}
	
	public static void fillRangeListByLength(@WritableValue ByteList array, int offset, byte inclusiveStart, int length)
	{
		fillRangeListByLength(array, offset, inclusiveStart, length, (byte)1);
	}
	
	@ThrowAwayValue
	@WritableValue
	public static ByteList rangeListByLength(byte inclusiveStart, int length, byte step)
	{
		ByteList array = newByteListZerofilled(length);
		fillRangeListByLength(array, 0, inclusiveStart, length, step);
		return array;
	}
	
	@ThrowAwayValue
	@WritableValue
	public static ByteList rangeListByLength(byte inclusiveStart, int length)
	{
		return rangeListByLength(inclusiveStart, length, (byte)1);
	}
	
	
	
	@WritableValue
	public static ByteList rangeList(byte inclusiveStart, byte exclusiveEnd)
	{
		int length = (exclusiveEnd - inclusiveStart);
		return rangeListByLength(inclusiveStart, length);
	}
	
	@WritableValue
	public static ByteList rangeList(byte exclusiveEnd)
	{
		return rangeList((byte)0, exclusiveEnd);
	}
	
	
	
	
	
	public static void fillRangeByLength(@WritableValue char[] array, int offset, char inclusiveStart, int length, char step)
	{
		for (int i = 0; i < length; i++)
			array[offset+i] = (char)(inclusiveStart + (step * i));
	}
	
	public static void fillRangeByLength(@WritableValue char[] array, int offset, char inclusiveStart, int length)
	{
		fillRangeByLength(array, offset, inclusiveStart, length, (char)1);
	}
	
	@ThrowAwayValue
	public static char[] rangeByLength(char inclusiveStart, int length, char step)
	{
		char[] array = new char[length];
		fillRangeByLength(array, 0, inclusiveStart, length, step);
		return array;
	}
	
	@ThrowAwayValue
	public static char[] rangeByLength(char inclusiveStart, int length)
	{
		return rangeByLength(inclusiveStart, length, (char)1);
	}
	
	
	
	public static char[] range(char inclusiveStart, char exclusiveEnd)
	{
		int length = (exclusiveEnd - inclusiveStart);
		return rangeByLength(inclusiveStart, length);
	}
	
	public static char[] range(char exclusiveEnd)
	{
		return range((char)0, exclusiveEnd);
	}
	
	
	
	
	
	
	
	public static void fillRangeListByLength(@WritableValue CharacterList array, int offset, char inclusiveStart, int length, char step)
	{
		for (int i = 0; i < length; i++)
			array.setChar(offset+i, (char)(inclusiveStart + (step * i)));
	}
	
	public static void fillRangeListByLength(@WritableValue CharacterList array, int offset, char inclusiveStart, int length)
	{
		fillRangeListByLength(array, offset, inclusiveStart, length, (char)1);
	}
	
	@ThrowAwayValue
	@WritableValue
	public static CharacterList rangeListByLength(char inclusiveStart, int length, char step)
	{
		CharacterList array = newCharacterListZerofilled(length);
		fillRangeListByLength(array, 0, inclusiveStart, length, step);
		return array;
	}
	
	@ThrowAwayValue
	@WritableValue
	public static CharacterList rangeListByLength(char inclusiveStart, int length)
	{
		return rangeListByLength(inclusiveStart, length, (char)1);
	}
	
	
	
	@WritableValue
	public static CharacterList rangeList(char inclusiveStart, char exclusiveEnd)
	{
		int length = (exclusiveEnd - inclusiveStart);
		return rangeListByLength(inclusiveStart, length);
	}
	
	@WritableValue
	public static CharacterList rangeList(char exclusiveEnd)
	{
		return rangeList((char)0, exclusiveEnd);
	}
	
	
	
	
	
	public static void fillRangeByLength(@WritableValue short[] array, int offset, short inclusiveStart, int length, short step)
	{
		for (int i = 0; i < length; i++)
			array[offset+i] = (short)(inclusiveStart + (step * i));
	}
	
	public static void fillRangeByLength(@WritableValue short[] array, int offset, short inclusiveStart, int length)
	{
		fillRangeByLength(array, offset, inclusiveStart, length, (short)1);
	}
	
	@ThrowAwayValue
	public static short[] rangeByLength(short inclusiveStart, int length, short step)
	{
		short[] array = new short[length];
		fillRangeByLength(array, 0, inclusiveStart, length, step);
		return array;
	}
	
	@ThrowAwayValue
	public static short[] rangeByLength(short inclusiveStart, int length)
	{
		return rangeByLength(inclusiveStart, length, (short)1);
	}
	
	
	
	public static short[] range(short inclusiveStart, short exclusiveEnd)
	{
		int length = (exclusiveEnd - inclusiveStart);
		return rangeByLength(inclusiveStart, length);
	}
	
	public static short[] range(short exclusiveEnd)
	{
		return range((short)0, exclusiveEnd);
	}
	
	
	
	
	
	
	
	public static void fillRangeListByLength(@WritableValue ShortList array, int offset, short inclusiveStart, int length, short step)
	{
		for (int i = 0; i < length; i++)
			array.setShort(offset+i, (short)(inclusiveStart + (step * i)));
	}
	
	public static void fillRangeListByLength(@WritableValue ShortList array, int offset, short inclusiveStart, int length)
	{
		fillRangeListByLength(array, offset, inclusiveStart, length, (short)1);
	}
	
	@ThrowAwayValue
	@WritableValue
	public static ShortList rangeListByLength(short inclusiveStart, int length, short step)
	{
		ShortList array = newShortListZerofilled(length);
		fillRangeListByLength(array, 0, inclusiveStart, length, step);
		return array;
	}
	
	@ThrowAwayValue
	@WritableValue
	public static ShortList rangeListByLength(short inclusiveStart, int length)
	{
		return rangeListByLength(inclusiveStart, length, (short)1);
	}
	
	
	
	@WritableValue
	public static ShortList rangeList(short inclusiveStart, short exclusiveEnd)
	{
		int length = (exclusiveEnd - inclusiveStart);
		return rangeListByLength(inclusiveStart, length);
	}
	
	@WritableValue
	public static ShortList rangeList(short exclusiveEnd)
	{
		return rangeList((short)0, exclusiveEnd);
	}
	
	
	
	
	
	public static void fillRangeByLength(@WritableValue int[] array, int offset, int inclusiveStart, int length, int step)
	{
		for (int i = 0; i < length; i++)
			array[offset+i] = (int)(inclusiveStart + (step * i));
	}
	
	public static void fillRangeByLength(@WritableValue int[] array, int offset, int inclusiveStart, int length)
	{
		fillRangeByLength(array, offset, inclusiveStart, length, (int)1);
	}
	
	@ThrowAwayValue
	public static int[] rangeByLength(int inclusiveStart, int length, int step)
	{
		int[] array = new int[length];
		fillRangeByLength(array, 0, inclusiveStart, length, step);
		return array;
	}
	
	@ThrowAwayValue
	public static int[] rangeByLength(int inclusiveStart, int length)
	{
		return rangeByLength(inclusiveStart, length, (int)1);
	}
	
	
	
	public static int[] range(int inclusiveStart, int exclusiveEnd)
	{
		int length = (exclusiveEnd - inclusiveStart);
		return rangeByLength(inclusiveStart, length);
	}
	
	public static int[] range(int exclusiveEnd)
	{
		return range((int)0, exclusiveEnd);
	}
	
	
	
	
	
	
	
	public static void fillRangeListByLength(@WritableValue IntegerList array, int offset, int inclusiveStart, int length, int step)
	{
		for (int i = 0; i < length; i++)
			array.setInt(offset+i, (int)(inclusiveStart + (step * i)));
	}
	
	public static void fillRangeListByLength(@WritableValue IntegerList array, int offset, int inclusiveStart, int length)
	{
		fillRangeListByLength(array, offset, inclusiveStart, length, (int)1);
	}
	
	@ThrowAwayValue
	@WritableValue
	public static IntegerList rangeListByLength(int inclusiveStart, int length, int step)
	{
		IntegerList array = newIntegerListZerofilled(length);
		fillRangeListByLength(array, 0, inclusiveStart, length, step);
		return array;
	}
	
	@ThrowAwayValue
	@WritableValue
	public static IntegerList rangeListByLength(int inclusiveStart, int length)
	{
		return rangeListByLength(inclusiveStart, length, (int)1);
	}
	
	
	
	@WritableValue
	public static IntegerList rangeList(int inclusiveStart, int exclusiveEnd)
	{
		int length = (exclusiveEnd - inclusiveStart);
		return rangeListByLength(inclusiveStart, length);
	}
	
	@WritableValue
	public static IntegerList rangeList(int exclusiveEnd)
	{
		return rangeList((int)0, exclusiveEnd);
	}
	
	
	
	
	
	public static void fillRangeByLength(@WritableValue float[] array, int offset, float inclusiveStart, int length, float step)
	{
		for (int i = 0; i < length; i++)
			array[offset+i] = (float)(inclusiveStart + (step * i));
	}
	
	public static void fillRangeByLength(@WritableValue float[] array, int offset, float inclusiveStart, int length)
	{
		fillRangeByLength(array, offset, inclusiveStart, length, (float)1);
	}
	
	@ThrowAwayValue
	public static float[] rangeByLength(float inclusiveStart, int length, float step)
	{
		float[] array = new float[length];
		fillRangeByLength(array, 0, inclusiveStart, length, step);
		return array;
	}
	
	@ThrowAwayValue
	public static float[] rangeByLength(float inclusiveStart, int length)
	{
		return rangeByLength(inclusiveStart, length, (float)1);
	}
	
	
	
	public static float[] range(float inclusiveStart, float exclusiveEnd)
	{
		int length = safeCastIntegerValuedFloatingPointF32toS32(exclusiveEnd - inclusiveStart);
		return rangeByLength(inclusiveStart, length);
	}
	
	public static float[] range(float exclusiveEnd)
	{
		return range((float)0, exclusiveEnd);
	}
	
	
	
	
	
	
	
	public static void fillRangeListByLength(@WritableValue FloatList array, int offset, float inclusiveStart, int length, float step)
	{
		for (int i = 0; i < length; i++)
			array.setFloat(offset+i, (float)(inclusiveStart + (step * i)));
	}
	
	public static void fillRangeListByLength(@WritableValue FloatList array, int offset, float inclusiveStart, int length)
	{
		fillRangeListByLength(array, offset, inclusiveStart, length, (float)1);
	}
	
	@ThrowAwayValue
	@WritableValue
	public static FloatList rangeListByLength(float inclusiveStart, int length, float step)
	{
		FloatList array = newFloatListZerofilled(length);
		fillRangeListByLength(array, 0, inclusiveStart, length, step);
		return array;
	}
	
	@ThrowAwayValue
	@WritableValue
	public static FloatList rangeListByLength(float inclusiveStart, int length)
	{
		return rangeListByLength(inclusiveStart, length, (float)1);
	}
	
	
	
	@WritableValue
	public static FloatList rangeList(float inclusiveStart, float exclusiveEnd)
	{
		int length = safeCastIntegerValuedFloatingPointF32toS32(exclusiveEnd - inclusiveStart);
		return rangeListByLength(inclusiveStart, length);
	}
	
	@WritableValue
	public static FloatList rangeList(float exclusiveEnd)
	{
		return rangeList((float)0, exclusiveEnd);
	}
	
	
	
	
	
	public static void fillRangeByLength(@WritableValue double[] array, int offset, double inclusiveStart, int length, double step)
	{
		for (int i = 0; i < length; i++)
			array[offset+i] = (double)(inclusiveStart + (step * i));
	}
	
	public static void fillRangeByLength(@WritableValue double[] array, int offset, double inclusiveStart, int length)
	{
		fillRangeByLength(array, offset, inclusiveStart, length, (double)1);
	}
	
	@ThrowAwayValue
	public static double[] rangeByLength(double inclusiveStart, int length, double step)
	{
		double[] array = new double[length];
		fillRangeByLength(array, 0, inclusiveStart, length, step);
		return array;
	}
	
	@ThrowAwayValue
	public static double[] rangeByLength(double inclusiveStart, int length)
	{
		return rangeByLength(inclusiveStart, length, (double)1);
	}
	
	
	
	public static double[] range(double inclusiveStart, double exclusiveEnd)
	{
		int length = safeCastIntegerValuedFloatingPointF64toS32(exclusiveEnd - inclusiveStart);
		return rangeByLength(inclusiveStart, length);
	}
	
	public static double[] range(double exclusiveEnd)
	{
		return range((double)0, exclusiveEnd);
	}
	
	
	
	
	
	
	
	public static void fillRangeListByLength(@WritableValue DoubleList array, int offset, double inclusiveStart, int length, double step)
	{
		for (int i = 0; i < length; i++)
			array.setDouble(offset+i, (double)(inclusiveStart + (step * i)));
	}
	
	public static void fillRangeListByLength(@WritableValue DoubleList array, int offset, double inclusiveStart, int length)
	{
		fillRangeListByLength(array, offset, inclusiveStart, length, (double)1);
	}
	
	@ThrowAwayValue
	@WritableValue
	public static DoubleList rangeListByLength(double inclusiveStart, int length, double step)
	{
		DoubleList array = newDoubleListZerofilled(length);
		fillRangeListByLength(array, 0, inclusiveStart, length, step);
		return array;
	}
	
	@ThrowAwayValue
	@WritableValue
	public static DoubleList rangeListByLength(double inclusiveStart, int length)
	{
		return rangeListByLength(inclusiveStart, length, (double)1);
	}
	
	
	
	@WritableValue
	public static DoubleList rangeList(double inclusiveStart, double exclusiveEnd)
	{
		int length = safeCastIntegerValuedFloatingPointF64toS32(exclusiveEnd - inclusiveStart);
		return rangeListByLength(inclusiveStart, length);
	}
	
	@WritableValue
	public static DoubleList rangeList(double exclusiveEnd)
	{
		return rangeList((double)0, exclusiveEnd);
	}
	
	
	
	
	
	public static void fillRangeByLength(@WritableValue long[] array, int offset, long inclusiveStart, int length, long step)
	{
		for (int i = 0; i < length; i++)
			array[offset+i] = (long)(inclusiveStart + (step * i));
	}
	
	public static void fillRangeByLength(@WritableValue long[] array, int offset, long inclusiveStart, int length)
	{
		fillRangeByLength(array, offset, inclusiveStart, length, (long)1);
	}
	
	@ThrowAwayValue
	public static long[] rangeByLength(long inclusiveStart, int length, long step)
	{
		long[] array = new long[length];
		fillRangeByLength(array, 0, inclusiveStart, length, step);
		return array;
	}
	
	@ThrowAwayValue
	public static long[] rangeByLength(long inclusiveStart, int length)
	{
		return rangeByLength(inclusiveStart, length, (long)1);
	}
	
	
	
	public static long[] range(long inclusiveStart, long exclusiveEnd)
	{
		int length = safeCastS64toS32(exclusiveEnd - inclusiveStart);
		return rangeByLength(inclusiveStart, length);
	}
	
	public static long[] range(long exclusiveEnd)
	{
		return range((long)0, exclusiveEnd);
	}
	
	
	
	
	
	
	
	public static void fillRangeListByLength(@WritableValue LongList array, int offset, long inclusiveStart, int length, long step)
	{
		for (int i = 0; i < length; i++)
			array.setLong(offset+i, (long)(inclusiveStart + (step * i)));
	}
	
	public static void fillRangeListByLength(@WritableValue LongList array, int offset, long inclusiveStart, int length)
	{
		fillRangeListByLength(array, offset, inclusiveStart, length, (long)1);
	}
	
	@ThrowAwayValue
	@WritableValue
	public static LongList rangeListByLength(long inclusiveStart, int length, long step)
	{
		LongList array = newLongListZerofilled(length);
		fillRangeListByLength(array, 0, inclusiveStart, length, step);
		return array;
	}
	
	@ThrowAwayValue
	@WritableValue
	public static LongList rangeListByLength(long inclusiveStart, int length)
	{
		return rangeListByLength(inclusiveStart, length, (long)1);
	}
	
	
	
	@WritableValue
	public static LongList rangeList(long inclusiveStart, long exclusiveEnd)
	{
		int length = safeCastS64toS32(exclusiveEnd - inclusiveStart);
		return rangeListByLength(inclusiveStart, length);
	}
	
	@WritableValue
	public static LongList rangeList(long exclusiveEnd)
	{
		return rangeList((long)0, exclusiveEnd);
	}
	
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	primxp
	
	@ThrowAwayValue
	public static _$$prim$$_[] join(_$$prim$$_ delimiter, @ReadonlyValue _$$prim$$_[]... strings)
	{
		if (strings.length == 0)
		{
			return Empty_$$Prim$$_Array;
		}
		else
		{
			int nStrings = strings.length;
			
			int totalLengthOfStrings;
			{
				totalLengthOfStrings = 0;
				for (_$$prim$$_[] string : strings)
					totalLengthOfStrings += string.length;
			}
			
			
			final int delimiterLength = 1;
			
			int totalLength = totalLengthOfStrings + delimiterLength * (nStrings - 1);
			
			_$$prim$$_[] joined = new _$$prim$$_[totalLength];
			
			int pos = 0;
			for (int stringIndex = 0; stringIndex < nStrings; stringIndex++)
			{
				boolean last = stringIndex == nStrings - 1;
				
				_$$prim$$_[] s = strings[stringIndex];
				
				int sl = s.length;
				System.arraycopy(s, 0, joined, pos, sl);
				pos += sl;
				
				if (!last)
				{
					joined[pos] = delimiter;
					pos += delimiterLength;
				}
			}
			
			return joined;
		}
	}
	
	
	
	
	 */
	
	@ThrowAwayValue
	public static boolean[] join(boolean delimiter, @ReadonlyValue boolean[]... strings)
	{
		if (strings.length == 0)
		{
			return EmptyBooleanArray;
		}
		else
		{
			int nStrings = strings.length;
			
			int totalLengthOfStrings;
			{
				totalLengthOfStrings = 0;
				for (boolean[] string : strings)
					totalLengthOfStrings += string.length;
			}
			
			
			final int delimiterLength = 1;
			
			int totalLength = totalLengthOfStrings + delimiterLength * (nStrings - 1);
			
			boolean[] joined = new boolean[totalLength];
			
			int pos = 0;
			for (int stringIndex = 0; stringIndex < nStrings; stringIndex++)
			{
				boolean last = stringIndex == nStrings - 1;
				
				boolean[] s = strings[stringIndex];
				
				int sl = s.length;
				System.arraycopy(s, 0, joined, pos, sl);
				pos += sl;
				
				if (!last)
				{
					joined[pos] = delimiter;
					pos += delimiterLength;
				}
			}
			
			return joined;
		}
	}
	
	
	
	
	
	@ThrowAwayValue
	public static byte[] join(byte delimiter, @ReadonlyValue byte[]... strings)
	{
		if (strings.length == 0)
		{
			return EmptyByteArray;
		}
		else
		{
			int nStrings = strings.length;
			
			int totalLengthOfStrings;
			{
				totalLengthOfStrings = 0;
				for (byte[] string : strings)
					totalLengthOfStrings += string.length;
			}
			
			
			final int delimiterLength = 1;
			
			int totalLength = totalLengthOfStrings + delimiterLength * (nStrings - 1);
			
			byte[] joined = new byte[totalLength];
			
			int pos = 0;
			for (int stringIndex = 0; stringIndex < nStrings; stringIndex++)
			{
				boolean last = stringIndex == nStrings - 1;
				
				byte[] s = strings[stringIndex];
				
				int sl = s.length;
				System.arraycopy(s, 0, joined, pos, sl);
				pos += sl;
				
				if (!last)
				{
					joined[pos] = delimiter;
					pos += delimiterLength;
				}
			}
			
			return joined;
		}
	}
	
	
	
	
	
	@ThrowAwayValue
	public static char[] join(char delimiter, @ReadonlyValue char[]... strings)
	{
		if (strings.length == 0)
		{
			return EmptyCharArray;
		}
		else
		{
			int nStrings = strings.length;
			
			int totalLengthOfStrings;
			{
				totalLengthOfStrings = 0;
				for (char[] string : strings)
					totalLengthOfStrings += string.length;
			}
			
			
			final int delimiterLength = 1;
			
			int totalLength = totalLengthOfStrings + delimiterLength * (nStrings - 1);
			
			char[] joined = new char[totalLength];
			
			int pos = 0;
			for (int stringIndex = 0; stringIndex < nStrings; stringIndex++)
			{
				boolean last = stringIndex == nStrings - 1;
				
				char[] s = strings[stringIndex];
				
				int sl = s.length;
				System.arraycopy(s, 0, joined, pos, sl);
				pos += sl;
				
				if (!last)
				{
					joined[pos] = delimiter;
					pos += delimiterLength;
				}
			}
			
			return joined;
		}
	}
	
	
	
	
	
	@ThrowAwayValue
	public static short[] join(short delimiter, @ReadonlyValue short[]... strings)
	{
		if (strings.length == 0)
		{
			return EmptyShortArray;
		}
		else
		{
			int nStrings = strings.length;
			
			int totalLengthOfStrings;
			{
				totalLengthOfStrings = 0;
				for (short[] string : strings)
					totalLengthOfStrings += string.length;
			}
			
			
			final int delimiterLength = 1;
			
			int totalLength = totalLengthOfStrings + delimiterLength * (nStrings - 1);
			
			short[] joined = new short[totalLength];
			
			int pos = 0;
			for (int stringIndex = 0; stringIndex < nStrings; stringIndex++)
			{
				boolean last = stringIndex == nStrings - 1;
				
				short[] s = strings[stringIndex];
				
				int sl = s.length;
				System.arraycopy(s, 0, joined, pos, sl);
				pos += sl;
				
				if (!last)
				{
					joined[pos] = delimiter;
					pos += delimiterLength;
				}
			}
			
			return joined;
		}
	}
	
	
	
	
	
	@ThrowAwayValue
	public static float[] join(float delimiter, @ReadonlyValue float[]... strings)
	{
		if (strings.length == 0)
		{
			return EmptyFloatArray;
		}
		else
		{
			int nStrings = strings.length;
			
			int totalLengthOfStrings;
			{
				totalLengthOfStrings = 0;
				for (float[] string : strings)
					totalLengthOfStrings += string.length;
			}
			
			
			final int delimiterLength = 1;
			
			int totalLength = totalLengthOfStrings + delimiterLength * (nStrings - 1);
			
			float[] joined = new float[totalLength];
			
			int pos = 0;
			for (int stringIndex = 0; stringIndex < nStrings; stringIndex++)
			{
				boolean last = stringIndex == nStrings - 1;
				
				float[] s = strings[stringIndex];
				
				int sl = s.length;
				System.arraycopy(s, 0, joined, pos, sl);
				pos += sl;
				
				if (!last)
				{
					joined[pos] = delimiter;
					pos += delimiterLength;
				}
			}
			
			return joined;
		}
	}
	
	
	
	
	
	@ThrowAwayValue
	public static int[] join(int delimiter, @ReadonlyValue int[]... strings)
	{
		if (strings.length == 0)
		{
			return EmptyIntArray;
		}
		else
		{
			int nStrings = strings.length;
			
			int totalLengthOfStrings;
			{
				totalLengthOfStrings = 0;
				for (int[] string : strings)
					totalLengthOfStrings += string.length;
			}
			
			
			final int delimiterLength = 1;
			
			int totalLength = totalLengthOfStrings + delimiterLength * (nStrings - 1);
			
			int[] joined = new int[totalLength];
			
			int pos = 0;
			for (int stringIndex = 0; stringIndex < nStrings; stringIndex++)
			{
				boolean last = stringIndex == nStrings - 1;
				
				int[] s = strings[stringIndex];
				
				int sl = s.length;
				System.arraycopy(s, 0, joined, pos, sl);
				pos += sl;
				
				if (!last)
				{
					joined[pos] = delimiter;
					pos += delimiterLength;
				}
			}
			
			return joined;
		}
	}
	
	
	
	
	
	@ThrowAwayValue
	public static double[] join(double delimiter, @ReadonlyValue double[]... strings)
	{
		if (strings.length == 0)
		{
			return EmptyDoubleArray;
		}
		else
		{
			int nStrings = strings.length;
			
			int totalLengthOfStrings;
			{
				totalLengthOfStrings = 0;
				for (double[] string : strings)
					totalLengthOfStrings += string.length;
			}
			
			
			final int delimiterLength = 1;
			
			int totalLength = totalLengthOfStrings + delimiterLength * (nStrings - 1);
			
			double[] joined = new double[totalLength];
			
			int pos = 0;
			for (int stringIndex = 0; stringIndex < nStrings; stringIndex++)
			{
				boolean last = stringIndex == nStrings - 1;
				
				double[] s = strings[stringIndex];
				
				int sl = s.length;
				System.arraycopy(s, 0, joined, pos, sl);
				pos += sl;
				
				if (!last)
				{
					joined[pos] = delimiter;
					pos += delimiterLength;
				}
			}
			
			return joined;
		}
	}
	
	
	
	
	
	@ThrowAwayValue
	public static long[] join(long delimiter, @ReadonlyValue long[]... strings)
	{
		if (strings.length == 0)
		{
			return EmptyLongArray;
		}
		else
		{
			int nStrings = strings.length;
			
			int totalLengthOfStrings;
			{
				totalLengthOfStrings = 0;
				for (long[] string : strings)
					totalLengthOfStrings += string.length;
			}
			
			
			final int delimiterLength = 1;
			
			int totalLength = totalLengthOfStrings + delimiterLength * (nStrings - 1);
			
			long[] joined = new long[totalLength];
			
			int pos = 0;
			for (int stringIndex = 0; stringIndex < nStrings; stringIndex++)
			{
				boolean last = stringIndex == nStrings - 1;
				
				long[] s = strings[stringIndex];
				
				int sl = s.length;
				System.arraycopy(s, 0, joined, pos, sl);
				pos += sl;
				
				if (!last)
				{
					joined[pos] = delimiter;
					pos += delimiterLength;
				}
			}
			
			return joined;
		}
	}
	
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	primxp
	
	
	public static void replaceByPatternInPlace(@WritableValue _$$prim$$_[] array, UnaryFunction_$$Prim$$_ToBoolean pattern, _$$prim$$_ replacement)
	{
		replaceByPattern(array, array, pattern, replacement);
	}
	
	@ThrowAwayValue
	public static _$$prim$$_[] replaceByPatternToNew(@ReadonlyValue _$$prim$$_[] sourceArray, UnaryFunction_$$Prim$$_ToBoolean pattern, _$$prim$$_ replacement)
	{
		_$$prim$$_[] newArray = new _$$prim$$_[sourceArray.length];
		replaceByPattern(sourceArray, newArray, pattern, replacement);
		return newArray;
	}
	
	public static void replaceByPattern(@ReadonlyValue _$$prim$$_[] sourceArray, @WritableValue _$$prim$$_[] destArray, UnaryFunction_$$Prim$$_ToBoolean pattern, _$$prim$$_ replacement)
	{
		if (sourceArray.length != destArray.length)
			throw new IllegalArgumentException();
		replaceByPattern(sourceArray, 0, destArray, 0, sourceArray.length, pattern, replacement);
	}
	
	public static void replaceByPattern(@ReadonlyValue _$$prim$$_[] sourceArray, int sourceOffset, @WritableValue _$$prim$$_[] destArray, int destOffset, int length, UnaryFunction_$$Prim$$_ToBoolean pattern, _$$prim$$_ replacement)
	{
		for (int i = 0; i < length; i++)
		{
			_$$prim$$_ s = sourceArray[sourceOffset+i];
			destArray[destOffset+i] = pattern.f(s) ? replacement : s;
		}
	}
	
	
	
	
	 */
	
	
	public static void replaceByPatternInPlace(@WritableValue boolean[] array, UnaryFunctionBooleanToBoolean pattern, boolean replacement)
	{
		replaceByPattern(array, array, pattern, replacement);
	}
	
	@ThrowAwayValue
	public static boolean[] replaceByPatternToNew(@ReadonlyValue boolean[] sourceArray, UnaryFunctionBooleanToBoolean pattern, boolean replacement)
	{
		boolean[] newArray = new boolean[sourceArray.length];
		replaceByPattern(sourceArray, newArray, pattern, replacement);
		return newArray;
	}
	
	public static void replaceByPattern(@ReadonlyValue boolean[] sourceArray, @WritableValue boolean[] destArray, UnaryFunctionBooleanToBoolean pattern, boolean replacement)
	{
		if (sourceArray.length != destArray.length)
			throw new IllegalArgumentException();
		replaceByPattern(sourceArray, 0, destArray, 0, sourceArray.length, pattern, replacement);
	}
	
	public static void replaceByPattern(@ReadonlyValue boolean[] sourceArray, int sourceOffset, @WritableValue boolean[] destArray, int destOffset, int length, UnaryFunctionBooleanToBoolean pattern, boolean replacement)
	{
		for (int i = 0; i < length; i++)
		{
			boolean s = sourceArray[sourceOffset+i];
			destArray[destOffset+i] = pattern.f(s) ? replacement : s;
		}
	}
	
	
	
	
	
	
	public static void replaceByPatternInPlace(@WritableValue byte[] array, UnaryFunctionByteToBoolean pattern, byte replacement)
	{
		replaceByPattern(array, array, pattern, replacement);
	}
	
	@ThrowAwayValue
	public static byte[] replaceByPatternToNew(@ReadonlyValue byte[] sourceArray, UnaryFunctionByteToBoolean pattern, byte replacement)
	{
		byte[] newArray = new byte[sourceArray.length];
		replaceByPattern(sourceArray, newArray, pattern, replacement);
		return newArray;
	}
	
	public static void replaceByPattern(@ReadonlyValue byte[] sourceArray, @WritableValue byte[] destArray, UnaryFunctionByteToBoolean pattern, byte replacement)
	{
		if (sourceArray.length != destArray.length)
			throw new IllegalArgumentException();
		replaceByPattern(sourceArray, 0, destArray, 0, sourceArray.length, pattern, replacement);
	}
	
	public static void replaceByPattern(@ReadonlyValue byte[] sourceArray, int sourceOffset, @WritableValue byte[] destArray, int destOffset, int length, UnaryFunctionByteToBoolean pattern, byte replacement)
	{
		for (int i = 0; i < length; i++)
		{
			byte s = sourceArray[sourceOffset+i];
			destArray[destOffset+i] = pattern.f(s) ? replacement : s;
		}
	}
	
	
	
	
	
	
	public static void replaceByPatternInPlace(@WritableValue char[] array, UnaryFunctionCharToBoolean pattern, char replacement)
	{
		replaceByPattern(array, array, pattern, replacement);
	}
	
	@ThrowAwayValue
	public static char[] replaceByPatternToNew(@ReadonlyValue char[] sourceArray, UnaryFunctionCharToBoolean pattern, char replacement)
	{
		char[] newArray = new char[sourceArray.length];
		replaceByPattern(sourceArray, newArray, pattern, replacement);
		return newArray;
	}
	
	public static void replaceByPattern(@ReadonlyValue char[] sourceArray, @WritableValue char[] destArray, UnaryFunctionCharToBoolean pattern, char replacement)
	{
		if (sourceArray.length != destArray.length)
			throw new IllegalArgumentException();
		replaceByPattern(sourceArray, 0, destArray, 0, sourceArray.length, pattern, replacement);
	}
	
	public static void replaceByPattern(@ReadonlyValue char[] sourceArray, int sourceOffset, @WritableValue char[] destArray, int destOffset, int length, UnaryFunctionCharToBoolean pattern, char replacement)
	{
		for (int i = 0; i < length; i++)
		{
			char s = sourceArray[sourceOffset+i];
			destArray[destOffset+i] = pattern.f(s) ? replacement : s;
		}
	}
	
	
	
	
	
	
	public static void replaceByPatternInPlace(@WritableValue short[] array, UnaryFunctionShortToBoolean pattern, short replacement)
	{
		replaceByPattern(array, array, pattern, replacement);
	}
	
	@ThrowAwayValue
	public static short[] replaceByPatternToNew(@ReadonlyValue short[] sourceArray, UnaryFunctionShortToBoolean pattern, short replacement)
	{
		short[] newArray = new short[sourceArray.length];
		replaceByPattern(sourceArray, newArray, pattern, replacement);
		return newArray;
	}
	
	public static void replaceByPattern(@ReadonlyValue short[] sourceArray, @WritableValue short[] destArray, UnaryFunctionShortToBoolean pattern, short replacement)
	{
		if (sourceArray.length != destArray.length)
			throw new IllegalArgumentException();
		replaceByPattern(sourceArray, 0, destArray, 0, sourceArray.length, pattern, replacement);
	}
	
	public static void replaceByPattern(@ReadonlyValue short[] sourceArray, int sourceOffset, @WritableValue short[] destArray, int destOffset, int length, UnaryFunctionShortToBoolean pattern, short replacement)
	{
		for (int i = 0; i < length; i++)
		{
			short s = sourceArray[sourceOffset+i];
			destArray[destOffset+i] = pattern.f(s) ? replacement : s;
		}
	}
	
	
	
	
	
	
	public static void replaceByPatternInPlace(@WritableValue float[] array, UnaryFunctionFloatToBoolean pattern, float replacement)
	{
		replaceByPattern(array, array, pattern, replacement);
	}
	
	@ThrowAwayValue
	public static float[] replaceByPatternToNew(@ReadonlyValue float[] sourceArray, UnaryFunctionFloatToBoolean pattern, float replacement)
	{
		float[] newArray = new float[sourceArray.length];
		replaceByPattern(sourceArray, newArray, pattern, replacement);
		return newArray;
	}
	
	public static void replaceByPattern(@ReadonlyValue float[] sourceArray, @WritableValue float[] destArray, UnaryFunctionFloatToBoolean pattern, float replacement)
	{
		if (sourceArray.length != destArray.length)
			throw new IllegalArgumentException();
		replaceByPattern(sourceArray, 0, destArray, 0, sourceArray.length, pattern, replacement);
	}
	
	public static void replaceByPattern(@ReadonlyValue float[] sourceArray, int sourceOffset, @WritableValue float[] destArray, int destOffset, int length, UnaryFunctionFloatToBoolean pattern, float replacement)
	{
		for (int i = 0; i < length; i++)
		{
			float s = sourceArray[sourceOffset+i];
			destArray[destOffset+i] = pattern.f(s) ? replacement : s;
		}
	}
	
	
	
	
	
	
	public static void replaceByPatternInPlace(@WritableValue int[] array, UnaryFunctionIntToBoolean pattern, int replacement)
	{
		replaceByPattern(array, array, pattern, replacement);
	}
	
	@ThrowAwayValue
	public static int[] replaceByPatternToNew(@ReadonlyValue int[] sourceArray, UnaryFunctionIntToBoolean pattern, int replacement)
	{
		int[] newArray = new int[sourceArray.length];
		replaceByPattern(sourceArray, newArray, pattern, replacement);
		return newArray;
	}
	
	public static void replaceByPattern(@ReadonlyValue int[] sourceArray, @WritableValue int[] destArray, UnaryFunctionIntToBoolean pattern, int replacement)
	{
		if (sourceArray.length != destArray.length)
			throw new IllegalArgumentException();
		replaceByPattern(sourceArray, 0, destArray, 0, sourceArray.length, pattern, replacement);
	}
	
	public static void replaceByPattern(@ReadonlyValue int[] sourceArray, int sourceOffset, @WritableValue int[] destArray, int destOffset, int length, UnaryFunctionIntToBoolean pattern, int replacement)
	{
		for (int i = 0; i < length; i++)
		{
			int s = sourceArray[sourceOffset+i];
			destArray[destOffset+i] = pattern.f(s) ? replacement : s;
		}
	}
	
	
	
	
	
	
	public static void replaceByPatternInPlace(@WritableValue double[] array, UnaryFunctionDoubleToBoolean pattern, double replacement)
	{
		replaceByPattern(array, array, pattern, replacement);
	}
	
	@ThrowAwayValue
	public static double[] replaceByPatternToNew(@ReadonlyValue double[] sourceArray, UnaryFunctionDoubleToBoolean pattern, double replacement)
	{
		double[] newArray = new double[sourceArray.length];
		replaceByPattern(sourceArray, newArray, pattern, replacement);
		return newArray;
	}
	
	public static void replaceByPattern(@ReadonlyValue double[] sourceArray, @WritableValue double[] destArray, UnaryFunctionDoubleToBoolean pattern, double replacement)
	{
		if (sourceArray.length != destArray.length)
			throw new IllegalArgumentException();
		replaceByPattern(sourceArray, 0, destArray, 0, sourceArray.length, pattern, replacement);
	}
	
	public static void replaceByPattern(@ReadonlyValue double[] sourceArray, int sourceOffset, @WritableValue double[] destArray, int destOffset, int length, UnaryFunctionDoubleToBoolean pattern, double replacement)
	{
		for (int i = 0; i < length; i++)
		{
			double s = sourceArray[sourceOffset+i];
			destArray[destOffset+i] = pattern.f(s) ? replacement : s;
		}
	}
	
	
	
	
	
	
	public static void replaceByPatternInPlace(@WritableValue long[] array, UnaryFunctionLongToBoolean pattern, long replacement)
	{
		replaceByPattern(array, array, pattern, replacement);
	}
	
	@ThrowAwayValue
	public static long[] replaceByPatternToNew(@ReadonlyValue long[] sourceArray, UnaryFunctionLongToBoolean pattern, long replacement)
	{
		long[] newArray = new long[sourceArray.length];
		replaceByPattern(sourceArray, newArray, pattern, replacement);
		return newArray;
	}
	
	public static void replaceByPattern(@ReadonlyValue long[] sourceArray, @WritableValue long[] destArray, UnaryFunctionLongToBoolean pattern, long replacement)
	{
		if (sourceArray.length != destArray.length)
			throw new IllegalArgumentException();
		replaceByPattern(sourceArray, 0, destArray, 0, sourceArray.length, pattern, replacement);
	}
	
	public static void replaceByPattern(@ReadonlyValue long[] sourceArray, int sourceOffset, @WritableValue long[] destArray, int destOffset, int length, UnaryFunctionLongToBoolean pattern, long replacement)
	{
		for (int i = 0; i < length; i++)
		{
			long s = sourceArray[sourceOffset+i];
			destArray[destOffset+i] = pattern.f(s) ? replacement : s;
		}
	}
	
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	primxp
	
	
	
	
	
	public static _$$prim$$_[] filterArray(UnaryFunction_$$Prim$$_ToBoolean predicate, _$$prim$$_[] a)
	{
		final int nIn = a.length;
		final _$$prim$$_[] temp = new _$$prim$$_[nIn];
		
		int nOut = 0;
		
		for (int i = 0; i < nIn; i++)
		{
			_$$prim$$_ c = a[i];
			if (predicate.f(c))
			{
				temp[nOut] = c;
				nOut++;
			}
		}
		
		return Arrays.copyOf(temp, nOut);
	}
	 */
	
	
	
	
	
	public static boolean[] filterArray(UnaryFunctionBooleanToBoolean predicate, boolean[] a)
	{
		final int nIn = a.length;
		final boolean[] temp = new boolean[nIn];
		
		int nOut = 0;
		
		for (int i = 0; i < nIn; i++)
		{
			boolean c = a[i];
			if (predicate.f(c))
			{
				temp[nOut] = c;
				nOut++;
			}
		}
		
		return Arrays.copyOf(temp, nOut);
	}
	
	
	
	
	
	public static byte[] filterArray(UnaryFunctionByteToBoolean predicate, byte[] a)
	{
		final int nIn = a.length;
		final byte[] temp = new byte[nIn];
		
		int nOut = 0;
		
		for (int i = 0; i < nIn; i++)
		{
			byte c = a[i];
			if (predicate.f(c))
			{
				temp[nOut] = c;
				nOut++;
			}
		}
		
		return Arrays.copyOf(temp, nOut);
	}
	
	
	
	
	
	public static char[] filterArray(UnaryFunctionCharToBoolean predicate, char[] a)
	{
		final int nIn = a.length;
		final char[] temp = new char[nIn];
		
		int nOut = 0;
		
		for (int i = 0; i < nIn; i++)
		{
			char c = a[i];
			if (predicate.f(c))
			{
				temp[nOut] = c;
				nOut++;
			}
		}
		
		return Arrays.copyOf(temp, nOut);
	}
	
	
	
	
	
	public static short[] filterArray(UnaryFunctionShortToBoolean predicate, short[] a)
	{
		final int nIn = a.length;
		final short[] temp = new short[nIn];
		
		int nOut = 0;
		
		for (int i = 0; i < nIn; i++)
		{
			short c = a[i];
			if (predicate.f(c))
			{
				temp[nOut] = c;
				nOut++;
			}
		}
		
		return Arrays.copyOf(temp, nOut);
	}
	
	
	
	
	
	public static float[] filterArray(UnaryFunctionFloatToBoolean predicate, float[] a)
	{
		final int nIn = a.length;
		final float[] temp = new float[nIn];
		
		int nOut = 0;
		
		for (int i = 0; i < nIn; i++)
		{
			float c = a[i];
			if (predicate.f(c))
			{
				temp[nOut] = c;
				nOut++;
			}
		}
		
		return Arrays.copyOf(temp, nOut);
	}
	
	
	
	
	
	public static int[] filterArray(UnaryFunctionIntToBoolean predicate, int[] a)
	{
		final int nIn = a.length;
		final int[] temp = new int[nIn];
		
		int nOut = 0;
		
		for (int i = 0; i < nIn; i++)
		{
			int c = a[i];
			if (predicate.f(c))
			{
				temp[nOut] = c;
				nOut++;
			}
		}
		
		return Arrays.copyOf(temp, nOut);
	}
	
	
	
	
	
	public static double[] filterArray(UnaryFunctionDoubleToBoolean predicate, double[] a)
	{
		final int nIn = a.length;
		final double[] temp = new double[nIn];
		
		int nOut = 0;
		
		for (int i = 0; i < nIn; i++)
		{
			double c = a[i];
			if (predicate.f(c))
			{
				temp[nOut] = c;
				nOut++;
			}
		}
		
		return Arrays.copyOf(temp, nOut);
	}
	
	
	
	
	
	public static long[] filterArray(UnaryFunctionLongToBoolean predicate, long[] a)
	{
		final int nIn = a.length;
		final long[] temp = new long[nIn];
		
		int nOut = 0;
		
		for (int i = 0; i < nIn; i++)
		{
			long c = a[i];
			if (predicate.f(c))
			{
				temp[nOut] = c;
				nOut++;
			}
		}
		
		return Arrays.copyOf(temp, nOut);
	}
	//>>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@ThrowAwayValue
	public static <T> T[] castArrayRuntimeTypeToNew(Object[] baseArray, Class<T> newComponentType)
	{
		if (baseArray == null)
			return null;
		else
		{
			int n = baseArray.length;
			T[] newArray = (T[]) Array.newInstance(newComponentType, n);
			System.arraycopy(baseArray, 0, newArray, 0, n);
			return newArray;
		}
	}
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static <T> T[] castArrayRuntimeTypeOrPass(Object[] baseArray, Class<T> newComponentType)
	{
		if (newComponentType.isAssignableFrom(baseArray.getClass().getComponentType()))
			return (T[]) baseArray;
		else
			return castArrayRuntimeTypeToNew(baseArray, newComponentType);
	}
	
	
	
	
	
	
	
	
	
	//Todo ones for all the primitives :>
	
	/* <<<
	python
	
	s="""
	
	public static _$$$$_prim$$_[] castArray_$$Prim$$_To_$$$$_Prim$$_(_$$prim$$_[] input)
	{
		int n = input.length;
		_$$$$_prim$$_[] output = new _$$$$_prim$$_[n];
		for (int i = 0; i < n; i++)
			output[i] = (_$$$$_prim$$_)input[i];
		return output;
	}
	""";
	
	
	
	
	def f(x):
		return primxp.primxp(prims=primxp.NumPrims, source=x)
	
	
	
	p(f(f(s)));
	
	 */
	
	
	public static byte[] castArrayByteToByte(byte[] input)
	{
		int n = input.length;
		byte[] output = new byte[n];
		for (int i = 0; i < n; i++)
			output[i] = (byte)input[i];
		return output;
	}
	
	
	public static byte[] castArrayCharToByte(char[] input)
	{
		int n = input.length;
		byte[] output = new byte[n];
		for (int i = 0; i < n; i++)
			output[i] = (byte)input[i];
		return output;
	}
	
	
	public static byte[] castArrayShortToByte(short[] input)
	{
		int n = input.length;
		byte[] output = new byte[n];
		for (int i = 0; i < n; i++)
			output[i] = (byte)input[i];
		return output;
	}
	
	
	public static byte[] castArrayFloatToByte(float[] input)
	{
		int n = input.length;
		byte[] output = new byte[n];
		for (int i = 0; i < n; i++)
			output[i] = (byte)input[i];
		return output;
	}
	
	
	public static byte[] castArrayIntToByte(int[] input)
	{
		int n = input.length;
		byte[] output = new byte[n];
		for (int i = 0; i < n; i++)
			output[i] = (byte)input[i];
		return output;
	}
	
	
	public static byte[] castArrayDoubleToByte(double[] input)
	{
		int n = input.length;
		byte[] output = new byte[n];
		for (int i = 0; i < n; i++)
			output[i] = (byte)input[i];
		return output;
	}
	
	
	public static byte[] castArrayLongToByte(long[] input)
	{
		int n = input.length;
		byte[] output = new byte[n];
		for (int i = 0; i < n; i++)
			output[i] = (byte)input[i];
		return output;
	}
	
	
	public static char[] castArrayByteToChar(byte[] input)
	{
		int n = input.length;
		char[] output = new char[n];
		for (int i = 0; i < n; i++)
			output[i] = (char)input[i];
		return output;
	}
	
	
	public static char[] castArrayCharToChar(char[] input)
	{
		int n = input.length;
		char[] output = new char[n];
		for (int i = 0; i < n; i++)
			output[i] = (char)input[i];
		return output;
	}
	
	
	public static char[] castArrayShortToChar(short[] input)
	{
		int n = input.length;
		char[] output = new char[n];
		for (int i = 0; i < n; i++)
			output[i] = (char)input[i];
		return output;
	}
	
	
	public static char[] castArrayFloatToChar(float[] input)
	{
		int n = input.length;
		char[] output = new char[n];
		for (int i = 0; i < n; i++)
			output[i] = (char)input[i];
		return output;
	}
	
	
	public static char[] castArrayIntToChar(int[] input)
	{
		int n = input.length;
		char[] output = new char[n];
		for (int i = 0; i < n; i++)
			output[i] = (char)input[i];
		return output;
	}
	
	
	public static char[] castArrayDoubleToChar(double[] input)
	{
		int n = input.length;
		char[] output = new char[n];
		for (int i = 0; i < n; i++)
			output[i] = (char)input[i];
		return output;
	}
	
	
	public static char[] castArrayLongToChar(long[] input)
	{
		int n = input.length;
		char[] output = new char[n];
		for (int i = 0; i < n; i++)
			output[i] = (char)input[i];
		return output;
	}
	
	
	public static short[] castArrayByteToShort(byte[] input)
	{
		int n = input.length;
		short[] output = new short[n];
		for (int i = 0; i < n; i++)
			output[i] = (short)input[i];
		return output;
	}
	
	
	public static short[] castArrayCharToShort(char[] input)
	{
		int n = input.length;
		short[] output = new short[n];
		for (int i = 0; i < n; i++)
			output[i] = (short)input[i];
		return output;
	}
	
	
	public static short[] castArrayShortToShort(short[] input)
	{
		int n = input.length;
		short[] output = new short[n];
		for (int i = 0; i < n; i++)
			output[i] = (short)input[i];
		return output;
	}
	
	
	public static short[] castArrayFloatToShort(float[] input)
	{
		int n = input.length;
		short[] output = new short[n];
		for (int i = 0; i < n; i++)
			output[i] = (short)input[i];
		return output;
	}
	
	
	public static short[] castArrayIntToShort(int[] input)
	{
		int n = input.length;
		short[] output = new short[n];
		for (int i = 0; i < n; i++)
			output[i] = (short)input[i];
		return output;
	}
	
	
	public static short[] castArrayDoubleToShort(double[] input)
	{
		int n = input.length;
		short[] output = new short[n];
		for (int i = 0; i < n; i++)
			output[i] = (short)input[i];
		return output;
	}
	
	
	public static short[] castArrayLongToShort(long[] input)
	{
		int n = input.length;
		short[] output = new short[n];
		for (int i = 0; i < n; i++)
			output[i] = (short)input[i];
		return output;
	}
	
	
	public static float[] castArrayByteToFloat(byte[] input)
	{
		int n = input.length;
		float[] output = new float[n];
		for (int i = 0; i < n; i++)
			output[i] = (float)input[i];
		return output;
	}
	
	
	public static float[] castArrayCharToFloat(char[] input)
	{
		int n = input.length;
		float[] output = new float[n];
		for (int i = 0; i < n; i++)
			output[i] = (float)input[i];
		return output;
	}
	
	
	public static float[] castArrayShortToFloat(short[] input)
	{
		int n = input.length;
		float[] output = new float[n];
		for (int i = 0; i < n; i++)
			output[i] = (float)input[i];
		return output;
	}
	
	
	public static float[] castArrayFloatToFloat(float[] input)
	{
		int n = input.length;
		float[] output = new float[n];
		for (int i = 0; i < n; i++)
			output[i] = (float)input[i];
		return output;
	}
	
	
	public static float[] castArrayIntToFloat(int[] input)
	{
		int n = input.length;
		float[] output = new float[n];
		for (int i = 0; i < n; i++)
			output[i] = (float)input[i];
		return output;
	}
	
	
	public static float[] castArrayDoubleToFloat(double[] input)
	{
		int n = input.length;
		float[] output = new float[n];
		for (int i = 0; i < n; i++)
			output[i] = (float)input[i];
		return output;
	}
	
	
	public static float[] castArrayLongToFloat(long[] input)
	{
		int n = input.length;
		float[] output = new float[n];
		for (int i = 0; i < n; i++)
			output[i] = (float)input[i];
		return output;
	}
	
	
	public static int[] castArrayByteToInt(byte[] input)
	{
		int n = input.length;
		int[] output = new int[n];
		for (int i = 0; i < n; i++)
			output[i] = (int)input[i];
		return output;
	}
	
	
	public static int[] castArrayCharToInt(char[] input)
	{
		int n = input.length;
		int[] output = new int[n];
		for (int i = 0; i < n; i++)
			output[i] = (int)input[i];
		return output;
	}
	
	
	public static int[] castArrayShortToInt(short[] input)
	{
		int n = input.length;
		int[] output = new int[n];
		for (int i = 0; i < n; i++)
			output[i] = (int)input[i];
		return output;
	}
	
	
	public static int[] castArrayFloatToInt(float[] input)
	{
		int n = input.length;
		int[] output = new int[n];
		for (int i = 0; i < n; i++)
			output[i] = (int)input[i];
		return output;
	}
	
	
	public static int[] castArrayIntToInt(int[] input)
	{
		int n = input.length;
		int[] output = new int[n];
		for (int i = 0; i < n; i++)
			output[i] = (int)input[i];
		return output;
	}
	
	
	public static int[] castArrayDoubleToInt(double[] input)
	{
		int n = input.length;
		int[] output = new int[n];
		for (int i = 0; i < n; i++)
			output[i] = (int)input[i];
		return output;
	}
	
	
	public static int[] castArrayLongToInt(long[] input)
	{
		int n = input.length;
		int[] output = new int[n];
		for (int i = 0; i < n; i++)
			output[i] = (int)input[i];
		return output;
	}
	
	
	public static double[] castArrayByteToDouble(byte[] input)
	{
		int n = input.length;
		double[] output = new double[n];
		for (int i = 0; i < n; i++)
			output[i] = (double)input[i];
		return output;
	}
	
	
	public static double[] castArrayCharToDouble(char[] input)
	{
		int n = input.length;
		double[] output = new double[n];
		for (int i = 0; i < n; i++)
			output[i] = (double)input[i];
		return output;
	}
	
	
	public static double[] castArrayShortToDouble(short[] input)
	{
		int n = input.length;
		double[] output = new double[n];
		for (int i = 0; i < n; i++)
			output[i] = (double)input[i];
		return output;
	}
	
	
	public static double[] castArrayFloatToDouble(float[] input)
	{
		int n = input.length;
		double[] output = new double[n];
		for (int i = 0; i < n; i++)
			output[i] = (double)input[i];
		return output;
	}
	
	
	public static double[] castArrayIntToDouble(int[] input)
	{
		int n = input.length;
		double[] output = new double[n];
		for (int i = 0; i < n; i++)
			output[i] = (double)input[i];
		return output;
	}
	
	
	public static double[] castArrayDoubleToDouble(double[] input)
	{
		int n = input.length;
		double[] output = new double[n];
		for (int i = 0; i < n; i++)
			output[i] = (double)input[i];
		return output;
	}
	
	
	public static double[] castArrayLongToDouble(long[] input)
	{
		int n = input.length;
		double[] output = new double[n];
		for (int i = 0; i < n; i++)
			output[i] = (double)input[i];
		return output;
	}
	
	
	public static long[] castArrayByteToLong(byte[] input)
	{
		int n = input.length;
		long[] output = new long[n];
		for (int i = 0; i < n; i++)
			output[i] = (long)input[i];
		return output;
	}
	
	
	public static long[] castArrayCharToLong(char[] input)
	{
		int n = input.length;
		long[] output = new long[n];
		for (int i = 0; i < n; i++)
			output[i] = (long)input[i];
		return output;
	}
	
	
	public static long[] castArrayShortToLong(short[] input)
	{
		int n = input.length;
		long[] output = new long[n];
		for (int i = 0; i < n; i++)
			output[i] = (long)input[i];
		return output;
	}
	
	
	public static long[] castArrayFloatToLong(float[] input)
	{
		int n = input.length;
		long[] output = new long[n];
		for (int i = 0; i < n; i++)
			output[i] = (long)input[i];
		return output;
	}
	
	
	public static long[] castArrayIntToLong(int[] input)
	{
		int n = input.length;
		long[] output = new long[n];
		for (int i = 0; i < n; i++)
			output[i] = (long)input[i];
		return output;
	}
	
	
	public static long[] castArrayDoubleToLong(double[] input)
	{
		int n = input.length;
		long[] output = new long[n];
		for (int i = 0; i < n; i++)
			output[i] = (long)input[i];
		return output;
	}
	
	
	public static long[] castArrayLongToLong(long[] input)
	{
		int n = input.length;
		long[] output = new long[n];
		for (int i = 0; i < n; i++)
			output[i] = (long)input[i];
		return output;
	}
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int compareBigEndianLengthsLast(Object[] a, Object[] b, Comparator comparator)
	{
		return compareBigEndianLengthsLast(a, 0, a.length, b, 0, b.length, comparator);
	}
	
	public static int compareBigEndianLengthsLast(Object[] arrayA, int offsetA, int lengthA, Object[] arrayB, int offsetB, int lengthB, Comparator comparator)
	{
		int min = least(lengthA, lengthB);
		
		for (int i = 0; i < min; i++)
		{
			Object elementA = arrayA[offsetA+i];
			Object elementB = arrayB[offsetB+i];
			
			int r = comparator.compare(elementA, elementB);
			if (r != 0) return r;
		}
		
		return cmp(lengthA, lengthB);
	}
	
	
	
	
	public static int compareBigEndianLengthsFirst(Object[] a, Object[] b, Comparator comparator)
	{
		return compareBigEndianLengthsFirst(a, 0, a.length, b, 0, b.length, comparator);
	}
	
	public static int compareBigEndianLengthsFirst(Object[] arrayA, int offsetA, int lengthA, Object[] arrayB, int offsetB, int lengthB, Comparator comparator)
	{
		if (lengthA != lengthB)
			return cmp(lengthA, lengthB);
		
		
		int length = lengthA;  //= lengthB
		
		for (int i = 0; i < length; i++)
		{
			Object elementA = arrayA[offsetA+i];
			Object elementB = arrayB[offsetB+i];
			
			int r = comparator.compare(elementA, elementB);
			if (r != 0) return r;
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	public static int compareLittleEndianLengthsLast(Object[] a, Object[] b, Comparator comparator)
	{
		return compareLittleEndianLengthsLast(a, 0, a.length, b, 0, b.length, comparator);
	}
	
	public static int compareLittleEndianLengthsLast(Object[] arrayA, int offsetA, int lengthA, Object[] arrayB, int offsetB, int lengthB, Comparator comparator)
	{
		int kA = offsetA+lengthA-1;
		int kB = offsetB+lengthB-1;
		int min = least(lengthA, lengthB);
		
		for (int i = 0; i < min; i++)
		{
			Object elementA = arrayA[kA - i];  //= arrayA[offsetA+(lengthA-i-1)];
			Object elementB = arrayB[kB - i];  //= arrayB[offsetB+(lengthB-i-1)];
			
			int r = comparator.compare(elementA, elementB);
			if (r != 0) return r;
		}
		
		return cmp(lengthA, lengthB);
	}
	
	
	
	
	public static int compareLittleEndianLengthsFirst(Object[] a, Object[] b, Comparator comparator)
	{
		return compareLittleEndianLengthsFirst(a, 0, a.length, b, 0, b.length, comparator);
	}
	
	public static int compareLittleEndianLengthsFirst(Object[] arrayA, int offsetA, int lengthA, Object[] arrayB, int offsetB, int lengthB, Comparator comparator)
	{
		if (lengthA != lengthB)
			return cmp(lengthA, lengthB);
		
		int kA = offsetA+lengthA-1;
		int kB = offsetB+lengthB-1;
		int length = lengthA;  //= lengthB
		
		for (int i = 0; i < length; i++)
		{
			Object elementA = arrayA[kA - i];  //= arrayA[offsetA+(lengthA-i-1)];
			Object elementB = arrayB[kB - i];  //= arrayB[offsetB+(lengthB-i-1)];
			
			int r = comparator.compare(elementA, elementB);
			if (r != 0) return r;
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	primxp
	
	
	
	
	public static int compareBigEndianLengthsLast(_$$prim$$_[] a, _$$prim$$_[] b)
	{
		return compareBigEndianLengthsLast(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareBigEndianLengthsLast(_$$prim$$_[] arrayA, int offsetA, int lengthA, _$$prim$$_[] arrayB, int offsetB, int lengthB)
	{
		int min = least(lengthA, lengthB);
		
		for (int i = 0; i < min; i++)
		{
			_$$prim$$_ elementA = arrayA[offsetA+i];
			_$$prim$$_ elementB = arrayB[offsetB+i];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return cmp(lengthA, lengthB);
	}
	
	
	
	
	public static int compareBigEndianLengthsFirst(_$$prim$$_[] a, _$$prim$$_[] b)
	{
		return compareBigEndianLengthsFirst(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareBigEndianLengthsFirst(_$$prim$$_[] arrayA, int offsetA, int lengthA, _$$prim$$_[] arrayB, int offsetB, int lengthB)
	{
		if (lengthA != lengthB)
			return cmp(lengthA, lengthB);
		
		
		int length = lengthA;  //= lengthB
		
		for (int i = 0; i < length; i++)
		{
			_$$prim$$_ elementA = arrayA[offsetA+i];
			_$$prim$$_ elementB = arrayB[offsetB+i];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	public static int compareLittleEndianLengthsLast(_$$prim$$_[] a, _$$prim$$_[] b)
	{
		return compareLittleEndianLengthsLast(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareLittleEndianLengthsLast(_$$prim$$_[] arrayA, int offsetA, int lengthA, _$$prim$$_[] arrayB, int offsetB, int lengthB)
	{
		int kA = offsetA+lengthA-1;
		int kB = offsetB+lengthB-1;
		int min = least(lengthA, lengthB);
		
		for (int i = 0; i < min; i++)
		{
			_$$prim$$_ elementA = arrayA[kA - i];  //= arrayA[offsetA+(lengthA-i-1)];
			_$$prim$$_ elementB = arrayB[kB - i];  //= arrayB[offsetB+(lengthB-i-1)];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return cmp(lengthA, lengthB);
	}
	
	
	
	
	public static int compareLittleEndianLengthsFirst(_$$prim$$_[] a, _$$prim$$_[] b)
	{
		return compareLittleEndianLengthsFirst(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareLittleEndianLengthsFirst(_$$prim$$_[] arrayA, int offsetA, int lengthA, _$$prim$$_[] arrayB, int offsetB, int lengthB)
	{
		if (lengthA != lengthB)
			return cmp(lengthA, lengthB);
		
		int kA = offsetA+lengthA-1;
		int kB = offsetB+lengthB-1;
		int length = lengthA;  //= lengthB
		
		for (int i = 0; i < length; i++)
		{
			_$$prim$$_ elementA = arrayA[kA - i];  //= arrayA[offsetA+(lengthA-i-1)];
			_$$prim$$_ elementB = arrayB[kB - i];  //= arrayB[offsetB+(lengthB-i-1)];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	 */
	
	
	
	
	public static int compareBigEndianLengthsLast(boolean[] a, boolean[] b)
	{
		return compareBigEndianLengthsLast(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareBigEndianLengthsLast(boolean[] arrayA, int offsetA, int lengthA, boolean[] arrayB, int offsetB, int lengthB)
	{
		int min = least(lengthA, lengthB);
		
		for (int i = 0; i < min; i++)
		{
			boolean elementA = arrayA[offsetA+i];
			boolean elementB = arrayB[offsetB+i];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return cmp(lengthA, lengthB);
	}
	
	
	
	
	public static int compareBigEndianLengthsFirst(boolean[] a, boolean[] b)
	{
		return compareBigEndianLengthsFirst(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareBigEndianLengthsFirst(boolean[] arrayA, int offsetA, int lengthA, boolean[] arrayB, int offsetB, int lengthB)
	{
		if (lengthA != lengthB)
			return cmp(lengthA, lengthB);
		
		
		int length = lengthA;  //= lengthB
		
		for (int i = 0; i < length; i++)
		{
			boolean elementA = arrayA[offsetA+i];
			boolean elementB = arrayB[offsetB+i];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	public static int compareLittleEndianLengthsLast(boolean[] a, boolean[] b)
	{
		return compareLittleEndianLengthsLast(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareLittleEndianLengthsLast(boolean[] arrayA, int offsetA, int lengthA, boolean[] arrayB, int offsetB, int lengthB)
	{
		int kA = offsetA+lengthA-1;
		int kB = offsetB+lengthB-1;
		int min = least(lengthA, lengthB);
		
		for (int i = 0; i < min; i++)
		{
			boolean elementA = arrayA[kA - i];  //= arrayA[offsetA+(lengthA-i-1)];
			boolean elementB = arrayB[kB - i];  //= arrayB[offsetB+(lengthB-i-1)];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return cmp(lengthA, lengthB);
	}
	
	
	
	
	public static int compareLittleEndianLengthsFirst(boolean[] a, boolean[] b)
	{
		return compareLittleEndianLengthsFirst(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareLittleEndianLengthsFirst(boolean[] arrayA, int offsetA, int lengthA, boolean[] arrayB, int offsetB, int lengthB)
	{
		if (lengthA != lengthB)
			return cmp(lengthA, lengthB);
		
		int kA = offsetA+lengthA-1;
		int kB = offsetB+lengthB-1;
		int length = lengthA;  //= lengthB
		
		for (int i = 0; i < length; i++)
		{
			boolean elementA = arrayA[kA - i];  //= arrayA[offsetA+(lengthA-i-1)];
			boolean elementB = arrayB[kB - i];  //= arrayB[offsetB+(lengthB-i-1)];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static int compareBigEndianLengthsLast(byte[] a, byte[] b)
	{
		return compareBigEndianLengthsLast(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareBigEndianLengthsLast(byte[] arrayA, int offsetA, int lengthA, byte[] arrayB, int offsetB, int lengthB)
	{
		int min = least(lengthA, lengthB);
		
		for (int i = 0; i < min; i++)
		{
			byte elementA = arrayA[offsetA+i];
			byte elementB = arrayB[offsetB+i];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return cmp(lengthA, lengthB);
	}
	
	
	
	
	public static int compareBigEndianLengthsFirst(byte[] a, byte[] b)
	{
		return compareBigEndianLengthsFirst(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareBigEndianLengthsFirst(byte[] arrayA, int offsetA, int lengthA, byte[] arrayB, int offsetB, int lengthB)
	{
		if (lengthA != lengthB)
			return cmp(lengthA, lengthB);
		
		
		int length = lengthA;  //= lengthB
		
		for (int i = 0; i < length; i++)
		{
			byte elementA = arrayA[offsetA+i];
			byte elementB = arrayB[offsetB+i];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	public static int compareLittleEndianLengthsLast(byte[] a, byte[] b)
	{
		return compareLittleEndianLengthsLast(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareLittleEndianLengthsLast(byte[] arrayA, int offsetA, int lengthA, byte[] arrayB, int offsetB, int lengthB)
	{
		int kA = offsetA+lengthA-1;
		int kB = offsetB+lengthB-1;
		int min = least(lengthA, lengthB);
		
		for (int i = 0; i < min; i++)
		{
			byte elementA = arrayA[kA - i];  //= arrayA[offsetA+(lengthA-i-1)];
			byte elementB = arrayB[kB - i];  //= arrayB[offsetB+(lengthB-i-1)];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return cmp(lengthA, lengthB);
	}
	
	
	
	
	public static int compareLittleEndianLengthsFirst(byte[] a, byte[] b)
	{
		return compareLittleEndianLengthsFirst(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareLittleEndianLengthsFirst(byte[] arrayA, int offsetA, int lengthA, byte[] arrayB, int offsetB, int lengthB)
	{
		if (lengthA != lengthB)
			return cmp(lengthA, lengthB);
		
		int kA = offsetA+lengthA-1;
		int kB = offsetB+lengthB-1;
		int length = lengthA;  //= lengthB
		
		for (int i = 0; i < length; i++)
		{
			byte elementA = arrayA[kA - i];  //= arrayA[offsetA+(lengthA-i-1)];
			byte elementB = arrayB[kB - i];  //= arrayB[offsetB+(lengthB-i-1)];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static int compareBigEndianLengthsLast(char[] a, char[] b)
	{
		return compareBigEndianLengthsLast(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareBigEndianLengthsLast(char[] arrayA, int offsetA, int lengthA, char[] arrayB, int offsetB, int lengthB)
	{
		int min = least(lengthA, lengthB);
		
		for (int i = 0; i < min; i++)
		{
			char elementA = arrayA[offsetA+i];
			char elementB = arrayB[offsetB+i];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return cmp(lengthA, lengthB);
	}
	
	
	
	
	public static int compareBigEndianLengthsFirst(char[] a, char[] b)
	{
		return compareBigEndianLengthsFirst(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareBigEndianLengthsFirst(char[] arrayA, int offsetA, int lengthA, char[] arrayB, int offsetB, int lengthB)
	{
		if (lengthA != lengthB)
			return cmp(lengthA, lengthB);
		
		
		int length = lengthA;  //= lengthB
		
		for (int i = 0; i < length; i++)
		{
			char elementA = arrayA[offsetA+i];
			char elementB = arrayB[offsetB+i];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	public static int compareLittleEndianLengthsLast(char[] a, char[] b)
	{
		return compareLittleEndianLengthsLast(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareLittleEndianLengthsLast(char[] arrayA, int offsetA, int lengthA, char[] arrayB, int offsetB, int lengthB)
	{
		int kA = offsetA+lengthA-1;
		int kB = offsetB+lengthB-1;
		int min = least(lengthA, lengthB);
		
		for (int i = 0; i < min; i++)
		{
			char elementA = arrayA[kA - i];  //= arrayA[offsetA+(lengthA-i-1)];
			char elementB = arrayB[kB - i];  //= arrayB[offsetB+(lengthB-i-1)];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return cmp(lengthA, lengthB);
	}
	
	
	
	
	public static int compareLittleEndianLengthsFirst(char[] a, char[] b)
	{
		return compareLittleEndianLengthsFirst(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareLittleEndianLengthsFirst(char[] arrayA, int offsetA, int lengthA, char[] arrayB, int offsetB, int lengthB)
	{
		if (lengthA != lengthB)
			return cmp(lengthA, lengthB);
		
		int kA = offsetA+lengthA-1;
		int kB = offsetB+lengthB-1;
		int length = lengthA;  //= lengthB
		
		for (int i = 0; i < length; i++)
		{
			char elementA = arrayA[kA - i];  //= arrayA[offsetA+(lengthA-i-1)];
			char elementB = arrayB[kB - i];  //= arrayB[offsetB+(lengthB-i-1)];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static int compareBigEndianLengthsLast(short[] a, short[] b)
	{
		return compareBigEndianLengthsLast(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareBigEndianLengthsLast(short[] arrayA, int offsetA, int lengthA, short[] arrayB, int offsetB, int lengthB)
	{
		int min = least(lengthA, lengthB);
		
		for (int i = 0; i < min; i++)
		{
			short elementA = arrayA[offsetA+i];
			short elementB = arrayB[offsetB+i];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return cmp(lengthA, lengthB);
	}
	
	
	
	
	public static int compareBigEndianLengthsFirst(short[] a, short[] b)
	{
		return compareBigEndianLengthsFirst(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareBigEndianLengthsFirst(short[] arrayA, int offsetA, int lengthA, short[] arrayB, int offsetB, int lengthB)
	{
		if (lengthA != lengthB)
			return cmp(lengthA, lengthB);
		
		
		int length = lengthA;  //= lengthB
		
		for (int i = 0; i < length; i++)
		{
			short elementA = arrayA[offsetA+i];
			short elementB = arrayB[offsetB+i];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	public static int compareLittleEndianLengthsLast(short[] a, short[] b)
	{
		return compareLittleEndianLengthsLast(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareLittleEndianLengthsLast(short[] arrayA, int offsetA, int lengthA, short[] arrayB, int offsetB, int lengthB)
	{
		int kA = offsetA+lengthA-1;
		int kB = offsetB+lengthB-1;
		int min = least(lengthA, lengthB);
		
		for (int i = 0; i < min; i++)
		{
			short elementA = arrayA[kA - i];  //= arrayA[offsetA+(lengthA-i-1)];
			short elementB = arrayB[kB - i];  //= arrayB[offsetB+(lengthB-i-1)];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return cmp(lengthA, lengthB);
	}
	
	
	
	
	public static int compareLittleEndianLengthsFirst(short[] a, short[] b)
	{
		return compareLittleEndianLengthsFirst(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareLittleEndianLengthsFirst(short[] arrayA, int offsetA, int lengthA, short[] arrayB, int offsetB, int lengthB)
	{
		if (lengthA != lengthB)
			return cmp(lengthA, lengthB);
		
		int kA = offsetA+lengthA-1;
		int kB = offsetB+lengthB-1;
		int length = lengthA;  //= lengthB
		
		for (int i = 0; i < length; i++)
		{
			short elementA = arrayA[kA - i];  //= arrayA[offsetA+(lengthA-i-1)];
			short elementB = arrayB[kB - i];  //= arrayB[offsetB+(lengthB-i-1)];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static int compareBigEndianLengthsLast(float[] a, float[] b)
	{
		return compareBigEndianLengthsLast(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareBigEndianLengthsLast(float[] arrayA, int offsetA, int lengthA, float[] arrayB, int offsetB, int lengthB)
	{
		int min = least(lengthA, lengthB);
		
		for (int i = 0; i < min; i++)
		{
			float elementA = arrayA[offsetA+i];
			float elementB = arrayB[offsetB+i];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return cmp(lengthA, lengthB);
	}
	
	
	
	
	public static int compareBigEndianLengthsFirst(float[] a, float[] b)
	{
		return compareBigEndianLengthsFirst(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareBigEndianLengthsFirst(float[] arrayA, int offsetA, int lengthA, float[] arrayB, int offsetB, int lengthB)
	{
		if (lengthA != lengthB)
			return cmp(lengthA, lengthB);
		
		
		int length = lengthA;  //= lengthB
		
		for (int i = 0; i < length; i++)
		{
			float elementA = arrayA[offsetA+i];
			float elementB = arrayB[offsetB+i];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	public static int compareLittleEndianLengthsLast(float[] a, float[] b)
	{
		return compareLittleEndianLengthsLast(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareLittleEndianLengthsLast(float[] arrayA, int offsetA, int lengthA, float[] arrayB, int offsetB, int lengthB)
	{
		int kA = offsetA+lengthA-1;
		int kB = offsetB+lengthB-1;
		int min = least(lengthA, lengthB);
		
		for (int i = 0; i < min; i++)
		{
			float elementA = arrayA[kA - i];  //= arrayA[offsetA+(lengthA-i-1)];
			float elementB = arrayB[kB - i];  //= arrayB[offsetB+(lengthB-i-1)];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return cmp(lengthA, lengthB);
	}
	
	
	
	
	public static int compareLittleEndianLengthsFirst(float[] a, float[] b)
	{
		return compareLittleEndianLengthsFirst(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareLittleEndianLengthsFirst(float[] arrayA, int offsetA, int lengthA, float[] arrayB, int offsetB, int lengthB)
	{
		if (lengthA != lengthB)
			return cmp(lengthA, lengthB);
		
		int kA = offsetA+lengthA-1;
		int kB = offsetB+lengthB-1;
		int length = lengthA;  //= lengthB
		
		for (int i = 0; i < length; i++)
		{
			float elementA = arrayA[kA - i];  //= arrayA[offsetA+(lengthA-i-1)];
			float elementB = arrayB[kB - i];  //= arrayB[offsetB+(lengthB-i-1)];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static int compareBigEndianLengthsLast(int[] a, int[] b)
	{
		return compareBigEndianLengthsLast(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareBigEndianLengthsLast(int[] arrayA, int offsetA, int lengthA, int[] arrayB, int offsetB, int lengthB)
	{
		int min = least(lengthA, lengthB);
		
		for (int i = 0; i < min; i++)
		{
			int elementA = arrayA[offsetA+i];
			int elementB = arrayB[offsetB+i];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return cmp(lengthA, lengthB);
	}
	
	
	
	
	public static int compareBigEndianLengthsFirst(int[] a, int[] b)
	{
		return compareBigEndianLengthsFirst(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareBigEndianLengthsFirst(int[] arrayA, int offsetA, int lengthA, int[] arrayB, int offsetB, int lengthB)
	{
		if (lengthA != lengthB)
			return cmp(lengthA, lengthB);
		
		
		int length = lengthA;  //= lengthB
		
		for (int i = 0; i < length; i++)
		{
			int elementA = arrayA[offsetA+i];
			int elementB = arrayB[offsetB+i];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	public static int compareLittleEndianLengthsLast(int[] a, int[] b)
	{
		return compareLittleEndianLengthsLast(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareLittleEndianLengthsLast(int[] arrayA, int offsetA, int lengthA, int[] arrayB, int offsetB, int lengthB)
	{
		int kA = offsetA+lengthA-1;
		int kB = offsetB+lengthB-1;
		int min = least(lengthA, lengthB);
		
		for (int i = 0; i < min; i++)
		{
			int elementA = arrayA[kA - i];  //= arrayA[offsetA+(lengthA-i-1)];
			int elementB = arrayB[kB - i];  //= arrayB[offsetB+(lengthB-i-1)];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return cmp(lengthA, lengthB);
	}
	
	
	
	
	public static int compareLittleEndianLengthsFirst(int[] a, int[] b)
	{
		return compareLittleEndianLengthsFirst(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareLittleEndianLengthsFirst(int[] arrayA, int offsetA, int lengthA, int[] arrayB, int offsetB, int lengthB)
	{
		if (lengthA != lengthB)
			return cmp(lengthA, lengthB);
		
		int kA = offsetA+lengthA-1;
		int kB = offsetB+lengthB-1;
		int length = lengthA;  //= lengthB
		
		for (int i = 0; i < length; i++)
		{
			int elementA = arrayA[kA - i];  //= arrayA[offsetA+(lengthA-i-1)];
			int elementB = arrayB[kB - i];  //= arrayB[offsetB+(lengthB-i-1)];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static int compareBigEndianLengthsLast(double[] a, double[] b)
	{
		return compareBigEndianLengthsLast(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareBigEndianLengthsLast(double[] arrayA, int offsetA, int lengthA, double[] arrayB, int offsetB, int lengthB)
	{
		int min = least(lengthA, lengthB);
		
		for (int i = 0; i < min; i++)
		{
			double elementA = arrayA[offsetA+i];
			double elementB = arrayB[offsetB+i];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return cmp(lengthA, lengthB);
	}
	
	
	
	
	public static int compareBigEndianLengthsFirst(double[] a, double[] b)
	{
		return compareBigEndianLengthsFirst(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareBigEndianLengthsFirst(double[] arrayA, int offsetA, int lengthA, double[] arrayB, int offsetB, int lengthB)
	{
		if (lengthA != lengthB)
			return cmp(lengthA, lengthB);
		
		
		int length = lengthA;  //= lengthB
		
		for (int i = 0; i < length; i++)
		{
			double elementA = arrayA[offsetA+i];
			double elementB = arrayB[offsetB+i];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	public static int compareLittleEndianLengthsLast(double[] a, double[] b)
	{
		return compareLittleEndianLengthsLast(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareLittleEndianLengthsLast(double[] arrayA, int offsetA, int lengthA, double[] arrayB, int offsetB, int lengthB)
	{
		int kA = offsetA+lengthA-1;
		int kB = offsetB+lengthB-1;
		int min = least(lengthA, lengthB);
		
		for (int i = 0; i < min; i++)
		{
			double elementA = arrayA[kA - i];  //= arrayA[offsetA+(lengthA-i-1)];
			double elementB = arrayB[kB - i];  //= arrayB[offsetB+(lengthB-i-1)];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return cmp(lengthA, lengthB);
	}
	
	
	
	
	public static int compareLittleEndianLengthsFirst(double[] a, double[] b)
	{
		return compareLittleEndianLengthsFirst(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareLittleEndianLengthsFirst(double[] arrayA, int offsetA, int lengthA, double[] arrayB, int offsetB, int lengthB)
	{
		if (lengthA != lengthB)
			return cmp(lengthA, lengthB);
		
		int kA = offsetA+lengthA-1;
		int kB = offsetB+lengthB-1;
		int length = lengthA;  //= lengthB
		
		for (int i = 0; i < length; i++)
		{
			double elementA = arrayA[kA - i];  //= arrayA[offsetA+(lengthA-i-1)];
			double elementB = arrayB[kB - i];  //= arrayB[offsetB+(lengthB-i-1)];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static int compareBigEndianLengthsLast(long[] a, long[] b)
	{
		return compareBigEndianLengthsLast(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareBigEndianLengthsLast(long[] arrayA, int offsetA, int lengthA, long[] arrayB, int offsetB, int lengthB)
	{
		int min = least(lengthA, lengthB);
		
		for (int i = 0; i < min; i++)
		{
			long elementA = arrayA[offsetA+i];
			long elementB = arrayB[offsetB+i];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return cmp(lengthA, lengthB);
	}
	
	
	
	
	public static int compareBigEndianLengthsFirst(long[] a, long[] b)
	{
		return compareBigEndianLengthsFirst(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareBigEndianLengthsFirst(long[] arrayA, int offsetA, int lengthA, long[] arrayB, int offsetB, int lengthB)
	{
		if (lengthA != lengthB)
			return cmp(lengthA, lengthB);
		
		
		int length = lengthA;  //= lengthB
		
		for (int i = 0; i < length; i++)
		{
			long elementA = arrayA[offsetA+i];
			long elementB = arrayB[offsetB+i];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	public static int compareLittleEndianLengthsLast(long[] a, long[] b)
	{
		return compareLittleEndianLengthsLast(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareLittleEndianLengthsLast(long[] arrayA, int offsetA, int lengthA, long[] arrayB, int offsetB, int lengthB)
	{
		int kA = offsetA+lengthA-1;
		int kB = offsetB+lengthB-1;
		int min = least(lengthA, lengthB);
		
		for (int i = 0; i < min; i++)
		{
			long elementA = arrayA[kA - i];  //= arrayA[offsetA+(lengthA-i-1)];
			long elementB = arrayB[kB - i];  //= arrayB[offsetB+(lengthB-i-1)];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return cmp(lengthA, lengthB);
	}
	
	
	
	
	public static int compareLittleEndianLengthsFirst(long[] a, long[] b)
	{
		return compareLittleEndianLengthsFirst(a, 0, a.length, b, 0, b.length);
	}
	
	public static int compareLittleEndianLengthsFirst(long[] arrayA, int offsetA, int lengthA, long[] arrayB, int offsetB, int lengthB)
	{
		if (lengthA != lengthB)
			return cmp(lengthA, lengthB);
		
		int kA = offsetA+lengthA-1;
		int kB = offsetB+lengthB-1;
		int length = lengthA;  //= lengthB
		
		for (int i = 0; i < length; i++)
		{
			long elementA = arrayA[kA - i];  //= arrayA[offsetA+(lengthA-i-1)];
			long elementB = arrayB[kB - i];  //= arrayB[offsetB+(lengthB-i-1)];
			
			if (elementA != elementB)
				return cmp(elementA, elementB);
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	//>>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static boolean equalsDeep(Object[] a, int aOffset, Object[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (!eq(a[aOffset + i], b[bOffset + i]))
				return false;
		return true;
	}
	
	public static boolean equals(Object[] a, int aOffset, Object[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (a[aOffset + i] != b[bOffset + i])
				return false;
		return true;
	}
	
	
	
	
	public static <T> boolean equalsSliceReference(Slice<T[]> a, int aOffset, Slice<T[]> b, int bOffset, int length)
	{
		return equals(a.getUnderlying(), a.getOffset()+aOffset, b.getUnderlying(), b.getOffset()+bOffset, length);
	}
	
	public static <T> boolean equalsSliceReference(Slice<T[]> a, Slice<T[]> b)
	{
		int length = a.getLength();
		if (length != b.getLength())
			return false;
		
		return equalsSliceReference(a, 0, b, 0, length);
	}
	
	
	
	
	
	public static <T> T getReference(Slice<T[]> source, int index)
	{
		return source.getUnderlying()[source.getOffset() + index];
	}
	
	public static <T> void setReference(Slice<T[]> source, int index, T value)
	{
		source.getUnderlying()[source.getOffset() + index] = value;
	}
	
	
	public static <T> boolean isReferenceSliceAllSame(Slice<T[]> source, T value)
	{
		int n = source.getLength();
		for (int i = 0; i < n; i++)
		{
			if (getReference(source, i) != value)
				return false;
		}
		return true;
	}
	
	public static <T> boolean isReferenceArrayAllSame(T[] source, int offset, int length, T value)
	{
		int e = offset + length;
		for (int i = offset; i < e; i++)
		{
			if (source[i] != value)
				return false;
		}
		return true;
	}
	
	public static <T> boolean isReferenceArrayAllSame(T[] source, T value)
	{
		return isReferenceArrayAllSame(source, 0, source.length, value);
	}
	
	
	public static <T> Object[] sliceToNewObjectArrayOP(@ReadonlyValue Slice<T[]> slice)
	{
		int n = slice.getLength();
		Object[] a = new Object[n];
		System.arraycopy(slice.getUnderlying(), slice.getOffset(), a, 0, n);
		return a;
	}
	
	public static <T> Object[] sliceToObjectArrayOPC(@ReadonlyValue Slice<T[]> slice)
	{
		int o = slice.getOffset();
		int n = slice.getLength();
		Object[] u = slice.getUnderlying();
		
		if (o == 0 && n == u.length)
		{
			return u;
		}
		else
		{
			Object[] a = new Object[n];
			System.arraycopy(u, o, a, 0, n);
			return a;
		}
	}
	
	public static <A> Slice<A> wholeArraySlice(A array)
	{
		return new Slice<>(array, 0, Array.getLength(array));
	}
	
	
	public static <T> Slice<T[]> wholeArraySliceReference(T[] array)
	{
		return new Slice<>(array, 0, array.length);
	}
	
	
	public static Slice<Object[]> newArrayToSliceObject(int length)
	{
		return new Slice<>(new Object[length], 0, length);
	}
	
	public static <T> Slice<T[]> cloneArrayToSliceReference(T[] array)
	{
		return new Slice<>(array.clone(), 0, array.length);
	}
	
	public static Slice<Object[]> cloneSliceObject(Slice<Object[]> array)
	{
		return wholeArraySliceReference(sliceToNewObjectArrayOP(array));
	}
	
	
	public static Slice<Object[]> trimSliceOPCObject(Slice<Object[]> arraySlice)
	{
		if (isArraySliceFullReference(arraySlice))
			return arraySlice;
		else
			return cloneSliceObject(arraySlice);
	}
	
	public static <T> boolean isArraySliceFullReference(Slice<T[]> arraySlice)
	{
		return arraySlice.getOffset() == 0 && arraySlice.getLength() == arraySlice.getUnderlying().length;
	}
	
	
	
	public static boolean isArraySliceFull(Slice arraySlice)
	{
		return arraySlice.getOffset() == 0 && arraySlice.getLength() == Array.getLength(arraySlice.getUnderlying());
	}
	
	
	
	
	
	
	
	/* <<<
	primxp
	
	
	
	public static boolean equals(_$$prim$$_[] a, int aOffset, _$$prim$$_[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (!eqSane(a[aOffset + i], b[bOffset + i]))
				return false;
		return true;
	}
	
	public static boolean equalsSlice_$$Prim$$_(Slice<_$$prim$$_[]> a, Slice<_$$prim$$_[]> b)
	{
		int length = a.getLength();
		if (length != b.getLength())
			return false;
		
		return equals(a.getUnderlying(), a.getOffset(), b.getUnderlying(), b.getOffset(), length);
	}
	
	
	
	
	
	public static _$$prim$$_ get_$$Prim$$_(Slice<_$$prim$$_[]> slice, int index)
	{
		return slice.getUnderlying()[slice.getOffset() + index];
	}
	
	public static void set_$$Prim$$_(Slice<_$$prim$$_[]> slice, int index, _$$prim$$_ value)
	{
		slice.getUnderlying()[slice.getOffset() + index] = value;
	}
	
	
	public static boolean is_$$Prim$$_SliceAllSame(Slice<_$$prim$$_[]> source, _$$prim$$_ value)
	{
		int n = source.getLength();
		for (int i = 0; i < n; i++)
		{
			if (get_$$Prim$$_(source, i) != value)
				return false;
		}
		return true;
	}
	
	public static boolean is_$$Prim$$_ArrayAllSame(_$$prim$$_[] source, int offset, int length, _$$prim$$_ value)
	{
		int e = offset + length;
		for (int i = offset; i < e; i++)
		{
			if (source[i] != value)
				return false;
		}
		return true;
	}
	
	public static boolean is_$$Prim$$_ArrayAllSame(_$$prim$$_[] source, _$$prim$$_ value)
	{
		return is_$$Prim$$_ArrayAllSame(source, 0, source.length, value);
	}
	
	
	public static _$$prim$$_[] sliceToNew_$$Prim$$_ArrayOP(@ReadonlyValue Slice<_$$prim$$_[]> slice)
	{
		int n = slice.getLength();
		_$$prim$$_[] a = new _$$prim$$_[n];
		System.arraycopy(slice.getUnderlying(), slice.getOffset(), a, 0, n);
		return a;
	}
	
	public static _$$prim$$_[] sliceTo_$$Prim$$_ArrayOPC(@ReadonlyValue Slice<_$$prim$$_[]> slice)
	{
		int o = slice.getOffset();
		int n = slice.getLength();
		_$$prim$$_[] u = slice.getUnderlying();
		
		if (o == 0 && n == u.length)
		{
			return u;
		}
		else
		{
			_$$prim$$_[] a = new _$$prim$$_[n];
			System.arraycopy(u, o, a, 0, n);
			return a;
		}
	}
	
	public static Slice<_$$prim$$_[]> wholeArraySlice_$$Prim$$_(_$$prim$$_[] array)
	{
		return new Slice<>(array, 0, array.length);
	}
	
	public static Slice<_$$prim$$_[]> newArraySlice_$$Prim$$_(_$$prim$$_... array)
	{
		return new Slice<>(array, 0, array.length);
	}
	
	
	
	public static Slice<_$$prim$$_[]> newArrayToSlice_$$Prim$$_(int length)
	{
		return new Slice<>(new _$$prim$$_[length], 0, length);
	}
	
	public static Slice<_$$prim$$_[]> cloneArrayToSlice_$$Prim$$_(_$$prim$$_[] array)
	{
		return new Slice<>(array.clone(), 0, array.length);
	}
	
	public static Slice<_$$prim$$_[]> cloneSlice_$$Prim$$_(Slice<_$$prim$$_[]> slice)
	{
		return wholeArraySlice_$$Prim$$_(sliceToNew_$$Prim$$_ArrayOP(slice));
	}
	
	public static Slice<_$$prim$$_[]> trimSliceOPC_$$Prim$$_(Slice<_$$prim$$_[]> arraySlice)
	{
		if (isArraySliceFull_$$Prim$$_(arraySlice))
			return arraySlice;
		else
			return cloneSlice_$$Prim$$_(arraySlice);
	}
	
	public static boolean isArraySliceFull_$$Prim$$_(Slice<_$$prim$$_[]> arraySlice)
	{
		return arraySlice.getOffset() == 0 && arraySlice.getLength() == arraySlice.getUnderlying().length;
	}
	
	
	
	
	
	public static void arrayslicecopy_$$Prim$$_(Slice<_$$prim$$_[]> arraySliceSource, Slice<_$$prim$$_[]> arraySliceDest)
	{
		arrayslicecopy_$$Prim$$_(arraySliceSource, arraySliceDest, least(arraySliceSource.getLength(), arraySliceDest.getLength()));
	}
	
	public static void arrayslicecopy_$$Prim$$_(Slice<_$$prim$$_[]> arraySliceSource, Slice<_$$prim$$_[]> arraySliceDest, int length)
	{
		System.arraycopy(arraySliceSource.getUnderlying(), arraySliceSource.getOffset(), arraySliceDest.getUnderlying(), arraySliceDest.getOffset(), length);
	}
	
	
	
	
	 */
	
	
	
	public static boolean equals(boolean[] a, int aOffset, boolean[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (!eqSane(a[aOffset + i], b[bOffset + i]))
				return false;
		return true;
	}
	
	public static boolean equalsSliceBoolean(Slice<boolean[]> a, Slice<boolean[]> b)
	{
		int length = a.getLength();
		if (length != b.getLength())
			return false;
		
		return equals(a.getUnderlying(), a.getOffset(), b.getUnderlying(), b.getOffset(), length);
	}
	
	
	
	
	
	public static boolean getBoolean(Slice<boolean[]> slice, int index)
	{
		return slice.getUnderlying()[slice.getOffset() + index];
	}
	
	public static void setBoolean(Slice<boolean[]> slice, int index, boolean value)
	{
		slice.getUnderlying()[slice.getOffset() + index] = value;
	}
	
	
	public static boolean isBooleanSliceAllSame(Slice<boolean[]> source, boolean value)
	{
		int n = source.getLength();
		for (int i = 0; i < n; i++)
		{
			if (getBoolean(source, i) != value)
				return false;
		}
		return true;
	}
	
	public static boolean isBooleanArrayAllSame(boolean[] source, int offset, int length, boolean value)
	{
		int e = offset + length;
		for (int i = offset; i < e; i++)
		{
			if (source[i] != value)
				return false;
		}
		return true;
	}
	
	public static boolean isBooleanArrayAllSame(boolean[] source, boolean value)
	{
		return isBooleanArrayAllSame(source, 0, source.length, value);
	}
	
	
	public static boolean[] sliceToNewBooleanArrayOP(@ReadonlyValue Slice<boolean[]> slice)
	{
		int n = slice.getLength();
		boolean[] a = new boolean[n];
		System.arraycopy(slice.getUnderlying(), slice.getOffset(), a, 0, n);
		return a;
	}
	
	public static boolean[] sliceToBooleanArrayOPC(@ReadonlyValue Slice<boolean[]> slice)
	{
		int o = slice.getOffset();
		int n = slice.getLength();
		boolean[] u = slice.getUnderlying();
		
		if (o == 0 && n == u.length)
		{
			return u;
		}
		else
		{
			boolean[] a = new boolean[n];
			System.arraycopy(u, o, a, 0, n);
			return a;
		}
	}
	
	public static Slice<boolean[]> wholeArraySliceBoolean(boolean[] array)
	{
		return new Slice<>(array, 0, array.length);
	}
	
	public static Slice<boolean[]> newArraySliceBoolean(boolean... array)
	{
		return new Slice<>(array, 0, array.length);
	}
	
	
	
	public static Slice<boolean[]> newArrayToSliceBoolean(int length)
	{
		return new Slice<>(new boolean[length], 0, length);
	}
	
	public static Slice<boolean[]> cloneArrayToSliceBoolean(boolean[] array)
	{
		return new Slice<>(array.clone(), 0, array.length);
	}
	
	public static Slice<boolean[]> cloneSliceBoolean(Slice<boolean[]> slice)
	{
		return wholeArraySliceBoolean(sliceToNewBooleanArrayOP(slice));
	}
	
	public static Slice<boolean[]> trimSliceOPCBoolean(Slice<boolean[]> arraySlice)
	{
		if (isArraySliceFullBoolean(arraySlice))
			return arraySlice;
		else
			return cloneSliceBoolean(arraySlice);
	}
	
	public static boolean isArraySliceFullBoolean(Slice<boolean[]> arraySlice)
	{
		return arraySlice.getOffset() == 0 && arraySlice.getLength() == arraySlice.getUnderlying().length;
	}
	
	
	
	
	
	public static void arrayslicecopyBoolean(Slice<boolean[]> arraySliceSource, Slice<boolean[]> arraySliceDest)
	{
		arrayslicecopyBoolean(arraySliceSource, arraySliceDest, least(arraySliceSource.getLength(), arraySliceDest.getLength()));
	}
	
	public static void arrayslicecopyBoolean(Slice<boolean[]> arraySliceSource, Slice<boolean[]> arraySliceDest, int length)
	{
		System.arraycopy(arraySliceSource.getUnderlying(), arraySliceSource.getOffset(), arraySliceDest.getUnderlying(), arraySliceDest.getOffset(), length);
	}
	
	
	
	
	
	
	
	public static boolean equals(byte[] a, int aOffset, byte[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (!eqSane(a[aOffset + i], b[bOffset + i]))
				return false;
		return true;
	}
	
	public static boolean equalsSliceByte(Slice<byte[]> a, Slice<byte[]> b)
	{
		int length = a.getLength();
		if (length != b.getLength())
			return false;
		
		return equals(a.getUnderlying(), a.getOffset(), b.getUnderlying(), b.getOffset(), length);
	}
	
	
	
	
	
	public static byte getByte(Slice<byte[]> slice, int index)
	{
		return slice.getUnderlying()[slice.getOffset() + index];
	}
	
	public static void setByte(Slice<byte[]> slice, int index, byte value)
	{
		slice.getUnderlying()[slice.getOffset() + index] = value;
	}
	
	
	public static boolean isByteSliceAllSame(Slice<byte[]> source, byte value)
	{
		int n = source.getLength();
		for (int i = 0; i < n; i++)
		{
			if (getByte(source, i) != value)
				return false;
		}
		return true;
	}
	
	public static boolean isByteArrayAllSame(byte[] source, int offset, int length, byte value)
	{
		int e = offset + length;
		for (int i = offset; i < e; i++)
		{
			if (source[i] != value)
				return false;
		}
		return true;
	}
	
	public static boolean isByteArrayAllSame(byte[] source, byte value)
	{
		return isByteArrayAllSame(source, 0, source.length, value);
	}
	
	
	public static byte[] sliceToNewByteArrayOP(@ReadonlyValue Slice<byte[]> slice)
	{
		int n = slice.getLength();
		byte[] a = new byte[n];
		System.arraycopy(slice.getUnderlying(), slice.getOffset(), a, 0, n);
		return a;
	}
	
	public static byte[] sliceToByteArrayOPC(@ReadonlyValue Slice<byte[]> slice)
	{
		int o = slice.getOffset();
		int n = slice.getLength();
		byte[] u = slice.getUnderlying();
		
		if (o == 0 && n == u.length)
		{
			return u;
		}
		else
		{
			byte[] a = new byte[n];
			System.arraycopy(u, o, a, 0, n);
			return a;
		}
	}
	
	public static Slice<byte[]> wholeArraySliceByte(byte[] array)
	{
		return new Slice<>(array, 0, array.length);
	}
	
	public static Slice<byte[]> newArraySliceByte(byte... array)
	{
		return new Slice<>(array, 0, array.length);
	}
	
	
	
	public static Slice<byte[]> newArrayToSliceByte(int length)
	{
		return new Slice<>(new byte[length], 0, length);
	}
	
	public static Slice<byte[]> cloneArrayToSliceByte(byte[] array)
	{
		return new Slice<>(array.clone(), 0, array.length);
	}
	
	public static Slice<byte[]> cloneSliceByte(Slice<byte[]> slice)
	{
		return wholeArraySliceByte(sliceToNewByteArrayOP(slice));
	}
	
	public static Slice<byte[]> trimSliceOPCByte(Slice<byte[]> arraySlice)
	{
		if (isArraySliceFullByte(arraySlice))
			return arraySlice;
		else
			return cloneSliceByte(arraySlice);
	}
	
	public static boolean isArraySliceFullByte(Slice<byte[]> arraySlice)
	{
		return arraySlice.getOffset() == 0 && arraySlice.getLength() == arraySlice.getUnderlying().length;
	}
	
	
	
	
	
	public static void arrayslicecopyByte(Slice<byte[]> arraySliceSource, Slice<byte[]> arraySliceDest)
	{
		arrayslicecopyByte(arraySliceSource, arraySliceDest, least(arraySliceSource.getLength(), arraySliceDest.getLength()));
	}
	
	public static void arrayslicecopyByte(Slice<byte[]> arraySliceSource, Slice<byte[]> arraySliceDest, int length)
	{
		System.arraycopy(arraySliceSource.getUnderlying(), arraySliceSource.getOffset(), arraySliceDest.getUnderlying(), arraySliceDest.getOffset(), length);
	}
	
	
	
	
	
	
	
	public static boolean equals(char[] a, int aOffset, char[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (!eqSane(a[aOffset + i], b[bOffset + i]))
				return false;
		return true;
	}
	
	public static boolean equalsSliceChar(Slice<char[]> a, Slice<char[]> b)
	{
		int length = a.getLength();
		if (length != b.getLength())
			return false;
		
		return equals(a.getUnderlying(), a.getOffset(), b.getUnderlying(), b.getOffset(), length);
	}
	
	
	
	
	
	public static char getChar(Slice<char[]> slice, int index)
	{
		return slice.getUnderlying()[slice.getOffset() + index];
	}
	
	public static void setChar(Slice<char[]> slice, int index, char value)
	{
		slice.getUnderlying()[slice.getOffset() + index] = value;
	}
	
	
	public static boolean isCharSliceAllSame(Slice<char[]> source, char value)
	{
		int n = source.getLength();
		for (int i = 0; i < n; i++)
		{
			if (getChar(source, i) != value)
				return false;
		}
		return true;
	}
	
	public static boolean isCharArrayAllSame(char[] source, int offset, int length, char value)
	{
		int e = offset + length;
		for (int i = offset; i < e; i++)
		{
			if (source[i] != value)
				return false;
		}
		return true;
	}
	
	public static boolean isCharArrayAllSame(char[] source, char value)
	{
		return isCharArrayAllSame(source, 0, source.length, value);
	}
	
	
	public static char[] sliceToNewCharArrayOP(@ReadonlyValue Slice<char[]> slice)
	{
		int n = slice.getLength();
		char[] a = new char[n];
		System.arraycopy(slice.getUnderlying(), slice.getOffset(), a, 0, n);
		return a;
	}
	
	public static char[] sliceToCharArrayOPC(@ReadonlyValue Slice<char[]> slice)
	{
		int o = slice.getOffset();
		int n = slice.getLength();
		char[] u = slice.getUnderlying();
		
		if (o == 0 && n == u.length)
		{
			return u;
		}
		else
		{
			char[] a = new char[n];
			System.arraycopy(u, o, a, 0, n);
			return a;
		}
	}
	
	public static Slice<char[]> wholeArraySliceChar(char[] array)
	{
		return new Slice<>(array, 0, array.length);
	}
	
	public static Slice<char[]> newArraySliceChar(char... array)
	{
		return new Slice<>(array, 0, array.length);
	}
	
	
	
	public static Slice<char[]> newArrayToSliceChar(int length)
	{
		return new Slice<>(new char[length], 0, length);
	}
	
	public static Slice<char[]> cloneArrayToSliceChar(char[] array)
	{
		return new Slice<>(array.clone(), 0, array.length);
	}
	
	public static Slice<char[]> cloneSliceChar(Slice<char[]> slice)
	{
		return wholeArraySliceChar(sliceToNewCharArrayOP(slice));
	}
	
	public static Slice<char[]> trimSliceOPCChar(Slice<char[]> arraySlice)
	{
		if (isArraySliceFullChar(arraySlice))
			return arraySlice;
		else
			return cloneSliceChar(arraySlice);
	}
	
	public static boolean isArraySliceFullChar(Slice<char[]> arraySlice)
	{
		return arraySlice.getOffset() == 0 && arraySlice.getLength() == arraySlice.getUnderlying().length;
	}
	
	
	
	
	
	public static void arrayslicecopyChar(Slice<char[]> arraySliceSource, Slice<char[]> arraySliceDest)
	{
		arrayslicecopyChar(arraySliceSource, arraySliceDest, least(arraySliceSource.getLength(), arraySliceDest.getLength()));
	}
	
	public static void arrayslicecopyChar(Slice<char[]> arraySliceSource, Slice<char[]> arraySliceDest, int length)
	{
		System.arraycopy(arraySliceSource.getUnderlying(), arraySliceSource.getOffset(), arraySliceDest.getUnderlying(), arraySliceDest.getOffset(), length);
	}
	
	
	
	
	
	
	
	public static boolean equals(short[] a, int aOffset, short[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (!eqSane(a[aOffset + i], b[bOffset + i]))
				return false;
		return true;
	}
	
	public static boolean equalsSliceShort(Slice<short[]> a, Slice<short[]> b)
	{
		int length = a.getLength();
		if (length != b.getLength())
			return false;
		
		return equals(a.getUnderlying(), a.getOffset(), b.getUnderlying(), b.getOffset(), length);
	}
	
	
	
	
	
	public static short getShort(Slice<short[]> slice, int index)
	{
		return slice.getUnderlying()[slice.getOffset() + index];
	}
	
	public static void setShort(Slice<short[]> slice, int index, short value)
	{
		slice.getUnderlying()[slice.getOffset() + index] = value;
	}
	
	
	public static boolean isShortSliceAllSame(Slice<short[]> source, short value)
	{
		int n = source.getLength();
		for (int i = 0; i < n; i++)
		{
			if (getShort(source, i) != value)
				return false;
		}
		return true;
	}
	
	public static boolean isShortArrayAllSame(short[] source, int offset, int length, short value)
	{
		int e = offset + length;
		for (int i = offset; i < e; i++)
		{
			if (source[i] != value)
				return false;
		}
		return true;
	}
	
	public static boolean isShortArrayAllSame(short[] source, short value)
	{
		return isShortArrayAllSame(source, 0, source.length, value);
	}
	
	
	public static short[] sliceToNewShortArrayOP(@ReadonlyValue Slice<short[]> slice)
	{
		int n = slice.getLength();
		short[] a = new short[n];
		System.arraycopy(slice.getUnderlying(), slice.getOffset(), a, 0, n);
		return a;
	}
	
	public static short[] sliceToShortArrayOPC(@ReadonlyValue Slice<short[]> slice)
	{
		int o = slice.getOffset();
		int n = slice.getLength();
		short[] u = slice.getUnderlying();
		
		if (o == 0 && n == u.length)
		{
			return u;
		}
		else
		{
			short[] a = new short[n];
			System.arraycopy(u, o, a, 0, n);
			return a;
		}
	}
	
	public static Slice<short[]> wholeArraySliceShort(short[] array)
	{
		return new Slice<>(array, 0, array.length);
	}
	
	public static Slice<short[]> newArraySliceShort(short... array)
	{
		return new Slice<>(array, 0, array.length);
	}
	
	
	
	public static Slice<short[]> newArrayToSliceShort(int length)
	{
		return new Slice<>(new short[length], 0, length);
	}
	
	public static Slice<short[]> cloneArrayToSliceShort(short[] array)
	{
		return new Slice<>(array.clone(), 0, array.length);
	}
	
	public static Slice<short[]> cloneSliceShort(Slice<short[]> slice)
	{
		return wholeArraySliceShort(sliceToNewShortArrayOP(slice));
	}
	
	public static Slice<short[]> trimSliceOPCShort(Slice<short[]> arraySlice)
	{
		if (isArraySliceFullShort(arraySlice))
			return arraySlice;
		else
			return cloneSliceShort(arraySlice);
	}
	
	public static boolean isArraySliceFullShort(Slice<short[]> arraySlice)
	{
		return arraySlice.getOffset() == 0 && arraySlice.getLength() == arraySlice.getUnderlying().length;
	}
	
	
	
	
	
	public static void arrayslicecopyShort(Slice<short[]> arraySliceSource, Slice<short[]> arraySliceDest)
	{
		arrayslicecopyShort(arraySliceSource, arraySliceDest, least(arraySliceSource.getLength(), arraySliceDest.getLength()));
	}
	
	public static void arrayslicecopyShort(Slice<short[]> arraySliceSource, Slice<short[]> arraySliceDest, int length)
	{
		System.arraycopy(arraySliceSource.getUnderlying(), arraySliceSource.getOffset(), arraySliceDest.getUnderlying(), arraySliceDest.getOffset(), length);
	}
	
	
	
	
	
	
	
	public static boolean equals(float[] a, int aOffset, float[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (!eqSane(a[aOffset + i], b[bOffset + i]))
				return false;
		return true;
	}
	
	public static boolean equalsSliceFloat(Slice<float[]> a, Slice<float[]> b)
	{
		int length = a.getLength();
		if (length != b.getLength())
			return false;
		
		return equals(a.getUnderlying(), a.getOffset(), b.getUnderlying(), b.getOffset(), length);
	}
	
	
	
	
	
	public static float getFloat(Slice<float[]> slice, int index)
	{
		return slice.getUnderlying()[slice.getOffset() + index];
	}
	
	public static void setFloat(Slice<float[]> slice, int index, float value)
	{
		slice.getUnderlying()[slice.getOffset() + index] = value;
	}
	
	
	public static boolean isFloatSliceAllSame(Slice<float[]> source, float value)
	{
		int n = source.getLength();
		for (int i = 0; i < n; i++)
		{
			if (getFloat(source, i) != value)
				return false;
		}
		return true;
	}
	
	public static boolean isFloatArrayAllSame(float[] source, int offset, int length, float value)
	{
		int e = offset + length;
		for (int i = offset; i < e; i++)
		{
			if (source[i] != value)
				return false;
		}
		return true;
	}
	
	public static boolean isFloatArrayAllSame(float[] source, float value)
	{
		return isFloatArrayAllSame(source, 0, source.length, value);
	}
	
	
	public static float[] sliceToNewFloatArrayOP(@ReadonlyValue Slice<float[]> slice)
	{
		int n = slice.getLength();
		float[] a = new float[n];
		System.arraycopy(slice.getUnderlying(), slice.getOffset(), a, 0, n);
		return a;
	}
	
	public static float[] sliceToFloatArrayOPC(@ReadonlyValue Slice<float[]> slice)
	{
		int o = slice.getOffset();
		int n = slice.getLength();
		float[] u = slice.getUnderlying();
		
		if (o == 0 && n == u.length)
		{
			return u;
		}
		else
		{
			float[] a = new float[n];
			System.arraycopy(u, o, a, 0, n);
			return a;
		}
	}
	
	public static Slice<float[]> wholeArraySliceFloat(float[] array)
	{
		return new Slice<>(array, 0, array.length);
	}
	
	public static Slice<float[]> newArraySliceFloat(float... array)
	{
		return new Slice<>(array, 0, array.length);
	}
	
	
	
	public static Slice<float[]> newArrayToSliceFloat(int length)
	{
		return new Slice<>(new float[length], 0, length);
	}
	
	public static Slice<float[]> cloneArrayToSliceFloat(float[] array)
	{
		return new Slice<>(array.clone(), 0, array.length);
	}
	
	public static Slice<float[]> cloneSliceFloat(Slice<float[]> slice)
	{
		return wholeArraySliceFloat(sliceToNewFloatArrayOP(slice));
	}
	
	public static Slice<float[]> trimSliceOPCFloat(Slice<float[]> arraySlice)
	{
		if (isArraySliceFullFloat(arraySlice))
			return arraySlice;
		else
			return cloneSliceFloat(arraySlice);
	}
	
	public static boolean isArraySliceFullFloat(Slice<float[]> arraySlice)
	{
		return arraySlice.getOffset() == 0 && arraySlice.getLength() == arraySlice.getUnderlying().length;
	}
	
	
	
	
	
	public static void arrayslicecopyFloat(Slice<float[]> arraySliceSource, Slice<float[]> arraySliceDest)
	{
		arrayslicecopyFloat(arraySliceSource, arraySliceDest, least(arraySliceSource.getLength(), arraySliceDest.getLength()));
	}
	
	public static void arrayslicecopyFloat(Slice<float[]> arraySliceSource, Slice<float[]> arraySliceDest, int length)
	{
		System.arraycopy(arraySliceSource.getUnderlying(), arraySliceSource.getOffset(), arraySliceDest.getUnderlying(), arraySliceDest.getOffset(), length);
	}
	
	
	
	
	
	
	
	public static boolean equals(int[] a, int aOffset, int[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (!eqSane(a[aOffset + i], b[bOffset + i]))
				return false;
		return true;
	}
	
	public static boolean equalsSliceInt(Slice<int[]> a, Slice<int[]> b)
	{
		int length = a.getLength();
		if (length != b.getLength())
			return false;
		
		return equals(a.getUnderlying(), a.getOffset(), b.getUnderlying(), b.getOffset(), length);
	}
	
	
	
	
	
	public static int getInt(Slice<int[]> slice, int index)
	{
		return slice.getUnderlying()[slice.getOffset() + index];
	}
	
	public static void setInt(Slice<int[]> slice, int index, int value)
	{
		slice.getUnderlying()[slice.getOffset() + index] = value;
	}
	
	
	public static boolean isIntSliceAllSame(Slice<int[]> source, int value)
	{
		int n = source.getLength();
		for (int i = 0; i < n; i++)
		{
			if (getInt(source, i) != value)
				return false;
		}
		return true;
	}
	
	public static boolean isIntArrayAllSame(int[] source, int offset, int length, int value)
	{
		int e = offset + length;
		for (int i = offset; i < e; i++)
		{
			if (source[i] != value)
				return false;
		}
		return true;
	}
	
	public static boolean isIntArrayAllSame(int[] source, int value)
	{
		return isIntArrayAllSame(source, 0, source.length, value);
	}
	
	
	public static int[] sliceToNewIntArrayOP(@ReadonlyValue Slice<int[]> slice)
	{
		int n = slice.getLength();
		int[] a = new int[n];
		System.arraycopy(slice.getUnderlying(), slice.getOffset(), a, 0, n);
		return a;
	}
	
	public static int[] sliceToIntArrayOPC(@ReadonlyValue Slice<int[]> slice)
	{
		int o = slice.getOffset();
		int n = slice.getLength();
		int[] u = slice.getUnderlying();
		
		if (o == 0 && n == u.length)
		{
			return u;
		}
		else
		{
			int[] a = new int[n];
			System.arraycopy(u, o, a, 0, n);
			return a;
		}
	}
	
	public static Slice<int[]> wholeArraySliceInt(int[] array)
	{
		return new Slice<>(array, 0, array.length);
	}
	
	public static Slice<int[]> newArraySliceInt(int... array)
	{
		return new Slice<>(array, 0, array.length);
	}
	
	
	
	public static Slice<int[]> newArrayToSliceInt(int length)
	{
		return new Slice<>(new int[length], 0, length);
	}
	
	public static Slice<int[]> cloneArrayToSliceInt(int[] array)
	{
		return new Slice<>(array.clone(), 0, array.length);
	}
	
	public static Slice<int[]> cloneSliceInt(Slice<int[]> slice)
	{
		return wholeArraySliceInt(sliceToNewIntArrayOP(slice));
	}
	
	public static Slice<int[]> trimSliceOPCInt(Slice<int[]> arraySlice)
	{
		if (isArraySliceFullInt(arraySlice))
			return arraySlice;
		else
			return cloneSliceInt(arraySlice);
	}
	
	public static boolean isArraySliceFullInt(Slice<int[]> arraySlice)
	{
		return arraySlice.getOffset() == 0 && arraySlice.getLength() == arraySlice.getUnderlying().length;
	}
	
	
	
	
	
	public static void arrayslicecopyInt(Slice<int[]> arraySliceSource, Slice<int[]> arraySliceDest)
	{
		arrayslicecopyInt(arraySliceSource, arraySliceDest, least(arraySliceSource.getLength(), arraySliceDest.getLength()));
	}
	
	public static void arrayslicecopyInt(Slice<int[]> arraySliceSource, Slice<int[]> arraySliceDest, int length)
	{
		System.arraycopy(arraySliceSource.getUnderlying(), arraySliceSource.getOffset(), arraySliceDest.getUnderlying(), arraySliceDest.getOffset(), length);
	}
	
	
	
	
	
	
	
	public static boolean equals(double[] a, int aOffset, double[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (!eqSane(a[aOffset + i], b[bOffset + i]))
				return false;
		return true;
	}
	
	public static boolean equalsSliceDouble(Slice<double[]> a, Slice<double[]> b)
	{
		int length = a.getLength();
		if (length != b.getLength())
			return false;
		
		return equals(a.getUnderlying(), a.getOffset(), b.getUnderlying(), b.getOffset(), length);
	}
	
	
	
	
	
	public static double getDouble(Slice<double[]> slice, int index)
	{
		return slice.getUnderlying()[slice.getOffset() + index];
	}
	
	public static void setDouble(Slice<double[]> slice, int index, double value)
	{
		slice.getUnderlying()[slice.getOffset() + index] = value;
	}
	
	
	public static boolean isDoubleSliceAllSame(Slice<double[]> source, double value)
	{
		int n = source.getLength();
		for (int i = 0; i < n; i++)
		{
			if (getDouble(source, i) != value)
				return false;
		}
		return true;
	}
	
	public static boolean isDoubleArrayAllSame(double[] source, int offset, int length, double value)
	{
		int e = offset + length;
		for (int i = offset; i < e; i++)
		{
			if (source[i] != value)
				return false;
		}
		return true;
	}
	
	public static boolean isDoubleArrayAllSame(double[] source, double value)
	{
		return isDoubleArrayAllSame(source, 0, source.length, value);
	}
	
	
	public static double[] sliceToNewDoubleArrayOP(@ReadonlyValue Slice<double[]> slice)
	{
		int n = slice.getLength();
		double[] a = new double[n];
		System.arraycopy(slice.getUnderlying(), slice.getOffset(), a, 0, n);
		return a;
	}
	
	public static double[] sliceToDoubleArrayOPC(@ReadonlyValue Slice<double[]> slice)
	{
		int o = slice.getOffset();
		int n = slice.getLength();
		double[] u = slice.getUnderlying();
		
		if (o == 0 && n == u.length)
		{
			return u;
		}
		else
		{
			double[] a = new double[n];
			System.arraycopy(u, o, a, 0, n);
			return a;
		}
	}
	
	public static Slice<double[]> wholeArraySliceDouble(double[] array)
	{
		return new Slice<>(array, 0, array.length);
	}
	
	public static Slice<double[]> newArraySliceDouble(double... array)
	{
		return new Slice<>(array, 0, array.length);
	}
	
	
	
	public static Slice<double[]> newArrayToSliceDouble(int length)
	{
		return new Slice<>(new double[length], 0, length);
	}
	
	public static Slice<double[]> cloneArrayToSliceDouble(double[] array)
	{
		return new Slice<>(array.clone(), 0, array.length);
	}
	
	public static Slice<double[]> cloneSliceDouble(Slice<double[]> slice)
	{
		return wholeArraySliceDouble(sliceToNewDoubleArrayOP(slice));
	}
	
	public static Slice<double[]> trimSliceOPCDouble(Slice<double[]> arraySlice)
	{
		if (isArraySliceFullDouble(arraySlice))
			return arraySlice;
		else
			return cloneSliceDouble(arraySlice);
	}
	
	public static boolean isArraySliceFullDouble(Slice<double[]> arraySlice)
	{
		return arraySlice.getOffset() == 0 && arraySlice.getLength() == arraySlice.getUnderlying().length;
	}
	
	
	
	
	
	public static void arrayslicecopyDouble(Slice<double[]> arraySliceSource, Slice<double[]> arraySliceDest)
	{
		arrayslicecopyDouble(arraySliceSource, arraySliceDest, least(arraySliceSource.getLength(), arraySliceDest.getLength()));
	}
	
	public static void arrayslicecopyDouble(Slice<double[]> arraySliceSource, Slice<double[]> arraySliceDest, int length)
	{
		System.arraycopy(arraySliceSource.getUnderlying(), arraySliceSource.getOffset(), arraySliceDest.getUnderlying(), arraySliceDest.getOffset(), length);
	}
	
	
	
	
	
	
	
	public static boolean equals(long[] a, int aOffset, long[] b, int bOffset, int length)
	{
		for (int i = 0; i < length; i++)
			if (!eqSane(a[aOffset + i], b[bOffset + i]))
				return false;
		return true;
	}
	
	public static boolean equalsSliceLong(Slice<long[]> a, Slice<long[]> b)
	{
		int length = a.getLength();
		if (length != b.getLength())
			return false;
		
		return equals(a.getUnderlying(), a.getOffset(), b.getUnderlying(), b.getOffset(), length);
	}
	
	
	
	
	
	public static long getLong(Slice<long[]> slice, int index)
	{
		return slice.getUnderlying()[slice.getOffset() + index];
	}
	
	public static void setLong(Slice<long[]> slice, int index, long value)
	{
		slice.getUnderlying()[slice.getOffset() + index] = value;
	}
	
	
	public static boolean isLongSliceAllSame(Slice<long[]> source, long value)
	{
		int n = source.getLength();
		for (int i = 0; i < n; i++)
		{
			if (getLong(source, i) != value)
				return false;
		}
		return true;
	}
	
	public static boolean isLongArrayAllSame(long[] source, int offset, int length, long value)
	{
		int e = offset + length;
		for (int i = offset; i < e; i++)
		{
			if (source[i] != value)
				return false;
		}
		return true;
	}
	
	public static boolean isLongArrayAllSame(long[] source, long value)
	{
		return isLongArrayAllSame(source, 0, source.length, value);
	}
	
	
	public static long[] sliceToNewLongArrayOP(@ReadonlyValue Slice<long[]> slice)
	{
		int n = slice.getLength();
		long[] a = new long[n];
		System.arraycopy(slice.getUnderlying(), slice.getOffset(), a, 0, n);
		return a;
	}
	
	public static long[] sliceToLongArrayOPC(@ReadonlyValue Slice<long[]> slice)
	{
		int o = slice.getOffset();
		int n = slice.getLength();
		long[] u = slice.getUnderlying();
		
		if (o == 0 && n == u.length)
		{
			return u;
		}
		else
		{
			long[] a = new long[n];
			System.arraycopy(u, o, a, 0, n);
			return a;
		}
	}
	
	public static Slice<long[]> wholeArraySliceLong(long[] array)
	{
		return new Slice<>(array, 0, array.length);
	}
	
	public static Slice<long[]> newArraySliceLong(long... array)
	{
		return new Slice<>(array, 0, array.length);
	}
	
	
	
	public static Slice<long[]> newArrayToSliceLong(int length)
	{
		return new Slice<>(new long[length], 0, length);
	}
	
	public static Slice<long[]> cloneArrayToSliceLong(long[] array)
	{
		return new Slice<>(array.clone(), 0, array.length);
	}
	
	public static Slice<long[]> cloneSliceLong(Slice<long[]> slice)
	{
		return wholeArraySliceLong(sliceToNewLongArrayOP(slice));
	}
	
	public static Slice<long[]> trimSliceOPCLong(Slice<long[]> arraySlice)
	{
		if (isArraySliceFullLong(arraySlice))
			return arraySlice;
		else
			return cloneSliceLong(arraySlice);
	}
	
	public static boolean isArraySliceFullLong(Slice<long[]> arraySlice)
	{
		return arraySlice.getOffset() == 0 && arraySlice.getLength() == arraySlice.getUnderlying().length;
	}
	
	
	
	
	
	public static void arrayslicecopyLong(Slice<long[]> arraySliceSource, Slice<long[]> arraySliceDest)
	{
		arrayslicecopyLong(arraySliceSource, arraySliceDest, least(arraySliceSource.getLength(), arraySliceDest.getLength()));
	}
	
	public static void arrayslicecopyLong(Slice<long[]> arraySliceSource, Slice<long[]> arraySliceDest, int length)
	{
		System.arraycopy(arraySliceSource.getUnderlying(), arraySliceSource.getOffset(), arraySliceDest.getUnderlying(), arraySliceDest.getOffset(), length);
	}
	
	
	
	
	//>>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static boolean equalsWithinTolerances(double[] a, double[] b, double absoluteTolerance, double relativeTolerance)
	{
		if (a.length != b.length)
			return false;
		int n = a.length; // = b.length;
		
		return equalsWithinTolerances(a, 0, b, 0, n, absoluteTolerance, relativeTolerance);
	}
	
	public static boolean equalsWithinTolerances(double[] a, int aOffset, double[] b, int bOffset, int length, double absoluteTolerance, double relativeTolerance)
	{
		for (int i = 0; i < length; i++)
			if (!equalWithinTolerances(a[aOffset + i], b[bOffset + i], absoluteTolerance, relativeTolerance))
				return false;
		return true;
	}
	
	public static boolean equalsWithinTolerancesSliceDouble(Slice<double[]> a, Slice<double[]> b, double absoluteTolerance, double relativeTolerance)
	{
		int length = a.getLength();
		if (length != b.getLength())
			return false;
		
		return equalsWithinTolerances(a.getUnderlying(), a.getOffset(), b.getUnderlying(), b.getOffset(), length, absoluteTolerance, relativeTolerance);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static <I> boolean forAll(Predicate<? super I> predicate, I[] inputs)
	{
		for (I v : inputs)
		{
			if (!predicate.test(v))
				return false;
		}
		
		return true;
	}
	
	public static <I> boolean forAny(Predicate<? super I> predicate, I[] inputs)
	{
		for (I v : inputs)
		{
			if (predicate.test(v))
				return true;
		}
		
		return false;
	}
	
	
	
	
	
	
	
	/* <<<
	primxp
	
	public static boolean forAll(UnaryFunction_$$Prim$$_ToBoolean predicate, _$$prim$$_[] inputs)
	{
		for (_$$prim$$_ v : inputs)
		{
			if (!predicate.f(v))
				return false;
		}
		
		return true;
	}
	
	public static boolean forAny(UnaryFunction_$$Prim$$_ToBoolean predicate, _$$prim$$_[] inputs)
	{
		for (_$$prim$$_ v : inputs)
		{
			if (predicate.f(v))
				return true;
		}
		
		return false;
	}
	
	
	
	public static boolean forAll(UnaryFunction_$$Prim$$_ToBoolean predicate, _$$prim$$_[] inputs, int offset, int length)
	{
		for (int i = 0; i < length; i++)
		{
			_$$prim$$_ v = inputs[offset+i];
			if (!predicate.f(v))
				return false;
		}
		
		return true;
	}
	
	public static boolean forAny(UnaryFunction_$$Prim$$_ToBoolean predicate, _$$prim$$_[] inputs, int offset, int length)
	{
		for (int i = 0; i < length; i++)
		{
			_$$prim$$_ v = inputs[offset+i];
			if (predicate.f(v))
				return true;
		}
		
		return false;
	}
	
	
	
	public static boolean forAll(UnaryFunction_$$Prim$$_ToBoolean predicate, Slice<_$$prim$$_[]> inputs)
	{
		return forAll(predicate, inputs.getUnderlying(), inputs.getOffset(), inputs.getLength());
	}
	
	public static boolean forAny(UnaryFunction_$$Prim$$_ToBoolean predicate, Slice<_$$prim$$_[]> inputs)
	{
		return forAny(predicate, inputs.getUnderlying(), inputs.getOffset(), inputs.getLength());
	}
	
	
	
	
	
	
	 */
	
	public static boolean forAll(UnaryFunctionBooleanToBoolean predicate, boolean[] inputs)
	{
		for (boolean v : inputs)
		{
			if (!predicate.f(v))
				return false;
		}
		
		return true;
	}
	
	public static boolean forAny(UnaryFunctionBooleanToBoolean predicate, boolean[] inputs)
	{
		for (boolean v : inputs)
		{
			if (predicate.f(v))
				return true;
		}
		
		return false;
	}
	
	
	
	public static boolean forAll(UnaryFunctionBooleanToBoolean predicate, boolean[] inputs, int offset, int length)
	{
		for (int i = 0; i < length; i++)
		{
			boolean v = inputs[offset+i];
			if (!predicate.f(v))
				return false;
		}
		
		return true;
	}
	
	public static boolean forAny(UnaryFunctionBooleanToBoolean predicate, boolean[] inputs, int offset, int length)
	{
		for (int i = 0; i < length; i++)
		{
			boolean v = inputs[offset+i];
			if (predicate.f(v))
				return true;
		}
		
		return false;
	}
	
	
	
	public static boolean forAll(UnaryFunctionBooleanToBoolean predicate, Slice<boolean[]> inputs)
	{
		return forAll(predicate, inputs.getUnderlying(), inputs.getOffset(), inputs.getLength());
	}
	
	public static boolean forAny(UnaryFunctionBooleanToBoolean predicate, Slice<boolean[]> inputs)
	{
		return forAny(predicate, inputs.getUnderlying(), inputs.getOffset(), inputs.getLength());
	}
	
	
	
	
	
	
	
	public static boolean forAll(UnaryFunctionByteToBoolean predicate, byte[] inputs)
	{
		for (byte v : inputs)
		{
			if (!predicate.f(v))
				return false;
		}
		
		return true;
	}
	
	public static boolean forAny(UnaryFunctionByteToBoolean predicate, byte[] inputs)
	{
		for (byte v : inputs)
		{
			if (predicate.f(v))
				return true;
		}
		
		return false;
	}
	
	
	
	public static boolean forAll(UnaryFunctionByteToBoolean predicate, byte[] inputs, int offset, int length)
	{
		for (int i = 0; i < length; i++)
		{
			byte v = inputs[offset+i];
			if (!predicate.f(v))
				return false;
		}
		
		return true;
	}
	
	public static boolean forAny(UnaryFunctionByteToBoolean predicate, byte[] inputs, int offset, int length)
	{
		for (int i = 0; i < length; i++)
		{
			byte v = inputs[offset+i];
			if (predicate.f(v))
				return true;
		}
		
		return false;
	}
	
	
	
	public static boolean forAll(UnaryFunctionByteToBoolean predicate, Slice<byte[]> inputs)
	{
		return forAll(predicate, inputs.getUnderlying(), inputs.getOffset(), inputs.getLength());
	}
	
	public static boolean forAny(UnaryFunctionByteToBoolean predicate, Slice<byte[]> inputs)
	{
		return forAny(predicate, inputs.getUnderlying(), inputs.getOffset(), inputs.getLength());
	}
	
	
	
	
	
	
	
	public static boolean forAll(UnaryFunctionCharToBoolean predicate, char[] inputs)
	{
		for (char v : inputs)
		{
			if (!predicate.f(v))
				return false;
		}
		
		return true;
	}
	
	public static boolean forAny(UnaryFunctionCharToBoolean predicate, char[] inputs)
	{
		for (char v : inputs)
		{
			if (predicate.f(v))
				return true;
		}
		
		return false;
	}
	
	
	
	public static boolean forAll(UnaryFunctionCharToBoolean predicate, char[] inputs, int offset, int length)
	{
		for (int i = 0; i < length; i++)
		{
			char v = inputs[offset+i];
			if (!predicate.f(v))
				return false;
		}
		
		return true;
	}
	
	public static boolean forAny(UnaryFunctionCharToBoolean predicate, char[] inputs, int offset, int length)
	{
		for (int i = 0; i < length; i++)
		{
			char v = inputs[offset+i];
			if (predicate.f(v))
				return true;
		}
		
		return false;
	}
	
	
	
	public static boolean forAll(UnaryFunctionCharToBoolean predicate, Slice<char[]> inputs)
	{
		return forAll(predicate, inputs.getUnderlying(), inputs.getOffset(), inputs.getLength());
	}
	
	public static boolean forAny(UnaryFunctionCharToBoolean predicate, Slice<char[]> inputs)
	{
		return forAny(predicate, inputs.getUnderlying(), inputs.getOffset(), inputs.getLength());
	}
	
	
	
	
	
	
	
	public static boolean forAll(UnaryFunctionShortToBoolean predicate, short[] inputs)
	{
		for (short v : inputs)
		{
			if (!predicate.f(v))
				return false;
		}
		
		return true;
	}
	
	public static boolean forAny(UnaryFunctionShortToBoolean predicate, short[] inputs)
	{
		for (short v : inputs)
		{
			if (predicate.f(v))
				return true;
		}
		
		return false;
	}
	
	
	
	public static boolean forAll(UnaryFunctionShortToBoolean predicate, short[] inputs, int offset, int length)
	{
		for (int i = 0; i < length; i++)
		{
			short v = inputs[offset+i];
			if (!predicate.f(v))
				return false;
		}
		
		return true;
	}
	
	public static boolean forAny(UnaryFunctionShortToBoolean predicate, short[] inputs, int offset, int length)
	{
		for (int i = 0; i < length; i++)
		{
			short v = inputs[offset+i];
			if (predicate.f(v))
				return true;
		}
		
		return false;
	}
	
	
	
	public static boolean forAll(UnaryFunctionShortToBoolean predicate, Slice<short[]> inputs)
	{
		return forAll(predicate, inputs.getUnderlying(), inputs.getOffset(), inputs.getLength());
	}
	
	public static boolean forAny(UnaryFunctionShortToBoolean predicate, Slice<short[]> inputs)
	{
		return forAny(predicate, inputs.getUnderlying(), inputs.getOffset(), inputs.getLength());
	}
	
	
	
	
	
	
	
	public static boolean forAll(UnaryFunctionFloatToBoolean predicate, float[] inputs)
	{
		for (float v : inputs)
		{
			if (!predicate.f(v))
				return false;
		}
		
		return true;
	}
	
	public static boolean forAny(UnaryFunctionFloatToBoolean predicate, float[] inputs)
	{
		for (float v : inputs)
		{
			if (predicate.f(v))
				return true;
		}
		
		return false;
	}
	
	
	
	public static boolean forAll(UnaryFunctionFloatToBoolean predicate, float[] inputs, int offset, int length)
	{
		for (int i = 0; i < length; i++)
		{
			float v = inputs[offset+i];
			if (!predicate.f(v))
				return false;
		}
		
		return true;
	}
	
	public static boolean forAny(UnaryFunctionFloatToBoolean predicate, float[] inputs, int offset, int length)
	{
		for (int i = 0; i < length; i++)
		{
			float v = inputs[offset+i];
			if (predicate.f(v))
				return true;
		}
		
		return false;
	}
	
	
	
	public static boolean forAll(UnaryFunctionFloatToBoolean predicate, Slice<float[]> inputs)
	{
		return forAll(predicate, inputs.getUnderlying(), inputs.getOffset(), inputs.getLength());
	}
	
	public static boolean forAny(UnaryFunctionFloatToBoolean predicate, Slice<float[]> inputs)
	{
		return forAny(predicate, inputs.getUnderlying(), inputs.getOffset(), inputs.getLength());
	}
	
	
	
	
	
	
	
	public static boolean forAll(UnaryFunctionIntToBoolean predicate, int[] inputs)
	{
		for (int v : inputs)
		{
			if (!predicate.f(v))
				return false;
		}
		
		return true;
	}
	
	public static boolean forAny(UnaryFunctionIntToBoolean predicate, int[] inputs)
	{
		for (int v : inputs)
		{
			if (predicate.f(v))
				return true;
		}
		
		return false;
	}
	
	
	
	public static boolean forAll(UnaryFunctionIntToBoolean predicate, int[] inputs, int offset, int length)
	{
		for (int i = 0; i < length; i++)
		{
			int v = inputs[offset+i];
			if (!predicate.f(v))
				return false;
		}
		
		return true;
	}
	
	public static boolean forAny(UnaryFunctionIntToBoolean predicate, int[] inputs, int offset, int length)
	{
		for (int i = 0; i < length; i++)
		{
			int v = inputs[offset+i];
			if (predicate.f(v))
				return true;
		}
		
		return false;
	}
	
	
	
	public static boolean forAll(UnaryFunctionIntToBoolean predicate, Slice<int[]> inputs)
	{
		return forAll(predicate, inputs.getUnderlying(), inputs.getOffset(), inputs.getLength());
	}
	
	public static boolean forAny(UnaryFunctionIntToBoolean predicate, Slice<int[]> inputs)
	{
		return forAny(predicate, inputs.getUnderlying(), inputs.getOffset(), inputs.getLength());
	}
	
	
	
	
	
	
	
	public static boolean forAll(UnaryFunctionDoubleToBoolean predicate, double[] inputs)
	{
		for (double v : inputs)
		{
			if (!predicate.f(v))
				return false;
		}
		
		return true;
	}
	
	public static boolean forAny(UnaryFunctionDoubleToBoolean predicate, double[] inputs)
	{
		for (double v : inputs)
		{
			if (predicate.f(v))
				return true;
		}
		
		return false;
	}
	
	
	
	public static boolean forAll(UnaryFunctionDoubleToBoolean predicate, double[] inputs, int offset, int length)
	{
		for (int i = 0; i < length; i++)
		{
			double v = inputs[offset+i];
			if (!predicate.f(v))
				return false;
		}
		
		return true;
	}
	
	public static boolean forAny(UnaryFunctionDoubleToBoolean predicate, double[] inputs, int offset, int length)
	{
		for (int i = 0; i < length; i++)
		{
			double v = inputs[offset+i];
			if (predicate.f(v))
				return true;
		}
		
		return false;
	}
	
	
	
	public static boolean forAll(UnaryFunctionDoubleToBoolean predicate, Slice<double[]> inputs)
	{
		return forAll(predicate, inputs.getUnderlying(), inputs.getOffset(), inputs.getLength());
	}
	
	public static boolean forAny(UnaryFunctionDoubleToBoolean predicate, Slice<double[]> inputs)
	{
		return forAny(predicate, inputs.getUnderlying(), inputs.getOffset(), inputs.getLength());
	}
	
	
	
	
	
	
	
	public static boolean forAll(UnaryFunctionLongToBoolean predicate, long[] inputs)
	{
		for (long v : inputs)
		{
			if (!predicate.f(v))
				return false;
		}
		
		return true;
	}
	
	public static boolean forAny(UnaryFunctionLongToBoolean predicate, long[] inputs)
	{
		for (long v : inputs)
		{
			if (predicate.f(v))
				return true;
		}
		
		return false;
	}
	
	
	
	public static boolean forAll(UnaryFunctionLongToBoolean predicate, long[] inputs, int offset, int length)
	{
		for (int i = 0; i < length; i++)
		{
			long v = inputs[offset+i];
			if (!predicate.f(v))
				return false;
		}
		
		return true;
	}
	
	public static boolean forAny(UnaryFunctionLongToBoolean predicate, long[] inputs, int offset, int length)
	{
		for (int i = 0; i < length; i++)
		{
			long v = inputs[offset+i];
			if (predicate.f(v))
				return true;
		}
		
		return false;
	}
	
	
	
	public static boolean forAll(UnaryFunctionLongToBoolean predicate, Slice<long[]> inputs)
	{
		return forAll(predicate, inputs.getUnderlying(), inputs.getOffset(), inputs.getLength());
	}
	
	public static boolean forAny(UnaryFunctionLongToBoolean predicate, Slice<long[]> inputs)
	{
		return forAny(predicate, inputs.getUnderlying(), inputs.getOffset(), inputs.getLength());
	}
	
	
	
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	//From: org.bouncycastle.util.Arrays
	public static int compareBigEndianLengthsFirstUnsigned(@Nonnull byte[] a, @Nonnull byte[] b)
	{
		if (a == b)
			return 0;
		
		int al = a.length;
		int bl = b.length;
		
		if (al < bl)
			return -1;
		if (al > bl)
			return 1;
		
		int len = arbitrary(al, bl);
		
		for (int i = 0; i < len; ++i)
		{
			int aVal = a[i] & 0xFF;
			int bVal = b[i] & 0xFF;
			
			if (aVal < bVal)
				return -1;
			
			if (aVal > bVal)
				return 1;
		}
		
		return 0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	primxp
	
	
	
	
	
	
	
	public static int indexOfSubarray_$$Prim$$_(Slice<_$$prim$$_[]> array, Slice<_$$prim$$_[]> searchTarget)
	{
		return indexOfSubarray_$$Prim$$_(array, searchTarget, 0);
	}
	
	public static int indexOfSubarray_$$Prim$$_(Slice<_$$prim$$_[]> array, Slice<_$$prim$$_[]> searchTarget, int fromIndex)
	{
		return indexOfSubarray_$$Prim$$_(array, searchTarget, fromIndex, (a, b) -> a == b);
	}
	
	public static int indexOfSubarray_$$Prim$$_(Slice<_$$prim$$_[]> array, Slice<_$$prim$$_[]> searchTarget, int fromIndex, _$$Prim$$_EqualityComparator ceq)
	{
		//Copied and altered from String.java
		
		int sourceCount = array.getLength();
		int targetCount = searchTarget.getLength();
		
		if (fromIndex >= sourceCount)
		{
			return (targetCount == 0 ? sourceCount : -1);
		}
		
		if (fromIndex < 0)
		{
			fromIndex = 0;
		}
		
		if (targetCount == 0)
		{
			return fromIndex;
		}
		
		_$$prim$$_ first = get_$$Prim$$_(searchTarget, 0);
		int max = sourceCount - targetCount;
		
		for (int i = fromIndex; i <= max; i++)
		{
			/* Look for first _$$primitive$$_. ⎋a/
			if (!ceq._$$prim$$_sEqual(get_$$Prim$$_(array, i), first))
			{
				while (++i <= max && !ceq._$$prim$$_sEqual(get_$$Prim$$_(array, i), first));
			}
			
			/* Found first _$$primitive$$_, now look at the rest of v2 ⎋a/
			if (i <= max)
			{
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = 1; j < end && ceq._$$prim$$_sEqual(get_$$Prim$$_(array, j), get_$$Prim$$_(searchTarget, k)); j++, k++);
				
				if (j == end)
				{
					/* Found whole string. ⎋a/
					return i;
				}
			}
		}
		
		return -1;
	}
	 */
	
	
	
	
	
	
	
	public static int indexOfSubarrayBoolean(Slice<boolean[]> array, Slice<boolean[]> searchTarget)
	{
		return indexOfSubarrayBoolean(array, searchTarget, 0);
	}
	
	public static int indexOfSubarrayBoolean(Slice<boolean[]> array, Slice<boolean[]> searchTarget, int fromIndex)
	{
		return indexOfSubarrayBoolean(array, searchTarget, fromIndex, (a, b) -> a == b);
	}
	
	public static int indexOfSubarrayBoolean(Slice<boolean[]> array, Slice<boolean[]> searchTarget, int fromIndex, BooleanEqualityComparator ceq)
	{
		//Copied and altered from String.java
		
		int sourceCount = array.getLength();
		int targetCount = searchTarget.getLength();
		
		if (fromIndex >= sourceCount)
		{
			return (targetCount == 0 ? sourceCount : -1);
		}
		
		if (fromIndex < 0)
		{
			fromIndex = 0;
		}
		
		if (targetCount == 0)
		{
			return fromIndex;
		}
		
		boolean first = getBoolean(searchTarget, 0);
		int max = sourceCount - targetCount;
		
		for (int i = fromIndex; i <= max; i++)
		{
			/* Look for first boolean. */
			if (!ceq.booleansEqual(getBoolean(array, i), first))
			{
				while (++i <= max && !ceq.booleansEqual(getBoolean(array, i), first));
			}
			
			/* Found first boolean, now look at the rest of v2 */
			if (i <= max)
			{
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = 1; j < end && ceq.booleansEqual(getBoolean(array, j), getBoolean(searchTarget, k)); j++, k++);
				
				if (j == end)
				{
					/* Found whole string. */
					return i;
				}
			}
		}
		
		return -1;
	}
	
	
	
	
	
	
	
	public static int indexOfSubarrayByte(Slice<byte[]> array, Slice<byte[]> searchTarget)
	{
		return indexOfSubarrayByte(array, searchTarget, 0);
	}
	
	public static int indexOfSubarrayByte(Slice<byte[]> array, Slice<byte[]> searchTarget, int fromIndex)
	{
		return indexOfSubarrayByte(array, searchTarget, fromIndex, (a, b) -> a == b);
	}
	
	public static int indexOfSubarrayByte(Slice<byte[]> array, Slice<byte[]> searchTarget, int fromIndex, ByteEqualityComparator ceq)
	{
		//Copied and altered from String.java
		
		int sourceCount = array.getLength();
		int targetCount = searchTarget.getLength();
		
		if (fromIndex >= sourceCount)
		{
			return (targetCount == 0 ? sourceCount : -1);
		}
		
		if (fromIndex < 0)
		{
			fromIndex = 0;
		}
		
		if (targetCount == 0)
		{
			return fromIndex;
		}
		
		byte first = getByte(searchTarget, 0);
		int max = sourceCount - targetCount;
		
		for (int i = fromIndex; i <= max; i++)
		{
			/* Look for first byte. */
			if (!ceq.bytesEqual(getByte(array, i), first))
			{
				while (++i <= max && !ceq.bytesEqual(getByte(array, i), first));
			}
			
			/* Found first byte, now look at the rest of v2 */
			if (i <= max)
			{
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = 1; j < end && ceq.bytesEqual(getByte(array, j), getByte(searchTarget, k)); j++, k++);
				
				if (j == end)
				{
					/* Found whole string. */
					return i;
				}
			}
		}
		
		return -1;
	}
	
	
	
	
	
	
	
	public static int indexOfSubarrayChar(Slice<char[]> array, Slice<char[]> searchTarget)
	{
		return indexOfSubarrayChar(array, searchTarget, 0);
	}
	
	public static int indexOfSubarrayChar(Slice<char[]> array, Slice<char[]> searchTarget, int fromIndex)
	{
		return indexOfSubarrayChar(array, searchTarget, fromIndex, (a, b) -> a == b);
	}
	
	public static int indexOfSubarrayChar(Slice<char[]> array, Slice<char[]> searchTarget, int fromIndex, CharEqualityComparator ceq)
	{
		//Copied and altered from String.java
		
		int sourceCount = array.getLength();
		int targetCount = searchTarget.getLength();
		
		if (fromIndex >= sourceCount)
		{
			return (targetCount == 0 ? sourceCount : -1);
		}
		
		if (fromIndex < 0)
		{
			fromIndex = 0;
		}
		
		if (targetCount == 0)
		{
			return fromIndex;
		}
		
		char first = getChar(searchTarget, 0);
		int max = sourceCount - targetCount;
		
		for (int i = fromIndex; i <= max; i++)
		{
			/* Look for first character. */
			if (!ceq.charsEqual(getChar(array, i), first))
			{
				while (++i <= max && !ceq.charsEqual(getChar(array, i), first));
			}
			
			/* Found first character, now look at the rest of v2 */
			if (i <= max)
			{
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = 1; j < end && ceq.charsEqual(getChar(array, j), getChar(searchTarget, k)); j++, k++);
				
				if (j == end)
				{
					/* Found whole string. */
					return i;
				}
			}
		}
		
		return -1;
	}
	
	
	
	
	
	
	
	public static int indexOfSubarrayShort(Slice<short[]> array, Slice<short[]> searchTarget)
	{
		return indexOfSubarrayShort(array, searchTarget, 0);
	}
	
	public static int indexOfSubarrayShort(Slice<short[]> array, Slice<short[]> searchTarget, int fromIndex)
	{
		return indexOfSubarrayShort(array, searchTarget, fromIndex, (a, b) -> a == b);
	}
	
	public static int indexOfSubarrayShort(Slice<short[]> array, Slice<short[]> searchTarget, int fromIndex, ShortEqualityComparator ceq)
	{
		//Copied and altered from String.java
		
		int sourceCount = array.getLength();
		int targetCount = searchTarget.getLength();
		
		if (fromIndex >= sourceCount)
		{
			return (targetCount == 0 ? sourceCount : -1);
		}
		
		if (fromIndex < 0)
		{
			fromIndex = 0;
		}
		
		if (targetCount == 0)
		{
			return fromIndex;
		}
		
		short first = getShort(searchTarget, 0);
		int max = sourceCount - targetCount;
		
		for (int i = fromIndex; i <= max; i++)
		{
			/* Look for first short. */
			if (!ceq.shortsEqual(getShort(array, i), first))
			{
				while (++i <= max && !ceq.shortsEqual(getShort(array, i), first));
			}
			
			/* Found first short, now look at the rest of v2 */
			if (i <= max)
			{
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = 1; j < end && ceq.shortsEqual(getShort(array, j), getShort(searchTarget, k)); j++, k++);
				
				if (j == end)
				{
					/* Found whole string. */
					return i;
				}
			}
		}
		
		return -1;
	}
	
	
	
	
	
	
	
	public static int indexOfSubarrayFloat(Slice<float[]> array, Slice<float[]> searchTarget)
	{
		return indexOfSubarrayFloat(array, searchTarget, 0);
	}
	
	public static int indexOfSubarrayFloat(Slice<float[]> array, Slice<float[]> searchTarget, int fromIndex)
	{
		return indexOfSubarrayFloat(array, searchTarget, fromIndex, (a, b) -> a == b);
	}
	
	public static int indexOfSubarrayFloat(Slice<float[]> array, Slice<float[]> searchTarget, int fromIndex, FloatEqualityComparator ceq)
	{
		//Copied and altered from String.java
		
		int sourceCount = array.getLength();
		int targetCount = searchTarget.getLength();
		
		if (fromIndex >= sourceCount)
		{
			return (targetCount == 0 ? sourceCount : -1);
		}
		
		if (fromIndex < 0)
		{
			fromIndex = 0;
		}
		
		if (targetCount == 0)
		{
			return fromIndex;
		}
		
		float first = getFloat(searchTarget, 0);
		int max = sourceCount - targetCount;
		
		for (int i = fromIndex; i <= max; i++)
		{
			/* Look for first float. */
			if (!ceq.floatsEqual(getFloat(array, i), first))
			{
				while (++i <= max && !ceq.floatsEqual(getFloat(array, i), first));
			}
			
			/* Found first float, now look at the rest of v2 */
			if (i <= max)
			{
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = 1; j < end && ceq.floatsEqual(getFloat(array, j), getFloat(searchTarget, k)); j++, k++);
				
				if (j == end)
				{
					/* Found whole string. */
					return i;
				}
			}
		}
		
		return -1;
	}
	
	
	
	
	
	
	
	public static int indexOfSubarrayInt(Slice<int[]> array, Slice<int[]> searchTarget)
	{
		return indexOfSubarrayInt(array, searchTarget, 0);
	}
	
	public static int indexOfSubarrayInt(Slice<int[]> array, Slice<int[]> searchTarget, int fromIndex)
	{
		return indexOfSubarrayInt(array, searchTarget, fromIndex, (a, b) -> a == b);
	}
	
	public static int indexOfSubarrayInt(Slice<int[]> array, Slice<int[]> searchTarget, int fromIndex, IntEqualityComparator ceq)
	{
		//Copied and altered from String.java
		
		int sourceCount = array.getLength();
		int targetCount = searchTarget.getLength();
		
		if (fromIndex >= sourceCount)
		{
			return (targetCount == 0 ? sourceCount : -1);
		}
		
		if (fromIndex < 0)
		{
			fromIndex = 0;
		}
		
		if (targetCount == 0)
		{
			return fromIndex;
		}
		
		int first = getInt(searchTarget, 0);
		int max = sourceCount - targetCount;
		
		for (int i = fromIndex; i <= max; i++)
		{
			/* Look for first integer. */
			if (!ceq.intsEqual(getInt(array, i), first))
			{
				while (++i <= max && !ceq.intsEqual(getInt(array, i), first));
			}
			
			/* Found first integer, now look at the rest of v2 */
			if (i <= max)
			{
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = 1; j < end && ceq.intsEqual(getInt(array, j), getInt(searchTarget, k)); j++, k++);
				
				if (j == end)
				{
					/* Found whole string. */
					return i;
				}
			}
		}
		
		return -1;
	}
	
	
	
	
	
	
	
	public static int indexOfSubarrayDouble(Slice<double[]> array, Slice<double[]> searchTarget)
	{
		return indexOfSubarrayDouble(array, searchTarget, 0);
	}
	
	public static int indexOfSubarrayDouble(Slice<double[]> array, Slice<double[]> searchTarget, int fromIndex)
	{
		return indexOfSubarrayDouble(array, searchTarget, fromIndex, (a, b) -> a == b);
	}
	
	public static int indexOfSubarrayDouble(Slice<double[]> array, Slice<double[]> searchTarget, int fromIndex, DoubleEqualityComparator ceq)
	{
		//Copied and altered from String.java
		
		int sourceCount = array.getLength();
		int targetCount = searchTarget.getLength();
		
		if (fromIndex >= sourceCount)
		{
			return (targetCount == 0 ? sourceCount : -1);
		}
		
		if (fromIndex < 0)
		{
			fromIndex = 0;
		}
		
		if (targetCount == 0)
		{
			return fromIndex;
		}
		
		double first = getDouble(searchTarget, 0);
		int max = sourceCount - targetCount;
		
		for (int i = fromIndex; i <= max; i++)
		{
			/* Look for first double. */
			if (!ceq.doublesEqual(getDouble(array, i), first))
			{
				while (++i <= max && !ceq.doublesEqual(getDouble(array, i), first));
			}
			
			/* Found first double, now look at the rest of v2 */
			if (i <= max)
			{
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = 1; j < end && ceq.doublesEqual(getDouble(array, j), getDouble(searchTarget, k)); j++, k++);
				
				if (j == end)
				{
					/* Found whole string. */
					return i;
				}
			}
		}
		
		return -1;
	}
	
	
	
	
	
	
	
	public static int indexOfSubarrayLong(Slice<long[]> array, Slice<long[]> searchTarget)
	{
		return indexOfSubarrayLong(array, searchTarget, 0);
	}
	
	public static int indexOfSubarrayLong(Slice<long[]> array, Slice<long[]> searchTarget, int fromIndex)
	{
		return indexOfSubarrayLong(array, searchTarget, fromIndex, (a, b) -> a == b);
	}
	
	public static int indexOfSubarrayLong(Slice<long[]> array, Slice<long[]> searchTarget, int fromIndex, LongEqualityComparator ceq)
	{
		//Copied and altered from String.java
		
		int sourceCount = array.getLength();
		int targetCount = searchTarget.getLength();
		
		if (fromIndex >= sourceCount)
		{
			return (targetCount == 0 ? sourceCount : -1);
		}
		
		if (fromIndex < 0)
		{
			fromIndex = 0;
		}
		
		if (targetCount == 0)
		{
			return fromIndex;
		}
		
		long first = getLong(searchTarget, 0);
		int max = sourceCount - targetCount;
		
		for (int i = fromIndex; i <= max; i++)
		{
			/* Look for first long. */
			if (!ceq.longsEqual(getLong(array, i), first))
			{
				while (++i <= max && !ceq.longsEqual(getLong(array, i), first));
			}
			
			/* Found first long, now look at the rest of v2 */
			if (i <= max)
			{
				int j = i + 1;
				int end = j + targetCount - 1;
				for (int k = 1; j < end && ceq.longsEqual(getLong(array, j), getLong(searchTarget, k)); j++, k++);
				
				if (j == end)
				{
					/* Found whole string. */
					return i;
				}
			}
		}
		
		return -1;
	}
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static <E> E[] closeSequence(E[] array)
	{
		if (array.length == 0)
			throw new IllegalArgumentException();
		
		int n = array.length;
		E[] newa = Arrays.copyOf(array, n + 1);
		newa[n] = array[0];
		
		return newa;
	}
	
	
	
	
	
	
	public static byte[] getOrCreateBuffer(@Nullable ObjectContainer<byte[]> cache, int minSize)
	{
		if (cache == null)
			return new byte[minSize];
		else
		{
			byte[] v = cache.get();
			
			if (v == null || v.length < minSize)
			{
				v = new byte[minSize];
				cache.set(v);
			}
			
			return v;
		}
	}
	
	
	public static byte[] getOrGrowBuffer(@Nonnull ObjectContainer<byte[]> holder, int minSize)
	{
		byte[] v = holder.get();
		
		if (v == null || v.length < minSize)
		{
			v = Arrays.copyOf(v, minSize);
			holder.set(v);
		}
		
		return v;
	}
}
