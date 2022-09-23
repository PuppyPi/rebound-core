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
 * 
 * If this annotates a field, its value is expected to be exploded into the class with all its fields or elements becoming fields themselves in the containing class!
 * And all its methods (and constructor[s] invoked) inlined.
 * (Obviously this is only in very special cases; namely where no reference escape occurs and the runtime type is known at compile time, like a final field with a constructor invoked right there!)
 */
@Documented
@PerformanceHint
@Retention(RetentionPolicy.RUNTIME)
public @interface ExplosionAllocate
{
}
