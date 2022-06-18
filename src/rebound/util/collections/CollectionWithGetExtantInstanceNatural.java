package rebound.util.collections;

import rebound.annotations.semantic.SignalType;

@SignalType
public interface CollectionWithGetExtantInstanceNatural<E>
{
	public E getExtantInstance(E possiblyEquivalentButDifferentInstance);
}