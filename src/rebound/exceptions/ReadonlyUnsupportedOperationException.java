/*
 * Created on Nov 3, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

/**
 * For when something is readonly, and so any modification operation is {@link UnsupportedOperationException unsupported} ^_^
 * (eg, esp. when statically write and readwrite operations haves to be implemented because of supertypes/superinterfaces etc. :> )
 * 
 * @author Puppy Pie ^_^
 */
public class ReadonlyUnsupportedOperationException
extends UnsupportedOperationException
{
	private static final long serialVersionUID = 1L;
	
	public ReadonlyUnsupportedOperationException()
	{
	}
	
	public ReadonlyUnsupportedOperationException(String message)
	{
		super(message);
	}
	
	public ReadonlyUnsupportedOperationException(Throwable cause)
	{
		super(cause);
	}
	
	public ReadonlyUnsupportedOperationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
