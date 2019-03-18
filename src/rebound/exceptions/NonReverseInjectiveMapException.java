package rebound.exceptions;

public class NonReverseInjectiveMapException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public NonReverseInjectiveMapException()
	{
		super();
	}
	
	public NonReverseInjectiveMapException(String message)
	{
		super(message);
	}
	
	public NonReverseInjectiveMapException(Throwable cause)
	{
		super(cause);
	}
	
	public NonReverseInjectiveMapException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
