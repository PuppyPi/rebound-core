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
 * Indicates explicitly that this thing MUST properly support {@link Object#hashCode()}, {@link Object#equals(Object)}, so as to eg, be used as a key in a {@link java.util.Map} or an element in a {@link java.util.Set}  ^_^
 * 
 * @see IdentityHashableType
 * @see HashableValue
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface HashableType
{
}
