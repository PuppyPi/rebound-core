/*
 * Created on Nov 3, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

import rebound.util.collections.PolymorphicCollectionUtilities;
import rebound.util.collections.TransparentContiguousArrayBackedCollection;

/**
 * Like {@link ReadonlyUnsupportedOperationException}, but when we don't know if the thing is readonly; eg, {@link PolymorphicCollectionUtilities#isWritableCollection(Object)} == null
 * This is useful in eg, a context where attempts to write it may actually succeed and corrupt data instead of ultimately throwing an exception (eg, accessing something's underlying array through {@link TransparentContiguousArrayBackedCollection} to *write* to the array!!)
 */
public class ReadonlyPossiblyUnsupportedOperationException
extends UnsupportedOperationException  //don't change this!!
{
	private static final long serialVersionUID = 1L;
	
	public ReadonlyPossiblyUnsupportedOperationException()
	{
	}
	
	public ReadonlyPossiblyUnsupportedOperationException(String message)
	{
		super(message);
	}
	
	public ReadonlyPossiblyUnsupportedOperationException(Throwable cause)
	{
		super(cause);
	}
	
	public ReadonlyPossiblyUnsupportedOperationException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
