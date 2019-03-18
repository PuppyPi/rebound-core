package rebound.util.growth;

import javax.annotation.concurrent.Immutable;
import rebound.util.growth.Grower.GrowerComputationallyUnreducedPurelyRecursive;

/**
 * Multiplies the previous size by a constant :>
 *  (And also adds a constant too!)
 * 
 * + The multiplicative constant is given as a fraction of two integers rather than a float, for determinstic accuracy :>
 */
@Immutable
public class ExponentialGrower
implements GrowerComputationallyUnreducedPurelyRecursive
{
	protected final int n, d;
	
	public ExponentialGrower()
	{
		this(11, 10);
	}
	
	public ExponentialGrower(int n, int d)
	{
		this.n = n;
		this.d = d;
	}
	
	
	
	/**
	 * @return oldsize * n / d + a   :>
	 */
	@Override
	public int getNewSizeRecursive(int oldsize)
	{
		return (oldsize * this.n) / this.d;
	}
	
	
	public int getN()
	{
		return this.n;
	}
	
	public int getD()
	{
		return this.d;
	}
}
