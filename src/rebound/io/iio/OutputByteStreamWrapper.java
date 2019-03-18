package rebound.io.iio;

import java.io.IOException;
import java.io.OutputStream;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.io.iio.unions.CloseableFlushableOutputByteStreamInterface;

public class OutputByteStreamWrapper
implements CloseableFlushableOutputByteStreamInterface
{
	protected final OutputStream underlying;
	
	public static OutputByteStream wrap(OutputStream underlying)
	{
		return underlying instanceof OutputByteStream ? (OutputByteStream)underlying : new OutputByteStreamWrapper(underlying);
	}
	
	protected OutputByteStreamWrapper(OutputStream underlying)
	{
		this.underlying = underlying;
	}
	
	@ImplementationTransparency
	public OutputStream getUnderlying()
	{
		return this.underlying;
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
