package rebound.util.collections;

import static rebound.util.collections.CollectionUtilities.*;
import java.util.List;
import rebound.annotations.hints.IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser;

/**
 * Just some nice extra methods for anything that implements {@link List} :>
 */
public interface NiceList<E>
extends List<E>, ListWithRemoveRange
{
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser   //PrimitiveCollections (for one) relies on this delegating to subList(int, int)!!
	public default List<E> subListToEnd(int start)
	{
		return subList(start, size());
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser   //PrimitiveCollections (for one) relies on this delegating to subList(int, int)!!
	public default List<E> subListFromBeginning(int lengthWhichIsEndExclusiveInThisCaseXD)
	{
		return subList(0, lengthWhichIsEndExclusiveInThisCaseXD);
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser   //PrimitiveCollections (for one) relies on this delegating to subList(int, int)!!
	public default List<E> subListByLength(int start, int length)
	{
		return subList(start, start + length);
	}
	
	
	@Override
	public default void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
	{
		rangeCheckIntervalByLength(this.size(), start, pastEnd);
		
		for (int i = pastEnd - 1; i >= start; i--)  //most lists prefer us iterating backward ^^'
		{
			remove(i);
		}
	}
}
