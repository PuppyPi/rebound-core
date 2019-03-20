/*
 * Created on Oct 23, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections.maps;

import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;
import rebound.annotations.semantic.SignalType;

/**
 * Just an extending signal interface signalling the map uses identity (==) instead of canonical equivalence (.equals()) ^_^
 * Note that often times those are equivalent; in which case it would count as an {@link IdentityMap} as well as an equivalence map!
 * + {@link IdentityHashMap} and {@link EnumMap} are grandfathered in by this ^_^
 * @author RProgrammer
 */
@SignalType
public interface IdentityMap<K, V>
extends Map<K, V>
{
}
