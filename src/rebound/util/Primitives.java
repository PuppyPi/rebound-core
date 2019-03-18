/*
 * Created on Jan 18, 2005
 * 	by the wonderful Eclipse(c)
 */
package rebound.util;

import static rebound.bits.BitUtilities.*;
import static rebound.bits.Unsigned.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import rebound.annotations.semantic.simpledata.ActuallyUnsignedValue;
import rebound.bits.Unsigned;
import rebound.exceptions.StructuredClassCastException;
import rebound.util.objectutil.BasicObjectUtilities;
import rebound.util.objectutil.JavaNamespace;

/**
 * This provides many utilities for primitive-related things.
 * (including primitive-typed arrays :> )
 * 
 * @author sean
 */
@SuppressWarnings("cast")
public class Primitives
implements JavaNamespace
{
	public static final int JSIZEOF_BYTES_BYTE = 1;
	public static final int JSIZEOF_BYTES_CHAR = 2;
	public static final int JSIZEOF_BYTES_SHORT = 2;
	public static final int JSIZEOF_BYTES_FLOAT = 4;
	public static final int JSIZEOF_BYTES_INT = 4;
	public static final int JSIZEOF_BYTES_DOUBLE = 8;
	public static final int JSIZEOF_BYTES_LONG = 8;
	
	public static final int JSIZEOF_BITS_BYTE = 8;
	public static final int JSIZEOF_BITS_CHAR = 16;
	public static final int JSIZEOF_BITS_SHORT = 16;
	public static final int JSIZEOF_BITS_FLOAT = 32;
	public static final int JSIZEOF_BITS_INT = 32;
	public static final int JSIZEOF_BITS_DOUBLE = 64;
	public static final int JSIZEOF_BITS_LONG = 64;
	
	
	public static final List<Class> Primitive_Types = Collections.unmodifiableList(Arrays.asList(new Class[]{boolean.class, byte.class, char.class, short.class, float.class, int.class, double.class, long.class}));
	public static final List<Class> Primitive_Wrapper_Types = Collections.unmodifiableList(Arrays.asList(new Class[]{Boolean.class, Byte.class, Character.class, Short.class, Float.class, Integer.class, Double.class, Long.class}));
	
	
	
	/**
	 * Like a == b, except that if they're both NaN, then it's true, not false.
	 * Ie,
	 * 		NaN == NaN -> False
	 * 		eqSane(NaN, NaN) -> True
	 * 
	 * ..yeah..the one little horrible thing in IEEE754..which breaks math X'D
	 */
	public static boolean eqSane(float a, float b)
	{
		//if a is not NaN and b is NaN, then it will return false, as it's 'upposeds to ^^
		//return Float.isNaN(a) ? Float.isNaN(b) : a == b;
		return a == b || (a != a && b != b);  //manually inlined :>
	}
	
	/**
	 * Like a == b, except that if they're both NaN, then it's true, not false.
	 * Ie,
	 * 		NaN == NaN -> False
	 * 		eqSane(NaN, NaN) -> True
	 * 
	 * ..yeah..the one little horrible thing in IEEE754..which breaks math X'D
	 */
	public static boolean eqSane(double a, double b)
	{
		//if a is not NaN and b is NaN, then it will return false, as it's 'upposeds to ^^
		//return Double.isNaN(a) ? Double.isNaN(b) : a == b;
		return a == b || (a != a && b != b);  //manually inlined :>
	}
	
	
	
	public static boolean eqSane(Float a, Float b)
	{
		//This is like Float.equals in that NaN == NaN, but unlike it in that +0.0 == -0.0   (if it's not one quirk, it's another! XD )
		if (a == null)
			return b == null;
		else if (b == null)
			return false;
		else
			return eqSane(a.floatValue(), b.floatValue());
	}
	
	public static boolean eqSane(Double a, Double b)
	{
		if (a == null)
			return b == null;
		else if (b == null)
			return false;
		else
			return eqSane(a.doubleValue(), b.doubleValue());
	}
	
	
	
	
	
	
	
	
	
	//Nevermind, doubles a == b works differently than Double.equals()  heavens, what does that do to hashtables and autoboxing?!?!  X"DD
	//eqSane == eqMath already!! \:DDD/
	//	/**
	//	 * Just like {@link #eqSane(float, float)}, except -0 == +0  X""DD
	//	 */
	//	public static boolean eqMath(float a, float b)
	//	{
	//		return a == b || (a != a && b != b) || ((a == 0f || a == -0f) && (b == 0f || b == -0f));
	//		//return a == b || (a != a && b != b) || ((a == 0f && b == -0f) || (a == -0f && b == 0f));
	//	}
	//
	//	/**
	//	 * Just like {@link #eqSane(double, double)}, except -0 == +0  X""DD
	//	 */
	//	public static boolean eqMath(double a, double b)
	//	{
	//		return a == b || (a != a && b != b) || ((a == 0d || a == -0d) && (b == 0d || b == -0d));
	//		//return a == b || (a != a && b != b) || ((a == 0d && b == -0d) || (a == -0d && b == 0d));
	//	}
	//
	//
	//
	//	public static boolean eqMath(Float a, Float b)
	//	{
	//		//This is like Float.equals in that NaN == NaN, but unlike it in that +0.0 == -0.0   (if it's not one quirk, it's another! XD )
	//		if (a == null)
	//			return b == null;
	//		else if (b == null)
	//			return false;
	//		else
	//			return eqMath(a.floatValue(), b.floatValue());
	//	}
	//
	//	public static boolean eqMath(Double a, Double b)
	//	{
	//		if (a == null)
	//			return b == null;
	//		else if (b == null)
	//			return false;
	//		else
	//			return eqMath(a.doubleValue(), b.doubleValue());
	//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//Others, for use with primitive expanders ^_~
	
	/*
	public static boolean eqSane(_$$prim$$_ a, _$$prim$$_ b)
	{
		return a == b;
	}
	 */
	
	public static boolean eqSane(boolean a, boolean b)
	{
		return a == b;
	}
	
	public static boolean eqSane(byte a, byte b)
	{
		return a == b;
	}
	
	public static boolean eqSane(char a, char b)
	{
		return a == b;
	}
	
	public static boolean eqSane(short a, short b)
	{
		return a == b;
	}
	
	public static boolean eqSane(int a, int b)
	{
		return a == b;
	}
	
	public static boolean eqSane(long a, long b)
	{
		return a == b;
	}
	
	
	
	
	
	
	
	
	
	
	//<Null-tolerant binary-logic testing
	
	//<TRINARY LOGIC \o/
	public static boolean isTrueAndNotNull(Object x)
	{
		return x != null && x instanceof Boolean && (Boolean)x;
	}
	public static boolean isFalseAndNotNull(Object x)
	{
		return x != null && x instanceof Boolean && !((Boolean)x);
	}
	
	
	public static boolean isTrueOrNull(Boolean x)
	{
		return x == null || x;
	}
	
	public static boolean isFalseOrNull(Boolean x)
	{
		return x == null || !x;
	}
	
	
	public static boolean falseIfNullOrPass(Boolean x)
	{
		//return x == null ? false : x;
		return x != null && x;
	}
	
	public static boolean trueIfNullOrPass(Boolean x)
	{
		//return x == null ? true : x;
		return x == null || x;
	}
	
	
	
	public static boolean eq(Boolean a, Boolean b)
	{
		return BasicObjectUtilities.eq(a, b);
	}
	
	public static boolean eq(Boolean a, boolean b)
	{
		return a != null && a.booleanValue() == b;
	}
	
	public static boolean eq(boolean a, Boolean b)
	{
		return b != null && b.booleanValue() == a;
	}
	
	public static boolean eq(boolean a, boolean b)
	{
		return a == b;
	}
	//TRINARY LOGIC \o/ >
	
	
	
	public static boolean eq(Character a, Character b)
	{
		return BasicObjectUtilities.eq(a, b);
	}
	
	public static boolean eq(Character a, char b)
	{
		return a != null && a.charValue() == b;
	}
	
	public static boolean eq(char a, Character b)
	{
		return b != null && b.charValue() == a;
	}
	
	public static boolean eq(char a, char b)
	{
		return a == b;
	}
	
	
	
	public static boolean eq(Byte a, Byte b)
	{
		return BasicObjectUtilities.eq(a, b);
	}
	
	public static boolean eq(Byte a, byte b)
	{
		return a != null && a.byteValue() == b;
	}
	
	public static boolean eq(byte a, Byte b)
	{
		return b != null && b.byteValue() == a;
	}
	
	public static boolean eq(byte a, byte b)
	{
		return a == b;
	}
	
	
	
	public static boolean eq(Short a, Short b)
	{
		return BasicObjectUtilities.eq(a, b);
	}
	
	public static boolean eq(Short a, short b)
	{
		return a != null && a.shortValue() == b;
	}
	
	public static boolean eq(short a, Short b)
	{
		return b != null && b.shortValue() == a;
	}
	
	public static boolean eq(short a, short b)
	{
		return a == b;
	}
	
	
	
	public static boolean eq(Integer a, Integer b)
	{
		return BasicObjectUtilities.eq(a, b);
	}
	
	public static boolean eq(Integer a, int b)
	{
		return a != null && a.intValue() == b;
	}
	
	public static boolean eq(int a, Integer b)
	{
		return b != null && b.intValue() == a;
	}
	
	public static boolean eq(int a, int b)
	{
		return a == b;
	}
	
	
	
	public static boolean eq(Long a, Long b)
	{
		return BasicObjectUtilities.eq(a, b);
	}
	
	public static boolean eq(Long a, long b)
	{
		return a != null && a.longValue() == b;
	}
	
	public static boolean eq(long a, Long b)
	{
		return b != null && b.longValue() == a;
	}
	
	public static boolean eq(long a, long b)
	{
		return a == b;
	}
	
	
	
	public static boolean eq(Float a, Float b)
	{
		return BasicObjectUtilities.eq(a, b);
	}
	
	public static boolean eq(Float a, float b)
	{
		return a != null && a.floatValue() == b;
	}
	
	public static boolean eq(float a, Float b)
	{
		return b != null && b.floatValue() == a;
	}
	
	public static boolean eq(float a, float b)
	{
		return a == b;
	}
	
	
	
	public static boolean eq(Double a, Double b)
	{
		return BasicObjectUtilities.eq(a, b);
	}
	
	public static boolean eq(Double a, double b)
	{
		return a != null && a.doubleValue() == b;
	}
	
	public static boolean eq(double a, Double b)
	{
		return b != null && b.doubleValue() == a;
	}
	
	public static boolean eq(double a, double b)
	{
		return a == b;
	}
	//Null-tolerant binary-logic testing>
	
	
	
	
	
	//Todo fuzzy trinary logic (ie, any nulls yield a null response, which is of type java.lang.Boolean)
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	public static _$$prim$$_ unboxNT(_$$Primitive$$_ wrappedValue, _$$prim$$_ nullValue)
	{
		return wrappedValue == null ? nullValue : wrappedValue._$$prim$$_Value();
	}
	
	 */
	
	public static boolean unboxNT(Boolean wrappedValue, boolean nullValue)
	{
		return wrappedValue == null ? nullValue : wrappedValue.booleanValue();
	}
	
	public static byte unboxNT(Byte wrappedValue, byte nullValue)
	{
		return wrappedValue == null ? nullValue : wrappedValue.byteValue();
	}
	
	public static short unboxNT(Short wrappedValue, short nullValue)
	{
		return wrappedValue == null ? nullValue : wrappedValue.shortValue();
	}
	
	public static char unboxNT(Character wrappedValue, char nullValue)
	{
		return wrappedValue == null ? nullValue : wrappedValue.charValue();
	}
	
	public static int unboxNT(Integer wrappedValue, int nullValue)
	{
		return wrappedValue == null ? nullValue : wrappedValue.intValue();
	}
	
	public static float unboxNT(Float wrappedValue, float nullValue)
	{
		return wrappedValue == null ? nullValue : wrappedValue.floatValue();
	}
	
	public static long unboxNT(Long wrappedValue, long nullValue)
	{
		return wrappedValue == null ? nullValue : wrappedValue.longValue();
	}
	
	public static double unboxNT(Double wrappedValue, double nullValue)
	{
		return wrappedValue == null ? nullValue : wrappedValue.doubleValue();
	}
	
	
	
	
	
	
	
	
	
	
	public static <E> E[] box(Object primitiveArray)
	{
		/*
		else if (primitiveArray instanceof _$$prim$$_[])
			return (E[])box((_$$prim$$_[])primitiveArray);
		 */
		
		if (primitiveArray == null)
			return null;
		
		else if (primitiveArray instanceof boolean[])
			return (E[])box((boolean[])primitiveArray);
		else if (primitiveArray instanceof byte[])
			return (E[])box((byte[])primitiveArray);
		else if (primitiveArray instanceof char[])
			return (E[])box((char[])primitiveArray);
		else if (primitiveArray instanceof short[])
			return (E[])box((short[])primitiveArray);
		else if (primitiveArray instanceof float[])
			return (E[])box((float[])primitiveArray);
		else if (primitiveArray instanceof int[])
			return (E[])box((int[])primitiveArray);
		else if (primitiveArray instanceof double[])
			return (E[])box((double[])primitiveArray);
		else if (primitiveArray instanceof long[])
			return (E[])box((long[])primitiveArray);
		
		else
			throw new StructuredClassCastException(primitiveArray.getClass());
	}
	
	
	public static void boxInto(Object primitiveArray, Object[] destWrapperArray)
	{
		/*
		else if (primitiveArray instanceof _$$prim$$_[])
			boxInto((_$$prim$$_[])primitiveArray, destWrapperArray);
		 */
		
		if (primitiveArray instanceof boolean[])
			boxInto((boolean[])primitiveArray, destWrapperArray);
		else if (primitiveArray instanceof byte[])
			boxInto((byte[])primitiveArray, destWrapperArray);
		else if (primitiveArray instanceof char[])
			boxInto((char[])primitiveArray, destWrapperArray);
		else if (primitiveArray instanceof short[])
			boxInto((short[])primitiveArray, destWrapperArray);
		else if (primitiveArray instanceof float[])
			boxInto((float[])primitiveArray, destWrapperArray);
		else if (primitiveArray instanceof int[])
			boxInto((int[])primitiveArray, destWrapperArray);
		else if (primitiveArray instanceof double[])
			boxInto((double[])primitiveArray, destWrapperArray);
		else if (primitiveArray instanceof long[])
			boxInto((long[])primitiveArray, destWrapperArray);
		
		else
			throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(primitiveArray);
	}
	
	
	
	public static Object unbox(Object[] wrapperArray)
	{
		/*
		else if (wrapperArray instanceof _$$Primitive$$_[])
			return unbox((_$$Primitive$$_[])wrapperArray);
		 */
		
		if (wrapperArray == null)
			return null;
		
		else if (wrapperArray instanceof Boolean[])
			return unbox((Boolean[])wrapperArray);
		else if (wrapperArray instanceof Byte[])
			return unbox((Byte[])wrapperArray);
		else if (wrapperArray instanceof Character[])
			return unbox((Character[])wrapperArray);
		else if (wrapperArray instanceof Short[])
			return unbox((Short[])wrapperArray);
		else if (wrapperArray instanceof Float[])
			return unbox((Float[])wrapperArray);
		else if (wrapperArray instanceof Integer[])
			return unbox((Integer[])wrapperArray);
		else if (wrapperArray instanceof Double[])
			return unbox((Double[])wrapperArray);
		else if (wrapperArray instanceof Long[])
			return unbox((Long[])wrapperArray);
		
		else
			throw new StructuredClassCastException(wrapperArray.getClass());
	}
	
	public static Object unboxNT(Object[] wrapperArray, Object nullValue)
	{
		/*
		else if (wrapperArray instanceof _$$Primitive$$_[])
			return unboxNT((_$$Primitive$$_[])wrapperArray, (_$$Primitive$$_)nullValue);
		 */
		
		if (wrapperArray == null)
			return null;
		
		else if (wrapperArray instanceof Boolean[])
			return unboxNT(wrapperArray, nullValue);
		else if (wrapperArray instanceof Byte[])
			return unboxNT(wrapperArray, nullValue);
		else if (wrapperArray instanceof Character[])
			return unboxNT(wrapperArray, nullValue);
		else if (wrapperArray instanceof Short[])
			return unboxNT(wrapperArray, nullValue);
		else if (wrapperArray instanceof Float[])
			return unboxNT(wrapperArray, nullValue);
		else if (wrapperArray instanceof Integer[])
			return unboxNT(wrapperArray, nullValue);
		else if (wrapperArray instanceof Double[])
			return unboxNT(wrapperArray, nullValue);
		else if (wrapperArray instanceof Long[])
			return unboxNT(wrapperArray, nullValue);
		
		else
			throw new StructuredClassCastException(wrapperArray.getClass());
	}
	
	
	
	
	
	
	public static Object getBoxing(Object array, int index)
	{
		/*
		else if (array instanceof _$$prim$$_[])
			return ((_$$prim$$_[])array)[index];
		 */
		
		if (array instanceof boolean[])
			return ((boolean[])array)[index];
		else if (array instanceof byte[])
			return ((byte[])array)[index];
		else if (array instanceof char[])
			return ((char[])array)[index];
		else if (array instanceof short[])
			return ((short[])array)[index];
		else if (array instanceof float[])
			return ((float[])array)[index];
		else if (array instanceof int[])
			return ((int[])array)[index];
		else if (array instanceof double[])
			return ((double[])array)[index];
		else if (array instanceof long[])
			return ((long[])array)[index];
		else
			throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(array);
	}
	
	/**
	 * Note!  Does not cast!  ><
	 * (value must be in correct wrapper type; sorry!)
	 */
	public static void setBoxing(Object array, int index, Object value)
	{
		/*
		else if (array instanceof _$$prim$$_[])
			((_$$prim$$_[])array)[index] = (_$$Primitive$$_)value;
		 */
		
		if (array instanceof boolean[])
			((boolean[])array)[index] = (Boolean)value;
		else if (array instanceof byte[])
			((byte[])array)[index] = (Byte)value;
		else if (array instanceof char[])
			((char[])array)[index] = (Character)value;
		else if (array instanceof short[])
			((short[])array)[index] = (Short)value;
		else if (array instanceof float[])
			((float[])array)[index] = (Float)value;
		else if (array instanceof int[])
			((int[])array)[index] = (Integer)value;
		else if (array instanceof double[])
			((double[])array)[index] = (Double)value;
		else if (array instanceof long[])
			((long[])array)[index] = (Long)value;
		else
			throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(array);
	}
	
	
	
	
	
	/*
	public static _$$Primitive$$_[] box(_$$prim$$_[] src)
	{
		int srclen = src.length;
		_$$Primitive$$_[] rv = new _$$Primitive$$_[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = src[i];
		return rv;
	}
	
	public static void boxInto(_$$prim$$_[] src, Object[] dest)
	{
		int srclen = src.length;
		for (int i = 0; i < srclen; i++)
			dest[i] = src[i];
	}
	
	public static _$$prim$$_[] unbox(_$$Primitive$$_[] src)
	{
		int srclen = src.length;
		_$$prim$$_[] rv = new _$$prim$$_[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = src[i]._$$prim$$_Value();  //will throw the NullPointerException if null :>
		return rv;
	}
	
	public static _$$prim$$_[] unboxNT(_$$Primitive$$_[] src, _$$prim$$_ nullValue)
	{
		int srclen = src.length;
		_$$prim$$_[] rv = new _$$prim$$_[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = unboxNT(src[i], nullValue);
		return rv;
	}
	
	
	
	
	
	 */
	
	public static Boolean[] box(boolean[] src)
	{
		int srclen = src.length;
		Boolean[] rv = new Boolean[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = src[i];
		return rv;
	}
	
	public static void boxInto(boolean[] src, Object[] dest)
	{
		int srclen = src.length;
		for (int i = 0; i < srclen; i++)
			dest[i] = src[i];
	}
	
	public static boolean[] unbox(Boolean[] src)
	{
		int srclen = src.length;
		boolean[] rv = new boolean[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = src[i].booleanValue();  //will throw the NullPointerException if null :>
		return rv;
	}
	
	public static boolean[] unboxNT(Boolean[] src, boolean nullValue)
	{
		int srclen = src.length;
		boolean[] rv = new boolean[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = unboxNT(src[i], nullValue);
		return rv;
	}
	
	
	
	
	
	public static Byte[] box(byte[] src)
	{
		int srclen = src.length;
		Byte[] rv = new Byte[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = src[i];
		return rv;
	}
	
	public static void boxInto(byte[] src, Object[] dest)
	{
		int srclen = src.length;
		for (int i = 0; i < srclen; i++)
			dest[i] = src[i];
	}
	
	public static byte[] unbox(Byte[] src)
	{
		int srclen = src.length;
		byte[] rv = new byte[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = src[i].byteValue();  //will throw the NullPointerException if null :>
		return rv;
	}
	
	public static byte[] unboxNT(Byte[] src, byte nullValue)
	{
		int srclen = src.length;
		byte[] rv = new byte[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = unboxNT(src[i], nullValue);
		return rv;
	}
	
	
	
	
	
	public static Character[] box(char[] src)
	{
		int srclen = src.length;
		Character[] rv = new Character[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = src[i];
		return rv;
	}
	
	public static void boxInto(char[] src, Object[] dest)
	{
		int srclen = src.length;
		for (int i = 0; i < srclen; i++)
			dest[i] = src[i];
	}
	
	public static char[] unbox(Character[] src)
	{
		int srclen = src.length;
		char[] rv = new char[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = src[i].charValue();  //will throw the NullPointerException if null :>
		return rv;
	}
	
	public static char[] unboxNT(Character[] src, char nullValue)
	{
		int srclen = src.length;
		char[] rv = new char[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = unboxNT(src[i], nullValue);
		return rv;
	}
	
	
	
	
	
	public static Short[] box(short[] src)
	{
		int srclen = src.length;
		Short[] rv = new Short[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = src[i];
		return rv;
	}
	
	public static void boxInto(short[] src, Object[] dest)
	{
		int srclen = src.length;
		for (int i = 0; i < srclen; i++)
			dest[i] = src[i];
	}
	
	public static short[] unbox(Short[] src)
	{
		int srclen = src.length;
		short[] rv = new short[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = src[i].shortValue();  //will throw the NullPointerException if null :>
		return rv;
	}
	
	public static short[] unboxNT(Short[] src, short nullValue)
	{
		int srclen = src.length;
		short[] rv = new short[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = unboxNT(src[i], nullValue);
		return rv;
	}
	
	
	
	
	
	public static Float[] box(float[] src)
	{
		int srclen = src.length;
		Float[] rv = new Float[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = src[i];
		return rv;
	}
	
	public static void boxInto(float[] src, Object[] dest)
	{
		int srclen = src.length;
		for (int i = 0; i < srclen; i++)
			dest[i] = src[i];
	}
	
	public static float[] unbox(Float[] src)
	{
		int srclen = src.length;
		float[] rv = new float[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = src[i].floatValue();  //will throw the NullPointerException if null :>
		return rv;
	}
	
	public static float[] unboxNT(Float[] src, float nullValue)
	{
		int srclen = src.length;
		float[] rv = new float[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = unboxNT(src[i], nullValue);
		return rv;
	}
	
	
	
	
	
	public static Integer[] box(int[] src)
	{
		int srclen = src.length;
		Integer[] rv = new Integer[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = src[i];
		return rv;
	}
	
	public static void boxInto(int[] src, Object[] dest)
	{
		int srclen = src.length;
		for (int i = 0; i < srclen; i++)
			dest[i] = src[i];
	}
	
	public static int[] unbox(Integer[] src)
	{
		int srclen = src.length;
		int[] rv = new int[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = src[i].intValue();  //will throw the NullPointerException if null :>
		return rv;
	}
	
	public static int[] unboxNT(Integer[] src, int nullValue)
	{
		int srclen = src.length;
		int[] rv = new int[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = unboxNT(src[i], nullValue);
		return rv;
	}
	
	
	
	
	
	public static Double[] box(double[] src)
	{
		int srclen = src.length;
		Double[] rv = new Double[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = src[i];
		return rv;
	}
	
	public static void boxInto(double[] src, Object[] dest)
	{
		int srclen = src.length;
		for (int i = 0; i < srclen; i++)
			dest[i] = src[i];
	}
	
	public static double[] unbox(Double[] src)
	{
		int srclen = src.length;
		double[] rv = new double[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = src[i].doubleValue();  //will throw the NullPointerException if null :>
		return rv;
	}
	
	public static double[] unboxNT(Double[] src, double nullValue)
	{
		int srclen = src.length;
		double[] rv = new double[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = unboxNT(src[i], nullValue);
		return rv;
	}
	
	
	
	
	
	public static Long[] box(long[] src)
	{
		int srclen = src.length;
		Long[] rv = new Long[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = src[i];
		return rv;
	}
	
	public static void boxInto(long[] src, Object[] dest)
	{
		int srclen = src.length;
		for (int i = 0; i < srclen; i++)
			dest[i] = src[i];
	}
	
	public static long[] unbox(Long[] src)
	{
		int srclen = src.length;
		long[] rv = new long[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = src[i].longValue();  //will throw the NullPointerException if null :>
		return rv;
	}
	
	public static long[] unboxNT(Long[] src, long nullValue)
	{
		int srclen = src.length;
		long[] rv = new long[srclen];
		for (int i = 0; i < srclen; i++)
			rv[i] = unboxNT(src[i], nullValue);
		return rv;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static List boxList(Object wrappedArray)
	{
		return Arrays.asList(box(wrappedArray));
	}
	
	//	@Deprecated  //just use the correct type--that works even for 0-length arrays, which this is lible to crash your code if given! x"D
	//	/**
	//	 * @return a primitive array :>
	//	 */
	//	public static Object unboxList(List wrappedList) throws NonDuckTypableException
	//	{
	//		if (wrappedList.isEmpty())
	//			throw new NonDuckTypableException();
	//		else
	//		{
	//			Class wrapperClass = wrappedList.get(0).getClass();
	//
	//			/*
	//			else if (wrapperClass == _$$Primitive$$_.class)
	//				return unboxList_$$Primitive$$_(wrappedList);
	//			 */
	//			if (wrapperClass == Boolean.class)
	//				return unboxListBoolean(wrappedList);
	//			else if (wrapperClass == Byte.class)
	//				return unboxListByte(wrappedList);
	//			else if (wrapperClass == Short.class)
	//				return unboxListShort(wrappedList);
	//			else if (wrapperClass == Character.class)
	//				return unboxListCharacter(wrappedList);
	//			else if (wrapperClass == Integer.class)
	//				return unboxListInteger(wrappedList);
	//			else if (wrapperClass == Float.class)
	//				return unboxListFloat(wrappedList);
	//			else if (wrapperClass == Long.class)
	//				return unboxListLong(wrappedList);
	//			else if (wrapperClass == Double.class)
	//				return unboxListDouble(wrappedList);
	//
	//			else
	//				throw new ClassCastException(wrapperClass.getName());
	//		}
	//	}
	
	
	/**
	 * @return a primitive array :>
	 */
	public static Object unboxList(List wrappedList, Class primitiveType)
	{
		Class wrapperClass = getWrapperClassFromPrimitiveOrPassThroughWrapper(primitiveType); //normalizes / handles both primitives and wrappers :>
		
		/*
		else if (wrapperClass == _$$Primitive$$_.class)
			return unboxList_$$Primitive$$_(wrappedList);
		 */
		if (wrapperClass == Boolean.class)
			return unboxListBoolean(wrappedList);
		else if (wrapperClass == Byte.class)
			return unboxListByte(wrappedList);
		else if (wrapperClass == Short.class)
			return unboxListShort(wrappedList);
		else if (wrapperClass == Character.class)
			return unboxListCharacter(wrappedList);
		else if (wrapperClass == Integer.class)
			return unboxListInteger(wrappedList);
		else if (wrapperClass == Float.class)
			return unboxListFloat(wrappedList);
		else if (wrapperClass == Long.class)
			return unboxListLong(wrappedList);
		else if (wrapperClass == Double.class)
			return unboxListDouble(wrappedList);
		
		else
			throw new StructuredClassCastException(wrapperClass);
	}
	
	/**
	 * @param nullValue can't be null of course! XD   (which means it can be relied upon to give the component type! :D )
	 * @return a primitive array :>
	 */
	public static Object unboxListNT(List wrappedList, Object nullValue)
	{
		Class wrapperClass = nullValue.getClass();
		
		/*
		else if (wrapperClass == _$$Primitive$$_.class)
			return unboxList_$$Primitive$$_NT(wrappedList, (_$$Primitive$$_)nullValue);
		 */
		if (wrapperClass == Boolean.class)
			return unboxListBooleanNT(wrappedList, (Boolean)nullValue);
		else if (wrapperClass == Byte.class)
			return unboxListByteNT(wrappedList, (Byte)nullValue);
		else if (wrapperClass == Short.class)
			return unboxListShortNT(wrappedList, (Short)nullValue);
		else if (wrapperClass == Character.class)
			return unboxListCharacterNT(wrappedList, (Character)nullValue);
		else if (wrapperClass == Integer.class)
			return unboxListIntegerNT(wrappedList, (Integer)nullValue);
		else if (wrapperClass == Float.class)
			return unboxListFloatNT(wrappedList, (Float)nullValue);
		else if (wrapperClass == Long.class)
			return unboxListLongNT(wrappedList, (Long)nullValue);
		else if (wrapperClass == Double.class)
			return unboxListDoubleNT(wrappedList, (Double)nullValue);
		
		else
			throw new StructuredClassCastException(wrapperClass);
	}
	
	
	
	
	
	
	
	/*
	public static List_$$<Primitive$$_> boxList(_$$prim$$_[] wrappedArray)
	{
		return Arrays.asList(box(wrappedArray));
	}
	
	public static _$$prim$$_[] unboxList_$$Primitive$$_(List_$$<Primitive$$_> wrappedList)
	{
		return unbox(wrappedList.toArray(new _$$Primitive$$_[wrappedList.size()]));
	}
	
	public static _$$prim$$_[] unboxList_$$Primitive$$_NT(List_$$<Primitive$$_> wrappedList, _$$prim$$_ nullValue)
	{
		return unboxNT(wrappedList.toArray(new _$$Primitive$$_[wrappedList.size()]), nullValue);
	}
	
	
	 */
	
	
	public static List<Boolean> boxList(boolean[] wrappedArray)
	{
		return Arrays.asList(box(wrappedArray));
	}
	
	public static boolean[] unboxListBoolean(List<Boolean> wrappedList)
	{
		return unbox(wrappedList.toArray(new Boolean[wrappedList.size()]));
	}
	
	public static boolean[] unboxListBooleanNT(List<Boolean> wrappedList, boolean nullValue)
	{
		return unboxNT(wrappedList.toArray(new Boolean[wrappedList.size()]), nullValue);
	}
	
	
	public static List<Byte> boxList(byte[] wrappedArray)
	{
		return Arrays.asList(box(wrappedArray));
	}
	
	public static byte[] unboxListByte(List<Byte> wrappedList)
	{
		return unbox(wrappedList.toArray(new Byte[wrappedList.size()]));
	}
	
	public static byte[] unboxListByteNT(List<Byte> wrappedList, byte nullValue)
	{
		return unboxNT(wrappedList.toArray(new Byte[wrappedList.size()]), nullValue);
	}
	
	
	public static List<Short> boxList(short[] wrappedArray)
	{
		return Arrays.asList(box(wrappedArray));
	}
	
	public static short[] unboxListShort(List<Short> wrappedList)
	{
		return unbox(wrappedList.toArray(new Short[wrappedList.size()]));
	}
	
	public static short[] unboxListShortNT(List<Short> wrappedList, short nullValue)
	{
		return unboxNT(wrappedList.toArray(new Short[wrappedList.size()]), nullValue);
	}
	
	
	public static List<Character> boxList(char[] wrappedArray)
	{
		return Arrays.asList(box(wrappedArray));
	}
	
	public static char[] unboxListCharacter(List<Character> wrappedList)
	{
		return unbox(wrappedList.toArray(new Character[wrappedList.size()]));
	}
	
	public static char[] unboxListCharacterNT(List<Character> wrappedList, char nullValue)
	{
		return unboxNT(wrappedList.toArray(new Character[wrappedList.size()]), nullValue);
	}
	
	
	public static List<Integer> boxList(int[] wrappedArray)
	{
		return Arrays.asList(box(wrappedArray));
	}
	
	public static int[] unboxListInteger(List<Integer> wrappedList)
	{
		return unbox(wrappedList.toArray(new Integer[wrappedList.size()]));
	}
	
	public static int[] unboxListIntegerNT(List<Integer> wrappedList, int nullValue)
	{
		return unboxNT(wrappedList.toArray(new Integer[wrappedList.size()]), nullValue);
	}
	
	
	public static List<Float> boxList(float[] wrappedArray)
	{
		return Arrays.asList(box(wrappedArray));
	}
	
	public static float[] unboxListFloat(List<Float> wrappedList)
	{
		return unbox(wrappedList.toArray(new Float[wrappedList.size()]));
	}
	
	public static float[] unboxListFloatNT(List<Float> wrappedList, float nullValue)
	{
		return unboxNT(wrappedList.toArray(new Float[wrappedList.size()]), nullValue);
	}
	
	
	public static List<Long> boxList(long[] wrappedArray)
	{
		return Arrays.asList(box(wrappedArray));
	}
	
	public static long[] unboxListLong(List<Long> wrappedList)
	{
		return unbox(wrappedList.toArray(new Long[wrappedList.size()]));
	}
	
	public static long[] unboxListLongNT(List<Long> wrappedList, long nullValue)
	{
		return unboxNT(wrappedList.toArray(new Long[wrappedList.size()]), nullValue);
	}
	
	
	public static List<Double> boxList(double[] wrappedArray)
	{
		return Arrays.asList(box(wrappedArray));
	}
	
	public static double[] unboxListDouble(List<Double> wrappedList)
	{
		return unbox(wrappedList.toArray(new Double[wrappedList.size()]));
	}
	
	public static double[] unboxListDoubleNT(List<Double> wrappedList, double nullValue)
	{
		return unboxNT(wrappedList.toArray(new Double[wrappedList.size()]), nullValue);
	}
	//Boxing>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<Casting
	
	/* <<<
	python
	
	s = """
	public static OUTPUTTYPE[] castINPUTCAPTYPEArrayToOUTPUTCAPTYPEArray(INPUTTYPE[] src)
	{
		OUTPUTTYPE[] rvs = new OUTPUTTYPE[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (OUTPUTTYPE)src[i];
			i++;
		}
		return rvs;
	}
	
	""";
	
	intermediate = primxp.primxp(prims=primxp.NumPrims, source=s.replace("INPUTTYPE", "_$$prim$$_").replace("INPUTCAPTYPE", "_$$Prim$$_"))
	p(primxp.primxp(prims=primxp.NumPrims, source=intermediate.replace("OUTPUTTYPE", "_$$prim$$_").replace("OUTPUTCAPTYPE", "_$$Prim$$_")))
	 */
	
	public static byte[] castByteArrayToByteArray(byte[] src)
	{
		byte[] rvs = new byte[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static byte[] castCharArrayToByteArray(char[] src)
	{
		byte[] rvs = new byte[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (byte)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static byte[] castShortArrayToByteArray(short[] src)
	{
		byte[] rvs = new byte[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (byte)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static byte[] castFloatArrayToByteArray(float[] src)
	{
		byte[] rvs = new byte[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (byte)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static byte[] castIntArrayToByteArray(int[] src)
	{
		byte[] rvs = new byte[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (byte)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static byte[] castDoubleArrayToByteArray(double[] src)
	{
		byte[] rvs = new byte[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (byte)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static byte[] castLongArrayToByteArray(long[] src)
	{
		byte[] rvs = new byte[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (byte)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static char[] castByteArrayToCharArray(byte[] src)
	{
		char[] rvs = new char[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (char)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static char[] castCharArrayToCharArray(char[] src)
	{
		char[] rvs = new char[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static char[] castShortArrayToCharArray(short[] src)
	{
		char[] rvs = new char[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (char)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static char[] castFloatArrayToCharArray(float[] src)
	{
		char[] rvs = new char[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (char)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static char[] castIntArrayToCharArray(int[] src)
	{
		char[] rvs = new char[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (char)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static char[] castDoubleArrayToCharArray(double[] src)
	{
		char[] rvs = new char[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (char)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static char[] castLongArrayToCharArray(long[] src)
	{
		char[] rvs = new char[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (char)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static short[] castByteArrayToShortArray(byte[] src)
	{
		short[] rvs = new short[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static short[] castCharArrayToShortArray(char[] src)
	{
		short[] rvs = new short[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (short)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static short[] castShortArrayToShortArray(short[] src)
	{
		short[] rvs = new short[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static short[] castFloatArrayToShortArray(float[] src)
	{
		short[] rvs = new short[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (short)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static short[] castIntArrayToShortArray(int[] src)
	{
		short[] rvs = new short[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (short)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static short[] castDoubleArrayToShortArray(double[] src)
	{
		short[] rvs = new short[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (short)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static short[] castLongArrayToShortArray(long[] src)
	{
		short[] rvs = new short[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (short)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static float[] castByteArrayToFloatArray(byte[] src)
	{
		float[] rvs = new float[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static float[] castCharArrayToFloatArray(char[] src)
	{
		float[] rvs = new float[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static float[] castShortArrayToFloatArray(short[] src)
	{
		float[] rvs = new float[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static float[] castFloatArrayToFloatArray(float[] src)
	{
		float[] rvs = new float[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static float[] castIntArrayToFloatArray(int[] src)
	{
		float[] rvs = new float[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static float[] castDoubleArrayToFloatArray(double[] src)
	{
		float[] rvs = new float[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (float)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static float[] castLongArrayToFloatArray(long[] src)
	{
		float[] rvs = new float[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static int[] castByteArrayToIntArray(byte[] src)
	{
		int[] rvs = new int[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static int[] castCharArrayToIntArray(char[] src)
	{
		int[] rvs = new int[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static int[] castShortArrayToIntArray(short[] src)
	{
		int[] rvs = new int[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static int[] castFloatArrayToIntArray(float[] src)
	{
		int[] rvs = new int[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (int)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static int[] castIntArrayToIntArray(int[] src)
	{
		int[] rvs = new int[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static int[] castDoubleArrayToIntArray(double[] src)
	{
		int[] rvs = new int[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (int)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static int[] castLongArrayToIntArray(long[] src)
	{
		int[] rvs = new int[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (int)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static double[] castByteArrayToDoubleArray(byte[] src)
	{
		double[] rvs = new double[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static double[] castCharArrayToDoubleArray(char[] src)
	{
		double[] rvs = new double[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static double[] castShortArrayToDoubleArray(short[] src)
	{
		double[] rvs = new double[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static double[] castFloatArrayToDoubleArray(float[] src)
	{
		double[] rvs = new double[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static double[] castIntArrayToDoubleArray(int[] src)
	{
		double[] rvs = new double[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static double[] castDoubleArrayToDoubleArray(double[] src)
	{
		double[] rvs = new double[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static double[] castLongArrayToDoubleArray(long[] src)
	{
		double[] rvs = new double[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static long[] castByteArrayToLongArray(byte[] src)
	{
		long[] rvs = new long[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static long[] castCharArrayToLongArray(char[] src)
	{
		long[] rvs = new long[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static long[] castShortArrayToLongArray(short[] src)
	{
		long[] rvs = new long[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static long[] castFloatArrayToLongArray(float[] src)
	{
		long[] rvs = new long[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (long)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static long[] castIntArrayToLongArray(int[] src)
	{
		long[] rvs = new long[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static long[] castDoubleArrayToLongArray(double[] src)
	{
		long[] rvs = new long[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = (long)src[i];
			i++;
		}
		return rvs;
	}
	
	
	public static long[] castLongArrayToLongArray(long[] src)
	{
		long[] rvs = new long[src.length];
		int i = 0;
		while (i < src.length)
		{
			rvs[i] = src[i];
			i++;
		}
		return rvs;
	}
	
	
	// >>>
	
	
	
	
	
	
	
	
	public static long castIntegerPrimitiveWrapperToLong(Object x)
	{
		if (x instanceof Byte)
			return (Byte)x;
		else if (x instanceof Character)
			return (Character)x;
		else if (x instanceof Short)
			return (Short)x;
		else if (x instanceof Integer)
			return (Integer)x;
		else if (x instanceof Long)
			return (Long)x;
		else
			throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(x);
	}
	//Casting>
	
	
	
	
	
	
	
	
	
	
	
	
	//<Bitfields/Integers!
	public static long getBitfieldLengthOfType(Class c)
	{
		if (c == byte.class || c == Byte.class) return 8;
		else if (c == short.class || c == Short.class) return 16;
		else if (c == char.class || c == Character.class) return 16;
		else if (c == int.class || c == Integer.class) return 32;
		else if (c == long.class || c == Long.class) return 64;
		else throw new IllegalArgumentException();
	}
	
	public static long getBitfieldLengthOfComponentType(Class c)
	{
		if (c != null && c.isArray()) return getBitfieldLengthOfType(c.getComponentType());
		else throw new IllegalArgumentException(c+" isn't an array Class!");
	}
	
	public static long getIntegerArrayElement(Object array, @ActuallyUnsignedValue long index, boolean signextend)
	{
		int index32 = safeCastU64toS32(index);
		
		//eclipse is confuzzled by my conditional expressions ._.   it makes the indentation unhappies..
		
		if (array instanceof long[])
		{
			return ((long[])array)[index32];
		}
		
		else if (array instanceof int[])
		{
			return signextend ? ((int[])array)[index32] : ((int[])array)[index32] & getMask32(32);
		}
		
		else if (array instanceof char[])
		{
			return signextend ? (short)((char[])array)[index32] : ((char[])array)[index32];
		}
		
		else if (array instanceof short[])
		{
			return signextend ? ((short[])array)[index32] : ((short[])array)[index32] & getMask32(16);
		}
		
		else if (array instanceof byte[])
		{
			return signextend ? ((byte[])array)[index32] : ((byte[])array)[index32] & getMask32(8);
		}
		
		else
			throw new IllegalArgumentException("this is not a bitfield array!: "+array);
	}
	
	public static long getIntegerValue(Object wrappedValue, boolean signextend)
	{
		if (wrappedValue == null)
			throw new NullPointerException();
		else if (!isIntegerPrimitiveOrWrapperClass(wrappedValue.getClass()))
			throw new StructuredClassCastException(wrappedValue.getClass());
		else
		{
			if (signextend)
				return ((Number)wrappedValue).longValue();
			else
				return ((Number)wrappedValue).longValue() & getMask64(getBitfieldLengthOfType(wrappedValue.getClass()));
		}
	}
	
	
	
	
	
	
	
	//All horribly little-endian; TODO big-endian versions??  (bit-endianness >,> )
	
	/*
	 * Bitcasting is conceptually the same as C unions, ie, simply reinterpreting the bits in ram as a different value :>
	 * Notes:
	 * 		+ It's always little-endian (see possible todo)
	 * 		+ Booleans are treated as 1-bit integers :>
	 * 			+ So, for example, 0x2 bitcasts to false ;>
	 * 			+ And signExtension on boolean is (x ? -1 : 0)  ;>
	 * 		+ Floating points are converted using the standard primitive wrapper methods :>
	 * 		+ And don't forget that char is an unsigned short! ;D
	 * 			(not that it matters except in the implementation here :> )
	 */
	
	public static long bitcastToLongUnsigned(boolean x)
	{
		return x ? 1l : 0l;
	}
	
	
	public static long bitcastToLongUnsigned(byte x)
	{
		return (x) & 0xFFl;
	}
	
	
	public static long bitcastToLongUnsigned(char x)
	{
		return x;
	}
	
	public static long bitcastToLongUnsigned(short x)
	{
		return (x) & 0xFFFFl;
	}
	
	
	public static long bitcastToLongUnsigned(float x)
	{
		return (Float.floatToRawIntBits(x)) & 0xFFFFFFFFl;
	}
	
	public static long bitcastToLongUnsigned(int x)
	{
		return (x) & 0xFFFFFFFFl;
	}
	
	
	public static long bitcastToLongUnsigned(double x)
	{
		return Double.doubleToRawLongBits(x);
	}
	
	public static long bitcastToLongUnsigned(long x)
	{
		return x;
	}
	
	
	
	
	
	public static long bitcastToLongTwosSignExtending(boolean x)
	{
		return x ? -1l : 0l;
	}
	
	
	public static long bitcastToLongTwosSignExtending(byte x)
	{
		return x;
	}
	
	
	public static long bitcastToLongTwosSignExtending(char x)
	{
		return ((short)x);
	}
	
	public static long bitcastToLongTwosSignExtending(short x)
	{
		return x;
	}
	
	
	public static long bitcastToLongTwosSignExtending(float x)
	{
		return Float.floatToRawIntBits(x);
	}
	
	public static long bitcastToLongTwosSignExtending(int x)
	{
		return x;
	}
	
	
	public static long bitcastToLongTwosSignExtending(double x)
	{
		return Double.doubleToRawLongBits(x);
	}
	
	public static long bitcastToLongTwosSignExtending(long x)
	{
		return x;
	}
	
	
	
	
	
	
	
	
	public static boolean bitcastFromLongToBoolean(long x)
	{
		return (x & 0x1l) != 0;
	}
	
	public static byte bitcastFromLongToByte(long x)
	{
		return (byte)x;
	}
	
	public static char bitcastFromLongToChar(long x)
	{
		return (char)x;
	}
	
	public static short bitcastFromLongToShort(long x)
	{
		return (short)x;
	}
	
	public static float bitcastFromLongToFloat(long x)
	{
		return Float.intBitsToFloat((int)x);
	}
	
	public static int bitcastFromLongToInt(long x)
	{
		return (int)x;
	}
	
	public static double bitcastFromLongToDouble(long x)
	{
		return Double.longBitsToDouble(x);
	}
	
	public static long bitcastFromLongToLong(long x)
	{
		return x;
	}
	//Bitfields/Integers!>
	
	
	
	
	
	
	
	
	
	
	
	//<Class/type things! :D
	
	public static Class getWrapperClassFromPrimitiveStrict(Class c)
	{
		if (c == boolean.class)
			return Boolean.class;
		else if (c == char.class)
			return Character.class;
		else if (c == byte.class)
			return Byte.class;
		else if (c == short.class)
			return Short.class;
		else if (c == int.class)
			return Integer.class;
		else if (c == long.class)
			return Long.class;
		else if (c == float.class)
			return Float.class;
		else if (c == double.class)
			return Double.class;
		else
			return c;
	}
	
	public static Class getPrimitiveClassFromWrapperStrict(Class c) throws IllegalArgumentException
	{
		if (c == Boolean.class)
			return boolean.class;
		else if (c == Character.class)
			return char.class;
		else if (c == Byte.class)
			return byte.class;
		else if (c == Short.class)
			return short.class;
		else if (c == Integer.class)
			return int.class;
		else if (c == Long.class)
			return long.class;
		else if (c == Float.class)
			return float.class;
		else if (c == Double.class)
			return double.class;
		else
			throw new IllegalArgumentException();
	}
	
	
	
	public static Class getPrimitiveClassFromWrapperOrPassThroughPrimitive(Class c)
	{
		if (c == null)
			throw new NullPointerException();
		
		/*
		else if (c == _$$Primitive$$_.class)
			return _$$prim$$_.class;
		 */
		else if (c == Boolean.class)
			return boolean.class;
		else if (c == Byte.class)
			return byte.class;
		else if (c == Short.class)
			return short.class;
		else if (c == Character.class)
			return char.class;
		else if (c == Integer.class)
			return int.class;
		else if (c == Float.class)
			return float.class;
		else if (c == Long.class)
			return long.class;
		else if (c == Double.class)
			return double.class;
		
		
		/*
		 * c == _$$prim$$_.class ||
		 */
		
		else if (c == boolean.class || c == byte.class || c == short.class || c == char.class || c == int.class || c == float.class || c == long.class || c == double.class)
			return c;
		
		
		else
			throw new StructuredClassCastException("Not a primitive or wrapper class >,>", c);
	}
	
	
	public static Class getWrapperClassFromPrimitiveOrPassThroughWrapper(Class c)
	{
		if (c == null)
			throw new NullPointerException();
		
		/*
		else if (c == _$$prim$$_.class)
			return _$$Primitive$$_.class;
		 */
		else if (c == boolean.class)
			return Boolean.class;
		else if (c == byte.class)
			return Byte.class;
		else if (c == short.class)
			return Short.class;
		else if (c == char.class)
			return Character.class;
		else if (c == int.class)
			return Integer.class;
		else if (c == float.class)
			return Float.class;
		else if (c == long.class)
			return Long.class;
		else if (c == double.class)
			return Double.class;
		
		
		/*
		 * c == _$$Primitive$$_.class ||
		 */
		
		else if (c == Boolean.class || c == Byte.class || c == Short.class || c == Character.class || c == Integer.class || c == Float.class || c == Long.class || c == Double.class)
			return c;
		
		
		else
			throw new StructuredClassCastException("Not a primitive or wrapper class >,> : ", c);
	}
	
	
	
	
	
	
	public static boolean isPrimitiveWrapperClass(Class c)
	{
		return
		c == Boolean.class ||
		c == Character.class ||
		c == Byte.class ||
		c == Short.class ||
		c == Integer.class ||
		c == Long.class ||
		c == Float.class ||
		c == Double.class;
	}
	
	public static boolean isNumericPrimitiveWrapperClass(Class c)
	{
		return
		c == Byte.class ||
		c == Short.class ||
		c == Integer.class ||
		c == Long.class ||
		c == Float.class ||
		c == Double.class;
	}
	
	public static boolean isIntegerPrimitiveWrapperClass(Class c)
	{
		return
		c == Byte.class ||
		c == Short.class ||
		c == Character.class || //Note: not "numeric"!
		c == Integer.class ||
		c == Long.class;
	}
	
	public static boolean isFloatingPrimitiveWrapperClass(Class c)
	{
		return
		c == Float.class ||
		c == Double.class;
	}
	
	
	
	
	
	
	public static boolean isPrimitiveClass(Class c)
	{
		return
		c == boolean.class ||
		c == char.class ||
		c == byte.class ||
		c == short.class ||
		c == int.class ||
		c == long.class ||
		c == float.class ||
		c == double.class;
	}
	
	public static boolean isNumericPrimitiveClass(Class c)
	{
		return
		c == byte.class ||
		c == short.class ||
		c == int.class ||
		c == long.class ||
		c == float.class ||
		c == double.class;
	}
	
	public static boolean isIntegerPrimitiveClass(Class c)
	{
		return
		c == byte.class ||
		c == short.class ||
		c == char.class || //Note: not "numeric"!
		c == int.class ||
		c == long.class;
	}
	
	public static boolean isFloatingPrimitiveClass(Class c)
	{
		return
		c == float.class ||
		c == double.class;
	}
	
	
	
	
	public static boolean isPrimitiveOrWrapperClass(Class c)
	{
		return isPrimitiveClass(c) || isPrimitiveWrapperClass(c);
	}
	
	public static boolean isNumericPrimitiveOrWrapperClass(Class c)
	{
		return isNumericPrimitiveClass(c) || isNumericPrimitiveWrapperClass(c);
	}
	
	public static boolean isIntegerPrimitiveOrWrapperClass(Class c)
	{
		return isIntegerPrimitiveClass(c) || isIntegerPrimitiveWrapperClass(c);
	}
	
	public static boolean isFloatingPrimitiveOrWrapperClass(Class c)
	{
		return isFloatingPrimitiveClass(c) || isFloatingPrimitiveWrapperClass(c);
	}
	
	
	
	
	
	
	
	
	
	public static boolean isPrimitiveWrapperInstance(Object o)
	{
		return
		o instanceof Boolean ||
		o instanceof Character ||
		o instanceof Byte ||
		o instanceof Short ||
		o instanceof Integer ||
		o instanceof Long ||
		o instanceof Float ||
		o instanceof Double;
	}
	
	public static boolean isNumericPrimitiveWrapperInstance(Object o)
	{
		return
		o instanceof Byte ||
		o instanceof Short ||
		o instanceof Integer ||
		o instanceof Long ||
		o instanceof Float ||
		o instanceof Double;
	}
	
	public static boolean isIntegerPrimitiveWrapperInstance(Object o)
	{
		return
		o instanceof Byte ||
		o instanceof Short ||
		o instanceof Character || //Note: not "numeric"!
		o instanceof Integer ||
		o instanceof Long;
	}
	
	public static boolean isFloatingPrimitiveWrapperInstance(Object o)
	{
		return
		o instanceof Float ||
		o instanceof Double;
	}
	//Class/type things!>
	
	
	
	
	
	
	
	//<Primitive hashings! :D
	public static int hashprim(boolean x)
	{
		return x ? 0x84657A2B : 0xDC3EA157;  //chosen by fair dice roll, guaranteed to be random
	}
	
	public static int hashprim(long x)
	{
		return ((int)x) ^ ((int)(x >>> 32));
	}
	
	public static int hashprim(float x)
	{
		return Float.floatToIntBits(x);  //NOT ..RawIntBits!
	}
	
	public static int hashprim(double x)
	{
		return hashprim(Double.doubleToLongBits(x));  //NOT ..RawLongBits!
	}
	
	
	
	//byte, short, char, and int can just be used as is (or implicitly casted :> )
	
	public static int hashprim(byte x)
	{
		return x;
	}
	
	public static int hashprim(short x)
	{
		return x;
	}
	
	public static int hashprim(char x)
	{
		return x;
	}
	
	public static int hashprim(int x)
	{
		return x;
	}
	
	//Primitive hashings! :D >
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Converts byte, short, char, int -> long
	 * And float -> double
	 * 
	 * Otherwise passes it through unchanged! ^w^
	 * 
	 * (so it will either be: Long, Double, Boolean, or some non-primitive-wrapper Object! :D )
	 */
	@Nonnull
	public static Object normalizePrimitive(@Nonnull Object input)
	{
		if (input == null)
			throw new NullPointerException();
		if (input instanceof Integer || input instanceof Short || input instanceof Character || input instanceof Byte)
			return ((Number)input).longValue();
		if (input instanceof Float)
			return ((Float)input).doubleValue();
		return input;
	}
	
	
	
	@Nonnull
	public static Object typeMassageAmongstIntegerPrimitives(@Nonnull Object input, @Nonnull Class destType, boolean actuallyUnsigned)
	{
		if (input == null)
			throw new NullPointerException();
		if (!isIntegerPrimitiveWrapperInstance(input))
			throw new IllegalArgumentException("Not an integer primitive type!!: "+input.getClass());
		
		long value64 = (Long) normalizePrimitive(input);
		
		return typeMassageAmongstIntegerPrimitives(value64, destType, actuallyUnsigned);
	}
	
	@Nonnull
	public static Object typeMassageAmongstIntegerPrimitives(@Nonnull long value64, @Nonnull Class destType, boolean actuallyUnsigned)
	{
		if (destType == byte.class || destType == Byte.class)
		{
			return actuallyUnsigned ? Unsigned.safeCastS64toU8(value64) : Unsigned.safeCastS64toS8(value64);
		}
		else if (destType == short.class || destType == Short.class)
		{
			return actuallyUnsigned ? Unsigned.safeCastS64toU16(value64) : Unsigned.safeCastS64toS16(value64);
		}
		else if (destType == char.class || destType == Character.class)
		{
			return Unsigned.safeCastS64toU16(value64);  //always unsigned XDD
		}
		else if (destType == int.class || destType == Integer.class)
		{
			return actuallyUnsigned ? Unsigned.safeCastS64toU32(value64) : Unsigned.safeCastS64toS32(value64);
		}
		else if (destType == long.class || destType == Long.class)
		{
			return actuallyUnsigned ? Unsigned.safeCastS64toU64(value64) : value64;
		}
		else
		{
			throw new IllegalArgumentException("Not an integer primitive type!!: "+destType);
		}
	}
}


//Hey, another cute funny way to sanely test floating point equality!:  a == b || (a != a && b != b)
//edit: we use this now XD
//	old one: (a != a) ? (b != b) : a == b
//	^^'
