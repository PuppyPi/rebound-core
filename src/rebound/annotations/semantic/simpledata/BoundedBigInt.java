package rebound.annotations.semantic.simpledata;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.BigInteger;

/**
 * This can apply to the JRE's {@link BigInteger} or possibly other, equivalent types.
 * @see BoundedInt
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface BoundedBigInt
{
	/**
	 * Inclusive :3
	 */
	String min();
	
	/**
	 * Inclusive :3
	 */
	String max();
}
