package rebound.math;

import javax.annotation.Nonnull;

public interface Radical<IntegerType>
{
	public @Nonnull IntegerType getDegree();
	
	
	/**
	 * Must be nonnegative if degree {@link MathUtilities#isEven(Object)}
	 */
	public @Nonnull IntegerType getRadicand();
}
