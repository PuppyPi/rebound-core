/*
 * Created on Feb 19, 2005
 * 	by the wonderful Eclipse(b)
 */
package rebound.bits;

import static rebound.util.Primitives.*;
import rebound.annotations.semantic.simpledata.ActuallyUnsigned;
import rebound.exceptions.DivisionByZeroException;
import rebound.exceptions.OverflowException;
import rebound.util.objectutil.JavaNamespace;

/**
 * This class contains methods for implementing Java operators that use primitive values
 * as if they were unsigned.<br>
 * The operators implemented are:<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&gt;,&lt;,&gt;=,&lt;=,cast[up],toString(),/,%<br>
 * &nbsp;The operators not implemented or semi done and why:<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;==		This checks if all binary bits's values are same in primitive, regardless of Sign.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;+-*		These arithmetic operators work the same way whether or not their operands are signed, (the results are just interpreted differently). <b>Note: Make sure you (cast)the result to the originating types (i.e. (byte)(a*b))<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;=		Assignment operators (=,+=,++,--,...) assign bits, regardless of sign.<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;casting&gt;	Some Java casts are okay (eg, widening, and using 'char' type) :>, but others aren't >>   We implement all the conversions here for clarity / locality, even signed->signed ones! ^_^ <br>
 * @author sean
 */
