package rebound.util.collections.prim;

import static java.util.Objects.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.util.NIOBufferUtilities.*;
import static rebound.util.collections.CollectionUtilities.*;
import static rebound.util.collections.prim.PrimitiveCollections.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.RandomAccess;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.util.collections.ShiftableList;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.BooleanListWithByteListConversion;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.collections.prim.PrimitiveCollections.DefaultShiftingBasedBooleanList;
import rebound.util.objectutil.Copyable;

/**
 * Super-efficient bit-packed boolean list! :D
 */
public class BitSetBackedBooleanList
implements DefaultShiftingBasedBooleanList, ShiftableList, BooleanListWithByteListConversion, Copyable, RandomAccess    //Trimmable Nope, the Java people used their favorite access level again (private), so we can't do this without copying the whole source code! X"3
{
	protected BitSet bitSet;
	protected int size;
	
	/**
	 * Initially empty.
	 */
	public BitSetBackedBooleanList()
	{
		this.bitSet = new BitSet();
		this.size = 0;
	}
	
	/**
	 * Initially empty.
	 * To make one that's full of false's, see {@link #newBooleanListZerofilled(int)} :3
	 * @param  capacity  see {@link ArrayList#ArrayList(int)}; it's the same thing :3
	 */
	public BitSetBackedBooleanList(int capacity, Void v)
	{
		this.bitSet = new BitSet(capacity);
		this.size = 0;
	}
	
	/**
	 * Initialized to all false's :>
	 */
	public static BitSetBackedBooleanList newBooleanListZerofilled(int size)
	{
		return new BitSetBackedBooleanList(new BitSet(), size);
	}
	
	public BitSetBackedBooleanList(@LiveValue BitSet bitSet, int size)
	{
		this.bitSet = requireNonNull(bitSet);
		this.size = size;
	}
	
	
	public static BitSetBackedBooleanList instCopying(@ReadonlyValue @SnapshotValue List<Boolean> other)
	{
		if (other instanceof BitSetBackedBooleanList)
			return ((BitSetBackedBooleanList)other).clone();
		else
		{
			BitSetBackedBooleanList l = new BitSetBackedBooleanList();
			l.setFrom(other);
			return l;
		}
	}
	
	
	
	@Override
	public BitSetBackedBooleanList clone()
	{
		return new BitSetBackedBooleanList((BitSet)this.bitSet.clone(), this.size);
	}
	
	@Override
	public Boolean isWritableCollection()
	{
		return true;
	}
	
	@Override
	public void setFrom(final Object source)
	{
		if (source instanceof BitSetBackedBooleanList)
		{
			final BitSetBackedBooleanList s = (BitSetBackedBooleanList)source;
			
			this.size = s.size;
			this.bitSet = (BitSet) s.bitSet.clone();
		}
		else
		{
			DefaultShiftingBasedBooleanList.super.setFrom(source);
		}
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
			for (int i = pastEnd-1; i >= start; i--)
				b.set(i + amount, b.get(i));
		}
		else if (amount < 0)
		{
			for (int i = start; i < pastEnd; i++)
				b.set(i + amount, b.get(i));
		}
	}
	
	
	
	@Override
	public int getNumberOfOnes()
	{
		return bitSet.cardinality();
	}
	
	
	
	
	
	@Override
	public void setSizeBoolean(int newSize, boolean elementToAddIfGrowing)
	{
		if (newSize > this.size)
		{
			if (elementToAddIfGrowing != false)
			{
				for (int i = this.size; i < newSize; i++)
					this.bitSet.set(i, elementToAddIfGrowing);
			}
		}
		
		this.size = newSize;
	}
	
	public void setSize(int newSize)
	{
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
	
	
	
	
	@Override
	public String toString()
	{
		return _toString();
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
