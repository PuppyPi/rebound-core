/*
 * Created on Jan 15, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

/**
 * The difference between a resource and another data file is that resources
 * are counted as part of the program, and thus the possibility of
 * them being absent is handled much more like an internal program error that is
 * supposed to not happen.
 * @author RProgrammer
 */
public class ResourceException
extends ImpossibleException
{
	private static final long serialVersionUID = 1L;
	
	public ResourceException()
	{
	}
	
	public ResourceException(String message)
	{
		super(message);
	}
	
	public ResourceException(Throwable cause)
	{
		super(cause);
	}
	
	public ResourceException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
