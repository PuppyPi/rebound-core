/*
 * Created on
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.temporal;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Like {@link OnlyOnStates}, but that it explicitly may be called on any state! :D
 * 
 * @see OnlyOnStates
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface OnAnyStates
{
	int[] depth() default {0};
}
