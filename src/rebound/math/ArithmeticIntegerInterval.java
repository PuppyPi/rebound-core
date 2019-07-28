package rebound.math;

import javax.annotation.concurrent.Immutable;
import rebound.exceptions.OverflowException;

@Immutable
public class ArithmeticIntegerInterval
{
	protected final long start;
	protected final long size;
	
	
	public ArithmeticIntegerInterval(long start, long size)
	{
		this.start = start;
		this.size = size;
		
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
	
	
	
	
	
	public ArithmeticIntegerInterval subinterval(long start, long size)
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
			return new ArithmeticIntegerInterval(this.start + start, size);
	}
	
	
	
	
	
	
	
	
	public ArithmeticIntegerInterval subintervalByExclusiveBound(long start, long end)
	{
		return subinterval(start, end - start);
	}
	
	public ArithmeticIntegerInterval subintervalToEnd(long start)
	{
		return subinterval(start, this.size - start);
	}
	
	public ArithmeticIntegerInterval subintervalFromBeginning(long sizeOrExclusiveEndingBound)
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
		ArithmeticIntegerInterval other = (ArithmeticIntegerInterval) obj;
		
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
