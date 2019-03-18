package rebound.io.streaming.util.numericconversion.resampling;

import rebound.annotations.semantic.temporal.ConstantReturnValue;

public interface InterpolationSupermixin
{
	@ConstantReturnValue
	public int getInterpolationOrder();
	
	public double interpolate(double relativeOffsetBetweenCenterPoints, double[] interpolationBuffer);
}
