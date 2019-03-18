/*
 * Created on Aug 20, 2010
 * 	by the great Eclipse(c)
 */
package rebound.util.res;

import static rebound.io.BasicIOUtilities.*;
import static rebound.text.StringUtilities.*;
import static rebound.util.collections.CollectionUtilities.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.ResourceLoadException;
import rebound.exceptions.ResourceNotFoundException;
import rebound.exceptions.UnreachableCodeException;
import rebound.exceptions.UnsupportedOptionException;
import rebound.exceptions.WrappedThrowableRuntimeException;
import rebound.file.FSUtilities;
import rebound.io.FSIOUtilities;
import rebound.io.JRECompatIOUtilities;
import rebound.io.TextIOUtilities;
import rebound.util.ExceptionUtilities;
import rebound.util.FlushableCache;
import rebound.util.objectutil.BasicObjectUtilities;
import rebound.util.objectutil.JavaNamespace;
import rebound.util.objectutil.ObjectUtilities;

public class ResourceUtilities
implements JavaNamespace
{
	//<Service Providers! :D!!
	
	//TODO support arbitrary ClassLoaders!!
	
	
	public static List<String> getServiceProviderClassNames(Class serviceSpecificationClass)
	{
		//Merge thems! ^w^
		List<String> all = new ArrayList<>();
		all.addAll(getServiceProviderClassNamesFromMetaInfs(serviceSpecificationClass));
		all.addAll(getServiceProviderClassNamesFromSystemProperty(serviceSpecificationClass));
		return all;
	}
	
	
	public static List<String> getServiceProviderClassNamesFromMetaInfs(Class serviceSpecificationClass)
	{
		String fullName = "META-INF/services/" + serviceSpecificationClass.getName();
		
		
		Enumeration<URL> configs = null;
		{
			try
			{
				configs = ClassLoader.getSystemResources(fullName);
			}
			catch (IOException exc)
			{
				throw new ServiceConfigurationError(serviceSpecificationClass.getName() + ": Error locating configuration files", exc);
			}
		}
		
		
		List<String> names = new ArrayList<String>();
		
		while (configs.hasMoreElements())
		{
			URL u = configs.nextElement();
			
			parse(serviceSpecificationClass, u, names);
		}
		
		return names;
	}
	
	protected static void parse(Class serviceSpecificationClass, URL u, Collection<String> names) throws ServiceConfigurationError
	{
		BufferedReader r = null;
		{
			try
			{
				r = new BufferedReader(new InputStreamReader(u.openStream(), "UTF-8"));
			}
			catch (IOException exc)
			{
				throw new ServiceConfigurationError(serviceSpecificationClass.getName() + ": Error opening configuration file "+u, exc);
			}
		}
		
		
		try
		{
			while (true)
			{
				String line = r.readLine();
				
				if (line == null)
					break;
				
				
				//Strip comment! ^_^
				{
					int cp = line.indexOf('#');
					if (cp != -1)
						line = line.substring(0, cp);
				}
				
				
				//Trim whitespace! :>
				line = line.trim();
				
				
				if (!line.isEmpty())
				{
					names.add(line);
				}
			}
		}
		catch (IOException exc)
		{
			Error e = new ServiceConfigurationError(serviceSpecificationClass.getName() + ": Error reading configuration file "+u, exc);
			
			try
			{
				r.close();
			}
			catch (IOException exc2)
			{
				e.addSuppressed(exc2);
			}
			
			throw e;
		}
		
		
		try
		{
			r.close();
		}
		catch (IOException exc)
		{
			throw new ServiceConfigurationError(serviceSpecificationClass.getName() + ": Error closing configuration file "+u, exc);
		}
	}
	
	
	
	public static List<String> getServiceProviderClassNamesFromSystemProperty(Class serviceSpecificationClass)
	{
		String[] splitPropertyValues = null;
		String rawPropertyValue = null;
		{
			try
			{
				rawPropertyValue = System.getProperty(serviceSpecificationClass.getName());
			}
			catch (SecurityException exc)
			{
				//ignore and continue on :>
				//(we wouldn't want to confuse the caller to think we had successfully loaded the service provider (sayyy a hardlinked proxy instance ;> XD ), but it caused a security exception!!)
				rawPropertyValue = null;
			}
			
			if (rawPropertyValue == null)
			{
				splitPropertyValues = null;
			}
			else
			{
				splitPropertyValues = rawPropertyValue.split(","); //it has a shortcut / fast-alternate-impl for non-regex, single-char cases :>'
				
				//Trim the spaces off them all :3
				splitPropertyValues = mapToNewArray(input -> input.trim(), String.class, splitPropertyValues);
				
				//Filter out empty ones :3
				splitPropertyValues = filterToNewArray(input -> !input.isEmpty(), splitPropertyValues);
			}
		}
		
		return Arrays.asList(splitPropertyValues);
	}
	
	
	
	public static <P> P loadAndInstantiateServiceProvider(Class<P> serviceSpecificationClass, String implementationClassName)
	{
		try
		{
			return (P)ObjectUtilities.newInstance(Class.forName(implementationClassName));
		}
		
		//Use same error messages as Sun's code :>
		catch (ClassNotFoundException exc)
		{
			throw new ServiceConfigurationError(serviceSpecificationClass.getName() + ": Provider " + implementationClassName + " from system property not found!", exc);
		}
		catch (WrappedThrowableRuntimeException wexc)
		{
			Throwable exc = wexc.getCause();
			if (exc instanceof InstantiationException)
				throw new ServiceConfigurationError(serviceSpecificationClass.getName() + ": Provider " + implementationClassName + " from system property could not be instantiated: "+exc, exc);
			else if (exc instanceof IllegalAccessException)
				throw new ServiceConfigurationError(serviceSpecificationClass.getName() + ": Provider " + implementationClassName + " from system property could not be instantiated: "+exc, exc);
			else
				throw wexc;
		}
	}
	
	
	/**
	 * Like {@link #loadAndInstantiateServiceProvider(Class, String)} but with less nice error messages and less safe generics :3
	 */
	public static <P> P loadAndInstantiateServiceProvider(String className)
	{
		try
		{
			return (P)ObjectUtilities.newInstance(Class.forName(className));
		}
		
		//Use same error messages as Sun's code :>
		catch (ClassNotFoundException exc)
		{
			throw new ServiceConfigurationError("Provider " + className + " from system property not found!", exc);
		}
		catch (WrappedThrowableRuntimeException wexc)
		{
			Throwable exc = wexc.getCause();
			if (exc instanceof InstantiationException)
				throw new ServiceConfigurationError("Provider " + className + " from system property could not be instantiated: "+exc, exc);
			else if (exc instanceof IllegalAccessException)
				throw new ServiceConfigurationError("Provider " + className + " from system property could not be instantiated: "+exc, exc);
			else
				throw wexc;
		}
	}
	
	
	
	
	
	
	//Note: all of these are caching, but support FlushableCache! :>!
	
	public static <P> List<P> getServiceProvidersEager(final Class<P> serviceSpecificationClass)
	{
		//Merge the two! :D
		
		final Object justMetaInfOnes = getServiceProvidersEagerJustMetaInf(serviceSpecificationClass);
		final Object justSystemPropertyOnes = getServiceProvidersEagerJustSystemProperty(serviceSpecificationClass);
		
		class fi
		extends ArrayList<P>
		implements List<P>, FlushableCache
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			public void resetCache()
			{
				((FlushableCache)justMetaInfOnes).resetCache();
				((FlushableCache)justSystemPropertyOnes).resetCache();
				
				super.addAll((Collection)justMetaInfOnes);
				super.addAll((Collection)justSystemPropertyOnes);
			}
		}
		
		
		fi theFlushableList = new fi();
		//theFlushableList.resetCache(); //not necessary here since they come preloaded! ^_^
		return theFlushableList;
	}
	
	
	public static <P> List<P> getServiceProvidersEagerJustMetaInf(final Class<P> serviceSpecificationClass)
	{
		class fi
		extends ArrayList<P>
		implements List<P>, FlushableCache
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			public void resetCache()
			{
				for (String name : getServiceProviderClassNamesFromMetaInfs(serviceSpecificationClass))
					super.add(loadAndInstantiateServiceProvider(serviceSpecificationClass, name));
			}
		}
		
		
		fi theFlushableList = new fi();
		theFlushableList.resetCache();
		return theFlushableList;
	}
	
	
	
	
	public static <P> List<P> getServiceProvidersEagerJustSystemProperty(final Class<P> serviceSpecificationClass)
	{
		class fi
		extends ArrayList<P>
		implements List<P>, FlushableCache
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			public void resetCache()
			{
				//Clear out the old!
				super.clear();
				
				
				
				
				//Add in the new! :D
				{
					for (String individualRawPropertyValue : getServiceProviderClassNamesFromSystemProperty(serviceSpecificationClass))
					{
						super.add(loadAndInstantiateServiceProvider(serviceSpecificationClass, individualRawPropertyValue));
					}
				}
			}
		}
		
		
		fi theFlushableList = new fi();
		theFlushableList.resetCache();
		return theFlushableList;
	}
	
	
	
	
	
	
	
	/*
	public static <P> Iterable<P> getServiceProvidersLazy(Class<P> serviceSpecificationClass)
	{
		
	}
	
	public static <P> Iterable<P> getServiceProvidersLazyJustMetaInf(Class<P> serviceSpecificationClass)
	{
		
	}
	
	public static <P> Iterable<P> getServiceProvidersLazyJustSystemProperty(final Class<P> serviceSpecificationClass)
	{
		class fi
		implements Iterable<P>, FlushableCache
		{
			
			
			@Override
			public Iterator<P> iterator()
			{
				return null;
			}
			
			@Override
			public void resetCache()
			{
			}
		}
		
		
		return CollectionUtilities.toIterable(new SimpleIterable<P>
		()
		{
			@Override
			public SimpleIterator<P> simpleIterator()
			{
				String[] splitPropertyValues = null;
				String rawPropertyValue = null;
				{
					try
					{
						rawPropertyValue = System.getProperty(serviceSpecificationClass.getName());
					}
					catch (SecurityException exc)
					{
						//ignore and continue on :>
						//(we wouldn't want to confuse the caller to think we had successfully loaded the service provider (sayyy a hardlinked proxy instance ;> XD ), but it caused a security exception!!)
						rawPropertyValue = null;
					}
					
					if (rawPropertyValue == null)
					{
						splitPropertyValues = null;
					}
					else
					{
						splitPropertyValues = StringUtilities.split(rawPropertyValue, ',');
						
						//Trim the spaces off them all :3
						splitPropertyValues = FunctionalUtilities.map(new UnaryFunction<String, String>
						()
						{
							@Override
							public String f(String input)
							{
								return input.trim();
							}
						}, String.class, splitPropertyValues);
						
						//Filter out empty ones :3
						splitPropertyValues = FunctionalUtilities.filter(new UnaryFunctionObjectToBoolean<String>
						()
						{
							@Override
							public boolean f(String input)
							{
								return !input.isEmpty();
							}
						}, splitPropertyValues);
					}
				}
				
				
				if (splitPropertyValues == null || splitPropertyValues.length == 0)
					return getEmptySimpleIterator();
				
				
				
				else
				{
					final SimpleIterator<String> iteratorOverSplitPropertyValues = CollectionUtilities.simpleIterator(splitPropertyValues);
					
					return new SimpleIterator<P>
					()
					{
						@Override
						public P nextrp() throws StopIterationReturnPath
						{
							String individualRawPropertyValue = iteratorOverSplitPropertyValues.nextrp(); //will throw the proper StopIteration thingy! ^w^
							
							try
							{
								return (P)Class.forName(individualRawPropertyValue).newInstance();
							}
							catch (ClassNotFoundException exc)
							{
								throw new ServiceConfigurationError(serviceSpecificationClass.getName() + ": Provider " + individualRawPropertyValue + " from system property not found!", exc);
							}
							catch (InstantiationException exc)
							{
								throw new ServiceConfigurationError(serviceSpecificationClass.getName() + ": Provider " + individualRawPropertyValue + " from system property could not be instantiated: "+exc, exc);
							}
							catch (IllegalAccessException exc)
							{
								throw new ServiceConfigurationError(serviceSpecificationClass.getName() + ": Provider " + individualRawPropertyValue + " from system property could not be instantiated: "+exc, exc);
							}
						}
					};
				}
			}
		});
	}
	
	
	
	public static <P> Iterable<P> getServiceProvidersEager(Class<P> serviceSpecificationClass)
	{
		Iterable<P> c = getServiceProvidersLazy(serviceSpecificationClass);
		
		//Just run through them once to load them (making it eager ;> )
		Iterator<P> i = c.iterator();
		while (i.hasNext()) i.next();
		
		return c;
	}
	
	public static <P> Iterable<P> getServiceProvidersEagerJustMetaInf(Class<P> serviceSpecificationClass)
	{
		Iterable<P> c = getServiceProvidersLazyJustMetaInf(serviceSpecificationClass);
		
		//Just run through them once to load them (making it eager ;> )
		Iterator<P> i = c.iterator();
		while (i.hasNext()) i.next();
		
		return c;
	}
	
	public static <P> Iterable<P> getServiceProvidersEagerJustSystemProperty(Class<P> serviceSpecificationClass)
	{
		Iterable<P> c = getServiceProvidersLazyJustSystemProperty(serviceSpecificationClass);
		
		//Just run through them once to load them (making it eager ;> )
		Iterator<P> i = c.iterator();
		while (i.hasNext()) i.next();
		
		return c;
	}
	 */
	//Service Providers! :D!! >
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//TODO Unit tests for JarURL construction (parsing uses Sun's code)
	//TODO Unit tests for consistency between suburi construction and decomposition (construction is surely correct)
	//Todo is the current suburi syntax a good scheme?
	
	
	
	
	//<Suburi
	
	/**
	 * Gets a suburi referencing a resource within another "container" URI (which points to, say, a zip file).
	 * A suburi is a URI of the form:<br>
	 * scheme ":" suburi "!" entry<br>
	 * The scheme is a normal URI scheme, meaning it cannot contain ":" or any other special characters (save "+" | "-" | ".", as per RFC 2396), but the container URI and entry might.
	 * To provide a proper nestable escaping of suburis as the container of a higher suburi, both the container uri and entry are fully escaped with application/x-www-form-urlencoded "%xx" escaping syntax.
	 * This means that in a suburi, the characters ':' and '!' can only every occur for the highest-level suburi.
	 * Note: following URI precedence for genericity, the syntax of the entry part is left to the scheme to interpret.
	 * Note: the scheme of a suburi should contain "+su", preferably at the end
	 * @param entry Must be absolute (eg, starting with '/')
	 * @throws URISyntaxException If the scheme is invalid.
	 */
	public static URI getSubURI(String scheme, URI containerURI, String entry) throws URISyntaxException
	{
		try
		{
			return new URI(scheme, URLEncoder.encode(containerURI.toString(), "UTF-8")+"!"+URLEncoder.encode(entry, "UTF-8"), "");
		}
		catch (UnsupportedEncodingException exc)
		{
			throw new ImpossibleException("No UTF-8!");
		}
	}
	
	/**
	 * Extracts the container-URL part of a suburi.
	 * @throws URISyntaxException if the container URI is invalid (after de-escaping)
	 * @throws SubURISyntaxException if the URI given is not a suburi
	 */
	public static URI getSubURIContainerURI(URI suburi) throws URISyntaxException, SubURISyntaxException
	{
		try
		{
			String ssp = suburi.getSchemeSpecificPart();
			int separatorPos = ssp.indexOf('!');
			if (separatorPos == -1)
				throw new SubURISyntaxException(suburi.toString(), "Missing separator \"!\" in scheme-specific part of the SubURI.");
			return new URI(URLDecoder.decode(ssp.substring(separatorPos), "UTF-8"));
		}
		catch (UnsupportedEncodingException exc)
		{
			throw new ImpossibleException("No UTF-8!");
		}
	}
	
	/**
	 * Extracts the opaque entry part of a suburi.
	 * @throws SubURISyntaxException if the URI given is not a suburi
	 */
	public static String getSubURIEntry(URI suburi) throws SubURISyntaxException
	{
		try
		{
			String ssp = suburi.getSchemeSpecificPart();
			int separatorPos = ssp.indexOf('!');
			if (separatorPos == -1)
				throw new SubURISyntaxException(suburi.toString(), "Missing separator \"!\" in scheme-specific part of the SubURI.");
			return URLDecoder.decode(ssp.substring(separatorPos+1), "UTF-8");
		}
		catch (UnsupportedEncodingException exc)
		{
			throw new ImpossibleException("No UTF-8!");
		}
	}
	
	public static boolean isSubURI(URI uri)
	{
		return uri.getScheme().toLowerCase().contains("+su");
	}
	
	
	public static class SubURISyntaxException
	extends URISyntaxException
	{
		private static final long serialVersionUID = 1L;
		
		public SubURISyntaxException(String input, String reason, int index)
		{
			super(input, reason, index);
		}
		
		public SubURISyntaxException(String input, String reason)
		{
			super(input, reason);
		}
	}
	//Suburi>
	
	
	
	
	
	
	//<JarURL
	/**
	 * Constructs a jar: scheme URL from the container URL and entry.
	 * Escapes both the container URL and the entry of '!' characters.
	 * @param entry Must should not start with a leading '/'
	 * @throws MalformedURLException Should only be thrown if the entry part has syntax errors.
	 */
	public static URL getJarURL(URL jarFileURL, String entry) throws MalformedURLException
	{
		if (jarFileURL.getProtocol().equalsIgnoreCase("jar"))
			throw new IllegalArgumentException("jar: scheme URL's cannot be nested as per the spec.  (it doesn't work in the JRE anyway; if you're rolling your own, use a fully escaped, nestable syntax like SubURI.)");
		
		StringBuilder url = null;
		{
			url = new StringBuilder("jar:");
			for (char c : jarFileURL.toString().toCharArray())
			{
				if (c == '!')
					url.append("%21");
				else
					url.append(c);
			}
			url.append("!/");
			try
			{
				url.append(URLEncoder.encode(entry, "UTF-8"));
			}
			catch (UnsupportedEncodingException exc)
			{
				throw new ImpossibleException("No UTF-8!");
			}
		}
		
		return new URL(url.toString());
	}
	
	public static boolean isJarURL(URL url)
	{
		return "jar".equalsIgnoreCase(url.getProtocol());
	}
	
	
	/**
	 * Uses a {@link #getDummyJarURLConnection(URL) dummy JarURLConnection} to parse out the container URL.
	 * @throws MalformedURLException if the URL does not contain a separator "!/"
	 */
	public static URL getJarURLContainerURL(URL jarURL) throws MalformedURLException
	{
		return getDummyJarURLConnection(jarURL).getJarFileURL();
	}
	
	/**
	 * Uses a {@link #getDummyJarURLConnection(URL) dummy JarURLConnection} to parse out the entry name.
	 * Note: this will not have a leading '/'
	 * @throws MalformedURLException if the URL does not contain a separator "!/"
	 */
	public static String getJarURLEntry(URL jarURL) throws MalformedURLException
	{
		return getDummyJarURLConnection(jarURL).getEntryName();
	}
	
	
	/**
	 * Gets a dummy {@link JarURLConnection} for using its parsing of the separator.
	 * @throws MalformedURLException if the URL does not contain a separator "!/"
	 */
	public static JarURLConnection getDummyJarURLConnection(URL jarURL) throws MalformedURLException
	{
		return new JarURLConnection(jarURL)
		{
			@Override
			public void connect() throws IOException
			{
				throw new UnsupportedOperationException();
			}
			
			@Override
			public JarFile getJarFile() throws IOException
			{
				throw new UnsupportedOperationException();
			}
		};
	}
	//JarURL>
	
	
	
	
	
	
	
	public static boolean isFileURL(URL url)
	{
		return "file".equalsIgnoreCase(url.getProtocol());
	}
	
	public static boolean isFileURI(URI url)
	{
		return "file".equalsIgnoreCase(url.getScheme());
	}
	
	public static boolean isLocal(URL url)
	{
		if (isFileURL(url))
			return true;
		else if (isJarURL(url))
		{
			try
			{
				URL jarurl = getJarURLContainerURL(url);
				return isLocal(jarurl);
			}
			catch (MalformedURLException exc)
			{
				return false;
			}
		}
		
		//Tests as a URI
		else
		{
			URI uriform = null;
			try
			{
				uriform = url.toURI();
			}
			catch (URISyntaxException exc)
			{
				return false;
			}
			
			if (isSubURI(uriform))
			{
				try
				{
					URI container = getSubURIContainerURI(uriform);
					return isLocal(container);
				}
				catch (SubURISyntaxException exc)
				{
					return false;
				}
				catch (URISyntaxException exc)
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
	}
	
	public static boolean isLocal(URI uri)
	{
		if (isFileURI(uri))
			return true;
		else if (isSubURI(uri))
		{
			try
			{
				URI container = getSubURIContainerURI(uri);
				return isLocal(container);
			}
			catch (SubURISyntaxException exc)
			{
				return false;
			}
			catch (URISyntaxException exc)
			{
				return false;
			}
		}
		
		//Tests as a URL
		else
		{
			URL urlform = null;
			try
			{
				urlform = uri.toURL();
			}
			catch (MalformedURLException exc1)
			{
				return false;
			}
			
			if (isJarURL(urlform))
			{
				try
				{
					URL jarurl = getJarURLContainerURL(urlform);
					return isLocal(jarurl);
				}
				catch (MalformedURLException exc)
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
	}
	
	
	
	
	
	/**
	 * Gets either the url to the JAR container if a JAR, or the SubURI container if a SubURI.
	 * 
	 * Syntax errors and the like silently return null, like non-container uri schemes.
	 */
	public static URI getContainerURI(URI uri)
	{
		if (isSubURI(uri))
		{
			try
			{
				return getSubURIContainerURI(uri);
			}
			catch (SubURISyntaxException exc)
			{
				return null;
			}
			catch (URISyntaxException exc)
			{
				return null;
			}
		}
		
		//Tests as a URL
		else
		{
			URL urlform = null;
			try
			{
				urlform = uri.toURL();
			}
			catch (MalformedURLException exc1)
			{
				return null;
			}
			
			if (isJarURL(urlform))
			{
				try
				{
					return getJarURLContainerURL(urlform).toURI();
				}
				catch (MalformedURLException exc)
				{
					return null;
				}
				catch (URISyntaxException exc)
				{
					return null;
				}
			}
			else
			{
				return null;
			}
		}
	}
	
	
	
	/**
	 * If this URI is file:// then return it.
	 * If this URI is one of those with a container and entry, then recursively operate on the container (eg, if it's a jar:// url, but the jar it refers to is local, then return the url to the jar).
	 */
	public static File getLocalmostURI(URI uri)
	{
		if (isFileURI(uri))
			return new File(uri);
		else
		{
			URI container = getContainerURI(uri);
			if (container != null)
				return getLocalmostURI(container);
			else
				return null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	@Nonnull
	public static byte[] loadBinaryResourceRelativeToClass(Class c, String name) throws ResourceNotFoundException, ResourceLoadException
	{
		name = ltrimstr(name, "/");
		
		Package p = c.getPackage();
		String path = "/";
		if (p != null)
			path += p.getName().replace('.', '/')+"/";
		path += name;
		
		return loadBinaryResource(c, path);
	}
	
	@Nonnull
	public static String loadTextResourceRelativeToClass(Class c, String name, Charset encoding) throws ResourceNotFoundException, ResourceLoadException
	{
		name = ltrimstr(name, "/");
		
		Package p = c.getPackage();
		String path = "/";
		if (p != null)
			path += p.getName().replace('.', '/')+"/";
		path += name;
		
		return loadTextResource(c, path, encoding);
	}
	
	
	
	
	
	
	
	
	
	@Nonnull
	public static byte[] loadBinaryResource(ResourceClassAndRelpath locator) throws ResourceNotFoundException, ResourceLoadException
	{
		return loadBinaryResource(locator.getBaseClass(), locator.getPathRelativeToClassFile());
	}
	
	@Nonnull
	public static byte[] loadBinaryResource(Class c, String name) throws ResourceNotFoundException, ResourceLoadException
	{
		InputStream in = c.getResourceAsStream(name);
		if (in == null)
			throw new ResourceNotFoundException(c, name);
		
		try
		{
			return JRECompatIOUtilities.readAll(in);
		}
		catch (IOException exc)
		{
			throw new ResourceLoadException(c, name, exc);
		}
		finally
		{
			closeWithoutError(in);
		}
	}
	
	
	
	
	
	
	@Nonnull
	public static String loadTextResource(ResourceClassAndRelpath locator, Charset encoding) throws ResourceNotFoundException, ResourceLoadException
	{
		return loadTextResource(locator.getBaseClass(), locator.getPathRelativeToClassFile(), encoding);
	}
	
	@Nonnull
	public static String loadTextResource(Class c, String name, Charset encoding) throws ResourceNotFoundException, ResourceLoadException
	{
		InputStream in = c.getResourceAsStream(name);
		if (in == null)
			throw new ResourceNotFoundException(c, name);
		
		try
		{
			return TextIOUtilities.readAllText(in, encoding);
		}
		catch (IOException exc)
		{
			throw new ResourceLoadException(c, name, exc);
		}
		finally
		{
			closeWithoutError(in);
		}
	}
	
	
	
	@Nonnull
	public static byte[] loadBinaryResource(File file) throws ResourceNotFoundException, ResourceLoadException
	{
		if (!FSUtilities.lexists(file))
			throw new ResourceNotFoundException(file.getAbsolutePath());
		
		try
		{
			return FSIOUtilities.readAll(file);
		}
		catch (IOException exc)
		{
			throw new ResourceLoadException(file.getAbsolutePath(), exc);
		}
	}
	
	
	@Nonnull
	public static String loadTextResource(File file, Charset encoding) throws ResourceNotFoundException, ResourceLoadException
	{
		if (!FSUtilities.lexists(file))
			throw new ResourceNotFoundException(file.getAbsolutePath());
		
		try
		{
			return FSIOUtilities.readAllText(file, encoding);
		}
		catch (IOException exc)
		{
			throw new ResourceLoadException(file.getAbsolutePath(), exc);
		}
	}
	
	
	
	
	
	
	
	//	public static <R> ResourceCache<R> createDefaultResourceCacheSetup(UncachedResourceLoader<R> loader)
	//	{
	//		/*
	//		 * + A map to cache the handles (which themselves have the potential to be even better caches)
	//		 * + A producer of wrapped handles which checks for changes in the filesystem last-modified date and reloads only then (note that this passes through handles if URI is not local)
	//		 * Todo a time-based periodic reloader
	//		 * + A simple handle-based cacher (relying on forceReload() to reload from the uncached handle)
	//		 * + The adapter to the actual uncached loader
	//		 */
	//		return new HandleCachingResourceCache<R>(new LocalFSTimestampCheckingResourceCache<R>(new StandardResourceCache<R>(new UncachedResourceCacheAdapter<R>(loader))));
	//	}
	//
	//	/**
	//	 * @param autoreload if <code>false</code>, don't ever reload the resources.
	//	 */
	//	public static <R> ResourceCache<R> createDefaultResourceCacheSetup(UncachedResourceLoader<R> loader, boolean autoreload)
	//	{
	//		if (autoreload)
	//			return createDefaultResourceCacheSetup(loader);
	//		else
	//			return new PermanentResourceCache<R>(new UncachedResourceCacheAdapter<R>(loader));
	//	}
	
	
	
	
	
	
	
	
	
	public static BufferedImage loadStandardImageResource(Class c, String resourceName) throws ResourceNotFoundException, ResourceLoadException
	{
		InputStream ii = c.getResourceAsStream(resourceName);
		
		if (ii == null)
			throw new ResourceNotFoundException(c, resourceName);
		
		try (InputStream i = ii)
		{
			return ImageIO.read(i);
		}
		catch (IOException exc)
		{
			throw new ResourceLoadException(c, resourceName);
		}
	}
	
	
	public static BufferedImage loadStandardImageResource(File file) throws ResourceNotFoundException, ResourceLoadException
	{
		if (!file.isFile())
			throw new ResourceNotFoundException(file.getAbsolutePath());
		
		try
		{
			return ImageIO.read(file);
		}
		catch (IOException exc)
		{
			throw new ResourceLoadException(file.getAbsolutePath());
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static ZipEntry[] readAllZipEntriesFromZipfile(URL url) throws IOException
	{
		InputStream in;
		try
		{
			in = url.openStream();
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
		
		try
		{
			return readAllZipEntriesFromZipfile(in);
		}
		finally
		{
			in.close();
		}
	}
	
	
	public static ZipEntry[] readAllZipEntriesFromZipfile(InputStream in) throws IOException
	{
		ZipInputStream jardecoder;
		{
			try
			{
				jardecoder = new ZipInputStream(in);
			}
			catch (Throwable t)
			{
				try
				{
					in.close();
				}
				catch (Throwable t2)
				{
					t.addSuppressed(t2);
				}
				
				ExceptionUtilities.throwGeneralThrowableAttemptingUnverifiedThrow(t);
				throw new UnreachableCodeException();
			}
		}
		
		
		
		
		List<ZipEntry> entries = new ArrayList<>();
		
		try
		{
			while (true)
			{
				ZipEntry e = jardecoder.getNextEntry();
				
				if (e == null)
					break;
				
				entries.add(e);
			}
		}
		finally
		{
			jardecoder.close();
		}
		
		return entries.toArray(new ZipEntry[entries.size()]);
	}
	
	
	
	
	
	
	
	
	
	protected static final Map<URI, List<ZipEntry>> staticDecodedZips = new HashMap<>();
	
	
	/**
	 * Nicely globally cached for quickly (after the first run!) scanning through zip files we expect not to change under our noses!  (*cough* jar files on the classpath *cough*  xD )
	 */
	public static synchronized List<ZipEntry> getStaticZipEntries(URI zipfileURI)
	{
		List<ZipEntry> entries = staticDecodedZips.get(zipfileURI);
		
		if (entries == null)
		{
			URL url;
			try
			{
				url = zipfileURI.toURL();
			}
			catch (MalformedURLException exc)
			{
				throw new WrappedThrowableRuntimeException(exc);
			}
			
			ZipEntry[] a;
			try
			{
				a = readAllZipEntriesFromZipfile(url);
			}
			catch (IOException exc)
			{
				throw new WrappedThrowableRuntimeException(exc);
			}
			
			entries = Collections.unmodifiableList(Arrays.asList(a));
			
			staticDecodedZips.put(zipfileURI, entries);
		}
		
		return entries;
	}
	
	
	
	
	
	public static boolean isDirectoryTypeResourceURL(URL url)
	{
		if (url.getPath().endsWith("/"))
		{
			return true;
		}
		else
		{
			if (url.getProtocol().equalsIgnoreCase("file"))
			{
				try
				{
					File f = new File(url.toURI());
					return f.isDirectory();
				}
				catch (URISyntaxException exc)
				{
					throw new IllegalArgumentException(exc);
				}
			}
			else if (url.getProtocol().equalsIgnoreCase("jar"))
			{
				URI jarfileURI;
				String entryInJarfile;
				String entryInJarfileWithTrailingSlash;
				{
					try
					{
						jarfileURI = ResourceUtilities.getJarURLContainerURL(url).toURI();
						entryInJarfile = ResourceUtilities.getJarURLEntry(url);
					}
					catch (MalformedURLException exc)
					{
						throw new IllegalArgumentException(exc);
					}
					catch (URISyntaxException exc)
					{
						throw new IllegalArgumentException(exc);
					}
					
					entryInJarfile = trim(entryInJarfile, '/');
					entryInJarfileWithTrailingSlash = entryInJarfile + '/';
				}
				
				
				for (ZipEntry e : getStaticZipEntries(jarfileURI))
				{
					String n = rtrim(e.getName(), '/');
					
					if (BasicObjectUtilities.eq(n, entryInJarfile))
					{
						return e.isDirectory();
					}
					else if (n.startsWith(entryInJarfileWithTrailingSlash))
					{
						return true;
					}
				}
				
				throw new IllegalArgumentException("The given entry "+repr(entryInJarfile)+" was not explicitly found as an entry (or implied as a parent directory of other entries) in the jarfile "+repr(jarfileURI.toString())+"     (perhaps the jar file needs to be created with actual entries for the directories?? ie, the opposite of 'zip --no-dir-entries')");
			}
			else
			{
				throw new UnsupportedOptionException("Currently we can't tell if '"+url.getProtocol()+":' URLs are directories or not (or if that concept even makes sense for that protocol/scheme! xD'' )");
			}
		}
	}
}
