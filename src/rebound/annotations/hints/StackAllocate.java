/*
 * Created on
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.hints;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This class or array is expected to be exploded into its fields/elements (if class or array) and for them to be stack-allocated instead of this class or array actually stored on the Java heap to be garbage collected.
 * If this annotates a method, its return value is expected to be stack allocated.
 */
@Documented
@PerformanceHint
@Retention(RetentionPolicy.RUNTIME)
public @interface StackAllocate
{
}
