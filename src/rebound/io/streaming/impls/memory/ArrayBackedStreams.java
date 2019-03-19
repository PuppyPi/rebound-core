package rebound.io.streaming.impls.memory;

import static java.util.Objects.*;
import static rebound.bits.Unsigned.*;
import java.io.EOFException;
import java.io.Flushable;
import java.io.IOException;
import java.nio.channels.UnsupportedAddressTypeException;
import javax.annotation.Nonnull;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.annotations.semantic.temporal.PossiblySnapshotPossiblyLiveValue;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.IllegalLengthException;
import rebound.io.streaming.api.StreamAPIs.BooleanBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.ByteBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.CharBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.DoubleBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.FloatBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.IntBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.LongBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.ShortBlockReadStream;
import rebound.io.streaming.api.advanced.LengthAwareStream;
import rebound.io.streaming.api.advanced.LengthMutableWriteStream;
import rebound.io.streaming.api.advanced.SeekableStream;
import rebound.io.streaming.util.implhelp.AbstractStream;
import rebound.io.streaming.util.implhelp.StreamImplUtilities.IndolentBooleanBlockWriteStream;
import rebound.io.streaming.util.implhelp.StreamImplUtilities.IndolentByteBlockWriteStream;
import rebound.io.streaming.util.implhelp.StreamImplUtilities.IndolentCharBlockWriteStream;
import rebound.io.streaming.util.implhelp.StreamImplUtilities.IndolentDoubleBlockWriteStream;
import rebound.io.streaming.util.implhelp.StreamImplUtilities.IndolentFloatBlockWriteStream;
import rebound.io.streaming.util.implhelp.StreamImplUtilities.IndolentIntBlockWriteStream;
import rebound.io.streaming.util.implhelp.StreamImplUtilities.IndolentLongBlockWriteStream;
import rebound.io.streaming.util.implhelp.StreamImplUtilities.IndolentShortBlockWriteStream;
import rebound.util.Primitives;
import rebound.util.collections.CollectionUtilities;
import rebound.util.collections.PolymorphicCollectionUtilities;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections;
import rebound.util.collections.prim.PrimitiveCollections.BooleanArrayList;
import rebound.util.collections.prim.PrimitiveCollections.BooleanList;
import rebound.util.collections.prim.PrimitiveCollections.ByteArrayList;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.collections.prim.PrimitiveCollections.CharacterArrayList;
import rebound.util.collections.prim.PrimitiveCollections.CharacterList;
import rebound.util.collections.prim.PrimitiveCollections.DoubleArrayList;
import rebound.util.collections.prim.PrimitiveCollections.DoubleList;
import rebound.util.collections.prim.PrimitiveCollections.FixedLengthArrayWrapperBooleanList;
import rebound.util.collections.prim.PrimitiveCollections.FixedLengthArrayWrapperByteList;
import rebound.util.collections.prim.PrimitiveCollections.FixedLengthArrayWrapperCharacterList;
import rebound.util.collections.prim.PrimitiveCollections.FixedLengthArrayWrapperDoubleList;
import rebound.util.collections.prim.PrimitiveCollections.FixedLengthArrayWrapperFloatList;
import rebound.util.collections.prim.PrimitiveCollections.FixedLengthArrayWrapperIntegerList;
import rebound.util.collections.prim.PrimitiveCollections.FixedLengthArrayWrapperLongList;
import rebound.util.collections.prim.PrimitiveCollections.FixedLengthArrayWrapperShortList;
import rebound.util.collections.prim.PrimitiveCollections.FloatArrayList;
import rebound.util.collections.prim.PrimitiveCollections.FloatList;
import rebound.util.collections.prim.PrimitiveCollections.IntegerArrayList;
import rebound.util.collections.prim.PrimitiveCollections.IntegerList;
import rebound.util.collections.prim.PrimitiveCollections.LongArrayList;
import rebound.util.collections.prim.PrimitiveCollections.LongList;
import rebound.util.collections.prim.PrimitiveCollections.ShortArrayList;
import rebound.util.collections.prim.PrimitiveCollections.ShortList;

public class ArrayBackedStreams
{
	/* <<<
	primxp
	
	public static _$$Primitive$$_ListBackedWriteStream newVariableLength_$$Prim$$_ArrayBackedWriteStream()
	{
		return new _$$Primitive$$_ListBackedWriteStream(new _$$Primitive$$_ArrayList());
	}
	
	
	
	
	public static _$$Primitive$$_ListBackedReadStream wrap_$$Prim$$_ArrayAsReadStream(@LiveValue @ReadonlyValue _$$prim$$_[] array)
	{
		return wrap_$$Prim$$_ArrayAsReadStream(array, 0, array.length);
	}
	
	public static _$$Primitive$$_ListBackedReadStream wrap_$$Prim$$_ArrayAsReadStream(@LiveValue @ReadonlyValue Slice<_$$prim$$_[]> arraySlice)
	{
		return wrap_$$Prim$$_ArrayAsReadStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static _$$Primitive$$_ListBackedReadStream wrap_$$Prim$$_ArrayAsReadStream(@LiveValue @ReadonlyValue _$$prim$$_[] array, int offset, int length)
	{
		return new _$$Primitive$$_ListBackedReadStream(PrimitiveCollections._$$prim$$_ArrayAsList(array, offset, length));
	}
	
	
	
	public static _$$Primitive$$_ListBackedWriteStream wrap_$$Prim$$_ArrayAsFixedLengthWriteStream(@LiveValue @WritableValue _$$prim$$_[] array)
	{
		return wrap_$$Prim$$_ArrayAsFixedLengthWriteStream(array, 0, array.length);
	}
	
	public static _$$Primitive$$_ListBackedWriteStream wrap_$$Prim$$_ArrayAsFixedLengthWriteStream(@LiveValue @WritableValue Slice<_$$prim$$_[]> arraySlice)
	{
		return wrap_$$Prim$$_ArrayAsFixedLengthWriteStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static _$$Primitive$$_ListBackedWriteStream wrap_$$Prim$$_ArrayAsFixedLengthWriteStream(@LiveValue @WritableValue _$$prim$$_[] array, int offset, int length)
	{
		return new _$$Primitive$$_ListBackedWriteStream(new FixedLengthArrayWrapper_$$Primitive$$_List(array, offset, length));
	}
	
	
	
	public static _$$Primitive$$_ListBackedWriteStream wrap_$$Prim$$_ArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue _$$prim$$_[] array)
	{
		return wrap_$$Prim$$_ArrayAsVariableLengthWriteStream(array, 0, array.length);
	}
	
	public static _$$Primitive$$_ListBackedWriteStream wrap_$$Prim$$_ArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue Slice<_$$prim$$_[]> arraySlice)
	{
		return wrap_$$Prim$$_ArrayAsVariableLengthWriteStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static _$$Primitive$$_ListBackedWriteStream wrap_$$Prim$$_ArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue _$$prim$$_[] array, int offset, int length)
	{
		return new _$$Primitive$$_ListBackedWriteStream(PrimitiveCollections._$$prim$$_ArrayAsMutableList(array, offset, length));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class _$$Primitive$$_ListBackedReadStream
	extends AbstractStream
	implements _$$Prim$$_BlockReadStream, LengthAwareStream, SeekableStream
	{
		protected _$$Primitive$$_List underlying;
		protected int cursor = 0;
		
		public _$$Primitive$$_ListBackedReadStream(@LiveValue @Nonnull _$$Primitive$$_List underlying)
		{
			this.underlying = requireNonNull(underlying);
		}
		
		@ImplementationTransparency
		public _$$Primitive$$_List getUnderlying()
		{
			return underlying;
		}
		
		
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return cursor >= underlying.size();
		}
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			super.requireOpen();
			
			int amt = (int)Math.min(amount, underlying.size() - cursor);
			cursor += amt;
			return amt;
		}
		
		
		@Override
		public _$$prim$$_ read() throws EOFException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (isEOF())
				throw new EOFException();
			
			_$$prim$$_ v = underlying.get_$$Prim$$_(cursor);
			cursor++;
			return v;
		}
		
		@Override
		public int read(_$$prim$$_[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			int realLength = Math.min(length, underlying.size() - cursor);
			
			underlying.getAll_$$Prim$$_s(cursor, buffer, offset, realLength);
			
			cursor += realLength;
			
			return realLength;
		}
		
		
		@Override
		public void setCursor(long position) throws IllegalArgumentException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (position < 0)
				throw new IllegalArgumentException();
			
			this.cursor = safeCastS64toS32(position);
		}
		
		@Override
		public long getCursor() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return cursor;
		}
		
		@Override
		public long getLength() throws ClosedStreamException
		{
			super.requireOpen();
			
			return underlying.size();
		}
		
		@Override
		protected void close0() throws IOException
		{
			//Be nice to the garbage collector :3
			this.underlying = null;
		}
	}
	
	
	
	
	
	
	
	
	public static class _$$Primitive$$_ListBackedWriteStream
	extends AbstractStream
	implements Indolent_$$Prim$$_BlockWriteStream, LengthMutableWriteStream, SeekableStream
	{
		protected _$$Primitive$$_List underlying;
		protected int cursor = 0;
		protected boolean autoExtendIfPossible;
		
		public _$$Primitive$$_ListBackedWriteStream(@LiveValue @Nonnull _$$Primitive$$_List underlying)
		{
			this.underlying = requireNonNull(underlying);
			this.autoExtendIfPossible = isUnderlyingDefinitelyOrPossiblyVariableLength();  //Must come after above line! XD
		}
		
		@ImplementationTransparency
		public _$$Primitive$$_List getUnderlying()
		{
			return underlying;
		}
		
		@ThrowAwayValue
		public _$$prim$$_[] to_$$Prim$$_ArraySnapshotting()
		{
			return getUnderlying().to_$$Prim$$_Array();
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public _$$prim$$_[] to_$$Prim$$_ArrayPossiblyLive()
		{
			return getUnderlying().to_$$Prim$$_ArrayPossiblyLive();
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public Slice<_$$prim$$_[]> to_$$Prim$$_ArraySlicePossiblyLive()
		{
			return getUnderlying().to_$$Prim$$_ArraySlicePossiblyLive();
		}
		
		
		
		
		protected boolean isUnderlyingDefinitelyOrPossiblyVariableLength()
		{
			return Primitives.isFalseOrNull(CollectionUtilities.isFixedLengthNotVariableLength(underlying));
		}
		
		@Override
		public boolean isAutoExtend() throws ClosedStreamException
		{
			super.requireOpen();
			
			return autoExtendIfPossible ? isUnderlyingDefinitelyOrPossiblyVariableLength() : false;
		}
		
		@Override
		public boolean supportsSettingToAutoExtendValue(boolean value) throws ClosedStreamException
		{
			super.requireOpen();
			
			if (value == false)
				return true;
			else
				return isUnderlyingDefinitelyOrPossiblyVariableLength();
		}
		
		@Override
		public void setAutoExtend(boolean autoExtend) throws ClosedStreamException, UnsupportedOperationException
		{
			super.requireOpen();
			
			if (autoExtend == false)
			{
				this.autoExtendIfPossible = false;
			}
			else
			{
				if (!isUnderlyingDefinitelyOrPossiblyVariableLength())
					throw new UnsupportedAddressTypeException();
				
				this.autoExtendIfPossible = true;
			}
		}
		
		
		
		
		@Override
		public void flush() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (underlying instanceof Flushable)
			{
				((Flushable)underlying).flush();
			}
		}
		
		
		
		@Override
		public void setLength(long value) throws IllegalLengthException, ClosedStreamException, IOException
		{
			super.requireOpen();
			
			if (!isAutoExtend())
				throw new UnsupportedOperationException();
			
			IllegalLengthException.checkLength(0, Integer.MAX_VALUE, value);
			
			CollectionUtilities.setListSize(underlying, (int)value);
		}
		
		
		
		
		
		
		
		
		
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return !isAutoExtend() && cursor >= underlying.size();
		}
		
		@Override
		public long skipIndolent(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			//We don't need to be indolent here :3
			return skip(amount);
		}
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			super.requireOpen();
			
			int amt = (int)Math.min(amount, underlying.size() - cursor);
			cursor += amt;
			return amt;
		}
		
		
		
		@Override
		public void write(_$$prim$$_ unit) throws EOFException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (isEOF())
				throw new EOFException();
			
			if (isAutoExtend() && cursor == underlying.size())
			{
				underlying.add_$$Prim$$_(unit);
			}
			else
			{
				underlying.set_$$Prim$$_(cursor, unit);
			}
			
			cursor++;
		}
		
		@Override
		public int writeIndolent(_$$prim$$_[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			int realLength;
			
			if (isAutoExtend() && cursor == underlying.size())
			{
				underlying.addAll_$$Prim$$_s(buffer, offset, length);
				realLength = length;
			}
			else
			{
				realLength = Math.min(length, underlying.size() - cursor);
				
				underlying.setAll_$$Prim$$_s(cursor, buffer, offset, realLength);
			}
			
			cursor += realLength;
			
			return realLength;
		}
		
		
		@Override
		public void setCursor(long position) throws IllegalArgumentException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (position < 0)
				throw new IllegalArgumentException();
			
			this.cursor = safeCastS64toS32(position);
		}
		
		@Override
		public long getCursor() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return cursor;
		}
		
		@Override
		public long getLength() throws ClosedStreamException
		{
			super.requireOpen();
			
			return underlying.size();
		}
		
		@Override
		protected void close0() throws IOException
		{
			//Be nice to the garbage collector :3
			this.underlying = null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	 */
	
