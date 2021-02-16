/*
 * Created on Jan 27, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.temporal.concurrencyprimitives;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The flagged object is to be used as a lock with a <code>synchronized</code> code block.
 * @author RProgrammer
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface LockValue
{
	int[] depth() default {1};
}
