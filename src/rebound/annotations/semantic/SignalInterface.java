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
 * This means that the interface's implementation itself is significant!
 * Eg, {@link java.util.RandomAccess} :3
 * As opposed to merely providing a mechanism to call functions on a thing :3
 * 
 * + It may also have methods defined, but that doesn't affect its signal status :>'
 * 
 * 
 * + It's probably a good design idea to get away from these for the most part,
 * since it SEVERELY inhibits things like decorators, wrappers, collectors, broadcasters, etc.c.c. :P
 * (requiring EXPONENTIALLY 2^(THE NUMBER OF SIGNAL INTERFACES) SUBTYPES to account for all the possible combinations! O_O )
 * 		A much more flexible solution is a trait system which uses boolean methods to test for traits (and if the interface defining the trait method isn't supported, then it is synonymous with <code>false</code>! ^_^ )
 * 		Eg, {@link java.io.InputStream#markSupported()}  ^_^
 * 
 * @see FunctionalityInterface
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SignalInterface
{
}
