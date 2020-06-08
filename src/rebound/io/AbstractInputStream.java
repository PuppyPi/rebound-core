package rebound.io;

import java.io.IOException;
import java.io.InputStream;
import rebound.exceptions.ImpossibleException;

public abstract class AbstractInputStream
extends InputStream
{
	@Override
	public abstract int read(byte[] b, int off, int len) throws IOException;
	
	@Override
	public abstract void close() throws IOException;
	
	
	
	
	
	
	protected byte[] buff = null;
	
	protected byte[] getTemporaryBufferEnsuringCapacity(int minCapacity)
	{
		byte[] buff = this.buff;
		
		if (buff == null || buff.length < minCapacity)
		{
			buff = new byte[minCapacity];
			this.buff = buff;
		}
		
		return buff;
	}
	
	
	
	
	@Override
	public int read() throws IOException
	{
		byte[] buff = getTemporaryBufferEnsuringCapacity(1);
		
		while (true)
		{
			int r = this.read(buff, 0, 1);
			if (r == 0)
				continue;
			else if (r == 1)
				return buff[0] & 0xFF;
			else if (r == -1)
				return -1;
			else
				throw new ImpossibleException("Returned: "+r);
		}
	}
	
	
	
	
	
	// MAX_SKIP_BUFFER_SIZE is used to determine the maximum buffer size to use when skipping.
	private static final int MAX_SKIP_BUFFER_SIZE = 2048;
	
	@Override
	public long skip(long n) throws IOException
	{
		long remaining = n;
		int nr;
		
		if (n <= 0)
		{
			return 0;
		}
		
		int size = (int) Math.min(MAX_SKIP_BUFFER_SIZE, remaining);
		byte[] skipBuffer = getTemporaryBufferEnsuringCapacity(size);
		
		while (remaining > 0)
		{
			nr = read(skipBuffer, 0, (int) Math.min(size, remaining));
			if (nr < 0)
			{
				break;
			}
			remaining -= nr;
		}
		
		return n - remaining;
	}
}
