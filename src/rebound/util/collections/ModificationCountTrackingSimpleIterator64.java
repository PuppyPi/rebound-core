package rebound.util.collections;

import rebound.annotations.hints.ImplementationTransparency;
import rebound.util.collections.SimpleIterator;

@ImplementationTransparency
public interface ModificationCountTrackingSimpleIterator64<E>
extends ModificationCountTrackingIterator64, SimpleIterator<E>
{
}
