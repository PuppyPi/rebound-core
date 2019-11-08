package rebound.util.collections;

import java.util.Collection;
import rebound.annotations.semantic.SignalType;

@SignalType
public interface CollectionWithDefaultElement<E>
{
	public E getDefaultElement();
	
	
	
	
	public static <E> E getDefaultElement(Collection<E> l)
	{
		return l instanceof CollectionWithDefaultElement ? ((CollectionWithDefaultElement<E>)l).getDefaultElement() : null;
	}
}
