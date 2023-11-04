package rebound.concurrency;

public enum RunningOperationHardCancellationResult
{
	/**
	 * Corresponds to {@link RunningOperationState#Unstarted} before, and {@link RunningOperationState#CancelledBeforeStarting} after!
	 */
	SucceededWhileUnstarted,
	
	/**
	 * Corresponds to {@link RunningOperationState#Running} before, and {@link RunningOperationState#CancelledWhileRunning} after!
	 */
	SucceededWhileRunning,
	
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
