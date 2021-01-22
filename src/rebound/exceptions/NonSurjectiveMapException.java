package rebound.exceptions;

public class NonSurjectiveMapException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public NonSurjectiveMapException()
	{
		super();
	}
	
	public NonSurjectiveMapException(String message)
	{
		super(message);
	}
	
	public NonSurjectiveMapException(Throwable cause)
	{
		super(cause);
	}
	
	public NonSurjectiveMapException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
