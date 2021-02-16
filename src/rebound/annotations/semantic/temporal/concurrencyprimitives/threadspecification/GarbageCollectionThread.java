/*
 * Created on Jul 15, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.temporal.concurrencyprimitives.threadspecification;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Specifies the annotated thing is only to be accessed/invoked by the JVM garbage collector thread!
 * (eg, {@link Object#finalize()} :> )
 * 
 * @see AWhichThreadsAnnotation
 * @see WhichThreads
 * @see ThreadSafe
 * @author Puppy Pie ^_^
 */
@Documented
@AWhichThreadsAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface GarbageCollectionThread
{
	int[] depth() default {0};
}
