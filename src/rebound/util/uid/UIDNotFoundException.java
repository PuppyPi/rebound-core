package rebound.util.uid;

//<<< stanexc UIDNotFoundException
public class UIDNotFoundException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public UIDNotFoundException()
	{
		super();
	}
	
	public UIDNotFoundException(String message)
	{
		super(message);
	}
	
	public UIDNotFoundException(Throwable cause)
	{
		super(cause);
	}
	
	public UIDNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
//>>>
