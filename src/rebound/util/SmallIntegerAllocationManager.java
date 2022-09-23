package rebound.util;

import javax.annotation.Nonnegative;
import javax.annotation.concurrent.NotThreadSafe;
import rebound.annotations.semantic.temporal.IdempotentOperation;

/**
 * Note: things rely on the indexes produced/provided being Non-Negative so that is a guaranteed aspect of this API that will never change.
 */
@NotThreadSafe  //subclasses may be thread safe, but implementing this interface by no means guarantees it (which should be the default assumption, but just to be sure!)
public interface SmallIntegerAllocationManager
{
	/**
	 * Finds an arbitrary free integer, marks it used, and returns it :>
	 * Subclasses and subinterfaces will surely have more constraints on this arbitrariness of which one is chosen next.
	 */
	public @Nonnegative int allocateNew();
	
	
	public boolean isUsed(@Nonnegative int number);
	
	
	/**
	 * This quietly does nothing if it already was marked as such (making it an {@link IdempotentOperation})
	 */
	@IdempotentOperation
	public void notifyUsed(@Nonnegative int number);
	
	
	/**
	 * This quietly does nothing if it already was marked as such (making it an {@link IdempotentOperation})
	 */
	@IdempotentOperation
	public void notifyFreed(@Nonnegative int number);
	
	
	
	
	
	//These are especially useful in testing the implementations of this interface!
	
	/**
	 * @return -1 if {@link #isAnyUsed() none are used}
	 */
	public int getLowestUsed();
	
	/**
	 * @return -1 if {@link #isAnyUsed() none are used}
	 */
	public int getHighestUsed();
	
	
	public boolean isAnyUsed();
	
	
	public void notifyAllFreed();
}
