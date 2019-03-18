/*
 * Created on Oct 29, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

public class ClassNotFoundRuntimeException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public ClassNotFoundRuntimeException()
	{
	}
	
	public ClassNotFoundRuntimeException(String message)
	{
		super(message);
	}
	
	public ClassNotFoundRuntimeException(Throwable cause)
	{
		super(cause);
	}
	
	public ClassNotFoundRuntimeException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
