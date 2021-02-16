/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.temporal.concurrencyprimitives.threadspecification;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Specifies which [types of] threads are supposed to invoke the given code :>
 * 
 * + Note that {@link ThreadSafe} and {@link AnyThreads} imply it can be used from *any* thread! :D
 * 
 * + If any of {@link AWhichThreadsAnnotation these annotations} are applied to classes as a whole, it means they simply apply to all methods and constructors within them ^_^
 * 
 * 
 * @see AWhichThreadsAnnotation
 * @see AnEnumBasedWhichThreadsAnnotation
 * @see AnyThreads
 * @see ThreadSafe
 * @author Puppy Pie ^_^
 */
@Documented
@AWhichThreadsAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface WhichThreads
{
	String[] value();
	int[] depth() default {0};
}
