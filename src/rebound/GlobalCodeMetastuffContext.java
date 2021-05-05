package rebound;

import static rebound.text.StringUtilities.*;
import static rebound.util.objectutil.ObjectUtilities.*;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.GlobalCodeMetastuffContextImpl.NoopingGlobalCodeMetastuffContextImpl;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.exceptions.NotYetImplementedException;
import rebound.util.objectutil.JavaNamespace;

public class GlobalCodeMetastuffContext
implements JavaNamespace
{
	public static final String ImplClassnameSystemProperty = "rebound.GlobalCodeMetastuffContextImpl";
	
	protected static volatile GlobalCodeMetastuffContextImpl Impl = null;
	
	
	@ImplementationTransparency
	@Nonnull
	public static void setImpl(GlobalCodeMetastuffContextImpl impl)
	{
		GlobalCodeMetastuffContext.Impl = impl;
	}
	
	@ImplementationTransparency
	@Nonnull
	public static GlobalCodeMetastuffContextImpl getImpl()
	{
		GlobalCodeMetastuffContextImpl i = Impl;
		
		if (i != null)
		{
			return i;
		}
		else
		{
			//Using volatiles instead of synchronized's, It is okayyyy if this makeImpl() happens more than once, and statistically, it will be cached over the vast majority of accesses so it's not much of a performance concern :3'
			i = makeImpl();
			
			if (i != null)
			{
				Impl = i;
				return i;
			}
			else
			{
				return NoopingGlobalCodeMetastuffContextImpl.I;
			}
		}
	}
	
	
	@Nullable
	protected static GlobalCodeMetastuffContextImpl makeImpl()
	{
		String p = System.getProperty(ImplClassnameSystemProperty);
		
		if (p != null) p = p.trim();
		
		if (p == null || p.isEmpty())
			return makeDefaultImpl();
		
		Class c;
		{
			try
			{
				c = Class.forName(p);
			}
			catch (ClassNotFoundException exc)
			{
				System.err.println("Error loading implementation of "+GlobalCodeMetastuffContextImpl.class.getName()+": "+p);
				exc.printStackTrace();
				return null;
			}
		}
		
		if (!GlobalCodeMetastuffContextImpl.class.isAssignableFrom(c))
		{
			System.err.println("Error loading implementation of "+GlobalCodeMetastuffContextImpl.class.getName()+": "+c.getName());
			System.err.println(c+" class does not implement "+GlobalCodeMetastuffContextImpl.class.getName());
			return null;
		}
		
		Object i;
		{
			try
			{
				i = c.newInstance();
			}
			catch (InstantiationException exc)
			{
				System.err.println("Error loading implementation of "+GlobalCodeMetastuffContextImpl.class.getName()+": "+c.getName());
				exc.printStackTrace();
				return null;
			}
			catch (IllegalAccessException exc)
			{
				System.err.println("Error loading implementation of "+GlobalCodeMetastuffContextImpl.class.getName()+": "+c.getName());
				exc.printStackTrace();
				return null;
			}
			catch (SecurityException exc)
			{
				System.err.println("Error loading implementation of "+GlobalCodeMetastuffContextImpl.class.getName()+": "+c.getName());
				exc.printStackTrace();
				return null;
			}
		}
		
		
		
		if (!(i instanceof GlobalCodeMetastuffContextImpl))
		{
			System.err.println("Error loading implementation of "+GlobalCodeMetastuffContextImpl.class.getName()+": "+c.getName());
			System.err.println(c+" instance does not implement "+GlobalCodeMetastuffContextImpl.class.getName()+"  (but we checked this against the Class object earlier using reflection!! o,O)");
			return null;
		}
		
		
		return (GlobalCodeMetastuffContextImpl)i;
	}
	
	
	
	
	
	
	@Nullable
	protected static GlobalCodeMetastuffContextImpl makeDefaultImpl()
	{
		//Todo make better error messages xD'
		
		return new GlobalCodeMetastuffContextImpl()
		{
			@Override
			public void logStaticResourceAccessIntegrityFailure(File resourceFile, Exception exc)
			{
				logBug("logStaticResourceAccessIntegrityFailure("+repr(resourceFile.getAbsolutePath())+", (Exception...)) called!!", exc);
			}
			
			@Override
			public void logStaticResourceAccessIOFailure(File resourceFile, IOException exc)
			{
				logBug("logStaticResourceAccessIOFailure("+repr(resourceFile.getAbsolutePath())+", (IOException...)) called!!", exc);
			}
			
			
			
			
			@Override
			public void logBug()
			{
				System.err.println("BUG!!");
				new Throwable("dummy throwable for formatting :P").printStackTrace();
				System.err.println("\n\n\n");
			}
			
			
			@Override
			public void logBug(String message)
			{
				System.err.println("BUG!!:\n"+message);
				new Throwable("dummy throwable for formatting :P").printStackTrace();
				System.err.println("\n\n\n");
			}
			
			
			@Override
			public void logBug(Throwable exc)
			{
				System.err.println("BUG!!");
				System.err.println("== Actual given throwable! ==");
				exc.printStackTrace();
				System.err.println("== Logging stacktrace ^^ ==");
				new Throwable("dummy throwable for formatting :P").printStackTrace();
				System.err.println("\n\n\n");
			}
			
			
			@Override
			public void logBug(String message, Throwable exc)
			{
				System.err.println("BUG!!:\n"+message);
				System.err.println("== Actual given throwable! ==");
				exc.printStackTrace();
				System.err.println("== Logging stacktrace ^^ ==");
				new Throwable("dummy throwable for formatting :P").printStackTrace();
				System.err.println("\n\n\n");
			}
		};
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	//Delegates /o/
	public static void logBug()
	{
		getImpl().logBug();
	}
	
	public static void logBugMsg(@Nullable Object message)
	{
		logBug(toStringNT(message));
	}
	
	public static void logBug(@Nullable String message)
	{
		getImpl().logBug(toStringNT(message));
	}
	
	public static <T extends Throwable> T logBug(T exc)
	{
		getImpl().logBug(exc);
		return exc;
	}
	
	public static <T extends Throwable> T logBug(String message, T exc)
	{
		getImpl().logBug(message, exc);
		return exc;
	}
	
	public static <T extends IOException> T logStaticResourceAccessIOFailure(@Nullable File resourceFile, @Nullable T exc)
	{
		getImpl().logStaticResourceAccessIOFailure(resourceFile, exc);
		return exc;
	}
	
	public static <T extends Exception> T logStaticResourceAccessIntegrityFailure(@Nullable File resourceFile, @Nullable T exc)
	{
		getImpl().logStaticResourceAccessIntegrityFailure(resourceFile, exc);
		return exc;
	}
	
	
	
	
	//Todo make these delegates/impl'able?
	public static NotYetImplementedException logNYI()
	{
		NotYetImplementedException e = new NotYetImplementedException();
		logBug(e);
		return e;
	}
	
	public static NotYetImplementedException logNYI(String message)
	{
		NotYetImplementedException e = new NotYetImplementedException(message);
		logBug(e);
		return e;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//Utilities! :D
	public static void expectNonnull(Object x)
	{
		if (x == null)
			logBug();
	}
	
	public static <E> E expectNonnull(E x, E dummyValue)
	{
		if (x == null)
		{
			logBug();
			return dummyValue;
		}
		else
		{
			return x;
		}
	}
	
	
	private GlobalCodeMetastuffContext() {}
}
