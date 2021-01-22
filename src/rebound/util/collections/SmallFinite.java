package rebound.util.collections;

import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;

/**
 * Eg, union() of small finite sets can just be actually performed XD
 * Otherwise, a lazy {@link UnionSet} should be constructed!
 * 
 * It *can't* be {@link InfiniteIterable} or InfiniteMap and {@link SmallFinite}!!
 * 
 * (similar for Lists and Maps and etc. :3 )
 * (similar for subset, intersection, etc. :3 )
 */
public interface SmallFinite
{
	//<<< tp SmallFinite
	@TraitPredicate
	public default boolean isSmallFinite()
	{
		return true;
	}
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof SmallFinite && ((SmallFinite)x).isSmallFinite();
	}
	//>>>
}
