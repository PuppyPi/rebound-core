/*
 * Created on May 23, 2008
 * 	by the great Eclipse(c)
 */
package rebound.util.objectutil;

import javax.annotation.Nonnull;

/**
 * Cloning an identityful object *always* creates new object with equ*ivalent* contents, but a *unique*, different identity, never returning the same object even for identityful immutables!
 * (though cloning can of course, completely fail XD  eg, as in the case of {@link UnduplicateableSingleton} ;> )
 * 
 * Cloning an identityless (and by extention immutable!) object merely returns that object, since an actual copy in the implementation is (supposed!) to be indistinguishable from the original :>
 * 
 * 
 * As a rule of thumb, the clone should have the same jvm runtime type as the original; but it might be sufficient to simply implement all the same interfaces and have the same behaviors as the original
 * 
 * @author RProgrammer
 */
public interface PubliclyCloneable<T>
{
	@Nonnull
	public T clone();
}
