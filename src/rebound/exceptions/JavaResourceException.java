/*
 * Created on Jan 15, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

public class JavaResourceException
extends ResourceException
{
	private static final long serialVersionUID = 1L;
	
	protected final Class resourceClass;
	protected final String resourceName;
	
	
	public JavaResourceException()
	{
		super("Resource not found.");
		this.resourceClass = null;
		this.resourceName = null;
	}
	
	public JavaResourceException(String resourceName)
	{
		super("Resource not found: "+resourceName);
		this.resourceClass = null;
		this.resourceName = resourceName;
	}
	
	public JavaResourceException(Class resourceClass, String resourceName)
	{
		super("Resource not found: ("+resourceClass+") "+resourceName);
		this.resourceClass = resourceClass;
		this.resourceName = resourceName;
	}
	
	public JavaResourceException(Throwable cause)
	{
		super("Resource not found.", cause);
		this.resourceClass = null;
		this.resourceName = null;
	}
	
	public JavaResourceException(String resourceName, Throwable cause)
	{
		super("Resource not found: "+resourceName, cause);
		this.resourceClass = null;
		this.resourceName = resourceName;
	}
	
	public JavaResourceException(Class resourceClass, String resourceName, Throwable cause)
	{
		super("Resource not found: ("+resourceClass+") "+resourceName, cause);
		this.resourceClass = resourceClass;
		this.resourceName = resourceName;
	}
	
	
	public Class getResourceClass()
	{
		return this.resourceClass;
	}
	
	public String getResourceName()
	{
		return this.resourceName;
	}
}
