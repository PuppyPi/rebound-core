/*
 * Created on May 15, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections;

import java.util.AbstractSet;
import java.util.Iterator;
import rebound.concurrency.immutability.StaticallyConcurrentlyImmutable;

/**
 * + A mutable singleton set would support change, but only through some kind of replace() operation that JCF doesn't include ^^'
 * 
 * @author Puppy Pie ^_^
 */
public class ImmutableSingletonSet<E>
extends AbstractSet<E>
implements FixedSizeCollection, CollectionWithGetArbitraryElement<E>, StaticallyConcurrentlyImmutable, RuntimeReadabilityCollection, RuntimeWriteabilityCollection
{
	protected final E element;
	
	public ImmutableSingletonSet(E element)
	{
		this.element = element;
	}
	
	
	@Override
	public boolean hasFixedSize()
	{
		return true;
	}
	
	@Override
	public int size()
	{
		return 1;
	}
	
	@Override
	public boolean isEmpty()
	{
		return false;
	}
	
	@Override
	public E getArbitraryElement()
	{
		return this.element;
	}
	
	@Override
	public Iterator<E> iterator()
	{
		return new SingletonIterator<E>(this.element);
	}
	
	@Override
	public boolean isReadableCollection()
	{
		return true;
	}
	
	@Override
	public boolean isWritableCollection()
	{
		return false;
	}
}
