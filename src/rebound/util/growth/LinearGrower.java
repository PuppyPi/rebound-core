package rebound.util.growth;

import javax.annotation.concurrent.Immutable;
import rebound.util.growth.Grower.GrowerComputationallyUnreducedPurelyRecursive;

/**
 * Adds to the previous size by a constant :>
 * 		(a constant..get it? :^} )
 */
@Immutable
public class LinearGrower
implements GrowerComputationallyUnreducedPurelyRecursive
{
	protected final int a;
	
	public LinearGrower()
	{
		this(16);
	}
	
	public LinearGrower(int a)
	{
		this.a = a;
	}
	
	
	
	@Override
	public int getNewSizeRecursive(int oldsize)
	{
		return oldsize + this.a;
	}
	
	public int getA()
	{
		return this.a;
	}
}
