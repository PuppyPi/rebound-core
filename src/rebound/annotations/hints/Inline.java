package rebound.annotations.hints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Analogous to the C keyword "inline".  Meant as a plea (that will probably be ignored X'D) to inline the function.
 */
@PerformanceHint
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Documented
public @interface Inline
{
}
