package rebound.io;

import java.io.IOException;
import java.io.OutputStream;
import rebound.io.OutputStreamWithBlockPaddingAwareness.DefaultOutputStreamWithBlockPaddingAwareness;

public class OutputStreamDecoratorTrackingPadding
extends DefaultOutputStreamWithBlockPaddingAwareness
{
	protected final OutputStream underlying;
	protected final int blockSize;
	
	protected int currentModulus = 0;
	
	public OutputStreamDecoratorTrackingPadding(OutputStream underlying, int blockSize)
	{
		this.underlying = underlying;
		this.blockSize = blockSize;
	}
	
	
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		underlying.write(b, off, len);
		currentModulus = ((len % blockSize) + currentModulus) % blockSize;
	}
	
	@Override
	public int getBlockSize()
	{
		return blockSize;
	}
	
	public int getCurrentModulus()
	{
		return currentModulus;
	}
	
	
	
	
	
	
	@Override
	public void flush() throws IOException
	{
		underlying.flush();
	}
	
	@Override
	public void close() throws IOException
	{
		underlying.close();
	}
	
	
	
	protected final byte[] buf1 = new byte[1];
	
	@Override
	public void write(int b) throws IOException
	{
		buf1[0] = (byte)b;
		this.write(buf1);
	}
}
