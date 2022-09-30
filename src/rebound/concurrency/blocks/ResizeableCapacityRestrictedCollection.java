package rebound.concurrency.blocks;

import javax.annotation.Nonnegative;
import rebound.annotations.semantic.temporal.ConstantReturnValue;
import rebound.util.collections.CapacityRestrictedCollection;

public interface ResizeableCapacityRestrictedCollection<E>
extends CapacityRestrictedCollection<E>
{
	/**
	 * This is the implementation-decided (inclusive) maximum that the user can set the user-decided maximum to with {@link #setCapacity(int)}.
	 * @return {@link Integer#MAX_VALUE} for "infinity" because {@link #size() we're already restricted to that anyway} XD''
	 */
	@ConstantReturnValue
	@Nonnegative
	public int getMaxCapacity();
	
	
	/**
	 * @throws IllegalArgumentException  if the provided capacity is > {@link #getMaxCapacity()}  (not >=)
	 */
	public void setCapacity(@Nonnegative int capacity) throws IllegalArgumentException;
	
	@Override
	public @Nonnegative int getCapacity();
}
