package rebound.math.geom.ints.analogoustojavaawt;

/**
 * A mostly-drop-in replacement for java.awt.Point but without needing dependencies on anything else in AWT! :D
 */
public class IntPoint
{
	public int x, y;
	
	
	public IntPoint()
	{
		this(0, 0);
	}
	
	public IntPoint(IntPoint p)
	{
		this(p.x, p.y);
	}
	
	public IntPoint(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public int getX()
	{
		return this.x;
	}
	
	public int getY()
	{
		return this.y;
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
	
	
	
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
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
		IntPoint other = (IntPoint) obj;
		if (this.x != other.x)
			return false;
		if (this.y != other.y)
			return false;
		return true;
	}
}
