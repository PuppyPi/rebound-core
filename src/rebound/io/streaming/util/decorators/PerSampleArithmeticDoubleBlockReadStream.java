package rebound.io.streaming.util.decorators;

import java.io.EOFException;
import java.io.IOException;
import javax.annotation.Nonnull;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.DoubleBlockReadStream;

public abstract class PerSampleArithmeticDoubleBlockReadStream
extends AbstractStreamDecorator<DoubleBlockReadStream>
implements DoubleBlockReadStream
{
	public PerSampleArithmeticDoubleBlockReadStream(@Nonnull DoubleBlockReadStream underlying)
	{
		super(underlying);
	}
	
	public abstract double arithmetic(double x);
	
	
	
	
	@Override
	public double read() throws EOFException, IOException, ClosedStreamException
	{
		double r = this.underlying.read();
		
		r = arithmetic(r);
		
		return r;
	}
	
	@Override
	public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
	{
		int r = this.underlying.read(buffer, offset, length);
		
		for (int i = 0; i < r; i++)
		{
			int j = offset + i;
			buffer[j] = arithmetic(buffer[j]);
		}
		
		return r;
	}
	
	@Override
	public long skip(long length) throws IOException, ClosedStreamException
	{
		return this.underlying.skip(length);
	}
	
	
	
	
	
	
	
	public static class PerSampleMultiplyingDoubleBlockReadStream
	extends PerSampleArithmeticDoubleBlockReadStream
	{
		protected double multiplier;
		
		public PerSampleMultiplyingDoubleBlockReadStream(DoubleBlockReadStream underlying, double multiplier)
		{
			super(underlying);
			this.multiplier = multiplier;
		}
		
		@Override
		public double arithmetic(double x)
		{
			return x * this.multiplier;
		}
		
		public double getMultiplier()
		{
			return this.multiplier;
		}
	}
}