	public static BooleanListBackedWriteStream newVariableLengthBooleanArrayBackedWriteStream()
	{
		return new BooleanListBackedWriteStream(new BooleanArrayList());
	}
	
	
	
	
	public static BooleanListBackedReadStream wrapBooleanArrayAsReadStream(@LiveValue @ReadonlyValue boolean[] array)
	{
		return wrapBooleanArrayAsReadStream(array, 0, array.length);
	}
	
	public static BooleanListBackedReadStream wrapBooleanArrayAsReadStream(@LiveValue @ReadonlyValue Slice<boolean[]> arraySlice)
	{
		return wrapBooleanArrayAsReadStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static BooleanListBackedReadStream wrapBooleanArrayAsReadStream(@LiveValue @ReadonlyValue boolean[] array, int offset, int length)
	{
		return new BooleanListBackedReadStream(PrimitiveCollections.booleanArrayAsList(array, offset, length));
	}
	
	
	
	public static BooleanListBackedWriteStream wrapBooleanArrayAsFixedLengthWriteStream(@LiveValue @WritableValue boolean[] array)
	{
		return wrapBooleanArrayAsFixedLengthWriteStream(array, 0, array.length);
	}
	
	public static BooleanListBackedWriteStream wrapBooleanArrayAsFixedLengthWriteStream(@LiveValue @WritableValue Slice<boolean[]> arraySlice)
	{
		return wrapBooleanArrayAsFixedLengthWriteStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static BooleanListBackedWriteStream wrapBooleanArrayAsFixedLengthWriteStream(@LiveValue @WritableValue boolean[] array, int offset, int length)
	{
		return new BooleanListBackedWriteStream(new FixedLengthArrayWrapperBooleanList(array, offset, length));
	}
	
	
	
	public static BooleanListBackedWriteStream wrapBooleanArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue boolean[] array)
	{
		return wrapBooleanArrayAsVariableLengthWriteStream(array, 0, array.length);
	}
	
	public static BooleanListBackedWriteStream wrapBooleanArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue Slice<boolean[]> arraySlice)
	{
		return wrapBooleanArrayAsVariableLengthWriteStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static BooleanListBackedWriteStream wrapBooleanArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue boolean[] array, int offset, int length)
	{
		return new BooleanListBackedWriteStream(PrimitiveCollections.booleanArrayAsMutableList(array, offset, length));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class BooleanListBackedReadStream
	extends AbstractStream
	implements BooleanBlockReadStream, LengthAwareStream, SeekableStream
	{
		protected BooleanList underlying;
		protected int cursor = 0;
		
		public BooleanListBackedReadStream(@LiveValue @Nonnull BooleanList underlying)
		{
			this.underlying = requireNonNull(underlying);
		}
		
		@ImplementationTransparency
		public BooleanList getUnderlying()
		{
			return this.underlying;
		}
		
		
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor >= this.underlying.size();
		}
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			super.requireOpen();
			
			int amt = (int)Math.min(amount, this.underlying.size() - this.cursor);
			this.cursor += amt;
			return amt;
		}
		
		
		@Override
		public boolean read() throws EOFException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (isEOF())
				throw new EOFException();
			
