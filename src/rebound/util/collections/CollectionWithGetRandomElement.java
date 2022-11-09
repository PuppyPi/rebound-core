package rebound.util.collections;

import java.util.Collection;
import rebound.annotations.semantic.FunctionalityType;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionIntToInt;

@FunctionalityType
public interface CollectionWithGetRandomElement<E>
extends Collection<E>
{
	public E getRandomElement(UnaryFunctionIntToInt pullIntegerZeroToExclusiveHighBound);
	
	
	//<<< tp CollectionWithGetRandomElement
	@TraitPredicate
	public default boolean isCollectionWithGetRandomElement()
	{
		return true;
	}
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof CollectionWithGetRandomElement && ((CollectionWithGetRandomElement)x).isCollectionWithGetRandomElement();
	}
	//>>>
}
