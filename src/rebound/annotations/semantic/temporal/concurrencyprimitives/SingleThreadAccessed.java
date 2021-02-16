/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.temporal.concurrencyprimitives;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import rebound.annotations.semantic.temporal.concurrencyprimitives.threadspecification.WhichThreads;

/**
 * Only one thread should be accessing this object!
 * Similar constraint to that needed for stack-allocating things in C[++]  ^_^
 * 
 * @see WhichThreads
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface SingleThreadAccessed
{
}
