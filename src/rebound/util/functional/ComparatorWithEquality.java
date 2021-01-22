package rebound.util.functional;

import java.util.Comparator;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;

public interface ComparatorWithEquality<T>
extends Comparator<T>
{
	public EqualityComparator<T> equalityComparator();
	
	
	
	//<<< tp ComparatorWithEquality
	@TraitPredicate
	public default boolean isComparatorWithEquality()
	{
		return true;
	}
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof ComparatorWithEquality && ((ComparatorWithEquality)x).isComparatorWithEquality();
	}
	//>>>
}
