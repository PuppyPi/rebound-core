/*
 * Created on Apr 22, 2008
 * 	by the wonderful Eclipse(c)
 */
package rebound.exceptions;

/**
 * Put this when you KNOW that the code can't be reached!
 * 
 * eg,
 * <code>
 * public ParsedArgs parseArgs(String[] args)
 * {
 * 		...
 * 
 * 		if (error)
 * 		{
 * 			System.exit(21);
 * 			throw new UnreachableCodeException();
 * 		}
 * 		else
 * 		{
 * 			return rv;
 * 		}
 * }
 * </code>
 * @author RProgrammer
 */
public class UnreachableCodeException
extends ImpossibleException
{
	private static final long serialVersionUID = 1L;
	
	public UnreachableCodeException()
	{
		super();
	}
	
	public UnreachableCodeException(String message)
	{
		super(message);
	}
	
	public UnreachableCodeException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public UnreachableCodeException(Throwable cause)
	{
		super(cause);
	}
}
