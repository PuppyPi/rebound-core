package rebound.exceptions;

public class NonForwardInjectiveMapException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public NonForwardInjectiveMapException()
	{
		super();
	}
	
	public NonForwardInjectiveMapException(String message)
	{
		super(message);
	}
	
	public NonForwardInjectiveMapException(Throwable cause)
	{
		super(cause);
	}
	
	public NonForwardInjectiveMapException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
