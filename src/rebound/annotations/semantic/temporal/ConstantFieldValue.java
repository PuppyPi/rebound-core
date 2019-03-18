/*
 * Created on Jan 27, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.temporal;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Applied to a field or variable.
 * The value of this field will never change following some kind initialization.
 * 
 * The {@link #value()} tells when it's constant (which should probably be a monotonic property; never becoming possibly-changing once it becomes constant)  :>
 * 
 * 
 * @see ConstantReturnValue
 * @see ConstantFieldValue
 * @see PossiblyChangingFieldValue
 * @see PossiblyChangingReturnValue
 * 
 * @author RProgrammer
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.PARAMETER})
public @interface ConstantFieldValue
{
	String value() default "";
}
