package rebound.io.streaming.util.numericconversion.resampling;

public interface Linear2Interpolation
extends InterpolationSupermixin
{
	@Override
	public default int getInterpolationOrder()
	{
		return 2;
	}
	
	
	@Override
	public default double interpolate(double relativeOffsetBetweenCenterPoints, double[] interpolationBuffer)
	{
		double y0 = interpolationBuffer[0];
		double y1 = interpolationBuffer[1];
		
		double x = relativeOffsetBetweenCenterPoints;
		
		return y0 * (1 - x) + y1 * x;
	}
}
