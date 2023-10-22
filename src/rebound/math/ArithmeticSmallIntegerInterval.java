package rebound.math;

import static rebound.math.SmallIntegerMathUtilities.*;
import javax.annotation.Nonnegative;
import javax.annotation.concurrent.Immutable;
import rebound.exceptions.OverflowException;

@Immutable
public class ArithmeticSmallIntegerInterval
{
	protected final long start;
	protected final @Nonnegative long size;
	
	
	public ArithmeticSmallIntegerInterval(long start, long size)
	{
		this.start = start;
		this.size = requireNonNegative(size);
		
		if (size < 0)
			throw new OverflowException();
		
		if (start + size < start)
			throw new OverflowException();
	}
	
	
	public long getStart()
	{
		return this.start;
	}
	
	public long getSize()
	{
		return this.size;
	}
	
	public long getPastEnd()
	{
		return this.start + this.size;
	}
	
	
	public boolean isEmpty()
	{
		return getSize() == 0;
	}
	
	
	public boolean containsPoint(long value)
	{
		//return value >= start && value < start + size;
		
		value -= this.start;
		return value >= 0 && value < this.size;
	}
	
	/**
	 * @return {@link Direction1D#Zero} if 'value' is {@link #containsPoint(long) inside} this interval, {@link Direction1D#LowerDown} if 'value' is below the start of this interval, and {@link Direction1D#HigherUp} if it's >= the {@link #getPastEnd() high bound} :>
	 */
	public Direction1D comparePoint(long value)
	{
		//return value >= start && value < start + size;
		
		value -= this.start;
		
		if (value < 0)
			return Direction1D.LowerDown;
		else if (value >= this.size)
			return Direction1D.HigherUp;
		else
			return Direction1D.Zero;
	}
	
	
	
	
	
	public ArithmeticSmallIntegerInterval subinterval(long start, long size)
	{
		if (size < 0)
			throw new IllegalArgumentException();
		if (start < 0)
			throw new IndexOutOfBoundsException();
		if (start + size > this.size)
			throw new IndexOutOfBoundsException();
		
		if (start == 0 && size == this.getSize())
			return this;
		else
			return new ArithmeticSmallIntegerInterval(this.start + start, size);
	}
	
	
	
	
	
	
	
	
	public ArithmeticSmallIntegerInterval subintervalByExclusiveBound(long start, long end)
	{
		return subinterval(start, end - start);
	}
	
	public ArithmeticSmallIntegerInterval subintervalToEnd(long start)
	{
		return subinterval(start, this.size - start);
	}
	
	public ArithmeticSmallIntegerInterval subintervalFromBeginning(long sizeOrExclusiveEndingBound)
	{
		return subinterval(0, sizeOrExclusiveEndingBound);
	}


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (size ^ (size >>> 32));
		result = prime * result + (int) (start ^ (start >>> 32));
		return result;
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
		ArithmeticSmallIntegerInterval other = (ArithmeticSmallIntegerInterval) obj;
		
		if (this.isEmpty())
			return other.isEmpty();
		else if (other.isEmpty())  // && !this.isEmpty()
			return false;
		
		if (size != other.size)
			return false;
		if (start != other.start)
			return false;
		
		return true;
	}


	@Override
	public String toString()
	{
		return "["+getStart()+", "+getPastEnd()+")";
	}
}
