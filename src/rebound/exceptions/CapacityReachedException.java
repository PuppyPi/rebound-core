package rebound.exceptions;

public class CapacityReachedException
extends RuntimeException
{
	public CapacityReachedException()
	{
		super();
	}
	
	public CapacityReachedException(String message)
	{
		super(message);
	}
	
	public CapacityReachedException(Throwable cause)
	{
		super(cause);
	}
	
	public CapacityReachedException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
