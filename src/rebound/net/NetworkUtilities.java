/*
 * Created on Apr 28, 2008
 * 	by the wonderful Eclipse(c)
 */
package rebound.net;

import static rebound.bits.Unsigned.*;
import static rebound.text.StringUtilities.*;
import static rebound.util.collections.ArrayUtilities.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import rebound.bits.Bytes;
import rebound.bits.Unsigned;
import rebound.exceptions.AddressLengthException;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.NotYetImplementedException;
import rebound.exceptions.TextSyntaxCheckedException;
import rebound.exceptions.TextSyntaxException;
import rebound.text.StringUtilities;
import rebound.util.BasicExceptionUtilities;
import rebound.util.functional.FunctionalInterfaces.UnaryFunctionIntToBoolean;
import rebound.util.objectutil.JavaNamespace;

public class NetworkUtilities
implements JavaNamespace
{
	/**
	 * Tests whether or not this IP address represents an IPv4 address.<br>
	 */
	public static boolean isIPv4(byte[] addr)
	{
		if (addr.length == 4)
		{
			return true;
		}
		else if (addr.length == 16)
		{
			return
			(
			addr[0] == 0 &&
			addr[1] == 0 &&
			addr[2] == 0 &&
			addr[3] == 0 &&
			addr[4] == 0 &&
			addr[5] == 0 &&
			addr[6] == 0 &&
			addr[7] == 0 &&
			addr[8] == 0 &&
			addr[9] == 0 &&
			addr[10] == (byte)0xFF &&
			addr[11] == (byte)0xFF
			);
		}
		else
		{
			throw new AddressLengthException();
		}
	}
	
	
	/**
	 * This removes the ambiguity between 4-Byte arrays (IPv4) and 16-Byte arrays (IPv6) that are mapped to IPv4 addresses.<br>
	 * <br>
	 * If <code>toV6</code> is <code>true</code>, then any IPv4 address is converted to the equivalent IPv6, 16-Byte array.<br>
	 * If <code>toV6</code> is <code>false</code>, then IPv4 mapped IPv6 addresses will be converted into 4-Byte arrays, otherwise nothing will be done to <code>src</code>.<br>
	 */
	public static byte[] canonicalizeIP(byte[] src, boolean toV6)
	{
		if (toV6)
		{
			if (src.length == 4)
			{
				byte[] v6 = new byte[16];
				//0-9] = 0;
				v6[10] = (byte)0xFF;
				v6[11] = (byte)0xFF;
				v6[12] = src[0];
				v6[13] = src[1];
				v6[14] = src[2];
				v6[15] = src[3];
				return v6;
			}
			else if (src.length == 16)
			{
				return src;
			}
			else
			{
				throw new AddressLengthException(src.length);
			}
		}
		
		
		
		else
		{
			if (src.length == 4)
			{
				return src;
			}
			else if (src.length == 16)
			{
				if (isIPv4(src))
				{
					byte[] v4 = new byte[4];
					v4[0] = src[12];
					v4[1] = src[13];
					v4[2] = src[14];
					v4[3] = src[15];
					return v4;
				}
				else
				{
					return src;
				}
			}
			else
			{
				throw new AddressLengthException(src.length);
			}
		}
	}
	
	
	
	
	
	
	
	/**
	 * This parses a textual representation of an IP address into the binary form.<br>
	 * If the string is of an IPv4 address, the array will be 4-byte.<br>
	 * Otherwise, even if it is an IPv4 address mapped to IPv6, the array will be 16-byte.<br>
	 */
	public static byte[] parseIP(@Nonnull String str) throws TextSyntaxCheckedException
	{
		if (str == null)
			throw new NullPointerException();
		
		//Eg: 0.0.0.0
		if (str.length() < 7)
			throw TextSyntaxCheckedException.inst("IP address is too small to possibly be valid.");
		
		byte[] addr = null;
		
		boolean v6 = str.indexOf(':') != -1;
		boolean dotted = str.indexOf('.') != -1;
		
		if (v6)
		{
			addr = new byte[16];
			
			//TODO
			throw new NotYetImplementedException();
		}
		
		
		
		else
		{
			addr = new byte[4];
			
			if (dotted)
			{
				int index = 0;
				for (int i = 0; i < 4; i++)
				{
					if (str.length() <= index || str.charAt(index) == '.')
						throw TextSyntaxCheckedException.inst("IP Address has a dot with nothing following it!");
					
					byte b = 0;
					{
						//Hex?
						if (str.length() > index + 1 && str.charAt(index) == '0' && str.charAt(index+1) == 'x')
						{
							//Hex
							
							//Skip the '0x'
							index += 2;
							
							if (str.length() <= index)
								throw TextSyntaxCheckedException.inst("IP Address has a 0x, but nothing after it!");
							
							b = digit(str.charAt(index), 16);
							
							index++;
							
							if (str.length() > index)
							{
								b <<= 4;
								b += digit(str.charAt(index), 16);
								index++;
							}
						}
						
						
						
						//Octal?
						else if
						(
						(
						str.length() > index + 4
						&&
						(
						//0000.	Octal
						//00.0.	Decimal
						//0.00.	Decimal
						str.charAt(index+4) == '.' &&
						str.charAt(index+1) != '.' &&
						str.charAt(index+2) != '.'
						)
						)
						||
						(
						//0000$	Octal
						//00.0$	Decimal
						//0.00$	Decimal
						str.length() == index + 4
						&&
						(
						str.charAt(index+1) != '.' &&
						str.charAt(index+2) != '.'
						)
						)
						)
						{
							//Octal.
							
							//Skip the prefixing '0'
							b = (byte)(digit(str.charAt(index+1), 8) * 64);
							b += digit(str.charAt(index+2), 8) * 8;
							b += digit(str.charAt(index+3), 8);
							
							index += 4;
						}
						
						
						
						//Decimal?
						else
						{
							b = digit(str.charAt(index), 10);
							index++;
							
							if (str.length() > index && str.charAt(index) != '.')
							{
								b *= 10;
								b += digit(str.charAt(index), 10);
								index++;
							}
							
							if (str.length() > index && str.charAt(index) != '.')
							{
								b *= 10;
								b += digit(str.charAt(index), 10);
								index++;
							}
						}
						
						index++;
					}
					
					
					addr[i] = b;
				}
			}
			else
			{
				//Eg: 0xC00002EB == 192.0.2.235
				
				int addrValue = 0;
				
				try
				{
					addrValue = Integer.parseInt(str);
				}
				catch (NumberFormatException exc)
				{
					throw TextSyntaxCheckedException.inst("Invalid raw IP address");
				}
				
				Bytes.putBigInt(addr, 0, addrValue);
			}
		}
		
		return addr;
	}
	
	private static byte digit(char c, int radix) throws TextSyntaxCheckedException
	{
		int v = Character.digit(c, radix);
		if (v == -1)
			throw TextSyntaxCheckedException.inst(c+" is not a valid digit in base "+radix);
		return (byte)v;
	}
	
	
	
	
	
	/**
	 * This formats a binary ip address into a textual representation in one of 10 possible formats.<br>
	 * <br>
	 * <br>
	 * For 127.0.0.1 as an example, these are the possible formats:<br>
	 * <br>
	 * As IPv4 (<code>v4=true</code>):<br>
	 * <table>
	 * <tr><th>Dotted</th><th>Leading Zeros</th><th>Text</th></tr>
	 * <tr><td><code>false</code></td><td><code>false</code></td> <td>127.0.0.1</td></tr>
	 * <tr><td><code>false</code></td><td><code>true</code></td> <td>127.000.000.1</td></tr>
	 * <tr><td><code>true</code></td><td><code>false</code></td> <td>2130706433</td></tr>
	 * <tr><td><code>true</code></td><td><code>true</code></td> <td>2130706433</td></tr>
	 * </table>
	 * <br>
	 * As IPv6 (<code>v4=false</code>):<br>
	 * <table>
	 * 	<tr><th>Compact</th><th>Leading Zeros</th><th>Dotted</th><th>Text</th></tr>
	 * 	<tr><td><code>true</code></td><td><code>false</code></td><td><code>false</code></td><td>::ffff:7f00:1</td></tr>
	 * 	<tr><td><code>true</code></td><td><code>false</code></td><td><code>true</code></td><td>::ffff:127.0.0.1</td></tr>
	 * 	<tr><td><code>true</code></td><td><code>true</code></td><td><code>false</code></td><td>::ffff:7f00:0001</td></tr>
	 * 	<tr><td><code>true</code></td><td><code>true</code></td><td><code>true</code></td><td>::ffff:127.000.000.001</td></tr>
	 * 	<tr><td><code>false</code></td><td><code>false</code></td><td><code>false</code></td><td>0:0:0:0:0:ffff:7f00:1</td></tr>
	 * 	<tr><td><code>false</code></td><td><code>false</code></td><td><code>true</code></td><td>0:0:0:0:0:ffff:127.0.0.1</td></tr>
	 * 	<tr><td><code>false</code></td><td><code>true</code></td><td><code>false</code></td><td>0000:0000:0000:0000:0000:ffff::7f00:0001</td></tr>
	 * 	<tr><td><code>false</code></td><td><code>true</code></td><td><code>true</code></td><td>0000:0000:0000:0000:0000:ffff::127.000.000.001</td></tr>
	 * </table>
	 */
	public static String formatIP
	(
	byte[] ip,
	
	//<Format
	boolean v4,
	boolean compact,
	boolean dotted,
	boolean leadingZeros
	//Format>
	)
	throws NullPointerException
	
	//TODO Add a Radix parameter (which only works for v4 || dotted)
	{
		StringBuilder buff = new StringBuilder();
		
		if (v4)
		{
			byte[] ip4 = canonicalizeIP(ip, false);
			
			if (ip4.length != 4)
				throw new IllegalArgumentException("Cannot convert a non-mapped IPv6 address to an IPv4.");
			
			if (!dotted)
				//TODO Make dotted==false apply to IPv4 addresses
				throw new NotYetImplementedException();
			
			//Do the formatting
			for (int i = 0; i < 4; i++)
			{
				String dec = StringUtilities.toStringU8(ip4[i]);
				
				if (leadingZeros)
				{
					int missing = 3 - dec.length();
					for (int e = 0; e < missing; e++)
						buff.append('0');
				}
				
				buff.append(dec);
				
				if (i < 3)
					buff.append('.');
			}
		}
		
		
		
		else
		{
			byte[] ip6 = canonicalizeIP(ip, true);
			
			boolean useDots = dotted && isIPv4(ip);
			
			//Calculate the (first) best place to compact
			int best_zerostrech_start = -1;
			int best_zerostrech_size = 0;
			{
				if (compact)
				{
					int this_zerostrech_start = 0;
					
					int len = useDots ? 6 : 8;
					
					for (int i = 0; i < len+1; i++)
					{
						if (i < len && ip6[i*2] == 0 && ip6[i*2+1] == 0)
						{
							if (this_zerostrech_start == -1)
							{
								this_zerostrech_start = i;
							}
						}
						else
						{
							if (this_zerostrech_start != -1)
							{
								if ((i - this_zerostrech_start) > best_zerostrech_size)
								{
									//Replace the best with the this
									best_zerostrech_start = this_zerostrech_start;
									best_zerostrech_size = i - this_zerostrech_start;
								}
								
								this_zerostrech_start = -1;
							}
						}
					}
				}
			}
			
			
			//Do the formatting
			for (int i = 0; i < (useDots ? 6 : 8); i++)
			{
				if (i == best_zerostrech_start) //&& compact
				{
					if (i == 0)
						buff.append(':');
					buff.append(':');
					
					i += best_zerostrech_size-1;
				}
				else
				{
					byte a = ip6[i*2];
					byte b = ip6[i*2+1];
					
					if (a != 0)
					{
						if (Unsigned.lessThanU32(a, 0x10))
						{
							if (leadingZeros)
								buff.append('0');
							buff.append(Character.forDigit(a, 16));
						}
						else
						{
							buff.append(Character.forDigit(a & 0xF0 >>> 4, 16));
							buff.append(Character.forDigit(a & 0x0F, 16));
						}
						
						
						if (Unsigned.lessThanU32(b, 0x10))
						{
							buff.append('0');
							buff.append(Character.forDigit(b, 16));
						}
						else
						{
							buff.append(Character.forDigit(b & 0xF0 >>> 4, 16));
							buff.append(Character.forDigit(b & 0x0F, 16));
						}
					}
					else
					{
						if (leadingZeros)
						{
							buff.append('0');
							buff.append('0');
						}
						
						if (Unsigned.lessThanU32(b, 0x10))
						{
							if (leadingZeros)
								buff.append('0');
							buff.append(Character.forDigit(b, 16));
						}
						else
						{
							buff.append(Character.forDigit(b & 0xF0 >>> 4, 16));
							buff.append(Character.forDigit(b & 0x0F, 16));
						}
					}
					
					if (i < 7)
						buff.append(':');
				}
			}
			
			
			if (useDots)
			{
				//Do the formatting
				for (int i = 12; i < 16; i++)
				{
					String dec = StringUtilities.toStringU8(ip6[i]);
					
					if (leadingZeros)
					{
						int missing = 3 - dec.length();
						for (int e = 0; e < missing; e++)
							buff.append('0');
					}
					
					buff.append(dec);
					
					if (i < 16)
						buff.append('.');
				}
			}
		}
		
		return buff.toString();
	}
	
	
	/**
	 * Formats an IP address in the standard fashion.<br>
	 * Note: 4-byte addresses will be formatted as IPv4, but IPv6 mapped IPv4 addresses will be formatted in the IPv6 fashion (ie ::ffff:f700:1).<br>
	 */
	public static String formatIP(byte[] ip) throws NullPointerException
	{
		if (ip.length == 4)
		{
			return formatIP(ip, true, true, true, false);
		}
		else
		{
			return formatIP(ip, false, true, false, false);
		}
	}
	
	
	
	
	public static boolean isURLAvailable(String url)
	{
		try
		{
			return isURLAvailable(new URL(url));
		}
		catch (MalformedURLException exc)
		{
			return false;
		}
	}
	
	/**
	 * A URL differs principally from a URI in that it encodes how to access the resource;
	 * thus this is testing a URL that happens to be contained in a <code>java.net.URI</code> object.
	 */
	public static boolean isURLAvailable(URI url)
	{
		//Special cases
		if (url.getScheme().equals("file"))
		{
			File f = new File(url);
			return f.isFile();
		}
		
		else
		{
			try
			{
				return isURLAvailable(url.toURL());
			}
			catch (MalformedURLException exc)
			{
				return false;
			}
		}
	}
	
	public static boolean isURLAvailable(URL url)
	{
		//Special cases
		if (url.getProtocol().equals("file"))
		{
			try
			{
				File f = new File(url.toURI());
				return f.isFile();
			}
			catch (URISyntaxException exc)
			{
				return false;
			}
		}
		
		//Generic case
		else
		{
			try
			{
				InputStream in = url.openStream();
				try
				{
					in.close();
				}
				catch (IOException exc)
				{
					//Ignore closing errors; the point is: it exists.
				}
				return true;
			}
			catch (IOException exc)
			{
				return false;
			}
		}
	}
	
	
	
	
	public static URL getFirstAvailable(Iterable<URL> urls)
	{
		for (URL url : urls)
			if (isURLAvailable(url))
				return url;
		return null;
	}
	
	public static List<URL> getAllAvailable(Iterable<URL> urls)
	{
		List<URL> available = new ArrayList<URL>();
		for (URL url : urls)
			if (isURLAvailable(url))
				available.add(url);
		return available;
	}
	
	
	
	
	
	
	public static InetAddress toInetAddress(Object host) throws UnknownHostException
	{
		if (host instanceof InetAddress)
			return (InetAddress)host;
		else if (host instanceof byte[])
			return InetAddress.getByAddress((byte[])host);
		else if (host instanceof String)
			return InetAddress.getByName((String)host);
		else
			throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(host);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static String urldescape(@Nonnull String s) throws TextSyntaxException
	{
		try
		{
			return URLDecoder.decode(s, "UTF-8");
		}
		catch (UnsupportedEncodingException exc)
		{
			throw new ImpossibleException(exc);
		}
		catch (IllegalArgumentException exc)
		{
			throw TextSyntaxException.inst(exc);
		}
	}
	
	public static String urlFormEscape(@Nonnull String s)
	{
		return _urlFormEscape_JRE(s);
	}
	
	public static String _urlFormEscape_JRE(@Nonnull String s)
	{
		try
		{
			return URLEncoder.encode(s, "UTF-8");
		}
		catch (UnsupportedEncodingException exc)
		{
			throw new ImpossibleException();
		}
	}
	
	//Good for testing :>
	public static String _urlFormEscape_Rebound(@Nonnull String s)
	{
		return urlescapeGeneric(s, EscapedCharsFormEncoding);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static final UnaryFunctionIntToBoolean EscapedCharsFormEncoding = c -> !( (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '.' || c == '-' || c == '*' || c == '_' );
	public static final UnaryFunctionIntToBoolean EscapedCharsJustUnicodesAndControls = c -> c <= ' ' || c >= 127;
	
	
	public static String urlescapeGeneric(@Nonnull String s, UnaryFunctionIntToBoolean shouldEscape)
	{
		int[] ucs4Chars = toCodePointArray(s);
		
		StringBuilder b = null;
		
		int n = ucs4Chars.length;
		for (int i = 0; i < n; i++)
		{
			int c = ucs4Chars[i];
			
			if (shouldEscape.f(c))
			{
				if (b == null)
				{
					b = new StringBuilder(s.substring(0, i));
				}
				
				if (c == ' ')
				{
					b.append('+');
				}
				else if (c < 128 && c > 0)
				{
					b.append('%');
					addTwoBEHexChars(c, b);
				}
				else
				{
					byte[] enc = new String(new int[]{c}, 0, 1).getBytes(StandardCharsets.UTF_8);
					
					for (byte encByte : enc)
					{
						b.append('%');
						addTwoBEHexChars(upcast(encByte), b);
					}
				}
			}
			else
			{
				if (b != null)
					b.appendCodePoint(c);
			}
		}
		
		return b == null ? s : b.toString();
	}
	
	
	
	
	/**
	 * This is compatible for use with an already-escaped URL/URI :>
	 */
	public static String urlescapeJustUnicodesAndControls(@Nonnull String s)
	{
		return urlescapeGeneric(s, EscapedCharsJustUnicodesAndControls);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * Protocol    :
	 * User        /@:
	 * Password    /@
	 * Host        /:
	 * Port        /
	 * Path        ?#
	 * Query       #
	 * Fragment    
	 * 
	 * + '%', '+', '\n', '\r' are always escaped X3
	 */
	
	public static final UnaryFunctionIntToBoolean InvalidCharsMinimallyForPartProtocol = c -> c <= ' ' || c ==  ':';
	public static final UnaryFunctionIntToBoolean InvalidCharsMinimallyForPartUser     = c -> c <= ' ' || c ==  '/' || c == '@' || c == ':';
	public static final UnaryFunctionIntToBoolean InvalidCharsMinimallyForPartPassword = c -> c <= ' ' || c ==  '/' || c == '@';
	public static final UnaryFunctionIntToBoolean InvalidCharsMinimallyForPartHost     = c -> c <= ' ' || c ==  '/' || c == ':';
	public static final UnaryFunctionIntToBoolean InvalidCharsMinimallyForPartPath     = c -> c <= ' ' || c ==  '?' || c == '#';
	public static final UnaryFunctionIntToBoolean InvalidCharsMinimallyForPartQuery    = c -> c <= ' ' || c ==  '#';
	public static final UnaryFunctionIntToBoolean InvalidCharsMinimallyForPartFragment = c -> c <= ' ';
	
	
	
	
	public static boolean isValidURLPartProtocol(@Nonnull String s)
	{
		return !forAny(InvalidCharsMinimallyForPartProtocol, toCodePointArray(s));
	}
	
	public static boolean isValidURLPartUser(@Nonnull String s)
	{
		return !forAny(InvalidCharsMinimallyForPartUser, toCodePointArray(s));
	}
	
	public static boolean isValidURLPartPassword(@Nonnull String s)
	{
		return !forAny(InvalidCharsMinimallyForPartPassword, toCodePointArray(s));
	}
	
	public static boolean isValidURLPartHost(@Nonnull String s)
	{
		return !forAny(InvalidCharsMinimallyForPartHost, toCodePointArray(s));
	}
	
	//Port is an integers and thus doesn't need to be escaped XD
	
	public static boolean isValidURLPartPath(@Nonnull String s)
	{
		return !forAny(InvalidCharsMinimallyForPartPath, toCodePointArray(s));
	}
	
	public static boolean isValidURLPartQuery(@Nonnull String s)
	{
		return !forAny(InvalidCharsMinimallyForPartQuery, toCodePointArray(s));
	}
	
	public static boolean isValidURLPartFragment(@Nonnull String s)
	{
		return !forAny(InvalidCharsMinimallyForPartFragment, toCodePointArray(s));
	}
}
