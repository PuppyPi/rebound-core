/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.temporal.concurrencyprimitives;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.concurrent.GuardedBy;

/**
 * Opposite of {@link GuardedBy}/{@link GuardedBy2} ^_^
 * 
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Unguarded
{
	int[] depth() default {0};
}
