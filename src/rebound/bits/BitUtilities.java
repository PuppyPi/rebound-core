/*
 * Created on Mar 28, 2009
 * 	by the great Eclipse(c)
 */
package rebound.bits;

import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.exceptions.OverflowException;
import rebound.math.SmallIntegerMathUtilities;
import rebound.util.objectutil.JavaNamespace;

/**
 * This contains various bit-hacking algorithms.
 * The difference between this and MathUtilities is that MathUtilities houses more complex algorithms related more to the concept of the numbers the bits represent,
 * and this houses simple algorithms related more to the binary representation of the primitives.
 * @author RProgrammer
 */
public class BitUtilities
implements JavaNamespace
{
	public static int cmp(boolean a, boolean b)
	{
		/*
		 *  a b | o
		 *  -------
		 *  0 0 | 0
		 *  1 0 | 1
		 *  0 1 | -1
		 *  1 1 | 0
		 */
		
		if (a == b)
			return 0;
		else
			return a ? 1 : -1;  // == b ? -1 : 1;
	}
	
	
	
	
	
	
	
	public static boolean getBit(long value, long bitIndex)
	{
		if (bitIndex < 0 || bitIndex >= 64) throw new IllegalArgumentException();
		
		return (value & (1 << bitIndex)) != 0;
	}
	
	
	//Todo primxp
	public static long setBit(long value, long bitIndex, boolean newBitValue)
	{
		if (bitIndex < 0 || bitIndex >= 64) throw new IllegalArgumentException();
		
		if (newBitValue)
		{
			return value | (1 << bitIndex);
		}
		else
		{
			return value & ~(1 << bitIndex);
		}
	}
	
	
	
	
	/**
	 * <p>
	 * This operation will unset all but the highest set bit.
	 * If you need to find the highest zero bit, then simply invert the data before and after use.
	 * </p>
	 * In every case, the algorithm runs in log2(N) time where N is the word size.<br>
	 * <br>
	 * Examples:
	 * <table border="1">
	 * 	<tr><th>In</th><th>Out</th></tr>
	 * 	<tr><td>01011010</td><td>01000000</td></tr>
	 * 	<tr><td>10110100</td><td>10000000</td></tr>
	 * 	<tr><td>10101010</td><td>10000000</td></tr>
	 * 	<tr><td>01010101</td><td>01000000</td></tr>
	 * 	<tr><td>00000111</td><td>00000100</td></tr>
	 * 	<tr><td>00100101</td><td>00100000</td></tr>
	 * 
	 * 	<tr><td>00000000</td><td>00000000</td></tr>
	 * 	<tr><td>11111111</td><td>10000000</td></tr>
	 * </table>
	 */
	public static int getHighestOneBit(int v)
	{
		//Fill all bits below the highest
		v = v >>> 1 | v;
			v = v >>> 2 | v;
		v = v >>> 4 | v;
		v = v >>> 8 | v;
		v = v >>> 16 | v;
		
		//Kill all bits below the highest
		v = (v >>> 1) ^ v; //Shift right and XOR with previous
		
		return v;
	}
	
	
	/**
	 * Same as {@link #getHighestOneBit(int)}, but for 64 bit words.
	 */
	public static long getHighestOneBit(long v)
	{
		//Fill all bits below the highest
		v = v >>> 1 | v;
		v = v >>> 2 | v;
		v = v >>> 4 | v;
		v = v >>> 8 | v;
		v = v >>> 16 | v;
		v = v >>> 32 | v;
		
		//Kill all bits below the highest
		v = (v >>> 1) ^ v; //Shift right and XOR with previous
		
		return v;
	}
	
	
	
	
	/**
	 * <p>
	 * This operation will unset all but the lowest set bit.
	 * If you need to find the lowest zero bit, then simply invert the data before and after use.
	 * </p>
	 * In every case, the algorithm runs in log2(N) time where N is the word size.<br>
	 * <br>
	 * Examples:
	 * <table border="1">
	 * 	<tr><th>In</th><th>Out</th></tr>
	 * 	<tr><td>01011010</td><td>00000010</td></tr>
	 * 	<tr><td>10110100</td><td>00000100</td></tr>
	 * 	<tr><td>10101010</td><td>00000010</td></tr>
	 * 	<tr><td>01010101</td><td>00000001</td></tr>
	 * 	<tr><td>00000100</td><td>00000100</td></tr>
	 * 	<tr><td>00100000</td><td>00100000</td></tr>
	 * 
	 * 	<tr><td>00000000</td><td>00000000</td></tr>
	 * 	<tr><td>11111111</td><td>00000001</td></tr>
	 * </table>
	 */
	@ImplementationTransparency
	public static int _getLowestOneBit_a(int v)
	{
		//Fill all bits above the lowest
		v = v << 1 | v;
		v = v << 2 | v;
		v = v << 4 | v;
		v = v << 8 | v;
		v = v << 16 | v;
		
		//Kill all bits above the lowest
		v = v << 1 ^ v; //Shift left and XOR with previous
		
		// Note: there's an alternate (and vastly faster) way to do this IF the bits are contiguous (eg, 0b01111100): ~(redMask << 1) & redMask
		
		return v;
	}
	
	
	
	
	
	/**
	 * Same as {@link #_getLowestOneBit_a(int)}, but for 64 bit words.
	 */
	@ImplementationTransparency
	public static long _getLowestOneBit_a(long v)
	{
		//Fill all bits above the lowest
		v = v << 1 | v;
		v = v << 2 | v;
		v = v << 4 | v;
		v = v << 8 | v;
		v = v << 16 | v;
		v = v << 32 | v;
		
		//Kill all bits above the lowest
		v = v << 1 ^ v; //Shift left and XOR with previous
		
		return v;
	}
	
	
	
	
	public static long getLowestOneBit(long v)
	{
		/*
		 * Ok so look (note: i shamelessly stole the idea from Henry ._.  then figured out how it works XD )
		 * If you have bits
		 * And you invert them
		 * Then the lowest group of zeros is going to be ones
		 * And the lowest one is going to be a zero
		 * So if you increment it by 1
		 * ONLY THOSE LOWEST THINGS FLIP
		 * And that means only the lowest slot that was one in the original thing is True in both bitfields!
		 * (the lowest zeros-in-original are *different* (which would make them light up in xor), but they're zero/false again!
		 * So using AND instead of XOR means only that lowest thing lights up! :D )
		 * Wham! Instant lowest-bit, in CONSTANT TIME! :D
		 */
		return v & (~v + 1);
	}
	public static int getLowestOneBit(int v)
	{
		return v & (~v + 1);
	}
	public static short getLowestOneBit(short v)
	{
		return (short)(v & (~v + 1));
	}
	public static byte getLowestOneBit(byte v)
	{
		return (byte)(v & (~v + 1));
	}
	
	@ImplementationTransparency
	public static long _getHighestOneBit_a(long v)
	{
		return reverse(getLowestOneBit(reverse(v)));  //XD
	}
	
	
	
	
	
	
	/**
	 * <p>
	 * This will return the bit index (0 for the 1 bit, 3 for the 8 bit, 31 for the last bit (in 32-bit long words))
	 * with the one condition that only a single bit is set in the input word.  If more than one bit
	 * is set, then the result is undefined (though no exceptions will be thrown).
	 * </p>
	 * 
	 * In the worst case the algorithm is log2(N) time, where N is the number of bits in the integer (32, in this case).<br>
	 * Note: dcd(0) is defined as 0.
	 */
	public static int dcd32(int v)
	{
		return v == 0 ? 0 : Integer.numberOfTrailingZeros(v);
	}
	
	/**
	 * Same as {@link #dcd32(int)}, but for 64 bit words.
	 */
	public static int dcd64(long v)
	{
		return v == 0 ? 0 : Long.numberOfTrailingZeros(v);
	}
	
	
	
	
	
	@ImplementationTransparency
	public static int _dcd32_a(int v)
	{
		//Todo Find some way to remove branching
		
		//Find some way to make it 0 or 1 and multiply with the OR mask
		//x/(x+1) ?
		//x/(x-1) ?
		//(x-1)/(x+1) ?
		
		//ding ding ding we have a winner!
		
		// (I tested it against the entire 32 bit address space on 2011-09-12, so.. it works, ok!)
		
		// x may be 0, but never 0xFFFFFFFF
		
		int e = 0;
		if ((v & 0xAAAAAAAA) != 0) e |= 0x01;
		if ((v & 0xCCCCCCCC) != 0) e |= 0x02;
		if ((v & 0xF0F0F0F0) != 0) e |= 0x04;
		if ((v & 0xFF00FF00) != 0) e |= 0x08;
		if ((v & 0xFFFF0000) != 0) e |= 0x10;
		return e;
	}
	
	@ImplementationTransparency
	public static int _dcd64_a(long v)
	{
		int e = 0;
		if ((v & 0xAAAAAAAAAAAAAAAAl) != 0) e |= 0x01;
		if ((v & 0xCCCCCCCCCCCCCCCCl) != 0) e |= 0x02;
		if ((v & 0xF0F0F0F0F0F0F0F0l) != 0) e |= 0x04;
		if ((v & 0xFF00FF00FF00FF00l) != 0) e |= 0x08;
		if ((v & 0xFFFF0000FFFF0000l) != 0) e |= 0x10;
		if ((v & 0xFFFFFFFF00000000l) != 0) e |= 0x20;
		return e;
	}
	
	
	
	
	/**
	 * <p>
	 * This will return the bit index (0 for the 1 bit, 3 for the 8 bit, 31 for the last bit (in 32-bit long words))
	 * with the one condition that only a single bit is set in the input word.  If more than one bit
	 * is set, then the result is undefined (though no exceptions will be thrown).
	 * </p>
	 * 
	 * In the worst case the algorithm is log2(N) time, where N is the number of bits in the integer (32, in this case).<br>
	 * Note: dcd(0) is defined as 0.
	 */
	@ImplementationTransparency
	public static int _dcd32_b(int v)
	{
		int e = 0;
		
		int temp = 0;
		
		temp = (v & 0xAAAAAAAA) >>> 1;
		e |= 0x01 * (((temp-1)/(temp+1))+1);
		
		temp = (v & 0xCCCCCCCC) >>> 1;
		e |= 0x02 * (((temp-1)/(temp+1))+1);
		
		temp = (v & 0xF0F0F0F0) >>> 1;
		e |= 0x04 * (((temp-1)/(temp+1))+1);
		
		temp = (v & 0xFF00FF00) >>> 1;
		e |= 0x08 * (((temp-1)/(temp+1))+1);
		
		temp = (v & 0xFFFF0000) >>> 1;
		e |= 0x10 * (((temp-1)/(temp+1))+1);
		
		return e;
	}
	
	@ImplementationTransparency
	public static int _dcd64_b(long v)
	{
		int e = 0;
		
		long temp = 0;
		
		temp = (v & 0xAAAAAAAAAAAAAAAAl) >>> 1;
		e |= 0x01 * (((temp-1)/(temp+1))+1);
		
		temp = (v & 0xCCCCCCCCCCCCCCCCl) >>> 1;
		e |= 0x02 * (((temp-1)/(temp+1))+1);
		
		temp = (v & 0xF0F0F0F0F0F0F0F0l) >>> 1;
		e |= 0x04 * (((temp-1)/(temp+1))+1);
		
		temp = (v & 0xFF00FF00FF00FF00l) >>> 1;
		e |= 0x08 * (((temp-1)/(temp+1))+1);
		
		temp = (v & 0xFFFF0000FFFF0000l) >>> 1;
		e |= 0x10 * (((temp-1)/(temp+1))+1);
		
		temp = (v & 0xFFFFFFFF00000000l) >>> 1;
		e |= 0x20 * (((temp-1)/(temp+1))+1);
		
		return e;
	}
	
	
	public static int getNumberOfOneBits(int v)
	{
		//		if (true)
		// ._.  x'D
		return Integer.bitCount(v);
		
		//		//Todo make a better one
		//		int bitCount = 0;
		//		int x = 0;
		//		for (int i = 0; i < 32; i++)
		//		{
		//			x = v & 1 << i;
		//			x = x >>> i;
		//			bitCount += x;
		//		}
		//		return bitCount;
	}
	
	public static int getNumberOfZeroBits(int v)
	{
		return getNumberOfOneBits(~v);
		//return 32 - getNumberOfOneBits(v); //another way
	}
	
	
	public static int getNumberOfOneBits(long v)
	{
		//		if (true)
		// ._.  x'D
		return Long.bitCount(v);
		
		//		//Todo make a better one
		//		int bitCount = 0;
		//		long x = 0;
		//		for (int i = 0; i < 64; i++)
		//		{
		//			x = v & 1 << i;
		//			x = x >>> i;
		//			bitCount += x;
		//		}
		//		return bitCount;
	}
	
	public static int getNumberOfZeroBits(long v)
	{
		return getNumberOfOneBits(~v);
		//return 64 - getNumberOfOneBits(v); //another way
	}
	
	
	public static int getNumberOfContiguousBitsOfAGivenValue(long bitfield, int offset, int maxLength, boolean value)
	{
		if (offset > 64) throw new IllegalArgumentException();
		
		if (value == true)
			bitfield = ~bitfield;
		
		bitfield >>>= offset;
		
		int number = Long.numberOfTrailingZeros(bitfield);
		
		return Math.min(number, maxLength);
	}
	
	public static int getNumberOfContiguousBitsOfAGivenValue(int bitfield, int offset, int maxLength, boolean value)
	{
		if (offset > 32) throw new IllegalArgumentException();
		return getNumberOfContiguousBitsOfAGivenValue(bitfield, offset, Math.min(maxLength, 32 - offset), value);
	}
	
	public static int getNumberOfContiguousBitsOfAGivenValue(short bitfield, int offset, int maxLength, boolean value)
	{
		if (offset > 16) throw new IllegalArgumentException();
		return getNumberOfContiguousBitsOfAGivenValue(bitfield, offset, Math.min(maxLength, 16 - offset), value);
	}
	
	public static int getNumberOfContiguousBitsOfAGivenValue(byte bitfield, int offset, int maxLength, boolean value)
	{
		if (offset > 8) throw new IllegalArgumentException();
		return getNumberOfContiguousBitsOfAGivenValue(bitfield, offset, Math.min(maxLength, 8 - offset), value);
	}
	
	
	public static int getNumberOfContiguousBitsOfAGivenValue(long bitfield, int offset, boolean value)
	{
		if (offset > 64) throw new IllegalArgumentException();
		return getNumberOfContiguousBitsOfAGivenValue(bitfield, offset, 64 - offset, value);
	}
	
	public static int getNumberOfContiguousBitsOfAGivenValue(int bitfield, int offset, boolean value)
	{
		if (offset > 32) throw new IllegalArgumentException();
		return getNumberOfContiguousBitsOfAGivenValue(bitfield, offset, 32 - offset, value);
	}
	
	public static int getNumberOfContiguousBitsOfAGivenValue(short bitfield, int offset, boolean value)
	{
		if (offset > 16) throw new IllegalArgumentException();
		return getNumberOfContiguousBitsOfAGivenValue(bitfield, offset, 16 - offset, value);
	}
	
	public static int getNumberOfContiguousBitsOfAGivenValue(byte bitfield, int offset, boolean value)
	{
		if (offset > 8) throw new IllegalArgumentException();
		return getNumberOfContiguousBitsOfAGivenValue(bitfield, offset, 8 - offset, value);
	}
	
	
	
	/**
	 * Applies the count-based variadic gate: [2, inf)  (ie, n > 1)
	 * In other words, (for each bit independently), the output is 1 iff exactly more than one of the input bits is 1.
	 * (oh, and this runs in O(n) time, not O(n^2)  ;> )
	 */
	public static int variadicMoreThanOne(int... inputs)
	{
		int accumulator = 0;
		int currentResult = 0;
		
		
		int overlap = 0;
		for (int input : inputs)
		{
			overlap = accumulator & input;
			currentResult |= overlap;
			accumulator |= input;
		}
		
		
		return currentResult;
	}
	
	
	
	
	/*
	 * TODO generic algorithms for ordering x,y coordinates with a single index in a diagonal or aligned square spiral around the origin?
	 */
	
	//	/**
	//	 * This results in an integer code that causes values of the signed parameters which
	//	 * are close to zero (signed-wise) to be close to zero in the unsigned result.
	//	 *
	//	 * This is very useful for tile caches which need more common x,y coordinates to have
	//	 * lower code values.
	//	 */
	//	public static int interleaveSignedCluster(short a, short b)
	//	{
	//		//TODO
	//		short aReverse1sComplement = (short)(a < 0 ? a << 1 | 1 : a << 1);
	//
	//		return interleaveBits(aReverse1sComplement, bReverse1sComplement);
	//	}
	//
	//	public static int interleaveBits(short a, short b)
	//	{
	//		/*
	//			111 -> 101010
	//
	//			0	0
	//			1	01
	//			01	0001
	//			11	0101
	//			001	000001
	//			101	010001
	//			011	000101
	//			111	010101
	//
	//			0	0
	//			1	2
	//			2	8
	//			3	10
	//			4	32
	//			5	34
	//			6	40
	//			7	42
	//		 */
	//	}
	
	
	
	//TODO Count one bits
	
	//TODO Explode (few one bits become many)
	
	//TODO Reverse add
	
	//TODO Arithmetic (mul, div, mod, pow, not add)
	
	//TODO Rotate
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int getMask32(int numberOfOneBits)
	{
		if (numberOfOneBits > 32)
			throw new IllegalArgumentException("Requested length is greater than a Java int's length (32 bits)! :  "+numberOfOneBits);
		if (numberOfOneBits < 0)
			throw new IllegalArgumentException("Requested length is either negative if signed or *WAY* greater than a Java int's length (32 bits) if unsigned! :  "+numberOfOneBits);
		
		return (1 << numberOfOneBits) - 1;
	}
	
	public static long getMask64(long numberOfOneBits)
	{
		if (numberOfOneBits > 64)
			throw new IllegalArgumentException("Requested length is greater than a Java long's length (64 bits)! :  "+numberOfOneBits);
		if (numberOfOneBits < 0)
			throw new IllegalArgumentException("Requested length is either negative if signed or *WAY* greater than a Java long's length (64 bits) if unsigned! :  "+numberOfOneBits);
		
		return (1L << numberOfOneBits) - 1L;
	}
	
	
	
	
	
	//Reverse bits (eg, 110101000 becomes 000101011)
	public static long reverse64(long value, int bitOffset, int bitLength)
	{
		/*
		 *  b b b b b b b b b b b b b b b b b b b
		 *       |---------|
		 *                       |---------|
		 */
		
		final long mask = ((1 << bitLength) - 1) << bitOffset;
		final long rev = Long.reverse(value >>> bitOffset) << bitOffset;
		assert (rev & mask) == rev;
		return (value & ~mask) | rev;
	}
	
	public static int reverse32(int value, int bitOffset, int bitLength)
	{
		/*
		 *  b b b b b b b b b b b b b b b b b b b
		 *       |---------|
		 *                       |---------|
		 */
		
		final int mask = ((1 << bitLength) - 1) << bitOffset;
		final int rev = Integer.reverse(value >>> bitOffset) << bitOffset;
		assert (rev & mask) == rev;
		return (value & ~mask) | rev;
	}
	
	
	
	
	
	/* <<<
python

p(primxp.primxp("""
	public static _$$prim$$_ reverse(_$$prim$$_ value, int bitOffset, int bitLength)
	{
		return (_$$prim$$_)reverse((int)value, bitOffset, bitLength);
	}

""", prims=newSubdict(primxp.AllPrims, ["byte", "short", "char"])));
	 */
	
	public static byte reverse(byte value, int bitOffset, int bitLength)
	{
		return (byte)reverse32(value, bitOffset, bitLength);
	}
	
	
	public static char reverse(char value, int bitOffset, int bitLength)
	{
		return (char)reverse32(value, bitOffset, bitLength);
	}
	
	
	public static short reverse(short value, int bitOffset, int bitLength)
	{
		return (short)reverse32(value, bitOffset, bitLength);
	}
	
	
	// >>>
	
	
	
	
	
	
	
	
	/* <<<
python

p(primxp.primxp("""
	public static _$$prim$$_ reverse(_$$prim$$_ value, int bitLength)
	{
		return reverse(value, 0, bitLength);
	}

""", prims=primxp.IntPrims));
	 */
	
	public static byte reverse(byte value, int bitLength)
	{
		return reverse(value, 0, bitLength);
	}
	
	
	public static char reverse(char value, int bitLength)
	{
		return reverse(value, 0, bitLength);
	}
	
	
	public static short reverse(short value, int bitLength)
	{
		return reverse(value, 0, bitLength);
	}
	
	
	public static int reverse(int value, int bitLength)
	{
		return reverse32(value, 0, bitLength);
	}
	
	
	public static long reverse(long value, int bitLength)
	{
		return reverse64(value, 0, bitLength);
	}
	
	
	// >>>
	
	
	
	
	public static byte reverse(byte value)
	{
		return reverse(value, 0, 8);
	}
	
	public static short reverse(short value)
	{
		return reverse(value, 0, 16);
	}
	
	public static char reverse(char value)
	{
		return reverse(value, 0, 16);
	}
	
	
	
	
	
	/*
	 * Urls :D
		http://aggregate.org/MAGIC/
		https://medium.com/square-corner-blog/reversing-bits-in-c-48a772dc02d7
		http://graphics.stanford.edu/~seander/bithacks.html
		https://codegolf.stackexchange.com/questions/36213/reverse-bit-order-of-32-bit-integers
		https://stackoverflow.com/a/23540392
		https://stackoverflow.com/a/32090693
		https://stackoverflow.com/a/32093050
		https://stackoverflow.com/a/32900487
		https://stackoverflow.com/a/746203
		https://stackoverflow.com/questions/746171/most-efficient-algorithm-for-bit-reversal-from-msb-lsb-to-lsb-msb-in-c
		https://www.dsprelated.com/showthread/comp.dsp/131817-1.php
		https://www.quora.com/How-do-I-reverse-the-order-of-BITS-in-assembly-language
		http://www.hackersdelight.org/revisions.pdf
	 */
	
	
	public static int reverse(int value)
	{
		//Note that this is not a single instruction on x86 but it is on ARM!
		return Integer.reverse(value);  //XD
	}
	
	public static long reverse(long value)
	{
		//Note that this is not a single instruction on x86 but it is on ARM!  (at least for 32bits, presumably for the 64bit version of ARM as well ^^' )
		return Long.reverse(value);  //XD
	}
	
	
	
	@ImplementationTransparency
	public static int _reverse_arbitraryBitlength(int x)
	{
		return reverse32(x, 0, 32);
	}
	
	@ImplementationTransparency
	public static long _reverse_arbitraryBitlength(long x)
	{
		return reverse64(x, 0, 64);
	}
	
	
	
	
	
	
	
	
	@ImplementationTransparency
	public static int _reverse_a(int x)
	{
		//This one (and versions of it) are everywhere; who knows who came up with it XD
		x = (x & 0x55555555) <<  1 | (x & 0xAAAAAAAA) >>>  1;
		x = (x & 0x33333333) <<  2 | (x & 0xCCCCCCCC) >>>  2;
		x = (x & 0x0F0F0F0F) <<  4 | (x & 0xF0F0F0F0) >>>  4;
		x = (x & 0x00FF00FF) <<  8 | (x & 0xFF00FF00) >>>  8;
		x = (x & 0x0000FFFF) << 16 | (x & 0xFFFF0000) >>> 16;
		return x;
	}
	
	
	@ImplementationTransparency
	public static long _reverse_a(long x)
	{
		//This one (and versions of it) are everywhere; who knows who came up with it XD
		x = (x & 0x5555555555555555l) <<  1 | (x & 0xAAAAAAAAAAAAAAAAl) >>>  1;
		x = (x & 0x3333333333333333l) <<  2 | (x & 0xCCCCCCCCCCCCCCCCl) >>>  2;
		x = (x & 0x0F0F0F0F0F0F0F0Fl) <<  4 | (x & 0xF0F0F0F0F0F0F0F0l) >>>  4;
		x = (x & 0x00FF00FF00FF00FFl) <<  8 | (x & 0xFF00FF00FF00FF00l) >>>  8;
		x = (x & 0x0000FFFF0000FFFFl) << 16 | (x & 0xFFFF0000FFFF0000l) >>> 16;
		x = (x & 0x00000000FFFFFFFFl) << 32 | (x & 0xFFFFFFFF00000000l) >>> 32;
		return x;
	}
	
	
	
	
	@ImplementationTransparency
	public static int _reverse_b(int x)
	{
		//I found this from: "Posted by steveu ●November 2, 2010" on "https://www.dsprelated.com/showthread/comp.dsp/131817-1.php"
		//I'm kind of embarrassed I didn't think of modifying the last line like that XD'
		//(I wonder if The Hacker's Delight is too :}  XD )
		x = (x & 0x55555555) <<  1 | (x & 0xAAAAAAAA) >>>  1;
		x = (x & 0x33333333) <<  2 | (x & 0xCCCCCCCC) >>>  2;
		x = (x & 0x0F0F0F0F) <<  4 | (x & 0xF0F0F0F0) >>>  4;
		x = (x & 0x00FF00FF) <<  8 | (x & 0xFF00FF00) >>>  8;
		x = x << 16 | x >>> 16;
		return x;
	}
	
	
	@ImplementationTransparency
	public static long _reverse_b(long x)
	{
		//I found this from: "Posted by steveu ●November 2, 2010" on "https://www.dsprelated.com/showthread/comp.dsp/131817-1.php"
		//I'm kind of embarrassed I didn't think of modifying the last line like that XD'
		//(I wonder if The Hacker's Delight is too :}  XD )
		x = (x & 0x5555555555555555l) <<  1 | (x & 0xAAAAAAAAAAAAAAAAl) >>>  1;
		x = (x & 0x3333333333333333l) <<  2 | (x & 0xCCCCCCCCCCCCCCCCl) >>>  2;
		x = (x & 0x0F0F0F0F0F0F0F0Fl) <<  4 | (x & 0xF0F0F0F0F0F0F0F0l) >>>  4;
		x = (x & 0x00FF00FF00FF00FFl) <<  8 | (x & 0xFF00FF00FF00FF00l) >>>  8;
		x = (x & 0x0000FFFF0000FFFFl) << 16 | (x & 0xFFFF0000FFFF0000l) >>> 16;
		x = x << 32 | x >>> 32;
		return x;
	}
	
	
	
	
	@ImplementationTransparency
	public static int _reverse_c(int i)
	{
		//The implementation in Java in my JRE at least, copied here in case JRE's differ in implementation (which they should be able to! XD )
		// HD, Figure 7-1
		i = (i & 0x55555555) << 1 | (i >>> 1) & 0x55555555;
		i = (i & 0x33333333) << 2 | (i >>> 2) & 0x33333333;
		i = (i & 0x0F0F0F0F) << 4 | (i >>> 4) & 0x0F0F0F0F;
		i = (i << 24) | ((i & 0xFF00) << 8) | ((i >>> 8) & 0xFF00) | (i >>> 24);
		return i;
	}
	
	@ImplementationTransparency
	public static long _reverse_c(long x)
	{
		//The implementation in Java in my JRE at least, copied here in case JRE's differ in implementation (which they should be able to! XD )
		// HD, Figure 7-1
		x = (x & 0x5555555555555555l) << 1 | (x >>> 1) & 0x5555555555555555l;
		x = (x & 0x3333333333333333l) << 2 | (x >>> 2) & 0x3333333333333333l;
		x = (x & 0x0F0F0F0F0F0F0F0Fl) << 4 | (x >>> 4) & 0x0F0F0F0F0F0F0F0Fl;
		x = (x & 0x00FF00FF00FF00FFl) << 8 | (x >>> 8) & 0x00FF00FF00FF00FFl;
		x = (x << 48) | ((x & 0xFFFF0000l) << 16) | ((x >>> 16) & 0xFFFF0000l) | (x >>> 48);
		return x;
	}
	
	
	
	
	@ImplementationTransparency
	public static int _reverse_d(int x)
	{
		//I probably subconsciously noticed this "And if you're doing this in x86 assembler, the last two lines can be replaced with a bswap." from "Posted by robe...@yahoo.com ●November 2, 2010" on https://www.dsprelated.com/showthread/comp.dsp/131817-1.php  XD'
		x = (x & 0x55555555) << 1 | (x >>> 1) & 0x55555555;
		x = (x & 0x33333333) << 2 | (x >>> 2) & 0x33333333;
		x = (x & 0x0F0F0F0F) << 4 | (x >>> 4) & 0x0F0F0F0F;
		x = Integer.reverseBytes(x);  //This *is* a single instruction on x86! :D   (BSWAP)
		return x;
	}
	
	@ImplementationTransparency
	public static long _reverse_d(long x)
	{
		//I probably subconsciously noticed this "And if you're doing this in x86 assembler, the last two lines can be replaced with a bswap." from "Posted by robe...@yahoo.com ●November 2, 2010" on https://www.dsprelated.com/showthread/comp.dsp/131817-1.php  XD'
		x = (x & 0x5555555555555555l) << 1 | (x >>> 1) & 0x5555555555555555l;
		x = (x & 0x3333333333333333l) << 2 | (x >>> 2) & 0x3333333333333333l;
		x = (x & 0x0F0F0F0F0F0F0F0Fl) << 4 | (x >>> 4) & 0x0F0F0F0F0F0F0F0Fl;
		x = Long.reverseBytes(x);  //This *is* a single instruction on x86! :D   (BSWAP)
		return x;
	}
	
	
	
	
	@ImplementationTransparency
	public static int _reverse_e(int x)
	{
		//From "Posted by robe...@yahoo.com ●November 2, 2010" on https://www.dsprelated.com/showthread/comp.dsp/131817-1.php
		
		/*
			; input/output in eax
			
			mov ebx,eax
			and eax,0xaaaaaaaa
			ror eax,2
			and ebx,0x55555555
			or eax,ebx
			
			mov ebx,eax
			and eax,0x66666666
			ror eax,4
			and ebx,0x99999999
			or eax,ebx
			
			mov ebx,eax
			and eax,0x1e1e1e1e
			ror eax,8
			and ebx,0xe1e1e1e1
			or eax,ebx
			
			rol eax,7
			bswap eax
		 */
		
		
		int eax = x;
		int ebx;
		
		ebx = eax;
		eax &= 0xAAAAAAAA;
		eax = rotateUp(eax, 2);
		ebx &= 0x55555555;
		eax |= ebx;
		
		ebx = eax;
		eax &= 0x66666666;
		eax = rotateUp(eax, 4);
		ebx &= 0x99999999;
		eax |= ebx;
		
		ebx = eax;
		eax &= 0x1E1E1E1E;
		eax = rotateDown(eax, 8);
		ebx &= 0xE1E1E1E1;
		eax |= ebx;
		
		eax = rotateUp(eax, 7);
		eax = Integer.reverseBytes(eax);
		
		return eax;
	}
	
	//	@ImplementationTransparency
	//	public static long _reverse_e(long x)
	//	{
	//		todo XD''
	//	}
	
	
	
	
	@ImplementationTransparency
	public static int _reverse_f(int x)
	{
		//From: http://aggregate.org/MAGIC/#Bit%20Reversal
		int y = 0x55555555;
		x = (((x >>> 1) & y) | ((x & y) << 1));
		y = 0x33333333;
		x = (((x >>> 2) & y) | ((x & y) << 2));
		y = 0x0F0F0F0F;
		x = (((x >>> 4) & y) | ((x & y) << 4));
		y = 0x00FF00FF;
		x = (((x >>> 8) & y) | ((x & y) << 8));
		return (x >>> 16) | (x << 16);
	}
	
	@ImplementationTransparency
	public static long _reverse_f(long x)
	{
		//Adapted from: http://aggregate.org/MAGIC/#Bit%20Reversal
		long y = 0x5555555555555555l;
		x = (((x >>> 1) & y) | ((x & y) << 1));
		y = 0x3333333333333333l;
		x = (((x >>> 2) & y) | ((x & y) << 2));
		y = 0x0F0F0F0F0F0F0F0Fl;
		x = (((x >>> 4) & y) | ((x & y) << 4));
		y = 0x00FF00FF00FF00FFl;
		x = (((x >>> 8) & y) | ((x & y) << 8));
		y = 0x0000FFFF0000FFFFl;
		x = (((x >>> 16) & y) | ((x & y) << 16));
		return (x >>> 32) | (x << 32);
	}
	
	
	
	
	@ImplementationTransparency
	public static int _reverse_g(int x)
	{
		//Adapted from: http://aggregate.org/MAGIC/#Bit%20Reversal
		int y = 0x55555555;
		x = (((x >>> 1) & y) | ((x & y) << 1));
		y = 0x33333333;
		x = (((x >>> 2) & y) | ((x & y) << 2));
		y = 0x0F0F0F0F;
		x = (((x >>> 4) & y) | ((x & y) << 4));
		return Integer.reverseBytes(x);
	}
	
	@ImplementationTransparency
	public static long _reverse_g(long x)
	{
		//Adapted from: http://aggregate.org/MAGIC/#Bit%20Reversal
		long y = 0x5555555555555555l;
		x = (((x >>> 1) & y) | ((x & y) << 1));
		y = 0x3333333333333333l;
		x = (((x >>> 2) & y) | ((x & y) << 2));
		y = 0x0F0F0F0F0F0F0F0Fl;
		x = (((x >>> 4) & y) | ((x & y) << 4));
		return Long.reverseBytes(x);
	}
	
	
	
	public static byte _reverse_h(byte x)
	{
		//From: https://stackoverflow.com/a/746203
		//From: https://medium.com/square-corner-blog/reversing-bits-in-c-48a772dc02d7
		return (byte)( ((x * 0x0802 & 0x22110) | (x * 0x8020 & 0x88440)) * 0x10101 >>> 16 );
	}
	
	public static byte _reverse_i(byte x)
	{
		//From: https://stackoverflow.com/a/746203
		//From: https://medium.com/square-corner-blog/reversing-bits-in-c-48a772dc02d7
		return (byte)( (x * 0x0202020202l & 0x010884422010l) % 1023 );
	}
	
	public static byte _reverse_ii(byte x)
	{
		//From: https://medium.com/square-corner-blog/reversing-bits-in-c-48a772dc02d7
		return (byte)( ((x * 0x00_8020_0802l) & 0x08_8442_2110l) * 0x01_0101_0101l >>> 32 );
	}
	
	
	@ImplementationTransparency
	public static int _reverse_j(int x)
	{
		//From: https://stackoverflow.com/a/32093050
		int m;
		x = (x >>> 16) | (x << 16);                             // swap halfwords
		m = 0x00FF00FF; x = ((x >>> 8) & m) | ((x << 8) & ~m);  // swap bytes
		m = m^(m << 4); x = ((x >>> 4) & m) | ((x << 4) & ~m);  // swap nibbles
		m = m^(m << 2); x = ((x >>> 2) & m) | ((x << 2) & ~m);
		m = m^(m << 1); x = ((x >>> 1) & m) | ((x << 1) & ~m);
		return x;
	}
	
	@ImplementationTransparency
	public static int _reverse_k(int x)
	{
		//From: https://stackoverflow.com/a/32093050  (supposedly from Donald Knuth!)
		int t;
		x = (x << 15) | (x >>> 17);
		t = (x ^ (x >>> 10)) & 0x003F801F;
		x = (t + (t << 10)) ^ x;
		t = (x ^ (x >>>  4)) & 0x0E038421;
		x = (t + (t <<  4)) ^ x;
		t = (x ^ (x >>>  2)) & 0x22488842;
		x = (t + (t <<  2)) ^ x;
		return x;
	}
	
	@ImplementationTransparency
	public static int _reverse_l(int x)
	{
		//From: http://www.hackersdelight.org/revisions.pdf
		x = x | ((x & 0x000000FF) << 16);
		x = (x & 0xF0F0F0F0) | ((x & 0x0F0F0F0F) << 8);
		x = (x & 0xCCCCCCCC) | ((x & 0x33333333) << 4);
		x = (x & 0xAAAAAAAA) | ((x & 0x55555555) << 2);
		x = x << 1;
		return x;
	}
	
	@ImplementationTransparency
	public static int _reverse_m(int x)
	{
		//From: http://www.hackersdelight.org/revisions.pdf
		x = rotateUp(x & 0x00FF00FF, 16) | x & ~0x00FF00FF;
		x = rotateUp(x & 0x0F0F0F0F,  8) | x & ~0x0F0F0F0F;
		x = rotateUp(x & 0x33333333,  4) | x & ~0x33333333;
		x = rotateUp(x & 0x55555555,  2) | x & ~0x55555555;
		x = rotateUp(x, 1);
		return x;
	}
	
	@ImplementationTransparency
	public static int _reverse_n(int x)
	{
		//From: http://www.hackersdelight.org/revisions.pdf
		x = rotateUp(x, 16) & 0x00FF00FF | x & ~0x00FF00FF;
		x = rotateUp(x,  8) & 0x0F0F0F0F | x & ~0x0F0F0F0F;
		x = rotateUp(x,  4) & 0x33333333 | x & ~0x33333333;
		x = rotateUp(x,  2) & 0x55555555 | x & ~0x55555555;
		x = rotateUp(x, 1);
		return x;
	}
	
	@ImplementationTransparency
	public static int _reverse_o(int x)
	{
		//From: http://www.hackersdelight.org/revisions.pdf
		x = rotateUp(x, 16) & 0x00FF00FF | x & ~0x00FF00FF;
		x = rotateUp(x,  8) & 0x0F0F0F0F | x & ~0x0F0F0F0F;
		x = rotateUp(x,  4) & 0x33333333 | x & ~0x33333333;
		x = rotateUp(x,  2) & 0x55555555 | x & ~0x55555555;
		x = rotateUp(x, 1);
		return x;
	}
	
	@ImplementationTransparency
	public static int _reverse_p(int x)
	{
		//From: http://www.hackersdelight.org/revisions.pdf
		x = ((rotateUp(x, 16) ^ x) & 0x00FF00FF) ^ x;
		x = ((rotateUp(x,  8) ^ x) & 0x0F0F0F0F) ^ x;
		x = ((rotateUp(x,  4) ^ x) & 0x33333333) ^ x;
		x = ((rotateUp(x,  2) ^ x) & 0x55555555) ^ x;
		x = rotateUp(x, 1);
		return x;
	}
	
	@ImplementationTransparency
	public static int _reverse_q(int x)
	{
		//From: http://www.hackersdelight.org/revisions.pdf
		int t;
		t = x & 0x00FF00FF; x = rotateUp(t, 16) | t ^ x;
		t = x & 0x0F0F0F0F; x = rotateUp(t,  8) | t ^ x;
		t = x & 0x33333333; x = rotateUp(t,  4) | t ^ x;
		t = x & 0x55555555; x = rotateUp(t,  2) | t ^ x;
		x = rotateUp(x, 1);
		return x;
	}
	
	@ImplementationTransparency
	public static int _reverse_r(int x)
	{
		//From: http://www.hackersdelight.org/revisions.pdf
		x = (x & 0x000001FF) << 18 | (x & 0x0003FE00) | (x >>> 18) & 0x000001FF;
		x = (x & 0x001C0E07) << 6 | (x & 0x00E07038) | (x >>>  6) & 0x001C0E07;
		x = (x & 0x01249249) << 2 | (x & 0x02492492) | (x >>>  2) & 0x01249249;
		return x;
	}
	
	@ImplementationTransparency
	public static int _reverse_s(int x)
	{
		//From: http://www.hackersdelight.org/revisions.pdf
		int t;
		x = rotateUp(x, 15);  // Rotate left 15.
		t = (x ^ (x >>> 10)) & 0x003F801F;  x = (t | (t << 10)) ^ x;
		t = (x ^ (x >>>  4)) & 0x0E038421;  x = (t | (t <<  4)) ^ x;
		t = (x ^ (x >>>  2)) & 0x22488842;  x = (t | (t <<  2)) ^ x;
		return x;
	}
	
	@ImplementationTransparency
	public static long _reverse_t(long x)
	{
		//From: http://www.hackersdelight.org/revisions.pdf
		long t;
		
		x = (x << 32) | (x >>> 32); // Swap register halves.
		
		x = (x & 0x0001FFFF0001FFFFl) << 15 | // Rotate left
		(x & 0xFFFE0000FFFE0000l) >>> 17;  // 15.
		
		t = (x ^ (x >>> 10)) & 0x003F801F003F801Fl;
		x = (t | (t << 10)) ^ x;
		t = (x ^ (x >>> 4)) & 0x0E0384210E038421l;
		x = (t | (t << 4)) ^ x;
		t = (x ^ (x >>> 2)) & 0x2248884222488842l;
		x = (t | (t << 2)) ^ x;
		
		return x;
	}
	
	@ImplementationTransparency
	public static long _reverse_u(long x)
	{
		//Adapted from: http://www.hackersdelight.org/revisions.pdf
		long t;
		
		//x = rotateUp(x, 32);  //Swap register halves.
		//x = rotateUp(x, 15);
		
		x = rotateUp(x, 47);
		
		t = (x ^ (x >>> 10)) & 0x003F801F003F801Fl;
		x = (t | (t << 10)) ^ x;
		t = (x ^ (x >>> 4)) & 0x0E0384210E038421l;
		x = (t | (t << 4)) ^ x;
		t = (x ^ (x >>> 2)) & 0x2248884222488842l;
		x = (t | (t << 2)) ^ x;
		
		return x;
	}
	
	@ImplementationTransparency
	public static long _reverse_v(long x)
	{
		//From: http://www.hackersdelight.org/revisions.pdf
		long t;
		x = (x << 31) | (x >>> 33);   // I.e., shlr(x, 31).
		t = (x ^ (x >>> 20)) & 0x00000FFF800007FFl;
		x = (t | (t << 20)) ^ x;
		t = (x ^ (x >>>  8)) & 0x00F8000F80700807l;
		x = (t | (t <<  8)) ^ x;
		t = (x ^ (x >>>  4)) & 0x0808708080807008l;
		x = (t | (t <<  4)) ^ x;
		t = (x ^ (x >>>  2)) & 0x1111111111111111l;
		x = (t | (t <<  2)) ^ x;
		return x;
	}
	
	@ImplementationTransparency
	public static long _reverse_w(long x)
	{
		//Adapted from: http://www.hackersdelight.org/revisions.pdf
		long t;
		x = rotateUp(x, 31);
		t = (x ^ (x >>> 20)) & 0x00000FFF800007FFl;
		x = (t | (t << 20)) ^ x;
		t = (x ^ (x >>>  8)) & 0x00F8000F80700807l;
		x = (t | (t <<  8)) ^ x;
		t = (x ^ (x >>>  4)) & 0x0808708080807008l;
		x = (t | (t <<  4)) ^ x;
		t = (x ^ (x >>>  2)) & 0x1111111111111111l;
		x = (t | (t <<  2)) ^ x;
		return x;
	}
	
	
	
	@ImplementationTransparency
	public static byte _reverse_x(byte x)
	{
		//From: https://stackoverflow.com/a/23540392
		
		byte k = 0, rev = 0;
		
		byte n = x;
		
		while (n != 0)
		{
			k = (byte)( n & (~(n - 1)) );
			n &= (n - 1);
			rev |= (128 / k);
		}
		
		return rev;
	}
	
	
	
	@ImplementationTransparency
	public static int _reverse_y(int num)
	{
		//From: https://stackoverflow.com/a/32090693
		
		int num_reverse = 0;
		int size = 32 - 1;
		int i=0, j=0;
		
		for(i=0, j=size; i<=size & j>=0; i++, j--)
		{
			if (((num >>> i) & 1) != 0)
			{
				num_reverse = (num_reverse | (1<<j));
			}
		}
		
		return num_reverse;
	}
	
	
	
	@ImplementationTransparency
	public static int _reverse_naive(int x)
	{
		//From: https://stackoverflow.com/a/32900487
		
		int bits = 32;
		
		int r = 0;
		for (int i = 0; i < bits; i++)
		{
			int bit = (x & (1 << i)) >>> i;  //u1 XD
			r |= bit << (bits - i - 1);
		}
		return r;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int rotateUp(int value, int numberOfBits)
	{
		return Integer.rotateLeft(value, numberOfBits);
	}
	
	public static int rotateDown(int value, int numberOfBits)
	{
		return Integer.rotateRight(value, numberOfBits);
	}
	
	
	public static long rotateUp(long value, int numberOfBits)
	{
		return Long.rotateLeft(value, numberOfBits);
	}
	
	public static long rotateDown(long value, int numberOfBits)
	{
		return Long.rotateRight(value, numberOfBits);
	}
	
	
	
	
	
	
	
	
	@ImplementationTransparency
	public static int _rotateUp_a(int value, int numberOfBits)
	{
		final int x = value;
		final int n = numberOfBits;
		
		return (x << n) | (x >>> (32-n));
	}
	
	@ImplementationTransparency
	public static int _rotateDown_a(int value, int numberOfBits)
	{
		final int x = value;
		final int n = numberOfBits;
		
		return (x >> n) | (x << (32-n));
	}
	
	
	@ImplementationTransparency
	public static long _rotateUp_a(long value, int numberOfBits)
	{
		final long x = value;
		final int n = numberOfBits;
		
		return (x << n) | (x >>> (64-n));
	}
	
	@ImplementationTransparency
	public static long _rotateDown_a(long value, int numberOfBits)
	{
		final long x = value;
		final int n = numberOfBits;
		
		return (x >> n) | (x << (64-n));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static long signextend(long value, long bitLength)
	{
		if (bitLength > 64)
			throw new IllegalArgumentException("Requested length is greater than a Java long's length (64 bits)! :  "+bitLength);
		if (bitLength < 0)
			throw new IllegalArgumentException("Requested length is either negative if signed or greater than a Java long's length (64 bits) if unsigned! :  "+bitLength);
		
		
		if (bitLength == 64)
			return value;
		
		boolean signbit = (value & (1 << (bitLength-1))) != 0;
		
		if (!signbit)
			return value;
		else
			return value | (getMask64(64 - bitLength) << bitLength);  //a 'mask' is all 1's :>
	}
	
	
	
	
	public static byte insureFitsUnsigned(byte value, long bits) throws OverflowException
	{
		if (bits == 0)
			throw new OverflowException();
		
		if (dcd32(getHighestOneBit(Unsigned.upcast(value))) >= bits)
			throw new OverflowException(value+" does not fit in "+bits+" bits!");
		else
			return value;
	}
	
	public static short insureFitsUnsigned(short value, long bits) throws OverflowException
	{
		if (bits == 0)
			throw new OverflowException();
		
		if (dcd32(getHighestOneBit(Unsigned.upcast(value))) >= bits)
			throw new OverflowException(value+" does not fit in "+bits+" bits!");
		else
			return value;
	}
	
	public static int insureFitsUnsigned(int value, long bits) throws OverflowException
	{
		if (bits == 0)
			throw new OverflowException();
		
		if (dcd32(getHighestOneBit(value)) >= bits)
			throw new OverflowException(value+" does not fit in "+bits+" bits!");
		else
			return value;
	}
	
	public static long insureFitsUnsigned(long value, long bits) throws OverflowException
	{
		if (bits == 0)
			throw new OverflowException();
		
		if (dcd64(getHighestOneBit(value)) >= bits)
			throw new OverflowException(value+" does not fit in "+bits+" bits!");
		else
			return value;
	}
	
	
	
	public static boolean isContiguousOnes(long value)
	{
		value >>>= dcd64(getLowestOneBit(value)); //move all the potential 1 bits to the start
		value += 1;
		return value == 0 || getNumberOfOneBits(value) == 1; //(if we added something, and it's now zero, the only logical possibiliy is that it was OVER NINE THOUSAND (or, you know, right over 18,446,744,073,709,551.614 thousand XD ) )
	}
	
	public static boolean isContiguousZeros(long value)
	{
		return isContiguousOnes(~value);
	}
	
	public static boolean isContiguousOnes(int value)
	{
		return isContiguousOnes(value & 0x00000000FFFFFFFFl);
	}
	public static boolean isContiguousOnes(short value)
	{
		return isContiguousOnes(value & 0x000000000000FFFFl);
	}
	public static boolean isContiguousOnes(byte value)
	{
		return isContiguousOnes(value & 0x00000000000000FFl);
	}
	
	public static boolean isContiguousZeros(int value)
	{
		return isContiguousZeros(value | 0xFFFFFFFF00000000l);
	}
	public static boolean isContiguousZeros(short value)
	{
		return isContiguousZeros(value | 0xFFFFFFFFFFFF0000l);
	}
	public static boolean isContiguousZeros(byte value)
	{
		return isContiguousZeros(value | 0xFFFFFFFFFFFFFF00l);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//// BITSTRING OPERATIONS! \o/ ////
	
	
	
	////<Simple function operations which can operate on the excess bits in primitive array fields without worrying about true bitlengths!
	
	/* <<<
python

rv = "";

simpleCommutativeBiOps = [("&", "and"), ("|", "or"), ("^", "xor")];

for op, name in simpleCommutativeBiOps:
	rv += """
/**
	 * Perform the operation out-of-place, with sourceA and sourceB as input, storing into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
 ⎋a/
public static void """+name+"""OP(@ReadonlyValue _$$prim$$_[] sourceA, @ReadonlyValue _$$prim$$_[] sourceB, @WritableValue _$$prim$$_[] dest)
{
	int length;
	{
		int lSA = sourceA.length;
		int lSB = sourceB.length;
		int lD = dest.length;
		length = lSB < lSA ? lSB : lSA;
		length = lD < length ? lD : length;
	}
	
	for (int i = 0; i < length; i++)
		dest[i] = (_$$prim$$_)(sourceA[i] """+op+""" sourceB[i]);
}

/**
	 * Perform the operation in-place, with source and dest as input, storing back into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
⎋a/
public static void """+name+"""IP(@ReadonlyValue _$$prim$$_[] source, @WritableValue _$$prim$$_[] dest)
{
	int length;
	{
		int lS = source.length;
		int lD = dest.length;
		length = lD < lS ? lD : lS;
	}
	
	for (int i = 0; i < length; i++)
		dest[i] = (_$$prim$$_)(source[i] """+op+""" dest[i]);
}


""";

rv += """
/**
	 * Perform the unary operation in-place, dest as the sole input and sole output ^_^
⎋a/
public static void notIP(@WritableValue _$$prim$$_[] x)
{
	int length = x.length;
	
	for (int i = 0; i < length; i++)
		x[i] = (_$$prim$$_)(_$$primnot$$_x[i]);
}

""";




p(primxp.primxp(rv, prims=primxp.BitPrims));
	 */
	
	/**
	 * Perform the operation out-of-place, with sourceA and sourceB as input, storing into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void andOP(@ReadonlyValue boolean[] sourceA, @ReadonlyValue boolean[] sourceB, @WritableValue boolean[] dest)
	{
		int length;
		{
			int lSA = sourceA.length;
			int lSB = sourceB.length;
			int lD = dest.length;
			length = lSB < lSA ? lSB : lSA;
			length = lD < length ? lD : length;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = sourceA[i] & sourceB[i];
	}
	
	/**
	 * Perform the operation in-place, with source and dest as input, storing back into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void andIP(@ReadonlyValue boolean[] source, @WritableValue boolean[] dest)
	{
		int length;
		{
			int lS = source.length;
			int lD = dest.length;
			length = lD < lS ? lD : lS;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = source[i] & dest[i];
	}
	
	
	
	/**
	 * Perform the operation out-of-place, with sourceA and sourceB as input, storing into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void orOP(@ReadonlyValue boolean[] sourceA, @ReadonlyValue boolean[] sourceB, @WritableValue boolean[] dest)
	{
		int length;
		{
			int lSA = sourceA.length;
			int lSB = sourceB.length;
			int lD = dest.length;
			length = lSB < lSA ? lSB : lSA;
			length = lD < length ? lD : length;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = sourceA[i] | sourceB[i];
	}
	
	/**
	 * Perform the operation in-place, with source and dest as input, storing back into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void orIP(@ReadonlyValue boolean[] source, @WritableValue boolean[] dest)
	{
		int length;
		{
			int lS = source.length;
			int lD = dest.length;
			length = lD < lS ? lD : lS;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = source[i] | dest[i];
	}
	
	
	
	/**
	 * Perform the operation out-of-place, with sourceA and sourceB as input, storing into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void xorOP(@ReadonlyValue boolean[] sourceA, @ReadonlyValue boolean[] sourceB, @WritableValue boolean[] dest)
	{
		int length;
		{
			int lSA = sourceA.length;
			int lSB = sourceB.length;
			int lD = dest.length;
			length = lSB < lSA ? lSB : lSA;
			length = lD < length ? lD : length;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = sourceA[i] ^ sourceB[i];
	}
	
	/**
	 * Perform the operation in-place, with source and dest as input, storing back into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void xorIP(@ReadonlyValue boolean[] source, @WritableValue boolean[] dest)
	{
		int length;
		{
			int lS = source.length;
			int lD = dest.length;
			length = lD < lS ? lD : lS;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = source[i] ^ dest[i];
	}
	
	
	
	/**
	 * Perform the unary operation in-place, dest as the sole input and sole output ^_^
	 */
	public static void notIP(@WritableValue boolean[] x)
	{
		int length = x.length;
		
		for (int i = 0; i < length; i++)
			x[i] = (!x[i]);
	}
	
	
	/**
	 * Perform the operation out-of-place, with sourceA and sourceB as input, storing into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void andOP(@ReadonlyValue byte[] sourceA, @ReadonlyValue byte[] sourceB, @WritableValue byte[] dest)
	{
		int length;
		{
			int lSA = sourceA.length;
			int lSB = sourceB.length;
			int lD = dest.length;
			length = lSB < lSA ? lSB : lSA;
			length = lD < length ? lD : length;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = (byte)(sourceA[i] & sourceB[i]);
	}
	
	/**
	 * Perform the operation in-place, with source and dest as input, storing back into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void andIP(@ReadonlyValue byte[] source, @WritableValue byte[] dest)
	{
		int length;
		{
			int lS = source.length;
			int lD = dest.length;
			length = lD < lS ? lD : lS;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = (byte)(source[i] & dest[i]);
	}
	
	
	
	/**
	 * Perform the operation out-of-place, with sourceA and sourceB as input, storing into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void orOP(@ReadonlyValue byte[] sourceA, @ReadonlyValue byte[] sourceB, @WritableValue byte[] dest)
	{
		int length;
		{
			int lSA = sourceA.length;
			int lSB = sourceB.length;
			int lD = dest.length;
			length = lSB < lSA ? lSB : lSA;
			length = lD < length ? lD : length;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = (byte)(sourceA[i] | sourceB[i]);
	}
	
	/**
	 * Perform the operation in-place, with source and dest as input, storing back into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void orIP(@ReadonlyValue byte[] source, @WritableValue byte[] dest)
	{
		int length;
		{
			int lS = source.length;
			int lD = dest.length;
			length = lD < lS ? lD : lS;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = (byte)(source[i] | dest[i]);
	}
	
	
	
	/**
	 * Perform the operation out-of-place, with sourceA and sourceB as input, storing into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void xorOP(@ReadonlyValue byte[] sourceA, @ReadonlyValue byte[] sourceB, @WritableValue byte[] dest)
	{
		int length;
		{
			int lSA = sourceA.length;
			int lSB = sourceB.length;
			int lD = dest.length;
			length = lSB < lSA ? lSB : lSA;
			length = lD < length ? lD : length;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = (byte)(sourceA[i] ^ sourceB[i]);
	}
	
	/**
	 * Perform the operation in-place, with source and dest as input, storing back into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void xorIP(@ReadonlyValue byte[] source, @WritableValue byte[] dest)
	{
		int length;
		{
			int lS = source.length;
			int lD = dest.length;
			length = lD < lS ? lD : lS;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = (byte)(source[i] ^ dest[i]);
	}
	
	
	
	/**
	 * Perform the unary operation in-place, dest as the sole input and sole output ^_^
	 */
	public static void notIP(@WritableValue byte[] x)
	{
		int length = x.length;
		
		for (int i = 0; i < length; i++)
			x[i] = (byte)(~x[i]);
	}
	
	
	/**
	 * Perform the operation out-of-place, with sourceA and sourceB as input, storing into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void andOP(@ReadonlyValue char[] sourceA, @ReadonlyValue char[] sourceB, @WritableValue char[] dest)
	{
		int length;
		{
			int lSA = sourceA.length;
			int lSB = sourceB.length;
			int lD = dest.length;
			length = lSB < lSA ? lSB : lSA;
			length = lD < length ? lD : length;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = (char)(sourceA[i] & sourceB[i]);
	}
	
	/**
	 * Perform the operation in-place, with source and dest as input, storing back into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void andIP(@ReadonlyValue char[] source, @WritableValue char[] dest)
	{
		int length;
		{
			int lS = source.length;
			int lD = dest.length;
			length = lD < lS ? lD : lS;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = (char)(source[i] & dest[i]);
	}
	
	
	
	/**
	 * Perform the operation out-of-place, with sourceA and sourceB as input, storing into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void orOP(@ReadonlyValue char[] sourceA, @ReadonlyValue char[] sourceB, @WritableValue char[] dest)
	{
		int length;
		{
			int lSA = sourceA.length;
			int lSB = sourceB.length;
			int lD = dest.length;
			length = lSB < lSA ? lSB : lSA;
			length = lD < length ? lD : length;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = (char)(sourceA[i] | sourceB[i]);
	}
	
	/**
	 * Perform the operation in-place, with source and dest as input, storing back into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void orIP(@ReadonlyValue char[] source, @WritableValue char[] dest)
	{
		int length;
		{
			int lS = source.length;
			int lD = dest.length;
			length = lD < lS ? lD : lS;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = (char)(source[i] | dest[i]);
	}
	
	
	
	/**
	 * Perform the operation out-of-place, with sourceA and sourceB as input, storing into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void xorOP(@ReadonlyValue char[] sourceA, @ReadonlyValue char[] sourceB, @WritableValue char[] dest)
	{
		int length;
		{
			int lSA = sourceA.length;
			int lSB = sourceB.length;
			int lD = dest.length;
			length = lSB < lSA ? lSB : lSA;
			length = lD < length ? lD : length;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = (char)(sourceA[i] ^ sourceB[i]);
	}
	
	/**
	 * Perform the operation in-place, with source and dest as input, storing back into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void xorIP(@ReadonlyValue char[] source, @WritableValue char[] dest)
	{
		int length;
		{
			int lS = source.length;
			int lD = dest.length;
			length = lD < lS ? lD : lS;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = (char)(source[i] ^ dest[i]);
	}
	
	
	
	/**
	 * Perform the unary operation in-place, dest as the sole input and sole output ^_^
	 */
	public static void notIP(@WritableValue char[] x)
	{
		int length = x.length;
		
		for (int i = 0; i < length; i++)
			x[i] = (char)(~x[i]);
	}
	
	
	/**
	 * Perform the operation out-of-place, with sourceA and sourceB as input, storing into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void andOP(@ReadonlyValue short[] sourceA, @ReadonlyValue short[] sourceB, @WritableValue short[] dest)
	{
		int length;
		{
			int lSA = sourceA.length;
			int lSB = sourceB.length;
			int lD = dest.length;
			length = lSB < lSA ? lSB : lSA;
			length = lD < length ? lD : length;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = (short)(sourceA[i] & sourceB[i]);
	}
	
	/**
	 * Perform the operation in-place, with source and dest as input, storing back into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void andIP(@ReadonlyValue short[] source, @WritableValue short[] dest)
	{
		int length;
		{
			int lS = source.length;
			int lD = dest.length;
			length = lD < lS ? lD : lS;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = (short)(source[i] & dest[i]);
	}
	
	
	
	/**
	 * Perform the operation out-of-place, with sourceA and sourceB as input, storing into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void orOP(@ReadonlyValue short[] sourceA, @ReadonlyValue short[] sourceB, @WritableValue short[] dest)
	{
		int length;
		{
			int lSA = sourceA.length;
			int lSB = sourceB.length;
			int lD = dest.length;
			length = lSB < lSA ? lSB : lSA;
			length = lD < length ? lD : length;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = (short)(sourceA[i] | sourceB[i]);
	}
	
	/**
	 * Perform the operation in-place, with source and dest as input, storing back into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void orIP(@ReadonlyValue short[] source, @WritableValue short[] dest)
	{
		int length;
		{
			int lS = source.length;
			int lD = dest.length;
			length = lD < lS ? lD : lS;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = (short)(source[i] | dest[i]);
	}
	
	
	
	/**
	 * Perform the operation out-of-place, with sourceA and sourceB as input, storing into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void xorOP(@ReadonlyValue short[] sourceA, @ReadonlyValue short[] sourceB, @WritableValue short[] dest)
	{
		int length;
		{
			int lSA = sourceA.length;
			int lSB = sourceB.length;
			int lD = dest.length;
			length = lSB < lSA ? lSB : lSA;
			length = lD < length ? lD : length;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = (short)(sourceA[i] ^ sourceB[i]);
	}
	
	/**
	 * Perform the operation in-place, with source and dest as input, storing back into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void xorIP(@ReadonlyValue short[] source, @WritableValue short[] dest)
	{
		int length;
		{
			int lS = source.length;
			int lD = dest.length;
			length = lD < lS ? lD : lS;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = (short)(source[i] ^ dest[i]);
	}
	
	
	
	/**
	 * Perform the unary operation in-place, dest as the sole input and sole output ^_^
	 */
	public static void notIP(@WritableValue short[] x)
	{
		int length = x.length;
		
		for (int i = 0; i < length; i++)
			x[i] = (short)(~x[i]);
	}
	
	
	/**
	 * Perform the operation out-of-place, with sourceA and sourceB as input, storing into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void andOP(@ReadonlyValue int[] sourceA, @ReadonlyValue int[] sourceB, @WritableValue int[] dest)
	{
		int length;
		{
			int lSA = sourceA.length;
			int lSB = sourceB.length;
			int lD = dest.length;
			length = lSB < lSA ? lSB : lSA;
			length = lD < length ? lD : length;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = sourceA[i] & sourceB[i];
	}
	
	/**
	 * Perform the operation in-place, with source and dest as input, storing back into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void andIP(@ReadonlyValue int[] source, @WritableValue int[] dest)
	{
		int length;
		{
			int lS = source.length;
			int lD = dest.length;
			length = lD < lS ? lD : lS;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = source[i] & dest[i];
	}
	
	
	
	/**
	 * Perform the operation out-of-place, with sourceA and sourceB as input, storing into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void orOP(@ReadonlyValue int[] sourceA, @ReadonlyValue int[] sourceB, @WritableValue int[] dest)
	{
		int length;
		{
			int lSA = sourceA.length;
			int lSB = sourceB.length;
			int lD = dest.length;
			length = lSB < lSA ? lSB : lSA;
			length = lD < length ? lD : length;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = sourceA[i] | sourceB[i];
	}
	
	/**
	 * Perform the operation in-place, with source and dest as input, storing back into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void orIP(@ReadonlyValue int[] source, @WritableValue int[] dest)
	{
		int length;
		{
			int lS = source.length;
			int lD = dest.length;
			length = lD < lS ? lD : lS;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = source[i] | dest[i];
	}
	
	
	
	/**
	 * Perform the operation out-of-place, with sourceA and sourceB as input, storing into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void xorOP(@ReadonlyValue int[] sourceA, @ReadonlyValue int[] sourceB, @WritableValue int[] dest)
	{
		int length;
		{
			int lSA = sourceA.length;
			int lSB = sourceB.length;
			int lD = dest.length;
			length = lSB < lSA ? lSB : lSA;
			length = lD < length ? lD : length;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = sourceA[i] ^ sourceB[i];
	}
	
	/**
	 * Perform the operation in-place, with source and dest as input, storing back into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void xorIP(@ReadonlyValue int[] source, @WritableValue int[] dest)
	{
		int length;
		{
			int lS = source.length;
			int lD = dest.length;
			length = lD < lS ? lD : lS;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = source[i] ^ dest[i];
	}
	
	
	
	/**
	 * Perform the unary operation in-place, dest as the sole input and sole output ^_^
	 */
	public static void notIP(@WritableValue int[] x)
	{
		int length = x.length;
		
		for (int i = 0; i < length; i++)
			x[i] = (~x[i]);
	}
	
	
	/**
	 * Perform the operation out-of-place, with sourceA and sourceB as input, storing into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void andOP(@ReadonlyValue long[] sourceA, @ReadonlyValue long[] sourceB, @WritableValue long[] dest)
	{
		int length;
		{
			int lSA = sourceA.length;
			int lSB = sourceB.length;
			int lD = dest.length;
			length = lSB < lSA ? lSB : lSA;
			length = lD < length ? lD : length;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = sourceA[i] & sourceB[i];
	}
	
	/**
	 * Perform the operation in-place, with source and dest as input, storing back into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void andIP(@ReadonlyValue long[] source, @WritableValue long[] dest)
	{
		int length;
		{
			int lS = source.length;
			int lD = dest.length;
			length = lD < lS ? lD : lS;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = source[i] & dest[i];
	}
	
	
	
	/**
	 * Perform the operation out-of-place, with sourceA and sourceB as input, storing into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void orOP(@ReadonlyValue long[] sourceA, @ReadonlyValue long[] sourceB, @WritableValue long[] dest)
	{
		int length;
		{
			int lSA = sourceA.length;
			int lSB = sourceB.length;
			int lD = dest.length;
			length = lSB < lSA ? lSB : lSA;
			length = lD < length ? lD : length;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = sourceA[i] | sourceB[i];
	}
	
	/**
	 * Perform the operation in-place, with source and dest as input, storing back into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void orIP(@ReadonlyValue long[] source, @WritableValue long[] dest)
	{
		int length;
		{
			int lS = source.length;
			int lD = dest.length;
			length = lD < lS ? lD : lS;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = source[i] | dest[i];
	}
	
	
	
	/**
	 * Perform the operation out-of-place, with sourceA and sourceB as input, storing into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void xorOP(@ReadonlyValue long[] sourceA, @ReadonlyValue long[] sourceB, @WritableValue long[] dest)
	{
		int length;
		{
			int lSA = sourceA.length;
			int lSB = sourceB.length;
			int lD = dest.length;
			length = lSB < lSA ? lSB : lSA;
			length = lD < length ? lD : length;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = sourceA[i] ^ sourceB[i];
	}
	
	/**
	 * Perform the operation in-place, with source and dest as input, storing back into dest ^_^
	 * Note that it is a commutative operation, so it doesn't matter the order source and dest are used!  ;)
	 */
	public static void xorIP(@ReadonlyValue long[] source, @WritableValue long[] dest)
	{
		int length;
		{
			int lS = source.length;
			int lD = dest.length;
			length = lD < lS ? lD : lS;
		}
		
		for (int i = 0; i < length; i++)
			dest[i] = source[i] ^ dest[i];
	}
	
	
	
	/**
	 * Perform the unary operation in-place, dest as the sole input and sole output ^_^
	 */
	public static void notIP(@WritableValue long[] x)
	{
		int length = x.length;
		
		for (int i = 0; i < length; i++)
			x[i] = (~x[i]);
	}
	
	
	// >>>
	////Simple function operations which can operate on the excess bits in primitive array fields without worrying about true bitlengths!>
	
	
	
	
	
	
	//<TODO Reversingssss!
	//Todo reverseOP ^^'
	
	
	/* <<<
python

p(primxp.primxp("""
	
	
	public static void reverseIP(final @WritableValue _$$prim$$_[] x, final int bitOffset, final int bitLength)
	{
		if (bitLength < 0)
			throw new IllegalArgumentException("bitOffset: "+bitOffset+", bitLength: "+bitLength+", capacity: "+(x.length * _$$primlogilen$$_));
		if (bitOffset < 0 || (bitOffset+bitLength > x.length * _$$primlogilen$$_))
			throw new IllegalArgumentException("bitOffset: "+bitOffset+", bitLength: "+bitLength+", capacity: "+(x.length * _$$primlogilen$$_));
		
		
		
		//Shortcuts! \o/
		
		if (bitLength == 0)
			return;
		
		
		if (bitLength == _$$primlogilen$$_ && (bitOffset % _$$primlogilen$$_) == 0)
		{
			final int elementIndex = bitOffset / _$$primlogilen$$_;
			
			x[elementIndex] = reverse(x[elementIndex]);
			
			return;
		}
		
		
		if (bitLength <= _$$primlogilen$$_ && (bitOffset / _$$primlogilen$$_ == ceilingDivision(bitOffset + bitLength, _$$primlogilen$$_)))
		{
			final int elementIndex = bitOffset / _$$primlogilen$$_;
			
			x[elementIndex] = reverse(x[elementIndex], bitOffset, bitLength);
			
			return;
		}
		
		
		
		
		//Main algorithm!
		{
			final boolean hasPartial0, hasPartial1;
			final int wholeElementOffset, wholeElementCount;
			{
				hasPartial0 = (bitOffset % _$$primlogilen$$_) != 0;
				hasPartial1 = ((bitOffset + bitLength) % _$$primlogilen$$_) != 0;
				final int wholeElementStart = (bitOffset / _$$primlogilen$$_) + (hasPartial0 ? 1 : 0);  //the + 1/0 makes it ceiling division, which is what we want ^_^
				final int wholeElementPastEnd = ((bitOffset + bitLength) / _$$primlogilen$$_);  //Java division is same as floor division for nonnegative integers, which is what we want ^_^
				
				if (wholeElementPastEnd < wholeElementStart)
				{
					wholeElementCount = 0;
					wholeElementOffset = 0;
				}
				else
				{
					wholeElementCount = wholeElementPastEnd - wholeElementStart;
					wholeElementOffset = wholeElementStart;
				}
			}
			
			
			//Deal with the whole-elements! :>
			{
				//Swap the whole-elements! ^w^
				{
					int other = 0;
					for (int i = 0; i < wholeElementCount; i++)
					{
						other = wholeElementCount - i - 1;
						
						x[i] ^= x[other];
						x[other] ^= x[i];
						x[i] ^= x[other];
					}
				}
				
				//Reverse the whole-elements! :D
				{
					for (int i = 0; i < wholeElementCount; i++)
						x[i] = reverse(x[i]);
				}
			}
			
			
			//Deal with the partials! :>''
			{
				if (hasPartial0 && hasPartial1)
				{
					//Remember, left/0 partial starts at a point and *up*, while
					//right/1 partial starts at 0 and *ends* at a point!
					final int partial0Index = bitOffset / _$$primlogilen$$_;
					final int partial1Index = (bitOffset + bitLength) / _$$primlogilen$$_ + 1;
					final int partial0StartInElement = bitOffset % _$$primlogilen$$_;
					final int partial1EndInElement = (bitOffset + bitLength) % _$$primlogilen$$_;
					final int partial0Bitlength = _$$primlogilen$$_ - partial0StartInElement;
					final int partial1Bitlength = partial1EndInElement;
					final _$$prim$$_ partial0Element = x[partial0Index];
					final _$$prim$$_ partial1Element = x[partial1Index];
					
					final _$$prim$$_ partial0ElementUntouchedMask = (_$$prim$$_)((1 << partial0StartInElement) - 1);
					final _$$prim$$_ partial1ElementMask = (_$$prim$$_)((1 << partial1EndInElement) - 1);
					final _$$prim$$_ partial0ElementMask = (_$$prim$$_)(~partial0ElementUntouchedMask);
					final _$$prim$$_ partial1ElementUntouchedMask = (_$$prim$$_)(~partial1ElementMask);
					
					final _$$prim$$_ partial0Bits = (_$$prim$$_)((partial0Element & partial0ElementMask) >>> partial0StartInElement);
					final _$$prim$$_ partial1Bits = (_$$prim$$_)(partial1Element & partial1ElementMask);
					
					//Reverse regions :>
					final _$$prim$$_ reversedPartial0Bits = reverse(partial0Bits, partial0Bitlength);
					final _$$prim$$_ reversedPartial1Bits = reverse(partial1Bits, partial1Bitlength);
					
					
					
					final _$$prim$$_ newPartial0Element;
					final _$$prim$$_ newPartial1Element;
					{
						if (partial0Bitlength == partial1Bitlength)
						{
							//They're the same length!!  No shifting needed!!  :D
							
							//Mask back in! ^^
							newPartial0Element = (_$$prim$$_)((partial0Element & partial0ElementUntouchedMask) | (reversedPartial1Bits << partial0StartInElement));
							newPartial1Element = (_$$prim$$_)((partial1Element & partial1ElementUntouchedMask) | (reversedPartial0Bits));
						}
						
						else
						{
							//Not the same bitlength..shifting needed :P
							
							int shiftAmount = partial1Bitlength - partial0Bitlength;
							
							assert shiftAmount != 0;
							
							if (shiftAmount > 0)  //twos-complement is nice here! ^^
							{
								//Shift everything up!
								
								assert partial0Bitlength < partial1Bitlength;
								final int smallerPartialBitlength = partial0Bitlength;
								final _$$prim$$_ commonDenominatorUntranslatedBitsMask = (_$$prim$$_)((1 << smallerPartialBitlength) - 1);
								
								final _$$prim$$_ lowCarryMask = (_$$prim$$_)((1 << shiftAmount) - 1);
								
								if (wholeElementCount != 0)
								{
									//Shift the whole-words that are in between!!
									_$$prim$$_ carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
									
									final int elementLengthMinusShiftAmount = _$$primlogilen$$_ - shiftAmount;
									final _$$prim$$_ highCarryMask = (_$$prim$$_)(lowCarryMask << elementLengthMinusShiftAmount);
									
									for (int i = wholeElementOffset; i < wholeElementCount; i++)
									{
										final _$$prim$$_ nextCarry = (_$$prim$$_)((x[i] & highCarryMask) >>> shiftAmount);
										x[i] = (_$$prim$$_)((x[i] << shiftAmount) | carry);
										carry = nextCarry;
									}
									
									//Carry it on up allll the way into the partial! ^v^
									final _$$prim$$_ partial1ElementWithCarriedBitsFromLastMiddleWholeElement = (_$$prim$$_)((partial1Element & (~lowCarryMask)) | carry);
									
									
									//Mask the small low0 one into the larger high1 one! ^^
									//newPartial1Element;
									{
										final _$$prim$$_ partial0BitsInPartial1Mask = (_$$prim$$_)(((1 << partial0Bitlength) - 1) << shiftAmount);
										newPartial1Element = (_$$prim$$_)((partial1ElementWithCarriedBitsFromLastMiddleWholeElement & (~partial0BitsInPartial1Mask)) | (reversedPartial0Bits << shiftAmount));
									}
									
									
									//Mask the larger high1 one into both the low0 one, AND a little into the next element!
									//If there is anything between the partials, then this spill-over will spill into the initial carry bits which we left as 0 above ^_~
									//newPartial0Element;
									{
										newPartial0Element = (_$$prim$$_)((partial0Element & partial0ElementUntouchedMask) | ((reversedPartial1Bits & commonDenominatorUntranslatedBitsMask) << partial0StartInElement));
										
										//x[wholeElementOffset] = (x[wholeElementOffset] & (~lowCarryMask)) | ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask);
										assert (reversedPartial1Bits >>> smallerPartialBitlength) == ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask); //all higher bits should be 0 anyways! :P
										
										x[wholeElementOffset] = (_$$prim$$_)((x[wholeElementOffset] & (~lowCarryMask)) | (reversedPartial1Bits >>> smallerPartialBitlength));
									}
								}
								else
								{
									//Mask the small low0 one into the larger high1 one! ^^
									final _$$prim$$_ newPartial1ElementWithoutSpill;
									{
										//Note: don't worry about the low shiftAmount bits, those will be overridden by the spill-over anyways XD'
										newPartial1ElementWithoutSpill = (_$$prim$$_)((partial1Element & partial1ElementUntouchedMask) | (reversedPartial0Bits << shiftAmount));
									}
									
									
									//Mask the larger high1 one into both the low0 one, AND a little into the next element!
									//If there is not anything between the partials, then this spill-over will spill into the other partial!
									//newPartial0Element;
									{
										newPartial0Element = (_$$prim$$_)((partial0Element & partial0ElementUntouchedMask) | ((reversedPartial1Bits & commonDenominatorUntranslatedBitsMask) << partial0StartInElement));
										
										//newPartial1Element = (newPartial1ElementWithoutSpill & (~lowCarryMask)) | ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask);
										assert (reversedPartial1Bits >>> smallerPartialBitlength) == ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask); //all higher bits should be 0 anyways, and Java shift is truncating not rotating! :P
										
										newPartial1Element = (_$$prim$$_)((newPartial1ElementWithoutSpill & (~lowCarryMask)) | (reversedPartial1Bits >>> smallerPartialBitlength));
									}
								}
							}
							else
							{
								//Shift everything down!
								assert shiftAmount < 0;
								final int absShiftAmount = -shiftAmount;
								
								assert partial0Bitlength > partial1Bitlength;
								final int smallerPartialBitlength = partial1Bitlength;
								final _$$prim$$_ commonDenominatorUntranslatedBitsMask = (_$$prim$$_)((1 << smallerPartialBitlength) - 1);
								
								final _$$prim$$_ lowCarryMask = (_$$prim$$_)((1 << absShiftAmount) - 1);
								final int elementLengthMinusShiftAmount = _$$primlogilen$$_ - absShiftAmount;
								final _$$prim$$_ highCarryMask = (_$$prim$$_)(lowCarryMask << elementLengthMinusShiftAmount);
								
								if (wholeElementCount != 0)
								{
									//Shift the whole-words that are in between!!
									_$$prim$$_ carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
									
									for (int i = wholeElementOffset+wholeElementCount-1; i >= 0; i--)
									{
										final _$$prim$$_ nextCarry = (_$$prim$$_)(x[i] & lowCarryMask);
										x[i] = (_$$prim$$_)((x[i] >>> absShiftAmount) | (carry << elementLengthMinusShiftAmount));
										carry = nextCarry;
									}
									
									//Carry it on down allll the way into the partial! ^v^
									final _$$prim$$_ partial0ElementWithCarriedBitsFromFirstMiddleWholeElement = (_$$prim$$_)((partial0Element & (~highCarryMask)) | (carry << elementLengthMinusShiftAmount));
									
									
									//Mask the small high1 one into the larger low0 one! ^^
									//newPartial0Element;
									{
										final _$$prim$$_ partial1BitsInPartial0Mask = (_$$prim$$_)(((1 << partial1Bitlength) - 1) << partial0StartInElement);
										newPartial0Element = (_$$prim$$_)((partial0ElementWithCarriedBitsFromFirstMiddleWholeElement & (~partial1BitsInPartial0Mask)) | (reversedPartial1Bits << partial0StartInElement));
									}
									
									
									//Mask the larger low0 one into both the high1 one, AND a little into the last whole element (the one adjacent to partial1)!
									//If there is anything between the partials, then this spill-over will spill into the initial carry bits which we left as 0 above ^_~
									//newPartial1Element;
									{
										newPartial1Element = (_$$prim$$_)((partial1Element & partial1ElementUntouchedMask) | ((reversedPartial0Bits & (commonDenominatorUntranslatedBitsMask << absShiftAmount)) >>> absShiftAmount));
										
										int lastElementIndex = wholeElementOffset + wholeElementCount - 1;
										x[lastElementIndex] = (_$$prim$$_)((x[lastElementIndex] & (~highCarryMask)) | ((reversedPartial0Bits & lowCarryMask) << elementLengthMinusShiftAmount));
									}
								}
								else
								{
									//If there is not anything between the partials, then this spill-over will spill into the other partial, taking the place of the carry bits if there were elements between the partials! ^^
									
									//Mask the small high1 one into the larger low0 one! ^^
									_$$prim$$_ newPartial0ElementWithoutSpill;
									{
										final _$$prim$$_ partial1BitsInPartial0Mask = (_$$prim$$_)(((1 << partial1Bitlength) - 1) << partial0StartInElement);
										newPartial0ElementWithoutSpill = (_$$prim$$_)((partial0Element & (~partial1BitsInPartial0Mask)) | (reversedPartial1Bits << partial0StartInElement));
									}
									
									
									//Mask the larger low0 one into both the high1 one, AND a little into the last whole element (the one adjacent to partial1)!
									//If there is not anything between the partials, then this spill-over will spill into the other partial!
									//newPartial1Element;
									{
										newPartial1Element = (_$$prim$$_)((partial1Element & partial1ElementUntouchedMask) | ((reversedPartial0Bits & (commonDenominatorUntranslatedBitsMask << absShiftAmount)) >>> absShiftAmount));
										
										newPartial0Element = (_$$prim$$_)((newPartial0ElementWithoutSpill & (~highCarryMask)) | ((reversedPartial0Bits & lowCarryMask) << elementLengthMinusShiftAmount));
									}
								}
							}
						}
					}
					
					
					x[partial0Index] = newPartial0Element;
					x[partial1Index] = newPartial1Element;
					
					//DONE! \o/ :D
				}
				
				
				
				
				else if (hasPartial0 && !hasPartial1)
				{
					//Remember, left/0 partial starts at a point and *up*, while
					//right/1 partial starts at 0 and *ends* at a point!
					final int partial0Index = bitOffset / _$$primlogilen$$_;
					final int partial0StartInElement = bitOffset % _$$primlogilen$$_;
					final int partial0Bitlength = _$$primlogilen$$_ - partial0StartInElement;
					//final int partial1Bitlength = 0;
					final _$$prim$$_ partial0Element = x[partial0Index];
					
					final _$$prim$$_ partial0ElementUntouchedMask = (_$$prim$$_)((1 << partial0StartInElement) - 1);
					final _$$prim$$_ partial0ElementMask = (_$$prim$$_)(~partial0ElementUntouchedMask);
					
					final _$$prim$$_ partial0Bits = (_$$prim$$_)((partial0Element & partial0ElementMask) >>> partial0StartInElement);
					
					//Reverse regions :>
					final _$$prim$$_ reversedPartial0Bits = reverse(partial0Bits, partial0Bitlength);
					
					
					
					final _$$prim$$_ newPartial0Element;
					{
						//Not the same bitlength..shifting needed :P
						//Shift everything down!
						int shiftAmount = 0 - partial0Bitlength;
						assert shiftAmount < 0;
						final int absShiftAmount = -shiftAmount;
						
						//final int smallerPartialBitlength = 0;
						
						final _$$prim$$_ lowCarryMask = (_$$prim$$_)((1 << absShiftAmount) - 1);
						final int elementLengthMinusShiftAmount = _$$primlogilen$$_ - absShiftAmount;
						final _$$prim$$_ highCarryMask = (_$$prim$$_)(lowCarryMask << elementLengthMinusShiftAmount);
						
						if (wholeElementCount != 0)
						{
							//Shift the whole-words that are in between!!
							_$$prim$$_ carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
							
							for (int i = wholeElementOffset+wholeElementCount-1; i >= 0; i--)
							{
								final _$$prim$$_ nextCarry = (_$$prim$$_)(x[i] & lowCarryMask);
								x[i] = (_$$prim$$_)((x[i] >>> absShiftAmount) | (carry << elementLengthMinusShiftAmount));
								carry = nextCarry;
							}
							
							//Carry it on down allll the way into the partial! ^v^
							assert partial0ElementUntouchedMask == (~highCarryMask);
							newPartial0Element = (_$$prim$$_)((partial0Element & partial0ElementUntouchedMask) | (carry << partial0StartInElement));
							
							
							//Mask the reversed partial into the the last whole element :>
							{
								int lastElementIndex = wholeElementOffset + wholeElementCount - 1;
								assert (reversedPartial0Bits & lowCarryMask) == reversedPartial0Bits;  //carry mask should completely cover it :>
								x[lastElementIndex] = (_$$prim$$_)((x[lastElementIndex] & (~highCarryMask)) | (reversedPartial0Bits << elementLengthMinusShiftAmount));
							}
						}
						else
						{
							throw new AssertionError("Should have been caught by single-element shortcuts ;_;");
						}
					}
					
					
					x[partial0Index] = newPartial0Element;
					
					//DONE! \o/ :D
				}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				else if (!hasPartial0 && hasPartial1)
				{
					//Remember, left/0 partial starts at a point and *up*, while
					//right/1 partial starts at 0 and *ends* at a point!
					final int partial1Index = (bitOffset + bitLength) / _$$primlogilen$$_ + 1;
					final int partial1EndInElement = (bitOffset + bitLength) % _$$primlogilen$$_;
					//final int partial0Bitlength = 0;
					final int partial1Bitlength = partial1EndInElement;
					final _$$prim$$_ partial1Element = x[partial1Index];
					
					final _$$prim$$_ partial1ElementMask = (_$$prim$$_)((1 << partial1EndInElement) - 1);
					final _$$prim$$_ partial1ElementUntouchedMask = (_$$prim$$_)(~partial1ElementMask);
					
					final _$$prim$$_ partial1Bits = (_$$prim$$_)((partial1Element & partial1ElementMask));
					
					//Reverse regions :>
					final _$$prim$$_ reversedPartial1Bits = reverse(partial1Bits, partial1Bitlength);
					
					
					
					final _$$prim$$_ newPartial1Element;
					{
						//Not the same bitlength..shifting needed :P
						//Shift everything up!
						int shiftAmount = partial1Bitlength - 0;
						assert shiftAmount > 0;
						
						final _$$prim$$_ lowCarryMask = (_$$prim$$_)((1 << shiftAmount) - 1);
						
						if (wholeElementCount != 0)
						{
							//Shift the whole-words that are in between!!
							_$$prim$$_ carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
							
							final int elementLengthMinusShiftAmount = _$$primlogilen$$_ - shiftAmount;
							final _$$prim$$_ highCarryMask = (_$$prim$$_)(lowCarryMask << elementLengthMinusShiftAmount);
							
							for (int i = wholeElementOffset; i < wholeElementCount; i++)
							{
								final _$$prim$$_ nextCarry = (_$$prim$$_)((x[i] & highCarryMask) >>> shiftAmount);
								x[i] = (_$$prim$$_)((x[i] << shiftAmount) | carry);
								carry = nextCarry;
							}
							
							//Carry it on up allll the way into the partial! ^v^
							assert (~lowCarryMask) == partial1ElementUntouchedMask;
							newPartial1Element = (_$$prim$$_)((partial1Element & partial1ElementUntouchedMask) | carry);
							
							
							//Mask the reversed partial into the the first whole element :>
							{
								assert (reversedPartial1Bits & lowCarryMask) == reversedPartial1Bits;  //carry mask should completely cover it :>
								x[wholeElementOffset] = (_$$prim$$_)((x[wholeElementOffset] & (~lowCarryMask)) | reversedPartial1Bits);
							}
						}
						else
						{
							throw new AssertionError("Should have been caught by single-element shortcuts ;_;");
						}
					}
					
					
					x[partial1Index] = newPartial1Element;
					
					//DONE! \o/ :D
				}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				else //if (!hasPartial0 && !hasPartial1)
				{
					//If no partials, then whole-elements were the only thing to reverse and swap..and we already did that!  SO WE'RE DONE JUST THAT FAST!  \o/  XD  :D
				}
			}
		}
	}

""", prims=primxp.IntPrims));
	 */
	
	
	
	public static void reverseIP(final @WritableValue byte[] x, final int bitOffset, final int bitLength)
	{
		if (bitLength < 0)
			throw new IllegalArgumentException("bitOffset: "+bitOffset+", bitLength: "+bitLength+", capacity: "+(x.length * 8));
		if (bitOffset < 0 || (bitOffset+bitLength > x.length * 8))
			throw new IllegalArgumentException("bitOffset: "+bitOffset+", bitLength: "+bitLength+", capacity: "+(x.length * 8));
		
		
		
		//Shortcuts! \o/
		
		if (bitLength == 0)
			return;
		
		
		if (bitLength == 8 && (bitOffset % 8) == 0)
		{
			final int elementIndex = bitOffset / 8;
			
			x[elementIndex] = reverse(x[elementIndex]);
			
			return;
		}
		
		
		if (bitLength <= 8 && (bitOffset / 8 == SmallIntegerMathUtilities.ceilingDivision(bitOffset + bitLength, 8)))
		{
			final int elementIndex = bitOffset / 8;
			
			x[elementIndex] = reverse(x[elementIndex], bitOffset, bitLength);
			
			return;
		}
		
		
		
		
		//Main algorithm!
		{
			final boolean hasPartial0, hasPartial1;
			final int wholeElementOffset, wholeElementCount;
			{
				hasPartial0 = (bitOffset % 8) != 0;
				hasPartial1 = ((bitOffset + bitLength) % 8) != 0;
				final int wholeElementStart = (bitOffset / 8) + (hasPartial0 ? 1 : 0);  //the + 1/0 makes it ceiling division, which is what we want ^_^
				final int wholeElementPastEnd = ((bitOffset + bitLength) / 8);  //Java division is same as floor division for nonnegative integers, which is what we want ^_^
				
				if (wholeElementPastEnd < wholeElementStart)
				{
					wholeElementCount = 0;
					wholeElementOffset = 0;
				}
				else
				{
					wholeElementCount = wholeElementPastEnd - wholeElementStart;
					wholeElementOffset = wholeElementStart;
				}
			}
			
			
			//Deal with the whole-elements! :>
			{
				//Swap the whole-elements! ^w^
				{
					int other = 0;
					for (int i = 0; i < wholeElementCount; i++)
					{
						other = wholeElementCount - i - 1;
						
						x[i] ^= x[other];
						x[other] ^= x[i];
						x[i] ^= x[other];
					}
				}
				
				//Reverse the whole-elements! :D
				{
					for (int i = 0; i < wholeElementCount; i++)
						x[i] = reverse(x[i]);
				}
			}
			
			
			//Deal with the partials! :>''
			{
				if (hasPartial0 && hasPartial1)
				{
					//Remember, left/0 partial starts at a point and *up*, while
					//right/1 partial starts at 0 and *ends* at a point!
					final int partial0Index = bitOffset / 8;
					final int partial1Index = (bitOffset + bitLength) / 8 + 1;
					final int partial0StartInElement = bitOffset % 8;
					final int partial1EndInElement = (bitOffset + bitLength) % 8;
					final int partial0Bitlength = 8 - partial0StartInElement;
					final int partial1Bitlength = partial1EndInElement;
					final byte partial0Element = x[partial0Index];
					final byte partial1Element = x[partial1Index];
					
					final byte partial0ElementUntouchedMask = (byte)((1 << partial0StartInElement) - 1);
					final byte partial1ElementMask = (byte)((1 << partial1EndInElement) - 1);
					final byte partial0ElementMask = (byte)(~partial0ElementUntouchedMask);
					final byte partial1ElementUntouchedMask = (byte)(~partial1ElementMask);
					
					final byte partial0Bits = (byte)((partial0Element & partial0ElementMask) >>> partial0StartInElement);
					final byte partial1Bits = (byte)(partial1Element & partial1ElementMask);
					
					//Reverse regions :>
					final byte reversedPartial0Bits = reverse(partial0Bits, partial0Bitlength);
					final byte reversedPartial1Bits = reverse(partial1Bits, partial1Bitlength);
					
					
					
					final byte newPartial0Element;
					final byte newPartial1Element;
					{
						if (partial0Bitlength == partial1Bitlength)
						{
							//They're the same length!!  No shifting needed!!  :D
							
							//Mask back in! ^^
							newPartial0Element = (byte)((partial0Element & partial0ElementUntouchedMask) | (reversedPartial1Bits << partial0StartInElement));
							newPartial1Element = (byte)((partial1Element & partial1ElementUntouchedMask) | (reversedPartial0Bits));
						}
						
						else
						{
							//Not the same bitlength..shifting needed :P
							
							int shiftAmount = partial1Bitlength - partial0Bitlength;
							
							assert shiftAmount != 0;
							
							if (shiftAmount > 0)  //twos-complement is nice here! ^^
							{
								//Shift everything up!
								
								assert partial0Bitlength < partial1Bitlength;
								final int smallerPartialBitlength = partial0Bitlength;
								final byte commonDenominatorUntranslatedBitsMask = (byte)((1 << smallerPartialBitlength) - 1);
								
								final byte lowCarryMask = (byte)((1 << shiftAmount) - 1);
								
								if (wholeElementCount != 0)
								{
									//Shift the whole-words that are in between!!
									byte carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
									
									final int elementLengthMinusShiftAmount = 8 - shiftAmount;
									final byte highCarryMask = (byte)(lowCarryMask << elementLengthMinusShiftAmount);
									
									for (int i = wholeElementOffset; i < wholeElementCount; i++)
									{
										final byte nextCarry = (byte)((x[i] & highCarryMask) >>> shiftAmount);
										x[i] = (byte)((x[i] << shiftAmount) | carry);
										carry = nextCarry;
									}
									
									//Carry it on up allll the way into the partial! ^v^
									final byte partial1ElementWithCarriedBitsFromLastMiddleWholeElement = (byte)((partial1Element & (~lowCarryMask)) | carry);
									
									
									//Mask the small low0 one into the larger high1 one! ^^
									//newPartial1Element;
									{
										final byte partial0BitsInPartial1Mask = (byte)(((1 << partial0Bitlength) - 1) << shiftAmount);
										newPartial1Element = (byte)((partial1ElementWithCarriedBitsFromLastMiddleWholeElement & (~partial0BitsInPartial1Mask)) | (reversedPartial0Bits << shiftAmount));
									}
									
									
									//Mask the larger high1 one into both the low0 one, AND a little into the next element!
									//If there is anything between the partials, then this spill-over will spill into the initial carry bits which we left as 0 above ^_~
									//newPartial0Element;
									{
										newPartial0Element = (byte)((partial0Element & partial0ElementUntouchedMask) | ((reversedPartial1Bits & commonDenominatorUntranslatedBitsMask) << partial0StartInElement));
										
										//x[wholeElementOffset] = (x[wholeElementOffset] & (~lowCarryMask)) | ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask);
										assert (reversedPartial1Bits >>> smallerPartialBitlength) == ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask); //all higher bits should be 0 anyways! :P
										
										x[wholeElementOffset] = (byte)((x[wholeElementOffset] & (~lowCarryMask)) | (reversedPartial1Bits >>> smallerPartialBitlength));
									}
								}
								else
								{
									//Mask the small low0 one into the larger high1 one! ^^
									final byte newPartial1ElementWithoutSpill;
									{
										//Note: don't worry about the low shiftAmount bits, those will be overridden by the spill-over anyways XD'
										newPartial1ElementWithoutSpill = (byte)((partial1Element & partial1ElementUntouchedMask) | (reversedPartial0Bits << shiftAmount));
									}
									
									
									//Mask the larger high1 one into both the low0 one, AND a little into the next element!
									//If there is not anything between the partials, then this spill-over will spill into the other partial!
									//newPartial0Element;
									{
										newPartial0Element = (byte)((partial0Element & partial0ElementUntouchedMask) | ((reversedPartial1Bits & commonDenominatorUntranslatedBitsMask) << partial0StartInElement));
										
										//newPartial1Element = (newPartial1ElementWithoutSpill & (~lowCarryMask)) | ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask);
										assert (reversedPartial1Bits >>> smallerPartialBitlength) == ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask); //all higher bits should be 0 anyways, and Java shift is truncating not rotating! :P
										
										newPartial1Element = (byte)((newPartial1ElementWithoutSpill & (~lowCarryMask)) | (reversedPartial1Bits >>> smallerPartialBitlength));
									}
								}
							}
							else
							{
								//Shift everything down!
								assert shiftAmount < 0;
								final int absShiftAmount = -shiftAmount;
								
								assert partial0Bitlength > partial1Bitlength;
								final int smallerPartialBitlength = partial1Bitlength;
								final byte commonDenominatorUntranslatedBitsMask = (byte)((1 << smallerPartialBitlength) - 1);
								
								final byte lowCarryMask = (byte)((1 << absShiftAmount) - 1);
								final int elementLengthMinusShiftAmount = 8 - absShiftAmount;
								final byte highCarryMask = (byte)(lowCarryMask << elementLengthMinusShiftAmount);
								
								if (wholeElementCount != 0)
								{
									//Shift the whole-words that are in between!!
									byte carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
									
									for (int i = wholeElementOffset+wholeElementCount-1; i >= 0; i--)
									{
										final byte nextCarry = (byte)(x[i] & lowCarryMask);
										x[i] = (byte)((x[i] >>> absShiftAmount) | (carry << elementLengthMinusShiftAmount));
										carry = nextCarry;
									}
									
									//Carry it on down allll the way into the partial! ^v^
									final byte partial0ElementWithCarriedBitsFromFirstMiddleWholeElement = (byte)((partial0Element & (~highCarryMask)) | (carry << elementLengthMinusShiftAmount));
									
									
									//Mask the small high1 one into the larger low0 one! ^^
									//newPartial0Element;
									{
										final byte partial1BitsInPartial0Mask = (byte)(((1 << partial1Bitlength) - 1) << partial0StartInElement);
										newPartial0Element = (byte)((partial0ElementWithCarriedBitsFromFirstMiddleWholeElement & (~partial1BitsInPartial0Mask)) | (reversedPartial1Bits << partial0StartInElement));
									}
									
									
									//Mask the larger low0 one into both the high1 one, AND a little into the last whole element (the one adjacent to partial1)!
									//If there is anything between the partials, then this spill-over will spill into the initial carry bits which we left as 0 above ^_~
									//newPartial1Element;
									{
										newPartial1Element = (byte)((partial1Element & partial1ElementUntouchedMask) | ((reversedPartial0Bits & (commonDenominatorUntranslatedBitsMask << absShiftAmount)) >>> absShiftAmount));
										
										int lastElementIndex = wholeElementOffset + wholeElementCount - 1;
										x[lastElementIndex] = (byte)((x[lastElementIndex] & (~highCarryMask)) | ((reversedPartial0Bits & lowCarryMask) << elementLengthMinusShiftAmount));
									}
								}
								else
								{
									//If there is not anything between the partials, then this spill-over will spill into the other partial, taking the place of the carry bits if there were elements between the partials! ^^
									
									//Mask the small high1 one into the larger low0 one! ^^
									byte newPartial0ElementWithoutSpill;
									{
										final byte partial1BitsInPartial0Mask = (byte)(((1 << partial1Bitlength) - 1) << partial0StartInElement);
										newPartial0ElementWithoutSpill = (byte)((partial0Element & (~partial1BitsInPartial0Mask)) | (reversedPartial1Bits << partial0StartInElement));
									}
									
									
									//Mask the larger low0 one into both the high1 one, AND a little into the last whole element (the one adjacent to partial1)!
									//If there is not anything between the partials, then this spill-over will spill into the other partial!
									//newPartial1Element;
									{
										newPartial1Element = (byte)((partial1Element & partial1ElementUntouchedMask) | ((reversedPartial0Bits & (commonDenominatorUntranslatedBitsMask << absShiftAmount)) >>> absShiftAmount));
										
										newPartial0Element = (byte)((newPartial0ElementWithoutSpill & (~highCarryMask)) | ((reversedPartial0Bits & lowCarryMask) << elementLengthMinusShiftAmount));
									}
								}
							}
						}
					}
					
					
					x[partial0Index] = newPartial0Element;
					x[partial1Index] = newPartial1Element;
					
					//DONE! \o/ :D
				}
				
				
				
				
				else if (hasPartial0 && !hasPartial1)
				{
					//Remember, left/0 partial starts at a point and *up*, while
					//right/1 partial starts at 0 and *ends* at a point!
					final int partial0Index = bitOffset / 8;
					final int partial0StartInElement = bitOffset % 8;
					final int partial0Bitlength = 8 - partial0StartInElement;
					//final int partial1Bitlength = 0;
					final byte partial0Element = x[partial0Index];
					
					final byte partial0ElementUntouchedMask = (byte)((1 << partial0StartInElement) - 1);
					final byte partial0ElementMask = (byte)(~partial0ElementUntouchedMask);
					
					final byte partial0Bits = (byte)((partial0Element & partial0ElementMask) >>> partial0StartInElement);
					
					//Reverse regions :>
					final byte reversedPartial0Bits = reverse(partial0Bits, partial0Bitlength);
					
					
					
					final byte newPartial0Element;
					{
						//Not the same bitlength..shifting needed :P
						//Shift everything down!
						int shiftAmount = 0 - partial0Bitlength;
						assert shiftAmount < 0;
						final int absShiftAmount = -shiftAmount;
						
						//final int smallerPartialBitlength = 0;
						
						final byte lowCarryMask = (byte)((1 << absShiftAmount) - 1);
						final int elementLengthMinusShiftAmount = 8 - absShiftAmount;
						final byte highCarryMask = (byte)(lowCarryMask << elementLengthMinusShiftAmount);
						
						if (wholeElementCount != 0)
						{
							//Shift the whole-words that are in between!!
							byte carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
							
							for (int i = wholeElementOffset+wholeElementCount-1; i >= 0; i--)
							{
								final byte nextCarry = (byte)(x[i] & lowCarryMask);
								x[i] = (byte)((x[i] >>> absShiftAmount) | (carry << elementLengthMinusShiftAmount));
								carry = nextCarry;
							}
							
							//Carry it on down allll the way into the partial! ^v^
							assert partial0ElementUntouchedMask == (~highCarryMask);
							newPartial0Element = (byte)((partial0Element & partial0ElementUntouchedMask) | (carry << partial0StartInElement));
							
							
							//Mask the reversed partial into the the last whole element :>
							{
								int lastElementIndex = wholeElementOffset + wholeElementCount - 1;
								assert (reversedPartial0Bits & lowCarryMask) == reversedPartial0Bits;  //carry mask should completely cover it :>
								x[lastElementIndex] = (byte)((x[lastElementIndex] & (~highCarryMask)) | (reversedPartial0Bits << elementLengthMinusShiftAmount));
							}
						}
						else
						{
							throw new AssertionError("Should have been caught by single-element shortcuts ;_;");
						}
					}
					
					
					x[partial0Index] = newPartial0Element;
					
					//DONE! \o/ :D
				}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				else if (!hasPartial0 && hasPartial1)
				{
					//Remember, left/0 partial starts at a point and *up*, while
					//right/1 partial starts at 0 and *ends* at a point!
					final int partial1Index = (bitOffset + bitLength) / 8 + 1;
					final int partial1EndInElement = (bitOffset + bitLength) % 8;
					//final int partial0Bitlength = 0;
					final int partial1Bitlength = partial1EndInElement;
					final byte partial1Element = x[partial1Index];
					
					final byte partial1ElementMask = (byte)((1 << partial1EndInElement) - 1);
					final byte partial1ElementUntouchedMask = (byte)(~partial1ElementMask);
					
					final byte partial1Bits = (byte)((partial1Element & partial1ElementMask));
					
					//Reverse regions :>
					final byte reversedPartial1Bits = reverse(partial1Bits, partial1Bitlength);
					
					
					
					final byte newPartial1Element;
					{
						//Not the same bitlength..shifting needed :P
						//Shift everything up!
						int shiftAmount = partial1Bitlength - 0;
						assert shiftAmount > 0;
						
						final byte lowCarryMask = (byte)((1 << shiftAmount) - 1);
						
						if (wholeElementCount != 0)
						{
							//Shift the whole-words that are in between!!
							byte carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
							
							final int elementLengthMinusShiftAmount = 8 - shiftAmount;
							final byte highCarryMask = (byte)(lowCarryMask << elementLengthMinusShiftAmount);
							
							for (int i = wholeElementOffset; i < wholeElementCount; i++)
							{
								final byte nextCarry = (byte)((x[i] & highCarryMask) >>> shiftAmount);
								x[i] = (byte)((x[i] << shiftAmount) | carry);
								carry = nextCarry;
							}
							
							//Carry it on up allll the way into the partial! ^v^
							assert (~lowCarryMask) == partial1ElementUntouchedMask;
							newPartial1Element = (byte)((partial1Element & partial1ElementUntouchedMask) | carry);
							
							
							//Mask the reversed partial into the the first whole element :>
							{
								assert (reversedPartial1Bits & lowCarryMask) == reversedPartial1Bits;  //carry mask should completely cover it :>
								x[wholeElementOffset] = (byte)((x[wholeElementOffset] & (~lowCarryMask)) | reversedPartial1Bits);
							}
						}
						else
						{
							throw new AssertionError("Should have been caught by single-element shortcuts ;_;");
						}
					}
					
					
					x[partial1Index] = newPartial1Element;
					
					//DONE! \o/ :D
				}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				else //if (!hasPartial0 && !hasPartial1)
				{
					//If no partials, then whole-elements were the only thing to reverse and swap..and we already did that!  SO WE'RE DONE JUST THAT FAST!  \o/  XD  :D
				}
			}
		}
	}
	
	
	
	
	public static void reverseIP(final @WritableValue char[] x, final int bitOffset, final int bitLength)
	{
		if (bitLength < 0)
			throw new IllegalArgumentException("bitOffset: "+bitOffset+", bitLength: "+bitLength+", capacity: "+(x.length * 16));
		if (bitOffset < 0 || (bitOffset+bitLength > x.length * 16))
			throw new IllegalArgumentException("bitOffset: "+bitOffset+", bitLength: "+bitLength+", capacity: "+(x.length * 16));
		
		
		
		//Shortcuts! \o/
		
		if (bitLength == 0)
			return;
		
		
		if (bitLength == 16 && (bitOffset % 16) == 0)
		{
			final int elementIndex = bitOffset / 16;
			
			x[elementIndex] = reverse(x[elementIndex]);
			
			return;
		}
		
		
		if (bitLength <= 16 && (bitOffset / 16 == SmallIntegerMathUtilities.ceilingDivision(bitOffset + bitLength, 16)))
		{
			final int elementIndex = bitOffset / 16;
			
			x[elementIndex] = reverse(x[elementIndex], bitOffset, bitLength);
			
			return;
		}
		
		
		
		
		//Main algorithm!
		{
			final boolean hasPartial0, hasPartial1;
			final int wholeElementOffset, wholeElementCount;
			{
				hasPartial0 = (bitOffset % 16) != 0;
				hasPartial1 = ((bitOffset + bitLength) % 16) != 0;
				final int wholeElementStart = (bitOffset / 16) + (hasPartial0 ? 1 : 0);  //the + 1/0 makes it ceiling division, which is what we want ^_^
				final int wholeElementPastEnd = ((bitOffset + bitLength) / 16);  //Java division is same as floor division for nonnegative integers, which is what we want ^_^
				
				if (wholeElementPastEnd < wholeElementStart)
				{
					wholeElementCount = 0;
					wholeElementOffset = 0;
				}
				else
				{
					wholeElementCount = wholeElementPastEnd - wholeElementStart;
					wholeElementOffset = wholeElementStart;
				}
			}
			
			
			//Deal with the whole-elements! :>
			{
				//Swap the whole-elements! ^w^
				{
					int other = 0;
					for (int i = 0; i < wholeElementCount; i++)
					{
						other = wholeElementCount - i - 1;
						
						x[i] ^= x[other];
						x[other] ^= x[i];
						x[i] ^= x[other];
					}
				}
				
				//Reverse the whole-elements! :D
				{
					for (int i = 0; i < wholeElementCount; i++)
						x[i] = reverse(x[i]);
				}
			}
			
			
			//Deal with the partials! :>''
			{
				if (hasPartial0 && hasPartial1)
				{
					//Remember, left/0 partial starts at a point and *up*, while
					//right/1 partial starts at 0 and *ends* at a point!
					final int partial0Index = bitOffset / 16;
					final int partial1Index = (bitOffset + bitLength) / 16 + 1;
					final int partial0StartInElement = bitOffset % 16;
					final int partial1EndInElement = (bitOffset + bitLength) % 16;
					final int partial0Bitlength = 16 - partial0StartInElement;
					final int partial1Bitlength = partial1EndInElement;
					final char partial0Element = x[partial0Index];
					final char partial1Element = x[partial1Index];
					
					final char partial0ElementUntouchedMask = (char)((1 << partial0StartInElement) - 1);
					final char partial1ElementMask = (char)((1 << partial1EndInElement) - 1);
					final char partial0ElementMask = (char)(~partial0ElementUntouchedMask);
					final char partial1ElementUntouchedMask = (char)(~partial1ElementMask);
					
					final char partial0Bits = (char)((partial0Element & partial0ElementMask) >>> partial0StartInElement);
					final char partial1Bits = (char)(partial1Element & partial1ElementMask);
					
					//Reverse regions :>
					final char reversedPartial0Bits = reverse(partial0Bits, partial0Bitlength);
					final char reversedPartial1Bits = reverse(partial1Bits, partial1Bitlength);
					
					
					
					final char newPartial0Element;
					final char newPartial1Element;
					{
						if (partial0Bitlength == partial1Bitlength)
						{
							//They're the same length!!  No shifting needed!!  :D
							
							//Mask back in! ^^
							newPartial0Element = (char)((partial0Element & partial0ElementUntouchedMask) | (reversedPartial1Bits << partial0StartInElement));
							newPartial1Element = (char)((partial1Element & partial1ElementUntouchedMask) | (reversedPartial0Bits));
						}
						
						else
						{
							//Not the same bitlength..shifting needed :P
							
							int shiftAmount = partial1Bitlength - partial0Bitlength;
							
							assert shiftAmount != 0;
							
							if (shiftAmount > 0)  //twos-complement is nice here! ^^
							{
								//Shift everything up!
								
								assert partial0Bitlength < partial1Bitlength;
								final int smallerPartialBitlength = partial0Bitlength;
								final char commonDenominatorUntranslatedBitsMask = (char)((1 << smallerPartialBitlength) - 1);
								
								final char lowCarryMask = (char)((1 << shiftAmount) - 1);
								
								if (wholeElementCount != 0)
								{
									//Shift the whole-words that are in between!!
									char carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
									
									final int elementLengthMinusShiftAmount = 16 - shiftAmount;
									final char highCarryMask = (char)(lowCarryMask << elementLengthMinusShiftAmount);
									
									for (int i = wholeElementOffset; i < wholeElementCount; i++)
									{
										final char nextCarry = (char)((x[i] & highCarryMask) >>> shiftAmount);
										x[i] = (char)((x[i] << shiftAmount) | carry);
										carry = nextCarry;
									}
									
									//Carry it on up allll the way into the partial! ^v^
									final char partial1ElementWithCarriedBitsFromLastMiddleWholeElement = (char)((partial1Element & (~lowCarryMask)) | carry);
									
									
									//Mask the small low0 one into the larger high1 one! ^^
									//newPartial1Element;
									{
										final char partial0BitsInPartial1Mask = (char)(((1 << partial0Bitlength) - 1) << shiftAmount);
										newPartial1Element = (char)((partial1ElementWithCarriedBitsFromLastMiddleWholeElement & (~partial0BitsInPartial1Mask)) | (reversedPartial0Bits << shiftAmount));
									}
									
									
									//Mask the larger high1 one into both the low0 one, AND a little into the next element!
									//If there is anything between the partials, then this spill-over will spill into the initial carry bits which we left as 0 above ^_~
									//newPartial0Element;
									{
										newPartial0Element = (char)((partial0Element & partial0ElementUntouchedMask) | ((reversedPartial1Bits & commonDenominatorUntranslatedBitsMask) << partial0StartInElement));
										
										//x[wholeElementOffset] = (x[wholeElementOffset] & (~lowCarryMask)) | ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask);
										assert (reversedPartial1Bits >>> smallerPartialBitlength) == ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask); //all higher bits should be 0 anyways! :P
										
										x[wholeElementOffset] = (char)((x[wholeElementOffset] & (~lowCarryMask)) | (reversedPartial1Bits >>> smallerPartialBitlength));
									}
								}
								else
								{
									//Mask the small low0 one into the larger high1 one! ^^
									final char newPartial1ElementWithoutSpill;
									{
										//Note: don't worry about the low shiftAmount bits, those will be overridden by the spill-over anyways XD'
										newPartial1ElementWithoutSpill = (char)((partial1Element & partial1ElementUntouchedMask) | (reversedPartial0Bits << shiftAmount));
									}
									
									
									//Mask the larger high1 one into both the low0 one, AND a little into the next element!
									//If there is not anything between the partials, then this spill-over will spill into the other partial!
									//newPartial0Element;
									{
										newPartial0Element = (char)((partial0Element & partial0ElementUntouchedMask) | ((reversedPartial1Bits & commonDenominatorUntranslatedBitsMask) << partial0StartInElement));
										
										//newPartial1Element = (newPartial1ElementWithoutSpill & (~lowCarryMask)) | ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask);
										assert (reversedPartial1Bits >>> smallerPartialBitlength) == ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask); //all higher bits should be 0 anyways, and Java shift is truncating not rotating! :P
										
										newPartial1Element = (char)((newPartial1ElementWithoutSpill & (~lowCarryMask)) | (reversedPartial1Bits >>> smallerPartialBitlength));
									}
								}
							}
							else
							{
								//Shift everything down!
								assert shiftAmount < 0;
								final int absShiftAmount = -shiftAmount;
								
								assert partial0Bitlength > partial1Bitlength;
								final int smallerPartialBitlength = partial1Bitlength;
								final char commonDenominatorUntranslatedBitsMask = (char)((1 << smallerPartialBitlength) - 1);
								
								final char lowCarryMask = (char)((1 << absShiftAmount) - 1);
								final int elementLengthMinusShiftAmount = 16 - absShiftAmount;
								final char highCarryMask = (char)(lowCarryMask << elementLengthMinusShiftAmount);
								
								if (wholeElementCount != 0)
								{
									//Shift the whole-words that are in between!!
									char carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
									
									for (int i = wholeElementOffset+wholeElementCount-1; i >= 0; i--)
									{
										final char nextCarry = (char)(x[i] & lowCarryMask);
										x[i] = (char)((x[i] >>> absShiftAmount) | (carry << elementLengthMinusShiftAmount));
										carry = nextCarry;
									}
									
									//Carry it on down allll the way into the partial! ^v^
									final char partial0ElementWithCarriedBitsFromFirstMiddleWholeElement = (char)((partial0Element & (~highCarryMask)) | (carry << elementLengthMinusShiftAmount));
									
									
									//Mask the small high1 one into the larger low0 one! ^^
									//newPartial0Element;
									{
										final char partial1BitsInPartial0Mask = (char)(((1 << partial1Bitlength) - 1) << partial0StartInElement);
										newPartial0Element = (char)((partial0ElementWithCarriedBitsFromFirstMiddleWholeElement & (~partial1BitsInPartial0Mask)) | (reversedPartial1Bits << partial0StartInElement));
									}
									
									
									//Mask the larger low0 one into both the high1 one, AND a little into the last whole element (the one adjacent to partial1)!
									//If there is anything between the partials, then this spill-over will spill into the initial carry bits which we left as 0 above ^_~
									//newPartial1Element;
									{
										newPartial1Element = (char)((partial1Element & partial1ElementUntouchedMask) | ((reversedPartial0Bits & (commonDenominatorUntranslatedBitsMask << absShiftAmount)) >>> absShiftAmount));
										
										int lastElementIndex = wholeElementOffset + wholeElementCount - 1;
										x[lastElementIndex] = (char)((x[lastElementIndex] & (~highCarryMask)) | ((reversedPartial0Bits & lowCarryMask) << elementLengthMinusShiftAmount));
									}
								}
								else
								{
									//If there is not anything between the partials, then this spill-over will spill into the other partial, taking the place of the carry bits if there were elements between the partials! ^^
									
									//Mask the small high1 one into the larger low0 one! ^^
									char newPartial0ElementWithoutSpill;
									{
										final char partial1BitsInPartial0Mask = (char)(((1 << partial1Bitlength) - 1) << partial0StartInElement);
										newPartial0ElementWithoutSpill = (char)((partial0Element & (~partial1BitsInPartial0Mask)) | (reversedPartial1Bits << partial0StartInElement));
									}
									
									
									//Mask the larger low0 one into both the high1 one, AND a little into the last whole element (the one adjacent to partial1)!
									//If there is not anything between the partials, then this spill-over will spill into the other partial!
									//newPartial1Element;
									{
										newPartial1Element = (char)((partial1Element & partial1ElementUntouchedMask) | ((reversedPartial0Bits & (commonDenominatorUntranslatedBitsMask << absShiftAmount)) >>> absShiftAmount));
										
										newPartial0Element = (char)((newPartial0ElementWithoutSpill & (~highCarryMask)) | ((reversedPartial0Bits & lowCarryMask) << elementLengthMinusShiftAmount));
									}
								}
							}
						}
					}
					
					
					x[partial0Index] = newPartial0Element;
					x[partial1Index] = newPartial1Element;
					
					//DONE! \o/ :D
				}
				
				
				
				
				else if (hasPartial0 && !hasPartial1)
				{
					//Remember, left/0 partial starts at a point and *up*, while
					//right/1 partial starts at 0 and *ends* at a point!
					final int partial0Index = bitOffset / 16;
					final int partial0StartInElement = bitOffset % 16;
					final int partial0Bitlength = 16 - partial0StartInElement;
					//final int partial1Bitlength = 0;
					final char partial0Element = x[partial0Index];
					
					final char partial0ElementUntouchedMask = (char)((1 << partial0StartInElement) - 1);
					final char partial0ElementMask = (char)(~partial0ElementUntouchedMask);
					
					final char partial0Bits = (char)((partial0Element & partial0ElementMask) >>> partial0StartInElement);
					
					//Reverse regions :>
					final char reversedPartial0Bits = reverse(partial0Bits, partial0Bitlength);
					
					
					
					final char newPartial0Element;
					{
						//Not the same bitlength..shifting needed :P
						//Shift everything down!
						int shiftAmount = 0 - partial0Bitlength;
						assert shiftAmount < 0;
						final int absShiftAmount = -shiftAmount;
						
						//final int smallerPartialBitlength = 0;
						
						final char lowCarryMask = (char)((1 << absShiftAmount) - 1);
						final int elementLengthMinusShiftAmount = 16 - absShiftAmount;
						final char highCarryMask = (char)(lowCarryMask << elementLengthMinusShiftAmount);
						
						if (wholeElementCount != 0)
						{
							//Shift the whole-words that are in between!!
							char carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
							
							for (int i = wholeElementOffset+wholeElementCount-1; i >= 0; i--)
							{
								final char nextCarry = (char)(x[i] & lowCarryMask);
								x[i] = (char)((x[i] >>> absShiftAmount) | (carry << elementLengthMinusShiftAmount));
								carry = nextCarry;
							}
							
							//Carry it on down allll the way into the partial! ^v^
							assert partial0ElementUntouchedMask == (~highCarryMask);
							newPartial0Element = (char)((partial0Element & partial0ElementUntouchedMask) | (carry << partial0StartInElement));
							
							
							//Mask the reversed partial into the the last whole element :>
							{
								int lastElementIndex = wholeElementOffset + wholeElementCount - 1;
								assert (reversedPartial0Bits & lowCarryMask) == reversedPartial0Bits;  //carry mask should completely cover it :>
								x[lastElementIndex] = (char)((x[lastElementIndex] & (~highCarryMask)) | (reversedPartial0Bits << elementLengthMinusShiftAmount));
							}
						}
						else
						{
							throw new AssertionError("Should have been caught by single-element shortcuts ;_;");
						}
					}
					
					
					x[partial0Index] = newPartial0Element;
					
					//DONE! \o/ :D
				}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				else if (!hasPartial0 && hasPartial1)
				{
					//Remember, left/0 partial starts at a point and *up*, while
					//right/1 partial starts at 0 and *ends* at a point!
					final int partial1Index = (bitOffset + bitLength) / 16 + 1;
					final int partial1EndInElement = (bitOffset + bitLength) % 16;
					//final int partial0Bitlength = 0;
					final int partial1Bitlength = partial1EndInElement;
					final char partial1Element = x[partial1Index];
					
					final char partial1ElementMask = (char)((1 << partial1EndInElement) - 1);
					final char partial1ElementUntouchedMask = (char)(~partial1ElementMask);
					
					final char partial1Bits = (char)((partial1Element & partial1ElementMask));
					
					//Reverse regions :>
					final char reversedPartial1Bits = reverse(partial1Bits, partial1Bitlength);
					
					
					
					final char newPartial1Element;
					{
						//Not the same bitlength..shifting needed :P
						//Shift everything up!
						int shiftAmount = partial1Bitlength - 0;
						assert shiftAmount > 0;
						
						final char lowCarryMask = (char)((1 << shiftAmount) - 1);
						
						if (wholeElementCount != 0)
						{
							//Shift the whole-words that are in between!!
							char carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
							
							final int elementLengthMinusShiftAmount = 16 - shiftAmount;
							final char highCarryMask = (char)(lowCarryMask << elementLengthMinusShiftAmount);
							
							for (int i = wholeElementOffset; i < wholeElementCount; i++)
							{
								final char nextCarry = (char)((x[i] & highCarryMask) >>> shiftAmount);
								x[i] = (char)((x[i] << shiftAmount) | carry);
								carry = nextCarry;
							}
							
							//Carry it on up allll the way into the partial! ^v^
							assert (~lowCarryMask) == partial1ElementUntouchedMask;
							newPartial1Element = (char)((partial1Element & partial1ElementUntouchedMask) | carry);
							
							
							//Mask the reversed partial into the the first whole element :>
							{
								assert (reversedPartial1Bits & lowCarryMask) == reversedPartial1Bits;  //carry mask should completely cover it :>
								x[wholeElementOffset] = (char)((x[wholeElementOffset] & (~lowCarryMask)) | reversedPartial1Bits);
							}
						}
						else
						{
							throw new AssertionError("Should have been caught by single-element shortcuts ;_;");
						}
					}
					
					
					x[partial1Index] = newPartial1Element;
					
					//DONE! \o/ :D
				}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				else //if (!hasPartial0 && !hasPartial1)
				{
					//If no partials, then whole-elements were the only thing to reverse and swap..and we already did that!  SO WE'RE DONE JUST THAT FAST!  \o/  XD  :D
				}
			}
		}
	}
	
	
	
	
	public static void reverseIP(final @WritableValue short[] x, final int bitOffset, final int bitLength)
	{
		if (bitLength < 0)
			throw new IllegalArgumentException("bitOffset: "+bitOffset+", bitLength: "+bitLength+", capacity: "+(x.length * 16));
		if (bitOffset < 0 || (bitOffset+bitLength > x.length * 16))
			throw new IllegalArgumentException("bitOffset: "+bitOffset+", bitLength: "+bitLength+", capacity: "+(x.length * 16));
		
		
		
		//Shortcuts! \o/
		
		if (bitLength == 0)
			return;
		
		
		if (bitLength == 16 && (bitOffset % 16) == 0)
		{
			final int elementIndex = bitOffset / 16;
			
			x[elementIndex] = reverse(x[elementIndex]);
			
			return;
		}
		
		
		if (bitLength <= 16 && (bitOffset / 16 == SmallIntegerMathUtilities.ceilingDivision(bitOffset + bitLength, 16)))
		{
			final int elementIndex = bitOffset / 16;
			
			x[elementIndex] = reverse(x[elementIndex], bitOffset, bitLength);
			
			return;
		}
		
		
		
		
		//Main algorithm!
		{
			final boolean hasPartial0, hasPartial1;
			final int wholeElementOffset, wholeElementCount;
			{
				hasPartial0 = (bitOffset % 16) != 0;
				hasPartial1 = ((bitOffset + bitLength) % 16) != 0;
				final int wholeElementStart = (bitOffset / 16) + (hasPartial0 ? 1 : 0);  //the + 1/0 makes it ceiling division, which is what we want ^_^
				final int wholeElementPastEnd = ((bitOffset + bitLength) / 16);  //Java division is same as floor division for nonnegative integers, which is what we want ^_^
				
				if (wholeElementPastEnd < wholeElementStart)
				{
					wholeElementCount = 0;
					wholeElementOffset = 0;
				}
				else
				{
					wholeElementCount = wholeElementPastEnd - wholeElementStart;
					wholeElementOffset = wholeElementStart;
				}
			}
			
			
			//Deal with the whole-elements! :>
			{
				//Swap the whole-elements! ^w^
				{
					int other = 0;
					for (int i = 0; i < wholeElementCount; i++)
					{
						other = wholeElementCount - i - 1;
						
						x[i] ^= x[other];
						x[other] ^= x[i];
						x[i] ^= x[other];
					}
				}
				
				//Reverse the whole-elements! :D
				{
					for (int i = 0; i < wholeElementCount; i++)
						x[i] = reverse(x[i]);
				}
			}
			
			
			//Deal with the partials! :>''
			{
				if (hasPartial0 && hasPartial1)
				{
					//Remember, left/0 partial starts at a point and *up*, while
					//right/1 partial starts at 0 and *ends* at a point!
					final int partial0Index = bitOffset / 16;
					final int partial1Index = (bitOffset + bitLength) / 16 + 1;
					final int partial0StartInElement = bitOffset % 16;
					final int partial1EndInElement = (bitOffset + bitLength) % 16;
					final int partial0Bitlength = 16 - partial0StartInElement;
					final int partial1Bitlength = partial1EndInElement;
					final short partial0Element = x[partial0Index];
					final short partial1Element = x[partial1Index];
					
					final short partial0ElementUntouchedMask = (short)((1 << partial0StartInElement) - 1);
					final short partial1ElementMask = (short)((1 << partial1EndInElement) - 1);
					final short partial0ElementMask = (short)(~partial0ElementUntouchedMask);
					final short partial1ElementUntouchedMask = (short)(~partial1ElementMask);
					
					final short partial0Bits = (short)((partial0Element & partial0ElementMask) >>> partial0StartInElement);
					final short partial1Bits = (short)(partial1Element & partial1ElementMask);
					
					//Reverse regions :>
					final short reversedPartial0Bits = reverse(partial0Bits, partial0Bitlength);
					final short reversedPartial1Bits = reverse(partial1Bits, partial1Bitlength);
					
					
					
					final short newPartial0Element;
					final short newPartial1Element;
					{
						if (partial0Bitlength == partial1Bitlength)
						{
							//They're the same length!!  No shifting needed!!  :D
							
							//Mask back in! ^^
							newPartial0Element = (short)((partial0Element & partial0ElementUntouchedMask) | (reversedPartial1Bits << partial0StartInElement));
							newPartial1Element = (short)((partial1Element & partial1ElementUntouchedMask) | (reversedPartial0Bits));
						}
						
						else
						{
							//Not the same bitlength..shifting needed :P
							
							int shiftAmount = partial1Bitlength - partial0Bitlength;
							
							assert shiftAmount != 0;
							
							if (shiftAmount > 0)  //twos-complement is nice here! ^^
							{
								//Shift everything up!
								
								assert partial0Bitlength < partial1Bitlength;
								final int smallerPartialBitlength = partial0Bitlength;
								final short commonDenominatorUntranslatedBitsMask = (short)((1 << smallerPartialBitlength) - 1);
								
								final short lowCarryMask = (short)((1 << shiftAmount) - 1);
								
								if (wholeElementCount != 0)
								{
									//Shift the whole-words that are in between!!
									short carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
									
									final int elementLengthMinusShiftAmount = 16 - shiftAmount;
									final short highCarryMask = (short)(lowCarryMask << elementLengthMinusShiftAmount);
									
									for (int i = wholeElementOffset; i < wholeElementCount; i++)
									{
										final short nextCarry = (short)((x[i] & highCarryMask) >>> shiftAmount);
										x[i] = (short)((x[i] << shiftAmount) | carry);
										carry = nextCarry;
									}
									
									//Carry it on up allll the way into the partial! ^v^
									final short partial1ElementWithCarriedBitsFromLastMiddleWholeElement = (short)((partial1Element & (~lowCarryMask)) | carry);
									
									
									//Mask the small low0 one into the larger high1 one! ^^
									//newPartial1Element;
									{
										final short partial0BitsInPartial1Mask = (short)(((1 << partial0Bitlength) - 1) << shiftAmount);
										newPartial1Element = (short)((partial1ElementWithCarriedBitsFromLastMiddleWholeElement & (~partial0BitsInPartial1Mask)) | (reversedPartial0Bits << shiftAmount));
									}
									
									
									//Mask the larger high1 one into both the low0 one, AND a little into the next element!
									//If there is anything between the partials, then this spill-over will spill into the initial carry bits which we left as 0 above ^_~
									//newPartial0Element;
									{
										newPartial0Element = (short)((partial0Element & partial0ElementUntouchedMask) | ((reversedPartial1Bits & commonDenominatorUntranslatedBitsMask) << partial0StartInElement));
										
										//x[wholeElementOffset] = (x[wholeElementOffset] & (~lowCarryMask)) | ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask);
										assert (reversedPartial1Bits >>> smallerPartialBitlength) == ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask); //all higher bits should be 0 anyways! :P
										
										x[wholeElementOffset] = (short)((x[wholeElementOffset] & (~lowCarryMask)) | (reversedPartial1Bits >>> smallerPartialBitlength));
									}
								}
								else
								{
									//Mask the small low0 one into the larger high1 one! ^^
									final short newPartial1ElementWithoutSpill;
									{
										//Note: don't worry about the low shiftAmount bits, those will be overridden by the spill-over anyways XD'
										newPartial1ElementWithoutSpill = (short)((partial1Element & partial1ElementUntouchedMask) | (reversedPartial0Bits << shiftAmount));
									}
									
									
									//Mask the larger high1 one into both the low0 one, AND a little into the next element!
									//If there is not anything between the partials, then this spill-over will spill into the other partial!
									//newPartial0Element;
									{
										newPartial0Element = (short)((partial0Element & partial0ElementUntouchedMask) | ((reversedPartial1Bits & commonDenominatorUntranslatedBitsMask) << partial0StartInElement));
										
										//newPartial1Element = (newPartial1ElementWithoutSpill & (~lowCarryMask)) | ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask);
										assert (reversedPartial1Bits >>> smallerPartialBitlength) == ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask); //all higher bits should be 0 anyways, and Java shift is truncating not rotating! :P
										
										newPartial1Element = (short)((newPartial1ElementWithoutSpill & (~lowCarryMask)) | (reversedPartial1Bits >>> smallerPartialBitlength));
									}
								}
							}
							else
							{
								//Shift everything down!
								assert shiftAmount < 0;
								final int absShiftAmount = -shiftAmount;
								
								assert partial0Bitlength > partial1Bitlength;
								final int smallerPartialBitlength = partial1Bitlength;
								final short commonDenominatorUntranslatedBitsMask = (short)((1 << smallerPartialBitlength) - 1);
								
								final short lowCarryMask = (short)((1 << absShiftAmount) - 1);
								final int elementLengthMinusShiftAmount = 16 - absShiftAmount;
								final short highCarryMask = (short)(lowCarryMask << elementLengthMinusShiftAmount);
								
								if (wholeElementCount != 0)
								{
									//Shift the whole-words that are in between!!
									short carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
									
									for (int i = wholeElementOffset+wholeElementCount-1; i >= 0; i--)
									{
										final short nextCarry = (short)(x[i] & lowCarryMask);
										x[i] = (short)((x[i] >>> absShiftAmount) | (carry << elementLengthMinusShiftAmount));
										carry = nextCarry;
									}
									
									//Carry it on down allll the way into the partial! ^v^
									final short partial0ElementWithCarriedBitsFromFirstMiddleWholeElement = (short)((partial0Element & (~highCarryMask)) | (carry << elementLengthMinusShiftAmount));
									
									
									//Mask the small high1 one into the larger low0 one! ^^
									//newPartial0Element;
									{
										final short partial1BitsInPartial0Mask = (short)(((1 << partial1Bitlength) - 1) << partial0StartInElement);
										newPartial0Element = (short)((partial0ElementWithCarriedBitsFromFirstMiddleWholeElement & (~partial1BitsInPartial0Mask)) | (reversedPartial1Bits << partial0StartInElement));
									}
									
									
									//Mask the larger low0 one into both the high1 one, AND a little into the last whole element (the one adjacent to partial1)!
									//If there is anything between the partials, then this spill-over will spill into the initial carry bits which we left as 0 above ^_~
									//newPartial1Element;
									{
										newPartial1Element = (short)((partial1Element & partial1ElementUntouchedMask) | ((reversedPartial0Bits & (commonDenominatorUntranslatedBitsMask << absShiftAmount)) >>> absShiftAmount));
										
										int lastElementIndex = wholeElementOffset + wholeElementCount - 1;
										x[lastElementIndex] = (short)((x[lastElementIndex] & (~highCarryMask)) | ((reversedPartial0Bits & lowCarryMask) << elementLengthMinusShiftAmount));
									}
								}
								else
								{
									//If there is not anything between the partials, then this spill-over will spill into the other partial, taking the place of the carry bits if there were elements between the partials! ^^
									
									//Mask the small high1 one into the larger low0 one! ^^
									short newPartial0ElementWithoutSpill;
									{
										final short partial1BitsInPartial0Mask = (short)(((1 << partial1Bitlength) - 1) << partial0StartInElement);
										newPartial0ElementWithoutSpill = (short)((partial0Element & (~partial1BitsInPartial0Mask)) | (reversedPartial1Bits << partial0StartInElement));
									}
									
									
									//Mask the larger low0 one into both the high1 one, AND a little into the last whole element (the one adjacent to partial1)!
									//If there is not anything between the partials, then this spill-over will spill into the other partial!
									//newPartial1Element;
									{
										newPartial1Element = (short)((partial1Element & partial1ElementUntouchedMask) | ((reversedPartial0Bits & (commonDenominatorUntranslatedBitsMask << absShiftAmount)) >>> absShiftAmount));
										
										newPartial0Element = (short)((newPartial0ElementWithoutSpill & (~highCarryMask)) | ((reversedPartial0Bits & lowCarryMask) << elementLengthMinusShiftAmount));
									}
								}
							}
						}
					}
					
					
					x[partial0Index] = newPartial0Element;
					x[partial1Index] = newPartial1Element;
					
					//DONE! \o/ :D
				}
				
				
				
				
				else if (hasPartial0 && !hasPartial1)
				{
					//Remember, left/0 partial starts at a point and *up*, while
					//right/1 partial starts at 0 and *ends* at a point!
					final int partial0Index = bitOffset / 16;
					final int partial0StartInElement = bitOffset % 16;
					final int partial0Bitlength = 16 - partial0StartInElement;
					//final int partial1Bitlength = 0;
					final short partial0Element = x[partial0Index];
					
					final short partial0ElementUntouchedMask = (short)((1 << partial0StartInElement) - 1);
					final short partial0ElementMask = (short)(~partial0ElementUntouchedMask);
					
					final short partial0Bits = (short)((partial0Element & partial0ElementMask) >>> partial0StartInElement);
					
					//Reverse regions :>
					final short reversedPartial0Bits = reverse(partial0Bits, partial0Bitlength);
					
					
					
					final short newPartial0Element;
					{
						//Not the same bitlength..shifting needed :P
						//Shift everything down!
						int shiftAmount = 0 - partial0Bitlength;
						assert shiftAmount < 0;
						final int absShiftAmount = -shiftAmount;
						
						//final int smallerPartialBitlength = 0;
						
						final short lowCarryMask = (short)((1 << absShiftAmount) - 1);
						final int elementLengthMinusShiftAmount = 16 - absShiftAmount;
						final short highCarryMask = (short)(lowCarryMask << elementLengthMinusShiftAmount);
						
						if (wholeElementCount != 0)
						{
							//Shift the whole-words that are in between!!
							short carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
							
							for (int i = wholeElementOffset+wholeElementCount-1; i >= 0; i--)
							{
								final short nextCarry = (short)(x[i] & lowCarryMask);
								x[i] = (short)((x[i] >>> absShiftAmount) | (carry << elementLengthMinusShiftAmount));
								carry = nextCarry;
							}
							
							//Carry it on down allll the way into the partial! ^v^
							assert partial0ElementUntouchedMask == (~highCarryMask);
							newPartial0Element = (short)((partial0Element & partial0ElementUntouchedMask) | (carry << partial0StartInElement));
							
							
							//Mask the reversed partial into the the last whole element :>
							{
								int lastElementIndex = wholeElementOffset + wholeElementCount - 1;
								assert (reversedPartial0Bits & lowCarryMask) == reversedPartial0Bits;  //carry mask should completely cover it :>
								x[lastElementIndex] = (short)((x[lastElementIndex] & (~highCarryMask)) | (reversedPartial0Bits << elementLengthMinusShiftAmount));
							}
						}
						else
						{
							throw new AssertionError("Should have been caught by single-element shortcuts ;_;");
						}
					}
					
					
					x[partial0Index] = newPartial0Element;
					
					//DONE! \o/ :D
				}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				else if (!hasPartial0 && hasPartial1)
				{
					//Remember, left/0 partial starts at a point and *up*, while
					//right/1 partial starts at 0 and *ends* at a point!
					final int partial1Index = (bitOffset + bitLength) / 16 + 1;
					final int partial1EndInElement = (bitOffset + bitLength) % 16;
					//final int partial0Bitlength = 0;
					final int partial1Bitlength = partial1EndInElement;
					final short partial1Element = x[partial1Index];
					
					final short partial1ElementMask = (short)((1 << partial1EndInElement) - 1);
					final short partial1ElementUntouchedMask = (short)(~partial1ElementMask);
					
					final short partial1Bits = (short)((partial1Element & partial1ElementMask));
					
					//Reverse regions :>
					final short reversedPartial1Bits = reverse(partial1Bits, partial1Bitlength);
					
					
					
					final short newPartial1Element;
					{
						//Not the same bitlength..shifting needed :P
						//Shift everything up!
						int shiftAmount = partial1Bitlength - 0;
						assert shiftAmount > 0;
						
						final short lowCarryMask = (short)((1 << shiftAmount) - 1);
						
						if (wholeElementCount != 0)
						{
							//Shift the whole-words that are in between!!
							short carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
							
							final int elementLengthMinusShiftAmount = 16 - shiftAmount;
							final short highCarryMask = (short)(lowCarryMask << elementLengthMinusShiftAmount);
							
							for (int i = wholeElementOffset; i < wholeElementCount; i++)
							{
								final short nextCarry = (short)((x[i] & highCarryMask) >>> shiftAmount);
								x[i] = (short)((x[i] << shiftAmount) | carry);
								carry = nextCarry;
							}
							
							//Carry it on up allll the way into the partial! ^v^
							assert (~lowCarryMask) == partial1ElementUntouchedMask;
							newPartial1Element = (short)((partial1Element & partial1ElementUntouchedMask) | carry);
							
							
							//Mask the reversed partial into the the first whole element :>
							{
								assert (reversedPartial1Bits & lowCarryMask) == reversedPartial1Bits;  //carry mask should completely cover it :>
								x[wholeElementOffset] = (short)((x[wholeElementOffset] & (~lowCarryMask)) | reversedPartial1Bits);
							}
						}
						else
						{
							throw new AssertionError("Should have been caught by single-element shortcuts ;_;");
						}
					}
					
					
					x[partial1Index] = newPartial1Element;
					
					//DONE! \o/ :D
				}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				else //if (!hasPartial0 && !hasPartial1)
				{
					//If no partials, then whole-elements were the only thing to reverse and swap..and we already did that!  SO WE'RE DONE JUST THAT FAST!  \o/  XD  :D
				}
			}
		}
	}
	
	
	
	
	public static void reverseIP(final @WritableValue int[] x, final int bitOffset, final int bitLength)
	{
		if (bitLength < 0)
			throw new IllegalArgumentException("bitOffset: "+bitOffset+", bitLength: "+bitLength+", capacity: "+(x.length * 32));
		if (bitOffset < 0 || (bitOffset+bitLength > x.length * 32))
			throw new IllegalArgumentException("bitOffset: "+bitOffset+", bitLength: "+bitLength+", capacity: "+(x.length * 32));
		
		
		
		//Shortcuts! \o/
		
		if (bitLength == 0)
			return;
		
		
		if (bitLength == 32 && (bitOffset % 32) == 0)
		{
			final int elementIndex = bitOffset / 32;
			
			x[elementIndex] = reverse(x[elementIndex]);
			
			return;
		}
		
		
		if (bitLength <= 32 && (bitOffset / 32 == SmallIntegerMathUtilities.ceilingDivision(bitOffset + bitLength, 32)))
		{
			final int elementIndex = bitOffset / 32;
			
			x[elementIndex] = reverse32(x[elementIndex], bitOffset, bitLength);
			
			return;
		}
		
		
		
		
		//Main algorithm!
		{
			final boolean hasPartial0, hasPartial1;
			final int wholeElementOffset, wholeElementCount;
			{
				hasPartial0 = (bitOffset % 32) != 0;
				hasPartial1 = ((bitOffset + bitLength) % 32) != 0;
				final int wholeElementStart = (bitOffset / 32) + (hasPartial0 ? 1 : 0);  //the + 1/0 makes it ceiling division, which is what we want ^_^
				final int wholeElementPastEnd = ((bitOffset + bitLength) / 32);  //Java division is same as floor division for nonnegative integers, which is what we want ^_^
				
				if (wholeElementPastEnd < wholeElementStart)
				{
					wholeElementCount = 0;
					wholeElementOffset = 0;
				}
				else
				{
					wholeElementCount = wholeElementPastEnd - wholeElementStart;
					wholeElementOffset = wholeElementStart;
				}
			}
			
			
			//Deal with the whole-elements! :>
			{
				//Swap the whole-elements! ^w^
				{
					int other = 0;
					for (int i = 0; i < wholeElementCount; i++)
					{
						other = wholeElementCount - i - 1;
						
						x[i] ^= x[other];
						x[other] ^= x[i];
						x[i] ^= x[other];
					}
				}
				
				//Reverse the whole-elements! :D
				{
					for (int i = 0; i < wholeElementCount; i++)
						x[i] = reverse(x[i]);
				}
			}
			
			
			//Deal with the partials! :>''
			{
				if (hasPartial0 && hasPartial1)
				{
					//Remember, left/0 partial starts at a point and *up*, while
					//right/1 partial starts at 0 and *ends* at a point!
					final int partial0Index = bitOffset / 32;
					final int partial1Index = (bitOffset + bitLength) / 32 + 1;
					final int partial0StartInElement = bitOffset % 32;
					final int partial1EndInElement = (bitOffset + bitLength) % 32;
					final int partial0Bitlength = 32 - partial0StartInElement;
					final int partial1Bitlength = partial1EndInElement;
					final int partial0Element = x[partial0Index];
					final int partial1Element = x[partial1Index];
					
					final int partial0ElementUntouchedMask = (1 << partial0StartInElement) - 1;
					final int partial1ElementMask = (1 << partial1EndInElement) - 1;
					final int partial0ElementMask = (~partial0ElementUntouchedMask);
					final int partial1ElementUntouchedMask = (~partial1ElementMask);
					
					final int partial0Bits = (partial0Element & partial0ElementMask) >>> partial0StartInElement;
					final int partial1Bits = partial1Element & partial1ElementMask;
					
					//Reverse regions :>
					final int reversedPartial0Bits = reverse(partial0Bits, partial0Bitlength);
					final int reversedPartial1Bits = reverse(partial1Bits, partial1Bitlength);
					
					
					
					final int newPartial0Element;
					final int newPartial1Element;
					{
						if (partial0Bitlength == partial1Bitlength)
						{
							//They're the same length!!  No shifting needed!!  :D
							
							//Mask back in! ^^
							newPartial0Element = (partial0Element & partial0ElementUntouchedMask) | (reversedPartial1Bits << partial0StartInElement);
							newPartial1Element = (partial1Element & partial1ElementUntouchedMask) | (reversedPartial0Bits);
						}
						
						else
						{
							//Not the same bitlength..shifting needed :P
							
							int shiftAmount = partial1Bitlength - partial0Bitlength;
							
							assert shiftAmount != 0;
							
							if (shiftAmount > 0)  //twos-complement is nice here! ^^
							{
								//Shift everything up!
								
								assert partial0Bitlength < partial1Bitlength;
								final int smallerPartialBitlength = partial0Bitlength;
								final int commonDenominatorUntranslatedBitsMask = (1 << smallerPartialBitlength) - 1;
								
								final int lowCarryMask = (1 << shiftAmount) - 1;
								
								if (wholeElementCount != 0)
								{
									//Shift the whole-words that are in between!!
									int carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
									
									final int elementLengthMinusShiftAmount = 32 - shiftAmount;
									final int highCarryMask = lowCarryMask << elementLengthMinusShiftAmount;
									
									for (int i = wholeElementOffset; i < wholeElementCount; i++)
									{
										final int nextCarry = (x[i] & highCarryMask) >>> shiftAmount;
									x[i] = (x[i] << shiftAmount) | carry;
									carry = nextCarry;
									}
									
									//Carry it on up allll the way into the partial! ^v^
									final int partial1ElementWithCarriedBitsFromLastMiddleWholeElement = (partial1Element & (~lowCarryMask)) | carry;
									
									
									//Mask the small low0 one into the larger high1 one! ^^
									//newPartial1Element;
									{
										final int partial0BitsInPartial1Mask = ((1 << partial0Bitlength) - 1) << shiftAmount;
										newPartial1Element = (partial1ElementWithCarriedBitsFromLastMiddleWholeElement & (~partial0BitsInPartial1Mask)) | (reversedPartial0Bits << shiftAmount);
									}
									
									
									//Mask the larger high1 one into both the low0 one, AND a little into the next element!
									//If there is anything between the partials, then this spill-over will spill into the initial carry bits which we left as 0 above ^_~
									//newPartial0Element;
									{
										newPartial0Element = (partial0Element & partial0ElementUntouchedMask) | ((reversedPartial1Bits & commonDenominatorUntranslatedBitsMask) << partial0StartInElement);
										
										//x[wholeElementOffset] = (x[wholeElementOffset] & (~lowCarryMask)) | ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask);
										assert (reversedPartial1Bits >>> smallerPartialBitlength) == ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask); //all higher bits should be 0 anyways! :P
										
										x[wholeElementOffset] = (x[wholeElementOffset] & (~lowCarryMask)) | (reversedPartial1Bits >>> smallerPartialBitlength);
									}
								}
								else
								{
									//Mask the small low0 one into the larger high1 one! ^^
									final int newPartial1ElementWithoutSpill;
									{
										//Note: don't worry about the low shiftAmount bits, those will be overridden by the spill-over anyways XD'
										newPartial1ElementWithoutSpill = (partial1Element & partial1ElementUntouchedMask) | (reversedPartial0Bits << shiftAmount);
									}
									
									
									//Mask the larger high1 one into both the low0 one, AND a little into the next element!
									//If there is not anything between the partials, then this spill-over will spill into the other partial!
									//newPartial0Element;
									{
										newPartial0Element = (partial0Element & partial0ElementUntouchedMask) | ((reversedPartial1Bits & commonDenominatorUntranslatedBitsMask) << partial0StartInElement);
										
										//newPartial1Element = (newPartial1ElementWithoutSpill & (~lowCarryMask)) | ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask);
										assert (reversedPartial1Bits >>> smallerPartialBitlength) == ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask); //all higher bits should be 0 anyways, and Java shift is truncating not rotating! :P
										
										newPartial1Element = (newPartial1ElementWithoutSpill & (~lowCarryMask)) | (reversedPartial1Bits >>> smallerPartialBitlength);
									}
								}
							}
							else
							{
								//Shift everything down!
								assert shiftAmount < 0;
								final int absShiftAmount = -shiftAmount;
								
								assert partial0Bitlength > partial1Bitlength;
								final int smallerPartialBitlength = partial1Bitlength;
								final int commonDenominatorUntranslatedBitsMask = (1 << smallerPartialBitlength) - 1;
								
								final int lowCarryMask = (1 << absShiftAmount) - 1;
								final int elementLengthMinusShiftAmount = 32 - absShiftAmount;
								final int highCarryMask = lowCarryMask << elementLengthMinusShiftAmount;
								
								if (wholeElementCount != 0)
								{
									//Shift the whole-words that are in between!!
									int carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
									
									for (int i = wholeElementOffset+wholeElementCount-1; i >= 0; i--)
									{
										final int nextCarry = x[i] & lowCarryMask;
										x[i] = (x[i] >>> absShiftAmount) | (carry << elementLengthMinusShiftAmount);
										carry = nextCarry;
									}
									
									//Carry it on down allll the way into the partial! ^v^
									final int partial0ElementWithCarriedBitsFromFirstMiddleWholeElement = (partial0Element & (~highCarryMask)) | (carry << elementLengthMinusShiftAmount);
									
									
									//Mask the small high1 one into the larger low0 one! ^^
									//newPartial0Element;
									{
										final int partial1BitsInPartial0Mask = ((1 << partial1Bitlength) - 1) << partial0StartInElement;
										newPartial0Element = (partial0ElementWithCarriedBitsFromFirstMiddleWholeElement & (~partial1BitsInPartial0Mask)) | (reversedPartial1Bits << partial0StartInElement);
									}
									
									
									//Mask the larger low0 one into both the high1 one, AND a little into the last whole element (the one adjacent to partial1)!
									//If there is anything between the partials, then this spill-over will spill into the initial carry bits which we left as 0 above ^_~
									//newPartial1Element;
									{
										newPartial1Element = (partial1Element & partial1ElementUntouchedMask) | ((reversedPartial0Bits & (commonDenominatorUntranslatedBitsMask << absShiftAmount)) >>> absShiftAmount);
										
										int lastElementIndex = wholeElementOffset + wholeElementCount - 1;
										x[lastElementIndex] = (x[lastElementIndex] & (~highCarryMask)) | ((reversedPartial0Bits & lowCarryMask) << elementLengthMinusShiftAmount);
									}
								}
								else
								{
									//If there is not anything between the partials, then this spill-over will spill into the other partial, taking the place of the carry bits if there were elements between the partials! ^^
									
									//Mask the small high1 one into the larger low0 one! ^^
									int newPartial0ElementWithoutSpill;
									{
										final int partial1BitsInPartial0Mask = ((1 << partial1Bitlength) - 1) << partial0StartInElement;
										newPartial0ElementWithoutSpill = (partial0Element & (~partial1BitsInPartial0Mask)) | (reversedPartial1Bits << partial0StartInElement);
									}
									
									
									//Mask the larger low0 one into both the high1 one, AND a little into the last whole element (the one adjacent to partial1)!
									//If there is not anything between the partials, then this spill-over will spill into the other partial!
									//newPartial1Element;
									{
										newPartial1Element = (partial1Element & partial1ElementUntouchedMask) | ((reversedPartial0Bits & (commonDenominatorUntranslatedBitsMask << absShiftAmount)) >>> absShiftAmount);
										
										newPartial0Element = (newPartial0ElementWithoutSpill & (~highCarryMask)) | ((reversedPartial0Bits & lowCarryMask) << elementLengthMinusShiftAmount);
									}
								}
							}
						}
					}
					
					
					x[partial0Index] = newPartial0Element;
					x[partial1Index] = newPartial1Element;
					
					//DONE! \o/ :D
				}
				
				
				
				
				else if (hasPartial0 && !hasPartial1)
				{
					//Remember, left/0 partial starts at a point and *up*, while
					//right/1 partial starts at 0 and *ends* at a point!
					final int partial0Index = bitOffset / 32;
					final int partial0StartInElement = bitOffset % 32;
					final int partial0Bitlength = 32 - partial0StartInElement;
					//final int partial1Bitlength = 0;
					final int partial0Element = x[partial0Index];
					
					final int partial0ElementUntouchedMask = (1 << partial0StartInElement) - 1;
					final int partial0ElementMask = (~partial0ElementUntouchedMask);
					
					final int partial0Bits = (partial0Element & partial0ElementMask) >>> partial0StartInElement;
					
					//Reverse regions :>
					final int reversedPartial0Bits = reverse(partial0Bits, partial0Bitlength);
					
					
					
					final int newPartial0Element;
					{
						//Not the same bitlength..shifting needed :P
						//Shift everything down!
						int shiftAmount = 0 - partial0Bitlength;
						assert shiftAmount < 0;
						final int absShiftAmount = -shiftAmount;
						
						//final int smallerPartialBitlength = 0;
						
						final int lowCarryMask = (1 << absShiftAmount) - 1;
						final int elementLengthMinusShiftAmount = 32 - absShiftAmount;
						final int highCarryMask = lowCarryMask << elementLengthMinusShiftAmount;
						
						if (wholeElementCount != 0)
						{
							//Shift the whole-words that are in between!!
							int carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
							
							for (int i = wholeElementOffset+wholeElementCount-1; i >= 0; i--)
							{
								final int nextCarry = x[i] & lowCarryMask;
								x[i] = (x[i] >>> absShiftAmount) | (carry << elementLengthMinusShiftAmount);
								carry = nextCarry;
							}
							
							//Carry it on down allll the way into the partial! ^v^
							assert partial0ElementUntouchedMask == (~highCarryMask);
							newPartial0Element = (partial0Element & partial0ElementUntouchedMask) | (carry << partial0StartInElement);
							
							
							//Mask the reversed partial into the the last whole element :>
							{
								int lastElementIndex = wholeElementOffset + wholeElementCount - 1;
								assert (reversedPartial0Bits & lowCarryMask) == reversedPartial0Bits;  //carry mask should completely cover it :>
								x[lastElementIndex] = (x[lastElementIndex] & (~highCarryMask)) | (reversedPartial0Bits << elementLengthMinusShiftAmount);
							}
						}
						else
						{
							throw new AssertionError("Should have been caught by single-element shortcuts ;_;");
						}
					}
					
					
					x[partial0Index] = newPartial0Element;
					
					//DONE! \o/ :D
				}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				else if (!hasPartial0 && hasPartial1)
				{
					//Remember, left/0 partial starts at a point and *up*, while
					//right/1 partial starts at 0 and *ends* at a point!
					final int partial1Index = (bitOffset + bitLength) / 32 + 1;
					final int partial1EndInElement = (bitOffset + bitLength) % 32;
					//final int partial0Bitlength = 0;
					final int partial1Bitlength = partial1EndInElement;
					final int partial1Element = x[partial1Index];
					
					final int partial1ElementMask = (1 << partial1EndInElement) - 1;
					final int partial1ElementUntouchedMask = (~partial1ElementMask);
					
					final int partial1Bits = ((partial1Element & partial1ElementMask));
					
					//Reverse regions :>
					final int reversedPartial1Bits = reverse(partial1Bits, partial1Bitlength);
					
					
					
					final int newPartial1Element;
					{
						//Not the same bitlength..shifting needed :P
						//Shift everything up!
						int shiftAmount = partial1Bitlength - 0;
						assert shiftAmount > 0;
						
						final int lowCarryMask = (1 << shiftAmount) - 1;
						
						if (wholeElementCount != 0)
						{
							//Shift the whole-words that are in between!!
							int carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
							
							final int elementLengthMinusShiftAmount = 32 - shiftAmount;
							final int highCarryMask = lowCarryMask << elementLengthMinusShiftAmount;
							
							for (int i = wholeElementOffset; i < wholeElementCount; i++)
							{
								final int nextCarry = (x[i] & highCarryMask) >>> shiftAmount;
							x[i] = (x[i] << shiftAmount) | carry;
							carry = nextCarry;
							}
							
							//Carry it on up allll the way into the partial! ^v^
							assert (~lowCarryMask) == partial1ElementUntouchedMask;
							newPartial1Element = (partial1Element & partial1ElementUntouchedMask) | carry;
							
							
							//Mask the reversed partial into the the first whole element :>
							{
								assert (reversedPartial1Bits & lowCarryMask) == reversedPartial1Bits;  //carry mask should completely cover it :>
								x[wholeElementOffset] = (x[wholeElementOffset] & (~lowCarryMask)) | reversedPartial1Bits;
							}
						}
						else
						{
							throw new AssertionError("Should have been caught by single-element shortcuts ;_;");
						}
					}
					
					
					x[partial1Index] = newPartial1Element;
					
					//DONE! \o/ :D
				}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				else //if (!hasPartial0 && !hasPartial1)
				{
					//If no partials, then whole-elements were the only thing to reverse and swap..and we already did that!  SO WE'RE DONE JUST THAT FAST!  \o/  XD  :D
				}
			}
		}
	}
	
	
	
	
	public static void reverseIP(final @WritableValue long[] x, final int bitOffset, final int bitLength)
	{
		if (bitLength < 0)
			throw new IllegalArgumentException("bitOffset: "+bitOffset+", bitLength: "+bitLength+", capacity: "+(x.length * 64));
		if (bitOffset < 0 || (bitOffset+bitLength > x.length * 64))
			throw new IllegalArgumentException("bitOffset: "+bitOffset+", bitLength: "+bitLength+", capacity: "+(x.length * 64));
		
		
		
		//Shortcuts! \o/
		
		if (bitLength == 0)
			return;
		
		
		if (bitLength == 64 && (bitOffset % 64) == 0)
		{
			final int elementIndex = bitOffset / 64;
			
			x[elementIndex] = reverse(x[elementIndex]);
			
			return;
		}
		
		
		if (bitLength <= 64 && (bitOffset / 64 == SmallIntegerMathUtilities.ceilingDivision(bitOffset + bitLength, 64)))
		{
			final int elementIndex = bitOffset / 64;
			
			x[elementIndex] = reverse64(x[elementIndex], bitOffset, bitLength);
			
			return;
		}
		
		
		
		
		//Main algorithm!
		{
			final boolean hasPartial0, hasPartial1;
			final int wholeElementOffset, wholeElementCount;
			{
				hasPartial0 = (bitOffset % 64) != 0;
				hasPartial1 = ((bitOffset + bitLength) % 64) != 0;
				final int wholeElementStart = (bitOffset / 64) + (hasPartial0 ? 1 : 0);  //the + 1/0 makes it ceiling division, which is what we want ^_^
				final int wholeElementPastEnd = ((bitOffset + bitLength) / 64);  //Java division is same as floor division for nonnegative integers, which is what we want ^_^
				
				if (wholeElementPastEnd < wholeElementStart)
				{
					wholeElementCount = 0;
					wholeElementOffset = 0;
				}
				else
				{
					wholeElementCount = wholeElementPastEnd - wholeElementStart;
					wholeElementOffset = wholeElementStart;
				}
			}
			
			
			//Deal with the whole-elements! :>
			{
				//Swap the whole-elements! ^w^
				{
					int other = 0;
					for (int i = 0; i < wholeElementCount; i++)
					{
						other = wholeElementCount - i - 1;
						
						x[i] ^= x[other];
						x[other] ^= x[i];
						x[i] ^= x[other];
					}
				}
				
				//Reverse the whole-elements! :D
				{
					for (int i = 0; i < wholeElementCount; i++)
						x[i] = reverse(x[i]);
				}
			}
			
			
			//Deal with the partials! :>''
			{
				if (hasPartial0 && hasPartial1)
				{
					//Remember, left/0 partial starts at a point and *up*, while
					//right/1 partial starts at 0 and *ends* at a point!
					final int partial0Index = bitOffset / 64;
					final int partial1Index = (bitOffset + bitLength) / 64 + 1;
					final int partial0StartInElement = bitOffset % 64;
					final int partial1EndInElement = (bitOffset + bitLength) % 64;
					final int partial0Bitlength = 64 - partial0StartInElement;
					final int partial1Bitlength = partial1EndInElement;
					final long partial0Element = x[partial0Index];
					final long partial1Element = x[partial1Index];
					
					final long partial0ElementUntouchedMask = (1 << partial0StartInElement) - 1;
					final long partial1ElementMask = (1 << partial1EndInElement) - 1;
					final long partial0ElementMask = (~partial0ElementUntouchedMask);
					final long partial1ElementUntouchedMask = (~partial1ElementMask);
					
					final long partial0Bits = (partial0Element & partial0ElementMask) >>> partial0StartInElement;
					final long partial1Bits = partial1Element & partial1ElementMask;
					
					//Reverse regions :>
					final long reversedPartial0Bits = reverse(partial0Bits, partial0Bitlength);
					final long reversedPartial1Bits = reverse(partial1Bits, partial1Bitlength);
					
					
					
					final long newPartial0Element;
					final long newPartial1Element;
					{
						if (partial0Bitlength == partial1Bitlength)
						{
							//They're the same length!!  No shifting needed!!  :D
							
							//Mask back in! ^^
							newPartial0Element = (partial0Element & partial0ElementUntouchedMask) | (reversedPartial1Bits << partial0StartInElement);
							newPartial1Element = (partial1Element & partial1ElementUntouchedMask) | (reversedPartial0Bits);
						}
						
						else
						{
							//Not the same bitlength..shifting needed :P
							
							int shiftAmount = partial1Bitlength - partial0Bitlength;
							
							assert shiftAmount != 0;
							
							if (shiftAmount > 0)  //twos-complement is nice here! ^^
							{
								//Shift everything up!
								
								assert partial0Bitlength < partial1Bitlength;
								final int smallerPartialBitlength = partial0Bitlength;
								final long commonDenominatorUntranslatedBitsMask = (1 << smallerPartialBitlength) - 1;
								
								final long lowCarryMask = (1 << shiftAmount) - 1;
								
								if (wholeElementCount != 0)
								{
									//Shift the whole-words that are in between!!
									long carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
									
									final int elementLengthMinusShiftAmount = 64 - shiftAmount;
									final long highCarryMask = lowCarryMask << elementLengthMinusShiftAmount;
									
									for (int i = wholeElementOffset; i < wholeElementCount; i++)
									{
										final long nextCarry = (x[i] & highCarryMask) >>> shiftAmount;
									x[i] = (x[i] << shiftAmount) | carry;
									carry = nextCarry;
									}
									
									//Carry it on up allll the way into the partial! ^v^
									final long partial1ElementWithCarriedBitsFromLastMiddleWholeElement = (partial1Element & (~lowCarryMask)) | carry;
									
									
									//Mask the small low0 one into the larger high1 one! ^^
									//newPartial1Element;
									{
										final long partial0BitsInPartial1Mask = ((1 << partial0Bitlength) - 1) << shiftAmount;
										newPartial1Element = (partial1ElementWithCarriedBitsFromLastMiddleWholeElement & (~partial0BitsInPartial1Mask)) | (reversedPartial0Bits << shiftAmount);
									}
									
									
									//Mask the larger high1 one into both the low0 one, AND a little into the next element!
									//If there is anything between the partials, then this spill-over will spill into the initial carry bits which we left as 0 above ^_~
									//newPartial0Element;
									{
										newPartial0Element = (partial0Element & partial0ElementUntouchedMask) | ((reversedPartial1Bits & commonDenominatorUntranslatedBitsMask) << partial0StartInElement);
										
										//x[wholeElementOffset] = (x[wholeElementOffset] & (~lowCarryMask)) | ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask);
										assert (reversedPartial1Bits >>> smallerPartialBitlength) == ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask); //all higher bits should be 0 anyways! :P
										
										x[wholeElementOffset] = (x[wholeElementOffset] & (~lowCarryMask)) | (reversedPartial1Bits >>> smallerPartialBitlength);
									}
								}
								else
								{
									//Mask the small low0 one into the larger high1 one! ^^
									final long newPartial1ElementWithoutSpill;
									{
										//Note: don't worry about the low shiftAmount bits, those will be overridden by the spill-over anyways XD'
										newPartial1ElementWithoutSpill = (partial1Element & partial1ElementUntouchedMask) | (reversedPartial0Bits << shiftAmount);
									}
									
									
									//Mask the larger high1 one into both the low0 one, AND a little into the next element!
									//If there is not anything between the partials, then this spill-over will spill into the other partial!
									//newPartial0Element;
									{
										newPartial0Element = (partial0Element & partial0ElementUntouchedMask) | ((reversedPartial1Bits & commonDenominatorUntranslatedBitsMask) << partial0StartInElement);
										
										//newPartial1Element = (newPartial1ElementWithoutSpill & (~lowCarryMask)) | ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask);
										assert (reversedPartial1Bits >>> smallerPartialBitlength) == ((reversedPartial1Bits >>> smallerPartialBitlength) & lowCarryMask); //all higher bits should be 0 anyways, and Java shift is truncating not rotating! :P
										
										newPartial1Element = (newPartial1ElementWithoutSpill & (~lowCarryMask)) | (reversedPartial1Bits >>> smallerPartialBitlength);
									}
								}
							}
							else
							{
								//Shift everything down!
								assert shiftAmount < 0;
								final int absShiftAmount = -shiftAmount;
								
								assert partial0Bitlength > partial1Bitlength;
								final int smallerPartialBitlength = partial1Bitlength;
								final long commonDenominatorUntranslatedBitsMask = (1 << smallerPartialBitlength) - 1;
								
								final long lowCarryMask = (1 << absShiftAmount) - 1;
								final int elementLengthMinusShiftAmount = 64 - absShiftAmount;
								final long highCarryMask = lowCarryMask << elementLengthMinusShiftAmount;
								
								if (wholeElementCount != 0)
								{
									//Shift the whole-words that are in between!!
									long carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
									
									for (int i = wholeElementOffset+wholeElementCount-1; i >= 0; i--)
									{
										final long nextCarry = x[i] & lowCarryMask;
										x[i] = (x[i] >>> absShiftAmount) | (carry << elementLengthMinusShiftAmount);
										carry = nextCarry;
									}
									
									//Carry it on down allll the way into the partial! ^v^
									final long partial0ElementWithCarriedBitsFromFirstMiddleWholeElement = (partial0Element & (~highCarryMask)) | (carry << elementLengthMinusShiftAmount);
									
									
									//Mask the small high1 one into the larger low0 one! ^^
									//newPartial0Element;
									{
										final long partial1BitsInPartial0Mask = ((1 << partial1Bitlength) - 1) << partial0StartInElement;
										newPartial0Element = (partial0ElementWithCarriedBitsFromFirstMiddleWholeElement & (~partial1BitsInPartial0Mask)) | (reversedPartial1Bits << partial0StartInElement);
									}
									
									
									//Mask the larger low0 one into both the high1 one, AND a little into the last whole element (the one adjacent to partial1)!
									//If there is anything between the partials, then this spill-over will spill into the initial carry bits which we left as 0 above ^_~
									//newPartial1Element;
									{
										newPartial1Element = (partial1Element & partial1ElementUntouchedMask) | ((reversedPartial0Bits & (commonDenominatorUntranslatedBitsMask << absShiftAmount)) >>> absShiftAmount);
										
										int lastElementIndex = wholeElementOffset + wholeElementCount - 1;
										x[lastElementIndex] = (x[lastElementIndex] & (~highCarryMask)) | ((reversedPartial0Bits & lowCarryMask) << elementLengthMinusShiftAmount);
									}
								}
								else
								{
									//If there is not anything between the partials, then this spill-over will spill into the other partial, taking the place of the carry bits if there were elements between the partials! ^^
									
									//Mask the small high1 one into the larger low0 one! ^^
									long newPartial0ElementWithoutSpill;
									{
										final long partial1BitsInPartial0Mask = ((1 << partial1Bitlength) - 1) << partial0StartInElement;
										newPartial0ElementWithoutSpill = (partial0Element & (~partial1BitsInPartial0Mask)) | (reversedPartial1Bits << partial0StartInElement);
									}
									
									
									//Mask the larger low0 one into both the high1 one, AND a little into the last whole element (the one adjacent to partial1)!
									//If there is not anything between the partials, then this spill-over will spill into the other partial!
									//newPartial1Element;
									{
										newPartial1Element = (partial1Element & partial1ElementUntouchedMask) | ((reversedPartial0Bits & (commonDenominatorUntranslatedBitsMask << absShiftAmount)) >>> absShiftAmount);
										
										newPartial0Element = (newPartial0ElementWithoutSpill & (~highCarryMask)) | ((reversedPartial0Bits & lowCarryMask) << elementLengthMinusShiftAmount);
									}
								}
							}
						}
					}
					
					
					x[partial0Index] = newPartial0Element;
					x[partial1Index] = newPartial1Element;
					
					//DONE! \o/ :D
				}
				
				
				
				
				else if (hasPartial0 && !hasPartial1)
				{
					//Remember, left/0 partial starts at a point and *up*, while
					//right/1 partial starts at 0 and *ends* at a point!
					final int partial0Index = bitOffset / 64;
					final int partial0StartInElement = bitOffset % 64;
					final int partial0Bitlength = 64 - partial0StartInElement;
					//final int partial1Bitlength = 0;
					final long partial0Element = x[partial0Index];
					
					final long partial0ElementUntouchedMask = (1 << partial0StartInElement) - 1;
					final long partial0ElementMask = (~partial0ElementUntouchedMask);
					
					final long partial0Bits = (partial0Element & partial0ElementMask) >>> partial0StartInElement;
					
					//Reverse regions :>
					final long reversedPartial0Bits = reverse(partial0Bits, partial0Bitlength);
					
					
					
					final long newPartial0Element;
					{
						//Not the same bitlength..shifting needed :P
						//Shift everything down!
						int shiftAmount = 0 - partial0Bitlength;
						assert shiftAmount < 0;
						final int absShiftAmount = -shiftAmount;
						
						//final int smallerPartialBitlength = 0;
						
						final long lowCarryMask = (1 << absShiftAmount) - 1;
						final int elementLengthMinusShiftAmount = 64 - absShiftAmount;
						final long highCarryMask = lowCarryMask << elementLengthMinusShiftAmount;
						
						if (wholeElementCount != 0)
						{
							//Shift the whole-words that are in between!!
							long carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
							
							for (int i = wholeElementOffset+wholeElementCount-1; i >= 0; i--)
							{
								final long nextCarry = x[i] & lowCarryMask;
								x[i] = (x[i] >>> absShiftAmount) | (carry << elementLengthMinusShiftAmount);
								carry = nextCarry;
							}
							
							//Carry it on down allll the way into the partial! ^v^
							assert partial0ElementUntouchedMask == (~highCarryMask);
							newPartial0Element = (partial0Element & partial0ElementUntouchedMask) | (carry << partial0StartInElement);
							
							
							//Mask the reversed partial into the the last whole element :>
							{
								int lastElementIndex = wholeElementOffset + wholeElementCount - 1;
								assert (reversedPartial0Bits & lowCarryMask) == reversedPartial0Bits;  //carry mask should completely cover it :>
								x[lastElementIndex] = (x[lastElementIndex] & (~highCarryMask)) | (reversedPartial0Bits << elementLengthMinusShiftAmount);
							}
						}
						else
						{
							throw new AssertionError("Should have been caught by single-element shortcuts ;_;");
						}
					}
					
					
					x[partial0Index] = newPartial0Element;
					
					//DONE! \o/ :D
				}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				else if (!hasPartial0 && hasPartial1)
				{
					//Remember, left/0 partial starts at a point and *up*, while
					//right/1 partial starts at 0 and *ends* at a point!
					final int partial1Index = (bitOffset + bitLength) / 64 + 1;
					final int partial1EndInElement = (bitOffset + bitLength) % 64;
					//final int partial0Bitlength = 0;
					final int partial1Bitlength = partial1EndInElement;
					final long partial1Element = x[partial1Index];
					
					final long partial1ElementMask = (1 << partial1EndInElement) - 1;
					final long partial1ElementUntouchedMask = (~partial1ElementMask);
					
					final long partial1Bits = ((partial1Element & partial1ElementMask));
					
					//Reverse regions :>
					final long reversedPartial1Bits = reverse(partial1Bits, partial1Bitlength);
					
					
					
					final long newPartial1Element;
					{
						//Not the same bitlength..shifting needed :P
						//Shift everything up!
						int shiftAmount = partial1Bitlength - 0;
						assert shiftAmount > 0;
						
						final long lowCarryMask = (1 << shiftAmount) - 1;
						
						if (wholeElementCount != 0)
						{
							//Shift the whole-words that are in between!!
							long carry = 0;  //don't worry about prepopulating from partial; they'll be taken care of separately ^_^
							
							final int elementLengthMinusShiftAmount = 64 - shiftAmount;
							final long highCarryMask = lowCarryMask << elementLengthMinusShiftAmount;
							
							for (int i = wholeElementOffset; i < wholeElementCount; i++)
							{
								final long nextCarry = (x[i] & highCarryMask) >>> shiftAmount;
							x[i] = (x[i] << shiftAmount) | carry;
							carry = nextCarry;
							}
							
							//Carry it on up allll the way into the partial! ^v^
							assert (~lowCarryMask) == partial1ElementUntouchedMask;
							newPartial1Element = (partial1Element & partial1ElementUntouchedMask) | carry;
							
							
							//Mask the reversed partial into the the first whole element :>
							{
								assert (reversedPartial1Bits & lowCarryMask) == reversedPartial1Bits;  //carry mask should completely cover it :>
								x[wholeElementOffset] = (x[wholeElementOffset] & (~lowCarryMask)) | reversedPartial1Bits;
							}
						}
						else
						{
							throw new AssertionError("Should have been caught by single-element shortcuts ;_;");
						}
					}
					
					
					x[partial1Index] = newPartial1Element;
					
					//DONE! \o/ :D
				}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				else //if (!hasPartial0 && !hasPartial1)
				{
					//If no partials, then whole-elements were the only thing to reverse and swap..and we already did that!  SO WE'RE DONE JUST THAT FAST!  \o/  XD  :D
				}
			}
		}
	}
	
	
	// >>>
	
	
	//Reversingssss!>
	
	
	
	
	//<Rotating shiftssss!
	//	public static void rotatingShift(@WritableValue _$$prim$$_[] x, int bitOffset, int bitLength)
	//	{
	//		if (bitLength < 0)
	//			throw new IllegalArgumentException("bitOffset: "+bitOffset+", bitLength: "+bitLength+", capacity: "+(x.length * _$$primlogilen$$_));
	//		if (bitOffset < 0 || (bitOffset+bitLength > x.length * _$$primlogilen$$_))
	//			throw new IllegalArgumentException("bitOffset: "+bitOffset+", bitLength: "+bitLength+", capacity: "+(x.length * _$$primlogilen$$_));
	//
	//		if (bitLength == 0)
	//			return;
	//
	//
	//
	//	}
	//>
	
	
	
	
	
	
	
	
	
	
	//<Bitfield type conversions! :PP
	
	/* <<<
python

codeSmallSource = """
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue _$$primA$$_[] source, int sourceOffset, @WritableValue _$$primB$$_[] dest, int destOffset, int lengthInLargerDestElements)
	{
		assert _$$primlogilenA$$_ < _$$primlogilenB$$_;
		
		final int ratio = _$$primlogilenB$$_ / _$$primlogilenA$$_;
		
		for (int i = 0; i < lengthInLargerDestElements; i++)
		{
			_$$primB$$_ d = 0;
			
			for (int e = 0; e < ratio; e++)
				d |= (source[sourceOffset+(i*ratio+e)] & ((1 << _$$primlogilenA$$_) - 1)) << (ratio*e);
			
			dest[i] = d;
		}
	}
	
""";


codeSmallDest = """
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue _$$primA$$_[] source, int sourceOffset, @WritableValue _$$primB$$_[] dest, int destOffset, int lengthInLargerSourceElements)
	{
		assert _$$primlogilenA$$_ > _$$primlogilenB$$_;
		
		final int ratio = _$$primlogilenA$$_ / _$$primlogilenB$$_;
		
		for (int i = 0; i < lengthInLargerSourceElements; i++)
		{
			_$$primA$$_ s = source[sourceOffset+i];
			
			for (int e = 0; e < ratio; e++)
				dest[i*ratio + e] = (_$$primB$$_)((s & (((1 << _$$primlogilenB$$_) - 1) << (ratio*e))) >>> (ratio*e));
		}
	}
""";


codeEqualSize = """
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue _$$primA$$_[] source, int sourceOffset, @WritableValue _$$primB$$_[] dest, int destOffset, int lengthInEqualSizeElements)
	{
		assert _$$primlogilenA$$_ == _$$primlogilenB$$_;
		
		System.arraycopy(source, sourceOffset, dest, destOffset, lengthInEqualSizeElements);
	}
""";



output = "";

intprims = ["byte", "char", "short", "int", "long"];
primlens = {"byte": 8, "char": 16, "short": 16, "int": 32, "long": 64};

for primA in intprims:
	primALen = primlens[primA];
	
	for primB in intprims:
		primBLen = primlens[primB];
		
		
		if (primALen < primBLen):
			s = codeSmallSource;
		elif (primALen > primBLen):
			s = codeSmallDest;
		else:
			s = codeEqualSize;
		
		p(s.replace("_$$primA$$_", primA).replace("_$$primB$$_", primB).replace("_$$primlogilenA$$_", str(primALen)).replace("_$$primlogilenB$$_", str(primBLen)));
	 */
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue byte[] source, int sourceOffset, @WritableValue byte[] dest, int destOffset, int lengthInEqualSizeElements)
	{
		System.arraycopy(source, sourceOffset, dest, destOffset, lengthInEqualSizeElements);
	}
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue byte[] source, int sourceOffset, @WritableValue char[] dest, int destOffset, int lengthInLargerDestElements)
	{
		assert 8 < 16;
		
		final int ratio = 16 / 8;
		
		for (int i = 0; i < lengthInLargerDestElements; i++)
		{
			char d = 0;
			
			for (int e = 0; e < ratio; e++)
				d |= (source[sourceOffset+(i*ratio+e)] & ((1 << 8) - 1)) << (ratio*e);
			
			dest[i] = d;
		}
	}
	
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue byte[] source, int sourceOffset, @WritableValue short[] dest, int destOffset, int lengthInLargerDestElements)
	{
		assert 8 < 16;
		
		final int ratio = 16 / 8;
		
		for (int i = 0; i < lengthInLargerDestElements; i++)
		{
			short d = 0;
			
			for (int e = 0; e < ratio; e++)
				d |= (source[sourceOffset+(i*ratio+e)] & ((1 << 8) - 1)) << (ratio*e);
			
			dest[i] = d;
		}
	}
	
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue byte[] source, int sourceOffset, @WritableValue int[] dest, int destOffset, int lengthInLargerDestElements)
	{
		assert 8 < 32;
		
		final int ratio = 32 / 8;
		
		for (int i = 0; i < lengthInLargerDestElements; i++)
		{
			int d = 0;
			
			for (int e = 0; e < ratio; e++)
				d |= (source[sourceOffset+(i*ratio+e)] & ((1 << 8) - 1)) << (ratio*e);
			
			dest[i] = d;
		}
	}
	
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue byte[] source, int sourceOffset, @WritableValue long[] dest, int destOffset, int lengthInLargerDestElements)
	{
		assert 8 < 64;
		
		final int ratio = 64 / 8;
		
		for (int i = 0; i < lengthInLargerDestElements; i++)
		{
			long d = 0;
			
			for (int e = 0; e < ratio; e++)
				d |= (source[sourceOffset+(i*ratio+e)] & ((1 << 8) - 1)) << (ratio*e);
			
			dest[i] = d;
		}
	}
	
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue char[] source, int sourceOffset, @WritableValue byte[] dest, int destOffset, int lengthInLargerSourceElements)
	{
		assert 16 > 8;
		
		final int ratio = 16 / 8;
		
		for (int i = 0; i < lengthInLargerSourceElements; i++)
		{
			char s = source[sourceOffset+i];
			
			for (int e = 0; e < ratio; e++)
				dest[i*ratio + e] = (byte)((s & (((1 << 8) - 1) << (ratio*e))) >>> (ratio*e));
		}
	}
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue char[] source, int sourceOffset, @WritableValue char[] dest, int destOffset, int lengthInEqualSizeElements)
	{
		System.arraycopy(source, sourceOffset, dest, destOffset, lengthInEqualSizeElements);
	}
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue char[] source, int sourceOffset, @WritableValue short[] dest, int destOffset, int lengthInEqualSizeElements)
	{
		System.arraycopy(source, sourceOffset, dest, destOffset, lengthInEqualSizeElements);
	}
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue char[] source, int sourceOffset, @WritableValue int[] dest, int destOffset, int lengthInLargerDestElements)
	{
		assert 16 < 32;
		
		final int ratio = 32 / 16;
		
		for (int i = 0; i < lengthInLargerDestElements; i++)
		{
			int d = 0;
			
			for (int e = 0; e < ratio; e++)
				d |= (source[sourceOffset+(i*ratio+e)] & ((1 << 16) - 1)) << (ratio*e);
			
			dest[i] = d;
		}
	}
	
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue char[] source, int sourceOffset, @WritableValue long[] dest, int destOffset, int lengthInLargerDestElements)
	{
		assert 16 < 64;
		
		final int ratio = 64 / 16;
		
		for (int i = 0; i < lengthInLargerDestElements; i++)
		{
			long d = 0;
			
			for (int e = 0; e < ratio; e++)
				d |= (source[sourceOffset+(i*ratio+e)] & ((1 << 16) - 1)) << (ratio*e);
			
			dest[i] = d;
		}
	}
	
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue short[] source, int sourceOffset, @WritableValue byte[] dest, int destOffset, int lengthInLargerSourceElements)
	{
		assert 16 > 8;
		
		final int ratio = 16 / 8;
		
		for (int i = 0; i < lengthInLargerSourceElements; i++)
		{
			short s = source[sourceOffset+i];
			
			for (int e = 0; e < ratio; e++)
				dest[i*ratio + e] = (byte)((s & (((1 << 8) - 1) << (ratio*e))) >>> (ratio*e));
		}
	}
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue short[] source, int sourceOffset, @WritableValue char[] dest, int destOffset, int lengthInEqualSizeElements)
	{
		System.arraycopy(source, sourceOffset, dest, destOffset, lengthInEqualSizeElements);
	}
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue short[] source, int sourceOffset, @WritableValue short[] dest, int destOffset, int lengthInEqualSizeElements)
	{
		System.arraycopy(source, sourceOffset, dest, destOffset, lengthInEqualSizeElements);
	}
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue short[] source, int sourceOffset, @WritableValue int[] dest, int destOffset, int lengthInLargerDestElements)
	{
		assert 16 < 32;
		
		final int ratio = 32 / 16;
		
		for (int i = 0; i < lengthInLargerDestElements; i++)
		{
			int d = 0;
			
			for (int e = 0; e < ratio; e++)
				d |= (source[sourceOffset+(i*ratio+e)] & ((1 << 16) - 1)) << (ratio*e);
			
			dest[i] = d;
		}
	}
	
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue short[] source, int sourceOffset, @WritableValue long[] dest, int destOffset, int lengthInLargerDestElements)
	{
		assert 16 < 64;
		
		final int ratio = 64 / 16;
		
		for (int i = 0; i < lengthInLargerDestElements; i++)
		{
			long d = 0;
			
			for (int e = 0; e < ratio; e++)
				d |= (source[sourceOffset+(i*ratio+e)] & ((1 << 16) - 1)) << (ratio*e);
			
			dest[i] = d;
		}
	}
	
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue int[] source, int sourceOffset, @WritableValue byte[] dest, int destOffset, int lengthInLargerSourceElements)
	{
		assert 32 > 8;
		
		final int ratio = 32 / 8;
		
		for (int i = 0; i < lengthInLargerSourceElements; i++)
		{
			int s = source[sourceOffset+i];
			
			for (int e = 0; e < ratio; e++)
				dest[i*ratio + e] = (byte)((s & (((1 << 8) - 1) << (ratio*e))) >>> (ratio*e));
		}
	}
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue int[] source, int sourceOffset, @WritableValue char[] dest, int destOffset, int lengthInLargerSourceElements)
	{
		assert 32 > 16;
		
		final int ratio = 32 / 16;
		
		for (int i = 0; i < lengthInLargerSourceElements; i++)
		{
			int s = source[sourceOffset+i];
			
			for (int e = 0; e < ratio; e++)
				dest[i*ratio + e] = (char)((s & (((1 << 16) - 1) << (ratio*e))) >>> (ratio*e));
		}
	}
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue int[] source, int sourceOffset, @WritableValue short[] dest, int destOffset, int lengthInLargerSourceElements)
	{
		assert 32 > 16;
		
		final int ratio = 32 / 16;
		
		for (int i = 0; i < lengthInLargerSourceElements; i++)
		{
			int s = source[sourceOffset+i];
			
			for (int e = 0; e < ratio; e++)
				dest[i*ratio + e] = (short)((s & (((1 << 16) - 1) << (ratio*e))) >>> (ratio*e));
		}
	}
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue int[] source, int sourceOffset, @WritableValue int[] dest, int destOffset, int lengthInEqualSizeElements)
	{
		System.arraycopy(source, sourceOffset, dest, destOffset, lengthInEqualSizeElements);
	}
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue int[] source, int sourceOffset, @WritableValue long[] dest, int destOffset, int lengthInLargerDestElements)
	{
		assert 32 < 64;
		
		final int ratio = 64 / 32;
		
		for (int i = 0; i < lengthInLargerDestElements; i++)
		{
			long d = 0;
			
			for (int e = 0; e < ratio; e++)
				d |= (source[sourceOffset+(i*ratio+e)] & ((1 << 32) - 1)) << (ratio*e);
			
			dest[i] = d;
		}
	}
	
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue long[] source, int sourceOffset, @WritableValue byte[] dest, int destOffset, int lengthInLargerSourceElements)
	{
		assert 64 > 8;
		
		final int ratio = 64 / 8;
		
		for (int i = 0; i < lengthInLargerSourceElements; i++)
		{
			long s = source[sourceOffset+i];
			
			for (int e = 0; e < ratio; e++)
				dest[i*ratio + e] = (byte)((s & (((1 << 8) - 1) << (ratio*e))) >>> (ratio*e));
		}
	}
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue long[] source, int sourceOffset, @WritableValue char[] dest, int destOffset, int lengthInLargerSourceElements)
	{
		assert 64 > 16;
		
		final int ratio = 64 / 16;
		
		for (int i = 0; i < lengthInLargerSourceElements; i++)
		{
			long s = source[sourceOffset+i];
			
			for (int e = 0; e < ratio; e++)
				dest[i*ratio + e] = (char)((s & (((1 << 16) - 1) << (ratio*e))) >>> (ratio*e));
		}
	}
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue long[] source, int sourceOffset, @WritableValue short[] dest, int destOffset, int lengthInLargerSourceElements)
	{
		assert 64 > 16;
		
		final int ratio = 64 / 16;
		
		for (int i = 0; i < lengthInLargerSourceElements; i++)
		{
			long s = source[sourceOffset+i];
			
			for (int e = 0; e < ratio; e++)
				dest[i*ratio + e] = (short)((s & (((1 << 16) - 1) << (ratio*e))) >>> (ratio*e));
		}
	}
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue long[] source, int sourceOffset, @WritableValue int[] dest, int destOffset, int lengthInLargerSourceElements)
	{
		assert 64 > 32;
		
		final int ratio = 64 / 32;
		
		for (int i = 0; i < lengthInLargerSourceElements; i++)
		{
			long s = source[sourceOffset+i];
			
			for (int e = 0; e < ratio; e++)
				dest[i*ratio + e] = (int)((s & (((1 << 32) - 1) << (ratio*e))) >>> (ratio*e));
		}
	}
	
	public static void copyExactlyBetweenJavaBitfieldArrays(@ReadonlyValue long[] source, int sourceOffset, @WritableValue long[] dest, int destOffset, int lengthInEqualSizeElements)
	{
		System.arraycopy(source, sourceOffset, dest, destOffset, lengthInEqualSizeElements);
	}
	
	// >>>
	//Bitfield type conversions! :PP >
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static boolean[] toBooleanArrayFrom8(byte bits32, int numBits)
	{
		if (numBits < 0) throw new OverflowException();
		if (numBits > 8) throw new OverflowException();
		
		boolean[] bits = new boolean[numBits];
		for (int i = 0; i < numBits; i++)
			bits[i] = (bits32 & (1 << i)) != 0;
		return bits;
	}
	
	public static boolean[] toBooleanArrayFrom16(short bits32, int numBits)
	{
		if (numBits < 0) throw new OverflowException();
		if (numBits > 16) throw new OverflowException();
		
		boolean[] bits = new boolean[numBits];
		for (int i = 0; i < numBits; i++)
			bits[i] = (bits32 & (1 << i)) != 0;
		return bits;
	}
	
	public static boolean[] toBooleanArrayFrom32(int bits32, int numBits)
	{
		if (numBits < 0) throw new OverflowException();
		if (numBits > 32) throw new OverflowException();
		
		boolean[] bits = new boolean[numBits];
		for (int i = 0; i < numBits; i++)
			bits[i] = (bits32 & (1 << i)) != 0;
		return bits;
	}
	
	public static boolean[] toBooleanArrayFrom64(long bits64, int numBits)
	{
		if (numBits < 0) throw new OverflowException();
		if (numBits > 64) throw new OverflowException();
		
		boolean[] bits = new boolean[numBits];
		for (int i = 0; i < numBits; i++)
			bits[i] = (bits64 & (1l << i)) != 0;
		return bits;
	}
	
	
	
	
	public static boolean[] toBooleanArray8(byte bits8)
	{
		return toBooleanArrayFrom64(bits8, 8);
	}
	
	public static boolean[] toBooleanArray16(short bits16)
	{
		return toBooleanArrayFrom64(bits16, 16);
	}
	
	public static boolean[] toBooleanArray32(int bits32)
	{
		return toBooleanArrayFrom64(bits32, 32);
	}
	
	public static boolean[] toBooleanArray64(long bits64)
	{
		return toBooleanArrayFrom64(bits64, 64);
	}
}
