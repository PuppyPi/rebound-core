/*
 * Created on Apr 28, 2008
 * 	by the wonderful Eclipse(c)
 */
package rebound.net;

import static java.util.Objects.*;
import static rebound.bits.DataEncodingUtilities.*;
import static rebound.bits.Unsigned.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.testing.WidespreadTestingUtilities.*;
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
import javax.annotation.Nullable;
import rebound.bits.Bytes;
import rebound.bits.DataEncodingUtilities;
import rebound.bits.InvalidInputCharacterException;
import rebound.bits.Unsigned;
import rebound.exceptions.AddressLengthException;
import rebound.exceptions.ImpossibleException;
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
	
	
	
	
	
	
	
	public static final Inet4Address IPv4Wildcard = (Inet4Address) ipToJRE(new byte[LengthOfIPv4AddressInBytes]);
	public static final Inet6Address IPv6Wildcard = (Inet6Address) ipToJRE(new byte[LengthOfIPv6AddressInBytes]);
	
	
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
	 * Tests whether or not this IP address represents an IPv4 address literally (4 bytes) or an IPv4-as-IPv6 address.
	 */
	public static boolean isLogicallyIPv4(ByteList addr)
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
			//subsequent ones can be anything :3
			);
		}
		else
		{
			throw new AddressLengthException(addr.size());
		}
	}
	
	public static boolean isLogicallyIPv6(ByteList addr)
	{
		return !isLogicallyIPv4(addr);
	}
	
	
	public static boolean isLiterallyIPv4(ByteList addr)
	{
		return addr.size() == 4;
	}
	
	public static boolean isLiterallyIPv6(ByteList addr)
	{
		return addr.size() == 16;
	}
	
	
	
	
	/**
	 * @see #canonicalizeIP(ByteList, boolean)
	 */
	public static SimpleNetworkHost canonicalizeIP(SimpleNetworkHost src, boolean toV6)
	{
		if (src instanceof SimpleNetworkHostResolved)
		{
			SimpleNetworkHostResolved s = (SimpleNetworkHostResolved) src;
			ImmutableByteArrayList in = s.getAddress();
			
			ImmutableByteArrayList out = canonicalizeIP(in, toV6);
			
			return out == in ? s : new SimpleNetworkHostResolved(out);
		}
		else
		{
			return src;
		}
	}
	
	
	
	
	
	public static ImmutableByteArrayList canonicalizeIP(ImmutableByteArrayList src, boolean toV6)
	{
		return (ImmutableByteArrayList)canonicalizeIP((ByteList)src, toV6);
	}
	
	/**
	 * This removes the ambiguity between 4-Byte arrays (IPv4) and 16-Byte arrays (IPv6) that are mapped to IPv4 addresses.<br>
	 * <br>
	 * If <code>toV6</code> is <code>true</code>, then any IPv4 address is converted to the equivalent IPv6, 16-Byte array.<br>
	 * If <code>toV6</code> is <code>false</code>, then IPv4 mapped IPv6 addresses will be converted into 4-Byte arrays, otherwise nothing will be done to <code>src</code>.<br>
	 * 
	 * Note that this does not canonicalize the loopback addresses!
	 * (IPv4 loopback is 127.0.0.1 and embedded in IPv6 is ::FFFF:7F00:0001 which are considered equivalent by this method)
	 * (IPv6 loopback is ::1 which is not considered equivalent to 127.0.0.1 by this method!)
	 * 
	 * @param toV6  If true, IPv4 addresses (4-byte lists) will be converted to IPv4-in-IPv6 ones (16-byte lists) and only 16-byte lists will be outputted!, if false IPv4-in-IPv6 addresses are converted to actual IPv4 addresses (4-byte lists) and either 4 or 16 byte lists can be output.
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
				if (isLogicallyIPv4(src))
				{
					return ImmutableByteArrayList.newCopying(src.subListToEnd(12));
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
	 * This is useful for canonicalizing network host names (particularly with IPv6, since "::1" == "::0001" == "::0000:0000::0001" == etc. XD'')
	 * 
	 * To canonicalize IPv4-as-IPv6 addresses and IPv4 addresses to the same thing, just pass the output of this through {@link #canonicalizeIP(SimpleNetworkHost, boolean)} :>
	 * 
	 * @throws IllegalArgumentException  If the input is empty only, no validation is done on unresolved hostnames.
	 * @see #parseIP(String)
	 */
	public static SimpleNetworkHost parseHost(@Nonnull String str) throws IllegalArgumentException
	{
		requireNonNull(str);
		if (str.isEmpty())
			throw new IllegalArgumentException();
		
		ImmutableByteArrayList r = parseIPOrNullOnSyntaxError(str);
		
		return r == null ? new SimpleNetworkHostUnresolved(str) : new SimpleNetworkHostResolved(r);
	}
	
	
	
	
	
	
	
	public static @Nullable ImmutableByteArrayList parseIPOrNullOnSyntaxError(@Nonnull String str)
	{
		try
		{
			return requireNonNull(parseIP(str));
		}
		catch (TextSyntaxCheckedException exc)
		{
			return null;
		}
	}
	
	public static @Nullable ImmutableByteArrayList parseIPv4OrNullOnSyntaxError(@Nonnull String str)
	{
		try
		{
			return requireNonNull(parseIPv4(str));
		}
		catch (TextSyntaxCheckedException exc)
		{
			return null;
		}
	}
	
	public static @Nullable ImmutableByteArrayList parseIPv6OrNullOnSyntaxError(@Nonnull String str)
	{
		try
		{
			return requireNonNull(parseIPv6(str));
		}
		catch (TextSyntaxCheckedException exc)
		{
			return null;
		}
	}
	
	
	
	/**
	 * This parses a textual representation of an IP address into the binary form.<br>
	 * If the string is of an IPv4 address, the array will be 4-byte.<br>
	 * Otherwise, even if it is an IPv4 address mapped to IPv6, the array will be 16-byte.<br>
	 * @see #parseHost(String)
	 */
	public static @Nonnull ImmutableByteArrayList parseIP(@Nonnull String str) throws TextSyntaxCheckedException
	{
		requireNonNull(str);
		
		boolean v6 = str.indexOf(':') != -1;
		
		return v6 ? parseIPv6(str) : parseIPv4(str);
	}
	
	
	public static @Nonnull ImmutableByteArrayList parseIPv4(@Nonnull String str) throws TextSyntaxCheckedException
	{
		int n = str.length();
		
		if (n < 7)  //Eg: 0.0.0.0
			throw TextSyntaxCheckedException.inst("Input is too small ("+n+" characters) to possibly be a valid IPv4 address.");
		if (n > 16)  //Eg: 000.000.000.000
			throw TextSyntaxCheckedException.inst("Input is too large ("+n+" characters) to possibly be a valid IPv4 address.");
		
		byte[] addr = new byte[4];
		
		int index = 0;
		for (int byteIndex = 0; byteIndex < 4; byteIndex++)
		{
			if (index >= n)
				throw TextSyntaxCheckedException.inst("Input was too short to have four dotted quads!");
			
			char c = str.charAt(index);
			
			if (c == '.')
				throw TextSyntaxCheckedException.inst("Input has a dot with nothing following it!");
			
			byte b = 0;
			{
				b = decdigit(c);
				
				index++;
				if (index != n)
					c = str.charAt(index);
				
				if (index < n && c != '.')
				{
					b *= 10;
					b += decdigit(c);
					index++;
					
					if (index != n)
						c = str.charAt(index);
				}
				
				if (index < n && c != '.')
				{
					b *= 10;
					b += decdigit(c);
					index++;
					
					if (index != n)
						c = str.charAt(index);
				}
				
				if (index < n)
				{
					if (byteIndex == 3)
						throw TextSyntaxCheckedException.inst("Input was excessive; expected only 4 dotted quads and nothing after!");
					
					if (c != '.')
						throw TextSyntaxCheckedException.inst("Expected a dot for the dotted-quad syntax but instead, got "+repr(c));
					
					index++;
				}
			}
			
			
			addr[byteIndex] = b;
		}
		
		if (index != n)
			throw TextSyntaxCheckedException.inst("Input was excessive; expected only 4 dotted quads and nothing after!");
		
		return ImmutableByteArrayList.newLIVE(addr);
	}
	
	private static byte decdigit(char c) throws TextSyntaxCheckedException
	{
		if (c >= '0' && c <= '9')
			return (byte)(c - '0');
		else
			throw TextSyntaxCheckedException.inst(c+" is not a valid digit in base 10");
	}
	
	
	
	
	
	
	public static @Nonnull ImmutableByteArrayList parseIPv6(@Nonnull String str) throws TextSyntaxCheckedException
	{
		byte[] addr = new byte[16];  //This is initialized to zero's so we don't need to worry about filling in the "::" part :>
		
		int n = str.length();
		
		if (n < 2)
			throw TextSyntaxCheckedException.inst("Input is too small ("+n+") to possibly be a valid IPv6 address.");
		
		
		/*
		 * RFC 4291 § 2.2.2:
		 * 		
		 * 		The "::" can only appear once in an address.
		 * 
		 * So we need only parse both directions up and down to it if it's present and leave untouched bytes as zeros!  :D
		 */
		
		//Parse the leading parts up to the end or double-colon
		boolean upwardParseFinishedOnDoubleColon;
		int bytesInUpwardParse;
		int upwardParseDoubleColonStart = -1;
		{
			upwardParseFinishedOnDoubleColon = false;
			
			int addressByteCursor = 0;
			
			boolean afterColon = false;
			int start = 0;
			
			for (int i = 0; i <= n; i++)
			{
				boolean eof = i == n;
				
				char c = eof ? 0 : str.charAt(i);
				
				if (eof || c == ':')
				{
					if (!eof && i == 0)
					{
						//Let's see if the next one is a colon!
						afterColon = true;
					}
					else if (afterColon)
					{
						if (eof)
							throw TextSyntaxCheckedException.inst("IPv6 address blocks can be 1 character but not 0 characters aside from the special \"::\", so they can end with the \"::\" but not with a single \":\"");
						
						upwardParseFinishedOnDoubleColon = true;
						upwardParseDoubleColonStart = i - 1;
						break;
					}
					else
					{
						String block = str.substring(start, i);
						
						if (block.length() > 4)
							throw TextSyntaxCheckedException.inst("IPv6 hex blocks can't be more than 4 characters long!  Got: "+repr(block));
						
						asrt(!block.isEmpty());
						
						int v;
						try
						{
							v = Integer.parseInt(block, 16);
						}
						catch (NumberFormatException exc)
						{
							throw new AssertionError(exc);  //We should have caught this earlier!!
						}
						
						byte low = (byte)v;
						byte high = (byte)(v >>> 8);
						
						if (addressByteCursor == addr.length)
							throw TextSyntaxCheckedException.inst("There were too many blocks!  (Before the \"::\" if there was one)");
						
						addr[addressByteCursor] = high;
						addressByteCursor++;
						addr[addressByteCursor] = low;
						addressByteCursor++;
						
						start = i+1;
						afterColon = true;
					}
				}
				else
				{
					if (i == 1 && afterColon)
						throw TextSyntaxCheckedException.inst("IPv6 address blocks can be 1 character but not 0 characters aside from the special \"::\", so they can start with the \"::\" but not with a single \":\"");
					
					if (!isHexDigit(c))
						throw TextSyntaxCheckedException.inst("Not a hex digit: "+repr(c));
					
					afterColon = false;
				}
			}
			
			
			if (!upwardParseFinishedOnDoubleColon)
			{
				if (addressByteCursor != addr.length)
					throw TextSyntaxCheckedException.inst("There were too few blocks!");
			}
			
			
			bytesInUpwardParse = addressByteCursor;
		}
		
		
		
		
		
		if (upwardParseFinishedOnDoubleColon)
		{
			//Parse the trailing parts down to the start or double-colon :>
			boolean downwardParseFinishedOnDoubleColon;
			int downwardParseDoubleColonStart = -1;
			{
				downwardParseFinishedOnDoubleColon = false;
				
				int addressBytePastcursor = addr.length;
				
				boolean afterColon = false;
				int end = n;
				
				for (int i = n; i >= 0;)
				{
					boolean bof = i == 0;
					
					i--;
					
					char c = bof ? 0 : str.charAt(i);
					
					if (bof || c == ':')
					{
						if (!bof && i == n - 1)  //we're after the i-- here
						{
							//Let's see if the next one is a colon!
							afterColon = true;
						}
						else if (afterColon)
						{
							if (bof)
								throw TextSyntaxCheckedException.inst("IPv6 address blocks can be 1 character but not 0 characters aside from the special \"::\", so they can start with the \"::\" but not with a single \":\"");
							
							downwardParseFinishedOnDoubleColon = true;
							downwardParseDoubleColonStart = i;
							break;
						}
						else
						{
							String block = str.substring(i+1, end);
							
							if (block.length() > 4)
								throw TextSyntaxCheckedException.inst("IPv6 hex blocks can't be more than 4 characters long!  Got: "+repr(block));
							
							asrt(!block.isEmpty());
							
							int v;
							try
							{
								v = Integer.parseInt(block, 16);
							}
							catch (NumberFormatException exc)
							{
								throw new AssertionError(exc);  //We should have caught this earlier!!
							}
							
							byte low = (byte)v;
							byte high = (byte)(v >>> 8);
							
							if (addressBytePastcursor == bytesInUpwardParse)
								throw TextSyntaxCheckedException.inst("There were too many blocks after the \"::\"!");
							
							addressBytePastcursor--;
							addr[addressBytePastcursor] = low;
							addressBytePastcursor--;
							addr[addressBytePastcursor] = high;
							
							end = i;
							afterColon = true;
						}
					}
					else
					{
						if (afterColon && i == n - 2)
							throw TextSyntaxCheckedException.inst("IPv6 address blocks can be 1 character but not 0 characters aside from the special \"::\", so they can end with the \"::\" but not with a single \":\"");
						
						if (!isHexDigit(c))
							throw TextSyntaxCheckedException.inst("Not a hex digit: "+repr(c));
						
						afterColon = false;
					}
				}
				
				
				asrt(downwardParseFinishedOnDoubleColon);
				asrt(downwardParseDoubleColonStart != -1);
				asrt(upwardParseDoubleColonStart != -1);
				
				if (downwardParseDoubleColonStart != upwardParseDoubleColonStart)
					throw TextSyntaxCheckedException.inst("There can only be one \"::\" in an IPv6 address (check RFC 4291 § 2.2.2 if you don't believe me!)");
			}
			
			assert upwardParseFinishedOnDoubleColon == downwardParseFinishedOnDoubleColon;
		}
		
		return ImmutableByteArrayList.newLIVE(addr);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static String formatIPv4(ByteList ip)
	{
		return formatIPv4(ip, false);
	}
	
	
	/**
	 * @param leadingZeros false for eg 127.0.0.1, true for eg 127.000.000.001  (like my wifi printer!)
	 */
	public static String formatIPv4(ByteList ip, boolean leadingZeros)
	{
		StringBuilder buff = new StringBuilder();
		
		ByteList ip4 = canonicalizeIP(ip, false);
		
		if (ip4.size() != 4)
			throw new IllegalArgumentException("Cannot convert a non-mapped IPv6 address to an IPv4.");
		
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
		
		return buff.toString();
	}
	
	
	
	
	
	
	
	/**
	 * Formats an IPv6 address in the standard fashion.
	 * Note: IPv6 mapped IPv4 addresses will be formatted in the IPv6 fashion (eg, ::ffff:f700:1).<br>
	 */
	public static String formatIPv6(ByteList addrBigEndian)
	{
		return formatIPv6(addrBigEndian, true, false);
	}
	
	/**
	 * This formats a binary IPv6 address into a textual representation in one of 4 possible formats.<br>
	 * <br>
	 * <br>
	 * For ::ffff:7f00:1 as an example, these are the possible formats:<br>
	 * <table>
	 * 	<tr><th>Compact</th><th>Leading Zeros Inside Blocks</th><th>Text</th></tr>
	 * 	<tr><td><code>false</code></td><td><code>false</code></td><td>0:0:0:0:0:ffff:7f00:1</td></tr>
	 * 	<tr><td><code>true</code></td><td><code>false</code></td><td>::ffff:7f00:1</td></tr>
	 * 	<tr><td><code>false</code></td><td><code>true</code></td><td>0000:0000:0000:0000:0000:ffff::7f00:0001</td></tr>
	 * 	<tr><td><code>true</code></td><td><code>true</code></td><td>::ffff:7f00:0001</td></tr>
	 * </table>
	 */
	public static String formatIPv6(ByteList ip6, boolean compact, boolean leadingZerosInsideBlocks)
	{
		StringBuilder buff = new StringBuilder();
		
		//Calculate the (first) best place to compact
		int best_zerostrech_start = -1;
		int best_zerostrech_size = 0;
		{
			if (compact)
			{
				int this_zerostrech_start = 0;
				
				int len = 8;
				
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
		for (int addrWordIndex = 0; addrWordIndex < 8; addrWordIndex++)
		{
			if (addrWordIndex == best_zerostrech_start) //&& compact
			{
				if (addrWordIndex == 0)
					buff.append(':');
				buff.append(':');
				
				addrWordIndex += best_zerostrech_size-1;
			}
			else
			{
				byte a = ip6.getByte(addrWordIndex*2);
				byte b = ip6.getByte(addrWordIndex*2+1);
				
				if (a != 0)
				{
					if (Unsigned.lessThanU8(a, (byte)0x10))
					{
						if (leadingZerosInsideBlocks)
							buff.append('0');
						buff.append(Character.forDigit(a, 16));
					}
					else
					{
						buff.append(Character.forDigit((a & 0xF0) >>> 4, 16));
						buff.append(Character.forDigit(a & 0x0F, 16));
					}
					
					
					if (Unsigned.lessThanU8(b, (byte)0x10))
					{
						buff.append('0');
						buff.append(Character.forDigit(b, 16));
					}
					else
					{
						buff.append(Character.forDigit((b & 0xF0) >>> 4, 16));
						buff.append(Character.forDigit(b & 0x0F, 16));
					}
				}
				else
				{
					if (leadingZerosInsideBlocks)
					{
						buff.append('0');
						buff.append('0');
					}
					
					if (Unsigned.lessThanU8(b, (byte)0x10))
					{
						if (leadingZerosInsideBlocks)
							buff.append('0');
						buff.append(Character.forDigit(b, 16));
					}
					else
					{
						buff.append(Character.forDigit((b & 0xF0) >>> 4, 16));
						buff.append(Character.forDigit(b & 0x0F, 16));
					}
				}
				
				if (addrWordIndex < 7)
					buff.append(':');
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
			return formatIPv4(ip);
		}
		else
		{
			return formatIPv6(ip);
		}
	}
	
	
	
	
	
	
	
	
	public static String formatMAC(ByteList addr)
	{
		return DataEncodingUtilities.encodeHex(addr, DataEncodingUtilities.HEX_UPPERCASE, ":");
	}
	
	public static String formatMAC(long addrBigEndian)
	{
		return formatMAC(byteArrayAsList(Bytes.packBigLong48(addrBigEndian)));
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
	
	
	
	
	public static @Nullable Long getAndParseIntegerValueIfPresentFromQueryString(String url, String parameterName) throws NumberFormatException
	{
		String u = getUnescapedIfPresentFromQueryString(url, parameterName);
		return u == null ? null : Long.parseLong(u);
	}
	
	public static @Nullable String getUnescapedIfPresentFromQueryString(String url, String parameterName)
	{
		int q = url.indexOf('?');
		if (q == -1)
			return null;
		
		int n = url.length();
		
		//Try "?name=" then "&name=" :3
		
		int startOfCurrentParameterNameIndex = q+1;
		
		while (startOfCurrentParameterNameIndex < n)
		{
			int nextAmpersand = url.indexOf('&', startOfCurrentParameterNameIndex);
			
			if (url.regionMatches(true, startOfCurrentParameterNameIndex, parameterName, 0, parameterName.length()))
			{
				int hopefullyEqualsIndex = startOfCurrentParameterNameIndex+1+parameterName.length();
				
				if (hopefullyEqualsIndex == n)
					break;
				else if (url.charAt(hopefullyEqualsIndex) == '=')
				{
					//Got it!! :DD
					int startOfValue = hopefullyEqualsIndex + 1;
					return url.substring(startOfValue, nextAmpersand == -1 ? n : nextAmpersand);
				}
				//else: continue on!  It's a prefix!  like if we're looking for "flutter" then the url is "/page?fluttershy=yay"  :3
			}
			
			if (nextAmpersand == -1)
				break;
			
			startOfCurrentParameterNameIndex = nextAmpersand + 1;
		}
		
		return null;
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<Bitfield forms!
	public static String formatIPv4(int addrBigEndian)
	{
		return formatIPv4(byteArrayAsList(Bytes.packBigInt(addrBigEndian)));
	}
	
	public static String formatIPv6(long addrBigEndianHigh, long addrBigEndianLow)
	{
		return formatIPv6(byteArrayAsList(ArrayUtilities.concatArrays(Bytes.packBigLong(addrBigEndianHigh), Bytes.packBigLong(addrBigEndianLow))));
	}
	
	public static int parseIPv4ToU32BE(@Nonnull String str) throws TextSyntaxCheckedException
	{
		ByteList ip = parseIP(str);
		
		if (ip.size() == 4)
			return Bytes.getBigInt(ip);
		else if (ip.size() == 16)
			throw TextSyntaxCheckedException.inst("IPv6 was given but IPv4 was requested");
		else
			throw new AssertionError();
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
	
	public static long parseMACToBitfield(String str) throws TextSyntaxCheckedException
	{
		return Bytes.getBigULong48(parseMACToList(str));
	}
	//Bitfield forms!>
	
	
	
	
	
	
	
	
	
	
	
	/////////////////// Old array-not-list code ///////////////////
	
	@Deprecated
	public static byte[] canonicalizeIP(byte[] src, boolean toV6)
	{
		return canonicalizeIP(byteArrayAsList(src), toV6).toByteArray();
	}
	
	@Deprecated
	public static byte[] parseIPToArray(@Nonnull String str) throws TextSyntaxCheckedException
	{
		return parseIP(str).toByteArray();
	}
	
	@Deprecated
	public static String formatIP(byte[] ip) throws NullPointerException
	{
		return formatIP(byteArrayAsList(ip));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/////////////////// I don't like these because .toString() doesn't return something useful :< ///////////////////
	
	public static InetAddress parseIPToJRE(String s) throws TextSyntaxCheckedException
	{
		return ipToJRE(parseIP(s).toByteArray());
	}
	
	public static InetAddress ipToJRE(byte[] s)
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
