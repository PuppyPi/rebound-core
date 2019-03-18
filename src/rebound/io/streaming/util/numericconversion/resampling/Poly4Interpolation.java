package rebound.io.streaming.util.numericconversion.resampling;

public interface Poly4Interpolation
extends InterpolationSupermixin
{
	@Override
	public default int getInterpolationOrder()
	{
		return 4;
	}
	
	
	@Override
	public default double interpolate(double relativeOffsetBetweenCenterPoints, double[] interpolationBuffer)
	{
		double y0 = interpolationBuffer[0];
		double y1 = interpolationBuffer[1];
		double y2 = interpolationBuffer[2];
		double y3 = interpolationBuffer[3];
		
		
		//The curve = a*x^3 + b*x^2 + c*x + d   :D
		// Where the x's are normalized to be 0, 1, 2, 3 respectively :>
		double a, b, c, d;
		{
			a = (y3-3*y2+3*y1-y0)/6;
			b = -(y3-4*y2+5*y1-2*y0)/2;
			c = (2*y3-9*y2+18*y1-11*y0)/6;
			d = y0;
		}
		
		
		double x = relativeOffsetBetweenCenterPoints;
		
		return ((a*x + b)*x + c)*x + d;
	}
}
