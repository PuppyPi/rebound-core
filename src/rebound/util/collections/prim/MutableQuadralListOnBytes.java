package rebound.util.collections.prim;

import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.util.collections.ArrayUtilities.*;
import static rebound.util.collections.CollectionUtilities.*;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.annotations.semantic.reachability.PossiblySnapshotPossiblyLiveValue;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.annotations.semantic.simpledata.Nonempty;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.ByteArrayList;
import rebound.util.collections.prim.PrimitiveCollections.DefaultShiftingBasedIntegerList;
import rebound.util.collections.prim.PrimitiveCollections.IntegerList;

//TODO TESTTTTTTTT ITTTTTTTTT XDD''
//Todo immutable version of this!!  And an efficient converter to it! :33
//Todo moreeeeeee super-efficient bulk operationssssssss :D   (this is where using java.util.List's shiiiines! :DD like NumPy for Java! XD :333 )

/**
 * A list of quadral digits in an efficient format :3
 * Useful for genetics/bioinformatics ;D and also quadtree traversals for 2D spatial information (*cough* Microsoft/Bing Maps! *cough*)
 */
public class MutableQuadralListOnBytes
implements DefaultShiftingBasedIntegerList
{
	/*
	 * The format is little-bit endian such that the low two bits of the first byte is the first quadral digit,
	 * the next two bits are the second digit, etc.
	 * and the low two bits of the second byte are the fifth quadral digit!
	 * 
	 * The high 2 bits in the last byte is always the number of digits in the last byte!  Which, because it's present, can never mean 4, only 0, 1, 2, or 3
	 * But that exactly corresponds to the value it would normally mean anyway! XD
	 * (We can't add one to its meaning though unfortunately—sometimes a whole byte is needed just to indicate this value :P )
	 * (But that's better than *an entire machine word always being used if it was in another field!!* 8>>  XD )
	 */
	protected @Nonempty ByteArrayList data;
	
	public MutableQuadralListOnBytes(@Nonempty @SnapshotValue byte[] encodedFormat)
	{
		requireNonEmpty(encodedFormat);
		this.data = new ByteArrayList(encodedFormat.clone());
	}
	
	public MutableQuadralListOnBytes(@Nonempty @LiveValue ByteArrayList LIVEUNDERLYING)
	{
		requireNonEmpty(LIVEUNDERLYING);
		this.data = LIVEUNDERLYING;
	}
	
	
	@ThrowAwayValue
	@Nonempty
	public byte[] toEncodedFormat()
	{
		return data.toByteArray();
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@Nonempty
	public Slice<byte[]> toEncodedFormatPossiblyLive()
	{
		return data.toByteArraySlicePossiblyLive();
	}
	
	
	
	public int size()
	{
		byte last = data.getByte(data.size() - 1);
		int numberInLast = (last & 0b11000000) >> 6;
		return (data.size() - 1) * 4 + numberInLast;
	}
	
	public int getInt(int index)
	{
		if (index < 0)
			throw new IndexOutOfBoundsException(String.valueOf(index));
		
		int byteIndex = index >>> 2;  // = index / 4;
		int indexInByte = index & 0b11;  // = index % 4
		
		int n = data.size();
		if (byteIndex > n - 1)
			throw new IndexOutOfBoundsException(String.valueOf(index));
		else
		{
			byte b;
			
			if (byteIndex == n - 1)
			{
				byte last = data.getByte(n - 1);
				int numberInLast = (last & 0b11000000) >> 6;
				
				if (indexInByte >= numberInLast)
					throw new IndexOutOfBoundsException(String.valueOf(index));
				
				b = last;
			}
			else
			{
				b = data.getByte(byteIndex);
			}
			
			return (b >>> (indexInByte << 1)) & 0b11;  //(b >>> (indexInByte * 2)) & 0b11
		}
	}
	
	@Override
	public void setInt(int index, int value)
	{
		checkValue(value);
		
		if (index < 0)
			throw new IndexOutOfBoundsException(String.valueOf(index));
		
		int byteIndex = index >>> 2;  // = index / 4;
			int indexInByte = index & 0b11;  // = index % 4
			
			int n = data.size();
			if (byteIndex > n - 1)
				throw new IndexOutOfBoundsException(String.valueOf(index));
			else
			{
				byte b;
				
				if (byteIndex == n - 1)
				{
					byte last = data.getByte(n - 1);
					int numberInLast = (last & 0b11000000) >> 6;
					
					if (indexInByte >= numberInLast)
						throw new IndexOutOfBoundsException(String.valueOf(index));
					
					b = last;
				}
				else
				{
					b = data.getByte(byteIndex);
				}
				
				b = setBits(b, indexInByte, value);
				
				data.setByte(byteIndex, b);
			}
	}
	
	@Override
	public boolean addInt(int value)
	{
		checkValue(value);
		
		int lastIndex = data.size() - 1;
		byte last = data.getByte(lastIndex);
		int numberInLast = (last & 0b11000000) >> 6;
		
		if (numberInLast == 3)
		{
			data.addByte((byte)0);
		}
		
		last = setBits(last, numberInLast, value);
		data.setByte(lastIndex, last);
		
		return true;
	}
	
	@Override
	public void setSizeInt(int newSize, int elementToAddIfGrowing)
	{
		checkValue(elementToAddIfGrowing);
		
		int currentSize = size();
		
		if (newSize < currentSize)
		{
			requireNonNegative(newSize);
			
			int q = newSize >>> 2;  //newSize / 4
			int m = newSize & 0b11;  //newSize % 4
			
			if (m == 0)
			{
				//It fits perfectly into bytes!
				//Which means we need to add a whole other byte to represent that information XD''
				data.setSizeByte(q+1, (byte)0);
				data.setByte(q, (byte)0);
			}
			else
			{
				//Preserve the m digits in the last byte!
				byte last = data.getByte(q);
				
				/*
				 * m = 1 → 0b00000011
				 * m = 2 → 0b00001111
				 * m = 3 → 0b00111111
				 */
				
				/*
				 * Shift 0b00111111 down by this much:
				 * m = 1 → 4
				 * m = 2 → 2
				 * m = 3 → 0
				 */
				
				/*
				 * Which is the same as the following map composition! :D
				 * m = 1 → 0 → 2 → 4
				 * m = 2 → 1 → 1 → 2
				 * m = 3 → 2 → 0 → 0
				 */
				int amt = (2 - (m - 1)) * 2;
				
				int mask = 0b00111111 >> amt;
				
				last &= mask;
				
				int numberInLast = m;
				
				//Set in the number of them into the last (high) bits of the last byte!
				last |= numberInLast << (3 * 2);
				
				data.setSizeByte(q+1, (byte)0);
				data.setByte(q, last);
			}
		}
		else if (newSize > currentSize)
		{
			//Todo more efficient implementation!
			int toAdd = newSize - currentSize;
			for (int i = 0; i < toAdd; i++)
				addInt(elementToAddIfGrowing);
		}
		else
		{
			assert newSize == currentSize;
			//Nothing needs to be done! XD :D
		}
	}
	
	@Override
	public void clear()
	{
		data.setSize(1);
		data.setByte(0, (byte)0);
	}
	
	
	
	
	protected static byte setBits(final byte oldValue, int indexInByte, int newValue)
	{
		byte b = oldValue;
		
		//Clear the existing bits
		b &= ~(0b11 << (indexInByte << 1));  //~(0b11 << (indexInByte * 2))
		
		//Set the new bits :3
		b |= newValue << (indexInByte << 1);
		
		return b;
	}
	
	protected static void checkValue(int value)
	{
		if (value < 0)
			throw new IllegalArgumentException("This is a quadral list, only elements 0, 1, 2, and 3 are supported!");
		if (value > 3)
			throw new IllegalArgumentException("This is a quadral list, only elements 0, 1, 2, and 3 are supported!");
	}
	
	
	
	
	
	
	
	@Override
	public void setFrom(Object source) throws ClassCastException
	{
		if (source instanceof MutableQuadralListOnBytes)
			this.data.setFrom(((MutableQuadralListOnBytes)source).data);
		else
			DefaultShiftingBasedIntegerList.super.setFrom(source);
	}
	
	@Override
	public IntegerList clone()
	{
		return new MutableQuadralListOnBytes(data.clone());
	}
	
	@Override
	public Boolean isWritableCollection()
	{
		return true;
	}
}
