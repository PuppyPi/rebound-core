package rebound.util.collections;

import java.util.Collection;
import javax.annotation.Nonnegative;
import rebound.exceptions.CapacityReachedException;

public interface CapacityRestrictedCollection<E>
extends Collection<E>
{
	/**
	 * @return {@link Integer#MAX_VALUE} for "infinity" because {@link #size() we're already restricted to that anyway} XD''
	 */
	public @Nonnegative int getCapacity();
	
	
	@Override
	public boolean add(E e) throws CapacityReachedException;
	
	@Override
	public boolean addAll(Collection<? extends E> c) throws CapacityReachedException;
}
