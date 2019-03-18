/*
 * Created on Oct 29, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

public class NoSuchMemberRuntimeException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public NoSuchMemberRuntimeException()
	{
	}
	
	public NoSuchMemberRuntimeException(String message)
	{
		super(message);
	}
	
	public NoSuchMemberRuntimeException(Throwable cause)
	{
		super(cause);
	}
	
	public NoSuchMemberRuntimeException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
