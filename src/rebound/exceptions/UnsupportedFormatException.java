/*
 * Created on
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

/**
 * Like {@link UnsupportedOperationException}, but when it's more of a [file/data/structure/object] format than an operation
 * ^^
 * 
 * @author Puppy Pie ^_^
 */
public class UnsupportedFormatException
extends UnsupportedOperationException
{
	private static final long serialVersionUID = 1L;
	
	public UnsupportedFormatException()
	{
	}
	
	public UnsupportedFormatException(String message)
	{
		super(message);
	}
	
	public UnsupportedFormatException(Throwable cause)
	{
		super(cause);
	}
	
	public UnsupportedFormatException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
