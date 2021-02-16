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
 * Ie, just like {@link ThreadSafe}, but can apply to methods/constructors individually :D
 * 
 * Specifically, it means 'this can be accessed from any thread'  ^_^
 * (note that more restrictions about ordering of operations and etc. may apply though!)
 * 
 * 
 * + A depth of 0 means the actual field/method
 * + A depth of 1 means accessing members of the object *in* the field or return value of the method/constructor!
 * + Etc. etc.  ^w^
 * 
 * 
 * @see AWhichThreadsAnnotation
 * @see WhichThreads
 * @see ThreadSafe
 * @author Puppy Pie ^_^
 */
@Documented
@AWhichThreadsAnnotation(threadsSpecifierAnnotationParameterName="")
@Retention(RetentionPolicy.RUNTIME)
public @interface AnyThreads
{
	int[] depth() default {0};
}
