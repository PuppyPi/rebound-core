package rebound.util.collections.prim;

import static rebound.bits.BitUtilities.*;
import static rebound.bits.BitfieldSafeCasts.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.util.collections.CollectionUtilities.*;
import static rebound.util.collections.prim.PrimitiveCollections.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.hints.IntendedToBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.hints.IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.annotations.semantic.simpledata.ActuallySigned;
import rebound.annotations.semantic.simpledata.ActuallyUnsigned;
import rebound.annotations.semantic.simpledata.BoundedInt;
import rebound.exceptions.OverflowException;
import rebound.util.Primitives;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
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
	/**
	 * The default implementation is just for legacy implementations that only support 32-bit (really 31-bit) indexes.
	 * Note that if this can return more than {@link Integer#MAX_VALUE}, then {@link #size()} must *throw an exception not truncate it to MAX_VALUE* otherwise silent
	 * errors will ensue!  And that can cause data corruption and be worse than loud errors!
	 */
	public @ActuallyUnsigned long size64();
	
	public byte getByteBy64(@ActuallyUnsigned long offsetInBytes);
	public void setByteBy64(@ActuallyUnsigned long offsetInBytes, byte value);
	
	
    public default ByteList subListBy64(@ActuallyUnsigned long fromIndex, @ActuallyUnsigned long toIndex)
    {
    	if (toIndex == 0)
    		return subListBy64i(fromIndex, toIndex);
    	else
    		return subListBy64i(fromIndex, toIndex-1);
    }
    
    /**
     * @param toIndexInclusive  This being inclusive means you can use {@link Primitives#U64_MAX_VALUE} for it if the list covers the entire address space!!
     */
    public ByteList subListBy64i(@ActuallyUnsigned long fromIndex, @ActuallyUnsigned long toIndexInclusive);
	
	
	
	
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	public default SpanningOperationImplementationType getMultibyteOperationsImplementationGuaranteesFor64bitOffsetsBy64()
	{
		return SpanningOperationImplementationType.Piecemeal;
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
	
	
	
	public default void setLittleShortBy64(@ActuallyUnsigned long offsetInBytes, short value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 0) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 8) & 0xFF));
	}
	
	public default void setLittleInt24By64(@ActuallyUnsigned long offsetInBytes, int value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 0) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 16) & 0xFF));
	}
	
	public default void setLittleIntBy64(@ActuallyUnsigned long offsetInBytes, int value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 0) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 16) & 0xFF));
		setByteBy64(offsetInBytes+3, (byte)((value >>> 24) & 0xFF));
	}
	
	public default void setLittleLong40By64(@ActuallyUnsigned long offsetInBytes, long value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 0) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 16) & 0xFF));
		setByteBy64(offsetInBytes+3, (byte)((value >>> 24) & 0xFF));
		setByteBy64(offsetInBytes+4, (byte)((value >>> 32) & 0xFF));
	}
	
	public default void setLittleLong48By64(@ActuallyUnsigned long offsetInBytes, long value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 0) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 16) & 0xFF));
		setByteBy64(offsetInBytes+3, (byte)((value >>> 24) & 0xFF));
		setByteBy64(offsetInBytes+4, (byte)((value >>> 32) & 0xFF));
		setByteBy64(offsetInBytes+5, (byte)((value >>> 40) & 0xFF));
	}
	
	public default void setLittleLong56By64(@ActuallyUnsigned long offsetInBytes, long value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 0) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 16) & 0xFF));
		setByteBy64(offsetInBytes+3, (byte)((value >>> 24) & 0xFF));
		setByteBy64(offsetInBytes+4, (byte)((value >>> 32) & 0xFF));
		setByteBy64(offsetInBytes+5, (byte)((value >>> 40) & 0xFF));
		setByteBy64(offsetInBytes+6, (byte)((value >>> 48) & 0xFF));
	}
	
	public default void setLittleLongBy64(@ActuallyUnsigned long offsetInBytes, long value)
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
	public default void setLittleCharBy64(@ActuallyUnsigned long offsetInBytes, char value)
	{
		setLittleShortBy64(offsetInBytes, (short)value);
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setLittleFloatBy64(@ActuallyUnsigned long offsetInBytes, float value)
	{
		setLittleIntBy64(offsetInBytes, Float.floatToRawIntBits(value));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setLittleDoubleBy64(@ActuallyUnsigned long offsetInBytes, double value)
	{
		setLittleLongBy64(offsetInBytes, Double.doubleToRawLongBits(value));
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
	
	
	
	public default void setBigShortBy64(@ActuallyUnsigned long offsetInBytes, short value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 0) & 0xFF));
	}
	
	public default void setBigInt24By64(@ActuallyUnsigned long offsetInBytes, int value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 16) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 0) & 0xFF));
	}
	
	public default void setBigIntBy64(@ActuallyUnsigned long offsetInBytes, int value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 24) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 16) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+3, (byte)((value >>> 0) & 0xFF));
	}
	
	public default void setBigLong40By64(@ActuallyUnsigned long offsetInBytes, long value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 32) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 24) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 16) & 0xFF));
		setByteBy64(offsetInBytes+3, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+4, (byte)((value >>> 0) & 0xFF));
	}
	
	public default void setBigLong48By64(@ActuallyUnsigned long offsetInBytes, long value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 40) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 32) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 24) & 0xFF));
		setByteBy64(offsetInBytes+3, (byte)((value >>> 16) & 0xFF));
		setByteBy64(offsetInBytes+4, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+5, (byte)((value >>> 0) & 0xFF));
	}
	
	public default void setBigLong56By64(@ActuallyUnsigned long offsetInBytes, long value)
	{
		setByteBy64(offsetInBytes+0, (byte)((value >>> 48) & 0xFF));
		setByteBy64(offsetInBytes+1, (byte)((value >>> 40) & 0xFF));
		setByteBy64(offsetInBytes+2, (byte)((value >>> 32) & 0xFF));
		setByteBy64(offsetInBytes+3, (byte)((value >>> 24) & 0xFF));
		setByteBy64(offsetInBytes+4, (byte)((value >>> 16) & 0xFF));
		setByteBy64(offsetInBytes+5, (byte)((value >>> 8) & 0xFF));
		setByteBy64(offsetInBytes+6, (byte)((value >>> 0) & 0xFF));
	}
	
	public default void setBigLongBy64(@ActuallyUnsigned long offsetInBytes, long value)
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
	public default void setBigCharBy64(@ActuallyUnsigned long offsetInBytes, char value)
	{
		setBigShortBy64(offsetInBytes, (short)value);
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setBigFloatBy64(@ActuallyUnsigned long offsetInBytes, float value)
	{
		setBigIntBy64(offsetInBytes, Float.floatToRawIntBits(value));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setBigDoubleBy64(@ActuallyUnsigned long offsetInBytes, double value)
	{
		setBigLongBy64(offsetInBytes, Double.doubleToRawLongBits(value));
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
	
	
	
	public default void setNativeShortBy64(@ActuallyUnsigned long offsetInBytes, short value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleShortBy64(offsetInBytes, value);
		else
			setBigShortBy64(offsetInBytes, value);
	}
	
	public default void setNativeInt24By64(@ActuallyUnsigned long offsetInBytes, int value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleInt24By64(offsetInBytes, value);
		else
			setBigInt24By64(offsetInBytes, value);
	}
	
	public default void setNativeIntBy64(@ActuallyUnsigned long offsetInBytes, int value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleIntBy64(offsetInBytes, value);
		else
			setBigIntBy64(offsetInBytes, value);
	}
	
	public default void setNativeLong40By64(@ActuallyUnsigned long offsetInBytes, long value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleLong40By64(offsetInBytes, value);
		else
			setBigLong40By64(offsetInBytes, value);
	}
	
	public default void setNativeLong48By64(@ActuallyUnsigned long offsetInBytes, long value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleLong48By64(offsetInBytes, value);
		else
			setBigLong48By64(offsetInBytes, value);
	}
	
	public default void setNativeLong56By64(@ActuallyUnsigned long offsetInBytes, long value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleLong56By64(offsetInBytes, value);
		else
			setBigLong56By64(offsetInBytes, value);
	}
	
	public default void setNativeLongBy64(@ActuallyUnsigned long offsetInBytes, long value)
	{
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN)
			setLittleLongBy64(offsetInBytes, value);
		else
			setBigLongBy64(offsetInBytes, value);
	}
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setNativeCharBy64(@ActuallyUnsigned long offsetInBytes, char value)
	{
		setNativeShortBy64(offsetInBytes, (short)value);
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setNativeFloatBy64(@ActuallyUnsigned long offsetInBytes, float value)
	{
		setNativeIntBy64(offsetInBytes, Float.floatToRawIntBits(value));
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setNativeDoubleBy64(@ActuallyUnsigned long offsetInBytes, double value)
	{
		setNativeLongBy64(offsetInBytes, Double.doubleToRawLongBits(value));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setAllBytesBy64(@ActuallyUnsigned long index, byte[] array)
	{
		setAllBytesBy64(index, array, 0, array.length);
	}
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setAllBytesBy64(@ActuallyUnsigned long index, Slice<byte[]> arraySlice)
	{
		setAllBytesBy64(index, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	public default void setAllBytesBy64(@ActuallyUnsigned long start, byte[] array, @Nonnegative int offset, @Nonnegative int length)
	{
		requireNonNegative(offset);
		requireNonNegative(length);
		
		long size = this.size64();
		
		rangeCheckIntervalByLengthU64(size, start, length);
		rangeCheckIntervalByLengthU64(array.length, offset, length);
		
		for (int i = 0; i < length; i++)
			setByteBy64(start + i, array[offset + i]);
	}
	
	
	
	
	
	@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
	public default void setAllBy64(@ActuallyUnsigned long destIndex, NonuniformMethodsForByteList64 source) throws IndexOutOfBoundsException
	{
		setAllBy64(destIndex, source, 0, source.size64());
	}
	
	
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	public default void setAllBy64(@ActuallyUnsigned long destIndex, NonuniformMethodsForByteList64 source, @ActuallyUnsigned long sourceIndex, @ActuallyUnsigned long amount) throws IndexOutOfBoundsException
	{
		NonuniformMethodsForByteList64 dest = this;
		
		@ActuallyUnsigned long sourceSize = source.size64();
		@ActuallyUnsigned long destSize = dest.size64();
		rangeCheckIntervalByLengthU64(sourceSize, sourceIndex, amount);
		rangeCheckIntervalByLengthU64(destSize, destIndex, amount);
		
		if (destIndex < sourceIndex)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
		{
			for (@ActuallyUnsigned long i = 0; Long.compareUnsigned(i, amount) < 0; i++)
				dest.setByteBy64(destIndex+i, source.getByteBy64(sourceIndex+i));
		}
		else
		{
			for (@ActuallyUnsigned long i = amount-1; Long.compareUnsigned(i, 0) >= 0; i--)
				dest.setByteBy64(destIndex+i, source.getByteBy64(sourceIndex+i));
		}
	}
	
	
	
	
	
	
	
	/**
	 * Copies <code>array.length</code> elements into <code>array</code> starting with the <code>start</code>th element.<br>
	 */
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	public default void getAllBytesBy64(@ActuallyUnsigned long start, @WritableValue byte[] array, @Nonnegative int offset, @Nonnegative int length)
	{
		requireNonNegative(offset);
		requireNonNegative(length);
		
		@ActuallyUnsigned long size = this.size64();
		
		rangeCheckIntervalByLengthU64(size, start, length);
		rangeCheckIntervalByLengthU64(array.length, offset, length);
		
		for (int i = 0; i < length; i++)
			array[offset + i] = getByteBy64(start + i);
	}
	
	
	
	
	
	
	@ThrowAwayValue
	public default byte[] getAllBytesBy64(@ActuallyUnsigned long start, @ActuallyUnsigned long end) throws OverflowException
	{
		rangeCheckIntervalU64(this.size(), start, end);
		
		byte[] buff = new byte[safeCastU64toS32(end-start)];
		getAllBytesBy64(start, buff, 0, buff.length);
		return buff;
	}
	
	
	
	
	public default void fillBySettingByteBy64(@ActuallyUnsigned long start, @ActuallyUnsigned long count, byte value)
	{
		rangeCheckIntervalByLengthU64(this.size64(), start, count);
		
		if (count >= FillWithArrayThreshold)
		{
			byte[] array = new byte[(int)least(count, FillWithArraySize)];
			
			if (value != ((byte)0))
			{
				Arrays.fill(array, value);
			}
			
			int al = array.length;
			
			while (count > al)
			{
				setAllBytesBy64(start, array);
				start += al;
				count -= al;
			}
			
			if (count > 0)
			{
				setAllBytesBy64(start, array, 0, safeCastU64toS32(count));
			}
		}
		else
		{
			@ActuallyUnsigned long e = start + count;
			for (@ActuallyUnsigned long i = start; i != e; i++)
				setByteBy64(i, value);
		}
	}
	
	
	
	
	
	
	
	/* <<<
	primxp
	_$$primxpconf:multibyteints$$_
	
	public default void setLittleArrayBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull _$$prim$$_[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = _$$primbytelen$$_;
		
		for (int i = 0; i < sourceLength; i++)
			setLittle_$$Prim$$_By64(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setLittleArrayFromSlice_$$Prim$$_By64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull Slice<_$$prim$$_[]> source)
	{
		setLittleArrayBy64(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	
	
	public default void getLittleArrayBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull _$$prim$$_[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = _$$primbytelen$$_;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (_$$prim$$_)getLittle_$$Prim$$_By64(sourceByteOffset+i*primbytelen);
	}
	
	public default void getLittleArrayFromSlice_$$Prim$$_By64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull Slice<_$$prim$$_[]> dest)
	{
		getLittleArrayBy64(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setBigArrayBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull _$$prim$$_[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = _$$primbytelen$$_;
		
		for (int i = 0; i < sourceLength; i++)
			setBig_$$Prim$$_By64(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setBigArrayFromSlice_$$Prim$$_By64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull Slice<_$$prim$$_[]> source)
	{
		setBigArrayBy64(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	
	
	public default void getBigArrayBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull _$$prim$$_[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = _$$primbytelen$$_;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (_$$prim$$_)getBig_$$Prim$$_By64(sourceByteOffset+i*primbytelen);
	}
	
	public default void getBigArrayFromSlice_$$Prim$$_By64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull Slice<_$$prim$$_[]> dest)
	{
		getBigArrayBy64(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setNativeArrayBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull _$$prim$$_[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = _$$primbytelen$$_;
		
		for (int i = 0; i < sourceLength; i++)
			setNative_$$Prim$$_By64(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setNativeArrayFromSlice_$$Prim$$_By64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull Slice<_$$prim$$_[]> source)
	{
		setNativeArrayBy64(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	
	
	public default void getNativeArrayBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull _$$prim$$_[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = _$$primbytelen$$_;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (_$$prim$$_)getNative_$$Prim$$_By64(sourceByteOffset+i*primbytelen);
	}
	
	public default void getNativeArrayFromSlice_$$Prim$$_By64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull Slice<_$$prim$$_[]> dest)
	{
		getNativeArrayBy64(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	 */
	
	
	public default void setLittleArrayBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull char[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < sourceLength; i++)
			setLittleCharBy64(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setLittleArrayFromSliceCharBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull Slice<char[]> source)
	{
		setLittleArrayBy64(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	
	
	public default void getLittleArrayBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull char[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (char)getLittleCharBy64(sourceByteOffset+i*primbytelen);
	}
	
	public default void getLittleArrayFromSliceCharBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull Slice<char[]> dest)
	{
		getLittleArrayBy64(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setBigArrayBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull char[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < sourceLength; i++)
			setBigCharBy64(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setBigArrayFromSliceCharBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull Slice<char[]> source)
	{
		setBigArrayBy64(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	
	
	public default void getBigArrayBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull char[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (char)getBigCharBy64(sourceByteOffset+i*primbytelen);
	}
	
	public default void getBigArrayFromSliceCharBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull Slice<char[]> dest)
	{
		getBigArrayBy64(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setNativeArrayBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull char[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < sourceLength; i++)
			setNativeCharBy64(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setNativeArrayFromSliceCharBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull Slice<char[]> source)
	{
		setNativeArrayBy64(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	
	
	public default void getNativeArrayBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull char[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (char)getNativeCharBy64(sourceByteOffset+i*primbytelen);
	}
	
	public default void getNativeArrayFromSliceCharBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull Slice<char[]> dest)
	{
		getNativeArrayBy64(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public default void setLittleArrayBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull short[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < sourceLength; i++)
			setLittleShortBy64(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setLittleArrayFromSliceShortBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull Slice<short[]> source)
	{
		setLittleArrayBy64(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	
	
	public default void getLittleArrayBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull short[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (short)getLittleShortBy64(sourceByteOffset+i*primbytelen);
	}
	
	public default void getLittleArrayFromSliceShortBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull Slice<short[]> dest)
	{
		getLittleArrayBy64(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setBigArrayBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull short[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < sourceLength; i++)
			setBigShortBy64(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setBigArrayFromSliceShortBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull Slice<short[]> source)
	{
		setBigArrayBy64(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	
	
	public default void getBigArrayBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull short[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (short)getBigShortBy64(sourceByteOffset+i*primbytelen);
	}
	
	public default void getBigArrayFromSliceShortBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull Slice<short[]> dest)
	{
		getBigArrayBy64(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setNativeArrayBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull short[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < sourceLength; i++)
			setNativeShortBy64(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setNativeArrayFromSliceShortBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull Slice<short[]> source)
	{
		setNativeArrayBy64(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	
	
	public default void getNativeArrayBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull short[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (short)getNativeShortBy64(sourceByteOffset+i*primbytelen);
	}
	
	public default void getNativeArrayFromSliceShortBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull Slice<short[]> dest)
	{
		getNativeArrayBy64(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public default void setLittleArrayBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull int[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 4;
		
		for (int i = 0; i < sourceLength; i++)
			setLittleIntBy64(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setLittleArrayFromSliceIntBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull Slice<int[]> source)
	{
		setLittleArrayBy64(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	
	
	public default void getLittleArrayBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull int[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 4;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (int)getLittleIntBy64(sourceByteOffset+i*primbytelen);
	}
	
	public default void getLittleArrayFromSliceIntBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull Slice<int[]> dest)
	{
		getLittleArrayBy64(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setBigArrayBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull int[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 4;
		
		for (int i = 0; i < sourceLength; i++)
			setBigIntBy64(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setBigArrayFromSliceIntBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull Slice<int[]> source)
	{
		setBigArrayBy64(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	
	
	public default void getBigArrayBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull int[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 4;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (int)getBigIntBy64(sourceByteOffset+i*primbytelen);
	}
	
	public default void getBigArrayFromSliceIntBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull Slice<int[]> dest)
	{
		getBigArrayBy64(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setNativeArrayBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull int[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 4;
		
		for (int i = 0; i < sourceLength; i++)
			setNativeIntBy64(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setNativeArrayFromSliceIntBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull Slice<int[]> source)
	{
		setNativeArrayBy64(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	
	
	public default void getNativeArrayBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull int[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 4;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (int)getNativeIntBy64(sourceByteOffset+i*primbytelen);
	}
	
	public default void getNativeArrayFromSliceIntBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull Slice<int[]> dest)
	{
		getNativeArrayBy64(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public default void setLittleArrayBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull long[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 8;
		
		for (int i = 0; i < sourceLength; i++)
			setLittleLongBy64(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setLittleArrayFromSliceLongBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull Slice<long[]> source)
	{
		setLittleArrayBy64(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	
	
	public default void getLittleArrayBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull long[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 8;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (long)getLittleLongBy64(sourceByteOffset+i*primbytelen);
	}
	
	public default void getLittleArrayFromSliceLongBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull Slice<long[]> dest)
	{
		getLittleArrayBy64(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setBigArrayBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull long[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 8;
		
		for (int i = 0; i < sourceLength; i++)
			setBigLongBy64(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setBigArrayFromSliceLongBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull Slice<long[]> source)
	{
		setBigArrayBy64(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	
	
	public default void getBigArrayBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull long[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 8;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (long)getBigLongBy64(sourceByteOffset+i*primbytelen);
	}
	
	public default void getBigArrayFromSliceLongBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull Slice<long[]> dest)
	{
		getBigArrayBy64(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setNativeArrayBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull long[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 8;
		
		for (int i = 0; i < sourceLength; i++)
			setNativeLongBy64(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setNativeArrayFromSliceLongBy64(@ActuallyUnsigned long destByteOffset, @ReadonlyValue @Nonnull Slice<long[]> source)
	{
		setNativeArrayBy64(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	
	
	public default void getNativeArrayBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull long[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 8;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (long)getNativeLongBy64(sourceByteOffset+i*primbytelen);
	}
	
	public default void getNativeArrayFromSliceLongBy64(@ActuallyUnsigned long sourceByteOffset, @WritableValue @Nonnull Slice<long[]> dest)
	{
		getNativeArrayBy64(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	// >>>
}
