package rebound.util.collections;

import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.simpledata.ActuallyUnsigned;

@ImplementationTransparency
public interface ModificationCountTrackingIterator32
{
	public @ActuallyUnsigned int getModificationCount();
	public void setModificationCount(@ActuallyUnsigned int modificationCount);
}
