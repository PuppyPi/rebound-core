package rebound.exceptions;

/**
 * Eg, thrown by a function that detects the exact combination of parameters you gave it would've caused an infinite loop/recursion/memory-usage!
 */
public class InfiniteComputationException
extends RuntimeException
{
	public InfiniteComputationException()
	{
		super();
	}
	
	public InfiniteComputationException(String message)
	{
		super(message);
	}
	
	public InfiniteComputationException(Throwable cause)
	{
		super(cause);
	}
	
	public InfiniteComputationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
