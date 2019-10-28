package rebound.io.iio;

import java.io.IOException;
import java.io.OutputStream;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.io.iio.unions.CloseableFlushableOutputByteStreamInterface;



public class OutputByteStreamAdapter
extends OutputStream
implements CloseableFlushableOutputByteStreamInterface
{
	public static OutputByteStream wrap(OutputStream underlying)
	{
		if (underlying instanceof OutputByteStreamWrapper)
			return ((OutputByteStreamWrapper) underlying).getUnderlying();
		else if (underlying instanceof OutputByteStream)
			return (OutputByteStream) underlying;
		else
			return new OutputByteStreamAdapter(underlying);
	}
	
	
	
	
	
	protected OutputStream underlying;
	
	@ImplementationTransparency
	public OutputByteStreamAdapter()
	{
	}
	
	/**
	 * Use {@link #wrap(OutputStream)} in preference to this if you can :3
	 */
	@ImplementationTransparency
	public OutputByteStreamAdapter(OutputStream underlying)
	{
		this.underlying = underlying;
	}
	
	@ImplementationTransparency
	public OutputStream getUnderlying()
	{
		return this.underlying;
	}
	
	@ImplementationTransparency
	public void setUnderlying(OutputStream underlying)
	{
		this.underlying = underlying;
	}
	
	
	
	@Override
	public void write(int b) throws IOException
	{
		this.underlying.write(b);
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		this.underlying.write(b, off, len);
	}
	
	
	
	@Override
	public void close() throws IOException
	{
		this.underlying.close();
	}
	
	@Override
	public void flush() throws IOException
	{
		this.underlying.flush();
	}
}
