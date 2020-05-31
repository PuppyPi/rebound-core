package rebound.util.collections.prim;

import static java.util.Objects.*;
import static rebound.bits.BitUtilities.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.testing.WidespreadTestingUtilities.*;
import static rebound.util.collections.prim.PrimitiveCollections.*;
import java.util.RandomAccess;
import rebound.bits.BitUtilities;
import rebound.bits.Endianness;
import rebound.util.collections.prim.PrimitiveCollections.BooleanList;
import rebound.util.collections.prim.PrimitiveCollections.BooleanListWithByteListConversion;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;

public class UnpackedByteListBackedBooleanListFixedLength
implements BooleanList, BooleanListWithByteListConversion, RandomAccess
{
	protected final ByteList underlying;
	protected final Endianness underlyingByteEndianness;
	protected final int elementBitLength;
	
	
	public UnpackedByteListBackedBooleanListFixedLength(ByteList underlying, Endianness underlyingByteEndianness, int elementBitLength)
	{
		this.underlying = requireNonNull(underlying);
		this.underlyingByteEndianness = underlyingByteEndianness;
		this.elementBitLength = elementBitLength;
	}
	
	public static BooleanList decodingWrapper(ByteList underlying, Endianness underlyingByteEndianness, int elementBitLength)
	{
		//TODO Readd this and run the tests again :D
//		if (elementBitLength % 8 == 0)
//			return new ByteListBackedBooleanListFixedLength(underlying, 8);
//		else
			return new UnpackedByteListBackedBooleanListFixedLength(underlying, underlyingByteEndianness, elementBitLength);
	}
	
	
	protected int sizeOfWordsInBytes()
	{
		return ceilingDivision(elementBitLength, 8);
	}
	
	
	
	
	@Override
	public int size()
	{
		int sizeOfWordsInBytes = sizeOfWordsInBytes();
		
		int byteSize = underlying.size();
		
		int wordSize = byteSize / sizeOfWordsInBytes;
		
		return wordSize * elementBitLength;
	}
	
	
	
	
	@Override
	public void clear()
	{
		underlying.clear();
	}
	
	
	
	@Override
	public boolean getBoolean(int bitIndex)
	{
		final int elementBitLength = this.elementBitLength;
		final int sizeOfWordsInBytes = sizeOfWordsInBytes();
		
		final int wordIndex = bitIndex / elementBitLength;
		final int bitIndexInWord = bitIndex % elementBitLength;
		
		final int wordStartInBytes = wordIndex * sizeOfWordsInBytes;
		
		int byteIndexInWord = bitIndexInWord / 8;
		final int byteIndex = wordStartInBytes + (underlyingByteEndianness == Endianness.Little ? byteIndexInWord : (sizeOfWordsInBytes - byteIndexInWord - 1));
		final int bitIndexInByte = bitIndexInWord % 8;
		
		final byte b = underlying.getByte(byteIndex);
		
		//Get it :D
		{
			return BitUtilities.getBit(b, bitIndexInByte);
		}
	}
	
	
	
	@Override
	public void setBoolean(int bitIndex, boolean newValue)
	{
		final int elementBitLength = this.elementBitLength;
		final int sizeOfWordsInBytes = sizeOfWordsInBytes();
		
		final int wordIndex = bitIndex / elementBitLength;
		final int bitIndexInWord = bitIndex % elementBitLength;
		
		final int wordStartInBytes = wordIndex * sizeOfWordsInBytes;
		
		int byteIndexInWord = bitIndexInWord / 8;
		final int byteIndex = wordStartInBytes + (underlyingByteEndianness == Endianness.Little ? byteIndexInWord : (sizeOfWordsInBytes - byteIndexInWord - 1));
		final int bitIndexInByte = bitIndexInWord % 8;
		
		final byte b = underlying.getByte(byteIndex);
		
		//Set it :D
		{
			final byte newB = (byte)BitUtilities.setBit(b, bitIndexInByte, newValue);
			
			underlying.setByte(byteIndex, newB);
		}
	}
	
	
	
	
	
	@Override
	public ByteList toNewByteList()
	{
		int elementBitLength = this.elementBitLength;
		int sizeOfWordsInBytes = sizeOfWordsInBytes();
		
		int wordSize = underlying.size() / sizeOfWordsInBytes;
		
		int bitSize = wordSize * elementBitLength;
		
		int byteSize = ceilingDivision(bitSize, 8);
		
		final byte[] bytes = new byte[byteSize];
		
		int currentByteIndex = 0;
		int offsetInCurrentByte = 0;
		
		for (int wordIndex = 0; wordIndex < wordSize; wordIndex++)
		{
			final int wordStartInBytes = wordIndex * sizeOfWordsInBytes;
			
			for (int i = 0; i < sizeOfWordsInBytes; i++)
			{
				int byteIndexInWord = underlyingByteEndianness == Endianness.Little ? i : (sizeOfWordsInBytes - i - 1);
				
				byte sourceByte = underlying.getByte(wordStartInBytes + byteIndexInWord);
				
				int sourceBitCount = i == sizeOfWordsInBytes - 1 ? (elementBitLength - (i * 8)) : 8;
				
				asrt(sourceBitCount > 0);
				asrt(sourceBitCount <= 8);
				
				//Store the low sourceBitCount of sourceByte into the output buffer :>
				{
					if (offsetInCurrentByte == 0)
					{
						int bits = sourceByte & getMask32(sourceBitCount);
						bytes[currentByteIndex] = (byte)bits;
						
						if (sourceBitCount == 8)
						{
							//offsetInCurrentByte = 0;
							currentByteIndex++;
						}
						else
						{
							offsetInCurrentByte = sourceBitCount;
						}
					}
					else
					{
						if (sourceBitCount + offsetInCurrentByte > 8)
						{
							int bc0 = 8 - offsetInCurrentByte;
							int bc1 = sourceBitCount - bc0;
							int bits0 = sourceByte & getMask32(bc0);
							int bits1 = sourceByte & (getMask32(bc1) << bc0) >>> bc0;
							
							bytes[currentByteIndex] |= bits0 << offsetInCurrentByte;
							offsetInCurrentByte += bc0;
							asrt(offsetInCurrentByte == 8);
							offsetInCurrentByte = 0;
							currentByteIndex++;
							
							bytes[currentByteIndex] |= bits1;
							offsetInCurrentByte += bc1;
							asrt(offsetInCurrentByte < 8);
						}
						else
						{
							int bits = sourceByte & getMask32(sourceBitCount);
							bytes[currentByteIndex] |= bits << offsetInCurrentByte;
						}
					}
				}
			}
			
		}
		
		return byteArrayAsList(bytes);
	}
	
	
	
	
	
	
	
	
	
	@Override
	public ByteList byteList()
	{
		//Todo if we ever make the inverse of this class, we can use it here and collapse spacetime, creating the meta-verse @,@ XD
		return toNewByteList();
	}
	
	
	
	@Override
	public BooleanList clone()
	{
		ByteList b = toNewByteList();
		
		int n = this.size();
		
		return new ByteListBackedBooleanListFixedLength(b, upmod(n, 8));
	}
	
	
	
	@Override
	public Boolean isWritableCollection()
	{
		return underlying.isWritableCollection();
	}
	
	
	
	
	
	
	
	//Fixed-length :3
	@Override
	public void insertBoolean(int index, boolean value)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void setSizeBoolean(int newSize, boolean elementToAddIfGrowing)
	{
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
	{
		throw new UnsupportedOperationException();
	}
	
	
	
	
	
	
	
	
	@Override
	public String toString()
	{
		return _toString();
	}
}