			boolean v = this.underlying.getBoolean(this.cursor);
			this.cursor++;
			return v;
		}
		
		@Override
		public int read(boolean[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			int realLength = Math.min(length, this.underlying.size() - this.cursor);
			
			this.underlying.getAllBooleans(this.cursor, buffer, offset, realLength);
			
			this.cursor += realLength;
			
			return realLength;
		}
		
		
		@Override
		public void setCursor(long position) throws IllegalArgumentException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (position < 0)
				throw new IllegalArgumentException();
			
			this.cursor = safeCastS64toS32(position);
		}
		
		@Override
		public long getCursor() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor;
		}
		
		@Override
		public long getLength() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.underlying.size();
		}
		
		@Override
		protected void close0() throws IOException
		{
			//Be nice to the garbage collector :3
			this.underlying = null;
		}
	}
	
	
	
	
	
	
	
	
	public static class BooleanListBackedWriteStream
	extends AbstractStream
	implements IndolentBooleanBlockWriteStream, LengthMutableWriteStream, SeekableStream
	{
		protected BooleanList underlying;
		protected int cursor = 0;
		protected boolean autoExtendIfPossible;
		
		public BooleanListBackedWriteStream(@LiveValue @Nonnull BooleanList underlying)
		{
			this.underlying = requireNonNull(underlying);
			this.autoExtendIfPossible = isUnderlyingDefinitelyOrPossiblyVariableLength();  //Must come after above line! XD
		}
		
		@ImplementationTransparency
		public BooleanList getUnderlying()
		{
			return this.underlying;
		}
		
		@ThrowAwayValue
		public boolean[] toBooleanArraySnapshotting()
		{
			return getUnderlying().toBooleanArray();
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public boolean[] toBooleanArrayPossiblyLive()
		{
			return getUnderlying().toBooleanArrayPossiblyLive();
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public Slice<boolean[]> toBooleanArraySlicePossiblyLive()
		{
			return getUnderlying().toBooleanArraySlicePossiblyLive();
		}
		
		
		
		
		protected boolean isUnderlyingDefinitelyOrPossiblyVariableLength()
		{
			return Primitives.isFalseOrNull(PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.underlying));
		}
		
		@Override
		public boolean isAutoExtend() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.autoExtendIfPossible ? isUnderlyingDefinitelyOrPossiblyVariableLength() : false;
		}
		
		@Override
		public boolean supportsSettingToAutoExtendValue(boolean value) throws ClosedStreamException
		{
			super.requireOpen();
			
			if (value == false)
				return true;
			else
				return isUnderlyingDefinitelyOrPossiblyVariableLength();
		}
		
		@Override
		public void setAutoExtend(boolean autoExtend) throws ClosedStreamException, UnsupportedOperationException
		{
			super.requireOpen();
			
			if (autoExtend == false)
			{
				this.autoExtendIfPossible = false;
			}
			else
			{
				if (!isUnderlyingDefinitelyOrPossiblyVariableLength())
					throw new UnsupportedAddressTypeException();
				
				this.autoExtendIfPossible = true;
			}
		}
		
		
		
		
		@Override
		public void flush() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (this.underlying instanceof Flushable)
			{
				((Flushable)this.underlying).flush();
			}
		}
		
		
		
		@Override
		public void setLength(long value) throws IllegalLengthException, ClosedStreamException, IOException
		{
			super.requireOpen();
			
			if (!isAutoExtend())
				throw new UnsupportedOperationException();
			
			IllegalLengthException.checkLength(0, Integer.MAX_VALUE, value);
			
			CollectionUtilities.setListSize(this.underlying, (int)value);
		}
		
		
		
		
		
		
		
		
		
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return !isAutoExtend() && this.cursor >= this.underlying.size();
		}
		
		@Override
		public long skipIndolent(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			//We don't need to be indolent here :3
			return skip(amount);
		}
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			super.requireOpen();
			
			int amt = (int)Math.min(amount, this.underlying.size() - this.cursor);
			this.cursor += amt;
			return amt;
		}
		
		
		
		@Override
		public void write(boolean unit) throws EOFException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (isEOF())
				throw new EOFException();
			
			if (isAutoExtend() && this.cursor == this.underlying.size())
			{
				this.underlying.addBoolean(unit);
			}
			else
			{
				this.underlying.setBoolean(this.cursor, unit);
			}
			
			this.cursor++;
		}
		
		@Override
		public int writeIndolent(boolean[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			int realLength;
			
			if (isAutoExtend() && this.cursor == this.underlying.size())
			{
				this.underlying.addAllBooleans(buffer, offset, length);
				realLength = length;
			}
			else
			{
				realLength = Math.min(length, this.underlying.size() - this.cursor);
				
				this.underlying.setAllBooleans(this.cursor, buffer, offset, realLength);
			}
			
			this.cursor += realLength;
			
			return realLength;
		}
		
		
		@Override
		public void setCursor(long position) throws IllegalArgumentException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (position < 0)
				throw new IllegalArgumentException();
			
			this.cursor = safeCastS64toS32(position);
		}
		
		@Override
		public long getCursor() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor;
		}
		
		@Override
		public long getLength() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.underlying.size();
		}
		
		@Override
		protected void close0() throws IOException
		{
			//Be nice to the garbage collector :3
			this.underlying = null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static ByteListBackedWriteStream newVariableLengthByteArrayBackedWriteStream()
	{
		return new ByteListBackedWriteStream(new ByteArrayList());
	}
	
	
	
	
	public static ByteListBackedReadStream wrapByteArrayAsReadStream(@LiveValue @ReadonlyValue byte[] array)
	{
		return wrapByteArrayAsReadStream(array, 0, array.length);
	}
	
	public static ByteListBackedReadStream wrapByteArrayAsReadStream(@LiveValue @ReadonlyValue Slice<byte[]> arraySlice)
	{
		return wrapByteArrayAsReadStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static ByteListBackedReadStream wrapByteArrayAsReadStream(@LiveValue @ReadonlyValue byte[] array, int offset, int length)
	{
		return new ByteListBackedReadStream(PrimitiveCollections.byteArrayAsList(array, offset, length));
	}
	
	
	
	public static ByteListBackedWriteStream wrapByteArrayAsFixedLengthWriteStream(@LiveValue @WritableValue byte[] array)
	{
		return wrapByteArrayAsFixedLengthWriteStream(array, 0, array.length);
	}
	
	public static ByteListBackedWriteStream wrapByteArrayAsFixedLengthWriteStream(@LiveValue @WritableValue Slice<byte[]> arraySlice)
	{
		return wrapByteArrayAsFixedLengthWriteStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static ByteListBackedWriteStream wrapByteArrayAsFixedLengthWriteStream(@LiveValue @WritableValue byte[] array, int offset, int length)
	{
		return new ByteListBackedWriteStream(new FixedLengthArrayWrapperByteList(array, offset, length));
	}
	
	
	
	public static ByteListBackedWriteStream wrapByteArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue byte[] array)
	{
		return wrapByteArrayAsVariableLengthWriteStream(array, 0, array.length);
	}
	
	public static ByteListBackedWriteStream wrapByteArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue Slice<byte[]> arraySlice)
	{
		return wrapByteArrayAsVariableLengthWriteStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static ByteListBackedWriteStream wrapByteArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue byte[] array, int offset, int length)
	{
		return new ByteListBackedWriteStream(PrimitiveCollections.byteArrayAsMutableList(array, offset, length));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ByteListBackedReadStream
	extends AbstractStream
	implements ByteBlockReadStream, LengthAwareStream, SeekableStream
	{
		protected ByteList underlying;
		protected int cursor = 0;
		
		public ByteListBackedReadStream(@LiveValue @Nonnull ByteList underlying)
		{
			this.underlying = requireNonNull(underlying);
		}
		
		@ImplementationTransparency
		public ByteList getUnderlying()
		{
			return this.underlying;
		}
		
		
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor >= this.underlying.size();
		}
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			super.requireOpen();
			
			int amt = (int)Math.min(amount, this.underlying.size() - this.cursor);
			this.cursor += amt;
			return amt;
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (isEOF())
				throw new EOFException();
			
			byte v = this.underlying.getByte(this.cursor);
			this.cursor++;
			return v;
		}
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			int realLength = Math.min(length, this.underlying.size() - this.cursor);
			
			this.underlying.getAllBytes(this.cursor, buffer, offset, realLength);
			
			this.cursor += realLength;
			
			return realLength;
		}
		
		
		@Override
		public void setCursor(long position) throws IllegalArgumentException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (position < 0)
				throw new IllegalArgumentException();
			
			this.cursor = safeCastS64toS32(position);
		}
		
		@Override
		public long getCursor() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor;
		}
		
		@Override
		public long getLength() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.underlying.size();
		}
		
		@Override
		protected void close0() throws IOException
		{
			//Be nice to the garbage collector :3
			this.underlying = null;
		}
	}
	
	
	
	
	
	
	
	
	public static class ByteListBackedWriteStream
	extends AbstractStream
	implements IndolentByteBlockWriteStream, LengthMutableWriteStream, SeekableStream
	{
		protected ByteList underlying;
		protected int cursor = 0;
		protected boolean autoExtendIfPossible;
		
		public ByteListBackedWriteStream(@LiveValue @Nonnull ByteList underlying)
		{
			this.underlying = requireNonNull(underlying);
			this.autoExtendIfPossible = isUnderlyingDefinitelyOrPossiblyVariableLength();  //Must come after above line! XD
		}
		
		@ImplementationTransparency
		public ByteList getUnderlying()
		{
			return this.underlying;
		}
		
		@ThrowAwayValue
		public byte[] toByteArraySnapshotting()
		{
			return getUnderlying().toByteArray();
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public byte[] toByteArrayPossiblyLive()
		{
			return getUnderlying().toByteArrayPossiblyLive();
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public Slice<byte[]> toByteArraySlicePossiblyLive()
		{
			return getUnderlying().toByteArraySlicePossiblyLive();
		}
		
		
		
		
		protected boolean isUnderlyingDefinitelyOrPossiblyVariableLength()
		{
			return Primitives.isFalseOrNull(PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.underlying));
		}
		
		@Override
		public boolean isAutoExtend() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.autoExtendIfPossible ? isUnderlyingDefinitelyOrPossiblyVariableLength() : false;
		}
		
		@Override
		public boolean supportsSettingToAutoExtendValue(boolean value) throws ClosedStreamException
		{
			super.requireOpen();
			
			if (value == false)
				return true;
			else
				return isUnderlyingDefinitelyOrPossiblyVariableLength();
		}
		
		@Override
		public void setAutoExtend(boolean autoExtend) throws ClosedStreamException, UnsupportedOperationException
		{
			super.requireOpen();
			
			if (autoExtend == false)
			{
				this.autoExtendIfPossible = false;
			}
			else
			{
				if (!isUnderlyingDefinitelyOrPossiblyVariableLength())
					throw new UnsupportedAddressTypeException();
				
				this.autoExtendIfPossible = true;
			}
		}
		
		
		
		
		@Override
		public void flush() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (this.underlying instanceof Flushable)
			{
				((Flushable)this.underlying).flush();
			}
		}
		
		
		
		@Override
		public void setLength(long value) throws IllegalLengthException, ClosedStreamException, IOException
		{
			super.requireOpen();
			
			if (!isAutoExtend())
				throw new UnsupportedOperationException();
			
			IllegalLengthException.checkLength(0, Integer.MAX_VALUE, value);
			
			CollectionUtilities.setListSize(this.underlying, (int)value);
		}
		
		
		
		
		
		
		
		
		
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return !isAutoExtend() && this.cursor >= this.underlying.size();
		}
		
		@Override
		public long skipIndolent(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			//We don't need to be indolent here :3
			return skip(amount);
		}
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			super.requireOpen();
			
			int amt = (int)Math.min(amount, this.underlying.size() - this.cursor);
			this.cursor += amt;
			return amt;
		}
		
		
		
		@Override
		public void write(byte unit) throws EOFException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (isEOF())
				throw new EOFException();
			
			if (isAutoExtend() && this.cursor == this.underlying.size())
			{
				this.underlying.addByte(unit);
			}
			else
			{
				this.underlying.setByte(this.cursor, unit);
			}
			
			this.cursor++;
		}
		
		@Override
		public int writeIndolent(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			int realLength;
			
			if (isAutoExtend() && this.cursor == this.underlying.size())
			{
				this.underlying.addAllBytes(buffer, offset, length);
				realLength = length;
			}
			else
			{
				realLength = Math.min(length, this.underlying.size() - this.cursor);
				
				this.underlying.setAllBytes(this.cursor, buffer, offset, realLength);
			}
			
			this.cursor += realLength;
			
			return realLength;
		}
		
		
		@Override
		public void setCursor(long position) throws IllegalArgumentException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (position < 0)
				throw new IllegalArgumentException();
			
			this.cursor = safeCastS64toS32(position);
		}
		
		@Override
		public long getCursor() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor;
		}
		
		@Override
		public long getLength() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.underlying.size();
		}
		
		@Override
		protected void close0() throws IOException
		{
			//Be nice to the garbage collector :3
			this.underlying = null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static CharacterListBackedWriteStream newVariableLengthCharArrayBackedWriteStream()
	{
		return new CharacterListBackedWriteStream(new CharacterArrayList());
	}
	
	
	
	
	public static CharacterListBackedReadStream wrapCharArrayAsReadStream(@LiveValue @ReadonlyValue char[] array)
	{
		return wrapCharArrayAsReadStream(array, 0, array.length);
	}
	
	public static CharacterListBackedReadStream wrapCharArrayAsReadStream(@LiveValue @ReadonlyValue Slice<char[]> arraySlice)
	{
		return wrapCharArrayAsReadStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static CharacterListBackedReadStream wrapCharArrayAsReadStream(@LiveValue @ReadonlyValue char[] array, int offset, int length)
	{
		return new CharacterListBackedReadStream(PrimitiveCollections.charArrayAsList(array, offset, length));
	}
	
	
	
	public static CharacterListBackedWriteStream wrapCharArrayAsFixedLengthWriteStream(@LiveValue @WritableValue char[] array)
	{
		return wrapCharArrayAsFixedLengthWriteStream(array, 0, array.length);
	}
	
	public static CharacterListBackedWriteStream wrapCharArrayAsFixedLengthWriteStream(@LiveValue @WritableValue Slice<char[]> arraySlice)
	{
		return wrapCharArrayAsFixedLengthWriteStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static CharacterListBackedWriteStream wrapCharArrayAsFixedLengthWriteStream(@LiveValue @WritableValue char[] array, int offset, int length)
	{
		return new CharacterListBackedWriteStream(new FixedLengthArrayWrapperCharacterList(array, offset, length));
	}
	
	
	
	public static CharacterListBackedWriteStream wrapCharArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue char[] array)
	{
		return wrapCharArrayAsVariableLengthWriteStream(array, 0, array.length);
	}
	
	public static CharacterListBackedWriteStream wrapCharArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue Slice<char[]> arraySlice)
	{
		return wrapCharArrayAsVariableLengthWriteStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static CharacterListBackedWriteStream wrapCharArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue char[] array, int offset, int length)
	{
		return new CharacterListBackedWriteStream(PrimitiveCollections.charArrayAsMutableList(array, offset, length));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class CharacterListBackedReadStream
	extends AbstractStream
	implements CharBlockReadStream, LengthAwareStream, SeekableStream
	{
		protected CharacterList underlying;
		protected int cursor = 0;
		
		public CharacterListBackedReadStream(@LiveValue @Nonnull CharacterList underlying)
		{
			this.underlying = requireNonNull(underlying);
		}
		
		@ImplementationTransparency
		public CharacterList getUnderlying()
		{
			return this.underlying;
		}
		
		
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor >= this.underlying.size();
		}
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			super.requireOpen();
			
			int amt = (int)Math.min(amount, this.underlying.size() - this.cursor);
			this.cursor += amt;
			return amt;
		}
		
		
		@Override
		public char read() throws EOFException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (isEOF())
				throw new EOFException();
			
			char v = this.underlying.getChar(this.cursor);
			this.cursor++;
			return v;
		}
		
		@Override
		public int read(char[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			int realLength = Math.min(length, this.underlying.size() - this.cursor);
			
			this.underlying.getAllChars(this.cursor, buffer, offset, realLength);
			
			this.cursor += realLength;
			
			return realLength;
		}
		
		
		@Override
		public void setCursor(long position) throws IllegalArgumentException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (position < 0)
				throw new IllegalArgumentException();
			
			this.cursor = safeCastS64toS32(position);
		}
		
		@Override
		public long getCursor() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor;
		}
		
		@Override
		public long getLength() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.underlying.size();
		}
		
		@Override
		protected void close0() throws IOException
		{
			//Be nice to the garbage collector :3
			this.underlying = null;
		}
	}
	
	
	
	
	
	
	
	
	public static class CharacterListBackedWriteStream
	extends AbstractStream
	implements IndolentCharBlockWriteStream, LengthMutableWriteStream, SeekableStream
	{
		protected CharacterList underlying;
		protected int cursor = 0;
		protected boolean autoExtendIfPossible;
		
		public CharacterListBackedWriteStream(@LiveValue @Nonnull CharacterList underlying)
		{
			this.underlying = requireNonNull(underlying);
			this.autoExtendIfPossible = isUnderlyingDefinitelyOrPossiblyVariableLength();  //Must come after above line! XD
		}
		
		@ImplementationTransparency
		public CharacterList getUnderlying()
		{
			return this.underlying;
		}
		
		@ThrowAwayValue
		public char[] toCharArraySnapshotting()
		{
			return getUnderlying().toCharArray();
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public char[] toCharArrayPossiblyLive()
		{
			return getUnderlying().toCharArrayPossiblyLive();
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public Slice<char[]> toCharArraySlicePossiblyLive()
		{
			return getUnderlying().toCharArraySlicePossiblyLive();
		}
		
		
		
		
		protected boolean isUnderlyingDefinitelyOrPossiblyVariableLength()
		{
			return Primitives.isFalseOrNull(PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.underlying));
		}
		
		@Override
		public boolean isAutoExtend() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.autoExtendIfPossible ? isUnderlyingDefinitelyOrPossiblyVariableLength() : false;
		}
		
		@Override
		public boolean supportsSettingToAutoExtendValue(boolean value) throws ClosedStreamException
		{
			super.requireOpen();
			
			if (value == false)
				return true;
			else
				return isUnderlyingDefinitelyOrPossiblyVariableLength();
		}
		
		@Override
		public void setAutoExtend(boolean autoExtend) throws ClosedStreamException, UnsupportedOperationException
		{
			super.requireOpen();
			
			if (autoExtend == false)
			{
				this.autoExtendIfPossible = false;
			}
			else
			{
				if (!isUnderlyingDefinitelyOrPossiblyVariableLength())
					throw new UnsupportedAddressTypeException();
				
				this.autoExtendIfPossible = true;
			}
		}
		
		
		
		
		@Override
		public void flush() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (this.underlying instanceof Flushable)
			{
				((Flushable)this.underlying).flush();
			}
		}
		
		
		
		@Override
		public void setLength(long value) throws IllegalLengthException, ClosedStreamException, IOException
		{
			super.requireOpen();
			
			if (!isAutoExtend())
				throw new UnsupportedOperationException();
			
			IllegalLengthException.checkLength(0, Integer.MAX_VALUE, value);
			
			CollectionUtilities.setListSize(this.underlying, (int)value);
		}
		
		
		
		
		
		
		
		
		
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return !isAutoExtend() && this.cursor >= this.underlying.size();
		}
		
		@Override
		public long skipIndolent(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			//We don't need to be indolent here :3
			return skip(amount);
		}
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			super.requireOpen();
			
			int amt = (int)Math.min(amount, this.underlying.size() - this.cursor);
			this.cursor += amt;
			return amt;
		}
		
		
		
		@Override
		public void write(char unit) throws EOFException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (isEOF())
				throw new EOFException();
			
			if (isAutoExtend() && this.cursor == this.underlying.size())
			{
				this.underlying.addChar(unit);
			}
			else
			{
				this.underlying.setChar(this.cursor, unit);
			}
			
			this.cursor++;
		}
		
		@Override
		public int writeIndolent(char[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			int realLength;
			
			if (isAutoExtend() && this.cursor == this.underlying.size())
			{
				this.underlying.addAllChars(buffer, offset, length);
				realLength = length;
			}
			else
			{
				realLength = Math.min(length, this.underlying.size() - this.cursor);
				
				this.underlying.setAllChars(this.cursor, buffer, offset, realLength);
			}
			
			this.cursor += realLength;
			
			return realLength;
		}
		
		
		@Override
		public void setCursor(long position) throws IllegalArgumentException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (position < 0)
				throw new IllegalArgumentException();
			
			this.cursor = safeCastS64toS32(position);
		}
		
		@Override
		public long getCursor() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor;
		}
		
		@Override
		public long getLength() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.underlying.size();
		}
		
		@Override
		protected void close0() throws IOException
		{
			//Be nice to the garbage collector :3
			this.underlying = null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static ShortListBackedWriteStream newVariableLengthShortArrayBackedWriteStream()
	{
		return new ShortListBackedWriteStream(new ShortArrayList());
	}
	
	
	
	
	public static ShortListBackedReadStream wrapShortArrayAsReadStream(@LiveValue @ReadonlyValue short[] array)
	{
		return wrapShortArrayAsReadStream(array, 0, array.length);
	}
	
	public static ShortListBackedReadStream wrapShortArrayAsReadStream(@LiveValue @ReadonlyValue Slice<short[]> arraySlice)
	{
		return wrapShortArrayAsReadStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static ShortListBackedReadStream wrapShortArrayAsReadStream(@LiveValue @ReadonlyValue short[] array, int offset, int length)
	{
		return new ShortListBackedReadStream(PrimitiveCollections.shortArrayAsList(array, offset, length));
	}
	
	
	
	public static ShortListBackedWriteStream wrapShortArrayAsFixedLengthWriteStream(@LiveValue @WritableValue short[] array)
	{
		return wrapShortArrayAsFixedLengthWriteStream(array, 0, array.length);
	}
	
	public static ShortListBackedWriteStream wrapShortArrayAsFixedLengthWriteStream(@LiveValue @WritableValue Slice<short[]> arraySlice)
	{
		return wrapShortArrayAsFixedLengthWriteStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static ShortListBackedWriteStream wrapShortArrayAsFixedLengthWriteStream(@LiveValue @WritableValue short[] array, int offset, int length)
	{
		return new ShortListBackedWriteStream(new FixedLengthArrayWrapperShortList(array, offset, length));
	}
	
	
	
	public static ShortListBackedWriteStream wrapShortArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue short[] array)
	{
		return wrapShortArrayAsVariableLengthWriteStream(array, 0, array.length);
	}
	
	public static ShortListBackedWriteStream wrapShortArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue Slice<short[]> arraySlice)
	{
		return wrapShortArrayAsVariableLengthWriteStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static ShortListBackedWriteStream wrapShortArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue short[] array, int offset, int length)
	{
		return new ShortListBackedWriteStream(PrimitiveCollections.shortArrayAsMutableList(array, offset, length));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ShortListBackedReadStream
	extends AbstractStream
	implements ShortBlockReadStream, LengthAwareStream, SeekableStream
	{
		protected ShortList underlying;
		protected int cursor = 0;
		
		public ShortListBackedReadStream(@LiveValue @Nonnull ShortList underlying)
		{
			this.underlying = requireNonNull(underlying);
		}
		
		@ImplementationTransparency
		public ShortList getUnderlying()
		{
			return this.underlying;
		}
		
		
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor >= this.underlying.size();
		}
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			super.requireOpen();
			
			int amt = (int)Math.min(amount, this.underlying.size() - this.cursor);
			this.cursor += amt;
			return amt;
		}
		
		
		@Override
		public short read() throws EOFException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (isEOF())
				throw new EOFException();
			
			short v = this.underlying.getShort(this.cursor);
			this.cursor++;
			return v;
		}
		
		@Override
		public int read(short[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			int realLength = Math.min(length, this.underlying.size() - this.cursor);
			
			this.underlying.getAllShorts(this.cursor, buffer, offset, realLength);
			
			this.cursor += realLength;
			
			return realLength;
		}
		
		
		@Override
		public void setCursor(long position) throws IllegalArgumentException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (position < 0)
				throw new IllegalArgumentException();
			
			this.cursor = safeCastS64toS32(position);
		}
		
		@Override
		public long getCursor() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor;
		}
		
		@Override
		public long getLength() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.underlying.size();
		}
		
		@Override
		protected void close0() throws IOException
		{
			//Be nice to the garbage collector :3
			this.underlying = null;
		}
	}
	
	
	
	
	
	
	
	
	public static class ShortListBackedWriteStream
	extends AbstractStream
	implements IndolentShortBlockWriteStream, LengthMutableWriteStream, SeekableStream
	{
		protected ShortList underlying;
		protected int cursor = 0;
		protected boolean autoExtendIfPossible;
		
		public ShortListBackedWriteStream(@LiveValue @Nonnull ShortList underlying)
		{
			this.underlying = requireNonNull(underlying);
			this.autoExtendIfPossible = isUnderlyingDefinitelyOrPossiblyVariableLength();  //Must come after above line! XD
		}
		
		@ImplementationTransparency
		public ShortList getUnderlying()
		{
			return this.underlying;
		}
		
		@ThrowAwayValue
		public short[] toShortArraySnapshotting()
		{
			return getUnderlying().toShortArray();
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public short[] toShortArrayPossiblyLive()
		{
			return getUnderlying().toShortArrayPossiblyLive();
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public Slice<short[]> toShortArraySlicePossiblyLive()
		{
			return getUnderlying().toShortArraySlicePossiblyLive();
		}
		
		
		
		
		protected boolean isUnderlyingDefinitelyOrPossiblyVariableLength()
		{
			return Primitives.isFalseOrNull(PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.underlying));
		}
		
		@Override
		public boolean isAutoExtend() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.autoExtendIfPossible ? isUnderlyingDefinitelyOrPossiblyVariableLength() : false;
		}
		
		@Override
		public boolean supportsSettingToAutoExtendValue(boolean value) throws ClosedStreamException
		{
			super.requireOpen();
			
			if (value == false)
				return true;
			else
				return isUnderlyingDefinitelyOrPossiblyVariableLength();
		}
		
		@Override
		public void setAutoExtend(boolean autoExtend) throws ClosedStreamException, UnsupportedOperationException
		{
			super.requireOpen();
			
			if (autoExtend == false)
			{
				this.autoExtendIfPossible = false;
			}
			else
			{
				if (!isUnderlyingDefinitelyOrPossiblyVariableLength())
					throw new UnsupportedAddressTypeException();
				
				this.autoExtendIfPossible = true;
			}
		}
		
		
		
		
		@Override
		public void flush() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (this.underlying instanceof Flushable)
			{
				((Flushable)this.underlying).flush();
			}
		}
		
		
		
		@Override
		public void setLength(long value) throws IllegalLengthException, ClosedStreamException, IOException
		{
			super.requireOpen();
			
			if (!isAutoExtend())
				throw new UnsupportedOperationException();
			
			IllegalLengthException.checkLength(0, Integer.MAX_VALUE, value);
			
			CollectionUtilities.setListSize(this.underlying, (int)value);
		}
		
		
		
		
		
		
		
		
		
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return !isAutoExtend() && this.cursor >= this.underlying.size();
		}
		
		@Override
		public long skipIndolent(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			//We don't need to be indolent here :3
			return skip(amount);
		}
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			super.requireOpen();
			
			int amt = (int)Math.min(amount, this.underlying.size() - this.cursor);
			this.cursor += amt;
			return amt;
		}
		
		
		
		@Override
		public void write(short unit) throws EOFException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (isEOF())
				throw new EOFException();
			
			if (isAutoExtend() && this.cursor == this.underlying.size())
			{
				this.underlying.addShort(unit);
			}
			else
			{
				this.underlying.setShort(this.cursor, unit);
			}
			
			this.cursor++;
		}
		
		@Override
		public int writeIndolent(short[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			int realLength;
			
			if (isAutoExtend() && this.cursor == this.underlying.size())
			{
				this.underlying.addAllShorts(buffer, offset, length);
				realLength = length;
			}
			else
			{
				realLength = Math.min(length, this.underlying.size() - this.cursor);
				
				this.underlying.setAllShorts(this.cursor, buffer, offset, realLength);
			}
			
			this.cursor += realLength;
			
			return realLength;
		}
		
		
		@Override
		public void setCursor(long position) throws IllegalArgumentException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (position < 0)
				throw new IllegalArgumentException();
			
			this.cursor = safeCastS64toS32(position);
		}
		
		@Override
		public long getCursor() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor;
		}
		
		@Override
		public long getLength() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.underlying.size();
		}
		
		@Override
		protected void close0() throws IOException
		{
			//Be nice to the garbage collector :3
			this.underlying = null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static FloatListBackedWriteStream newVariableLengthFloatArrayBackedWriteStream()
	{
		return new FloatListBackedWriteStream(new FloatArrayList());
	}
	
	
	
	
	public static FloatListBackedReadStream wrapFloatArrayAsReadStream(@LiveValue @ReadonlyValue float[] array)
	{
		return wrapFloatArrayAsReadStream(array, 0, array.length);
	}
	
	public static FloatListBackedReadStream wrapFloatArrayAsReadStream(@LiveValue @ReadonlyValue Slice<float[]> arraySlice)
	{
		return wrapFloatArrayAsReadStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static FloatListBackedReadStream wrapFloatArrayAsReadStream(@LiveValue @ReadonlyValue float[] array, int offset, int length)
	{
		return new FloatListBackedReadStream(PrimitiveCollections.floatArrayAsList(array, offset, length));
	}
	
	
	
	public static FloatListBackedWriteStream wrapFloatArrayAsFixedLengthWriteStream(@LiveValue @WritableValue float[] array)
	{
		return wrapFloatArrayAsFixedLengthWriteStream(array, 0, array.length);
	}
	
	public static FloatListBackedWriteStream wrapFloatArrayAsFixedLengthWriteStream(@LiveValue @WritableValue Slice<float[]> arraySlice)
	{
		return wrapFloatArrayAsFixedLengthWriteStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static FloatListBackedWriteStream wrapFloatArrayAsFixedLengthWriteStream(@LiveValue @WritableValue float[] array, int offset, int length)
	{
		return new FloatListBackedWriteStream(new FixedLengthArrayWrapperFloatList(array, offset, length));
	}
	
	
	
	public static FloatListBackedWriteStream wrapFloatArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue float[] array)
	{
		return wrapFloatArrayAsVariableLengthWriteStream(array, 0, array.length);
	}
	
	public static FloatListBackedWriteStream wrapFloatArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue Slice<float[]> arraySlice)
	{
		return wrapFloatArrayAsVariableLengthWriteStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static FloatListBackedWriteStream wrapFloatArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue float[] array, int offset, int length)
	{
		return new FloatListBackedWriteStream(PrimitiveCollections.floatArrayAsMutableList(array, offset, length));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class FloatListBackedReadStream
	extends AbstractStream
	implements FloatBlockReadStream, LengthAwareStream, SeekableStream
	{
		protected FloatList underlying;
		protected int cursor = 0;
		
		public FloatListBackedReadStream(@LiveValue @Nonnull FloatList underlying)
		{
			this.underlying = requireNonNull(underlying);
		}
		
		@ImplementationTransparency
		public FloatList getUnderlying()
		{
			return this.underlying;
		}
		
		
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor >= this.underlying.size();
		}
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			super.requireOpen();
			
			int amt = (int)Math.min(amount, this.underlying.size() - this.cursor);
			this.cursor += amt;
			return amt;
		}
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (isEOF())
				throw new EOFException();
			
			float v = this.underlying.getFloat(this.cursor);
			this.cursor++;
			return v;
		}
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			int realLength = Math.min(length, this.underlying.size() - this.cursor);
			
			this.underlying.getAllFloats(this.cursor, buffer, offset, realLength);
			
			this.cursor += realLength;
			
			return realLength;
		}
		
		
		@Override
		public void setCursor(long position) throws IllegalArgumentException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (position < 0)
				throw new IllegalArgumentException();
			
			this.cursor = safeCastS64toS32(position);
		}
		
		@Override
		public long getCursor() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor;
		}
		
		@Override
		public long getLength() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.underlying.size();
		}
		
		@Override
		protected void close0() throws IOException
		{
			//Be nice to the garbage collector :3
			this.underlying = null;
		}
	}
	
	
	
	
	
	
	
	
	public static class FloatListBackedWriteStream
	extends AbstractStream
	implements IndolentFloatBlockWriteStream, LengthMutableWriteStream, SeekableStream
	{
		protected FloatList underlying;
		protected int cursor = 0;
		protected boolean autoExtendIfPossible;
		
		public FloatListBackedWriteStream(@LiveValue @Nonnull FloatList underlying)
		{
			this.underlying = requireNonNull(underlying);
			this.autoExtendIfPossible = isUnderlyingDefinitelyOrPossiblyVariableLength();  //Must come after above line! XD
		}
		
		@ImplementationTransparency
		public FloatList getUnderlying()
		{
			return this.underlying;
		}
		
		@ThrowAwayValue
		public float[] toFloatArraySnapshotting()
		{
			return getUnderlying().toFloatArray();
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public float[] toFloatArrayPossiblyLive()
		{
			return getUnderlying().toFloatArrayPossiblyLive();
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public Slice<float[]> toFloatArraySlicePossiblyLive()
		{
			return getUnderlying().toFloatArraySlicePossiblyLive();
		}
		
		
		
		
		protected boolean isUnderlyingDefinitelyOrPossiblyVariableLength()
		{
			return Primitives.isFalseOrNull(PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.underlying));
		}
		
		@Override
		public boolean isAutoExtend() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.autoExtendIfPossible ? isUnderlyingDefinitelyOrPossiblyVariableLength() : false;
		}
		
		@Override
		public boolean supportsSettingToAutoExtendValue(boolean value) throws ClosedStreamException
		{
			super.requireOpen();
			
			if (value == false)
				return true;
			else
				return isUnderlyingDefinitelyOrPossiblyVariableLength();
		}
		
		@Override
		public void setAutoExtend(boolean autoExtend) throws ClosedStreamException, UnsupportedOperationException
		{
			super.requireOpen();
			
			if (autoExtend == false)
			{
				this.autoExtendIfPossible = false;
			}
			else
			{
				if (!isUnderlyingDefinitelyOrPossiblyVariableLength())
					throw new UnsupportedAddressTypeException();
				
				this.autoExtendIfPossible = true;
			}
		}
		
		
		
		
		@Override
		public void flush() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (this.underlying instanceof Flushable)
			{
				((Flushable)this.underlying).flush();
			}
		}
		
		
		
		@Override
		public void setLength(long value) throws IllegalLengthException, ClosedStreamException, IOException
		{
			super.requireOpen();
			
			if (!isAutoExtend())
				throw new UnsupportedOperationException();
			
			IllegalLengthException.checkLength(0, Integer.MAX_VALUE, value);
			
			CollectionUtilities.setListSize(this.underlying, (int)value);
		}
		
		
		
		
		
		
		
		
		
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return !isAutoExtend() && this.cursor >= this.underlying.size();
		}
		
		@Override
		public long skipIndolent(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			//We don't need to be indolent here :3
			return skip(amount);
		}
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			super.requireOpen();
			
			int amt = (int)Math.min(amount, this.underlying.size() - this.cursor);
			this.cursor += amt;
			return amt;
		}
		
		
		
		@Override
		public void write(float unit) throws EOFException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (isEOF())
				throw new EOFException();
			
			if (isAutoExtend() && this.cursor == this.underlying.size())
			{
				this.underlying.addFloat(unit);
			}
			else
			{
				this.underlying.setFloat(this.cursor, unit);
			}
			
			this.cursor++;
		}
		
		@Override
		public int writeIndolent(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			int realLength;
			
			if (isAutoExtend() && this.cursor == this.underlying.size())
			{
				this.underlying.addAllFloats(buffer, offset, length);
				realLength = length;
			}
			else
			{
				realLength = Math.min(length, this.underlying.size() - this.cursor);
				
				this.underlying.setAllFloats(this.cursor, buffer, offset, realLength);
			}
			
			this.cursor += realLength;
			
			return realLength;
		}
		
		
		@Override
		public void setCursor(long position) throws IllegalArgumentException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (position < 0)
				throw new IllegalArgumentException();
			
			this.cursor = safeCastS64toS32(position);
		}
		
		@Override
		public long getCursor() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor;
		}
		
		@Override
		public long getLength() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.underlying.size();
		}
		
		@Override
		protected void close0() throws IOException
		{
			//Be nice to the garbage collector :3
			this.underlying = null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static IntegerListBackedWriteStream newVariableLengthIntArrayBackedWriteStream()
	{
		return new IntegerListBackedWriteStream(new IntegerArrayList());
	}
	
	
	
	
	public static IntegerListBackedReadStream wrapIntArrayAsReadStream(@LiveValue @ReadonlyValue int[] array)
	{
		return wrapIntArrayAsReadStream(array, 0, array.length);
	}
	
	public static IntegerListBackedReadStream wrapIntArrayAsReadStream(@LiveValue @ReadonlyValue Slice<int[]> arraySlice)
	{
		return wrapIntArrayAsReadStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static IntegerListBackedReadStream wrapIntArrayAsReadStream(@LiveValue @ReadonlyValue int[] array, int offset, int length)
	{
		return new IntegerListBackedReadStream(PrimitiveCollections.intArrayAsList(array, offset, length));
	}
	
	
	
	public static IntegerListBackedWriteStream wrapIntArrayAsFixedLengthWriteStream(@LiveValue @WritableValue int[] array)
	{
		return wrapIntArrayAsFixedLengthWriteStream(array, 0, array.length);
	}
	
	public static IntegerListBackedWriteStream wrapIntArrayAsFixedLengthWriteStream(@LiveValue @WritableValue Slice<int[]> arraySlice)
	{
		return wrapIntArrayAsFixedLengthWriteStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static IntegerListBackedWriteStream wrapIntArrayAsFixedLengthWriteStream(@LiveValue @WritableValue int[] array, int offset, int length)
	{
		return new IntegerListBackedWriteStream(new FixedLengthArrayWrapperIntegerList(array, offset, length));
	}
	
	
	
	public static IntegerListBackedWriteStream wrapIntArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue int[] array)
	{
		return wrapIntArrayAsVariableLengthWriteStream(array, 0, array.length);
	}
	
	public static IntegerListBackedWriteStream wrapIntArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue Slice<int[]> arraySlice)
	{
		return wrapIntArrayAsVariableLengthWriteStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static IntegerListBackedWriteStream wrapIntArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue int[] array, int offset, int length)
	{
		return new IntegerListBackedWriteStream(PrimitiveCollections.intArrayAsMutableList(array, offset, length));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class IntegerListBackedReadStream
	extends AbstractStream
	implements IntBlockReadStream, LengthAwareStream, SeekableStream
	{
		protected IntegerList underlying;
		protected int cursor = 0;
		
		public IntegerListBackedReadStream(@LiveValue @Nonnull IntegerList underlying)
		{
			this.underlying = requireNonNull(underlying);
		}
		
		@ImplementationTransparency
		public IntegerList getUnderlying()
		{
			return this.underlying;
		}
		
		
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor >= this.underlying.size();
		}
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			super.requireOpen();
			
			int amt = (int)Math.min(amount, this.underlying.size() - this.cursor);
			this.cursor += amt;
			return amt;
		}
		
		
		@Override
		public int read() throws EOFException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (isEOF())
				throw new EOFException();
			
			int v = this.underlying.getInt(this.cursor);
			this.cursor++;
			return v;
		}
		
		@Override
		public int read(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			int realLength = Math.min(length, this.underlying.size() - this.cursor);
			
			this.underlying.getAllInts(this.cursor, buffer, offset, realLength);
			
			this.cursor += realLength;
			
			return realLength;
		}
		
		
		@Override
		public void setCursor(long position) throws IllegalArgumentException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (position < 0)
				throw new IllegalArgumentException();
			
			this.cursor = safeCastS64toS32(position);
		}
		
		@Override
		public long getCursor() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor;
		}
		
		@Override
		public long getLength() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.underlying.size();
		}
		
		@Override
		protected void close0() throws IOException
		{
			//Be nice to the garbage collector :3
			this.underlying = null;
		}
	}
	
	
	
	
	
	
	
	
	public static class IntegerListBackedWriteStream
	extends AbstractStream
	implements IndolentIntBlockWriteStream, LengthMutableWriteStream, SeekableStream
	{
		protected IntegerList underlying;
		protected int cursor = 0;
		protected boolean autoExtendIfPossible;
		
		public IntegerListBackedWriteStream(@LiveValue @Nonnull IntegerList underlying)
		{
			this.underlying = requireNonNull(underlying);
			this.autoExtendIfPossible = isUnderlyingDefinitelyOrPossiblyVariableLength();  //Must come after above line! XD
		}
		
		@ImplementationTransparency
		public IntegerList getUnderlying()
		{
			return this.underlying;
		}
		
		@ThrowAwayValue
		public int[] toIntArraySnapshotting()
		{
			return getUnderlying().toIntArray();
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public int[] toIntArrayPossiblyLive()
		{
			return getUnderlying().toIntArrayPossiblyLive();
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public Slice<int[]> toIntArraySlicePossiblyLive()
		{
			return getUnderlying().toIntArraySlicePossiblyLive();
		}
		
		
		
		
		protected boolean isUnderlyingDefinitelyOrPossiblyVariableLength()
		{
			return Primitives.isFalseOrNull(PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.underlying));
		}
		
		@Override
		public boolean isAutoExtend() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.autoExtendIfPossible ? isUnderlyingDefinitelyOrPossiblyVariableLength() : false;
		}
		
		@Override
		public boolean supportsSettingToAutoExtendValue(boolean value) throws ClosedStreamException
		{
			super.requireOpen();
			
			if (value == false)
				return true;
			else
				return isUnderlyingDefinitelyOrPossiblyVariableLength();
		}
		
		@Override
		public void setAutoExtend(boolean autoExtend) throws ClosedStreamException, UnsupportedOperationException
		{
			super.requireOpen();
			
			if (autoExtend == false)
			{
				this.autoExtendIfPossible = false;
			}
			else
			{
				if (!isUnderlyingDefinitelyOrPossiblyVariableLength())
					throw new UnsupportedAddressTypeException();
				
				this.autoExtendIfPossible = true;
			}
		}
		
		
		
		
		@Override
		public void flush() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (this.underlying instanceof Flushable)
			{
				((Flushable)this.underlying).flush();
			}
		}
		
		
		
		@Override
		public void setLength(long value) throws IllegalLengthException, ClosedStreamException, IOException
		{
			super.requireOpen();
			
			if (!isAutoExtend())
				throw new UnsupportedOperationException();
			
			IllegalLengthException.checkLength(0, Integer.MAX_VALUE, value);
			
			CollectionUtilities.setListSize(this.underlying, (int)value);
		}
		
		
		
		
		
		
		
		
		
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return !isAutoExtend() && this.cursor >= this.underlying.size();
		}
		
		@Override
		public long skipIndolent(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			//We don't need to be indolent here :3
			return skip(amount);
		}
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			super.requireOpen();
			
			int amt = (int)Math.min(amount, this.underlying.size() - this.cursor);
			this.cursor += amt;
			return amt;
		}
		
		
		
		@Override
		public void write(int unit) throws EOFException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (isEOF())
				throw new EOFException();
			
			if (isAutoExtend() && this.cursor == this.underlying.size())
			{
				this.underlying.addInt(unit);
			}
			else
			{
				this.underlying.setInt(this.cursor, unit);
			}
			
			this.cursor++;
		}
		
		@Override
		public int writeIndolent(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			int realLength;
			
			if (isAutoExtend() && this.cursor == this.underlying.size())
			{
				this.underlying.addAllInts(buffer, offset, length);
				realLength = length;
			}
			else
			{
				realLength = Math.min(length, this.underlying.size() - this.cursor);
				
				this.underlying.setAllInts(this.cursor, buffer, offset, realLength);
			}
			
			this.cursor += realLength;
			
			return realLength;
		}
		
		
		@Override
		public void setCursor(long position) throws IllegalArgumentException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (position < 0)
				throw new IllegalArgumentException();
			
			this.cursor = safeCastS64toS32(position);
		}
		
		@Override
		public long getCursor() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor;
		}
		
		@Override
		public long getLength() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.underlying.size();
		}
		
		@Override
		protected void close0() throws IOException
		{
			//Be nice to the garbage collector :3
			this.underlying = null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static DoubleListBackedWriteStream newVariableLengthDoubleArrayBackedWriteStream()
	{
		return new DoubleListBackedWriteStream(new DoubleArrayList());
	}
	
	
	
	
	public static DoubleListBackedReadStream wrapDoubleArrayAsReadStream(@LiveValue @ReadonlyValue double[] array)
	{
		return wrapDoubleArrayAsReadStream(array, 0, array.length);
	}
	
	public static DoubleListBackedReadStream wrapDoubleArrayAsReadStream(@LiveValue @ReadonlyValue Slice<double[]> arraySlice)
	{
		return wrapDoubleArrayAsReadStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static DoubleListBackedReadStream wrapDoubleArrayAsReadStream(@LiveValue @ReadonlyValue double[] array, int offset, int length)
	{
		return new DoubleListBackedReadStream(PrimitiveCollections.doubleArrayAsList(array, offset, length));
	}
	
	
	
	public static DoubleListBackedWriteStream wrapDoubleArrayAsFixedLengthWriteStream(@LiveValue @WritableValue double[] array)
	{
		return wrapDoubleArrayAsFixedLengthWriteStream(array, 0, array.length);
	}
	
	public static DoubleListBackedWriteStream wrapDoubleArrayAsFixedLengthWriteStream(@LiveValue @WritableValue Slice<double[]> arraySlice)
	{
		return wrapDoubleArrayAsFixedLengthWriteStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static DoubleListBackedWriteStream wrapDoubleArrayAsFixedLengthWriteStream(@LiveValue @WritableValue double[] array, int offset, int length)
	{
		return new DoubleListBackedWriteStream(new FixedLengthArrayWrapperDoubleList(array, offset, length));
	}
	
	
	
	public static DoubleListBackedWriteStream wrapDoubleArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue double[] array)
	{
		return wrapDoubleArrayAsVariableLengthWriteStream(array, 0, array.length);
	}
	
	public static DoubleListBackedWriteStream wrapDoubleArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue Slice<double[]> arraySlice)
	{
		return wrapDoubleArrayAsVariableLengthWriteStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static DoubleListBackedWriteStream wrapDoubleArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue double[] array, int offset, int length)
	{
		return new DoubleListBackedWriteStream(PrimitiveCollections.doubleArrayAsMutableList(array, offset, length));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class DoubleListBackedReadStream
	extends AbstractStream
	implements DoubleBlockReadStream, LengthAwareStream, SeekableStream
	{
		protected DoubleList underlying;
		protected int cursor = 0;
		
		public DoubleListBackedReadStream(@LiveValue @Nonnull DoubleList underlying)
		{
			this.underlying = requireNonNull(underlying);
		}
		
		@ImplementationTransparency
		public DoubleList getUnderlying()
		{
			return this.underlying;
		}
		
		
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor >= this.underlying.size();
		}
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			super.requireOpen();
			
			int amt = (int)Math.min(amount, this.underlying.size() - this.cursor);
			this.cursor += amt;
			return amt;
		}
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (isEOF())
				throw new EOFException();
			
			double v = this.underlying.getDouble(this.cursor);
			this.cursor++;
			return v;
		}
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			int realLength = Math.min(length, this.underlying.size() - this.cursor);
			
			this.underlying.getAllDoubles(this.cursor, buffer, offset, realLength);
			
			this.cursor += realLength;
			
			return realLength;
		}
		
		
		@Override
		public void setCursor(long position) throws IllegalArgumentException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (position < 0)
				throw new IllegalArgumentException();
			
			this.cursor = safeCastS64toS32(position);
		}
		
		@Override
		public long getCursor() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor;
		}
		
		@Override
		public long getLength() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.underlying.size();
		}
		
		@Override
		protected void close0() throws IOException
		{
			//Be nice to the garbage collector :3
			this.underlying = null;
		}
	}
	
	
	
	
	
	
	
	
	public static class DoubleListBackedWriteStream
	extends AbstractStream
	implements IndolentDoubleBlockWriteStream, LengthMutableWriteStream, SeekableStream
	{
		protected DoubleList underlying;
		protected int cursor = 0;
		protected boolean autoExtendIfPossible;
		
		public DoubleListBackedWriteStream(@LiveValue @Nonnull DoubleList underlying)
		{
			this.underlying = requireNonNull(underlying);
			this.autoExtendIfPossible = isUnderlyingDefinitelyOrPossiblyVariableLength();  //Must come after above line! XD
		}
		
		@ImplementationTransparency
		public DoubleList getUnderlying()
		{
			return this.underlying;
		}
		
		@ThrowAwayValue
		public double[] toDoubleArraySnapshotting()
		{
			return getUnderlying().toDoubleArray();
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public double[] toDoubleArrayPossiblyLive()
		{
			return getUnderlying().toDoubleArrayPossiblyLive();
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public Slice<double[]> toDoubleArraySlicePossiblyLive()
		{
			return getUnderlying().toDoubleArraySlicePossiblyLive();
		}
		
		
		
		
		protected boolean isUnderlyingDefinitelyOrPossiblyVariableLength()
		{
			return Primitives.isFalseOrNull(PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.underlying));
		}
		
		@Override
		public boolean isAutoExtend() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.autoExtendIfPossible ? isUnderlyingDefinitelyOrPossiblyVariableLength() : false;
		}
		
		@Override
		public boolean supportsSettingToAutoExtendValue(boolean value) throws ClosedStreamException
		{
			super.requireOpen();
			
			if (value == false)
				return true;
			else
				return isUnderlyingDefinitelyOrPossiblyVariableLength();
		}
		
		@Override
		public void setAutoExtend(boolean autoExtend) throws ClosedStreamException, UnsupportedOperationException
		{
			super.requireOpen();
			
			if (autoExtend == false)
			{
				this.autoExtendIfPossible = false;
			}
			else
			{
				if (!isUnderlyingDefinitelyOrPossiblyVariableLength())
					throw new UnsupportedAddressTypeException();
				
				this.autoExtendIfPossible = true;
			}
		}
		
		
		
		
		@Override
		public void flush() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (this.underlying instanceof Flushable)
			{
				((Flushable)this.underlying).flush();
			}
		}
		
		
		
		@Override
		public void setLength(long value) throws IllegalLengthException, ClosedStreamException, IOException
		{
			super.requireOpen();
			
			if (!isAutoExtend())
				throw new UnsupportedOperationException();
			
			IllegalLengthException.checkLength(0, Integer.MAX_VALUE, value);
			
			CollectionUtilities.setListSize(this.underlying, (int)value);
		}
		
		
		
		
		
		
		
		
		
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return !isAutoExtend() && this.cursor >= this.underlying.size();
		}
		
		@Override
		public long skipIndolent(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			//We don't need to be indolent here :3
			return skip(amount);
		}
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			super.requireOpen();
			
			int amt = (int)Math.min(amount, this.underlying.size() - this.cursor);
			this.cursor += amt;
			return amt;
		}
		
		
		
		@Override
		public void write(double unit) throws EOFException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (isEOF())
				throw new EOFException();
			
			if (isAutoExtend() && this.cursor == this.underlying.size())
			{
				this.underlying.addDouble(unit);
			}
			else
			{
				this.underlying.setDouble(this.cursor, unit);
			}
			
			this.cursor++;
		}
		
		@Override
		public int writeIndolent(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			int realLength;
			
			if (isAutoExtend() && this.cursor == this.underlying.size())
			{
				this.underlying.addAllDoubles(buffer, offset, length);
				realLength = length;
			}
			else
			{
				realLength = Math.min(length, this.underlying.size() - this.cursor);
				
				this.underlying.setAllDoubles(this.cursor, buffer, offset, realLength);
			}
			
			this.cursor += realLength;
			
			return realLength;
		}
		
		
		@Override
		public void setCursor(long position) throws IllegalArgumentException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (position < 0)
				throw new IllegalArgumentException();
			
			this.cursor = safeCastS64toS32(position);
		}
		
		@Override
		public long getCursor() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor;
		}
		
		@Override
		public long getLength() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.underlying.size();
		}
		
		@Override
		protected void close0() throws IOException
		{
			//Be nice to the garbage collector :3
			this.underlying = null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static LongListBackedWriteStream newVariableLengthLongArrayBackedWriteStream()
	{
		return new LongListBackedWriteStream(new LongArrayList());
	}
	
	
	
	
	public static LongListBackedReadStream wrapLongArrayAsReadStream(@LiveValue @ReadonlyValue long[] array)
	{
		return wrapLongArrayAsReadStream(array, 0, array.length);
	}
	
	public static LongListBackedReadStream wrapLongArrayAsReadStream(@LiveValue @ReadonlyValue Slice<long[]> arraySlice)
	{
		return wrapLongArrayAsReadStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static LongListBackedReadStream wrapLongArrayAsReadStream(@LiveValue @ReadonlyValue long[] array, int offset, int length)
	{
		return new LongListBackedReadStream(PrimitiveCollections.longArrayAsList(array, offset, length));
	}
	
	
	
	public static LongListBackedWriteStream wrapLongArrayAsFixedLengthWriteStream(@LiveValue @WritableValue long[] array)
	{
		return wrapLongArrayAsFixedLengthWriteStream(array, 0, array.length);
	}
	
	public static LongListBackedWriteStream wrapLongArrayAsFixedLengthWriteStream(@LiveValue @WritableValue Slice<long[]> arraySlice)
	{
		return wrapLongArrayAsFixedLengthWriteStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static LongListBackedWriteStream wrapLongArrayAsFixedLengthWriteStream(@LiveValue @WritableValue long[] array, int offset, int length)
	{
		return new LongListBackedWriteStream(new FixedLengthArrayWrapperLongList(array, offset, length));
	}
	
	
	
	public static LongListBackedWriteStream wrapLongArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue long[] array)
	{
		return wrapLongArrayAsVariableLengthWriteStream(array, 0, array.length);
	}
	
	public static LongListBackedWriteStream wrapLongArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue Slice<long[]> arraySlice)
	{
		return wrapLongArrayAsVariableLengthWriteStream(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static LongListBackedWriteStream wrapLongArrayAsVariableLengthWriteStream(@SnapshotValue @ReadonlyValue long[] array, int offset, int length)
	{
		return new LongListBackedWriteStream(PrimitiveCollections.longArrayAsMutableList(array, offset, length));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class LongListBackedReadStream
	extends AbstractStream
	implements LongBlockReadStream, LengthAwareStream, SeekableStream
	{
		protected LongList underlying;
		protected int cursor = 0;
		
		public LongListBackedReadStream(@LiveValue @Nonnull LongList underlying)
		{
			this.underlying = requireNonNull(underlying);
		}
		
		@ImplementationTransparency
		public LongList getUnderlying()
		{
			return this.underlying;
		}
		
		
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor >= this.underlying.size();
		}
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			super.requireOpen();
			
			int amt = (int)Math.min(amount, this.underlying.size() - this.cursor);
			this.cursor += amt;
			return amt;
		}
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (isEOF())
				throw new EOFException();
			
			long v = this.underlying.getLong(this.cursor);
			this.cursor++;
			return v;
		}
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			int realLength = Math.min(length, this.underlying.size() - this.cursor);
			
			this.underlying.getAllLongs(this.cursor, buffer, offset, realLength);
			
			this.cursor += realLength;
			
			return realLength;
		}
		
		
		@Override
		public void setCursor(long position) throws IllegalArgumentException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (position < 0)
				throw new IllegalArgumentException();
			
			this.cursor = safeCastS64toS32(position);
		}
		
		@Override
		public long getCursor() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor;
		}
		
		@Override
		public long getLength() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.underlying.size();
		}
		
		@Override
		protected void close0() throws IOException
		{
			//Be nice to the garbage collector :3
			this.underlying = null;
		}
	}
	
	
	
	
	
	
	
	
	public static class LongListBackedWriteStream
	extends AbstractStream
	implements IndolentLongBlockWriteStream, LengthMutableWriteStream, SeekableStream
	{
		protected LongList underlying;
		protected int cursor = 0;
		protected boolean autoExtendIfPossible;
		
		public LongListBackedWriteStream(@LiveValue @Nonnull LongList underlying)
		{
			this.underlying = requireNonNull(underlying);
			this.autoExtendIfPossible = isUnderlyingDefinitelyOrPossiblyVariableLength();  //Must come after above line! XD
		}
		
		@ImplementationTransparency
		public LongList getUnderlying()
		{
			return this.underlying;
		}
		
		@ThrowAwayValue
		public long[] toLongArraySnapshotting()
		{
			return getUnderlying().toLongArray();
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public long[] toLongArrayPossiblyLive()
		{
			return getUnderlying().toLongArrayPossiblyLive();
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public Slice<long[]> toLongArraySlicePossiblyLive()
		{
			return getUnderlying().toLongArraySlicePossiblyLive();
		}
		
		
		
		
		protected boolean isUnderlyingDefinitelyOrPossiblyVariableLength()
		{
			return Primitives.isFalseOrNull(PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.underlying));
		}
		
		@Override
		public boolean isAutoExtend() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.autoExtendIfPossible ? isUnderlyingDefinitelyOrPossiblyVariableLength() : false;
		}
		
		@Override
		public boolean supportsSettingToAutoExtendValue(boolean value) throws ClosedStreamException
		{
			super.requireOpen();
			
			if (value == false)
				return true;
			else
				return isUnderlyingDefinitelyOrPossiblyVariableLength();
		}
		
		@Override
		public void setAutoExtend(boolean autoExtend) throws ClosedStreamException, UnsupportedOperationException
		{
			super.requireOpen();
			
			if (autoExtend == false)
			{
				this.autoExtendIfPossible = false;
			}
			else
			{
				if (!isUnderlyingDefinitelyOrPossiblyVariableLength())
					throw new UnsupportedAddressTypeException();
				
				this.autoExtendIfPossible = true;
			}
		}
		
		
		
		
		@Override
		public void flush() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (this.underlying instanceof Flushable)
			{
				((Flushable)this.underlying).flush();
			}
		}
		
		
		
		@Override
		public void setLength(long value) throws IllegalLengthException, ClosedStreamException, IOException
		{
			super.requireOpen();
			
			if (!isAutoExtend())
				throw new UnsupportedOperationException();
			
			IllegalLengthException.checkLength(0, Integer.MAX_VALUE, value);
			
			CollectionUtilities.setListSize(this.underlying, (int)value);
		}
		
		
		
		
		
		
		
		
		
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return !isAutoExtend() && this.cursor >= this.underlying.size();
		}
		
		@Override
		public long skipIndolent(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			//We don't need to be indolent here :3
			return skip(amount);
		}
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			super.requireOpen();
			
			int amt = (int)Math.min(amount, this.underlying.size() - this.cursor);
			this.cursor += amt;
			return amt;
		}
		
		
		
		@Override
		public void write(long unit) throws EOFException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (isEOF())
				throw new EOFException();
			
			if (isAutoExtend() && this.cursor == this.underlying.size())
			{
				this.underlying.addLong(unit);
			}
			else
			{
				this.underlying.setLong(this.cursor, unit);
			}
			
			this.cursor++;
		}
		
		@Override
		public int writeIndolent(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			int realLength;
			
			if (isAutoExtend() && this.cursor == this.underlying.size())
			{
				this.underlying.addAllLongs(buffer, offset, length);
				realLength = length;
			}
			else
			{
				realLength = Math.min(length, this.underlying.size() - this.cursor);
				
				this.underlying.setAllLongs(this.cursor, buffer, offset, realLength);
			}
			
			this.cursor += realLength;
			
			return realLength;
		}
		
		
		@Override
		public void setCursor(long position) throws IllegalArgumentException, IOException, ClosedStreamException
		{
			super.requireOpen();
			
			if (position < 0)
				throw new IllegalArgumentException();
			
			this.cursor = safeCastS64toS32(position);
		}
		
		@Override
		public long getCursor() throws IOException, ClosedStreamException
		{
			super.requireOpen();
			
			return this.cursor;
		}
		
		@Override
		public long getLength() throws ClosedStreamException
		{
			super.requireOpen();
			
			return this.underlying.size();
		}
		
		@Override
		protected void close0() throws IOException
		{
			//Be nice to the garbage collector :3
			this.underlying = null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//Oldddddddddd cooooooodeeeeee from before PrimitiveCollections was awesome XD        (It had a bug relating to autoextending and eof'ing and implementing both Read and Write streams in the auto-extending classes! X'D )
	/* << <
	primxp
	

	
	
	
	
	
	
	
	
	
	/**
	 * Use this to prevent modifications to the array through the streaming interface.
	 * @author RProgrammer
	 ⎋a/
	public static class ReadOnlyArray_$$Prim$$_BlockReadStream
	extends AbstractStream
	implements _$$Prim$$_BlockReadStream, LengthAwareStream, SeekableStream
	{
		//<Specific
		protected _$$prim$$_[] data;
		protected int dataOffset, dataLength;
		protected int cursor;
		
		public ReadOnlyArray_$$Prim$$_BlockReadStream()
		{
			super();
		}
		
		public ReadOnlyArray_$$Prim$$_BlockReadStream(_$$prim$$_[] data)
		{
			super();
			setData(data);
		}
		
		public ReadOnlyArray_$$Prim$$_BlockReadStream(_$$prim$$_[] data, int offset, int length)
		{
			super();
			setData(data, offset, length);
		}
		
		public ReadOnlyArray_$$Prim$$_BlockReadStream(Slice<_$$prim$$_[]> data)
		{
			this(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		
		
		public _$$prim$$_[] getData()
		{
			return this.data;
		}
		
		public int getDataOffset()
		{
			return this.dataOffset;
		}
		
		public int getDataLength()
		{
			return this.dataLength;
		}
		
		/**
	 * Note: calling this method resets the cursor.
		 ⎋a/
		public void setData(_$$prim$$_[] data)
		{
			setData(data, 0, data.length);
		}
		
		/**
	 * Note: calling this method resets the cursor.
		 ⎋a/
		public void setData(_$$prim$$_[] data, int offset, int length) throws IllegalArgumentException
		{
			if (data == null)
				throw new NullPointerException();
			if (offset < 0 || offset + length > data.length)
				throw new IllegalArgumentException("Provided offset is invalid.");
			
			this.data = data;
			this.dataOffset = offset;
			this.dataLength = length;
			
			this.cursor = 0;
		}
		//Specific>
		
		
		
		
		
		@Override
		protected void close0() throws IOException
		{
		}
		
		public _$$prim$$_ read() throws EOFException, IOException, ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
			if (cursor >= dataLength)
				throw new EOFException();
			
			_$$prim$$_ unit = data[dataOffset + cursor];
			cursor++;
			return unit;
		}
		
		
		public int read(_$$prim$$_[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
			if (length < 0)
				throw new IllegalArgumentException("Provided length is negative.");
			
			int minlen = length;
			if (cursor + length >= dataLength)
				minlen = dataLength - cursor;
			
			if (minlen > 0)
			{
				System.arraycopy(data, dataOffset+cursor, buffer, offset, minlen);
				cursor += minlen;
			}
			
			return minlen;
		}
		
		
		public long skip(long amount) throws IOException, ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
			if (amount < 0)
				throw new IllegalArgumentException("Provided length is negative.");
			
			int minlen = 0;
			if (amount > Integer.MAX_VALUE || cursor + amount >= dataLength)
				minlen = dataLength - cursor;
			else
				minlen = (int)amount;
			
			cursor += minlen;
			
			return minlen;
		}
		
		public void setCursor(long position) throws IllegalArgumentException, IOException, ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
			if (position < 0)
				throw new IllegalArgumentException("Provided cursor is negative.");
			
			if (position > Integer.MAX_VALUE || position > dataLength)
				cursor = dataLength;
			else
				cursor = (int)position;
		}
		
		public void resetCursor() throws IOException, ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
			
			cursor = 0;
		}
		
		
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
			return cursor >= dataLength;
		}
		
		public long getLength() throws ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
			return dataLength;
		}
		
		public long getCursor() throws IOException, ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
			return cursor;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * If you have an array and want to present a stream-based interface to it, this class will do that for you.
	 * Since the array is provided, only it is used, meaning that writing must stop once the end is reached (unlike _$$Prim$$_ArrayOutputStream)
	 * @author RProgrammer
	 ⎋a/
	public static class Array_$$Prim$$_Stream
	extends ReadOnlyArray_$$Prim$$_BlockReadStream
	implements _$$Prim$$_BlockWriteStream, PreservingSkipWriteStream
	{
		//<Specific
		public Array_$$Prim$$_Stream()
		{
			super();
		}
		
		public Array_$$Prim$$_Stream(_$$prim$$_[] data)
		{
			super(data);
		}
		
		public Array_$$Prim$$_Stream(_$$prim$$_[] data, int offset, int length)
		{
			super(data, offset, length);
		}
		
		public Array_$$Prim$$_Stream(Slice<_$$prim$$_[]> data)
		{
			this(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		//Specific>
		
		
		
		
		
		public void write(_$$prim$$_ unit) throws EOFException, IOException, ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
			
			if (cursor >= dataLength)
				throw new EOFException();
			
			data[dataOffset + cursor] = unit;
			cursor++;
		}
		
		public int write(_$$prim$$_[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
			
			if (length < 0)
				throw new IllegalArgumentException("Provided length is negative.");
			
			int minlen = length;
			if (cursor + length >= dataLength)
				minlen = dataLength - cursor;
			
			if (minlen > 0)
			{
				System.arraycopy(buffer, offset, data, dataOffset+cursor, minlen);
				cursor += minlen;
			}
			
			return minlen;
		}
		
		public void flush() throws IOException, ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
		}
	}
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	/**
	 * An array-backed stream which can reallocate the array to extend the backing as needed!  (Manually with a standard setLength() call and/or Automatically with our auto-extend setting set to true)
	 ⎋a/
	public static class ExtendingArray_$$Prim$$_Stream
	extends AbstractStream
	implements _$$Prim$$_BlockReadStream, LengthAwareStream, SeekableStream,  _$$Prim$$_BlockWriteStream,  LengthMutableWriteStream
	{
		//<Specific
		protected _$$prim$$_[] data;
		protected int dataLength = 0;
		protected int cursor = 0;
		
		protected boolean autoExtend = true;
		
		
		public ExtendingArray_$$Prim$$_Stream()
		{
			super();
			data = new _$$prim$$_[32];
		}
		
		public ExtendingArray_$$Prim$$_Stream(@SnapshotValue _$$prim$$_[] data)
		{
			super();
			setData(data);
		}
		
		public ExtendingArray_$$Prim$$_Stream(@SnapshotValue _$$prim$$_[] data, int offset, int length)
		{
			super();
			setData(data, offset, length);
		}
		
		public ExtendingArray_$$Prim$$_Stream(Slice<_$$prim$$_[]> data)
		{
			this(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		
		
		/**
	 * This provides the actual array that is currently used as the store.
	 * Be careful, though, as writes and {@link #setLength(long)} can cause a new array to be created.
		 ⎋a/
		@LiveValue
		public _$$prim$$_[] getCurrentLiveDataBuffer()
		{
			return this.data;
		}
		
		/**
	 * This is exactly equivalent to (int){@link #getLength()}  :3
		 ⎋a/
		public int getCurrentLiveDataBufferLength()
		{
			return this.dataLength;
		}
		
		
		/**
	 * packages a copy of the data in the underlying buffer.
		 ⎋a/
		@ThrowAwayValue
		public _$$prim$$_[] getData()
		{
			_$$prim$$_[] newdata = new _$$prim$$_[dataLength];
			System.arraycopy(data, 0, newdata, 0, dataLength);
			return newdata;
		}
		
		/**
	 * copies the data in the underlying buffer.
		 ⎋a/
		public void getData(@ReadonlyValue _$$prim$$_[] buffer, int offset)
		{
			System.arraycopy(data, 0, buffer, offset, dataLength);
		}
		
		
		
		/**
	 * Note: calling this method resets the cursor.
		 ⎋a/
		public void setData(@SnapshotValue _$$prim$$_[] data)
		{
			setData(data, 0, data.length);
		}
		
		/**
	 * replaces the store <i>contents</i> with the given data.
	 * The passed array instance is never used; a new array may be created for the {@link #getCurrentLiveDataBuffer() store buffer} or the current one used,
	 * but this does NOT replace it!!!
	 * 
	 * Note: calling this method resets the cursor.
		 ⎋a/
		public void setData(@SnapshotValue _$$prim$$_[] buffer, int offset, int length) throws IllegalArgumentException
		{
			if (buffer == null)
				throw new NullPointerException();
			if (offset < 0 || offset + length > buffer.length)
				throw new IllegalArgumentException("Provided offset is invalid.");
			
			if (data == null || data.length < length)
				data = new _$$prim$$_[length];
			
			System.arraycopy(buffer, offset, data, 0, length);
			dataLength = length;
			
			this.cursor = 0;
		}
		
		
		//Do not touch dataLength, only data.length
		protected void ensureLength(int length)
		{
			if (data.length < length)
			{
				int pct = (int)(data.length * DefaultExponentialAutoExtendFactor);
				_$$prim$$_[] newbuffer = new _$$prim$$_[length < pct ? pct : length];
				System.arraycopy(data, 0, newbuffer, 0, dataLength);
				data = newbuffer;
			}
		}
		//Specific>
		
		
		
		
		
		
		
		@Override
		protected void close0() throws IOException
		{
		}
		
		public _$$prim$$_ read() throws EOFException, IOException, ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
			if (cursor >= dataLength)
				throw new EOFException();
			
			_$$prim$$_ unit = data[cursor];
			cursor++;
			return unit;
		}
		
		public int read(_$$prim$$_[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
			if (length < 0)
				throw new IllegalArgumentException("Provided length is negative.");
			
			int minlen = length;
			if (cursor + length >= dataLength)
				minlen = dataLength - cursor;
			
			if (minlen > 0) //minlen could be negative
			{
				System.arraycopy(data, cursor, buffer, offset, minlen);
				cursor += minlen;
			}
			
			return minlen;
		}
		
		public void resetCursor() throws IOException, ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
			
			cursor = 0;
		}
		
		
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
			return !autoExtend && cursor >= dataLength;
		}
		
		public long getLength() throws ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
			return dataLength;
		}
		
		public long getCursor() throws IOException, ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
			return cursor;
		}
		
		
		
		
		public void write(_$$prim$$_ unit) throws EOFException, IOException, ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
			
			if (cursor >= dataLength)
			{
				if (autoExtend)
				{
					ensureLength(cursor+1);
					dataLength = cursor+1;
				}
				else
				{
					throw new EOFException();
				}
			}
			
			data[cursor] = unit;
			cursor++;
		}
		
		
		public int write(_$$prim$$_[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
			
			if (length < 0)
				throw new IllegalArgumentException("Provided length is negative.");
			
			if (autoExtend)
			{
				ensureLength(cursor + length);
				System.arraycopy(buffer, offset, data, cursor, length);
				this.cursor += length;
				this.dataLength += length;
				return length;
			}
			else
			{
				int minlen = length;
				if (cursor + length >= dataLength)
					minlen = dataLength - cursor;
				
				if (minlen > 0)
				{
					System.arraycopy(buffer, offset, data, cursor, minlen);
					cursor += minlen;
				}
				
				return minlen;
			}
		}
		
		
		
		
		public long skip(long amount) throws IOException, ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
			if (amount < 0)
				throw new IllegalArgumentException("Provided length is negative.");
			
			int minlen = 0;
			if (amount > Integer.MAX_VALUE || cursor + amount >= dataLength)
				minlen = dataLength - cursor;
			else
				minlen = (int)amount;
			
			cursor += minlen;
			
			return minlen;
		}
		
		public void setCursor(long position) throws IllegalArgumentException, IOException, ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
			if (position < 0)
				throw new IllegalArgumentException("Provided cursor is negative.");
			
			if (position > Integer.MAX_VALUE || position > dataLength)
				cursor = dataLength;
			else
				cursor = (int)position;
		}
		
		public void setLength(long value) throws IllegalLengthException, ClosedStreamException, IOException
		{
			IllegalLengthException.checkLength(0, Integer.MAX_VALUE, value);
			int v = (int)value;
			ensureLength(v);
			dataLength = v;
		}
		
		
		
		
		
		
		public boolean isAutoExtend() throws ClosedStreamException
		{
			return autoExtend;
		}
		
		public void setAutoExtend(boolean autoExtend) throws ClosedStreamException
		{
			this.autoExtend = autoExtend;
		}
		
		public void flush() throws IOException, ClosedStreamException
		{
			if (closed)
				throw new ClosedStreamException();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	 */
	
	//>>>
}
