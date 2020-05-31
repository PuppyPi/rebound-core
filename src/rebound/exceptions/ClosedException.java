package rebound.exceptions;

public class ClosedException
extends IllegalStateException
{
	private static final long serialVersionUID = 1L;
	
	public ClosedException()
	{
		super();
	}
	
	public ClosedException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public ClosedException(String message)
	{
		super(message);
	}
	
	public ClosedException(Throwable cause)
	{
		super(cause);
	}
}
