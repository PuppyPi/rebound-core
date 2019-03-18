package rebound.io.streaming.util.numericconversion.byteencoding;

import rebound.io.streaming.api.StreamAPIs.ByteBlockReadStream;

public class ByteReadStreamAndEncodedLinearIntegerFormat
{
	protected final ByteBlockReadStream stream;
	protected final RealNumberAsLinearIntegerBytesFormatAndRate format;
	
	public ByteReadStreamAndEncodedLinearIntegerFormat(ByteBlockReadStream stream, RealNumberAsLinearIntegerBytesFormatAndRate format)
	{
		this.stream = stream;
		this.format = format;
	}
	
	public ByteBlockReadStream getStream()
	{
		return this.stream;
	}
	
	public RealNumberAsLinearIntegerBytesFormatAndRate getFormat()
	{
		return this.format;
	}
}
