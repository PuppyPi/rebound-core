package rebound.util.collections;

import static java.util.Objects.*;
import static rebound.util.CodeHinting.*;
import static rebound.util.collections.CollectionUtilities.*;
import static rebound.util.collections.prim.PrimitiveCollections.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import rebound.exceptions.DefensivelyUnsupportedOperationException;
import rebound.exceptions.StopIterationReturnPath;
import rebound.util.collections.prim.PrimitiveCollections.BooleanList;

public class UnionSet<E>
implements DefaultReadonlySet<E>, InfiniteIterable<E>
{
	protected final Set<Set<? extends E>> sets;
	protected int size;
	protected Boolean empty = null;
	
	public UnionSet(Set<Set<? extends E>> sets)
	{
		this(sets, -1);
	}
	
	public UnionSet(Set<Set<? extends E>> sets, int size)
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
		
		List l = mapToList(set -> InfiniteIterable.infiniteSimpleIteratorOf(set), sets);
		List<SimpleIterator<? extends E>> iterators = l;
		
		return new SimpleIterator<E>()
		{
			int turn = 0;
			int numberFinished = 0;
			BooleanList finisheds = newBooleanListZerofilled(iterators.size());
			
			@Override
			public E nextrp() throws StopIterationReturnPath
			{
				int n = arbitrary(finisheds.size(), iterators.size());
				
				while (true)
				{
					if (numberFinished == n)
						throw StopIterationReturnPath.I;
					
					if (!finisheds.getBoolean(turn))
					{
						SimpleIterator<? extends E> i = iterators.get(turn);
						
						boolean worked = false;
						E v = null;
						try
						{
							v = i.nextrp();
						}
						catch (StopIterationReturnPath rp)
						{
							finisheds.setBoolean(turn, true);
							numberFinished++;
							turn++;
							worked = false;
						}
						
						if (worked)
						{
							turn++;
							return v;
						}
						//else: continue
					}
					
					turn++;
				}
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
		
		if (sets.contains(subsetCandidate))
			return true;
		
		if (subsetCandidate instanceof UnionSet)
			if (this.getSets().containsAll(((UnionSet)subsetCandidate).getSets()))   //a + b  ⊆  a + b + c
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
		
		if (obj instanceof UnionSet)
		{
			UnionSet other = (UnionSet) obj;
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
