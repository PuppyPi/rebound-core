package rebound.io.streaming.util.numericconversion.byteencoding;

import rebound.io.streaming.api.StreamAPIs.FloatBlockReadStream;

public class FloatReadStreamAndRateFormat
{
	protected final FloatBlockReadStream stream;
	protected final Float rate;
	
	public FloatReadStreamAndRateFormat(FloatBlockReadStream stream, Float rate)
	{
		this.stream = stream;
		this.rate = rate;
	}
	
	public FloatBlockReadStream getStream()
	{
		return this.stream;
	}
	
	public Float getRate()
	{
		return this.rate;
	}
}
