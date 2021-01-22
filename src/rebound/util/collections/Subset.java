package rebound.util.collections;

import static rebound.util.objectutil.BasicObjectUtilities.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.exceptions.DefensivelyUnsupportedOperationException;
import rebound.util.collections.SimpleIterator.SimpleIterable;

public class Subset<E>
implements DefaultReadonlySet<E>, InfiniteIterable<E>
{
	protected final Set<E> superset;
	protected final Predicate<E> predicate;
	protected int size;
	protected Boolean empty = null;
	
	public Subset(Set<E> superset, Predicate<E> predicate)
	{
		this(superset, predicate, -1);
	}
	
	public Subset(Set<E> superset, Predicate<E> predicate, int size)
	{
		this.superset = superset;
		this.predicate = predicate;
		this.size = size;
		
		if (size != -1 && size > 0)
			this.empty = false;
		else if (size == 0)
			this.empty = true;
		else
			this.empty = null;
	}
	
	
	@ImplementationTransparency
	public Set<E> getSuperset()
	{
		return superset;
	}
	
	@ImplementationTransparency
	public Predicate<E> getPredicate()
	{
		return predicate;
	}
	
	
	
	@Override
	public boolean contains(Object o)
	{
		return superset.contains(o) && predicate.test((E)o);  //probably should've been contains(E) not contains(Object) but oh well X'3
	}
	
	
	
	@Override
	public Iterator<E> iterator()
	{
		return simpleIterator().toIterator();
	}
	
	@Override
	public SimpleIterator<E> simpleIterator()
	{
		if (this.isInfiniteIterable())
			throw new DefensivelyUnsupportedOperationException();
		else
			return infiniteSimpleIterator();
	}
	
	@Override
	public boolean isInfiniteIterable()
	{
		return InfiniteIterable.is(superset);
	}
	
	
	@Override
	public Iterator<E> infiniteIterator()
	{
		return infiniteSimpleIterator().toIterator();
	}
	
	@Override
	public SimpleIterator<E> infiniteSimpleIterator()
	{
		SimpleIterator<E> i = InfiniteIterable.is(superset) ? ((InfiniteIterable<E>)superset).infiniteSimpleIterator() : SimpleIterable.simpleIteratorOf(superset);
		
		return () ->
		{
			while (true)
			{
				E e = i.nextrp();  //propagate StopIteration :3
				
				if (predicate.test(e))
					return e;
				//else continue
			}
		};
	}
	
	
	
	
	
	
	
	
	
	@Override
	public int size()
	{
		if (isInfiniteIterable())
		{
			throw new UnsupportedOperationException();
		}
		else
		{
			int size = this.size;
			
			if (size == -1)
			{
				int s = 0;
				for (@SuppressWarnings("unused") E e : this)
					s++;
				this.size = s;
			}
			
			return size;
		}
	}
	
	@Override
	public boolean isEmpty()
	{
		Boolean empty = this.empty;
		
		if (empty == null)
		{
			if (superset.isEmpty())
				empty = true;
			else
				empty = this.infiniteIterator().hasNext();
			
			this.empty = empty;
		}
		
		return empty;
	}
	
	
	/**
	 * aka "isSupersetOf()"
	 * 
	 * isSubset(supersetCandidate=this, subsetCandidate=subsetCandidate)
	 */
	@Override
	public boolean containsAll(Collection<?> subsetCandidate)
	{
		if (subsetCandidate == this)
			return true;
		
		if (subsetCandidate.isEmpty())
			return true;
		else if (this.isEmpty()) //&& !subsetCandidate.isEmpty()
			return false;
		
		if (subsetCandidate instanceof Subset)
			if (((Subset)subsetCandidate).getSuperset() == this)   //(a - b) - c  ⊆  a - b
				return true;
		
		return DefaultReadonlySet.super.containsAll(subsetCandidate);
	}
	
	
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		if (obj instanceof Subset)
		{
			Subset other = (Subset) obj;
			if (eq(other.getSuperset(), this.getSuperset()) && eq(other.getPredicate(), this.getPredicate()))
				return true;
		}
		
		if (obj instanceof Set)
		{
			Set other = (Set) obj;
			
			if (other.isEmpty())
				return this.isEmpty();
			else if (this.isEmpty())  //&& !other.isEmpty()
				return false;
			
			if (this.size() != other.size())
				return false;
			
			return DefaultReadonlySet.super._equals(obj);
		}
		else
		{
			return false;
		}
	}
}
