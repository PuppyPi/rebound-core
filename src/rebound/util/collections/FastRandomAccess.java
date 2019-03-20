package rebound.util.collections;

import java.util.RandomAccess;
import rebound.annotations.semantic.FunctionalityType;
import rebound.annotations.semantic.SignalType;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;

/**
 * Analogous to {@link RandomAccess}, but a {@link FunctionalityType} not a {@link SignalType}!  ^_~
 * @author Puppy Pie ^_^
 */
@FunctionalityType
public interface FastRandomAccess
{
	//<<< tp FastRandomAccess
	@TraitPredicate
	public default boolean isFastRandomAccess()
	{
		return true;
	}
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof FastRandomAccess && ((FastRandomAccess)x).isFastRandomAccess();
	}
	//>>>
}