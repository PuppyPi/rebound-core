/*
 * Created on Feb 25, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections.maps;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public interface MapEntrySetView<K, V>
extends Set<Entry<K, V>>
{
	public Map<K, V> getOwningMap();
}
