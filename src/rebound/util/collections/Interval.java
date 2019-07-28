package rebound.util.collections;

import javax.annotation.concurrent.Immutable;
import rebound.annotations.hints.IntendedToBeSubclassedImplementedOrOverriddenByApiUser;

@Immutable
public class Interval<RuntimeType extends Interval<RuntimeType>>
{
	protected final int offset;
	protected final int length;
	
	
	public Interval(int offset, int length)
	{
		this.offset = offset;
		this.length = length;
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
}
