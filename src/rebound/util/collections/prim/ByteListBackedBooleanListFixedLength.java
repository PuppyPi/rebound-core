package rebound.util.collections.prim;

import static java.util.Objects.*;
import java.util.RandomAccess;
import rebound.bits.BitUtilities;
import rebound.util.collections.prim.PrimitiveCollections.BooleanList;
import rebound.util.collections.prim.PrimitiveCollections.BooleanListWithByteListConversion;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;

public class ByteListBackedBooleanListFixedLength
implements BooleanList, BooleanListWithByteListConversion, RandomAccess
{
	protected final ByteList underlying;
	protected final int bitsInLastByte;
	
	public ByteListBackedBooleanListFixedLength(ByteList underlying, int bitsInLastByte)
	{
		this.underlying = requireNonNull(underlying);
		this.bitsInLastByte = bitsInLastByte;
		
		if (bitsInLastByte < 0 || bitsInLastByte > 8)
			throw new IllegalArgumentException("bitsInLastByte = "+bitsInLastByte);
		
		if ((bitsInLastByte == 0) != (underlying.isEmpty()))
			throw new IllegalArgumentException();
	}
	
	@Override
	public int size()
	{
		//(bitsInLastByte == 0) == (underlying.isEmpty())   :>
		return bitsInLastByte == 0 ? 0 : (underlying.size() - 1) * 8 + bitsInLastByte;
	}
	
	@Override
	public Boolean isWritableCollection()
	{
		return underlying.isWritableCollection();
	}
	
	
	
	
	
	
	@Override
	public boolean getBoolean(int bitIndex)
	{
		if (bitIndex < 0 || bitIndex >= this.size())
			throw new IndexOutOfBoundsException();
		
		final int byteIndex = bitIndex / 8;
		final int bitIndexInByte = bitIndex % 8;
		
		final byte b = underlying.getByte(byteIndex);
		
		//Get it :D
		{
			return BitUtilities.getBit(b, bitIndexInByte);
		}
	}
	
	
	
	@Override
	public void setBoolean(int bitIndex, boolean newValue)
	{
		if (bitIndex < 0 || bitIndex >= this.size())
			throw new IndexOutOfBoundsException();
		
		final int byteIndex = bitIndex / 8;
		final int bitIndexInByte = bitIndex % 8;
		
		final byte b = underlying.getByte(byteIndex);
		
		//Set it :D
		{
			final byte newB = (byte)BitUtilities.setBit(b, bitIndexInByte, newValue);
			
			underlying.setByte(byteIndex, newB);
		}
	}
	
	
	
	
	@Override
	public ByteList byteList()
	{
		return underlying;
	}
	
	@Override
	public ByteList toNewByteList()
	{
		return underlying.clone();
	}
	
	@Override
	public BooleanList clone()
	{
		return new ByteListBackedBooleanListFixedLength(this.toNewByteList(), this.bitsInLastByte);
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
	public void clear()
	{
		throw new UnsupportedOperationException();
	}
	
	
	
	
	
	
	@Override
	public String toString()
	{
		return _toString();
	}
}
