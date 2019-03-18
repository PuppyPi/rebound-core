/*
 * Created on Oct 30, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

public class NonSingletonException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public NonSingletonException()
	{
	}
	
	public NonSingletonException(String message)
	{
		super(message);
	}
	
	public NonSingletonException(Throwable cause)
	{
		super(cause);
	}
	
	public NonSingletonException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
