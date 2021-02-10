package rebound.annotations.semantic.simpledata;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.Nonnegative;
import javax.annotation.Signed;

/**
 * The int value tagged with this can only be in the given interval!
 * Doubly-Inclusive form is used since empty intervals aren't useful, and this way we don't have to worry about integer overflow when making {@link #max()} be {@link Integer#MAX_VALUE} to explicitly indicate that exact bound is actually intentional :>
 * 	(or [0, -1] for {@link ActuallyUnsigned} ints)
 * 
 * The usefulness of this is that in other contexts (eg, persistence), an integer type smaller than the one used here could be used instead!  :D
 *  (and also for validation and documentation :3 )
 * 
 * Note that this is unsigned if an {@link ActuallyUnsigned} is a sister annotation!
 * 
 * These BoundedXyz annotations are a more general form of
 * 		{@link ActuallyBits} = [0, 2^n-1] for unsigned, [-2^(n-1), 2^(n-1)-1] for signed
 * 		{@link Nonnegative} = [0, MaxValue] only for signed
 * 		{@link Signed} = [MinValue, MaxValue] only for signed
 * 		{@link Negative} = [MinValue, -1] only for signed
 * 		{@link Positive} = [1, MaxValue] for signed or unsigned
 * 		(But not {@link Nonzero}, since that's a union of multiple disjoint intervals, not a single interval.)
 * Though if a BoundedXyz and one of these are both included, this BoundedXyz one should be used in preference and it should be considered an error if it's not a subset of the other specific one.
 * 
 * 
 * @see BoundedLong
 * @see BoundedBigInt
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface BoundedInt
{
	/**
	 * Inclusive :3
	 */
	int min();
	
	/**
	 * Inclusive :3
	 */
	int max();
}
