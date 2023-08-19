package rebound.math;

import javax.annotation.Nonnull;

public interface Radical<IntegerType, RadicandType>
{
	public @Nonnull IntegerType getDegree();
	
	
	/**
	 * Must be nonnegative if degree {@link MathUtilities#isEven(Object)}
	 */
	public @Nonnull RadicandType getRadicand();
}
