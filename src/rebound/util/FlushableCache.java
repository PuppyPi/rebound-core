package rebound.util;

/**
 * An interface into the cache for to control it, and (harmlessly save for performance!),
 * flush/clear/reset it! :D
 * 
 * @author Puppy Pie ^_^
 */
public interface FlushableCache
extends Cache
{
	public void resetCache();
}