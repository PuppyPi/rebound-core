/*
 * Created on
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.temporal.monotonicity;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * (whether start < end or end > start determines whether it's monotonically increasing or monotonically decreasing, respectivelies! ^w^ )
 * 
 * @see MonotonicValueGeneric
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface MonotonicValueIntegerRange
{
	long startInclusive() default 0;
	long endInclusive() default Long.MAX_VALUE;
}
