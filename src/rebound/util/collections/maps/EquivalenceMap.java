/*
 * Created on Oct 23, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections.maps;

import java.util.Map;
import rebound.annotations.semantic.SignalInterface;

/**
 * @see IdentityMap
 * @author RProgrammer
 */
@SignalInterface
public interface EquivalenceMap<K, V>
extends Map<K, V>
{
}
