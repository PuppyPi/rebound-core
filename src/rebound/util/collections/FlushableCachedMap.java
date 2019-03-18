package rebound.util.collections;

import rebound.util.FlushableCache;

public interface FlushableCachedMap<K, V>
extends CachedMap<K, V>, FlushableCache
{
}