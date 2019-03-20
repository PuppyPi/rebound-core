/*
 * Created on Sep 12, 2011
 * 	by the great Eclipse(c)
 */
package rebound.util.growth;

import java.util.ArrayList;
import javax.annotation.Nonnegative;
import rebound.annotations.hints.IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.SignalType;
import rebound.annotations.semantic.temporal.monotonicity.MonotonicValueIntegerSequence;

//TODO More standard algorithms than just LogisticLinearGrower (eg, LinearGrower, ExponentialGrower, LinearExponentialGrower (oldsize*r + k), HardThresholdLogisticLinearGrower, etc. :> )
//TODO Decorators (eg, for iteration counting / oldsize-remembering, initial capacity-providing!, min/max capping/thresholds, etc.  :> )

/**
 * Generically encapsulating an algorithm for growing something in size, especially a reallocating buffer (eg, {@link ArrayList} ^^ )
 * :D
 * 
 * @author Puppy Pie ^_^
 */
public interface Grower
{
	/**
	 * • This MUST clamp integer overflow!!
	 */
	@Nonnegative
	@MonotonicValueIntegerSequence
	public int getNewSize(@Nonnegative int iteration, @Nonnegative int oldsize);
	
	
	
	public static int defaultGetMonotonicNewSizeHeuristicallyClampingOverflow(@Nonnegative int oldsize, int newsize)
	{
		if (oldsize < 0)
			throw new IllegalArgumentException();
		
		if (newsize < oldsize)
		{
			if (oldsize < Integer.MAX_VALUE / 10)
				throw new IllegalArgumentException("Refusing to grow the size by a huge jump from ("+oldsize+" to "+Integer.MAX_VALUE+")!!  It's more likely that the grower is simply not monotonic and shrunk than that it overflowed!");
			
			return Integer.MAX_VALUE;
		}
		
		return newsize;
	}
	
	
	
	
	
	
	
	
	
	@SignalType
	public static interface GrowerComputationallyReduced
	extends Grower
	{
		public int getNewSizeReduced(@Nonnegative int iteration);
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int getNewSize(int iteration, int oldsize)
		{
			return getNewSizeReduced(iteration);
		}
	}
	
	
	@SignalType
	public static interface GrowerComputationallyUnreducedPurelyRecursive
	extends Grower
	{
		public int getNewSizeRecursive(@Nonnegative int oldsize);
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int getNewSize(int iteration, int oldsize)
		{
			return getNewSizeRecursive(oldsize);
		}
	}
}
