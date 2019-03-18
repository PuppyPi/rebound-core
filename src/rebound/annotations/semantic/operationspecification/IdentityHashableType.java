/*
 * Created on May 18, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.operationspecification;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates explicitly that this thing should be the default {@link Object} implementations of {@link Object#hashCode()}/{@link Object#equals(Object)} ^_^
 * Ie, equivalence should be identicalness
 * (useful often for mutable things or otherwise identityful things like opaque {@link Object} tokens and such things that should only be equivalent (eg, in maps and sets!) when they are identical (which like /said, is extra relevant for mutable things!) :> )
 * 
 * + Implies {@link HashableType} :>
 * 
 * @see HashableType
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface IdentityHashableType
{
}
