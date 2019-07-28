package rebound.math.geom2d;

import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.math.SmallIntegerMathUtilities;
import rebound.math.geom.ints.analogoustojavaawt.IntDimension;
import rebound.math.geom.ints.analogoustojavaawt.IntPoint;
import rebound.math.geom.ints.analogoustojavaawt.IntRectangle;

public class SmallIntegerBasicGeometry2D
{
	public static void makeRectangleDimsZeroIfNegative(@WritableValue IntRectangle r)
	{
		if (r.width < 0) r.width = 0;
		if (r.height < 0) r.height = 0;
	}
	
	
	
	
	public static IntPoint addVector(IntPoint a, IntPoint b)
	{
		return ipoint(a.x + b.x, a.y + b.y);
	}
	
	public static IntPoint subtractPoints(IntPoint a, IntPoint b)
	{
		return addVector(a, negateVector(b));
	}
	
	public static IntPoint negateVector(IntPoint a)
	{
		return ipoint(-a.x, -a.y);
	}
	
	public static IntPoint multiplyVectorByScalar(IntPoint a, int scalar)
	{
		return ipoint(a.x * scalar, a.y * scalar);
	}
	
	@ThrowAwayValue
	public static IntPoint ipoint(int x, int y)
	{
		return new IntPoint(x, y);
	}
	
	@ThrowAwayValue
	public static IntDimension idims(int width, int height)
	{
		return new IntDimension(width, height);
	}
	
	@ThrowAwayValue
	public static IntRectangle irect(int minX, int minY, int width, int height)
	{
		return new IntRectangle(minX, minY, width, height);
	}
	
	@ThrowAwayValue
	public static IntRectangle irectMaxMin(int minX, int minY, int maxX, int maxY)
	{
		return irect(minX, minY, maxX - minX, maxY - minY);
	}
	
	@ThrowAwayValue
	public static IntRectangle irect(IntPoint minCorner, IntDimension size)
	{
		return new IntRectangle(minCorner.x, minCorner.y, size.width, size.height);
	}
	
	@ThrowAwayValue
	public static IntRectangle irectMaxMin(IntPoint minCorner, IntPoint maxCorner)
	{
		return new IntRectangle(minCorner.x, minCorner.y, SmallIntegerMathUtilities.greatest(0, maxCorner.x - minCorner.x), SmallIntegerMathUtilities.greatest(0, maxCorner.y - minCorner.y));
	}
	
	@ThrowAwayValue
	public static IntRectangle irectTwoPoints(IntPoint cornerA, IntPoint cornerB)
	{
		IntPoint a = cornerA;
		IntPoint b = cornerB;
		return irectMaxMin(SmallIntegerMathUtilities.least(a.x, b.x), SmallIntegerMathUtilities.least(a.y, b.y), SmallIntegerMathUtilities.greatest(a.x, b.x), SmallIntegerMathUtilities.greatest(a.y, b.y));
	}
	
	@ThrowAwayValue
	public static IntRectangle irectSigned(IntPoint minCorner, IntPoint maxCorner)
	{
		return new IntRectangle(minCorner.x, minCorner.y, maxCorner.x - minCorner.x, maxCorner.y - minCorner.y);
	}
	
	@ThrowAwayValue
	public static IntPoint irectmin(IntRectangle r)
	{
		return ipoint(r.x, r.y);
	}
	
	@ThrowAwayValue
	public static IntPoint irectmax(IntRectangle r)
	{
		return ipoint(r.x + r.width, r.y + r.height);
	}
	
	@ThrowAwayValue
	public static IntDimension irectsize(IntRectangle r)
	{
		return idims(r.width, r.height);
	}
}
