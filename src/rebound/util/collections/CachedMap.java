package rebound.util.collections;

import java.util.Map;
import rebound.util.Cache;

/**
 * Just a nice mixed interface of {@link Map} and {@link Cache} :>
 * 
 * @author RProgrammer
 */
public interface CachedMap<K, V>
extends Map<K, V>, Cache
{
}