package rebound.util.collections;

import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.simpledata.ActuallyUnsigned;

@ImplementationTransparency
public interface ModificationCountManager32
extends ModificationCountManager
{
	public @ActuallyUnsigned int getModificationCount();
	public void setModificationCount(@ActuallyUnsigned int modificationCount);
	
	
	public default void invalidateAllIteratorsExcept(ModificationCountTrackingIterator32 i)
	{
		invalidateAllIterators();
		i.setModificationCount(this.getModificationCount());
	}
}
