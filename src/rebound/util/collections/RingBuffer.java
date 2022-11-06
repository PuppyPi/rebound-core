package rebound.util.collections;

import static rebound.math.SmallIntegerMathUtilities.*;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.temporal.concurrencyprimitives.threadspecification.AnyThreads;

@NotThreadSafe
public class RingBuffer
{
	protected final int capacity;
	
	//The used space
	protected int start;  //must never = capacity, use 0 instead
	protected int pastEnd;  //must never = capacity, use 0 instead
	protected boolean full;  //when pastEnd = start, this tells whether it's a full buffer or an empty one XD''
	
	public RingBuffer(int capacity)
	{
		this.capacity = requireNonNegative(capacity);
		this.start = 0;
		this.pastEnd = 0;
		this.full = false;
	}
	
	
	@AnyThreads
	public int getTotalSpace()
	{
		return capacity;
	}
	
	public int getUsedSpace()
	{
		if (pastEnd > start || (pastEnd == start && !full))
			return pastEnd - start;
		else
			return capacity - start + pastEnd;
	}
	
	public int getFreeSpace()
	{
		return getTotalSpace() - getUsedSpace();
	}
	
	
	@ImplementationTransparency
	public int getStart()
	{
		return start;
	}
	
	@ImplementationTransparency
	public int getPastEnd()
	{
		return pastEnd;
	}
	
	/**
	 * Meaningless and undefined if {@link #getTotalSpace()} == 0
	 */
	@ImplementationTransparency
	public boolean isFull()
	{
		return (start == pastEnd) && full;
	}
	
	
	
	
	/**
	 * This will allocate as much as it contiguously can, so the returned {@link Slice#getLength()} might be less than the amount requested—it might even be 0!
	 * Calling this twice guarantees you'll get all of it.
	 * 
	 * Use {@link System#arraycopy(Object, int, Object, int, int)} or equivalent on the actual buffer now as desired.
	 * 
	 * @param update  if false, this just peeks at the available region, otherwise it actually moves the buffer!
	 */
	public @Nonnull Interval allocate(int amount, boolean update)
	{
		requireNonNegative(amount);
		
		int pastEnd = this.pastEnd;  //this.pastEnd gets changed and keeping the original is important!
		
		if (amount != 0)
		{
			if (pastEnd > start || (pastEnd == start && !full))
			{
				int limit = capacity;
				
				amount = least(amount, limit - pastEnd);
				
				if (update)
				{
					this.pastEnd = wrap(pastEnd + amount, capacity);
					
					if (this.pastEnd == start)  //&& amount > 0)    //it can if it wraps back to 0!
						full = true;
				}
			}
			else
			{
				int limit = start;
				
				amount = least(amount, limit - pastEnd);
				
				if (update)
				{
					this.pastEnd = pastEnd + amount;
					
					if (this.pastEnd == start)  //&& amount > 0)    //it always can X3
						full = true;
				}
			}
		}
		
		return new Interval(pastEnd, amount);
	}
	
	
	/**
	 * This will deallocate as much as it contiguously can, so the returned {@link Slice#getLength()} might be less than the amount requested—it might even be 0!
	 * Calling this twice guarantees you'll get all of it.
	 * 
	 * Use {@link System#arraycopy(Object, int, Object, int, int)} or equivalent on the actual buffer now as desired.
	 * 
	 * @param update  if false, this just peeks at the available region, otherwise it actually moves the buffer!
	 */
	public @Nonnull Interval deallocate(int amount, boolean update)
	{
		requireNonNegative(amount);
		
		final int start = this.start;  //this.start gets changed and keeping the original is important!
		
		if (amount != 0)
		{
			if (pastEnd > start || (pastEnd == start && !full))
			{
				int limit = pastEnd;
				
				amount = least(amount, limit - start);
				
				if (update)
				{
					this.start = start + amount;
					
					if (this.start == pastEnd)  //&& amount > 0)    //it always can
					{
						full = false;
						
						//Move both to 0 when deallocating everything so future allocates don't need to split the buffer!
						this.start = 0;
						this.pastEnd = 0;
					}
				}
			}
			else
			{
				int limit = capacity;
				
				amount = least(amount, limit - start);
				
				if (update)
				{
					this.start = wrap(start + amount, capacity);
					
					if (this.start == pastEnd)  //&& amount > 0)    //it can if it wraps back to 0!
					{
						full = false;
						
						//Move both to 0 when deallocating everything so future allocates don't need to split the buffer!
						this.start = 0;
						this.pastEnd = 0;
					}
				}
			}
		}
		
		return new Interval(start, amount);
	}
}
