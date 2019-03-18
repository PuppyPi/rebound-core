package rebound.util.functional;

public interface CollectionFunctionalIterable<E>
{
	public SuccessfulIterationStopType iterate(CollectionFunctionalIterator<E> observer);
	
	
	/**
	 * This only exists as a separate method for the type signature, which makes Java 8 lambdas very nice to use :33
	 */
	public default void iterateUnstoppable(CollectionFunctionalIteratorUnstoppable<E> observer)
	{
		SuccessfulIterationStopType r = iterate(observer);
		assert r == SuccessfulIterationStopType.CompletedNaturally;
	}
}
