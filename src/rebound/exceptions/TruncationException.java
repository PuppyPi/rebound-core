/*
 * Created on Jun 2, 2008
 * 	by the great Eclipse(c)
 */
package rebound.exceptions;

/**
 * An unacceptable data loss has-, or would have- occurred.
 * @author RProgrammer
 */
public class TruncationException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public TruncationException()
	{
		super();
	}
	
	public TruncationException(String message)
	{
		super(message);
	}
	
	public TruncationException(String message, Throwable cause)
	{
		super(message, cause);  //ArithmeticException doesn't allow chaining here, so that's why we don't use it X'3
	}
	
	public TruncationException(Throwable cause)
	{
		super(cause);  //ArithmeticException doesn't allow chaining here, so that's why we don't use it X'3
	}
}
