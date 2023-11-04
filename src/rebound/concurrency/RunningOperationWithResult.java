package rebound.concurrency;

import java.util.concurrent.Future;

/**
 * Analogous to a {@link Future} but extending {@link RunningOperation}!
 */
public interface RunningOperationWithResult<R>
extends RunningOperation
{
	/**
	 * @throws IllegalStateException if {@link #getState()} != {@link RunningOperationState#Complete}
	 */
	public R getResult() throws IllegalStateException;
}
