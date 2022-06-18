package rebound.util.collections;

import rebound.annotations.semantic.SignalType;

@SignalType
public interface CollectionWithPopArbitraryElement<E>
{
	public E popArbitraryElement();
}
