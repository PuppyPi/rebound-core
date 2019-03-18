package rebound.math.geom.ints.analogoustojavaawt;

/**
 * A mostly-drop-in replacement for java.awt.Dimension but without needing dependencies on anything else in AWT! :D
 */
public class IntDimension
{
	public int width, height;
	
	
	public IntDimension()
	{
		this(0, 0);
	}
	
	public IntDimension(IntDimension d)
	{
		this(d.width, d.height);
	}
	
	public IntDimension(int width, int height)
	{
		this.width = width;
		this.height = height;
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
	
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + this.height;
		result = prime * result + this.width;
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
		IntDimension other = (IntDimension) obj;
		if (this.height != other.height)
			return false;
		if (this.width != other.width)
			return false;
		return true;
	}
}
