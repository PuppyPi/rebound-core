/*
 * Created on
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

/**
 * Like {@link UnsupportedOperationException}, but when it's more of a value / setting than an operation
 * ^^
 * 
 * @author Puppy Pie ^_^
 */
public class UnsupportedOptionException
extends UnsupportedOperationException
{
	private static final long serialVersionUID = 1L;
	
	public UnsupportedOptionException()
	{
	}
	
	public UnsupportedOptionException(String message)
	{
		super(message);
	}
	
	public UnsupportedOptionException(Throwable cause)
	{
		super(cause);
	}
	
	public UnsupportedOptionException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
