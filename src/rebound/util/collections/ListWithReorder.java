package rebound.util.collections;

import java.util.List;
import rebound.annotations.semantic.SignalType;

/*
 * Note! There exist things which expect this to NOT extend java.util.List!!!
 */
@SignalType
public interface ListWithReorder
{
	public int size();  //Required by default impl. of moveToEnd()!
	
	
	/**
	 * @see CollectionUtilities#reorderAbsolute(List, int, int)
	 */
	public void reorder(int source, int destInPreRemoveCoordinates) throws IndexOutOfBoundsException;
	
	
	/**
	 * @throws IndexOutOfBoundsException if <code>source</code> is out of bounds!
	 */
	public default void moveToEnd(int source) throws IndexOutOfBoundsException
	{
		reorder(source, size());
	}
	
	/**
	 * @throws IndexOutOfBoundsException if <code>source</code> is out of bounds!
	 */
	public default void moveToBeginning(int source) throws IndexOutOfBoundsException
	{
		reorder(source, 0);
	}
}