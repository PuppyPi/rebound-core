/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.temporal.concurrencyprimitives;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Same as {@link ThreadSafe}, but without the {@link Target} restrictions :P
 * (*cough* constructors and methods *cough* XD'' )
 * 
 * @see ThreadSafe
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ThreadSafe2
{
}
