package rebound.io.iio;

import java.io.IOException;
import java.io.InputStream;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.io.iio.unions.CloseableInputByteStreamInterface;

//Todo support mark/reset

public class InputByteStreamAdapter
extends InputStream
implements CloseableInputByteStreamInterface
{
	public static InputByteStream wrap(InputStream underlying)
	{
		if (underlying instanceof InputByteStreamWrapper)
			return ((InputByteStreamWrapper) underlying).getUnderlying();
		else if (underlying instanceof InputByteStream)
			return (InputByteStream) underlying;
		else
			return new InputByteStreamAdapter(underlying);
	}
	
	
	
	
	
	protected InputStream underlying;
	
	@ImplementationTransparency
	public InputByteStreamAdapter()
	{
	}

	/**
	 * Use {@link #wrap(InputStream)} in preference to this if you can :3
	 */
	@ImplementationTransparency
	public InputByteStreamAdapter(InputStream underlying)
	{
		this.underlying = underlying;
	}
	
	@ImplementationTransparency
	public InputStream getUnderlying()
	{
		return this.underlying;
	}
	
	@ImplementationTransparency
	public void setUnderlying(InputStream underlying)
	{
		this.underlying = underlying;
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
