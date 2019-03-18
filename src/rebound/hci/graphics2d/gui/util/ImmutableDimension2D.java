package rebound.hci.graphics2d.gui.util;

import java.awt.geom.Dimension2D;
import rebound.concurrency.immutability.StaticallyConcurrentlyImmutable;
import rebound.exceptions.ReadonlyUnsupportedOperationException;

public class ImmutableDimension2D
extends Dimension2D
implements StaticallyConcurrentlyImmutable
{
	public static final ImmutableDimension2D ZeroSize = new ImmutableDimension2D(0, 0);
	
	
	
	protected final double width, height;
	
	public ImmutableDimension2D(double width, double height)
	{
		this.width = width;
		this.height = height;
	}
	
	public ImmutableDimension2D(Dimension2D d)
	{
		this(d.getWidth(), d.getHeight());
	}
	
	
	@Override
	public double getWidth()
	{
		return this.width;
	}
	
	@Override
	public double getHeight()
	{
		return this.height;
	}
	
	@Override
	public void setSize(double width, double height)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(this.height);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(this.width);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		
		if (!(obj instanceof Dimension2D))
			return false;
		
		Dimension2D other = (Dimension2D) obj;
		return eqSane(getWidth(), other.getWidth()) && eqSane(getHeight(), other.getHeight());
	}
	
	
	@Override
	public String toString()
	{
		return getWidth() + " x " + getHeight();
	}
	
	
	
	
	
	
	
	
	
	//RCINLINE//
	/**
	 * Like a == b, except that if they're both NaN, then it's true, not false.
	 * Ie,
	 * 		NaN == NaN -> False
	 * 		eqSane(NaN, NaN) -> True
	 * 
	 * ..yeah..the one little horrible thing in IEEE754..which breaks math X'D
	 */
	private static boolean eqSane(double a, double b)
	{
		//if a is not NaN and b is NaN, then it will return false, as it's 'upposeds to ^^
		//return Double.isNaN(a) ? Double.isNaN(b) : a == b;
		return a == b || (a != a && b != b);  //manually inlined :>
	}
}
