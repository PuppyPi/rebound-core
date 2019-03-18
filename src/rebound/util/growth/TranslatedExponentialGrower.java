package rebound.util.growth;

import javax.annotation.concurrent.Immutable;
import rebound.util.growth.Grower.GrowerComputationallyUnreducedPurelyRecursive;

/**
 * Multiplies the previous size by a constant :>
 * (And also adds a constant too!)
 * 
 * + The multiplicative constant is given as a fraction of two integers rather than a float, for determinstic accuracy :>
 * 
 * + Set N and D to 1 to make this a {@link LinearGrower}, set A to 0 to make this an {@link ExponentialGrower} :3
 */
@Immutable
public class TranslatedExponentialGrower
implements GrowerComputationallyUnreducedPurelyRecursive
{
	protected final int n, d, a;
	
	public TranslatedExponentialGrower()
	{
		this(11, 10, 16);
	}
	
	public TranslatedExponentialGrower(int n, int d, int a)
	{
		this.n = n;
		this.d = d;
		this.a = a;
	}
	
	
	
	/**
	 * @return oldsize * n / d + a   :>
	 */
	@Override
	public int getNewSizeRecursive(int oldsize)
	{
		return (oldsize * this.n) / this.d + this.a;
	}
	
	
	public int getN()
	{
		return this.n;
	}
	
	public int getD()
	{
		return this.d;
	}
	
	public int getA()
	{
		return this.a;
	}
}
