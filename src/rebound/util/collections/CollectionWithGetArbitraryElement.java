package rebound.util.collections;

import rebound.annotations.semantic.SignalType;

@SignalType
public interface CollectionWithGetArbitraryElement<E>
{
	public E getArbitraryElement();
}