public class Unsigned
implements JavaNamespace
{
	public static final long LONG_MAX_VALUE = 0xFFFFFFFFFFFFFFFFL;
	public static final long LONG_MIN_VALUE = 0L;
	public static final int INT_MAX_VALUE = 0xFFFFFFFF;
	public static final int INT_MIN_VALUE = 0;
	public static final short SHORT_MAX_VALUE = (short)0xFFFF;
	public static final short SHORT_MIN_VALUE = 0;
	public static final byte BYTE_MAX_VALUE = (byte)0xFF;
	public static final byte BYTE_MIN_VALUE = 0;
	
	public static final long LONG_NEGFLAG = 0x8000000000000000L;
	public static final long LONG_DATAMASK = LONG_NEGFLAG-1;
	public static final int INT_NEGFLAG = 0x80000000;
	public static final int INT_DATAMASK = INT_NEGFLAG-1;
	public static final short SHORT_NEGFLAG = (short)0x8000;
	public static final short SHORT_DATAMASK = (short)(SHORT_NEGFLAG-1);
	public static final byte BYTE_NEGFLAG = (byte)0x80;
	public static final byte BYTE_DATAMASK = (byte)(BYTE_NEGFLAG-1);
	
	public static final float INT_HIGH_BIT_IN_SINGLEFLOAT = S32_MAX_VALUE + 1f;
	public static final float LONG_HIGH_BIT_IN_SINGLEFLOAT = Long.MAX_VALUE + 1f;
	public static final double INT_HIGH_BIT_IN_DOUBLEFLOAT = S32_MAX_VALUE + 1d;
	public static final double LONG_HIGH_BIT_IN_DOUBLEFLOAT = Long.MAX_VALUE + 1d;
	
	public static final float INT_MAX_SIGNED_VALUE_IN_SINGLEFLOAT = S32_MAX_VALUE;
	public static final float INT_MAX_UNSIGNED_VALUE_IN_SINGLEFLOAT = (S32_MAX_VALUE+1f)*2f - 1f;
	public static final double INT_MAX_SIGNED_VALUE_IN_DOUBLEFLOAT = S32_MAX_VALUE;
	public static final double INT_MAX_UNSIGNED_VALUE_IN_DOUBLEFLOAT = (S32_MAX_VALUE+1d)*2d - 1d;
	
	public static final float LONG_MAX_SIGNED_VALUE_IN_SINGLEFLOAT = Long.MAX_VALUE;
	public static final float LONG_MAX_UNSIGNED_VALUE_IN_SINGLEFLOAT = (Long.MAX_VALUE+1f)*2f - 1f;
	public static final double LONG_MAX_SIGNED_VALUE_IN_DOUBLEFLOAT = Long.MAX_VALUE;
	public static final double LONG_MAX_UNSIGNED_VALUE_IN_DOUBLEFLOAT = (Long.MAX_VALUE+1d)*2d - 1d;
	
	
	
	//Arithmetic: / %
	
	// /
	/**
	 * Returns number of times b fits into a (a / b) disregarding the remainder(a % b).
	 */
	@ActuallyUnsigned
	public static long divideU64(@ActuallyUnsigned long a, @ActuallyUnsigned long b)
	{
		//Speed daemons
		if (b == 0)
			throw new DivisionByZeroException();
		if (a == 0)
			return 0;
		if (b == 1)
			return a;
		if (a == b)
			return 1;
		
		/*
		 * x/y = (x-LONG_NEGFLAG)/y + LONG_NEGFLAG/y
		 * 		* y
		 * x = x - LONG_NEGFLAG + LONG_NEGFLAG
		 * 		<Cancelation>
		 * x = x
		 */
		
		
		boolean anega = a < 0;
		boolean bnega = b < 0;
		
		if (anega)
			a = (a & LONG_DATAMASK);
		if (bnega)
			b = (b & LONG_DATAMASK);
		
		
		long c = 0;
		long mod1 = 0;
		if (b > 0)
		{
			c = (a / b);
			mod1 = (a % b); //Max:126
		}
		
		if (anega && !bnega)
		{
			c += LONG_NEGFLAG/-b;
			long mod2 = -(LONG_NEGFLAG % b);
			if (mod1+mod2 >= b)
				c++;
		}
		else if (!anega && bnega)
			return 0;
		else if (anega && bnega)
		{
			if (a >= b)
				return 1;
			else
				return 0;
		}
		
		return c;
	}
	
	/**
	 * Returns number of times b fits into a (a / b) disregarding the remainder(a % b).
	 */
	@ActuallyUnsigned
	public static int divideU32(@ActuallyUnsigned int a, @ActuallyUnsigned int b)
	{
		//Speed daemons
		if (b == 0)
			throw new DivisionByZeroException();
		if (a == 0)
			return 0;
		if (b == 1)
			return a;
		if (a == b)
			return 1;
		
		/*
		 * x/y = (x-SHORT_NEGFLAG)/y + SHORT_NEGFLAG/y
		 * 		* y
		 * x = x - SHORT_NEGFLAG + SHORT_NEGFLAG
		 * 		<Cancelation>
		 * x = x
		 */
		
		
		boolean anega = a < 0;
		boolean bnega = b < 0;
		
		if (anega)
			a = (a & INT_DATAMASK);
		if (bnega)
			b = (b & INT_DATAMASK);
		
		
		int c = 0;
		int mod1 = 0;
		if (b > 0)
		{
			c = (a / b);
			mod1 = (a % b); //Max:126
		}
		
		if (anega && !bnega)
		{
			c += INT_NEGFLAG/-b;
			int mod2 = -(INT_NEGFLAG % b);
			if (mod1+mod2 >= b)
				c++;
		}
		else if (!anega && bnega)
			return 0;
		else if (anega && bnega)
		{
			if (a >= b)
				return 1;
			else
				return 0;
		}
		
		return c;
	}
	
	//	/**
	//	 * Returns number of times b fits into a (a / b) disregarding the remainder(a % b).
	//	 */
	//	@ActuallyUnsigned
	//	public static short divide(@ActuallyUnsigned short a, @ActuallyUnsigned short b)
	//	{
	//		//Speed daemons
	//		if (b == 0)
	//			throw new ArithmeticException("Division of "+toString(a)+" by 0");
	//		if (a == 0)
	//			return 0;
	//		if (b == 1)
	//			return a;
	//		if (a == b)
	//			return 1;
	//
	//		/*
	//		 * x/y = (x-SHORT_NEGFLAG)/y + SHORT_NEGFLAG/y
	//		 * 		* y
	//		 * x = x - SHORT_NEGFLAG + SHORT_NEGFLAG
	//		 * 		<Cancelation>
	//		 * x = x
	//		 */
	//
	//
	//		boolean anega = a < 0;
	//		boolean bnega = b < 0;
	//
	//		if (anega)
	//			a = (short)(a & SHORT_DATAMASK);
	//		if (bnega)
	//			b = (short)(b & SHORT_DATAMASK);
	//
	//
	//		int c = 0;
	//		int mod1 = 0;
	//		if (b > 0)
	//		{
	//			c = (short)(a / b);
	//			mod1 = (short)(a % b); //Max:126
	//		}
	//
	//		if (anega && !bnega)
	//		{
	//			c += (short)((SHORT_NEGFLAG)/-b);
	//			int mod2 = (short)-(SHORT_NEGFLAG % b);
	//			if (mod1+mod2 >= b)
	//				c++;
	//		}
	//		else if (!anega && bnega)
	//			return 0;
	//		else if (anega && bnega)
	//		{
	//			if (a >= b)
	//				return 1;
	//			else
	//				return 0;
	//		}
	//
	//		return (short)c;
	//	}
	
	/**
	 * Returns number of times b fits into a (a / b) disregarding the remainder(a % b).
	 */
	@ActuallyUnsigned
	public static byte divideU8(@ActuallyUnsigned byte a, @ActuallyUnsigned byte b)
	{
		//Speed daemons
		if (b == 0)
			throw new DivisionByZeroException();
		if (a == 0)
			return 0;
		if (b == 1)
			return a;
		if (a == b)
			return 1;
		
		/*
		 * x/y = (x-128)/y + 128/y
		 * 		* y
		 * x = x - 128 + 128
		 * 		<Cancelation>
		 * x = x
		 */
		
		
		boolean anega = a < 0;
		boolean bnega = b < 0;
		
		if (anega)
			a = (byte)(a & BYTE_DATAMASK);
		if (bnega)
			b = (byte)(b & BYTE_DATAMASK);
		
		
		int c = 0;
		int mod1 = 0;
		if (b > 0)
		{
			c = (byte)(a / b);
			mod1 = (byte)(a % b); //Max:126
		}
		
		if (anega && !bnega)
		{
			c += (byte)(BYTE_NEGFLAG/-b);
			int mod2 = (byte)-(BYTE_NEGFLAG % b);
			if (mod1+mod2 >= b)
				c++;
		}
		else if (!anega && bnega)
			return 0;
		else if (anega && bnega)
		{
			if (a >= b)
				return 1;
			else
				return 0;
		}
		
		return (byte)c;
	}
	
	
	
	
	
	// %
	/**
	 * Returns remainder after finding number of times b fits into a.
	 */
	@ActuallyUnsigned
	public static long modulusU64(@ActuallyUnsigned long a, @ActuallyUnsigned long b)
	{
		long div = divideU64(a, b);
		long inaccurateA = (div * b);
		return (a - inaccurateA);
	}
	
	/**
	 * Returns remainder after finding number of times b fits into a.
	 */
	@ActuallyUnsigned
	public static int modulusU32(@ActuallyUnsigned int a, @ActuallyUnsigned int b)
	{
		int div = divideU32(a, b);
		int inaccurateA = (div * b);
		return (a - inaccurateA);
	}
	
	//	/**
	//	 * Returns remainder after finding number of times b fits into a.
	//	 */
	//	@ActuallyUnsigned
	//	public static short modulus(@ActuallyUnsigned short a, @ActuallyUnsigned short b)
	//	{
	//		short div = divide(a, b);
	//		short inaccurateA = (short)(div * b);
	//		return (short)(a - inaccurateA);
	//	}
	
	/**
	 * Returns remainder after finding number of times b fits into a.
	 */
	@ActuallyUnsigned
	public static byte modulusU8(@ActuallyUnsigned byte a, @ActuallyUnsigned byte b)
	{
		byte div = divideU8(a, b);
		byte inaccurateA = (byte)(div * b);
		return (byte)(a - inaccurateA);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @return a < b
	 */
	public static boolean lessThanU8(@ActuallyUnsigned byte a, @ActuallyUnsigned byte b)
	{
		//return a < b
		if (a < 0 && b >= 0)
			return false;
		else if (a >= 0 && b < 0)
			return true;
		else
			return a < b;
	}
	
	/**
	 * @return a < b
	 */
	public static boolean lessThanU16(@ActuallyUnsigned short a, @ActuallyUnsigned short b)
	{
		//return a < b
		if (a < 0 && b >= 0)
			return false;
		else if (a >= 0 && b < 0)
			return true;
		else
			return a < b;
	}
	
	/**
	 * @return a < b
	 */
	public static boolean lessThanU32(@ActuallyUnsigned int a, @ActuallyUnsigned int b)
	{
		//return a < b
		
		if (a < 0 && b >= 0) //highest bit is set in a not b, then a > b
			return false;
		else if (a >= 0 && b < 0)  //highest bit set in b not a, then a < b
			return true;
		else  //Otherwise normal works just fine
			return a < b;
	}
	
	/**
	 * @return a < b
	 */
	public static boolean lessThanU64(@ActuallyUnsigned long a, @ActuallyUnsigned long b)
	{
		//return a < b
		if (a < 0 && b >= 0)
			return false;
		else if (a >= 0 && b < 0)
			return true;
		else
			return a < b;
	}
	
	
	
	
	
	
	
	/**
	 * @return a > b
	 */
	public static boolean greaterThanU8(@ActuallyUnsigned byte a, @ActuallyUnsigned byte b)
	{
		//return a > b
		if (a < 0 && b >= 0)
			return true;
		else if (a >= 0 && b < 0)
			return false;
		else
			return a > b;
	}
	
	/**
	 * @return a > b
	 */
	public static boolean greaterThanU16(@ActuallyUnsigned short a, @ActuallyUnsigned short b)
	{
		//return a > b
		if (a < 0 && b >= 0)
			return true;
		else if (a >= 0 && b < 0)
			return false;
		else
			return a > b;
	}
	
	/**
	 * @return a > b
	 */
	public static boolean greaterThanU32(@ActuallyUnsigned int a, @ActuallyUnsigned int b)
	{
		//return a > b
		if (a < 0 && b >= 0)
			return true;
		else if (a >= 0 && b < 0)
			return false;
		else
			return a > b;
	}
	
	/**
	 * @return a > b
	 */
	public static boolean greaterThanU64(@ActuallyUnsigned long a, @ActuallyUnsigned long b)
	{
		//return a > b
		if (a < 0 && b >= 0)
			return true;
		else if (a >= 0 && b < 0)
			return false;
		else
			return a > b;
	}
	
	
	
	/**
	 * @return a <= b
	 */
	public static boolean lessThanEqualToU8(@ActuallyUnsigned byte a, @ActuallyUnsigned byte b)
	{
		if (a == b)
			return true;
		
		//return a < b
		if (a < 0 && b >= 0)
			return false;
		else if (a >= 0 && b < 0)
			return true;
		else
			return a <= b;
	}
	
	/**
	 * @return a <= b
	 */
	public static boolean lessThanEqualToU16(@ActuallyUnsigned short a, @ActuallyUnsigned short b)
	{
		if (a == b)
			return true;
		
		//return a < b
		if (a < 0 && b >= 0)
			return false;
		else if (a >= 0 && b < 0)
			return true;
		else
			return a <= b;
	}
	
	/**
	 * @return a <= b
	 */
	public static boolean lessThanEqualToU32(@ActuallyUnsigned int a, @ActuallyUnsigned int b)
	{
		//return a < b
		if (a == b)
			return true;
		
		if (a < 0 && b >= 0)  //highest bit is set in a not b, then a > b
			return false;
		else if (a >= 0 && b < 0)  //highest bit set in b not a, then a < b
			return true;
		else  //Otherwise normal works just fine
			return a <= b;
	}
	
	/**
	 * @return a <= b
	 */
	public static boolean lessThanEqualToU64(@ActuallyUnsigned long a, @ActuallyUnsigned long b)
	{
		if (a == b)
			return true;
		
		//return a < b
		if (a < 0 && b >= 0)
			return false;
		else if (a >= 0 && b < 0)
			return true;
		else
			return a <= b;
	}
	
	
	
	
	
	
	
	/**
	 * @return a >= b
	 */
	public static boolean greaterThanEqualToU8(@ActuallyUnsigned byte a, @ActuallyUnsigned byte b)
	{
		if (a == b)
			return true;
		
		//return a > b
		if (a < 0 && b >= 0)
			return true;
		else if (a >= 0 && b < 0)
			return false;
		else
			return a >= b;
	}
	
	/**
	 * @return a >= b
	 */
	public static boolean greaterThanEqualToU16(@ActuallyUnsigned short a, @ActuallyUnsigned short b)
	{
		if (a == b)
			return true;
		
		//return a > b
		if (a < 0 && b >= 0)
			return true;
		else if (a >= 0 && b < 0)
			return false;
		else
			return a >= b;
	}
	
	/**
	 * @return a >= b
	 */
	public static boolean greaterThanEqualToU32(@ActuallyUnsigned int a, @ActuallyUnsigned int b)
	{
		if (a == b)
			return true;
		
		//return a > b
		if (a < 0 && b >= 0)
			return true;
		else if (b < 0 && a >= 0)
			return false;
		else
			return a >= b;
	}
	
	/**
	 * @return a >= b
	 */
	public static boolean greaterThanEqualToU64(@ActuallyUnsigned long a, @ActuallyUnsigned long b)
	{
		if (a == b)
			return true;
		
		//return a > b
		if (a < 0 && b >= 0)
			return true;
		else if (a >= 0 && b < 0)
			return false;
		else
			return a >= b;
	}
	
	
	
	
	
	
	/**
	 * This method expands <code>b</code> into an <code>int</code> as if the bits in <code>b</code> were intended for an unsigned.
	 * @param b bits of an unsigned
	 * @return b expanded to int
	 */
	public static int upcast(@ActuallyUnsigned byte b)
	{
		return upcastTo32(b);
	}
	
	/**
	 * This method expands <code>b</code> into an <code>int</code> as if the bits in <code>b</code> were intended for an unsigned.
	 * @param b bits of an unsigned
	 * @return b expanded to int
	 */
	public static int upcast(@ActuallyUnsigned short b)
	{
		return upcastTo32(b);
	}
	
	/**
	 * This method expands <code>b</code> into a <code>long</code> as if the bits in <code>b</code> were intended for an unsigned.
	 * @param b bits of an integer
	 * @return b expanded to long
	 */
	public static long upcast(@ActuallyUnsigned int b)
	{
		return upcastTo64(b);
	}
	
	
	
	
	
	public static int upcastTo32(@ActuallyUnsigned byte b)
	{
		return b & 0x000000FF;
	}
	
	public static int upcastTo32(@ActuallyUnsigned short b)
	{
		return b & 0x0000FFFF;
	}
	
	
	public static long upcastTo64(@ActuallyUnsigned byte b)
	{
		return b & 0x00000000000000FFl;
	}
	
	public static long upcastTo64(@ActuallyUnsigned short b)
	{
		return b & 0x000000000000FFFFl;
	}
	
	public static long upcastTo64(@ActuallyUnsigned int b)
	{
		return b & 0x00000000FFFFFFFFl;
	}
	
	
	
	
	
	
	
	
	public static int[] upcastAll(@ActuallyUnsigned byte[] a)
	{
		int[] rv = new int[a.length];
		for (int i = 0; i < a.length; i++)
			rv[i] = a[i] & 0x000000FF;
		return rv;
	}
	
	public static int[] upcastAll(@ActuallyUnsigned short[] a)
	{
		int[] rv = new int[a.length];
		for (int i = 0; i < a.length; i++)
			rv[i] = a[i] & 0x0000FFFF;
		return rv;
	}
	
	public static long[] upcastAll(@ActuallyUnsigned int[] a)
	{
		long[] rv = new long[a.length];
		for (int i = 0; i < a.length; i++)
			rv[i] = a[i] & 0x00000000FFFFFFFFl;
		return rv;
	}
	
	
	
	
	
	
	public static float safeCastU8toF32(@ActuallyUnsigned byte unsigned)
	{
		return upcast(unsigned);
	}
	
	public static float safeCastU16toF32(char unsigned)
	{
		return upcast(unsigned);
	}
	
	public static float safeCastU32toF32(@ActuallyUnsigned int unsigned)
	{
		if (unsigned >= 0)
			return unsigned;
		else
			return (unsigned & INT_DATAMASK) + INT_HIGH_BIT_IN_SINGLEFLOAT;
	}
	
	public static float safeCastU64toF32(@ActuallyUnsigned long unsigned)
	{
		if (unsigned >= 0)
			return unsigned;
		else
			return (unsigned & LONG_DATAMASK) + LONG_HIGH_BIT_IN_SINGLEFLOAT;
	}
	
	
	
	public static double safeCastU8toF64(@ActuallyUnsigned byte unsigned)
	{
		return upcast(unsigned);
	}
	
	public static double safeCastU16toF64(char unsigned)
	{
		return upcast(unsigned);
	}
	
	public static double safeCastU32toF64(@ActuallyUnsigned int unsigned)
	{
		if (unsigned >= 0)
			return unsigned;
		else
			return (unsigned & INT_DATAMASK) + INT_HIGH_BIT_IN_DOUBLEFLOAT;
	}
	
	public static double safeCastU64toF64(@ActuallyUnsigned long unsigned)
	{
		if (unsigned >= 0)
			return unsigned;
		else
			return (unsigned & LONG_DATAMASK) + LONG_HIGH_BIT_IN_DOUBLEFLOAT;
	}
	
	
	
	
	//TODO safeCastF32toU8 :p
	//TODO safeCastF32toU16 :p
	//TODO safeCastF64toU8 :p
	//TODO safeCastF64toU16 :p
	
	
	@ActuallyUnsigned
	public static int safeCastF32toU32(float floatingValue) throws OverflowException
	{
		if (floatingValue > INT_MAX_UNSIGNED_VALUE_IN_SINGLEFLOAT || floatingValue < 0)
		{
			throw new OverflowException();
		}
		else if (floatingValue > INT_MAX_SIGNED_VALUE_IN_SINGLEFLOAT)
		{
			return (int)(floatingValue - INT_MAX_SIGNED_VALUE_IN_SINGLEFLOAT) | INT_NEGFLAG;
		}
		else
		{
			return (int)floatingValue;
		}
	}
	
	@ActuallyUnsigned
	public static long safeCastF32toU64(float floatingValue) throws OverflowException
	{
		if (floatingValue > LONG_MAX_UNSIGNED_VALUE_IN_SINGLEFLOAT || floatingValue < 0)
		{
			throw new OverflowException();
		}
		else if (floatingValue > LONG_MAX_SIGNED_VALUE_IN_SINGLEFLOAT)
		{
			return (long)(floatingValue - LONG_MAX_SIGNED_VALUE_IN_SINGLEFLOAT) | LONG_NEGFLAG;
		}
		else
		{
			return (long)floatingValue;
		}
	}
	
	
	
	@ActuallyUnsigned
	public static int safeCastF64toU32(double floatingValue) throws OverflowException
	{
		if (floatingValue > INT_MAX_UNSIGNED_VALUE_IN_DOUBLEFLOAT || floatingValue < 0)
		{
			throw new OverflowException();
		}
		else if (floatingValue > INT_MAX_SIGNED_VALUE_IN_DOUBLEFLOAT)
		{
			return (int)(floatingValue - INT_MAX_SIGNED_VALUE_IN_DOUBLEFLOAT) | INT_NEGFLAG;
		}
		else
		{
			return (int)floatingValue;
		}
	}
	
	@ActuallyUnsigned
	public static long safeCastF64toU64(double floatingValue) throws OverflowException
	{
		if (floatingValue > LONG_MAX_UNSIGNED_VALUE_IN_DOUBLEFLOAT || floatingValue < 0)
		{
			throw new OverflowException();
		}
		else if (floatingValue > LONG_MAX_SIGNED_VALUE_IN_DOUBLEFLOAT)
		{
			return (long)(floatingValue - LONG_MAX_SIGNED_VALUE_IN_DOUBLEFLOAT) | LONG_NEGFLAG;
		}
		else
		{
			return (long)floatingValue;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<CCCCAAAAASSSSTTTTTSSSSS! :D
	
	/*
	 * Widening U -> S
	 * Always safe :>
	 */
	public static short upcastU8toS16(@ActuallyUnsigned byte input)
	{
		return (short)(input & 0xFF);
	}
	
	public static int upcastU8toS32(@ActuallyUnsigned byte input)
	{
		return input & 0xFF;
	}
	
	public static long upcastU8toS64(@ActuallyUnsigned byte input)
	{
		return input & 0xFFl;
	}
	
	public static long upcastU16toS64(@ActuallyUnsigned short input)
	{
		return input & 0xFFFFl;
	}
	
	public static long upcastU32toS64(@ActuallyUnsigned int input)
	{
		return input & 0xFFFFFFFFl;
	}
	
	
	
	
	/*
	 * Widening U -> U
	 * Always safe :>
	 */
	public static char upcastU8toU16(@ActuallyUnsigned byte input)
	{
		return (char)(input & 0xFF);
	}
	
	@ActuallyUnsigned
	public static int upcastU8toU32(@ActuallyUnsigned byte input)
	{
		return input & 0xFF;
	}
	
	@ActuallyUnsigned
	public static long upcastU8toU64(@ActuallyUnsigned byte input)
	{
		return input & 0xFFl;
	}
	
	@ActuallyUnsigned
	public static long upcastU32toU64(@ActuallyUnsigned int input)
	{
		return input & 0xFFFFFFFFl;
	}
	
	
	
	
	
	
	/*
Things Java does (safely!) naturally! :>

public static short safeCastS8toS16(byte input)
public static int safeCastS8toS32(byte input)
public static long safeCastS8toS64(byte input)
public static int safeCastS16toS32(short input)
public static long safeCastS16toS64(short input)
public static long safeCastS32toS64(int input)

public static int safeCastU16toS32(char input)
public static long safeCastU16toS64(char input)

public static int safeCastU16toU32(char input)
public static long safeCastU16toU64(char input)
	 */
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//These are useful for code generators since they're consistently- and simply- named :3
	
	public static short reinterpretLowBitsAsS16(char in)
	{
		return (short)in;
	}
	
	public static short reinterpretLowBitsAsS16(int in)
	{
		return (short)in;
	}
	
	public static short reinterpretLowBitsAsS16(long in)
	{
		return (short)in;
	}
	
	
	
	public static char reinterpretLowBitsAsU16(short in)
	{
		return (char)in;
	}
	
	public static char reinterpretLowBitsAsU16(int in)
	{
		return (char)in;
	}
	
	public static char reinterpretLowBitsAsU16(long in)
	{
		return (char)in;
	}
	
	
	
	public static int reinterpretLowBitsAsU24(int in)
	{
		return in & 0x00FFFFFF;
	}
	
	public static int reinterpretLowBitsAsU24(long in)
	{
		return reinterpretLowBitsAsU24((int)in);
	}
	
	
	
	public static int reinterpretLowBitsAsS24(int in)
	{
		return signextendInt24(in & 0x00FFFFFF);
	}
	
	public static int reinterpretLowBitsAsS24(long in)
	{
		return reinterpretLowBitsAsS24((int)in);
	}
	
	
	
	
	public static int reinterpretLowBitsAsS32(long in)
	{
		return (int)in;
	}
	
	
	
	
	public static long reinterpretLowBitsAsU40(long in)
	{
		return in & 0x0000_00FF_FFFF_FFFFl;
	}
	
	public static long reinterpretLowBitsAsS40(long in)
	{
		return signextendLong40(in & 0x0000_00FF_FFFF_FFFFl);
	}
	
	
	
	
	public static long reinterpretLowBitsAsU48(long in)
	{
		return in & 0x0000_FFFF_FFFF_FFFFl;
	}
	
	public static long reinterpretLowBitsAsS48(long in)
	{
		return signextendLong48(in & 0x0000_FFFF_FFFF_FFFFl);
	}
	
	
	
	
	public static long reinterpretLowBitsAsU56(long in)
	{
		return in & 0x00FF_FFFF_FFFF_FFFFl;
	}
	
	public static long reinterpretLowBitsAsS56(long in)
	{
		return signextendLong56(in & 0x00FF_FFFF_FFFF_FFFFl);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	//Not needed for 24 (because it's stored as int!) and 16 bits (because a Java primitive for unsigned 16 bit ints exists!)
	
	public static long reinterpretLowBitsAsU32(int in)
	{
		return in & 0xFFFF_FFFF;
	}
	
	public static long reinterpretLowBitsAsU32(long in)
	{
		return in & 0xFFFF_FFFF;
	}
	//CCCCAAAAASSSSTTTTTSSSSS! :D >
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Like an opposite-of-{@link #upcast(byte)}'s, this makes an *unsigned* value *signed*! XD
	 * Which you can't do through casts like you can for 8,16,32 bits (eg, "(int)(short)40200" )  because Java/JVM doesn't have direct support for any signedness 24 bit integers!
	 */
	public static int signextendInt24(int value24)
	{
		//return ((value24 & 0x800000) != 0) ? value24 | 0xFF000000 : value24;
		return value24 | (((value24 & 0x00800000) >>> 23) * 0xFF000000);
	}
	
	
	/**
	 * Like an opposite-of-{@link #upcast(byte)}'s, this makes an *unsigned* value *signed*! XD
	 * Which you can't do through, eg, (int)(short)40200 because Java/JVM doesn't have direct support for any signedness 40 bit integers!
	 */
	public static long signextendLong40(long value40)
	{
		return value40 | (((value40 & 0x0000008000000000l) >>> 39l) * 0xFFFFFF0000000000l);
	}
	
	/**
	 * Like an opposite-of-{@link #upcast(byte)}'s, this makes an *unsigned* value *signed*! XD
	 * Which you can't do through, eg, (int)(short)40200 because Java/JVM doesn't have direct support for any signedness 48 bit integers!
	 */
	public static long signextendLong48(long value48)
	{
		return value48 | (((value48 & 0x0000800000000000l) >>> 47l) * 0xFFFF000000000000l);
	}
	
	/**
	 * Like an opposite-of-{@link #upcast(byte)}'s, this makes an *unsigned* value *signed*! XD
	 * Which you can't do through, eg, (int)(short)40200 because Java/JVM doesn't have direct support for any signedness 56 bit integers!
	 */
	public static long signextendLong56(long value56)
	{
		return value56 | (((value56 & 0x0080000000000000l) >>> 55l) * 0xFF00000000000000l);
	}
}
