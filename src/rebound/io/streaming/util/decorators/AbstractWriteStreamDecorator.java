/*
 * Created on Jun 13, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.util.decorators;

import java.io.IOException;
import javax.annotation.Nonnull;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.WriteStream;

/**
 * Simply extends the {@link AbstractStreamDecorator} for the {@link WriteStream} methods; it still does a simple delegation to the {@link #getUnderlying() underlying stream}.
 * @author RProgrammer
 */
public abstract class AbstractWriteStreamDecorator<S extends WriteStream>
extends AbstractStreamDecorator<S>
implements WriteStream
{
	protected AbstractWriteStreamDecorator()
	{
		super();
	}
	
	protected AbstractWriteStreamDecorator(@Nonnull S underlying)
	{
		super(underlying);
	}
	
	
	@Override
	public void flush() throws IOException, ClosedStreamException
	{
		this.underlying.flush();
	}
}
