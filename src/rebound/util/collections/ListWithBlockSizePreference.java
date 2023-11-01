package rebound.util.collections;

import javax.annotation.Nonnegative;
import rebound.annotations.semantic.FunctionalityType;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;

/**
 * Eg, the number of bytes in a {@link ByteList} that is highly performant to operate on aligned chunks of!!
 */
@FunctionalityType
public interface ListWithBlockSizePreference
{
	/**
	 * @return the preferred block size, or 0 if there is none!
	 */
	public @Nonnegative long getBlockSize();
	
	
	
	//<<< tp ListWithBlockSizePreference
	@TraitPredicate
	public default boolean isListWithBlockSizePreference()
	{
		return true;
	}
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof ListWithBlockSizePreference && ((ListWithBlockSizePreference)x).isListWithBlockSizePreference();
	}
	//>>>
}
