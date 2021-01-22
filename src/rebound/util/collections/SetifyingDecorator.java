/*
 * Created on Jun 28, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections;

import static rebound.util.collections.CollectionUtilities.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.exceptions.AlreadyExistsException;
import rebound.util.objectutil.Copyable;
import rebound.util.objectutil.ObjectUtilities;
import rebound.util.objectutil.PubliclyCloneable;
import rebound.util.objectutil.Trimmable;

/**
 * For those times when a super-heavy set implementation is actually less efficient ^_^
 * @author RProgrammer
 */
public class SetifyingDecorator<E>
implements Set<E>, PubliclyCloneable<SetifyingDecorator<E>>, Copyable, Trimmable, CollectionWithGetArbitraryElement<E>, CollectionWithPopArbitraryElement<E>
{
	protected Collection<E> backing;
	
	public SetifyingDecorator()
	{
		this.backing = new ArrayList<E>();
	}
	
	public SetifyingDecorator(@LiveValue Collection<E> backing)
	{
		this.backing = backing;
	}
	
	public static <E> SetifyingDecorator<E> newSafe(@LiveValue Collection<E> backing)
	{
		if (new HashSet<>(backing).size() != backing.size())
			throw new AlreadyExistsException();
		
		return new SetifyingDecorator<>(backing);
	}
	
	public static <E> SetifyingDecorator<E> newUniqueifyingOP(@SnapshotValue Collection<E> backing)
	{
		Collection<E> newBacking = new ArrayList<>(backing.size());
		
		for (E e : backing)
			if (!newBacking.contains(e))
				newBacking.add(e);
		
		return new SetifyingDecorator<>(newBacking);
	}
	
	
	
	public Collection<E> getBacking()
	{
		return this.backing;
	}
	
	public void setBacking(Collection<E> backing)
	{
		this.backing = backing;
	}
	
	@Override
	public void setFrom(Object s)
	{
		SetifyingDecorator<E> source = (SetifyingDecorator<E>) s;
		setBacking(ObjectUtilities.attemptCloneWithReflection(source.getBacking()));
	}
	
	
	@Override
	public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
	{
		return ObjectUtilities.trimThing(backing, false);
	}
	
	
	
	
	//Adding (this is the *only* place we actually check to insure the Set constraint :> )
	
	@Override
	public boolean add(E e)
	{
		boolean contains = contains(e);
		if (!contains)
			backing.add(e);
		return !contains;
	}
	
	
	
	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		boolean atLeastOne = false;
		for (E e : c)
			atLeastOne |= add(e);
		return atLeastOne;
	}
	
	
	
	
	
	
	//Removing things
	@Override
	public boolean remove(Object o)
	{
		return backing.remove(o);
	}
	
	@Override
	public void clear()
	{
		backing.clear();
	}
	
	@Override
	public boolean removeAll(Collection<?> c)
	{
		return backing.removeAll(c);
	}
	
	@Override
	public boolean retainAll(Collection<?> c)
	{
		return backing.retainAll(c);
	}
	
	@Override
	public Iterator<E> iterator()
	{
		return backing.iterator();
	}
	
	@Override
	public E popArbitraryElement()
	{
		return popArbitraryElementDefaulting(backing);
	}
	
	@Override
	public E getArbitraryElement()
	{
		return getArbitraryElementDefaulting(backing);
	}
	
	
	
	
	//Read-only!
	
	//Membership testing!
	@Override
	public boolean containsAll(Collection<?> c)
	{
		return backing.containsAll(c);
	}
	
	@Override
	public boolean contains(Object o)
	{
		return backing.contains(o);
	}
	
	
	
	
	
	@Override
	public int size()
	{
		return backing.size();
	}
	
	@Override
	public boolean isEmpty()
	{
		return backing.isEmpty();
	}
	
	@Override
	public Object[] toArray()
	{
		return backing.toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a)
	{
		return backing.toArray(a);
	}
	
	
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof SetifyingDecorator)
			return this.backing.equals(((SetifyingDecorator)obj).backing);
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return backing.hashCode();
	}
	
	@Override
	public String toString()
	{
		return backing.toString();
	}
	
	@Override
	public SetifyingDecorator<E> clone()
	{
		return new SetifyingDecorator<E>(ObjectUtilities.attemptCloneWithReflection(backing));
	}
}
