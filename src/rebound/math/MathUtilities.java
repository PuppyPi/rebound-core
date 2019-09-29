/*
 * Created on Sep 19, 2008
 * 	by the great Eclipse(c)
 */
package rebound.math;

import static java.lang.Math.*;
import static java.util.Objects.*;
import static rebound.bits.BitfieldSafeCasts.*;
import static rebound.bits.Unsigned.*;
import static rebound.math.SmallFloatMathUtilities.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.testing.WidespreadTestingUtilities.*;
import static rebound.text.StringUtilities.*;
import static rebound.util.BasicExceptionUtilities.*;
import static rebound.util.Primitives.*;
import static rebound.util.objectutil.BasicObjectUtilities.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Comparator;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.annotations.semantic.simpledata.ActuallyUnsigned;
import rebound.annotations.semantic.simpledata.NormalizesPrimitives;
import rebound.bits.Bytes;
import rebound.exceptions.DivisionByZeroException;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.InfinityException;
import rebound.exceptions.NonfiniteException;
import rebound.exceptions.NotANumberException;
import rebound.exceptions.NotYetImplementedException;
import rebound.exceptions.OverflowException;
import rebound.exceptions.StructuredClassCastException;
import rebound.exceptions.TruncationException;
import rebound.math.MathUtilities.CastableToIntegerTrait.CastableToSmallIntegerTrait;
import rebound.util.BasicExceptionUtilities;
import rebound.util.collections.ArrayUtilities;
import rebound.util.collections.FilterAwayReturnPath;
import rebound.util.collections.Mapper;
import rebound.util.collections.PairOrdered;
import rebound.util.collections.PairOrderedImmutable;
import rebound.util.collections.PolymorphicCollectionUtilities;
import rebound.util.collections.prim.PrimitiveCollections.LongArrayList;
import rebound.util.collections.prim.PrimitiveCollections.LongList;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionIntToObject;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionLongToObject;
import rebound.util.objectutil.JavaNamespace;

