package rebound.annotations.semantic.simpledata;

import rebound.math.SmallFloatMathUtilities;

/**
 * This value goes from 0 to the given bound like an angle goes from 0 to 360°  :>
 * (That is, it never can be the actual bound value, because it would wrap to zero)
 * 
 * @see SmallFloatMathUtilities#progmod(double, double)
 */
public @interface ModularBoundedDouble
{
	@Positive
	double value();
}
