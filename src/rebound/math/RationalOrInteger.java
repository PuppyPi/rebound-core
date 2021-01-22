package rebound.math;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigInteger;
import rebound.annotations.semantic.simpledata.ActuallyUnsigned;

/**
 * Like {@link ActuallyUnsigned}, this offers some more information than the Java type system does ^^'
 * 
 * Specifically, values of this type will be typed as {@link Object} but only contain a {@link Rational} or a primitive boxed integer (usually {@link Long} :> ) or {@link BigInteger}
 * They're meant to be used with things like {@link MathUtilities#add(Object, Object)} and {@link MathUtilities#multiply(Object, Object)} :>
 * 
 * @see PolyInteger
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface RationalOrInteger
{
}
