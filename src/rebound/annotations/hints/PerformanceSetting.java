/*
 * Created on
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.hints;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Something that may significantly alter a system, but shouldn't actually affect its functionality, just its performance.  ^_^
 * 
 * @author Puppy Pie ^_^
 */
@Documented
@PerformanceHint
@Retention(RetentionPolicy.RUNTIME)
public @interface PerformanceSetting
{
}
