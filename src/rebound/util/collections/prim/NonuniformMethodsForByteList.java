package rebound.util.collections.prim;

import javax.annotation.Nonnegative;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.hints.IntendedToBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.collections.prim.PrimitiveCollections.DefaultToArraysByteCollection;

@ImplementationTransparency  //This class only exists to keep the bulk functions exactly uniform and correspondent :3     This might be removed soon XD''
public interface NonuniformMethodsForByteList
extends DefaultToArraysByteCollection, NonuniformMethodsForByteList32, NonuniformMethodsForByteList64, NonuniformMethodsForByteListMiscellaneous
{
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
	public default byte getByteBy64(@Nonnegative long index)
	{
		if (index < 0)
			throw new IndexOutOfBoundsException();
		if (index > Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException();
		return getByte((int)index);
	}
	
	
	/**
	 * The default implementation is just for legacy implementations that only support 32-bit (really 31-bit) indexes.
	 */
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	public default void setByteBy64(@Nonnegative long index, byte value)
	{
		if (index < 0)
			throw new IndexOutOfBoundsException();
		if (index > Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException();
		setByte((int)index, value);
	}
	
	
	/**
	 * The default implementation is just for legacy implementations that only support 32-bit (really 31-bit) indexes.
	 */
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	@Override
	public default ByteList subListBy64i(long fromIndex, long toIndexInclusive)
	{
		if (fromIndex < 0)
			throw new IndexOutOfBoundsException();
		if (fromIndex > Integer.MAX_VALUE)
			throw new IndexOutOfBoundsException();
		
		if (toIndexInclusive < 0)
			throw new IndexOutOfBoundsException();
		if (toIndexInclusive >= Integer.MAX_VALUE)  //note the or-equal-to here since this is inclusive-high-bound and we're calling an old exclusive-high-bound function!
			throw new IndexOutOfBoundsException();
		
		return ((ByteList)this).subList((int)fromIndex, (int)(toIndexInclusive+1));
	}
}
