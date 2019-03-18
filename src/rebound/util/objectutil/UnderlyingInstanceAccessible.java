/*
 * Created on
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.objectutil;

import java.util.List;
import rebound.exceptions.ContainerMismatchException;

/**
 * An underlying/view type of setup; eg, {@link List#subList(int, int) List sublist views}, etc.c. :>
 * 
 * Differs from {@link OwningInstanceAccessible} in that views of this kind usually
 * aren't able to be passed back to owners and cause exceptions if used with the
 * wrong owner!  (eg, {@link ContainerMismatchException})  :>
 * 
 * @see OwningInstanceAccessible
 * @see ContainerMismatchException
 * @author RProgrammer
 */
public interface UnderlyingInstanceAccessible<UnderlyingType>
{
	public UnderlyingType getUnderlying();
	
	
	public static interface UnderlyingInstanceModifiable<UnderlyingType>
	{
		public void setUnderlying(UnderlyingType newValue);
	}
}