public class MathUtilities
implements JavaNamespace
{
	public static final Object Zero = normalizeNumberToRationalOrInteger(0);
	public static final Object One = normalizeNumberToRationalOrInteger(1);
	
	
	
	/*
	public static _$$prim$$_ checkNotZeroForDivide(_$$prim$$_ x)
	{
		if (x == 0)
			throw new DivisionByZeroException();
		return x;
	}
	
	public static _$$Primitive$$_ checkNotZeroForDivide(_$$Primitive$$_ x)
	{
		if (x == 0)
			throw new DivisionByZeroException();
		return x;
	}
	
	
	 */
	
	
	public static enum DivisionType
	{
		/**
		 * Ensure lossless division by throwing a {@link TruncationException} if it would be lossy.
		 */
		LOSSLESS,
		
		/**
		 * Allow lossy division (note: rounding strategy must be provided)
		 */
		LOSSY,
	}
	
	
	
	
	
	public static enum Infinity
	{
		Negative,
		Positive,
	}
	
	
	
	
	public static boolean isNegativeInfinity(Object o)
	{
		return eq(o, Float.NEGATIVE_INFINITY) || eq(o, Double.NEGATIVE_INFINITY) || o == Infinity.Negative;
	}
	
	public static boolean isPositiveInfinity(Object o)
	{
		return eq(o, Float.POSITIVE_INFINITY) || eq(o, Double.POSITIVE_INFINITY) || o == Infinity.Positive;
	}
	
	public static boolean isInfinity(Object o)
	{
		return isNegativeInfinity(o) || isPositiveInfinity(o);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<Integers factors
	/**
	 * This factors a number and stores the results as a list of primes and the number of them in the prime factor list.
	 * eg, 16 --> 2^4 --> {2} {4}
	 * eg, 12 --> 2^2 * 3 --> {2,3} {2,1}
	 * eg, 600 --> 2^3 * 3 * 5^2 --> {2,3,5}, {3,1,2}
	 * Note: the list of primes in a prime factorization cannot be longer than log2(n) (since the longest prime lists up to the given point are powers of two).  This means that <code>primeFactorization</code> definitely does not need to be longer than log2(n).  (which will never be more than 64 since only 64bit integers are accepted)
	 * @return number of distinct primes
	 */
	public static int factor(long number, long[] primes, long[] primeExponents)
	{
		if (number == 0 || number == 1)
			return 0;
		
		if (number < 0)
			throw new NotYetImplementedException("unsigned integers not yet supported");
		
		//Todo use a better algorithm
		int numberOfDistinctPrimes = 0;
		
		//I'm hoping this works
		for (long i = 2; i <= number; i++)
		{
			if (number == 1)
				return numberOfDistinctPrimes;
			
			if (number % i == 0)
			{
				primes[numberOfDistinctPrimes] = i;
				primeExponents[numberOfDistinctPrimes] = 0;
				
				while (number % i == 0)
				{
					primeExponents[numberOfDistinctPrimes]++;
					number /= i;
				}
				
				numberOfDistinctPrimes++;
			}
		}
		
		if (number == 1)
			return numberOfDistinctPrimes;
		
		throw new ImpossibleException();
	}
	
	/**
	 * The number of factors of a number is the product of 1 plus the counts of each prime.
	 * eg, for 12 = 2^2 * 3 = {2,3} {2,1}.  facs(12) = (2+1)*(1+1) = 6 factors
	 * eg, for 246960 = 2^4 * 3^2 * 5^1 * 7^3 = {2,3,5,7}{4,2,1,3}.  facs(246960) = (4+1)*(2+1)*(1+1)*(3+1) = 120 factors
	 */
	public static long getNumberOfFactors(long[] primeExponents, int primeCount)
	{
		long facs = 1;
		for (int i = 0; i < primeCount; i++)
			facs *= primeExponents[i]+1;
		return facs;
	}
	
	/**
	 * The number of prime factors is simply the length of the list from prime factorization.
	 * eg, for 12 = 2^2 * 3 = {2,3} {2,1}.  pfacs(12) = 2+1 = 3 primes long
	 * eg, for 246960 = 2^4 * 3^2 * 5^1 * 7^3 = {2,3,5,7}{4,2,1,3}.  pfacs(246960) = 4+2+1+3 = 10
	 */
	public static long getNumberOfPrimeFactors(long[] primeExponents, int primeCount)
	{
		long pfacs = 0;
		for (int i = 0; i < primeCount; i++)
			pfacs += primeExponents[i];
		return pfacs;
	}
	
	/**
	 * convert a list-pair like {2,3,11}^{2,5,4} into {2,3,5,7,11}^{2,5,0,0,4}.
	 * The last parameter is a list (at least as long as exhaustivePrimes) to hold the new, 0-expanded list of exponents.
	 * @param morePrimeExponentList gets set as the expanded list of prime exponents (expanded with 0's)
	 * @return the length of the expanded list
	 */
	public static int expandPrimeExponentList(long[] primes, long[] primeExponents, int count, long[] morePrimes, long[] morePrimeExponentList)
	{
		int e = 0;
		
		for (int i = 0; i < count; i++)
		{
			//Scan for the next prime in the list
			while (primes[i] != morePrimes[e] && e < morePrimes.length)
			{
				morePrimeExponentList[e] = 0;
				e++;
			}
			
			if (primes[i] != morePrimes[e])
			{
				throw new IllegalArgumentException("The second prime list is not a superset of the first prime list.");
			}
			
			
			morePrimeExponentList[e] = primeExponents[i];
			e++;
		}
		
		return e; //which is now 1+(last index), which is count
	}
	
	
	/**
	 * Gets a sorted list of prime numbers either until the bound is exceeded.
	 * @param max no prime greater than max will be added, but max may be added
	 */
	public static long[] getPrimesByCount(long max)
	{
		//Todo use a better algorithm
		LongList list = new LongArrayList();
		
		for (long n = 2; n <= max; n++)
			if (isPrime(n))
				list.addLong(n);
		
		return list.toLongArray();
	}
	
	/**
	 * Gets a sorted list of prime numbers until the desired number of primes is achieved.
	 * @param count  the number of primes to find
	 */
	public static long[] getPrimesByBound(int count)
	{
		//Todo use a better algorithm
		LongList list = new LongArrayList();
		
		for (long n = 2; list.size() < count; n++)
			if (isPrime(n))
				list.addLong(n);
		
		return list.toLongArray();
	}
	
	/**
	 * @return the first prime after <code>startingPoint</code>, or 0 on integer overflow
	 */
	public static long getFirstPrimeAboveBound(long startingPoint)
	{
		long n = startingPoint;
		while (true)
		{
			n++;
			if (n < startingPoint)
				return 0;
			
			if (isPrime(n))
				return n;
		}
	}
	
	
	public static interface LongInfiniteIterator
	{
		public long next();
	}
	
	/**
	 * Note: if <code>startingPoint</code> is a prime, it will be the first one returned; otherwise, the next prime will be returned.
	 */
	public static LongInfiniteIterator getPrimeGenerator(final long startingPoint)
	{
		//Todo use a better algorithm
		return new LongInfiniteIterator()
		{
			long n = startingPoint;
			
			@Override
			public long next()
			{
				while (!isPrime(this.n))
					this.n++;
				
				long p = this.n;
				
				//advance it for the next iteration
				this.n++;
				
				return p;
			}
		};
	}
	
	
	
	public static boolean isPrime(long number)
	{
		//Todo use a better algorithm
		for (long n = 2; n < number; n++)
			if (number % n == 0)
				return false;
		return true;
	}
	
	
	
	
	//Note: GCD and Reduce operate on unsigned integers, unless otherwise specified
	/**
	 * @return The GCD that the numbers were divided by
	 */
	public static int reduceUnsigned(int[] numbers)
	{
		return reduceUnsigned(numbers, 0, numbers.length);
	}
	
	/**
	 * @return The GCD that the numbers were divided by
	 */
	public static int reduceSigned(int[] numbers)
	{
		return reduceSigned(numbers, 0, numbers.length);
	}
	
	/**
	 * @return The GCD that the numbers were divided by
	 */
	public static int reduceUnsigned(int[] numbers, int start, int end)
	{
		int gcd = SmallIntegerMathUtilities.gcd(numbers.clone(), start, end);
		
		if (gcd == 0 || gcd == 1)
			//The numbers cannot be reduced any further
			return gcd;
		
		for (int i = start; i < end; i++)
			numbers[i] = divideU32(numbers[i], gcd);
		
		return gcd;
	}
	
	/**
	 * This version of reduce() works on <b>positive</b> signed integers.  In theory, it works exactly
	 * the same as reduce(), but it has a bit of a speed boost since it can utilize Java division.
	 * @return The GCD that the numbers were divided by
	 */
	public static int reduceSigned(int[] numbers, int start, int end)
	{
		int gcd = SmallIntegerMathUtilities.gcd(numbers.clone(), start, end);
		
		if (gcd == 0 || gcd == 1)
			//The numbers cannot be reduced any further
			return gcd;
		
		for (int i = start; i < end; i++)
			numbers[i] /= gcd;
		
		return gcd;
	}
	
	
	
	
	//TODO test this (if it's wrong, it's just false negatives, since GCD will always include the simple self-check to prevent non-factors from being returned (though it may not be the largest common factor))
	/**
	 * Test the equivalence of two fractions.
	 */
	public static boolean isFractionsEqual(int n1, int d1, int n2, int d2)
	{
		int gcd1 = SmallIntegerMathUtilities.gcd(n1, n1);
		int gcd2 = SmallIntegerMathUtilities.gcd(n2, d2);
		
		n1 /= gcd1;
		d1 /= gcd1;
		
		n2 /= gcd2;
		d2 /= gcd2;
		
		return n1 == n2 && d1 == d2;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int factorial32(int n)
	{
		if (n < 0)
			throw new IllegalArgumentException();
		if (n == 0)
			return 0;
		if (n > 12)
			throw new OverflowException();
		
		
		int f = 1;
		
		for (int i = 1; i <= n; i++)
			f *= i;
		
		return f;
	}
	
	
	
	public static long factorial64(int n)
	{
		if (n < 0)
			throw new IllegalArgumentException();
		if (n == 0)
			return 0;
		if (n > 20)
			throw new OverflowException();
		
		
		long f = 1;
		
		for (int i = 1; i <= n; i++)
			f *= i;
		
		return f;
	}
	//Integer factors>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<Safe arithmetic operations
	
	//TODO test theseeee :\
	
	
	//TODO Check that these algorithms I copied from heavens-knows-where are correct X'D''''
	
	
	
	
	
	
	
	//Safe arthmetic operations>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<Missing BigInteger constructors
	public static BigInteger toBigInteger(byte value)
	{
		//return new BigInteger(new byte[]{value});
		return toBigInteger((long)value);
	}
	
	public static BigInteger toBigInteger(short value)
	{
		//return new BigInteger(Bytes.packBigShort(value));
		return toBigInteger((long)value);
	}
	
	public static BigInteger toBigInteger(char value)
	{
		//return new BigInteger(Bytes.packBigShort((short)value));
		return toBigInteger((long)value);
	}
	
	public static BigInteger toBigInteger(int value)
	{
		//return new BigInteger(Bytes.packBigInt(value));
		return toBigInteger((long)value);
	}
	
	public static BigInteger toBigInteger(long value)
	{
		//return new BigInteger(Bytes.packBigLong(value));  //ha now BigInteger.valueOf() is FINALLY a thing! X'D
		return BigInteger.valueOf(value);
	}
	
	@NormalizesPrimitives
	public static BigInteger toBigInteger(@PolyInteger Object value)
	{
		requireNonNull(value);
		
		if (value instanceof Byte)
			return toBigInteger((byte)(Byte)value);
		
		else if (value instanceof Short)
			return toBigInteger((short)(Short)value);
		
		else if (value instanceof Character)
			return toBigInteger((char)(Character)value);
		
		else if (value instanceof Integer)
			return toBigInteger((int)(Integer)value);
		
		else if (value instanceof Long)
			return toBigInteger((long)(Long)value);
		
		
		else if (value instanceof BigInteger)
			return (BigInteger)value;
		
		
		else
			throw new StructuredClassCastException("Not a supported integer type!!: "+value.getClass().getName(), value.getClass());
	}
	
	
	
	
	public static BigInteger toBigIntegerFromUnsignedLong(@ActuallyUnsigned long value)
	{
		if (value >= 0)
		{
			return toBigInteger(value);
		}
		else
		{
			byte[] b = new byte[9];
			
			//b[0] = 0;  //this makes it be positive, since it's big-endian two's complement! ;D
			Bytes.putBigLong(b, 1, value);
			
			return new BigInteger(b);
		}
	}
	
	
	
	
	
	
	
	//ABOVE_MAX_VAL's are a thing because of two's complement! ^_~
	//(ESP the Long ones! :D )
	
	public static final BigInteger BIGINT_BYTE_MIN_VAL = toBigInteger(Byte.MIN_VALUE);
	public static final BigInteger BIGINT_BYTE_MAX_VAL = toBigInteger(Byte.MAX_VALUE);
	public static final BigInteger BIGINT_BYTE_ABOVE_MAX_VAL = toBigInteger(Byte.MAX_VALUE + 1);    // 2^7  :D
	
	public static final BigInteger BIGINT_SHORT_MIN_VAL = toBigInteger(Short.MIN_VALUE);
	public static final BigInteger BIGINT_SHORT_MAX_VAL = toBigInteger(Short.MAX_VALUE);
	public static final BigInteger BIGINT_SHORT_ABOVE_MAX_VAL = toBigInteger(Short.MAX_VALUE + 1);    // 2^15  :D
	
	public static final BigInteger BIGINT_CHAR_MIN_VAL = toBigInteger(Character.MIN_VALUE);
	public static final BigInteger BIGINT_CHAR_MAX_VAL = toBigInteger(Character.MAX_VALUE);
	public static final BigInteger BIGINT_CHAR_ABOVE_MAX_VAL = toBigInteger(Character.MAX_VALUE + 1);    // 2^16  :D
	
	public static final BigInteger BIGINT_INT_MIN_VAL = toBigInteger(Integer.MIN_VALUE);
	public static final BigInteger BIGINT_INT_MAX_VAL = toBigInteger(Integer.MAX_VALUE);
	public static final BigInteger BIGINT_INT_ABOVE_MAX_VAL = toBigInteger(Integer.MAX_VALUE + 1L);    // 2^31  :D
	
	public static final BigInteger BIGINT_LONG_MIN_VAL = toBigInteger(Long.MIN_VALUE);
	public static final BigInteger BIGINT_LONG_MAX_VAL = toBigInteger(Long.MAX_VALUE);
	public static final BigInteger BIGINT_LONG_ABOVE_MAX_VAL = BIGINT_LONG_MAX_VAL.add(BigInteger.ONE);    // 2^63  :D
	
	public static final BigInteger BIGINT_ULONG_MIN_VAL = BigInteger.ZERO;
	public static final BigInteger BIGINT_ULONG_MAX_VAL = toBigInteger(Long.MAX_VALUE).multiply(BigInteger.valueOf(2));
	public static final BigInteger BIGINT_ULONG_ABOVE_MAX_VAL = toBigInteger(Long.MAX_VALUE).add(BigInteger.ONE);    // 2^64  :D
	//Missing BigInteger constructors>
	
	
	
	
	
	
	
	
	
	public static boolean increment(boolean[] integer)
	{
		return increment(integer, 0, integer.length);
	}
	
	/**
	 * Increment a boolean binary array (little endian).
	 * @return <code>true</code> if there's more to go, <code>false</code> if it wrapped around
	 */
	public static boolean increment(boolean[] integer, int offset, int length)
	{
		int i = 0;
		while (true)
		{
			if (i >= length)
				break;
			
			if (integer[offset+i])
			{
				integer[offset+i] = false;
				i++;
			}
			else
			{
				integer[offset+i] = true;
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * low is the smallest value each digit can take on.  (the array should never contain things below this unless you want to do soomething funky XD :3 )
	 * high is exclusive high bound; one more than the highest possible value.
	 * Example: for incrementing base 10 digits, use low=0, high=10
	 * 
	 * @return <code>true</code> if there's more to go, <code>false</code> if it wrapped around
	 */
	public static boolean increment(int[] values, int inclusiveLowBound, int exclusiveHighBound)
	{
		int i = 0;
		while (i < values.length)
		{
			if (values[i] >= exclusiveHighBound-1)
			{
				values[i] = inclusiveLowBound;
				i++;
			}
			else
			{
				values[i]++;
				return true;
			}
		}
		return false;
	}
	public static boolean increment(long[] values, long inclusiveLowBound, long exclusiveHighBound)
	{
		int i = 0;
		while (i < values.length)
		{
			if (values[i] >= exclusiveHighBound-1)
			{
				values[i] = inclusiveLowBound;
				i++;
			}
			else
			{
				values[i]++;
				return true;
			}
		}
		return false;
	}
	
	
	
	public static boolean increment(int[] values, int inclusiveLowBound, int[] exclusiveHighBounds)
	{
		int i = 0;
		while (i < values.length)
		{
			if (values[i] >= exclusiveHighBounds[i]-1)
			{
				values[i] = inclusiveLowBound;
				i++;
			}
			else
			{
				values[i]++;
				return true;
			}
		}
		return false;
	}
	
	
	
	
	
	/**
	 * Polymorphic integer compare!
	 * (Supports mixing primitive wrapper types with each other and with {@link BigInteger}!)
	 */
	public static int cmpPolyIntegers(Object a, Object b)
	{
		a = normalizeNumberToRationalOrInteger(a);
		b = normalizeNumberToRationalOrInteger(b);
		
		
		if (a instanceof Long)
		{
			long ap = (Long)a;
			
			if (b instanceof Long)
			{
				long bp = (Long)b;
				return SmallIntegerMathUtilities.cmp(ap, bp);
			}
			else if (b instanceof BigInteger)
			{
				return toBigInteger(ap).compareTo((BigInteger)b);
			}
			else
			{
				if (!isInteger(b))
					throw new IllegalArgumentException();
				
				//I hope whatever that crazy integer type is supports comparing to java.lang.Long's!!
				return -((Comparable)b).compareTo(a);
			}
		}
		else if (a instanceof BigInteger)
		{
			return ((BigInteger) a).compareTo(toBigInteger(b));
		}
		else
		{
			if (!isInteger(a))
				throw new IllegalArgumentException();
			if (!isInteger(b))
				throw new IllegalArgumentException();
			
			//I hope whatever that crazy integer type is supports comparing to whatever type the other integer is!!
			return ((Comparable)a).compareTo(b);
		}
	}
	
	
	
	
	
	
	
	
	
	
	/* inline attic!
	public static int progmod_float(int index, int highBound)
	{
		if (highBound == 0)
			return 0;
		
		if (highBound == 0)
			throw new ArithmeticException("Division by zero");
		return (int)progmod((double)index, (double)highBound);
	}
	 */
	
	
	
	
	public static interface IntegerToBooleanFunction
	{
		public boolean evaluate(long argument);
	}
	
	public static enum RootingParity { MINIMIZE, MAXIMIZE }
	
	
	/**
	 * Find bounds, then {@link #imizeOnerootIntegerIndependent(IntegerToBooleanFunction, long, long, RootingParity) root} it! :D
	 * (in practice, since these are integers, it just uses the minimum and maximum values, since the best general strategy for finding smaller values..would be equivalent to rooting! XD)
	 */
	public static Long imizeOnerootIntegerIndependent(IntegerToBooleanFunction function, RootingParity rootingParity)
	{
		return imizeOnerootIntegerIndependent(function, Long.MIN_VALUE, Long.MAX_VALUE, rootingParity);
	}
	
	/**
	 * Finds the lowest value for which the given function will return true;
	 * assuming it only crosses the boolean axis once! (ie, there is a single minimum value! xD)
	 * @return <code>null</code> if there is none; ie, the function is <code>false</code> (apparently) everywhere (determined by checking the bounds, under the assumption that is has only one root)
	 */
	public static Long imizeOnerootIntegerIndependent(IntegerToBooleanFunction function, long lowBound, long highBound, RootingParity rootingParity)
	{
		if (rootingParity == RootingParity.MINIMIZE)
		{
			if (function.evaluate(lowBound) == false)
			{
				//Then it's what we expect.
			}
			else
			{
				//We got a free ride! :D
				return lowBound;
			}
			
			if (function.evaluate(highBound) == true)
			{
				//Then it's what we expect.
			}
			else
			{
				//Even the highBound is too low apparently..
				return null;
			}
		}
		
		else//if (rootingParity == RootingParity.MAXIMIZE)
		{
			if (function.evaluate(lowBound) == true)
			{
				//Then it's what we expect.
			}
			else
			{
				//Even the lowBound is too high apparently..
				return null;
			}
			
			if (function.evaluate(highBound) == false)
			{
				//Then it's what we expect.
			}
			else
			{
				//We got a free ride! :D
				return highBound;
			}
		}
		
		
		
		
		//Root it! :D
		{
			while (true)
			{
				if (rootingParity == RootingParity.MINIMIZE)
				{
					if (highBound == lowBound + 1)
						//highBound is the one we know it returns true for, and lowBound is the one we know it returns false for; so we're trying to find the smallest highBound.  If this is it, then return it! :D
						return highBound;
				}
				else
				{
					if (highBound == lowBound + 1)
						//highBound is the one we know it returns false for, and lowBound is the one we know it returns true for; so we're trying to find the largest lowBound.  If this is it, then return it! :D
						return lowBound;
				}
				
				if (highBound <= lowBound)
					throw new ImpossibleException("crazy rooting glitch?!");
				
				long midpoint = lowBound + (highBound - lowBound) / 2;  //if range is odd and > 1, rounding type is arbitrary :>
				
				//This is impossible because midpoint == lowBound happens when abs(range) < 2 (in which case, the above checks would have caught it), and midpoint == highBound when...well, it should just never EVER happen! XD
				if (midpoint == lowBound || midpoint == highBound)
					throw new ImpossibleException("rooting glitch?!");
				
				if (function.evaluate(midpoint) == (rootingParity == RootingParity.MINIMIZE ? true : false))
					highBound = midpoint;
				else
					lowBound = midpoint;
			}
		}
	}
	
	
	//Todo more rooting! :D
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * + postprocess: NaNs and floating points!


	public static _$$prim$$_ least(_$$prim$$_ a, _$$prim$$_ b)
	{
		return b < a ? b : a;
	}
	
	public static _$$prim$$_ greatest(_$$prim$$_ a, _$$prim$$_ b)
	{
		return b > a ? b : a;
	}
	
	
	public static _$$prim$$_ least(_$$prim$$_ a, _$$prim$$_ b, _$$prim$$_ c)
	{
		if (c > a && c > b)
			return least(a, b);
		else if (b > a && b > c)
			return least(a, c);
		else if (a > b && a > c)
			return least(b, c);
		else
			throw new ImpossibleException();
	}
	
	public static _$$prim$$_ greatest(_$$prim$$_ a, _$$prim$$_ b, _$$prim$$_ c)
	{
		if (c < a && c < b)
			return greatest(a, b);
		else if (b < a && b < c)
			return greatest(a, c);
		else if (a < b && a < c)
			return greatest(b, c);
		else
			throw new ImpossibleException();
	}
	
	
	public static _$$prim$$_ least(_$$prim$$_ a, _$$prim$$_ b, _$$prim$$_ c, _$$prim$$_ d)
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
			throw new ImpossibleException();
	}
	
	public static _$$prim$$_ greatest(_$$prim$$_ a, _$$prim$$_ b, _$$prim$$_ c, _$$prim$$_ d)
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
			throw new ImpossibleException();
	}
	
	
	
	
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
	
	
	 */
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @return null if there were 0 values given that weren't filtered away! XD''
	 */
	@Nullable
	public static <I, O extends Comparable> O leastMap(Mapper<I, O> function, Iterable<I> inputs)
	{
		PairOrdered<I, O> p = leastMapPair(function, inputs);
		return p == null ? null : p.getB();
	}
	
	public static <E extends Comparable> E least(Iterable<E> inputs)
	{
		return leastMap(x -> x, inputs);
	}
	
	public static <E extends Comparable> E least(E a, E b)
	{
		return mathcmp(a, b) < 0 ? a : b;
	}
	
	
	
	
	/**
	 * @return null if there were 0 values given that weren't filtered away! XD''
	 */
	@Nullable
	public static <I, O extends Comparable> O greatestMap(Mapper<I, O> function, Iterable<I> inputs)
	{
		PairOrdered<I, O> p = greatestMapPair(function, inputs);
		return p == null ? null : p.getB();
	}
	
	
	public static <E extends Comparable> E greatest(Iterable<E> inputs)
	{
		return greatestMap(x -> x, inputs);
	}
	
	public static <E extends Comparable> E greatest(E a, E b)
	{
		return mathcmp(a, b) > 0 ? a : b;
	}
	
	
	
	
	
	
	
	
	/**
	 * If multiple are the least, then only count on this API to return an arbitrary one!
	 * @return null if there were 0 values given that weren't filtered away! XD''
	 */
	@Nullable
	public static <I, O extends Comparable> PairOrdered<I, O> leastMapPair(Mapper<I, O> function, Iterable<I> inputs)
	{
		boolean has = false;
		I extremestInput = null;
		O extremestOutput = null;
		
		for (I input : inputs)
		{
			O output;
			try
			{
				output = function.f(input);
			}
			catch (FilterAwayReturnPath exc)
			{
				continue;
			}
			
			
			if (!has)
			{
				extremestOutput = output;
				extremestInput = input;
				has = true;
			}
			else
			{
				if (mathcmp(output, extremestOutput) < 0)
				{
					extremestOutput = output;
					extremestInput = input;
				}
			}
		}
		
		return has ? new PairOrderedImmutable<>(extremestInput, extremestOutput) : null;
	}
	
	
	
	
	
	
	
	
	/**
	 * If multiple are the least, then only count on this API to return an arbitrary one!
	 * @return null if there were 0 values given that weren't filtered away! XD''
	 */
	@Nullable
	public static <I, O extends Comparable> PairOrdered<I, O> greatestMapPair(Mapper<I, O> function, Iterable<I> inputs)
	{
		boolean has = false;
		I extremestInput = null;
		O extremestOutput = null;
		
		for (I input : inputs)
		{
			O output;
			try
			{
				output = function.f(input);
			}
			catch (FilterAwayReturnPath exc)
			{
				continue;
			}
			
			
			if (!has)
			{
				extremestOutput = output;
				extremestInput = input;
				has = true;
			}
			else
			{
				if (mathcmp(output, extremestOutput) > 0)
				{
					extremestOutput = output;
					extremestInput = input;
				}
			}
		}
		
		return has ? new PairOrderedImmutable<>(extremestInput, extremestOutput) : null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//Todo public static int getTheOnlyOneNotZeroOrNegative(Object excInstantiator, int... values)
	//Todo public static int getTheOnlyOneNotNegative(Object excInstantiator, int... values)
	//Todo public static int getTheOnlyOneMatching(MatcherInt pattern, Object excInstantiator, int... values)
	
	//Todo public static _$$prim$$_ getTheOnlyOneMatching(Matcher_$$Prim$$_ pattern, Object excInstantiator, _$$prim$$_... values)
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//////// <CASTING/CONVERTING!, with floats!! ////////
	
	public static int safeCastBigIntegerToS32(BigInteger input)
	{
		if (input.compareTo(BIGINT_INT_MIN_VAL) < 0 || input.compareTo(BIGINT_INT_MAX_VAL) > 0)
			throw new OverflowException(input+" -> S32");
		return input.intValue();
	}
	
	public static boolean isOverflowsCastBigIntegerToS32(BigInteger input)
	{
		return input.compareTo(BIGINT_INT_MIN_VAL) < 0 || input.compareTo(BIGINT_INT_MAX_VAL) > 0;
	}
	
	public static long safeCastBigIntegerToS64(BigInteger input) throws OverflowException
	{
		if (isOverflowsCastBigIntegerToS64(input))
			throw new OverflowException(input+" -> S64");
		return input.longValue();
	}
	
	public static boolean isOverflowsCastBigIntegerToS64(BigInteger input)
	{
		return input.compareTo(BIGINT_LONG_MIN_VAL) < 0 || input.compareTo(BIGINT_LONG_MAX_VAL) > 0;
	}
	
	
	public static @ActuallyUnsigned long safeCastBigIntegerToU64(BigInteger input) throws OverflowException
	{
		if (isOverflowsCastBigIntegerToU64(input))
			throw new OverflowException(input+" -> U64");
		return input.longValue();
	}
	
	public static boolean isOverflowsCastBigIntegerToU64(BigInteger input)
	{
		return input.compareTo(BIGINT_ULONG_MIN_VAL) < 0 || input.compareTo(BIGINT_ULONG_MAX_VAL) > 0;
	}
	
	
	
	public static BigInteger ulongToBigInteger(@ActuallyUnsigned long v)
	{
		return new BigInteger(1, Bytes.packBigLong(v));
	}
	
	
	
	
	
	public static byte safeCastIntegerToS8(Object input)
	{
		return safeCastS64toS8(safeCastIntegerToS64(input));
	}
	
	public static short safeCastIntegerToS16(Object input)
	{
		return safeCastS64toS16(safeCastIntegerToS64(input));
	}
	
	public static int safeCastIntegerToS32(Object input) throws OverflowException
	{
		return safeCastS64toS32(safeCastIntegerToS64(input));
	}
	
	
	
	public static byte safeCastAnythingToS8(Object input) throws OverflowException, TruncationException
	{
		return safeCastS64toS8(safeCastAnythingToS64(input));
	}
	
	public static short safeCastAnythingToS16(Object input) throws OverflowException, TruncationException
	{
		return safeCastS64toS16(safeCastAnythingToS64(input));
	}
	
	public static int safeCastAnythingToS32(Object input) throws OverflowException, TruncationException
	{
		return safeCastS64toS32(safeCastAnythingToS64(input));
	}
	
	
	
	
	public static long safeCastIntegerToS64(Object input) throws OverflowException
	{
		if (input instanceof CastableToSmallIntegerTrait) //order is many importants here!!
			return ((CastableToSmallIntegerTrait)input).toSmallInteger();
		else if (input instanceof CastableToIntegerTrait)
			input = ((CastableToIntegerTrait)input).toInteger(); //only one level deep srries
		
		if (input instanceof Byte)
			return (Byte)input;
		else if (input instanceof Short)
			return (Short)input;
		else if (input instanceof Character)
			return (Character)input;
		else if (input instanceof Integer)
			return (Integer)input;
		
		else if (input instanceof Long)
			return (Long)input;
		
		else if (input instanceof BigInteger)
			return safeCastBigIntegerToS64((BigInteger)input);
		
		throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(input);
	}
	
	public static long safeCastAnythingToS64(Object input) throws OverflowException, TruncationException
	{
		if (isInteger(input))
			return safeCastIntegerToS64(input);
		
		else if (input instanceof Float)
			return safeCastIntegerToS64(safeCastFloatToInteger((Float)input));
		else if (input instanceof Double)
			return safeCastIntegerToS64(safeCastFloatToInteger((Double)input));
		
		throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(input);
	}
	
	
	
	
	
	public static @ActuallyUnsigned long safeCastIntegerToU64(Object input) throws OverflowException
	{
		if (input instanceof CastableToSmallIntegerTrait) //order is many importants here!!
			return ((CastableToSmallIntegerTrait)input).toSmallInteger();
		else if (input instanceof CastableToIntegerTrait)
			input = ((CastableToIntegerTrait)input).toInteger(); //only one level deep srries
		
		if (input instanceof Byte)
			return (Byte)input;
		else if (input instanceof Short)
			return (Short)input;
		else if (input instanceof Character)
			return (Character)input;
		else if (input instanceof Integer)
			return (Integer)input;
		
		else if (input instanceof Long)
			return (Long)input;
		
		else if (input instanceof BigInteger)
			return safeCastBigIntegerToU64((BigInteger)input);
		
		throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(input);
	}
	
	public static @ActuallyUnsigned long safeCastAnythingToU64(Object input) throws OverflowException, TruncationException
	{
		if (isInteger(input))
			return safeCastIntegerToU64(input);
		
		else if (input instanceof Float)
			return safeCastIntegerToU64(safeCastFloatToInteger((Float)input));
		else if (input instanceof Double)
			return safeCastIntegerToU64(safeCastFloatToInteger((Double)input));
		
		throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(input);
	}
	
	
	
	
	
	
	public static boolean isOverflowsCastIntegerToS64(Object input)
	{
		if (input instanceof Byte)
			return false;
		else if (input instanceof Short)
			return false;
		else if (input instanceof Character)
			return false;
		else if (input instanceof Integer)
			return false;
		else if (input instanceof Long)
			return false;
		
		else if (input instanceof BigInteger)
			return isOverflowsCastBigIntegerToS64((BigInteger)input);
		
		else
			throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(input);
	}
	
	
	
	public static boolean isOverflowsCastToS32(Object input)
	{
		if (input instanceof Byte)
			return false;
		else if (input instanceof Short)
			return false;
		else if (input instanceof Character)
			return false;
		else if (input instanceof Integer)
			return false;
		
		else if (input instanceof Long)
		{ //oy Eclipse indenterrrr x'D
			return (Long)input < Integer.MIN_VALUE || (Long)input > Integer.MAX_VALUE;
		}
		else if (input instanceof BigInteger)
			return isOverflowsCastBigIntegerToS32((BigInteger)input);
		
		else
		{
			try
			{
				safeCastIntegerToS32(input);
			}
			catch (OverflowException exc)
			{
				return false;
			}
			
			return true;
			
			
			
		}
	}
	
	
	
	
	
	
	public static Object safeCastFloatToInteger(float f) throws TruncationException
	{
		if (!isFloatingPointAnInteger(f))
			throw new TruncationException();
		
		
		if (isFloatingOutOfS64Bounds(f))
		{
			Object r = convertFloatToRationalOrInteger(f);
			assert r instanceof Long || r instanceof BigInteger;
			return r;
		}
		else
		{
			return (long)f;
		}
	}
	
	public static Object safeCastFloatToInteger(double f) throws TruncationException
	{
		if (!isFloatingPointAnInteger(f))
			throw new TruncationException();
		
		
		if (isFloatingOutOfS64Bounds(f))
		{
			Object r = convertFloatToRationalOrInteger(f);
			assert r instanceof Long || r instanceof BigInteger;
			return r;
		}
		else
		{
			return (long)f;
		}
	}
	
	
	
	
	
	
	public static final BigInteger DoubleMaxBoundAsBigInteger = (BigInteger) safeCastFloatToInteger(Double.MAX_VALUE);
	public static final BigInteger DoubleMinBoundAsBigInteger = (BigInteger) safeCastFloatToInteger(-Double.MAX_VALUE);
	
	public static final BigInteger FloatMaxBoundAsBigInteger = (BigInteger) safeCastFloatToInteger(Float.MAX_VALUE);
	public static final BigInteger FloatMinBoundAsBigInteger = (BigInteger) safeCastFloatToInteger(-Float.MAX_VALUE);
	
	
	public static double safeCastAnythingToDouble(Object x) throws OverflowException
	{
		if (x instanceof BigInteger)
		{
			BigInteger bix = (BigInteger)x;
			
			if (DoubleMaxBoundAsBigInteger.compareTo(bix) < 0)
				throw new OverflowException();
			if (DoubleMinBoundAsBigInteger.compareTo(bix) < 0)
				throw new OverflowException();
			
			return bix.doubleValue();
		}
		else
		{
			return ((Number)x).doubleValue();
		}
	}
	
	public static float safeCastAnythingToFloat(Object x) throws OverflowException
	{
		if (x instanceof BigInteger)
		{
			BigInteger bix = (BigInteger)x;
			
			if (FloatMaxBoundAsBigInteger.compareTo(bix) < 0)
				throw new OverflowException();
			if (FloatMinBoundAsBigInteger.compareTo(bix) < 0)
				throw new OverflowException();
			
			return bix.floatValue();
		}
		else
		{
			return ((Number)x).floatValue();
		}
	}
	
	
	
	
	
	
	
	
	
	
	protected static final Object IEEE754SingleConversionSubnormalDenominator = pow(2, SizeOfSignificandInIEEE754Single - SubnormalActualCharacteristicInIEEE754Single);
	protected static final Object IEEE754DoubleConversionSubnormalDenominator = pow(2, SizeOfSignificandInIEEE754Double - SubnormalActualCharacteristicInIEEE754Double);
	
	
	@RationalOrInteger
	public static Object convertFloatToRationalOrInteger(float f)
	{
		int[] p = rawparseIEEE754SinglePrecision(f);
		int sign = p[0];
		int presignificand = p[1];
		int postcharacteristic = p[2];
		
		if (postcharacteristic == NonfinitePostCharacteristicInIEEE754Single)
		{
			throw isNaN(f) ? new NotANumberException() : new InfinityException();
		}
		
		Object v;
		{
			if (postcharacteristic == SubnormalPostCharacteristicInIEEE754Single)
			{
				// s / 2^(Ns - SNc)
				
				v = divide(presignificand, IEEE754SingleConversionSubnormalDenominator);
			}
			else
			{
				// 2^c + s/2^(Ns - c)
				v = add(pow(2, postcharacteristic), divide(presignificand, pow(2, SizeOfSignificandInIEEE754Single - postcharacteristic)));
			}
		}
		
		return sign == -1 ? negate(v) : v;
	}
	
	
	@RationalOrInteger
	public static Object convertFloatToRationalOrInteger(double f)
	{
		long[] p = rawparseIEEE754DoublePrecision(f);
		long sign = p[0];
		long presignificand = p[1];
		long postcharacteristic = p[2];
		
		if (postcharacteristic == NonfinitePostCharacteristicInIEEE754Double)
		{
			throw isNaN(f) ? new NotANumberException() : new InfinityException();
		}
		
		Object v;
		{
			if (postcharacteristic == SubnormalPostCharacteristicInIEEE754Double)
			{
				// s / 2^(Ns - SNc)
				
				v = divide(presignificand, IEEE754DoubleConversionSubnormalDenominator);
			}
			else
			{
				// 2^c + s/2^(Ns - c)
				v = add(pow(2, postcharacteristic), divide(presignificand, pow(2, SizeOfSignificandInIEEE754Double - postcharacteristic)));
			}
		}
		
		return sign == -1 ? negate(v) : v;
	}
	
	
	@RationalOrInteger
	public static Object convertFloatToClosestSmallRationalOrInteger(float f)
	{
		return castTruncatingPrecisionToClosestSmallRational(convertFloatToRationalOrInteger(f));
	}
	
	@RationalOrInteger
	public static Object convertFloatToClosestSmallRationalOrInteger(double f)
	{
		return castTruncatingPrecisionToClosestSmallRational(convertFloatToRationalOrInteger(f));
	}
	
	
	
	
	@RationalOrInteger
	public static Object castTruncatingPrecisionToClosestSmallRational(@RationalOrInteger Object possiblyBigRational) throws OverflowException
	{
		return castTruncatingPrecisionToClosestSmallRational(possiblyBigRational, RoundingMode.DOWN);  //Todo is DOWN a good choice of default? X'D
	}
	
	
	@RationalOrInteger
	public static Object castTruncatingPrecisionToClosestSmallRational(@RationalOrInteger Object possiblyBigRational, RoundingMode roundingMode) throws OverflowException
	{
		if (possiblyBigRational instanceof BigInteger)
			possiblyBigRational = normalizeNumberToRationalOrInteger(possiblyBigRational);
		
		if (possiblyBigRational instanceof BigInteger)
			throw new OverflowException();
		else if (possiblyBigRational instanceof Rational)
		{
			Rational ir = (Rational) possiblyBigRational;
			
			if (ir.getNumerator() instanceof BigInteger || ir.getDenominator() instanceof BigInteger)
			{
				Object rv = castTruncatingPrecisionToClosestRationalOfGivenDenominator(possiblyBigRational, Long.MAX_VALUE, roundingMode);
				
				if (rv instanceof BigInteger)
				{
					throw new OverflowException();
				}
				else if (rv instanceof Rational)
				{
					Rational r = (Rational) rv;
					
					if (r.getDenominator() instanceof BigInteger)
						throw new AssertionError();
					
					if (r.getNumerator() instanceof BigInteger)
						throw new OverflowException();
				}
				
				return rv;
			}
			else
			{
				return ir;
			}
		}
		else
		{
			//Small integer :3
			return possiblyBigRational;
		}
	}
	
	
	
	@PolyInteger
	public static Object getClosestIntegerNumberOfDenominationsToArbitraryRational(@RationalOrInteger Object possiblyBigRational, @RationalOrInteger Object denomination, RoundingMode roundingMode)
	{
		/*
		 * i*m ≈ x
		 * i ≈ x/m
		 * i = round(x/m)
		 */
		
		return round(divide(possiblyBigRational, denomination), roundingMode);
	}
	
	
	
	
	
	@RationalOrInteger
	public static Object castTruncatingPrecisionToClosestRational(@RationalOrInteger Object possiblyBigRational, @RationalOrInteger Object denomination, RoundingMode roundingMode)
	{
		return multiply(denomination, getClosestIntegerNumberOfDenominationsToArbitraryRational(possiblyBigRational, denomination, roundingMode));
	}
	
	
	
	
	
	@RationalOrInteger
	public static Object castTruncatingPrecisionToClosestRationalOfGivenDenominator(@RationalOrInteger Object possiblyBigRational, Object largestDenominator)
	{
		return castTruncatingPrecisionToClosestRationalOfGivenDenominator(possiblyBigRational, largestDenominator, RoundingMode.DOWN);
	}
	
	@RationalOrInteger
	public static Object castTruncatingPrecisionToClosestRationalOfGivenDenominator(@RationalOrInteger Object possiblyBigRational, @PolyInteger Object largestDenominator, RoundingMode roundingMode)
	{
		/*
		 * a/b
		 * a*D / b*D
		 *  D = d/b
		 * (a*d/b) / d
		 */
		
		Object[] r = getRationalNumeratorAndDenominator(possiblyBigRational);
		
		Object possiblyBigRationalN = r[0];
		Object possiblyBigRationalD = r[1];
		
		return rational(roundingIntegerDivision(multiply(possiblyBigRationalN, largestDenominator), possiblyBigRationalD, roundingMode), largestDenominator);
	}
	
	
	
	
	
	//TODO Support boolean testing for overflow here!! x'D
	public static interface CastableToIntegerTrait
	{
		/**
		 * Note that the result of this must be a basic integer thing, not another {@link CastableToIntegerTrait} override thing!
		 */
		public Object toInteger();
		
		
		public static interface CastableToSmallIntegerTrait
		extends CastableToIntegerTrait
		{
			public long toSmallInteger() throws OverflowException;
		}
	}
	
	
	
	
	
	public static long roundingDivision(long numerator, long divisor, RoundingMode roundingMode)
	{
		if (roundingMode == RoundingMode.CEILING)
			return ceilingDivision(numerator, divisor);
		else if (roundingMode == RoundingMode.FLOOR)
			return floorDivision(numerator, divisor);
		else if (roundingMode == RoundingMode.UNNECESSARY)
			return losslessDivision(numerator, divisor);
		else if (roundingMode == RoundingMode.UP)
			return awayfromzeroDivision(numerator, divisor);
		else if (roundingMode == RoundingMode.DOWN)
			return towardzeroDivision(numerator, divisor);
		else if (roundingMode == RoundingMode.HALF_UP)
			return halfawayfromzeroDivision(numerator, divisor);
		else if (roundingMode == RoundingMode.HALF_DOWN)
			return halftowardzeroDivision(numerator, divisor);
		else if (roundingMode == RoundingMode.HALF_EVEN)
			return halfevenDivision(numerator, divisor);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(roundingMode);
	}
	
	
	
	@PolyInteger
	public static Object round(@RationalOrInteger Object x, RoundingMode roundingMode)
	{
		if (x instanceof Rational)
		{
			Object n, d;
			{
				Object[] r = getRationalNumeratorAndDenominator(x);
				n = r[0];
				d = r[1];
			}
			
			return roundingIntegerDivision(n, d, roundingMode);
		}
		else
		{
			return x;
		}
	}
	
	@PolyInteger
	public static Object roundingIntegerDivision(@PolyInteger Object n, @PolyInteger Object d, RoundingMode roundingMode) throws DivisionByZeroException
	{
		if (matheq(d, One))
			return n;
		else if (matheq(n, Zero))
			return n;
		else if (matheq(d, Zero))
			throw new DivisionByZeroException();
		else
		{
			if (n instanceof Long && d instanceof Long)
			{
				return roundingDivision((Long)n, (Long)d, roundingMode);
			}
			else
			{
				BigInteger nb = toBigInteger(n);
				BigInteger db = toBigInteger(d);
				
				return normalizeNumberToRationalOrInteger(roundingBigIntegerDivision(nb, db, roundingMode));
			}
		}
	}
	
	
	
	public static BigInteger roundingBigIntegerDivision(BigInteger n, BigInteger d, RoundingMode roundingMode) throws DivisionByZeroException
	{
		if (matheq(d, Zero))
			throw new DivisionByZeroException();
		
		if (roundingMode == RoundingMode.DOWN)
			return n.divide(d);
		else if (roundingMode == RoundingMode.UP)
		{
			BigInteger[] qr = n.divideAndRemainder(d);
			BigInteger q = qr[0];
			BigInteger r = qr[1];
			
			if (r.equals(BigInteger.ZERO))
				return q;
			else
			{
				int s = n.signum() * d.signum();
				
				if (s == 1)
					return q.add(BigInteger.ONE);
				else if (s == -1)
					return q.subtract(BigInteger.ONE);
				else
					throw new AssertionError();  //We already special-cased that! X'D
			}
		}
		else
			throw new NotYetImplementedException("Rounding mode: "+roundingMode);
	}
	
	
	
	
	//Todo add *more* override interface things?
	//Todo support CastableToIntegerTrait?
	
	//TODO handle infinities!
	//			(+∞ + -∞ = exception XD, since finite+∞ is lossy ;> )
	
	
	
	
	
	/**
	 * Note: this does NOT perform fraction-reduction!!  It only performs type conversion (eg, (int)13 --> (long)13,  2/1 --> 2)
	 * It DOES convert integer-type floats to
	 */
	@NormalizesPrimitives
	public static Object normalizeNumberToRationalOrInteger(Object a)
	{
		requireNonNull(a);
		
		if (a instanceof Long)
			return a;
		
		if (a instanceof Integer || a instanceof Short || a instanceof Character || a instanceof Byte)
			return ((Number)a).longValue();
		
		if (a instanceof Rational)
		{
			if (matheq(((Rational)a).getDenominator(), 1))
				return normalizeNumberToRationalOrInteger(((Rational)a).getNumerator());
			else
				return a;
		}
		
		if (a instanceof BigInteger)
		{
			if (!isOverflowsCastBigIntegerToS64((BigInteger)a))
				return ((BigInteger)a).longValue();
			else
				return a;
		}
		
		
		
		if (a instanceof Float)
			return convertFloatToRationalOrInteger((Double)a);
		if (a instanceof Double)
			return convertFloatToRationalOrInteger((Double)a);
		
		
		
		throw new StructuredClassCastException(a.getClass());
	}
	
	@NormalizesPrimitives
	public static Rational normalizeNumberToRational(Object a)
	{
		@RationalOrInteger Object roi = normalizeNumberToRationalOrInteger(a);
		return isInteger(roi) ? new ImmutableRational(roi, 1l) : (Rational)roi;
	}
	
	
	
	public static double floatingApproximationDouble(Object a)
	{
		if (a instanceof Number)  //accounts for the pass-through case where it's already in floating point form! :D
			return ((Number)a).doubleValue();
		else if (a instanceof Rational)
			return ((Rational) a).toDoubleEstimate();
		else
			throw newClassCastExceptionOrNullPointerException(a);
	}
	
	public static float floatingApproximationSingle(Object a)
	{
		if (a instanceof Number)  //accounts for the pass-through case where it's already in floating point form! :D
			return ((Number)a).floatValue();
		else if (a instanceof Rational)
			return (float)((Rational) a).toDoubleEstimate();
		else
			throw newClassCastExceptionOrNullPointerException(a);
	}
	//////// CASTING/CONVERTING!> ////////
	
	
	
	
	
	
	
	
	//This is an example of what we *don't* need to tag with @NormalizesPrimitives :D
	public static int signum(Object a)
	{
		requireNonNull(a);
		
		
		if (a instanceof Double)
		{
			double aa = requireFinite((Double)a);
			if (aa < 0)
				return -1;
			if (aa > 0)
				return +1;
			if (aa == 0)
				return 0;
			throw new AssertionError();
		}
		if (a instanceof Float)
		{
			float aa = requireFinite((Float)a);
			if (aa < 0)
				return -1;
			if (aa > 0)
				return +1;
			if (aa == 0)
				return 0;
			throw new AssertionError();
		}
		
		
		a = normalizePrimitive(a);
		
		if (a instanceof Long)
		{
			return SmallIntegerMathUtilities.signum((Long)a);
		}
		else if (a instanceof BigInteger)
		{
			return ((BigInteger)a).signum();
		}
		else if (a instanceof BigDecimal)
		{
			return ((BigDecimal)a).signum();
		}
		else if (a instanceof Rational)
		{
			assert signum(((Rational) a).getDenominator()) == 1;
			return signum(((Rational) a).getNumerator());
		}
		else
		{
			throw new StructuredClassCastException(a.getClass());
		}
	}
	
	public static boolean isNegative(Object x)
	{
		return signum(x) < 0;
	}
	
	public static boolean isZero(Object x)
	{
		return signum(x) == 0;
	}
	
	public static boolean isPositive(Object x)
	{
		return signum(x) > 0;
	}
	
	
	
	/**
	 * If x <= 0, throw {@link ArithmeticException} XD
	 * If x < 1, return -1
	 * If x = 1, return 0
	 * If x > 1, return 1
	 * 
	 * :>
	 */
	public static int multiplicativeSignum(@RationalOrInteger Object x)
	{
		if (mathcmp(x, Zero) <= 0)
			throw new ArithmeticException();
		
		return signum(subtract(x, One));
	}
	
	
	
	
	
	
	
	public static int mathhash(@Nonnull Object a)
	{
		requireNonNull(a);
		
		if (isIntegerPrimitiveWrapperInstance(a))
		{
			return mathhashS64(((Number)a).longValue());
		}
		else if (isInteger(a))
		{
			try
			{
				final long i = safeCastAnythingToS64(a);
				return mathhashS64(i);
			}
			catch (OverflowException exc)
			{
			}
			catch (TruncationException exc)
			{
			}
			
			if (a instanceof BigInteger)
			{
				BigInteger bi = (BigInteger) a;
				BigInteger i = bi.mod(BIGINT_ULONG_ABOVE_MAX_VAL);
				return mathhashS64(i.longValueExact());
			}
			else if (a instanceof Rational)
			{
				Object n = ((Rational)a).getNumerator();
				Object d = ((Rational)a).getNumerator();
				
				assert matheq(d, 1);
				
				return mathhash(n);
			}
			else
			{
				throw new NotYetImplementedException("Numeric type: "+a.getClass());
			}
		}
		else if (a instanceof Rational)
		{
			Object n = ((Rational)a).getNumerator();
			Object d = ((Rational)a).getNumerator();
			
			if (matheq(d, 1))
			{
				return mathhash(n);
			}
			else
			{
				int nHash = mathhash(n);
				int dHash = mathhash(d);
				
				return nHash ^ dHash;
			}
		}
		else
		{
			throw new NotYetImplementedException("Numeric type: "+a.getClass());
		}
	}
	
	public static int mathhashS64(long i)
	{
		if (i > Integer.MIN_VALUE && i < Integer.MAX_VALUE)
			return (int)i;
		else
			return ((int)i) ^ ((int)(i >> 32));
	}
	
	
	
	
	public static boolean matheq(@Nonnull Object a, @Nonnull Object b)
	{
		return mathcmp(a, b) == 0;
	}
	
	//Todo more optimized versions??  (how well do they jit with Sun's jit!?)
	public static int mathcmp(@Nonnull Object a, @Nonnull Object b)
	{
		requireNonNull(a);
		requireNonNull(b);
		
		if (eq(a, b))
			return 0;
		
		
		//Handle infinities :3
		{
			boolean ani = isNegativeInfinity(a);
			boolean bni = isNegativeInfinity(b);
			boolean api = isPositiveInfinity(a);
			boolean bpi = isPositiveInfinity(b);
			
			if (ani)
				return bni ? 0 : -1;
			else if (bni)
				return 1;
			
			if (api)
				return bpi ? 0 : 1;
			else if (bpi)
				return -1;
		}
		
		
		
		
		if (a instanceof Long && b instanceof Long)
			return SmallIntegerMathUtilities.cmp((Long)a, (Long)b);
		
		
		if (a instanceof BigInteger && b instanceof BigInteger)
			return ((BigInteger)a).compareTo((BigInteger)b);
		
		if (a instanceof BigInteger && b instanceof Long)
			return ((BigInteger)a).compareTo(BigInteger.valueOf((Long)b));
		
		if (a instanceof Long && b instanceof BigInteger)
			return BigInteger.valueOf((Long)a).compareTo((BigInteger)b);
		
		
		
		
		if (a instanceof Comparable && a.getClass() == b.getClass())
			return ((Comparable) a).compareTo(b);
		
		return signum(subtract(a, b));
	}
	
	
	public static final Comparator<Object> MathComparator = MathUtilities::mathcmp;
	public static final Comparator<Object> MathComparatorReverse = (a,b) -> mathcmp(b, a);
	
	
	
	
	
	
	
	public static boolean isInteger(Object x)
	{
		return x instanceof Long || x instanceof Integer || x instanceof Short || x instanceof Character || x instanceof Byte || x instanceof BigInteger;
	}
	
	public static boolean isRationalOrInteger(Object x)
	{
		return isInteger(x) || x instanceof Rational;
	}
	
	public static boolean isNonintegerRational(Object x)
	{
		return x instanceof Rational && !matheq(((Rational)x).getDenominator(), 1);
	}
	
	@NormalizesPrimitives
	protected static Object internalRational(Object numerator, Object denominator)
	{
		numerator = normalizeNumberToRationalOrInteger(numerator);
		denominator = normalizeNumberToRationalOrInteger(denominator);
		
		if (!isInteger(numerator))
			throw new IllegalArgumentException("Numerator not an integer!!: "+numerator);
		if (!isInteger(denominator))
			throw new IllegalArgumentException("Numerator not an integer!!: "+numerator);
		
		if (matheq(denominator, 1))
			return numerator;
		else
			return new ImmutableRational(numerator, denominator);
	}
	
	
	/**
	 * Note that this *may* return an integer if the given fraction is in fact an integer!! \o/
	 * (like 4/2 or 12/3 or anything/1! XD   ^_~  )
	 */
	@NormalizesPrimitives
	public static Object rational(Object numerator, Object denominator)
	{
		return reduce(numerator, denominator);  //it needs to be reduced for hashcode/equals anyways--ohwell XD''
	}
	
	
	
	public static Object[] getRationalNumeratorAndDenominator(@RationalOrInteger Object rationalOrInteger)
	{
		Object n, d;
		{
			if (rationalOrInteger instanceof Rational)
			{
				Rational r = (Rational) rationalOrInteger;
				n = r.getNumerator();
				d = r.getDenominator();
			}
			else
			{
				n = rationalOrInteger;
				d = One;
			}
		}
		
		return new Object[]{n, d};
	}
	
	
	public static long[] getRationalNumeratorAndDenominatorSmall(@RationalOrInteger Object rationalOrInteger)
	{
		long n, d;
		{
			if (rationalOrInteger instanceof Rational)
			{
				Rational r = (Rational) rationalOrInteger;
				Object nn = r.getNumerator();
				Object dd = r.getDenominator();
				
				if (dd instanceof BigInteger)  //this comes first, because if they're both BigIntegers, the actual value *might* not actually be out of range! XD''
					throw new TruncationException();
				if (nn instanceof BigInteger)
					throw new OverflowException();
				
				n = (Long)nn;
				d = (Long)dd;
			}
			else
			{
				if (rationalOrInteger instanceof BigInteger)
					throw new OverflowException();
				
				n = (Long)rationalOrInteger;
				d = (Long)One;
			}
		}
		
		return new long[]{n, d};
	}
	
	
	
	
	
	
	
	@NormalizesPrimitives
	public static Object negate(Object a)
	{
		a = normalizeNumberToRationalOrInteger(a);
		
		if (a instanceof Long)
		{
			long l = (Long)a;
			if (l == Long.MIN_VALUE)
				return BIGINT_LONG_ABOVE_MAX_VAL;
			else
				return -l;
		}
		else if (a instanceof Rational)
		{
			Rational r = (Rational)a;
			
			return rational(negate(r.getNumerator()), r.getDenominator());
		}
		else if (a instanceof BigInteger)
		{
			return ((BigInteger)a).negate();
		}
		else if (a instanceof Double)
		{
			return -((Double)a);
		}
		else if (a instanceof BigDecimal)
		{
			return ((BigDecimal)a).negate();
		}
		else
		{
			throw new StructuredClassCastException(a.getClass());
		}
	}
	
	
	
	
	@NormalizesPrimitives
	public static Object reciprocate(Object a)
	{
		a = normalizeNumberToRationalOrInteger(a);
		
		
		//		//Floating points...just smile and noddd xD
		//		{
		//			if (a instanceof Double)
		//				return 1d / (Double)a;
		//		}
		
		
		//RRRRRRAtttionallssss! /o/ :D!
		{
			if (a instanceof Rational)
				return rational(((Rational) a).getDenominator(), ((Rational) a).getNumerator());
		}
		
		
		//Innnntegerrrrssss! ^ww^
		{
			return rational(1, a);
		}
	}
	
	
	
	
	
	
	
	
	@NormalizesPrimitives
	public static Object add(Object a, Object b)
	{
		a = normalizeNumberToRationalOrInteger(a);
		b = normalizeNumberToRationalOrInteger(b);
		
		
		
		//		//Floating points...just smile and noddd xD
		//		{
		//			if (a instanceof Double || b instanceof Double)
		//			{
		//				a = ((Number)a).doubleValue();
		//				b = ((Number)b).doubleValue();
		//
		//				return (Double)a + (Double)b;
		//			}
		//		}
		
		
		
		//RRRRRRAtttionallssss! /o/ :D!
		{
			boolean aRat = a instanceof Rational;
			boolean bRat = b instanceof Rational;
			if (aRat || bRat)
			{
				Object aNum = aRat ? ((Rational)a).getNumerator() : a;
				Object aDen = aRat ? ((Rational)a).getDenominator() : 1;
				Object bNum = bRat ? ((Rational)b).getNumerator() : b;
				Object bDen = bRat ? ((Rational)b).getDenominator() : 1;
				return reduce(rational(add(multiply(aNum, bDen), multiply(bNum, aDen)), multiply(aDen, bDen)));
			}
		}
		
		
		
		//Innnntegerrrrssss! ^ww^
		{
			if (a instanceof Long && b instanceof Long && !SmallIntegerMathUtilities.isOverflow_add_s64((Long)a, (Long)b))
			{
				long sa = (Long)a;
				long sb = (Long)b;
				
				long c = sa + sb;
				
				if (sa > 0 && sb > 0 && !(c > 0 && c > sa && c > sb)) throw new AssertionError();
				if (sa < 0 && sb < 0 && !(c < 0 && c < sa && c < sb)) throw new AssertionError();
				if ((c - sa) != sb) throw new AssertionError();
				if ((c - sb) != sa) throw new AssertionError();
				
				return c;
			}
			
			
			if (a instanceof Long)
				a = BigInteger.valueOf((Long)a);
			
			if (b instanceof Long)
				b = BigInteger.valueOf((Long)b);
			
			BigInteger c = ((BigInteger)a).add((BigInteger)b);
			
			return isOverflowsCastBigIntegerToS64(c) ? c : c.longValue();
		}
	}
	
	
	
	
	
	
	@NormalizesPrimitives
	public static Object multiply(Object a, Object b)
	{
		a = normalizeNumberToRationalOrInteger(a);
		b = normalizeNumberToRationalOrInteger(b);
		
		
		
		//		//Floating points...just smile and noddd xD
		//		{
		//			if (a instanceof Double || b instanceof Double)
		//			{
		//				a = ((Number)a).doubleValue();
		//				b = ((Number)b).doubleValue();
		//
		//				return (Double)a * (Double)b;
		//			}
		//		}
		
		
		
		//RRRRRRAtttionallssss! /o/ :D!
		{
			boolean aRat = a instanceof Rational;
			boolean bRat = b instanceof Rational;
			if (aRat || bRat)
			{
				Object aNum = aRat ? ((Rational)a).getNumerator() : a;
				Object aDen = aRat ? ((Rational)a).getDenominator() : 1;
				Object bNum = bRat ? ((Rational)b).getNumerator() : b;
				Object bDen = bRat ? ((Rational)b).getDenominator() : 1;
				
				return reduce(rational(multiply(aNum, bNum), multiply(aDen, bDen)));
			}
		}
		
		
		
		//Innnntegerrrrssss! ^ww^
		{
			if (a instanceof Long && b instanceof Long && !SmallIntegerMathUtilities.isOverflow_mul_s64((Long)a, (Long)b))
			{
				long sa = (Long)a;
				long sb = (Long)b;
				
				long c = (Long)a * (Long)b;
				
				if (((sa > 0 && sb > 0) || (sa < 0 && sb < 0)) && !(c > 0))
					throw new AssertionError();
				if (((sa > 0 && sb < 0) || (sa < 0 && sb > 0)) && !(c < 0)) throw new AssertionError();
				if (c != 0 && (c % sa) != 0) throw new AssertionError();
				if (c != 0 && (c % sb) != 0) throw new AssertionError();
				if (c != 0 && (c / sa) != sb) throw new AssertionError();
				if (c != 0 && (c / sb) != sa) throw new AssertionError();
				
				return c;
			}
			
			
			if (a instanceof Long)
				a = BigInteger.valueOf((Long)a);
			
			if (b instanceof Long)
				b = BigInteger.valueOf((Long)b);
			
			BigInteger c = ((BigInteger)a).multiply((BigInteger)b);
			return isOverflowsCastBigIntegerToS64(c) ? c : c.longValue();
		}
	}
	
	
	
	
	
	
	
	@NormalizesPrimitives
	public static Object pow(Object base, Object exponent)
	{
		base = normalizeNumberToRationalOrInteger(base);
		exponent = normalizeNumberToRationalOrInteger(exponent);
		
		
		if (isZero(exponent))
		{
			if (isZero(base))
				throw new ArithmeticException("0 to the power of 0!!  :O");
			else
				return 1L;
		}
		
		
		
		//		//Floating points...just smile and noddd xD
		//		{
		//			if (a instanceof Double || b instanceof Double)
		//			{
		//				a = ((Number)a).doubleValue();
		//				b = ((Number)b).doubleValue();
		//
		//				return Math.pow((Double)a, (Double)b);
		//			}
		//		}
		
		
		
		
		
		if (!isInteger(exponent))
			throw new NotYetImplementedException("Fractional exponents!! (Ie, Roots!) \\o/");
		
		
		
		
		
		boolean aRat = base instanceof Rational;
		Object aNum = aRat ? ((Rational)base).getNumerator() : base;
		Object aDen = aRat ? ((Rational)base).getDenominator() : One;
		
		
		
		//Remember that trick from algebra? ;D
		if (isNegative(exponent))
		{
			exponent = negate(exponent);
			
			Object s = aNum;
			aNum = aDen;
			aDen = s;
		}
		
		
		
		
		if (matheq(aDen, 1))
		{
			//Innnntegerrrrssss! ^ww^
			base = aNum;
			
			
			
			//TODO Make overflow checking for this X'D'''
			//if (a instanceof Long && b instanceof Long && !isOverflow_pow_s64((Long)a, (Long)b))
			//	return pow((long)(Long)a, (long)(Long)b);
			
			
			BigInteger baseBI;
			{
				if (base instanceof Long)
					baseBI = BigInteger.valueOf((Long)base);
				else if (base instanceof BigInteger)
					baseBI = (BigInteger) base;
				else
					throw newClassCastExceptionOrNullPointerException(base);
			}
			
			int exponentS32 = safeCastIntegerToS32(exponent);
			
			
			BigInteger power = baseBI.pow(exponentS32);
			return isOverflowsCastBigIntegerToS64(power) ? power : power.longValueExact();
		}
		
		
		else
		{
			//RRRRRRAtttionallssss! /o/ :D!
			
			return rational(pow(aNum, exponent), pow(aDen, exponent));
		}
	}
	
	
	
	
	
	
	
	
	
	@NormalizesPrimitives
	@RationalOrInteger
	public static Object subtract(@RationalOrInteger Object a, @RationalOrInteger Object b)
	{
		return add(a, negate(b));
	}
	
	@NormalizesPrimitives
	@RationalOrInteger
	public static Object divide(@RationalOrInteger Object dividend, @RationalOrInteger Object divisor)
	{
		return multiply(dividend, reciprocate(divisor));
	}
	
	
	
	
	
	
	
	
	
	
	@NormalizesPrimitives
	@RationalOrInteger
	public static Object reduce(@RationalOrInteger Object a)
	{
		a = normalizeNumberToRationalOrInteger(a);
		
		if (a instanceof Rational)
		{
			Object n = ((Rational) a).getNumerator();
			Object d = ((Rational) a).getDenominator();
			
			return reduce(n, d);
		}
		else if (isInteger(a))
		{
			return a;
		}
		else
		{
			throw new StructuredClassCastException("Either it's an unsupported type of rational or integer, or it's neither a rational nor an integer!!", a.getClass());
		}
	}
	
	
	
	@NormalizesPrimitives
	public static Object reduce(Object numerator, Object denominator)
	{
		Object n = normalizeNumberToRationalOrInteger(numerator);
		Object d = normalizeNumberToRationalOrInteger(denominator);
		
		if (matheq(d, Zero))
			throw new DivisionByZeroException();
		
		
		if (n instanceof Long && d instanceof Long && !SmallIntegerMathUtilities.isOverflow_neg_s64((Long)n) && !SmallIntegerMathUtilities.isOverflow_neg_s64((Long)d))
		{
			long nn = (Long)n;
			long dd = (Long)d;
			
			
			boolean negative = (nn < 0) ^ (dd < 0);
			
			nn = abs(nn);
			dd = abs(dd);
			
			
			long gcd = SmallIntegerMathUtilities.gcd_binary(nn, dd);
			
			//Juuuuust make suuuuure ^^''''''
			if ((nn % gcd) != 0)
				throw new AssertionError();
			if ((dd % gcd) != 0)
				throw new AssertionError();
			
			nn /= gcd;
			dd /= gcd;
			
			if (negative)
				nn = -nn;  //this one can't fail! :D
			
			return internalRational(nn, dd);
		}
		
		else
		{
			BigInteger nn;
			{
				if (n instanceof Long)
					nn = BigInteger.valueOf((Long)n);
				else if (n instanceof BigInteger)
					nn = (BigInteger)n;
				else
					throw new IllegalArgumentException("Numerator not an integer!!: ("+getClassNT(n)+")"+n);
			}
			
			
			BigInteger dd;
			{
				if (d instanceof Long)
					dd = BigInteger.valueOf((Long)d);
				else if (d instanceof BigInteger)
					dd = (BigInteger)d;
				else
					throw new IllegalArgumentException("Denominator not an integer!!: ("+getClassNT(n)+")"+d);
			}
			
			
			
			
			
			boolean negative = (nn.compareTo(BigInteger.ZERO) < 1) ^ (dd.compareTo(BigInteger.ZERO) < 1);
			
			nn = nn.abs();
			dd = dd.abs();
			
			
			
			BigInteger gcd = nn.gcd(dd);
			
			nn = nn.divide(gcd);
			dd = dd.divide(gcd);
			
			if (negative)
				nn = nn.negate();
			
			return internalRational(nn, dd);
		}
	}
	
	
	
	
	
	
	
	
	@NormalizesPrimitives
	public static <I> Object sumMapping(Mapper<I, Object> mapper, Iterable<I> inputs)
	{
		Object result = Zero;
		
		for (I input : inputs)
		{
			Object output;
			try
			{
				output = mapper.f(input);
			}
			catch (FilterAwayReturnPath exc)
			{
				continue;
			}
			
			result = add(result, output);
		}
		
		return result;
	}
	
	
	@NormalizesPrimitives
	public static <I> Object productMapping(Mapper<I, Object> mapper, Iterable<I> inputs)
	{
		Object result = One;
		
		for (I input : inputs)
		{
			Object output;
			try
			{
				output = mapper.f(input);
			}
			catch (FilterAwayReturnPath exc)
			{
				continue;
			}
			
			result = multiply(result, output);
		}
		
		return result;
	}
	
	
	
	
	
	
	
	
	
	@NormalizesPrimitives
	public static <I> long sumMapping64(Mapper<I, Long> mapper, Iterable<I> inputs)
	{
		long result = 0;
		
		for (I input : inputs)
		{
			long output;
			try
			{
				output = mapper.f(input);
			}
			catch (FilterAwayReturnPath exc)
			{
				continue;
			}
			
			result = addExact(result, output);
		}
		
		return result;
	}
	
	
	@NormalizesPrimitives
	public static <I> long productMapping64(Mapper<I, Long> mapper, Iterable<I> inputs)
	{
		long result = 1;
		
		for (I input : inputs)
		{
			long output;
			try
			{
				output = mapper.f(input);
			}
			catch (FilterAwayReturnPath exc)
			{
				continue;
			}
			
			result = multiplyExact(result, output);
		}
		
		return result;
	}
	
	
	
	
	
	@NormalizesPrimitives
	public static <I> int sumMapping32(Mapper<I, Integer> mapper, Iterable<I> inputs)
	{
		return safeCastS64toS32(sumMapping64(i -> upcastNT(mapper.f(i)), inputs));
	}
	
	
	@NormalizesPrimitives
	public static <I> int productMapping32(Mapper<I, Integer> mapper, Iterable<I> inputs)
	{
		return safeCastS64toS32(productMapping64(i -> upcastNT(mapper.f(i)), inputs));
	}
	
	
	public static Integer upcastNT(Byte b)
	{
		return b == null ? null : (int)(byte)b;
	}
	
	public static Integer upcastNT(Short b)
	{
		return b == null ? null : (int)(short)b;
	}
	
	public static Long upcastNT(Integer b)
	{
		return b == null ? null : (long)(int)b;
	}
	
	
	
	
	
	
	//See ObjectUtilities.eq(), and cmp()  ^_^
	
	//Todo more Object-polymorphics! :D
	
	
	
	
	
	
	
	
	//Math itself doesn't contain utilities for single precision floats!  Oh wells!
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<Very specific, yet mathematical things X>
	
	//<Statistics/Scientific/Graphing dataset things! :D
	
	/*
	 * Three formats:
	 * 		+ [Bundled] Samples      ([[i,d,d], [i,d,d], [i,d,d], ...])    (probably conceptually the best one ;>  ..though least efficient naively ,_, )
	 * 			+ Note: individual samples usually have to be *actual* java array things, to improve performance xP
	 * 		+ Interleaved [Samples]  ([i0, d0, d0, i1, d1, d1, ...])
	 * 		+ Rotated [Samples]      ([[i0, i1, i2, ...], [d0, d1, d2, ...], [d0, d1, d2, ...]])
	 */
	
	/**
	 * Finds a line, y = m*x + b through the datass :D
	 * 
	 * + Note: for a vertical line, returns {NaN, (the horizontal displacement of it)} :>
	 * 
	 * @return {m, b}
	 */
	public static double[] linearRegression_BundledSamplesLongs(Object input)
	{
		//TODO
		throw new NotYetImplementedException();
	}
	
	
	
	
	
	
	
	
	
	//Statistics/Scientific/Graphing dataset things! :D>
	
	/*
	 * @param containerAspectRatio container width/height
	 * @return new int[]{width/columns, height/rows}  ^_^
	 * /
	public static int[] calculateOptimalGridDimensions(int numberOfElements, double containerAspectRatio)
	{
		throw new NotYetImplementedException();
	}
	///*///
	
	
	
	public static Object sumIntegers(Object a, Object b)
	{
		//TODO
		throw new NotYetImplementedException();
	}
	
	public static Object sumIntegersVariadic(Object collectionOfIntegers)
	{
		Object rv = 0;
		
		for (Object x : PolymorphicCollectionUtilities.anyToSingleUseIterable(collectionOfIntegers))
		{
			rv = sumIntegers(rv, x);
		}
		
		return rv;
	}
	
	public static Object rectDataSumIntegersCrossDimension(Object collectionOfCollectionsOfIntegers, int crossIndex)
	{
		Object rv = 0;
		
		for (Object row : PolymorphicCollectionUtilities.anyToSingleUseIterable(collectionOfCollectionsOfIntegers))
		{
			Object v = PolymorphicCollectionUtilities.getuni(row, crossIndex);
			
			if (v == null)
				throw new IndexOutOfBoundsException(String.valueOf(crossIndex));
			
			rv = sumIntegers(rv, v);
		}
		
		return rv;
	}
	
	//Trivial X3
	public static Object rectDataSumIntegersFirstDimension(Object collectionOfCollectionsOfIntegers, int index)
	{
		return sumIntegersVariadic(PolymorphicCollectionUtilities.getuni(collectionOfCollectionsOfIntegers, index));
	}
	
	
	
	
	public static long rectDataSumLongIntegersCrossDimension(Object collectionOfCollectionsOfIntegers, int crossIndex)
	{
		long rv = 0;
		
		for (Object row : PolymorphicCollectionUtilities.anyToSingleUseIterable(collectionOfCollectionsOfIntegers))
		{
			Object v = PolymorphicCollectionUtilities.getuni(row, crossIndex);
			
			if (v == null)
				throw new IndexOutOfBoundsException(String.valueOf(crossIndex));
			
			long smallv = safeCastIntegerToS64(v);
			
			rv = SmallIntegerMathUtilities.safe_add_s64(rv, smallv);
		}
		
		return rv;
	}
	
	
	
	
	public static BigInteger getBigIntegerFromTwosComplementBinaryOctets(byte[] octets)
	{
		return new BigInteger(octets);
	}
	
	public static BigInteger getBigIntegerFromUnsignedBinaryOctets(byte[] octets)
	{
		//Adding extra zeros in front doesn't change the (unsigned) magnitude or the bits there, but it does change the bit *length*, which changes which bit is the sign bit--which is always zero since you added extra zeros!  Instant happy unsignedness! :D!
		return new BigInteger(ArrayUtilities.concat1WithArray((byte)0, octets));
	}
	
	//for (int i = 0; i < 256; i++)
	//	p(MathUtilities.getBigIntegerFromTwosComplementBinaryOctets(new byte[]{(byte)i})+" "+MathUtilities.getBigIntegerFromUnsignedBinaryOctets(new byte[]{(byte)i}));
	
	
	
	
	
	
	
	
	
	/**
	 * Say you want to test if a polar coordinate is in an interval of 225° to 135°, ie all but a 90° pie on the left side (in right-handed coors)
	 * Modularly, any interval of two points could be one of TWO intervals!  Usually one smaller and one larger (unless it's half the modular base, eg 180° and 180° :3 )
	 * If you just compare >=/<= with the lowest and highest, you'll end up testing against the smaller 135° to 225°!
	 * 
	 * And to make matters more complicated, modular parameters can be arbitrarily normalized!
	 * Ie, is it -45° to 45° or 315° to 45° ?!  Because that completely changes the result with simple >=/<= ! \o/
	 * 
	 * So one way that is completely non-arbitrary, is to specify a *modular* low and high.
	 * Whereas the order in points on a normal interval is arbitrary (ie, if it's high then low you can just swap it to be nice :3, since it can't work any other way than low-to-high!)
	 * On a *modular* interval, the "low" point can actually be greater numerically than the "high" point,
	 * and the ordering of them IS important, and specifies which of the two pie slices you mean! \o/ :D
	 * 
	 * So, the *modular* interval specified always starts at the modular low, and increases until it reaches the modular high.
	 * For example, if counter-clockwise is increasing the value, then if the low is 225° and the high 135°, then it would be a very large arc interval of 270°!
	 * But if it's 135° to 225°, then it's the smaller arc of only 90°.
	 * 
	 * ^ww^
	 */
	public static boolean isInModularInterval(double x, double modularLow, double modularHigh, double modularBase)
	{
		x = progmod(x, modularBase);
		modularLow = progmod(modularLow, modularBase);
		modularHigh = progmod(modularHigh, modularBase);
		
		if (modularLow > modularHigh)
		{
			//Oh that's how it works?!
			//That's so cool 8)
			return x >= modularLow || x < modularHigh;
		}
		else
		{
			//note: handles modularLow == modularHigh too ^^
			return x >= modularLow && x < modularHigh;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static Object parsePolyInteger(String text) throws NumberFormatException
	{
		try
		{
			return Long.parseLong(text);
		}
		catch (NumberFormatException exc)
		{
			//Maybe it was too big for 64-bit two's complement integer format (ie, "small integer") \o/
			return new BigInteger(text);
		}
	}
	
	
	
	
	//TODO Support formatting to possibly-repeating-decimals! :D
	//TODO Support PARSING FROM possibly-repeating-decimals! :0 :D!(?)
	
	/**
	 * Note that this may return an integer if the denominator would be 1, but that's not guaranteed!
	 * 
	 * This will never return anything other than a rational or an integer though, since those are pretty well-defined (super/sub) sets in...well probably pretty much the whole of reality in all universes XDD  ^wwwww^
	 * 
	 * + Hey!  Did you know this allows you to use scientific notation (eg "1.522e9") for integers!!?  :D!
	 */
	@Nonnull
	public static Number parseRationalOrInteger(@Nonnull String text) throws NumberFormatException
	{
		requireNonNull(text);
		
		Object base = 10L;  //this could be configurable here, but you'd have to update parsePolyInteger() to support others, though ^^' XD
		
		text = text.trim();
		text = trim(text, '+');
		
		
		boolean negative = false;
		{
			//Todo more optimized form ^^'
			while (text.startsWith("-"))
			{
				negative = !negative;
				text = text.substring(1);
				
				text = text.trim();
				text = trim(text, '+');
			}
		}
		
		
		
		
		Object rational;
		
		
		int slash = text.indexOf('/');
		if (slash != -1)
		{
			String num = text.substring(0, slash).trim();
			String den = text.substring(slash+1).trim();
			
			rational = reduce(parsePolyInteger(num), parsePolyInteger(den));
		}
		else
		{
			int e = text.indexOf('e');
			
			if (e == -1)
				e = text.indexOf('E');
			
			
			int exponent = 0;
			if (e != -1)
			{
				exponent = safeCastIntegerToS16(parsePolyInteger(text.substring(e+1).trim()));  //S16 and not S32 or higher because if the exponent is too large..we could end up taking a loooootta RAM X"D    (the amount of ram used is proportional to the log of the value (ie, basically the exponent!!) )
				text = text.substring(0, e).trim();
			}
			
			
			int d = text.indexOf('.');
			Object fractionalPart = null;
			if (d != -1)
			{
				String decpart = text.substring(d+1).trim();
				int len = decpart.length();
				
				Object fracnum = parsePolyInteger(decpart);
				Object fracden = pow(base, len);  //.1 -> 1/10, .01 -> 1/100, ...    Yup, checks out! ^,^
				fractionalPart = rational(fracnum, fracden);
				
				text = text.substring(0, d).trim();
			}
			
			
			//Remainder is an integer, regardless of if the others were present! :D
			Object intpart = text.isEmpty() ? Zero : parsePolyInteger(text);
			
			
			
			
			rational = intpart;
			
			//*Very* important to come before multiplying by the exponent part!! XD''
			if (fractionalPart != null)
				rational = add(rational, fractionalPart);
			
			if (exponent != 0)
				rational = multiply(rational, pow(base, exponent));  //explicit function invocation to make sure to use the poly/BigInteger version! (for future-proofing, in case base becomes a primitive or something)
		}
		
		
		if (negative)
			rational = negate(rational);
		
		return (Number) reduce(rational);
	}
	
	
	
	public static String formatRationalOrIntegerBase10NoExponentNotation(@RationalOrInteger Object number, int maximumDecimalPlaces)
	{
		if (isInteger(number))
		{
			return number.toString();
		}
		else if (number instanceof Rational)
		{
			//TODO Do this right X'DD
			
			double f = floatingApproximationDouble(number);
			
			if (!Double.isFinite(f))
				throw new NonfiniteException();
			
			return new DecimalFormat("#."+mulnn('#', maximumDecimalPlaces)).format(f);
		}
		else
		{
			throw newClassCastExceptionOrNullPointerException(number);
		}
	}
	
	
	
	
	
	
	public static Object parseRationalOrIntegerPassingNulls(String x)
	{
		return x == null ? null : parseRationalOrInteger(x);
	}
	
	public static String formatRationalOrIntegerBase10NoExponentNotationPassingNulls(@RationalOrInteger Object number, int maximumDecimalPlaces)
	{
		return number == null ? null : formatRationalOrIntegerBase10NoExponentNotation(number, maximumDecimalPlaces);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Returns M - S, but modularly/wrappingly!  :D
	 * Imagine a clock.
	 * M and S are hands on that clock :>
	 * How many hours backwards around the clock do you have to go to get from M back to S?  :D
	 * 
	 * If M is slightly after S, then a small number is returned.
	 * If M is slightly before S, then a big number is returned!  (That much slightly smaller than the size of the clock; eg, 12 hours or 24 hours or 360 degrees or etc.  :3 )
	 */
	@Nonnegative
	public static int modularSubtraction(int minuend, int subtrahend, int modularBase)
	{
		int diff = minuend - subtrahend;
		
		if (diff >= 0)
			return diff;
		else
			return modularBase + diff;  //diff will be negative here :33
	}
	
	
	
	@Nonnegative
	public static int modularSubtractionLesser(int minuend, int subtrahend, int modularBase)
	{
		int diff = minuend - subtrahend;
		
		if (diff >= 0)
			return least(diff, modularBase - diff);
		else
			return least(-diff, modularBase + diff);  //diff will be negative here :33
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//TODO Test theseeeeeeee! :D
	
	
	/**
	 * Note: empty intervals are not equivalent to each other nor interchangeable!  Many times code will use an empty interval on a point (ie, "[x, x)" ) to represent a single point without needing to use a whole other format than interval-typed values :3
	 */
	public static final ArithmeticIntegerInterval EmptyInterval = new ArithmeticIntegerInterval(0, 0);
	
	public static ArithmeticIntegerInterval emptyInterval()
	{
		return EmptyInterval;
	}
	
	
	public static ArithmeticIntegerInterval singletonInterval(long v)
	{
		return intervalByPointAndSize(v, 1);
	}
	
	
	public static ArithmeticIntegerInterval intervalByPoints(long lowInclusive, long highExclusive)
	{
		return new ArithmeticIntegerInterval(lowInclusive, highExclusive - lowInclusive);
	}
	
	public static ArithmeticIntegerInterval intervalByPointAndSize(long lowInclusive, long size)
	{
		return new ArithmeticIntegerInterval(lowInclusive, size);
	}
	
	
	public static ArithmeticIntegerInterval intervalByPointsOrEmptyIfReversed(long lowInclusive, long highExclusive)
	{
		return intervalByPointAndSizeOrEmptyIfReversed(lowInclusive, highExclusive - lowInclusive);
	}
	
	public static ArithmeticIntegerInterval intervalByPointAndSizeOrEmptyIfReversed(long lowInclusive, long size)
	{
		if (size < 0)
			return emptyInterval();
		else
			return intervalByPointAndSize(lowInclusive, size);
	}
	
	
	public static ArithmeticIntegerInterval intervalByIntervalExplicitBounds(ArithmeticIntegerInterval low, boolean lowInclusive, ArithmeticIntegerInterval high, boolean highInclusive)
	{
		long l = lowInclusive ? low.getStart() : low.getPastEnd();
		long h = highInclusive ? high.getPastEnd() : high.getStart();
		return intervalByPointsOrEmptyIfReversed(l, h);
	}
	
	public static ArithmeticIntegerInterval intervalByExplicitBounds(long low, boolean lowInclusive, long high, boolean highInclusive)
	{
		//return intervalByIntervalExplicitBounds(intervalByPointAndSize(low, 1), lowInclusive, intervalByPointAndSize(high, 1), highInclusive);
		long l = lowInclusive ? low : low+1;
		long h = highInclusive ? high+1 : high;
		return intervalByPointsOrEmptyIfReversed(l, h);
	}
	
	
	
	
	public static ArithmeticIntegerInterval intervalAdd(ArithmeticIntegerInterval a, ArithmeticIntegerInterval b)
	{
		long al = a.getStart();
		long ah = a.getPastEnd();
		long bl = b.getStart();
		long bh = b.getPastEnd();
		
		return intervalByPoints(al + bl, ah + bh);
	}
	
	public static ArithmeticIntegerInterval intervalSubtract(ArithmeticIntegerInterval a, ArithmeticIntegerInterval b)
	{
		long al = a.getStart();
		long ah = a.getPastEnd();
		long bl = b.getStart();
		long bh = b.getPastEnd();
		
		return intervalByPoints(al - bh, ah - bl);
	}
	
	public static ArithmeticIntegerInterval intervalNegate(ArithmeticIntegerInterval a)
	{
		long al = a.getStart();
		long ah = a.getPastEnd();
		
		return intervalByPoints(-ah, -al);
	}
	
	public static ArithmeticIntegerInterval intervalMultiply(ArithmeticIntegerInterval a, ArithmeticIntegerInterval b)
	{
		long al = a.getStart();
		long ah = a.getPastEnd();
		long bl = b.getStart();
		long bh = b.getPastEnd();
		
		long c0 = al*bl;
		long c1 = ah*bl;
		long c2 = al*bh;
		long c3 = ah*bh;
		
		return intervalByPoints(SmallIntegerMathUtilities.least(c0, c1, c2, c3), SmallIntegerMathUtilities.greatest(c0, c1, c2, c3));
	}
	
	/**
	 * NOTE: This can't work properly without the ability to represent infinity ^^'   (think of what happens when some points in b (the denominator) get close to and then include zero!  (eg, by being negative on one side and positive on the other!))
	 */
	public static ArithmeticIntegerInterval intervalDivide(ArithmeticIntegerInterval a, ArithmeticIntegerInterval b)
	{
		if (b.containsPoint(0))
			throw new DivisionByZeroException();
		
		long al = a.getStart();
		long ah = a.getPastEnd();
		long bl = b.getStart();
		long bh = b.getPastEnd();
		
		long c0 = al/bl;
		long c1 = ah/bl;
		long c2 = al/bh;
		long c3 = ah/bh;
		
		return intervalByPoints(SmallIntegerMathUtilities.least(c0, c1, c2, c3), SmallIntegerMathUtilities.greatest(c0, c1, c2, c3));
	}
	
	
	
	
	
	
	/**
	 * @throws IllegalArgumentException if they aren't touching or overlapping!  (we don't support compound intervals!)
	 */
	public static ArithmeticIntegerInterval intervalUnion(ArithmeticIntegerInterval a, ArithmeticIntegerInterval b)
	{
		long al = a.getStart();
		long ah = a.getPastEnd();
		long bl = b.getStart();
		long bh = b.getPastEnd();
		
		//Check :>
		{
			long l = greatest(al, bl);
			long h = least(ah, bh);
			
			if (l > h)
				throw new IllegalArgumentException("Tried to union disjoint intervals: "+a+" ∪ "+b);
		}
		
		long l = least(al, bl);
		long h = greatest(ah, bh);
		
		return intervalByPoints(l, h);
	}
	
	
	/**
	 * The lowest of the low to the highest of the high..including any space between the intervals even if that wasn't actually in either interval (which is what makes this different from {@link #intervalUnion(ArithmeticIntegerInterval, ArithmeticIntegerInterval)})
	 */
	public static ArithmeticIntegerInterval intervalBoundsUnion(ArithmeticIntegerInterval a, ArithmeticIntegerInterval b)
	{
		long al = a.getStart();
		long ah = a.getPastEnd();
		long bl = b.getStart();
		long bh = b.getPastEnd();
		
		long l = least(al, bl);
		long h = greatest(ah, bh);
		
		return intervalByPoints(l, h);
	}
	
	
	/**
	 * @return an {@link #emptyInterval() empty interval} if they don't overlap (including if they just barely touch!)
	 */
	public static ArithmeticIntegerInterval intervalIntersection(ArithmeticIntegerInterval a, ArithmeticIntegerInterval b)
	{
		long al = a.getStart();
		long ah = a.getPastEnd();
		long bl = b.getStart();
		long bh = b.getPastEnd();
		
		long l = greatest(al, bl);
		long h = least(ah, bh);
		
		return intervalByPointsOrEmptyIfReversed(l, h);
	}
	
	
	
	public static long intervalMidpointFloor(ArithmeticIntegerInterval a)
	{
		long al = a.getStart();
		long ah = a.getPastEnd();
		return SmallIntegerMathUtilities.floorDivision(ah - al, 2);
	}
	
	public static long intervalMidpointCeiling(ArithmeticIntegerInterval a)
	{
		long al = a.getStart();
		long ah = a.getPastEnd();
		return SmallIntegerMathUtilities.ceilingDivision(ah - al, 2);
	}
	
	public static long intervalMidpointNearzero(ArithmeticIntegerInterval a)
	{
		long al = a.getStart();
		long ah = a.getPastEnd();
		return (ah - al) / 2;
	}
	
	public static long intervalMidpointAwayzero(ArithmeticIntegerInterval a)
	{
		long al = a.getStart();
		long ah = a.getPastEnd();
		return SmallIntegerMathUtilities.awayfromzeroDivision(ah - al, 2);
	}
	
	public static long intervalMidpointRounding(ArithmeticIntegerInterval a)
	{
		long al = a.getStart();
		long ah = a.getPastEnd();
		return SmallIntegerMathUtilities.roundingIntegerDivision(ah - al, 2);
	}
	
	
	
	
	
	
	
	
	/**
	 * @return null if super doesn't completely enclose sub!
	 */
	public static ArithmeticIntegerInterval intervalIntersectionOfSubset(ArithmeticIntegerInterval superCandidate, ArithmeticIntegerInterval subCandidate)
	{
		return intervalIsSubsetOrEqual(superCandidate, subCandidate) ? subCandidate : null;
	}
	
	
	public static boolean intervalIsSubsetOrEqual(ArithmeticIntegerInterval superCandidate, ArithmeticIntegerInterval subCandidate)
	{
		ArithmeticIntegerInterval a = superCandidate;
		ArithmeticIntegerInterval b = subCandidate;
		
		long al = a.getStart();
		long ah = a.getPastEnd();
		long bl = b.getStart();
		long bh = b.getPastEnd();
		
		return al <= bl && ah >= bh;
	}
	
	public static boolean intervalIsSubsetNotEqual(ArithmeticIntegerInterval superCandidate, ArithmeticIntegerInterval subCandidate)
	{
		return intervalIsSubsetOrEqual(superCandidate, subCandidate) && !eq(superCandidate, subCandidate);
	}
	
	
	/**
	 * @return null if one doesn't completely enclose the other!
	 */
	public static ArithmeticIntegerInterval intervalIntersectionOfSubsetSymmetric(ArithmeticIntegerInterval a, ArithmeticIntegerInterval b)
	{
		ArithmeticIntegerInterval r = intervalIntersectionOfSubset(a, b);
		return r != null ? r : intervalIntersectionOfSubset(b, a);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static @Nullable Integer binarySearchS32(UnaryFunctionIntToObject<Direction1D> predicate, int inclusiveLowBound, int exclusiveHighBound)
	{
		Long r = binarySearchS64(i -> predicate.f(safeCastS64toS32(i)), inclusiveLowBound, exclusiveHighBound);
		return r == null ? null : safeCastS64toS32(r);
	}
	
	public static @Nullable Long binarySearchS64(UnaryFunctionLongToObject<Direction1D> predicate, long inclusiveLowBound, long exclusiveHighBound)
	{
		if (exclusiveHighBound < inclusiveLowBound)
			throw new IllegalArgumentException();
		
		if (exclusiveHighBound == inclusiveLowBound)
			return null;
		
		long l = inclusiveLowBound;
		long h = exclusiveHighBound;
		
		while (true)
		{
			asrt(l != h);
			
			//Todo consider overflows ^^'
			//long m = (h - l) / 2 + l;
			long m = (l + h) / 2;
			
			asrt(m != h);
			asrt((h == l + 1) == (m == l));
			
			Direction1D r = predicate.f(m);
			
			if (r == Direction1D.HigherUp)
			{
				if (h == l + 1)
				{
					asrt(m == l);
					return null;
				}
				else
				{
					l = m;
				}
			}
			else if (r == Direction1D.LowerDown)
			{
				if (h == l + 1)
				{
					asrt(m == l);
					return null;
				}
				
				h = m;
			}
			else //r == 0
			{
				if (r == null)
					throw new IllegalArgumentException();
				
				asrt(r == Direction1D.Zero);
				
				return m;
			}
		}
	}
}
