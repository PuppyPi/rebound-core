/*
 * Created on
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.temporal.monotonicity;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates the [return] value of this thing always follows the specified progression without going backwards :>
 * (many usefuls for concurrency things! :D )
 * 
 * (empty array means specified-in-documentation :3 )
 * 
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface MonotonicValueGeneric
{
	String[] value() default {};
}
