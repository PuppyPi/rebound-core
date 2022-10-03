package rebound.util.collections;

import rebound.annotations.semantic.temporal.IdempotentOperation;
import rebound.exceptions.StopIterationReturnPath;

public interface BidirectionalSimpleIterator<E>
extends SimpleIterator<E>
{
	/**
	 * Can be called more than once once it's at BOF and it will keep throwing {@link StopIterationReturnPath}!
	 */
	public E prevrp() throws StopIterationReturnPath;
	
	
	@IdempotentOperation
	public default void reset()
	{
		while (true)
		{
			try
			{
				prevrp();
			}
			catch (StopIterationReturnPath exc)
			{
				break;
			}
		}
	}
}
