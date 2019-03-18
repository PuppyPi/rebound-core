package rebound.util.collections;

import rebound.annotations.semantic.FunctionalityInterface;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;
import rebound.exceptions.ImpossibleException;

@FunctionalityInterface
public interface TransparentContiguousArrayBackedCollection<A>
{
	/**
	 * returns the underlying array that backs the array list—and only the relevant slice of it!.
	 * Be careful, though, as updates can cause a new array to be creates, obsoleting this one.
	 */
	public Slice<A> getLiveContiguousArrayBackingUNSAFE();
	
	
	
	
	
	
	//<<< tp TransparentContiguousArrayBackedCollection
	@TraitPredicate
	public default boolean isTransparentContiguousArrayBackedCollection()
	{
		return true;
	}
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof TransparentContiguousArrayBackedCollection && ((TransparentContiguousArrayBackedCollection)x).isTransparentContiguousArrayBackedCollection();
	}
	//>>>
	
	
	
	
	
	
	public static void checkUnderlyingLengthAndExposedSizeMatch(int apparentCollectionSize, Slice underlyingSlice) throws ImpossibleException
	{
		int sliceLength = underlyingSlice.getLength();
		if (sliceLength != apparentCollectionSize)
			throw newUnderlyingLengthAndExposedSizeMismatchException(apparentCollectionSize, sliceLength);
	}
	
	public static ImpossibleException newUnderlyingLengthAndExposedSizeMismatchException(int apparentCollectionSize, int underlyingSliceLength)
	{
		return new ImpossibleException("Collection.size() ("+apparentCollectionSize+") and underlying Slice.getLength() ("+underlyingSliceLength+") are not equal!!");
	}
}
