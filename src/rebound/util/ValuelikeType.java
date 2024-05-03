package rebound.util;

import javax.annotation.Nullable;
import rebound.annotations.semantic.FunctionalityType;
import rebound.annotations.semantic.TraitPredicate;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableByteArrayList;
import rebound.util.objectutil.ObjectUtilities;

/**
 * For example String but not char[] or {@link StringBuilder}
 * {@link ImmutableByteArrayList} but not byte[]
 * 
 * Things whose hashCode()/equals() is immutable and value/contents-defined not reference-defined :)
 */
@FunctionalityType
public interface ValuelikeType
{
	/**
	 * @return null if unknown (namely if it depends on a field's value and {@link ObjectUtilities#isValuelikeType(Object)} returns null for it)
	 */
	@TraitPredicate
	public default @Nullable Boolean isValuelikeType()
	{
		return true;
	}
}
