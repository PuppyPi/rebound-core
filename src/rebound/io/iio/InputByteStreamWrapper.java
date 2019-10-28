package rebound.io.iio;

import java.io.IOException;
import java.io.InputStream;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.io.iio.unions.CloseableInputByteStreamInterface;

//Todo support mark/reset

public class InputByteStreamWrapper
extends InputStream
implements CloseableInputByteStreamInterface
{
	public static InputStream wrap(InputByteStream underlying)
	{
		if (underlying instanceof InputByteStreamAdapter)
			return ((InputByteStreamAdapter) underlying).getUnderlying();
		else if (underlying instanceof InputStream)
			return (InputStream) underlying;
		else
			return new InputByteStreamWrapper(underlying);
	}
	
	
	
	
	
	protected InputByteStream underlying;

	@ImplementationTransparency
	public InputByteStreamWrapper()
	{
	}

	/**
	 * Use {@link #wrap(InputByteStream)} in preference to this if you can :3
	 */
	@ImplementationTransparency
	public InputByteStreamWrapper(InputByteStream underlying)
	{
		this.underlying = underlying;
	}

	@ImplementationTransparency
	public InputByteStream getUnderlying()
	{
		return underlying;
	}

	@ImplementationTransparency
	public void setUnderlying(InputByteStream underlying)
	{
		this.underlying = underlying;
	}
	
	
	
	
	
	
	
	
	@Override
	public int read() throws IOException
	{
		return underlying.read();
	}
	
	@Override
	public void close() throws IOException
	{
		underlying.close();
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		return underlying.read(b, off, len);
	}
	
	@Override
	public long skip(long n) throws IOException
	{
		return underlying.skip(n);
	}
}
