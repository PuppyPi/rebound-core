package rebound.util.collections;

import static java.util.Objects.*;
import static rebound.util.collections.BasicCollectionUtilities.*;
import static rebound.util.collections.CollectionUtilities.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import rebound.exceptions.DefensivelyUnsupportedOperationException;

public class IntersectionSet<E>
implements DefaultReadonlySet<E>, InfiniteIterable<E>
{
	protected final Set<Set<? extends E>> sets;
	protected int size;
	protected Boolean empty = null;
	
	public IntersectionSet(Set<Set<? extends E>> sets)
	{
		this(sets, -1);
	}
	
	public IntersectionSet(Set<Set<? extends E>> sets, int size)
	{
		this.sets = requireNonNull(sets);
		this.size = size;
		
		if (size != -1 && size > 0)
			this.empty = false;
		else if (size == 0)
			this.empty = true;
		else
			this.empty = null;
	}
	
	public Set<Set<? extends E>> getSets()
	{
		return sets;
	}
	
	
	
	@Override
	public boolean contains(Object o)
	{
		return forAny(set -> set.contains(o), sets);  //hmm, maybe it's a good thing it's contains(Object) not contains(E)!  sflkjsdflfj X'D
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
		return forAny(set -> InfiniteIterable.is(set), sets);
	}
	
	
	@Override
	public Iterator<E> infiniteIterator()
	{
		return infiniteSimpleIterator().toIterator();
	}
	
	@Override
	public SimpleIterator<E> infiniteSimpleIterator()
	{
		//Breadth-first enumeration ensures a complete span of all the sets :3
		//Todo a version that supports an infinite number of sets we're unioning!  (Using triangle/diagonal enumeration :3 )     (but then contains() would never terminate if false, so is that even a valid kind of set?! XD )
		
		if (this.isEmpty() || sets.isEmpty())
			return emptySimpleIterator();
		
		if (sets.size() == 1)
			return InfiniteIterable.infiniteSimpleIteratorOf((Iterable)getArbitraryElementThrowing(sets));
		
		//else:
		
		List<Set<? extends E>> setsArbitraryOrder = new ArrayList<>(sets);
		
		SimpleIterator<? extends E> i = InfiniteIterable.infiniteSimpleIteratorOf(setsArbitraryOrder.get(0));
		List<Set<? extends E>> others = setsArbitraryOrder.subList(1, setsArbitraryOrder.size());
		
		return () ->
		{
			while (true)
			{
				E e = i.nextrp();  //propagate StopIteration :3
				
				if (forAll(o -> o.contains(e), others))
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
			if (forAll(set -> set.isEmpty(), sets))
				empty = true;
			else
				empty = this.infiniteIterator().hasNext();
			
			this.empty = empty;
		}
		
		return empty;
	}
	
	
	/**
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
		
		if (sets.contains(subsetCandidate))
			return true;
		
		if (subsetCandidate instanceof IntersectionSet)
			if (((IntersectionSet)subsetCandidate).getSets().containsAll(this.getSets()))   //a * b * c  ⊆  a * b
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
		
		if (obj instanceof IntersectionSet)
		{
			IntersectionSet other = (IntersectionSet) obj;
			if (eqv(other.getSets(), this.getSets()))
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
