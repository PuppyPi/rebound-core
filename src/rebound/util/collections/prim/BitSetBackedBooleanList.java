package rebound.util.collections.prim;

import static java.util.Objects.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.util.NIOBufferUtilities.*;
import static rebound.util.collections.CollectionUtilities.*;
import static rebound.util.collections.prim.PrimitiveCollections.*;
import java.nio.ByteBuffer;
import java.util.BitSet;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.util.collections.ShiftableList;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.BooleanList;
import rebound.util.collections.prim.PrimitiveCollections.BooleanListWithByteListConversion;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.collections.prim.PrimitiveCollections.DefaultShiftingBasedBooleanList;

/**
 * Super-efficient bit-packed boolean list! :D
 */
public class BitSetBackedBooleanList
implements DefaultShiftingBasedBooleanList, ShiftableList, BooleanListWithByteListConversion    //Trimmable Nope, the Java people used their favorite access level again (private), so we can't do this without copying the whole source code! X"3
{
	protected BitSet bitSet = new BitSet();
	protected int size = 0;
	
	public BitSetBackedBooleanList()
	{
		super();
	}
	
	/**
	 * Initialized to all zeros :>
	 */
	public BitSetBackedBooleanList(@LiveValue BitSet bitSet, int size)
	{
		this.bitSet = requireNonNull(bitSet);
		this.size = size;
	}
	
	
	
	
	@Override
	public BooleanList clone()
	{
		return new BitSetBackedBooleanList((BitSet)this.bitSet.clone(), this.size);
	}
	
	
	@ImplementationTransparency
	public BitSet getLiveBitSetBackingUNSAFE()
	{
		return this.bitSet;
	}
	
	
	
	@Override
	public int size()
	{
		return this.size;
	}
	
	@Override
	public boolean getBoolean(int index)
	{
		rangeCheckMember(size(), index);
		return this.bitSet.get(index);
	}
	
	@Override
	public void setBoolean(int index, boolean value)
	{
		rangeCheckMember(size(), index);
		this.bitSet.set(index, value);
	}
	
	
	
	//You're kidding me; BitSet doesn't support something like these??? X"D
	//public long getBitfield(int offset, int length)
	//public void setBitfield(long bitfield, int offset, int length)
	
	
	
	
	
	@Override
	public void shiftRegionStretchingFromIndexToEndByAmountChangingSize(int start, int amount)
	{
		this.size += amount;
		shiftBitSubset(this.bitSet, start, this.size, amount);
	}
	
	
	
	
	public static void shiftBitSubset(BitSet b, int start, int pastEnd, int amount)
	{
		if (amount > 0)
		{
			for (int i = pastEnd-1; i >= start; i++)
				b.set(start + i - amount, b.get(i));
		}
		else if (amount < 0)
		{
			for (int i = start; i < pastEnd; i++)
				b.set(i - amount, b.get(i));
		}
	}
	
	
	
	
	
	
	
	@Override
	public void setSizeBoolean(int newSize, boolean elementToAddIfGrowing)
	{
		if (newSize > this.size)
		{
			for (int i = this.size; i < newSize; i++)
				setBoolean(i, elementToAddIfGrowing);
		}
		
		this.size = newSize;
	}
	
	
	
	
	
	@Override
	public ByteList byteList()
	{
		return toNewByteList();
	}
	
	@Override
	public ByteList toNewByteList()
	{
		int nBytes = ceilingDivision(size(), 8);
		byte[] a = this.bitSet.toByteArray();
		
		byte[] b;
		{
			int al = a.length;
			
			if (al < nBytes)
			{
				b = new byte[nBytes];
				System.arraycopy(a, 0, b, 0, a.length);
			}
			else if (al > nBytes)
			{
				b = new byte[nBytes];
				System.arraycopy(a, 0, b, 0, b.length);
			}
			else
			{
				b = a;
			}
		}
		
		return byteArrayAsList(b);
	}
	
	
	
	
	
	
	
	
	
	
	public static BitSetBackedBooleanList newFromBytesArray(byte[] bytes, int sizeInBits)
	{
		return new BitSetBackedBooleanList(BitSet.valueOf(bytes), sizeInBits);
	}
	
	public static BitSetBackedBooleanList newFromBytesNIOBuffer(ByteBuffer bytes, int sizeInBits)
	{
		return new BitSetBackedBooleanList(BitSet.valueOf(bytes), sizeInBits);
	}
	
	public static BitSetBackedBooleanList newFromBytesArraySlice(Slice<byte[]> bytes, int sizeInBits)
	{
		return BitSetBackedBooleanList.newFromBytesNIOBuffer(wrapInBufferBySliceByte(bytes));
	}
	
	
	
	
	public static BitSetBackedBooleanList newFromBytesArray(byte[] bytes)
	{
		return BitSetBackedBooleanList.newFromBytesArray(bytes, safe_mul_s32(bytes.length, 8));
	}
	
	public static BitSetBackedBooleanList newFromBytesNIOBuffer(ByteBuffer bytes)
	{
		return BitSetBackedBooleanList.newFromBytesNIOBuffer(bytes, safe_mul_s32(bytes.remaining(), 8));
	}
	
	public static BitSetBackedBooleanList newFromBytesArraySlice(Slice<byte[]> bytes)
	{
		return BitSetBackedBooleanList.newFromBytesArraySlice(bytes, bytes.getLength() * 8);
	}
}
