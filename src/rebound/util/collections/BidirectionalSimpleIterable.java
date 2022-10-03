package rebound.util.collections;

import rebound.util.collections.SimpleIterator.SimpleIterable;

public interface BidirectionalSimpleIterable<E>
extends SimpleIterable<E>
{
	@Override
	public BidirectionalSimpleIterator<E> simpleIterator();
}
