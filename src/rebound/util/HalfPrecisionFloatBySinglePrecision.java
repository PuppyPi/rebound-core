package rebound.util;

import static rebound.math.MathUtilities.*;

public class HalfPrecisionFloatBySinglePrecision
extends Number
implements Comparable<Number>, ValuelikeType
{
	private static final long serialVersionUID = 1l;
	
	protected final float singlePrecisionVersionInJava;
	
	public HalfPrecisionFloatBySinglePrecision(float singlePrecisionVersionInJava)
	{
		this.singlePrecisionVersionInJava = singlePrecisionVersionInJava;
	}
	
	public float getSinglePrecisionVersionInJava()
	{
		return singlePrecisionVersionInJava;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(singlePrecisionVersionInJava);
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
		HalfPrecisionFloatBySinglePrecision other = (HalfPrecisionFloatBySinglePrecision) obj;
		if (Float.floatToIntBits(singlePrecisionVersionInJava) != Float.floatToIntBits(other.singlePrecisionVersionInJava))
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return String.valueOf(singlePrecisionVersionInJava);
	}
	
	
	
	
	
	
	@Override
	public int intValue()
	{
		return (int)singlePrecisionVersionInJava;
	}
	
	@Override
	public long longValue()
	{
		return (long)singlePrecisionVersionInJava;
	}
	
	@Override
	public float floatValue()
	{
		return singlePrecisionVersionInJava;
	}
	
	@Override
	public double doubleValue()
	{
		return singlePrecisionVersionInJava;
	}
	
	
	
	@Override
	public int compareTo(Number o)
	{
		return mathcmp(singlePrecisionVersionInJava, o);
	}
}
