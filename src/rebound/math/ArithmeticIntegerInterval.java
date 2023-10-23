package rebound.math;

import static java.util.Objects.*;
import static rebound.math.MathUtilities.*;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import rebound.exceptions.OverflowException;

@Immutable
public class ArithmeticIntegerInterval
{
	protected final @PolyInteger @Nonnull Object start;
	protected final @Nonnegative @PolyInteger @Nonnull Object size;
	
	
	public ArithmeticIntegerInterval(@PolyInteger Object start, @PolyInteger Object size)
	{
		requireNonNull(start);
		requireNonNull(size);
		requireNonNegativePoly(size);
		
		this.start = start;
		this.size = size;
		
		if (mathcmp(size, 0) < 0)
			throw new OverflowException();
		
		if (mathcmp(add(start, size), start) < 0)
			throw new OverflowException();
	}
	
	
	public @PolyInteger Object getStart()
	{
		return this.start;
	}
	
	public @PolyInteger Object getSize()
	{
		return this.size;
	}
	
	public @PolyInteger Object getPastEnd()
	{
		return add(this.start, this.size);
	}
	
	
	public boolean isEmpty()
	{
		return matheq(getSize(), 0);
	}
	
	
	public boolean containsPoint(@PolyInteger Object value)
	{
		//return value >= start && value < start + size;
		
		value = subtract(value, this.start);
		return mathcmp(value, 0) >= 0 && mathcmp(value, this.size) < 0;
	}
	
	/**
	 * @return {@link Direction1D#Zero} if 'value' is {@link #containsPoint(Object) inside} this interval, {@link Direction1D#LowerDown} if 'value' is below the start of this interval, and {@link Direction1D#HigherUp} if it's >= the {@link #getPastEnd() high bound} :>
	 */
	public Direction1D comparePoint(@PolyInteger Object value)
	{
		//return value >= start && value < start + size;
		
		value = subtract(value, this.start);
		
		if (mathcmp(value, 0) < 0)
			return Direction1D.LowerDown;
		else if (mathcmp(value, this.size) >= 0)
			return Direction1D.HigherUp;
		else
			return Direction1D.Zero;
	}
	
	
	
	
	
	public ArithmeticIntegerInterval subinterval(@PolyInteger Object start, @PolyInteger Object size)
	{
		if (mathcmp(size, 0) < 0)
			throw new IllegalArgumentException();
		if (mathcmp(start, 0) < 0)
			throw new IndexOutOfBoundsException();
		if (mathcmp(add(start, size), this.size) > 0)
			throw new IndexOutOfBoundsException();
		
		if (matheq(start, 0l) && matheq(size, this.getSize()))
			return this;
		else
			return new ArithmeticIntegerInterval(add(this.start, start), size);
	}
	
	
	
	
	
	
	
	
	public ArithmeticIntegerInterval subintervalByExclusiveBound(@PolyInteger Object start, @PolyInteger Object end)
	{
		return subinterval(start, subtract(end, start));
	}
	
	public ArithmeticIntegerInterval subintervalToEnd(@PolyInteger Object start)
	{
		return subinterval(start, subtract(this.size, start));
	}
	
	public ArithmeticIntegerInterval subintervalFromBeginning(@PolyInteger Object sizeOrExclusiveEndingBound)
	{
		return subinterval(0, sizeOrExclusiveEndingBound);
	}
	
	
	
	
	//Note: empty intervals are not equivalent to each other nor interchangeable!  Many times code will use an empty interval on a point (ie, [x, x]) to represent a single point without needing to use a whole other format than interval-typed values :3
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArithmeticIntegerInterval other = (ArithmeticIntegerInterval) obj;
		if (!matheq(size, other.size))
			return false;
		if (!matheq(start, other.start))
			return false;
		return true;
	}
	
	
	@Override
	public String toString()
	{
		return "["+getStart()+", "+getPastEnd()+")";
	}
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		return result;
	}
}
