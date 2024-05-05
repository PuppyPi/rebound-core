package rebound.io.streaming.util.numericconversion.byteencoding;

import static java.util.Objects.*;
import static rebound.bits.Unsigned.*;
import static rebound.io.streaming.impls.memory.ArrayBackedStreams.*;
import static rebound.util.BasicExceptionUtilities.*;
import static rebound.util.collections.ArrayUtilities.*;
import java.io.EOFException;
import java.io.IOException;
import rebound.bits.Bytes;
import rebound.bits.Endianness;
import rebound.exceptions.ImpossibleException;
import rebound.io.streaming.api.BlockReadStream;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.ByteBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.ByteBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.CharBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.DoubleBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.DoubleBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.FloatBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.IntBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.LongBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.ShortBlockReadStream;
import rebound.io.streaming.util.decorators.AbstractStreamDecorator;
import rebound.io.streaming.util.decorators.MultipleReadingByteBlockReadStreamDecorator;
import rebound.io.streaming.util.implhelp.AbstractStream;
import rebound.math.SmallIntegerMathUtilities;
import rebound.util.collections.Slice;
import rebound.util.objectutil.Trimmable;

//TODO Make sure we got the signedness right for nonnatively typed primitives (Int24, Long48, ..)  XD''

//Todo support offset binary decoding in the integer decoders :3
//Todo more encoders!! XD'''

public class NumberFormatConversion
{
	/* <<<
	python-header
	
	primsForFloats = [
		["byte", "byte", 8],
		["short", "short", 16],
		["sint24", "int", 24],
		["int", "int", 32],
	]
	
	destfloatprims = [
		["Float", "Single"],
		["Double", "Double"],
	]
	
	
	
	intprims = [
		["byte", "short", "signed", 8],
		["byte", "short", "unsigned", 8],
		["byte", "char", "unsigned", 8],
		["byte", "int", "signed", 8],
		["byte", "int", "unsigned", 8],
		["byte", "long", "signed", 8],
		["byte", "long", "unsigned", 8],
		
		["short", "short", "signed", 16],
		["short", "char", "unsigned", 16],
		["short", "int", "signed", 16],
		["short", "int", "unsigned", 16],
		["short", "long", "signed", 16],
		["short", "long", "unsigned", 16],
		
		["sint24", "int", "signed", 24],
		["uint24", "int", "unsigned", 24],
		["sint24", "long", "signed", 24],
		["uint24", "long", "unsigned", 24],
		
		["int", "int", "signed", 32],
		["int", "long", "signed", 32],
		["int", "long", "unsigned", 32],
		
		["slong40", "long", "signed", 40],
		["ulong40", "long", "unsigned", 40],
		
		["slong48", "long", "signed", 48],
		["ulong48", "long", "unsigned", 48],
		
		["slong56", "long", "signed", 56],
		["ulong56", "long", "unsigned", 56],
		
		["long", "long", "signed", 64],
	]
	
	
	
	reverseIntprims = [
		["byte", "short", 8],
		["byte", "char", 8],
		["byte", "int", 8],
		["byte", "long", 8],
		
		["short", "short", 16],
		["short", "char", 16],
		["short", "int", 16],
		["short", "long", 16],
		
		["int24", "int", 24],
		["int24", "long", 24],
		
		["int", "int", 32],
		["int", "long", 32],
		
		["long40", "long", 40],
		
		["long48", "long", 48],
		
		["long56", "long", 56],
		
		["long", "long", 64],
	]
	
	
	
	
	
	def primcap(s):
		nn = (s.startswith("u") or s.startswith("s")) and s[-1].isdigit()
		if (nn):
			return s[:2].upper() + s[2:]
		else:
			return s.capitalize()
	
	
	
	
	
	def expandfulltemplateInts(s):
		out = ""
		
		for logiprim, physprim, signedness, bitlen in intprims:
			LogiPrim = primcap(logiprim)
			PhysPrim = primcap(physprim)
			bytelen = bitlen / 8
			
			S = signedness[0].upper()
			
			signed = S == "S"
			Signedness = signedness.capitalize()
			
			mask = "0x"+("F" * (bytelen * 2))
			
			
			if (bytelen == 1):
				GetterOpener = "_$$physprim$$_ v = (_$$physprim$$_)(tempBuffer["
				GetterCloser = "]"+((" & "+mask) if not signed else "")+");"
			else:
				if (logiprim != physprim):
					GetterOpener = "_$$physprim$$_ v = reinterpretLowBitsAs"+S+str(bitlen)+"(Bytes.get_$$Endianness$$__$$LogiPrim$$_(tempBuffer, "
					GetterCloser = "));"
				else:
					GetterOpener = "_$$physprim$$_ v = Bytes.get_$$Endianness$$__$$LogiPrim$$_(tempBuffer, "
					GetterCloser = ");"
			
			
			
			
			
			for endianness in ["little", "big"]:
				Endianness = endianness.capitalize()
				E = Endianness[0]+"E"
				
				
				
				def expandtemplate(r):
					r = r.replace("_$$E$$_", E)
					r = r.replace("_$$Endianness$$_", Endianness)
					
					r = r.replace("_$$Signedness$$_", Signedness)
					
					r = r.replace("_$$PhysPrim$$_", PhysPrim)
					r = r.replace("_$$physprim$$_", physprim)
					r = r.replace("_$$LogiPrim$$_", LogiPrim)
					r = r.replace("_$$logiprim$$_", logiprim)
					r = r.replace("_$$bytelen$$_", str(bytelen))
					
					return r
				
				
				x = expandtemplate(s)
				x = x.replace("_$$GetterOpener$$_", expandtemplate(GetterOpener))
				x = x.replace("_$$GetterCloser$$_", expandtemplate(GetterCloser))
				
				
				out += x
		#
		
		return out
	#
	
	
	
	
	
	
	
	
	
	
	
	
	def expandfulltemplateReverseInts(s):
		out = ""
		
		for logiprim, physprim, bitlen in reverseIntprims:
			LogiPrim = primcap(logiprim)
			PhysPrim = primcap(physprim)
			bytelen = bitlen / 8
			
			mask = "0x"+("F" * (bytelen * 2))
			
			
			if (bytelen == 1):
				Putter = "buffer[i] = (byte)v;"
			else:
				physphysprim = ["byte", "short", "int", "int", "long", "long", "long", "long"][bytelen-1]
				
				Putter = "Bytes.put_$$Endianness$$__$$LogiPrim$$_(buffer, offset + i * _$$bytelen$$_, ("+physphysprim+")v);"
			
			
			
			
			
			for endianness in ["little", "big"]:
				Endianness = endianness.capitalize()
				E = Endianness[0]+"E"
				
				
				
				def expandtemplate(r):
					r = r.replace("_$$E$$_", E)
					r = r.replace("_$$Endianness$$_", Endianness)
					
					r = r.replace("_$$PhysPrim$$_", PhysPrim)
					r = r.replace("_$$physprim$$_", physprim)
					r = r.replace("_$$LogiPrim$$_", LogiPrim)
					r = r.replace("_$$logiprim$$_", logiprim)
					r = r.replace("_$$bytelen$$_", str(bytelen))
					
					return r
				
				
				x = expandtemplate(s)
				x = x.replace("_$$Putter$$_", expandtemplate(Putter))
				
				
				out += x
		#
		
		return out
	#
	
	
	
	
	
	
	
	
	
	
	
	
	def expandfulltemplateFloats(s):
		out = ""
		
		for DestFloatPrim, DestFloatTypeIEEE in destfloatprims:
			destfloatprim = DestFloatPrim.lower()
			
			for logiprim, physprim, bitlen in primsForFloats:
				LogiPrim = primcap(logiprim)
				PhysPrim = primcap(physprim)
				bytelen = bitlen / 8
				
				
				if (bytelen == 1):
					GetterOpener = "byte v = tempBuffer["
					GetterCloser = "];"
				else:
					GetterOpener = "_$$physprim$$_ v = Bytes.get_$$Endianness$$__$$LogiPrim$$_(tempBuffer, "
					GetterCloser = ");"
				
				
				
				
				for SignednessFormat in ["TwosComplement", "Offset"]:
					
					if (SignednessFormat == "Offset"):
						OffsetToTwosComplementCodeIfInputIsOffset = "v -= ("+physprim+")"+str(1 << (bitlen-1))+"l;"
					else:
						OffsetToTwosComplementCodeIfInputIsOffset = ""
					
					
					for endianness in ["little", "big"]:
						Endianness = endianness.capitalize()
						E = Endianness[0]+"E"
						
						for Normalizedness, NormalizationLong in [["Unnormalized", "Unnormalized"], ["NormalizedN1P1", "NormalizedNegative1ToPositive1"]]:
							normalizedness = Normalizedness.lower()
							
							if (normalizedness == "normalizedn1p1"):
								DivisionIfNormalized = " / "+str(1 << (bitlen-1)) + destfloatprim[0]
							else:
								DivisionIfNormalized = ""
							
							
							
							def expandtemplate(r):
								r = r.replace("_$$Normalizedness$$_", Normalizedness)
								r = r.replace("_$$NormalizationLong$$_", NormalizationLong)
								r = r.replace("_$$normalizedness$$_", normalizedness)
								r = r.replace("_$$DivisionIfNormalized$$_", DivisionIfNormalized)
								
								r = r.replace("_$$E$$_", E)
								r = r.replace("_$$Endianness$$_", Endianness)
								
								r = r.replace("_$$SignednessFormat$$_", SignednessFormat)
								
								r = r.replace("_$$PhysPrim$$_", PhysPrim)
								r = r.replace("_$$physprim$$_", physprim)
								r = r.replace("_$$LogiPrim$$_", LogiPrim)
								r = r.replace("_$$logiprim$$_", logiprim)
								r = r.replace("_$$bytelen$$_", str(bytelen))
								
								r = r.replace("_$$DestFloatPrim$$_", DestFloatPrim)
								r = r.replace("_$$destfloatprim$$_", destfloatprim)
								r = r.replace("_$$DestFloatTypeIEEE$$_", DestFloatTypeIEEE)
								
								return r
							
							
							x = expandtemplate(s)
							x = x.replace("_$$OffsetToTwosComplementCodeIfInputIsOffset$$_", OffsetToTwosComplementCodeIfInputIsOffset)
							x = x.replace("_$$GetterOpener$$_", expandtemplate(GetterOpener))
							x = x.replace("_$$GetterCloser$$_", expandtemplate(GetterCloser))
							
							
							out += x
		#
		
		return out
	#
	
	
	
	
	
	
	
	
	 */
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static enum Signedness
	{
		Signed,
		Unsigned,
	}
	
