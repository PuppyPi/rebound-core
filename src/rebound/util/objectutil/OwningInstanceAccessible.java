/*
 * Created on Oct 25, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.objectutil;

import java.util.Map.Entry;
import rebound.exceptions.ContainerMismatchException;
import rebound.exceptions.ContainerMismatchException.LiteralJavaEnclosingInstanceOwnerMismatchException;

/**
 * An owning/enclosing/view type of setup; eg, {@link Entry Map Entries}, etc. :>
 * 
 * This differs from {@link UnderlyingInstanceAccessible} in that *owning* patterns
 * usually impose a constraint / throw exceptions if an owned thing is passed to
 * an owning-type-thing which is not the actual owner of it!
 * 
 * Also see {@link ContainerMismatchException} ^_^
 * 
 * @see UnderlyingInstanceAccessible
 * @see ContainerMismatchException
 * @author RProgrammer
 */
public interface OwningInstanceAccessible<OwningType>
{
	public OwningType getOwner();
	
	
	/**
	 * Looking at JLS 7, §15.8.4, I'm pretty sure it's not accessible outside the inner class's code
	 * So here's an interface to make it accessible! :D
	 * 
	 * This is just an extending signal version to signal it's *literally* the Java enclosing instance, not just the same kind of idea/pattern :>
	 * ^^
	 * 
	 * Also see {@link LiteralJavaEnclosingInstanceOwnerMismatchException} ^_^
	 * 
	 * @author Puppy Pie ^_^
	 */
	public static interface LiteralJavaEnclosingOwningInstanceAccessible<EnclosingType>
	extends OwningInstanceAccessible<EnclosingType>
	{
	}
	
	
	/**
	 * Obviously doesn't apply to {@link LiteralJavaEnclosingOwningInstanceAccessible *actual*} Java inner classes / enclosing instances XD
	 * @author RProgrammer
	 */
	@SuppressWarnings("javadoc")
	public static interface LogicalOwningInstanceModifiable<OwningType>
	{
		public void setOwner(OwningType newValue);
	}
}
