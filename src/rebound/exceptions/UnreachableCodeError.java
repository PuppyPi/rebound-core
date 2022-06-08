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
 * 			throw new UnreachableCodeError();
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
public class UnreachableCodeError
extends AssertionError
{
	private static final long serialVersionUID = 1l;
	
	public UnreachableCodeError()
	{
		super();
	}
	
	public UnreachableCodeError(String message)
	{
		super(message);
	}
	
	public UnreachableCodeError(Throwable cause)
	{
		super(cause);
	}
	
	public UnreachableCodeError(String message, Throwable cause)
	{
		super(message, cause);
	}
}
