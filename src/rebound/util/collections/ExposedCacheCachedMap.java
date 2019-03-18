package rebound.util.collections;

import java.util.Map;
import rebound.util.ExposedCache;

public interface ExposedCacheCachedMap<K, V>
extends CachedMap<K, V>, ExposedCache<Map<K, V>>
{
}