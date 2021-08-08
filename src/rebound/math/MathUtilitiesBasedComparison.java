package rebound.math;

import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;

public interface MathUtilitiesBasedComparison
{
	//<<< tp MathUtilitiesBasedComparison
	@TraitPredicate
	public default boolean isMathUtilitiesBasedComparison()
	{
		return true;
	}
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof MathUtilitiesBasedComparison && ((MathUtilitiesBasedComparison)x).isMathUtilitiesBasedComparison();
	}
	//>>>
}
