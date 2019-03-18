package rebound.io.streaming.util.numericconversion.byteencoding;

import rebound.io.streaming.api.StreamAPIs.DoubleBlockReadStream;

public class DoubleReadStreamAndRateFormat
{
	protected final DoubleBlockReadStream stream;
	protected final double rate;
	
	public DoubleReadStreamAndRateFormat(DoubleBlockReadStream stream, double rate)
	{
		this.stream = stream;
		this.rate = rate;
	}
	
	public DoubleBlockReadStream getStream()
	{
		return this.stream;
	}
	
	public double getRate()
	{
		return this.rate;
	}
}
