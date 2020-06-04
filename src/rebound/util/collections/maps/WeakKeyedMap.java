package rebound.util.collections.maps;

import java.util.WeakHashMap;
import rebound.annotations.semantic.FunctionalityType;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;
import rebound.util.collections.WeakCollection;

/**
 * @see WeakCollection
 * @see WeakKeyedMap
 */
@FunctionalityType
public interface WeakKeyedMap
{
	@TraitPredicate
	public default boolean isWeakKeyedMap()
	{
		return true;
	}
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof WeakKeyedMap && ((WeakKeyedMap)x).isWeakKeyedMap() ||
		
		//Grandfatheringgg :33
		x instanceof WeakHashMap;
	}
}
