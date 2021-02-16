/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.temporal.concurrencyprimitives;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.concurrent.GuardedBy;

/**
 * Same as {@link GuardedBy}, but without the {@link Target} restrictions :P
 * (*cough* constructors and local variables *cough* XD'' )
 * 
 * (edit: oh, it's also {@link Documented}..why isn't {@link GuardedBy} {@link Documented}? ;-;? )
 * 
 * @see GuardedBy
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface GuardedBy2
{
	String value() default "";
	
	int[] depth() default {0};
}
