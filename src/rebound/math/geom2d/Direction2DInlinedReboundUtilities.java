package rebound.math.geom2d;

import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import javax.annotation.Nonnull;
import rebound.exceptions.DivisionByZeroException;
import rebound.exceptions.StructuredClassCastException;

//RCINLINE//
class Direction2DInlinedReboundUtilities
{
	static RuntimeException newClassCastExceptionOrNullPointerException(Object o)
	{
		if (o == null)
			return new NullPointerException();
		else
			return new StructuredClassCastException(o.getClass());
	}
	
	static int progmod(int index, int highBound)
	{
		if (highBound == 0)
			throw new DivisionByZeroException();
		
		//does this work? is it fasters? :>
		return (index % highBound + highBound) % highBound;
		//edit: seems to! :D!
		
		//		if (index >= 0)
		//			return index % highBound;
		//		else //if (n < 0)
		//			return index - (floorDivision(index, highBound)*highBound);
	}
	
	@Nonnull
	static Point2D rectcenter(RectangularShape shape)
	{
		return new Point2D.Double(shape.getCenterX(), shape.getCenterY());
	}
}
