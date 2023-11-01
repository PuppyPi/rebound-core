package rebound.util.collections.prim;

import static rebound.bits.BitUtilities.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.hints.IntendedToBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.hints.IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.simpledata.ActuallySigned;
import rebound.annotations.semantic.simpledata.ActuallyUnsigned;
import rebound.annotations.semantic.simpledata.BoundedInt;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.DefaultToArraysByteCollection;

//Todo Elementwise boolean operations between ByteLists!!  AND, OR, NOT, XOR!  \:D/

/**
 * This is crucial for contexts where access to a multibyte field *must* take place in a single actual memory access operation, like device-mapped memory!
 * (Because {@link ByteBuffer} very much does not do that in DirectByteBuffer, which is bad for performance as well as making it incapable of being used to write userspace device drivers >_> )
 */
@ImplementationTransparency  //This class only exists to keep the bulk functions exactly uniform and correspondent :3     This might be removed soon XD''
public interface NonuniformMethodsForByteList64
extends DefaultToArraysByteCollection
{
	public @ActuallyUnsigned long size64();
	public byte getByteBy64(@ActuallyUnsigned long offsetInBytes);
	public void setByteBy64(@ActuallyUnsigned long offsetInBytes, byte value);
	
	
	
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	public default SpanningOperationImplementationType getMultibyteOperationsImplementationGuaranteesFor64bitOffsetsBy64()
	{
		return SpanningOperationImplementationType.OneByOne;
	}
	
	
	
	
	
	public default short getLittleShortBy64(@ActuallyUnsigned long offsetInBytes)
	{
		short rv = 0;
		rv |= (getByteBy64(offsetInBytes+0) & 0xFF) << 0;
		rv |= (getByteBy64(offsetInBytes+1) & 0xFF) << 8;
		return rv;
	}
	
	public default @ActuallyUnsigned(24) int getLittleUInt24By64(@ActuallyUnsigned long offsetInBytes)
	{
		int rv = 0;
		rv |= (getByteBy64(offsetInBytes+0) & 0xFF) << 0;
		rv |= (getByteBy64(offsetInBytes+1) & 0xFF) << 8;
		rv |= (getByteBy64(offsetInBytes+2) & 0xFF) << 16;
		return rv;
	}
	
	public default int getLittleIntBy64(@ActuallyUnsigned long offsetInBytes)
	{
		int rv = 0;
		rv |= (getByteBy64(offsetInBytes+0) & 0xFF) << 0;
		rv |= (getByteBy64(offsetInBytes+1) & 0xFF) << 8;
		rv |= (getByteBy64(offsetInBytes+2) & 0xFF) << 16;
		rv |= (getByteBy64(offsetInBytes+3) & 0xFF) << 24;
		return rv;
	}
	
	public default @ActuallyUnsigned(40) long getLittleULong40By64(@ActuallyUnsigned long offsetInBytes)
	{
		long rv = 0;
		rv |= (getByteBy64(offsetInBytes+0) & 0xFFl) << 0;
		rv |= (getByteBy64(offsetInBytes+1) & 0xFFl) << 8;
		rv |= (getByteBy64(offsetInBytes+2) & 0xFFl) << 16;
		rv |= (getByteBy64(offsetInBytes+3) & 0xFFl) << 24;
		rv |= (getByteBy64(offsetInBytes+4) & 0xFFl) << 32;
		return rv;
	}
	
	public default @ActuallyUnsigned(48) long getLittleULong48By64(@ActuallyUnsigned long offsetInBytes)
	{
		long rv = 0;
		rv |= (getByteBy64(offsetInBytes+0) & 0xFFl) << 0;
		rv |= (getByteBy64(offsetInBytes+1) & 0xFFl) << 8;
		rv |= (getByteBy64(offsetInBytes+2) & 0xFFl) << 16;
		rv |= (getByteBy64(offsetInBytes+3) & 0xFFl) << 24;
		rv |= (getByteBy64(offsetInBytes+4) & 0xFFl) << 32;
		rv |= (getByteBy64(offsetInBytes+5) & 0xFFl) << 40;
		return rv;
	}
	
	public default @ActuallyUnsigned(56) long getLittleULong56By64(@ActuallyUnsigned long offsetInBytes)
	{
		long rv = 0;
		rv |= (getByteBy64(offsetInBytes+0) & 0xFFl) << 0;
		rv |= (getByteBy64(offsetInBytes+1) & 0xFFl) << 8;
		rv |= (getByteBy64(offsetInBytes+2) & 0xFFl) << 16;
		rv |= (getByteBy64(offsetInBytes+3) & 0xFFl) << 24;
		rv |= (getByteBy64(offsetInBytes+4) & 0xFFl) << 32;
		rv |= (getByteBy64(offsetInBytes+5) & 0xFFl) << 40;
		rv |= (getByteBy64(offsetInBytes+6) & 0xFFl) << 48;
		return rv;
	}
	
	public default long getLittleLongBy64(@ActuallyUnsigned long offsetInBytes)
	{
		long rv = 0;
		rv |= (getByteBy64(offsetInBytes+0) & 0xFFl) << 0;
		rv |= (getByteBy64(offsetInBytes+1) & 0xFFl) << 8;
		rv |= (getByteBy64(offsetInBytes+2) & 0xFFl) << 16;
		rv |= (getByteBy64(offsetInBytes+3) & 0xFFl) << 24;
		rv |= (getByteBy64(offsetInBytes+4) & 0xFFl) << 32;
		rv |= (getByteBy64(offsetInBytes+5) & 0xFFl) << 40;
		rv |= (getByteBy64(offsetInBytes+6) & 0xFFl) << 48;
		rv |= (getByteBy64(offsetInBytes+7) & 0xFFl) << 56;
		return rv;
	}
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default char getLittleCharBy64(@ActuallyUnsigned long offsetInBytes)
	{
		return (char)getLittleShortBy64(offsetInBytes);
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default float getLittleFloatBy64(@ActuallyUnsigned long offsetInBytes)
	{
		return Float.intBitsToFloat(getLittleIntBy64(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default double getLittleDoubleBy64(@ActuallyUnsigned long offsetInBytes)
	{
		return Double.longBitsToDouble(getLittleLongBy64(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(24) int getLittleSInt24By64(@ActuallyUnsigned long offsetInBytes)
	{
		return signedUpcast24(getLittleUInt24By64(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(40) long getLittleSLong40By64(@ActuallyUnsigned long offsetInBytes)
	{
		return signedUpcast40(getLittleULong40By64(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(48) long getLittleSLong48By64(@ActuallyUnsigned long offsetInBytes)
	{
		return signedUpcast48(getLittleULong48By64(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(56) long getLittleSLong56By64(@ActuallyUnsigned long offsetInBytes)
	{
		return signedUpcast56(getLittleULong56By64(offsetInBytes));
	}
	
	
	
	public default void setLittleShort(@ActuallyUnsigned long offsetInBytes, short value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 0) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 8) & 0xFF));
	}
	
	public default void setLittleInt24(@ActuallyUnsigned long offsetInBytes, int value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 0) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 16) & 0xFF));
	}
	
	public default void setLittleInt(@ActuallyUnsigned long offsetInBytes, int value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 0) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 16) & 0xFF));
		setByteBy64(offsetInBytes+3, (byte)((value >>> 24) & 0xFF));
	}
	
	public default void setLittleLong40(@ActuallyUnsigned long offsetInBytes, long value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 0) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 16) & 0xFF));
		setByteBy64(offsetInBytes+3, (byte)((value >>> 24) & 0xFF));
		setByteBy64(offsetInBytes+4, (byte)((value >>> 32) & 0xFF));
	}
	
	public default void setLittleLong48(@ActuallyUnsigned long offsetInBytes, long value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 0) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 16) & 0xFF));
		setByteBy64(offsetInBytes+3, (byte)((value >>> 24) & 0xFF));
		setByteBy64(offsetInBytes+4, (byte)((value >>> 32) & 0xFF));
		setByteBy64(offsetInBytes+5, (byte)((value >>> 40) & 0xFF));
	}
	
	public default void setLittleLong56(@ActuallyUnsigned long offsetInBytes, long value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 0) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 16) & 0xFF));
		setByteBy64(offsetInBytes+3, (byte)((value >>> 24) & 0xFF));
		setByteBy64(offsetInBytes+4, (byte)((value >>> 32) & 0xFF));
		setByteBy64(offsetInBytes+5, (byte)((value >>> 40) & 0xFF));
		setByteBy64(offsetInBytes+6, (byte)((value >>> 48) & 0xFF));
	}
	
	public default void setLittleLong(@ActuallyUnsigned long offsetInBytes, long value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 0) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 16) & 0xFF));
		setByteBy64(offsetInBytes+3, (byte)((value >>> 24) & 0xFF));
		setByteBy64(offsetInBytes+4, (byte)((value >>> 32) & 0xFF));
		setByteBy64(offsetInBytes+5, (byte)((value >>> 40) & 0xFF));
		setByteBy64(offsetInBytes+6, (byte)((value >>> 48) & 0xFF));
		setByteBy64(offsetInBytes+7, (byte)((value >>> 56) & 0xFF));
	}
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setLittleChar(@ActuallyUnsigned long offsetInBytes, char value)
	{
		setLittleShort(offsetInBytes, (short)value);
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setLittleFloat(@ActuallyUnsigned long offsetInBytes, float value)
	{
		setLittleInt(offsetInBytes, Float.floatToRawIntBits(value));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setLittleDouble(@ActuallyUnsigned long offsetInBytes, double value)
	{
		setLittleLong(offsetInBytes, Double.doubleToRawLongBits(value));
	}
	
	
	
	
	
	
	
	
	
	
	public default short getBigShortBy64(@ActuallyUnsigned long offsetInBytes)
	{
		short rv = 0;
		rv |= (getByteBy64(offsetInBytes+0) & 0xFF) << 8;
		rv |= (getByteBy64(offsetInBytes+1) & 0xFF) << 0;
		return rv;
	}
	
	public default @ActuallyUnsigned(24) int getBigUInt24By64(@ActuallyUnsigned long offsetInBytes)
	{
		int rv = 0;
		rv |= (getByteBy64(offsetInBytes+0) & 0xFF) << 16;
		rv |= (getByteBy64(offsetInBytes+1) & 0xFF) << 8;
		rv |= (getByteBy64(offsetInBytes+2) & 0xFF) << 0;
		return rv;
	}
	
	public default int getBigIntBy64(@ActuallyUnsigned long offsetInBytes)
	{
		int rv = 0;
		rv |= (getByteBy64(offsetInBytes+0) & 0xFF) << 24;
		rv |= (getByteBy64(offsetInBytes+1) & 0xFF) << 16;
		rv |= (getByteBy64(offsetInBytes+2) & 0xFF) << 8;
		rv |= (getByteBy64(offsetInBytes+3) & 0xFF) << 0;
		return rv;
	}
	
	public default @ActuallyUnsigned(40) long getBigULong40By64(@ActuallyUnsigned long offsetInBytes)
	{
		long rv = 0;
		rv |= (getByteBy64(offsetInBytes+0) & 0xFFl) << 32;
		rv |= (getByteBy64(offsetInBytes+1) & 0xFFl) << 24;
		rv |= (getByteBy64(offsetInBytes+2) & 0xFFl) << 16;
		rv |= (getByteBy64(offsetInBytes+3) & 0xFFl) << 8;
		rv |= (getByteBy64(offsetInBytes+4) & 0xFFl) << 0;
		return rv;
	}
	
	public default @ActuallyUnsigned(48) long getBigULong48By64(@ActuallyUnsigned long offsetInBytes)
	{
		long rv = 0;
		rv |= (getByteBy64(offsetInBytes+0) & 0xFFl) << 40;
		rv |= (getByteBy64(offsetInBytes+1) & 0xFFl) << 32;
		rv |= (getByteBy64(offsetInBytes+2) & 0xFFl) << 24;
		rv |= (getByteBy64(offsetInBytes+3) & 0xFFl) << 16;
		rv |= (getByteBy64(offsetInBytes+4) & 0xFFl) << 8;
		rv |= (getByteBy64(offsetInBytes+5) & 0xFFl) << 0;
		return rv;
	}
	
	public default @ActuallyUnsigned(56) long getBigULong56By64(@ActuallyUnsigned long offsetInBytes)
	{
		long rv = 0;
		rv |= (getByteBy64(offsetInBytes+0) & 0xFFl) << 48;
		rv |= (getByteBy64(offsetInBytes+1) & 0xFFl) << 40;
		rv |= (getByteBy64(offsetInBytes+2) & 0xFFl) << 32;
		rv |= (getByteBy64(offsetInBytes+3) & 0xFFl) << 24;
		rv |= (getByteBy64(offsetInBytes+4) & 0xFFl) << 16;
		rv |= (getByteBy64(offsetInBytes+5) & 0xFFl) << 8;
		rv |= (getByteBy64(offsetInBytes+6) & 0xFFl) << 0;
		return rv;
	}
	
	public default long getBigLongBy64(@ActuallyUnsigned long offsetInBytes)
	{
		long rv = 0;
		rv |= (getByteBy64(offsetInBytes+0) & 0xFFl) << 56;
		rv |= (getByteBy64(offsetInBytes+1) & 0xFFl) << 48;
		rv |= (getByteBy64(offsetInBytes+2) & 0xFFl) << 40;
		rv |= (getByteBy64(offsetInBytes+3) & 0xFFl) << 32;
		rv |= (getByteBy64(offsetInBytes+4) & 0xFFl) << 24;
		rv |= (getByteBy64(offsetInBytes+5) & 0xFFl) << 16;
		rv |= (getByteBy64(offsetInBytes+6) & 0xFFl) << 8;
		rv |= (getByteBy64(offsetInBytes+7) & 0xFFl) << 0;
		return rv;
	}
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default char getBigCharBy64(@ActuallyUnsigned long offsetInBytes)
	{
		return (char)getBigShortBy64(offsetInBytes);
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default float getBigFloatBy64(@ActuallyUnsigned long offsetInBytes)
	{
		return Float.intBitsToFloat(getBigIntBy64(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default double getBigDoubleBy64(@ActuallyUnsigned long offsetInBytes)
	{
		return Double.longBitsToDouble(getBigLongBy64(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(24) int getBigSInt24By64(@ActuallyUnsigned long offsetInBytes)
	{
		return signedUpcast24(getBigUInt24By64(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(40) long getBigSLong40By64(@ActuallyUnsigned long offsetInBytes)
	{
		return signedUpcast40(getBigULong40By64(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(48) long getBigSLong48By64(@ActuallyUnsigned long offsetInBytes)
	{
		return signedUpcast48(getBigULong48By64(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(56) long getBigSLong56By64(@ActuallyUnsigned long offsetInBytes)
	{
		return signedUpcast56(getBigULong56By64(offsetInBytes));
	}
	
	
	
	public default void setBigShort(@ActuallyUnsigned long offsetInBytes, short value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 0) & 0xFF));
	}
	
	public default void setBigInt24(@ActuallyUnsigned long offsetInBytes, int value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 16) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 0) & 0xFF));
	}
	
	public default void setBigInt(@ActuallyUnsigned long offsetInBytes, int value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 24) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 16) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+3, (byte)((value >>> 0) & 0xFF));
	}
	
	public default void setBigLong40(@ActuallyUnsigned long offsetInBytes, long value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 32) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 24) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 16) & 0xFF));
		setByteBy64(offsetInBytes+3, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+4, (byte)((value >>> 0) & 0xFF));
	}
	
	public default void setBigLong48(@ActuallyUnsigned long offsetInBytes, long value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 40) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 32) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 24) & 0xFF));
		setByteBy64(offsetInBytes+3, (byte)((value >>> 16) & 0xFF));
		setByteBy64(offsetInBytes+4, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+5, (byte)((value >>> 0) & 0xFF));
	}
	
	public default void setBigLong56(@ActuallyUnsigned long offsetInBytes, long value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 48) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 40) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 32) & 0xFF));
		setByteBy64(offsetInBytes+3, (byte)((value >>> 24) & 0xFF));
		setByteBy64(offsetInBytes+4, (byte)((value >>> 16) & 0xFF));
		setByteBy64(offsetInBytes+5, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+6, (byte)((value >>> 0) & 0xFF));
	}
	
	public default void setBigLong(@ActuallyUnsigned long offsetInBytes, long value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 56) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 48) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 40) & 0xFF));
		setByteBy64(offsetInBytes+3, (byte)((value >>> 32) & 0xFF));
		setByteBy64(offsetInBytes+4, (byte)((value >>> 24) & 0xFF));
		setByteBy64(offsetInBytes+5, (byte)((value >>> 16) & 0xFF));
		setByteBy64(offsetInBytes+6, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+7, (byte)((value >>> 0) & 0xFF));
	}
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setBigChar(@ActuallyUnsigned long offsetInBytes, char value)
	{
		setBigShort(offsetInBytes, (short)value);
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setBigFloat(@ActuallyUnsigned long offsetInBytes, float value)
	{
		setBigInt(offsetInBytes, Float.floatToRawIntBits(value));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setBigDouble(@ActuallyUnsigned long offsetInBytes, double value)
	{
		setBigLong(offsetInBytes, Double.doubleToRawLongBits(value));
	}
	
	
	
	
	
	
	
	
	
	
	public default short getNativeShortBy64(@ActuallyUnsigned long offsetInBytes)
	{
		return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? getLittleShortBy64(offsetInBytes) : getBigShortBy64(offsetInBytes);
	}
	
	public default @ActuallyUnsigned(24) int getNativeUInt24By64(@ActuallyUnsigned long offsetInBytes)
	{
		return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? getLittleUInt24By64(offsetInBytes) : getBigUInt24By64(offsetInBytes);
	}
	
	public default int getNativeIntBy64(@ActuallyUnsigned long offsetInBytes)
	{
		return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? getLittleIntBy64(offsetInBytes) : getBigIntBy64(offsetInBytes);
	}
	
	public default @ActuallyUnsigned(40) long getNativeULong40By64(@ActuallyUnsigned long offsetInBytes)
	{
		return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? getLittleULong40By64(offsetInBytes) : getBigULong40By64(offsetInBytes);
	}
	
	public default @ActuallyUnsigned(48) long getNativeULong48By64(@ActuallyUnsigned long offsetInBytes)
	{
		return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? getLittleULong48By64(offsetInBytes) : getBigULong48By64(offsetInBytes);
	}
	
	public default @ActuallyUnsigned(56) long getNativeULong56By64(@ActuallyUnsigned long offsetInBytes)
	{
		return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? getLittleULong56By64(offsetInBytes) : getBigULong56By64(offsetInBytes);
	}
	
	public default long getNativeLongBy64(@ActuallyUnsigned long offsetInBytes)
	{
		return ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? getLittleLongBy64(offsetInBytes) : getBigLongBy64(offsetInBytes);
	}
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default char getNativeCharBy64(@ActuallyUnsigned long offsetInBytes)
	{
		return (char)getNativeShortBy64(offsetInBytes);
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default float getNativeFloatBy64(@ActuallyUnsigned long offsetInBytes)
	{
		return Float.intBitsToFloat(getNativeIntBy64(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default double getNativeDoubleBy64(@ActuallyUnsigned long offsetInBytes)
	{
		return Double.longBitsToDouble(getNativeLongBy64(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(24) int getNativeSInt24By64(@ActuallyUnsigned long offsetInBytes)
	{
		return signedUpcast24(getNativeUInt24By64(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(40) long getNativeSLong40By64(@ActuallyUnsigned long offsetInBytes)
	{
		return signedUpcast40(getNativeULong40By64(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(48) long getNativeSLong48By64(@ActuallyUnsigned long offsetInBytes)
	{
		return signedUpcast48(getNativeULong48By64(offsetInBytes));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default @ActuallySigned(56) long getNativeSLong56By64(@ActuallyUnsigned long offsetInBytes)
	{
		return signedUpcast56(getNativeULong56By64(offsetInBytes));
	}
	
	
	
	public default void setNativeShort(@ActuallyUnsigned long offsetInBytes, short value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleShort(offsetInBytes, value);
		else
			setBigShort(offsetInBytes, value);
	}
	
	public default void setNativeInt24(@ActuallyUnsigned long offsetInBytes, int value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleInt24(offsetInBytes, value);
		else
			setBigInt24(offsetInBytes, value);
	}
	
	public default void setNativeInt(@ActuallyUnsigned long offsetInBytes, int value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleInt(offsetInBytes, value);
		else
			setBigInt(offsetInBytes, value);
	}
	
	public default void setNativeLong40(@ActuallyUnsigned long offsetInBytes, long value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleLong40(offsetInBytes, value);
		else
			setBigLong40(offsetInBytes, value);
	}
	
	public default void setNativeLong48(@ActuallyUnsigned long offsetInBytes, long value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleLong48(offsetInBytes, value);
		else
			setBigLong48(offsetInBytes, value);
	}
	
	public default void setNativeLong56(@ActuallyUnsigned long offsetInBytes, long value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleLong56(offsetInBytes, value);
		else
			setBigLong56(offsetInBytes, value);
	}
	
	public default void setNativeLong(@ActuallyUnsigned long offsetInBytes, long value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleLong(offsetInBytes, value);
		else
			setBigLong(offsetInBytes, value);
	}
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setNativeChar(@ActuallyUnsigned long offsetInBytes, char value)
	{
		setNativeShort(offsetInBytes, (short)value);
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setNativeFloat(@ActuallyUnsigned long offsetInBytes, float value)
	{
		setNativeInt(offsetInBytes, Float.floatToRawIntBits(value));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setNativeDouble(@ActuallyUnsigned long offsetInBytes, double value)
	{
		setNativeLong(offsetInBytes, Double.doubleToRawLongBits(value));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public default void setArrayBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull byte[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 1;
		
		for (int i = 0; i < sourceLength; i++)
			setByteBy64(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setArrayBy64(@ReadonlyValue @Nonnull byte[] source)
	{
		setArrayBy64(0, source, 0, source.length);
	}
	
	public default void setArrayFromSliceByteBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull Slice<byte[]> source)
	{
		setArrayBy64(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public default void setArrayFromSliceByteBy64(@ReadonlyValue @Nonnull Slice<byte[]> source)
	{
		setArrayFromSliceByteBy64(0, source);
	}
	
	
	
	public default void getArrayBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull byte[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 1;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (byte)getByteBy64(sourceByteOffset+i*primbytelen);
	}
	
	public default void getArrayBy64(@WritableValue @Nonnull byte[] dest)
	{
		getArrayBy64(0, dest, 0, dest.length);
	}
	
	public default void getArrayFromSliceByteBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull Slice<byte[]> dest)
	{
		getArrayBy64(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	public default void getArrayFromSliceByteBy64(@WritableValue @Nonnull Slice<byte[]> dest)
	{
		getArrayFromSliceByteBy64(0, dest);
	}
	
	
	
	
	
	
	
	/* <<<
	primxp
	_$$primxpconf:multibyteints$$_
	
	public default void setLittleArray(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull _$$prim$$_[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = _$$primbytelen$$_;
		
		for (int i = 0; i < sourceLength; i++)
			setLittle_$$Prim$$_(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setLittleArray(@ReadonlyValue @Nonnull _$$prim$$_[] source)
	{
		setLittleArray(0, source, 0, source.length);
	}
	
	public default void setLittleArrayFromSlice_$$Prim$$_(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull Slice<_$$prim$$_[]> source)
	{
		setLittleArray(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public default void setLittleArrayFromSlice_$$Prim$$_(@ReadonlyValue @Nonnull Slice<_$$prim$$_[]> source)
	{
		setLittleArrayFromSlice_$$Prim$$_(0, source);
	}
	
	
	
	public default void getLittleArrayBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull _$$prim$$_[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = _$$primbytelen$$_;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (_$$prim$$_)getLittle_$$Prim$$_By64(sourceByteOffset+i*primbytelen);
	}
	
	public default void getLittleArrayBy64(@WritableValue @Nonnull _$$prim$$_[] dest)
	{
		getLittleArrayBy64(0, dest, 0, dest.length);
	}
	
	public default void getLittleArrayFromSlice_$$Prim$$_By64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull Slice<_$$prim$$_[]> dest)
	{
		getLittleArrayBy64(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	public default void getLittleArrayFromSlice_$$Prim$$_By64(@WritableValue @Nonnull Slice<_$$prim$$_[]> dest)
	{
		getLittleArrayFromSlice_$$Prim$$_By64(0, dest);
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setBigArray(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull _$$prim$$_[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = _$$primbytelen$$_;
		
		for (int i = 0; i < sourceLength; i++)
			setBig_$$Prim$$_(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setBigArray(@ReadonlyValue @Nonnull _$$prim$$_[] source)
	{
		setBigArray(0, source, 0, source.length);
	}
	
	public default void setBigArrayFromSlice_$$Prim$$_(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull Slice<_$$prim$$_[]> source)
	{
		setBigArray(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public default void setBigArrayFromSlice_$$Prim$$_(@ReadonlyValue @Nonnull Slice<_$$prim$$_[]> source)
	{
		setBigArrayFromSlice_$$Prim$$_(0, source);
	}
	
	
	
	public default void getBigArrayBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull _$$prim$$_[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = _$$primbytelen$$_;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (_$$prim$$_)getBig_$$Prim$$_By64(sourceByteOffset+i*primbytelen);
	}
	
	public default void getBigArrayBy64(@WritableValue @Nonnull _$$prim$$_[] dest)
	{
		getBigArrayBy64(0, dest, 0, dest.length);
	}
	
	public default void getBigArrayFromSlice_$$Prim$$_By64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull Slice<_$$prim$$_[]> dest)
	{
		getBigArrayBy64(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	public default void getBigArrayFromSlice_$$Prim$$_By64(@WritableValue @Nonnull Slice<_$$prim$$_[]> dest)
	{
		getBigArrayFromSlice_$$Prim$$_By64(0, dest);
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setNativeArray(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull _$$prim$$_[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = _$$primbytelen$$_;
		
		for (int i = 0; i < sourceLength; i++)
			setNative_$$Prim$$_(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setNativeArray(@ReadonlyValue @Nonnull _$$prim$$_[] source)
	{
		setNativeArray(0, source, 0, source.length);
	}
	
	public default void setNativeArrayFromSlice_$$Prim$$_(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull Slice<_$$prim$$_[]> source)
	{
		setNativeArray(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public default void setNativeArrayFromSlice_$$Prim$$_(@ReadonlyValue @Nonnull Slice<_$$prim$$_[]> source)
	{
		setNativeArrayFromSlice_$$Prim$$_(0, source);
	}
	
	
	
	public default void getNativeArrayBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull _$$prim$$_[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = _$$primbytelen$$_;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (_$$prim$$_)getNative_$$Prim$$_By64(sourceByteOffset+i*primbytelen);
	}
	
	public default void getNativeArrayBy64(@WritableValue @Nonnull _$$prim$$_[] dest)
	{
		getNativeArrayBy64(0, dest, 0, dest.length);
	}
	
	public default void getNativeArrayFromSlice_$$Prim$$_By64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull Slice<_$$prim$$_[]> dest)
	{
		getNativeArrayBy64(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	public default void getNativeArrayFromSlice_$$Prim$$_By64(@WritableValue @Nonnull Slice<_$$prim$$_[]> dest)
	{
		getNativeArrayFromSlice_$$Prim$$_By64(0, dest);
	}
	
	
	
	
	
	
	
	
	
	
	
	 */
	// >>>
}
