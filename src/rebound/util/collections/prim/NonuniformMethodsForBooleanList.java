package rebound.util.collections.prim;

import static rebound.bits.BitfieldSafeCasts.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import java.math.BigInteger;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.simpledata.ActuallyUnsigned;
import rebound.annotations.semantic.simpledata.BoundedInt;
import rebound.annotations.semantic.simpledata.Emptyable;
import rebound.annotations.semantic.simpledata.Nonempty;
import rebound.exceptions.OverflowException;
import rebound.math.SmallIntegerMathUtilities;
import rebound.text.StringUtilities;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.BooleanList;
import rebound.util.collections.prim.PrimitiveCollections.DefaultToArraysBooleanCollection;

//TODO Elementwise boolean operations between BooleanLists!!  AND, OR, NOT, XOR!  \:D/

@ImplementationTransparency  //This only exists to keep the bulk functions exactly uniform and correspondent :3     This might be removed soon XD''
public interface NonuniformMethodsForBooleanList
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
	
	
	
	public default long getEntireByBitfield()
	{
		int s = size();
		
		if (s > 64)
			throw new OverflowException();
		
		return getBitfield(0, s);
	}
	
	
	
	
	
	
	
	
	
	public default @Nonnull BigInteger toBigIntegerLE()
	{
		BigInteger bigInteger = BigInteger.ZERO;
		
		int sizeInBits = size();
		
		final int wordLength = SmallIntegerMathUtilities.ceilingDivision(sizeInBits, 64);
		for (int wordIndex = 0; wordIndex < wordLength; wordIndex++)
		{
			final int bitsUsedInWord = wordIndex < wordLength - 1 ? 64 : sizeInBits - (wordIndex * 64);
			assert bitsUsedInWord > 0 && bitsUsedInWord <= 64;
			
			final long maskedWord = getBitfield(wordIndex * 64, bitsUsedInWord);
			
			BigInteger operand = BigInteger.valueOf(maskedWord);
			operand = operand.shiftLeft(wordIndex * 64);
			bigInteger = bigInteger.or(operand);
		}
		
		return bigInteger;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	primxp
	_$$primxpconf:intsonly$$_
	
	public default void putArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull _$$prim$$_[] bitfields, @Nonnegative int sourceElementOffset, @Nonnegative int elementCount, @ActuallyUnsigned int totalLengthOfDataToInsertInBits)
	{
		int primlen = _$$primlen$$_;
		
		int lengthInBitsInt = safeCastU64toS32(totalLengthOfDataToInsertInBits);
		if (elementCount != -1 && ceilingDivision(lengthInBitsInt, primlen) > elementCount)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = lengthInBitsInt/primlen;
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfield(destBitOffset+i*primlen, primlen, bitfields[sourceElementOffset+i]);
		
		int fullAmount = numberOfFullElementsToUse * primlen;
		int remainder = lengthInBitsInt - fullAmount;
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfield(destBitOffset+fullAmount, remainder, bitfields[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void putArray(@ReadonlyValue @Nonnull _$$prim$$_[] bitfields)
	{
		putArray(0, bitfields, 0, bitfields.length, bitfields.length*_$$primlen$$_);
	}
	
	public default void putArrayFromSlice_$$Prim$$_(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<_$$prim$$_[]> bitfields)
	{
		putArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*_$$primlen$$_);
	}
	
	public default void putArrayFromSlice_$$Prim$$_(@ReadonlyValue @Nonnull Slice<_$$prim$$_[]> bitfields)
	{
		putArrayFromSlice_$$Prim$$_(0, bitfields);
	}
	
	
	
	
	public default void putUnpackedArray(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull _$$prim$$_[] bitfields, @Nonnegative int sourceElementOffset, @Nonnegative int elementCount, @BoundedInt(min=0, max=_$$primbitlen$$_) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > _$$primlen$$_)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < elementCount; i++)
			setBitfield(destBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits, bitfields[sourceElementOffset+i]);
	}
	
	public default void putUnpackedArray(@ReadonlyValue @Nonnull _$$prim$$_[] bitfields, @BoundedInt(min=0, max=_$$primbitlen$$_) int lengthOfEachElementInBits)
	{
		putUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSlice_$$Prim$$_(@Nonnegative int destBitOffset, @ReadonlyValue @Nonnull Slice<_$$prim$$_[]> bitfields, @BoundedInt(min=0, max=_$$primbitlen$$_) int lengthOfEachElementInBits)
	{
		putUnpackedArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSlice_$$Prim$$_(@ReadonlyValue @Nonnull Slice<_$$prim$$_[]> bitfields, @BoundedInt(min=0, max=_$$primbitlen$$_) int lengthOfEachElementInBits)
	{
		putUnpackedArrayFromSlice_$$Prim$$_(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArray(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull _$$prim$$_[] bitfields, @Nonnegative int sourceElementOffset, @Nonnegative int elementCount, @BoundedInt(min=0, max=_$$primbitlen$$_) int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > _$$primlen$$_)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < elementCount; i++)
			bitfields[sourceElementOffset+i] = (_$$prim$$_)getBitfield(sourceBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArray(@Nonnull @WritableValue _$$prim$$_[] bitfields, @BoundedInt(min=0, max=_$$primbitlen$$_) int lengthOfEachElementInBits)
	{
		getUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSlice_$$Prim$$_(@Nonnegative int sourceBitOffset, @WritableValue @Nonnull Slice<_$$prim$$_[]> bitfields, @BoundedInt(min=0, max=_$$primbitlen$$_) int lengthOfEachElementInBits)
	{
		getUnpackedArray(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSlice_$$Prim$$_(@WritableValue @Nonnull Slice<_$$prim$$_[]> bitfields, @BoundedInt(min=0, max=_$$primbitlen$$_) int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSlice_$$Prim$$_(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	 */
	
	
	public default void putArray(int destBitOffset, @ReadonlyValue byte[] bitfields, int sourceElementOffset, int elementCount, @ActuallyUnsigned int totalLengthOfDataToInsertInBits)
	{
		int primlen = 8;
		
		int lengthInBitsInt = safeCastU64toS32(totalLengthOfDataToInsertInBits);
		if (elementCount != -1 && ceilingDivision(lengthInBitsInt, primlen) > elementCount)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = lengthInBitsInt/primlen;
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfield(destBitOffset+i*primlen, primlen, bitfields[sourceElementOffset+i]);
		
		int fullAmount = numberOfFullElementsToUse * primlen;
		int remainder = lengthInBitsInt - fullAmount;
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfield(destBitOffset+fullAmount, remainder, bitfields[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void putArray(@ReadonlyValue byte[] bitfields)
	{
		putArray(0, bitfields, 0, bitfields.length, bitfields.length*8);
	}
	
	public default void putArrayFromSliceByte(int destBitOffset, @ReadonlyValue Slice<byte[]> bitfields)
	{
		putArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*8);
	}
	
	public default void putArrayFromSliceByte(@ReadonlyValue Slice<byte[]> bitfields)
	{
		putArrayFromSliceByte(0, bitfields);
	}
	
	
	
	
	public default void putUnpackedArray(int destBitOffset, @ReadonlyValue byte[] bitfields, int sourceElementOffset, int elementCount, int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 8)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < elementCount; i++)
			setBitfield(destBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits, bitfields[sourceElementOffset+i]);
	}
	
	public default void putUnpackedArray(@ReadonlyValue byte[] bitfields, int lengthOfEachElementInBits)
	{
		putUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceByte(int destBitOffset, @ReadonlyValue Slice<byte[]> bitfields, int lengthOfEachElementInBits)
	{
		putUnpackedArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceByte(@ReadonlyValue Slice<byte[]> bitfields, int lengthOfEachElementInBits)
	{
		putUnpackedArrayFromSliceByte(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArray(int sourceBitOffset, @WritableValue byte[] bitfields, int sourceElementOffset, int elementCount, int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 8)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < elementCount; i++)
			bitfields[sourceElementOffset+i] = (byte)getBitfield(sourceBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArray(@WritableValue byte[] bitfields, int lengthOfEachElementInBits)
	{
		getUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceByte(int sourceBitOffset, @WritableValue Slice<byte[]> bitfields, int lengthOfEachElementInBits)
	{
		getUnpackedArray(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceByte(@WritableValue Slice<byte[]> bitfields, int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSliceByte(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	
	
	public default void putArray(int destBitOffset, @ReadonlyValue char[] bitfields, int sourceElementOffset, int elementCount, @ActuallyUnsigned int totalLengthOfDataToInsertInBits)
	{
		int primlen = 16;
		
		int lengthInBitsInt = safeCastU64toS32(totalLengthOfDataToInsertInBits);
		if (elementCount != -1 && ceilingDivision(lengthInBitsInt, primlen) > elementCount)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = lengthInBitsInt/primlen;
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfield(destBitOffset+i*primlen, primlen, bitfields[sourceElementOffset+i]);
		
		int fullAmount = numberOfFullElementsToUse * primlen;
		int remainder = lengthInBitsInt - fullAmount;
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfield(destBitOffset+fullAmount, remainder, bitfields[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void putArray(@ReadonlyValue char[] bitfields)
	{
		putArray(0, bitfields, 0, bitfields.length, bitfields.length*16);
	}
	
	public default void putArrayFromSliceChar(int destBitOffset, @ReadonlyValue Slice<char[]> bitfields)
	{
		putArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*16);
	}
	
	public default void putArrayFromSliceChar(@ReadonlyValue Slice<char[]> bitfields)
	{
		putArrayFromSliceChar(0, bitfields);
	}
	
	
	
	
	public default void putUnpackedArray(int destBitOffset, @ReadonlyValue char[] bitfields, int sourceElementOffset, int elementCount, int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 16)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < elementCount; i++)
			setBitfield(destBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits, bitfields[sourceElementOffset+i]);
	}
	
	public default void putUnpackedArray(@ReadonlyValue char[] bitfields, int lengthOfEachElementInBits)
	{
		putUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceChar(int destBitOffset, @ReadonlyValue Slice<char[]> bitfields, int lengthOfEachElementInBits)
	{
		putUnpackedArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceChar(@ReadonlyValue Slice<char[]> bitfields, int lengthOfEachElementInBits)
	{
		putUnpackedArrayFromSliceChar(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArray(int sourceBitOffset, @WritableValue char[] bitfields, int sourceElementOffset, int elementCount, int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 16)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < elementCount; i++)
			bitfields[sourceElementOffset+i] = (char)getBitfield(sourceBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArray(@WritableValue char[] bitfields, int lengthOfEachElementInBits)
	{
		getUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceChar(int sourceBitOffset, @WritableValue Slice<char[]> bitfields, int lengthOfEachElementInBits)
	{
		getUnpackedArray(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceChar(@WritableValue Slice<char[]> bitfields, int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSliceChar(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	
	
	public default void putArray(int destBitOffset, @ReadonlyValue short[] bitfields, int sourceElementOffset, int elementCount, @ActuallyUnsigned int totalLengthOfDataToInsertInBits)
	{
		int primlen = 16;
		
		int lengthInBitsInt = safeCastU64toS32(totalLengthOfDataToInsertInBits);
		if (elementCount != -1 && ceilingDivision(lengthInBitsInt, primlen) > elementCount)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = lengthInBitsInt/primlen;
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfield(destBitOffset+i*primlen, primlen, bitfields[sourceElementOffset+i]);
		
		int fullAmount = numberOfFullElementsToUse * primlen;
		int remainder = lengthInBitsInt - fullAmount;
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfield(destBitOffset+fullAmount, remainder, bitfields[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void putArray(@ReadonlyValue short[] bitfields)
	{
		putArray(0, bitfields, 0, bitfields.length, bitfields.length*16);
	}
	
	public default void putArrayFromSliceShort(int destBitOffset, @ReadonlyValue Slice<short[]> bitfields)
	{
		putArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*16);
	}
	
	public default void putArrayFromSliceShort(@ReadonlyValue Slice<short[]> bitfields)
	{
		putArrayFromSliceShort(0, bitfields);
	}
	
	
	
	
	public default void putUnpackedArray(int destBitOffset, @ReadonlyValue short[] bitfields, int sourceElementOffset, int elementCount, int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 16)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < elementCount; i++)
			setBitfield(destBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits, bitfields[sourceElementOffset+i]);
	}
	
	public default void putUnpackedArray(@ReadonlyValue short[] bitfields, int lengthOfEachElementInBits)
	{
		putUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceShort(int destBitOffset, @ReadonlyValue Slice<short[]> bitfields, int lengthOfEachElementInBits)
	{
		putUnpackedArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceShort(@ReadonlyValue Slice<short[]> bitfields, int lengthOfEachElementInBits)
	{
		putUnpackedArrayFromSliceShort(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArray(int sourceBitOffset, @WritableValue short[] bitfields, int sourceElementOffset, int elementCount, int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 16)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < elementCount; i++)
			bitfields[sourceElementOffset+i] = (short)getBitfield(sourceBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArray(@WritableValue short[] bitfields, int lengthOfEachElementInBits)
	{
		getUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceShort(int sourceBitOffset, @WritableValue Slice<short[]> bitfields, int lengthOfEachElementInBits)
	{
		getUnpackedArray(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceShort(@WritableValue Slice<short[]> bitfields, int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSliceShort(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	
	
	public default void putArray(int destBitOffset, @ReadonlyValue int[] bitfields, int sourceElementOffset, int elementCount, @ActuallyUnsigned int totalLengthOfDataToInsertInBits)
	{
		int primlen = 32;
		
		int lengthInBitsInt = safeCastU64toS32(totalLengthOfDataToInsertInBits);
		if (elementCount != -1 && ceilingDivision(lengthInBitsInt, primlen) > elementCount)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = lengthInBitsInt/primlen;
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfield(destBitOffset+i*primlen, primlen, bitfields[sourceElementOffset+i]);
		
		int fullAmount = numberOfFullElementsToUse * primlen;
		int remainder = lengthInBitsInt - fullAmount;
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfield(destBitOffset+fullAmount, remainder, bitfields[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void putArray(@ReadonlyValue int[] bitfields)
	{
		putArray(0, bitfields, 0, bitfields.length, bitfields.length*32);
	}
	
	public default void putArrayFromSliceInt(int destBitOffset, @ReadonlyValue Slice<int[]> bitfields)
	{
		putArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*32);
	}
	
	public default void putArrayFromSliceInt(@ReadonlyValue Slice<int[]> bitfields)
	{
		putArrayFromSliceInt(0, bitfields);
	}
	
	
	
	
	public default void putUnpackedArray(int destBitOffset, @ReadonlyValue int[] bitfields, int sourceElementOffset, int elementCount, int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 32)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < elementCount; i++)
			setBitfield(destBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits, bitfields[sourceElementOffset+i]);
	}
	
	public default void putUnpackedArray(@ReadonlyValue int[] bitfields, int lengthOfEachElementInBits)
	{
		putUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceInt(int destBitOffset, @ReadonlyValue Slice<int[]> bitfields, int lengthOfEachElementInBits)
	{
		putUnpackedArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceInt(@ReadonlyValue Slice<int[]> bitfields, int lengthOfEachElementInBits)
	{
		putUnpackedArrayFromSliceInt(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArray(int sourceBitOffset, @WritableValue int[] bitfields, int sourceElementOffset, int elementCount, int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 32)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < elementCount; i++)
			bitfields[sourceElementOffset+i] = (int)getBitfield(sourceBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArray(@WritableValue int[] bitfields, int lengthOfEachElementInBits)
	{
		getUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceInt(int sourceBitOffset, @WritableValue Slice<int[]> bitfields, int lengthOfEachElementInBits)
	{
		getUnpackedArray(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceInt(@WritableValue Slice<int[]> bitfields, int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSliceInt(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	
	
	public default void putArray(int destBitOffset, @ReadonlyValue long[] bitfields, int sourceElementOffset, int elementCount, @ActuallyUnsigned int totalLengthOfDataToInsertInBits)
	{
		int primlen = 64;
		
		int lengthInBitsInt = safeCastU64toS32(totalLengthOfDataToInsertInBits);
		if (elementCount != -1 && ceilingDivision(lengthInBitsInt, primlen) > elementCount)
			throw new IllegalArgumentException("Array bounds check failed; it would have gone past! :[!");
		
		int numberOfFullElementsToUse = lengthInBitsInt/primlen;
		for (int i = 0; i < numberOfFullElementsToUse; i++)
			setBitfield(destBitOffset+i*primlen, primlen, bitfields[sourceElementOffset+i]);
		
		int fullAmount = numberOfFullElementsToUse * primlen;
		int remainder = lengthInBitsInt - fullAmount;
		
		assert remainder >= 0;
		assert remainder < primlen;
		if (remainder != 0)
			setBitfield(destBitOffset+fullAmount, remainder, bitfields[sourceElementOffset+numberOfFullElementsToUse]);
	}
	
	public default void putArray(@ReadonlyValue long[] bitfields)
	{
		putArray(0, bitfields, 0, bitfields.length, bitfields.length*64);
	}
	
	public default void putArrayFromSliceLong(int destBitOffset, @ReadonlyValue Slice<long[]> bitfields)
	{
		putArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), bitfields.getLength()*64);
	}
	
	public default void putArrayFromSliceLong(@ReadonlyValue Slice<long[]> bitfields)
	{
		putArrayFromSliceLong(0, bitfields);
	}
	
	
	
	
	public default void putUnpackedArray(int destBitOffset, @ReadonlyValue long[] bitfields, int sourceElementOffset, int elementCount, int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 64)
			throw new IllegalArgumentException();
		
		
		for (int i = 0; i < elementCount; i++)
			setBitfield(destBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits, bitfields[sourceElementOffset+i]);
	}
	
	public default void putUnpackedArray(@ReadonlyValue long[] bitfields, int lengthOfEachElementInBits)
	{
		putUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceLong(int destBitOffset, @ReadonlyValue Slice<long[]> bitfields, int lengthOfEachElementInBits)
	{
		putUnpackedArray(destBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void putUnpackedArrayFromSliceLong(@ReadonlyValue Slice<long[]> bitfields, int lengthOfEachElementInBits)
	{
		putUnpackedArrayFromSliceLong(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	public default void getUnpackedArray(int sourceBitOffset, @WritableValue long[] bitfields, int sourceElementOffset, int elementCount, int lengthOfEachElementInBits)
	{
		if (lengthOfEachElementInBits < 0)
			throw new IllegalArgumentException();
		if (lengthOfEachElementInBits > 64)
			throw new IllegalArgumentException();
		
		for (int i = 0; i < elementCount; i++)
			bitfields[sourceElementOffset+i] = getBitfield(sourceBitOffset+i*lengthOfEachElementInBits, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArray(@WritableValue long[] bitfields, int lengthOfEachElementInBits)
	{
		getUnpackedArray(0, bitfields, 0, bitfields.length, lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceLong(int sourceBitOffset, @WritableValue Slice<long[]> bitfields, int lengthOfEachElementInBits)
	{
		getUnpackedArray(sourceBitOffset, bitfields.getUnderlying(), bitfields.getOffset(), bitfields.getLength(), lengthOfEachElementInBits);
	}
	
	public default void getUnpackedArrayToSliceLong(@WritableValue Slice<long[]> bitfields, int lengthOfEachElementInBits)
	{
		getUnpackedArrayToSliceLong(0, bitfields, lengthOfEachElementInBits);
	}
	
	
	
	
	
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public default String toBinaryStringLE(@Nonnegative long regionLength, @Emptyable @Nonnull String delimiter)
	{
		return toBinaryStringLE(regionLength, delimiter, "0", "1");
	}
	
	public default String toBinaryStringLE(@Nonnegative long regionLength, @Emptyable @Nonnull String delimiter, @Nonempty @Nonnull String zeroRepr, @Nonempty @Nonnull String oneRepr)
	{
		return toBinaryStringLE(new long[]{regionLength}, new String[]{delimiter}, zeroRepr, oneRepr);
	}
	
	public default String toBinaryStringLENoDelimiters(@Nonnull String zeroRepr, @Emptyable @Nonnull String oneRepr)
	{
		return toBinaryStringLE(null, null, zeroRepr, oneRepr);
	}
	
	public default String toBinaryStringLENoDelimiters()
	{
		return toBinaryStringLENoDelimiters("0", "1");
	}
	
	public default String toBinaryStringLE(@Nullable long[] regionLengths, @Nullable String[] delimiters, @Nonempty @Nonnull String zeroRepr, @Nonempty @Nonnull String oneRepr)
	{
		StringBuilder rv = new StringBuilder();
		
		for (int i = 0; i < size(); i++)
		{
			boolean bit = getBoolean(i);
			rv.append(bit ? oneRepr : zeroRepr);
			
			if (regionLengths != null && regionLengths.length != 0)
			{
				int regionIndexOfLongestMatchingRegion = 0;
				{
					regionIndexOfLongestMatchingRegion = -1;
					for (int e = 0; e < regionLengths.length; e++)
					{
						if ((i+1) % regionLengths[e] == 0)
						{
							if (regionIndexOfLongestMatchingRegion == -1 || regionLengths[e] > regionLengths[regionIndexOfLongestMatchingRegion])
								regionIndexOfLongestMatchingRegion = e;
						}
					}
				}
				
				if (regionIndexOfLongestMatchingRegion != -1 && i < size()-1)
				{
					rv.append(delimiters[regionIndexOfLongestMatchingRegion]);
				}
			}
		}
		
		return rv.toString();
	}
	
	
	
	
	
	public default void unsignedIntegerToStringBE(@BoundedInt(min=2, max=36) int radix, @WritableValue @Nonnull StringBuilder buff)
	{
		if (size() <= 63)
		{
			buff.append(Long.toString(getEntireByBitfield(), radix));
		}
		else if (size() == 64)
		{
			buff.append(StringUtilities.toStringU64(getEntireByBitfield(), radix));
		}
		else
		{
			//TODO! BETTER IMPL!
			BigInteger bi = toBigIntegerLE();
			buff.append(bi.toString(radix));
		}
	}
	
	public default String unsignedIntegerToStringBE(@BoundedInt(min=2, max=36) int radix)
	{
		if (size() <= 63)
		{
			return Long.toString(getEntireByBitfield(), radix);
		}
		else if (size() == 64)
		{
			return StringUtilities.toStringU64(getEntireByBitfield(), radix);
		}
		else
		{
			StringBuilder buff = new StringBuilder();
			unsignedIntegerToStringBE(radix, buff);
			return buff.toString();
		}
	}
	
	
	
	
	public default String _toString()
	{
		return PrimitiveCollections.defaultBooleanListToString((BooleanList) this);
	}
}
