/*
 * Created on May 8, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.classhacking.jre;

import static rebound.util.classhacking.ClasshackingUtilities.*;
import rebound.util.AngryReflectionUtility;
import rebound.util.classhacking.HackedClassOrMemberUnavailableException;
import rebound.util.objectutil.JavaNamespace;

public class ClasshackingSunUnsafe
implements JavaNamespace
{
	/**
	 * Note that this only works if AccessibleObject.setAccessible(true) is allowed for us by the SecurityManager  (specifically Field.setAccessible(true), for sun.mic.Unsafe.theUnsafe  ;>  )
	 * @throws SecurityException if the coppers got us!
	 */
	public static Object getTheUnsafe() throws SecurityException
	{
		Class c = AngryReflectionUtility.forName("sun.misc.Unsafe");
		if (c == null)  c = AngryReflectionUtility.forName("jdk.internal.misc.Unsafe");
		
		if (c == null)
			throw new HackedClassOrMemberUnavailableException("Neither sun.misc.Unsafe nor jdk.internal.misc.Unsafe were available!");
		
		return horriblyGetOtherwiseInaccessibleStaticFieldValueYouAreCertainExistsAndEverything(c, "theUnsafe");
	}
}
