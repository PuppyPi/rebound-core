package rebound.util.collections;

import rebound.util.FlushableCache;

/**
 * Note: if one of these also extends {@link FlushableCache}, resetting the cache must also effectively do a {@link #recomputeKeyset()} :>
 * 
 * @author Puppy Pie ^_^
 */
public interface MapWithExplicitlyRecomputedKeyset
{
	public void recomputeKeyset();
}