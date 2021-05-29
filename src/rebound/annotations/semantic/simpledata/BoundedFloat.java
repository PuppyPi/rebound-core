package rebound.annotations.semantic.simpledata;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Like {@link BoundedLong}, {@link LowerBoundedSmallInt}, {@link UpperBoundedSmallInt} is for integers :3
 * Since floats can encode infinity directly, this incorporates the ability to specify infinite bounds!
 * (And they *can* be inclusive! Meaning the actual value of infinity is allowed XD )
 * (But they can't be NaN!)
 * 
 * @see BoundedDouble
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface BoundedFloat
{
	@FiniteOrInfinity
	float min();
	
	@FiniteOrInfinity
	float max();
	
	
	boolean minInclusive();
	
	boolean maxInclusive();
}
