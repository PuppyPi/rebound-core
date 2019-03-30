package rebound.util.classhacking.jre;

import java.awt.Component;
import java.awt.Window;
import java.lang.reflect.Field;
import rebound.exceptions.ImpossibleException;
import rebound.util.classhacking.ClasshackingUtilities;
import rebound.util.classhacking.HackedClassOrMemberUnavailableException;

public class ClasshackingSunX11
{
	protected Class class_XBaseWindow;
	
	protected Field field_Component_peer;
	protected Field field_XBaseWindow_window;
	
	
	
	public ClasshackingSunX11() throws HackedClassOrMemberUnavailableException
	{
		class_XBaseWindow = ClasshackingUtilities.classhackingForName("sun.awt.X11.XBaseWindow");
		
		try
		{
			field_Component_peer = Component.class.getDeclaredField("peer");
			field_Component_peer.setAccessible(true);
		}
		catch (NoSuchFieldException exc)
		{
			throw new HackedClassOrMemberUnavailableException(exc);
		}
		
		try
		{
			field_XBaseWindow_window = class_XBaseWindow.getDeclaredField("window");
			field_XBaseWindow_window.setAccessible(true);
		}
		catch (NoSuchFieldException exc)
		{
			throw new HackedClassOrMemberUnavailableException(exc);
		}
	}
	
	
	
	/**
	 * Gets the X11 window id from an AWT window.  ;D
	 * @throws IllegalArgumentException wrapping a {@link ClassCastException} if the provided window is not of the correct type (currently, sun.awt.X11.XBaseWindow  :> ), or doesn't have a peer! xD
	 * @throws SecurityException if the coppers got us!
	 */
	public long getX11WindowId(Window window) throws SecurityException, IllegalArgumentException
	{
		//Finally removed between Java 8 and Java 11 XD
		//		@SuppressWarnings("deprecation")
		//		Object windowPeer = window.getPeer();
		
		Object windowPeer;
		{
			try
			{
				windowPeer = field_Component_peer.get(window);
			}
			catch (IllegalArgumentException exc)
			{
				throw new HackedClassOrMemberUnavailableException(exc);
			}
			catch (IllegalAccessException exc)
			{
				//SecurityException? yus, IllegalAccessException? hmmm..
				throw new ImpossibleException(exc);
			}
		}
		
		
		if (windowPeer == null)
			throw new IllegalArgumentException("Well, the window *does* have to have an operating-system-level peer xD");
		
		if (!class_XBaseWindow.isInstance(windowPeer))
			throw new IllegalArgumentException(new ClassCastException());
		
		
		try
		{
			Long windowId = (Long)field_XBaseWindow_window.get(windowPeer);
			return windowId;
		}
		catch (IllegalArgumentException exc)
		{
			throw new HackedClassOrMemberUnavailableException(exc);
		}
		catch (IllegalAccessException exc)
		{
			//SecurityException? yus, IllegalAccessException? hmmm..
			throw new ImpossibleException(exc);
		}
	}
}
