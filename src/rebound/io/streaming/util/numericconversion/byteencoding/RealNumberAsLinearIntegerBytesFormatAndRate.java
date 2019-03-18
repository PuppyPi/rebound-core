package rebound.io.streaming.util.numericconversion.byteencoding;

import rebound.bits.Endianness;
import rebound.io.streaming.util.numericconversion.byteencoding.NumberFormatConversion.SignednessFormat;

/**
 * The rate is conventionally in Hz but that's up to you :33
 */
public class RealNumberAsLinearIntegerBytesFormatAndRate
{
	protected final RealNumberAsLinearIntegerBytesFormat dataFormat;
	protected final double rate;
	
	
	
	public RealNumberAsLinearIntegerBytesFormatAndRate(RealNumberAsLinearIntegerBytesFormat dataFormat, double rate)
	{
		this.dataFormat = dataFormat;
		this.rate = rate;
	}
	
	public RealNumberAsLinearIntegerBytesFormatAndRate(int bitsPerSample, SignednessFormat signednessFormat, Endianness endianness,   double rate)
	{
		this(new RealNumberAsLinearIntegerBytesFormat(bitsPerSample, signednessFormat, endianness), rate);
	}
	
	
	
	
	public RealNumberAsLinearIntegerBytesFormat getDataFormat()
	{
		return this.dataFormat;
	}
	
	public double getRate()
	{
		return this.rate;
	}
	
	
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.dataFormat == null) ? 0 : this.dataFormat.hashCode());
		long temp;
		temp = Double.doubleToLongBits(this.rate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		RealNumberAsLinearIntegerBytesFormatAndRate other = (RealNumberAsLinearIntegerBytesFormatAndRate) obj;
		if (this.dataFormat == null)
		{
			if (other.dataFormat != null)
				return false;
		}
		else if (!this.dataFormat.equals(other.dataFormat))
			return false;
		if (Double.doubleToLongBits(this.rate) != Double.doubleToLongBits(other.rate))
			return false;
		return true;
	}
	
	
	
	@Override
	public String toString()
	{
		return "(" + this.dataFormat + " @ " + this.rate + ")";
	}
}
