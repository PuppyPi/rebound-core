package rebound.math;

import static java.lang.Math.*;
import static rebound.bits.BitUtilities.*;
import static rebound.bits.Unsigned.*;
import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.simpledata.ActuallyUnsignedValue;
import rebound.bits.BitUtilities;
import rebound.bits.Unsigned;
import rebound.exceptions.DivisionByZeroException;
import rebound.exceptions.NotYetImplementedException;
import rebound.exceptions.OutOfDomainArithmeticException;
import rebound.exceptions.OutOfDomainArithmeticException.ComplexNumberArithmeticException;
import rebound.exceptions.OverflowException;
import rebound.exceptions.TruncationException;

public class SmallIntegerMathUtilities
{
	public static long awayfromzeroDivision(long numerator, long divisor)
	{
		return towardzeroDivision(numerator, divisor) + (numerator % divisor != 0 ? signum(numerator) * signum(divisor) : 0);
	}
	
	public static long towardzeroDivision(long numerator, long divisor)
	{
		return numerator / divisor;  //XDD
	}
	
	public static long halftowardzeroDivision(long numerator, long divisor)
	{
		//TODO
		throw new NotYetImplementedException();
	}
	
	public static long halfawayfromzeroDivision(long numerator, long divisor)
	{
		//TODO
		throw new NotYetImplementedException();
	}
	
	public static long halfevenDivision(long numerator, long divisor)
	{
		//TODO
		throw new NotYetImplementedException();
	}
	
	
	
	
	public static int ceilingDivision(int numerator, int divisor)
	{
		return floorDivision(numerator, divisor) + (numerator % divisor != 0 ? 1 : 0); //modulo == 0 will work regardless of modulo-vs-modulus handling of negative things :>
	}
	
	/**
	 * Gets the only value that's not x, or x if all are x, and throw an exception if more than one is. :>
	 */
	public static int getTheOnlyOneNotXAsserting(int x, int... values) throws AssertionError
	{
		int rv = x;
		
		boolean hasOne = false;
		
		for (int v : values)
		{
			if (v != x)
			{
				if (hasOne)
				{
					throw new IllegalArgumentException();
				}
				
				rv = v;
				hasOne = true;
			}
		}
		
		return rv;
	}
	
	/**
	 * Gets the only value that's not zero, or zero if all are, and throw an error if more than one is. :>
	 */
	public static int getTheOnlyOneNotzeroAsserting(int... values) throws AssertionError
	{
		return getTheOnlyOneNotXAsserting(0, values);
	}
	
	public static boolean unsignedAddOverflows(long a, long b, long bitlength)
	{
		long highestBitA = a >>> 63;
		long highestBitB = b >>> 63;
		
		long carryIntoHighestBit = 0;
		{
			long lowA = a & ~(1 << 63);
			long lowB = b & ~(1 << 63);
			
			carryIntoHighestBit = (lowA + lowB) >>> 63;
		}
		
		return highestBitA + highestBitB + carryIntoHighestBit > 1;
	}
	
	
	
	
	
