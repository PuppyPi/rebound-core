package rebound.util.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import rebound.concurrency.immutability.JavaImmutability;
import rebound.exceptions.NotYetImplementedException;
import rebound.exceptions.OverflowException;
import rebound.util.objectutil.ObjectUtilities;
import rebound.util.objectutil.ObjectUtilities.CloneNotSupportedReturnPath;
import rebound.util.objectutil.PubliclyCloneable;
import rebound.util.objectutil.RuntimeImmutability;
import rebound.util.objectutil.StaticallyIdentityless;

/**
 * The omega set is defined as the set that contains all things that it could possibly contain! XD
 * So to make a mutable one, we keep track of the things it *doesn't* contains ;D
 * 
 * ..unless you call {@link #clear()} on it..then it just becomes a normal set XD!
 * ...UNLESSUNLESS YOU CALL {@link #addAll(Collection)} WITH ANOTHER {@link CollectionUtilities#isOmegaSet(Object) OMEGA SET}! :O
 * XD
 */
public class MutableOmegaSet<E>
implements Set<E>, RuntimeOmegaSet, StaticallyIdentityless, RuntimeImmutability, PubliclyCloneable<MutableOmegaSet<E>>
{
	protected boolean areWeOmega = true;
	protected Set<E> underlyingNegativeSet;
	protected Set<E> underlyingPositiveSet;
	
	public MutableOmegaSet()
	{
	}
	
	public MutableOmegaSet(Set<E> underlyingNegativeSet, Set<E> underlyingPositiveSet)
	{
		this.underlyingNegativeSet = underlyingNegativeSet;
	}
	
	
	
	public Set<E> getUnderlyingNegativeSet()
	{
		if (this.underlyingNegativeSet == null)
			this.underlyingNegativeSet = new HashSet<>();
		return this.underlyingNegativeSet;
	}
	
	public void setUnderlyingNegativeSet(Set<E> underlyingNegativeSet)
	{
		this.underlyingNegativeSet = underlyingNegativeSet;
	}
	
	
	public Set<E> getUnderlyingPositiveSet()
	{
		if (this.underlyingPositiveSet == null)
			this.underlyingPositiveSet = new HashSet<>();
		return this.underlyingPositiveSet;
	}
	
	public void setUnderlyingPositiveSet(Set<E> underlyingPositiveSet)
	{
		this.underlyingPositiveSet = underlyingPositiveSet;
	}
	
	
	
	
	/**
	 * Basically, returns if {@link #clear()} hasn't been called, or, then, if {@link #addAll(Collection)}(<an omega set>) has been! XD
	 */
	@Override
	public boolean isOmegaSet()
	{
		return this.areWeOmega;
	}
	
	
	/**
	 * Subclasses can override if desired / makes sense :3
	 */
	@Override
	public JavaImmutability isImmutable()
	{
		return JavaImmutability.Mutable;
	}
	
	
	
	
	
	
	
	@Override
	public boolean contains(Object o)
	{
		if (this.isOmegaSet())
		{
			//Contains everything.  it's the omega set B)   XD
			//..minus the exact things removed from it ..XD
			return !getUnderlyingNegativeSet().contains(o);
		}
		else
		{
			return getUnderlyingPositiveSet().contains(o);
		}
	}
	
	@Override
	public boolean containsAll(Collection c)
	{
		if (this.isOmegaSet())
		{
			//Contains everything.  it's the omega set B)   XD
			//..minus the exact things removed from it ..XD
			for (Object o : c)
				if (getUnderlyingNegativeSet().contains(o))
					return false;
			return true;
		}
		else
		{
			return getUnderlyingPositiveSet().containsAll(c);
		}
	}
	
	
	@Override
	public int size()
	{
		if (this.isOmegaSet())
			throw new OverflowException();
		else
			return getUnderlyingPositiveSet().size();
	}
	
	@Override
	public boolean isEmpty()
	{
		if (this.isOmegaSet())
			//...WELL IT'S DEFINITELY NOT EMPTY THAT'S FOR SURE!! XD!!
			return false;
		else
			return getUnderlyingPositiveSet().isEmpty();
	}
	
	@Override
	public Iterator iterator()
	{
		if (this.isOmegaSet())
			throw new UnsupportedOperationException();
		else
			return getUnderlyingPositiveSet().iterator();
	}
	
	@Override
	public Object[] toArray()
	{
		if (this.isOmegaSet())
			throw new OverflowException();
		else
			return getUnderlyingPositiveSet().toArray();
	}
	
	@Override
	public <T> T[] toArray(T[] a)
	{
		if (this.isOmegaSet())
			throw new OverflowException();
		else
			return getUnderlyingPositiveSet().toArray(a);
	}
	
	
	@Override
	public boolean equals(Object obj)
	{
		return super.equals(obj);
		
	}
	
	@Override
	public int hashCode()
	{
		return super.hashCode();
		
	}
	
	@Override
	public MutableOmegaSet<E> clone()
	{
		MutableOmegaSet<E> clone = new MutableOmegaSet<>();
		
		clone.areWeOmega = this.areWeOmega;
		
		//TODO @@@
		try
		{
			clone.underlyingNegativeSet = ObjectUtilities.attemptCloneRp(this.underlyingNegativeSet);
		}
		catch (CloneNotSupportedReturnPath exc)
		{
			
		}
		
		throw new NotYetImplementedException();
		
		//return clone;
	}
	
	
	
	@Override
	public boolean add(E e)
	{
		if (this.isOmegaSet())
		{
			return getUnderlyingNegativeSet().remove(e);
		}
		else
		{
			return getUnderlyingPositiveSet().add(e);
		}
	}
	
	@Override
	public boolean remove(Object e)
	{
		if (this.isOmegaSet())
		{
			return getUnderlyingNegativeSet().add((E)e);
		}
		else
		{
			return getUnderlyingPositiveSet().remove(e);
		}
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		if (this.isOmegaSet())
		{
			return getUnderlyingNegativeSet().removeAll(c);
		}
		else
		{
			return getUnderlyingPositiveSet().addAll(c);
		}
	}
	
	@Override
	public boolean removeAll(Collection<?> c)
	{
		return false;
		
	}
	
	@Override
	public boolean retainAll(Collection<?> c)
	{
		return false;
		
	}
	
	@Override
	public void clear()
	{
	}
}