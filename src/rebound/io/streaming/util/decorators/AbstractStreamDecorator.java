/*
 * Created on Jun 13, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.util.decorators;

import static java.util.Objects.*;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import javax.annotation.Nonnull;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.Stream;

/**
 * A Decorator is something which wraps a class of object and exposes the same interface (possibly with extra methods), but different functionality.
 * The Java SIO analogues to this class are {@link FilterInputStream} and {@link FilterOutputStream}, which are abstract decorator classes.
 * Like in the Filter classes, the defaults for this simply delegate to the underlying stream.
 * 
 * NOTE: It might expose a different api than the underlying stream!!  Eg, a stream that wraps a byte-stream and presents an int-stream! :>
 * @author RProgrammer
 */
public abstract class AbstractStreamDecorator<S extends Stream>
implements Stream
{
	protected S underlying;
	
	protected AbstractStreamDecorator()
	{
	}
	
	protected AbstractStreamDecorator(@Nonnull S underlying)
	{
		this.underlying = requireNonNull(underlying);
	}
	
	
	@Override
	public void close() throws IOException
	{
		this.underlying.close();
	}
	
	@Override
	public boolean isClosed()
	{
		return this.underlying.isClosed();
	}
	
	@Override
	public boolean isEOF() throws IOException, ClosedStreamException
	{
		return this.underlying.isEOF();
	}
	
	@Override
	public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
	{
		return this.underlying.skip(amount);
	}
	
	
	/**
	 * @throws ClosedStreamException If {@link #isClosed()}
	 */
	protected void checkOpen() throws ClosedStreamException
	{
		if (isClosed())
			throw new ClosedStreamException();
	}
	
	
	@Nonnull
	public S getUnderlying()
	{
		return this.underlying;
	}
}
