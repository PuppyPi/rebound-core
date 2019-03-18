package rebound.io.streaming.util.numericconversion.byteencoding;

import rebound.io.streaming.api.StreamAPIs.ByteBlockWriteStream;

public class ByteWriteStreamAndEncodedLinearIntegerFormat
{
	protected final ByteBlockWriteStream stream;
	protected final RealNumberAsLinearIntegerBytesFormatAndRate format;
	
	public ByteWriteStreamAndEncodedLinearIntegerFormat(ByteBlockWriteStream stream, RealNumberAsLinearIntegerBytesFormatAndRate format)
	{
		this.stream = stream;
		this.format = format;
	}
	
	public ByteBlockWriteStream getStream()
	{
		return this.stream;
	}
	
	public RealNumberAsLinearIntegerBytesFormatAndRate getFormat()
	{
		return this.format;
	}
}
