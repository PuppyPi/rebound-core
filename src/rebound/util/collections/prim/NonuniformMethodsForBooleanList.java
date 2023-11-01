package rebound.util.collections.prim;

import javax.annotation.Nonnegative;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.hints.IntendedToBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.simpledata.ActuallyUnsigned;
import rebound.util.collections.prim.PrimitiveCollections.BooleanList;
import rebound.util.collections.prim.PrimitiveCollections.DefaultToArraysBooleanCollection;

@ImplementationTransparency  //This only exists to keep the bulk functions exactly uniform and correspondent :3     This might be removed soon XD''
public interface NonuniformMethodsForBooleanList
extends DefaultToArraysBooleanCollection, NonuniformMethodsForBooleanList32, NonuniformMethodsForBooleanList64, NonuniformMethodsForBooleanListMiscellaneous
{
	@Override
	public default long getBitfield(@Nonnegative int offset, @Nonnegative int length)
	{
		return NonuniformMethodsForBooleanList32.super.getBitfield(offset, length);
	}
	
	
	
	
	/**
	 * The default implementation is just for legacy implementations that only support 32-bit (really 31-bit) indexes.
	 */
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallyUnsigned long size64()
	{
		return size();
	}
	
	
	/**
	 * The default implementation is just for legacy implementations that only support 32-bit (really 31-bit) indexes.
	 */
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	public default boolean getBooleanBy64(@ActuallyUnsigned long index)
	{
		if (index < 0)
			throw new IndexOutOfBoundsException();
		if (index > Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException();
		return getBoolean((int)index);
	}
	
	
	/**
	 * The default implementation is just for legacy implementations that only support 32-bit (really 31-bit) indexes.
	 */
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	public default void setBooleanBy64(@ActuallyUnsigned long index, boolean value)
	{
		if (index < 0)
			throw new IndexOutOfBoundsException();
		if (index > Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException();
		setBoolean((int)index, value);
	}
	
	
	/**
	 * The default implementation is just for legacy implementations that only support 32-bit (really 31-bit) indexes.
	 */
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	@Override
	public default BooleanList subListBy64i(@ActuallyUnsigned long fromIndex, @ActuallyUnsigned long toIndexInclusive)
	{
		if (fromIndex < 0)
			throw new IndexOutOfBoundsException();
		if (fromIndex > Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException();
		
		if (toIndexInclusive < 0)
			throw new IndexOutOfBoundsException();
		if (toIndexInclusive >= Integer.MAX_VALUE)  //note the or-equal-to here since this is inclusive-high-bound and we're calling an old exclusive-high-bound function!
			throw new IndexOutOfBoundsException();
		
		return ((BooleanList)this).subList((int)fromIndex, (int)(toIndexInclusive+1));
	}
}
