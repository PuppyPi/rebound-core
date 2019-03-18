package rebound.io.bitstream;

import java.io.EOFException;

public class BitInputStreamFromByteArrayLE
implements BitInputStream
{
	protected byte[] byteArray;
	protected int byteIndex;
	protected int bitIndex;
	
	protected int exclusiveHighBound;
	
	
	public BitInputStreamFromByteArrayLE(byte[] octetData, int offset, int length)
	{
		if (offset < 0) throw new IllegalArgumentException();
		if (length < 0) throw new IllegalArgumentException();
		if (octetData == null) throw new NullPointerException();
		
		this.byteArray = octetData;
		this.byteIndex = offset;
		this.exclusiveHighBound = offset + length;
		
		this.bitIndex = 0;  //It is little bit-endian, remember!
	}
	
	
	public BitInputStreamFromByteArrayLE(byte[] octetData, int offset)
	{
		this(octetData, offset, octetData.length - offset);
	}
	
	public BitInputStreamFromByteArrayLE(byte[] octetData)
	{
		this(octetData, 0, octetData.length);
	}
	
	
	
	
	
	@Override
	public boolean read() throws EOFException
	{
		if (this.byteIndex >= this.exclusiveHighBound)
			throw new EOFException("Premature EOF!");
		
		
		
		boolean bit = (this.byteArray[this.byteIndex] & (1 << this.bitIndex)) != 0;
		
		
		if (this.bitIndex == 7)
		{
			this.bitIndex = 0;
			this.byteIndex++;
		}
		else
		{
			this.bitIndex++;
		}
		
		
		return bit;
	}
}
