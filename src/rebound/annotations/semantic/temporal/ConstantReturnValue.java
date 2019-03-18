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
 * Applied to a 'getter' method..or something similar.
 * The return value of this method will never change (following <init> initialization, of course).
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
@Target({ElementType.METHOD})
public @interface ConstantReturnValue
{
	String value() default "";
}
