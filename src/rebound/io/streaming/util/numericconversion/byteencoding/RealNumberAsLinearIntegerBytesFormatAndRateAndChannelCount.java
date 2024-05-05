package rebound.io.streaming.util.numericconversion.byteencoding;

import rebound.bits.Endianness;
import rebound.io.streaming.util.numericconversion.byteencoding.NumberFormatConversion.SignednessFormat;

/**
 * The rate is conventionally in Hz but that's up to you :33
 */
public class RealNumberAsLinearIntegerBytesFormatAndRateAndChannelCount
{
	protected final RealNumberAsLinearIntegerBytesFormat dataFormat;
	protected final double rate;
	protected final int numberOfChannels;
	
	
	
	public RealNumberAsLinearIntegerBytesFormatAndRateAndChannelCount(RealNumberAsLinearIntegerBytesFormat dataFormat, double rate, int numberOfChannels)
	{
		this.dataFormat = dataFormat;
		this.rate = rate;
		this.numberOfChannels = numberOfChannels;
	}
	
	public RealNumberAsLinearIntegerBytesFormatAndRateAndChannelCount(int bitsPerSample, SignednessFormat signednessFormat, Endianness endianness,   double rate, int numberOfChannels)
	{
		this(new RealNumberAsLinearIntegerBytesFormat(bitsPerSample, signednessFormat, endianness), rate, numberOfChannels);
	}
	
	
	
	
	public RealNumberAsLinearIntegerBytesFormat getDataFormat()
	{
		return this.dataFormat;
	}
	
	public double getRate()
	{
		return this.rate;
	}
	
	public int getNumberOfChannels()
	{
		return this.numberOfChannels;
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
		RealNumberAsLinearIntegerBytesFormatAndRateAndChannelCount other = (RealNumberAsLinearIntegerBytesFormatAndRateAndChannelCount) obj;
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
