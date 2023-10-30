package rebound.util.collections;

import rebound.annotations.hints.ImplementationTransparency;
import rebound.util.collections.SimpleIterator.SimpleIteratorWithRemove;

@ImplementationTransparency
public interface ModificationCountTrackingSimpleIteratorWithRemove64<E>
extends ModificationCountTrackingIterator64, SimpleIteratorWithRemove<E>
{
}
