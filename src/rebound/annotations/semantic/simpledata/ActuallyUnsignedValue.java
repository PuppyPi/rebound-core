/*
 * Created on Apr 14, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.simpledata;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The integer value given (eg, int, long) is actually to be interpreted as an *unsigned* integer, even though it would otherwise be signed! :O
 * :>
 * 
 * + Note that it's probably bad/redundant practice to annotate 'char' things with this, since java actually *does* randomly support unsigned 16 bit integers (for old-unicode) X'D
 * 
 * (more supports for them was added in Java 8  ^w^! )
 * 
 * @author RProgrammer
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ActuallyUnsignedValue
{
}
