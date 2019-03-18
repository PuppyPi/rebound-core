package rebound.util.collections;

import java.util.RandomAccess;
import rebound.annotations.semantic.FunctionalityInterface;
import rebound.annotations.semantic.SignalInterface;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;

/**
 * Analogous to {@link RandomAccess}, but a {@link FunctionalityInterface} not a {@link SignalInterface}!  ^_~
 * @author Puppy Pie ^_^
 */
@FunctionalityInterface
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