	public static enum NonbyteIntegerPrimitiveType
	{
		Short,
		Char,
		Int,
		Long,
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static enum Normalization
	{
		Unnormalized,
		NormalizedNegative1ToPositive1,
		//Todo NormalizedZeroToPositive1
		;
		
		
		public static String formatRange(Normalization normalization, int bits)
		{
			return normalization == Normalization.NormalizedNegative1ToPositive1 ? "[-1, +1]" : formatUnnormalizedRange(bits);
		}
		
		public static String formatUnnormalizedRange(int bits)
		{
			int h = bits - 1;
			long lowIncl = -1l << h;
			long highExcl = 1l << h;
			
			return "["+lowIncl+", "+highExcl+")";
		}
	}
	
	
	public static enum SignednessFormat
	{
		/**
		 * The data is represented as standard two's-complement signed data and is directly what you expect ^_^
		 * https://en.wikipedia.org/wiki/Two%27s_complement  ^_^
		 * 
		 * This has the issue that the absolute value / negation of the smallest negative value (eg, -32768 for 16-bit integers) is not possible because the positive version of it isn't encodeable!
		 * On the other hand, it doesn't suffer from the negative-zero problem!
		 * (This is an intrinsic tradeoff because there is always an even number of binary bit-patterns for an n-bit value, but an odd number of mathematical integers in a symmetric interval around zero, since zero is logically the same whether positive or negative!)
		 * 
		 * + 0xFFFF --> Logical -1
		 * + 0xFFFE --> Logical -2
		 * + 0x8001 --> Logical -32767
		 * + 0x8000 --> Logical -32768
		 * + 0x7FFF --> Logical 32767
		 * + 0x0001 --> Logical 0
		 * + 0x0000 --> Logical 0
		 * 
		 * In audio jargon this is commonly (but confusingly) just called "signed" format.
		 * 
		 * + Note that addition and subtraction are the same operation in terms of binary bits as {@link #Unsigned} and {@link #Offset Offset-Binary}!
		 */
		TwosComplement,
		
		
		/**
		 * The data is represented as standard one's-complement signed data!  This is a format rarely seen these days (though it's still used in a few things, like the Internet Protocol packet checksums, μ-law/a-law companding, etc.)
		 * https://en.wikipedia.org/wiki/Ones%27_complement  ^_^
		 * 
		 * This has the significant issue that there is such a thing as positive and negative zero!  So just because two binary bit patterns are equal doesn't mean their logical values are equal!
		 * On the other hand, it doesn't suffer from the absolute-magnitude-of-the-smallest-value problem!
		 * (This is an intrinsic tradeoff because there is always an even number of binary bit-patterns for an n-bit value, but an odd number of mathematical integers in a symmetric interval around zero, since zero is logically the same whether positive or negative!)
		 * 
		 * + 0xFFFF --> Logical -0
		 * + 0xFFFE --> Logical -1
		 * + 0x8001 --> Logical -32766
		 * + 0x8000 --> Logical -32767
		 * + 0x7FFF --> Logical +32767
		 * + 0x0001 --> Logical +1
		 * + 0x0000 --> Logical +0
		 */
		OnesComplement,
		
		
		/**
		 * The data is represented as sign-magnitude signed data!  This is a format rarely seen these days (though it's still used in a few things, like internally inside floating-point values!)
		 * https://en.wikipedia.org/wiki/Signed_number_representations#Sign%E2%80%93magnitude  ^_^
		 * 
		 * This has the significant issue that there is such a thing as positive and negative zero!  So just because two binary bit patterns are equal doesn't mean their logical values are equal!
		 * On the other hand, it doesn't suffer from the absolute-magnitude-of-the-smallest-value problem!
		 * (This is an intrinsic tradeoff because there is always an even number of binary bit-patterns for an n-bit value, but an odd number of mathematical integers in a symmetric interval around zero, since zero is logically the same whether positive or negative!)
		 * 
		 * + 0xFFFF --> Logical -32767
		 * + 0xFFFE --> Logical -32766
		 * + 0x8001 --> Logical -1
		 * + 0x8000 --> Logical -0
		 * + 0x7FFF --> Logical +32767
		 * + 0x0001 --> Logical +1
		 * + 0x0000 --> Logical +0
		 */
		SignMagnitude,
		
		
		/**
		 * The signed data can be thought of as being unsigned integers which are conceptually subtracted from to produce the signed value :3
		 * https://en.wikipedia.org/wiki/Offset_binary  ^_^
		 * 
		 * This has the issue that the absolute value / negation of the smallest negative value (eg, -32768 for 16-bit integers) is not possible because the positive version of it isn't encodeable!
		 * On the other hand, it doesn't suffer from the negative-zero problem!
		 * (This is an intrinsic tradeoff because there is always an even number of binary bit-patterns for an n-bit value, but an odd number of mathematical integers in a symmetric interval around zero, since zero is logically the same whether positive or negative!)
		 * 
		 * + 0xFFFF --> Logical 32767
		 * + 0xFFFE --> Logical 32766
		 * + 0x8001 --> Logical 1
		 * + 0x8000 --> Logical 0
		 * + 0x7FFF --> Logical -1
		 * + 0x0001 --> Logical -32767
		 * + 0x0000 --> Logical -32768
		 * 
		 * In audio jargon this is commonly (but confusingly) called "unsigned" format.
		 * 
		 * + Note that addition and subtraction are the same operation in terms of binary bits as {@link #TwosComplement} and {@link #Offset Offset-Binary}!
		 */
		Offset,
		
		
		/**
		 * This is just *actual* unsigned data!  It can't represent negative numbers!!
		 * https://en.wikipedia.org/wiki/Integer_(computer_science)  ^_^
		 * 
		 * This has the issue that the absolute value / negation of the smallest negative value (eg, -32768 for 16-bit integers) is not possible because the positive version of it isn't encodeable!
		 * On the other hand, it doesn't suffer from the negative-zero problem!
		 * (This is an intrinsic tradeoff because there is always an even number of binary bit-patterns for an n-bit value, but an odd number of mathematical integers in a symmetric interval around zero, since zero is logically the same whether positive or negative!)
		 * 
		 * + 0xFFFF --> Logical 65535
		 * + 0xFFFE --> Logical 65534
		 * + 0x8001 --> Logical 32769
		 * + 0x8000 --> Logical 32768
		 * + 0x7FFF --> Logical 32767
		 * + 0x0001 --> Logical 1
		 * + 0x0000 --> Logical 0
		 * 
		 * + Note that addition and subtraction are the same operation in terms of binary bits as {@link #TwosComplement} and {@link #Offset Offset-Binary}!
		 */
		Unsigned,
	}
	
	
	
	
	
	public static enum FloatingPointType
	{
		Single,
		Double,
	}
	
	
	/**
	 * Companding is a function applied to *each sample individually on its own* to simply redistribute the dynamic range!
	 * 
	 * Usually so that most of the possible bit-patterns in an n-bit sample refer to small numbers close to zero, since there is often more information carried in the difference between a (perhaps air/sound pressure value) of 0.01 and 0.02 than between 1000.01 and 1000.02  XD
	 * Ie, it preserves more detail in quiet sounds at the expense of the detail in louder sounds.
	 * Note however, that if the original *analog* sensor data is using a uncompanded/linear response curve..then companding it does nothing to help and in fact destroys information unless you're simultaneously converting it to samples of a smaller bitsize!
	 */
	public static enum PopularCompandingFunction
	{
		ContinuousMuLaw,
		
		ContinuousALaw,
		
		G711DiscreteMuLaw,
		
		G711DiscreteALaw,
		
		IEEE754FloatingPoint,
	}
	
	
	
	
	
	public static class NumberFormat
	{
		public static class NumberFormatInteger
		extends NumberFormat
		{
			protected final SignednessFormat contents;

			public NumberFormatInteger(SignednessFormat contents)
			{
				this.contents = contents;
			}
			
			public SignednessFormat getContents()
			{
				return contents;
			}

			@Override
			public int hashCode()
			{
				return contents.hashCode();
			}

			@Override
			public boolean equals(Object obj)
			{
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				NumberFormatInteger other = (NumberFormatInteger)obj;
				if (contents != other.contents)
					return false;
				return true;
			}
			
			@Override
			public String toString()
			{
				return contents.toString();
			}
		}
		
		
		
