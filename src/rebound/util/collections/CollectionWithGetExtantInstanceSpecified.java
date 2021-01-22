package rebound.util.collections;

import rebound.util.functional.EqualityComparator;

public interface CollectionWithGetExtantInstanceSpecified<E>
{
	public E getExtantInstance(E possiblyEquivalentButDifferentInstance, EqualityComparator<E> equalityComparator);
}