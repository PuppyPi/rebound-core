/*
 * Created on
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

public class AlreadyExistsException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public AlreadyExistsException()
	{
		super();
	}
	
	public AlreadyExistsException(String message)
	{
		super(message);
	}
	
	public AlreadyExistsException(Throwable cause)
	{
		super(cause);
	}
	
	public AlreadyExistsException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
