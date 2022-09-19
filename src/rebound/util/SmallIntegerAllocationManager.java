package rebound.util;

import javax.annotation.Nonnegative;

public interface SmallIntegerAllocationManager
{
	public @Nonnegative int allocateNew();
	
	public boolean isUsed(@Nonnegative int number);
	
	public void notifyUsed(@Nonnegative int number);
	public void notifyFreed(@Nonnegative int number);
}
