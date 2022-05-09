package rebound.annotations.semantic.simpledata;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.BigInteger;

/**
 * This means it goes (conceptually) from a lower bound (inclusive) to positive infinity (exclusive XD) which is given as a {@link BigInteger} in Base 10 form :3
 * 
 * @see BoundedInt
 * @see BoundedLong
 * @see BoundedBigInt
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface LowerBoundedBigInt
{
	/**
	 * Inclusive :3
	 */
	String value();
}
