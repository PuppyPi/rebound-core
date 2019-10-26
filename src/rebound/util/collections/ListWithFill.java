package rebound.util.collections;

import java.util.List;

public interface ListWithFill<E>
extends List<E>
{
	public void fillBySetting(int start, int count, E value);
	
	
	public default void fillBySetting(E value)
	{
		fillBySetting(0, size(), value);
	}
}
