package rebound.util.collections;

import rebound.annotations.semantic.SignalInterface;

@SignalInterface
public interface ListWithRemoveRange
{
	public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException;
	
	
	public default void removeRangeByLength(int start, int length) throws IndexOutOfBoundsException
	{
		removeRange(start, start+length);
	}
}
