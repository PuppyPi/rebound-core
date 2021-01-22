/*
 * Created on Apr 15, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import rebound.annotations.semantic.reachability.EscapesVarargs;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.annotations.semantic.temporal.ConstantReturnValue;

/**
 * Need to wrap a {@link Collection} in a {@link Set} that you merely <i>assert</i> has no duplicate elements?..
 * 
 * {@link AssertedSet} :)
 * 
 * 
 * @author Puppy Pie ^_^
 */
public class AssertedSet<E>
implements Set<E>
{
	@LiveValue
	protected Collection<E> underlying;
	
	
	public AssertedSet()
	{
	}
	
	public AssertedSet(@LiveValue Collection<E> underlying)
	{
		this.underlying = underlying;
	}
	
	@EscapesVarargs
	public AssertedSet(@LiveValue E... underlying)
	{
		this.underlying = Arrays.asList(underlying);
	}
	
	public static <E> AssertedSet<E> checkNonduplicationAndInst(@LiveValue Collection<E> underlying)
	{
		if (!PolymorphicCollectionUtilities.checkSet(underlying))
			throw new IllegalArgumentException("The given collection ("+underlying+") is not a set: it contains duplicate members! :O");
		
		return new AssertedSet<>(underlying);
	}
	
	@EscapesVarargs
	public static <E> AssertedSet<E> checkNonduplicationAndInstVarargs(@LiveValue E... underlying)
	{
		if (!PolymorphicCollectionUtilities.checkSet(underlying))
			throw new IllegalArgumentException("The given array ("+underlying+") is not a set: it contains duplicate members! :O");
		
		return new AssertedSet<>(underlying);
	}
	
	
	/**
	 * Note: this is also used as a hook, so you can override this class and make this method return different things :D
	 * (ie, not {@link ConstantReturnValue} ;3 )
	 */
	public Collection<E> getUnderlying()
	{
		return this.underlying;
	}
	
	public void setUnderlying(@LiveValue Collection<E> underlying)
	{
		this.underlying = underlying;
	}
	
	public void checkNonduplicationAndSetUnderlying(@LiveValue Collection<E> underlying)
	{
		if (!PolymorphicCollectionUtilities.checkSet(underlying))
			throw new IllegalArgumentException("The given collection ("+underlying+") is not a set: it contains duplicate members! :O");
		
		this.underlying = underlying;
	}
	
	public void checkNonduplicationAndSetUnderlying(@LiveValue E... underlying)
	{
		if (!PolymorphicCollectionUtilities.checkSet(underlying))
			throw new IllegalArgumentException("The given array ("+underlying+") is not a set: it contains duplicate members! :O");
		
		this.underlying = Arrays.asList(underlying);
	}
	
	
	
	
	@Override
	public int size()
	{
		return getUnderlying().size();
	}
	
	@Override
	public boolean isEmpty()
	{
		return getUnderlying().isEmpty();
	}
	
	@Override
	public boolean contains(Object o)
	{
		return getUnderlying().contains(o);
	}
	
	@Override
	public Iterator<E> iterator()
	{
		return getUnderlying().iterator();
	}
	
	@Override
	public Object[] toArray()
	{
		return getUnderlying().toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a)
	{
		return getUnderlying().toArray(a);
	}
	
	@Override
	public boolean add(E e)
	{
		return getUnderlying().add(e);
	}
	
	@Override
	public boolean remove(Object o)
	{
		return getUnderlying().remove(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c)
	{
		return getUnderlying().containsAll(c);
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		return getUnderlying().addAll(c);
	}
	
	@Override
	public boolean retainAll(Collection<?> c)
	{
		return getUnderlying().retainAll(c);
	}
	
	@Override
	public boolean removeAll(Collection<?> c)
	{
		return getUnderlying().removeAll(c);
	}
	
	@Override
	public void clear()
	{
		getUnderlying().clear();
	}
	
	
	
	@Override
	public boolean equals(Object obj)
	{
		return getUnderlying().equals(obj);
	}
	
	@Override
	public int hashCode()
	{
		return getUnderlying().hashCode();
	}
	
	@Override
	public String toString()
	{
		return getUnderlying().toString();
	}
}
