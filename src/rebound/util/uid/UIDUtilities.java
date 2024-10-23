package rebound.util.uid;

import static java.util.Objects.*;
import static rebound.math.MathUtilities.*;
import static rebound.testing.WidespreadTestingUtilities.*;
import static rebound.text.StringUtilities.*;
import java.io.EOFException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.purelyforhumans.DeprecatedInFavorOf;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.bits.Bytes;
import rebound.bits.DataEncodingUtilities;
import rebound.bits.InvalidInputCharacterException;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.OverflowException;
import rebound.exceptions.TextSyntaxException;
import rebound.util.RUID128;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableByteArrayList;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionCharToBoolean;

public class UIDUtilities
{
	public static final UnaryFunctionCharToBoolean DelimiterPattern = c -> c == '-' || c == ' ' || c == ':' || c == '/' || c == ',';
	
	
	public static RUID128 parseRUID128(String s) throws TextSyntaxException
	{
		if (s.length() != 39)
			throw TextSyntaxException.inst("Not even the right length!!: "+repr(s));
		
		//Todo more efficient implementation? :3
		return bigIntegerToRUID128(new BigInteger(s));
	}
	
	/**
	 * @return the lowercase version
	 */
	public static String formatRUID128(RUID128 id)
	{
		//Todo more efficient implementation? :3
		String r = zeroPad(ruid128ToBigInteger(id).toString(), 39);
		asrt(r.length() == 39);
		return r;
	}
	
	
	
	
	public static BigInteger ruid128ToBigInteger(RUID128 id)
	{
		BigInteger low = toBigIntegerFromUnsignedLong(id.getLowBits());
		BigInteger high = toBigIntegerFromUnsignedLong(id.getHighBits());
		
		BigInteger r = high.shiftLeft(64).or(low);
		asrt(r.signum() >= 0);
		return r;
	}
	
	
	protected static final BigInteger MASK64 = toBigIntegerFromUnsignedLong(0xFFFFFFFFFFFFFFFFl);
	protected static final BigInteger MASK128 = MASK64.shiftLeft(64).or(MASK64);
	
	public static RUID128 bigIntegerToRUID128(BigInteger i) throws OverflowException
	{
		if (i.signum() < 0)
			throw new OverflowException();
		
		BigInteger low = i.and(MASK64);
		i = i.shiftRight(64);
		BigInteger high = i.and(MASK64);
		i = i.shiftRight(64);
		if (i.signum() != 0)
			throw new OverflowException();
		
		return new RUID128(low.longValue(), high.longValue());
	}
	
	
	
	
	
	
	
	public static byte[] packLittleRUID128(RUID128 id)
	{
		byte[] bytes = new byte[16];
		putLittleRUID128(bytes, id);
		return bytes;
	}
	
	public static void putLittleRUID128(@WritableValue byte[] bytes, RUID128 id)
	{
		if (bytes.length != 16)
			throw new IllegalArgumentException("128 bits is 16 bytes, not "+bytes.length+" bytes!");
		putLittleRUID128(bytes, 0, id);
	}
	
	public static void putLittleRUID128(@WritableValue byte[] bytes, int offset, RUID128 id)
	{
		Bytes.putLittleLong(bytes, offset, id.getLowBits());
		Bytes.putLittleLong(bytes, offset+8, id.getHighBits());
	}
	
	public static RUID128 getLittleRUID128(byte[] bytes)
	{
		if (bytes.length != 16)
			throw new IllegalArgumentException("128 bits is 16 bytes, not "+bytes.length+" bytes!");
		return getLittleRUID128(bytes, 0);
	}
	
	public static RUID128 getLittleRUID128(byte[] bytes, int offset)
	{
		return new RUID128(Bytes.getLittleLong(bytes, 0), Bytes.getLittleLong(bytes, 8));
	}
	
	
	
	
	
	
	
	
	public static byte[] packBigRUID128(RUID128 id)
	{
		byte[] bytes = new byte[16];
		putBigRUID128(bytes, id);
		return bytes;
	}
	
	public static void putBigRUID128(@WritableValue byte[] bytes, RUID128 id)
	{
		if (bytes.length != 16)
			throw new IllegalArgumentException("128 bits is 16 bytes, not "+bytes.length+" bytes!");
		putBigRUID128(bytes, 0, id);
	}
	
	public static void putBigRUID128(@WritableValue byte[] bytes, int offset, RUID128 id)
	{
		Bytes.putBigLong(bytes, offset, id.getLowBits());
		Bytes.putBigLong(bytes, offset+8, id.getHighBits());
	}
	
	public static RUID128 getBigRUID128(byte[] bytes)
	{
		if (bytes.length != 16)
			throw new IllegalArgumentException("128 bits is 16 bytes, not "+bytes.length+" bytes!");
		return getBigRUID128(bytes, 0);
	}
	
	public static RUID128 getBigRUID128(byte[] bytes, int offset)
	{
		return new RUID128(Bytes.getBigLong(bytes, 0), Bytes.getBigLong(bytes, 8));
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
	public static String formatString(@ReadonlyValue @Nonnull byte[] bytes, boolean uppercase)
	{
		requireNonNull(bytes);
		
		//return DataEncodingUtilities.encodeHexNoDelimiter(bytes, DataEncodingUtilities.HEX_LOWERCASE);
		
		
		String s = DataEncodingUtilities.encodeHexNoDelimiter(bytes, uppercase ? DataEncodingUtilities.HEX_UPPERCASE : DataEncodingUtilities.HEX_LOWERCASE);  //Lowercase by default because Eclipse scans across it as one token and life is easier XD'  (also it's less shouty X3 )
		
		//s = StringUtilities.reverse(s);  //DEU only does big-endian nibbles XP
		
		//		//Add leading zeros if necessaries! ^^
		//		int maxPossibleLength = bytes.length * 2;
		//		if (maxPossibleLength - s.length() > 0)
		//			s = s + StringUtilities.mul('0', maxPossibleLength - s.length());  //zeros go on end for little endian place value format ^w^
		
		if (s.length() != bytes.length * 2)
			throw new ImpossibleException();
		
		
		return s;
	}
	
	@Deprecated
	@DeprecatedInFavorOf("formatStringUppercase() | formatStringLowercase()")
	@ImplementationTransparency
	public static String formatString(@Nonnull ImmutableByteArrayList bytes)
	{
		return formatStringUppercase(bytes);
	}
	
	@ImplementationTransparency
	public static String formatStringUppercase(@Nonnull ImmutableByteArrayList bytes)
	{
		requireNonNull(bytes);
		return formatString(bytes.getREADONLYLiveWholeArrayBackingUNSAFE(), true);
	}
	
	@ImplementationTransparency
	public static String formatStringLowercase(@Nonnull ImmutableByteArrayList bytes)
	{
		requireNonNull(bytes);
		return formatString(bytes.getREADONLYLiveWholeArrayBackingUNSAFE(), false);
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
