/*
 * Created on Apr 22, 2008
 * 	by the wonderful Eclipse(c)
 */
package rebound.exceptions;

/**
 * Put this when you KNOW that the code can't be reached!  (Even if the Java Compiler doesn't XD )
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
 * 
 * 
 * So if you see this in a stacktrace.  You should freak out XD
 * (Unless someone just threw it to mess with you XD )
 * 
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
