package rebound.util.collections;

import static java.util.Collections.*;
import java.util.List;

public class SingletonBracelet<E>
implements Bracelet<E>
{
	protected final E e;
	
	public SingletonBracelet(E e)
	{
		this.e = e;
	}
	
	public E getSingleElement()
	{
		return e;
	}
	
	@Override
	public int size()
	{
		return 1;
	}
	
	@Override
	public List<E> asListFromCanonicalStartingPointAndReflection() throws UnsupportedOperationException
	{
		return singletonList(e);
	}
}
