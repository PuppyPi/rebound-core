package rebound.util.collections;

import static rebound.text.StringUtilities.*;
import java.util.Collection;
import java.util.Set;
import rebound.util.objectutil.DefaultEqualsRestrictionCircumvention;
import rebound.util.objectutil.DefaultHashCodeRestrictionCircumvention;
import rebound.util.objectutil.DefaultToStringRestrictionCircumvention;

public interface DefaultSet<E>
extends Set<E>, DefaultCollection<E>, DefaultToStringRestrictionCircumvention
{
	public default boolean _equals(Object o)
	{
		if (o instanceof Set)
			return CollectionUtilities.defaultSetsEquivalent(this, (Set)o);
		else
			return false;
	}
	
	
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
	public default String _toString()
	{
		return "{" + reprListContents(this) + "}";
	}
	
	
	
	
	
	
	public static interface DefaultMutableSet<E>
	extends DefaultSet<E>, DefaultEqualsRestrictionCircumvention, DefaultHashCodeRestrictionCircumvention, DefaultToStringRestrictionCircumvention
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
	
	public static interface DefaultImmutableSet<E>
	extends DefaultSet<E>, DefaultEqualsRestrictionCircumvention, DefaultHashCodeRestrictionCircumvention, DefaultToStringRestrictionCircumvention
	{
		@Override
		public default int _hashCode()
		{
			return CollectionUtilities.defaultSetHashCode(this);
		}
		
		@Override
		public default boolean _equals(Object o)
		{
			return o instanceof Set ? CollectionUtilities.defaultSetsEquivalent(this, (Set)o) : false;
		}
	}
}