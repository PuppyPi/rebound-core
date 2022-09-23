package rebound.util;

import java.util.BitSet;
import rebound.annotations.hints.ExplosionAllocate;
import rebound.annotations.hints.RuntimeTypeGuaranteed;

/**
 * Simply scans through the in-use ones until it reaches a free one!
 * But does it about as optimized as possible, using a {@link BitSet} :>
 */
public class NaiveConsecutiveSmallIntegerAllocationManager
implements ConsecutiveSmallIntegerAllocationManager
{
	@ExplosionAllocate
	@RuntimeTypeGuaranteed(BitSet.class)
	protected final BitSet inUse = new BitSet();
	
	@Override
	public int allocateNew()
	{
		int n = inUse.nextClearBit(0);
		notifyUsed(n);
		return n;
	}
	
	@Override
	public boolean isUsed(int number)
	{
		return inUse.get(number);
	}
	
	@Override
	public void notifyUsed(int number)
	{
		inUse.set(number, true);
	}
	
	@Override
	public void notifyFreed(int number)
	{
		inUse.set(number, false);
	}
	
	@Override
	public void notifyAllFreed()
	{
		inUse.clear();
	}
	
	
	
	
	@Override
	public int getLowestUsed()
	{
		return isAnyUsed() ? inUse.nextSetBit(0) : -1;
	}
	
	@Override
	public int getHighestUsed()
	{
		return inUse.length() - 1;  //Handles special case "-1" with arithmetic!
	}
	
	@Override
	public boolean isAnyUsed()
	{
		return !inUse.isEmpty();
	}
}
