package rebound.util.collections.prim;

import java.nio.ByteBuffer;
import rebound.annotations.semantic.simpledata.ActuallyBits;
import rebound.annotations.semantic.simpledata.ActuallySigned;
import rebound.annotations.semantic.simpledata.ActuallyUnsigned;
import rebound.bits.Bytes;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;

/**
 * This is crucial for contexts where access to a multibyte field *must* take place in a single actual memory access operation, like device-mapped memory!
 * (Because {@link ByteBuffer} very much does not do that in DirectByteBuffer, which is bad for performance as well as making it incapable of being used to write userspace device drivers >_> )
 * 
 * Note that this can't be in the main type with default methods or it would make {@link Bytes} unable to test for <code>instanceof</code> this interface for performance ^^'
 */
public interface ByteListWithMultibyteAccess
extends ByteList
{
	@Override
	public ByteListWithMultibyteAccess subList(int fromIndex, int toIndex);
	
	
	@Override
	public default ByteListWithMultibyteAccess subListByLength(int start, int length)
	{
		return (ByteListWithMultibyteAccess)ByteList.super.subListByLength(start, length);
	}
	
	@Override
	public default ByteListWithMultibyteAccess subListFromBeginning(int lengthWhichIsEndExclusiveInThisCaseXD)
	{
		return (ByteListWithMultibyteAccess)ByteList.super.subListFromBeginning(lengthWhichIsEndExclusiveInThisCaseXD);
	}
	
	@Override
	public default ByteListWithMultibyteAccess subListToEnd(int start)
	{
		return (ByteListWithMultibyteAccess)ByteList.super.subListToEnd(start);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public short getLittleShort(int byteIndexOfFirstByte);
	public int getLittleInt(int byteIndexOfFirstByte);
	public long getLittleLong(int byteIndexOfFirstByte);
	
	public @ActuallyUnsigned(24) int getLittleUInt24(int byteIndexOfFirstByte);
	public @ActuallyUnsigned(40) long getLittleULong40(int byteIndexOfFirstByte);
	public @ActuallyUnsigned(48) long getLittleULong48(int byteIndexOfFirstByte);
	public @ActuallyUnsigned(56) long getLittleULong56(int byteIndexOfFirstByte);
	
	public @ActuallySigned(24) int getLittleSInt24(int byteIndexOfFirstByte);
	public @ActuallySigned(40) long getLittleSLong40(int byteIndexOfFirstByte);
	public @ActuallySigned(48) long getLittleSLong48(int byteIndexOfFirstByte);
	public @ActuallySigned(56) long getLittleSLong56(int byteIndexOfFirstByte);
	
	
	
	public void setLittleShort(int byteIndexOfFirstByte, short value);
	public void setLittleInt(int byteIndexOfFirstByte, int value);
	public void setLittleLong(int byteIndexOfFirstByte, long value);
	
	public void setLittleInt24(int byteIndexOfFirstByte, @ActuallyBits(24) int value);
	public void setLittleLong40(int byteIndexOfFirstByte, @ActuallyBits(40) long value);
	public void setLittleLong48(int byteIndexOfFirstByte, @ActuallyBits(48) long value);
	public void setLittleLong56(int byteIndexOfFirstByte, @ActuallyBits(56) long value);
	
	
	
	
	
	
	
	
	
	
	
	
	
	public short getBigShort(int byteIndexOfFirstByte);
	public int getBigInt(int byteIndexOfFirstByte);
	public long getBigLong(int byteIndexOfFirstByte);
	
	public @ActuallyUnsigned(24) int getBigUInt24(int byteIndexOfFirstByte);
	public @ActuallyUnsigned(40) long getBigULong40(int byteIndexOfFirstByte);
	public @ActuallyUnsigned(48) long getBigULong48(int byteIndexOfFirstByte);
	public @ActuallyUnsigned(56) long getBigULong56(int byteIndexOfFirstByte);
	
	public @ActuallySigned(24) int getBigSInt24(int byteIndexOfFirstByte);
	public @ActuallySigned(40) long getBigSLong40(int byteIndexOfFirstByte);
	public @ActuallySigned(48) long getBigSLong48(int byteIndexOfFirstByte);
	public @ActuallySigned(56) long getBigSLong56(int byteIndexOfFirstByte);
	
	
	
	public void setBigShort(int byteIndexOfFirstByte, short value);
	public void setBigInt(int byteIndexOfFirstByte, int value);
	public void setBigLong(int byteIndexOfFirstByte, long value);
	
	public void setBigInt24(int byteIndexOfFirstByte, @ActuallyBits(24) int value);
	public void setBigLong40(int byteIndexOfFirstByte, @ActuallyBits(40) long value);
	public void setBigLong48(int byteIndexOfFirstByte, @ActuallyBits(48) long value);
	public void setBigLong56(int byteIndexOfFirstByte, @ActuallyBits(56) long value);
	
	
	
	
	
	
	
	
	
	
	
	
	
	public short getNativeShort(int byteIndexOfFirstByte);
	public int getNativeInt(int byteIndexOfFirstByte);
	public long getNativeLong(int byteIndexOfFirstByte);
	
	public @ActuallyUnsigned(24) int getNativeUInt24(int byteIndexOfFirstByte);
	public @ActuallyUnsigned(40) long getNativeULong40(int byteIndexOfFirstByte);
	public @ActuallyUnsigned(48) long getNativeULong48(int byteIndexOfFirstByte);
	public @ActuallyUnsigned(56) long getNativeULong56(int byteIndexOfFirstByte);
	
	public @ActuallySigned(24) int getNativeSInt24(int byteIndexOfFirstByte);
	public @ActuallySigned(40) long getNativeSLong40(int byteIndexOfFirstByte);
	public @ActuallySigned(48) long getNativeSLong48(int byteIndexOfFirstByte);
	public @ActuallySigned(56) long getNativeSLong56(int byteIndexOfFirstByte);
	
	
	
	public void setNativeShort(int byteIndexOfFirstByte, short value);
	public void setNativeInt(int byteIndexOfFirstByte, int value);
	public void setNativeLong(int byteIndexOfFirstByte, long value);
	
	public void setNativeInt24(int byteIndexOfFirstByte, @ActuallyBits(24) int value);
	public void setNativeLong40(int byteIndexOfFirstByte, @ActuallyBits(40) long value);
	public void setNativeLong48(int byteIndexOfFirstByte, @ActuallyBits(48) long value);
	public void setNativeLong56(int byteIndexOfFirstByte, @ActuallyBits(56) long value);
}
