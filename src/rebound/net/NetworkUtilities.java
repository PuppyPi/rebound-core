/*
 * Created on Apr 28, 2008
 * 	by the wonderful Eclipse(c)
 */
package rebound.net;

import static rebound.bits.Unsigned.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.text.StringUtilities.*;
import static rebound.util.collections.ArrayUtilities.*;
import static rebound.util.collections.prim.PrimitiveCollections.*;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.Inet6Address;
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
import rebound.bits.DataEncodingUtilities;
import rebound.bits.InvalidInputCharacterException;
import rebound.bits.Unsigned;
import rebound.exceptions.AddressLengthException;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.NotYetImplementedException;
import rebound.exceptions.TextSyntaxCheckedException;
import rebound.exceptions.TextSyntaxException;
import rebound.text.StringUtilities;
import rebound.util.BasicExceptionUtilities;
import rebound.util.collections.ArrayUtilities;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableByteArrayList;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionIntToBoolean;
import rebound.util.objectutil.JavaNamespace;

public class NetworkUtilities
implements JavaNamespace
{
	public static final int LengthOfIPv4AddressInBytes = 4;
	public static final int LengthOfIPv6AddressInBytes = 16;
	
	
	
	
	
	
	
	public static final Inet4Address IPv4Wildcard = (Inet4Address) ipToOOP(new byte[LengthOfIPv4AddressInBytes]);
	public static final Inet6Address IPv6Wildcard = (Inet6Address) ipToOOP(new byte[LengthOfIPv6AddressInBytes]);
	
	
	protected static final boolean IsIPv6Preferred = InetAddress.getLoopbackAddress() instanceof Inet6Address;  //Todo get java.net.InetAddressImplFactory.isIPv6Supported() separately!
	public static boolean isIPv6Preferred()
	{
		return IsIPv6Preferred;
	}
	
	public static InetAddress getWildcardAddress()
	{
		return isIPv6Preferred() ? IPv6Wildcard : IPv4Wildcard;
	}
	
	
	/**
	 * @return usually the string "localhost", which is usually equivalent to "127.0.0.1" and "::1"
	 */
	public static String getLoopbackHost()
	{
		return InetAddress.getLoopbackAddress().getHostName();
	}
	
	/**
	 * @return the computer's name (ie, /etc/hostname ) not to be confused with "localhost" which is generally what's returned by {@link #getLoopbackHost()}!
	 */
	public static String getHostname()
	{
		try
		{
			return InetAddress.getLocalHost().getHostName();
		}
		catch (UnknownHostException exc)
		{
			throw new ImpossibleException(exc);
		}
	}
	
	/**
	 * @return usually the string "0.0.0.0"
	 */
	public static String getWildcardHost()
	{
		return getWildcardAddress().getHostName();
	}
	
	
	
	
	
	
	
	public static ByteList getNetmaskForIPv4(int bitLength)
	{
		if (bitLength < 0 || bitLength > 32)
			throw new IllegalArgumentException(repr(bitLength));
		
		int v = (truncatingShift32(1, bitLength) - 1) << (32 - bitLength);
		
		return ImmutableByteArrayList.newLIVE(Bytes.packBigInt(v));
	}
	
	public static String getNetmaskForIPv4asString(int bitLength)
	{
		return formatIP(getNetmaskForIPv4(bitLength));
	}
	
	
	/**
	 * Tests whether or not this IP address represents an IPv4 address.<br>
	 */
	public static boolean isIPv4(ByteList addr)
	{
		if (addr.size() == 4)
		{
			return true;
		}
		else if (addr.size() == 16)
		{
			return
			(
			addr.getByte(0) == 0 &&
			addr.getByte(1) == 0 &&
			addr.getByte(2) == 0 &&
			addr.getByte(3) == 0 &&
			addr.getByte(4) == 0 &&
			addr.getByte(5) == 0 &&
			addr.getByte(6) == 0 &&
			addr.getByte(7) == 0 &&
			addr.getByte(8) == 0 &&
			addr.getByte(9) == 0 &&
			addr.getByte(10) == (byte)0xFF &&
			addr.getByte(11) == (byte)0xFF
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
	public static ByteList canonicalizeIP(ByteList src, boolean toV6)
	{
		if (toV6)
		{
			if (src.size() == 4)
			{
				byte[] v6 = new byte[16];
				
				//[0:10] = 0;
				v6[10] = (byte)0xFF;
				v6[11] = (byte)0xFF;
				v6[12] = src.getByte(0);
				v6[13] = src.getByte(1);
				v6[14] = src.getByte(2);
				v6[15] = src.getByte(3);
				
				return ImmutableByteArrayList.newLIVE(v6);
			}
			else if (src.size() == 16)
			{
				return src;
			}
			else
			{
				throw new AddressLengthException(src.size());
			}
		}
		
		
		
		else
		{
			if (src.size() == 4)
			{
				return src;
			}
			else if (src.size() == 16)
			{
				if (isIPv4(src))
				{
					return src.subListToEnd(12);
				}
				else
				{
					return src;
				}
			}
			else
			{
				throw new AddressLengthException(src.size());
			}
		}
	}
	
	
	
	
	
	/**
	 * Convert IPv6 addresses that are actually IPv4 addresses into the actual IPv4 format :>
	 * @return -1 if the IPv6 address doesn't map to an IPv4 one!
	 */
	public static long unmapToIPv4(long ipv6HighBE, long ipv6LowBE)
	{
		if (ipv6HighBE == 0 && ((ipv6LowBE & 0x0000FFFF_00000000l) == 0x0000FFFF_00000000l))
		{
			return ipv6LowBE & 0xFFFFFFFFl;
		}
		else
		{
			return -1;
		}
	}
	
	
	
	
	
	/**
	 * This parses a textual representation of an IP address into the binary form.<br>
	 * If the string is of an IPv4 address, the array will be 4-byte.<br>
	 * Otherwise, even if it is an IPv4 address mapped to IPv6, the array will be 16-byte.<br>
	 */
	public static ByteList parseIPToList(@Nonnull String str) throws TextSyntaxCheckedException
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
		
		return ImmutableByteArrayList.newLIVE(addr);
	}
	
	private static byte digit(char c, int radix) throws TextSyntaxCheckedException
	{
		int v = Character.digit(c, radix);
		if (v == -1)
			throw TextSyntaxCheckedException.inst(c+" is not a valid digit in base "+radix);
		return (byte)v;
	}
	
	
	
	public static int parseIPv4ToU32BE(@Nonnull String str) throws TextSyntaxCheckedException
	{
		ByteList ip = parseIPToList(str);
		
		if (ip.size() == 4)
			return Bytes.getBigInt(ip);
		else if (ip.size() == 16)
			throw TextSyntaxCheckedException.inst("IPv6 was given but IPv4 was requested");
		else
			throw new AssertionError();
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
	ByteList ip,
	
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
			ByteList ip4 = canonicalizeIP(ip, false);
			
			if (ip4.size() != 4)
				throw new IllegalArgumentException("Cannot convert a non-mapped IPv6 address to an IPv4.");
			
			if (!dotted)
				//TODO Make dotted==false apply to IPv4 addresses
				throw new NotYetImplementedException();
			
			//Do the formatting
			for (int i = 0; i < 4; i++)
			{
				String dec = StringUtilities.toStringU8(ip4.getByte(i));
				
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
			ByteList ip6 = canonicalizeIP(ip, true);
			
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
						if (i < len && ip6.getByte(i*2) == 0 && ip6.getByte(i*2+1) == 0)
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
					byte a = ip6.getByte(i*2);
					byte b = ip6.getByte(i*2+1);
					
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
					String dec = StringUtilities.toStringU8(ip6.getByte(i));
					
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
	 * Note: 4-byte addresses will be formatted as IPv4, but IPv6 mapped IPv4 addresses will be formatted in the IPv6 fashion (eg, ::ffff:f700:1).<br>
	 */
	public static String formatIP(ByteList ip) throws NullPointerException
	{
		if (ip.size() == 4)
		{
			return formatIP(ip, true, true, true, false);
		}
		else
		{
			return formatIP(ip, false, true, false, false);
		}
	}
	
	public static String formatIPv4(int addrBigEndian)
	{
		return formatIP(byteArrayAsList(Bytes.packBigInt(addrBigEndian)));
	}
	
	public static String formatIPv6(long addrBigEndianHigh, long addrBigEndianLow)
	{
		return formatIP(byteArrayAsList(ArrayUtilities.concatArrays(Bytes.packBigLong(addrBigEndianHigh), Bytes.packBigLong(addrBigEndianLow))));
	}
	
	
	public static String formatMAC(ByteList addr)
	{
		return DataEncodingUtilities.encodeHex(addr, DataEncodingUtilities.HEX_UPPERCASE, ":");
	}
	
	public static String formatMAC(long addrBigEndian)
	{
		return formatMAC(Bytes.packBigLong48(addrBigEndian));
	}
	
	
	public static @Nonnull ByteList parseMACToList(String str) throws TextSyntaxCheckedException
	{
		if (str.length() != 17)
		{
			throw TextSyntaxCheckedException.inst("Length is wrong, MACs are 17 characters");
		}
		else
		{
			try
			{
				return DataEncodingUtilities.decodeHexToList(str, 1);
			}
			catch (EOFException exc)
			{
				throw TextSyntaxCheckedException.inst(exc);
			}
			catch (InvalidInputCharacterException exc)
			{
				throw TextSyntaxCheckedException.inst(exc);
			}
		}
	}
	
	
	public static long parseMACToBitfield(String str) throws TextSyntaxCheckedException
	{
		return Bytes.getBigULong48(parseMACToList(str));
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
	
	public static String urlPathEscape(@Nonnull String s)
	{
		return _urlFormEscape_JRE(s).replace("%2F", "/");
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
		//note that we don't just do this inside _urlescape() because ReURL needs urlescapeJustUnicodesAndControls to escape unicode characters (AND NOT DOUBLY-ESCAPE THE ENTIRE ALREADY-ENCODED URL!! XDD)
		return urlescapeRaw(s, c -> c == '%' || c == '+' || shouldEscape.f(c));  //'%' and '+' are the escape chars we might replace others with!, so them being escaped is non-negotiable XD
	}
	
	public static String urlescapeRaw(@Nonnull String s, UnaryFunctionIntToBoolean shouldEscape)
	{
		int[] ucs4Chars = utf16ToUCS4Array(s);
		
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
					//Thanks UTF-8, for being one char per byte (albeit possibly many bytes per char)  :>
					
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
	 * This is compatible for use with an already-escaped URL/URI to simply make it ASCII-safe :>
	 */
	public static String urlescapeJustUnicodesAndControls(@Nonnull String s)
	{
		return urlescapeRaw(s, EscapedCharsJustUnicodesAndControls);
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
		return !forAny(InvalidCharsMinimallyForPartProtocol, utf16ToUCS4Array(s));
	}
	
	public static boolean isValidURLPartUser(@Nonnull String s)
	{
		return !forAny(InvalidCharsMinimallyForPartUser, utf16ToUCS4Array(s));
	}
	
	public static boolean isValidURLPartPassword(@Nonnull String s)
	{
		return !forAny(InvalidCharsMinimallyForPartPassword, utf16ToUCS4Array(s));
	}
	
	public static boolean isValidURLPartHost(@Nonnull String s)
	{
		return !forAny(InvalidCharsMinimallyForPartHost, utf16ToUCS4Array(s));
	}
	
	//Port is an integers and thus doesn't need to be escaped XD
	
	public static boolean isValidURLPartPath(@Nonnull String s)
	{
		return !forAny(InvalidCharsMinimallyForPartPath, utf16ToUCS4Array(s));
	}
	
	public static boolean isValidURLPartQuery(@Nonnull String s)
	{
		return !forAny(InvalidCharsMinimallyForPartQuery, utf16ToUCS4Array(s));
	}
	
	public static boolean isValidURLPartFragment(@Nonnull String s)
	{
		return !forAny(InvalidCharsMinimallyForPartFragment, utf16ToUCS4Array(s));
	}
	
	
	
	
	
	public static String getSecondLevelAndTopLevelDomain(String domainName)
	{
		domainName = rtrimstr(domainName, ".");
		
		int l = domainName.lastIndexOf('.');
		
		if (l == -1 || l == 0)
		{
			return null;
		}
		else
		{
			int sl = domainName.lastIndexOf('.', l-1);
			
			if (sl == -1 || sl == 0)
			{
				return domainName;
			}
			else
			{
				return domainName.substring(sl+1);
			}
		}
	}
	
	
	public static String getTopLevelDomain(String domainName)
	{
		domainName = rtrimstr(domainName, ".");
		return domainName.isEmpty() ? null : rsplitonceReturnSucceedingOrWhole(domainName, '.');
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/////////////////// Old array-not-list code ///////////////////
	
	@Deprecated
	public static boolean isIPv4(byte[] addr)
	{
		return isIPv4(byteArrayAsList(addr));
	}
	
	@Deprecated
	public static byte[] canonicalizeIP(byte[] src, boolean toV6)
	{
		return canonicalizeIP(byteArrayAsList(src), toV6).toByteArray();
	}
	
	@Deprecated
	public static String formatMAC(byte[] addr)
	{
		return formatMAC(byteArrayAsList(addr));
	}
	
	@Deprecated
	public static byte[] parseIPToArray(@Nonnull String str) throws TextSyntaxCheckedException
	{
		return parseIPToList(str).toByteArray();
	}
	
	
	@Deprecated
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
	{
		return formatIP(byteArrayAsList(ip), v4, compact, dotted, leadingZeros);
	}
	
	
	@Deprecated
	public static String formatIP(byte[] ip) throws NullPointerException
	{
		return formatIP(byteArrayAsList(ip));
	}
	
	
	
	@Deprecated
	public static @Nonnull byte[] parseMAC(String str) throws TextSyntaxCheckedException
	{
		return parseMACToList(str).toByteArray();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/////////////////// I don't like these because .toString() doesn't return something useful :< ///////////////////
	
	public static InetAddress parseIPToOOP(String s) throws TextSyntaxCheckedException
	{
		return ipToOOP(parseIPToArray(s));
	}
	
	public static InetAddress ipToOOP(byte[] s)
	{
		try
		{
			return InetAddress.getByAddress(s);
		}
		catch (UnknownHostException exc)
		{
			throw new ImpossibleException(exc);
		}
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
}
