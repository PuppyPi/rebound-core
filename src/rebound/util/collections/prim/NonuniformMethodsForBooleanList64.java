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

@ImplementationTransparency  //This only exists to keep the bulk functions exactly uniform and correspondent :3     This might be removed soon XD''
public interface NonuniformMethodsForBooleanList64
extends DefaultToArraysBooleanCollection
{
	public @Nonnegative long size64();
	public boolean getBooleanBy64(@Nonnegative long index);
	public void setBooleanBy64(@Nonnegative long index, boolean value);
	
	
	
	
	public default @Nonnegative long getNumberOfOnesBy64()
	{
		long n = size64();
		long n1 = 0;
		for (int i = 0; i < n; i++)
			n1 += getBooleanBy64(i) ? 1 : 0;
		return n1;
	}
	
	public default @Nonnegative long getNumberOfZerosBy64()
	{
		return size64() - getNumberOfOnesBy64();
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * This only applies to the get/set's!
	 */
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	public default SpanningOperationImplementationType getMultibitOperationsImplementationGuaranteesFor64bitOffsets()
	{
		return SpanningOperationImplementationType.OneByOne;
	}
	
	
	
	public default long getBitfieldBy64(@Nonnegative long offset, @BoundedInt(min=1, max=64) int length)
	{
		long r = 0;
		
		for (int i = 0; i < length; i++)
		{
			boolean bit = getBooleanBy64(offset+i);
			r |= (bit ? 1l : 0l) << i;
		}
		
		return r;
	}
	
	public default void setBitfieldBy64(@Nonnegative long offset, @BoundedInt(min=1, max=64) int length, long bitfield)
	{
		for (int i = 0; i < length; i++)
		{
			boolean bit = ((1l << i) & bitfield) != 0;
			setBooleanBy64(offset+i, bit);
		}
	}
	
	
	
	
	public default byte getByteBy64(@Nonnegative long offsetInBits)
	{
		return (byte)getBitfieldBy64(offsetInBits, 8);
	}
	
	public default short getShortBy64(@Nonnegative long offsetInBits)
	{
		return (short)getBitfieldBy64(offsetInBits, 16);
	}
	
	public default int getIntBy64(@Nonnegative long offsetInBits)
	{
		return (int)getBitfieldBy64(offsetInBits, 32);
	}
	
	public default long getLongBy64(@Nonnegative long offsetInBits)
	{
		return getBitfieldBy64(offsetInBits, 64);
	}
	
	
	public default void setByteBy64(@Nonnegative long offsetInBits, byte value)
	{
		setBitfieldBy64(offsetInBits, 8, value);
	}
	
	public default void setShortBy64(@Nonnegative long offsetInBits, short value)
	{
		setBitfieldBy64(offsetInBits, 16, value);
	}
	
	public default void setIntBy64(@Nonnegative long offsetInBits, int value)
	{
		setBitfieldBy64(offsetInBits, 32, value);
	}
	
	public default void setLongBy64(@Nonnegative long offsetInBits, long value)
	{
		setBitfieldBy64(offsetInBits, 64, value);
	}
	
	
	
	
	public default byte getAlignedByteBy64(@Nonnegative long offsetAlignedInElements)
	{
		return (byte)getBitfieldBy64(offsetAlignedInElements * 8, 8);
	}
	
	public default short getAlignedShortBy64(@Nonnegative long offsetAlignedInElements)
	{
		return (short)getBitfieldBy64(offsetAlignedInElements * 16, 16);
	}
	
	public default int getAlignedIntBy64(@Nonnegative long offsetAlignedInElements)
	{
		return (int)getBitfieldBy64(offsetAlignedInElements * 32, 32);
	}
	
	public default long getAlignedLongBy64(@Nonnegative long offsetAlignedInElements)
	{
		return getBitfieldBy64(offsetAlignedInElements * 64, 64);
	}
	
	
	public default void setAlignedByteBy64(@Nonnegative long offsetAlignedInElements, byte value)
	{
		setBitfieldBy64(offsetAlignedInElements * 8, 8, value);
	}
	
	public default void setAlignedShortBy64(@Nonnegative long offsetAlignedInElements, short value)
	{
		setBitfieldBy64(offsetAlignedInElements * 16, 16, value);
	}
	
	public default void setAlignedIntBy64(@Nonnegative long offsetAlignedInElements, int value)
	{
		setBitfieldBy64(offsetAlignedInElements * 32, 32, value);
	}
	
	public default void setAlignedLongByBy64(@Nonnegative long offsetAlignedInElements, long value)
	{
		setBitfieldBy64(offsetAlignedInElements * 64, 64, value);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	primxp
	_$$primxpconf:intsonly$$_
	
	public default void putArrayBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull _$$prim$$_[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*_$$primlen$$_l) long totalLengthOfDataToWriteInBits)
	{
		int primlen = _$$primlen$$_;
		
		if (sourceLengthCheck != -1 && ceilingDivision(totalLengthOfDataToWriteInBits, primlen) > sourceLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToWriteInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfieldBy64(destBitOffset+i*primlen, primlen, bitfields[sourceElementOffset+i]);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToWriteInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfieldBy64(destBitOffset+fullAmount, remainder, bitfields[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void putArrayBy64(@ReadonlyValue @Nonnull _$$prim$$_[] bitfields)
	{
		putArrayBy64(0, bitfields, 0, bitfields.length, bitfields.length*_$$primlen$$_l);
	}
	
	public default void putArrayFromSlice_$$Prim$$_64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull Slice<_$$prim$$_[]> bitfields)
	{
		putArrayBy64(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*_$$primlen$$_l);
	}
	
	public default void putArrayFromSlice_$$Prim$$_64(@ReadonlyValue @Nonnull Slice<_$$prim$$_[]> bitfields)
	{
		putArrayFromSlice_$$Prim$$_64(0, bitfields);
	}
	
	
	
	public default void getArrayBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull _$$prim$$_[] bitfields, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*_$$primlen$$_l) long totalLengthOfDataToReadInBits)
	{
		int primlen = _$$primlen$$_;
		
		if (destLengthCheck != -1 && ceilingDivision(totalLengthOfDataToReadInBits, primlen) > destLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToReadInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			bitfields[destElementOffset+i] = (_$$prim$$_)getBitfieldBy64(sourceBitOffset+i*primlen, primlen);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToReadInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			bitfields[destElementOffset+numberOfFullElementsToUse] = (_$$prim$$_)((bitfields[destElementOffset+numberOfFullElementsToUse] & (((1_$$promotedsuffix$$_ << remainder) - 1) << (_$$primlen$$_ - remainder))) | getBitfieldBy64(sourceBitOffset+fullAmount, remainder));
	}
	
	public default void getArrayBy64(@WritableValue @Nonnull _$$prim$$_[] bitfields)
	{
		getArrayBy64(0, bitfields, 0, bitfields.length, bitfields.length*_$$primlen$$_l);
	}
	
	public default void getArrayFromSlice_$$Prim$$_64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull Slice<_$$prim$$_[]> bitfields)
	{
		getArrayBy64(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*_$$primlen$$_l);
	}
	
	public default void getArrayFromSlice_$$Prim$$_64(@WritableValue @Nonnull Slice<_$$prim$$_[]> bitfields)
	{
		getArrayFromSlice_$$Prim$$_64(0, bitfields);
	}
	
	
	
	
	
	
	
	
	public default void putUnpackedArrayBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull _$$prim$$_[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > _$$primlen$$_)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < sourceLengthCheck; i++)
			setBitfieldBy64(destBitOffset+i*((long)lengthOfEachElementInBits), lengthOfEachElementInBits, bitfields[sourceElementOffset+i]);
	}
	
	public default void putUnpackedArrayBy64(@ReadonlyValue @Nonnull _$$prim$$_[] bitfields, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		putUnpackedArrayBy64(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSlice_$$Prim$$_64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull Slice<_$$prim$$_[]> bitfields, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		putUnpackedArrayBy64(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSlice_$$Prim$$_64(@ReadonlyValue @Nonnull Slice<_$$prim$$_[]> bitfields, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		putUnpackedArrayFromSlice_$$Prim$$_64(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArrayBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull _$$prim$$_[] bitfields, @Nonnegative int destElementOffset, @Nonnegative int destLengthCheck, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > _$$primlen$$_)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < destLengthCheck; i++)
			bitfields[destElementOffset+i] = (_$$prim$$_)getBitfieldBy64(sourceBitOffset+i*((long)lengthOfEachElementInBits), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayBy64(@Nonnull @WritableValue _$$prim$$_[] bitfields, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		getUnpackedArrayBy64(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSlice_$$Prim$$_64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull Slice<_$$prim$$_[]> bitfields, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		getUnpackedArrayBy64(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSlice_$$Prim$$_64(@WritableValue @Nonnull Slice<_$$prim$$_[]> bitfields, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSlice_$$Prim$$_64(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	 */
	
	
	public default void putArrayBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull byte[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*8l) long totalLengthOfDataToWriteInBits)
	{
		int primlen = 8;
		
		if (sourceLengthCheck != -1 && ceilingDivision(totalLengthOfDataToWriteInBits, primlen) > sourceLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToWriteInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfieldBy64(destBitOffset+i*primlen, primlen, bitfields[sourceElementOffset+i]);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToWriteInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfieldBy64(destBitOffset+fullAmount, remainder, bitfields[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void putArrayBy64(@ReadonlyValue @Nonnull byte[] bitfields)
	{
		putArrayBy64(0, bitfields, 0, bitfields.length, bitfields.length*8l);
	}
	
	public default void putArrayFromSliceByteBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull Slice<byte[]> bitfields)
	{
		putArrayBy64(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*8l);
	}
	
	public default void putArrayFromSliceByteBy64(@ReadonlyValue @Nonnull Slice<byte[]> bitfields)
	{
		putArrayFromSliceByteBy64(0, bitfields);
	}
	
	
	
	public default void getArrayBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull byte[] bitfields, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*8l) long totalLengthOfDataToReadInBits)
	{
		int primlen = 8;
		
		if (destLengthCheck != -1 && ceilingDivision(totalLengthOfDataToReadInBits, primlen) > destLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToReadInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			bitfields[destElementOffset+i] = (byte)getBitfieldBy64(sourceBitOffset+i*primlen, primlen);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToReadInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			bitfields[destElementOffset+numberOfFullElementsToUse] = (byte)((bitfields[destElementOffset+numberOfFullElementsToUse] & (((1 << remainder) - 1) << (8 - remainder))) | getBitfieldBy64(sourceBitOffset+fullAmount, remainder));
	}
	
	public default void getArrayBy64(@WritableValue @Nonnull byte[] bitfields)
	{
		getArrayBy64(0, bitfields, 0, bitfields.length, bitfields.length*8l);
	}
	
	public default void getArrayFromSliceByteBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull Slice<byte[]> bitfields)
	{
		getArrayBy64(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*8l);
	}
	
	public default void getArrayFromSliceByteBy64(@WritableValue @Nonnull Slice<byte[]> bitfields)
	{
		getArrayFromSliceByteBy64(0, bitfields);
	}
	
	
	
	
	
	
	
	
	public default void putUnpackedArrayBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull byte[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 8)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < sourceLengthCheck; i++)
			setBitfieldBy64(destBitOffset+i*((long)lengthOfEachElementInBits), lengthOfEachElementInBits, bitfields[sourceElementOffset+i]);
	}
	
	public default void putUnpackedArrayBy64(@ReadonlyValue @Nonnull byte[] bitfields, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		putUnpackedArrayBy64(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceByteBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull Slice<byte[]> bitfields, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		putUnpackedArrayBy64(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceByteBy64(@ReadonlyValue @Nonnull Slice<byte[]> bitfields, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		putUnpackedArrayFromSliceByteBy64(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArrayBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull byte[] bitfields, @Nonnegative int destElementOffset, @Nonnegative int destLengthCheck, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 8)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < destLengthCheck; i++)
			bitfields[destElementOffset+i] = (byte)getBitfieldBy64(sourceBitOffset+i*((long)lengthOfEachElementInBits), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayBy64(@Nonnull @WritableValue byte[] bitfields, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		getUnpackedArrayBy64(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceByteBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull Slice<byte[]> bitfields, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		getUnpackedArrayBy64(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceByteBy64(@WritableValue @Nonnull Slice<byte[]> bitfields, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSliceByteBy64(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	
	
	public default void putArrayBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull char[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*16l) long totalLengthOfDataToWriteInBits)
	{
		int primlen = 16;
		
		if (sourceLengthCheck != -1 && ceilingDivision(totalLengthOfDataToWriteInBits, primlen) > sourceLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToWriteInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfieldBy64(destBitOffset+i*primlen, primlen, bitfields[sourceElementOffset+i]);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToWriteInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfieldBy64(destBitOffset+fullAmount, remainder, bitfields[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void putArrayBy64(@ReadonlyValue @Nonnull char[] bitfields)
	{
		putArrayBy64(0, bitfields, 0, bitfields.length, bitfields.length*16l);
	}
	
	public default void putArrayFromSliceCharBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull Slice<char[]> bitfields)
	{
		putArrayBy64(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*16l);
	}
	
	public default void putArrayFromSliceCharBy64(@ReadonlyValue @Nonnull Slice<char[]> bitfields)
	{
		putArrayFromSliceCharBy64(0, bitfields);
	}
	
	
	
	public default void getArrayBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull char[] bitfields, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*16l) long totalLengthOfDataToReadInBits)
	{
		int primlen = 16;
		
		if (destLengthCheck != -1 && ceilingDivision(totalLengthOfDataToReadInBits, primlen) > destLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToReadInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			bitfields[destElementOffset+i] = (char)getBitfieldBy64(sourceBitOffset+i*primlen, primlen);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToReadInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			bitfields[destElementOffset+numberOfFullElementsToUse] = (char)((bitfields[destElementOffset+numberOfFullElementsToUse] & (((1 << remainder) - 1) << (16 - remainder))) | getBitfieldBy64(sourceBitOffset+fullAmount, remainder));
	}
	
	public default void getArrayBy64(@WritableValue @Nonnull char[] bitfields)
	{
		getArrayBy64(0, bitfields, 0, bitfields.length, bitfields.length*16l);
	}
	
	public default void getArrayFromSliceCharBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull Slice<char[]> bitfields)
	{
		getArrayBy64(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*16l);
	}
	
	public default void getArrayFromSliceCharBy64(@WritableValue @Nonnull Slice<char[]> bitfields)
	{
		getArrayFromSliceCharBy64(0, bitfields);
	}
	
	
	
	
	
	
	
	
	public default void putUnpackedArrayBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull char[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 16)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < sourceLengthCheck; i++)
			setBitfieldBy64(destBitOffset+i*((long)lengthOfEachElementInBits), lengthOfEachElementInBits, bitfields[sourceElementOffset+i]);
	}
	
	public default void putUnpackedArrayBy64(@ReadonlyValue @Nonnull char[] bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		putUnpackedArrayBy64(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceCharBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull Slice<char[]> bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		putUnpackedArrayBy64(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceCharBy64(@ReadonlyValue @Nonnull Slice<char[]> bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		putUnpackedArrayFromSliceCharBy64(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArrayBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull char[] bitfields, @Nonnegative int destElementOffset, @Nonnegative int destLengthCheck, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 16)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < destLengthCheck; i++)
			bitfields[destElementOffset+i] = (char)getBitfieldBy64(sourceBitOffset+i*((long)lengthOfEachElementInBits), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayBy64(@Nonnull @WritableValue char[] bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		getUnpackedArrayBy64(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceCharBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull Slice<char[]> bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		getUnpackedArrayBy64(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceCharBy64(@WritableValue @Nonnull Slice<char[]> bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSliceCharBy64(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	
	
	public default void putArrayBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull short[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*16l) long totalLengthOfDataToWriteInBits)
	{
		int primlen = 16;
		
		if (sourceLengthCheck != -1 && ceilingDivision(totalLengthOfDataToWriteInBits, primlen) > sourceLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToWriteInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfieldBy64(destBitOffset+i*primlen, primlen, bitfields[sourceElementOffset+i]);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToWriteInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfieldBy64(destBitOffset+fullAmount, remainder, bitfields[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void putArrayBy64(@ReadonlyValue @Nonnull short[] bitfields)
	{
		putArrayBy64(0, bitfields, 0, bitfields.length, bitfields.length*16l);
	}
	
	public default void putArrayFromSliceShortBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull Slice<short[]> bitfields)
	{
		putArrayBy64(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*16l);
	}
	
	public default void putArrayFromSliceShortBy64(@ReadonlyValue @Nonnull Slice<short[]> bitfields)
	{
		putArrayFromSliceShortBy64(0, bitfields);
	}
	
	
	
	public default void getArrayBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull short[] bitfields, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*16l) long totalLengthOfDataToReadInBits)
	{
		int primlen = 16;
		
		if (destLengthCheck != -1 && ceilingDivision(totalLengthOfDataToReadInBits, primlen) > destLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToReadInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			bitfields[destElementOffset+i] = (short)getBitfieldBy64(sourceBitOffset+i*primlen, primlen);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToReadInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			bitfields[destElementOffset+numberOfFullElementsToUse] = (short)((bitfields[destElementOffset+numberOfFullElementsToUse] & (((1 << remainder) - 1) << (16 - remainder))) | getBitfieldBy64(sourceBitOffset+fullAmount, remainder));
	}
	
	public default void getArrayBy64(@WritableValue @Nonnull short[] bitfields)
	{
		getArrayBy64(0, bitfields, 0, bitfields.length, bitfields.length*16l);
	}
	
	public default void getArrayFromSliceShortBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull Slice<short[]> bitfields)
	{
		getArrayBy64(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*16l);
	}
	
	public default void getArrayFromSliceShortBy64(@WritableValue @Nonnull Slice<short[]> bitfields)
	{
		getArrayFromSliceShortBy64(0, bitfields);
	}
	
	
	
	
	
	
	
	
	public default void putUnpackedArrayBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull short[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 16)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < sourceLengthCheck; i++)
			setBitfieldBy64(destBitOffset+i*((long)lengthOfEachElementInBits), lengthOfEachElementInBits, bitfields[sourceElementOffset+i]);
	}
	
	public default void putUnpackedArrayBy64(@ReadonlyValue @Nonnull short[] bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		putUnpackedArrayBy64(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceShortBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull Slice<short[]> bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		putUnpackedArrayBy64(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceShortBy64(@ReadonlyValue @Nonnull Slice<short[]> bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		putUnpackedArrayFromSliceShortBy64(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArrayBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull short[] bitfields, @Nonnegative int destElementOffset, @Nonnegative int destLengthCheck, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 16)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < destLengthCheck; i++)
			bitfields[destElementOffset+i] = (short)getBitfieldBy64(sourceBitOffset+i*((long)lengthOfEachElementInBits), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayBy64(@Nonnull @WritableValue short[] bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		getUnpackedArrayBy64(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceShortBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull Slice<short[]> bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		getUnpackedArrayBy64(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceShortBy64(@WritableValue @Nonnull Slice<short[]> bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSliceShortBy64(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	
	
	public default void putArrayBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull int[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*32l) long totalLengthOfDataToWriteInBits)
	{
		int primlen = 32;
		
		if (sourceLengthCheck != -1 && ceilingDivision(totalLengthOfDataToWriteInBits, primlen) > sourceLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToWriteInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfieldBy64(destBitOffset+i*primlen, primlen, bitfields[sourceElementOffset+i]);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToWriteInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfieldBy64(destBitOffset+fullAmount, remainder, bitfields[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void putArrayBy64(@ReadonlyValue @Nonnull int[] bitfields)
	{
		putArrayBy64(0, bitfields, 0, bitfields.length, bitfields.length*32l);
	}
	
	public default void putArrayFromSliceIntBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull Slice<int[]> bitfields)
	{
		putArrayBy64(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*32l);
	}
	
	public default void putArrayFromSliceIntBy64(@ReadonlyValue @Nonnull Slice<int[]> bitfields)
	{
		putArrayFromSliceIntBy64(0, bitfields);
	}
	
	
	
	public default void getArrayBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull int[] bitfields, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*32l) long totalLengthOfDataToReadInBits)
	{
		int primlen = 32;
		
		if (destLengthCheck != -1 && ceilingDivision(totalLengthOfDataToReadInBits, primlen) > destLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToReadInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			bitfields[destElementOffset+i] = (int)getBitfieldBy64(sourceBitOffset+i*primlen, primlen);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToReadInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			bitfields[destElementOffset+numberOfFullElementsToUse] = (int)((bitfields[destElementOffset+numberOfFullElementsToUse] & (((1 << remainder) - 1) << (32 - remainder))) | getBitfieldBy64(sourceBitOffset+fullAmount, remainder));
	}
	
	public default void getArrayBy64(@WritableValue @Nonnull int[] bitfields)
	{
		getArrayBy64(0, bitfields, 0, bitfields.length, bitfields.length*32l);
	}
	
	public default void getArrayFromSliceIntBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull Slice<int[]> bitfields)
	{
		getArrayBy64(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*32l);
	}
	
	public default void getArrayFromSliceIntBy64(@WritableValue @Nonnull Slice<int[]> bitfields)
	{
		getArrayFromSliceIntBy64(0, bitfields);
	}
	
	
	
	
	
	
	
	
	public default void putUnpackedArrayBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull int[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 32)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < sourceLengthCheck; i++)
			setBitfieldBy64(destBitOffset+i*((long)lengthOfEachElementInBits), lengthOfEachElementInBits, bitfields[sourceElementOffset+i]);
	}
	
	public default void putUnpackedArrayBy64(@ReadonlyValue @Nonnull int[] bitfields, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		putUnpackedArrayBy64(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceIntBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull Slice<int[]> bitfields, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		putUnpackedArrayBy64(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceIntBy64(@ReadonlyValue @Nonnull Slice<int[]> bitfields, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		putUnpackedArrayFromSliceIntBy64(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArrayBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull int[] bitfields, @Nonnegative int destElementOffset, @Nonnegative int destLengthCheck, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 32)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < destLengthCheck; i++)
			bitfields[destElementOffset+i] = (int)getBitfieldBy64(sourceBitOffset+i*((long)lengthOfEachElementInBits), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayBy64(@Nonnull @WritableValue int[] bitfields, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		getUnpackedArrayBy64(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceIntBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull Slice<int[]> bitfields, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		getUnpackedArrayBy64(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceIntBy64(@WritableValue @Nonnull Slice<int[]> bitfields, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSliceIntBy64(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	
	
	public default void putArrayBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull long[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*64l) long totalLengthOfDataToWriteInBits)
	{
		int primlen = 64;
		
		if (sourceLengthCheck != -1 && ceilingDivision(totalLengthOfDataToWriteInBits, primlen) > sourceLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToWriteInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfieldBy64(destBitOffset+i*primlen, primlen, bitfields[sourceElementOffset+i]);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToWriteInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfieldBy64(destBitOffset+fullAmount, remainder, bitfields[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void putArrayBy64(@ReadonlyValue @Nonnull long[] bitfields)
	{
		putArrayBy64(0, bitfields, 0, bitfields.length, bitfields.length*64l);
	}
	
	public default void putArrayFromSliceLongBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull Slice<long[]> bitfields)
	{
		putArrayBy64(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*64l);
	}
	
	public default void putArrayFromSliceLongBy64(@ReadonlyValue @Nonnull Slice<long[]> bitfields)
	{
		putArrayFromSliceLongBy64(0, bitfields);
	}
	
	
	
	public default void getArrayBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull long[] bitfields, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*64l) long totalLengthOfDataToReadInBits)
	{
		int primlen = 64;
		
		if (destLengthCheck != -1 && ceilingDivision(totalLengthOfDataToReadInBits, primlen) > destLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToReadInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			bitfields[destElementOffset+i] = (long)getBitfieldBy64(sourceBitOffset+i*primlen, primlen);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToReadInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			bitfields[destElementOffset+numberOfFullElementsToUse] = (long)((bitfields[destElementOffset+numberOfFullElementsToUse] & (((1l << remainder) - 1) << (64 - remainder))) | getBitfieldBy64(sourceBitOffset+fullAmount, remainder));
	}
	
	public default void getArrayBy64(@WritableValue @Nonnull long[] bitfields)
	{
		getArrayBy64(0, bitfields, 0, bitfields.length, bitfields.length*64l);
	}
	
	public default void getArrayFromSliceLongBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull Slice<long[]> bitfields)
	{
		getArrayBy64(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*64l);
	}
	
	public default void getArrayFromSliceLongBy64(@WritableValue @Nonnull Slice<long[]> bitfields)
	{
		getArrayFromSliceLongBy64(0, bitfields);
	}
	
	
	
	
	
	
	
	
	public default void putUnpackedArrayBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull long[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 64)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < sourceLengthCheck; i++)
			setBitfieldBy64(destBitOffset+i*((long)lengthOfEachElementInBits), lengthOfEachElementInBits, bitfields[sourceElementOffset+i]);
	}
	
	public default void putUnpackedArrayBy64(@ReadonlyValue @Nonnull long[] bitfields, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		putUnpackedArrayBy64(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceLongBy64(@Nonnegative long destBitOffset, @ReadonlyValue @Nonnull Slice<long[]> bitfields, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		putUnpackedArrayBy64(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceLongBy64(@ReadonlyValue @Nonnull Slice<long[]> bitfields, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		putUnpackedArrayFromSliceLongBy64(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArrayBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull long[] bitfields, @Nonnegative int destElementOffset, @Nonnegative int destLengthCheck, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 64)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < destLengthCheck; i++)
			bitfields[destElementOffset+i] = (long)getBitfieldBy64(sourceBitOffset+i*((long)lengthOfEachElementInBits), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayBy64(@Nonnull @WritableValue long[] bitfields, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		getUnpackedArrayBy64(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceLongBy64(@Nonnegative long sourceBitOffset, @WritableValue @Nonnull Slice<long[]> bitfields, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		getUnpackedArrayBy64(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceLongBy64(@WritableValue @Nonnull Slice<long[]> bitfields, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSliceLongBy64(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	// >>>
}
