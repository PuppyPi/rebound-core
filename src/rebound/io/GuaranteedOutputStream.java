package rebound.io;

import java.io.IOException;
import java.io.OutputStream;
import rebound.annotations.semantic.SignalType;

/**
 * Just an {@link OutputStream} that doesn't throw {@link IOException}s
 * @see TransparentByteArrayOutputStream
 */
@SignalType
public abstract class GuaranteedOutputStream
extends OutputStream
{
	@Override
	public abstract void write(int b);
	
	@Override
	public void write(byte[] b)
	{
		write(b, 0, b.length);
	}
	
	@Override
	public abstract void write(byte[] b, int off, int len);
	
	@Override
	public abstract void flush();
	
	@Override
	public abstract void close();
}
