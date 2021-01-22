package rebound.util.collections;

import static java.util.Objects.*;
import java.util.List;

public class PrecanonicalizedBracelet<E>
implements Bracelet<E>
{
	protected List<E> underlyingCanonicalRotationAndReflection;
	
	public PrecanonicalizedBracelet(List<E> underlyingCanonicalRotationAndReflection)
	{
		this.underlyingCanonicalRotationAndReflection = requireNonNull(underlyingCanonicalRotationAndReflection);
	}
	
	
	@Override
	public List<E> asListFromCanonicalStartingPointAndReflection() throws UnsupportedOperationException
	{
		return this.underlyingCanonicalRotationAndReflection;
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
