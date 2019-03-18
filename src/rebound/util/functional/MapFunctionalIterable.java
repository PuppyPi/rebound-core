package rebound.util.functional;

public interface MapFunctionalIterable<K, V>
{
	public SuccessfulIterationStopType iterate(MapFunctionalIterator<K, V> observer);
	
	
	/**
	 * This only exists as a separate method for the type signature, which makes Java 8 lambdas very nice to use :33
	 */
	public default void iterateUnstoppable(MapFunctionalIteratorUnstoppable<K, V> observer)
	{
		SuccessfulIterationStopType r = iterate(observer);
		assert r == SuccessfulIterationStopType.CompletedNaturally;
	}
}
