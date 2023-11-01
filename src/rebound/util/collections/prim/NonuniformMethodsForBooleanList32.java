package rebound.util.collections.prim;

import static rebound.bits.BitfieldSafeCasts.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.hints.IntendedToBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.simpledata.BoundedInt;
import rebound.annotations.semantic.simpledata.BoundedLong;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.DefaultToArraysBooleanCollection;

//TODO Elementwise boolean operations between BooleanLists!!  AND, OR, NOT, XOR!  \:D/

/**
 * • Note: both bit-endianness and byte-endianness are always Little in this API!  Use functions on bitfields in memory/registers to swap them if you want something different!  (This is because the mask of the i'th bit is simply 2^i)
 */
@ImplementationTransparency  //This only exists to keep the bulk functions exactly uniform and correspondent :3     This might be removed soon XD''
public interface NonuniformMethodsForBooleanList32
extends DefaultToArraysBooleanCollection
{
	public boolean getBoolean(@Nonnegative int index);
	public void setBoolean(@Nonnegative int index, boolean value);
	
	
	
	
	public default @Nonnegative int getNumberOfOnes()
	{
		int n = size();
		int n1 = 0;
		for (int i = 0; i < n; i++)
			n1 += getBoolean(i) ? 1 : 0;
		return n1;
	}
	
	public default @Nonnegative int getNumberOfZeros()
	{
		return size() - getNumberOfOnes();
	}
	
	
	
	
	
	
	
	
	
	/**
	 * This only applies to the get/set's!
	 */
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	public default SpanningOperationImplementationType getMultibitOperationsImplementationGuaranteesFor32bitOffsets()
	{
		return SpanningOperationImplementationType.Piecemeal;
	}
	
	
	
	public default long getBitfield(@Nonnegative int offset, @BoundedInt(min=1, max=64) int length)
	{
		long r = 0;
		
		for (int i = 0; i < length; i++)
		{
			boolean bit = getBoolean(offset+i);
			r |= (bit ? 1l : 0l) << i;
		}
		
		return r;
	}
	
	public default void setBitfield(@Nonnegative int offset, @BoundedInt(min=1, max=64) int length, long bitfield)
	{
		for (int i = 0; i < length; i++)
		{
			boolean bit = ((1l << i) & bitfield) != 0;
			setBoolean(offset+i, bit);
		}
	}
	
	
	
	
	public default byte getByte(@Nonnegative int offsetInBits)
	{
		return (byte)getBitfield(offsetInBits, 8);
	}
	
	public default short getShort(@Nonnegative int offsetInBits)
	{
		return (short)getBitfield(offsetInBits, 16);
	}
	
	public default int getInt(@Nonnegative int offsetInBits)
	{
		return (int)getBitfield(offsetInBits, 32);
	}
	
	public default long getLong(@Nonnegative int offsetInBits)
	{
		return getBitfield(offsetInBits, 64);
	}
	
	
	public default void setByte(@Nonnegative int offsetInBits, byte value)
	{
		setBitfield(offsetInBits, 8, value);
	}
	
	public default void setShort(@Nonnegative int offsetInBits, short value)
	{
		setBitfield(offsetInBits, 16, value);
	}
	
	public default void setInt(@Nonnegative int offsetInBits, int value)
	{
		setBitfield(offsetInBits, 32, value);
	}
	
	public default void setLong(@Nonnegative int offsetInBits, long value)
	{
		setBitfield(offsetInBits, 64, value);
	}
	
	
	
	
	public default byte getAlignedByte(@Nonnegative int offsetAlignedInElements)
	{
		return (byte)getBitfield(offsetAlignedInElements * 8, 8);
	}
	
	public default short getAlignedShort(@Nonnegative int offsetAlignedInElements)
	{
		return (short)getBitfield(offsetAlignedInElements * 16, 16);
	}
	
	public default int getAlignedInt(@Nonnegative int offsetAlignedInElements)
	{
		return (int)getBitfield(offsetAlignedInElements * 32, 32);
	}
	
	public default long getAlignedLong(@Nonnegative int offsetAlignedInElements)
	{
		return getBitfield(offsetAlignedInElements * 64, 64);
	}
	
	
	public default void setAlignedByte(@Nonnegative int offsetAlignedInElements, byte value)
	{
		setBitfield(offsetAlignedInElements * 8, 8, value);
	}
	
	public default void setAlignedShort(@Nonnegative int offsetAlignedInElements, short value)
	{
		setBitfield(offsetAlignedInElements * 16, 16, value);
	}
	
	public default void setAlignedInt(@Nonnegative int offsetAlignedInElements, int value)
	{
		setBitfield(offsetAlignedInElements * 32, 32, value);
	}
	
	public default void setAlignedLong(@Nonnegative int offsetAlignedInElements, long value)
	{
		setBitfield(offsetAlignedInElements * 64, 64, value);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	primxp
	_$$primxpconf:intsonly$$_
	
	public default void setArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull _$$prim$$_[] source, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*_$$primlen$$_l) long totalLengthOfDataToWriteInBits)
	{
		int primlen = _$$primlen$$_;
		
		if (sourceLengthCheck != -1 && ceilingDivision(totalLengthOfDataToWriteInBits, primlen) > sourceLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToWriteInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfield(destBitOffset+i*primlen, primlen, source[sourceElementOffset+i]);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToWriteInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfield(safeCastS64toS32(destBitOffset+fullAmount), remainder, source[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void setArray(@ReadonlyValue @Nonnull _$$prim$$_[] source)
	{
		setArray(0, source, 0, source.length, source.length*_$$primlen$$_l);
	}
	
	public default void setArrayFromSlice_$$Prim$$_(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<_$$prim$$_[]> source)
	{
		setArray(destBitOffset, source.getUnderlying(), source.getOffset(), source.getLength(), source.getLength()*_$$primlen$$_l);
	}
	
	public default void setArrayFromSlice_$$Prim$$_(@ReadonlyValue @Nonnull Slice<_$$prim$$_[]> source)
	{
		setArrayFromSlice_$$Prim$$_(0, source);
	}
	
	
	
	public default void getArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull _$$prim$$_[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*_$$primlen$$_l) long totalLengthOfDataToReadInBits)
	{
		int primlen = _$$primlen$$_;
		
		if (destLengthCheck != -1 && ceilingDivision(totalLengthOfDataToReadInBits, primlen) > destLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToReadInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			dest[destElementOffset+i] = (_$$prim$$_)getBitfield(sourceBitOffset+i*primlen, primlen);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToReadInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			dest[destElementOffset+numberOfFullElementsToUse] = (_$$prim$$_)((dest[destElementOffset+numberOfFullElementsToUse] & (((1_$$promotedsuffix$$_ << remainder) - 1) << (_$$primlen$$_ - remainder))) | getBitfield(safeCastS64toS32(sourceBitOffset+fullAmount), remainder));
	}
	
	public default void getArray(@WritableValue @Nonnull _$$prim$$_[] dest)
	{
		getArray(0, dest, 0, dest.length, dest.length*_$$primlen$$_l);
	}
	
	public default void getArrayFromSlice_$$Prim$$_(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<_$$prim$$_[]> dest)
	{
		getArray(sourceBitOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength(), dest.getLength()*_$$primlen$$_l);
	}
	
	public default void getArrayFromSlice_$$Prim$$_(@WritableValue @Nonnull Slice<_$$prim$$_[]> dest)
	{
		getArrayFromSlice_$$Prim$$_(0, dest);
	}
	
	
	
	
	
	
	
	
	public default void setUnpackedArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull _$$prim$$_[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > _$$primlen$$_)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < sourceLength; i++)
			setBitfield(destBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits, source[sourceElementOffset+i]);
	}
	
	public default void setUnpackedArray(@ReadonlyValue @Nonnull _$$prim$$_[] source, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		setUnpackedArray(0, source, 0, source.length, lengthOfEachElementInBits);
	}
	
	public default void setUnpackedArrayFromSlice_$$Prim$$_(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<_$$prim$$_[]> source, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		setUnpackedArray(destBitOffset, source.getUnderlying(), source.getOffset(), source.getLength(), lengthOfEachElementInBits);
	}
	
	public default void setUnpackedArrayFromSlice_$$Prim$$_(@ReadonlyValue @Nonnull Slice<_$$prim$$_[]> source, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		setUnpackedArrayFromSlice_$$Prim$$_(0, source, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull _$$prim$$_[] dest, @Nonnegative int destElementOffset, @Nonnegative int destLength, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > _$$primlen$$_)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (_$$prim$$_)getBitfield(sourceBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArray(@Nonnull @WritableValue _$$prim$$_[] dest, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		getUnpackedArray(0, dest, 0, dest.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSlice_$$Prim$$_(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<_$$prim$$_[]> dest, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		getUnpackedArray(sourceBitOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSlice_$$Prim$$_(@WritableValue @Nonnull Slice<_$$prim$$_[]> dest, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSlice_$$Prim$$_(0, dest, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	 */
	
	
	public default void setArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull byte[] source, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*8l) long totalLengthOfDataToWriteInBits)
	{
		int primlen = 8;
		
		if (sourceLengthCheck != -1 && ceilingDivision(totalLengthOfDataToWriteInBits, primlen) > sourceLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToWriteInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfield(destBitOffset+i*primlen, primlen, source[sourceElementOffset+i]);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToWriteInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfield(safeCastS64toS32(destBitOffset+fullAmount), remainder, source[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void setArray(@ReadonlyValue @Nonnull byte[] source)
	{
		setArray(0, source, 0, source.length, source.length*8l);
	}
	
	public default void setArrayFromSliceByte(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<byte[]> source)
	{
		setArray(destBitOffset, source.getUnderlying(), source.getOffset(), source.getLength(), source.getLength()*8l);
	}
	
	public default void setArrayFromSliceByte(@ReadonlyValue @Nonnull Slice<byte[]> source)
	{
		setArrayFromSliceByte(0, source);
	}
	
	
	
	public default void getArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull byte[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*8l) long totalLengthOfDataToReadInBits)
	{
		int primlen = 8;
		
		if (destLengthCheck != -1 && ceilingDivision(totalLengthOfDataToReadInBits, primlen) > destLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToReadInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			dest[destElementOffset+i] = (byte)getBitfield(sourceBitOffset+i*primlen, primlen);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToReadInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			dest[destElementOffset+numberOfFullElementsToUse] = (byte)((dest[destElementOffset+numberOfFullElementsToUse] & (((1 << remainder) - 1) << (8 - remainder))) | getBitfield(safeCastS64toS32(sourceBitOffset+fullAmount), remainder));
	}
	
	public default void getArray(@WritableValue @Nonnull byte[] dest)
	{
		getArray(0, dest, 0, dest.length, dest.length*8l);
	}
	
	public default void getArrayFromSliceByte(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<byte[]> dest)
	{
		getArray(sourceBitOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength(), dest.getLength()*8l);
	}
	
	public default void getArrayFromSliceByte(@WritableValue @Nonnull Slice<byte[]> dest)
	{
		getArrayFromSliceByte(0, dest);
	}
	
	
	
	
	
	
	
	
	public default void setUnpackedArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull byte[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 8)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < sourceLength; i++)
			setBitfield(destBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits, source[sourceElementOffset+i]);
	}
	
	public default void setUnpackedArray(@ReadonlyValue @Nonnull byte[] source, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		setUnpackedArray(0, source, 0, source.length, lengthOfEachElementInBits);
	}
	
	public default void setUnpackedArrayFromSliceByte(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<byte[]> source, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		setUnpackedArray(destBitOffset, source.getUnderlying(), source.getOffset(), source.getLength(), lengthOfEachElementInBits);
	}
	
	public default void setUnpackedArrayFromSliceByte(@ReadonlyValue @Nonnull Slice<byte[]> source, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		setUnpackedArrayFromSliceByte(0, source, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull byte[] dest, @Nonnegative int destElementOffset, @Nonnegative int destLength, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 8)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (byte)getBitfield(sourceBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArray(@Nonnull @WritableValue byte[] dest, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		getUnpackedArray(0, dest, 0, dest.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceByte(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<byte[]> dest, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		getUnpackedArray(sourceBitOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceByte(@WritableValue @Nonnull Slice<byte[]> dest, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSliceByte(0, dest, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	
	
	public default void setArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull char[] source, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*16l) long totalLengthOfDataToWriteInBits)
	{
		int primlen = 16;
		
		if (sourceLengthCheck != -1 && ceilingDivision(totalLengthOfDataToWriteInBits, primlen) > sourceLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToWriteInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfield(destBitOffset+i*primlen, primlen, source[sourceElementOffset+i]);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToWriteInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfield(safeCastS64toS32(destBitOffset+fullAmount), remainder, source[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void setArray(@ReadonlyValue @Nonnull char[] source)
	{
		setArray(0, source, 0, source.length, source.length*16l);
	}
	
	public default void setArrayFromSliceChar(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<char[]> source)
	{
		setArray(destBitOffset, source.getUnderlying(), source.getOffset(), source.getLength(), source.getLength()*16l);
	}
	
	public default void setArrayFromSliceChar(@ReadonlyValue @Nonnull Slice<char[]> source)
	{
		setArrayFromSliceChar(0, source);
	}
	
	
	
	public default void getArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull char[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*16l) long totalLengthOfDataToReadInBits)
	{
		int primlen = 16;
		
		if (destLengthCheck != -1 && ceilingDivision(totalLengthOfDataToReadInBits, primlen) > destLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToReadInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			dest[destElementOffset+i] = (char)getBitfield(sourceBitOffset+i*primlen, primlen);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToReadInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			dest[destElementOffset+numberOfFullElementsToUse] = (char)((dest[destElementOffset+numberOfFullElementsToUse] & (((1 << remainder) - 1) << (16 - remainder))) | getBitfield(safeCastS64toS32(sourceBitOffset+fullAmount), remainder));
	}
	
	public default void getArray(@WritableValue @Nonnull char[] dest)
	{
		getArray(0, dest, 0, dest.length, dest.length*16l);
	}
	
	public default void getArrayFromSliceChar(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<char[]> dest)
	{
		getArray(sourceBitOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength(), dest.getLength()*16l);
	}
	
	public default void getArrayFromSliceChar(@WritableValue @Nonnull Slice<char[]> dest)
	{
		getArrayFromSliceChar(0, dest);
	}
	
	
	
	
	
	
	
	
	public default void setUnpackedArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull char[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 16)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < sourceLength; i++)
			setBitfield(destBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits, source[sourceElementOffset+i]);
	}
	
	public default void setUnpackedArray(@ReadonlyValue @Nonnull char[] source, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		setUnpackedArray(0, source, 0, source.length, lengthOfEachElementInBits);
	}
	
	public default void setUnpackedArrayFromSliceChar(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<char[]> source, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		setUnpackedArray(destBitOffset, source.getUnderlying(), source.getOffset(), source.getLength(), lengthOfEachElementInBits);
	}
	
	public default void setUnpackedArrayFromSliceChar(@ReadonlyValue @Nonnull Slice<char[]> source, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		setUnpackedArrayFromSliceChar(0, source, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull char[] dest, @Nonnegative int destElementOffset, @Nonnegative int destLength, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 16)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (char)getBitfield(sourceBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArray(@Nonnull @WritableValue char[] dest, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		getUnpackedArray(0, dest, 0, dest.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceChar(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<char[]> dest, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		getUnpackedArray(sourceBitOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceChar(@WritableValue @Nonnull Slice<char[]> dest, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSliceChar(0, dest, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	
	
	public default void setArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull short[] source, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*16l) long totalLengthOfDataToWriteInBits)
	{
		int primlen = 16;
		
		if (sourceLengthCheck != -1 && ceilingDivision(totalLengthOfDataToWriteInBits, primlen) > sourceLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToWriteInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfield(destBitOffset+i*primlen, primlen, source[sourceElementOffset+i]);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToWriteInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfield(safeCastS64toS32(destBitOffset+fullAmount), remainder, source[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void setArray(@ReadonlyValue @Nonnull short[] source)
	{
		setArray(0, source, 0, source.length, source.length*16l);
	}
	
	public default void setArrayFromSliceShort(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<short[]> source)
	{
		setArray(destBitOffset, source.getUnderlying(), source.getOffset(), source.getLength(), source.getLength()*16l);
	}
	
	public default void setArrayFromSliceShort(@ReadonlyValue @Nonnull Slice<short[]> source)
	{
		setArrayFromSliceShort(0, source);
	}
	
	
	
	public default void getArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull short[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*16l) long totalLengthOfDataToReadInBits)
	{
		int primlen = 16;
		
		if (destLengthCheck != -1 && ceilingDivision(totalLengthOfDataToReadInBits, primlen) > destLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToReadInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			dest[destElementOffset+i] = (short)getBitfield(sourceBitOffset+i*primlen, primlen);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToReadInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			dest[destElementOffset+numberOfFullElementsToUse] = (short)((dest[destElementOffset+numberOfFullElementsToUse] & (((1 << remainder) - 1) << (16 - remainder))) | getBitfield(safeCastS64toS32(sourceBitOffset+fullAmount), remainder));
	}
	
	public default void getArray(@WritableValue @Nonnull short[] dest)
	{
		getArray(0, dest, 0, dest.length, dest.length*16l);
	}
	
	public default void getArrayFromSliceShort(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<short[]> dest)
	{
		getArray(sourceBitOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength(), dest.getLength()*16l);
	}
	
	public default void getArrayFromSliceShort(@WritableValue @Nonnull Slice<short[]> dest)
	{
		getArrayFromSliceShort(0, dest);
	}
	
	
	
	
	
	
	
	
	public default void setUnpackedArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull short[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 16)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < sourceLength; i++)
			setBitfield(destBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits, source[sourceElementOffset+i]);
	}
	
	public default void setUnpackedArray(@ReadonlyValue @Nonnull short[] source, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		setUnpackedArray(0, source, 0, source.length, lengthOfEachElementInBits);
	}
	
	public default void setUnpackedArrayFromSliceShort(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<short[]> source, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		setUnpackedArray(destBitOffset, source.getUnderlying(), source.getOffset(), source.getLength(), lengthOfEachElementInBits);
	}
	
	public default void setUnpackedArrayFromSliceShort(@ReadonlyValue @Nonnull Slice<short[]> source, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		setUnpackedArrayFromSliceShort(0, source, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull short[] dest, @Nonnegative int destElementOffset, @Nonnegative int destLength, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 16)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (short)getBitfield(sourceBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArray(@Nonnull @WritableValue short[] dest, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		getUnpackedArray(0, dest, 0, dest.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceShort(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<short[]> dest, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		getUnpackedArray(sourceBitOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceShort(@WritableValue @Nonnull Slice<short[]> dest, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSliceShort(0, dest, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	
	
	public default void setArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull int[] source, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*32l) long totalLengthOfDataToWriteInBits)
	{
		int primlen = 32;
		
		if (sourceLengthCheck != -1 && ceilingDivision(totalLengthOfDataToWriteInBits, primlen) > sourceLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToWriteInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfield(destBitOffset+i*primlen, primlen, source[sourceElementOffset+i]);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToWriteInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfield(safeCastS64toS32(destBitOffset+fullAmount), remainder, source[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void setArray(@ReadonlyValue @Nonnull int[] source)
	{
		setArray(0, source, 0, source.length, source.length*32l);
	}
	
	public default void setArrayFromSliceInt(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<int[]> source)
	{
		setArray(destBitOffset, source.getUnderlying(), source.getOffset(), source.getLength(), source.getLength()*32l);
	}
	
	public default void setArrayFromSliceInt(@ReadonlyValue @Nonnull Slice<int[]> source)
	{
		setArrayFromSliceInt(0, source);
	}
	
	
	
	public default void getArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull int[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*32l) long totalLengthOfDataToReadInBits)
	{
		int primlen = 32;
		
		if (destLengthCheck != -1 && ceilingDivision(totalLengthOfDataToReadInBits, primlen) > destLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToReadInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			dest[destElementOffset+i] = (int)getBitfield(sourceBitOffset+i*primlen, primlen);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToReadInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			dest[destElementOffset+numberOfFullElementsToUse] = (int)((dest[destElementOffset+numberOfFullElementsToUse] & (((1 << remainder) - 1) << (32 - remainder))) | getBitfield(safeCastS64toS32(sourceBitOffset+fullAmount), remainder));
	}
	
	public default void getArray(@WritableValue @Nonnull int[] dest)
	{
		getArray(0, dest, 0, dest.length, dest.length*32l);
	}
	
	public default void getArrayFromSliceInt(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<int[]> dest)
	{
		getArray(sourceBitOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength(), dest.getLength()*32l);
	}
	
	public default void getArrayFromSliceInt(@WritableValue @Nonnull Slice<int[]> dest)
	{
		getArrayFromSliceInt(0, dest);
	}
	
	
	
	
	
	
	
	
	public default void setUnpackedArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull int[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 32)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < sourceLength; i++)
			setBitfield(destBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits, source[sourceElementOffset+i]);
	}
	
	public default void setUnpackedArray(@ReadonlyValue @Nonnull int[] source, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		setUnpackedArray(0, source, 0, source.length, lengthOfEachElementInBits);
	}
	
	public default void setUnpackedArrayFromSliceInt(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<int[]> source, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		setUnpackedArray(destBitOffset, source.getUnderlying(), source.getOffset(), source.getLength(), lengthOfEachElementInBits);
	}
	
	public default void setUnpackedArrayFromSliceInt(@ReadonlyValue @Nonnull Slice<int[]> source, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		setUnpackedArrayFromSliceInt(0, source, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull int[] dest, @Nonnegative int destElementOffset, @Nonnegative int destLength, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 32)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (int)getBitfield(sourceBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArray(@Nonnull @WritableValue int[] dest, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		getUnpackedArray(0, dest, 0, dest.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceInt(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<int[]> dest, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		getUnpackedArray(sourceBitOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceInt(@WritableValue @Nonnull Slice<int[]> dest, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSliceInt(0, dest, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	
	
	public default void setArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull long[] source, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*64l) long totalLengthOfDataToWriteInBits)
	{
		int primlen = 64;
		
		if (sourceLengthCheck != -1 && ceilingDivision(totalLengthOfDataToWriteInBits, primlen) > sourceLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToWriteInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfield(destBitOffset+i*primlen, primlen, source[sourceElementOffset+i]);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToWriteInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfield(safeCastS64toS32(destBitOffset+fullAmount), remainder, source[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void setArray(@ReadonlyValue @Nonnull long[] source)
	{
		setArray(0, source, 0, source.length, source.length*64l);
	}
	
	public default void setArrayFromSliceLong(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<long[]> source)
	{
		setArray(destBitOffset, source.getUnderlying(), source.getOffset(), source.getLength(), source.getLength()*64l);
	}
	
	public default void setArrayFromSliceLong(@ReadonlyValue @Nonnull Slice<long[]> source)
	{
		setArrayFromSliceLong(0, source);
	}
	
	
	
	public default void getArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull long[] dest, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*64l) long totalLengthOfDataToReadInBits)
	{
		int primlen = 64;
		
		if (destLengthCheck != -1 && ceilingDivision(totalLengthOfDataToReadInBits, primlen) > destLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToReadInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			dest[destElementOffset+i] = (long)getBitfield(sourceBitOffset+i*primlen, primlen);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToReadInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			dest[destElementOffset+numberOfFullElementsToUse] = (long)((dest[destElementOffset+numberOfFullElementsToUse] & (((1l << remainder) - 1) << (64 - remainder))) | getBitfield(safeCastS64toS32(sourceBitOffset+fullAmount), remainder));
	}
	
	public default void getArray(@WritableValue @Nonnull long[] dest)
	{
		getArray(0, dest, 0, dest.length, dest.length*64l);
	}
	
	public default void getArrayFromSliceLong(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<long[]> dest)
	{
		getArray(sourceBitOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength(), dest.getLength()*64l);
	}
	
	public default void getArrayFromSliceLong(@WritableValue @Nonnull Slice<long[]> dest)
	{
		getArrayFromSliceLong(0, dest);
	}
	
	
	
	
	
	
	
	
	public default void setUnpackedArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull long[] source, @Nonnegative int sourceElementOffset, @Nonnegative int sourceLength, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 64)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < sourceLength; i++)
			setBitfield(destBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits, source[sourceElementOffset+i]);
	}
	
	public default void setUnpackedArray(@ReadonlyValue @Nonnull long[] source, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		setUnpackedArray(0, source, 0, source.length, lengthOfEachElementInBits);
	}
	
	public default void setUnpackedArrayFromSliceLong(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<long[]> source, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		setUnpackedArray(destBitOffset, source.getUnderlying(), source.getOffset(), source.getLength(), lengthOfEachElementInBits);
	}
	
	public default void setUnpackedArrayFromSliceLong(@ReadonlyValue @Nonnull Slice<long[]> source, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		setUnpackedArrayFromSliceLong(0, source, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull long[] dest, @Nonnegative int destElementOffset, @Nonnegative int destLength, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 64)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < destLength; i++)
			dest[destElementOffset+i] = (long)getBitfield(sourceBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArray(@Nonnull @WritableValue long[] dest, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		getUnpackedArray(0, dest, 0, dest.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceLong(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<long[]> dest, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		getUnpackedArray(sourceBitOffset, dest.getUnderlying(), dest.getOffset(), dest.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceLong(@WritableValue @Nonnull Slice<long[]> dest, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSliceLong(0, dest, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	// >>>
}
