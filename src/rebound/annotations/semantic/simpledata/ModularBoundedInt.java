package rebound.annotations.semantic.simpledata;

import rebound.math.SmallIntegerMathUtilities;

/**
 * This value goes from 0 to the given bound like a quadrant angle goes from 0 to 4  :>
 * (That is, it never can be the actual bound value, because it would wrap to zero)
 * 
 * @see SmallIntegerMathUtilities#progmod(int, int)
 */
public @interface ModularBoundedInt
{
	@Positive
	int value();
}
