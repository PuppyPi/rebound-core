/*
 * Created on Nov 2, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

import rebound.util.UniqueIdProviders.UniqueIdProvider;

/**
 * NOTE: NOT (NECESSARILY) REGARDING ACTUAL STDLIBC FREE XD!
 * So don't freak out and blow everything up if this is in a stacktrace! XD
 * 
 * Just for the same general idea of freeing; eg, {@link UniqueIdProvider#release(Object)} :>
 * 
 * 
 * @author Puppy Pie ^_^
 */
public class DoubleFreeException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public DoubleFreeException()
	{
	}
	
	public DoubleFreeException(String message)
	{
		super(message);
	}
	
	public DoubleFreeException(Throwable cause)
	{
		super(cause);
	}
	
	public DoubleFreeException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
