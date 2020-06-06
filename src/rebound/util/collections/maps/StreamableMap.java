package rebound.util.collections.maps;

import java.util.Iterator;
import java.util.Map.Entry;
import rebound.annotations.semantic.SignalType;

/**
 * Note that there is no CBORStreamableList / CBORStreamableArray because that's just {@link Iterable} XD
 */
@SignalType
public interface StreamableMap<K, V>
{
	public Iterator<Entry<K, V>> entryIterator();
}
