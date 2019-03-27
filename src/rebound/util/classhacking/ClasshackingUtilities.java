/*
 * Created on May 19, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.classhacking;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceConfigurationError;
import rebound.exceptions.ImpossibleException;
import rebound.text.StringUtilities;
import rebound.util.AngryReflectionUtility;
import rebound.util.functional.FunctionInterfaces.NullaryFunction;
import rebound.util.objectutil.JavaNamespace;
import rebound.util.res.ResourceUtilities;

public class ClasshackingUtilities
implements JavaNamespace
{
	public static <E> E ensureHackedClassThingNonNull(E thing, String descNameForException)
	{
		if (thing != null)
			return thing;
		else
			throw new HackedClassOrMemberUnavailableException("Thing not available: "+StringUtilities.repr(descNameForException));
	}
	
	public static Class classhackingForName(String name) throws HackedClassOrMemberUnavailableException
	{
		return ensureHackedClassThingNonNull(AngryReflectionUtility.forName(name), name);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @return either the properly loaded hardlinked proxy, or null if it is missing a linked-to class :>
	 */
	public static <P> P catchCorrectErrorsForServiceLoading(NullaryFunction<P> serviceLoaderThing)
	{
		try
		{
			return serviceLoaderThing.f();
		}
		catch (ServiceConfigurationError exc)
		{
			if (exc.getCause() instanceof LinkageError)
			{
				//skip it--it almost surely simply is missing the hardlinked-to thing :>
				return null;
			}
			else
			{
				throw exc; //rethrow it :>
			}
		}
	}
	
	/**
	 * @return either the properly loaded hardlinked proxy, or null if it is missing a linked-to class :>
	 */
	public static <P> P catchCorrectErrorsForNormalInstantiation(NullaryFunction<P> loaderThing)
	{
		try
		{
			return loaderThing.f();
		}
		catch (LinkageError exc)
		{
			//skip it--it almost surely simply is missing the hardlinked-to thing :>
			return null;
		}
	}
	
	
	
	
	
	
	
	
	
	public static <P> P loadHardLinkedProxy(final Class<P> serviceSpecificationClass, final String implementationClassName)
	{
		//Centralize the important error-catching code, but also produce normal ServiceConfigurationError's ^_^
		return catchCorrectErrorsForServiceLoading(new NullaryFunction<P>
		()
		{
			@Override
			public P f()
			{
				return ResourceUtilities.loadAndInstantiateServiceProvider(serviceSpecificationClass, implementationClassName);
			}
		});
	}
	
	
	public static <P> List<P> loadHardLinkedProxies(Class<P> proxyInterfaceClass)
	{
		List<String> names = ResourceUtilities.getServiceProviderClassNames(proxyInterfaceClass);
		
		List<P> ps = new ArrayList<>();
		
		for (String name : names)
		{
			P p = loadHardLinkedProxy(proxyInterfaceClass, name);
			
			if (p != null)
				ps.add(p);
		}
		
		return ps;
	}
	
	
	/**
	 * @return either <i>a</i> properly loaded hardlinked proxy, or null if there are none that are not missing their linked-to class[es] :>
	 */
	public static <P> P getArbitraryHardLinkedProxy(Class<P> proxyInterfaceClass)
	{
		List<P> ps = loadHardLinkedProxies(proxyInterfaceClass);
		
		if (ps.isEmpty())
			return null;
		else
			return ps.get(0);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static Object horriblyGetOtherwiseInaccessibleFieldValueYouAreCertainExistsAndEverything(Class leClass, String fieldName, Object obj) throws ImpossibleException, SecurityException
	{
		try
		{
			Field f = leClass.getDeclaredField(fieldName);
			f.setAccessible(true);
			return f.get(obj);
		}
		catch (NoSuchFieldException exc)
		{
			throw new ImpossibleException(exc);
		}
		catch (IllegalArgumentException exc)
		{
			throw new ImpossibleException(exc);
		}
		catch (IllegalAccessException exc)
		{
			//SecurityException? yus, IllegalAccessException? hmmm..
			throw new ImpossibleException(exc);
		}
	}
	
	public static Object horriblyGetOtherwiseInaccessibleStaticFieldValueYouAreCertainExistsAndEverything(Class leClass, String fieldName) throws ImpossibleException, SecurityException
	{
		return horriblyGetOtherwiseInaccessibleFieldValueYouAreCertainExistsAndEverything(leClass, fieldName, null);
	}
	
	
	
	
	public static Object horriblyGetOtherwiseInaccessibleFieldValueYouAreCertainExistsAndEverything(String classname, String fieldName, Object obj) throws ImpossibleException, SecurityException
	{
		try
		{
			Class c = Class.forName(classname);
			return horriblyGetOtherwiseInaccessibleFieldValueYouAreCertainExistsAndEverything(c, fieldName, obj);
		}
		catch (ClassNotFoundException exc)
		{
			throw new ImpossibleException(exc);
		}
	}
	
	public static Object horriblyGetOtherwiseInaccessibleStaticFieldValueYouAreCertainExistsAndEverything(String classname, String fieldName) throws ImpossibleException, SecurityException
	{
		return horriblyGetOtherwiseInaccessibleFieldValueYouAreCertainExistsAndEverything(classname, fieldName, null);
	}
}
