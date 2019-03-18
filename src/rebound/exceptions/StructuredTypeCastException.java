/*
 * Created on May 18, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

import java.lang.reflect.Type;

/**
 * A nice {@link ClassCastException} which can tell you what the source/runtime and destination/target {@link Class}es were! ^w^
 * 
 * @author Puppy Pie ^_^
 */
public class StructuredTypeCastException
extends ClassCastException
{
	private static final long serialVersionUID = 1L;
	
	protected final Type runtimeType, targetType;
	
	
	public StructuredTypeCastException()
	{
		super();
		this.runtimeType = null;
		this.targetType = null;
	}
	
	
	public StructuredTypeCastException(String message)
	{
		super(message);
		this.runtimeType = null;
		this.targetType = null;
	}
	
	
	/**
	 * Seriously, why isn't this in {@link ClassCastException}?? XD?
	 */
	public StructuredTypeCastException(Type runtimeType)
	{
		super(runtimeType == null ? "null" : runtimeType.toString());
		this.runtimeType = runtimeType;
		this.targetType = null;
	}
	
	public StructuredTypeCastException(Type runtimeType, Type targetType)
	{
		super(targetType == null ? (runtimeType == null ? "null" : runtimeType.toString()) : ((runtimeType == null ? "null" : runtimeType.toString()) + " to " + targetType.toString()));
		this.runtimeType = runtimeType;
		this.targetType = targetType;
	}
	
	
	/**
	 * Seriously, why isn't this in {@link ClassCastException}?? XD?
	 */
	public StructuredTypeCastException(String message, Type runtimeType)
	{
		super(message);
		this.runtimeType = runtimeType;
		this.targetType = null;
	}
	
	
	public StructuredTypeCastException(String message, Type runtimeType, Type targetType)
	{
		super(message);
		this.runtimeType = runtimeType;
		this.targetType = targetType;
	}
	
	
	
	/**
	 * @return the class of the actual object, or <code>null</code> if not known
	 */
	public Type getRuntimeType()
	{
		return this.runtimeType;
	}
	
	/**
	 * @return the class it was attempted to be cast to, or <code>null</code> if not known
	 */
	public Type getTargetType()
	{
		return this.targetType;
	}
}
