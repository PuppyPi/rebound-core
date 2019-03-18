/*
 * Created on
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.temporal;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Opposite of {@link ConstantReturnValue}, for to be explicits ^_^
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
public @interface PossiblyChangingReturnValue
{
}
