package rebound.util.collections.prim;

import static rebound.bits.BitfieldSafeCasts.*;
import static rebound.util.collections.CollectionUtilities.*;
import static rebound.util.collections.prim.PrimitiveCollections.*;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.List;
import rebound.exceptions.OverflowException;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.ByteArrayList;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.collections.prim.PrimitiveCollections.DefaultShiftingBasedByteList;

//OH YEAH 8D
// "Memory-Mapped" IO (-like API) for Java XD
//  (by which I mean "presenting files with the same interface you use for in-memory stuff", where the "same interface" in C is..memory XD, and in Java is the Java Collections Framework! :D )
//  (but not actually memory mapped IO, because that would involve native memory, ofc XD )
//  (you can already do that though, with FixedLengthBufferWrapperByteList and ByteBuffer memory-mapped files! :D )

//TODO TESTTTTTTTT THISSSSSSSS! XD

/**
 * {@link UncheckedIOException} is heavily used here XD
 *  :3
 * 
 * + Note that this whole thing will crash and burn ({@link OverflowException}) if the file is larger than {@link Integer#MAX_VALUE} bytes! XD''
 * 		(what would {@link #size()} return!? XD )
 * 
 * + BE CAREFUL WITH GIANT FILES; You don't want to end up iterating over the *entire thing!* XD
 * 
 * + Don't change the file cursor/position in the underlying {@link RandomAccessFile} (other than through this decorator) or we won't know about it!
 */
public class RandomAccessFileBackedByteList
implements DefaultShiftingBasedByteList
{
	protected final RandomAccessFile underlying;
	protected int currentPosition;
	protected Boolean writable;
	
	public RandomAccessFileBackedByteList(RandomAccessFile underlying, Boolean writable)
	{
		try
		{
			this.underlying = underlying;
			this.currentPosition = safeCastU64toS32(underlying.getFilePointer());
			this.writable = writable;
			size();  //fail early if it's too big XD''
		}
		catch (IOException exc)
		{
			throw new UncheckedIOException(exc);
		}
	}
	
	
	
	
	@Override
	public int size()
	{
		try
		{
			return safeCastU64toS32(underlying.length());
		}
		catch (IOException exc)
		{
			throw new UncheckedIOException(exc);
		}
	}
	
	protected void ensurePositionS32(int index)
	{
		try
		{
			if (index < 0)
				throw new IndexOutOfBoundsException();
			if (index > size())  //we can set the position to the end though (index == size())  :>
				throw new IndexOutOfBoundsException();
			
			if (currentPosition != index)
			{
				underlying.seek(index);
			}
		}
		catch (IOException exc)
		{
			throw new UncheckedIOException(exc);
		}
	}
	
	
	@Override
	public byte getByte(int index)
	{
		try
		{
			ensurePositionS32(index);
			
			int c = underlying.read();
			if (c == -1)
				throw new IndexOutOfBoundsException();
			else
			{
				currentPosition++;
				return (byte)c;
			}
		}
		catch (IOException exc)
		{
			throw new UncheckedIOException(exc);
		}
	}
	
	@Override
	public void setByte(int index, byte value)
	{
		try
		{
			ensurePositionS32(index);
			
			underlying.write(value);
			currentPosition++;
		}
		catch (IOException exc)
		{
			throw new UncheckedIOException(exc);
		}
	}
	
	@Override
	public boolean addByte(byte value)
	{
		try
		{
			ensurePositionS32(size());
			
			underlying.write(value);
			currentPosition++;
			return true;
		}
		catch (IOException exc)
		{
			throw new UncheckedIOException(exc);
		}
	}
	
	
	@Override
	public void getAllBytes(int start, byte[] array, int offset, int length)
	{
		try
		{
			ensurePositionS32(start);
			underlying.readFully(array, offset, length);
		}
		catch (IOException exc)
		{
			throw new UncheckedIOException(exc);
		}
	}
	
	protected void writeAllBytes(int position, byte[] array, int offset, int length)
	{
		try
		{
			ensurePositionS32(position);
			underlying.write(array, offset, length);
		}
		catch (IOException exc)
		{
			throw new UncheckedIOException(exc);
		}
	}
	
	
	
	
	
	protected void writeAll(int position, Collection<? extends Byte> c)
	{
		Slice<byte[]> s = wrappedByteCollection(c).toByteArraySlicePossiblyLive();
		writeAllBytes(position, s.getUnderlying(), s.getOffset(), s.getLength());
	}
	
	@Override
	public boolean addAll(Collection<? extends Byte> c)
	{
		writeAll(size(), c);
		return true;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends Byte> c)
	{
		if (index == size())
			this.addAll(c);
		else
			DefaultShiftingBasedByteList.super.addAll(index, c);
		return true;
	}
	
	@Override
	public void setAll(int destIndex, List source, int sourceIndex, int amount) throws IndexOutOfBoundsException
	{
		rangeCheckIntervalByLength(source.size(), sourceIndex, amount);
		rangeCheckIntervalByLength(this.size(), destIndex, amount);
		writeAll(destIndex, subListBySize(source, sourceIndex, amount));
	}
	
	@Override
	public void setSizeByte(int newSize, byte elementToAddIfGrowing)
	{
		if (newSize < 0)
			throw new IllegalArgumentException();
		
		int oldSize = this.size();
		int amount = newSize - oldSize;
		
		try
		{
			underlying.setLength(newSize);
		}
		catch (IOException exc)
		{
			throw new UncheckedIOException(exc);
		}
		
		//"In this case, the contents of the extended portion of the file are not defined."
		this.fillBySettingByte(oldSize, amount, elementToAddIfGrowing);
	}
	
	
	
	
	
	
	
	
	
	
	@Override
	public ByteList clone()
	{
		return new ByteArrayList(toByteArray());
	}
	
	
	@Override
	public Boolean isWritableCollection()
	{
		return writable;
	}
}
