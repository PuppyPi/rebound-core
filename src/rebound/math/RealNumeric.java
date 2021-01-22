package rebound.math;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;

/**
 * Basically the same as {@link RationalOrInteger}, but includes floats and {@link BigDecimal} and etc.
 * Generally the value will be a {@link Number} but since that's not an interface (heh) let's not restrict ourselves to that ^^'
 * 
 * Note that it does *not* include complex numbers though!!
 * 
 * So the order goes from least restrictive to most: {@link RealNumeric} → {@link RationalOrInteger} → {@link PolyInteger}
 *   :>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface RealNumeric
{
}
