package rebound.io.streaming.util.numericconversion.resampling.combinations;

import rebound.io.streaming.api.StreamAPIs.DoubleBlockReadStream;
import rebound.io.streaming.util.numericconversion.resampling.AbstractUpsamplingDoubleReadStream;
import rebound.io.streaming.util.numericconversion.resampling.Poly4Interpolation;

public class UpsamplingDoubleReadStreamPoly4
extends AbstractUpsamplingDoubleReadStream
implements Poly4Interpolation
{
	public UpsamplingDoubleReadStreamPoly4(DoubleBlockReadStream underlying, double ourPeriodRelativeToTheirs)
	{
		super(underlying, ourPeriodRelativeToTheirs);
	}
}
