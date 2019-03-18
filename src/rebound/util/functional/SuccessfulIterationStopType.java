package rebound.util.functional;

/**
 * Don't use null in place of this!!  The results will probably be undefined/inconsistent!
 */
public enum SuccessfulIterationStopType
{
	CompletedNaturally,
	
	/**
	 * This is returned if and only if we stopped prematurely from {@link ContinueSignal#Stop}
	 */
	StoppedPrematurely,
}
