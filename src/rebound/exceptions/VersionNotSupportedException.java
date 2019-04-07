/*
 * Created on
 * 	by the great Eclipse(c)
 */
package rebound.exceptions;

public class VersionNotSupportedException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	
	public VersionNotSupportedException()
	{
		super();
	}
	
	public VersionNotSupportedException(String message)
	{
		super(message);
	}
	
	public VersionNotSupportedException(Throwable cause)
	{
		super(cause);
	}
	
	public VersionNotSupportedException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
