/*
 * Created on Nov 1, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.util.adapters;

import java.io.EOFException;
import java.io.IOException;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.ReferenceBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.ReferenceUnitReadStream;
import rebound.io.streaming.util.decorators.AbstractStreamDecorator;
import rebound.io.streaming.util.usagehelp.StreamUsageUtilities;

/**
 * -takes a {@link ReferenceUnitReadStream} and exposes a {@link ReferenceBlockReadStream} interface.
 * Since a unit stream is a lower performance model than a block stream, this will most likely have a performance decrease compared to a native block stream implementation.
 * <br>Note: {@link StreamUsageUtilities#getAsBlockReadStream(ReferenceUnitReadStream)} should normally be used to instantiate this class for performance reasons.
 * @author RProgrammer
 */
public class ReferenceUnitToBlockReadStream<D>
extends AbstractStreamDecorator<ReferenceUnitReadStream<D>>
implements ReferenceBlockReadStream<D>
{
	public ReferenceUnitToBlockReadStream(ReferenceUnitReadStream<D> underlying)
	{
		super(underlying);
	}
	
	
	
	@Override
	public int read(D[] buffer, int offset, int length) throws IOException, ClosedStreamException
	{
		if (isEOF())
			return 0;
		
		D unit = null;
		for (int i = 0; i < length; i++)
		{
			try
			{
				unit = this.underlying.read();
			}
			catch (EOFException exc)
			{
				return i;
			}
			
			buffer[offset+i] = unit;
		}
		
		return length;
	}
	
	
	@Override
	public D read() throws EOFException, IOException, ClosedStreamException
	{
		return this.underlying.read();
	}
	
	@Override
	public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
	{
		return this.underlying.skip(amount);
	}
}
