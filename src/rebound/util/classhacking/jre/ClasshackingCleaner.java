package rebound.util.classhacking.jre;

import static rebound.testing.WidespreadTestingUtilities.*;
import static rebound.util.AngryReflectionUtility.*;
import static rebound.util.BasicExceptionUtilities.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.UnreachableCodeException;
import rebound.util.AngryReflectionUtility;

public class ClasshackingCleaner
{
	protected static final Object defaultCleaner;
	protected static final Method register;
	protected static final Method clean;
	
	static
	{
		Class cleaner = forName("java.lang.ref.Cleaner");
		
		if (cleaner != null)
		{
			Class cleanable = forName("java.lang.ref.Cleaner$Cleanable");
			
			Method create = getMethod(cleaner, "create", new Class[]{});
			register = getMethod(cleaner, "register", new Class[]{Object.class, Runnable.class});
			clean = getMethod(cleanable, "clean", new Class[]{});
			
			asrt(Modifier.isPublic(create.getModifiers()));
			asrt(Modifier.isPublic(register.getModifiers()));
			asrt(Modifier.isPublic(clean.getModifiers()));
			
			asrt(Modifier.isStatic(create.getModifiers()));
			asrt(!Modifier.isStatic(register.getModifiers()));
			asrt(!Modifier.isStatic(clean.getModifiers()));
			
			try
			{
				defaultCleaner = create.invoke(null);
			}
			catch (IllegalAccessException exc)
			{
				throw new ImpossibleException(exc);
			}
			catch (IllegalArgumentException exc)
			{
				throw new ImpossibleException(exc);
			}
			catch (InvocationTargetException exc)
			{
				rethrowSafe(exc);
				throw new UnreachableCodeException();
			}
		}
		else
		{
			Class c = AngryReflectionUtility.forName("sun.misc.Cleaner");
			
			if (c != null)
			{
				register = getMethod(c, "create", new Class[]{Object.class, Runnable.class});
				clean = getMethod(c, "clean", new Class[]{});
				
				asrt(Modifier.isPublic(register.getModifiers()));
				asrt(Modifier.isPublic(clean.getModifiers()));
				
				asrt(Modifier.isStatic(register.getModifiers()));
				asrt(!Modifier.isStatic(clean.getModifiers()));
				
				defaultCleaner = null;
			}
			else
			{
				throw new ImpossibleException("Neither the old sun.misc.Cleaner nor the new java.lang.ref.Cleaner were found!");
			}
		}
	}
	
	
	
	
	
	
	public static Runnable register(Object referent, Runnable action)
	{
		Object cleanable;
		try
		{
			cleanable = register.invoke(defaultCleaner, referent, action);
		}
		catch (IllegalAccessException exc)
		{
			throw new ImpossibleException(exc);
		}
		catch (IllegalArgumentException exc)
		{
			throw new ImpossibleException(exc);
		}
		catch (InvocationTargetException exc)
		{
			rethrowSafe(exc);
			throw new UnreachableCodeException();
		}
		
		
		
		return () ->
		{
			try
			{
				clean.invoke(cleanable);
			}
			catch (IllegalAccessException exc)
			{
				throw new ImpossibleException(exc);
			}
			catch (IllegalArgumentException exc)
			{
				throw new ImpossibleException(exc);
			}
			catch (InvocationTargetException exc)
			{
				rethrowSafe(exc);
				throw new UnreachableCodeException();
			}
		};
	}
}
