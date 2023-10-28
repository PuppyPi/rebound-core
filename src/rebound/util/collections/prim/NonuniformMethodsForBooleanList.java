package rebound.util.collections.prim;

import javax.annotation.Nonnegative;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.hints.IntendedToBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.util.collections.prim.PrimitiveCollections.DefaultToArraysBooleanCollection;

//TODO Elementwise boolean operations between BooleanLists!!  AND, OR, NOT, XOR!  \:D/

@ImplementationTransparency  //This only exists to keep the bulk functions exactly uniform and correspondent :3     This might be removed soon XD''
public interface NonuniformMethodsForBooleanList
extends DefaultToArraysBooleanCollection, NonuniformMethodsForBooleanList32, NonuniformMethodsForBooleanList64, NonuniformMethodsForBooleanListMiscellaneous
{
	@Override
	public default long getBitfield(int offset, int length)
	{
		return NonuniformMethodsForBooleanList32.super.getBitfield(offset, length);
	}
	
	
	
	
	/**
	 * The default implementation is just for legacy implementations that only support 32-bit (really 31-bit) indexes.
	 */
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	public default @Nonnegative long size64()
	{
		return size();
	}
	
	
	/**
	 * The default implementation is just for legacy implementations that only support 32-bit (really 31-bit) indexes.
	 */
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	public default boolean getBoolean64(@Nonnegative long index)
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
	public default void setBoolean64(@Nonnegative long index, boolean value)
	{
		if (index < 0)
			throw new IndexOutOfBoundsException();
		if (index > Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException();
		setBoolean((int)index, value);
	}
}
