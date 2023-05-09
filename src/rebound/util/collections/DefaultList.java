package rebound.util.collections;

import static rebound.text.StringUtilities.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import rebound.util.objectutil.DefaultEqualsRestrictionCircumvention;
import rebound.util.objectutil.DefaultHashCodeRestrictionCircumvention;
import rebound.util.objectutil.DefaultToStringRestrictionCircumvention;

public interface DefaultList<E>
extends List<E>, DefaultCollection<E>, DefaultToStringRestrictionCircumvention
{
	@Override
	public default boolean addAll(Collection<? extends E> c)
	{
		return DefaultCollection.super.addAll(c);
	}
	
	@Override
	public default boolean containsAll(Collection<?> c)
	{
		return DefaultCollection.super.containsAll(c);
	}
	
	@Override
	public default boolean retainAll(Collection<?> c)
	{
		return DefaultCollection.super.retainAll(c);
	}
	
	@Override
	public default boolean removeAll(Collection<?> c)
	{
		return DefaultCollection.super.removeAll(c);
	}
	
	@Override
	public default boolean isEmpty()
	{
		return DefaultCollection.super.isEmpty();
	}
	
	@Override
	public default Object[] toArray()
	{
		return DefaultCollection.super.toArray();
	}
	
	@Override
	public default <T> T[] toArray(T[] a)
	{
		return DefaultCollection.super.toArray(a);
	}
	
	
	
	
	
	@Override
	public default boolean addAll(int index, Collection<? extends E> c)
	{
		return CollectionUtilities.defaultListAddAll(this, index, c);
	}
	
	@Override
	public default boolean add(E e)
	{
		add(size(), e);
		return true;
	}
	
	@Override
	public default int indexOf(Object o)
	{
		return CollectionUtilities.defaultListIndexOf(this, o);
	}
	
	@Override
	public default int lastIndexOf(Object o)
	{
		return CollectionUtilities.defaultListLastIndexOf(this, o);
	}
	
	@Override
	public default boolean contains(Object o)
	{
		return indexOf(o) != -1;
	}
	
	@Override
	public default boolean remove(Object o)
	{
		return CollectionUtilities.defaultRemoveBySearch(this, o);
	}
	
	@Override
	public default Iterator<E> iterator()
	{
		return listIterator();
	}
	
	@Override
	public default ListIterator<E> listIterator()
	{
		return this.listIterator(0);
	}
	
	
	
	
	@Override
	public default ListIterator<E> listIterator(int index)
	{
		return new DelegatingListIterator<E>(this, index);
	}
	
	@Override
	public default List<E> subList(int fromIndex, int toIndex)
	{
		return DelegatingSublist.instByRange(this, fromIndex, toIndex);
	}
	
	
	
	
	
	
	
	@Override
	public default String _toString()
	{
		return "[" + reprListContents(this) + "]";
	}
	
	
	
	
	
	
	
	
	
	
	
	public static interface DefaultMutableList<E>
	extends DefaultList<E>, DefaultEqualsRestrictionCircumvention, DefaultHashCodeRestrictionCircumvention, DefaultToStringRestrictionCircumvention
	{
		@Override
		public default int _hashCode()
		{
			return System.identityHashCode(this);
		}
		
		@Override
		public default boolean _equals(Object o)
		{
			return o == this;
		}
	}
	
	public static interface DefaultImmutableList<E>
	extends DefaultReadonlyList<E>, DefaultEqualsRestrictionCircumvention, DefaultHashCodeRestrictionCircumvention, DefaultToStringRestrictionCircumvention
	{
		@Override
		public default int _hashCode()
		{
			return CollectionUtilities.defaultListHashCode(this);
		}
		
		@Override
		public default boolean _equals(Object o)
		{
			return o instanceof List ? CollectionUtilities.defaultListsEquivalent(this, (List)o) : false;
		}
	}
}