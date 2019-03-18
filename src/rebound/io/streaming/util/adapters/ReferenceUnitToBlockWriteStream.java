/*
 * Created on Nov 1, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.util.adapters;

import java.io.EOFException;
import java.io.IOException;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.ReferenceBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.ReferenceUnitWriteStream;
import rebound.io.streaming.util.decorators.AbstractWriteStreamDecorator;
import rebound.io.streaming.util.usagehelp.StreamUsageUtilities;

/**
 * -takes a {@link ReferenceUnitWriteStream} and exposes a {@link ReferenceBlockWriteStream} interface.
 * Since a unit stream is a lower performance model than a block stream, this will most likely have a performance decrease compared to a native block stream implementation.
 * <br>Note: {@link StreamUsageUtilities#getAsBlockWriteStream(ReferenceUnitWriteStream)} should normally be used to instantiate this class for performance reasons.
 * @author RProgrammer
 */
public class ReferenceUnitToBlockWriteStream<D>
extends AbstractWriteStreamDecorator<ReferenceUnitWriteStream<D>>
implements ReferenceBlockWriteStream<D>
{
	public ReferenceUnitToBlockWriteStream(ReferenceUnitWriteStream<D> underlying)
	{
		super(underlying);
	}
	
	
	
	@Override
	public int write(D[] buffer, int offset, int length) throws IOException, ClosedStreamException
	{
		if (isEOF())
			return 0;
		
		D unit = null;
		for (int i = 0; i < length; i++)
		{
			unit = buffer[offset+i];
			
			try
			{
				this.underlying.write(unit);
			}
			catch (EOFException exc)
			{
				return i;
			}
		}
		
		return length;
	}
	
	
	@Override
	public void write(D unit) throws EOFException, IOException, ClosedStreamException
	{
		this.underlying.write(unit);
	}
	
	@Override
	public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
	{
		return this.underlying.skip(amount);
	}
}
