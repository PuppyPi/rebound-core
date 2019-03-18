/*
 * Created on Jan 3, 2012
 * 	by the great Eclipse(c)
 */
package rebound.util.collections;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.hints.IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser;

/**
 * Note: Supports weak-keyed underlying maps! :D
 */
public class MapSet<E, V>
extends AbstractSet<E>
implements CollectionWithGetArbitraryElement<E>, CollectionWithPopArbitraryElement<E>, CollectionWithGetExtantInstanceNatural<E>
{
	protected Map<E, V> underlyingMap;
	
	/*
	 * NOTE: EXISTENCE OF THIS IS ABSOLUTELY NECESSARY FOR EXTERNALIZABLE SUBTYPES!!!
	 */
	public MapSet()
	{
	}
	
	public MapSet(Map<E, V> underlyingMap)
	{
		this.underlyingMap = underlyingMap;
	}
	
	
	
	@ImplementationTransparency
	public Map<E, V> getUnderlyingMap()
	{
		return this.underlyingMap;
	}
	
	@ImplementationTransparency
	public void setUnderlyingMap(Map<E, V> underlyingMap)
	{
		this.underlyingMap = underlyingMap;
	}
	
	
	
	
	@Override
	public Iterator<E> iterator()
	{
		return this.underlyingMap.keySet().iterator();
	}
	
	@Override
	public int size()
	{
		return this.underlyingMap.size();
	}
	
	@Override
	public boolean add(E e)
	{
		boolean previouslyContained = contains(e);
		if (!previouslyContained)
		{
			V dummyValue = usesElementsAsTheirOwnValues() ? (V)e : getDummyValueIfNotUsingElementsThemselves();
			this.underlyingMap.put(e, dummyValue);
		}
		return !previouslyContained;
	}
	
	public boolean addAll(Iterable<? extends E> c)
	{
		boolean atLeastOne = false;
		for (E e : c)
			atLeastOne |= add(e);
		return atLeastOne;
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		return addAll((Iterable<? extends E>)c);
	}
	
	@Override
	public boolean remove(Object o)
	{
		return this.underlyingMap.remove(o) != null;
	}
	
	@Override
	public void clear()
	{
		this.underlyingMap.clear();
	}
	
	@Override
	public boolean isEmpty()
	{
		return this.underlyingMap.isEmpty();
	}
	
	@Override
	public int hashCode()
	{
		return this.underlyingMap.keySet().hashCode();
	}
	
	
	@Override
	public boolean contains(Object o)
	{
		return this.underlyingMap.containsKey(o);
	}
	
	
	@Override
	public E popArbitraryElement()
	{
		if (isEmpty())
			return null;
		
		Iterator<E> i = this.iterator();
		
		if (!i.hasNext())
			//throw new ImpossibleException("We checked not-empty! :[");
			return null;  //we checked non-empty but it might be a weak-keyed map!! \o/
		else
		{
			E e = i.next();
			i.remove();
			return e;
		}
	}
	
	@Override
	public E getArbitraryElement()
	{
		if (isEmpty())
			return null;
		
		Iterator<E> i = this.iterator();
		
		if (!i.hasNext())
			//throw new ImpossibleException("We checked not-empty! :[");
			return null;  //we checked non-empty but it might be a weak-keyed map!! \o/
		else
		{
			E e = i.next();
			return e;
		}
	}
	
	/**
	 * @return one arbitrary element from this set that is not in the other set, or <code>null</code> if there is none
	 */
	public E getArbitraryElementFromAntiIntersection(Set<E> otherSet)
	{
		if (this.isEmpty())
			return null;
		if (otherSet.isEmpty())
			return getArbitraryElement();
		
		//SPEED faster way of doing this
		
		for (E e : this)
			if (!otherSet.contains(e))
				return e;
		return null;
	}
	
	
	@Override
	public E getExtantInstance(E possiblyEquivalentButDifferentInstance)
	{
		if (!usesElementsAsTheirOwnValues())
			throw new UnsupportedOperationException();
		
		return (E)this.underlyingMap.get(possiblyEquivalentButDifferentInstance);
	}
	
	
	
	
	
	
	
	/**
	 * Note that if they aren't, then {@link #getExtantInstance(Object)} won't work!
	 * (but..does anyone use that anyways? xD'' )
	 * 
	 * + If this is true, then the type parameter V should just be E  XD
	 * 
	 * + Default: true
	 */
	@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
	public boolean usesElementsAsTheirOwnValues()
	{
		return true;
	}
	
	@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
	protected V getDummyValueIfNotUsingElementsThemselves()
	{
		return null;  //that's pretty much always fine as long as it's not an injective/bijective map underneath us! XD''
	}
}
