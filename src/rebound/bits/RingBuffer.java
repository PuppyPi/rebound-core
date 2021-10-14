package rebound.bits;

import static rebound.math.SmallIntegerMathUtilities.*;
import javax.annotation.concurrent.NotThreadSafe;
import rebound.annotations.semantic.temporal.concurrencyprimitives.threadspecification.AnyThreads;
import rebound.util.collections.Interval;
import rebound.util.collections.Slice;

@NotThreadSafe
public class RingBuffer
{
	protected final int capacity;
	
	//The used space
	protected int start;  //must never = capacity
	protected int pastEnd;  //must never = capacity
	
	public RingBuffer(int capacity)
	{
		this.capacity = capacity;
		this.start = 0;
		this.pastEnd = 0;
	}
	
	
	@AnyThreads
	public int getTotalSize()
	{
		return capacity;
	}
	
	public int getUsedSize()
	{
		if (pastEnd >= start)
			return pastEnd - start;
		else
			return capacity - pastEnd + start;
	}
	
	public int getRemainingSize()
	{
		return getTotalSize() - getUsedSize();
	}
	
	
	
	/**
	 * This will allocate as much as it contiguously can, so the returned {@link Slice#getLength()} might be less than the amount requested—it might even be 0!
	 * Calling this twice guarantees you'll get all of it.
	 * 
	 * Use {@link System#arraycopy(Object, int, Object, int, int)} or equivalent on the actual buffer now as desired.
	 */
	public Interval allocate(int amount)
	{
		int pastEnd = this.pastEnd;  //this.pastEnd gets changed and keeping the original is important!
		
		if (pastEnd >= start)
		{
			int limit = capacity;
			
			amount = least(amount, limit - pastEnd);
			this.pastEnd = wrap(pastEnd + amount, capacity);
		}
		else
		{
			int limit = start;
			
			amount = least(amount, limit - pastEnd);
			this.pastEnd = pastEnd + amount;
		}
		
		return new Interval(pastEnd, amount);
	}
	
	
	/**
	 * This will deallocate as much as it contiguously can, so the returned {@link Slice#getLength()} might be less than the amount requested—it might even be 0!
	 * Calling this twice guarantees you'll get all of it.
	 * 
	 * Use {@link System#arraycopy(Object, int, Object, int, int)} or equivalent on the actual buffer now as desired.
	 */
	public Interval deallocate(int amount)
	{
		int start = this.start;  //this.start gets changed and keeping the original is important!
		
		if (pastEnd >= start)
		{
			int limit = pastEnd;
			
			amount = least(amount, limit - start);
			this.start = start + amount;
		}
		else
		{
			int limit = capacity;
			
			amount = least(amount, limit - start);
			this.start = wrap(start + amount, capacity);
		}
		
		return new Interval(start, amount);
	}
}
