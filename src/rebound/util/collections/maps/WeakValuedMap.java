package rebound.util.collections.maps;

import rebound.annotations.semantic.FunctionalityType;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;
import rebound.util.collections.WeakCollection;

/**
 * @see WeakCollection
 * @see WeakValuedMap
 */
@FunctionalityType
public interface WeakValuedMap
{
	//<<< tp WeakValuedMap
	@TraitPredicate
	public default boolean isWeakValuedMap()
	{
		return true;
	}
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof WeakValuedMap && ((WeakValuedMap)x).isWeakValuedMap();
	}
	//>>>
}
