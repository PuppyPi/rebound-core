package rebound.annotations.semantic;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This indicates the return value of a method is always and exactly one of its arguments!
 * 
 * Eg, {@link StringBuilder#append(String)}
 * or {@link java.util.Objects#requireNonNull(Object)}
 * :>
 * 
 * {@link #value()} is the parameter name for which one! :>
 * 
 * If there is only one argument, the parameter name need not be given :3
 * If there is only one argument that is of exactly the same type as the return value, the parameter name need not be given :3
 * Otherwise you really should give it!!  (Even for super/sub class mismatches between the parameter type and return value type!)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Passthrough
{
	String value() default "";
}
