package rebound.text.parsing.apis.tokenstream;

import rebound.annotations.semantic.FunctionalityInterface;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;

/**
 * Note that the {@link #getOriginalSource()}es of a stream of these tokens MUST ALL BE *REFERENCE*-IDENTICAL!! 0,0
 */
@FunctionalityInterface
public interface SimpleTokenWithMemoryOfOriginalSource
extends WherefulToken
{
	public String getOriginalSource();
	
	
	
	
	//<<< tp SimpleTokenWithMemoryOfOriginalSource
	@TraitPredicate
	public default boolean isSimpleTokenWithMemoryOfOriginalSource()
	{
		return true;
	}
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof SimpleTokenWithMemoryOfOriginalSource && ((SimpleTokenWithMemoryOfOriginalSource)x).isSimpleTokenWithMemoryOfOriginalSource();
	}
	//>>>
}
