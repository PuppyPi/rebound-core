package rebound.util.uid;

import static java.util.Objects.*;
import static rebound.text.StringUtilities.*;
import java.io.EOFException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.bits.Bytes;
import rebound.bits.DataEncodingUtilities;
import rebound.bits.InvalidInputCharacterException;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.TextSyntaxException;
import rebound.text.StringUtilities;
import rebound.util.RUID128;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableByteArrayList;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionCharToBoolean;

public class UIDUtilities
{
	public static final UnaryFunctionCharToBoolean DelimiterPattern = c -> c == '-' || c == ' ' || c == ':' || c == '/' || c == ',';
	
	
	public static RUID128 parseRUID128(String s) throws TextSyntaxException
	{
		if (s.length() != 32)
			throw TextSyntaxException.inst("Not even the right length!!: "+repr(s));
		
		long highBits;
		long lowBits;
		try
		{
			highBits = Long.parseUnsignedLong(s.substring(0, 16), 16);
			lowBits = Long.parseUnsignedLong(s.substring(16, 32), 16);
		}
		catch (NumberFormatException exc)
		{
			throw TextSyntaxException.inst(exc);
		}
		
		return new RUID128(lowBits, highBits);
	}
	
	/**
	 * @return the lowercase version
	 */
	public static String formatRUID128(RUID128 id)
	{
		return zeroPad(Long.toString(id.getHighBits(), 16), 16) + zeroPad(Long.toString(id.getLowBits(), 16), 16);
	}
	
	
	
	
	public static ImmutableByteArrayList parseAnnotation(@Nonnull UID uid) throws TextSyntaxException
	{
		requireNonNull(uid);
		return parseString(uid.value());
	}
	
	
	
	
	public static ImmutableByteArrayList parseString(@Nonnull String uidstr) throws TextSyntaxException
	{
		requireNonNull(uidstr);
		
		uidstr = removeWhitespace(uidstr);
		
		if (!isEncodedUID(uidstr))
			throw TextSyntaxException.inst();
		
		return _parseStringNoCheck(uidstr);
	}
	
	
	@ImplementationTransparency
	public static ImmutableByteArrayList _parseStringNoCheck(@Nonnull String uidstr)
	{
		try
		{
			uidstr = replaceCharsWithStringsByPattern(uidstr, DelimiterPattern, "");
			
			byte[] a = DataEncodingUtilities.decodeHexNoDelimiter(uidstr);
			return ImmutableByteArrayList.newLIVE(a);
		}
		catch (EOFException exc)
		{
			throw TextSyntaxException.inst(exc);
		}
		catch (InvalidInputCharacterException exc)
		{
			throw new ImpossibleException("But we validated it!!", exc);
		}
	}
	
	@ImplementationTransparency
	public static String formatString(@ReadonlyValue @Nonnull byte[] bytes)
	{
		requireNonNull(bytes);
		
		//return DataEncodingUtilities.encodeHexNoDelimiter(bytes, DataEncodingUtilities.HEX_LOWERCASE);
		
		
		String s = DataEncodingUtilities.encodeHexNoDelimiter(bytes, DataEncodingUtilities.HEX_LOWERCASE);  //Lowercase by default because Eclipse scans across it as one token and life is easier XD'  (also it's less shouty X3 )
		
		//s = StringUtilities.reverse(s);  //DEU only does big-endian nibbles XP
		
		//		//Add leading zeros if necessaries! ^^
		//		int maxPossibleLength = bytes.length * 2;
		//		if (maxPossibleLength - s.length() > 0)
		//			s = s + StringUtilities.mul('0', maxPossibleLength - s.length());  //zeros go on end for little endian place value format ^w^
		
		if (s.length() != bytes.length * 2)
			throw new ImpossibleException();
		
		
		return s;
	}
	
	@ImplementationTransparency
	public static String formatString(@Nonnull ImmutableByteArrayList bytes)
	{
		requireNonNull(bytes);
		return formatString(bytes.getREADONLYLiveWholeArrayBackingUNSAFE());
	}
	
	
	
	
	
	
	@Nullable
	public static ImmutableByteArrayList getClassUIDOrNull(@Nonnull Class c)
	{
		requireNonNull(c);
		
		UID uid = (UID)c.getAnnotation(UID.class);
		
		return uid != null ? parseAnnotation(uid) : null;
	}
	
	
	@Nonnull
	public static ImmutableByteArrayList getClassUIDOrFail(@Nonnull Class c) throws ClassUIDNotFoundException
	{
		requireNonNull(c);
		
		ImmutableByteArrayList uid = getClassUIDOrNull(c);
		if (uid == null)
			throw new ClassUIDNotFoundException(c);
		else
			return uid;
	}
	
	/**
	 * @return either an {@link ImmutableByteArrayList} of the UID or a {@link String} of the {@link Class#getName() classname}!
	 */
	@Nonnull
	public static Object getClassUIDOrName(@Nonnull Class c)
	{
		requireNonNull(c);
		
		ImmutableByteArrayList uid = getClassUIDOrNull(c);
		return uid != null ? uid : c.getName();
	}
	
	
	
	
	
	
	/**
	 * Note: see SimpleScannerForClassnames for a SUUUUPER basic way of getting the classes ^^'
	 */
	public static Map<ImmutableByteArrayList, Class> findClassUIDs(Iterable<Class> classes)
	{
		requireNonNull(classes);
		
		Map<ImmutableByteArrayList, Class> uidsToClasses = new HashMap<>();
		
		for (Class c : classes)
		{
			requireNonNull(c);
			
			UID uid = (UID)c.getAnnotation(UID.class);
			
			if (uid != null)
			{
				uidsToClasses.put(parseAnnotation(uid), c);
			}
		}
		
		return uidsToClasses;
	}
	
	
	
	
	public static boolean isEncodedUID(@Nonnull String uidstr)
	{
		requireNonNull(uidstr);
		
		int n = uidstr.length();
		
		if ((n % 2) != 0)  //must be a multiple of two hex chars (nibbles) — ie, an integer number of bytes :>
			return false;
		
		for (int i = 0; i < n; i++)
		{
			char c = uidstr.charAt(i);
			
			boolean low = c >= 'a' && c <= 'f';
			boolean high = c >= 'A' && c <= 'F';
			boolean num = c >= '0' && c <= '9';
			boolean delimiter = DelimiterPattern.f(c);
			
			if (!low && !high && !num && !delimiter)
				return false;
		}
		
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static UUID toUUID(ByteList bytes)
	{
		requireNonNull(bytes);
		
		if (bytes.size() != 16)
			throw new IllegalArgumentException("A UUID is 16 bytes, we were given "+bytes.size());
		
		return new UUID(Bytes.getBigLong(bytes, 0), Bytes.getBigLong(bytes, 8));
	}
	
	public static ImmutableByteArrayList fromUUID(UUID uuid)
	{
		requireNonNull(uuid);
		
		byte[] a = new byte[16];
		Bytes.putBigLong(a, 0, uuid.getMostSignificantBits());
		Bytes.putBigLong(a, 8, uuid.getLeastSignificantBits());
		return ImmutableByteArrayList.newLIVE(a);
	}
}
