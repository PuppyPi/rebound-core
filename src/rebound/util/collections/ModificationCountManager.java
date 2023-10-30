package rebound.util.collections;

import rebound.annotations.hints.ImplementationTransparency;

@ImplementationTransparency
public interface ModificationCountManager
{
	public void invalidateAllIterators();
}
