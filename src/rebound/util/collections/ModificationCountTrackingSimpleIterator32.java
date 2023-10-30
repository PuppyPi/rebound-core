package rebound.util.collections;

import rebound.annotations.hints.ImplementationTransparency;
import rebound.util.collections.SimpleIterator;

@ImplementationTransparency
public interface ModificationCountTrackingSimpleIterator32<E>
extends ModificationCountTrackingIterator32, SimpleIterator<E>
{
}
