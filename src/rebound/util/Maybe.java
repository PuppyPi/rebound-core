package rebound.util;

import java.util.Map;
import rebound.util.collections.BasicCollectionUtilities;

/**
 * null is used for Nothing here!
 * 
 * This is different from normal Nullable fields/parameters/returntypes because it can nested-ly encapsulate nulls/nothings!
 * 
 * Say you have a function like {@link Map#get(Object)} that could return null because that's just the value or because the key is absent from the map.
 * If it was typed to return Maybe<E> instead of (nullable) E, then you could have get() return null only when the key was absent, and an instance of this class (wrapping whatever the actual value in the map was, null or anything else) when the key's present in the map! :>
 * 
 * (In almost all JVM's it's only efficient when the function gets specialized and inlined, since most JVM's don't optimize the runtime form of types other than primitive boxing classes, unlike the GHC does for Haskell ^^'' )
 */
public class Maybe<E>
{
	protected final E just;
	
	/**
	 * {@link BasicCollectionUtilities#just(Object) just()} is probably clearer code—this class should probably only be used for Type signatures ^^'
	 */
	public Maybe(E just)
	{
		this.just = just;
	}
	
	public E getJust()
	{
		return just;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((just == null) ? 0 : just.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Maybe other = (Maybe) obj;
		if (just == null)
		{
			if (other.just != null)
				return false;
		}
		else if (!just.equals(other.just))
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return "Just(" + just + ")";
	}
}
