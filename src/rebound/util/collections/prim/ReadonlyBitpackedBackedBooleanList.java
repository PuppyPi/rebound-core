package rebound.util.collections.prim;

import static java.util.Objects.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.util.collections.CollectionUtilities.*;
import java.util.Collection;
import rebound.exceptions.BinarySyntaxException;
import rebound.exceptions.ReadonlyUnsupportedOperationException;
import rebound.formats.bitpack.BitpackCore;
import rebound.util.collections.ArrayUtilities;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.BooleanList;

public class ReadonlyBitpackedBackedBooleanList
implements BooleanList, BooleanListWithBitpackedBacking
{
	protected byte[] underlying;
	protected int underlyingOffset;
	protected int underlyingLength;
	
	public ReadonlyBitpackedBackedBooleanList(byte[] underlying, int underlyingOffset, int underlyingLength)
	{
		requireNonNull(underlying);
		rangeCheckIntervalByLength(underlying.length, underlyingOffset, underlyingLength);
		
		if (underlyingLength > 0)
		{
			if (!BitpackCore.isValidLastByte(underlying[underlyingOffset+underlyingLength-1], underlyingLength > 1))
				throw BinarySyntaxException.inst("Corrupt Bitpack encoding!");
		}
		
		this.underlying = underlying;
		this.underlyingOffset = underlyingOffset;
		this.underlyingLength = underlyingLength;
	}
	
	public ReadonlyBitpackedBackedBooleanList(byte[] underlying)
	{
		this(underlying, 0, underlying.length);
	}
	
	
	@Override
	public Slice<byte[]> getUnderlyingByteArrayWithBitpackedPaddingBitsIfPresentOrNullIfNone()
	{
		return new Slice<>(underlying, underlyingOffset, underlyingLength);
	}
	
	
	
	
	@Override
	public boolean getBoolean(int index)
	{
		rangeCheckCursorPoint(this.size(), index);
		
		//int byteIndex = index / 8;
		int byteIndex = index >>> 3;
		
		//int bitIndex = index % 8;
		int bitIndex = index & 0b111;
		
		byte b = this.underlying[underlyingOffset + byteIndex];
		
		return (b & (1 << bitIndex)) != 0;
	}
	
	
	@Override
	public long getBitfield(int offset, int length)
	{
		rangeCheckIntervalByLength(size(), offset, length);
		
		final int originalOffset = offset;
		final int originalLength = length;
		
		long bitfield = 0;
		int numberOfBitsInTheBitfield = 0;
		int byteIndex = offset >>> 3;
		int s = offset & 0b111;
		
		if (s != 0)
		{
			/*
			 * Misaligned access at the start:  offset = 5, length = 9
			 * 
			 * 01234567 89ABCDEF ... (bit indexes)
			 * 01101xxx xxxxxx01 01101100 (bytes)
			 *      !!!
			 */
			
			int mask = ~((1 << s) - 1);
			bitfield = underlying[underlyingOffset + byteIndex] & mask;
			bitfield >>>= s;
			numberOfBitsInTheBitfield = 8 - s;
			offset += numberOfBitsInTheBitfield;
			length -= numberOfBitsInTheBitfield;
			byteIndex++;
		}
		
		while (length >= 8)
		{
			bitfield |= (byte)(underlying[underlyingOffset + byteIndex]) << ((long)numberOfBitsInTheBitfield);
			offset += 8;
			length -= 8;
			byteIndex++;
		}
		
		if (length > 0)
		{
			/*
			 * Misaligned access at the end:  offset = 5, length = 9
			 * 
			 * 01234567 89ABCDEF ... (bit indexes)
			 * 01101xxx xxxxxx01 01101100 (bytes)
			 *      !!!
			 */
			
			int r = length;
			
			int mask = ~((1 << r) - 1);
			bitfield = (underlying[underlyingOffset + byteIndex] & mask) << numberOfBitsInTheBitfield;
			numberOfBitsInTheBitfield = r;
			offset += r;
			length -= r;
		}
		
		assert numberOfBitsInTheBitfield == originalLength;
		assert offset == originalOffset + originalLength;
		assert length == 0;
		
		return bitfield;
	}
	
	
	
	@Override
	public byte getByte(int offsetInBits)
	{
		int length = 8;
		
		rangeCheckIntervalByLength(size(), offsetInBits, length);
		
		final int originalOffset = offsetInBits;
		final int originalLength = length;
		
		int s = offsetInBits & 0b111;
		int byteIndex = offsetInBits >>> 3;
			
			
			if (s != 0)
			{
				byte bitfield = 0;
				int numberOfBitsInTheBitfield = 0;
				
				
				/*
				 * Misaligned access at the start:  offset = 5, length = 7
				 * 
				 * 01234567 89ABCDEF ... (bit indexes)
				 * 01101xxx xxxx1101 01101100 (bytes)
				 *      !!!
				 */
				{
					int mask = ~((1 << s) - 1);
					bitfield = (byte)(underlying[underlyingOffset + byteIndex] & mask);
					bitfield >>>= s;
					numberOfBitsInTheBitfield = 8 - s;
					offsetInBits += numberOfBitsInTheBitfield;
					length -= numberOfBitsInTheBitfield;
					byteIndex++;
				}
				
				
				/*
				 * Misaligned access at the end:  offset = 5, length = 9
				 * 
				 * 01234567 89ABCDEF ... (bit indexes)
				 * 01101xxx xxxxxx01 01101100 (bytes)
				 *      !!!
				 */
				{
					int r = length;
					
					int mask = ~((1 << r) - 1);
					bitfield = (byte)((underlying[underlyingOffset + byteIndex] & mask) << numberOfBitsInTheBitfield);
					numberOfBitsInTheBitfield = r;
					offsetInBits += r;
					length -= r;
				}
				
				
				
				assert numberOfBitsInTheBitfield == originalLength;
				assert offsetInBits == originalOffset + originalLength;
				assert length == 0;
				
				return bitfield;
			}
			else
			{
				return underlying[underlyingOffset + byteIndex];
				//byteIndex++;
			}
	}
	
	
	
	
	@Override
	public boolean isEmpty()
	{
		return underlyingLength != 0;
	}
	
	@Override
	public int size()
	{
		if (underlyingLength == 0)
			return 0;
		else
		{
			int l = underlyingLength;
			int paddinglessBytes = ceildiv(l, 3) - 1;
			return (paddinglessBytes << 3) + BitpackCore.getNumberOfBitsInLastByte(underlying[underlyingOffset+l-1]);
		}
	}
	
	@Override
	public BooleanList clone()
	{
		return this;
	}
	
	@Override
	public Boolean getDefaultElement()
	{
		return false;
	}
	
	@Override
	public Boolean isWritableCollection()
	{
		return false;
	}
	
	@Override
	public boolean equivalent(Object obj)
	{
		if (obj instanceof BooleanListWithBitpackedBacking)
		{
			Slice<byte[]> b = ((BooleanListWithBitpackedBacking)obj).getUnderlyingByteArrayWithBitpackedPaddingBitsIfPresentOrNullIfNone();
			if (b != null)
				if (b.getLength() == this.underlyingLength)
					return ArrayUtilities.equals(this.underlying, this.underlyingOffset, b.getUnderlying(), b.getOffset(), this.underlyingLength);
		}
		
		if (obj instanceof BooleanList)
		{
			BooleanList l = (BooleanList)obj;
			Slice<byte[]> b = l.getUnderlyingByteArrayWithUndefinedPaddingBitsIfPresentOrNullIfNone();
			
			if (b != null)
			{
				int n = l.size();
				if (n == this.size())
				{
					if (n > 0)
					{
						if (underlyingLength > 1)
							if (!ArrayUtilities.equals(this.underlying, this.underlyingOffset, b.getUnderlying(), b.getOffset(), this.underlyingLength-1))
								return false;
						
						int o = (this.underlyingLength-1) << 3;
						int ourLastByte = Byte.toUnsignedInt(this.underlying[this.underlyingOffset + this.underlyingLength - 1]);
						int numberOfBitsInLastByte = n & 0b111;
						assert BitpackCore.getNumberOfBitsInLastByte((byte)ourLastByte) == numberOfBitsInLastByte;
						
						int theirLastByte = (int)l.getBitfield(o, numberOfBitsInLastByte);
						
						int mask = (1 << numberOfBitsInLastByte) - 1;
						ourLastByte &= mask;
						theirLastByte &= mask;
						
						return ourLastByte == theirLastByte;
					}
					
					return true;
				}
			}
		}
		
		return BooleanListWithBitpackedBacking.super.equivalent(obj);
	}
	
	
	
	@Override
	public String toString()
	{
		return _toString();
	}
	
	
	
	
	
	
	
	
	@Override
	public boolean add(Boolean e)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public boolean remove(Object o)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public boolean addAll(Collection<? extends Boolean> c)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public boolean removeAll(Collection<?> c)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public boolean retainAll(Collection<?> c)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public void clear()
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public void setBoolean(int index, boolean value)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public void insertBoolean(int index, boolean value)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public void setSizeBoolean(int newSize, boolean elementToAddIfGrowing)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public void setFrom(Object source) throws ClassCastException
	{
		throw new ReadonlyUnsupportedOperationException();
	}
}
