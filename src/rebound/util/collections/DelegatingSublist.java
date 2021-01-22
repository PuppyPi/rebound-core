/*
 * Created on May 12, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections;

import static rebound.util.collections.CollectionUtilities.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.util.objectutil.PubliclyCloneable;

public class DelegatingSublist<E>
implements Sublist<E>, List<E>, PubliclyCloneable<Sublist<E>>
{
	protected final List<E> underlyingList;
	protected final int startingIndex;
	protected int length;
	
	public DelegatingSublist(List<E> underlyingList, int startingIndex, int length)
	{
		rangeCheckIntervalByLength(underlyingList.size(), startingIndex, length);
		
		this.underlyingList = underlyingList;
		this.startingIndex = startingIndex;
		this.length = length;
	}
	
	public static <E> DelegatingSublist<E> instByLength(List<E> underlyingList, int startingIndex, int length)
	{
		return new DelegatingSublist<>(underlyingList, startingIndex, length);
	}
	
	/**
	 * Matches exactly with {@link List#subList(int, int)} :3
	 */
	public static <E> DelegatingSublist<E> instByRange(List<E> underlyingList, int fromIndex, int toIndex)
	{
		return new DelegatingSublist<>(underlyingList, fromIndex, toIndex - fromIndex);
	}
	
	
	
	@ImplementationTransparency
	@Override
	public List<E> getUnderlying()
	{
		return underlyingList;
	}
	
	@Override
	public int getSublistStartingIndex()
	{
		return this.startingIndex;
	}
	
	@Override
	public int size()
	{
		return length;
	}
	
	
	@Override
	public boolean add(E e)
	{
		add(length, e);
		return true;
	}
	
	@Override
	public void add(int index, E e)
	{
		underlyingList.add(startingIndex+index, e);
		length++;
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		return CollectionUtilities.defaultAddAll(this, c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends E> c)
	{
		return CollectionUtilities.defaultListAddAll(this, index, c);
	}
	
	@Override
	public void clear()
	{
		redimToZeroByZeroErasingAllContents();
	}
	
	public void redimToZeroByZeroErasingAllContents()
	{
		CollectionUtilities.defaultClear(this);
	}
	
	@Override
	public boolean contains(Object o)
	{
		return CollectionUtilities.defaultContains(this, o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c)
	{
		return CollectionUtilities.defaultContainsAll(this, c);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Sublist)
		{
			Sublist o = (Sublist)obj;
			
			if (o.getUnderlying() == this.getUnderlying())
			{
				return o.getSublistStartingIndex() == this.getSublistStartingIndex() && o.size() == this.size();
			}
			//else, filter through to below :>
		}
		
		if (obj instanceof List)
			return CollectionUtilities.defaultListsEquivalent(this, (List)obj);
		return false;
	}
	
	@Override
	public E get(int index)
	{
		return underlyingList.get(startingIndex+index);
	}
	
	@Override
	public int hashCode()
	{
		return CollectionUtilities.defaultListHashCode(this);
	}
	
	@Override
	public int indexOf(Object o)
	{
		return CollectionUtilities.defaultListIndexOf(this, o);
	}
	
	@Override
	public boolean isEmpty()
	{
		return size() == 0;
	}
	
	@Override
	public Iterator<E> iterator()
	{
		return listIterator();
	}
	
	@Override
	public int lastIndexOf(Object o)
	{
		return CollectionUtilities.defaultListLastIndexOf(this, o);
	}
	
	@Override
	public ListIterator<E> listIterator()
	{
		return listIterator(0);
	}
	
	@Override
	public ListIterator<E> listIterator(int index)
	{
		return new SimpleRandomAccessBasedListIterator<E>(this, index);
	}
	
	@Override
	public E remove(int index)
	{
		//Do it in this order so if it fails, length is not modified ;>
		E r = underlyingList.remove(startingIndex+index);
		length--;
		return r;
	}
	
	@Override
	public boolean remove(Object o)
	{
		boolean success = CollectionUtilities.defaultRemoveBySearch(this, o);
		if (success)
			length--;
		return success;
	}
	
	@Override
	public boolean removeAll(Collection<?> c)
	{
		return CollectionUtilities.defaultRemoveAll(this, c);
	}
	
	@Override
	public boolean retainAll(Collection<?> c)
	{
		return CollectionUtilities.defaultRetainAll(this, c);
	}
	
	@Override
	public E set(int index, E element)
	{
		return underlyingList.set(startingIndex+index, element);
	}
	
	@Override
	public List<E> subList(int fromIndex, int toIndex)
	{
		//It needs to be mutable eg, through add's and remove's which change the length, so it *always* needs to be a different instance for these things to happen to, separately (intrinsic Identity of Mutables :> )
		//But we can at least flatten the ownership! :D
		return new DelegatingSublist<E>(underlyingList, startingIndex+fromIndex, toIndex - fromIndex);
	}
	
	@Override
	public Object[] toArray()
	{
		return toArray(new Object[size()]);
	}
	
	@Override
	public <T> T[] toArray(T[] a)
	{
		return (T[])CollectionUtilities.defaultToArray(this, a);
	}
	
	@Override
	public Sublist<E> clone()
	{
		return (Sublist<E>)this.subList(0, size());
	}
}
