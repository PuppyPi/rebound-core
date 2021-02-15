package rebound.util.collections;

import java.util.Iterator;
import rebound.annotations.hints.IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.FunctionalityType;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;
import rebound.exceptions.DefensivelyUnsupportedOperationException;
import rebound.util.collections.SimpleIterator.SimpleIterable;

@FunctionalityType
public interface InfiniteIterable<E>
extends SimpleIterable<E>
{
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser  //*if* it isInfiniteIterable()!  otherwise (like a decorator) that's fine!
	@Override
	public default Iterator<E> iterator()
	{
		throw new DefensivelyUnsupportedOperationException();
	}
	
	@Override
	public default SimpleIterator<E> simpleIterator()
	{
		throw new DefensivelyUnsupportedOperationException();
	}
	
	
	public default Iterator<E> infiniteIterator()
	{
		return infiniteSimpleIterator().toIterator();
	}
	
	public SimpleIterator<E> infiniteSimpleIterator();
	
	
	public static <E> SimpleIterator<E> infiniteSimpleIteratorOf(Iterable<E> e)
	{
		if (e instanceof InfiniteIterable)
			return ((InfiniteIterable)e).infiniteSimpleIterator();
		else
			return SimpleIterable.simpleIteratorOf(e);
	}
	
	
	
	
	
	
	
	//<<< tp InfiniteIterable
	@TraitPredicate
	public default boolean isInfiniteIterable()
	{
		return true;
	}
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof InfiniteIterable && ((InfiniteIterable)x).isInfiniteIterable();
	}
	//>>>
}
