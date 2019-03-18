package rebound.util.uid;

//<<< stanexc UIDConflictException
public class UIDConflictException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public UIDConflictException()
	{
		super();
	}
	
	public UIDConflictException(String message)
	{
		super(message);
	}
	
	public UIDConflictException(Throwable cause)
	{
		super(cause);
	}
	
	public UIDConflictException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
//>>>