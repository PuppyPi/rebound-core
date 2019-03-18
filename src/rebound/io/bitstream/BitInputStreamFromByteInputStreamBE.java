package rebound.io.bitstream;

import java.io.EOFException;
import java.io.IOException;
import rebound.io.iio.InputByteStream;

public class BitInputStreamFromByteInputStreamBE
implements BitInputStream
{
	protected final InputByteStream byteIn;
	protected final byte[] buffer;
	protected int byteIndex;
	protected int bufferSize;
	protected int bitIndex;
	
	public BitInputStreamFromByteInputStreamBE(InputByteStream byteIn, int bufferSize)
	{
		if (bufferSize <= 0) throw new IllegalArgumentException();
		
		this.byteIn = byteIn;
		this.buffer = new byte[bufferSize];
		this.bufferSize = 0;
		this.byteIndex = 0;
		
		this.bitIndex = 7;  //It is big bit-endian, remember!
	}
	
	public BitInputStreamFromByteInputStreamBE(InputByteStream byteIn)
	{
		this(byteIn, 4096);
	}
	
	
	
	
	@Override
	public boolean read() throws EOFException, IOException
	{
		//Ensure the buffer is fulllll :3
		{
			while (this.bufferSize == 0)
			{
				int amt = this.byteIn.read(this.buffer);
				if (amt == -1)  //this is the Java spec! -1 means EOF, not 0!    ( you just have to keep read()ing until it breaks out of a 0-rut xD' )
					throw new EOFException();
				
				this.bufferSize = amt;
				this.byteIndex = 0;
				this.bitIndex = 7;
			}
		}
		
		
		
		
		
		boolean bit = (this.buffer[this.byteIndex] & (1 << this.bitIndex)) != 0;
		
		
		if (this.bitIndex == 0)
		{
			this.bitIndex = 7;
			
			if (this.byteIndex == this.bufferSize-1)
			{
				this.bufferSize = 0;  //mark it invalid/used-up, for next time :3
			}
			else
			{
				this.byteIndex++;
			}
		}
		else
		{
			this.bitIndex--;
		}
		
		
		return bit;
	}
}
