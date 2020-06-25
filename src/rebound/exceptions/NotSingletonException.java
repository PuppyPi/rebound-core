/*
 * Created on Oct 30, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

public class NotSingletonException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public NotSingletonException()
	{
	}
	
	public NotSingletonException(String message)
	{
		super(message);
	}
	
	public NotSingletonException(Throwable cause)
	{
		super(cause);
	}
	
	public NotSingletonException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
