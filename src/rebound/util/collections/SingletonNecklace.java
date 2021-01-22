package rebound.util.collections;

import static java.util.Collections.*;
import java.util.List;

public class SingletonNecklace<E>
implements Necklace<E>
{
	protected final E e;
	
	public SingletonNecklace(E e)
	{
		this.e = e;
	}
	
	public E getSingleElement()
	{
		return e;
	}
	
	@Override
	public List<E> asListFromCanonicalStartingPoint() throws UnsupportedOperationException
	{
		return singletonList(e);
	}
}
