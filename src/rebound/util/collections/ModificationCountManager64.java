package rebound.util.collections;

import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.simpledata.ActuallyUnsigned;

@ImplementationTransparency
public interface ModificationCountManager64
extends ModificationCountManager
{
	public @ActuallyUnsigned long getModificationCount();
	public void setModificationCount(@ActuallyUnsigned long modificationCount);
	
	
	public default void invalidateAllIteratorsExcept(ModificationCountTrackingIterator64 i)
	{
		invalidateAllIterators();
		i.setModificationCount(this.getModificationCount());
	}
}
