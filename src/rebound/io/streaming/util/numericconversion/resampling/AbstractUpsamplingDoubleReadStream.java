package rebound.io.streaming.util.numericconversion.resampling;

import java.io.EOFException;
import java.io.IOException;
import rebound.exceptions.ImpossibleException;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.DoubleBlockReadStream;
import rebound.io.streaming.util.implhelp.AbstractStream;

public abstract class AbstractUpsamplingDoubleReadStream
extends AbstractStream
implements DoubleBlockReadStream, InterpolationSupermixin
{
	protected final DoubleBlockReadStream underlying;
	protected final double ourPeriodRelativeToTheirs;  //if our period is taken to be 1 unit :3
	
	protected int bufferFill = 0;
	protected double[] interpolationBuffer;
	protected double position;
	
	
	
	public AbstractUpsamplingDoubleReadStream(DoubleBlockReadStream underlying, double ourPeriodRelativeToTheirs)
	{
		if (ourPeriodRelativeToTheirs > 1)  //we use periods not frequencies here, so it's inverted :3
			throw new IllegalArgumentException("We said *up*sampling! XD");
		
		int bufferSizeForInterpolation = getInterpolationOrder();
		
		if (bufferSizeForInterpolation < 2)
			throw new ImpossibleException();
		
		if (bufferSizeForInterpolation % 2 != 0)
			throw new ImpossibleException();
		
		this.underlying = underlying;
		this.ourPeriodRelativeToTheirs = ourPeriodRelativeToTheirs;
		this.interpolationBuffer = new double[bufferSizeForInterpolation];
	}
	
	public static double getOurPeriodRelativeToTheirs(double underlyingFrameRate, double exposedFrameRate)
	{
		return underlyingFrameRate / exposedFrameRate;
	}
	
	
	
	
	
	
	
	@Override
	public double read() throws EOFException, IOException, ClosedStreamException
	{
		int order = this.interpolationBuffer.length;
		
		
		
		while (this.bufferFill < order)
		{
			this.interpolationBuffer[this.bufferFill] = this.underlying.read();
			this.bufferFill++;
		}
		
		
		
		if (this.position > 1)
		{
			System.arraycopy(this.interpolationBuffer, 1, this.interpolationBuffer, 0, order-1);
			this.interpolationBuffer[order-1] = this.underlying.read();
			
			this.position -= 1;
		}
		
		
		double y;
		{
			if (this.position == 0)
			{
				y = this.interpolationBuffer[order/2-1];
			}
			else if (this.position == 1)
			{
				y = this.interpolationBuffer[order/2];
			}
			else
			{
				y = interpolate(this.position + order/2-1, this.interpolationBuffer);
			}
		}
		
		
		this.position += this.ourPeriodRelativeToTheirs;
		
		return y;
	}
	
	
	
	@Override
	public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
	{
		requireOpen();
		
		int i = offset;
		try
		{
			int e = offset + length;
			for (; i < e; i++)
				buffer[i] = read();
		}
		catch (EOFException exc)
		{
		}
		
		return i - offset;
	}
	
	
	
	
	
	@Override
	public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
	{
		long i = 0;
		try
		{
			for (; i < amount; i++)
				read();
		}
		catch (EOFException exc)
		{
		}
		return i;
	}
	
	@Override
	protected void close0() throws IOException
	{
		this.underlying.close();
	}
	
	@Override
	public boolean isEOF() throws IOException, ClosedStreamException
	{
		return this.underlying.isEOF();
	}
}