	public static long greatest(long a, long b, long c, long d)
	{
		if (d < a && d < b && d < c)
			return SmallIntegerMathUtilities.greatest(a, b, c);
		else if (c < a && c < b && c < d)
			return SmallIntegerMathUtilities.greatest(a, b, d);
		else if (b < a && b < c && b < d)
			return SmallIntegerMathUtilities.greatest(a, c, d);
		else if (a < b && a < c && a < d)
			return SmallIntegerMathUtilities.greatest(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static long least(long a, long b, long c, long d)
	{
		if (d > a && d > b && d > c)
			return SmallIntegerMathUtilities.least(a, b, c);
		else if (c > a && c > b && c > d)
			return SmallIntegerMathUtilities.least(a, b, d);
		else if (b > a && b > c && b > d)
			return SmallIntegerMathUtilities.least(a, c, d);
		else if (a > b && a > c && a > d)
			return SmallIntegerMathUtilities.least(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static long greatest(long a, long b, long c)
	{
		if (c < a && c < b)
			return SmallIntegerMathUtilities.greatest(a, b);
		else if (b < a && b < c)
			return SmallIntegerMathUtilities.greatest(a, c);
		else if (a < b && a < c)
			return SmallIntegerMathUtilities.greatest(b, c);
		else
			throw new AssertionError();
	}
	
	public static long least(long a, long b, long c)
	{
		if (c > a && c > b)
			return SmallIntegerMathUtilities.least(a, b);
		else if (b > a && b > c)
			return SmallIntegerMathUtilities.least(a, c);
		else if (a > b && a > c)
			return SmallIntegerMathUtilities.least(b, c);
		else
			throw new AssertionError();
	}
	
	public static int least(int a, int b)
	{
		return b < a ? b : a;
	}
	
	public static byte checkNotZeroForDivide(byte x)
	{
		if (x == 0)
			throw new DivisionByZeroException();
		return x;
	}
	
	public static Byte checkNotZeroForDivide(Byte x)
	{
		if (x == 0)
			throw new DivisionByZeroException();
		return x;
	}
	
	public static short checkNotZeroForDivide(short x)
	{
		if (x == 0)
			throw new DivisionByZeroException();
		return x;
	}
	
	public static Short checkNotZeroForDivide(Short x)
	{
		if (x == 0)
			throw new DivisionByZeroException();
		return x;
	}
	
	public static int checkNotZeroForDivide(int x)
	{
		if (x == 0)
			throw new DivisionByZeroException();
		return x;
	}
	
	public static Integer checkNotZeroForDivide(Integer x)
	{
		if (x == 0)
			throw new DivisionByZeroException();
		return x;
	}
	
	public static long checkNotZeroForDivide(long x)
	{
		if (x == 0)
			throw new DivisionByZeroException();
		return x;
	}
	
	public static Long checkNotZeroForDivide(Long x)
	{
		if (x == 0)
			throw new DivisionByZeroException();
		return x;
	}
	
	
	
	
	public static int cmp(long a, long b)
	{
		if (a < b) return -1;
		if (a > b) return 1;
		return 0; //if (a == b)
	}
	
	public static int cmp(int a, int b)
	{
		if (a < b) return -1;
		if (a > b) return 1;
		return 0; //if (a == b)
	}
	
	public static int cmp(short a, short b)
	{
		if (a < b) return -1;
		if (a > b) return 1;
		return 0; //if (a == b)
	}
	
	public static int cmp(byte a, byte b)
	{
		if (a < b) return -1;
		if (a > b) return 1;
		return 0; //if (a == b)
	}
	
	public static int cmp(char a, char b)
	{
		if (a < b) return -1;
		if (a > b) return 1;
		return 0; //if (a == b)
	}
	
	
	
	
	public static int cmpChainable(int prev, long a, long b)
	{
		return prev != 0 ? prev : cmp(a, b);
	}
	
	public static int cmpChainable(int prev, int a, int b)
	{
		return prev != 0 ? prev : cmp(a, b);
	}
	
	public static int cmpChainable(int prev, short a, short b)
	{
		return prev != 0 ? prev : cmp(a, b);
	}
	
	public static int cmpChainable(int prev, byte a, byte b)
	{
		return prev != 0 ? prev : cmp(a, b);
	}
	
	public static int cmpChainable(int prev, char a, char b)
	{
		return prev != 0 ? prev : cmp(a, b);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int cmpNullAsNinf(@Nullable Long a, @Nullable Long b)
	{
		if (a == null) return b == null ? 0 : -1;
		if (b == null) return 1;
		if (a < b) return -1;
		if (a > b) return 1;
		return 0; //if (a == b)
	}
	
	public static int cmpNullAsNinf(@Nullable Integer a, @Nullable Integer b)
	{
		if (a == null) return b == null ? 0 : -1;
		if (b == null) return 1;
		if (a < b) return -1;
		if (a > b) return 1;
		return 0; //if (a == b)
	}
	
	public static int cmpNullAsNinf(@Nullable Short a, @Nullable Short b)
	{
		if (a == null) return b == null ? 0 : -1;
		if (b == null) return 1;
		if (a < b) return -1;
		if (a > b) return 1;
		return 0; //if (a == b)
	}
	
	public static int cmpNullAsNinf(@Nullable Byte a, @Nullable Byte b)
	{
		if (a == null) return b == null ? 0 : -1;
		if (b == null) return 1;
		if (a < b) return -1;
		if (a > b) return 1;
		return 0; //if (a == b)
	}
	
	public static int cmpNullAsNinf(@Nullable Character a, @Nullable Character b)
	{
		if (a == null) return b == null ? 0 : -1;
		if (b == null) return 1;
		if (a < b) return -1;
		if (a > b) return 1;
		return 0; //if (a == b)
	}
	
	
	
	
	public static int cmpChainableNullAsNinf(int prev, @Nullable Long a, @Nullable Long b)
	{
		return prev != 0 ? prev : cmpNullAsNinf(a, b);
	}
	
	public static int cmpChainableNullAsNinf(int prev, @Nullable Integer a, @Nullable Integer b)
	{
		return prev != 0 ? prev : cmpNullAsNinf(a, b);
	}
	
	public static int cmpChainableNullAsNinf(int prev, @Nullable Short a, @Nullable Short b)
	{
		return prev != 0 ? prev : cmpNullAsNinf(a, b);
	}
	
	public static int cmpChainableNullAsNinf(int prev, @Nullable Byte a, @Nullable Byte b)
	{
		return prev != 0 ? prev : cmpNullAsNinf(a, b);
	}
	
	public static int cmpChainableNullAsNinf(int prev, @Nullable Character a, @Nullable Character b)
	{
		return prev != 0 ? prev : cmpNullAsNinf(a, b);
	}
	
	
	
	
	
	
	
	/**
	 * The GCD of any amount of numbers.<br>
	 * Note: The provided array will be horribly mangled.<br>
	 */
	public static int gcd(@WritableValue int[] numbers)
	{
		return gcd(numbers, 0, numbers.length);
	}
	
	public static int gcd(@WritableValue int[] numbers, int start, int end)
	{
		return gcd_binary(numbers, start, end);
	}
	
	public static int gcd_binary(@WritableValue int[] numbers, int start, int end)
	{
		int n = numbers.length;
		for (int i = 0; i < n; i++)
		{
			int x = numbers[i];
			if (x < 0) numbers[i] = -x;
		}
		
		
		
		int i = 0;
		
		//Record the number of common 2-factors, and drop all the 2's from each number's factor-list
		int shift = -1;
		{
			int cShift = 0, curr = 0;
			for (i = start; i < end; i++)
			{
				curr = numbers[i];
				if (curr != 0)
				{
					cShift = dcd32(curr & -curr);
					if (shift == -1 || cShift < shift)
						shift = cShift;
					
					/*
					 * Divide all numbers by the common power-of-2 divisor
					 * Then divide all remaining even numbers until they are odd, since at least one of them was odd after the above step, which means 2 is no longer a common divisor, so we can throw it away without pause
				
					 * Rewriting the above code, you get:
					 * 	drop this many 2's from each number (number of common 2's) + (number of extra 2's this number has beyond the number of common 2's)
					 * The second adden (extra 2's) will be zero for some numbers, but the above refactoring still holds
					 * This line of code is then equivalent to:
					 * 	drop all the 2's from each number
					 * since the number of common 2's plus the number of extra 2's (total - common) == total 2's
					 */
					curr >>>= cShift;
			
			numbers[i] = curr;
				}
			}
		}
		
		
		
		//Main body
		
		boolean moreThanOneLeft = false;
		boolean minimumSet = false;
		int min = 0;
		int curr = 0;
		
		boolean skippedOnce = false;
		while (true)
		{
			//Find the lowest number, and if there is one besides it
			//>sets: moreThanOneLeft, min
			{
				moreThanOneLeft = false;
				minimumSet = false;
				min = 0;
				for (i = start; i < end; i++)
				{
					curr = numbers[i];
					if (curr != 0)
					{
						if (!minimumSet)
						{
							min = curr;
							minimumSet = true;
						}
						else
						{
							moreThanOneLeft = true;
							if (curr < min)
								min = curr;
						}
					}
				}
			}
			
			if (!moreThanOneLeft)
				return min << shift;
			
			//Subtract all other numbers by min
			//Then drop all 2's from their factor-list, since at least one of the numbers is odd (min)
			//>sets: only values in numbers[]
			{
				skippedOnce = false;
				for (i = start; i < end; i++)
				{
					curr = numbers[i];
					if (curr != 0)
					{
						if (curr == min && !skippedOnce)
							skippedOnce = true;
						else
						{
							curr -= min;
							curr >>>= dcd32(curr & -curr);
			numbers[i] = curr;
						}
					}
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * Note: gcd(0, 0) is defined to be 0
	 * @return The Greatest Common Divisor between a and b
	 */
	public static int gcd(int a, int b)
	{
		if (a < 0) a = -a;
		if (b < 0) b = -b;
		
		if (a == 0) return b;
		if (b == 0) return a;
		if (a == 1 || b == 1)
			return 1;
		
		
		int gcd = (int)gcd_binary(a, b);
		
		//These will always be here, since it is such an easy check to insure no GCDs that are not CDs are returned (they may not be the *greatest* common divisior, but by jo they've definitely got to at least be a divisor!)
		if ((a % gcd) != 0 || (b % gcd) != 0)
			throw new AssertionError("Bug in GCD algorithm, for parameters: "+a+", "+b+"  (result was "+gcd+")");
		
		return gcd;
	}
	
	
	
	/**
	 * Note: gcd(0, 0) is defined to be 0
	 * @return The Greatest Common Divisor between a and b
	 */
	public static long gcd(long a, long b)
	{
		if (a < 0) a = -a;
		if (b < 0) b = -b;
		
		if (a == 0) return b;
		if (b == 0) return a;
		if (a == 1 || b == 1)
			return 1;
		
		
		long gcd = gcd_binary(a, b);
		
		//These will always be here, since it is such an easy check to insure no GCDs that are not CDs are returned (they may not be the *greatest* common divisior, but by jo they've definitely got to at least be a divisor!)
		if ((a % gcd) != 0 || (b % gcd) != 0)
			throw new AssertionError("Bug in GCD algorithm, for parameters: "+a+", "+b+"  (result was "+gcd+")");
		
		return gcd;
	}
	
	
	
	public static int gcd_euclidean(int a, int b)
	{
		if (a < 0) a = -a;
		if (b < 0) b = -b;
		
		if (a == 0) return b;
		if (b == 0) return a;
		if (a == 1 || b == 1)
			return 1;
		
		
		int mod = 0;
		while (true)
		{
			if (a == 0)
				return b;
			mod = b % a;
			b = a;
			a = mod;
		}
	}
	
	
	
	public static long gcd_binary(long a, long b)
	{
		if (a < 0) a = -a;
		if (b < 0) b = -b;
		
		if (a == 0) return b;
		if (b == 0) return a;
		if (a == 1 || b == 1)
			return 1;
		
		
		//x & -x = just the lowest 1-bit
		//dcd ≈ log2
		int aShift = dcd64(a & -a);
		int bShift = dcd64(b & -b);
		
		//Remember how many 2's they had in common
		int shift = aShift > bShift ? bShift : aShift;
		
		//Throw away the common ones since they are recorded, and throw away the extras since they can't be part of the GCD
		a >>>= aShift;
		b >>>= bShift;
		
		while (true)
		{
			if (b > a)
			{
				//Swap
				a ^= b;
				b ^= a;
				a ^= b;
			}
			
			//a and b are both odd
			//a >= b
			
			//a = (a-b)/2
			a -= b;
			
			//If a == 0, then b is the GCD (along with the common 2's we got rid of at the beginning)
			if (a == 0)
				return b << shift;
			
			//throw away the 2's in a's factor-list, since b is odd
			a >>>= dcd64(a & -a);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int safe_abs_s32(int a)
	{
		return a < 0 ? safe_neg_s32(a) : a;
	}
	
	@Nonnegative
	public static int lossyAbs(int a)
	{
		if (a < 0)
			if (a == Integer.MIN_VALUE)
				return Integer.MAX_VALUE;
			else
				return -a;
		else
			return a;
	}
	
	
	public static boolean isOverflow_neg_s32(int a)
	{
		//Two's complement has oooooone freakydeaky ness XD
		return a == Integer.MIN_VALUE;
	}
	
	public static boolean isOverflow_neg_s64(long a)
	{
		//Two's complement has oooooone freakydeaky ness XD
		return a == Long.MIN_VALUE;
	}
	
	public static boolean isOverflow_inc_s32(int a)
	{
		return a == Integer.MAX_VALUE;
	}
	
	public static boolean isOverflow_dec_s32(int a)
	{
		return a == Integer.MIN_VALUE;
	}
	
	public static boolean isOverflow_inc_s64(long a)
	{
		return a == Long.MAX_VALUE;
	}
	
	public static boolean isOverflow_dec_s64(long a)
	{
		return a == Long.MIN_VALUE;
	}
	
	
	
	public static boolean isOverflow_add_u32(@ActuallyUnsignedValue int a, @ActuallyUnsignedValue int b)
	{
		return _isOverflow_add_u32__a(a, b);
	}
	
	@ImplementationTransparency
	public static boolean _isOverflow_add_u32__a(@ActuallyUnsignedValue int a, @ActuallyUnsignedValue int b)
	{
		return Unsigned.greaterThanU32(a, 0xFFFF_FFFF - b);
	}
	
	@ImplementationTransparency
	public static boolean _isOverflow_add_u32__control(@ActuallyUnsignedValue int a, @ActuallyUnsignedValue int b)
	{
		long aa = upcast(a);
		long bb = upcast(b);
		
		long cc = aa + bb;
		
		return cc >= 0x1_0000_0000l;
	}
	
	
	
	
	public static boolean isOverflow_add_u64(@ActuallyUnsignedValue long a, @ActuallyUnsignedValue long b)
	{
		return Unsigned.greaterThanU64(a, 0xFFFF_FFFF_FFFF_FFFFl - b);
	}
	
	
	
	
	
	
	
	public static boolean isOverflow_add_s32(int a, int b)
	{
		//if (a > Integer.MAX_VALUE - b)
		//	return true;
		//if (a < Integer.MIN_VALUE - b)
		//	return true;
		//return a + b;
		
		
		//		Second Old Algorithm
		return (a ^ b) >= 0 && (a ^ (a+b)) < 0;
		
		
		//		First Old Algorithm (doesn't use a+b<0, a+b>0)
		//		if (a > 0 && b > 0)
		//		{
		//			boolean aHi = (a & HIGHEST_BIT_S32) != 0;
		//			boolean bHi = (b & HIGHEST_BIT_S32) != 0;
		//
		//			if (!aHi && !bHi)
		//			{
		//				//If neither high-bit is set, then it's impossible to overflow
		//				return a + b;
		//			}
		//			else if (aHi ^ bHi)
		//			{
		//				//If exactly one high-bit is set, then it may be possible to overflow
		//
		//				//Unset the bit
		//				if (aHi)
		//					a &= LOWER_BITS_MASK_S32;
		//				else//if (bHi)
		//					b &= LOWER_BITS_MASK_S32;
		//
		//				int c = a + b;
		//
		//				if ((c & HIGHEST_BIT_S32) == 0)
		//					return c | HIGHEST_BIT_S32;
		//				else
		//					//They carried a bit to the high-place, meaning it would have added to the highest bit, which would have carried past the edge (ie, overflowed)
		//					throw new OverflowException();
		//			}
		//			else//if (aHi && bHi)
		//			{
		//				//If both high-bits are set, then it will always overflow
		//				throw new OverflowException();
		//			}
		//		}
		//
		//
		//
		//		else if (a < 0 && b < 0)
		//		{
		//			//Unset the sign bits for now
		//			//TODo Is this necessary?
		//			a = a & MAGNITUDE_BITMASK_S32;
		//			b = b & MAGNITUDE_BITMASK_S32;
		//
		//			boolean aHi = (a & HIGHEST_BIT_S32) != 0;
		//			boolean bHi = (b & HIGHEST_BIT_S32) != 0;
		//
		//			if (!aHi && !bHi)
		//			{
		//				//If neither high-bit is set, then the sign bit won't carry over and it will underflow
		//				throw new OverflowException();
		//			}
		//			else if (aHi ^ bHi)
		//			{
		//				//If exactly one high-bit is set, then it may be possible to overflow
		//
		//				//Unset the bit
		//				if (aHi)
		//					a &= LOWER_BITS_MASK_S32;
		//				else//if (bHi)
		//					b &= LOWER_BITS_MASK_S32;
		//
		//				int c = a + b;
		//
		//				if ((c & HIGHEST_BIT_S32) == 0)
		//					throw new OverflowException();
		//				else
		//					//They carried a bit to the high-place, meaning it would have added to the highest bit, which would have carried past the edge, which means the sign bit would have been preserved, ie: no underflow
		//					return c | HIGHEST_BIT_S32 | SIGN_BIT_S32;
		//				}
		//			else//if (aHi && bHi)
		//			{
		//				//If both high-bits are set, then the sign bit will carry over and it will not underflow
		//				return a + b;
		//			}
		//		}
		//
		//
		//		//If one or both is 0, then it can't overflow
		//		//If they are opposite signs, then it can't overflow in either direction
		//
		//		return a + b;
	}
	
	public static boolean isOverflow_sub_s32(int a, int b)
	{
		//		if (a > Integer.MAX_VALUE + b)
		//			throw new OverflowException();
		//		if (a < Integer.MIN_VALUE + b)
		//			throw new OverflowException();
		//		return a - b;
		
		
		return (a ^ b) >= 0 && (a ^ (a+b)) < 0;
		
		
		//		Second Old algorithm
		//		if (b == Integer.MIN_VALUE)
		//			throw new OverflowException();
		//
		//		b = -b;
		//
		//		if ((a ^ b) >= 0 && (a ^ (a+b)) < 0)
		//			throw new OverflowException();
		//
		//		return a+b;
		
		
		//		First old algorithm
		//		return safe_add_s32(a, safe_neg_s32(b));
	}
	
	//TODO THIS IS BROKEN X'D, just try 536870912 * -4  X'D
	public static boolean isOverflow_mul_s32(int a, int b)
	{
		if (a == 0 || a == 1 || b == 0 || b == 1)
			return false;
		
		if (a == -1)
			return isOverflow_neg_s32(b);
		if (b == -1)
			return isOverflow_neg_s32(a);
		
		
		
		//Todo this might be a little too safe, making false-positives; we should check that ^^'
		
		if (isOverflow_neg_s32(a) || isOverflow_neg_s32(b))
			return true;
		
		a = abs(a);
		b = abs(b);
		
		int ah = Integer.highestOneBit(a);
		int bh = Integer.highestOneBit(b);
		
		int ab = dcd32(ah) + 1;
		int bb = dcd32(bh) + 1;
		
		//Todo perhaps find the exact place where the overflow would occur to reduce false-positives? ^^''
		boolean neitherArePowers = a != ah && b != bh;
		int cb = neitherArePowers ? (ab + bb) : (ab + bb - 1);
		
		return cb > 31;
		
		
		
		//		if (b == 0)
		//			return 0;
		//
		//		if (a > Integer.MAX_VALUE / b)
		//			throw new OverflowException();
		//
		//		if (a < (Integer.MIN_VALUE % b == 0 ? Integer.MIN_VALUE / b : Integer.MIN_VALUE / b + 1))
		//			throw new OverflowException();
		//
		//		return a * b;
	}
	
	public static boolean isOverflow_add_s64(long a, long b)
	{
		return (a ^ b) >= 0 && (a ^ (a+b)) < 0;
	}
	
	public static boolean isOverflow_sub_s64(long a, long b)
	{
		return (a ^ b) >= 0 && (a ^ (a+b)) < 0;
	}
	
	public static boolean isOverflow_mul_s64(long a, long b)
	{
		if (a == 0 || a == 1 || b == 0 || b == 1)
			return false;
		
		if (a == -1)
			return isOverflow_neg_s64(b);
		if (b == -1)
			return isOverflow_neg_s64(a);
		
		
		
		//Todo this might be a little too safe, making false-positives; we should check that ^^'
		
		if (isOverflow_neg_s64(a) || isOverflow_neg_s64(b))
			return true;
		
		a = abs(a);
		b = abs(b);
		
		long aHigh = Long.highestOneBit(a);
		long bHigh = Long.highestOneBit(b);
		
		int nBitsRequiredForA = dcd64(aHigh) + 1;
		int nBitsRequiredForB = dcd64(bHigh) + 1;
		
		//Todo perhaps find the exact place where the overflow would occur to reduce false-positives? ^^''
		boolean neitherArePowers = a != aHigh && b != bHigh;
		int maximumNBitsRequiredForResult = neitherArePowers ? (nBitsRequiredForA + nBitsRequiredForB) : (nBitsRequiredForA + nBitsRequiredForB - 1);
		
		return maximumNBitsRequiredForResult > 63;
		
		
		
		
		//		if (b == 0)
		//			return 0;
		//
		//		if (a > Long.MAX_VALUE / b)
		//			throw new OverflowException();
		//
		//		if (a < (Long.MIN_VALUE % b == 0 ? Long.MIN_VALUE / b : Long.MIN_VALUE / b + 1))
		//			throw new OverflowException();
		//
		//		return a * b;
	}
	
	public static int safe_neg_s32(int a) throws OverflowException
	{
		if (isOverflow_neg_s32(a))
			throw new OverflowException();
		
		return -a;
	}
	
	public static int safe_inc_s32(int a) throws OverflowException
	{
		if (isOverflow_inc_s32(a))
			throw new OverflowException();
		
		return a + 1;
	}
	
	public static int safe_dec_s32(int a) throws OverflowException
	{
		if (isOverflow_dec_s32(a))
			throw new OverflowException();
		
		return a - 1;
	}
	
	public static int safe_add_s32(int a, int b) throws OverflowException
	{
		if (isOverflow_add_s32(a, b))
			throw new OverflowException();
		
		return a + b;
	}
	
	public static int safe_sub_s32(int a, int b) throws OverflowException
	{
		if (isOverflow_sub_s32(a, b))
			throw new OverflowException();
		
		return a + b;
	}
	
	public static int safe_mul_s32(int a, int b) throws OverflowException
	{
		if (isOverflow_mul_s32(a, b))
			throw new OverflowException();
		
		return a * b;
	}
	
	public static long safe_neg_s64(long a) throws OverflowException
	{
		if (isOverflow_neg_s64(a))
			throw new OverflowException();
		
		return -a;
	}
	
	public static long safe_inc_s64(long a) throws OverflowException
	{
		if (isOverflow_inc_s64(a))
			throw new OverflowException();
		
		return a + 1;
	}
	
	public static long safe_dec_s64(long a) throws OverflowException
	{
		if (isOverflow_dec_s64(a))
			throw new OverflowException();
		
		return a - 1;
	}
	
	public static long safe_add_s64(long a, long b) throws OverflowException
	{
		if (isOverflow_add_s64(a, b))
			throw new OverflowException();
		
		return a + b;
	}
	
	public static long safe_sub_s64(long a, long b) throws OverflowException
	{
		if (isOverflow_sub_s64(a, b))
			throw new OverflowException();
		
		return a - b;
	}
	
	public static long safe_mul_s64(long a, long b) throws OverflowException
	{
		if (isOverflow_mul_s64(a, b))
			throw new OverflowException();
		
		return a * b;
	}
	
	/*
	 * Protects from remainder-producing divisions, rather than overflows
	 */
	public static int safe_div_s32(int a, int b) throws TruncationException, DivisionByZeroException
	{
		if (b == 0)
			throw new DivisionByZeroException();
		
		if (a % b != 0)
			throw new TruncationException("Lossy integer division");
		
		return a / b;
	}
	
	/*
	 * Protects from remainder-producing divisions, rather than overflows
	 */
	public static long safe_div_s64(long a, long b) throws TruncationException, DivisionByZeroException
	{
		if (b == 0)
			throw new DivisionByZeroException();
		
		if (a % b != 0)
			throw new TruncationException("Lossy integer division");
		
		return a / b;
	}
	
	public static int losslessDivision(int numerator, int divisor) throws ArithmeticException
	{
		if ((numerator % divisor) != 0)
			throw new ArithmeticException("Lossy division detected");
		return numerator / divisor;
	}
	
	public static long losslessDivision(long numerator, long divisor) throws ArithmeticException
	{
		if ((numerator % divisor) != 0)
			throw new ArithmeticException("Lossy division detected");
		return numerator / divisor;
	}
	
	public static int floorDivision(int numerator, int divisor)
	{
		boolean negative = (numerator < 0) ^ (divisor < 0);
		
		if (negative)
		{
			//For negatives, nearzero (Java) >= floor
			
			int nearzeroQuotient = numerator / divisor;
			
			if ((nearzeroQuotient) * divisor == numerator) //division without remainder is equal in any truncation/rounding scheme
			{
				return nearzeroQuotient;
			}
			else
			{
				//nearzero returns the integer nearer to zero, which is 1 greater than floor.
				return nearzeroQuotient - 1;
			}
		}
		else
		{
			//For positives, nearzero (Java) = floor
			
			//TODO is this necessary?
			if (numerator < 0 && divisor < 0)
			{
				numerator = -numerator;
				divisor = -divisor;
			}
			
			return numerator / divisor;
		}
	}
	
	public static long floorDivision(long numerator, long divisor)
	{
		boolean negative = (numerator < 0) ^ (divisor < 0);
		
		if (negative)
		{
			//For negatives, nearzero (Java) >= floor
			
			long nearzeroQuotient = numerator / divisor;
			
			if ((nearzeroQuotient) * divisor == numerator) //division without remainder is equal in any truncation/rounding scheme
			{
				return nearzeroQuotient;
			}
			else
			{
				//nearzero returns the integer nearer to zero, which is 1 greater than floor.
				return nearzeroQuotient - 1;
			}
		}
		else
		{
			//For positives, nearzero (Java) = floor
			
			//TODO is this necessary?
			if (numerator < 0 && divisor < 0)
			{
				numerator = -numerator;
				divisor = -divisor;
			}
			
			return numerator / divisor;
		}
	}
	
	
	
	
	
	//TODO Error: floorLog(1 000 000, 10) == 5
	public static int floorLog(int value, int base) throws ArithmeticException
	{
		if (value <= 0)
			throw new ArithmeticException("Log of non-positive integer");
		
		double v = Math.log(value) / Math.log(base);
		
		if (v > Integer.MAX_VALUE || v < Integer.MIN_VALUE)
			throw new OverflowException();
		
		int exponent = (int)v;
		
		//Todo check back through pow() to determine if it's accurate
		
		return exponent;
	}
	
	public static int signum(long a)
	{
		//return (a < 0) ? -1 : (a == 0) ? 0 : 1;
		return a > 0 ? 1 : (a < 0 ? -1 : 0);
	}
	
	public static int signum(int a)
	{
		return a > 0 ? 1 : (a < 0 ? -1 : 0);
	}
	
	public static int signum(short a)
	{
		return a > 0 ? 1 : (a < 0 ? -1 : 0);
	}
	
	public static int signum(byte a)
	{
		return a > 0 ? 1 : (a < 0 ? -1 : 0);
	}
	
	public static int signum(char a)
	{
		return a > 0 ? 1 : 0;   // ^_~
	}
	
	public static long truncate(long x, long min, long max)
	{
		if (x < min) return min;
		if (x > max) return max;
		return x;
	}
	
	public static int truncate(int x, int min, int max)
	{
		if (x < min) return min;
		if (x > max) return max;
		return x;
	}
	
	public static short truncate(short x, short min, short max)
	{
		if (x < min) return min;
		if (x > max) return max;
		return x;
	}
	
	public static char truncate(char x, char min, char max)
	{
		if (x < min) return min;
		if (x > max) return max;
		return x;
	}
	
	public static byte truncate(byte x, byte min, byte max)
	{
		if (x < min) return min;
		if (x > max) return max;
		return x;
	}
	
	public static byte least(byte... values)
	{
		if (values.length == 0)
			throw new IllegalArgumentException();
		
		byte e = values[0];
		for (int i = 1; i < values.length; i++)
		{
			if (values[i] < e)
				e = values[i];
		}
		
		return e;
	}
	
	public static byte greatest(byte... values)
	{
		if (values.length == 0)
			throw new IllegalArgumentException();
		
		byte e = values[0];
		for (int i = 1; i < values.length; i++)
		{
			if (values[i] > e)
				e = values[i];
		}
		
		return e;
	}
	
	public static char least(char... values)
	{
		if (values.length == 0)
			throw new IllegalArgumentException();
		
		char e = values[0];
		for (int i = 1; i < values.length; i++)
		{
			if (values[i] < e)
				e = values[i];
		}
		
		return e;
	}
	
	public static char greatest(char... values)
	{
		if (values.length == 0)
			throw new IllegalArgumentException();
		
		char e = values[0];
		for (int i = 1; i < values.length; i++)
		{
			if (values[i] > e)
				e = values[i];
		}
		
		return e;
	}
	
	public static short least(short... values)
	{
		if (values.length == 0)
			throw new IllegalArgumentException();
		
		short e = values[0];
		for (int i = 1; i < values.length; i++)
		{
			if (values[i] < e)
				e = values[i];
		}
		
		return e;
	}
	
	public static short greatest(short... values)
	{
		if (values.length == 0)
			throw new IllegalArgumentException();
		
		short e = values[0];
		for (int i = 1; i < values.length; i++)
		{
			if (values[i] > e)
				e = values[i];
		}
		
		return e;
	}
	
	public static int least(int... values)
	{
		if (values.length == 0)
			throw new IllegalArgumentException();
		
		int e = values[0];
		for (int i = 1; i < values.length; i++)
		{
			if (values[i] < e)
				e = values[i];
		}
		
		return e;
	}
	
	public static int greatest(int... values)
	{
		if (values.length == 0)
			throw new IllegalArgumentException();
		
		int e = values[0];
		for (int i = 1; i < values.length; i++)
		{
			if (values[i] > e)
				e = values[i];
		}
		
		return e;
	}
	
	public static long least(long... values)
	{
		if (values.length == 0)
			throw new IllegalArgumentException();
		
		long e = values[0];
		for (int i = 1; i < values.length; i++)
		{
			if (values[i] < e)
				e = values[i];
		}
		
		return e;
	}
	
	public static long greatest(long... values)
	{
		if (values.length == 0)
			throw new IllegalArgumentException();
		
		long e = values[0];
		for (int i = 1; i < values.length; i++)
		{
			if (values[i] > e)
				e = values[i];
		}
		
		return e;
	}
	
	public static byte least(byte a, byte b)
	{
		return b < a ? b : a;
	}
	
	public static byte greatest(byte a, byte b)
	{
		return b > a ? b : a;
	}
	
	public static byte least(byte a, byte b, byte c)
	{
		if (c > a && c > b)
			return least(a, b);
		else if (b > a && b > c)
			return least(a, c);
		else if (a > b && a > c)
			return least(b, c);
		else
			throw new AssertionError();
	}
	
	public static byte greatest(byte a, byte b, byte c)
	{
		if (c < a && c < b)
			return greatest(a, b);
		else if (b < a && b < c)
			return greatest(a, c);
		else if (a < b && a < c)
			return greatest(b, c);
		else
			throw new AssertionError();
	}
	
	public static byte least(byte a, byte b, byte c, byte d)
	{
		if (d > a && d > b && d > c)
			return least(a, b, c);
		else if (c > a && c > b && c > d)
			return least(a, b, d);
		else if (b > a && b > c && b > d)
			return least(a, c, d);
		else if (a > b && a > c && a > d)
			return least(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static byte greatest(byte a, byte b, byte c, byte d)
	{
		if (d < a && d < b && d < c)
			return greatest(a, b, c);
		else if (c < a && c < b && c < d)
			return greatest(a, b, d);
		else if (b < a && b < c && b < d)
			return greatest(a, c, d);
		else if (a < b && a < c && a < d)
			return greatest(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static char least(char a, char b)
	{
		return b < a ? b : a;
	}
	
	public static char greatest(char a, char b)
	{
		return b > a ? b : a;
	}
	
	public static char least(char a, char b, char c)
	{
		if (c > a && c > b)
			return least(a, b);
		else if (b > a && b > c)
			return least(a, c);
		else if (a > b && a > c)
			return least(b, c);
		else
			throw new AssertionError();
	}
	
	public static char greatest(char a, char b, char c)
	{
		if (c < a && c < b)
			return greatest(a, b);
		else if (b < a && b < c)
			return greatest(a, c);
		else if (a < b && a < c)
			return greatest(b, c);
		else
			throw new AssertionError();
	}
	
	public static char least(char a, char b, char c, char d)
	{
		if (d > a && d > b && d > c)
			return least(a, b, c);
		else if (c > a && c > b && c > d)
			return least(a, b, d);
		else if (b > a && b > c && b > d)
			return least(a, c, d);
		else if (a > b && a > c && a > d)
			return least(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static char greatest(char a, char b, char c, char d)
	{
		if (d < a && d < b && d < c)
			return greatest(a, b, c);
		else if (c < a && c < b && c < d)
			return greatest(a, b, d);
		else if (b < a && b < c && b < d)
			return greatest(a, c, d);
		else if (a < b && a < c && a < d)
			return greatest(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static short least(short a, short b)
	{
		return b < a ? b : a;
	}
	
	public static short greatest(short a, short b)
	{
		return b > a ? b : a;
	}
	
	public static short least(short a, short b, short c)
	{
		if (c > a && c > b)
			return least(a, b);
		else if (b > a && b > c)
			return least(a, c);
		else if (a > b && a > c)
			return least(b, c);
		else
			throw new AssertionError();
	}
	
	public static short greatest(short a, short b, short c)
	{
		if (c < a && c < b)
			return greatest(a, b);
		else if (b < a && b < c)
			return greatest(a, c);
		else if (a < b && a < c)
			return greatest(b, c);
		else
			throw new AssertionError();
	}
	
	public static short least(short a, short b, short c, short d)
	{
		if (d > a && d > b && d > c)
			return least(a, b, c);
		else if (c > a && c > b && c > d)
			return least(a, b, d);
		else if (b > a && b > c && b > d)
			return least(a, c, d);
		else if (a > b && a > c && a > d)
			return least(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static short greatest(short a, short b, short c, short d)
	{
		if (d < a && d < b && d < c)
			return greatest(a, b, c);
		else if (c < a && c < b && c < d)
			return greatest(a, b, d);
		else if (b < a && b < c && b < d)
			return greatest(a, c, d);
		else if (a < b && a < c && a < d)
			return greatest(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static int greatest(int a, int b)
	{
		return b > a ? b : a;
	}
	
	public static int least(int a, int b, int c)
	{
		if (c > a && c > b)
			return least(a, b);
		else if (b > a && b > c)
			return least(a, c);
		else if (a > b && a > c)
			return least(b, c);
		else
			throw new AssertionError();
	}
	
	public static int greatest(int a, int b, int c)
	{
		if (c < a && c < b)
			return greatest(a, b);
		else if (b < a && b < c)
			return greatest(a, c);
		else if (a < b && a < c)
			return greatest(b, c);
		else
			throw new AssertionError();
	}
	
	public static int least(int a, int b, int c, int d)
	{
		if (d > a && d > b && d > c)
			return least(a, b, c);
		else if (c > a && c > b && c > d)
			return least(a, b, d);
		else if (b > a && b > c && b > d)
			return least(a, c, d);
		else if (a > b && a > c && a > d)
			return least(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static int greatest(int a, int b, int c, int d)
	{
		if (d < a && d < b && d < c)
			return greatest(a, b, c);
		else if (c < a && c < b && c < d)
			return greatest(a, b, d);
		else if (b < a && b < c && b < d)
			return greatest(a, c, d);
		else if (a < b && a < c && a < d)
			return greatest(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static long least(long a, long b)
	{
		return b < a ? b : a;
	}
	
	public static long greatest(long a, long b)
	{
		return b > a ? b : a;
	}
	
	public static long ceilingDivision(long numerator, long divisor)
	{
		return floorDivision(numerator, divisor) + (numerator % divisor != 0 ? 1 : 0); //modulo == 0 will work regardless of modulo-vs-modulus handling of negative things :>
	}
	
	public static int progmod(int index, int highBound)
	{
		if (highBound == 0)
			throw new DivisionByZeroException();
		
		//does this work? is it fasters? :>
		return (index % highBound + highBound) % highBound;
		//edit: seems to! :D!
		
		//		if (index >= 0)
		//			return index % highBound;
		//		else //if (n < 0)
		//			return index - (floorDivision(index, highBound)*highBound);
	}
	
	public static long progmod(long index, long highBound)
	{
		if (highBound == 0)
			throw new DivisionByZeroException();
		
		//does this work? is it fasters? :>
		return (index % highBound + highBound) % highBound;
		//edit: seems to! :D!
		
		//		if (index >= 0)
		//			return index % highBound;
		//		else //if (n < 0)
		//			return index - (floorDivision(index, highBound)*highBound);
	}
	
	public static char checkNotZeroForDivide(char x)
	{
		if (x == 0)
			throw new DivisionByZeroException();
		return x;
	}
	
	public static Character checkNotZeroForDivide(Character x)
	{
		if (x == 0)
			throw new DivisionByZeroException();
		return x;
	}
	
	public static int lastBlockLength(int entireSizeAkaNumerator, int blockSizeAkaDenominator)
	{
		int r = entireSizeAkaNumerator % blockSizeAkaDenominator;
		return r == 0 ? blockSizeAkaDenominator : r;
	}
	
	public static long lastBlockLength(long entireSizeAkaNumerator, long blockSizeAkaDenominator)
	{
		long r = entireSizeAkaNumerator % blockSizeAkaDenominator;
		return r == 0 ? blockSizeAkaDenominator : r;
	}
	
	public static int pow(int base, int length) throws ArithmeticException, OverflowException, TruncationException
	{
		//TODO implement properly!!
		long rv = pow((long)base, (long)length);
		if (((int)rv) != rv)
			throw new OverflowException();
		return (int)rv;
	}
	
	public static long pow(long base, long exponent) throws ArithmeticException, OverflowException, TruncationException
	{
		return pow_new(base, exponent);
	}
	
	public static long pow_old(long base, long exponent) throws ArithmeticException, OverflowException, TruncationException
	{
		if (base == 1)
			return 1;
		
		if (exponent == 0)
			if (base == 0)
				throw new ArithmeticException("0^0 is undefined");
			else
				return 1;
		
		if (base == -1)
			return exponent % 2 == 0 ? 1 : -1;
		
		if (exponent < 0)
			if (base == 0)
				throw new ArithmeticException("0^(-P) is undefined (division by zero)");
			else if (base == 1 && exponent == -1)
				return 1;
		//else if (base == -1 && exponent == -1)
		//	return -1;
			else
				throw new TruncationException("x^(-P) is not an integer");
		
		if (base == 0)
			return 0;
		
		
		
		boolean negativeBase = base < 0;
		if (negativeBase) base = -base;
		
		
		//TODO do proper overflow checking
		{
			double quickCheck = Math.pow(base, exponent);
			if (quickCheck > Long.MAX_VALUE)
				throw new OverflowException();
		}
		
		
		//TODO much faster implementation (preferably O(log(N)))
		long rv = 1;
		{
			long l = exponent;
			while (l > 0)
			{
				if ((negativeBase && -rv * base > -rv) || (!negativeBase && rv * base < rv))
					throw new OverflowException();
				
				rv *= base;
				l--;
			}
		}
		
		if (negativeBase && exponent % 2 != 0) //even powers are always positive; odd powers preserve the base's sign
			return -rv;
		else
			return rv;
	}
	
	/*
	 * TODO Do this correctly with multiple strategies for handling controversial points :>
	 * 
	 * Rounding integer division:
	 * 		n \ d = integer division
	 * 		n / d = theoretical continuous division  (or discrete rational division! :D )
	 * 
	 * 		 |========|========|========|========|========|========|========|========|========|========|========|========|========|
	 * 		-3      -2.5      -2      -1.5      -1      -0.5       0       0.5       1       1.5       2       2.5       3       3.5
	 * 
	 * 		1 / 4 = 0.25                                               |
	 * 		1 / 2 = 0.5                                                     |
	 * 		2 / 3 = 0.66*                                                      |
	 * 		3 / 3 = 1.0                                                              |
	 * 
	 * 		What counts as 1                                                !+++++++++++++++++
	 * 		What counts as 2                                                                  !+++++++++++++++++
	 * 		What counts as -1            +++++++++++++++++!
	 * 		What counts as 0                               +++++++++++++++++
	 * 		etc.c.
	 * 
	 * 	+ Bottom line is: the equidistant point (x.5) belongs to the integer with the higher magnitude; this makes sense because intervals are usually exclusive at their far extremity.
	 * 
	 * I'm not sure how the fact that 0 therefore has less points which map to it will bias things, but this is probably more arithmetically sound than an asymmetrical strategy, which it would have to be to keep equal size of mappings (eg, floor or ceiling).
	 * 
	 * 
	 * For positive results:
	 * 		Floor/nearzero (Java) integer division will sometimes return 1 less than the correct result.
	 * 		The condition under which this happens is: numerator / divisor - floor(numerator / divisor) >= 0.5
	 * 
	 * 		numerator / divisor - floor(numerator / divisor) >= 0.5
	 * 		(numerator - floor(numerator / divisor)*divisor) / divisor >= 0.5
	 * 		remainder / divisor >= 0.5
	 * 		remainder / divisor * 2 >= 1
	 * 
	 * 		If divisor is > 0 (can't be = 0)
	 * 			remainder / divisor * 2 >= 1
	 * 			remainder * 2 >= divisor
	 * 		If divisor is < 0
	 * 			remainder / divisor * 2 >= 1
	 * 			remainder * 2 <= divisor
	 * 
	 * 
	 * 
	 * For negative results:
	 * 		NOTE: Java integer division is not floor; it is closest-to-zero (which only differs from floor for negative results)
	 * 
	 * 		+ Just take the absolute value of the operands, then negate the result.
	 */
	public static int roundingIntegerDivision(int numerator, int divisor)
	{
		int negative = 0;
		
		if (numerator < 0)
		{
			numerator = -numerator;
			
			if (divisor < 0)
			{
				negative = 1;
				divisor = -divisor;
			}
			else
			{
				negative = -1;
			}
		}
		else
		{
			if (divisor < 0)
			{
				negative = -1;
				divisor = -divisor;
			}
			else
			{
				negative = 1;
			}
		}
		
		
		int result = numerator / divisor; //the ArithmeticException for dividing by zero will occur here
		
		int remainder = numerator % divisor;
		
		if (remainder * 2 >= divisor)
			result++;
		
		return negative*result;
	}
	
	public static long roundingIntegerDivision(long numerator, long divisor)
	{
		int negative = 0;
		
		if (numerator < 0)
		{
			numerator = -numerator;
			
			if (divisor < 0)
			{
				negative = 1;
				divisor = -divisor;
			}
			else
			{
				negative = -1;
			}
		}
		else
		{
			if (divisor < 0)
			{
				negative = -1;
				divisor = -divisor;
			}
			else
			{
				negative = 1;
			}
		}
		
		
		long result = numerator / divisor; //the ArithmeticException for dividing by zero will occur here
		
		long remainder = numerator % divisor;
		
		if (remainder * 2 >= divisor)
			result++;
		
		return negative*result;
	}
	
	
	public static int pow_new(int base, int exponent) throws ArithmeticException
	{
		if (base == 2)
			return powb2_s32(exponent); //faster impl ^,^
		
		
		if (exponent == 0 && base == 0)
			throw new ArithmeticException("0^0 is undefined");
		
		if (exponent < 0)
		{
			if (base == 0)
				throw new ArithmeticException("Division by zero!  (x^(-p) = 1/(x^p); 0^(-p) = 1/(0^p) = 1/0");
			else if (base == 1)
				return 1;
			else if (base == -1)
				return exponent % 2 == 0 ? 1 : -1;
			else
				throw new TruncationException("x^(-P) is not an integer");
		}
		
		long v = 1;
		
		while (exponent > 0) //so if exponent == 0, result is 1
		{
			v *= base;
			if (v > Integer.MAX_VALUE || v < Integer.MIN_VALUE)
				throw new OverflowException();
			exponent--;
		}
		
		return (int)v;
	}
	
	public static long pow_new(long base, long exponent) throws ArithmeticException
	{
		if (base == 2)
			return powb2_s64(exponent); //faster impl ^,^
		
		
		if (exponent == 0 && base == 0)
			throw new ArithmeticException("0^0 is undefined");
		
		if (exponent < 0)
		{
			if (base == 0)
				throw new ArithmeticException("Division by zero!  (x^(-p) = 1/(x^p); 0^(-p) = 1/(0^p) = 1/0");
			else if (base == 1)
				return 1;
			else if (base == -1)
				return exponent % 2 == 0 ? 1 : -1;
			else
				throw new TruncationException("x^(-P) is not an integer");
		}
		
		if (base == 0)
			//exponent will be > 0 from the other checks
			return 0;
		
		long pv = 0;
		long v = 1;
		while (exponent > 0) //so if exponent == 0, result is 1
		{
			pv = v;
			v *= base;
			
			if (v / base != pv)
				throw new OverflowException();
			
			exponent--;
		}
		
		return v;
	}
	
	public static int powb2_s32(int exponent)
	{
		if (exponent < 0)
			throw new TruncationException("2^(-P) is not an integer");
		
		if (exponent >= 31)
			throw new OverflowException();
		
		return 1 << exponent;
	}
	
	public static long powb2_s64(long exponent)
	{
		if (exponent < 0)
			throw new TruncationException("2^(-P) is not an integer");
		
		if (exponent >= 31)
			throw new OverflowException();
		
		return 1 << exponent;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int safeCastS8toS32(byte input)
	{
		return input;
	}
	
	public static int safeCastS16toS32(short input)
	{
		return input;
	}
	
	public static int safeCastU16toS32(char input)
	{
		return input;
	}
	
	public static int safeCastS32toS32(int input)
	{
		return input;
	}
	
	public static long safeCastS8toS64(byte input)
	{
		return input;
	}
	
	public static long safeCastS16toS64(short input)
	{
		return input;
	}
	
	public static long safeCastU16toS64(char input)
	{
		return input;
	}
	
	public static long safeCastS32toS64(int input)
	{
		return input;
	}
	
	public static long safeCastS64toS64(long input)
	{
		return input;
	}
	
	public static byte safeCastU16toS8(char input)
	{
		if (input > Byte.MAX_VALUE || input < Byte.MIN_VALUE)
			throw new OverflowException("(U16)"+input+" -> S8");
		return (byte)input;
	}
	
	public static short safeCastU16toS16(char input)
	{
		if (input > Short.MAX_VALUE || input < Short.MIN_VALUE)
			throw new OverflowException("(U16)"+input+" -> S16");
		return (short)input;
	}
	
	//<Discrete frames
	public static int pyr(int l)
	{
		return (l*l + l)/2;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static long[] reduceRat(long n, long d)
	{
		long gcd = gcd(n, d);
		
		assert n % gcd == 0;
		assert d % gcd == 0;
		
		return new long[]{n / gcd, d / gcd};
	}
	
	public static long[] reduceRat(long[] r)
	{
		assert r.length == 2;
		return reduceRat(r[0], r[1]);
	}
	
	
	
	
	public static long[] addRatRaw(long aN, long aD, long bN, long bD)
	{
		long newDen = safe_mul_s64(aD, bD);
		return new long[]{safe_add_s64(safe_mul_s64(aN, bD), safe_mul_s64(bN, aD)), newDen};
	}
	
	public static long[] mulRatRaw(long aN, long aD, long bN, long bD)
	{
		return new long[]{safe_mul_s64(aN, bN), safe_mul_s64(aD, bD)};
	}
	
	
	public static long[] subRatRaw(long aN, long aD, long bN, long bD)
	{
		return addRatRaw(aN, aD, -bN, bD);
	}
	
	public static long[] divRatRaw(long aN, long aD, long bN, long bD)
	{
		return mulRatRaw(aN, aD,  bD, bN);  //note the commutation! ;D
	}
	
	
	
	
	public static long[] addRatReducing(long aN, long aD, long bN, long bD)
	{
		//TODO!!
		throw new NotYetImplementedException();
	}
	
	public static long[] mulRatReducing(long aN, long aD, long bN, long bD)
	{
		return reduceRat(mulRatRaw(aN, aD, bN, bD));
	}
	
	
	public static long[] subRatReducing(long aN, long aD, long bN, long bD)
	{
		return addRatReducing(aN, aD, -bN, bD);
	}
	
	public static long[] divRatReducing(long aN, long aD, long bN, long bD)
	{
		return mulRatReducing(aN, aD,  bD, bN);  //note the commutation! ;D
	}
	
	
	
	
	
	
	
	
	
	
	public static int losslessLogBase2(int value) throws TruncationException
	{
		if (value == 0)
			throw new OutOfDomainArithmeticException("log(0)");
		
		if (value < 0) //takes care of two's-complement thingies :3
			throw new ComplexNumberArithmeticException();
		
		int exponent = BitUtilities.dcd32(value);
		
		if (powb2_s32(exponent) != value)
			throw new TruncationException("result was not integer");
		
		return exponent;
	}
	
	public static int floorLogBase2(int value)
	{
		if (value == 0)
			throw new OutOfDomainArithmeticException("log(0)");
		
		if (value < 0) //takes care of two's-complement thingies :3
			throw new ComplexNumberArithmeticException();
		
		return dcd32(getHighestOneBit(value));
	}
	
	public static int ceilLogBase2(int value)
	{
		if (value == 0)
			throw new OutOfDomainArithmeticException("log(0)");
		
		if (value < 0) //takes care of two's-complement thingies :3
			throw new ComplexNumberArithmeticException();
		
		int h = getHighestOneBit(value);
		return dcd32(h) + (value != h ? 1 : 0);
	}
	
	
	
	
	
	
	
	
	public static boolean commutativePairEq(int a, int b,   int x, int y)
	{
		return (a == x && b == y) || (a == y && b == x);
	}
	
	public static int log2(long value)
	{
		if (value == 0)
			throw new OutOfDomainArithmeticException("log(0)");
		
		if (value < 0) //takes care of two's-complement thingies :3
			throw new ComplexNumberArithmeticException();
		
		int exponent = BitUtilities.dcd64(value);
		
		if (powb2_s32(exponent) != value)
			throw new TruncationException("result was not integer");
		
		return exponent;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static boolean doesIntegerIntervalOverflowByteWithIntSize(byte start, @Nonnegative int size)
	{
		return start + size < 128;
	}
	
	public static boolean doesIntegerIntervalOverflowShortWithIntSize(short start, @Nonnegative int size)
	{
		return start + size < 32768;
	}
	
	public static boolean doesIntegerIntervalOverflowCharWithIntSize(char start, @Nonnegative int size)
	{
		return start + size < 65536;
	}
	
	public static boolean doesIntegerIntervalOverflowIntWithIntSize(int start, @Nonnegative int size)
	{
		return isOverflow_add_s32(start, size);
	}
	
	public static boolean doesIntegerIntervalOverflowLongWithIntSize(long start, @Nonnegative int size)
	{
		return isOverflow_add_s64(start, size);
	}
}
