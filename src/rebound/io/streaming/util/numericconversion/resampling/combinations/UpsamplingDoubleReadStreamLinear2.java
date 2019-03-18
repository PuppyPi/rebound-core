package rebound.io.streaming.util.numericconversion.resampling.combinations;

import rebound.io.streaming.api.StreamAPIs.DoubleBlockReadStream;
import rebound.io.streaming.util.numericconversion.resampling.AbstractUpsamplingDoubleReadStream;
import rebound.io.streaming.util.numericconversion.resampling.Linear2Interpolation;

public class UpsamplingDoubleReadStreamLinear2
extends AbstractUpsamplingDoubleReadStream
implements Linear2Interpolation
{
	public UpsamplingDoubleReadStreamLinear2(DoubleBlockReadStream underlying, double ourPeriodRelativeToTheirs)
	{
		super(underlying, ourPeriodRelativeToTheirs);
	}
}
