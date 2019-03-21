/*
 * Created on May 8, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.classhacking.jre;

import static rebound.util.classhacking.ClasshackingUtilities.*;
import java.awt.Window;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.FileStore;
import rebound.exceptions.UnreachableCodeException;
import rebound.util.AngryReflectionUtility;
import rebound.util.classhacking.ClasshackingUtilities;
import rebound.util.classhacking.HackedClassOrMemberUnavailableException;
import rebound.util.objectutil.JavaNamespace;

public class HorribleHackeyUtilities_SunJDK
implements JavaNamespace
{
	//<Unsafe things!  >;)
	public static boolean isUnsafePresentAndAccessible()
	{
		//See the 'fixme' note (itz not workin ._. )
		return false;
		
		//		try
		//		{
		//			loadTheUnsafe();
		//			return true;
		//		}
		//		catch (HackedClassOrMemberUnavailableException exc)
		//		{
		//			return false;
		//		}
		//		catch (SecurityException exc)
		//		{
		//			return false;
		//		}
	}
	
	
	//Todo use a proper classhacking proxy class generator for making an Unsafe.java delegator proxy class, and use that :>
	
	protected static final Object lockUnsafe = new Object();
	protected static Class cachedUnsafeClass;
	protected static Method cachedUnsafeMethod_throwException;
	protected static Object cachedUnsafeFieldValue_theUnsafe;
	
	public static void loadTheUnsafe() throws SecurityException, HackedClassOrMemberUnavailableException
	{
		synchronized (lockUnsafe)
		{
			if (cachedUnsafeClass == null)
			{
				cachedUnsafeClass = ClasshackingUtilities.classhackingForName("sun.misc.Unsafe");
				
				cachedUnsafeFieldValue_theUnsafe = horriblyGetOtherwiseInaccessibleStaticFieldValueYouAreCertainExistsAndEverything("sun.misc.Unsafe", "theUnsafe");
				
				cachedUnsafeMethod_throwException = AngryReflectionUtility.getMethod(cachedUnsafeClass, "throwException", new Class[]{Throwable.class}, null, null, false, false);
			}
		}
	}
	
	
	/**
	 * Note that this only works if AccessibleObject.setAccessible(true) is allowed for us by the SecurityManager  (specifically Field.setAccessible(true), for sun.mic.Unsafe.theUnsafe  ;>  )
	 * @throws SecurityException if the coppers got us!
	 */
	public static Object getTheUnsafe() throws SecurityException, HackedClassOrMemberUnavailableException
	{
		synchronized (lockUnsafe)
		{
			loadTheUnsafe();
			
			return cachedUnsafeFieldValue_theUnsafe;
		}
	}
	
	
	
	
	public static void unsafeThrowExceptionUnchecked(Throwable throwable) throws SecurityException, HackedClassOrMemberUnavailableException
	{
		synchronized (lockUnsafe)
		{
			loadTheUnsafe();
			
			try
			{
				cachedUnsafeMethod_throwException.invoke(getTheUnsafe(), throwable);
			}
			catch (IllegalAccessException exc)
			{
			}
			catch (IllegalArgumentException exc)
			{
			}
			catch (InvocationTargetException exc)
			{
				//Well now, this is a funny one XD
				System.out.println("aw, man!");
				
				//FIXME hmmmmmmm... XD
			}
			
			
			throw new UnreachableCodeException();
		}
	}
	//Unsafe things>
	
	
	
	
	
	
	
	
	
	
	
	//<Unix/Posix things ;D
	
	//<X11 things  >;)
	/**
	 * Gets the X11 window id from an AWT window.  ;D
	 * @throws IllegalArgumentException wrapping a {@link ClassCastException} if the provided window is not of the correct type (currently, sun.awt.X11.XBaseWindow  :> ), or doesn't have a peer! xD
	 * @throws SecurityException if the coppers got us!
	 */
	public static long getX11WindowId(Window window) throws SecurityException, HackedClassOrMemberUnavailableException, IllegalArgumentException
	{
		@SuppressWarnings("deprecation")
		Object windowPeer = window.getPeer();
		
		if (windowPeer == null)
			throw new IllegalArgumentException("Well, the window *does* have to have a peer xD");
		
		Class XBaseWindow = ClasshackingUtilities.classhackingForName("sun.awt.X11.XBaseWindow");
		
		if (!XBaseWindow.isInstance(windowPeer))
			throw new IllegalArgumentException(new ClassCastException());
		
		return (Long)horriblyGetOtherwiseInaccessibleFieldValueYouAreCertainExistsAndEverything("sun.awt.X11.XBaseWindow", "window", windowPeer);
	}
	//X11 things>
	
	
	//See ClasshackingSunUnixFileStore for getting mount points, both getting all mount points on the current system and getting the mount point given a pathname :D     (both of those are handled via FileStore in the standard NIO api :> )
	
	
	//Unix/Posix things ;D >
}
