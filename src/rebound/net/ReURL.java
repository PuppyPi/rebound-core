package rebound.net;

import static java.util.Objects.*;
import static rebound.net.NetworkUtilities.*;
import static rebound.text.StringUtilities.*;
import java.net.URI;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import rebound.annotations.semantic.operationspecification.HashableType;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.TextSyntaxException;
import rebound.file.FSUtilities;

//TO DO Waittt shouuuuuuld we descape/escape the parts???
//I like doing that because it considers '%20', '+', and ' ' to be equivalent during parsing, which, afaik, everything on the web does too X3
//NOPE; IT TOTALLY BREAKS THE FORM-ENCODED QUERY STRING OF COURSE X"DDDD
//AND PATHS CAN'T HAVE "/" ESCAPED, BUT OTHER PARTS PROBABLY SHOULD (HOST *HAS* TO)!

/**
 * Re-implementation of {@link URI} (except for URL's, specifically) because it chokes on domain names with underscores—
 * Oh sorry
 * "Host names"
 * Pssht XD
 * 
 * For example:
 * 		http://vitaly_filatov.tripod.com/ng/asm/asm_001.9.html exists though, and browsers support it!
 */
@Immutable
@HashableType
public class ReURL
{
	protected final @Nonnull String protocol;
	protected final @Nullable String user;
	protected final @Nullable String password;
	protected final @Nonnull String host;
	protected final @Nullable Integer port;
	protected final @Nonnull String path;
	protected final @Nullable String query;
	protected final @Nullable String fragment;
	
	
	public ReURL(String protocol, String user, String password, String host, Integer port, String path, String query, String fragment)
	{
		this.protocol = requireNonNull(protocol);
		this.user = user;
		this.password = password;
		this.host = requireNonNull(host);
		this.port = port;
		this.path = requireNonNull(path);
		this.query = query;
		this.fragment = fragment;
		
		if (user == null && password != null)
			throw new NullPointerException();
		
		if (path.isEmpty())  //be lenient :3    (I've seen URLs (eg, "http://www.linear.com/docs/41280") that redirect through HTTP 30x to these kind of pathless urls (eg, "https://investor.analog.com") so this is used in the wild inside web servers, not just html pages X'D )
			path = "/";
		else if (!path.startsWith("/"))
			throw new IllegalArgumentException("URL paths must start with '/'");
		
		
		
		
		if (!isValidURLPartProtocol(protocol))
			throw new IllegalArgumentException("Invalid protocol!: "+repr(protocol));
		
		if (user != null && !isValidURLPartUser(user))
			throw new IllegalArgumentException("Invalid user!: "+repr(user));
		
		if (password != null && !isValidURLPartPassword(password))
			throw new IllegalArgumentException("Invalid password!: "+repr(password));
		
		if (!isValidURLPartHost(host))
			throw new IllegalArgumentException("Invalid host!: "+repr(host));
		
		if (!isValidURLPartPath(path))
			throw new IllegalArgumentException("Invalid path!: "+repr(path));
		
		if (query != null && !isValidURLPartQuery(query))
			throw new IllegalArgumentException("Invalid query!: "+repr(query));
		
		if (fragment != null && !isValidURLPartFragment(fragment))
			throw new IllegalArgumentException("Invalid fragment!: "+repr(fragment));
	}
	
	
	
	
	
	
	public String getProtocol()
	{
		return protocol;
	}
	
