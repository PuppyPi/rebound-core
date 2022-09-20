package rebound.io;

import java.io.IOException;
import java.io.InputStream;
import rebound.annotations.semantic.SignalType;

/**
 * Just an {@link InputStream} that doesn't throw {@link IOException}s
 * @see TransparentByteArrayInputStream
 */
@SignalType
public abstract class GuaranteedInputStream
extends InputStream
{
	@Override
	public abstract int read();
	
	@Override
	public int read(byte[] b)
	{
		return read(b, 0, b.length);
	}
	
	@Override
	public abstract int read(byte[] b, int off, int len);
	
	@Override
	public abstract long skip(long n);
	
	@Override
	public abstract void close();
}
