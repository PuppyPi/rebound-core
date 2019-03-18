package rebound.io.iio;

import java.io.IOException;
import java.io.InputStream;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.io.iio.unions.CloseableInputByteStreamInterface;

public class InputByteStreamWrapper
implements CloseableInputByteStreamInterface
{
	protected final InputStream underlying;
	
	
	public static InputByteStream wrap(InputStream underlying)
	{
		return underlying instanceof InputByteStream ? (InputByteStream)underlying : new InputByteStreamWrapper(underlying);
	}
	
	protected InputByteStreamWrapper(InputStream underlying)
	{
		this.underlying = underlying;
	}
	
	@ImplementationTransparency
	public InputStream getUnderlying()
	{
		return this.underlying;
	}
	
	
	
	@Override
	public int read() throws IOException
	{
		return this.underlying.read();
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		return this.underlying.read(b, off, len);
	}
	
	
	@Override
	public void close() throws IOException
	{
		this.underlying.close();
	}
	
	
	
	@Override
	public long skip(long amount) throws IOException
	{
		return this.underlying.skip(amount);
	}
}
