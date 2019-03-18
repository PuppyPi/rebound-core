/*
 * Created on Feb 19, 2005
 * 	by the wonderful Eclipse(c)
 */
package rebound.exceptions;

/**
 * An arithmetic overflow occurred (or would have occurred).<br>
 * Terminology note: two large negative integers added can "overflow in the negative direction", rather than "underflow".
 * @author RProgrammer
 */
public class OverflowException
extends TruncationException
{
	private static final long serialVersionUID = 1L;
	
	public OverflowException()
	{
		super();
	}
	
	public OverflowException(String message)
	{
		super(message);
	}
	
	public OverflowException(Throwable cause)
	{
		super(cause);
	}
	
	public OverflowException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