	public String getUser()
	{
		return user;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public String getHost()
	{
		return host;
	}
	
	public Integer getPort()
	{
		return port;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public String getQuery()
	{
		return query;
	}
	
	public String getFragment()
	{
		return fragment;
	}
	
	
	
	public String getUserInfo()
	{
		return this.getUser() == null ? null : (this.getUser()+(this.getPassword() == null ? "" : ":"+this.getPassword()));
	}
	
	
	
	
	
	public ReURL withDifferentProtocol(String newProtocol)
	{
		return new ReURL(newProtocol, this.getUser(), this.getPassword(), this.getHost(), this.getPort(), this.getPath(), this.getQuery(), this.getFragment());
	}
	
	public ReURL withDifferentUser(String newUser)
	{
		return new ReURL(this.getProtocol(), newUser, this.getPassword(), this.getHost(), this.getPort(), this.getPath(), this.getQuery(), this.getFragment());
	}
	
	public ReURL withDifferentPassword(String newPassword)
	{
		return new ReURL(this.getProtocol(), this.getUser(), newPassword, this.getHost(), this.getPort(), this.getPath(), this.getQuery(), this.getFragment());
	}
	
	public ReURL withDifferentHost(String newHost)
	{
		return new ReURL(this.getProtocol(), this.getUser(), this.getPassword(), newHost, this.getPort(), this.getPath(), this.getQuery(), this.getFragment());
	}
	
	public ReURL withDifferentPort(Integer newPort)
	{
		return new ReURL(this.getProtocol(), this.getUser(), this.getPassword(), this.getHost(), newPort, this.getPath(), this.getQuery(), this.getFragment());
	}
	
	public ReURL withDifferentPath(String newPath)
	{
		return new ReURL(this.getProtocol(), this.getUser(), this.getPassword(), this.getHost(), this.getPort(), newPath, this.getQuery(), this.getFragment());
	}
	
	public ReURL withDifferentQuery(String newQuery)
	{
		return new ReURL(this.getProtocol(), this.getUser(), this.getPassword(), this.getHost(), this.getPort(), this.getPath(), newQuery, this.getFragment());
	}
	
	public ReURL withDifferentFragment(String newFragment)
	{
		return new ReURL(this.getProtocol(), this.getUser(), this.getPassword(), this.getHost(), this.getPort(), this.getPath(), this.getQuery(), newFragment);
	}
	
	
	
	
	public static String resolvePossiblyRelativeURLEncoded(String baseURL, String urlPossiblyRelativeToBase) throws TextSyntaxException
	{
		return resolvePossiblyRelativeURLToEncoded(parse(baseURL), urlPossiblyRelativeToBase);
	}
	
	public static String resolvePossiblyRelativeURLToEncoded(ReURL baseURL, String urlPossiblyRelativeToBase)
	{
		if (urlPossiblyRelativeToBase.startsWith("://"))
		{
			return baseURL.getProtocol()+urlPossiblyRelativeToBase;
		}
		else if (urlPossiblyRelativeToBase.startsWith("//"))
		{
			return baseURL.getProtocol()+':'+urlPossiblyRelativeToBase;
		}
		else if (urlPossiblyRelativeToBase.contains("://"))
		{
			//It's fine as it is :3
			return urlPossiblyRelativeToBase;
		}
		else if (urlPossiblyRelativeToBase.startsWith("/"))
		{
			//Absolute url! :D
			// (note that it might still have a query string!)
			
			String b = new ReURL(baseURL.getProtocol(), baseURL.getUser(), baseURL.getPassword(), baseURL.getHost(), baseURL.getPort(), "/", null, null).serialize();
			
			if (!b.endsWith("/"))
				throw new ImpossibleException(repr(baseURL.serialize()));
			b = b.substring(0, b.length()-1);  //trim off the trailing '/'
			
			/*
			 * Example:
			 *		"https://katalog.we-online.de/pbs/download/STEP-760308111-rev1.stp"  ->  "https://katalog.we-online.com/pbs/download/760308111 (rev1).stp"			@ 2019-08-23 12:14:47 z
			 */
			
			return b + urlPossiblyRelativeToBase;
		}
		else
		{
			//Relative url! :>
			// (note that it might still have a query string!)
			
			String p = baseURL.getPath();
			
			String dp = rsplitonceReturnPrecedingOrNull(p, '/');
			
			if (dp == null)
				throw new ImpossibleException("Invalid URL should have been caught in parsing/constructing!!: "+repr(baseURL.serialize()));
			
			String newPath = FSUtilities.normpathPosix(dp+'/'+urlPossiblyRelativeToBase);
			
			String b = new ReURL(baseURL.getProtocol(), baseURL.getUser(), baseURL.getPassword(), baseURL.getHost(), baseURL.getPort(), "/", null, null).serialize();
			
			if (!b.endsWith("/"))
				throw new ImpossibleException(repr(baseURL.serialize()));
			b = b.substring(0, b.length()-1);  //trim off the trailing '/'
			
			return b + newPath;
		}
	}
	
	public static ReURL resolvePossiblyRelativeURL(ReURL baseURL, String urlPossiblyRelativeToBase) throws TextSyntaxException
	{
		return parse(resolvePossiblyRelativeURLToEncoded(baseURL, urlPossiblyRelativeToBase));
	}
	
	public ReURL resolvePossiblyRelativeURL(String urlPossiblyRelativeToThis)
	{
		return resolvePossiblyRelativeURL(this, urlPossiblyRelativeToThis);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fragment == null) ? 0 : fragment.hashCode());
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((port == null) ? 0 : port.hashCode());
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		result = prime * result + ((query == null) ? 0 : query.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReURL other = (ReURL) obj;
		if (fragment == null)
		{
			if (other.fragment != null)
				return false;
		}
		else if (!fragment.equals(other.fragment))
			return false;
		if (host == null)
		{
			if (other.host != null)
				return false;
		}
		else if (!host.equals(other.host))
			return false;
		if (password == null)
		{
			if (other.password != null)
				return false;
		}
		else if (!password.equals(other.password))
			return false;
		if (path == null)
		{
			if (other.path != null)
				return false;
		}
		else if (!path.equals(other.path))
			return false;
		if (port == null)
		{
			if (other.port != null)
				return false;
		}
		else if (!port.equals(other.port))
			return false;
		if (protocol == null)
		{
			if (other.protocol != null)
				return false;
		}
		else if (!protocol.equals(other.protocol))
			return false;
		if (query == null)
		{
			if (other.query != null)
				return false;
		}
		else if (!query.equals(other.query))
			return false;
		if (user == null)
		{
			if (other.user != null)
				return false;
		}
		else if (!user.equals(other.user))
			return false;
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static ReURL parse(String url) throws TextSyntaxException
	{
		/*
		 *    http://user:password@hostname.com:8080/path?query#fragment
		 *        a     [b]      [c]          [d]   e   [f]   [g]
		 */
		
		int a = url.indexOf("://");
		if (a == -1)
			throw TextSyntaxException.inst("Missing \"://\"   "+repr(url));
		
		int e = url.indexOf('/', a+3);
		if (e == -1)
			throw TextSyntaxException.inst("Missing \"/\" after protocol delimiter   "+repr(url));
		
		int c = url.indexOf('@', a+3);
		if (c != -1 && c > e)  //&& e != -1
			c = -1;
		
		int b;
		{
			if (c == -1)
			{
				b = -1;
			}
			else
			{
				b = url.indexOf(':', a+3);
				if (b != -1 && b > c)  //&& c != -1
					b = -1;
			}
		}
		
		int d = url.indexOf(':', c == -1 ? a+3 : c+1);  //Important since ':' may appear in the UserInfo!
		if (d != -1 && d > e)  //&& e != -1
			d = -1;
		
		int g = url.indexOf('#', e+1);
		
		int f = url.indexOf('?', e+1);
		if (f != -1 && g != -1 && f > g)
			f = -1;
		
		
		
		@Nonnull String protocol = url.substring(0, a);
		@Nullable String user = c == -1 ? null : url.substring(a+3, b == -1 ? c : b);
		@Nullable String password = b == -1 ? null : url.substring(b+1, c);   //b != -1  ⇒  c != -1
		@Nonnull String host = c == -1 ? url.substring(a+3, d == -1 ? e : d) : url.substring(c+1, d == -1 ? e : d);
		@Nullable String _port = d == -1 ? null : url.substring(d+1, e);
		@Nonnull String path = url.substring(e, f == -1 ? (g == -1 ? url.length() : g) : f);  //note (e) instead of (e+1) to keep the leading slash in the path :>
		@Nullable String query = f == -1 ? null : url.substring(f+1, g == -1 ? url.length() : g);
		@Nullable String fragment = g == -1 ? null : url.substring(g+1, url.length());
		
		@Nullable Integer port;
		try
		{
			port = _port == null ? null : Integer.parseInt(urldescape(_port));
		}
		catch (NumberFormatException exc)
		{
			throw TextSyntaxException.inst("Invalid port: "+repr(_port), exc);
		}
		
		try
		{
			return new ReURL(protocol, user, password, host, port, path, query, fragment);
		}
		catch (IllegalArgumentException exc)
		{
			throw TextSyntaxException.inst(exc);
		}
	}
	
	
	
	
	
	
	public String serialize()
	{
		StringBuilder b = new StringBuilder();
		
		b.append(protocol);
		b.append("://");
		
		if (user != null)
		{
			b.append(user);
			
			if (password != null)
			{
				b.append(':');
				b.append(password);
			}
			
			b.append('@');
		}
		
		b.append(host);
		
		if (port != null)
		{
			b.append(':');
			b.append(port);  //Base 10 integer; doesn't need to be escaped :>
		}
		
		b.append(path);  //contains the leading slash :3
		
		if (query != null)
		{
			b.append('?');
			b.append(query);
		}
		
		if (fragment != null)
		{
			b.append('#');
			b.append(fragment);
		}
		
		return b.toString();
	}
	
	public String serializeToASCII()
	{
		return urlescapeJustUnicodesAndControls(this.serialize());
	}
	
	
	
	
	
	@Override
	public String toString()
	{
		return serialize();
	}
}
