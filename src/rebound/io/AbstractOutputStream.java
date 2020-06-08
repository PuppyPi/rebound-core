package rebound.io;

import java.io.IOException;
import java.io.OutputStream;

public abstract class AbstractOutputStream
extends OutputStream
{
	@Override
	public abstract void write(byte[] b, int off, int len) throws IOException;
	
	@Override
	public abstract void flush() throws IOException;
	
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
	public void write(int b) throws IOException
	{
		byte[] buff = getTemporaryBufferEnsuringCapacity(1);
		
		buff[0] = (byte)b;
		this.write(buff);
	}
}
