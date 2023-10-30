package rebound.util.collections;

import rebound.annotations.hints.ImplementationTransparency;
import rebound.util.collections.SimpleIterator.SimpleIteratorWithRemove;

@ImplementationTransparency
public interface ModificationCountTrackingSimpleIteratorWithRemove32<E>
extends ModificationCountTrackingIterator32, SimpleIteratorWithRemove<E>
{
}
