package rebound.io.streaming.util.numericconversion.byteencoding;

import static java.util.Objects.*;
import rebound.bits.Endianness;
import rebound.io.streaming.util.numericconversion.byteencoding.NumberFormatConversion.SignednessFormat;

public class RealNumberAsLinearIntegerBytesFormat
{
	protected final int bitsPerSample;
	protected final SignednessFormat signednessFormat;
	protected final Endianness endianness;
	
	public RealNumberAsLinearIntegerBytesFormat(int bitsPerSample, SignednessFormat signednessFormat, Endianness endianness)
	{
		if (bitsPerSample < 0 || bitsPerSample == 0)
			throw new IllegalArgumentException();
		
		this.bitsPerSample = bitsPerSample;
		this.signednessFormat = requireNonNull(signednessFormat);
		this.endianness = requireNonNull(endianness);
	}
	
	public int requireBytesPerSample()
	{
		if ((this.bitsPerSample % 8) != 0)
			throw new IllegalArgumentException("Bits-per-sample is not a multiple of 8!  (Ie, it's not even bytes!)");
		
		return this.bitsPerSample / 8;
	}
	
	public int getBitsPerSample()
	{
		return this.bitsPerSample;
	}
	
	public SignednessFormat getSignednessFormat()
	{
		return this.signednessFormat;
	}
	
	public Endianness getEndianness()
	{
		return this.endianness;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + this.bitsPerSample;
		result = prime * result + ((this.signednessFormat == null) ? 0 : this.signednessFormat.hashCode());
		result = prime * result + ((this.endianness == null) ? 0 : this.endianness.hashCode());
		return result;
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
		RealNumberAsLinearIntegerBytesFormat other = (RealNumberAsLinearIntegerBytesFormat) obj;
		if (this.bitsPerSample != other.bitsPerSample)
			return false;
		if (this.signednessFormat != other.signednessFormat)
			return false;
		if (this.endianness != other.endianness)
			return false;
		return true;
	}
	
	
	@Override
	public String toString()
	{
		return "(" + this.bitsPerSample + " bits, " + (this.signednessFormat == SignednessFormat.TwosComplement ? "two's complement" : "offset binary") + ", " + (this.endianness == Endianness.Little ? "LE" : "BE") + ")";
	}
}
