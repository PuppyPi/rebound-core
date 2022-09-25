package rebound.util.collections;

import static rebound.math.SmallIntegerMathUtilities.*;
import javax.annotation.Nonnegative;
import javax.annotation.concurrent.Immutable;
import rebound.annotations.hints.IntendedToBeSubclassedImplementedOrOverriddenByApiUser;

@Immutable
public class Interval<RuntimeType extends Interval<RuntimeType>>
{
	protected final @Nonnegative int offset;
	protected final @Nonnegative int length;
	
	
	public Interval(int offset, int length)
	{
		this.offset = requireNonNegative(offset);
		this.length = requireNonNegative(length);
	}
	
	public int getOffset()
	{
		return this.offset;
	}
	
	public int getLength()
	{
		return this.length;
	}
	
	public int getPastEnd()
	{
		return this.offset + this.length;
	}
	
	
	public boolean isEmpty()
	{
		return getLength() == 0;
	}
	
	
	public boolean isIndexInUnderlyingInRange(int indexInUnderlying)
	{
		//return indexInUnderlying >= offset && indexInUnderlying < offset + length;
		
		indexInUnderlying -= this.offset;
		return indexInUnderlying >= 0 && indexInUnderlying < this.length;
	}
	
	
	
	
	
	public RuntimeType subslice(int offset, int length)
	{
		if (length < 0)
			throw new IllegalArgumentException();
		if (offset < 0)
			throw new IndexOutOfBoundsException();
		if (offset + length > this.length)
			throw new IndexOutOfBoundsException();
		
		if (offset == 0 && length == this.getLength())
			return (RuntimeType)this;
		else
			return subslice0(offset, length);
	}
	
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	protected RuntimeType subslice0(int offset, int length)
	{
		return (RuntimeType)new Interval(this.offset + offset, length);
	}
	
	
	
	
	
	
	
	
	public RuntimeType subsliceByExclusiveBound(int start, int end)
	{
		return subslice(start, end - start);
	}
	
	public RuntimeType subsliceToEnd(int offset)
	{
		return subslice(offset, this.length - offset);
	}
	
	public RuntimeType subsliceFromBeginning(int lengthOrExclusiveEndingBound)
	{
		return subslice(0, lengthOrExclusiveEndingBound);
	}
	
	
	
	
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + length;
		result = prime * result + offset;
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
		Interval other = (Interval) obj;
		if (length != other.length)
			return false;
		if (offset != other.offset)
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return "["+getOffset()+", "+(getOffset()+getLength())+") : "+getLength();
	}
}
