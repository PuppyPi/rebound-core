package rebound.util.collections;

import rebound.util.objectutil.EqualityComparator;

public interface CollectionWithGetExtantInstanceSpecified<E>
{
	public E getExtantInstance(E possiblyEquivalentButDifferentInstance, EqualityComparator<E> equalityComparator);
}