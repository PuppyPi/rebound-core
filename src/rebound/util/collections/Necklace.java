package rebound.util.collections;

import static rebound.util.collections.CollectionUtilities.*;
import java.util.List;

/**
 * Readonly/immutable Necklaces!
 * 
 * Necklaces are basically just lists but with no particular starting point—modular/circular lists! :D
 * The most famous example being a simple polygon given by a necklace of its vertices!
 */
public interface Necklace<E>
{
	public List<E> asListFromCanonicalStartingPoint();
	
	
	
	
	
	public default int _hashCode()
	{
		return asListFromCanonicalStartingPoint().hashCode();
	}
	
	public default boolean _equals(Object o)
	{
		if (o instanceof Necklace)
			return eqv(asListFromCanonicalStartingPoint(), ((Necklace)o).asListFromCanonicalStartingPoint());
		else
			return false;
	}
	
	public default String _toString()
	{
		return asListFromCanonicalStartingPoint().toString();
	}
}
