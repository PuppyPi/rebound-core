package rebound.net;

import static java.util.Collections.*;
import static rebound.util.collections.CollectionUtilities.*;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StandardURLProtocolPorts
{
	public static final int HTTP = 80;
	public static final int HTTPS = 80;
	public static final int FTP = 21;
	public static final int FTPS = 989;
	
	public static final Map<String, Integer> DefaultPortsByProtocol = unmodifiableMap(newmap(
	"http", HTTP,
	"https", HTTPS,
	"ftp", FTP,
	"ftps", FTPS
	));
	
	
	public static @Nullable Integer getDefaultPortForProtocol(@Nonnull String protocol)
	{
		return DefaultPortsByProtocol.get(protocol);
	}
}
