package rebound.io.iio;

import java.io.IOException;
import java.io.OutputStream;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.io.iio.unions.CloseableFlushableOutputByteStreamInterface;



public class OutputByteStreamWrapper
extends OutputStream
implements CloseableFlushableOutputByteStreamInterface
{
	public static OutputStream wrap(OutputByteStream underlying)
	{
		if (underlying instanceof OutputByteStreamAdapter)
			return ((OutputByteStreamAdapter) underlying).getUnderlying();
		else if (underlying instanceof OutputStream)
			return (OutputStream) underlying;
		else
			return new OutputByteStreamWrapper(underlying);
	}
	
	
	
	
	
	protected OutputByteStream underlying;
	
	@ImplementationTransparency
	public OutputByteStreamWrapper()
	{
	}
	
	/**
	 * Use {@link #wrap(OutputByteStream)} in preference to this if you can :3
	 */
	@ImplementationTransparency
	public OutputByteStreamWrapper(OutputByteStream underlying)
	{
		this.underlying = underlying;
	}
	
	@ImplementationTransparency
	public OutputByteStream getUnderlying()
	{
		return underlying;
	}
	
	@ImplementationTransparency
	public void setUnderlying(OutputByteStream underlying)
	{
		this.underlying = underlying;
	}
	
	
	
	
	
	
	
	
	@Override
	public void close() throws IOException
	{
		underlying.close();
	}
	
	public void flush() throws IOException
	{
		underlying.flush();
	}
	
	@Override
	public void write(int b) throws IOException
	{
		underlying.write(b);
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		underlying.write(b, off, len);
	}
}
