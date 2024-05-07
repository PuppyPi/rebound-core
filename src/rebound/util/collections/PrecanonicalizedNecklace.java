package rebound.util.collections;

import static java.util.Objects.*;
import java.util.List;

public class PrecanonicalizedNecklace<E>
implements Necklace<E>
{
	protected List<E> underlyingCanonicalRotation;
	
	public PrecanonicalizedNecklace(List<E> underlyingCanonicalRotation)
	{
		this.underlyingCanonicalRotation = requireNonNull(underlyingCanonicalRotation);
	}
	
	
	@Override
	public int size()
	{
		return underlyingCanonicalRotation.size();
	}
	
	@Override
	public List<E> asListFromCanonicalStartingPoint() throws UnsupportedOperationException
	{
		return this.underlyingCanonicalRotation;
	}
	
	
	
	
	
	@Override
	public int hashCode()
	{
		return _hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return _equals(obj);
	}
	
	@Override
	public String toString()
	{
		return _toString();
	}
}
