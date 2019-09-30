package rebound.util.collections;

import java.util.List;

public interface ListWithFill<E>
extends List<E>
{
	public void fill(int start, int count, E value);
	
	
	public default void fill(E value)
	{
		fill(0, size(), value);
	}
}
