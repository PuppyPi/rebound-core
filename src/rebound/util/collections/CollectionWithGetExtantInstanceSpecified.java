package rebound.util.collections;

import rebound.annotations.semantic.SignalType;
import rebound.util.functional.EqualityComparator;

@SignalType
public interface CollectionWithGetExtantInstanceSpecified<E>
{
	public E getExtantInstance(E possiblyEquivalentButDifferentInstance, EqualityComparator<E> equalityComparator);
}