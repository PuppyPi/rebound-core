package rebound.annotations.semantic.simpledata;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.BigInteger;

/**
 * This means it goes (conceptually) from negative infinity (exclusive XD) to an upper bound (inclusive) which is given as a small int (primitive not {@link BigInteger}) :3
 * 
 * @see BoundedInt
 * @see BoundedLong
 * @see BoundedBigInt
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface UpperBoundedSmallInt
{
	/**
	 * Inclusive :3
	 */
	long max();
}
