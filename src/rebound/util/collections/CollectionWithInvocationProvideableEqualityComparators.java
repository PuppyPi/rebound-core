/*
 * Created on Jan 12, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import rebound.util.objectutil.EqualityComparator;

//Todo now actually support these in my code XD

public interface CollectionWithInvocationProvideableEqualityComparators<E>
extends Collection<E>
{
	/*
	 * Methods updated for allowing EqualityComparator's :>
		add
		remove
		contains
		equals
		containsAll
		addAll
		removeAll
		retainAll
	 */
	
	/*
	 * Methods that don't need them!
		size
		isEmpty
		iterator
		toArray's
		clear
		hashCode
		[clone]
	 */
	
	
	public boolean add(E e, EqualityComparator<E> comparator);
	public boolean remove(E e, EqualityComparator<E> comparator);
	public boolean contains(E e, EqualityComparator<E> comparator);
	
	public boolean equals(Collection other, EqualityComparator<E> comparator);
	public boolean containsAll(Collection c, EqualityComparator<E> comparator);
	public boolean addAll(Collection c, EqualityComparator<E> comparator);
	public boolean removeAll(Collection c, EqualityComparator<E> comparator);
	public boolean retainAll(Collection c, EqualityComparator<E> comparator);
	
	
	
	
	
	public static interface ListWithEqualityComparators<E>
	extends List<E>, CollectionWithInvocationProvideableEqualityComparators<E>
	{
		//NOT NEEDED
		//E get(int index);
		//E set(int index, E element);
		//void add(int index, E element);
		//E remove(int index);
		//ListIterator<E> listIterator();
		//ListIterator<E> listIterator(int index);
		///////
		
		//YES NEEDED
		public int indexOf(Object o, EqualityComparator<E> comparator);
		public int lastIndexOf(Object o, EqualityComparator<E> comparator);
		public List<E> subList(int fromIndex, int toIndex, EqualityComparator<E> comparator);
		///////
	}
	
	
	
	public static interface SetWithEqualityComparators<E>
	extends Set<E>, CollectionWithInvocationProvideableEqualityComparators<E>
	{
		//Nothing new outside of Collection!
		//*shrugs*
		//:>
	}
}
