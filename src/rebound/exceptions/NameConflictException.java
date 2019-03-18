/*
 * Created on Jan 9, 2011
 * 	by the great Eclipse(c)
 */
package rebound.exceptions;

/**
 * A generic exception for simple name (as in abstract identifying object) conflicts.
 * @author RProgrammer
 */
public class NameConflictException
extends Exception
{
	private static final long serialVersionUID = 1L;
	
	public NameConflictException()
	{
		super();
	}
	
	public NameConflictException(String message)
	{
		super(message);
	}
	
	public NameConflictException(Throwable cause)
	{
		super(cause);
	}
	
	public NameConflictException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
