/*
 * Created on May 19, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is a convenience for specifying {@link FunctionalityType#traitPredicate()} :>
 * If this is present on at least one method, then the following must be true:
 * 		+ It must only be present one exactly one method! (not counting inheritance :P )
 * 		+ Its method's return type must be boolean, and must take no arguments
 * 		+ The declaring class or interface must be annotated with {@link FunctionalityType}
 * 			+ And it must have {@link FunctionalityType#traitPredicate()} and {@link FunctionalityType#equivalentBehavior()} set to the empty string ""  (which is the default :> )
 * 
 * + Also, it's optional, but if you're in Java 8, it's usually nice to make {@link TraitPredicate}s have a default implementation of 'return true'  ^_^
 * 
 * @see FunctionalityType
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TraitPredicate
{
}
