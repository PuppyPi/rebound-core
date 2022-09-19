package rebound.util;

import javax.annotation.Nonnegative;

/**
 * Note: things rely on the indexes produced/provided being Non-Negative so that is a guaranteed aspect of this API that will never change.
 */
public interface SmallIntegerAllocationManager
{
	public @Nonnegative int allocateNew();
	
	public boolean isUsed(@Nonnegative int number);
	
	public void notifyUsed(@Nonnegative int number);
	public void notifyFreed(@Nonnegative int number);
}
