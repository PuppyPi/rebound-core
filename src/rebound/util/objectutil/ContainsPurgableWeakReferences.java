package rebound.util.objectutil;

import static rebound.GlobalCodeMetastuffContext.*;
import java.util.Map;
import java.util.WeakHashMap;
import rebound.annotations.semantic.SignalType;

/**
 * This is critical for, say, a {@link WeakHashMap} which might be the only reference to some of its *values*, which should be cleared when the keys are!
 * 
 * The JVM doesn't have a nice efficient garbage collection callback mechanism, so the conventional technique (used in the JRE's {@link WeakHashMap} among others), is simply to let those {@link Map.Entry}'s linger until the map is accessed again.
 * But if you rely on using that map to create an effective strong reference from the key to the value without being able to add a field to the key class,
 *  and the map is not frequently accessed,
 *  then you've got a problem!!!
 * 
 * So you've got to make sure *to* frequently get it to purge those entries!!  At least when the user desires to clean up memory usage!  (Eg, the "Collect Garbage" button! ;D )
 * This interface exists to make that easier :3
 * (but with this or without it, purging weak references will always require cooperation from the weak-reference-containing-datastructure implementations of course!)
 */
@SuppressWarnings("javadoc")
@SignalType
public interface ContainsPurgableWeakReferences
{
	public void purgeClearedWeakReferences();
	
	
	
	
	
	/**
	 * @return if we recognized it!
	 */
	public static boolean purgeClearedWeakReferencesWithGrandfathering(Object x)
	{
		if (x instanceof ContainsPurgableWeakReferences)
		{
			((ContainsPurgableWeakReferences)x).purgeClearedWeakReferences();
			return true;
		}
		
		if (x instanceof java.util.WeakHashMap)
		{
			((Map)x).size();  //at least as of jdk1.8.0_45, this calls expungeStaleEntries() !
			return true;
		}
		
		return false;
	}
	
	
	public static void purgeClearedWeakReferencesWithGrandfatheringLoggingIfUnsupported(Object x)
	{
		if (!purgeClearedWeakReferencesWithGrandfathering(x))
			logBug();
	}
}
