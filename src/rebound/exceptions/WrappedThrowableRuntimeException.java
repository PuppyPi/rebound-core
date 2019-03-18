/*
 * Created on Sep 4, 2008
 * 	by the great Eclipse(c)
 */
package rebound.exceptions;

/**
 * for when a non-{@link RuntimeException} is thrown, but there is no <code>throws</code> declaration in the method.<br>
 * @author RProgrammer
 */
public class WrappedThrowableRuntimeException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public WrappedThrowableRuntimeException(Throwable cause)
	{
		super(cause);
	}
}
