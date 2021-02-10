package rebound.annotations.semantic.simpledata;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Like {@link BoundedInt} but for longs :3
 * 
 * @see BoundedInt
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface BoundedLong
{
	/**
	 * Inclusive :3
	 */
	long min();
	
	/**
	 * Inclusive :3
	 */
	long max();
}