		public static class NumberFormatCompanding
		extends NumberFormat
		{
			protected final PopularCompandingFunction contents;
			
			public NumberFormatCompanding(PopularCompandingFunction contents)
			{
				this.contents = contents;
			}
			
			public PopularCompandingFunction getContents()
			{
				return contents;
			}
			
			@Override
			public int hashCode()
			{
				return contents.hashCode();
			}
			
			@Override
			public boolean equals(Object obj)
			{
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				NumberFormatCompanding other = (NumberFormatCompanding)obj;
				if (contents != other.contents)
					return false;
				return true;
			}
			
			@Override
			public String toString()
			{
				return contents.toString();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static ShortBlockReadStream converterReaderByteToShort(ByteBlockReadStream rawIn, int bytesPerSample, Signedness signedness, Endianness endianness)
	{
		return (ShortBlockReadStream) converterReaderByteToInteger(rawIn, bytesPerSample, signedness, endianness, NonbyteIntegerPrimitiveType.Short);
	}
	
	public static CharBlockReadStream converterReaderByteToChar(ByteBlockReadStream rawIn, int bytesPerSample, Signedness signedness, Endianness endianness)
	{
		return (CharBlockReadStream) converterReaderByteToInteger(rawIn, bytesPerSample, signedness, endianness, NonbyteIntegerPrimitiveType.Char);
	}
	
	public static IntBlockReadStream converterReaderByteToInt(ByteBlockReadStream rawIn, int bytesPerSample, Signedness signedness, Endianness endianness)
	{
		return (IntBlockReadStream) converterReaderByteToInteger(rawIn, bytesPerSample, signedness, endianness, NonbyteIntegerPrimitiveType.Int);
	}
	
	public static LongBlockReadStream converterReaderByteToLong(ByteBlockReadStream rawIn, int bytesPerSample, Signedness signedness, Endianness endianness)
	{
		return (LongBlockReadStream) converterReaderByteToInteger(rawIn, bytesPerSample, signedness, endianness, NonbyteIntegerPrimitiveType.Long);
	}
	
	
	
	public static BlockReadStream converterReaderByteToInteger(ByteBlockReadStream rawIn, int bytesPerSample, Signedness signedness, Endianness endianness, NonbyteIntegerPrimitiveType destprimtype)
	{
		requireNonNull(signedness);
		requireNonNull(endianness);
		requireNonNull(destprimtype);
		
		if (bytesPerSample < 1 || bytesPerSample > 8)
			throw new IllegalArgumentException();
		
		
		
		/* <<<
		python
		
		
		s = """
		
		if (bytesPerSample == _$$bytelen$$_ && signedness == Signedness._$$Signedness$$_ && endianness == Endianness._$$Endianness$$_ && destprimtype == NonbyteIntegerPrimitiveType._$$PhysPrim$$_)
			return new _$$Signedness$$_Integer_$$bytelen$$_Bytes_$$E$$_To_$$PhysPrim$$_ReadStream(rawIn);
		"""
		
		
		p(expandfulltemplateInts(s))
		
		 */
		
		
		if (bytesPerSample == 1 && signedness == Signedness.Signed && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Short)
			return new SignedInteger1BytesLEToShortReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signedness == Signedness.Signed && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Short)
			return new SignedInteger1BytesBEToShortReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signedness == Signedness.Unsigned && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Short)
			return new UnsignedInteger1BytesLEToShortReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signedness == Signedness.Unsigned && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Short)
			return new UnsignedInteger1BytesBEToShortReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signedness == Signedness.Unsigned && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Char)
			return new UnsignedInteger1BytesLEToCharReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signedness == Signedness.Unsigned && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Char)
			return new UnsignedInteger1BytesBEToCharReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signedness == Signedness.Signed && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Int)
			return new SignedInteger1BytesLEToIntReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signedness == Signedness.Signed && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Int)
			return new SignedInteger1BytesBEToIntReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signedness == Signedness.Unsigned && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Int)
			return new UnsignedInteger1BytesLEToIntReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signedness == Signedness.Unsigned && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Int)
			return new UnsignedInteger1BytesBEToIntReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signedness == Signedness.Signed && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new SignedInteger1BytesLEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signedness == Signedness.Signed && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new SignedInteger1BytesBEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signedness == Signedness.Unsigned && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new UnsignedInteger1BytesLEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signedness == Signedness.Unsigned && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new UnsignedInteger1BytesBEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signedness == Signedness.Signed && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Short)
			return new SignedInteger2BytesLEToShortReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signedness == Signedness.Signed && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Short)
			return new SignedInteger2BytesBEToShortReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signedness == Signedness.Unsigned && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Char)
			return new UnsignedInteger2BytesLEToCharReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signedness == Signedness.Unsigned && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Char)
			return new UnsignedInteger2BytesBEToCharReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signedness == Signedness.Signed && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Int)
			return new SignedInteger2BytesLEToIntReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signedness == Signedness.Signed && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Int)
			return new SignedInteger2BytesBEToIntReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signedness == Signedness.Unsigned && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Int)
			return new UnsignedInteger2BytesLEToIntReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signedness == Signedness.Unsigned && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Int)
			return new UnsignedInteger2BytesBEToIntReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signedness == Signedness.Signed && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new SignedInteger2BytesLEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signedness == Signedness.Signed && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new SignedInteger2BytesBEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signedness == Signedness.Unsigned && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new UnsignedInteger2BytesLEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signedness == Signedness.Unsigned && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new UnsignedInteger2BytesBEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signedness == Signedness.Signed && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Int)
			return new SignedInteger3BytesLEToIntReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signedness == Signedness.Signed && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Int)
			return new SignedInteger3BytesBEToIntReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signedness == Signedness.Unsigned && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Int)
			return new UnsignedInteger3BytesLEToIntReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signedness == Signedness.Unsigned && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Int)
			return new UnsignedInteger3BytesBEToIntReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signedness == Signedness.Signed && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new SignedInteger3BytesLEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signedness == Signedness.Signed && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new SignedInteger3BytesBEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signedness == Signedness.Unsigned && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new UnsignedInteger3BytesLEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signedness == Signedness.Unsigned && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new UnsignedInteger3BytesBEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signedness == Signedness.Signed && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Int)
			return new SignedInteger4BytesLEToIntReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signedness == Signedness.Signed && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Int)
			return new SignedInteger4BytesBEToIntReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signedness == Signedness.Signed && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new SignedInteger4BytesLEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signedness == Signedness.Signed && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new SignedInteger4BytesBEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signedness == Signedness.Unsigned && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new UnsignedInteger4BytesLEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signedness == Signedness.Unsigned && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new UnsignedInteger4BytesBEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 5 && signedness == Signedness.Signed && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new SignedInteger5BytesLEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 5 && signedness == Signedness.Signed && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new SignedInteger5BytesBEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 5 && signedness == Signedness.Unsigned && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new UnsignedInteger5BytesLEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 5 && signedness == Signedness.Unsigned && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new UnsignedInteger5BytesBEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 6 && signedness == Signedness.Signed && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new SignedInteger6BytesLEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 6 && signedness == Signedness.Signed && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new SignedInteger6BytesBEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 6 && signedness == Signedness.Unsigned && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new UnsignedInteger6BytesLEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 6 && signedness == Signedness.Unsigned && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new UnsignedInteger6BytesBEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 7 && signedness == Signedness.Signed && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new SignedInteger7BytesLEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 7 && signedness == Signedness.Signed && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new SignedInteger7BytesBEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 7 && signedness == Signedness.Unsigned && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new UnsignedInteger7BytesLEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 7 && signedness == Signedness.Unsigned && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new UnsignedInteger7BytesBEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 8 && signedness == Signedness.Signed && endianness == Endianness.Little && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new SignedInteger8BytesLEToLongReadStream(rawIn);
		
		
		if (bytesPerSample == 8 && signedness == Signedness.Signed && endianness == Endianness.Big && destprimtype == NonbyteIntegerPrimitiveType.Long)
			return new SignedInteger8BytesBEToLongReadStream(rawIn);
		
		//>>>
		
		
		
		throw new IllegalArgumentException("Unsupported conversion: "+bytesPerSample+" bytes --> "+destprimtype.name()+"s");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static ByteBlockReadStream converterReaderIntegerToByte(BlockReadStream rawIn, int bytesPerSample, Endianness endianness, NonbyteIntegerPrimitiveType sourceprimtype)
	{
		return new MultipleReadingByteBlockReadStreamDecorator(onlyMultipleAcceptingConverterReaderIntegerToByte(rawIn, bytesPerSample, endianness, sourceprimtype), bytesPerSample);
	}
	
	
	public static ByteBlockReadStream onlyMultipleAcceptingConverterReaderIntegerToByte(BlockReadStream rawIn, int bytesPerSample, Endianness endianness, NonbyteIntegerPrimitiveType sourceprimtype)
	{
		requireNonNull(endianness);
		requireNonNull(sourceprimtype);
		
		if (bytesPerSample < 1 || bytesPerSample > 8)
			throw new IllegalArgumentException();
		
		
		
		/* <<<
		python
		
		
		s = """
		
		if (bytesPerSample == _$$bytelen$$_ && endianness == Endianness._$$Endianness$$_ && sourceprimtype == NonbyteIntegerPrimitiveType._$$PhysPrim$$_)
			return new _$$PhysPrim$$_To_$$bytelen$$_Bytes_$$E$$_ReadStream((_$$PhysPrim$$_BlockReadStream)rawIn);
		"""
		
		
		p(expandfulltemplateReverseInts(s))
		
		 */
		
		
		if (bytesPerSample == 1 && endianness == Endianness.Little && sourceprimtype == NonbyteIntegerPrimitiveType.Short)
			return new ShortTo1BytesLEReadStream((ShortBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 1 && endianness == Endianness.Big && sourceprimtype == NonbyteIntegerPrimitiveType.Short)
			return new ShortTo1BytesBEReadStream((ShortBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 1 && endianness == Endianness.Little && sourceprimtype == NonbyteIntegerPrimitiveType.Char)
			return new CharTo1BytesLEReadStream((CharBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 1 && endianness == Endianness.Big && sourceprimtype == NonbyteIntegerPrimitiveType.Char)
			return new CharTo1BytesBEReadStream((CharBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 1 && endianness == Endianness.Little && sourceprimtype == NonbyteIntegerPrimitiveType.Int)
			return new IntTo1BytesLEReadStream((IntBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 1 && endianness == Endianness.Big && sourceprimtype == NonbyteIntegerPrimitiveType.Int)
			return new IntTo1BytesBEReadStream((IntBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 1 && endianness == Endianness.Little && sourceprimtype == NonbyteIntegerPrimitiveType.Long)
			return new LongTo1BytesLEReadStream((LongBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 1 && endianness == Endianness.Big && sourceprimtype == NonbyteIntegerPrimitiveType.Long)
			return new LongTo1BytesBEReadStream((LongBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 2 && endianness == Endianness.Little && sourceprimtype == NonbyteIntegerPrimitiveType.Short)
			return new ShortTo2BytesLEReadStream((ShortBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 2 && endianness == Endianness.Big && sourceprimtype == NonbyteIntegerPrimitiveType.Short)
			return new ShortTo2BytesBEReadStream((ShortBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 2 && endianness == Endianness.Little && sourceprimtype == NonbyteIntegerPrimitiveType.Char)
			return new CharTo2BytesLEReadStream((CharBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 2 && endianness == Endianness.Big && sourceprimtype == NonbyteIntegerPrimitiveType.Char)
			return new CharTo2BytesBEReadStream((CharBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 2 && endianness == Endianness.Little && sourceprimtype == NonbyteIntegerPrimitiveType.Int)
			return new IntTo2BytesLEReadStream((IntBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 2 && endianness == Endianness.Big && sourceprimtype == NonbyteIntegerPrimitiveType.Int)
			return new IntTo2BytesBEReadStream((IntBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 2 && endianness == Endianness.Little && sourceprimtype == NonbyteIntegerPrimitiveType.Long)
			return new LongTo2BytesLEReadStream((LongBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 2 && endianness == Endianness.Big && sourceprimtype == NonbyteIntegerPrimitiveType.Long)
			return new LongTo2BytesBEReadStream((LongBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 3 && endianness == Endianness.Little && sourceprimtype == NonbyteIntegerPrimitiveType.Int)
			return new IntTo3BytesLEReadStream((IntBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 3 && endianness == Endianness.Big && sourceprimtype == NonbyteIntegerPrimitiveType.Int)
			return new IntTo3BytesBEReadStream((IntBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 3 && endianness == Endianness.Little && sourceprimtype == NonbyteIntegerPrimitiveType.Long)
			return new LongTo3BytesLEReadStream((LongBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 3 && endianness == Endianness.Big && sourceprimtype == NonbyteIntegerPrimitiveType.Long)
			return new LongTo3BytesBEReadStream((LongBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 4 && endianness == Endianness.Little && sourceprimtype == NonbyteIntegerPrimitiveType.Int)
			return new IntTo4BytesLEReadStream((IntBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 4 && endianness == Endianness.Big && sourceprimtype == NonbyteIntegerPrimitiveType.Int)
			return new IntTo4BytesBEReadStream((IntBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 4 && endianness == Endianness.Little && sourceprimtype == NonbyteIntegerPrimitiveType.Long)
			return new LongTo4BytesLEReadStream((LongBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 4 && endianness == Endianness.Big && sourceprimtype == NonbyteIntegerPrimitiveType.Long)
			return new LongTo4BytesBEReadStream((LongBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 5 && endianness == Endianness.Little && sourceprimtype == NonbyteIntegerPrimitiveType.Long)
			return new LongTo5BytesLEReadStream((LongBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 5 && endianness == Endianness.Big && sourceprimtype == NonbyteIntegerPrimitiveType.Long)
			return new LongTo5BytesBEReadStream((LongBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 6 && endianness == Endianness.Little && sourceprimtype == NonbyteIntegerPrimitiveType.Long)
			return new LongTo6BytesLEReadStream((LongBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 6 && endianness == Endianness.Big && sourceprimtype == NonbyteIntegerPrimitiveType.Long)
			return new LongTo6BytesBEReadStream((LongBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 7 && endianness == Endianness.Little && sourceprimtype == NonbyteIntegerPrimitiveType.Long)
			return new LongTo7BytesLEReadStream((LongBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 7 && endianness == Endianness.Big && sourceprimtype == NonbyteIntegerPrimitiveType.Long)
			return new LongTo7BytesBEReadStream((LongBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 8 && endianness == Endianness.Little && sourceprimtype == NonbyteIntegerPrimitiveType.Long)
			return new LongTo8BytesLEReadStream((LongBlockReadStream)rawIn);
		
		
		if (bytesPerSample == 8 && endianness == Endianness.Big && sourceprimtype == NonbyteIntegerPrimitiveType.Long)
			return new LongTo8BytesBEReadStream((LongBlockReadStream)rawIn);
		
		//>>>
		
		
		
		throw new IllegalArgumentException("Unsupported conversion: "+sourceprimtype.name()+"s --> "+bytesPerSample+" bytes");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//Todo faster versions of these!? ^^'''
	
	public static void convertDataByteEncodedIntegerToFloating(Slice<byte[]> input, Slice output, RealNumberAsLinearIntegerBytesFormat format, Normalization normalization)
	{
		convertDataByteEncodedIntegerToFloating(input, output, format.requireBytesPerSample(), format.getSignednessFormat(), format.getEndianness(), normalization);
	}
	
	public static void convertDataByteEncodedIntegerToFloating(Slice<byte[]> input, Slice output, int bytesPerSample, SignednessFormat signednessFormat, Endianness endianness, Normalization normalization)
	{
		if (input.getLength() != output.getLength() * bytesPerSample)
			throw new IllegalArgumentException();
		
		
		FloatingPointType destfloattype;
		{
			Object u = output.getUnderlying();
			
			if (u instanceof double[])
				destfloattype = FloatingPointType.Double;
			else if (u instanceof float[])
				destfloattype = FloatingPointType.Single;
			else
				throw newClassCastExceptionOrNullPointerException(u);
		}
		
		
		BlockReadStream converter = converterReaderByteEncodedIntegerToFloating(wrapByteArrayAsReadStream(input), bytesPerSample, signednessFormat, endianness, normalization, destfloattype);
		
		int amt;
		try
		{
			amt = converter.read(output);
		}
		catch (ClosedStreamException exc)
		{
			throw new ImpossibleException(exc);
		}
		catch (IOException exc)
		{
			throw new ImpossibleException(exc);
		}
		
		if (amt != output.getLength())
			throw new ImpossibleException();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static FloatBlockReadStream converterReaderByteEncodedIntegerToSingle(ByteBlockReadStream rawIn, RealNumberAsLinearIntegerBytesFormat format, Normalization normalization)
	{
		return converterReaderByteEncodedIntegerToSingle(rawIn, format.requireBytesPerSample(), format.getSignednessFormat(), format.getEndianness(), normalization);
	}
	
	public static DoubleBlockReadStream converterReaderByteEncodedIntegerToDouble(ByteBlockReadStream rawIn, RealNumberAsLinearIntegerBytesFormat format, Normalization normalization)
	{
		return converterReaderByteEncodedIntegerToDouble(rawIn, format.requireBytesPerSample(), format.getSignednessFormat(), format.getEndianness(), normalization);
	}
	
	
	
	public static BlockReadStream converterReaderByteEncodedIntegerToFloating(ByteBlockReadStream rawIn, RealNumberAsLinearIntegerBytesFormat format, Normalization normalization, FloatingPointType destfloattype)
	{
		return converterReaderByteEncodedIntegerToFloating(rawIn, format.requireBytesPerSample(), format.getSignednessFormat(), format.getEndianness(), normalization, destfloattype);
	}
	
	
	
	
	
	
	public static FloatBlockReadStream converterReaderByteEncodedIntegerToSingle(ByteBlockReadStream rawIn, int bytesPerSample, SignednessFormat signednessFormat, Endianness endianness, Normalization normalization)
	{
		return (FloatBlockReadStream) converterReaderByteEncodedIntegerToFloating(rawIn, bytesPerSample, signednessFormat, endianness, normalization, FloatingPointType.Single);
	}
	
	public static DoubleBlockReadStream converterReaderByteEncodedIntegerToDouble(ByteBlockReadStream rawIn, int bytesPerSample, SignednessFormat signednessFormat, Endianness endianness, Normalization normalization)
	{
		return (DoubleBlockReadStream) converterReaderByteEncodedIntegerToFloating(rawIn, bytesPerSample, signednessFormat, endianness, normalization, FloatingPointType.Double);
	}
	
	
	
	public static BlockReadStream converterReaderByteEncodedIntegerToFloating(ByteBlockReadStream rawIn, int bytesPerSample, SignednessFormat signednessFormat, Endianness endianness, Normalization normalization, FloatingPointType destfloattype)
	{
		requireNonNull(signednessFormat);
		requireNonNull(endianness);
		requireNonNull(normalization);
		requireNonNull(destfloattype);
		
		if (bytesPerSample < 1 || bytesPerSample > 4)
			throw new IllegalArgumentException();
		
		
		
		/* <<<
		python
		
		
		s = """
		
		if (bytesPerSample == _$$bytelen$$_ && signednessFormat == SignednessFormat._$$SignednessFormat$$_ && endianness == Endianness._$$Endianness$$_ && normalization == Normalization._$$NormalizationLong$$_ && destfloattype == FloatingPointType._$$DestFloatTypeIEEE$$_)
			return new _$$Normalizedness$$__$$SignednessFormat$$_Integer_$$bytelen$$_Bytes_$$E$$_To_$$DestFloatPrim$$_ReadStream(rawIn);
		"""
		
		
		p(expandfulltemplateFloats(s))
		
		 */
		
		
		if (bytesPerSample == 1 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Little && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Single)
			return new UnnormalizedTwosComplementInteger1BytesLEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Little && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Single)
			return new NormalizedN1P1TwosComplementInteger1BytesLEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Big && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Single)
			return new UnnormalizedTwosComplementInteger1BytesBEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Big && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Single)
			return new NormalizedN1P1TwosComplementInteger1BytesBEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Little && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Single)
			return new UnnormalizedOffsetInteger1BytesLEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Little && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Single)
			return new NormalizedN1P1OffsetInteger1BytesLEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Big && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Single)
			return new UnnormalizedOffsetInteger1BytesBEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Big && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Single)
			return new NormalizedN1P1OffsetInteger1BytesBEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Little && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Single)
			return new UnnormalizedTwosComplementInteger2BytesLEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Little && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Single)
			return new NormalizedN1P1TwosComplementInteger2BytesLEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Big && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Single)
			return new UnnormalizedTwosComplementInteger2BytesBEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Big && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Single)
			return new NormalizedN1P1TwosComplementInteger2BytesBEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Little && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Single)
			return new UnnormalizedOffsetInteger2BytesLEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Little && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Single)
			return new NormalizedN1P1OffsetInteger2BytesLEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Big && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Single)
			return new UnnormalizedOffsetInteger2BytesBEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Big && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Single)
			return new NormalizedN1P1OffsetInteger2BytesBEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Little && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Single)
			return new UnnormalizedTwosComplementInteger3BytesLEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Little && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Single)
			return new NormalizedN1P1TwosComplementInteger3BytesLEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Big && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Single)
			return new UnnormalizedTwosComplementInteger3BytesBEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Big && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Single)
			return new NormalizedN1P1TwosComplementInteger3BytesBEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Little && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Single)
			return new UnnormalizedOffsetInteger3BytesLEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Little && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Single)
			return new NormalizedN1P1OffsetInteger3BytesLEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Big && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Single)
			return new UnnormalizedOffsetInteger3BytesBEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Big && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Single)
			return new NormalizedN1P1OffsetInteger3BytesBEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Little && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Single)
			return new UnnormalizedTwosComplementInteger4BytesLEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Little && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Single)
			return new NormalizedN1P1TwosComplementInteger4BytesLEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Big && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Single)
			return new UnnormalizedTwosComplementInteger4BytesBEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Big && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Single)
			return new NormalizedN1P1TwosComplementInteger4BytesBEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Little && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Single)
			return new UnnormalizedOffsetInteger4BytesLEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Little && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Single)
			return new NormalizedN1P1OffsetInteger4BytesLEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Big && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Single)
			return new UnnormalizedOffsetInteger4BytesBEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Big && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Single)
			return new NormalizedN1P1OffsetInteger4BytesBEToFloatReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Little && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Double)
			return new UnnormalizedTwosComplementInteger1BytesLEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Little && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Double)
			return new NormalizedN1P1TwosComplementInteger1BytesLEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Big && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Double)
			return new UnnormalizedTwosComplementInteger1BytesBEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Big && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Double)
			return new NormalizedN1P1TwosComplementInteger1BytesBEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Little && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Double)
			return new UnnormalizedOffsetInteger1BytesLEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Little && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Double)
			return new NormalizedN1P1OffsetInteger1BytesLEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Big && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Double)
			return new UnnormalizedOffsetInteger1BytesBEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 1 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Big && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Double)
			return new NormalizedN1P1OffsetInteger1BytesBEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Little && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Double)
			return new UnnormalizedTwosComplementInteger2BytesLEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Little && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Double)
			return new NormalizedN1P1TwosComplementInteger2BytesLEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Big && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Double)
			return new UnnormalizedTwosComplementInteger2BytesBEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Big && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Double)
			return new NormalizedN1P1TwosComplementInteger2BytesBEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Little && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Double)
			return new UnnormalizedOffsetInteger2BytesLEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Little && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Double)
			return new NormalizedN1P1OffsetInteger2BytesLEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Big && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Double)
			return new UnnormalizedOffsetInteger2BytesBEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 2 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Big && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Double)
			return new NormalizedN1P1OffsetInteger2BytesBEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Little && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Double)
			return new UnnormalizedTwosComplementInteger3BytesLEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Little && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Double)
			return new NormalizedN1P1TwosComplementInteger3BytesLEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Big && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Double)
			return new UnnormalizedTwosComplementInteger3BytesBEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Big && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Double)
			return new NormalizedN1P1TwosComplementInteger3BytesBEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Little && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Double)
			return new UnnormalizedOffsetInteger3BytesLEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Little && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Double)
			return new NormalizedN1P1OffsetInteger3BytesLEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Big && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Double)
			return new UnnormalizedOffsetInteger3BytesBEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 3 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Big && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Double)
			return new NormalizedN1P1OffsetInteger3BytesBEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Little && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Double)
			return new UnnormalizedTwosComplementInteger4BytesLEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Little && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Double)
			return new NormalizedN1P1TwosComplementInteger4BytesLEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Big && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Double)
			return new UnnormalizedTwosComplementInteger4BytesBEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signednessFormat == SignednessFormat.TwosComplement && endianness == Endianness.Big && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Double)
			return new NormalizedN1P1TwosComplementInteger4BytesBEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Little && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Double)
			return new UnnormalizedOffsetInteger4BytesLEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Little && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Double)
			return new NormalizedN1P1OffsetInteger4BytesLEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Big && normalization == Normalization.Unnormalized && destfloattype == FloatingPointType.Double)
			return new UnnormalizedOffsetInteger4BytesBEToDoubleReadStream(rawIn);
		
		
		if (bytesPerSample == 4 && signednessFormat == SignednessFormat.Offset && endianness == Endianness.Big && normalization == Normalization.NormalizedNegative1ToPositive1 && destfloattype == FloatingPointType.Double)
			return new NormalizedN1P1OffsetInteger4BytesBEToDoubleReadStream(rawIn);
		
		//>>>
		
		
		throw new AssertionError();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	python
	
	
	s = """
	
	
	
	
	
	public static class _$$Signedness$$_Integer_$$bytelen$$_Bytes_$$E$$_To_$$PhysPrim$$_ReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements _$$PhysPrim$$_BlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public _$$Signedness$$_Integer_$$bytelen$$_Bytes_$$E$$_To_$$PhysPrim$$_ReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * _$$bytelen$$_;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, _$$bytelen$$_);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public _$$physprim$$_ read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = _$$bytelen$$_;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				_$$GetterOpener$$_0_$$GetterCloser$$_
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(_$$physprim$$_[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * _$$bytelen$$_;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, _$$bytelen$$_);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				_$$GetterOpener$$_i * _$$bytelen$$__$$GetterCloser$$_
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	"""
	
	
	
	
	
	
	p(expandfulltemplateInts(s))
	
	 */
	
	
	
	
	
	
	public static class SignedInteger1BytesLEToShortReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements ShortBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger1BytesLEToShortReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public short read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = (short)(tempBuffer[0]);
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(short[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = (short)(tempBuffer[i * 1]);
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger1BytesBEToShortReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements ShortBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger1BytesBEToShortReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public short read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = (short)(tempBuffer[0]);
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(short[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = (short)(tempBuffer[i * 1]);
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger1BytesLEToShortReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements ShortBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger1BytesLEToShortReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public short read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = (short)(tempBuffer[0] & 0xFF);
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(short[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = (short)(tempBuffer[i * 1] & 0xFF);
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger1BytesBEToShortReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements ShortBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger1BytesBEToShortReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public short read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = (short)(tempBuffer[0] & 0xFF);
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(short[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = (short)(tempBuffer[i * 1] & 0xFF);
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger1BytesLEToCharReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements CharBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger1BytesLEToCharReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public char read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				char v = (char)(tempBuffer[0] & 0xFF);
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(char[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				char v = (char)(tempBuffer[i * 1] & 0xFF);
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger1BytesBEToCharReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements CharBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger1BytesBEToCharReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public char read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				char v = (char)(tempBuffer[0] & 0xFF);
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(char[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				char v = (char)(tempBuffer[i * 1] & 0xFF);
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger1BytesLEToIntReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements IntBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger1BytesLEToIntReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public int read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = (int)(tempBuffer[0]);
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = (int)(tempBuffer[i * 1]);
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger1BytesBEToIntReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements IntBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger1BytesBEToIntReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public int read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = (int)(tempBuffer[0]);
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = (int)(tempBuffer[i * 1]);
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger1BytesLEToIntReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements IntBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger1BytesLEToIntReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public int read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = (int)(tempBuffer[0] & 0xFF);
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = (int)(tempBuffer[i * 1] & 0xFF);
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger1BytesBEToIntReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements IntBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger1BytesBEToIntReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public int read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = (int)(tempBuffer[0] & 0xFF);
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = (int)(tempBuffer[i * 1] & 0xFF);
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger1BytesLEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger1BytesLEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = (long)(tempBuffer[0]);
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = (long)(tempBuffer[i * 1]);
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger1BytesBEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger1BytesBEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = (long)(tempBuffer[0]);
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = (long)(tempBuffer[i * 1]);
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger1BytesLEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger1BytesLEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = (long)(tempBuffer[0] & 0xFF);
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = (long)(tempBuffer[i * 1] & 0xFF);
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger1BytesBEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger1BytesBEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = (long)(tempBuffer[0] & 0xFF);
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = (long)(tempBuffer[i * 1] & 0xFF);
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger2BytesLEToShortReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements ShortBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger2BytesLEToShortReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public short read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = Bytes.getLittleShort(tempBuffer, 0);
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(short[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = Bytes.getLittleShort(tempBuffer, i * 2);
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger2BytesBEToShortReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements ShortBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger2BytesBEToShortReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public short read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = Bytes.getBigShort(tempBuffer, 0);
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(short[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = Bytes.getBigShort(tempBuffer, i * 2);
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger2BytesLEToCharReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements CharBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger2BytesLEToCharReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public char read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				char v = reinterpretLowBitsAsU16(Bytes.getLittleShort(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(char[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				char v = reinterpretLowBitsAsU16(Bytes.getLittleShort(tempBuffer, i * 2));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger2BytesBEToCharReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements CharBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger2BytesBEToCharReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public char read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				char v = reinterpretLowBitsAsU16(Bytes.getBigShort(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(char[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				char v = reinterpretLowBitsAsU16(Bytes.getBigShort(tempBuffer, i * 2));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger2BytesLEToIntReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements IntBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger2BytesLEToIntReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public int read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = reinterpretLowBitsAsS16(Bytes.getLittleShort(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = reinterpretLowBitsAsS16(Bytes.getLittleShort(tempBuffer, i * 2));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger2BytesBEToIntReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements IntBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger2BytesBEToIntReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public int read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = reinterpretLowBitsAsS16(Bytes.getBigShort(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = reinterpretLowBitsAsS16(Bytes.getBigShort(tempBuffer, i * 2));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger2BytesLEToIntReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements IntBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger2BytesLEToIntReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public int read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = reinterpretLowBitsAsU16(Bytes.getLittleShort(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = reinterpretLowBitsAsU16(Bytes.getLittleShort(tempBuffer, i * 2));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger2BytesBEToIntReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements IntBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger2BytesBEToIntReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public int read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = reinterpretLowBitsAsU16(Bytes.getBigShort(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = reinterpretLowBitsAsU16(Bytes.getBigShort(tempBuffer, i * 2));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger2BytesLEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger2BytesLEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsS16(Bytes.getLittleShort(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsS16(Bytes.getLittleShort(tempBuffer, i * 2));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger2BytesBEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger2BytesBEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsS16(Bytes.getBigShort(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsS16(Bytes.getBigShort(tempBuffer, i * 2));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger2BytesLEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger2BytesLEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsU16(Bytes.getLittleShort(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsU16(Bytes.getLittleShort(tempBuffer, i * 2));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger2BytesBEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger2BytesBEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsU16(Bytes.getBigShort(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsU16(Bytes.getBigShort(tempBuffer, i * 2));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger3BytesLEToIntReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements IntBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger3BytesLEToIntReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public int read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = reinterpretLowBitsAsS24(Bytes.getLittleSInt24(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = reinterpretLowBitsAsS24(Bytes.getLittleSInt24(tempBuffer, i * 3));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger3BytesBEToIntReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements IntBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger3BytesBEToIntReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public int read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = reinterpretLowBitsAsS24(Bytes.getBigSInt24(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = reinterpretLowBitsAsS24(Bytes.getBigSInt24(tempBuffer, i * 3));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger3BytesLEToIntReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements IntBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger3BytesLEToIntReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public int read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = reinterpretLowBitsAsU24(Bytes.getLittleUInt24(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = reinterpretLowBitsAsU24(Bytes.getLittleUInt24(tempBuffer, i * 3));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger3BytesBEToIntReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements IntBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger3BytesBEToIntReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public int read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = reinterpretLowBitsAsU24(Bytes.getBigUInt24(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = reinterpretLowBitsAsU24(Bytes.getBigUInt24(tempBuffer, i * 3));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger3BytesLEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger3BytesLEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsS24(Bytes.getLittleSInt24(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsS24(Bytes.getLittleSInt24(tempBuffer, i * 3));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger3BytesBEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger3BytesBEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsS24(Bytes.getBigSInt24(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsS24(Bytes.getBigSInt24(tempBuffer, i * 3));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger3BytesLEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger3BytesLEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsU24(Bytes.getLittleUInt24(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsU24(Bytes.getLittleUInt24(tempBuffer, i * 3));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger3BytesBEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger3BytesBEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsU24(Bytes.getBigUInt24(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsU24(Bytes.getBigUInt24(tempBuffer, i * 3));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger4BytesLEToIntReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements IntBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger4BytesLEToIntReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public int read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getLittleInt(tempBuffer, 0);
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getLittleInt(tempBuffer, i * 4);
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger4BytesBEToIntReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements IntBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger4BytesBEToIntReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public int read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getBigInt(tempBuffer, 0);
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getBigInt(tempBuffer, i * 4);
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger4BytesLEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger4BytesLEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsS32(Bytes.getLittleInt(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsS32(Bytes.getLittleInt(tempBuffer, i * 4));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger4BytesBEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger4BytesBEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsS32(Bytes.getBigInt(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsS32(Bytes.getBigInt(tempBuffer, i * 4));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger4BytesLEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger4BytesLEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsU32(Bytes.getLittleInt(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsU32(Bytes.getLittleInt(tempBuffer, i * 4));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger4BytesBEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger4BytesBEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsU32(Bytes.getBigInt(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsU32(Bytes.getBigInt(tempBuffer, i * 4));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger5BytesLEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger5BytesLEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 5;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 5);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 5;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsS40(Bytes.getLittleSLong40(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 5;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 5);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsS40(Bytes.getLittleSLong40(tempBuffer, i * 5));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger5BytesBEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger5BytesBEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 5;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 5);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 5;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsS40(Bytes.getBigSLong40(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 5;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 5);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsS40(Bytes.getBigSLong40(tempBuffer, i * 5));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger5BytesLEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger5BytesLEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 5;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 5);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 5;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsU40(Bytes.getLittleULong40(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 5;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 5);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsU40(Bytes.getLittleULong40(tempBuffer, i * 5));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger5BytesBEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger5BytesBEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 5;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 5);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 5;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsU40(Bytes.getBigULong40(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 5;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 5);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsU40(Bytes.getBigULong40(tempBuffer, i * 5));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger6BytesLEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger6BytesLEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 6;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 6);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 6;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsS48(Bytes.getLittleSLong48(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 6;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 6);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsS48(Bytes.getLittleSLong48(tempBuffer, i * 6));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger6BytesBEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger6BytesBEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 6;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 6);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 6;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsS48(Bytes.getBigSLong48(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 6;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 6);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsS48(Bytes.getBigSLong48(tempBuffer, i * 6));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger6BytesLEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger6BytesLEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 6;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 6);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 6;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsU48(Bytes.getLittleULong48(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 6;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 6);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsU48(Bytes.getLittleULong48(tempBuffer, i * 6));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger6BytesBEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger6BytesBEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 6;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 6);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 6;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsU48(Bytes.getBigULong48(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 6;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 6);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsU48(Bytes.getBigULong48(tempBuffer, i * 6));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger7BytesLEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger7BytesLEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 7;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 7);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 7;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsS56(Bytes.getLittleSLong56(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 7;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 7);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsS56(Bytes.getLittleSLong56(tempBuffer, i * 7));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger7BytesBEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger7BytesBEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 7;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 7);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 7;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsS56(Bytes.getBigSLong56(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 7;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 7);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsS56(Bytes.getBigSLong56(tempBuffer, i * 7));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger7BytesLEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger7BytesLEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 7;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 7);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 7;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsU56(Bytes.getLittleULong56(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 7;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 7);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsU56(Bytes.getLittleULong56(tempBuffer, i * 7));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnsignedInteger7BytesBEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnsignedInteger7BytesBEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 7;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 7);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 7;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = reinterpretLowBitsAsU56(Bytes.getBigULong56(tempBuffer, 0));
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 7;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 7);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = reinterpretLowBitsAsU56(Bytes.getBigULong56(tempBuffer, i * 7));
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger8BytesLEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger8BytesLEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 8;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 8);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 8;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = Bytes.getLittleLong(tempBuffer, 0);
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 8;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 8);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = Bytes.getLittleLong(tempBuffer, i * 8);
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class SignedInteger8BytesBEToLongReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public SignedInteger8BytesBEToLongReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 8;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 8);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 8;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				long v = Bytes.getBigLong(tempBuffer, 0);
				
				return v;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 8;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 8);  //short reads only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				long v = Bytes.getBigLong(tempBuffer, i * 8);
				
				buffer[i] = v;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	python
	
	
	s = """
	
	
	
	
	
	
	
	
	
	public static class _$$PhysPrim$$_To_$$bytelen$$_Bytes_$$E$$_ReadStream
	extends AbstractStreamDecorator<_$$PhysPrim$$_BlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public _$$PhysPrim$$_To_$$bytelen$$_Bytes_$$E$$_ReadStream(_$$PhysPrim$$_BlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / _$$bytelen$$_);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected _$$physprim$$_[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / _$$bytelen$$_;
			
			
			_$$physprim$$_[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new _$$physprim$$_[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				_$$physprim$$_ v = tempBuffer[i];
				_$$Putter$$_
			}
			
			return a * _$$bytelen$$_;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	"""
	
	
	
	
	p(expandfulltemplateReverseInts(s))
	
	 */
	
	
	
	
	
	
	
	
	
	
	public static class ShortTo1BytesLEReadStream
	extends AbstractStreamDecorator<ShortBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public ShortTo1BytesLEReadStream(ShortBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 1);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected short[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 1;
			
			
			short[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new short[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				short v = tempBuffer[i];
				buffer[i] = (byte)v;
			}
			
			return a * 1;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ShortTo1BytesBEReadStream
	extends AbstractStreamDecorator<ShortBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public ShortTo1BytesBEReadStream(ShortBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 1);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected short[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 1;
			
			
			short[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new short[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				short v = tempBuffer[i];
				buffer[i] = (byte)v;
			}
			
			return a * 1;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class CharTo1BytesLEReadStream
	extends AbstractStreamDecorator<CharBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public CharTo1BytesLEReadStream(CharBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 1);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected char[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 1;
			
			
			char[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new char[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				char v = tempBuffer[i];
				buffer[i] = (byte)v;
			}
			
			return a * 1;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class CharTo1BytesBEReadStream
	extends AbstractStreamDecorator<CharBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public CharTo1BytesBEReadStream(CharBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 1);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected char[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 1;
			
			
			char[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new char[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				char v = tempBuffer[i];
				buffer[i] = (byte)v;
			}
			
			return a * 1;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class IntTo1BytesLEReadStream
	extends AbstractStreamDecorator<IntBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public IntTo1BytesLEReadStream(IntBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 1);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected int[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 1;
			
			
			int[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new int[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				int v = tempBuffer[i];
				buffer[i] = (byte)v;
			}
			
			return a * 1;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class IntTo1BytesBEReadStream
	extends AbstractStreamDecorator<IntBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public IntTo1BytesBEReadStream(IntBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 1);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected int[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 1;
			
			
			int[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new int[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				int v = tempBuffer[i];
				buffer[i] = (byte)v;
			}
			
			return a * 1;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class LongTo1BytesLEReadStream
	extends AbstractStreamDecorator<LongBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public LongTo1BytesLEReadStream(LongBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 1);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected long[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 1;
			
			
			long[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new long[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				long v = tempBuffer[i];
				buffer[i] = (byte)v;
			}
			
			return a * 1;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class LongTo1BytesBEReadStream
	extends AbstractStreamDecorator<LongBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public LongTo1BytesBEReadStream(LongBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 1);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected long[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 1;
			
			
			long[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new long[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				long v = tempBuffer[i];
				buffer[i] = (byte)v;
			}
			
			return a * 1;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ShortTo2BytesLEReadStream
	extends AbstractStreamDecorator<ShortBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public ShortTo2BytesLEReadStream(ShortBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 2);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected short[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 2;
			
			
			short[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new short[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				short v = tempBuffer[i];
				Bytes.putLittleShort(buffer, offset + i * 2, (short)v);
			}
			
			return a * 2;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ShortTo2BytesBEReadStream
	extends AbstractStreamDecorator<ShortBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public ShortTo2BytesBEReadStream(ShortBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 2);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected short[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 2;
			
			
			short[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new short[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				short v = tempBuffer[i];
				Bytes.putBigShort(buffer, offset + i * 2, (short)v);
			}
			
			return a * 2;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class CharTo2BytesLEReadStream
	extends AbstractStreamDecorator<CharBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public CharTo2BytesLEReadStream(CharBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 2);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected char[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 2;
			
			
			char[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new char[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				char v = tempBuffer[i];
				Bytes.putLittleShort(buffer, offset + i * 2, (short)v);
			}
			
			return a * 2;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class CharTo2BytesBEReadStream
	extends AbstractStreamDecorator<CharBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public CharTo2BytesBEReadStream(CharBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 2);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected char[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 2;
			
			
			char[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new char[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				char v = tempBuffer[i];
				Bytes.putBigShort(buffer, offset + i * 2, (short)v);
			}
			
			return a * 2;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class IntTo2BytesLEReadStream
	extends AbstractStreamDecorator<IntBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public IntTo2BytesLEReadStream(IntBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 2);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected int[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 2;
			
			
			int[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new int[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				int v = tempBuffer[i];
				Bytes.putLittleShort(buffer, offset + i * 2, (short)v);
			}
			
			return a * 2;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class IntTo2BytesBEReadStream
	extends AbstractStreamDecorator<IntBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public IntTo2BytesBEReadStream(IntBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 2);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected int[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 2;
			
			
			int[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new int[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				int v = tempBuffer[i];
				Bytes.putBigShort(buffer, offset + i * 2, (short)v);
			}
			
			return a * 2;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class LongTo2BytesLEReadStream
	extends AbstractStreamDecorator<LongBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public LongTo2BytesLEReadStream(LongBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 2);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected long[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 2;
			
			
			long[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new long[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				long v = tempBuffer[i];
				Bytes.putLittleShort(buffer, offset + i * 2, (short)v);
			}
			
			return a * 2;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class LongTo2BytesBEReadStream
	extends AbstractStreamDecorator<LongBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public LongTo2BytesBEReadStream(LongBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 2);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected long[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 2;
			
			
			long[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new long[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				long v = tempBuffer[i];
				Bytes.putBigShort(buffer, offset + i * 2, (short)v);
			}
			
			return a * 2;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class IntTo3BytesLEReadStream
	extends AbstractStreamDecorator<IntBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public IntTo3BytesLEReadStream(IntBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 3);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected int[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 3;
			
			
			int[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new int[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				int v = tempBuffer[i];
				Bytes.putLittleInt24(buffer, offset + i * 3, (int)v);
			}
			
			return a * 3;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class IntTo3BytesBEReadStream
	extends AbstractStreamDecorator<IntBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public IntTo3BytesBEReadStream(IntBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 3);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected int[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 3;
			
			
			int[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new int[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				int v = tempBuffer[i];
				Bytes.putBigInt24(buffer, offset + i * 3, (int)v);
			}
			
			return a * 3;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class LongTo3BytesLEReadStream
	extends AbstractStreamDecorator<LongBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public LongTo3BytesLEReadStream(LongBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 3);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected long[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 3;
			
			
			long[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new long[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				long v = tempBuffer[i];
				Bytes.putLittleInt24(buffer, offset + i * 3, (int)v);
			}
			
			return a * 3;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class LongTo3BytesBEReadStream
	extends AbstractStreamDecorator<LongBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public LongTo3BytesBEReadStream(LongBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 3);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected long[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 3;
			
			
			long[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new long[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				long v = tempBuffer[i];
				Bytes.putBigInt24(buffer, offset + i * 3, (int)v);
			}
			
			return a * 3;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class IntTo4BytesLEReadStream
	extends AbstractStreamDecorator<IntBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public IntTo4BytesLEReadStream(IntBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 4);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected int[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 4;
			
			
			int[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new int[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				int v = tempBuffer[i];
				Bytes.putLittleInt(buffer, offset + i * 4, (int)v);
			}
			
			return a * 4;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class IntTo4BytesBEReadStream
	extends AbstractStreamDecorator<IntBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public IntTo4BytesBEReadStream(IntBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 4);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected int[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 4;
			
			
			int[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new int[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				int v = tempBuffer[i];
				Bytes.putBigInt(buffer, offset + i * 4, (int)v);
			}
			
			return a * 4;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class LongTo4BytesLEReadStream
	extends AbstractStreamDecorator<LongBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public LongTo4BytesLEReadStream(LongBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 4);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected long[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 4;
			
			
			long[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new long[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				long v = tempBuffer[i];
				Bytes.putLittleInt(buffer, offset + i * 4, (int)v);
			}
			
			return a * 4;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class LongTo4BytesBEReadStream
	extends AbstractStreamDecorator<LongBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public LongTo4BytesBEReadStream(LongBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 4);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected long[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 4;
			
			
			long[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new long[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				long v = tempBuffer[i];
				Bytes.putBigInt(buffer, offset + i * 4, (int)v);
			}
			
			return a * 4;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class LongTo5BytesLEReadStream
	extends AbstractStreamDecorator<LongBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public LongTo5BytesLEReadStream(LongBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 5);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected long[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 5;
			
			
			long[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new long[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				long v = tempBuffer[i];
				Bytes.putLittleLong40(buffer, offset + i * 5, (long)v);
			}
			
			return a * 5;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class LongTo5BytesBEReadStream
	extends AbstractStreamDecorator<LongBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public LongTo5BytesBEReadStream(LongBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 5);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected long[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 5;
			
			
			long[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new long[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				long v = tempBuffer[i];
				Bytes.putBigLong40(buffer, offset + i * 5, (long)v);
			}
			
			return a * 5;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class LongTo6BytesLEReadStream
	extends AbstractStreamDecorator<LongBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public LongTo6BytesLEReadStream(LongBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 6);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected long[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 6;
			
			
			long[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new long[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				long v = tempBuffer[i];
				Bytes.putLittleLong48(buffer, offset + i * 6, (long)v);
			}
			
			return a * 6;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class LongTo6BytesBEReadStream
	extends AbstractStreamDecorator<LongBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public LongTo6BytesBEReadStream(LongBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 6);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected long[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 6;
			
			
			long[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new long[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				long v = tempBuffer[i];
				Bytes.putBigLong48(buffer, offset + i * 6, (long)v);
			}
			
			return a * 6;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class LongTo7BytesLEReadStream
	extends AbstractStreamDecorator<LongBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public LongTo7BytesLEReadStream(LongBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 7);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected long[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 7;
			
			
			long[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new long[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				long v = tempBuffer[i];
				Bytes.putLittleLong56(buffer, offset + i * 7, (long)v);
			}
			
			return a * 7;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class LongTo7BytesBEReadStream
	extends AbstractStreamDecorator<LongBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public LongTo7BytesBEReadStream(LongBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 7);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected long[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 7;
			
			
			long[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new long[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				long v = tempBuffer[i];
				Bytes.putBigLong56(buffer, offset + i * 7, (long)v);
			}
			
			return a * 7;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class LongTo8BytesLEReadStream
	extends AbstractStreamDecorator<LongBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public LongTo8BytesLEReadStream(LongBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 8);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected long[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 8;
			
			
			long[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new long[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				long v = tempBuffer[i];
				Bytes.putLittleLong(buffer, offset + i * 8, (long)v);
			}
			
			return a * 8;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class LongTo8BytesBEReadStream
	extends AbstractStreamDecorator<LongBlockReadStream>
	implements ByteBlockReadStream, Trimmable
	{
		public LongTo8BytesBEReadStream(LongBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return underlying.skip(amount / 8);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new ImpossibleException();
		}
		
		
		
		
		protected long[] tempBuffer;
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			int lengthInElements = length / 8;
			
			
			long[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < lengthInElements)
			{
				tempBuffer = new long[lengthInElements];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, lengthInElements);
			
			for (int i = 0; i < a; i++)
			{
				long v = tempBuffer[i];
				Bytes.putBigLong(buffer, offset + i * 8, (long)v);
			}
			
			return a * 8;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	python
	
	
	s = """
	
	
	
	
	
	public static class _$$Normalizedness$$__$$SignednessFormat$$_Integer_$$bytelen$$_Bytes_$$E$$_To_$$DestFloatPrim$$_ReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements _$$DestFloatPrim$$_BlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public _$$Normalizedness$$__$$SignednessFormat$$_Integer_$$bytelen$$_Bytes_$$E$$_To_$$DestFloatPrim$$_ReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * _$$bytelen$$_;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, _$$bytelen$$_);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public _$$destfloatprim$$_ read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = _$$bytelen$$_;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				_$$GetterOpener$$_0_$$GetterCloser$$_
				
				_$$OffsetToTwosComplementCodeIfInputIsOffset$$_
				
				return ((_$$destfloatprim$$_)v)_$$DivisionIfNormalized$$_;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(_$$destfloatprim$$_[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * _$$bytelen$$_;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, _$$bytelen$$_);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				_$$GetterOpener$$_i * _$$bytelen$$__$$GetterCloser$$_
				
				_$$OffsetToTwosComplementCodeIfInputIsOffset$$_
				
				buffer[i] = ((_$$destfloatprim$$_)v)_$$DivisionIfNormalized$$_;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	"""
	
	
	
	
	
	
	p(expandfulltemplateFloats(s))
	
	 */
	
	
	
	
	
	
	public static class UnnormalizedTwosComplementInteger1BytesLEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedTwosComplementInteger1BytesLEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				byte v = tempBuffer[0];
				
				
				
				return ((float)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				byte v = tempBuffer[i * 1];
				
				
				
				buffer[i] = ((float)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1TwosComplementInteger1BytesLEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1TwosComplementInteger1BytesLEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				byte v = tempBuffer[0];
				
				
				
				return ((float)v) / 128f;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				byte v = tempBuffer[i * 1];
				
				
				
				buffer[i] = ((float)v) / 128f;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedTwosComplementInteger1BytesBEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedTwosComplementInteger1BytesBEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				byte v = tempBuffer[0];
				
				
				
				return ((float)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				byte v = tempBuffer[i * 1];
				
				
				
				buffer[i] = ((float)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1TwosComplementInteger1BytesBEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1TwosComplementInteger1BytesBEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				byte v = tempBuffer[0];
				
				
				
				return ((float)v) / 128f;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				byte v = tempBuffer[i * 1];
				
				
				
				buffer[i] = ((float)v) / 128f;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedOffsetInteger1BytesLEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedOffsetInteger1BytesLEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				byte v = tempBuffer[0];
				
				v -= (byte)128l;
				
				return ((float)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				byte v = tempBuffer[i * 1];
				
				v -= (byte)128l;
				
				buffer[i] = ((float)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1OffsetInteger1BytesLEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1OffsetInteger1BytesLEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				byte v = tempBuffer[0];
				
				v -= (byte)128l;
				
				return ((float)v) / 128f;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				byte v = tempBuffer[i * 1];
				
				v -= (byte)128l;
				
				buffer[i] = ((float)v) / 128f;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedOffsetInteger1BytesBEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedOffsetInteger1BytesBEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				byte v = tempBuffer[0];
				
				v -= (byte)128l;
				
				return ((float)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				byte v = tempBuffer[i * 1];
				
				v -= (byte)128l;
				
				buffer[i] = ((float)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1OffsetInteger1BytesBEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1OffsetInteger1BytesBEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				byte v = tempBuffer[0];
				
				v -= (byte)128l;
				
				return ((float)v) / 128f;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				byte v = tempBuffer[i * 1];
				
				v -= (byte)128l;
				
				buffer[i] = ((float)v) / 128f;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedTwosComplementInteger2BytesLEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedTwosComplementInteger2BytesLEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = Bytes.getLittleShort(tempBuffer, 0);
				
				
				
				return ((float)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = Bytes.getLittleShort(tempBuffer, i * 2);
				
				
				
				buffer[i] = ((float)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1TwosComplementInteger2BytesLEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1TwosComplementInteger2BytesLEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = Bytes.getLittleShort(tempBuffer, 0);
				
				
				
				return ((float)v) / 32768f;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = Bytes.getLittleShort(tempBuffer, i * 2);
				
				
				
				buffer[i] = ((float)v) / 32768f;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedTwosComplementInteger2BytesBEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedTwosComplementInteger2BytesBEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = Bytes.getBigShort(tempBuffer, 0);
				
				
				
				return ((float)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = Bytes.getBigShort(tempBuffer, i * 2);
				
				
				
				buffer[i] = ((float)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1TwosComplementInteger2BytesBEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1TwosComplementInteger2BytesBEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = Bytes.getBigShort(tempBuffer, 0);
				
				
				
				return ((float)v) / 32768f;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = Bytes.getBigShort(tempBuffer, i * 2);
				
				
				
				buffer[i] = ((float)v) / 32768f;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedOffsetInteger2BytesLEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedOffsetInteger2BytesLEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = Bytes.getLittleShort(tempBuffer, 0);
				
				v -= (short)32768l;
				
				return ((float)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = Bytes.getLittleShort(tempBuffer, i * 2);
				
				v -= (short)32768l;
				
				buffer[i] = ((float)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1OffsetInteger2BytesLEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1OffsetInteger2BytesLEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = Bytes.getLittleShort(tempBuffer, 0);
				
				v -= (short)32768l;
				
				return ((float)v) / 32768f;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = Bytes.getLittleShort(tempBuffer, i * 2);
				
				v -= (short)32768l;
				
				buffer[i] = ((float)v) / 32768f;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedOffsetInteger2BytesBEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedOffsetInteger2BytesBEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = Bytes.getBigShort(tempBuffer, 0);
				
				v -= (short)32768l;
				
				return ((float)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = Bytes.getBigShort(tempBuffer, i * 2);
				
				v -= (short)32768l;
				
				buffer[i] = ((float)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1OffsetInteger2BytesBEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1OffsetInteger2BytesBEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = Bytes.getBigShort(tempBuffer, 0);
				
				v -= (short)32768l;
				
				return ((float)v) / 32768f;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = Bytes.getBigShort(tempBuffer, i * 2);
				
				v -= (short)32768l;
				
				buffer[i] = ((float)v) / 32768f;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedTwosComplementInteger3BytesLEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedTwosComplementInteger3BytesLEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getLittleSInt24(tempBuffer, 0);
				
				
				
				return ((float)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getLittleSInt24(tempBuffer, i * 3);
				
				
				
				buffer[i] = ((float)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1TwosComplementInteger3BytesLEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1TwosComplementInteger3BytesLEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getLittleSInt24(tempBuffer, 0);
				
				
				
				return ((float)v) / 8388608f;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getLittleSInt24(tempBuffer, i * 3);
				
				
				
				buffer[i] = ((float)v) / 8388608f;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedTwosComplementInteger3BytesBEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedTwosComplementInteger3BytesBEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getBigSInt24(tempBuffer, 0);
				
				
				
				return ((float)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getBigSInt24(tempBuffer, i * 3);
				
				
				
				buffer[i] = ((float)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1TwosComplementInteger3BytesBEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1TwosComplementInteger3BytesBEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getBigSInt24(tempBuffer, 0);
				
				
				
				return ((float)v) / 8388608f;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getBigSInt24(tempBuffer, i * 3);
				
				
				
				buffer[i] = ((float)v) / 8388608f;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedOffsetInteger3BytesLEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedOffsetInteger3BytesLEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getLittleSInt24(tempBuffer, 0);
				
				v -= (int)8388608l;
				
				return ((float)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getLittleSInt24(tempBuffer, i * 3);
				
				v -= (int)8388608l;
				
				buffer[i] = ((float)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1OffsetInteger3BytesLEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1OffsetInteger3BytesLEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getLittleSInt24(tempBuffer, 0);
				
				v -= (int)8388608l;
				
				return ((float)v) / 8388608f;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getLittleSInt24(tempBuffer, i * 3);
				
				v -= (int)8388608l;
				
				buffer[i] = ((float)v) / 8388608f;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedOffsetInteger3BytesBEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedOffsetInteger3BytesBEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getBigSInt24(tempBuffer, 0);
				
				v -= (int)8388608l;
				
				return ((float)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getBigSInt24(tempBuffer, i * 3);
				
				v -= (int)8388608l;
				
				buffer[i] = ((float)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1OffsetInteger3BytesBEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1OffsetInteger3BytesBEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getBigSInt24(tempBuffer, 0);
				
				v -= (int)8388608l;
				
				return ((float)v) / 8388608f;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getBigSInt24(tempBuffer, i * 3);
				
				v -= (int)8388608l;
				
				buffer[i] = ((float)v) / 8388608f;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedTwosComplementInteger4BytesLEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedTwosComplementInteger4BytesLEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getLittleInt(tempBuffer, 0);
				
				
				
				return ((float)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getLittleInt(tempBuffer, i * 4);
				
				
				
				buffer[i] = ((float)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1TwosComplementInteger4BytesLEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1TwosComplementInteger4BytesLEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getLittleInt(tempBuffer, 0);
				
				
				
				return ((float)v) / 2147483648f;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getLittleInt(tempBuffer, i * 4);
				
				
				
				buffer[i] = ((float)v) / 2147483648f;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedTwosComplementInteger4BytesBEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedTwosComplementInteger4BytesBEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getBigInt(tempBuffer, 0);
				
				
				
				return ((float)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getBigInt(tempBuffer, i * 4);
				
				
				
				buffer[i] = ((float)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1TwosComplementInteger4BytesBEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1TwosComplementInteger4BytesBEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getBigInt(tempBuffer, 0);
				
				
				
				return ((float)v) / 2147483648f;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getBigInt(tempBuffer, i * 4);
				
				
				
				buffer[i] = ((float)v) / 2147483648f;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedOffsetInteger4BytesLEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedOffsetInteger4BytesLEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getLittleInt(tempBuffer, 0);
				
				v -= (int)2147483648l;
				
				return ((float)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getLittleInt(tempBuffer, i * 4);
				
				v -= (int)2147483648l;
				
				buffer[i] = ((float)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1OffsetInteger4BytesLEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1OffsetInteger4BytesLEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getLittleInt(tempBuffer, 0);
				
				v -= (int)2147483648l;
				
				return ((float)v) / 2147483648f;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getLittleInt(tempBuffer, i * 4);
				
				v -= (int)2147483648l;
				
				buffer[i] = ((float)v) / 2147483648f;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedOffsetInteger4BytesBEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedOffsetInteger4BytesBEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getBigInt(tempBuffer, 0);
				
				v -= (int)2147483648l;
				
				return ((float)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getBigInt(tempBuffer, i * 4);
				
				v -= (int)2147483648l;
				
				buffer[i] = ((float)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1OffsetInteger4BytesBEToFloatReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1OffsetInteger4BytesBEToFloatReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getBigInt(tempBuffer, 0);
				
				v -= (int)2147483648l;
				
				return ((float)v) / 2147483648f;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getBigInt(tempBuffer, i * 4);
				
				v -= (int)2147483648l;
				
				buffer[i] = ((float)v) / 2147483648f;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedTwosComplementInteger1BytesLEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedTwosComplementInteger1BytesLEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				byte v = tempBuffer[0];
				
				
				
				return ((double)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				byte v = tempBuffer[i * 1];
				
				
				
				buffer[i] = ((double)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1TwosComplementInteger1BytesLEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1TwosComplementInteger1BytesLEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				byte v = tempBuffer[0];
				
				
				
				return ((double)v) / 128d;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				byte v = tempBuffer[i * 1];
				
				
				
				buffer[i] = ((double)v) / 128d;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedTwosComplementInteger1BytesBEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedTwosComplementInteger1BytesBEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				byte v = tempBuffer[0];
				
				
				
				return ((double)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				byte v = tempBuffer[i * 1];
				
				
				
				buffer[i] = ((double)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1TwosComplementInteger1BytesBEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1TwosComplementInteger1BytesBEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				byte v = tempBuffer[0];
				
				
				
				return ((double)v) / 128d;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				byte v = tempBuffer[i * 1];
				
				
				
				buffer[i] = ((double)v) / 128d;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedOffsetInteger1BytesLEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedOffsetInteger1BytesLEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				byte v = tempBuffer[0];
				
				v -= (byte)128l;
				
				return ((double)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				byte v = tempBuffer[i * 1];
				
				v -= (byte)128l;
				
				buffer[i] = ((double)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1OffsetInteger1BytesLEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1OffsetInteger1BytesLEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				byte v = tempBuffer[0];
				
				v -= (byte)128l;
				
				return ((double)v) / 128d;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				byte v = tempBuffer[i * 1];
				
				v -= (byte)128l;
				
				buffer[i] = ((double)v) / 128d;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedOffsetInteger1BytesBEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedOffsetInteger1BytesBEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				byte v = tempBuffer[0];
				
				v -= (byte)128l;
				
				return ((double)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				byte v = tempBuffer[i * 1];
				
				v -= (byte)128l;
				
				buffer[i] = ((double)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1OffsetInteger1BytesBEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1OffsetInteger1BytesBEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 1;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				byte v = tempBuffer[0];
				
				v -= (byte)128l;
				
				return ((double)v) / 128d;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 1;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 1);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				byte v = tempBuffer[i * 1];
				
				v -= (byte)128l;
				
				buffer[i] = ((double)v) / 128d;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedTwosComplementInteger2BytesLEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedTwosComplementInteger2BytesLEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = Bytes.getLittleShort(tempBuffer, 0);
				
				
				
				return ((double)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = Bytes.getLittleShort(tempBuffer, i * 2);
				
				
				
				buffer[i] = ((double)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1TwosComplementInteger2BytesLEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1TwosComplementInteger2BytesLEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = Bytes.getLittleShort(tempBuffer, 0);
				
				
				
				return ((double)v) / 32768d;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = Bytes.getLittleShort(tempBuffer, i * 2);
				
				
				
				buffer[i] = ((double)v) / 32768d;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedTwosComplementInteger2BytesBEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedTwosComplementInteger2BytesBEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = Bytes.getBigShort(tempBuffer, 0);
				
				
				
				return ((double)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = Bytes.getBigShort(tempBuffer, i * 2);
				
				
				
				buffer[i] = ((double)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1TwosComplementInteger2BytesBEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1TwosComplementInteger2BytesBEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = Bytes.getBigShort(tempBuffer, 0);
				
				
				
				return ((double)v) / 32768d;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = Bytes.getBigShort(tempBuffer, i * 2);
				
				
				
				buffer[i] = ((double)v) / 32768d;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedOffsetInteger2BytesLEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedOffsetInteger2BytesLEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = Bytes.getLittleShort(tempBuffer, 0);
				
				v -= (short)32768l;
				
				return ((double)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = Bytes.getLittleShort(tempBuffer, i * 2);
				
				v -= (short)32768l;
				
				buffer[i] = ((double)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1OffsetInteger2BytesLEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1OffsetInteger2BytesLEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = Bytes.getLittleShort(tempBuffer, 0);
				
				v -= (short)32768l;
				
				return ((double)v) / 32768d;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = Bytes.getLittleShort(tempBuffer, i * 2);
				
				v -= (short)32768l;
				
				buffer[i] = ((double)v) / 32768d;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedOffsetInteger2BytesBEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedOffsetInteger2BytesBEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = Bytes.getBigShort(tempBuffer, 0);
				
				v -= (short)32768l;
				
				return ((double)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = Bytes.getBigShort(tempBuffer, i * 2);
				
				v -= (short)32768l;
				
				buffer[i] = ((double)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1OffsetInteger2BytesBEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1OffsetInteger2BytesBEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				short v = Bytes.getBigShort(tempBuffer, 0);
				
				v -= (short)32768l;
				
				return ((double)v) / 32768d;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				short v = Bytes.getBigShort(tempBuffer, i * 2);
				
				v -= (short)32768l;
				
				buffer[i] = ((double)v) / 32768d;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedTwosComplementInteger3BytesLEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedTwosComplementInteger3BytesLEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getLittleSInt24(tempBuffer, 0);
				
				
				
				return ((double)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getLittleSInt24(tempBuffer, i * 3);
				
				
				
				buffer[i] = ((double)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1TwosComplementInteger3BytesLEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1TwosComplementInteger3BytesLEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getLittleSInt24(tempBuffer, 0);
				
				
				
				return ((double)v) / 8388608d;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getLittleSInt24(tempBuffer, i * 3);
				
				
				
				buffer[i] = ((double)v) / 8388608d;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedTwosComplementInteger3BytesBEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedTwosComplementInteger3BytesBEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getBigSInt24(tempBuffer, 0);
				
				
				
				return ((double)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getBigSInt24(tempBuffer, i * 3);
				
				
				
				buffer[i] = ((double)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1TwosComplementInteger3BytesBEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1TwosComplementInteger3BytesBEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getBigSInt24(tempBuffer, 0);
				
				
				
				return ((double)v) / 8388608d;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getBigSInt24(tempBuffer, i * 3);
				
				
				
				buffer[i] = ((double)v) / 8388608d;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedOffsetInteger3BytesLEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedOffsetInteger3BytesLEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getLittleSInt24(tempBuffer, 0);
				
				v -= (int)8388608l;
				
				return ((double)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getLittleSInt24(tempBuffer, i * 3);
				
				v -= (int)8388608l;
				
				buffer[i] = ((double)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1OffsetInteger3BytesLEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1OffsetInteger3BytesLEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getLittleSInt24(tempBuffer, 0);
				
				v -= (int)8388608l;
				
				return ((double)v) / 8388608d;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getLittleSInt24(tempBuffer, i * 3);
				
				v -= (int)8388608l;
				
				buffer[i] = ((double)v) / 8388608d;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedOffsetInteger3BytesBEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedOffsetInteger3BytesBEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getBigSInt24(tempBuffer, 0);
				
				v -= (int)8388608l;
				
				return ((double)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getBigSInt24(tempBuffer, i * 3);
				
				v -= (int)8388608l;
				
				buffer[i] = ((double)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1OffsetInteger3BytesBEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1OffsetInteger3BytesBEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 3;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getBigSInt24(tempBuffer, 0);
				
				v -= (int)8388608l;
				
				return ((double)v) / 8388608d;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 3;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 3);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getBigSInt24(tempBuffer, i * 3);
				
				v -= (int)8388608l;
				
				buffer[i] = ((double)v) / 8388608d;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedTwosComplementInteger4BytesLEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedTwosComplementInteger4BytesLEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getLittleInt(tempBuffer, 0);
				
				
				
				return ((double)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getLittleInt(tempBuffer, i * 4);
				
				
				
				buffer[i] = ((double)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1TwosComplementInteger4BytesLEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1TwosComplementInteger4BytesLEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getLittleInt(tempBuffer, 0);
				
				
				
				return ((double)v) / 2147483648d;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getLittleInt(tempBuffer, i * 4);
				
				
				
				buffer[i] = ((double)v) / 2147483648d;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedTwosComplementInteger4BytesBEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedTwosComplementInteger4BytesBEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getBigInt(tempBuffer, 0);
				
				
				
				return ((double)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getBigInt(tempBuffer, i * 4);
				
				
				
				buffer[i] = ((double)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1TwosComplementInteger4BytesBEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1TwosComplementInteger4BytesBEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getBigInt(tempBuffer, 0);
				
				
				
				return ((double)v) / 2147483648d;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getBigInt(tempBuffer, i * 4);
				
				
				
				buffer[i] = ((double)v) / 2147483648d;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedOffsetInteger4BytesLEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedOffsetInteger4BytesLEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getLittleInt(tempBuffer, 0);
				
				v -= (int)2147483648l;
				
				return ((double)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getLittleInt(tempBuffer, i * 4);
				
				v -= (int)2147483648l;
				
				buffer[i] = ((double)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1OffsetInteger4BytesLEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1OffsetInteger4BytesLEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getLittleInt(tempBuffer, 0);
				
				v -= (int)2147483648l;
				
				return ((double)v) / 2147483648d;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getLittleInt(tempBuffer, i * 4);
				
				v -= (int)2147483648l;
				
				buffer[i] = ((double)v) / 2147483648d;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class UnnormalizedOffsetInteger4BytesBEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public UnnormalizedOffsetInteger4BytesBEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getBigInt(tempBuffer, 0);
				
				v -= (int)2147483648l;
				
				return ((double)v);
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getBigInt(tempBuffer, i * 4);
				
				v -= (int)2147483648l;
				
				buffer[i] = ((double)v);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1OffsetInteger4BytesBEToDoubleReadStream
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public NormalizedN1P1OffsetInteger4BytesBEToDoubleReadStream(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				int v = Bytes.getBigInt(tempBuffer, 0);
				
				v -= (int)2147483648l;
				
				return ((double)v) / 2147483648d;
			}
		}
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				int v = Bytes.getBigInt(tempBuffer, i * 4);
				
				v -= (int)2147483648l;
				
				buffer[i] = ((double)v) / 2147483648d;
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// ENcoders!!  :DDD
	
	/**
	 * N1P1 refers to {@link Normalization#NormalizedNegative1ToPositive1}, which is the only one supported currently, sry ^^'
	 */
	public static void convertDataDoubleFloatsToByteEncodedInteger_N1P1(Slice<double[]> asFloats, Slice<byte[]> asRaw, RealNumberAsLinearIntegerBytesFormat format)
	{
		int bytesPerSample = format.requireBytesPerSample();
		convertDataDoubleFloatsToByteEncodedInteger_N1P1(asFloats, asRaw, bytesPerSample, format.getSignednessFormat(), format.getEndianness());
	}
	
	
	public static void convertDataDoubleFloatsToByteEncodedInteger_N1P1(Slice<double[]> asFloats, Slice<byte[]> asRaw, int bytesPerSample, SignednessFormat signednessFormat, Endianness endianness)
	{
		int n = asFloats.getLength();
		
		if (asRaw.getLength() != n * bytesPerSample)
			throw new IllegalArgumentException();
		
		
		if (bytesPerSample == 1)
		{
			if (signednessFormat == SignednessFormat.TwosComplement)
			{
				for (int i = 0; i < n; i++)
				{
					double f = getDouble(asFloats, i);
					byte v = f == 1 ? 127 : (byte)(f * 128);
					setByte(asRaw, i*bytesPerSample, v);
				}
			}
			else
			{
				for (int i = 0; i < n; i++)
				{
					double f = getDouble(asFloats, i);
					byte v = f == 1 ? 127 : (byte)(f * 128);
					v += 128;
					setByte(asRaw, i*bytesPerSample, v);
				}
			}
		}
		
		
		else if (bytesPerSample == 2)
		{
			if (signednessFormat == SignednessFormat.TwosComplement)
			{
				if (endianness == Endianness.Little)
				{
					for (int i = 0; i < n; i++)
					{
						double f = getDouble(asFloats, i);
						short v = f == 1 ? 32767 : (short)(f * 32768);
						Bytes.putLittleShort(asRaw, i*bytesPerSample, v);
					}
				}
				else
				{
					for (int i = 0; i < n; i++)
					{
						double f = getDouble(asFloats, i);
						short v = f == 1 ? 32767 : (short)(f * 32768);
						Bytes.putBigShort(asRaw, i*bytesPerSample, v);
					}
				}
			}
			else
			{
				if (endianness == Endianness.Little)
				{
					for (int i = 0; i < n; i++)
					{
						double f = getDouble(asFloats, i);
						short v = f == 1 ? 32767 : (short)(f * 32768);
						v += 32768;
						Bytes.putLittleShort(asRaw, i*bytesPerSample, v);
					}
				}
				else
				{
					for (int i = 0; i < n; i++)
					{
						double f = getDouble(asFloats, i);
						short v = f == 1 ? 32767 : (short)(f * 32768);
						v += 32768;
						Bytes.putBigShort(asRaw, i*bytesPerSample, v);
					}
				}
			}
		}
		
		
		else if (bytesPerSample == 3)
		{
			if (signednessFormat == SignednessFormat.TwosComplement)
			{
				if (endianness == Endianness.Little)
				{
					for (int i = 0; i < n; i++)
					{
						double f = getDouble(asFloats, i);
						int v = f == 1 ? 8388607 : (int)(f * 8388608);
						Bytes.putLittleInt24(asRaw, i*bytesPerSample, v);
					}
				}
				else
				{
					for (int i = 0; i < n; i++)
					{
						double f = getDouble(asFloats, i);
						int v = f == 1 ? 8388607 : (int)(f * 8388608);
						Bytes.putBigInt24(asRaw, i*bytesPerSample, v);
					}
				}
			}
			else
			{
				if (endianness == Endianness.Little)
				{
					for (int i = 0; i < n; i++)
					{
						double f = getDouble(asFloats, i);
						int v = f == 1 ? 8388607 : (int)(f * 8388608);
						v += 8388608;
						Bytes.putLittleInt24(asRaw, i*bytesPerSample, v);
					}
				}
				else
				{
					for (int i = 0; i < n; i++)
					{
						double f = getDouble(asFloats, i);
						int v = f == 1 ? 8388607 : (int)(f * 8388608);
						v += 8388608;
						Bytes.putBigInt24(asRaw, i*bytesPerSample, v);
					}
				}
			}
		}
		
		
		else if (bytesPerSample == 4)
		{
			if (signednessFormat == SignednessFormat.TwosComplement)
			{
				if (endianness == Endianness.Little)
				{
					for (int i = 0; i < n; i++)
					{
						double f = getDouble(asFloats, i);
						int v = f == 1 ? 2147483647 : (int)(f * 2147483648l);
						Bytes.putLittleInt24(asRaw, i*bytesPerSample, v);
					}
				}
				else
				{
					for (int i = 0; i < n; i++)
					{
						double f = getDouble(asFloats, i);
						int v = f == 1 ? 2147483647 : (int)(f * 2147483648l);
						Bytes.putBigInt24(asRaw, i*bytesPerSample, v);
					}
				}
			}
			else
			{
				if (endianness == Endianness.Little)
				{
					for (int i = 0; i < n; i++)
					{
						double f = getDouble(asFloats, i);
						int v = f == 1 ? 2147483647 : (int)(f * 2147483648l);
						v += 2147483648l;
						Bytes.putLittleInt24(asRaw, i*bytesPerSample, v);
					}
				}
				else
				{
					for (int i = 0; i < n; i++)
					{
						double f = getDouble(asFloats, i);
						int v = f == 1 ? 2147483647 : (int)(f * 2147483648l);
						v += 2147483648l;
						Bytes.putBigInt24(asRaw, i*bytesPerSample, v);
					}
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class NormalizedN1P1DoubleToByteEncodedIntegerWriteStream
	extends AbstractStream
	implements DoubleBlockWriteStream
	{
		protected final ByteBlockWriteStream underlying;
		protected final int bytesPerSample;
		protected final SignednessFormat signednessFormat;
		protected final Endianness endianness;
		
		protected byte[] intermediateBuffer;
		
		
		public NormalizedN1P1DoubleToByteEncodedIntegerWriteStream(ByteBlockWriteStream underlying, RealNumberAsLinearIntegerBytesFormat format)
		{
			this(underlying, format.requireBytesPerSample(), format.getSignednessFormat(), format.getEndianness());
		}
		
		public NormalizedN1P1DoubleToByteEncodedIntegerWriteStream(ByteBlockWriteStream underlying, int bytesPerSample, SignednessFormat signednessFormat, Endianness endianness)
		{
			this.underlying = underlying;
			this.bytesPerSample = bytesPerSample;
			this.signednessFormat = signednessFormat;
			this.endianness = endianness;
		}
		
		
		
		protected void ensureBufferCapacity(int length)
		{
			if (this.intermediateBuffer == null || this.intermediateBuffer.length < length)
				this.intermediateBuffer = new byte[length];
		}
		
		@Override
		public int write(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			requireOpen();
			
			int lengthBytes = length * this.bytesPerSample;
			
			ensureBufferCapacity(lengthBytes);
			
			convertDataDoubleFloatsToByteEncodedInteger_N1P1(new Slice<double[]>(buffer, offset, length), new Slice<byte[]>(this.intermediateBuffer, 0, lengthBytes), this.bytesPerSample, this.signednessFormat, this.endianness);
			
			int amountWrittenBytes = this.underlying.write(this.intermediateBuffer, 0, lengthBytes);
			
			assert amountWrittenBytes >= 0;
			
			//If we use ceiling division, it might appear as having written all the samples and thus not convey EOF anymore!!  \o/
			
			return amountWrittenBytes / this.bytesPerSample;
		}
		
		
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			requireOpen();
			return this.underlying.skip(amount * this.bytesPerSample);
		}
		
		@Override
		protected void close0() throws IOException
		{
			this.underlying.close();
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.underlying.isEOF();
		}
		
		@Override
		public void flush() throws IOException, ClosedStreamException
		{
			requireOpen();
			this.underlying.flush();
		}
		
		
		
		
		
		
		
		double[] buf1 = new double[1];
		
		@Override
		public void write(double unit) throws EOFException, IOException, ClosedStreamException
		{
			this.buf1[0] = unit;
			write(this.buf1);
		}
	}
	
	
	
	
	
	
	
	
	
	
	public static ByteBlockReadStream converterReaderDoubleToByteEncodedInteger(DoubleBlockReadStream data, RealNumberAsLinearIntegerBytesFormat format)
	{
		ByteBlockReadStream m = new NormalizedN1P1DoubleToByteEncodedIntegerReadStreamOnlyForMultiples(data, format);
		ByteBlockReadStream c = new MultipleReadingByteBlockReadStreamDecorator(m, format.requireBytesPerSample());
		return c;
	}
	
	
	public static class NormalizedN1P1DoubleToByteEncodedIntegerReadStreamOnlyForMultiples
	extends AbstractStreamDecorator<DoubleBlockReadStream>
	implements ByteBlockReadStream
	{
		protected final int bytesPerSample;
		protected final SignednessFormat signednessFormat;
		protected final Endianness endianness;
		
		protected double[] intermediateBuffer;
		
		
		public NormalizedN1P1DoubleToByteEncodedIntegerReadStreamOnlyForMultiples(DoubleBlockReadStream underlying, RealNumberAsLinearIntegerBytesFormat format)
		{
			this(underlying, format.requireBytesPerSample(), format.getSignednessFormat(), format.getEndianness());
		}
		
		public NormalizedN1P1DoubleToByteEncodedIntegerReadStreamOnlyForMultiples(DoubleBlockReadStream underlying, int bytesPerSample, SignednessFormat signednessFormat, Endianness endianness)
		{
			super(underlying);
			this.bytesPerSample = bytesPerSample;
			this.signednessFormat = signednessFormat;
			this.endianness = endianness;
		}
		
		
		
		protected void ensureBufferCapacity(int length)
		{
			if (this.intermediateBuffer == null || this.intermediateBuffer.length < length)
				this.intermediateBuffer = new double[length];
		}
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (length % this.bytesPerSample != 0)
				throw new IllegalArgumentException("We only support reading in multiples of "+this.bytesPerSample+" sorry ^^'");
			
			int lengthElements = length / this.bytesPerSample;
			
			ensureBufferCapacity(lengthElements);
			
			int amountReadElements = this.underlying.read(this.intermediateBuffer, 0, lengthElements);
			
			if (amountReadElements != lengthElements)
			{
				if (!this.underlying.isEOF())
					throw new ImpossibleException();
			}
			
			convertDataDoubleFloatsToByteEncodedInteger_N1P1(new Slice<double[]>(this.intermediateBuffer, 0, amountReadElements), new Slice<byte[]>(buffer, offset, amountReadElements*this.bytesPerSample), this.bytesPerSample, this.signednessFormat, this.endianness);
			
			return amountReadElements * this.bytesPerSample;
		}
		
		
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			return this.underlying.skip(amount / this.bytesPerSample);
		}
		
		
		
		
		
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			throw new IllegalArgumentException();
		}
	}
}
