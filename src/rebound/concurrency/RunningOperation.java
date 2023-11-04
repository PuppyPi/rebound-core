package rebound.concurrency;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import rebound.annotations.semantic.temporal.monotonicity.MonotonicValueGeneric;
import rebound.concurrency.blocks.Blocking;
import rebound.concurrency.blocks.Nonblocking;

/**
 * Analogous to a {@link Future} but with no value.
 * Note that it also doesn't distinguish success and error!  Those are meant to be done by subclasses.
 */
public interface RunningOperation
{
	/**
	 * Note that for {@link RunningOperationState#Unstarted} and {@link RunningOperationState#Running}, the actual value may change at any moment, including in between this function returning and the next line of code being executed!
	 * However, once this returns a Final State ({@link RunningOperationState#Complete}, {@link RunningOperationState#CancelledBeforeStarting}, or {@link RunningOperationState#CancelledWhileRunning}), it is {@link MonotonicValueGeneric guaranteed} to never return anything else forever!!
	 */
	@Nonblocking
	@MonotonicValueGeneric
	public @Nonnull RunningOperationState getState();
	
	
	@Nonblocking
	public @Nonnull RunningOperationHardCancellationResult cancelOnlyIfNotYetStarted();
	
	@Nonblocking
	public @Nonnull RunningOperationHardCancellationResult cancelEvenIfRunning();
	
	
	/**
	 * Waits for either completion (whether success or failure) or cancellation!
	 */
	@Blocking
	public void waitForFinish() throws InterruptedException;
	
	
	/**
	 * Waits for either completion (whether success or failure) or cancellation!
	 * @return true if completed, false if and only if we timed out (though before the next line of your code runs but after this times out, the operation may have completed!)
	 */
	@Blocking
	public boolean waitForFinishUpTo(@Nonnegative long timeout, TimeUnit unit) throws InterruptedException;
}
