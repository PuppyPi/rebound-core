package rebound.util.collections;

import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.simpledata.ActuallyUnsigned;

@ImplementationTransparency
public interface ModificationCountTrackingIterator64
{
	public @ActuallyUnsigned long getModificationCount();
	public void setModificationCount(@ActuallyUnsigned long modificationCount);
}
