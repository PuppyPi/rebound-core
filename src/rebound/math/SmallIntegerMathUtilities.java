package rebound.math;

import static java.lang.Math.*;
import static rebound.bits.BitUtilities.*;
import static rebound.bits.Unsigned.*;
import static rebound.math.SmallFloatMathUtilities.*;
import static rebound.testing.WidespreadTestingUtilities.*;
import static rebound.util.CodeHinting.*;
import java.util.Comparator;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Signed;
import jx.lang.UnsignedByte;
import jx.lang.UnsignedInteger;
import jx.lang.UnsignedLong;
import jx.lang.UnsignedShort;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.annotations.semantic.simpledata.ActuallyUnsigned;
import rebound.annotations.semantic.simpledata.Negative;
import rebound.annotations.semantic.simpledata.Nonpositive;
import rebound.annotations.semantic.simpledata.Nonzero;
import rebound.annotations.semantic.simpledata.Positive;
import rebound.bits.BitUtilities;
import rebound.bits.Unsigned;
import rebound.exceptions.DivisionByZeroException;
import rebound.exceptions.NotYetImplementedException;
import rebound.exceptions.OutOfDomainArithmeticException;
import rebound.exceptions.OutOfDomainArithmeticException.ComplexNumberArithmeticException;
import rebound.exceptions.OverflowException;
import rebound.exceptions.TruncationException;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.collections.prim.PrimitiveCollections.CharacterList;
import rebound.util.collections.prim.PrimitiveCollections.IntegerList;
import rebound.util.collections.prim.PrimitiveCollections.LongList;
import rebound.util.collections.prim.PrimitiveCollections.ShortList;

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
	
	
	
	public static int ceildiv(int numerator, int divisor)
	{
		return ceilingDivision(numerator, divisor);
	}
	
	public static long ceildiv(long numerator, long divisor)
	{
		return ceilingDivision(numerator, divisor);
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
	
	
	
	
	/**
	 * Like modulus but only wraps to 0
	 */
	public static byte wrap(byte x, byte exclusiveHighBound)
	{
		return x == exclusiveHighBound ? 0 : x;
	}
	
	/**
	 * Like modulus but only wraps to 0
	 */
	public static short wrap(short x, short exclusiveHighBound)
	{
		return x == exclusiveHighBound ? 0 : x;
	}
	
	/**
	 * Like modulus but only wraps to 0
	 */
	public static char wrap(char x, char exclusiveHighBound)
	{
		return x == exclusiveHighBound ? 0 : x;
	}
	
	/**
	 * Like modulus but only wraps to 0
	 */
	public static int wrap(int x, int exclusiveHighBound)
	{
		return x == exclusiveHighBound ? 0 : x;
	}
	
	/**
	 * Like modulus but only wraps to 0
	 */
	public static long wrap(long x, long exclusiveHighBound)
	{
		return x == exclusiveHighBound ? 0 : x;
	}
	
	
	
	
	
	
	
	public static Comparator<Byte> ByteComparison = (a, b) -> cmp((byte)a, (byte)b);
	public static Comparator<Short> ShortComparison = (a, b) -> cmp((short)a, (short)b);
	public static Comparator<Character> CharacterComparison = (a, b) -> cmp((char)a, (char)b);
	public static Comparator<Integer> IntComparison = (a, b) -> cmp((int)a, (int)b);
	public static Comparator<Long> LongComparison = (a, b) -> cmp((long)a, (long)b);
	
	
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
		return cmp(a.longValue(), b.longValue());
	}
	
	public static int cmpNullAsNinf(@Nullable Integer a, @Nullable Integer b)
	{
		if (a == null) return b == null ? 0 : -1;
		if (b == null) return 1;
		return cmp(a.intValue(), b.intValue());
	}
	
	public static int cmpNullAsNinf(@Nullable Short a, @Nullable Short b)
	{
		if (a == null) return b == null ? 0 : -1;
		if (b == null) return 1;
		return cmp(a.shortValue(), b.shortValue());
	}
	
	public static int cmpNullAsNinf(@Nullable Byte a, @Nullable Byte b)
	{
		if (a == null) return b == null ? 0 : -1;
		if (b == null) return 1;
		return cmp(a.byteValue(), b.byteValue());
	}
	
	public static int cmpNullAsNinf(@Nullable Character a, @Nullable Character b)
	{
		if (a == null) return b == null ? 0 : -1;
		if (b == null) return 1;
		return cmp(a.charValue(), b.charValue());
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
	
	
	
	
	
	
	
	
	
	public static int cmpNullAsPinf(@Nullable Long a, @Nullable Long b)
	{
		if (a == null) return b == null ? 0 : 1;
		if (b == null) return -1;
		return cmp(a.longValue(), b.longValue());
	}
	
	public static int cmpNullAsPinf(@Nullable Integer a, @Nullable Integer b)
	{
		if (a == null) return b == null ? 0 : 1;
		if (b == null) return -1;
		return cmp(a.intValue(), b.intValue());
	}
	
	public static int cmpNullAsPinf(@Nullable Short a, @Nullable Short b)
	{
		if (a == null) return b == null ? 0 : 1;
		if (b == null) return -1;
		return cmp(a.shortValue(), b.shortValue());
	}
	
	public static int cmpNullAsPinf(@Nullable Byte a, @Nullable Byte b)
	{
		if (a == null) return b == null ? 0 : 1;
		if (b == null) return -1;
		return cmp(a.byteValue(), b.byteValue());
	}
	
	public static int cmpNullAsPinf(@Nullable Character a, @Nullable Character b)
	{
		if (a == null) return b == null ? 0 : 1;
		if (b == null) return -1;
		return cmp(a.charValue(), b.charValue());
	}
	
	
	
	
	public static int cmpChainableNullAsPinf(int prev, @Nullable Long a, @Nullable Long b)
	{
		return prev != 0 ? prev : cmpNullAsPinf(a, b);
	}
	
	public static int cmpChainableNullAsPinf(int prev, @Nullable Integer a, @Nullable Integer b)
	{
		return prev != 0 ? prev : cmpNullAsPinf(a, b);
	}
	
	public static int cmpChainableNullAsPinf(int prev, @Nullable Short a, @Nullable Short b)
	{
		return prev != 0 ? prev : cmpNullAsPinf(a, b);
	}
	
	public static int cmpChainableNullAsPinf(int prev, @Nullable Byte a, @Nullable Byte b)
	{
		return prev != 0 ? prev : cmpNullAsPinf(a, b);
	}
	
	public static int cmpChainableNullAsPinf(int prev, @Nullable Character a, @Nullable Character b)
	{
		return prev != 0 ? prev : cmpNullAsPinf(a, b);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static Comparator<Byte> UnsignedByteComparison = (a, b) -> cmpUnsigned((byte)a, (byte)b);
	public static Comparator<Short> UnsignedShortComparison = (a, b) -> cmpUnsigned((short)a, (short)b);
	public static Comparator<Integer> UnsignedIntComparison = (a, b) -> cmpUnsigned((int)a, (int)b);
	public static Comparator<Long> UnsignedLongComparison = (a, b) -> cmpUnsigned((long)a, (long)b);
	
	public static Comparator<UnsignedByte> ProperUnsignedByteComparison = (a, b) -> cmpUnsigned(a.byteValue(), b.byteValue());
	public static Comparator<UnsignedShort> ProperUnsignedShortComparison = (a, b) -> cmpUnsigned(a.shortValue(), b.shortValue());
	public static Comparator<UnsignedInteger> ProperUnsignedIntComparison = (a, b) -> cmpUnsigned(a.intValue(), b.intValue());
	public static Comparator<UnsignedLong> ProperUnsignedLongComparison = (a, b) -> cmpUnsigned(a.longValue(), b.longValue());
	
	
	public static int cmpUnsigned(long a, long b)
	{
		return UnsignedLong.compare(a, b);
	}
	
	public static int cmpUnsigned(int a, int b)
	{
		return UnsignedInteger.compare(a, b);
	}
	
	public static int cmpUnsigned(short a, short b)
	{
		return UnsignedShort.compare(a, b);
	}
	
	public static int cmpUnsigned(byte a, byte b)
	{
		return UnsignedByte.compare(a, b);
	}
	
	
	
	
	public static int cmpUnsignedChainable(int prev, @ActuallyUnsigned long a, @ActuallyUnsigned long b)
	{
		return prev != 0 ? prev : cmpUnsigned(a, b);
	}
	
	public static int cmpUnsignedChainable(int prev, @ActuallyUnsigned int a, @ActuallyUnsigned int b)
	{
		return prev != 0 ? prev : cmpUnsigned(a, b);
	}
	
	public static int cmpUnsignedChainable(int prev, @ActuallyUnsigned short a, @ActuallyUnsigned short b)
	{
		return prev != 0 ? prev : cmpUnsigned(a, b);
	}
	
	public static int cmpUnsignedChainable(int prev, @ActuallyUnsigned byte a, @ActuallyUnsigned byte b)
	{
		return prev != 0 ? prev : cmpUnsigned(a, b);
	}
	
	
	
	
	
	
	
	
	
	public static int cmpUnsignedNullAsNinf(@ActuallyUnsigned @Nullable Long a, @ActuallyUnsigned @Nullable Long b)
	{
		if (a == null) return b == null ? 0 : -1;
		if (b == null) return 1;
		return cmpUnsigned(a.longValue(), b.longValue());
	}
	
	public static int cmpUnsignedNullAsNinf(@ActuallyUnsigned @Nullable Integer a, @ActuallyUnsigned @Nullable Integer b)
	{
		if (a == null) return b == null ? 0 : -1;
		if (b == null) return 1;
		return cmpUnsigned(a.intValue(), b.intValue());
	}
	
	public static int cmpUnsignedNullAsNinf(@ActuallyUnsigned @Nullable Short a, @ActuallyUnsigned @Nullable Short b)
	{
		if (a == null) return b == null ? 0 : -1;
		if (b == null) return 1;
		return cmpUnsigned(a.shortValue(), b.shortValue());
	}
	
	public static int cmpUnsignedNullAsNinf(@ActuallyUnsigned @Nullable Byte a, @ActuallyUnsigned @Nullable Byte b)
	{
		if (a == null) return b == null ? 0 : -1;
		if (b == null) return 1;
		return cmpUnsigned(a.byteValue(), b.byteValue());
	}
	
	
	
	
	public static int cmpUnsignedChainableNullAsNinf(int prev, @ActuallyUnsigned @Nullable Long a, @ActuallyUnsigned @Nullable Long b)
	{
		return prev != 0 ? prev : cmpUnsignedNullAsNinf(a, b);
	}
	
	public static int cmpUnsignedChainableNullAsNinf(int prev, @ActuallyUnsigned @Nullable Integer a, @ActuallyUnsigned @Nullable Integer b)
	{
		return prev != 0 ? prev : cmpUnsignedNullAsNinf(a, b);
	}
	
	public static int cmpUnsignedChainableNullAsNinf(int prev, @ActuallyUnsigned @Nullable Short a, @ActuallyUnsigned @Nullable Short b)
	{
		return prev != 0 ? prev : cmpUnsignedNullAsNinf(a, b);
	}
	
	public static int cmpUnsignedChainableNullAsNinf(int prev, @ActuallyUnsigned @Nullable Byte a, @ActuallyUnsigned @Nullable Byte b)
	{
		return prev != 0 ? prev : cmpUnsignedNullAsNinf(a, b);
	}
	
	
	
	
	
	
	
	
	
	
	
	public static int cmpUnsignedNullAsPinf(@ActuallyUnsigned @Nullable Long a, @ActuallyUnsigned @Nullable Long b)
	{
		if (a == null) return b == null ? 0 : 1;
		if (b == null) return -1;
		return cmpUnsigned(a.longValue(), b.longValue());
	}
	
	public static int cmpUnsignedNullAsPinf(@ActuallyUnsigned @Nullable Integer a, @ActuallyUnsigned @Nullable Integer b)
	{
		if (a == null) return b == null ? 0 : 1;
		if (b == null) return -1;
		return cmpUnsigned(a.intValue(), b.intValue());
	}
	
	public static int cmpUnsignedNullAsPinf(@ActuallyUnsigned @Nullable Short a, @ActuallyUnsigned @Nullable Short b)
	{
		if (a == null) return b == null ? 0 : 1;
		if (b == null) return -1;
		return cmpUnsigned(a.shortValue(), b.shortValue());
	}
	
	public static int cmpUnsignedNullAsPinf(@ActuallyUnsigned @Nullable Byte a, @ActuallyUnsigned @Nullable Byte b)
	{
		if (a == null) return b == null ? 0 : 1;
		if (b == null) return -1;
		if (a < b) return -1;
		if (a > b) return 1;
		return cmpUnsigned(a.byteValue(), b.byteValue());
	}
	
	
	
	
	public static int cmpUnsignedChainableNullAsPinf(int prev, @ActuallyUnsigned @Nullable Long a, @ActuallyUnsigned @Nullable Long b)
	{
		return prev != 0 ? prev : cmpUnsignedNullAsPinf(a, b);
	}
	
	public static int cmpUnsignedChainableNullAsPinf(int prev, @ActuallyUnsigned @Nullable Integer a, @ActuallyUnsigned @Nullable Integer b)
	{
		return prev != 0 ? prev : cmpUnsignedNullAsPinf(a, b);
	}
	
	public static int cmpUnsignedChainableNullAsPinf(int prev, @ActuallyUnsigned @Nullable Short a, @ActuallyUnsigned @Nullable Short b)
	{
		return prev != 0 ? prev : cmpUnsignedNullAsPinf(a, b);
	}
	
	public static int cmpUnsignedChainableNullAsPinf(int prev, @ActuallyUnsigned @Nullable Byte a, @ActuallyUnsigned @Nullable Byte b)
	{
		return prev != 0 ? prev : cmpUnsignedNullAsPinf(a, b);
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
	
	
	
	
	public static long lcm(long a, long b)
	{
		//return (a * b) / gcd(a,b);
		
		if (arbitraryBoolean())
			return a * (b / gcd(a,b));
		else
			return b * (a / gcd(a,b));
	}
	
	
	
	
	
	
	/**
	 * @param a  {numerator, denominator}
	 * @param b  {numerator, denominator}
	 */
	public static void commonizeFractionsDenominators(@WritableValue long[] a, @WritableValue long[] b) throws OverflowException
	{
		if (a.length != 2)  throw new IllegalArgumentException();
		if (b.length != 2)  throw new IllegalArgumentException();
		
		long an = a[0];
		long ad = a[1];
		long bn = b[0];
		long bd = b[1];
		
		long[] r = commonizeFractionsDenominators(ad, bd);
		
		long multiplierA = r[0];
		long multiplierB = r[1];
		
		a[0] = safe_mul_s64(an, multiplierA);
		a[1] = safe_mul_s64(ad, multiplierA);
		
		b[0] = safe_mul_s64(bn, multiplierB);
		b[1] = safe_mul_s64(bd, multiplierB);
		
		asrt(a[1] == b[1]);
		asrt(arbitrary(a[1], b[1]) != 0);
	}
	
	
	/**
	 * @return the thing to multiply both the numerator and denominator by, first for a then for b;  ie: {multiplierA, multiplierB}
	 */
	@ThrowAwayValue
	public static long[] commonizeFractionsDenominators(long ad, long bd)
	{
		if (ad < 1)  throw new IllegalArgumentException();
		if (bd < 1)  throw new IllegalArgumentException();
		
		/*
		 * 3/10 + 4/6
		 * 
		 * lcm(10, 6) = 30
		 * 
		 * 30/10 for the first = 3
		 * 30/6 for the second = 5
		 * 
		 * (3*3)/(10*3) + (4*5)/(6*5)
		 * 9/30 + 20/30
		 */
		
		long gcd = gcd(ad, bd);
		
		//long multiplierA = lcm / ad;
		//long multiplierB = lcm / bd;
		
		long multiplierA = bd / gcd;
		long multiplierB = ad / gcd;
		
		return new long[]{multiplierA, multiplierB};
	}
	
	
	
	
	
	
	
	
	
	public static int safe_abs_s32(int a)
	{
		return a < 0 ? safe_neg_s32(a) : a;
	}
	
	public static long safe_abs_s64(long a)
	{
		return a < 0 ? safe_neg_s64(a) : a;
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
	
	
	
	public static boolean isOverflow_add_u32(@ActuallyUnsigned int a, @ActuallyUnsigned int b)
	{
		return _isOverflow_add_u32__a(a, b);
	}
	
	@ImplementationTransparency
	public static boolean _isOverflow_add_u32__a(@ActuallyUnsigned int a, @ActuallyUnsigned int b)
	{
		return Unsigned.greaterThanU32(a, 0xFFFF_FFFF - b);
	}
	
	@ImplementationTransparency
	public static boolean _isOverflow_add_u32__control(@ActuallyUnsigned int a, @ActuallyUnsigned int b)
	{
		long aa = upcast(a);
		long bb = upcast(b);
		
		long cc = aa + bb;
		
		return cc >= 0x1_0000_0000l;
	}
	
	
	
	
	public static boolean isOverflow_add_u64(@ActuallyUnsigned long a, @ActuallyUnsigned long b)
	{
		return Unsigned.greaterThanU64(a, 0xFFFF_FFFF_FFFF_FFFFl - b);
	}
	
	public static boolean isOverflow_inc_u64(@ActuallyUnsigned long a)
	{
		return a == 0xFFFF_FFFF_FFFF_FFFFl;
	}
	
	public static boolean isOverflow_dec_u64(@ActuallyUnsigned long a)
	{
		return a == 0;
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
	
	public static @ActuallyUnsigned long safe_inc_u64(@ActuallyUnsigned long a) throws OverflowException
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
	
	public static @ActuallyUnsigned long safe_dec_u64(@ActuallyUnsigned long a) throws OverflowException
	{
		if (isOverflow_dec_u64(a))
			throw new OverflowException();
		
		return a - 1;
	}
	
	public static long safe_add_s64(long a, long b) throws OverflowException
	{
		if (isOverflow_add_s64(a, b))
			throw new OverflowException();
		
		return a + b;
	}
	
	public static @ActuallyUnsigned long safe_add_u64(@ActuallyUnsigned long a, @ActuallyUnsigned long b) throws OverflowException
	{
		if (isOverflow_add_u64(a, b))
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
	
	
	
	public static boolean isEven(long a)
	{
		return (a & 1l) == 0l;
	}
	
	public static boolean isEven(int a)
	{
		return (a & 1) == 0;
	}
	
	
	public static boolean isOdd(long a)
	{
		return (a & 1l) == 1l;
	}
	
	public static boolean isOdd(int a)
	{
		return (a & 1) == 1;
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
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
primxp
_$$primxpconf:intsonly$$_
	
	
	
	public static _$$prim$$_ least(_$$prim$$_... values)
	{
		if (values.length == 0)
			throw new IllegalArgumentException();
		
		_$$prim$$_ e = values[0];
		for (int i = 1; i < values.length; i++)
		{
			if (values[i] < e)
				e = values[i];
		}
		
		return e;
	}
	
	public static _$$prim$$_ greatest(_$$prim$$_... values)
	{
		if (values.length == 0)
			throw new IllegalArgumentException();
		
		_$$prim$$_ e = values[0];
		for (int i = 1; i < values.length; i++)
		{
			if (values[i] > e)
				e = values[i];
		}
		
		return e;
	}
	
	
	public static _$$prim$$_ least(_$$Primitive$$_List values)
	{
		if (values.size() == 0)
			throw new IllegalArgumentException();
		
		_$$prim$$_ e = values.get_$$Prim$$_(0);
		for (int i = 1; i < values.size(); i++)
		{
			if (values.get_$$Prim$$_(i) < e)
				e = values.get_$$Prim$$_(i);
		}
		
		return e;
	}
	
	public static _$$prim$$_ greatest(_$$Primitive$$_List values)
	{
		if (values.size() == 0)
			throw new IllegalArgumentException();
		
		_$$prim$$_ e = values.get_$$Prim$$_(0);
		for (int i = 1; i < values.size(); i++)
		{
			if (values.get_$$Prim$$_(i) > e)
				e = values.get_$$Prim$$_(i);
		}
		
		return e;
	}
	 */
	
	
	
	
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
	
	
	public static byte least(ByteList values)
	{
		if (values.size() == 0)
			throw new IllegalArgumentException();
		
		byte e = values.getByte(0);
		for (int i = 1; i < values.size(); i++)
		{
			if (values.getByte(i) < e)
				e = values.getByte(i);
		}
		
		return e;
	}
	
	public static byte greatest(ByteList values)
	{
		if (values.size() == 0)
			throw new IllegalArgumentException();
		
		byte e = values.getByte(0);
		for (int i = 1; i < values.size(); i++)
		{
			if (values.getByte(i) > e)
				e = values.getByte(i);
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
	
	
	public static char least(CharacterList values)
	{
		if (values.size() == 0)
			throw new IllegalArgumentException();
		
		char e = values.getChar(0);
		for (int i = 1; i < values.size(); i++)
		{
			if (values.getChar(i) < e)
				e = values.getChar(i);
		}
		
		return e;
	}
	
	public static char greatest(CharacterList values)
	{
		if (values.size() == 0)
			throw new IllegalArgumentException();
		
		char e = values.getChar(0);
		for (int i = 1; i < values.size(); i++)
		{
			if (values.getChar(i) > e)
				e = values.getChar(i);
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
	
	
	public static short least(ShortList values)
	{
		if (values.size() == 0)
			throw new IllegalArgumentException();
		
		short e = values.getShort(0);
		for (int i = 1; i < values.size(); i++)
		{
			if (values.getShort(i) < e)
				e = values.getShort(i);
		}
		
		return e;
	}
	
	public static short greatest(ShortList values)
	{
		if (values.size() == 0)
			throw new IllegalArgumentException();
		
		short e = values.getShort(0);
		for (int i = 1; i < values.size(); i++)
		{
			if (values.getShort(i) > e)
				e = values.getShort(i);
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
	
	
	public static int least(IntegerList values)
	{
		if (values.size() == 0)
			throw new IllegalArgumentException();
		
		int e = values.getInt(0);
		for (int i = 1; i < values.size(); i++)
		{
			if (values.getInt(i) < e)
				e = values.getInt(i);
		}
		
		return e;
	}
	
	public static int greatest(IntegerList values)
	{
		if (values.size() == 0)
			throw new IllegalArgumentException();
		
		int e = values.getInt(0);
		for (int i = 1; i < values.size(); i++)
		{
			if (values.getInt(i) > e)
				e = values.getInt(i);
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
	
	
	public static long least(LongList values)
	{
		if (values.size() == 0)
			throw new IllegalArgumentException();
		
		long e = values.getLong(0);
		for (int i = 1; i < values.size(); i++)
		{
			if (values.getLong(i) < e)
				e = values.getLong(i);
		}
		
		return e;
	}
	
	public static long greatest(LongList values)
	{
		if (values.size() == 0)
			throw new IllegalArgumentException();
		
		long e = values.getLong(0);
		for (int i = 1; i < values.size(); i++)
		{
			if (values.getLong(i) > e)
				e = values.getLong(i);
		}
		
		return e;
	}
	// >>>	
	
	
	
	
	
	
	
	
	
	
	public static byte least(byte a, byte b)
	{
		return b < a ? b : a;
	}
	
	public static byte greatest(byte a, byte b)
	{
		return b > a ? b : a;
	}
	
	public static char least(char a, char b)
	{
		return b < a ? b : a;
	}
	
	public static char greatest(char a, char b)
	{
		return b > a ? b : a;
	}
	
	public static short least(short a, short b)
	{
		return b < a ? b : a;
	}
	
	public static short greatest(short a, short b)
	{
		return b > a ? b : a;
	}
	
	public static int greatest(int a, int b)
	{
		return b > a ? b : a;
	}
	
	public static int least(int a, int b)
	{
		return b < a ? b : a;
	}
	
	public static long least(long a, long b)
	{
		return b < a ? b : a;
	}
	
	public static long greatest(long a, long b)
	{
		return b > a ? b : a;
	}
	
	
	
	
	
	
	//These *have* to be >=/<= not >/< other wise imagine if the input was oh, I dunno (4, 6, 6)
	
	public static byte least(byte a, byte b, byte c)
	{
		if (c >= a && c >= b)
			return least(a, b);
		else if (b >= a && b >= c)
			return least(a, c);
		else if (a >= b && a >= c)
			return least(b, c);
		else
			throw new AssertionError();
	}
	
	public static byte greatest(byte a, byte b, byte c)
	{
		if (c <= a && c <= b)
			return greatest(a, b);
		else if (b <= a && b <= c)
			return greatest(a, c);
		else if (a <= b && a <= c)
			return greatest(b, c);
		else
			throw new AssertionError();
	}
	
	public static byte least(byte a, byte b, byte c, byte d)
	{
		if (d >= a && d >= b && d >= c)
			return least(a, b, c);
		else if (c >= a && c >= b && c >= d)
			return least(a, b, d);
		else if (b >= a && b >= c && b >= d)
			return least(a, c, d);
		else if (a >= b && a >= c && a >= d)
			return least(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static byte greatest(byte a, byte b, byte c, byte d)
	{
		if (d <= a && d <= b && d <= c)
			return greatest(a, b, c);
		else if (c <= a && c <= b && c <= d)
			return greatest(a, b, d);
		else if (b <= a && b <= c && b <= d)
			return greatest(a, c, d);
		else if (a <= b && a <= c && a <= d)
			return greatest(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static short least(short a, short b, short c)
	{
		if (c >= a && c >= b)
			return least(a, b);
		else if (b >= a && b >= c)
			return least(a, c);
		else if (a >= b && a >= c)
			return least(b, c);
		else
			throw new AssertionError();
	}
	
	public static short greatest(short a, short b, short c)
	{
		if (c <= a && c <= b)
			return greatest(a, b);
		else if (b <= a && b <= c)
			return greatest(a, c);
		else if (a <= b && a <= c)
			return greatest(b, c);
		else
			throw new AssertionError();
	}
	
	public static short least(short a, short b, short c, short d)
	{
		if (d >= a && d >= b && d >= c)
			return least(a, b, c);
		else if (c >= a && c >= b && c >= d)
			return least(a, b, d);
		else if (b >= a && b >= c && b >= d)
			return least(a, c, d);
		else if (a >= b && a >= c && a >= d)
			return least(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static short greatest(short a, short b, short c, short d)
	{
		if (d <= a && d <= b && d <= c)
			return greatest(a, b, c);
		else if (c <= a && c <= b && c <= d)
			return greatest(a, b, d);
		else if (b <= a && b <= c && b <= d)
			return greatest(a, c, d);
		else if (a <= b && a <= c && a <= d)
			return greatest(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static char least(char a, char b, char c)
	{
		if (c >= a && c >= b)
			return least(a, b);
		else if (b >= a && b >= c)
			return least(a, c);
		else if (a >= b && a >= c)
			return least(b, c);
		else
			throw new AssertionError();
	}
	
	public static char greatest(char a, char b, char c)
	{
		if (c <= a && c <= b)
			return greatest(a, b);
		else if (b <= a && b <= c)
			return greatest(a, c);
		else if (a <= b && a <= c)
			return greatest(b, c);
		else
			throw new AssertionError();
	}
	
	public static char least(char a, char b, char c, char d)
	{
		if (d >= a && d >= b && d >= c)
			return least(a, b, c);
		else if (c >= a && c >= b && c >= d)
			return least(a, b, d);
		else if (b >= a && b >= c && b >= d)
			return least(a, c, d);
		else if (a >= b && a >= c && a >= d)
			return least(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static char greatest(char a, char b, char c, char d)
	{
		if (d <= a && d <= b && d <= c)
			return greatest(a, b, c);
		else if (c <= a && c <= b && c <= d)
			return greatest(a, b, d);
		else if (b <= a && b <= c && b <= d)
			return greatest(a, c, d);
		else if (a <= b && a <= c && a <= d)
			return greatest(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static int least(int a, int b, int c)
	{
		if (c >= a && c >= b)
			return least(a, b);
		else if (b >= a && b >= c)
			return least(a, c);
		else if (a >= b && a >= c)
			return least(b, c);
		else
			throw new AssertionError();
	}
	
	public static int greatest(int a, int b, int c)
	{
		if (c <= a && c <= b)
			return greatest(a, b);
		else if (b <= a && b <= c)
			return greatest(a, c);
		else if (a <= b && a <= c)
			return greatest(b, c);
		else
			throw new AssertionError();
	}
	
	public static int least(int a, int b, int c, int d)
	{
		if (d >= a && d >= b && d >= c)
			return least(a, b, c);
		else if (c >= a && c >= b && c >= d)
			return least(a, b, d);
		else if (b >= a && b >= c && b >= d)
			return least(a, c, d);
		else if (a >= b && a >= c && a >= d)
			return least(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static int greatest(int a, int b, int c, int d)
	{
		if (d <= a && d <= b && d <= c)
			return greatest(a, b, c);
		else if (c <= a && c <= b && c <= d)
			return greatest(a, b, d);
		else if (b <= a && b <= c && b <= d)
			return greatest(a, c, d);
		else if (a <= b && a <= c && a <= d)
			return greatest(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static long greatest(long a, long b, long c, long d)
	{
		if (d <= a && d <= b && d <= c)
			return SmallIntegerMathUtilities.greatest(a, b, c);
		else if (c <= a && c <= b && c <= d)
			return SmallIntegerMathUtilities.greatest(a, b, d);
		else if (b <= a && b <= c && b <= d)
			return SmallIntegerMathUtilities.greatest(a, c, d);
		else if (a <= b && a <= c && a <= d)
			return SmallIntegerMathUtilities.greatest(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static long least(long a, long b, long c, long d)
	{
		if (d >= a && d >= b && d >= c)
			return SmallIntegerMathUtilities.least(a, b, c);
		else if (c >= a && c >= b && c >= d)
			return SmallIntegerMathUtilities.least(a, b, d);
		else if (b >= a && b >= c && b >= d)
			return SmallIntegerMathUtilities.least(a, c, d);
		else if (a >= b && a >= c && a >= d)
			return SmallIntegerMathUtilities.least(b, c, d);
		else
			throw new AssertionError();
	}
	
	public static long greatest(long a, long b, long c)
	{
		if (c <= a && c <= b)
			return SmallIntegerMathUtilities.greatest(a, b);
		else if (b <= a && b <= c)
			return SmallIntegerMathUtilities.greatest(a, c);
		else if (a <= b && a <= c)
			return SmallIntegerMathUtilities.greatest(b, c);
		else
			throw new AssertionError();
	}
	
	public static long least(long a, long b, long c)
	{
		if (c >= a && c >= b)
			return SmallIntegerMathUtilities.least(a, b);
		else if (b >= a && b >= c)
			return SmallIntegerMathUtilities.least(a, c);
		else if (a >= b && a >= c)
			return SmallIntegerMathUtilities.least(b, c);
		else
			throw new AssertionError(a+","+b+","+c);
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
	
	/**
	 * + There's no safe_pow_s32() because this is already safe—it'll throw {@link OverflowException} instead of give corrupted results :3
	 */
	public static int pow(int base, int exponent) throws ArithmeticException, OverflowException, TruncationException
	{
		//TODO implement properly!!
		long rv = pow((long)base, (long)exponent);
		if (((int)rv) != rv)
			throw new OverflowException();
		return (int)rv;
	}
	
	/**
	 * + There's no safe_pow_s64() because this is already safe—it'll throw {@link OverflowException} instead of give corrupted results :3
	 */
	public static long pow(long base, long exponent) throws ArithmeticException, OverflowException, TruncationException
	{
		return pow_new(base, exponent);
	}
	
	@ImplementationTransparency
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
	
	
	@ImplementationTransparency
	public static int pow_new(int base, int exponent) throws ArithmeticException, OverflowException, TruncationException
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
	
	@ImplementationTransparency
	public static long pow_new(long base, long exponent) throws ArithmeticException, OverflowException, TruncationException
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
	
	
	/**
	 * @return pow(2, exponent)
	 */
	public static int powb2_s32(int exponent) throws ArithmeticException
	{
		if (exponent < 0)
			throw new TruncationException("2^(-P) is not an integer");
		
		if (exponent >= 31)
			throw new OverflowException();
		
		return 1 << exponent;
	}
	
	/**
	 * @return pow(2, exponent)
	 */
	public static long powb2_s64(long exponent) throws ArithmeticException
	{
		if (exponent < 0)
			throw new TruncationException("2^(-P) is not an integer");
		
		if (exponent >= 64)
			throw new OverflowException();
		
		return 1 << exponent;
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
		
		//TODO 32 - Integer.countLeadingZeros() ?????
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
	
	
	
	
	public static int losslessLogBase2(long value) throws TruncationException
	{
		if (value == 0)
			throw new OutOfDomainArithmeticException("log(0)");
		
		if (value < 0) //takes care of two's-complement thingies :3
			throw new ComplexNumberArithmeticException();
		
		int exponent = BitUtilities.dcd64(value);
		
		if (powb2_s64(exponent) != value)
			throw new TruncationException("result was not integer");
		
		return exponent;
	}
	
	public static int floorLogBase2(long value)
	{
		if (value == 0)
			throw new OutOfDomainArithmeticException("log(0)");
		
		if (value < 0) //takes care of two's-complement thingies :3
			throw new ComplexNumberArithmeticException();
		
		return dcd64(getHighestOneBit(value));
	}
	
	public static int ceilLogBase2(long value)
	{
		if (value == 0)
			throw new OutOfDomainArithmeticException("log(0)");
		
		if (value < 0) //takes care of two's-complement thingies :3
			throw new ComplexNumberArithmeticException();
		
		long h = getHighestOneBit(value);
		return dcd64(h) + (value != h ? 1 : 0);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int losslessLogBase2Unsigned(@ActuallyUnsigned int value) throws TruncationException
	{
		if (value == 0)
			throw new OutOfDomainArithmeticException("log(0)");
		
		int exponent = BitUtilities.dcd32(value);
		
		if (powb2_s32(exponent) != value)
			throw new TruncationException("result was not integer");
		
		return exponent;
	}
	
	public static int floorLogBase2Unsigned(@ActuallyUnsigned int value)
	{
		if (value == 0)
			throw new OutOfDomainArithmeticException("log(0)");
		
		return dcd32(getHighestOneBit(value));
	}
	
	public static int ceilLogBase2Unsigned(@ActuallyUnsigned int value)
	{
		if (value == 0)
			throw new OutOfDomainArithmeticException("log(0)");
		
		int h = getHighestOneBit(value);
		return dcd32(h) + (value != h ? 1 : 0);
	}
	
	
	
	
	
	public static int losslessLogBase2Unsigned(@ActuallyUnsigned long value) throws TruncationException
	{
		if (value == 0)
			throw new OutOfDomainArithmeticException("log(0)");
		
		int exponent = BitUtilities.dcd64(value);
		
		if (powb2_s64(exponent) != value)
			throw new TruncationException("result was not integer");
		
		return exponent;
	}
	
	public static int floorLogBase2Unsigned(@ActuallyUnsigned long value)
	{
		if (value == 0)
			throw new OutOfDomainArithmeticException("log(0)");
		
		return dcd64(getHighestOneBit(value));
	}
	
	public static int ceilLogBase2Unsigned(@ActuallyUnsigned long value)
	{
		if (value == 0)
			throw new OutOfDomainArithmeticException("log(0)");
		
		long h = getHighestOneBit(value);
		return dcd64(h) + (value != h ? 1 : 0);
	}
	
	
	
	
	
	
	
	
	
	
	public static int losslessLog(int value, int base) throws TruncationException
	{
		//TODO A proper way of doing this!! X'D
		
		int exponent = floorLog(value, base);
		
		if (pow(base, exponent) != value)
			throw new TruncationException("log["+base+"]("+value+") ≠ "+exponent);
		
		return exponent;
	}
	
	public static int floorLog(int value, int base) throws ArithmeticException
	{
		//TODO A proper way of doing this!! X'D
		
		if (value == 0)
			throw new OutOfDomainArithmeticException("log(0)");
		
		if (value < 0)
			throw new ComplexNumberArithmeticException();
		
		
		double v = Math.log(value) / Math.log(base);
		
		if (v > Integer.MAX_VALUE || v < Integer.MIN_VALUE)
			throw new OverflowException();
		
		int exponent;
		
		if (abs(round(v) - v) < 1e-12)
			exponent = (int)roundClosestArbtiesS32(v);
		else
			exponent = (int)roundFloorS32(v);
		
		return exponent;
	}
	
	
	public static int ceilLog(int value, int base)
	{
		//TODO A proper way of doing this!! X'D
		
		if (value == 0)
			throw new OutOfDomainArithmeticException("log(0)");
		
		if (value < 0)
			throw new ComplexNumberArithmeticException();
		
		
		double v = Math.log(value) / Math.log(base);
		
		if (v > Integer.MAX_VALUE || v < Integer.MIN_VALUE)
			throw new OverflowException();
		
		int exponent;
		
		if (abs(round(v) - v) < 1e-12)
			exponent = (int)roundClosestArbtiesS32(v);
		else
			exponent = (int)roundCeilS32(v);
		
		return exponent;
	}
	
	
	
	
	
	
	
	
	
	
	public static int losslessLog(long value, long base) throws TruncationException
	{
		//TODO A proper way of doing this!! X'D
		
		int exponent = floorLog(value, base);
		
		if (pow(base, exponent) != value)
			throw new TruncationException("log["+base+"]("+value+") ≠ "+exponent);
		
		return exponent;
	}
	
	public static int floorLog(long value, long base) throws ArithmeticException
	{
		//TODO A proper way of doing this!! X'D
		
		if (value == 0)
			throw new OutOfDomainArithmeticException("log(0)");
		
		if (value < 0)
			throw new ComplexNumberArithmeticException();
		
		
		double v = Math.log(value) / Math.log(base);
		
		if (v > Integer.MAX_VALUE || v < Integer.MIN_VALUE)
			throw new OverflowException();
		
		int exponent;
		
		if (abs(round(v) - v) < 1e-12)
			exponent = (int)roundClosestArbtiesS32(v);
		else
			exponent = (int)roundFloorS32(v);
		
		return exponent;
	}
	
	
	public static int ceilLog(long value, long base)
	{
		//TODO A proper way of doing this!! X'D
		
		if (value == 0)
			throw new OutOfDomainArithmeticException("log(0)");
		
		if (value < 0)
			throw new ComplexNumberArithmeticException();
		
		
		double v = Math.log(value) / Math.log(base);
		
		if (v > Integer.MAX_VALUE || v < Integer.MIN_VALUE)
			throw new OverflowException();
		
		int exponent;
		
		if (abs(round(v) - v) < 1e-12)
			exponent = (int)roundClosestArbtiesS32(v);
		else
			exponent = (int)roundCeilS32(v);
		
		return exponent;
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
	
	/**
	 * This simplifies a fraction to a canonical form, which:<br>
	 * <ul>
	 * 	<li>Is irreducible (no common factors other than 1)</li>
	 * 	<li>Has a positive denominator (eg, 1/(-2) = (-1)/2,   -1/-2 = 1/2)</li>
	 * 	<li>Is 0/1 for all zero-values.</li>
	 * </ul>
	 * Note: This works on normal signed integers of both signs
	 * @param halves This must consist of exactly 2 integers, [0] being the Numerator, and [1] being the Denominator.  The results are overwritten in this same array.
	 */
	public static void simplifyFraction(@WritableValue @Nonnull int[] halves)
	{
		int n = halves[0];
		int d = halves[1];
		
		if (d == 0)
			//Just ignore the fact that it does not compute.
			return;
		
		boolean negative = n < 0 ^ d < 0;
		
		n = safe_abs_s32(n);
		d = safe_abs_s32(d);
		
		//GCD will intrinsically make 0/28 = 0/1
		int gcd = gcd(n, d);
		
		n /= gcd;
		d /= gcd;
		
		if (negative)
			n = -n;
		
		halves[0] = n;
		halves[1] = d;
	}
	
	/**
	 * Convenience and array-length-safety for {@link #simplifyFraction(int[])}.
	 */
	@ThrowAwayValue
	public static int[] simplifyFraction(int n, int d)
	{
		@WritableValue int[] halves = new int[]{n, d};
		simplifyFraction(halves);
		return halves;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
primxp

_$$primxpconf:noboolean$$_
	
	public static @Negative _$$prim$$_ requireNegative(_$$prim$$_ i)
	{
		if (i >= 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonnegative _$$prim$$_ requireNonNegative(_$$prim$$_ i)
	{
		if (i < 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Positive _$$prim$$_ requirePositive(_$$prim$$_ i)
	{
		if (i <= 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonpositive _$$prim$$_ requireNonPositive(_$$prim$$_ i)
	{
		if (i > 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonzero _$$prim$$_ requireNonZero(_$$prim$$_ i)
	{
		if (i == 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	
	
	public static _$$prim$$_ requireAboveOrAt(_$$prim$$_ i, _$$prim$$_ minimumInclusive)
	{
		if (i < minimumInclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static _$$prim$$_ requireAboveButNot(_$$prim$$_ i, _$$prim$$_ minimumExclusive)
	{
		if (i <= minimumExclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static _$$prim$$_ requireBelowOrAt(_$$prim$$_ i, _$$prim$$_ maximumInclusive)
	{
		if (i > maximumInclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static _$$prim$$_ requireBelowButNot(_$$prim$$_ i, _$$prim$$_ maximumExclusive)
	{
		if (i >= maximumExclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static _$$prim$$_ requireBetweenOrAt(_$$prim$$_ i, _$$prim$$_ minimumInclusive, _$$prim$$_ maximumInclusive)
	{
		return requireBelowOrAt(requireAboveOrAt(i, minimumInclusive), maximumInclusive);
	}
	
	public static _$$prim$$_ requireBetweenButNot(_$$prim$$_ i, _$$prim$$_ minimumExclusive, _$$prim$$_ maximumExclusive)
	{
		return requireBelowButNot(requireAboveButNot(i, minimumExclusive), maximumExclusive);
	}
	
	public static _$$prim$$_ requireBetweenOrAtLowButNotHigh(_$$prim$$_ i, _$$prim$$_ minimumInclusive, _$$prim$$_ maximumExclusive)
	{
		return requireBelowButNot(requireAboveOrAt(i, minimumInclusive), maximumExclusive);
	}
	
	public static _$$prim$$_ requireBetweenButNotLowOrAtHigh(_$$prim$$_ i, _$$prim$$_ minimumExclusive, _$$prim$$_ maximumInclusive)  //English makes this confusing x'D
	{
		return requireBelowOrAt(requireAboveButNot(i, minimumExclusive), maximumInclusive);
	}
	public static _$$prim$$_ requireBetweenOrAtHighButNotLow(_$$prim$$_ i, _$$prim$$_ minimumExclusive, _$$prim$$_ maximumInclusive)
	{
		return requireBetweenButNotLowOrAtHigh(i, minimumExclusive, maximumInclusive);
	}
	public static _$$prim$$_ requireBetweenButNotLowButAtHigh(_$$prim$$_ i, _$$prim$$_ minimumExclusive, _$$prim$$_ maximumInclusive)
	{
		return requireBetweenButNotLowOrAtHigh(i, minimumExclusive, maximumInclusive);
	}
	
	
	
	 */
	
	
	
	public static @Negative byte requireNegative(byte i)
	{
		if (i >= 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonnegative byte requireNonNegative(byte i)
	{
		if (i < 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Positive byte requirePositive(byte i)
	{
		if (i <= 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonpositive byte requireNonPositive(byte i)
	{
		if (i > 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonzero byte requireNonZero(byte i)
	{
		if (i == 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	
	
	public static byte requireAboveOrAt(byte i, byte minimumInclusive)
	{
		if (i < minimumInclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static byte requireAboveButNot(byte i, byte minimumExclusive)
	{
		if (i <= minimumExclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static byte requireBelowOrAt(byte i, byte maximumInclusive)
	{
		if (i > maximumInclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static byte requireBelowButNot(byte i, byte maximumExclusive)
	{
		if (i >= maximumExclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static byte requireBetweenOrAt(byte i, byte minimumInclusive, byte maximumInclusive)
	{
		return requireBelowOrAt(requireAboveOrAt(i, minimumInclusive), maximumInclusive);
	}
	
	public static byte requireBetweenButNot(byte i, byte minimumExclusive, byte maximumExclusive)
	{
		return requireBelowButNot(requireAboveButNot(i, minimumExclusive), maximumExclusive);
	}
	
	public static byte requireBetweenOrAtLowButNotHigh(byte i, byte minimumInclusive, byte maximumExclusive)
	{
		return requireBelowButNot(requireAboveOrAt(i, minimumInclusive), maximumExclusive);
	}
	
	public static byte requireBetweenButNotLowOrAtHigh(byte i, byte minimumExclusive, byte maximumInclusive)  //English makes this confusing x'D
	{
		return requireBelowOrAt(requireAboveButNot(i, minimumExclusive), maximumInclusive);
	}
	public static byte requireBetweenOrAtHighButNotLow(byte i, byte minimumExclusive, byte maximumInclusive)
	{
		return requireBetweenButNotLowOrAtHigh(i, minimumExclusive, maximumInclusive);
	}
	public static byte requireBetweenButNotLowButAtHigh(byte i, byte minimumExclusive, byte maximumInclusive)
	{
		return requireBetweenButNotLowOrAtHigh(i, minimumExclusive, maximumInclusive);
	}
	
	
	
	
	
	
	public static @Negative char requireNegative(char i)
	{
		if (i >= 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonnegative char requireNonNegative(char i)
	{
		if (i < 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Positive char requirePositive(char i)
	{
		if (i <= 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonpositive char requireNonPositive(char i)
	{
		if (i > 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonzero char requireNonZero(char i)
	{
		if (i == 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	
	
	public static char requireAboveOrAt(char i, char minimumInclusive)
	{
		if (i < minimumInclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static char requireAboveButNot(char i, char minimumExclusive)
	{
		if (i <= minimumExclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static char requireBelowOrAt(char i, char maximumInclusive)
	{
		if (i > maximumInclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static char requireBelowButNot(char i, char maximumExclusive)
	{
		if (i >= maximumExclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static char requireBetweenOrAt(char i, char minimumInclusive, char maximumInclusive)
	{
		return requireBelowOrAt(requireAboveOrAt(i, minimumInclusive), maximumInclusive);
	}
	
	public static char requireBetweenButNot(char i, char minimumExclusive, char maximumExclusive)
	{
		return requireBelowButNot(requireAboveButNot(i, minimumExclusive), maximumExclusive);
	}
	
	public static char requireBetweenOrAtLowButNotHigh(char i, char minimumInclusive, char maximumExclusive)
	{
		return requireBelowButNot(requireAboveOrAt(i, minimumInclusive), maximumExclusive);
	}
	
	public static char requireBetweenButNotLowOrAtHigh(char i, char minimumExclusive, char maximumInclusive)  //English makes this confusing x'D
	{
		return requireBelowOrAt(requireAboveButNot(i, minimumExclusive), maximumInclusive);
	}
	public static char requireBetweenOrAtHighButNotLow(char i, char minimumExclusive, char maximumInclusive)
	{
		return requireBetweenButNotLowOrAtHigh(i, minimumExclusive, maximumInclusive);
	}
	public static char requireBetweenButNotLowButAtHigh(char i, char minimumExclusive, char maximumInclusive)
	{
		return requireBetweenButNotLowOrAtHigh(i, minimumExclusive, maximumInclusive);
	}
	
	
	
	
	
	
	public static @Negative short requireNegative(short i)
	{
		if (i >= 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonnegative short requireNonNegative(short i)
	{
		if (i < 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Positive short requirePositive(short i)
	{
		if (i <= 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonpositive short requireNonPositive(short i)
	{
		if (i > 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonzero short requireNonZero(short i)
	{
		if (i == 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	
	
	public static short requireAboveOrAt(short i, short minimumInclusive)
	{
		if (i < minimumInclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static short requireAboveButNot(short i, short minimumExclusive)
	{
		if (i <= minimumExclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static short requireBelowOrAt(short i, short maximumInclusive)
	{
		if (i > maximumInclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static short requireBelowButNot(short i, short maximumExclusive)
	{
		if (i >= maximumExclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static short requireBetweenOrAt(short i, short minimumInclusive, short maximumInclusive)
	{
		return requireBelowOrAt(requireAboveOrAt(i, minimumInclusive), maximumInclusive);
	}
	
	public static short requireBetweenButNot(short i, short minimumExclusive, short maximumExclusive)
	{
		return requireBelowButNot(requireAboveButNot(i, minimumExclusive), maximumExclusive);
	}
	
	public static short requireBetweenOrAtLowButNotHigh(short i, short minimumInclusive, short maximumExclusive)
	{
		return requireBelowButNot(requireAboveOrAt(i, minimumInclusive), maximumExclusive);
	}
	
	public static short requireBetweenButNotLowOrAtHigh(short i, short minimumExclusive, short maximumInclusive)  //English makes this confusing x'D
	{
		return requireBelowOrAt(requireAboveButNot(i, minimumExclusive), maximumInclusive);
	}
	public static short requireBetweenOrAtHighButNotLow(short i, short minimumExclusive, short maximumInclusive)
	{
		return requireBetweenButNotLowOrAtHigh(i, minimumExclusive, maximumInclusive);
	}
	public static short requireBetweenButNotLowButAtHigh(short i, short minimumExclusive, short maximumInclusive)
	{
		return requireBetweenButNotLowOrAtHigh(i, minimumExclusive, maximumInclusive);
	}
	
	
	
	
	
	
	public static @Negative float requireNegative(float i)
	{
		if (i >= 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonnegative float requireNonNegative(float i)
	{
		if (i < 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Positive float requirePositive(float i)
	{
		if (i <= 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonpositive float requireNonPositive(float i)
	{
		if (i > 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonzero float requireNonZero(float i)
	{
		if (i == 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	
	
	public static float requireAboveOrAt(float i, float minimumInclusive)
	{
		if (i < minimumInclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static float requireAboveButNot(float i, float minimumExclusive)
	{
		if (i <= minimumExclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static float requireBelowOrAt(float i, float maximumInclusive)
	{
		if (i > maximumInclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static float requireBelowButNot(float i, float maximumExclusive)
	{
		if (i >= maximumExclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static float requireBetweenOrAt(float i, float minimumInclusive, float maximumInclusive)
	{
		return requireBelowOrAt(requireAboveOrAt(i, minimumInclusive), maximumInclusive);
	}
	
	public static float requireBetweenButNot(float i, float minimumExclusive, float maximumExclusive)
	{
		return requireBelowButNot(requireAboveButNot(i, minimumExclusive), maximumExclusive);
	}
	
	public static float requireBetweenOrAtLowButNotHigh(float i, float minimumInclusive, float maximumExclusive)
	{
		return requireBelowButNot(requireAboveOrAt(i, minimumInclusive), maximumExclusive);
	}
	
	public static float requireBetweenButNotLowOrAtHigh(float i, float minimumExclusive, float maximumInclusive)  //English makes this confusing x'D
	{
		return requireBelowOrAt(requireAboveButNot(i, minimumExclusive), maximumInclusive);
	}
	public static float requireBetweenOrAtHighButNotLow(float i, float minimumExclusive, float maximumInclusive)
	{
		return requireBetweenButNotLowOrAtHigh(i, minimumExclusive, maximumInclusive);
	}
	public static float requireBetweenButNotLowButAtHigh(float i, float minimumExclusive, float maximumInclusive)
	{
		return requireBetweenButNotLowOrAtHigh(i, minimumExclusive, maximumInclusive);
	}
	
	
	
	
	
	
	public static @Negative int requireNegative(int i)
	{
		if (i >= 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonnegative int requireNonNegative(int i)
	{
		if (i < 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Positive int requirePositive(int i)
	{
		if (i <= 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonpositive int requireNonPositive(int i)
	{
		if (i > 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonzero int requireNonZero(int i)
	{
		if (i == 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	
	
	public static int requireAboveOrAt(int i, int minimumInclusive)
	{
		if (i < minimumInclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static int requireAboveButNot(int i, int minimumExclusive)
	{
		if (i <= minimumExclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static int requireBelowOrAt(int i, int maximumInclusive)
	{
		if (i > maximumInclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static int requireBelowButNot(int i, int maximumExclusive)
	{
		if (i >= maximumExclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static int requireBetweenOrAt(int i, int minimumInclusive, int maximumInclusive)
	{
		return requireBelowOrAt(requireAboveOrAt(i, minimumInclusive), maximumInclusive);
	}
	
	public static int requireBetweenButNot(int i, int minimumExclusive, int maximumExclusive)
	{
		return requireBelowButNot(requireAboveButNot(i, minimumExclusive), maximumExclusive);
	}
	
	public static int requireBetweenOrAtLowButNotHigh(int i, int minimumInclusive, int maximumExclusive)
	{
		return requireBelowButNot(requireAboveOrAt(i, minimumInclusive), maximumExclusive);
	}
	
	public static int requireBetweenButNotLowOrAtHigh(int i, int minimumExclusive, int maximumInclusive)  //English makes this confusing x'D
	{
		return requireBelowOrAt(requireAboveButNot(i, minimumExclusive), maximumInclusive);
	}
	public static int requireBetweenOrAtHighButNotLow(int i, int minimumExclusive, int maximumInclusive)
	{
		return requireBetweenButNotLowOrAtHigh(i, minimumExclusive, maximumInclusive);
	}
	public static int requireBetweenButNotLowButAtHigh(int i, int minimumExclusive, int maximumInclusive)
	{
		return requireBetweenButNotLowOrAtHigh(i, minimumExclusive, maximumInclusive);
	}
	
	
	
	
	
	
	public static @Negative double requireNegative(double i)
	{
		if (i >= 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonnegative double requireNonNegative(double i)
	{
		if (i < 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Positive double requirePositive(double i)
	{
		if (i <= 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonpositive double requireNonPositive(double i)
	{
		if (i > 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonzero double requireNonZero(double i)
	{
		if (i == 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	
	
	public static double requireAboveOrAt(double i, double minimumInclusive)
	{
		if (i < minimumInclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static double requireAboveButNot(double i, double minimumExclusive)
	{
		if (i <= minimumExclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static double requireBelowOrAt(double i, double maximumInclusive)
	{
		if (i > maximumInclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static double requireBelowButNot(double i, double maximumExclusive)
	{
		if (i >= maximumExclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static double requireBetweenOrAt(double i, double minimumInclusive, double maximumInclusive)
	{
		return requireBelowOrAt(requireAboveOrAt(i, minimumInclusive), maximumInclusive);
	}
	
	public static double requireBetweenButNot(double i, double minimumExclusive, double maximumExclusive)
	{
		return requireBelowButNot(requireAboveButNot(i, minimumExclusive), maximumExclusive);
	}
	
	public static double requireBetweenOrAtLowButNotHigh(double i, double minimumInclusive, double maximumExclusive)
	{
		return requireBelowButNot(requireAboveOrAt(i, minimumInclusive), maximumExclusive);
	}
	
	public static double requireBetweenButNotLowOrAtHigh(double i, double minimumExclusive, double maximumInclusive)  //English makes this confusing x'D
	{
		return requireBelowOrAt(requireAboveButNot(i, minimumExclusive), maximumInclusive);
	}
	public static double requireBetweenOrAtHighButNotLow(double i, double minimumExclusive, double maximumInclusive)
	{
		return requireBetweenButNotLowOrAtHigh(i, minimumExclusive, maximumInclusive);
	}
	public static double requireBetweenButNotLowButAtHigh(double i, double minimumExclusive, double maximumInclusive)
	{
		return requireBetweenButNotLowOrAtHigh(i, minimumExclusive, maximumInclusive);
	}
	
	
	
	
	
	
	public static @Negative long requireNegative(long i)
	{
		if (i >= 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonnegative long requireNonNegative(long i)
	{
		if (i < 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Positive long requirePositive(long i)
	{
		if (i <= 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonpositive long requireNonPositive(long i)
	{
		if (i > 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static @Nonzero long requireNonZero(long i)
	{
		if (i == 0)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	
	
	public static long requireAboveOrAt(long i, long minimumInclusive)
	{
		if (i < minimumInclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static long requireAboveButNot(long i, long minimumExclusive)
	{
		if (i <= minimumExclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static long requireBelowOrAt(long i, long maximumInclusive)
	{
		if (i > maximumInclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static long requireBelowButNot(long i, long maximumExclusive)
	{
		if (i >= maximumExclusive)
			throw new IllegalArgumentException(String.valueOf(i));
		return i;
	}
	
	public static long requireBetweenOrAt(long i, long minimumInclusive, long maximumInclusive)
	{
		return requireBelowOrAt(requireAboveOrAt(i, minimumInclusive), maximumInclusive);
	}
	
	public static long requireBetweenButNot(long i, long minimumExclusive, long maximumExclusive)
	{
		return requireBelowButNot(requireAboveButNot(i, minimumExclusive), maximumExclusive);
	}
	
	public static long requireBetweenOrAtLowButNotHigh(long i, long minimumInclusive, long maximumExclusive)
	{
		return requireBelowButNot(requireAboveOrAt(i, minimumInclusive), maximumExclusive);
	}
	
	public static long requireBetweenButNotLowOrAtHigh(long i, long minimumExclusive, long maximumInclusive)  //English makes this confusing x'D
	{
		return requireBelowOrAt(requireAboveButNot(i, minimumExclusive), maximumInclusive);
	}
	public static long requireBetweenOrAtHighButNotLow(long i, long minimumExclusive, long maximumInclusive)
	{
		return requireBetweenButNotLowOrAtHigh(i, minimumExclusive, maximumInclusive);
	}
	public static long requireBetweenButNotLowButAtHigh(long i, long minimumExclusive, long maximumInclusive)
	{
		return requireBetweenButNotLowOrAtHigh(i, minimumExclusive, maximumInclusive);
	}
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * upmod(0, 5) = 0 (!)
	 * upmod(1, 5) = 1
	 * upmod(2, 5) = 2
	 * upmod(3, 5) = 3
	 * upmod(4, 5) = 4
	 * upmod(5, 5) = 5 (!)
	 * upmod(6, 5) = 1
	 * upmod(7, 5) = 2
	 * upmod(8, 5) = 3
	 * 
	 * upmod(n < 0, d) = undefined
	 * 
	 * + It's useful for determining, eg, the number of bits in the last byte, given n = number of bits and d = 8  :>
	 * 		The number of small-things inside the last of larger uniformly-sized containers :>
	 */
	public static int upmod(int n, int d)
	{
		int r = n % d;
		return r == 0 ? (n == 0 ? 0 : d) : r;
	}
	
	public static long upmod(long n, long d)
	{
		long r = n % d;
		return r == 0 ? (n == 0 ? 0 : d) : r;
	}
	
	
	
	
	
	
	
	/**
	 * a << b = a << (b & 31)
	 * Try it if you don't believe me! X'D
	 * 
	 * (x << 32) == x instead of always 0!
	 */
	public static int truncatingShift32(int value, @ActuallyUnsigned int bits)
	{
		boolean overflows = (bits & 31) != bits;
		return overflows ? 0 : (value << bits);
	}
	
	public static long truncatingShift64(long value, @ActuallyUnsigned long bits)
	{
		boolean overflows = (bits & 63) != bits;
		return overflows ? 0 : (value << bits);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Nonnegative
	public static int floorSqrtS32(@Nonnegative int v)
	{
		return (int)floorSqrtS64(v);
	}
	
	@Nonnegative
	public static int ceilSqrtS32(@Nonnegative int v)
	{
		return (int)ceilSqrtS64(v);
	}
	
	
	@Nonnegative
	public static int floorSqrtU32(@ActuallyUnsigned int v)
	{
		return (int)floorSqrtS64(upcast(v));
	}
	
	@Nonnegative
	public static int ceilSqrtU32(@ActuallyUnsigned int v)
	{
		return (int)ceilSqrtS64(upcast(v));
	}
	
	
	
	@Nonnegative
	public static long floorSqrtS64(@Nonnegative long v)
	{
		//TODO X'D
		return roundFloorS64(sqrt((double)v));
	}
	
	@Nonnegative
	public static long ceilSqrtS64(@Nonnegative long v)
	{
		//TODO X'D
		return roundCeilS64(sqrt((double)v));
	}
	
	
	@Nonnegative
	public static long floorSqrtU64(@ActuallyUnsigned long v)
	{
		//TODO X'D
		return roundFloorS64(sqrt(safeCastU64toF64(v)));
	}
	
	@Nonnegative
	public static long ceilSqrtU64(@ActuallyUnsigned long v)
	{
		//TODO X'D
		return roundCeilS64(sqrt(safeCastU64toF64(v)));
	}
	
	
	
	
	
	/**
	 * Negatives are interpreted like ∞-n and thus are greater than all other values, but still, the more negative they are, the less they get compared to each other.
	 * This is how negative temperature works in physics!
	 */
	public static int reverseNegativesComparison(int a, int b)
	{
		if (a < 0)
		{
			if (b < 0)
			{
				return cmp(a, b);
			}
			else
			{
				//a is negative, b is not
				return 1;  //a > b
			}
		}
		else
		{
			if (b < 0)
			{
				//b is negative, b is not
				return -1;  //a < b
			}
			else
			{
				return cmp(a, b);
			}
		}
	}
	
	
	
	public static final int AllIntegersSequenceEnd = Integer.MIN_VALUE;
	
	public static int allIntegersSequenceNext(int v) throws OverflowException
	{
		if (v == AllIntegersSequenceEnd)
			throw new OverflowException();
		
		return v < 0 ? -v : -(v+1);
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * + Note that the unsigned version of this would just always be true XD
	 * + Also note that if you could efficiently use unsigned arithmetic, you could just make the *new* interval's max bound and values be stored as unsigned integers of the same bitlength and everything would be fine! X'D
	 * 		+ Note that both the new inclusive max bound (inclusiveMaximum - inclusiveMinimum) and the size / exclusive max bound (inclusiveMaximum - inclusiveMinimum + 1, unless inclusiveMaximum - inclusiveMinimum = 2^64-1, the *unsigned* max bound!) both will work in unsigned arithmetic because our signed numbers are two's-complement not one's-complement add/subtract are the same binary operations on bits regardless of twos-signed or unsigned interpretation of the bits!! :D
	 * 			(I checked this a bit too, don't worry X3 )
	 * @param inclusiveMaximum  inclusive is used so you can use {@link Long#MAX_VALUE} safely, which wouldn't be encodeable as an exclusive bound since that + 1 overflows to 0! ;3
	 * @return if you can safely translate the given range by subtracting inclusiveMinimum from a value from [inclusiveMinimum, inclusiveMaximum] to [0, inclusiveMaximum - inclusiveMinimum], which has (inclusiveMaximum - inclusiveMinimum + 1) elements in it :3
	 */
	public static boolean isTranslatableToZero(@Signed long inclusiveMinimum, @Signed long inclusiveMaximum)
	{
		if (inclusiveMinimum > inclusiveMaximum)  //they can be equal however, so it's > not >=
			throw new IllegalArgumentException();
		
		
		/*
		 * + We'll use pure theoretical (unbounded by bitlength) mathematics here, and say things like finiteAbs() to mean the actual finite-bitlength operation X3
		 * 
		 * inclusiveMaximum - inclusiveMinimum <= MAX
		 * inclusiveMaximum <= MAX + inclusiveMinimum
		 * 
		 * 
		 * • inclusiveMinimum < 0
		 * 		inclusiveMaximum <= MAX + inclusiveMinimum
		 * 		inclusiveMaximum <= MAX - |inclusiveMinimum|
		 * 		• inclusiveMinimum ≠ MIN
		 * 			inclusiveMaximum <= MAX - |inclusiveMinimum|
		 * 			inclusiveMaximum <= MAX - finiteAbs(inclusiveMinimum)
		 * 			inclusiveMaximum <= MAX + inclusiveMinimum
		 * 				The right-hand side can't overflow! \:D/
		 * 		• inclusiveMinimum = MIN
		 * 			inclusiveMaximum <= MAX - |MIN|
		 * 			inclusiveMaximum <= MAX - SIZE
		 * 				SIZE = MAX + 1
		 * 			inclusiveMaximum <= MAX - (MAX + 1)
		 * 			inclusiveMaximum <= MAX - MAX - 1
		 * 			inclusiveMaximum <= -1
		 * 			\:D/
		 * 			
		 * 			finiteSignedAdd(MAX, MIN) =
		 * 			toSigned(finiteUnsignedAdd(SMAX, SMIN)) =
		 * 			toSigned((SMAX + SMIN) % USIZE)
		 * 			toSigned(((2^Ns - 1) + -2^Ns) % 2^Nu)
		 * 			toSigned((2^Ns - 1 + -2^Ns) % 2^Nu)
		 * 			toSigned((-1) % 2^Nu)
		 * 			toSigned(2^Nu - 1)  (% = progmod!)
		 * 			toSigned(UMAX)
		 * 			-1
		 * 			
		 * 			So it's the same either way!! \:DD/
		 * 			
		 * 			inclusiveMaximum <= MAX + inclusiveMinimum
		 * 
		 * 
		 * • inclusiveMinimum >= 0
		 * 		( ⇒ inclusiveMaximum >= 0)
		 * 		inclusiveMaximum <= MAX + inclusiveMinimum
		 * 		
		 * 		If MAX >= inclusiveMaximum
		 * 		Then MAX + (anything >= 0) is still >= inclusiveMaximum XD
		 * 		and inclusiveMinimum >= 0!
		 * 		So MAX + inclusiveMinimum >= inclusiveMaximum always!
		 * 		
		 * 		So there you go!  \:DD/
		 * 		
		 * 		True!
		 * 
		 * 
		 * THAT'S IT!!  \:'DD/
		 */
		
		
		long MAX = Long.MAX_VALUE;
		//return inclusiveMinimum < 0 ? (inclusiveMaximum <= MAX + inclusiveMinimum) : true;
		return inclusiveMinimum >= 0 || (inclusiveMaximum <= MAX + inclusiveMinimum);
	}
	
	
	
	/**
	 * + Also note that if you could efficiently use unsigned arithmetic, you could just make the *new* interval's max bound and values be stored as unsigned integers of the same bitlength and everything would be fine! X'D
	 * 		+ Note that both the new inclusive max bound (inclusiveMaximum - inclusiveMinimum) and the size / exclusive max bound (inclusiveMaximum - inclusiveMinimum + 1, unless inclusiveMaximum - inclusiveMinimum = 2^64-1, the *unsigned* max bound!) both will work in unsigned arithmetic because our signed numbers are two's-complement not one's-complement add/subtract are the same binary operations on bits regardless of twos-signed or unsigned interpretation of the bits!! :D
	 * 			(I checked this a bit too, don't worry X3 )
	 * 
	 * + If this is true, {@link #isTranslatableToZero(long, long)} is also true, but not necessarily vice versa (logical implication).
	 * 
	 * @param inclusiveMaximum  inclusive is used so you can use {@link Long#MAX_VALUE} safely, which wouldn't be encodeable as an exclusive bound since that + 1 overflows to 0! ;3
	 * @return if you can safely store the size of the given interval (inclusiveMaximum - inclusiveMinimum + 1) in a long without causing overflow!
	 */
	public static boolean isIntervalSizeInRange(@Signed long inclusiveMinimum, @Signed long inclusiveMaximum)
	{
		if (inclusiveMinimum > inclusiveMaximum)  //they can be equal however, so it's > not >=
			throw new IllegalArgumentException();
		
		
		/*
		 * + We'll use pure theoretical (unbounded by bitlength) mathematics here, and say things like finiteAbs() to mean the actual finite-bitlength operation X3
		 * 
		 * inclusiveMaximum - inclusiveMinimum + 1 <= MAX
		 * inclusiveMaximum + 1 <= MAX + inclusiveMinimum
		 * 
		 * 
		 * • inclusiveMinimum < 0
		 * 		inclusiveMaximum + 1 <= MAX + inclusiveMinimum
		 * 		inclusiveMaximum <= MAX - 1 + inclusiveMinimum
		 * 		inclusiveMaximum <= MAX-1 - |inclusiveMinimum|
		 * 		
		 * 		• inclusiveMinimum = MIN
		 * 			inclusiveMaximum <= MAX-1 - |MIN|
		 * 			inclusiveMaximum <= MAX-1 - |-SIZE|
		 * 			inclusiveMaximum <= MAX-1 - SIZE
		 * 			inclusiveMaximum <= MAX-1 - (MAX+1)
		 * 			inclusiveMaximum <= MAX - 1 - MAX - 1
		 * 			inclusiveMaximum <= -1 - 1
		 * 			inclusiveMaximum <= -2
		 * 			
		 * 			Let's see if finite arithmetic produces this result already for us like in isTranslatableToZero()!  :D
		 * 			MAX - 1 + inclusiveMinimum =
		 * 			MAX - 1 + MIN
		 * 			asSigned(unsignedAdd(SMAX - 1, SSIZE))
		 * 			asSigned(unsignedAdd((2^Ns-1) - 1, 2^Ns))
		 * 			asSigned(((2^Ns-1) - 1 + 2^Ns) % 2^Nu)
		 * 			asSigned((2^Ns - 1 - 1 + 2^Ns) % 2^Nu)
		 * 			asSigned((2^Ns - 2 + 2^Ns) % 2^Nu)
		 * 			asSigned((2^Ns + 2^Ns - 2) % 2^Nu)
		 * 			asSigned((2*2^Ns - 2) % 2^Nu)
		 * 			asSigned((2^Nu - 2) % 2^Nu)
		 * 			asSigned(2^Nu - 2)
		 * 				(The high bit is oneeee sooooo)
		 * 				(Yeah! It's 0 - 1 - 1 in signed arithmetic \:D/ )
		 * 			-2
		 * 			
		 * 			\:D/
		 * 		
		 * 		
		 * 		• inclusiveMinimum = MIN + 1
		 * 			inclusiveMaximum <= MAX-1 - |MIN+1|
		 * 			inclusiveMaximum <= MAX-1 - |-SIZE+1|
		 * 			inclusiveMaximum <= MAX-1 - |1 - SIZE|
		 * 				SIZE = 2^Ns and Ns (number of signed bits; 31 or 63) we'll assume is more than 1—ie, the total bitlength (Nu) of the integer is more than 2 bits XD
		 * 			inclusiveMaximum <= MAX-1 - (SIZE - 1)
		 * 			inclusiveMaximum <= MAX-1 - (MAX+1 - 1)
		 * 			inclusiveMaximum <= MAX-1 - (MAX)
		 * 			inclusiveMaximum <= MAX-1 - MAX
		 * 			inclusiveMaximum <= -1
		 * 			
		 * 			And for this one :3
		 * 			Well let's see,
		 * 			In this:
		 * 			MAX - 1 + inclusiveMinimum
		 * 			If finiteAbs(inclusiveMinimum) doesn't experience its problem unless *its input* is SMIN
		 * 			Then adding a negative number is just the same as subtracting the absolute value and there's no worries!  finite arithmetic = infinite arithmetic! :D
		 * 			Because we might wrap below zero if it's (SMIN+1)
		 * 			But that's not considered overflow in signed arithmetic! :D  (of course, by definition! XD )
		 * 			So we didn't really need to consider this as a special case! XD
		 * 			\:D/
		 * 		
		 * 		So yes! :D
		 * 		This works in all cases :333
		 * 		inclusiveMaximum <= MAX - 1 + inclusiveMinimum
		 * 
		 * 
		 * • inclusiveMinimum >= 0
		 * 		inclusiveMaximum + 1 <= MAX + inclusiveMinimum
		 * 		
		 * 		• inclusiveMinimum == 0
		 * 			inclusiveMaximum + 1 <= MAX + inclusiveMinimum
		 * 			inclusiveMaximum + 1 <= MAX + 0
		 * 			inclusiveMaximum + 1 <= MAX
		 * 			inclusiveMaximum <= MAX - 1
		 * 			!(inclusiveMaximum > MAX - 1)
		 * 				inclusiveMaximum <= MAX
		 * 			!(inclusiveMaximum = MAX)
		 * 			inclusiveMaximum ≠ MAX
		 * 		
		 * 		• inclusiveMinimum > 0
		 * 			inclusiveMaximum + 1 <= MAX + inclusiveMinimum
		 * 			inclusiveMaximum <= MAX + inclusiveMinimum - 1
		 * 			inclusiveMaximum <= MAX + (inclusiveMinimumB + 1) - 1
		 * 				inclusiveMinimumB >= 0
		 * 			inclusiveMaximum <= MAX + inclusiveMinimumB + 1 - 1
		 * 			inclusiveMaximum <= MAX + inclusiveMinimumB
		 * 			So the same logic as last time applies :3
		 * 			Given inclusiveMaximum <= MAX already, adding 0 or more to MAX definitely doesn't change that! ^w^
		 * 			
		 * 			True!
		 */
		
		
		long MAX = Long.MAX_VALUE;
		
		if (inclusiveMinimum > 0)
			return true;
		else if (inclusiveMinimum == 0)
			return inclusiveMaximum != MAX;
		else
			//return inclusiveMaximum <= MAX - 1 + inclusiveMinimum;  //this right-hand side is either -2, -1, or >= 0 in signed arithmetic :>
			return inclusiveMaximum < MAX + inclusiveMinimum;  //so this is perfectly equivalent in all cases, because inclusiveMinimum <= 0! \:D/
	}
	
	
	
	
	
	
	/**
	 * @see #isTranslatableToZero(long, long)
	 */
	public static boolean isTranslatableToZero(@Signed int inclusiveMinimum, @Signed int inclusiveMaximum)
	{
		if (inclusiveMinimum > inclusiveMaximum)  //they can be equal however, so it's > not >=
			throw new IllegalArgumentException();
		
		int MAX = Integer.MAX_VALUE;
		//return inclusiveMinimum < 0 ? (inclusiveMaximum <= MAX + inclusiveMinimum) : true;
		return inclusiveMinimum >= 0 || (inclusiveMaximum <= MAX + inclusiveMinimum);
	}
	
	/**
	 * @see #isIntervalSizeInRange(long, long)
	 */
	public static boolean isIntervalSizeInRange(@Signed int inclusiveMinimum, @Signed int inclusiveMaximum)
	{
		int MAX = Integer.MAX_VALUE;
		
		if (inclusiveMinimum > 0)
			return true;
		else if (inclusiveMinimum == 0)
			return inclusiveMaximum != MAX;
		else
			//return inclusiveMaximum <= MAX - 1 + inclusiveMinimum;  //this right-hand side is either -2, -1, or >= 0 in signed arithmetic :>
			return inclusiveMaximum < MAX + inclusiveMinimum;  //so this is perfectly equivalent in all cases, because inclusiveMinimum <= 0! \:D/
	}
	
	
	
	
	
	
	
	
	/**
	 * Equivalent to <code>x / {@link #factorPower(long, long) factorPower}(x, factor))</code> but possibly more efficient.
	 * (eg, if x is 4981000 and factor is 10, then this returns 3, {@link #factorPower(long, long)} returns 1000, and {@link #removeUniqueFactor(long, long)} returns 4981 :D )
	 */
	public static long removeUniqueFactor(@Positive long x, @Positive long factor)
	{
		requirePositive(x);
		requirePositive(factor);
		
		//Todo more efficient implementation??!
		return x / factorPower(x, factor);
	}
	
	
	/**
	 * Equivalent to <code>{@link #pow(long, long) pow}(factor, {@link #factorMultiplicity(long, long) factorMultiplicity}(x, factor))</code> but possibly more efficient.
	 * (eg, if x is 4981000 and factor is 10, then {@link #factorMultiplicity(long, long)} returns 3, this returns 1000, and {@link #removeUniqueFactor(long, long)} returns 4981 :D )
	 */
	public static long factorPower(@Positive long x, @Positive long factor)
	{
		requirePositive(x);
		requirePositive(factor);
		
		//Todo more efficient implementation??!  Like gcd(x, factor^∞) basically? XD
		//  (then factorMultiplicity() could just take the logarithm of *this!* :D )
		return pow(factor, factorMultiplicity(x, factor));
	}
	
	
	/**
	 * The number of times you can wholly/losslessly divide <code>x</code> by <code>factor</code> :3
	 * (eg, if x is 4981000 and factor is 10, then this returns 3, {@link #factorPower(long, long)} returns 1000, and {@link #removeUniqueFactor(long, long)} returns 4981 :D )
	 */
	public static long factorMultiplicity(@Positive long x, @Positive long factor)
	{
		requirePositive(x);
		requirePositive(factor);
		
		
		//Todo more efficient implementation??!
		//  (like how countTrailingZeros() works for factor == 2???)
		
		long c = 0;
		
		while (x % factor == 0)
		{
			c++;
			x /= factor;
		}
		
		return c;
	}
}
