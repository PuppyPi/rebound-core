package rebound.util.collections.prim;

import static rebound.bits.BitUtilities.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.annotation.Nonnegative;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.hints.IntendedToBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.hints.IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.simpledata.ActuallySigned;
import rebound.annotations.semantic.simpledata.ActuallyUnsigned;
import rebound.bits.Bytes;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.collections.prim.PrimitiveCollections.DefaultToArraysByteCollection;

//Todo Elementwise boolean operations between ByteLists!!  AND, OR, NOT, XOR!  \:D/

/**
 * This is crucial for contexts where access to a multibyte field *must* take place in a single actual memory access operation, like device-mapped memory!
 * (Because {@link ByteBuffer} very much does not do that in DirectByteBuffer, which is bad for performance as well as making it incapable of being used to write userspace device drivers >_> )
 */
@ImplementationTransparency  //This class only exists to keep the bulk functions exactly uniform and correspondent :3     This might be removed soon XD''
public interface NonuniformMethodsForByteList32
extends DefaultToArraysByteCollection
{
	public byte getByte(@Nonnegative int offsetInBytes);
	public void setByte(@Nonnegative int offsetInBytes, byte value);
	
	
	
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	public default boolean isMultibyteOperationsOverriddenFor32bitOffsets()
	{
		return false;
	}
	
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	public default boolean isMultibyteOperationsAtomicFor32bitOffsets()
	{
		return false;
	}
	
	
	
	
	
	public default short getLittleShort(@Nonnegative int offsetInBytes)
	{
		return Bytes.getLittleShort((ByteList)this, offsetInBytes);
	}
	
	public default @ActuallyUnsigned(24) int getLittleUInt24(@Nonnegative int offsetInBytes)
	{
		return Bytes.getLittleUInt24((ByteList)this, offsetInBytes);
	}
	
	public default int getLittleInt(@Nonnegative int offsetInBytes)
	{
		return Bytes.getLittleInt((ByteList)this, offsetInBytes);
	}
	
	public default @ActuallyUnsigned(40) long getLittleULong40(@Nonnegative int offsetInBytes)
	{
		return Bytes.getLittleULong40((ByteList)this, offsetInBytes);
	}
	
	public default @ActuallyUnsigned(48) long getLittleULong48(@Nonnegative int offsetInBytes)
	{
		return Bytes.getLittleULong48((ByteList)this, offsetInBytes);
	}
	
	public default @ActuallyUnsigned(56) long getLittleULong56(@Nonnegative int offsetInBytes)
	{
		return Bytes.getLittleULong56((ByteList)this, offsetInBytes);
	}
	
	public default long getLittleLong(@Nonnegative int offsetInBytes)
	{
		return Bytes.getLittleLong((ByteList)this, offsetInBytes);
	}
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default char getLittleChar(@Nonnegative int offsetInBytes)
	{
		return (char)getLittleShort(offsetInBytes);
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default float getLittleFloat(@Nonnegative int offsetInBytes)
	{
		return Float.intBitsToFloat(getLittleInt(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default double getLittleDouble(@Nonnegative int offsetInBytes)
	{
		return Double.longBitsToDouble(getLittleLong(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(24) int getLittleSInt24(@Nonnegative int offsetInBytes)
	{
		return signedUpcast24(getLittleUInt24(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(40) long getLittleSLong40(@Nonnegative int offsetInBytes)
	{
		return signedUpcast40(getLittleULong40(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(48) long getLittleSLong48(@Nonnegative int offsetInBytes)
	{
		return signedUpcast48(getLittleULong48(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(56) long getLittleSLong56(@Nonnegative int offsetInBytes)
	{
		return signedUpcast56(getLittleULong56(offsetInBytes));
	}
	
	
	
	public default void setLittleShort(@Nonnegative int offsetInBytes, short value)
	{
		Bytes.putLittleShort((ByteList)this, offsetInBytes, value);
	}
	
	public default void setLittleInt24(@Nonnegative int offsetInBytes, int value)
	{
		Bytes.putLittleInt24((ByteList)this, offsetInBytes, value);
	}
	
	public default void setLittleInt(@Nonnegative int offsetInBytes, int value)
	{
		Bytes.putLittleInt((ByteList)this, offsetInBytes, value);
	}
	
	public default void setLittleLong40(@Nonnegative int offsetInBytes, long value)
	{
		Bytes.putLittleLong40((ByteList)this, offsetInBytes, value);
	}
	
	public default void setLittleLong48(@Nonnegative int offsetInBytes, long value)
	{
		Bytes.putLittleLong48((ByteList)this, offsetInBytes, value);
	}
	
	public default void setLittleLong56(@Nonnegative int offsetInBytes, long value)
	{
		Bytes.putLittleLong56((ByteList)this, offsetInBytes, value);
	}
	
	public default void setLittleLong(@Nonnegative int offsetInBytes, long value)
	{
		Bytes.putLittleLong((ByteList)this, offsetInBytes, value);
	}
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setLittleChar(@Nonnegative int offsetInBytes, char value)
	{
		setLittleShort(offsetInBytes, (short)value);
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setLittleFloat(@Nonnegative int offsetInBytes, float value)
	{
		setLittleInt(offsetInBytes, Float.floatToRawIntBits(value));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setLittleDouble(@Nonnegative int offsetInBytes, double value)
	{
		setLittleLong(offsetInBytes, Double.doubleToRawLongBits(value));
	}
	
	
	
	
	
	
	
	
	
	
	public default short getBigShort(@Nonnegative int offsetInBytes)
	{
		return Bytes.getBigShort((ByteList)this, offsetInBytes);
	}
	
	public default @ActuallyUnsigned(24) int getBigUInt24(@Nonnegative int offsetInBytes)
	{
		return Bytes.getBigUInt24((ByteList)this, offsetInBytes);
	}
	
	public default int getBigInt(@Nonnegative int offsetInBytes)
	{
		return Bytes.getBigInt((ByteList)this, offsetInBytes);
	}
	
	public default @ActuallyUnsigned(40) long getBigULong40(@Nonnegative int offsetInBytes)
	{
		return Bytes.getBigULong40((ByteList)this, offsetInBytes);
	}
	
	public default @ActuallyUnsigned(48) long getBigULong48(@Nonnegative int offsetInBytes)
	{
		return Bytes.getBigULong48((ByteList)this, offsetInBytes);
	}
	
	public default @ActuallyUnsigned(56) long getBigULong56(@Nonnegative int offsetInBytes)
	{
		return Bytes.getBigULong56((ByteList)this, offsetInBytes);
	}
	
	public default long getBigLong(@Nonnegative int offsetInBytes)
	{
		return Bytes.getBigLong((ByteList)this, offsetInBytes);
	}
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default char getBigChar(@Nonnegative int offsetInBytes)
	{
		return (char)getBigShort(offsetInBytes);
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default float getBigFloat(@Nonnegative int offsetInBytes)
	{
		return Float.intBitsToFloat(getBigInt(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default double getBigDouble(@Nonnegative int offsetInBytes)
	{
		return Double.longBitsToDouble(getBigLong(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(24) int getBigSInt24(@Nonnegative int offsetInBytes)
	{
		return signedUpcast24(getBigUInt24(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(40) long getBigSLong40(@Nonnegative int offsetInBytes)
	{
		return signedUpcast40(getBigULong40(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(48) long getBigSLong48(@Nonnegative int offsetInBytes)
	{
		return signedUpcast48(getBigULong48(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(56) long getBigSLong56(@Nonnegative int offsetInBytes)
	{
		return signedUpcast56(getBigULong56(offsetInBytes));
	}
	
	
	
	public default void setBigShort(@Nonnegative int offsetInBytes, short value)
	{
		Bytes.putBigShort((ByteList)this, offsetInBytes, value);
	}
	
	public default void setBigInt24(@Nonnegative int offsetInBytes, int value)
	{
		Bytes.putBigInt24((ByteList)this, offsetInBytes, value);
	}
	
	public default void setBigInt(@Nonnegative int offsetInBytes, int value)
	{
		Bytes.putBigInt((ByteList)this, offsetInBytes, value);
	}
	
	public default void setBigLong40(@Nonnegative int offsetInBytes, long value)
	{
		Bytes.putBigLong40((ByteList)this, offsetInBytes, value);
	}
	
	public default void setBigLong48(@Nonnegative int offsetInBytes, long value)
	{
		Bytes.putBigLong48((ByteList)this, offsetInBytes, value);
	}
	
	public default void setBigLong56(@Nonnegative int offsetInBytes, long value)
	{
		Bytes.putBigLong56((ByteList)this, offsetInBytes, value);
	}
	
	public default void setBigLong(@Nonnegative int offsetInBytes, long value)
	{
		Bytes.putBigLong((ByteList)this, offsetInBytes, value);
	}
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setBigChar(@Nonnegative int offsetInBytes, char value)
	{
		setBigShort(offsetInBytes, (short)value);
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setBigFloat(@Nonnegative int offsetInBytes, float value)
	{
		setBigInt(offsetInBytes, Float.floatToRawIntBits(value));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setBigDouble(@Nonnegative int offsetInBytes, double value)
	{
		setBigLong(offsetInBytes, Double.doubleToRawLongBits(value));
	}
	
	
	
	
	
	
	
	
	
	
	public default short getNativeShort(@Nonnegative int offsetInBytes)
	{
		return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? getLittleShort(offsetInBytes) : getBigShort(offsetInBytes);
	}
	
	public default @ActuallyUnsigned(24) int getNativeUInt24(@Nonnegative int offsetInBytes)
	{
		return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? getLittleUInt24(offsetInBytes) : getBigUInt24(offsetInBytes);
	}
	
	public default int getNativeInt(@Nonnegative int offsetInBytes)
	{
		return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? getLittleInt(offsetInBytes) : getBigInt(offsetInBytes);
	}
	
	public default @ActuallyUnsigned(40) long getNativeULong40(@Nonnegative int offsetInBytes)
	{
		return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? getLittleULong40(offsetInBytes) : getBigULong40(offsetInBytes);
	}
	
	public default @ActuallyUnsigned(48) long getNativeULong48(@Nonnegative int offsetInBytes)
	{
		return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? getLittleULong48(offsetInBytes) : getBigULong48(offsetInBytes);
	}
	
	public default @ActuallyUnsigned(56) long getNativeULong56(@Nonnegative int offsetInBytes)
	{
		return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? getLittleULong56(offsetInBytes) : getBigULong56(offsetInBytes);
	}
	
	public default long getNativeLong(@Nonnegative int offsetInBytes)
	{
		return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? getLittleLong(offsetInBytes) : getBigLong(offsetInBytes);
	}
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default char getNativeChar(@Nonnegative int offsetInBytes)
	{
		return (char)getNativeShort(offsetInBytes);
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default float getNativeFloat(@Nonnegative int offsetInBytes)
	{
		return Float.intBitsToFloat(getNativeInt(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default double getNativeDouble(@Nonnegative int offsetInBytes)
	{
		return Double.longBitsToDouble(getNativeLong(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(24) int getNativeSInt24(@Nonnegative int offsetInBytes)
	{
		return signedUpcast24(getNativeUInt24(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(40) long getNativeSLong40(@Nonnegative int offsetInBytes)
	{
		return signedUpcast40(getNativeULong40(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(48) long getNativeSLong48(@Nonnegative int offsetInBytes)
	{
		return signedUpcast48(getNativeULong48(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(56) long getNativeSLong56(@Nonnegative int offsetInBytes)
	{
		return signedUpcast56(getNativeULong56(offsetInBytes));
	}
	
	
	
	public default void setNativeShort(@Nonnegative int offsetInBytes, short value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleShort(offsetInBytes, value);
		else
			setBigShort(offsetInBytes, value);
	}
	
	public default void setNativeInt24(@Nonnegative int offsetInBytes, int value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleInt24(offsetInBytes, value);
		else
			setBigInt24(offsetInBytes, value);
	}
	
	public default void setNativeInt(@Nonnegative int offsetInBytes, int value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleInt(offsetInBytes, value);
		else
			setBigInt(offsetInBytes, value);
	}
	
	public default void setNativeLong40(@Nonnegative int offsetInBytes, long value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleLong40(offsetInBytes, value);
		else
			setBigLong40(offsetInBytes, value);
	}
	
	public default void setNativeLong48(@Nonnegative int offsetInBytes, long value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleLong48(offsetInBytes, value);
		else
			setBigLong48(offsetInBytes, value);
	}
	
	public default void setNativeLong56(@Nonnegative int offsetInBytes, long value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleLong56(offsetInBytes, value);
		else
			setBigLong56(offsetInBytes, value);
	}
	
	public default void setNativeLong(@Nonnegative int offsetInBytes, long value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleLong(offsetInBytes, value);
		else
			setBigLong(offsetInBytes, value);
	}
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setNativeChar(@Nonnegative int offsetInBytes, char value)
	{
		setNativeShort(offsetInBytes, (short)value);
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setNativeFloat(@Nonnegative int offsetInBytes, float value)
	{
		setNativeInt(offsetInBytes, Float.floatToRawIntBits(value));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setNativeDouble(@Nonnegative int offsetInBytes, double value)
	{
		setNativeLong(offsetInBytes, Double.doubleToRawLongBits(value));
	}
}
