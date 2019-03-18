/*
 * Created on May 19, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.objectutil;

import javax.annotation.Nonnull;

/**
 * Like {@link Object#getClass()}, but for instantiators (which account for the case of no no-args constructors :> )
 * @author RProgrammer
 */
public interface ProvidesInstantiator<Sub>
{
	/**
	 * Must be either a {@link Class}, indicating {@link Class#newInstance()} is to be used (public, no-args constructor :> ),
	 * or an {@link Instantiator}
	 * ^_^
	 */
	@Nonnull
	public Object getInstantiator();
	
	
	@Nonnull
	public Sub newInstance();
	
	/*
	@Nonnull
	public default Sub newInstance();
	{
		return ObjectUtilities.newInstance(this.getInstantiator());  //this is better than making getInstantiator() return a lambda to this otherwise it might recreate the instantiator every call! D: and worse, possibly never let the 'this' object become unreachable since that lambda would have a reference to this--can you say MEMORY LEAK!!!! \0/      Generally, instantiating a new object doesn't require an *instance* of an object to be done, so the operation shouldn't really be done on this, this is just for convenience :3"
	}
	 */
}
