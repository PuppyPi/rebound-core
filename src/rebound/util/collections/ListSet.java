package rebound.util.collections;

import java.util.Iterator;
import java.util.Set;

public interface ListSet<E>
extends Set<E>
{
	/**
	 * Always runs in a consistent, specific order :3
	 */
	@Override
	public Iterator<E> iterator();
}
