/*
 * Created on Feb 25, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections.maps;

import java.util.Map;
import java.util.Set;

public interface MapValueCollectionView<V>
extends Set<V>
{
	public Map<?, V> getOwningMap();
}
