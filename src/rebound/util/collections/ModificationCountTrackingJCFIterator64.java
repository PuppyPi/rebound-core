package rebound.util.collections;

import java.util.Iterator;
import rebound.annotations.hints.ImplementationTransparency;

@ImplementationTransparency
public interface ModificationCountTrackingJCFIterator64<E>
extends ModificationCountTrackingIterator64, Iterator<E>
{
}
