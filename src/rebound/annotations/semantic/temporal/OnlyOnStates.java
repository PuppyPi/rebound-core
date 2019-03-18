/*
 * Created on
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.temporal;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies the annotated member is only to be accessed on the {@link #value() given} states! :>
 * 
 * @see AnEnumBasedOnlyOnStatesAnnotation
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface OnlyOnStates
{
	String[] value();
	int[] depth() default {0};
}
