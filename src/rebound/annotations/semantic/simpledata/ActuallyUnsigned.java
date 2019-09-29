/*
 * Created on Apr 14, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.simpledata;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import rebound.bits.Unsigned;

/**
 * The integer value given (eg, int, long) is actually to be interpreted as an *unsigned* integer, even though it would otherwise be signed! :O
 * :>
 * 
 * + Note that it's probably bad/redundant practice to annotate 'char' things with this, since java actually *does* arbitrarily support unsigned 16 bit integers (for old-unicode)  X'D
 * 
 * + Note: more support for them was added to the JRE in Java 8  ^w^!
 * 
 * 
 * Note: don't rename this Unsigned or it will conflict with {@link Unsigned} X'D
 * 
 * @see ActuallySigned
 * @author RProgrammer
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.PARAMETER})
public @interface ActuallyUnsigned
{
	/**
	 * The bitlength! :D
	 * If 0, it means 'the bitlength of the java primitive it's attached to'  :3
	 */
	int value() default 0;
}
