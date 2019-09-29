/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.allowedoperations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import rebound.annotations.semantic.reachability.ThrowAwayValue;

/**
 * @see VariableLengthValue (the opposite! ^_^ )
 * @see ThrowAwayValue
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface FixedLengthValue
{
	int[] depth() default {1};
}
