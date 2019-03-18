/*
 * Created on Feb 27, 2012
 * 	by the great Eclipse(c)
 */
package rebound.exceptions;

public class ResourceNotFoundException
extends JavaResourceException
{
	private static final long serialVersionUID = 1L;
	
	public ResourceNotFoundException()
	{
	}
	
	public ResourceNotFoundException(String resourceName)
	{
		super(resourceName);
	}
	
	public ResourceNotFoundException(Class resourceClass, String resourceName)
	{
		super(resourceClass, resourceName);
	}
	
	public ResourceNotFoundException(Throwable cause)
	{
		super(cause);
	}
	
	public ResourceNotFoundException(String resourceName, Throwable cause)
	{
		super(resourceName, cause);
	}
	
	public ResourceNotFoundException(Class resourceClass, String resourceName, Throwable cause)
	{
		super(resourceClass, resourceName, cause);
	}
}
