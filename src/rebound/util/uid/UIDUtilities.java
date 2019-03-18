package rebound.util.uid;

import java.io.EOFException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.bits.DataEncodingUtilities;
import rebound.bits.InvalidInputCharacterException;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.TextSyntaxException;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableByteArrayList;

public class UIDUtilities
{
	public static ImmutableByteArrayList parseAnnotation(@Nonnull UID uid) throws TextSyntaxException
	{
		return parseString(uid.value());
	}
	
	
	
	
	public static ImmutableByteArrayList parseString(@Nonnull String uidstr) throws TextSyntaxException
	{
		if (!isEncodedUID(uidstr))
			throw TextSyntaxException.inst();
		
		return _parseStringNoCheck(uidstr);
	}
	
	
	@ImplementationTransparency
	public static ImmutableByteArrayList _parseStringNoCheck(@Nonnull String uidstr)
	{
		try
		{
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
		//return DataEncodingUtilities.encodeHexNoDelimiter(bytes, DataEncodingUtilities.HEX_UPPERCASE);
		
		
		String s = DataEncodingUtilities.encodeHexNoDelimiter(bytes, DataEncodingUtilities.HEX_UPPERCASE);
		
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
		return formatString(bytes.getREADONLYLiveWholeArrayBackingUNSAFE());
	}
	
	
	
	
	
	
	@Nullable
	public static ImmutableByteArrayList getClassUIDOrNull(@Nonnull Class c)
	{
		UID uid = (UID)c.getAnnotation(UID.class);
		
		return uid != null ? parseAnnotation(uid) : null;
	}
	
	
	@Nonnull
	public static ImmutableByteArrayList getClassUIDOrFail(@Nonnull Class c) throws ClassUIDNotFoundException
	{
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
		ImmutableByteArrayList uid = getClassUIDOrNull(c);
		return uid != null ? uid : c.getName();
	}
	
	
	
	
	
	
	/**
	 * Note: see SimpleScannerForClassnames for a SUUUUPER basic way of getting the classes ^^'
	 */
	public static Map<ImmutableByteArrayList, Class> findClassUIDs(Iterable<Class> classes)
	{
		Map<ImmutableByteArrayList, Class> uidsToClasses = new HashMap<>();
		
		for (Class c : classes)
		{
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
		int n = uidstr.length();
		
		if ((n % 2) != 0)  //must be a multiple of two hex chars (nibbles) — ie, an integer number of bytes :>
			return false;
		
		for (int i = 0; i < n; i++)
		{
			char c = uidstr.charAt(i);
			
			boolean low = c >= 'a' && c <= 'f';
			boolean high = c >= 'A' && c <= 'F';
			boolean num = c >= '0' && c <= '9';
			
			if (!low && !high && !num)
				return false;
		}
		
		return true;
	}
}
