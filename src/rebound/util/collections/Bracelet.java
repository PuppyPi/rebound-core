package rebound.util.collections;

import static rebound.util.collections.CollectionUtilities.*;
import java.util.List;

/**
 * Readonly/immutable Bracelets!
 * 
 * Bracelets are like {@link Necklace}s, but with reflection (reversal) also included along with rotation :3
 * (ie, like a physical {@link Necklace} in three dimensions :3 )
 */
public interface Bracelet<E>
{
	public int size();
	
	public List<E> asListFromCanonicalStartingPointAndReflection();
	
	
	
	
	
	public default int _hashCode()
	{
		return asListFromCanonicalStartingPointAndReflection().hashCode();
	}
	
	public default boolean _equals(Object o)
	{
		if (o instanceof Bracelet)
			return eqv(asListFromCanonicalStartingPointAndReflection(), ((Bracelet)o).asListFromCanonicalStartingPointAndReflection());
		else
			return false;
	}
	
	public default String _toString()
	{
		return asListFromCanonicalStartingPointAndReflection().toString();
	}
}
