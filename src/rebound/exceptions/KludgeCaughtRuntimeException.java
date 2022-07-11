/*
 * Created
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

/**
 * For when you do something that's not mathematically correct (ie, it could technically fail in normal operation, though extremely unlikely)...and the extremely unlikely even occurs and it fails in normal operation X'D
 * @author RProgrammer
 */
public class KludgeCaughtRuntimeException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public KludgeCaughtRuntimeException()
	{
		super("WHOOPS, They caught our kludge! ^^'''");
	}
	
	public KludgeCaughtRuntimeException(Throwable cause)
	{
		super("WHOOPS, They caught our kludge! ^^'''", cause);
	}
	
	public KludgeCaughtRuntimeException(String message)
	{
		super(message);
	}
	
	public KludgeCaughtRuntimeException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
