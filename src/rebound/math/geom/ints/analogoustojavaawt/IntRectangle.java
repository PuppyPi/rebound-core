package rebound.math.geom.ints.analogoustojavaawt;

import static rebound.math.SmallIntegerMathUtilities.*;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;

/**
 * A mostly-drop-in replacement for java.awt.Rectangle but without needing dependencies on anything else in AWT! :D
 */
public class IntRectangle
{
	public int x, y, width, height;
	
	
	public IntRectangle()
	{
		this(0, 0, 0, 0);
	}
	
	public IntRectangle(IntRectangle r)
	{
		this(r.x, r.y, r.width, r.height);
	}
	
	public IntRectangle(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public IntRectangle(IntPoint p, IntDimension d)
	{
		this(p.x, p.y, d.width, d.height);
	}
	
	
	
	
	public IntRectangle getBounds()
	{
		return new IntRectangle(this.x, this.y, this.width, this.height);
	}
	
	public void setBounds(IntRectangle r)
	{
		setBounds(r.x, r.y, r.width, r.height);
	}
	
	public void setBounds(int x, int y, int width, int height)
	{
		setRect(x, y, width, height);
	}
	
	public void setRect(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public IntPoint getLocation()
	{
		return new IntPoint(this.x, this.y);
	}
	
	public void setLocation(IntPoint p)
	{
		setLocation(p.x, p.y);
	}
	
	public void setLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void translate(int dx, int dy)
	{
		this.x += dx;
		this.y += dy;
	}
	
	public IntDimension getSize()
	{
		return new IntDimension(this.width, this.height);
	}
	
	public void setSize(IntDimension d)
	{
		setSize(d.width, d.height);
	}
	
	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}
	
	
	
	public boolean isEmpty()
	{
		return (this.width <= 0) || (this.height <= 0);
	}
	
	
	
	public int getMinX()
	{
		return this.x;
	}
	
	public int getMinY()
	{
		return this.y;
	}
	
	public int getMaxX()
	{
		return this.x + this.width;
	}
	
	public int getMaxY()
	{
		return this.y + this.height;
	}
	
	public int getWidth()
	{
		return this.width;
	}
	
	public int getHeight()
	{
		return this.height;
	}
	
	
	
	
	
	
	
	
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + this.height;
		result = prime * result + this.width;
		result = prime * result + this.x;
		result = prime * result + this.y;
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IntRectangle other = (IntRectangle) obj;
		if (this.height != other.height)
			return false;
		if (this.width != other.width)
			return false;
		if (this.x != other.x)
			return false;
		if (this.y != other.y)
			return false;
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public boolean contains(IntPoint p)
	{
		return contains(p.x, p.y);
	}
	
	public boolean contains(int x, int y)
	{
		return doesContainsPoint(this, x, y);
	}
	
	public boolean contains(IntRectangle r)
	{
		return doesContain(this, r);
	}
	
	public boolean contains(int minX, int minY, int w, int h)
	{
		return doesContain(this, new IntRectangle(minX, minY, w, h));
	}
	
	public boolean intersects(IntRectangle r)
	{
		return doesIntersectOrContain(this, r);
	}
	
	public IntRectangle intersection(IntRectangle r)
	{
		return intersectToNew(this, r);
	}
	
	
	//Todo public IntRectangle unionOP(IntRectangle r)
	//Todo public void unionIP(IntRectangle r)
	
	//Todo public IntRectangle unionWithPointOP(int x, int y)
	//Todo public void unionWithPointIP(int x, int y)
	
	
	
	public static boolean doesContainsPoint(IntRectangle a, int pointX, int pointY)
	{
		int rMinX = a.getMinX();
		int rMaxX = a.getMaxX();
		
		int rMinY = a.getMinY();
		int rMaxY = a.getMaxY();
		
		return pointX >= rMinX && pointX < rMaxX && pointY >= rMinY && pointY < rMaxY;
	}
	
	public static boolean doesContain(IntRectangle outer, IntRectangle inner)
	{
		return
		inner.getMaxX() <= outer.getMaxX() &&
		inner.getMaxY() <= outer.getMaxY() &&
		inner.getMinX() >= outer.getMinX() &&
		inner.getMinY() >= outer.getMinY();
	}
	
	public static boolean doesIntersectOrContain(IntRectangle a, IntRectangle b)
	{
		int newMinX = greatest(a.getMinX(), b.getMinX());
		int newMinY = greatest(a.getMinY(), b.getMinY());
		int newMaxX = least(a.getMaxX(), b.getMaxX());
		int newMaxY = least(a.getMaxY(), b.getMaxY());
		
		int newW = newMaxX - newMinX;
		int newH = newMaxY - newMinY;
		
		return newW > 0 && newH > 0;
	}
	
	
	/**
	 * @param out Note that this can be either A or B! :DD
	 */
	public static void intersect(@ReadonlyValue IntRectangle a, @ReadonlyValue IntRectangle b, @WritableValue IntRectangle out)
	{
		int newMinX = greatest(a.getMinX(), b.getMinX());
		int newMinY = greatest(a.getMinY(), b.getMinY());
		int newMaxX = least(a.getMaxX(), b.getMaxX());
		int newMaxY = least(a.getMaxY(), b.getMaxY());
		out.setRect(newMinX, newMinY, newMaxX - newMinX, newMaxY - newMinY);
	}
	
	public static IntRectangle intersectToNew(@ReadonlyValue IntRectangle a, @ReadonlyValue IntRectangle b)
	{
		IntRectangle r = new IntRectangle();
		intersect(a, b, r);
		return r;
	}
}
