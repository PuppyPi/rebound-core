package rebound.util.collections;

import static rebound.math.SmallIntegerMathUtilities.*;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import rebound.annotations.hints.IntendedToBeSubclassedImplementedOrOverriddenByApiUser;

@Immutable
public class Interval<RuntimeType extends Interval<RuntimeType>>
{
	protected final @Nonnegative int offset;
	protected final @Nonnegative int length;
	
	
	public Interval(@Nonnegative int offset, @Nonnegative int length)
	{
		this.offset = requireNonNegative(offset);
		this.length = requireNonNegative(length);
	}
	
	@Nonnegative
	public int getOffset()
	{
		return this.offset;
	}
	
	@Nonnegative
	public int getLength()
	{
		return this.length;
	}
	
	@Nonnegative
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
	
	
	
	
	@Nonnull
	public RuntimeType subslice(@Nonnegative int offset, @Nonnegative int length)
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
	
	@Nonnull
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	protected RuntimeType subslice0(@Nonnegative int offset, @Nonnegative int length)
	{
		return (RuntimeType)new Interval(this.offset + offset, length);
	}
	
	
	
	
	
	
	
	
	@Nonnull
	public RuntimeType subsliceByExclusiveBound(@Nonnegative int start, @Nonnegative int end)
	{
		return subslice(start, end - start);
	}
	
	@Nonnull
	public RuntimeType subsliceToEnd(@Nonnegative int offset)
	{
		return subslice(offset, this.length - offset);
	}
	
	@Nonnull
	public RuntimeType subsliceFromBeginning(@Nonnegative int lengthOrExclusiveEndingBound)
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
