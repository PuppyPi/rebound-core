/*
 * Created on Nov 1, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.util.adapters;

import java.io.EOFException;
import java.io.IOException;
import rebound.exceptions.ImpossibleException;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.ReferenceBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.ReferenceUnitReadStream;
import rebound.io.streaming.util.decorators.AbstractStreamDecorator;
import rebound.io.streaming.util.implhelp.StreamImplUtilities;
import rebound.io.streaming.util.usagehelp.StreamUsageUtilities;

/**
 * -takes a {@link ReferenceBlockReadStream} and exposes a {@link ReferenceUnitReadStream} interface.
 * There is no great performance penalty incurred by doing this, as a block stream can be of higher performance than a unit stream; it is a step down.
 * <br>Note: {@link StreamUsageUtilities#getAsUnitReadStream(ReferenceBlockReadStream)} should normally be used to instantiate this class for performance reasons.
 * @author RProgrammer
 */
public class ReferenceBlockToUnitReadStream<D>
extends AbstractStreamDecorator<ReferenceBlockReadStream<D>>
implements ReferenceUnitReadStream<D>
{
	public ReferenceBlockToUnitReadStream(ReferenceBlockReadStream<D> underlying)
	{
		super(underlying);
	}
	
	
	protected Object[] buff = new Object[1];
	
	@Override
	public D read() throws EOFException, IOException, ClosedStreamException
	{
		int c = this.underlying.read((D[])this.buff, 0, 1);
		
		if (c == 0)
		{
			if (!this.underlying.isEOF())
				throw new ImpossibleException();
			
			throw new EOFException();
		}
		else if (c == 1)
		{
			return (D)this.buff[0];
		}
		else
		{
			throw StreamImplUtilities.getExceptionForInvalidBlockAmount(c, this.underlying);
		}
	}
	
	@Override
	public void close() throws IOException
	{
		this.buff[0] = null; //release reference
		super.close();
	}
	
	@Override
	public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
	{
		return this.underlying.skip(amount);
	}
}
