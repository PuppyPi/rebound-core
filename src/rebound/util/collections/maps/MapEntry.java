/*
 * Created on Feb 25, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections.maps;

import java.util.Map;
import java.util.Map.Entry;

public interface MapEntry<K, V>
extends Entry<K, V>
{
	public Map<K, V> getOwningMap();
}
