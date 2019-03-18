package rebound.io.streaming.util.decorators;

import java.io.EOFException;
import java.io.IOException;
import javax.annotation.Nonnull;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.DoubleBlockReadStream;

public class FrameCountingDoubleBlockReadStream
extends AbstractStreamDecorator<DoubleBlockReadStream>
implements DoubleBlockReadStream
{
	protected long numberRead = 0;
	
	public FrameCountingDoubleBlockReadStream(@Nonnull DoubleBlockReadStream underlying)
	{
		super(underlying);
	}
	
	
	
	@Override
	public double read() throws EOFException, IOException, ClosedStreamException
	{
		double r = this.underlying.read();
		
		//If it throws EOFException, then code flow won't make it to this point! XD
		this.numberRead++;
		
		return r;
	}
	
	@Override
	public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
	{
		int r = this.underlying.read(buffer, offset, length);
		this.numberRead += r;
		return r;
	}
	
	@Override
	public long skip(long length) throws IOException, ClosedStreamException
	{
		long r = this.underlying.skip(length);
		this.numberRead += r;
		return r;
	}
	
	
	
	
	
	public long getNumberRead()
	{
		return this.numberRead;
	}
	
	public void setNumberRead(long numberRead)
	{
		this.numberRead = numberRead;
	}
}
