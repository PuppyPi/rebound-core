/*
 * Created on Apr 28, 2008
 * 	by the wonderful Eclipse(c)
 */
package rebound.exceptions;

public abstract class SyntaxCheckedException
extends Exception
{
	private static final long serialVersionUID = 1L;
	
	public SyntaxCheckedException()
	{
	}
	
	public SyntaxCheckedException(String message)
	{
		super(message);
	}
	
	public SyntaxCheckedException(Throwable cause)
	{
		super(cause);
	}
	
	public SyntaxCheckedException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	
	public abstract SyntaxException toSyntaxRuntimeException();
}
