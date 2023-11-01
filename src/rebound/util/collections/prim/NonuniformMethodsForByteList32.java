package rebound.util.collections.prim;

import javax.annotation.Nonnegative;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.hints.IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.simpledata.BoundedInt;
import rebound.annotations.semantic.simpledata.BoundedLong;
import rebound.bits.Bytes;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.collections.prim.PrimitiveCollections.DefaultToArraysByteCollection;

//Todo Elementwise boolean operations between ByteLists!!  AND, OR, NOT, XOR!  \:D/

@ImplementationTransparency  //This class only exists to keep the bulk functions exactly uniform and correspondent :3     This might be removed soon XD''
public interface NonuniformMethodsForByteList32
extends DefaultToArraysByteCollection
{
	public byte getByte(@Nonnegative int offsetInBytes);
	public void setByte(@Nonnegative int offsetInBytes, byte value);
	
	
	public default long getByteFieldLE(@Nonnegative int offsetInBytes, @BoundedInt(min=1, max=8) int count)
	{
		long v = 0;
		int i = offsetInBytes + count;
		while (i >= offsetInBytes)
		{
			i--;
			v <<= 8;
			v |= getByte(offsetInBytes);
		}
		return v;
	}
	
	public default long getByteFieldBE(@Nonnegative int offsetInBytes, @BoundedInt(min=1, max=8) int count)
	{
		long v = 0;
		int stop = offsetInBytes + count;
		while (offsetInBytes < stop)
		{
			v <<= 8;
			v |= getByte(offsetInBytes);
			offsetInBytes++;
		}
		return v;
	}
	
	
	
	public default void setByteFieldLE(@Nonnegative int offsetInBytes, @BoundedInt(min=1, max=8) int count, long field)
	{
		int stop = offsetInBytes + count;
		while (offsetInBytes < stop)
		{
			setByte(offsetInBytes, (byte)field);
			offsetInBytes++;
			field >>= 8;
		}
	}
	
	public default void setByteFieldBE(@Nonnegative int offsetInBytes, @BoundedInt(min=1, max=8) int count, long field)
	{
		int i = offsetInBytes + count;
		while (i >= offsetInBytes)
		{
			i--;
			setByte(i, (byte)field);
			offsetInBytes++;
			field >>= 8;
		}
	}
	
	
	
	
	
	
	
	public default short getShortLE(@Nonnegative int offsetInBytes)
	{
		return Bytes.getLittleShort((ByteList)this, offsetInBytes);
	}
	
	public default short getShortBE(@Nonnegative int offsetInBytes)
	{
		return Bytes.getBigShort((ByteList)this, offsetInBytes);
	}
	
	
	public default @BoundedInt(min=0, max=16777215) int getUInt24LE(@Nonnegative int offsetInBytes)
	{
		return Bytes.getLittleUInt24((ByteList)this, offsetInBytes);
	}
	
	public default @BoundedInt(min=0, max=16777215) int getUInt24BE(@Nonnegative int offsetInBytes)
	{
		return Bytes.getBigUInt24((ByteList)this, offsetInBytes);
	}
	
	
	public default int getIntLE(@Nonnegative int offsetInBytes)
	{
		return Bytes.getLittleInt((ByteList)this, offsetInBytes);
	}
	
	public default int getIntBE(@Nonnegative int offsetInBytes)
	{
		return Bytes.getBigInt((ByteList)this, offsetInBytes);
	}
	
	
	public default @BoundedLong(min=0, max=1099511627775l) long getULong40LE(@Nonnegative int offsetInBytes)
	{
		return Bytes.getLittleULong40((ByteList)this, offsetInBytes);
	}
	
	public default @BoundedLong(min=0, max=1099511627775l) long getULong40BE(@Nonnegative int offsetInBytes)
	{
		return Bytes.getBigULong40((ByteList)this, offsetInBytes);
	}
	
	
	public default @BoundedLong(min=0, max=281474976710655l) long getULong48LE(@Nonnegative int offsetInBytes)
	{
		return Bytes.getLittleULong48((ByteList)this, offsetInBytes);
	}
	
	public default @BoundedLong(min=0, max=281474976710655l) long getULong48BE(@Nonnegative int offsetInBytes)
	{
		return Bytes.getBigULong48((ByteList)this, offsetInBytes);
	}
	
	
	public default @BoundedLong(min=0, max=72057594037927935l) long getULong56LE(@Nonnegative int offsetInBytes)
	{
		return Bytes.getLittleULong56((ByteList)this, offsetInBytes);
	}
	
	public default @BoundedLong(min=0, max=72057594037927935l) long getULong56BE(@Nonnegative int offsetInBytes)
	{
		return Bytes.getBigULong56((ByteList)this, offsetInBytes);
	}
	
	
	public default long getLongLE(@Nonnegative int offsetInBytes)
	{
		return Bytes.getLittleLong((ByteList)this, offsetInBytes);
	}
	
	public default long getLongBE(@Nonnegative int offsetInBytes)
	{
		return Bytes.getBigLong((ByteList)this, offsetInBytes);
	}
	
	
	
	
	
	
	
	
	public default void setShortLE(@Nonnegative int offsetInBytes, short value)
	{
		Bytes.putLittleShort((ByteList)this, offsetInBytes, value);
	}
	
	public default void setShortBE(@Nonnegative int offsetInBytes, short value)
	{
		Bytes.putBigShort((ByteList)this, offsetInBytes, value);
	}
	
	
	public default void setUInt24LE(@Nonnegative int offsetInBytes, @BoundedInt(min=0, max=16777215) int value)
	{
		Bytes.putLittleInt24((ByteList)this, offsetInBytes, value);
	}
	
	public default void setUInt24BE(@Nonnegative int offsetInBytes, @BoundedInt(min=0, max=16777215) int value)
	{
		Bytes.putBigInt24((ByteList)this, offsetInBytes, value);
	}
	
	
	public default void setIntLE(@Nonnegative int offsetInBytes, int value)
	{
		Bytes.putLittleInt((ByteList)this, offsetInBytes, value);
	}
	
	public default void setIntBE(@Nonnegative int offsetInBytes, int value)
	{
		Bytes.putBigInt((ByteList)this, offsetInBytes, value);
	}
	
	
	public default void setULong40LE(@Nonnegative int offsetInBytes, @BoundedLong(min=0, max=1099511627775l) long value)
	{
		Bytes.putLittleLong40((ByteList)this, offsetInBytes, value);
	}
	
	public default void setULong40BE(@Nonnegative int offsetInBytes, @BoundedLong(min=0, max=1099511627775l) long value)
	{
		Bytes.putBigLong40((ByteList)this, offsetInBytes, value);
	}
	
	
	public default void setULong48LE(@Nonnegative int offsetInBytes, @BoundedLong(min=0, max=281474976710655l) long value)
	{
		Bytes.putLittleLong48((ByteList)this, offsetInBytes, value);
	}
	
	public default void setULong48BE(@Nonnegative int offsetInBytes, @BoundedLong(min=0, max=281474976710655l) long value)
	{
		Bytes.putBigLong48((ByteList)this, offsetInBytes, value);
	}
	
	
	public default void setULong56LE(@Nonnegative int offsetInBytes, @BoundedLong(min=0, max=72057594037927935l) long value)
	{
		Bytes.putLittleLong56((ByteList)this, offsetInBytes, value);
	}
	
	public default void setULong56BE(@Nonnegative int offsetInBytes, @BoundedLong(min=0, max=72057594037927935l) long value)
	{
		Bytes.putBigLong56((ByteList)this, offsetInBytes, value);
	}
	
	
	public default void setLongLE(@Nonnegative int offsetInBytes, long value)
	{
		Bytes.putLittleLong((ByteList)this, offsetInBytes, value);
	}
	
	public default void setLongBE(@Nonnegative int offsetInBytes, long value)
	{
		Bytes.putBigLong((ByteList)this, offsetInBytes, value);
	}
	
	
	
	
	
	
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default char getCharLE(@Nonnegative int offsetInBytes)
	{
		return (char)getShortLE(offsetInBytes);
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default char getCharBE(@Nonnegative int offsetInBytes)
	{
		return (char)getShortBE(offsetInBytes);
	}
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default float getFloatLE(@Nonnegative int offsetInBytes)
	{
		return Float.intBitsToFloat(getIntLE(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default float getFloatBE(@Nonnegative int offsetInBytes)
	{
		return Float.intBitsToFloat(getIntBE(offsetInBytes));
	}
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default double getDoubleLE(@Nonnegative int offsetInBytes)
	{
		return Double.longBitsToDouble(getLongLE(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default double getDoubleBE(@Nonnegative int offsetInBytes)
	{
		return Double.longBitsToDouble(getLongBE(offsetInBytes));
	}
	
	
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setCharLE(@Nonnegative int offsetInBytes, char value)
	{
		setShortLE(offsetInBytes, (short)value);
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setCharBE(@Nonnegative int offsetInBytes, char value)
	{
		setShortBE(offsetInBytes, (short)value);
	}
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setFloatLE(@Nonnegative int offsetInBytes, float value)
	{
		setIntLE(offsetInBytes, Float.floatToRawIntBits(value));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setFloatBE(@Nonnegative int offsetInBytes, float value)
	{
		setIntBE(offsetInBytes, Float.floatToRawIntBits(value));
	}
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setDoubleLE(@Nonnegative int offsetInBytes, double value)
	{
		setLongLE(offsetInBytes, Double.doubleToRawLongBits(value));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setDoubleBE(@Nonnegative int offsetInBytes, double value)
	{
		setLongBE(offsetInBytes, Double.doubleToRawLongBits(value));
	}
}
