/*
 * Created on Jan 14, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.math.geom2d;

import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.hci.graphics2d.gui.util.ImmutableDimension2D;

public interface Direction2D
{
	public static final HorizontalDirection2D Left = HorizontalDirection2D.Left;
	public static final HorizontalDirection2D Right = HorizontalDirection2D.Right;
	public static final VerticalDirection2D Up = VerticalDirection2D.Up;
	public static final VerticalDirection2D Down = VerticalDirection2D.Down;
	public static final Axis2D XHorizontal = Axis2D.XHorizontal;
	public static final Axis2D YVertical = Axis2D.YVertical;
	
	
	public static enum Axis2D
	{
		XHorizontal
		{
			@Override
			public Axis2D perpendicularAxis()
			{
				return YVertical;
			}
			
			@Override
			public Direction2D positiveDirection()
			{
				return Direction2D.Right;
			}
			
			@Override
			public Direction2D negativeDirection()
			{
				return Direction2D.Left;
			}
			
			@Override
			public Point2D newPoint(double onThisAxis, double onOtherAxis)
			{
				return new Point2D.Double(onThisAxis, onOtherAxis);
			}
			
			@Override
			public Dimension2D newSpanPair(double onThisAxis, double onOtherAxis)
			{
				return new ImmutableDimension2D(onThisAxis, onOtherAxis);
			}
			
			@Override
			public double getPointCoordinateOnThisAxis(Point2D p)
			{
				return p.getX();
			}
			
			@Override
			public double getCoordinateSpanOnThisAxis(Dimension2D d)
			{
				return d.getWidth();
			}
		},
		
		
		
		YVertical
		{
			@Override
			public Axis2D perpendicularAxis()
			{
				return XHorizontal;
			}
			
			@Override
			public Direction2D positiveDirection()
			{
				return Direction2D.Down;
			}
			
			@Override
			public Direction2D negativeDirection()
			{
				return Direction2D.Up;
			}
			
			@Override
			public Point2D newPoint(double onThisAxis, double onOtherAxis)
			{
				return new Point2D.Double(onOtherAxis, onThisAxis);
			}
			
			@Override
			public Dimension2D newSpanPair(double onThisAxis, double onOtherAxis)
			{
				return new ImmutableDimension2D(onOtherAxis, onThisAxis);
			}
			
			@Override
			public double getPointCoordinateOnThisAxis(Point2D p)
			{
				return p.getY();
			}
			
			@Override
			public double getCoordinateSpanOnThisAxis(Dimension2D d)
			{
				return d.getHeight();
			}
		},
		;
		
		
		
		@Nonnull
		public abstract Axis2D perpendicularAxis();
		
		@Nonnull
		public abstract Direction2D positiveDirection();
		
		@Nonnull
		public Direction2D negativeDirection()
		{
			return positiveDirection().oppositeDirection();
		}
		
		@Nonnull
		public Direction2D direction(boolean positive)
		{
			return positive ? positiveDirection() : negativeDirection();
		}
		
		
		public abstract @Nonnull Point2D newPoint(double onThisAxis, double onOtherAxis);
		public abstract @Nonnull Dimension2D newSpanPair(double onThisAxis, double onOtherAxis);
		public abstract double getPointCoordinateOnThisAxis(Point2D p);
		public abstract double getCoordinateSpanOnThisAxis(Dimension2D d);
	}
	
	
	
	
	
	
	
	
	public static int getSignumOnAxis(@Nullable Direction2D d)
	{
		return d == null ? 0 : d.getSignumOnAxis();
	}
	
	
	
	
	public @Nonnull Direction2D oppositeDirection();
	public @Nonnull Axis2D getAxis();
	public int getSignumOnAxis();
	public @Nonnull Point2D getUnitVector();
	
	public @Nonnull Line2D getSideOfRectangle(Rectangle2D rectangle);
	public @Nonnull Point2D getPointOnCenterSideOfRectangle(Rectangle2D rectangle);
	public double getPositionInwardAwayFromSideInThisDirection(double amount, Rectangle2D box);
	
	public default double getPositionInwardAwayFromSideInThisDirection(double amount, Dimension2D box)
	{
		return getPositionInwardAwayFromSideInThisDirection(amount, new Rectangle2D.Double(0, 0, box.getWidth(), box.getHeight()));
	}
	
	
	
	
	
	public static enum HorizontalDirection2D
	implements Direction2D
	{
		Left
		{
			@Override
			public Direction2D oppositeDirection()
			{
				return Right;
			}
			
			@Override
			public int getSignumOnAxis()
			{
				return -1;
			}
			
			@Override
			public Point2D getUnitVector()
			{
				return new Point2D.Double(-1, 0);
			}
			
			@Override
			public Line2D getSideOfRectangle(Rectangle2D r)
			{
				return new Line2D.Double(r.getMinX(), r.getMinY(), r.getMinX(), r.getMaxY());
			}
			
			@Override
			public Point2D getPointOnCenterSideOfRectangle(Rectangle2D rectangle)
			{
				return new Point2D.Double(rectangle.getMinX(), rectangle.getCenterY());
			}
			
			@Override
			public double getPositionInwardAwayFromSideInThisDirection(double amount, Rectangle2D box)
			{
				return box.getMaxX() - amount;
			}
		},
		
		Right
		{
			@Override
			public Direction2D oppositeDirection()
			{
				return Left;
			}
			
			@Override
			public int getSignumOnAxis()
			{
				return 1;
			}
			
			@Override
			public Point2D getUnitVector()
			{
				return new Point2D.Double(1, 0);
			}
			
			@Override
			public Line2D getSideOfRectangle(Rectangle2D r)
			{
				return new Line2D.Double(r.getMaxX(), r.getMaxY(), r.getMaxX(), r.getMinY());
			}
			
			@Override
			public Point2D getPointOnCenterSideOfRectangle(Rectangle2D rectangle)
			{
				return new Point2D.Double(rectangle.getMaxX(), rectangle.getCenterY());
			}
			
			@Override
			public double getPositionInwardAwayFromSideInThisDirection(double amount, Rectangle2D box)
			{
				return amount;
			}
		},
		;
		
		
		
		@Override
		public Axis2D getAxis()
		{
			return Axis2D.XHorizontal;
		}
	}
	
	
	
	
	
	
	
	
	public static enum VerticalDirection2D
	implements Direction2D
	{
		Up
		{
			@Override
			public Direction2D oppositeDirection()
			{
				return Down;
			}
			
			@Override
			public int getSignumOnAxis()
			{
				return -1;
			}
			
			@Override
			public Point2D getUnitVector()
			{
				return new Point2D.Double(0, -1);
			}
			
			@Override
			public Line2D getSideOfRectangle(Rectangle2D r)
			{
				return new Line2D.Double(r.getMaxX(), r.getMinY(), r.getMinX(), r.getMinY());
			}
			
			@Override
			public Point2D getPointOnCenterSideOfRectangle(Rectangle2D rectangle)
			{
				return new Point2D.Double(rectangle.getCenterX(), rectangle.getMinY());
			}
			
			@Override
			public double getPositionInwardAwayFromSideInThisDirection(double amount, Rectangle2D box)
			{
				return box.getMaxY() - amount;
			}
		},
		
		Down
		{
			@Override
			public Direction2D oppositeDirection()
			{
				return Up;
			}
			
			@Override
			public int getSignumOnAxis()
			{
				return 1;
			}
			
			@Override
			public Point2D getUnitVector()
			{
				return new Point2D.Double(0, 1);
			}
			
			@Override
			public Line2D getSideOfRectangle(Rectangle2D r)
			{
				return new Line2D.Double(r.getMinX(), r.getMaxY(), r.getMaxX(), r.getMaxY());
			}
			
			@Override
			public Point2D getPointOnCenterSideOfRectangle(Rectangle2D rectangle)
			{
				return new Point2D.Double(rectangle.getCenterX(), rectangle.getMaxY());
			}
			
			@Override
			public double getPositionInwardAwayFromSideInThisDirection(double amount, Rectangle2D box)
			{
				return amount;
			}
		},
		;
		
		
		
		@Override
		public Axis2D getAxis()
		{
			return Axis2D.YVertical;
		}
	}
	
	
	
	
	
	/**
	 * Multiply by {@value Math#PI} / 2 to get the angle in radians in the standard right-handed 2D coordinate system! :D
	 * (eg, mathematics/science convention)
	 */
	public static int getQuadrantAngleIndexRH(Direction2D d)
	{
		if (d == Right)
			return 0;
		else if (d == Up)
			return 1;
		else if (d == Left)
			return 2;
		else if (d == Down)
			return 3;
		
		throw Direction2DInlinedReboundUtilities.newClassCastExceptionOrNullPointerException(d);
	}
	
	
	/**
	 * Multiply by {@value Math#PI} / 2 to get the angle in radians in the left-handed 2D coordinate system! :D
	 * (eg, computer graphics convention)
	 */
	public default int getQuadrantAngleIndex()
	{
		if (this == Right)
			return 0;
		else if (this == Down)
			return 1;
		else if (this == Left)
			return 2;
		else if (this == Up)
			return 3;
		
		throw Direction2DInlinedReboundUtilities.newClassCastExceptionOrNullPointerException(this);
	}
	
	
	
	
	
	
	
	@ImplementationTransparency
	public static final Direction2D[] inQuadrantRotationOrderRH = {Right, Up, Left, Down};
	
	
	@ImplementationTransparency
	public static final Direction2D[] inQuadrantRotationOrderLH = {Right, Down, Left, Up};
	
	
	
	public static Direction2D fromQuadantIndexRH(int quadrantIndex)
	{
		quadrantIndex = Direction2DInlinedReboundUtilities.progmod(quadrantIndex, 4);
		return inQuadrantRotationOrderRH[quadrantIndex];
	}
	
	public static Direction2D fromQuadantIndexLH(int quadrantIndex)
	{
		quadrantIndex = Direction2DInlinedReboundUtilities.progmod(quadrantIndex, 4);
		return inQuadrantRotationOrderLH[quadrantIndex];
	}
	
	
	
	
	
	
	
	
	public static Point2D positionInwardFromSides(Direction2D directionOf1, double amount1, Direction2D directionOf2, double amount2, Rectangle2D box)
	{
		if (directionOf1.getAxis() == directionOf2.getAxis())
		{
			//GlobalCodeMetastuffContext.logBug();
			return Direction2DInlinedReboundUtilities.rectcenter(box);
		}
		
		double p1 = directionOf1.getPositionInwardAwayFromSideInThisDirection(amount1, box);
		double p2 = directionOf2.getPositionInwardAwayFromSideInThisDirection(amount2, box);
		return directionOf1.getAxis().newPoint(p1, p2);
	}
	
	public static Point2D positionInwardFromSides(Direction2D directionOf1, double amount1, Direction2D directionOf2, double amount2, Dimension2D box)
	{
		return positionInwardFromSides(directionOf1, amount1, directionOf2, amount2, new Rectangle2D.Double(0, 0, box.getWidth(), box.getHeight()));
	}
	
	
	
	
	
	public static Rectangle2D newRectangleByTwoPerpendicularDirectionsFromOriginCorner(Point2D originCornerPoint, Direction2D directionOf1, double amount1, Direction2D directionOf2, double amount2)
	{
		Axis2D axis1 = directionOf1.getAxis();
		Axis2D axis2 = directionOf2.getAxis();
		
		if (axis1 == axis2)
		{
			//GlobalCodeMetastuffContext.logBug();
			double avg = (amount1 + amount2) / 2;
			return new Rectangle2D.Double(originCornerPoint.getX(), originCornerPoint.getY(), avg, avg);
		}
		
		
		
		
		double x = originCornerPoint.getX();
		double y = originCornerPoint.getY();
		
		
		double Δx, Δy;
		{
			double s1 = amount1 * directionOf1.getSignumOnAxis();
			double s2 = amount2 * directionOf2.getSignumOnAxis();
			
			if (axis1 == Axis2D.XHorizontal)
			{
				Δx = s1;
				Δy = s2;
			}
			else
			{
				Δx = s2;
				Δy = s1;
			}
		}
		
		
		return new Rectangle2D.Double(x, y, x+Δx, y+Δy);
	}
}
