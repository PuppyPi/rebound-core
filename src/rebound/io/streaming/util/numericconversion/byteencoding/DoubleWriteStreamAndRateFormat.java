package rebound.io.streaming.util.numericconversion.byteencoding;

import rebound.io.streaming.api.StreamAPIs.DoubleBlockWriteStream;

public class DoubleWriteStreamAndRateFormat
{
	protected final DoubleBlockWriteStream stream;
	protected final double rate;
	
	public DoubleWriteStreamAndRateFormat(DoubleBlockWriteStream stream, double rate)
	{
		this.stream = stream;
		this.rate = rate;
	}
	
	public DoubleBlockWriteStream getStream()
	{
		return this.stream;
	}
	
	public double getRate()
	{
		return this.rate;
	}
}
