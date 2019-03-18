/*
 * Created on Mar 30, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

/**
 * For those cases where the spec specifies a possible exception..but...yeah. xD
 * @author RProgrammer
 */
public class ImPrettySureThisNeverActuallyHappensRuntimeException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public ImPrettySureThisNeverActuallyHappensRuntimeException()
	{
		super("If you see this, then obviously it does happen! xD");
	}
	
	public ImPrettySureThisNeverActuallyHappensRuntimeException(Throwable cause)
	{
		super("If you see this, then obviously it does happen! xD", cause);
	}
	
	public ImPrettySureThisNeverActuallyHappensRuntimeException(String message)
	{
		super(message);
	}
	
	public ImPrettySureThisNeverActuallyHappensRuntimeException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
