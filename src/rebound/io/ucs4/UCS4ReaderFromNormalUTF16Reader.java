package rebound.io.ucs4;

import java.io.IOException;
import java.io.Reader;

public class UCS4ReaderFromNormalUTF16Reader
extends AbstractUCS4Reader
{
	protected final Reader underlying;
	
	public UCS4ReaderFromNormalUTF16Reader(Reader underlying)
	{
		this.underlying = underlying;
	}
	
	
	@Override
	public int read(int[] cbuf, int off, int len) throws IOException
	{
		int m = off + len;
		
		for (int i = off; i < m; i++)
		{
			long v = read();
			
			if (v == -1)
			{
				if (i == off) //EOF!
					return -1;
				else
					return i - off;
			}
			else
			{
				cbuf[i] = (int)v;
			}
		}
		
		return len;
	}
	
	
	
	@Override
	public void close() throws IOException
	{
		this.underlying.close();
	}
	
	
	
	
	
	
	
	
	@Override
	public long read() throws IOException
	{
		// UTF-16 decoder!! \:D/
		
		
		int first = this.underlying.read();
		
		if (first == -1)
			//EOF!
			return -1;
		
		
		
		char firstC = (char)first;
		
		if (isHighSurrogate(firstC))  //UTF-16 is:  High = Always Leading, no matter the Endianness!!
		{
			int second = this.underlying.read();
			
			if (second == -1)
				//EOF IN THE MIDDLE!! DECODING ERRORRR!!
				throw new IOException("UTF-16 Decoding Error!!  High/Leading Surrogate followed by EOF!!");
			
			
			
			char secondC = (char)second;
			
			if (isLowSurrogate(secondC))  //UTF-16 is:  Low = Always Trailing, no matter the Endianness!!
			{
				int codePoint = surrogatesToCodePoint(firstC, secondC);
				return codePoint;  //currently, the codepoints that can be produced from this are only 20 bits long, thus never "negative" in an sint32 :33
			}
			else
			{
				throw new IOException("UTF-16 Decoding Error!!  High/Leading Surrogate followed by something other than a Low/Trailing Surrogate!!");
			}
		}
		else if (isLowSurrogate(firstC))
		{
			throw new IOException("UTF-16 Decoding Error!!  Low/Trailing Surrogate preceded by BOF or something other than a High/Leading Surrogate!!");
		}
		else
		{
			//BMP character--just pass straight through unaltered--set the high 16 bits to 0  ^wwww^
			return first;
		}
	}
	
	
	
	
	
	
	public static boolean isSurrogate(char c)
	{
		return (c & 0b11111_00000000000) == 0b11011_00000000000;
	}
	
	public static boolean isHighSurrogate(char c)
	{
		return (c & 0b11111_1_0000000000) == 0b11011_0_0000000000;
	}
	
	public static boolean isLowSurrogate(char c)
	{
		return (c & 0b11111_1_0000000000) == 0b11011_1_0000000000;
	}
	
	
	
	
	public static int surrogatesToCodePoint(char highSurrogate, char lowSurrogate)
	{
		int lowBits = lowSurrogate & 0b1111111111;
		int highBits = highSurrogate & 0b1111111111;
		
		return lowBits | (highBits << 10);
	}
}
