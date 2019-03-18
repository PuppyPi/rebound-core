/*
 * Created on Nov 1, 2009
 * 	by the great Eclipse(c)
 */
package rebound.io.streaming.util.adapters;

import java.io.EOFException;
import java.io.IOException;
import rebound.exceptions.ImpossibleException;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.ReferenceBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.ReferenceUnitWriteStream;
import rebound.io.streaming.util.decorators.AbstractWriteStreamDecorator;
import rebound.io.streaming.util.implhelp.StreamImplUtilities;
import rebound.io.streaming.util.usagehelp.StreamUsageUtilities;

/**
 * -takes a {@link ReferenceBlockWriteStream} and exposes a {@link ReferenceUnitWriteStream} interface.
 * There is no great performance penalty incurred by doing this, as a block stream can be of higher performance than a unit stream; it is a step down.
 * <br>Note: {@link StreamUsageUtilities#getAsUnitWriteStream(ReferenceBlockWriteStream)} should normally be used to instantiate this class for performance reasons.
 * @author RProgrammer
 */
public class ReferenceBlockToUnitWriteStream<D>
extends AbstractWriteStreamDecorator<ReferenceBlockWriteStream<D>>
implements ReferenceUnitWriteStream<D>
{
	public ReferenceBlockToUnitWriteStream(ReferenceBlockWriteStream<D> underlying)
	{
		super(underlying);
	}
	
	
	protected Object[] buff = new Object[1];
	
	@Override
	public void write(D unit) throws EOFException, IOException, ClosedStreamException
	{
		this.buff[0] = unit;
		int c = this.underlying.write((D[])this.buff, 0, 1);
		
		if (c == 0)
		{
			if (!this.underlying.isEOF())
				throw new ImpossibleException();
			
			throw new EOFException();
		}
		else if (c == 1)
		{
			return;
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
