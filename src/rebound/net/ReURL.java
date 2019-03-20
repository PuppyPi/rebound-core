package rebound.net;

import static java.util.Objects.*;
import static rebound.net.NetworkUtilities.*;
import java.net.URI;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import rebound.annotations.semantic.operationspecification.HashableType;
import rebound.exceptions.TextSyntaxException;

//TODO Waittt shouuuuuuld we descape/escape the parts???
//I like doing that because it considers '%20', '+', and ' ' to be equivalent during parsing, which, afaik, everything on the web does too X3
//NOPE; IT TOTALLY BREAKS THE FORM-ENCODED QUERY STRING X"DDDD

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
		
		if (!path.startsWith("/"))
			throw new IllegalArgumentException("URL paths must start with '/'");
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
			throw TextSyntaxException.inst("Missing \"://\"   "+url);
		
		int e = url.indexOf('/', a+3);
		if (e == -1)
			throw TextSyntaxException.inst("Missing \"/\" after protocol delimiter   "+url);
		
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
		
		
		
		@Nonnull String _protocol = url.substring(0, a);
		@Nullable String _user = c == -1 ? null : url.substring(a+3, b == -1 ? c : b);
		@Nullable String _password = b == -1 ? null : url.substring(b+1, c);   //b != -1  ⇒  c != -1
		@Nonnull String _host = c == -1 ? url.substring(a+3, d == -1 ? e : d) : url.substring(c+1, d == -1 ? e : d);
		@Nullable String _port = d == -1 ? null : url.substring(d+1, e);
		@Nonnull String _path = url.substring(e, f == -1 ? (g == -1 ? url.length() : g) : f);  //note (e) instead of (e+1) to keep the leading slash in the path :>
		@Nullable String _query = f == -1 ? null : url.substring(f+1, g == -1 ? url.length() : g);
		@Nullable String _fragment = g == -1 ? null : url.substring(g+1, url.length());
		
		@Nonnull String protocol = urldescape(_protocol);
		@Nullable String user = _user == null ? null : urldescape(_user);
		@Nullable String password = _password == null ? null : urldescape(_password);
		@Nonnull String host = urldescape(_host);
		
		@Nullable Integer port;
		try
		{
			port = _port == null ? null : Integer.parseInt(urldescape(_port));
		}
		catch (NumberFormatException exc)
		{
			throw TextSyntaxException.inst("Invalid port: "+_port, exc);
		}
		
		@Nonnull String path = urldescape(_path);
		@Nullable String query = _query == null ? null : urldescape(_query);
		@Nullable String fragment = _fragment == null ? null : urldescape(_fragment);
		
		
		
		return new ReURL(protocol, user, password, host, port, path, query, fragment);
	}
	
	
	
	
	
	
	public String serializeMinimalEscaping()
	{
		StringBuilder b = new StringBuilder();
		
		b.append(urlescapeMinimallyPartProtocol(protocol));
		b.append("://");
		
		if (user != null)
		{
			b.append(urlescapeMinimallyPartUser(user));
			
			if (password != null)
			{
				b.append(':');
				b.append(urlescapeMinimallyPartPassword(password));
			}
			
			b.append('@');
		}
		
		b.append(urlescapeMinimallyPartHost(host));
		
		if (port != null)
		{
			b.append(':');
			b.append(port);  //Base 10 integer; doesn't need to be escaped :>
		}
		
		b.append(urlescapeMinimallyPartPath(path));  //contains the leading slash :3
		
		if (query != null)
		{
			b.append('?');
			b.append(urlescapeMinimallyPartQuery(query));
		}
		
		if (fragment != null)
		{
			b.append('#');
			b.append(urlescapeMinimallyPartFragment(fragment));
		}
		
		return b.toString();
	}
	
	
	
	
	public String serializeMaximalEscaping()
	{
		StringBuilder b = new StringBuilder();
		
		b.append(urlescapeMaximallyPartNonPath(protocol));
		b.append("://");
		
		if (user != null)
		{
			b.append(urlescapeMaximallyPartNonPath(user));
			
			if (password != null)
			{
				b.append(':');
				b.append(urlescapeMaximallyPartNonPath(password));
			}
			
			b.append('@');
		}
		
		b.append(urlescapeMaximallyPartNonPath(host));
		
		if (port != null)
		{
			b.append(':');
			b.append(port);  //Base 10 :>
		}
		
		b.append(urlescapeFullyPartPath(path));  //contains the leading slash :3
		
		if (query != null)
		{
			b.append('?');
			b.append(urlescapeMaximallyPartNonPath(query));
		}
		
		if (fragment != null)
		{
			b.append('#');
			b.append(urlescapeMaximallyPartNonPath(fragment));
		}
		
		return b.toString();
	}
	
	
	
	
	
	
	@Override
	public String toString()
	{
		return serializeMinimalEscaping();
	}
}
