package rebound.bits;

import static rebound.util.Primitives.*;
import rebound.annotations.semantic.simpledata.ActuallySigned;
import rebound.annotations.semantic.simpledata.ActuallyUnsigned;
import rebound.exceptions.OverflowException;

//TODO TESTTTTTTTTT
// Especially the @ActuallyUnsigned ones that reinterpret Java primitives *as if they were* signed!!

public class BitfieldSafeCasts
{
	/* <<<
primxp
_$$primxpconf:byte,short,int,long,sint24,slong40,slong48,slong56,uint24,ulong40,ulong48,ulong56,ubyte,ushort,uint$$_
	public static _$$@litprim$$_ safeCastU8to_$$PrimShort$$_(@ActuallyUnsigned byte input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < _$$PrimShort$$__MIN_VALUE || u > _$$PrimShort$$__MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (_$$litprim$$_)input;
	}
	 */
	
	public static byte safeCastU8toS8(@ActuallyUnsigned byte input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S8_MIN_VALUE || u > S8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static short safeCastU8toS16(@ActuallyUnsigned byte input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S16_MIN_VALUE || u > S16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static int safeCastU8toS32(@ActuallyUnsigned byte input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S32_MIN_VALUE || u > S32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static long safeCastU8toS64(@ActuallyUnsigned byte input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S64_MIN_VALUE || u > S64_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(24) int safeCastU8toS24(@ActuallyUnsigned byte input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S24_MIN_VALUE || u > S24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallySigned(40) long safeCastU8toS40(@ActuallyUnsigned byte input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S40_MIN_VALUE || u > S40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(48) long safeCastU8toS48(@ActuallyUnsigned byte input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S48_MIN_VALUE || u > S48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(56) long safeCastU8toS56(@ActuallyUnsigned byte input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S56_MIN_VALUE || u > S56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(24) int safeCastU8toU24(@ActuallyUnsigned byte input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U24_MIN_VALUE || u > U24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallyUnsigned(40) long safeCastU8toU40(@ActuallyUnsigned byte input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U40_MIN_VALUE || u > U40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(48) long safeCastU8toU48(@ActuallyUnsigned byte input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U48_MIN_VALUE || u > U48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(56) long safeCastU8toU56(@ActuallyUnsigned byte input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U56_MIN_VALUE || u > U56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned byte safeCastU8toU8(@ActuallyUnsigned byte input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U8_MIN_VALUE || u > U8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static @ActuallyUnsigned short safeCastU8toU16(@ActuallyUnsigned byte input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U16_MIN_VALUE || u > U16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static @ActuallyUnsigned int safeCastU8toU32(@ActuallyUnsigned byte input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U32_MIN_VALUE || u > U32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	// >>>
	
	
	/* <<<
primxp
_$$primxpconf:byte,short,int,long,sint24,slong40,slong48,slong56,uint24,ulong40,ulong48,ulong56,ubyte,ushort,uint$$_
	public static _$$@litprim$$_ safeCastS8to_$$PrimShort$$_(byte input) throws OverflowException
	{
		long u = input;
		if (u < _$$PrimShort$$__MIN_VALUE || u > _$$PrimShort$$__MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (_$$litprim$$_)input;
	}
	 */
	
	public static byte safeCastS8toS8(byte input) throws OverflowException
	{
		long u = input;
		if (u < S8_MIN_VALUE || u > S8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static short safeCastS8toS16(byte input) throws OverflowException
	{
		long u = input;
		if (u < S16_MIN_VALUE || u > S16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static int safeCastS8toS32(byte input) throws OverflowException
	{
		long u = input;
		if (u < S32_MIN_VALUE || u > S32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static long safeCastS8toS64(byte input) throws OverflowException
	{
		long u = input;
		if (u < S64_MIN_VALUE || u > S64_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(24) int safeCastS8toS24(byte input) throws OverflowException
	{
		long u = input;
		if (u < S24_MIN_VALUE || u > S24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallySigned(40) long safeCastS8toS40(byte input) throws OverflowException
	{
		long u = input;
		if (u < S40_MIN_VALUE || u > S40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(48) long safeCastS8toS48(byte input) throws OverflowException
	{
		long u = input;
		if (u < S48_MIN_VALUE || u > S48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(56) long safeCastS8toS56(byte input) throws OverflowException
	{
		long u = input;
		if (u < S56_MIN_VALUE || u > S56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(24) int safeCastS8toU24(byte input) throws OverflowException
	{
		long u = input;
		if (u < U24_MIN_VALUE || u > U24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallyUnsigned(40) long safeCastS8toU40(byte input) throws OverflowException
	{
		long u = input;
		if (u < U40_MIN_VALUE || u > U40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(48) long safeCastS8toU48(byte input) throws OverflowException
	{
		long u = input;
		if (u < U48_MIN_VALUE || u > U48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(56) long safeCastS8toU56(byte input) throws OverflowException
	{
		long u = input;
		if (u < U56_MIN_VALUE || u > U56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned byte safeCastS8toU8(byte input) throws OverflowException
	{
		long u = input;
		if (u < U8_MIN_VALUE || u > U8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static @ActuallyUnsigned short safeCastS8toU16(byte input) throws OverflowException
	{
		long u = input;
		if (u < U16_MIN_VALUE || u > U16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static @ActuallyUnsigned int safeCastS8toU32(byte input) throws OverflowException
	{
		long u = input;
		if (u < U32_MIN_VALUE || u > U32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	// >>>
	
	
	/* <<<
primxp
_$$primxpconf:byte,short,int,long,sint24,slong40,slong48,slong56,uint24,ulong40,ulong48,ulong56,ubyte,ushort,uint$$_
	public static _$$@litprim$$_ safeCastU16to_$$PrimShort$$_(@ActuallyUnsigned short input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < _$$PrimShort$$__MIN_VALUE || u > _$$PrimShort$$__MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (_$$litprim$$_)input;
	}
	 */
	
	public static byte safeCastU16toS8(@ActuallyUnsigned short input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S8_MIN_VALUE || u > S8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static short safeCastU16toS16(@ActuallyUnsigned short input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S16_MIN_VALUE || u > S16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static int safeCastU16toS32(@ActuallyUnsigned short input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S32_MIN_VALUE || u > S32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static long safeCastU16toS64(@ActuallyUnsigned short input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S64_MIN_VALUE || u > S64_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(24) int safeCastU16toS24(@ActuallyUnsigned short input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S24_MIN_VALUE || u > S24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallySigned(40) long safeCastU16toS40(@ActuallyUnsigned short input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S40_MIN_VALUE || u > S40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(48) long safeCastU16toS48(@ActuallyUnsigned short input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S48_MIN_VALUE || u > S48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(56) long safeCastU16toS56(@ActuallyUnsigned short input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S56_MIN_VALUE || u > S56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(24) int safeCastU16toU24(@ActuallyUnsigned short input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U24_MIN_VALUE || u > U24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallyUnsigned(40) long safeCastU16toU40(@ActuallyUnsigned short input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U40_MIN_VALUE || u > U40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(48) long safeCastU16toU48(@ActuallyUnsigned short input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U48_MIN_VALUE || u > U48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(56) long safeCastU16toU56(@ActuallyUnsigned short input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U56_MIN_VALUE || u > U56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned byte safeCastU16toU8(@ActuallyUnsigned short input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U8_MIN_VALUE || u > U8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static @ActuallyUnsigned short safeCastU16toU16(@ActuallyUnsigned short input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U16_MIN_VALUE || u > U16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static @ActuallyUnsigned int safeCastU16toU32(@ActuallyUnsigned short input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U32_MIN_VALUE || u > U32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	// >>>
	
	
	/* <<<
primxp
_$$primxpconf:byte,short,int,long,sint24,slong40,slong48,slong56,uint24,ulong40,ulong48,ulong56,ubyte,ushort,uint$$_
	public static _$$@litprim$$_ safeCastS16to_$$PrimShort$$_(short input) throws OverflowException
	{
		long u = input;
		if (u < _$$PrimShort$$__MIN_VALUE || u > _$$PrimShort$$__MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (_$$litprim$$_)input;
	}
	 */
	
	public static byte safeCastS16toS8(short input) throws OverflowException
	{
		long u = input;
		if (u < S8_MIN_VALUE || u > S8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static short safeCastS16toS16(short input) throws OverflowException
	{
		long u = input;
		if (u < S16_MIN_VALUE || u > S16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static int safeCastS16toS32(short input) throws OverflowException
	{
		long u = input;
		if (u < S32_MIN_VALUE || u > S32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static long safeCastS16toS64(short input) throws OverflowException
	{
		long u = input;
		if (u < S64_MIN_VALUE || u > S64_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(24) int safeCastS16toS24(short input) throws OverflowException
	{
		long u = input;
		if (u < S24_MIN_VALUE || u > S24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallySigned(40) long safeCastS16toS40(short input) throws OverflowException
	{
		long u = input;
		if (u < S40_MIN_VALUE || u > S40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(48) long safeCastS16toS48(short input) throws OverflowException
	{
		long u = input;
		if (u < S48_MIN_VALUE || u > S48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(56) long safeCastS16toS56(short input) throws OverflowException
	{
		long u = input;
		if (u < S56_MIN_VALUE || u > S56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(24) int safeCastS16toU24(short input) throws OverflowException
	{
		long u = input;
		if (u < U24_MIN_VALUE || u > U24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallyUnsigned(40) long safeCastS16toU40(short input) throws OverflowException
	{
		long u = input;
		if (u < U40_MIN_VALUE || u > U40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(48) long safeCastS16toU48(short input) throws OverflowException
	{
		long u = input;
		if (u < U48_MIN_VALUE || u > U48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(56) long safeCastS16toU56(short input) throws OverflowException
	{
		long u = input;
		if (u < U56_MIN_VALUE || u > U56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned byte safeCastS16toU8(short input) throws OverflowException
	{
		long u = input;
		if (u < U8_MIN_VALUE || u > U8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static @ActuallyUnsigned short safeCastS16toU16(short input) throws OverflowException
	{
		long u = input;
		if (u < U16_MIN_VALUE || u > U16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static @ActuallyUnsigned int safeCastS16toU32(short input) throws OverflowException
	{
		long u = input;
		if (u < U32_MIN_VALUE || u > U32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	// >>>
	
	
	/* <<<
primxp
_$$primxpconf:byte,short,int,long,sint24,slong40,slong48,slong56,uint24,ulong40,ulong48,ulong56,ubyte,ushort,uint$$_
	public static _$$@litprim$$_ safeCastU24to_$$PrimShort$$_(@ActuallyUnsigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < _$$PrimShort$$__MIN_VALUE || u > _$$PrimShort$$__MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (_$$litprim$$_)input;
	}
	 */
	
	public static byte safeCastU24toS8(@ActuallyUnsigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < S8_MIN_VALUE || u > S8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static short safeCastU24toS16(@ActuallyUnsigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < S16_MIN_VALUE || u > S16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static int safeCastU24toS32(@ActuallyUnsigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < S32_MIN_VALUE || u > S32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static long safeCastU24toS64(@ActuallyUnsigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < S64_MIN_VALUE || u > S64_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(24) int safeCastU24toS24(@ActuallyUnsigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < S24_MIN_VALUE || u > S24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallySigned(40) long safeCastU24toS40(@ActuallyUnsigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < S40_MIN_VALUE || u > S40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(48) long safeCastU24toS48(@ActuallyUnsigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < S48_MIN_VALUE || u > S48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(56) long safeCastU24toS56(@ActuallyUnsigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < S56_MIN_VALUE || u > S56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(24) int safeCastU24toU24(@ActuallyUnsigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < U24_MIN_VALUE || u > U24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallyUnsigned(40) long safeCastU24toU40(@ActuallyUnsigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < U40_MIN_VALUE || u > U40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(48) long safeCastU24toU48(@ActuallyUnsigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < U48_MIN_VALUE || u > U48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(56) long safeCastU24toU56(@ActuallyUnsigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < U56_MIN_VALUE || u > U56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned byte safeCastU24toU8(@ActuallyUnsigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < U8_MIN_VALUE || u > U8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static @ActuallyUnsigned short safeCastU24toU16(@ActuallyUnsigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < U16_MIN_VALUE || u > U16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static @ActuallyUnsigned int safeCastU24toU32(@ActuallyUnsigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < U32_MIN_VALUE || u > U32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	// >>>
	
	
	/* <<<
primxp
_$$primxpconf:byte,short,int,long,sint24,slong40,slong48,slong56,uint24,ulong40,ulong48,ulong56,ubyte,ushort,uint$$_
	public static _$$@litprim$$_ safeCastS24to_$$PrimShort$$_(@ActuallySigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < _$$PrimShort$$__MIN_VALUE || u > _$$PrimShort$$__MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (_$$litprim$$_)input;
	}
	 */
	
	public static byte safeCastS24toS8(@ActuallySigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < S8_MIN_VALUE || u > S8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static short safeCastS24toS16(@ActuallySigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < S16_MIN_VALUE || u > S16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static int safeCastS24toS32(@ActuallySigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < S32_MIN_VALUE || u > S32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static long safeCastS24toS64(@ActuallySigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < S64_MIN_VALUE || u > S64_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(24) int safeCastS24toS24(@ActuallySigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < S24_MIN_VALUE || u > S24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallySigned(40) long safeCastS24toS40(@ActuallySigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < S40_MIN_VALUE || u > S40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(48) long safeCastS24toS48(@ActuallySigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < S48_MIN_VALUE || u > S48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(56) long safeCastS24toS56(@ActuallySigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < S56_MIN_VALUE || u > S56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(24) int safeCastS24toU24(@ActuallySigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < U24_MIN_VALUE || u > U24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallyUnsigned(40) long safeCastS24toU40(@ActuallySigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < U40_MIN_VALUE || u > U40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(48) long safeCastS24toU48(@ActuallySigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < U48_MIN_VALUE || u > U48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(56) long safeCastS24toU56(@ActuallySigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < U56_MIN_VALUE || u > U56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned byte safeCastS24toU8(@ActuallySigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < U8_MIN_VALUE || u > U8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static @ActuallyUnsigned short safeCastS24toU16(@ActuallySigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < U16_MIN_VALUE || u > U16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static @ActuallyUnsigned int safeCastS24toU32(@ActuallySigned(24) int input) throws OverflowException
	{
		long u = input;
		if (u < U32_MIN_VALUE || u > U32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	// >>>
	
	
	/* <<<
primxp
_$$primxpconf:byte,short,int,long,sint24,slong40,slong48,slong56,uint24,ulong40,ulong48,ulong56,ubyte,ushort,uint$$_
	public static _$$@litprim$$_ safeCastU32to_$$PrimShort$$_(@ActuallyUnsigned int input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < _$$PrimShort$$__MIN_VALUE || u > _$$PrimShort$$__MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (_$$litprim$$_)input;
	}
	 */
	
	public static byte safeCastU32toS8(@ActuallyUnsigned int input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S8_MIN_VALUE || u > S8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static short safeCastU32toS16(@ActuallyUnsigned int input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S16_MIN_VALUE || u > S16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static int safeCastU32toS32(@ActuallyUnsigned int input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S32_MIN_VALUE || u > S32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static long safeCastU32toS64(@ActuallyUnsigned int input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S64_MIN_VALUE || u > S64_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(24) int safeCastU32toS24(@ActuallyUnsigned int input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S24_MIN_VALUE || u > S24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallySigned(40) long safeCastU32toS40(@ActuallyUnsigned int input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S40_MIN_VALUE || u > S40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(48) long safeCastU32toS48(@ActuallyUnsigned int input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S48_MIN_VALUE || u > S48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(56) long safeCastU32toS56(@ActuallyUnsigned int input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < S56_MIN_VALUE || u > S56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(24) int safeCastU32toU24(@ActuallyUnsigned int input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U24_MIN_VALUE || u > U24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallyUnsigned(40) long safeCastU32toU40(@ActuallyUnsigned int input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U40_MIN_VALUE || u > U40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(48) long safeCastU32toU48(@ActuallyUnsigned int input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U48_MIN_VALUE || u > U48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(56) long safeCastU32toU56(@ActuallyUnsigned int input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U56_MIN_VALUE || u > U56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned byte safeCastU32toU8(@ActuallyUnsigned int input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U8_MIN_VALUE || u > U8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static @ActuallyUnsigned short safeCastU32toU16(@ActuallyUnsigned int input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U16_MIN_VALUE || u > U16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static @ActuallyUnsigned int safeCastU32toU32(@ActuallyUnsigned int input) throws OverflowException
	{
		long u = Unsigned.upcastTo64(input);
		if (u < U32_MIN_VALUE || u > U32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	// >>>
	
	
	/* <<<
primxp
_$$primxpconf:byte,short,int,long,sint24,slong40,slong48,slong56,uint24,ulong40,ulong48,ulong56,ubyte,ushort,uint$$_
	public static _$$@litprim$$_ safeCastS32to_$$PrimShort$$_(int input) throws OverflowException
	{
		long u = input;
		if (u < _$$PrimShort$$__MIN_VALUE || u > _$$PrimShort$$__MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (_$$litprim$$_)input;
	}
	 */
	
	public static byte safeCastS32toS8(int input) throws OverflowException
	{
		long u = input;
		if (u < S8_MIN_VALUE || u > S8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static short safeCastS32toS16(int input) throws OverflowException
	{
		long u = input;
		if (u < S16_MIN_VALUE || u > S16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static int safeCastS32toS32(int input) throws OverflowException
	{
		long u = input;
		if (u < S32_MIN_VALUE || u > S32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static long safeCastS32toS64(int input) throws OverflowException
	{
		long u = input;
		if (u < S64_MIN_VALUE || u > S64_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(24) int safeCastS32toS24(int input) throws OverflowException
	{
		long u = input;
		if (u < S24_MIN_VALUE || u > S24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallySigned(40) long safeCastS32toS40(int input) throws OverflowException
	{
		long u = input;
		if (u < S40_MIN_VALUE || u > S40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(48) long safeCastS32toS48(int input) throws OverflowException
	{
		long u = input;
		if (u < S48_MIN_VALUE || u > S48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(56) long safeCastS32toS56(int input) throws OverflowException
	{
		long u = input;
		if (u < S56_MIN_VALUE || u > S56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(24) int safeCastS32toU24(int input) throws OverflowException
	{
		long u = input;
		if (u < U24_MIN_VALUE || u > U24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallyUnsigned(40) long safeCastS32toU40(int input) throws OverflowException
	{
		long u = input;
		if (u < U40_MIN_VALUE || u > U40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(48) long safeCastS32toU48(int input) throws OverflowException
	{
		long u = input;
		if (u < U48_MIN_VALUE || u > U48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(56) long safeCastS32toU56(int input) throws OverflowException
	{
		long u = input;
		if (u < U56_MIN_VALUE || u > U56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned byte safeCastS32toU8(int input) throws OverflowException
	{
		long u = input;
		if (u < U8_MIN_VALUE || u > U8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static @ActuallyUnsigned short safeCastS32toU16(int input) throws OverflowException
	{
		long u = input;
		if (u < U16_MIN_VALUE || u > U16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static @ActuallyUnsigned int safeCastS32toU32(int input) throws OverflowException
	{
		long u = input;
		if (u < U32_MIN_VALUE || u > U32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	// >>>
	
	
	
	
	
	
	
	
	
	/* <<<
primxp
_$$primxpconf:byte,short,int,long,sint24,slong40,slong48,slong56,uint24,ulong40,ulong48,ulong56,ubyte,ushort,uint$$_
	public static _$$@litprim$$_ safeCastU40to_$$PrimShort$$_(@ActuallyUnsigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < _$$PrimShort$$__MIN_VALUE || u > _$$PrimShort$$__MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (_$$litprim$$_)input;
	}
	 */
	
	public static byte safeCastU40toS8(@ActuallyUnsigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < S8_MIN_VALUE || u > S8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static short safeCastU40toS16(@ActuallyUnsigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < S16_MIN_VALUE || u > S16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static int safeCastU40toS32(@ActuallyUnsigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < S32_MIN_VALUE || u > S32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static long safeCastU40toS64(@ActuallyUnsigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < S64_MIN_VALUE || u > S64_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(24) int safeCastU40toS24(@ActuallyUnsigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < S24_MIN_VALUE || u > S24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallySigned(40) long safeCastU40toS40(@ActuallyUnsigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < S40_MIN_VALUE || u > S40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(48) long safeCastU40toS48(@ActuallyUnsigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < S48_MIN_VALUE || u > S48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(56) long safeCastU40toS56(@ActuallyUnsigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < S56_MIN_VALUE || u > S56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(24) int safeCastU40toU24(@ActuallyUnsigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < U24_MIN_VALUE || u > U24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallyUnsigned(40) long safeCastU40toU40(@ActuallyUnsigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < U40_MIN_VALUE || u > U40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(48) long safeCastU40toU48(@ActuallyUnsigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < U48_MIN_VALUE || u > U48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(56) long safeCastU40toU56(@ActuallyUnsigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < U56_MIN_VALUE || u > U56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned byte safeCastU40toU8(@ActuallyUnsigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < U8_MIN_VALUE || u > U8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static @ActuallyUnsigned short safeCastU40toU16(@ActuallyUnsigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < U16_MIN_VALUE || u > U16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static @ActuallyUnsigned int safeCastU40toU32(@ActuallyUnsigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < U32_MIN_VALUE || u > U32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	// >>>
	
	
	/* <<<
primxp
_$$primxpconf:byte,short,int,long,sint24,slong40,slong48,slong56,uint24,ulong40,ulong48,ulong56,ubyte,ushort,uint$$_
	public static _$$@litprim$$_ safeCastS40to_$$PrimShort$$_(@ActuallySigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < _$$PrimShort$$__MIN_VALUE || u > _$$PrimShort$$__MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (_$$litprim$$_)input;
	}
	 */
	
	public static byte safeCastS40toS8(@ActuallySigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < S8_MIN_VALUE || u > S8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static short safeCastS40toS16(@ActuallySigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < S16_MIN_VALUE || u > S16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static int safeCastS40toS32(@ActuallySigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < S32_MIN_VALUE || u > S32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static long safeCastS40toS64(@ActuallySigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < S64_MIN_VALUE || u > S64_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(24) int safeCastS40toS24(@ActuallySigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < S24_MIN_VALUE || u > S24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallySigned(40) long safeCastS40toS40(@ActuallySigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < S40_MIN_VALUE || u > S40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(48) long safeCastS40toS48(@ActuallySigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < S48_MIN_VALUE || u > S48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(56) long safeCastS40toS56(@ActuallySigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < S56_MIN_VALUE || u > S56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(24) int safeCastS40toU24(@ActuallySigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < U24_MIN_VALUE || u > U24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallyUnsigned(40) long safeCastS40toU40(@ActuallySigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < U40_MIN_VALUE || u > U40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(48) long safeCastS40toU48(@ActuallySigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < U48_MIN_VALUE || u > U48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(56) long safeCastS40toU56(@ActuallySigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < U56_MIN_VALUE || u > U56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned byte safeCastS40toU8(@ActuallySigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < U8_MIN_VALUE || u > U8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static @ActuallyUnsigned short safeCastS40toU16(@ActuallySigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < U16_MIN_VALUE || u > U16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static @ActuallyUnsigned int safeCastS40toU32(@ActuallySigned(40) long input) throws OverflowException
	{
		long u = input;
		if (u < U32_MIN_VALUE || u > U32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	// >>>
	
	
	/* <<<
primxp
_$$primxpconf:byte,short,int,long,sint24,slong40,slong48,slong56,uint24,ulong40,ulong48,ulong56,ubyte,ushort,uint$$_
	public static _$$@litprim$$_ safeCastU48to_$$PrimShort$$_(@ActuallyUnsigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < _$$PrimShort$$__MIN_VALUE || u > _$$PrimShort$$__MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (_$$litprim$$_)input;
	}
	 */
	
	public static byte safeCastU48toS8(@ActuallyUnsigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < S8_MIN_VALUE || u > S8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static short safeCastU48toS16(@ActuallyUnsigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < S16_MIN_VALUE || u > S16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static int safeCastU48toS32(@ActuallyUnsigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < S32_MIN_VALUE || u > S32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static long safeCastU48toS64(@ActuallyUnsigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < S64_MIN_VALUE || u > S64_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(24) int safeCastU48toS24(@ActuallyUnsigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < S24_MIN_VALUE || u > S24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallySigned(40) long safeCastU48toS40(@ActuallyUnsigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < S40_MIN_VALUE || u > S40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(48) long safeCastU48toS48(@ActuallyUnsigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < S48_MIN_VALUE || u > S48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(56) long safeCastU48toS56(@ActuallyUnsigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < S56_MIN_VALUE || u > S56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(24) int safeCastU48toU24(@ActuallyUnsigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < U24_MIN_VALUE || u > U24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallyUnsigned(40) long safeCastU48toU40(@ActuallyUnsigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < U40_MIN_VALUE || u > U40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(48) long safeCastU48toU48(@ActuallyUnsigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < U48_MIN_VALUE || u > U48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(56) long safeCastU48toU56(@ActuallyUnsigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < U56_MIN_VALUE || u > U56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned byte safeCastU48toU8(@ActuallyUnsigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < U8_MIN_VALUE || u > U8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static @ActuallyUnsigned short safeCastU48toU16(@ActuallyUnsigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < U16_MIN_VALUE || u > U16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static @ActuallyUnsigned int safeCastU48toU32(@ActuallyUnsigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < U32_MIN_VALUE || u > U32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	// >>>
	
	
	/* <<<
primxp
_$$primxpconf:byte,short,int,long,sint24,slong40,slong48,slong56,uint24,ulong40,ulong48,ulong56,ubyte,ushort,uint$$_
	public static _$$@litprim$$_ safeCastS48to_$$PrimShort$$_(@ActuallySigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < _$$PrimShort$$__MIN_VALUE || u > _$$PrimShort$$__MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (_$$litprim$$_)input;
	}
	 */
	
	public static byte safeCastS48toS8(@ActuallySigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < S8_MIN_VALUE || u > S8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static short safeCastS48toS16(@ActuallySigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < S16_MIN_VALUE || u > S16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static int safeCastS48toS32(@ActuallySigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < S32_MIN_VALUE || u > S32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static long safeCastS48toS64(@ActuallySigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < S64_MIN_VALUE || u > S64_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(24) int safeCastS48toS24(@ActuallySigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < S24_MIN_VALUE || u > S24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallySigned(40) long safeCastS48toS40(@ActuallySigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < S40_MIN_VALUE || u > S40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(48) long safeCastS48toS48(@ActuallySigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < S48_MIN_VALUE || u > S48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(56) long safeCastS48toS56(@ActuallySigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < S56_MIN_VALUE || u > S56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(24) int safeCastS48toU24(@ActuallySigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < U24_MIN_VALUE || u > U24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallyUnsigned(40) long safeCastS48toU40(@ActuallySigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < U40_MIN_VALUE || u > U40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(48) long safeCastS48toU48(@ActuallySigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < U48_MIN_VALUE || u > U48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(56) long safeCastS48toU56(@ActuallySigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < U56_MIN_VALUE || u > U56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned byte safeCastS48toU8(@ActuallySigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < U8_MIN_VALUE || u > U8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static @ActuallyUnsigned short safeCastS48toU16(@ActuallySigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < U16_MIN_VALUE || u > U16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static @ActuallyUnsigned int safeCastS48toU32(@ActuallySigned(48) long input) throws OverflowException
	{
		long u = input;
		if (u < U32_MIN_VALUE || u > U32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	// >>>
	
	
	/* <<<
primxp
_$$primxpconf:byte,short,int,long,sint24,slong40,slong48,slong56,uint24,ulong40,ulong48,ulong56,ubyte,ushort,uint$$_
	public static _$$@litprim$$_ safeCastU56to_$$PrimShort$$_(@ActuallyUnsigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < _$$PrimShort$$__MIN_VALUE || u > _$$PrimShort$$__MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (_$$litprim$$_)input;
	}
	 */
	
	public static byte safeCastU56toS8(@ActuallyUnsigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < S8_MIN_VALUE || u > S8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static short safeCastU56toS16(@ActuallyUnsigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < S16_MIN_VALUE || u > S16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static int safeCastU56toS32(@ActuallyUnsigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < S32_MIN_VALUE || u > S32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static long safeCastU56toS64(@ActuallyUnsigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < S64_MIN_VALUE || u > S64_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(24) int safeCastU56toS24(@ActuallyUnsigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < S24_MIN_VALUE || u > S24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallySigned(40) long safeCastU56toS40(@ActuallyUnsigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < S40_MIN_VALUE || u > S40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(48) long safeCastU56toS48(@ActuallyUnsigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < S48_MIN_VALUE || u > S48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(56) long safeCastU56toS56(@ActuallyUnsigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < S56_MIN_VALUE || u > S56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(24) int safeCastU56toU24(@ActuallyUnsigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < U24_MIN_VALUE || u > U24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallyUnsigned(40) long safeCastU56toU40(@ActuallyUnsigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < U40_MIN_VALUE || u > U40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(48) long safeCastU56toU48(@ActuallyUnsigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < U48_MIN_VALUE || u > U48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(56) long safeCastU56toU56(@ActuallyUnsigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < U56_MIN_VALUE || u > U56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned byte safeCastU56toU8(@ActuallyUnsigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < U8_MIN_VALUE || u > U8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static @ActuallyUnsigned short safeCastU56toU16(@ActuallyUnsigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < U16_MIN_VALUE || u > U16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static @ActuallyUnsigned int safeCastU56toU32(@ActuallyUnsigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < U32_MIN_VALUE || u > U32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	// >>>
	
	
	/* <<<
primxp
_$$primxpconf:byte,short,int,long,sint24,slong40,slong48,slong56,uint24,ulong40,ulong48,ulong56,ubyte,ushort,uint$$_
	public static _$$@litprim$$_ safeCastS56to_$$PrimShort$$_(@ActuallySigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < _$$PrimShort$$__MIN_VALUE || u > _$$PrimShort$$__MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (_$$litprim$$_)input;
	}
	 */
	
	public static byte safeCastS56toS8(@ActuallySigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < S8_MIN_VALUE || u > S8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static short safeCastS56toS16(@ActuallySigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < S16_MIN_VALUE || u > S16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static int safeCastS56toS32(@ActuallySigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < S32_MIN_VALUE || u > S32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static long safeCastS56toS64(@ActuallySigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < S64_MIN_VALUE || u > S64_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(24) int safeCastS56toS24(@ActuallySigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < S24_MIN_VALUE || u > S24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallySigned(40) long safeCastS56toS40(@ActuallySigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < S40_MIN_VALUE || u > S40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(48) long safeCastS56toS48(@ActuallySigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < S48_MIN_VALUE || u > S48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(56) long safeCastS56toS56(@ActuallySigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < S56_MIN_VALUE || u > S56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(24) int safeCastS56toU24(@ActuallySigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < U24_MIN_VALUE || u > U24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallyUnsigned(40) long safeCastS56toU40(@ActuallySigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < U40_MIN_VALUE || u > U40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(48) long safeCastS56toU48(@ActuallySigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < U48_MIN_VALUE || u > U48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(56) long safeCastS56toU56(@ActuallySigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < U56_MIN_VALUE || u > U56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned byte safeCastS56toU8(@ActuallySigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < U8_MIN_VALUE || u > U8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static @ActuallyUnsigned short safeCastS56toU16(@ActuallySigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < U16_MIN_VALUE || u > U16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static @ActuallyUnsigned int safeCastS56toU32(@ActuallySigned(56) long input) throws OverflowException
	{
		long u = input;
		if (u < U32_MIN_VALUE || u > U32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	// >>>
	
	
	
	/* <<<
primxp
_$$primxpconf:byte,short,int,long,sint24,slong40,slong48,slong56,uint24,ulong40,ulong48,ulong56,ubyte,ushort,uint$$_
	public static _$$@litprim$$_ safeCastS64to_$$PrimShort$$_(long input) throws OverflowException
	{
		long u = input;
		if (u < _$$PrimShort$$__MIN_VALUE || u > _$$PrimShort$$__MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (_$$litprim$$_)input;
	}
	 */
	
	public static byte safeCastS64toS8(long input) throws OverflowException
	{
		long u = input;
		if (u < S8_MIN_VALUE || u > S8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static short safeCastS64toS16(long input) throws OverflowException
	{
		long u = input;
		if (u < S16_MIN_VALUE || u > S16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static int safeCastS64toS32(long input) throws OverflowException
	{
		long u = input;
		if (u < S32_MIN_VALUE || u > S32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static long safeCastS64toS64(long input) throws OverflowException
	{
		long u = input;
		if (u < S64_MIN_VALUE || u > S64_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(24) int safeCastS64toS24(long input) throws OverflowException
	{
		long u = input;
		if (u < S24_MIN_VALUE || u > S24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallySigned(40) long safeCastS64toS40(long input) throws OverflowException
	{
		long u = input;
		if (u < S40_MIN_VALUE || u > S40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(48) long safeCastS64toS48(long input) throws OverflowException
	{
		long u = input;
		if (u < S48_MIN_VALUE || u > S48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(56) long safeCastS64toS56(long input) throws OverflowException
	{
		long u = input;
		if (u < S56_MIN_VALUE || u > S56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(24) int safeCastS64toU24(long input) throws OverflowException
	{
		long u = input;
		if (u < U24_MIN_VALUE || u > U24_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallyUnsigned(40) long safeCastS64toU40(long input) throws OverflowException
	{
		long u = input;
		if (u < U40_MIN_VALUE || u > U40_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(48) long safeCastS64toU48(long input) throws OverflowException
	{
		long u = input;
		if (u < U48_MIN_VALUE || u > U48_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(56) long safeCastS64toU56(long input) throws OverflowException
	{
		long u = input;
		if (u < U56_MIN_VALUE || u > U56_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned byte safeCastS64toU8(long input) throws OverflowException
	{
		long u = input;
		if (u < U8_MIN_VALUE || u > U8_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static @ActuallyUnsigned short safeCastS64toU16(long input) throws OverflowException
	{
		long u = input;
		if (u < U16_MIN_VALUE || u > U16_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static @ActuallyUnsigned int safeCastS64toU32(long input) throws OverflowException
	{
		long u = input;
		if (u < U32_MIN_VALUE || u > U32_MAX_VALUE)
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//Special care must be taken with U64's, since we can represent all other types in S64 arithmetic except U64!
	
	
	/* <<<
primxp
_$$primxpconf:byte,short,int,long,sint24,slong40,slong48,slong56,uint24,ulong40,ulong48,ulong56,ubyte,ushort,uint$$_
	public static _$$@litprim$$_ safeCastU64to_$$PrimShort$$_(@ActuallyUnsigned long input) throws OverflowException
	{
		if (input < 0 || input > _$$PrimShort$$__MAX_VALUE)  //the input will never "be negative", so < 0 means it's really freaking big; way bigger than this type! XD
			throw new OverflowException(String.valueOf(input));
		return (_$$litprim$$_)input;
	}
	 */
	
	public static byte safeCastU64toS8(@ActuallyUnsigned long input) throws OverflowException
	{
		if (input < 0 || input > S8_MAX_VALUE)  //the input will never "be negative", so < 0 means it's really freaking big; way bigger than this type! XD
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static short safeCastU64toS16(@ActuallyUnsigned long input) throws OverflowException
	{
		if (input < 0 || input > S16_MAX_VALUE)  //the input will never "be negative", so < 0 means it's really freaking big; way bigger than this type! XD
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static int safeCastU64toS32(@ActuallyUnsigned long input) throws OverflowException
	{
		if (input < 0 || input > S32_MAX_VALUE)  //the input will never "be negative", so < 0 means it's really freaking big; way bigger than this type! XD
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static long safeCastU64toS64(@ActuallyUnsigned long input) throws OverflowException
	{
		if (input < 0 || input > S64_MAX_VALUE)  //the input will never "be negative", so < 0 means it's really freaking big; way bigger than this type! XD
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(24) int safeCastU64toS24(@ActuallyUnsigned long input) throws OverflowException
	{
		if (input < 0 || input > S24_MAX_VALUE)  //the input will never "be negative", so < 0 means it's really freaking big; way bigger than this type! XD
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallySigned(40) long safeCastU64toS40(@ActuallyUnsigned long input) throws OverflowException
	{
		if (input < 0 || input > S40_MAX_VALUE)  //the input will never "be negative", so < 0 means it's really freaking big; way bigger than this type! XD
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(48) long safeCastU64toS48(@ActuallyUnsigned long input) throws OverflowException
	{
		if (input < 0 || input > S48_MAX_VALUE)  //the input will never "be negative", so < 0 means it's really freaking big; way bigger than this type! XD
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallySigned(56) long safeCastU64toS56(@ActuallyUnsigned long input) throws OverflowException
	{
		if (input < 0 || input > S56_MAX_VALUE)  //the input will never "be negative", so < 0 means it's really freaking big; way bigger than this type! XD
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(24) int safeCastU64toU24(@ActuallyUnsigned long input) throws OverflowException
	{
		if (input < 0 || input > U24_MAX_VALUE)  //the input will never "be negative", so < 0 means it's really freaking big; way bigger than this type! XD
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	
	public static @ActuallyUnsigned(40) long safeCastU64toU40(@ActuallyUnsigned long input) throws OverflowException
	{
		if (input < 0 || input > U40_MAX_VALUE)  //the input will never "be negative", so < 0 means it's really freaking big; way bigger than this type! XD
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(48) long safeCastU64toU48(@ActuallyUnsigned long input) throws OverflowException
	{
		if (input < 0 || input > U48_MAX_VALUE)  //the input will never "be negative", so < 0 means it's really freaking big; way bigger than this type! XD
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned(56) long safeCastU64toU56(@ActuallyUnsigned long input) throws OverflowException
	{
		if (input < 0 || input > U56_MAX_VALUE)  //the input will never "be negative", so < 0 means it's really freaking big; way bigger than this type! XD
			throw new OverflowException(String.valueOf(input));
		return (long)input;
	}
	
	public static @ActuallyUnsigned byte safeCastU64toU8(@ActuallyUnsigned long input) throws OverflowException
	{
		if (input < 0 || input > U8_MAX_VALUE)  //the input will never "be negative", so < 0 means it's really freaking big; way bigger than this type! XD
			throw new OverflowException(String.valueOf(input));
		return (byte)input;
	}
	
	public static @ActuallyUnsigned short safeCastU64toU16(@ActuallyUnsigned long input) throws OverflowException
	{
		if (input < 0 || input > U16_MAX_VALUE)  //the input will never "be negative", so < 0 means it's really freaking big; way bigger than this type! XD
			throw new OverflowException(String.valueOf(input));
		return (short)input;
	}
	
	public static @ActuallyUnsigned int safeCastU64toU32(@ActuallyUnsigned long input) throws OverflowException
	{
		if (input < 0 || input > U32_MAX_VALUE)  //the input will never "be negative", so < 0 means it's really freaking big; way bigger than this type! XD
			throw new OverflowException(String.valueOf(input));
		return (int)input;
	}
	// >>>
	
	
	
	
	
	
	
	
	
	
	/* <<<
primxp
_$$primxpconf:byte,short,int,long,sint24,slong40,slong48,slong56$$_
	public static @ActuallyUnsigned long safeCast_$$PrimShort$$_toU64(_$$@litprim$$_ input) throws OverflowException
	{
		if (input < 0)
			throw new OverflowException(String.valueOf(input));
		return input;
	}
	 */
	
	public static @ActuallyUnsigned long safeCastS8toU64(byte input) throws OverflowException
	{
		if (input < 0)
			throw new OverflowException(String.valueOf(input));
		return input;
	}
	
	public static @ActuallyUnsigned long safeCastS16toU64(short input) throws OverflowException
	{
		if (input < 0)
			throw new OverflowException(String.valueOf(input));
		return input;
	}
	
	public static @ActuallyUnsigned long safeCastS32toU64(int input) throws OverflowException
	{
		if (input < 0)
			throw new OverflowException(String.valueOf(input));
		return input;
	}
	
	public static @ActuallyUnsigned long safeCastS64toU64(long input) throws OverflowException
	{
		if (input < 0)
			throw new OverflowException(String.valueOf(input));
		return input;
	}
	
	public static @ActuallyUnsigned long safeCastS24toU64(@ActuallySigned(24) int input) throws OverflowException
	{
		if (input < 0)
			throw new OverflowException(String.valueOf(input));
		return input;
	}
	
	public static @ActuallyUnsigned long safeCastS40toU64(@ActuallySigned(40) long input) throws OverflowException
	{
		if (input < 0)
			throw new OverflowException(String.valueOf(input));
		return input;
	}
	
	public static @ActuallyUnsigned long safeCastS48toU64(@ActuallySigned(48) long input) throws OverflowException
	{
		if (input < 0)
			throw new OverflowException(String.valueOf(input));
		return input;
	}
	
	public static @ActuallyUnsigned long safeCastS56toU64(@ActuallySigned(56) long input) throws OverflowException
	{
		if (input < 0)
			throw new OverflowException(String.valueOf(input));
		return input;
	}
	// >>>
	
	
	
	
	
	
	
	
	
	
	/* <<<
primxp
_$$primxpconf:uint24,ulong40,ulong48,ulong56,ulong$$_
	public static @ActuallyUnsigned long safeCast_$$PrimShort$$_toU64(_$$@litprim$$_ input) throws OverflowException
	{
		return input;
	}
	 */
	
	public static @ActuallyUnsigned long safeCastU24toU64(@ActuallyUnsigned(24) int input) throws OverflowException
	{
		return input;
	}
	
	public static @ActuallyUnsigned long safeCastU40toU64(@ActuallyUnsigned(40) long input) throws OverflowException
	{
		return input;
	}
	
	public static @ActuallyUnsigned long safeCastU48toU64(@ActuallyUnsigned(48) long input) throws OverflowException
	{
		return input;
	}
	
	public static @ActuallyUnsigned long safeCastU56toU64(@ActuallyUnsigned(56) long input) throws OverflowException
	{
		return input;
	}
	
	public static @ActuallyUnsigned long safeCastU64toU64(@ActuallyUnsigned long input) throws OverflowException
	{
		return input;
	}
	// >>>
	
	
	
	
	
	
	public static @ActuallyUnsigned long safeCastU8toU64(@ActuallyUnsigned byte input) throws OverflowException
	{
		return Unsigned.upcastTo64(input);
	}
	
	public static @ActuallyUnsigned long safeCastU16toU64(@ActuallyUnsigned short input) throws OverflowException
	{
		return Unsigned.upcastTo64(input);
	}
	
	public static @ActuallyUnsigned long safeCastU32toU64(@ActuallyUnsigned int input) throws OverflowException
	{
		return Unsigned.upcastTo64(input);
	}
}
