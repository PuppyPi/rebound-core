package rebound.exceptions;

/**
 * Eg, thrown by a function that detects the exact combination of parameters you gave it would've caused an infinite loop/recursion/memory-usage!
 */
public class WouldCauseInfiniteComputationException
extends RuntimeException
{
	public WouldCauseInfiniteComputationException()
	{
		super();
	}
	
	public WouldCauseInfiniteComputationException(String message)
	{
		super(message);
	}
	
	public WouldCauseInfiniteComputationException(Throwable cause)
	{
		super(cause);
	}
	
	public WouldCauseInfiniteComputationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
