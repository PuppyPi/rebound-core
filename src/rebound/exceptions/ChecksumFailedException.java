package rebound.exceptions;

public class ChecksumFailedException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public ChecksumFailedException()
	{
		super();
	}
	
	public ChecksumFailedException(String message)
	{
		super(message);
	}
	
	public ChecksumFailedException(Throwable cause)
	{
		super(cause);
	}
	
	public ChecksumFailedException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
