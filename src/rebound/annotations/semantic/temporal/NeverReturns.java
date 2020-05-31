/*
 * Created on Feb 17, 2014   NOTE!!!  THIS LIESSS SOMETIMESSSSS   When I copy files....it apparently copies the timestamp too..    ._______________________.         ..ohwell; ..just be careful about that, okays? XD''
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.temporal;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import rebound.util.BasicExceptionUtilities;

/**
 * Eg, {@link System#exit(int)}, or {@link BasicExceptionUtilities#rethrowSafe(Throwable)} or etc. :>
 * 
 * Note that these methods may commonly return an exception—that's merely to make it easy on users, so they can 'throw' the return-value of this method,
 * since Java's flow path analysis of course does not account for this annotation XDD
 * 
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface NeverReturns
{
}
