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
import rebound.bits.Bytes;
import rebound.util.collections.Slice;
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
	public default SpanningOperationImplementationType getMultibyteOperationsImplementationGuaranteesFor32bitOffsets()
	{
		return SpanningOperationImplementationType.OneByOne;
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public default void setArray(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull byte[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 1;
		
		for (int i = 0; i < sourceLength; i++)
			setByte(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setArray(@ReadonlyValue @Nonnull byte[] source)
	{
		setArray(0, source, 0, source.length);
	}
	
	public default void setArrayFromSliceByte(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull Slice<byte[]> source)
	{
		setArray(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public default void setArrayFromSliceByte(@ReadonlyValue @Nonnull Slice<byte[]> source)
	{
		setArrayFromSliceByte(0, source);
	}
	
	
	
	public default void getArray(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull byte[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 1;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (byte)getByte(sourceByteOffset+i*primbytelen);
	}
	
	public default void getArray(@WritableValue @Nonnull byte[] dest)
	{
		getArray(0, dest, 0, dest.length);
	}
	
	public default void getArrayFromSliceByte(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull Slice<byte[]> dest)
	{
		getArray(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	public default void getArrayFromSliceByte(@WritableValue @Nonnull Slice<byte[]> dest)
	{
		getArrayFromSliceByte(0, dest);
	}
	
	
	
	
	
	
	
	/* <<<
	primxp
	_$$primxpconf:multibyteints$$_
	
	public default void setLittleArray(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull _$$prim$$_[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = _$$primbytelen$$_;
		
		for (int i = 0; i < sourceLength; i++)
			setLittle_$$Prim$$_(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setLittleArray(@ReadonlyValue @Nonnull _$$prim$$_[] source)
	{
		setLittleArray(0, source, 0, source.length);
	}
	
	public default void setLittleArrayFromSlice_$$Prim$$_(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull Slice<_$$prim$$_[]> source)
	{
		setLittleArray(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public default void setLittleArrayFromSlice_$$Prim$$_(@ReadonlyValue @Nonnull Slice<_$$prim$$_[]> source)
	{
		setLittleArrayFromSlice_$$Prim$$_(0, source);
	}
	
	
	
	public default void getLittleArray(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull _$$prim$$_[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = _$$primbytelen$$_;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (_$$prim$$_)getLittle_$$Prim$$_(sourceByteOffset+i*primbytelen);
	}
	
	public default void getLittleArray(@WritableValue @Nonnull _$$prim$$_[] dest)
	{
		getLittleArray(0, dest, 0, dest.length);
	}
	
	public default void getLittleArrayFromSlice_$$Prim$$_(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull Slice<_$$prim$$_[]> dest)
	{
		getLittleArray(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	public default void getLittleArrayFromSlice_$$Prim$$_(@WritableValue @Nonnull Slice<_$$prim$$_[]> dest)
	{
		getLittleArrayFromSlice_$$Prim$$_(0, dest);
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setBigArray(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull _$$prim$$_[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = _$$primbytelen$$_;
		
		for (int i = 0; i < sourceLength; i++)
			setBig_$$Prim$$_(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setBigArray(@ReadonlyValue @Nonnull _$$prim$$_[] source)
	{
		setBigArray(0, source, 0, source.length);
	}
	
	public default void setBigArrayFromSlice_$$Prim$$_(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull Slice<_$$prim$$_[]> source)
	{
		setBigArray(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public default void setBigArrayFromSlice_$$Prim$$_(@ReadonlyValue @Nonnull Slice<_$$prim$$_[]> source)
	{
		setBigArrayFromSlice_$$Prim$$_(0, source);
	}
	
	
	
	public default void getBigArray(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull _$$prim$$_[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = _$$primbytelen$$_;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (_$$prim$$_)getBig_$$Prim$$_(sourceByteOffset+i*primbytelen);
	}
	
	public default void getBigArray(@WritableValue @Nonnull _$$prim$$_[] dest)
	{
		getBigArray(0, dest, 0, dest.length);
	}
	
	public default void getBigArrayFromSlice_$$Prim$$_(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull Slice<_$$prim$$_[]> dest)
	{
		getBigArray(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	public default void getBigArrayFromSlice_$$Prim$$_(@WritableValue @Nonnull Slice<_$$prim$$_[]> dest)
	{
		getBigArrayFromSlice_$$Prim$$_(0, dest);
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setNativeArray(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull _$$prim$$_[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = _$$primbytelen$$_;
		
		for (int i = 0; i < sourceLength; i++)
			setNative_$$Prim$$_(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setNativeArray(@ReadonlyValue @Nonnull _$$prim$$_[] source)
	{
		setNativeArray(0, source, 0, source.length);
	}
	
	public default void setNativeArrayFromSlice_$$Prim$$_(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull Slice<_$$prim$$_[]> source)
	{
		setNativeArray(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public default void setNativeArrayFromSlice_$$Prim$$_(@ReadonlyValue @Nonnull Slice<_$$prim$$_[]> source)
	{
		setNativeArrayFromSlice_$$Prim$$_(0, source);
	}
	
	
	
	public default void getNativeArray(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull _$$prim$$_[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = _$$primbytelen$$_;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (_$$prim$$_)getNative_$$Prim$$_(sourceByteOffset+i*primbytelen);
	}
	
	public default void getNativeArray(@WritableValue @Nonnull _$$prim$$_[] dest)
	{
		getNativeArray(0, dest, 0, dest.length);
	}
	
	public default void getNativeArrayFromSlice_$$Prim$$_(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull Slice<_$$prim$$_[]> dest)
	{
		getNativeArray(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	public default void getNativeArrayFromSlice_$$Prim$$_(@WritableValue @Nonnull Slice<_$$prim$$_[]> dest)
	{
		getNativeArrayFromSlice_$$Prim$$_(0, dest);
	}
	
	
	
	
	
	
	
	
	
	
	
	 */
	
	
	public default void setLittleArray(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull char[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < sourceLength; i++)
			setLittleChar(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setLittleArray(@ReadonlyValue @Nonnull char[] source)
	{
		setLittleArray(0, source, 0, source.length);
	}
	
	public default void setLittleArrayFromSliceChar(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull Slice<char[]> source)
	{
		setLittleArray(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public default void setLittleArrayFromSliceChar(@ReadonlyValue @Nonnull Slice<char[]> source)
	{
		setLittleArrayFromSliceChar(0, source);
	}
	
	
	
	public default void getLittleArray(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull char[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (char)getLittleChar(sourceByteOffset+i*primbytelen);
	}
	
	public default void getLittleArray(@WritableValue @Nonnull char[] dest)
	{
		getLittleArray(0, dest, 0, dest.length);
	}
	
	public default void getLittleArrayFromSliceChar(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull Slice<char[]> dest)
	{
		getLittleArray(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	public default void getLittleArrayFromSliceChar(@WritableValue @Nonnull Slice<char[]> dest)
	{
		getLittleArrayFromSliceChar(0, dest);
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setBigArray(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull char[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < sourceLength; i++)
			setBigChar(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setBigArray(@ReadonlyValue @Nonnull char[] source)
	{
		setBigArray(0, source, 0, source.length);
	}
	
	public default void setBigArrayFromSliceChar(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull Slice<char[]> source)
	{
		setBigArray(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public default void setBigArrayFromSliceChar(@ReadonlyValue @Nonnull Slice<char[]> source)
	{
		setBigArrayFromSliceChar(0, source);
	}
	
	
	
	public default void getBigArray(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull char[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (char)getBigChar(sourceByteOffset+i*primbytelen);
	}
	
	public default void getBigArray(@WritableValue @Nonnull char[] dest)
	{
		getBigArray(0, dest, 0, dest.length);
	}
	
	public default void getBigArrayFromSliceChar(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull Slice<char[]> dest)
	{
		getBigArray(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	public default void getBigArrayFromSliceChar(@WritableValue @Nonnull Slice<char[]> dest)
	{
		getBigArrayFromSliceChar(0, dest);
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setNativeArray(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull char[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < sourceLength; i++)
			setNativeChar(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setNativeArray(@ReadonlyValue @Nonnull char[] source)
	{
		setNativeArray(0, source, 0, source.length);
	}
	
	public default void setNativeArrayFromSliceChar(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull Slice<char[]> source)
	{
		setNativeArray(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public default void setNativeArrayFromSliceChar(@ReadonlyValue @Nonnull Slice<char[]> source)
	{
		setNativeArrayFromSliceChar(0, source);
	}
	
	
	
	public default void getNativeArray(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull char[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (char)getNativeChar(sourceByteOffset+i*primbytelen);
	}
	
	public default void getNativeArray(@WritableValue @Nonnull char[] dest)
	{
		getNativeArray(0, dest, 0, dest.length);
	}
	
	public default void getNativeArrayFromSliceChar(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull Slice<char[]> dest)
	{
		getNativeArray(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	public default void getNativeArrayFromSliceChar(@WritableValue @Nonnull Slice<char[]> dest)
	{
		getNativeArrayFromSliceChar(0, dest);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public default void setLittleArray(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull short[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < sourceLength; i++)
			setLittleShort(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setLittleArray(@ReadonlyValue @Nonnull short[] source)
	{
		setLittleArray(0, source, 0, source.length);
	}
	
	public default void setLittleArrayFromSliceShort(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull Slice<short[]> source)
	{
		setLittleArray(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public default void setLittleArrayFromSliceShort(@ReadonlyValue @Nonnull Slice<short[]> source)
	{
		setLittleArrayFromSliceShort(0, source);
	}
	
	
	
	public default void getLittleArray(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull short[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (short)getLittleShort(sourceByteOffset+i*primbytelen);
	}
	
	public default void getLittleArray(@WritableValue @Nonnull short[] dest)
	{
		getLittleArray(0, dest, 0, dest.length);
	}
	
	public default void getLittleArrayFromSliceShort(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull Slice<short[]> dest)
	{
		getLittleArray(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	public default void getLittleArrayFromSliceShort(@WritableValue @Nonnull Slice<short[]> dest)
	{
		getLittleArrayFromSliceShort(0, dest);
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setBigArray(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull short[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < sourceLength; i++)
			setBigShort(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setBigArray(@ReadonlyValue @Nonnull short[] source)
	{
		setBigArray(0, source, 0, source.length);
	}
	
	public default void setBigArrayFromSliceShort(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull Slice<short[]> source)
	{
		setBigArray(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public default void setBigArrayFromSliceShort(@ReadonlyValue @Nonnull Slice<short[]> source)
	{
		setBigArrayFromSliceShort(0, source);
	}
	
	
	
	public default void getBigArray(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull short[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (short)getBigShort(sourceByteOffset+i*primbytelen);
	}
	
	public default void getBigArray(@WritableValue @Nonnull short[] dest)
	{
		getBigArray(0, dest, 0, dest.length);
	}
	
	public default void getBigArrayFromSliceShort(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull Slice<short[]> dest)
	{
		getBigArray(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	public default void getBigArrayFromSliceShort(@WritableValue @Nonnull Slice<short[]> dest)
	{
		getBigArrayFromSliceShort(0, dest);
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setNativeArray(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull short[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < sourceLength; i++)
			setNativeShort(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setNativeArray(@ReadonlyValue @Nonnull short[] source)
	{
		setNativeArray(0, source, 0, source.length);
	}
	
	public default void setNativeArrayFromSliceShort(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull Slice<short[]> source)
	{
		setNativeArray(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public default void setNativeArrayFromSliceShort(@ReadonlyValue @Nonnull Slice<short[]> source)
	{
		setNativeArrayFromSliceShort(0, source);
	}
	
	
	
	public default void getNativeArray(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull short[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 2;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (short)getNativeShort(sourceByteOffset+i*primbytelen);
	}
	
	public default void getNativeArray(@WritableValue @Nonnull short[] dest)
	{
		getNativeArray(0, dest, 0, dest.length);
	}
	
	public default void getNativeArrayFromSliceShort(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull Slice<short[]> dest)
	{
		getNativeArray(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	public default void getNativeArrayFromSliceShort(@WritableValue @Nonnull Slice<short[]> dest)
	{
		getNativeArrayFromSliceShort(0, dest);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public default void setLittleArray(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull int[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 4;
		
		for (int i = 0; i < sourceLength; i++)
			setLittleInt(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setLittleArray(@ReadonlyValue @Nonnull int[] source)
	{
		setLittleArray(0, source, 0, source.length);
	}
	
	public default void setLittleArrayFromSliceInt(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull Slice<int[]> source)
	{
		setLittleArray(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public default void setLittleArrayFromSliceInt(@ReadonlyValue @Nonnull Slice<int[]> source)
	{
		setLittleArrayFromSliceInt(0, source);
	}
	
	
	
	public default void getLittleArray(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull int[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 4;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (int)getLittleInt(sourceByteOffset+i*primbytelen);
	}
	
	public default void getLittleArray(@WritableValue @Nonnull int[] dest)
	{
		getLittleArray(0, dest, 0, dest.length);
	}
	
	public default void getLittleArrayFromSliceInt(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull Slice<int[]> dest)
	{
		getLittleArray(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	public default void getLittleArrayFromSliceInt(@WritableValue @Nonnull Slice<int[]> dest)
	{
		getLittleArrayFromSliceInt(0, dest);
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setBigArray(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull int[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 4;
		
		for (int i = 0; i < sourceLength; i++)
			setBigInt(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setBigArray(@ReadonlyValue @Nonnull int[] source)
	{
		setBigArray(0, source, 0, source.length);
	}
	
	public default void setBigArrayFromSliceInt(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull Slice<int[]> source)
	{
		setBigArray(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public default void setBigArrayFromSliceInt(@ReadonlyValue @Nonnull Slice<int[]> source)
	{
		setBigArrayFromSliceInt(0, source);
	}
	
	
	
	public default void getBigArray(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull int[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 4;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (int)getBigInt(sourceByteOffset+i*primbytelen);
	}
	
	public default void getBigArray(@WritableValue @Nonnull int[] dest)
	{
		getBigArray(0, dest, 0, dest.length);
	}
	
	public default void getBigArrayFromSliceInt(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull Slice<int[]> dest)
	{
		getBigArray(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	public default void getBigArrayFromSliceInt(@WritableValue @Nonnull Slice<int[]> dest)
	{
		getBigArrayFromSliceInt(0, dest);
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setNativeArray(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull int[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 4;
		
		for (int i = 0; i < sourceLength; i++)
			setNativeInt(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setNativeArray(@ReadonlyValue @Nonnull int[] source)
	{
		setNativeArray(0, source, 0, source.length);
	}
	
	public default void setNativeArrayFromSliceInt(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull Slice<int[]> source)
	{
		setNativeArray(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public default void setNativeArrayFromSliceInt(@ReadonlyValue @Nonnull Slice<int[]> source)
	{
		setNativeArrayFromSliceInt(0, source);
	}
	
	
	
	public default void getNativeArray(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull int[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 4;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (int)getNativeInt(sourceByteOffset+i*primbytelen);
	}
	
	public default void getNativeArray(@WritableValue @Nonnull int[] dest)
	{
		getNativeArray(0, dest, 0, dest.length);
	}
	
	public default void getNativeArrayFromSliceInt(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull Slice<int[]> dest)
	{
		getNativeArray(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	public default void getNativeArrayFromSliceInt(@WritableValue @Nonnull Slice<int[]> dest)
	{
		getNativeArrayFromSliceInt(0, dest);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public default void setLittleArray(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull long[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 8;
		
		for (int i = 0; i < sourceLength; i++)
			setLittleLong(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setLittleArray(@ReadonlyValue @Nonnull long[] source)
	{
		setLittleArray(0, source, 0, source.length);
	}
	
	public default void setLittleArrayFromSliceLong(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull Slice<long[]> source)
	{
		setLittleArray(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public default void setLittleArrayFromSliceLong(@ReadonlyValue @Nonnull Slice<long[]> source)
	{
		setLittleArrayFromSliceLong(0, source);
	}
	
	
	
	public default void getLittleArray(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull long[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 8;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (long)getLittleLong(sourceByteOffset+i*primbytelen);
	}
	
	public default void getLittleArray(@WritableValue @Nonnull long[] dest)
	{
		getLittleArray(0, dest, 0, dest.length);
	}
	
	public default void getLittleArrayFromSliceLong(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull Slice<long[]> dest)
	{
		getLittleArray(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	public default void getLittleArrayFromSliceLong(@WritableValue @Nonnull Slice<long[]> dest)
	{
		getLittleArrayFromSliceLong(0, dest);
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setBigArray(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull long[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 8;
		
		for (int i = 0; i < sourceLength; i++)
			setBigLong(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setBigArray(@ReadonlyValue @Nonnull long[] source)
	{
		setBigArray(0, source, 0, source.length);
	}
	
	public default void setBigArrayFromSliceLong(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull Slice<long[]> source)
	{
		setBigArray(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public default void setBigArrayFromSliceLong(@ReadonlyValue @Nonnull Slice<long[]> source)
	{
		setBigArrayFromSliceLong(0, source);
	}
	
	
	
	public default void getBigArray(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull long[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 8;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (long)getBigLong(sourceByteOffset+i*primbytelen);
	}
	
	public default void getBigArray(@WritableValue @Nonnull long[] dest)
	{
		getBigArray(0, dest, 0, dest.length);
	}
	
	public default void getBigArrayFromSliceLong(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull Slice<long[]> dest)
	{
		getBigArray(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	public default void getBigArrayFromSliceLong(@WritableValue @Nonnull Slice<long[]> dest)
	{
		getBigArrayFromSliceLong(0, dest);
	}
	
	
	
	
	
	
	
	
	
	
	
	public default void setNativeArray(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull long[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength)
	{
		int primbytelen = 8;
		
		for (int i = 0; i < sourceLength; i++)
			setNativeLong(destByteOffset+i*primbytelen, source[sourceElementOffset+i]);
	}
	
	public default void setNativeArray(@ReadonlyValue @Nonnull long[] source)
	{
		setNativeArray(0, source, 0, source.length);
	}
	
	public default void setNativeArrayFromSliceLong(@Nonnegative int destByteOffset, @ReadonlyValue @Nonnull Slice<long[]> source)
	{
		setNativeArray(destByteOffset, source.getUnderlying(), source.getOffset(), source.getLength());
	}
	
	public default void setNativeArrayFromSliceLong(@ReadonlyValue @Nonnull Slice<long[]> source)
	{
		setNativeArrayFromSliceLong(0, source);
	}
	
	
	
	public default void getNativeArray(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull long[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLength)
	{
		int primbytelen = 8;
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (long)getNativeLong(sourceByteOffset+i*primbytelen);
	}
	
	public default void getNativeArray(@WritableValue @Nonnull long[] dest)
	{
		getNativeArray(0, dest, 0, dest.length);
	}
	
	public default void getNativeArrayFromSliceLong(@Nonnegative int sourceByteOffset, @WritableValue @Nonnull Slice<long[]> dest)
	{
		getNativeArray(sourceByteOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength());
	}
	
	public default void getNativeArrayFromSliceLong(@WritableValue @Nonnull Slice<long[]> dest)
	{
		getNativeArrayFromSliceLong(0, dest);
	}
	
	
	
	
	
	
	
	
	
	
	
	// >>>
}
