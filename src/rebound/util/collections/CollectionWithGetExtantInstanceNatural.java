package rebound.util.collections;

public interface CollectionWithGetExtantInstanceNatural<E>
{
	public E getExtantInstance(E possiblyEquivalentButDifferentInstance);
}