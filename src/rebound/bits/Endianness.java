/*
 * Created on Sep 5, 2008
 * 	by the great Eclipse(c)
 */
package rebound.bits;

import java.nio.ByteOrder;

/**
 * A useful enum to have handy.
 * @author RProgrammer
 */
public enum Endianness
{
	Little,
	Big,
	;
	
	
	
	public static Endianness convert(ByteOrder nioEndianness)
	{
		if (nioEndianness == ByteOrder.LITTLE_ENDIAN)
			return Endianness.Little;
		else if (nioEndianness == ByteOrder.BIG_ENDIAN)
			return Endianness.Big;
		else if (nioEndianness == null)
			return null; //could mean 'unknown'  /shrugs
		else
			throw new IllegalArgumentException();
	}
	
	public static ByteOrder convert(Endianness rpEndianness)
	{
		if (rpEndianness == Endianness.Little)
			return ByteOrder.LITTLE_ENDIAN;
		else if (rpEndianness == Endianness.Big)
			return ByteOrder.BIG_ENDIAN;
		else if (rpEndianness == null)
			return null; //could mean 'unknown'  /shrugs
		else
			throw new AssertionError();
	}
	
	
	public static Endianness nativeByteEndianness()
	{
		return convert(ByteOrder.nativeOrder());
	}
}
