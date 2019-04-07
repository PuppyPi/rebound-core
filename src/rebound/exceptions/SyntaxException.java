/*
 * Created on Apr 28, 2008
 * 	by the wonderful Eclipse(c)
 */
package rebound.exceptions;

public abstract class SyntaxException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public SyntaxException()
	{
	}
	
	public SyntaxException(String message)
	{
		super(message);
	}
	
	public SyntaxException(Throwable cause)
	{
		super(cause);
	}
	
	public SyntaxException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
