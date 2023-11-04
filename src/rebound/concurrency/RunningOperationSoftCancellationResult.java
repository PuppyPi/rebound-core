package rebound.concurrency;

public enum RunningOperationSoftCancellationResult
{
	/**
	 * Corresponds to {@link RunningOperationState#Unstarted} before, and {@link RunningOperationState#CancelledBeforeStarting} after!
	 */
	SucceededWhileUnstarted,
	
	/**
	 * Corresponds to {@link RunningOperationState#Running} before, and undefined after, since it may change in the time between returning from the cancel() function and running the next line of code!  However, it's guaranteed not to be {@link RunningOperationState#Unstarted}!
	 */
	NoopedBecauseRunning,
	
	/**
	 * Corresponds to {@link RunningOperationState#CancelledBeforeStarting}
	 */
	AlreadyCancelledBeforeStarting,
	
	/**
	 * Corresponds to {@link RunningOperationState#CancelledWhileRunning}
	 */
	AlreadyCancelledWhileRunning,
	
	/**
	 * Corresponds to {@link RunningOperationState#Complete}
	 */
	AlreadyComplete,
}
