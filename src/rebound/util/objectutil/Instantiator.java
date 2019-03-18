/*
 * Created on Feb 26, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.objectutil;

import javax.annotation.Nonnull;
import rebound.exceptions.WrappedThrowableRuntimeException;

/**
 * Like a littly bitty Class.newInstance() thing, but more general.
 * (actually it "instantiates" and "initializes" ..of course xP xD )
 * @author RProgrammer
 */
@FunctionalInterface
public interface Instantiator<T>
{
	@Nonnull
	public T newInstance() throws WrappedThrowableRuntimeException;
}
