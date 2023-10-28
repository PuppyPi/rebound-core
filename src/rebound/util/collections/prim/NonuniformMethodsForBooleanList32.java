package rebound.util.collections.prim;

import static rebound.bits.BitfieldSafeCasts.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.simpledata.BoundedInt;
import rebound.annotations.semantic.simpledata.BoundedLong;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.DefaultToArraysBooleanCollection;

//TODO Elementwise boolean operations between BooleanLists!!  AND, OR, NOT, XOR!  \:D/

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
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	primxp
	_$$primxpconf:intsonly$$_
	
	public default void putArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull _$$prim$$_[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*_$$primlen$$_l) long totalLengthOfDataToWriteInBits)
	{
		int primlen = _$$primlen$$_;
		
		if (sourceLengthCheck != -1 && ceilingDivision(totalLengthOfDataToWriteInBits, primlen) > sourceLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToWriteInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfield(destBitOffset+i*primlen, primlen, bitfields[sourceElementOffset+i]);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToWriteInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfield(safeCastS64toS32(destBitOffset+fullAmount), remainder, bitfields[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void putArray(@ReadonlyValue @Nonnull _$$prim$$_[] bitfields)
	{
		putArray(0, bitfields, 0, bitfields.length, bitfields.length*_$$primlen$$_l);
	}
	
	public default void putArrayFromSlice_$$Prim$$_(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<_$$prim$$_[]> bitfields)
	{
		putArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*_$$primlen$$_l);
	}
	
	public default void putArrayFromSlice_$$Prim$$_(@ReadonlyValue @Nonnull Slice<_$$prim$$_[]> bitfields)
	{
		putArrayFromSlice_$$Prim$$_(0, bitfields);
	}
	
	
	
	public default void getArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull _$$prim$$_[] bitfields, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*_$$primlen$$_l) long totalLengthOfDataToReadInBits)
	{
		int primlen = _$$primlen$$_;
		
		if (destLengthCheck != -1 && ceilingDivision(totalLengthOfDataToReadInBits, primlen) > destLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToReadInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			bitfields[destElementOffset+i] = (_$$prim$$_)getBitfield(sourceBitOffset+i*primlen, primlen);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToReadInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			bitfields[destElementOffset+numberOfFullElementsToUse] = (_$$prim$$_)((bitfields[destElementOffset+numberOfFullElementsToUse] & (((1_$$promotedsuffix$$_ << remainder) - 1) << (_$$primlen$$_ - remainder))) | getBitfield(safeCastS64toS32(sourceBitOffset+fullAmount), remainder));
	}
	
	public default void getArray(@WritableValue @Nonnull _$$prim$$_[] bitfields)
	{
		getArray(0, bitfields, 0, bitfields.length, bitfields.length*_$$primlen$$_l);
	}
	
	public default void getArrayFromSlice_$$Prim$$_(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<_$$prim$$_[]> bitfields)
	{
		getArray(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*_$$primlen$$_l);
	}
	
	public default void getArrayFromSlice_$$Prim$$_(@WritableValue @Nonnull Slice<_$$prim$$_[]> bitfields)
	{
		getArrayFromSlice_$$Prim$$_(0, bitfields);
	}
	
	
	
	
	
	
	
	
	public default void putUnpackedArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull _$$prim$$_[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > _$$primlen$$_)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < sourceLengthCheck; i++)
			setBitfield(destBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits, bitfields[sourceElementOffset+i]);
	}
	
	public default void putUnpackedArray(@ReadonlyValue @Nonnull _$$prim$$_[] bitfields, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		putUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSlice_$$Prim$$_(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<_$$prim$$_[]> bitfields, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		putUnpackedArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSlice_$$Prim$$_(@ReadonlyValue @Nonnull Slice<_$$prim$$_[]> bitfields, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		putUnpackedArrayFromSlice_$$Prim$$_(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull _$$prim$$_[] bitfields, @Nonnegative int destElementOffset, @Nonnegative int destLengthCheck, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > _$$primlen$$_)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < destLengthCheck; i++)
			bitfields[destElementOffset+i] = (_$$prim$$_)getBitfield(sourceBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArray(@Nonnull @WritableValue _$$prim$$_[] bitfields, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		getUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSlice_$$Prim$$_(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<_$$prim$$_[]> bitfields, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		getUnpackedArray(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSlice_$$Prim$$_(@WritableValue @Nonnull Slice<_$$prim$$_[]> bitfields, @BoundedInt(min=0, max=_$$primlen$$_) int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSlice_$$Prim$$_(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	 */
	
	
	public default void putArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull byte[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*8l) long totalLengthOfDataToWriteInBits)
	{
		int primlen = 8;
		
		if (sourceLengthCheck != -1 && ceilingDivision(totalLengthOfDataToWriteInBits, primlen) > sourceLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToWriteInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfield(destBitOffset+i*primlen, primlen, bitfields[sourceElementOffset+i]);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToWriteInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfield(safeCastS64toS32(destBitOffset+fullAmount), remainder, bitfields[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void putArray(@ReadonlyValue @Nonnull byte[] bitfields)
	{
		putArray(0, bitfields, 0, bitfields.length, bitfields.length*8l);
	}
	
	public default void putArrayFromSliceByte(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<byte[]> bitfields)
	{
		putArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*8l);
	}
	
	public default void putArrayFromSliceByte(@ReadonlyValue @Nonnull Slice<byte[]> bitfields)
	{
		putArrayFromSliceByte(0, bitfields);
	}
	
	
	
	public default void getArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull byte[] bitfields, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*8l) long totalLengthOfDataToReadInBits)
	{
		int primlen = 8;
		
		if (destLengthCheck != -1 && ceilingDivision(totalLengthOfDataToReadInBits, primlen) > destLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToReadInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			bitfields[destElementOffset+i] = (byte)getBitfield(sourceBitOffset+i*primlen, primlen);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToReadInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			bitfields[destElementOffset+numberOfFullElementsToUse] = (byte)((bitfields[destElementOffset+numberOfFullElementsToUse] & (((1 << remainder) - 1) << (8 - remainder))) | getBitfield(safeCastS64toS32(sourceBitOffset+fullAmount), remainder));
	}
	
	public default void getArray(@WritableValue @Nonnull byte[] bitfields)
	{
		getArray(0, bitfields, 0, bitfields.length, bitfields.length*8l);
	}
	
	public default void getArrayFromSliceByte(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<byte[]> bitfields)
	{
		getArray(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*8l);
	}
	
	public default void getArrayFromSliceByte(@WritableValue @Nonnull Slice<byte[]> bitfields)
	{
		getArrayFromSliceByte(0, bitfields);
	}
	
	
	
	
	
	
	
	
	public default void putUnpackedArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull byte[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 8)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < sourceLengthCheck; i++)
			setBitfield(destBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits, bitfields[sourceElementOffset+i]);
	}
	
	public default void putUnpackedArray(@ReadonlyValue @Nonnull byte[] bitfields, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		putUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceByte(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<byte[]> bitfields, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		putUnpackedArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceByte(@ReadonlyValue @Nonnull Slice<byte[]> bitfields, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		putUnpackedArrayFromSliceByte(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull byte[] bitfields, @Nonnegative int destElementOffset, @Nonnegative int destLengthCheck, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 8)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < destLengthCheck; i++)
			bitfields[destElementOffset+i] = (byte)getBitfield(sourceBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArray(@Nonnull @WritableValue byte[] bitfields, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		getUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceByte(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<byte[]> bitfields, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		getUnpackedArray(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceByte(@WritableValue @Nonnull Slice<byte[]> bitfields, @BoundedInt(min=0, max=8) int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSliceByte(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	
	
	public default void putArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull char[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*16l) long totalLengthOfDataToWriteInBits)
	{
		int primlen = 16;
		
		if (sourceLengthCheck != -1 && ceilingDivision(totalLengthOfDataToWriteInBits, primlen) > sourceLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToWriteInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfield(destBitOffset+i*primlen, primlen, bitfields[sourceElementOffset+i]);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToWriteInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfield(safeCastS64toS32(destBitOffset+fullAmount), remainder, bitfields[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void putArray(@ReadonlyValue @Nonnull char[] bitfields)
	{
		putArray(0, bitfields, 0, bitfields.length, bitfields.length*16l);
	}
	
	public default void putArrayFromSliceChar(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<char[]> bitfields)
	{
		putArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*16l);
	}
	
	public default void putArrayFromSliceChar(@ReadonlyValue @Nonnull Slice<char[]> bitfields)
	{
		putArrayFromSliceChar(0, bitfields);
	}
	
	
	
	public default void getArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull char[] bitfields, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*16l) long totalLengthOfDataToReadInBits)
	{
		int primlen = 16;
		
		if (destLengthCheck != -1 && ceilingDivision(totalLengthOfDataToReadInBits, primlen) > destLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToReadInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			bitfields[destElementOffset+i] = (char)getBitfield(sourceBitOffset+i*primlen, primlen);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToReadInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			bitfields[destElementOffset+numberOfFullElementsToUse] = (char)((bitfields[destElementOffset+numberOfFullElementsToUse] & (((1 << remainder) - 1) << (16 - remainder))) | getBitfield(safeCastS64toS32(sourceBitOffset+fullAmount), remainder));
	}
	
	public default void getArray(@WritableValue @Nonnull char[] bitfields)
	{
		getArray(0, bitfields, 0, bitfields.length, bitfields.length*16l);
	}
	
	public default void getArrayFromSliceChar(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<char[]> bitfields)
	{
		getArray(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*16l);
	}
	
	public default void getArrayFromSliceChar(@WritableValue @Nonnull Slice<char[]> bitfields)
	{
		getArrayFromSliceChar(0, bitfields);
	}
	
	
	
	
	
	
	
	
	public default void putUnpackedArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull char[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 16)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < sourceLengthCheck; i++)
			setBitfield(destBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits, bitfields[sourceElementOffset+i]);
	}
	
	public default void putUnpackedArray(@ReadonlyValue @Nonnull char[] bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		putUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceChar(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<char[]> bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		putUnpackedArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceChar(@ReadonlyValue @Nonnull Slice<char[]> bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		putUnpackedArrayFromSliceChar(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull char[] bitfields, @Nonnegative int destElementOffset, @Nonnegative int destLengthCheck, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 16)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < destLengthCheck; i++)
			bitfields[destElementOffset+i] = (char)getBitfield(sourceBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArray(@Nonnull @WritableValue char[] bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		getUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceChar(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<char[]> bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		getUnpackedArray(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceChar(@WritableValue @Nonnull Slice<char[]> bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSliceChar(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	
	
	public default void putArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull short[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*16l) long totalLengthOfDataToWriteInBits)
	{
		int primlen = 16;
		
		if (sourceLengthCheck != -1 && ceilingDivision(totalLengthOfDataToWriteInBits, primlen) > sourceLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToWriteInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfield(destBitOffset+i*primlen, primlen, bitfields[sourceElementOffset+i]);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToWriteInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfield(safeCastS64toS32(destBitOffset+fullAmount), remainder, bitfields[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void putArray(@ReadonlyValue @Nonnull short[] bitfields)
	{
		putArray(0, bitfields, 0, bitfields.length, bitfields.length*16l);
	}
	
	public default void putArrayFromSliceShort(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<short[]> bitfields)
	{
		putArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*16l);
	}
	
	public default void putArrayFromSliceShort(@ReadonlyValue @Nonnull Slice<short[]> bitfields)
	{
		putArrayFromSliceShort(0, bitfields);
	}
	
	
	
	public default void getArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull short[] bitfields, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*16l) long totalLengthOfDataToReadInBits)
	{
		int primlen = 16;
		
		if (destLengthCheck != -1 && ceilingDivision(totalLengthOfDataToReadInBits, primlen) > destLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToReadInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			bitfields[destElementOffset+i] = (short)getBitfield(sourceBitOffset+i*primlen, primlen);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToReadInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			bitfields[destElementOffset+numberOfFullElementsToUse] = (short)((bitfields[destElementOffset+numberOfFullElementsToUse] & (((1 << remainder) - 1) << (16 - remainder))) | getBitfield(safeCastS64toS32(sourceBitOffset+fullAmount), remainder));
	}
	
	public default void getArray(@WritableValue @Nonnull short[] bitfields)
	{
		getArray(0, bitfields, 0, bitfields.length, bitfields.length*16l);
	}
	
	public default void getArrayFromSliceShort(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<short[]> bitfields)
	{
		getArray(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*16l);
	}
	
	public default void getArrayFromSliceShort(@WritableValue @Nonnull Slice<short[]> bitfields)
	{
		getArrayFromSliceShort(0, bitfields);
	}
	
	
	
	
	
	
	
	
	public default void putUnpackedArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull short[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 16)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < sourceLengthCheck; i++)
			setBitfield(destBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits, bitfields[sourceElementOffset+i]);
	}
	
	public default void putUnpackedArray(@ReadonlyValue @Nonnull short[] bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		putUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceShort(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<short[]> bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		putUnpackedArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceShort(@ReadonlyValue @Nonnull Slice<short[]> bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		putUnpackedArrayFromSliceShort(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull short[] bitfields, @Nonnegative int destElementOffset, @Nonnegative int destLengthCheck, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 16)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < destLengthCheck; i++)
			bitfields[destElementOffset+i] = (short)getBitfield(sourceBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArray(@Nonnull @WritableValue short[] bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		getUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceShort(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<short[]> bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		getUnpackedArray(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceShort(@WritableValue @Nonnull Slice<short[]> bitfields, @BoundedInt(min=0, max=16) int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSliceShort(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	
	
	public default void putArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull int[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*32l) long totalLengthOfDataToWriteInBits)
	{
		int primlen = 32;
		
		if (sourceLengthCheck != -1 && ceilingDivision(totalLengthOfDataToWriteInBits, primlen) > sourceLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToWriteInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfield(destBitOffset+i*primlen, primlen, bitfields[sourceElementOffset+i]);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToWriteInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfield(safeCastS64toS32(destBitOffset+fullAmount), remainder, bitfields[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void putArray(@ReadonlyValue @Nonnull int[] bitfields)
	{
		putArray(0, bitfields, 0, bitfields.length, bitfields.length*32l);
	}
	
	public default void putArrayFromSliceInt(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<int[]> bitfields)
	{
		putArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*32l);
	}
	
	public default void putArrayFromSliceInt(@ReadonlyValue @Nonnull Slice<int[]> bitfields)
	{
		putArrayFromSliceInt(0, bitfields);
	}
	
	
	
	public default void getArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull int[] bitfields, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*32l) long totalLengthOfDataToReadInBits)
	{
		int primlen = 32;
		
		if (destLengthCheck != -1 && ceilingDivision(totalLengthOfDataToReadInBits, primlen) > destLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToReadInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			bitfields[destElementOffset+i] = (int)getBitfield(sourceBitOffset+i*primlen, primlen);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToReadInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			bitfields[destElementOffset+numberOfFullElementsToUse] = (int)((bitfields[destElementOffset+numberOfFullElementsToUse] & (((1 << remainder) - 1) << (32 - remainder))) | getBitfield(safeCastS64toS32(sourceBitOffset+fullAmount), remainder));
	}
	
	public default void getArray(@WritableValue @Nonnull int[] bitfields)
	{
		getArray(0, bitfields, 0, bitfields.length, bitfields.length*32l);
	}
	
	public default void getArrayFromSliceInt(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<int[]> bitfields)
	{
		getArray(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*32l);
	}
	
	public default void getArrayFromSliceInt(@WritableValue @Nonnull Slice<int[]> bitfields)
	{
		getArrayFromSliceInt(0, bitfields);
	}
	
	
	
	
	
	
	
	
	public default void putUnpackedArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull int[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 32)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < sourceLengthCheck; i++)
			setBitfield(destBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits, bitfields[sourceElementOffset+i]);
	}
	
	public default void putUnpackedArray(@ReadonlyValue @Nonnull int[] bitfields, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		putUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceInt(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<int[]> bitfields, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		putUnpackedArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceInt(@ReadonlyValue @Nonnull Slice<int[]> bitfields, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		putUnpackedArrayFromSliceInt(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull int[] bitfields, @Nonnegative int destElementOffset, @Nonnegative int destLengthCheck, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 32)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < destLengthCheck; i++)
			bitfields[destElementOffset+i] = (int)getBitfield(sourceBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArray(@Nonnull @WritableValue int[] bitfields, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		getUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceInt(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<int[]> bitfields, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		getUnpackedArray(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceInt(@WritableValue @Nonnull Slice<int[]> bitfields, @BoundedInt(min=0, max=32) int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSliceInt(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	
	
	public default void putArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull long[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*64l) long totalLengthOfDataToWriteInBits)
	{
		int primlen = 64;
		
		if (sourceLengthCheck != -1 && ceilingDivision(totalLengthOfDataToWriteInBits, primlen) > sourceLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToWriteInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfield(destBitOffset+i*primlen, primlen, bitfields[sourceElementOffset+i]);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToWriteInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfield(safeCastS64toS32(destBitOffset+fullAmount), remainder, bitfields[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void putArray(@ReadonlyValue @Nonnull long[] bitfields)
	{
		putArray(0, bitfields, 0, bitfields.length, bitfields.length*64l);
	}
	
	public default void putArrayFromSliceLong(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<long[]> bitfields)
	{
		putArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*64l);
	}
	
	public default void putArrayFromSliceLong(@ReadonlyValue @Nonnull Slice<long[]> bitfields)
	{
		putArrayFromSliceLong(0, bitfields);
	}
	
	
	
	public default void getArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull long[] bitfields, @Nonnegative int destElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int destLengthCheck, @BoundedLong(min=0, max=Integer.MAX_VALUE*64l) long totalLengthOfDataToReadInBits)
	{
		int primlen = 64;
		
		if (destLengthCheck != -1 && ceilingDivision(totalLengthOfDataToReadInBits, primlen) > destLengthCheck)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = safeCastS64toS32(totalLengthOfDataToReadInBits/primlen);
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			bitfields[destElementOffset+i] = (long)getBitfield(sourceBitOffset+i*primlen, primlen);
		
		long fullAmount = numberOfFullElementsToUse * ((long)primlen);
		int remainder = safeCastS64toS32(totalLengthOfDataToReadInBits - fullAmount);
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			bitfields[destElementOffset+numberOfFullElementsToUse] = (long)((bitfields[destElementOffset+numberOfFullElementsToUse] & (((1l << remainder) - 1) << (64 - remainder))) | getBitfield(safeCastS64toS32(sourceBitOffset+fullAmount), remainder));
	}
	
	public default void getArray(@WritableValue @Nonnull long[] bitfields)
	{
		getArray(0, bitfields, 0, bitfields.length, bitfields.length*64l);
	}
	
	public default void getArrayFromSliceLong(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<long[]> bitfields)
	{
		getArray(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*64l);
	}
	
	public default void getArrayFromSliceLong(@WritableValue @Nonnull Slice<long[]> bitfields)
	{
		getArrayFromSliceLong(0, bitfields);
	}
	
	
	
	
	
	
	
	
	public default void putUnpackedArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull long[] bitfields, @Nonnegative int sourceElementOffset, @BoundedInt(min=-1, max=Integer.MAX_VALUE) int sourceLengthCheck, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 64)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < sourceLengthCheck; i++)
			setBitfield(destBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits, bitfields[sourceElementOffset+i]);
	}
	
	public default void putUnpackedArray(@ReadonlyValue @Nonnull long[] bitfields, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		putUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceLong(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<long[]> bitfields, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		putUnpackedArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceLong(@ReadonlyValue @Nonnull Slice<long[]> bitfields, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		putUnpackedArrayFromSliceLong(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull long[] bitfields, @Nonnegative int destElementOffset, @Nonnegative int destLengthCheck, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 64)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < destLengthCheck; i++)
			bitfields[destElementOffset+i] = (long)getBitfield(sourceBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArray(@Nonnull @WritableValue long[] bitfields, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		getUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceLong(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<long[]> bitfields, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		getUnpackedArray(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceLong(@WritableValue @Nonnull Slice<long[]> bitfields, @BoundedInt(min=0, max=64) int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSliceLong(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	// >>>
}
