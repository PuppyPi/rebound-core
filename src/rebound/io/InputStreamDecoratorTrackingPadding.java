package rebound.io;

import java.io.IOException;
import java.io.InputStream;
import rebound.exceptions.ImpossibleException;
import rebound.io.InputStreamWithBlockPaddingAwareness.DefaultInputStreamWithBlockPaddingAwareness;
import rebound.io.util.JRECompatIOUtilities;

public class InputStreamDecoratorTrackingPadding
extends DefaultInputStreamWithBlockPaddingAwareness
{
	protected final InputStream underlying;
	protected final int blockSize;
	
	protected int currentModulus = 0;
	
	public InputStreamDecoratorTrackingPadding(InputStream underlying, int blockSize)
	{
		this.underlying = underlying;
		this.blockSize = blockSize;
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
	public int read(byte[] b, int off, int len) throws IOException
	{
		int amt = underlying.read(b, off, len);
		
		if (amt != -1)
			currentModulus = ((amt % blockSize) + currentModulus) % blockSize;
		
		return amt;
	}
	
	@Override
	public long skip(long n) throws IOException
	{
		long amt = underlying.skip(n);
		
		if (amt != -1)
			currentModulus = ((int)(amt % (long)blockSize) + currentModulus) % blockSize;
		
		return amt;
	}
	
	
	
	
	
	
	
	
	@Override
	public void close() throws IOException
	{
		underlying.close();
	}
	
	
	
	
	protected final byte[] buf1 = new byte[1];
	
	@Override
	public int read() throws IOException
	{
		int r = JRECompatIOUtilities.readAsMuchAsPossible(this, buf1);
		
		if (r == 0)
			return -1;
		else if (r == 1)
			return buf1[0] & 0xFF;
		else
			throw new ImpossibleException(r+" bytes");
	}
}
