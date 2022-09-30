package rebound.util.collections;

import java.util.Collection;
import javax.annotation.Nonnegative;
import rebound.exceptions.CapacityReachedException;

public interface CapacityRestrictedCollection<E>
extends Collection<E>
{
	/**
	 * This is the (inclusive) maximum of {@link #size()}.
	 * Once the collection is at capacity ({@link #size()} == {@link #getCapacity()}), any attempts to {@link #add(Object) add} more elements will result in a {@link CapacityReachedException} being thrown and nothing done.
	 * 
	 * @return {@link Integer#MAX_VALUE} for "infinity" because {@link #size() we're already restricted to that anyway} XD''
	 */
	public @Nonnegative int getCapacity();
	
	
	@Override
	public boolean add(E e) throws CapacityReachedException;
	
	/**
	 * @throws CapacityReachedException  if this is thrown, nothing was done (not a single element added) and you can cleanly try again later when conditions are different!
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) throws CapacityReachedException;
}
