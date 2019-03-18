/*
 * Created on May 8, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.classhacking.jre;

import static rebound.util.classhacking.ClasshackingUtilities.*;
import java.awt.Window;
import rebound.exceptions.ImpossibleException;
import rebound.util.classhacking.HackedClassOrMemberUnavailableException;
import rebound.util.objectutil.JavaNamespace;
import sun.misc.Unsafe;

public class HorribleHackeyUtilities_SunJDK_Hardlinked
implements JavaNamespace
{
	/**
	 * Note that this only works if AccessibleObject.setAccessible(true) is allowed for us by the SecurityManager  (specifically Field.setAccessible(true), for sun.mic.Unsafe.theUnsafe  ;>  )
	 * @throws SecurityException if the coppers got us!
	 */
	public static Unsafe getTheUnsafe() throws SecurityException
	{
		return (Unsafe)horriblyGetOtherwiseInaccessibleStaticFieldValueYouAreCertainExistsAndEverything("sun.misc.Unsafe", "theUnsafe");
	}
	
	
	public static long getX11WindowId(Window window) throws SecurityException, IllegalArgumentException
	{
		try
		{
			return HorribleHackeyUtilities_SunJDK.getX11WindowId(window);
		}
		catch (HackedClassOrMemberUnavailableException exc)
		{
			throw new ImpossibleException(exc);
		}
	}
}
