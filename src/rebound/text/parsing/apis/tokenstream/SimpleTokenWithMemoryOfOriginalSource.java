package rebound.text.parsing.apis.tokenstream;

import rebound.annotations.semantic.FunctionalityType;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;
import rebound.util.collections.Slice;

/**
 * Note that the {@link #getOriginalSource()}es of a stream of these tokens MUST ALL BE *REFERENCE*-IDENTICAL!! 0,0
 */
@FunctionalityType
public interface SimpleTokenWithMemoryOfOriginalSource
extends WherefulToken
{
	public String getOriginalSource();
	
	@Override
	public default Slice<CharSequence> getMaskedSourceSlice()
	{
		return new Slice<>(getOriginalSource(), getStartingCharacterIndexInSource(), getLengthOfMaskedSource());
	}
	
	
	
	
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
