package rebound.io.streaming.util.numericconversion.byteencoding;

import rebound.io.streaming.api.StreamAPIs.FloatBlockWriteStream;

public class FloatWriteStreamAndRateFormat
{
	protected final FloatBlockWriteStream stream;
	protected final Float rate;
	
	public FloatWriteStreamAndRateFormat(FloatBlockWriteStream stream, Float rate)
	{
		this.stream = stream;
		this.rate = rate;
	}
	
	public FloatBlockWriteStream getStream()
	{
		return this.stream;
	}
	
	public Float getRate()
	{
		return this.rate;
	}
}
