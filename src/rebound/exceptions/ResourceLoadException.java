/*
 * Created on Feb 27, 2012
 * 	by the great Eclipse(c)
 */
package rebound.exceptions;

public class ResourceLoadException
extends JavaResourceException
{
	private static final long serialVersionUID = 1L;
	
	public ResourceLoadException()
	{
	}
	
	public ResourceLoadException(String resourceName)
	{
		super(resourceName);
	}
	
	public ResourceLoadException(Class resourceClass, String resourceName)
	{
		super(resourceClass, resourceName);
	}
	
	public ResourceLoadException(Throwable cause)
	{
		super(cause);
	}
	
	public ResourceLoadException(String resourceName, Throwable cause)
	{
		super(resourceName, cause);
	}
	
	public ResourceLoadException(Class resourceClass, String resourceName, Throwable cause)
	{
		super(resourceClass, resourceName, cause);
	}
}
