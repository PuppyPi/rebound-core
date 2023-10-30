package rebound.util.collections;

import java.util.Iterator;
import rebound.annotations.hints.ImplementationTransparency;

@ImplementationTransparency
public interface ModificationCountTrackingJCFIterator32<E>
extends ModificationCountTrackingIterator32, Iterator<E>
{
}
