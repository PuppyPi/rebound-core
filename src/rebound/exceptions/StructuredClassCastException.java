/*
 * Created on May 18, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

/**
 * A nice {@link ClassCastException} which can tell you what the source/runtime and destination/target {@link Class}es were! ^w^
 * 
 * @author Puppy Pie ^_^
 */
public class StructuredClassCastException
extends ClassCastException
{
	private static final long serialVersionUID = 1L;
	
	protected final Class runtimeType, targetType;
	
	
	public StructuredClassCastException()
	{
		super();
		this.runtimeType = null;
		this.targetType = null;
	}
	
	
	public StructuredClassCastException(String message)
	{
		super(message);
		this.runtimeType = null;
		this.targetType = null;
	}
	
	
	/**
	 * Seriously, why isn't this in {@link ClassCastException}?? XD?
	 */
	public StructuredClassCastException(Class runtimeType)
	{
		super(runtimeType == null ? "null" : runtimeType.getName());
		this.runtimeType = runtimeType;
		this.targetType = null;
	}
	
	public StructuredClassCastException(Class runtimeType, Class targetType)
	{
		super(targetType == null ? (runtimeType == null ? "null" : runtimeType.getName()) : ((runtimeType == null ? "null" : runtimeType.getName()) + " to " + targetType.getName()));
		this.runtimeType = runtimeType;
		this.targetType = targetType;
	}
	
	
	/**
	 * Seriously, why isn't this in {@link ClassCastException}?? XD?
	 */
	public StructuredClassCastException(String message, Class runtimeType)
	{
		super(message);
		this.runtimeType = runtimeType;
		this.targetType = null;
	}
	
	
	public StructuredClassCastException(String message, Class runtimeType, Class targetType)
	{
		super(message);
		this.runtimeType = runtimeType;
		this.targetType = targetType;
	}
	
	
	
	/**
	 * @return the class of the actual object, or <code>null</code> if not known
	 */
	public Class getRuntimeType()
	{
		return this.runtimeType;
	}
	
	/**
	 * @return the class it was attempted to be cast to, or <code>null</code> if not known
	 */
	public Class getTargetType()
	{
		return this.targetType;
	}
}
