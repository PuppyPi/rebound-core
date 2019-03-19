package rebound.util.collections.prim;

import static java.util.Objects.*;
import static rebound.bits.Unsigned.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.util.BasicExceptionUtilities.*;
import static rebound.util.Primitives.*;
import static rebound.util.collections.ArrayUtilities.*;
import static rebound.util.collections.CollectionUtilities.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.hints.IntendedToBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.hints.IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.hints.IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.SignalInterface;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.annotations.semantic.temporal.ImmutableValue;
import rebound.annotations.semantic.temporal.PossiblySnapshotPossiblyLiveValue;
import rebound.concurrency.immutability.StaticallyConcurrentlyImmutable;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.NotYetImplementedException;
import rebound.exceptions.OverflowException;
import rebound.exceptions.ReadonlyUnsupportedOperationException;
import rebound.exceptions.StopIterationReturnPath;
import rebound.exceptions.StructuredClassCastException;
import rebound.util.collections.AbstractReadonlyList;
import rebound.util.collections.AbstractReadonlySet;
import rebound.util.collections.ArrayUtilities;
import rebound.util.collections.KnowsLengthFixedness;
import rebound.util.collections.ListWithRemoveRange;
import rebound.util.collections.ListWithSetAll;
import rebound.util.collections.ListWithSetSize;
import rebound.util.collections.NiceList;
import rebound.util.collections.PolymorphicCollectionUtilities;
import rebound.util.collections.ShiftableList;
import rebound.util.collections.SimpleIterator;
import rebound.util.collections.SimpleIterator.SimpleIterable;
import rebound.util.collections.Slice;
import rebound.util.collections.SortingUtilities;
import rebound.util.collections.Sublist;
import rebound.util.collections.TransparentContiguousArrayBackedCollection;
import rebound.util.growth.Grower.GrowerComputationallyUnreducedPurelyRecursive;
import rebound.util.growth.TranslatedExponentialGrower;
import rebound.util.objectutil.PubliclyCloneable;
import rebound.util.objectutil.Trimmable;
import rebound.util.objectutil.UnderlyingInstanceAccessible;

//Todo primitive iterators with remove(), then we can add a default retainAll() into the collection interface!  :D

//TODO TEST THEM ALL! X'D :D

public class PrimitiveCollections
{
	@LiveValue
	public static List asListObjectOrPrimitiveArray(Object array)
	{
		if (array instanceof Object[])
			return Arrays.asList(array);
		
		
		/* <<<
		primxp
		
		else if (array instanceof _$$prim$$_[])
			return _$$prim$$_ArrayAsList((_$$prim$$_[])array);
		 */
		
		else if (array instanceof boolean[])
			return booleanArrayAsList((boolean[])array);
		
		else if (array instanceof byte[])
			return byteArrayAsList((byte[])array);
		
		else if (array instanceof char[])
			return charArrayAsList((char[])array);
		
		else if (array instanceof short[])
			return shortArrayAsList((short[])array);
		
		else if (array instanceof float[])
			return floatArrayAsList((float[])array);
		
		else if (array instanceof int[])
			return intArrayAsList((int[])array);
		
		else if (array instanceof double[])
			return doubleArrayAsList((double[])array);
		
		else if (array instanceof long[])
			return longArrayAsList((long[])array);
		
		// >>>
		
		
		else
			throw newClassCastExceptionOrNullPointerException(array);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static final int DefaultPrimitiveArrayListInitialCapacity = 16;
	
	
	public static @ImmutableValue GrowerComputationallyUnreducedPurelyRecursive defaultPrimitiveArrayListGrower(int initialCapacity)
	{
		return new TranslatedExponentialGrower(11, 10, initialCapacity);
	}
	
	
	
	
	
	
	public static BooleanList newDefaultBooleanList()
	{
		return new BitSetBackedBooleanList();
	}
	
	public static BooleanList newDefaultBooleanListPresizedZeros(int size)
	{
		return new BitSetBackedBooleanList(new BitSet(), size);
	}
	
	
	
	
	
	
	
	@SignalInterface
	public static interface PrimitiveCollection<T>
	extends Collection<T>
	{
	}
	
	
	
	
	
	
	@SignalInterface
	public static interface BooleanListWithByteListConversion  //Todo move these into NonuniformMethodsForBooleanList ^^'
	extends BooleanList
	{
		@PossiblySnapshotPossiblyLiveValue
		public ByteList byteList();
		
		@ThrowAwayValue
		public ByteList toNewByteList();
	}
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static ByteList booleanListToByteList(BooleanList bits)
	{
		if (bits instanceof BooleanListWithByteListConversion)
			return ((BooleanListWithByteListConversion)bits).byteList();
		else
			return byteArrayAsList(defaultBooleanListToNewByteArray(bits));
	}
	
	
	@ThrowAwayValue
	public static ByteList booleanListToNewByteList(BooleanList bits)
	{
		if (bits instanceof BooleanListWithByteListConversion)
			return ((BooleanListWithByteListConversion)bits).toNewByteList();
		else
			return byteArrayAsList(defaultBooleanListToNewByteArray(bits));
	}
	
	
	@ThrowAwayValue
	public static byte[] defaultBooleanListToNewByteArray(BooleanList bits)
	{
		int nBits = bits.size();
		int nBytes = ceilingDivision(nBits, 8);
		int lastByteLength = nBits - ((nBytes - 1) * 8);
		
		byte[] bytes = new byte[nBytes];
		
		for (int i = 0; i < nBytes; i++)
		{
			byte b;
			{
				final int base = i * 8;
				
				int nb = (i == nBytes - 1) ? lastByteLength : 8;
				
				b = 0;
				for (int j = 0; j < nb; j++)
				{
					b |= (bits.getBoolean(base+j) ? 1 : 0) << j;
				}
			}
			
			bytes[i] = b;
		}
		
		return bytes;
	}
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static Slice<byte[]> booleanListToByteArray(BooleanList bits)
	{
		return booleanListToByteList(bits).toByteArraySlicePossiblyLive();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static BooleanList byteListToBooleanList(List<Byte> bytes)
	{
		if (TransparentContiguousArrayBackedCollection.is(bytes))
		{
			Slice s = ((TransparentContiguousArrayBackedCollection)bytes).getLiveContiguousArrayBackingUNSAFE();
			Object u = s.getUnderlying();
			
			if (u instanceof byte[])
			{
				return byteArrayToBooleanList(s);
			}
		}
		
		//Todo a better fallback algorithm than this?  ^^'
		return byteArrayToBooleanList(PolymorphicCollectionUtilities.anyToArrayByte(bytes));
	}
	
	
	
	
	public static BooleanList byteArrayToBooleanList(Slice<byte[]> bytes)
	{
		return BitSetBackedBooleanList.newFromBytesArraySlice(bytes);
	}
	
	public static BooleanList byteArrayToBooleanList(byte[] bytes)
	{
		return BitSetBackedBooleanList.newFromBytesArray(bytes);
	}
	
	public static BooleanList byteArrayToBooleanList(byte[] bytes, int offset, int length)
	{
		return BitSetBackedBooleanList.newFromBytesArraySlice(new Slice<byte[]>(bytes, offset, length));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@ImplementationTransparency  //This only exists to keep the bulk functions exactly uniform and correspondent :3     This might be removed soon XD''
	public static interface NonuniformMethodsForByteList
	extends List<Byte>
	{
		
	}
	
	@ImplementationTransparency  //This only exists to keep the bulk functions exactly uniform and correspondent :3     This might be removed soon XD''
	public static interface NonuniformMethodsForCharacterList
	extends List<Character>
	{
		
	}
	
	@ImplementationTransparency  //This only exists to keep the bulk functions exactly uniform and correspondent :3     This might be removed soon XD''
	public static interface NonuniformMethodsForShortList
	extends List<Short>
	{
		
	}
	
	@ImplementationTransparency  //This only exists to keep the bulk functions exactly uniform and correspondent :3     This might be removed soon XD''
	public static interface NonuniformMethodsForFloatList
	extends List<Float>
	{
		
	}
	
	@ImplementationTransparency  //This only exists to keep the bulk functions exactly uniform and correspondent :3     This might be removed soon XD''
	public static interface NonuniformMethodsForIntegerList
	extends List<Integer>
	{
		
	}
	
	@ImplementationTransparency  //This only exists to keep the bulk functions exactly uniform and correspondent :3     This might be removed soon XD''
	public static interface NonuniformMethodsForDoubleList
	extends List<Double>
	{
		
	}
	
	@ImplementationTransparency  //This only exists to keep the bulk functions exactly uniform and correspondent :3     This might be removed soon XD''
	public static interface NonuniformMethodsForLongList
	extends List<Long>
	{
		
	}
	
	
	
	/* << <   Disabled X3
primxp
	
	@ImplementationTransparency  //This only exists to keep the bulk functions exactly uniform and correspondent :3     This might be removed soon XD''
	public static interface NonuniformMethodsFor_$$Primitive$$_List
	extends List<_$$Primitive$$_>
	{
		
	}
	 */
	
	//>> >
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
primxp
	
	
	public static _$$prim$$_ get_$$Prim$$_(List<_$$Primitive$$_> list, int index)
	{
		if (list instanceof _$$Primitive$$_List)
			return ((_$$Primitive$$_List) list).get_$$Prim$$_(index);
		else
			return list.get(index);
	}
	
	public static void set_$$Prim$$_(List<_$$Primitive$$_> list, int index, _$$prim$$_ value)
	{
		if (list instanceof _$$Primitive$$_List)
			((_$$Primitive$$_List) list).set_$$Prim$$_(index, value);
		else
			list.set(index, value);
	}
	
	
	
	
	
	
	
	public static interface Simple_$$Primitive$$_Iterable
	extends SimpleIterable<_$$Primitive$$_>
	{
		public Simple_$$Primitive$$_Iterator newSimple_$$Primitive$$_Iterator();
		
		public default SimpleIterator<_$$Primitive$$_> simpleIterator()
		{
			Simple_$$Primitive$$_Iterator i = this.newSimple_$$Primitive$$_Iterator();
			return () -> i.nextrp_$$Prim$$_();
		}
	}
	
	
	public static interface Simple_$$Primitive$$_Iterator
	extends SimpleIterator<_$$Primitive$$_>
	{
		public _$$prim$$_ nextrp_$$Prim$$_() throws StopIterationReturnPath;
		
		
		@Override
		public default _$$Primitive$$_ nextrp() throws StopIterationReturnPath
		{
			return nextrp_$$Prim$$_();
		}
	}
	
	
	
	@SignalInterface
	public static interface _$$Primitive$$_Collection
	extends PrimitiveCollection<_$$Primitive$$_>, Simple_$$Primitive$$_Iterable
	{
		public boolean add_$$Prim$$_(_$$prim$$_ value);
		
		public boolean remove_$$Prim$$_(_$$prim$$_ value);
		
		
		
		
		
		@ThrowAwayValue
		public static _$$prim$$_[] defaultTo_$$Prim$$_Array(_$$Primitive$$_Collection collection)
		{
			_$$prim$$_[] array = new _$$prim$$_[collection.size()];
			Simple_$$Primitive$$_Iterator i = collection.newSimple_$$Primitive$$_Iterator();
			
			int index = 0;
			while (true)
			{
				try
				{
					array[index] = i.nextrp_$$Prim$$_();
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
				
				index++;
			}
			
			return array;
		}
		
		@ThrowAwayValue
		public default _$$prim$$_[] to_$$Prim$$_Array()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(this.size(), u);
				
				if (u.getUnderlying() instanceof _$$prim$$_[])
				{
					return sliceToNew_$$Prim$$_ArrayOP((Slice<_$$prim$$_[]>) u);
				}
			}
			
			return defaultTo_$$Prim$$_Array(this);
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public default _$$prim$$_[] to_$$Prim$$_ArrayPossiblyLive()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				int size = this.size();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				Object und = u.getUnderlying();
				
				if (und instanceof _$$prim$$_[] && u.getOffset() == 0 && ((_$$prim$$_[])und).length == size)
				{
					return (_$$prim$$_[])und;
				}
			}
			
			return defaultTo_$$Prim$$_Array(this);
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public default Slice<_$$prim$$_[]> to_$$Prim$$_ArraySlicePossiblyLive()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(this.size(), u);
				
				if (u.getUnderlying() instanceof _$$prim$$_[])
				{
					return (Slice<_$$prim$$_[]>) u;
				}
			}
			
			return wholeArraySlice_$$Prim$$_(defaultTo_$$Prim$$_Array(this));
		}
		
		
		
		
		
		
		public default boolean contains_$$Prim$$_(_$$prim$$_ value)
		{
			Simple_$$Primitive$$_Iterator i = newSimple_$$Primitive$$_Iterator();
			
			while (true)
			{
				try
				{
					if (eqSane(i.nextrp_$$Prim$$_(), value))
						return true;
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
			}
			
			return false;
		}
		
		
		
		
		
		
		@Override
		public default Iterator<_$$Primitive$$_> iterator()
		{
			return Simple_$$Primitive$$_Iterable.super.iterator();
		}
		
		@Override
		public default boolean isEmpty()
		{
			return size() == 0;
		}
		
		
		
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAll_$$Prim$$_s(_$$prim$$_[] array)
		{
			return addAll_$$Prim$$_s(array, 0, array.length);
		}
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAll_$$Prim$$_s(Slice<_$$prim$$_[]> arraySlice)
		{
			return addAll_$$Prim$$_s(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAll_$$Prim$$_s(_$$prim$$_[] elements, int offset, int length)
		{
			return defaultAddAll_$$Prim$$_s(this, elements, offset, length);
		}
		
		public static boolean defaultAddAll_$$Prim$$_s(_$$Primitive$$_Collection self, _$$prim$$_[] elements, int offset, int length)
		{
			boolean changedAtAll = false;
			int e = offset + length;
			for (int i = offset; i < e; i++)
				changedAtAll |= self.add_$$Prim$$_(elements[i]);
			return changedAtAll;
		}
		
		
		
		
		public default void removeAll_$$Prim$$_s(_$$prim$$_[] array)
		{
			removeAll_$$Prim$$_s(array, 0, array.length);
		}
		
		public default void removeAll_$$Prim$$_s(Slice<_$$prim$$_[]> arraySlice)
		{
			removeAll_$$Prim$$_s(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public default void removeAll_$$Prim$$_s(_$$prim$$_[] a, int offset, int length)
		{
			int e = offset + length;
			for (int i = offset; i < e; i++)
				this.remove_$$Prim$$_(a[i]);
		}
		
		
		
		
		
		
		
		
		
		
		@Override
		public default boolean contains(Object o)
		{
			return o instanceof _$$Primitive$$_ && contains_$$Prim$$_((_$$Primitive$$_)o);
		}
		
		
		@Override
		public default boolean add(_$$Primitive$$_ e)
		{
			return add_$$Prim$$_(e);
		}
		
		
		@Override
		public default boolean remove(Object o)
		{
			return o instanceof _$$Primitive$$_ && remove_$$Prim$$_((_$$Primitive$$_)o);
		}
		
		
		@Override
		public default boolean containsAll(Collection<?> c)
		{
			if (c instanceof _$$Primitive$$_Collection)
			{
				_$$Primitive$$_Collection cc = (_$$Primitive$$_Collection) c;
				
				Simple_$$Primitive$$_Iterator i = cc.newSimple_$$Primitive$$_Iterator();
				while (true)
				{
					try
					{
						if (this.contains_$$Prim$$_(i.nextrp_$$Prim$$_()))
							return true;
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return false;
			}
			else
			{
				for (Object e : c)
					if (this.contains(e))
						return true;
				return false;
			}
		}
		
		
		@Override
		public default boolean addAll(Collection<? extends _$$Primitive$$_> c)
		{
			if (c instanceof _$$Primitive$$_Collection)
			{
				boolean changedAtAll = false;
				
				Simple_$$Primitive$$_Iterator i = ((_$$Primitive$$_Collection)c).newSimple_$$Primitive$$_Iterator();
				
				while (true)
				{
					try
					{
						changedAtAll |= this.add(i.nextrp_$$Prim$$_());
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (_$$Primitive$$_ e : c)
				{
					changedAtAll |= this.add(e);
				}
				
				return changedAtAll;
			}
		}



		@Override
		public default boolean removeAll(Collection<?> c)
		{
			if (c instanceof _$$Primitive$$_Collection)
			{
				boolean changedAtAll = false;
				
				Simple_$$Primitive$$_Iterator i = ((_$$Primitive$$_Collection)c).newSimple_$$Primitive$$_Iterator();
				
				while (true)
				{
					try
					{
						changedAtAll |= this.remove(i.nextrp_$$Prim$$_());
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (Object e : c)
				{
					changedAtAll |= this.remove(e);
				}
				
				return changedAtAll;
			}
		}
		
		
		
		public default String _toString()
		{
			StringBuilder b = new StringBuilder("[");
			
			Simple_$$Primitive$$_Iterator i = newSimple_$$Primitive$$_Iterator();
			
			boolean first = true;
			
			while (true)
			{
				_$$prim$$_ e;
				
				try
				{
					e = i.nextrp_$$Prim$$_();
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
				
				
				
				//Notice this comes after the possible 'break'!  ^_~
				if (first)
					first = false;
				else
					b.append(", ");
				
				b.append(e);
			}
			
			b.append(']');
			
			return b.toString();
		}
	}
	
	
	
	public static interface DefaultToArrays_$$Primitive$$_Collection
	extends _$$Primitive$$_Collection
	{
		@Override
		public default Object[] toArray()
		{
			Object[] a = new Object[size()];
			storeBoxingIntoArray(wholeArraySliceReference(a));
			return a;
		}
		
		@Override
		public default <T> T[] toArray(T[] a)
		{
			if (a.length >= this.size())
			{
				storeBoxingIntoArray(wholeArraySliceReference(a));
				return a;
			}
			else
			{
				Object[] aa;
				
				if (a.getClass() == _$$Primitive$$_[].class)
				{
					aa = new _$$Primitive$$_[size()];
				}
				else if (a.getClass() == Object[].class)
				{
					aa = new Object[size()];
				}
				else
				{
					throw new StructuredClassCastException(a.getClass());
				}
				
				storeBoxingIntoArray(wholeArraySliceReference(aa));
				
				return (T[])aa;
			}
		}
		
		public void storeBoxingIntoArray(Slice<Object[]> array);
	}
	
	
	
	
	
	
	@SignalInterface
	public static interface _$$Primitive$$_ListRO
	{
		public _$$prim$$_ get_$$Prim$$_(int index);
		
		public int size();
		
		
		
		public default int indexOf_$$Prim$$_(_$$prim$$_ value)
		{
			int n = this.size();
			for (int i = 0; i < n; i++)
				if (eqSane(this.get_$$Prim$$_(i), value))
					return i;
			return -1;
		}
		
		public default int lastIndexOf_$$Prim$$_(_$$prim$$_ value)
		{
			int i = this.size()-1;
			while (i >= 0)
			{
				if (eqSane(this.get_$$Prim$$_(i), value))
					return i;
				break;
			}
			return i;   // ;D
		}
		
		
		
		public default boolean equivalent(List<_$$Primitive$$_> other)
		{
			int size = this.size();
			if (other.size() != size)
				return false;
			
			
			if (other instanceof _$$Primitive$$_ListRO)  //All _$$Primitive$$_Lists will implement this interface too so this accounts for them as well :3
			{
				return equivalentFixedRO((_$$Primitive$$_ListRO)other);
			}
			else
			{
				for (int i = 0; i < size; i++)
				{
					if (!eqSane(this.get_$$Prim$$_(i), other.get(i)._$$prim$$_Value()))
						return false;
				}
			}
			
			return true;
		}
		
		
		
		public default boolean equivalentFixedRO(_$$Primitive$$_ListRO other)
		{
			int size = this.size();
			if (other.size() != size)
				return false;
			
			for (int i = 0; i < size; i++)
			{
				if (!eqSane(this.get_$$Prim$$_(i), other.get_$$Prim$$_(i)))
					return false;
			}
			
			return true;
		}
	}
	
	
	
	@SignalInterface
	public static interface _$$Primitive$$_ListRWFixed
	extends _$$Primitive$$_ListRO
	{
		public void set_$$Prim$$_(int index, _$$prim$$_ value);
	}
	
	
	
	@SignalInterface
	public static interface _$$Primitive$$_List
	extends _$$Primitive$$_Collection, NiceList<_$$Primitive$$_>, _$$Primitive$$_ListRO, _$$Primitive$$_ListRWFixed, ListWithRemoveRange, DefaultToArrays_$$Primitive$$_Collection, PubliclyCloneable, ListWithSetSize<_$$Primitive$$_>, ListWithSetAll, NonuniformMethodsFor_$$Primitive$$_List
	{
		public void insert_$$Prim$$_(int index, _$$prim$$_ value);
		
		
		
		/**
	 * This should be equivalent to just constructing and returning a new in-memory stock implementation with the same contents  (but can easily be faster, especially for those stock impl.s! XD )
	 * As for whether it's fixed-length or read-only or duplicateless or etc., that should be the same in the clone as it is in the main impl.
	 * If *and only if* it is immutable, this is allowed to return this same instance!  (ie the body of this method being <code>return this;</code>)
	 	⎋a/
		public _$$Primitive$$_List clone();
		
		
		/**
	 * If size is smaller than the current, then this is equivalent to {@link #removeRange(int, int) removeRange}(newSize, this.{@link #size() size()})  :3
	 * If size is larger, then this appends newSize - this.{@link #size() size()} elementToAddIfGrowing's to the end like that many {@link #add_$$Prim$$_(_$$prim$$_)}  :D
	 	⎋a/
		public void setSize_$$Prim$$_(int newSize, _$$prim$$_ elementToAddIfGrowing);
		
		public default void setSize(int newSize, _$$Primitive$$_ elementToAddIfGrowing)
		{
			setSize_$$Prim$$_(newSize, elementToAddIfGrowing);
		}
		
		public default void setSize(int newSize)
		{
			setSize_$$Prim$$_(newSize, _$$primdef$$_);
		}
		
		
		
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void setAll_$$Prim$$_s(int index, _$$prim$$_[] array)
		{
			setAll_$$Prim$$_s(index, array, 0, array.length);
		}
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void setAll_$$Prim$$_s(int index, Slice<_$$prim$$_[]> arraySlice)
		{
			setAll_$$Prim$$_s(index, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		public default void setAll_$$Prim$$_s(int start, _$$prim$$_[] array, int offset, int length)
		{
			int size = this.size();
			
			rangeCheckIntervalByLength(size, start, length);
			rangeCheckIntervalByLength(array.length, offset, length);
			
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(size, start, length);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				if (u.getUnderlying() instanceof _$$prim$$_[])
				{
					Slice<_$$prim$$_[]> s = (Slice<_$$prim$$_[]>) u;
					
					System.arraycopy(array, offset, s.getUnderlying(), s.getOffset()+start, length);
					
					return;
				}
			}
			
			
			defaultSetAll_$$Prim$$_s(this, start, array, offset, length);
		}
		
		
		public static void defaultSetAll_$$Prim$$_s(_$$Primitive$$_List list, int start, @WritableValue _$$prim$$_[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				list.set_$$Prim$$_(start + i, array[offset + i]);
		}
		
		
		
		
		
		@Override
		public default void setAll(int destIndex, List sourceU, int sourceIndex, @Nonnegative int amount) throws IndexOutOfBoundsException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			List<_$$Primitive$$_> source = sourceU;
			_$$Primitive$$_List dest = this;
			
			int sourceSize = source.size();
			int destSize = dest.size();
			rangeCheckIntervalByLength(sourceSize, sourceIndex, amount);
			rangeCheckIntervalByLength(destSize, destIndex, amount);
			
			
			
			if (TransparentContiguousArrayBackedCollection.is(source))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection<?>)source).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(sourceSize, sourceIndex, amount);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(sourceSize, u);
				
				if (u.getUnderlying() instanceof _$$prim$$_[])
				{
					Slice<_$$prim$$_[]> s = (Slice<_$$prim$$_[]>)u;
					this.setAll_$$Prim$$_s(destIndex, s.subslice(sourceIndex, amount));
					return;
				}
			}
			
			
			
			
			if (source instanceof _$$Primitive$$_List)
			{
				_$$Primitive$$_List primSource = (_$$Primitive$$_List) source;
				
				if (destIndex < sourceIndex)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
				{
					for (int i = 0; i < amount; i++)
						dest.set_$$Prim$$_(destIndex+i, primSource.get_$$Prim$$_(sourceIndex+i));
				}
				else
				{
					for (int i = amount-1; i >= 0; i--)
						dest.set_$$Prim$$_(destIndex+i, primSource.get_$$Prim$$_(sourceIndex+i));
				}
			}
			else
			{
				if (destIndex < sourceIndex)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
				{
					for (int i = 0; i < amount; i++)
						dest.set(destIndex+i, source.get(sourceIndex+i));
				}
				else
				{
					for (int i = amount-1; i >= 0; i--)
						dest.set(destIndex+i, source.get(sourceIndex+i));
				}
			}
		}
		
		
		
		
		
		
		
		/**
	 * Copies <code>array.length</code> elements into <code>array</code> starting with the <code>start</code>th element.<br>
		 ⎋a/
		public default void getAll_$$Prim$$_s(int start, @WritableValue _$$prim$$_[] array, int offset, int length)
		{
			int size = this.size();
			
			rangeCheckIntervalByLength(size, start, length);
			rangeCheckIntervalByLength(array.length, offset, length);
			
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(size, start, length);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				if (u.getUnderlying() instanceof _$$prim$$_[])
				{
					Slice<_$$prim$$_[]> s = (Slice<_$$prim$$_[]>) u;
					
					System.arraycopy(s.getUnderlying(), s.getOffset()+start, array, offset, length);
					
					return;
				}
			}
			
			
			defaultGetAll_$$Prim$$_s(this, start, array, offset, length);
		}
		
		
		public static void defaultGetAll_$$Prim$$_s(_$$Primitive$$_List list, int start, @WritableValue _$$prim$$_[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				array[offset + i] = list.get_$$Prim$$_(start + i);
		}
		
		
		
		
		
		
		@ThrowAwayValue
		public default _$$prim$$_[] getAll_$$Prim$$_s(int start, int end)
		{
			rangeCheckInterval(this.size(), start, end);
			
			_$$prim$$_[] buff = new _$$prim$$_[end-start];
			getAll_$$Prim$$_s(start, buff, 0, buff.length);
			return buff;
		}
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public default boolean add_$$Prim$$_(_$$prim$$_ value)
		{
			this.insert_$$Prim$$_(this.size(), value);
			return true;
		}
		
		@Override
		public default boolean addAll(Collection<? extends _$$Primitive$$_> c)
		{
			this.addAll(this.size(), c);
			return true;
		}
		
		@Override
		public default boolean addAll_$$Prim$$_s(_$$prim$$_[] elements, int offset, int length)
		{
			insertAll_$$Prim$$_s(this.size(), elements, offset, length);
			return true;
		}
		
		public default void removeByIndex(int index)
		{
			removeRange(index, index+1);
		}
		
		public default _$$prim$$_ remove_$$Prim$$_ByIndex(int index) throws IndexOutOfBoundsException
		{
			_$$prim$$_ v = this.get_$$Prim$$_(index);
			removeByIndex(index);
			return v;
		}
		
		@Override
		public default boolean remove_$$Prim$$_(_$$prim$$_ value)
		{
			int i = this.indexOf_$$Prim$$_(value);
			
			if (i != -1)
			{
				this.removeByIndex(i);
				return true;
			}
			else
			{
				return false;
			}
		}
		
		@Override
		public default boolean retainAll(Collection<?> c)
		{
			if (c instanceof _$$Primitive$$_Collection)
			{
				_$$Primitive$$_Collection cc = (_$$Primitive$$_Collection) c;
				
				boolean changedAtAll = false;
				
				for (int i = size() - 1; i >= 0; i--)
				{
					if (!cc.contains_$$Prim$$_(get_$$Prim$$_(i)))
					{
						remove_$$Prim$$_ByIndex(i);
						changedAtAll = true;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (int i = size() - 1; i >= 0; i--)
				{
					if (!c.contains(get_$$Prim$$_(i)))
					{
						remove_$$Prim$$_ByIndex(i);
						changedAtAll = true;
					}
				}
				
				return changedAtAll;
			}
		}
		
		
		
		
		
		
		
		
		@Override
		public default Simple_$$Primitive$$_Iterator newSimple_$$Primitive$$_Iterator()
		{
			return new Simple_$$Primitive$$_Iterator()
			{
				int index = 0;
				
						public _$$prim$$_ nextrp_$$Prim$$_() throws StopIterationReturnPath
				{
					int i = index;
					
					if (i >= size())
					{
						throw StopIterationReturnPath.I;
					}
					else
					{
						index = i + 1;
						return get_$$Prim$$_(i);
					}
				}
			};
		}
		
		
		
		
		
		@Override
		public default boolean contains_$$Prim$$_(_$$prim$$_ value)
		{
			return indexOf_$$Prim$$_(value) != -1;
		}
		
		
		public default void insertAll_$$Prim$$_s(int index, _$$prim$$_[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				insert_$$Prim$$_(index+i, array[offset+i]);
		}
		
		
		@Override
		public default boolean addAll(int index, Collection<? extends _$$Primitive$$_> c)
		{
			if (c instanceof _$$Primitive$$_Collection)
			{
				_$$Primitive$$_Collection cc = (_$$Primitive$$_Collection)c;
				
				Simple_$$Primitive$$_Iterator i = cc.newSimple_$$Primitive$$_Iterator();
				while (true)
				{
					try
					{
						insert_$$Prim$$_(index, i.nextrp_$$Prim$$_());
						index++;  //doesn't matter if this gets executed on the last iteration :3
					}
					catch (StopIterationReturnPath rp)
					{
						break;
					}
				}
			}
			else
			{
				for (_$$Primitive$$_ e : c)
				{
					insert_$$Prim$$_(index, e);
					index++;
				}
			}
			
			return true;
		}
		
		
		
		public default void insertAll_$$Prim$$_s(int index, _$$prim$$_[] array)
		{
			insertAll_$$Prim$$_s(index, array, 0, array.length);
		}
		
		public default void insertAll_$$Prim$$_s(int index, Slice<_$$prim$$_[]> arraySlice)
		{
			insertAll_$$Prim$$_s(index, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}

		
		
		@Override
		public default _$$Primitive$$_ get(int index)
		{
			return this.get_$$Prim$$_(index);
		}
		
		@Override
		public default _$$Primitive$$_ set(int index, _$$Primitive$$_ value)
		{
			_$$Primitive$$_ previous = get(index);
			this.set_$$Prim$$_(index, value);
			return previous;
		}
		
		
		@Override
		public default void add(int index, _$$Primitive$$_ value)
		{
			this.insert_$$Prim$$_(index, value);
		}
		
		@Override
		public default _$$Primitive$$_ remove(int index) throws IndexOutOfBoundsException
		{
			return this.remove_$$Prim$$_ByIndex(index);
		}
		
		
		@Override
		public default int indexOf(Object o)
		{
			return o instanceof _$$Primitive$$_ ? indexOf_$$Prim$$_((_$$Primitive$$_)o) : -1;
		}
		
		@Override
		public default int lastIndexOf(Object o)
		{
			return o instanceof _$$Primitive$$_ ? lastIndexOf_$$Prim$$_((_$$Primitive$$_)o) : -1;
		}

		
		
		@Override
		public default ListIterator<_$$Primitive$$_> listIterator()
		{
			return listIterator(0);
		}
		
		@Override
		public default ListIterator<_$$Primitive$$_> listIterator(int index)
		{
			//Todo
			throw new NotYetImplementedException();
		}
		
		@Override
		public default _$$Primitive$$_List subList(int fromIndex, int toIndex)   //Also note that we narrow the return types of this :3
		{
			if (fromIndex == 0 && toIndex == this.size())
				return this;
			
			if (this instanceof Sublist)
			{
				Sublist<_$$Primitive$$_> s = (Sublist<_$$Primitive$$_>)this;
				return new _$$Primitive$$_Sublist((_$$Primitive$$_List)s.getUnderlying(), s.getSublistStartingIndex() + fromIndex, toIndex - fromIndex);
			}
			else
			{
				return new _$$Primitive$$_Sublist(this, fromIndex, toIndex - fromIndex);
			}
		}
		
		
		
		
		
		
		
		
		
		
		// Multiple-Inheritance conflict resolvers X'3
		@Override
		public default boolean removeAll(Collection<?> c)
		{
			return DefaultToArrays_$$Primitive$$_Collection.super.removeAll(c);
		}
		
		@Override
		public default boolean remove(Object o)
		{
			return DefaultToArrays_$$Primitive$$_Collection.super.remove(o);
		}
		
		@Override
		public default boolean containsAll(Collection<?> c)
		{
			return DefaultToArrays_$$Primitive$$_Collection.super.containsAll(c);
		}
		
		@Override
		public default boolean add(_$$Primitive$$_ e)
		{
			return DefaultToArrays_$$Primitive$$_Collection.super.add(e);
		}
		
		@Override
		public default boolean isEmpty()
		{
			return DefaultToArrays_$$Primitive$$_Collection.super.isEmpty();
		}
		
		@Override
		public default boolean contains(Object o)
		{
			return DefaultToArrays_$$Primitive$$_Collection.super.contains(o);
		}
		
		
		
		
		
		@Override
		public default Object[] toArray()
		{
			return DefaultToArrays_$$Primitive$$_Collection.super.toArray();
		}
		
		@Override
		public default <T> T[] toArray(T[] a)
		{
			return DefaultToArrays_$$Primitive$$_Collection.super.toArray(a);
		}
		
		
		
		
		
		
		
		@Override
		public default void storeBoxingIntoArray(Slice<Object[]> array)
		{
			int n = this.size();
			for (int i = 0; i < n; i++)
				ArrayUtilities.setReference((Slice)array, i, this.get(i));
		}
		
		
		
		
		
		
		//Narrow the return types of these :3
		
		@Override
		public default _$$Primitive$$_List subListToEnd(int start)
		{
			return (_$$Primitive$$_List)NiceList.super.subListToEnd(start);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
		
		@Override
		public default _$$Primitive$$_List subListFromBeginning(int lengthWhichIsEndExclusiveInThisCaseXD)
		{
			return (_$$Primitive$$_List)NiceList.super.subListFromBeginning(lengthWhichIsEndExclusiveInThisCaseXD);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
		
		@Override
		public default _$$Primitive$$_List subListByLength(int start, int length)
		{
			return (_$$Primitive$$_List)NiceList.super.subListByLength(start, length);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
	}
	
	
	
	
	
	
	public static interface DefaultShiftingBased_$$Primitive$$_List
	extends _$$Primitive$$_List
	{
		public default void clear()
		{
			removeRange(0, size());
		}
		
		@Override
		public default void insert_$$Prim$$_(int index, _$$prim$$_ value)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, 1);
			this.set_$$Prim$$_(index, value);
		}
		
		@Override
		public default void insertAll_$$Prim$$_s(int index, _$$prim$$_[] array, int offset, int length)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, length);
			
			this.setAll_$$Prim$$_s(index, array, offset, length);
		}
		
		
		@Override
		public default boolean addAll(int index, Collection<? extends _$$Primitive$$_> c)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			int amount = c.size();  //copy this incase c == this! XD
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, amount);
			
			if (c instanceof List)
			{
				listcopy((List)c, 0, this, index, amount);
			}
			else
			{
				if (c instanceof _$$Primitive$$_Collection)
				{
					Simple_$$Primitive$$_Iterator iterator = ((_$$Primitive$$_Collection)c).newSimple_$$Primitive$$_Iterator();
					
					int i = 0;
					while (true)
					{
						_$$prim$$_ e;
						try
						{
							e = iterator.nextrp_$$Prim$$_();
						}
						catch (StopIterationReturnPath rp)
						{
							break;
						}
						
						this.set_$$Prim$$_(index+i, e);
						i++;
					}
				}
				else
				{
					int i = 0;
					for (_$$Primitive$$_ e : c)
					{
						this.set(index+i, e);
						i++;
					}
				}
			}
			
			return true;
		}
		
		@Override
		public default void setSize_$$Prim$$_(int newSize, _$$prim$$_ elementToAddIfGrowing)
		{
			int oldSize = this.size();
			int amount = newSize - oldSize;
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, oldSize, amount);
			
			//Growing results in *undefined* contents, not necessarily initialized ones!  (eg, it may already have some/enough elements allocated eg, in an underlying array and it just increases the int size field :3 )
			for (int i = oldSize; i < newSize; i++)
				this.set_$$Prim$$_(i, elementToAddIfGrowing);
		}
		
		
		
		
		@Override
		public default void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			rangeCheckInterval(this.size(), start, pastEnd);
			
			int length = pastEnd - start;
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, pastEnd, -length);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class _$$Primitive$$_Sublist
	implements _$$Primitive$$_List, DefaultShiftingBased_$$Primitive$$_List, Sublist<_$$Primitive$$_>, ShiftableList
	{
		protected final _$$Primitive$$_List underlying;
		protected final int start;
		protected int size;
		
		public _$$Primitive$$_Sublist(_$$Primitive$$_List underlying, int start, int initialSize)
		{
			rangeCheckIntervalByLength(underlying.size(), start, initialSize);
			
			this.underlying = underlying;
			this.start = start;
			this.size = initialSize;
		}
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public _$$prim$$_ get_$$Prim$$_(int index)
		{
			return underlying.get_$$Prim$$_(index + start);
		}
		
		@Override
		public void set_$$Prim$$_(int index, _$$prim$$_ value)
		{
			underlying.set_$$Prim$$_(index + start, value);
		}
		
		@Override
		public void shiftRegionStretchingFromIndexToEndByAmountChangingSize(int start, int amount)
		{
			rangeCheckCursorPoint(size(), start);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(underlying, start, amount);
			
			size += amount;
		}
		
		
		
		@Override
		public _$$Primitive$$_List clone()
		{
			_$$Primitive$$_List c = new _$$Primitive$$_ArrayList();
			c.addAll(this);
			return c;
		}
		
		
		
		@Override
		public _$$Primitive$$_List getUnderlying()
		{
			return underlying;
		}
		
		@Override
		public int getSublistStartingIndex()
		{
			return start;
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class _$$Primitive$$_ArrayList
	implements DefaultShiftingBased_$$Primitive$$_List, ListWithSetSize<_$$Primitive$$_>, Trimmable, TransparentContiguousArrayBackedCollection<_$$prim$$_[]>, KnowsLengthFixedness
	{
		protected int size = 0;
		protected _$$prim$$_[] data;
		protected GrowerComputationallyUnreducedPurelyRecursive grower;
		
		
		
		
		//In case subclasses don't want to deal with the other things ^^'
		protected _$$Primitive$$_ArrayList(Void thisiswhyyoushouldntusejavaconstructorsAlthoughIdontknowhowtomakeinheritanceworkwellwithoutinitmethodsorfactoriesorsomething)
		{
		}
		
		
		/**
	 * Capacity increment is different from Vector's.  It is the percentage of the capacity by which the capacity is increased.
	 * The formula being: <code>newCapacity = capacity + (capacity * capacityIncrement)</code><br>
	 * @param grower This can be anything.  The only downside is performance: all methods will add at least the minimum amount required regardless of what this specifies.
		 ⎋a/
		public _$$Primitive$$_ArrayList(int initialCapacity, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this.data = initialCapacity == 0 ? ArrayUtilities.Empty_$$Prim$$_Array : new _$$prim$$_[initialCapacity];
			this.grower = grower;
		}
		
		public _$$Primitive$$_ArrayList(@LiveValue @WritableValue _$$prim$$_[] LIVEBACKING, int size, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this.data = LIVEBACKING;
			this.grower = grower;
			this.size = size;
		}
		
		public _$$Primitive$$_ArrayList(@SnapshotValue @ReadonlyValue Collection<_$$Primitive$$_> c, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this(c.size(), grower);
			this.addAll(c);
		}
		
		
		
		
		
		public _$$Primitive$$_ArrayList()
		{
			this(DefaultPrimitiveArrayListInitialCapacity);
		}
		
		public _$$Primitive$$_ArrayList(int initialCapacity)
		{
			this(initialCapacity, defaultPrimitiveArrayListGrower(initialCapacity));
		}
		
		public _$$Primitive$$_ArrayList(@SnapshotValue @ReadonlyValue Collection<_$$Primitive$$_> c)
		{
			this(c, defaultPrimitiveArrayListGrower(c.size()));
		}
		
		public _$$Primitive$$_ArrayList(@LiveValue @WritableValue _$$prim$$_[] LIVEBACKING, int size)
		{
			this(LIVEBACKING, size, defaultPrimitiveArrayListGrower(LIVEBACKING.length));
		}
		
		public _$$Primitive$$_ArrayList(@LiveValue @WritableValue _$$prim$$_[] LIVEBACKING)
		{
			this(LIVEBACKING, LIVEBACKING.length);
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return false;
		}
		
		
		
		@Override
		public void setSize(int newSize)
		{
			ensureCapacity(newSize);
			this.size = newSize;
			
			// :D
		}
		
		
		
		
		
		//This pattern is nicer for subclassing in any Java class! :D
		protected void setupClone(_$$Primitive$$_ArrayList freshClone)
		{
			freshClone.size = this.size;
			
			freshClone.data = new _$$prim$$_[size];
			System.arraycopy(this.data, 0, freshClone.data, 0, size);
			
			freshClone.grower = this.grower;
		}
		
		@Override
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public _$$Primitive$$_ArrayList clone()
		{
			_$$Primitive$$_ArrayList clone = new _$$Primitive$$_ArrayList();
			this.setupClone(clone);
			return clone;
		}
		
		
		
		
		
		
		
		
		
		
		//This is what unlocks all the System.arraycopy() magic!  8>
		@ImplementationTransparency
		@LiveValue
		public Slice<_$$prim$$_[]> getLiveContiguousArrayBackingUNSAFE()
		{
			return new Slice(data, 0, size);
		}
		
		
		
		
		
		
		
		
		
		public void set_$$Prim$$_(int index, _$$prim$$_ value)
		{
			rangeCheckMember(this.size, index);
			data[index] = value;
		}
		
		public _$$prim$$_ get_$$Prim$$_(int index)
		{
			rangeCheckMember(this.size(), index);
			return data[index];
		}
		
		
		
		
		
		
		
		@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public void clear()
		{
			size = 0;
		}
		
		@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public int size()
		{
			return size;
		}
		
		@ImplementationTransparency
		public void trimToSize()
		{
			_$$prim$$_[] newdata = new _$$prim$$_[size];
			System.arraycopy(data, 0, newdata, 0, size);
			this.data = newdata;
		}
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.trimToSize();
			return TrimmableTrimRV.DontKeepInvoking;
		}
		
		
		
		
		
		@ImplementationTransparency
		public int getCapacity()
		{
			return data.length;
		}
		
		@ImplementationTransparency
		public void ensureCapacity(int minCapacity)
		{
			if (data.length < minCapacity)
			{
				//Enlarge
				int newCapacity = getNewCapacity(minCapacity);
				_$$prim$$_[] newData = new _$$prim$$_[newCapacity];
				System.arraycopy(data, 0, newData, 0, data.length);
				this.data = newData;
			}
		}
		
		/**
	 * This is also guaranteed not to return a capacity smaller than the provided minimum.
		 ⎋a/
		@ImplementationTransparency
		public int getNewCapacity(int minCapacity)
		{
			if (minCapacity < 0)
				throw new IllegalArgumentException();
			
			return Math.max(minCapacity, grower.getNewSizeRecursive(data.length));  //The Math.max(min, ...) ensures that the grower can't break anything!  :3
		}
		
		
		
		
		
		
		
		/**
	 * sets the underlying array that backs the array list.
	 * Be careful, though, as updates can cause a new array to be created, obsoleting this one.
	 * Note, also, that only up to {@link #size()} elements will be used.
		 ⎋a/
		@ImplementationTransparency
		public void setDirectBuffer(@LiveValue _$$prim$$_[] array)
		{
			if (array.length < size)
				throw new IllegalArgumentException("Array is too small; size="+size+", capacity="+array.length);
			
			this.data = array;
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static class FixedLengthArrayWrapper_$$Primitive$$_List
	implements _$$Primitive$$_List, TransparentContiguousArrayBackedCollection<_$$prim$$_[]>, KnowsLengthFixedness
	{
		protected final _$$prim$$_[] underlying;
		protected final int offset, length;
		
		public FixedLengthArrayWrapper_$$Primitive$$_List(_$$prim$$_[] underlying, int offset, int length)
		{
			rangeCheckIntervalByLength(underlying.length, offset, length);
			
			this.underlying = underlying;
			this.offset = offset;
			this.length = length;
		}
		
		public FixedLengthArrayWrapper_$$Primitive$$_List(_$$prim$$_[] underlying)
		{
			this(underlying, 0, underlying.length);
		}
		
		public FixedLengthArrayWrapper_$$Primitive$$_List(Slice<_$$prim$$_[]> underlying)
		{
			this(underlying.getUnderlying(), underlying.getOffset(), underlying.getLength());
		}
		
		@Override
		public _$$Primitive$$_List clone()
		{
			return new FixedLengthArrayWrapper_$$Primitive$$_List(to_$$Prim$$_Array());
		}
		
		
		
		@Override
		public _$$prim$$_[] to_$$Prim$$_Array()
		{
			return sliceToNew_$$Prim$$_ArrayOP(this.getLiveContiguousArrayBackingUNSAFE());
		}
		
		@Override
		public Slice<_$$prim$$_[]> getLiveContiguousArrayBackingUNSAFE()
		{
			return new Slice<>(underlying, offset, length);
		}
		
		@Override
		public int size()
		{
			return length;
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return true;
		}
		
		
		
		@Override
		public Simple_$$Primitive$$_Iterator newSimple_$$Primitive$$_Iterator()
		{
			return new Simple_$$Primitive$$_Iterator()
			{
				int index = 0;
				
				public _$$prim$$_ nextrp_$$Prim$$_() throws StopIterationReturnPath
				{
					int i = index;
					
					if (i >= size())
					{
						throw StopIterationReturnPath.I;
					}
					else
					{
						index = i + 1;
						return underlying[offset+i];
					}
				}
			};
		}
		
		@Override
		public _$$Primitive$$_List subList(int fromIndex, int toIndex)
		{
			rangeCheckInterval(this.size(), fromIndex, toIndex);
			return new FixedLengthArrayWrapper_$$Primitive$$_List(this.underlying, fromIndex+offset, toIndex-fromIndex);
		}
		
		@Override
		public _$$prim$$_ get_$$Prim$$_(int index)
		{
			return underlying[offset + index];
		}
		
		@Override
		public void set_$$Prim$$_(int index, _$$prim$$_ value)
		{
			underlying[offset + index] = value;
		}
		
		
		
		
		
		
		
		
		
		//Unallowed Dim-altering Methods!
		
		@Override
		public void clear()
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setSize_$$Prim$$_(int newSize, _$$prim$$_ elementToAddIfGrowing)
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void insert_$$Prim$$_(int index, _$$prim$$_ value)
		{
			throw new UnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	@ReadonlyValue
	public static _$$Primitive$$_List _$$prim$$_ArrayAsList(@LiveValue @WritableValue _$$prim$$_[] array, int offset, int length)
	{
		return new FixedLengthArrayWrapper_$$Primitive$$_List(array, offset, length);
	}
	
	@ReadonlyValue
	public static _$$Primitive$$_List _$$prim$$_ArrayAsList(@LiveValue @WritableValue _$$prim$$_[] array)
	{
		return _$$prim$$_ArrayAsList(array, 0, array.length);
	}
	
	@ReadonlyValue
	public static _$$Primitive$$_List _$$prim$$_ArrayAsList(@LiveValue @WritableValue Slice<_$$prim$$_[]> arraySlice)
	{
		return _$$prim$$_ArrayAsList(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	
	
	
	@ThrowAwayValue
	public static _$$Primitive$$_List _$$prim$$_ArrayAsMutableList(@SnapshotValue @ReadonlyValue _$$prim$$_[] array, int offset, int length)
	{
		return new _$$Primitive$$_ArrayList(new FixedLengthArrayWrapper_$$Primitive$$_List(array, offset, length));
	}
	
	@ThrowAwayValue
	public static _$$Primitive$$_List _$$prim$$_ArrayAsMutableList(@SnapshotValue @ReadonlyValue _$$prim$$_[] array)
	{
		return _$$prim$$_ArrayAsMutableList(array, 0, array.length);
	}
	
	@ThrowAwayValue
	public static _$$Primitive$$_List _$$prim$$_ArrayAsMutableList(@SnapshotValue @ReadonlyValue Slice<_$$prim$$_[]> arraySlice)
	{
		return _$$prim$$_ArrayAsMutableList(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	@Immutable
	public static class Immutable_$$Primitive$$_ArrayList
	implements Serializable, Comparable<Immutable_$$Primitive$$_ArrayList>, _$$Primitive$$_List, KnowsLengthFixedness     //Let's not implement the general interfaces for accessing the underlying data jussssst to help make sure anyone who accesses it *knows* it's meant to be VERY MUCH READONLY!  XD'''
	{
		private static final long serialVersionUID = 1L;
		
		
		protected final _$$prim$$_[] data;
		
		//Yeah I know it's bad practice for a thread-safe immutable object to cache things, but at best effort is wasted, no constraints are violated!     (If I'm wrong, fire me X'D )
		protected transient long hashCode;     //we cache it in case the _$$prim$$_array is large :33
		
		protected Immutable_$$Primitive$$_ArrayList(_$$prim$$_[] LIVEDATA)
		{
			if (LIVEDATA == null) throw new NullPointerException();
			this.data = LIVEDATA;
		}
		
		
		
		
		
		/**
	 * DO NOT *EVER* MODIFY THE ARRAY YOU PASSED HERE AFTER CALLING THIS X"D
	 * ( THE HASH CODE IS CACHED FOR ONE, AND THIS MAY BE EMBEDDED IN HASHMAPS--THIS IS SUPPOSED TO BE IMMUTABLE AFTER ALL X'DDD )
	 * @see #getREADONLYLiveWholeArrayBackingUNSAFE()
		 ⎋a/
		@ImplementationTransparency
		public static Immutable_$$Primitive$$_ArrayList newLIVE(@LiveValue _$$prim$$_[] LIVEDATA)
		{
			return new Immutable_$$Primitive$$_ArrayList(LIVEDATA);
		}
		
		
		@ImplementationTransparency
		public static Immutable_$$Primitive$$_ArrayList newSingleton(_$$prim$$_ singleMember)
		{
			return new Immutable_$$Primitive$$_ArrayList(new _$$prim$$_[]{singleMember});
		}
		
		
		
		
		public static Immutable_$$Primitive$$_ArrayList newCopying(@SnapshotValue List<_$$Primitive$$_> data)
		{
			if (data instanceof Immutable_$$Primitive$$_ArrayList)
				return (Immutable_$$Primitive$$_ArrayList)data;
			
			if (TransparentContiguousArrayBackedCollection.is(data))
			{
				Slice<Object> s = ((TransparentContiguousArrayBackedCollection)data).getLiveContiguousArrayBackingUNSAFE();
				if (s.getUnderlying() instanceof _$$prim$$_[])
				{
					return newCopying((Slice)s);
				}
			}
			
			if (data instanceof _$$Primitive$$_List)
			{
				return newLIVE(((_$$Primitive$$_List)data).to_$$Prim$$_Array());
			}
			else
			{
				int n = data.size();
				_$$prim$$_[] a = new _$$prim$$_[n];
				
				int i = 0;
				for (_$$Primitive$$_ e : data)
				{
					if (i >= n)
						throw new ImpossibleException();
					
					a[i] = e;
					i++;
				}
				
				if (i != n)
					throw new ImpossibleException();
				
				return newLIVE(a);
			}
		}
		
		public static Immutable_$$Primitive$$_ArrayList newCopying(@SnapshotValue _$$prim$$_[] data)
		{
			return new Immutable_$$Primitive$$_ArrayList(data.clone());
		}
		
		public static Immutable_$$Primitive$$_ArrayList newCopying(@SnapshotValue _$$prim$$_[] data, int offset, int length)
		{
			if (offset == 0 && length == data.length)
				return newCopying(data);  //who knows, _$$prim$$_[].clone() might be faster! \o/   (it certainly shouldn't be slower!!)
			else
			{
				_$$prim$$_[] newArray = new _$$prim$$_[length];
				System.arraycopy(data, offset, newArray, 0, length);
				return new Immutable_$$Primitive$$_ArrayList(newArray);
			}
		}
		
		public static Immutable_$$Primitive$$_ArrayList newCopying(@SnapshotValue Slice<_$$prim$$_[]> data)
		{
			return newCopying(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		
		
		
		
		public static Immutable_$$Primitive$$_ArrayList newLIVEOrCopying(@PossiblySnapshotPossiblyLiveValue _$$prim$$_[] data, int offset, int length)
		{
			if (offset == 0 && length == data.length)
				return newLIVE(data);
			else
				return newCopying(data, offset, length);
		}
		
		public static Immutable_$$Primitive$$_ArrayList newLIVEOrCopying(@PossiblySnapshotPossiblyLiveValue Slice<_$$prim$$_[]> data)
		{
			return newLIVEOrCopying(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		
		
		
		
		
		
		public static final Immutable_$$Primitive$$_ArrayList Empty = Immutable_$$Primitive$$_ArrayList.newLIVE(ArrayUtilities.Empty_$$Prim$$_Array);
		
		
		
		
		
		
		
		
		@ThrowAwayValue
		@Override
		public _$$prim$$_[] to_$$Prim$$_Array()
		{
			return data.clone();
		}
		
		/**
	 * DO NOT MODIFY THIS X"D
	 * ( THE HASH CODE IS CACHED FOR ONE, AND THIS MAY BE EMBEDDED IN HASHMAPS--THIS IS SUPPOSED TO BE IMMUTABLE AFTER ALL X'DDD )
		 ⎋a/
		@ImplementationTransparency
		@LiveValue
		@ReadonlyValue //!!!!
		public _$$prim$$_[] getREADONLYLiveWholeArrayBackingUNSAFE()
		{
			return data;
		}
		
		@Override
		public int size()
		{
			return data.length;
		}
		
		@Override
		public _$$prim$$_ get_$$Prim$$_(int index)
		{
			return data[index];
		}
		
		@Override
		public void getAll_$$Prim$$_s(int offsetInThisSource, @WritableValue _$$prim$$_[] dest, int offsetInDest, int lengthInBoth)
		{
			System.arraycopy(this.data, offsetInThisSource, dest, offsetInDest, lengthInBoth);
		}
		
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return true;
		}
		
		
		
		
		@Override
		public int hashCode()
		{
			long hashCodeField = this.hashCode;
			
			boolean cached = hashCodeField != 0;
			
			
			if (cached)
			{
				return (int)this.hashCode;
			}
			else
			{
				int hashCode = Arrays.hashCode(this.data);
				this.hashCode = hashCode | 0xFFFF_FFFF_0000_0000l;  //hashCodes can be zero!  Not often XDD   But can be!  So it's not correct unless we account for that :>      (And if it was two fields then it'd be a concurrency bug!)
				return hashCode;
			}
		}
		
		
		
		
		@Override
		public boolean equals(@ReadonlyValue Object obj)
		{
			if (obj == this)
			{
				return true;
			}
			else if (obj instanceof Immutable_$$Primitive$$_ArrayList)
			{
				Immutable_$$Primitive$$_ArrayList other = (Immutable_$$Primitive$$_ArrayList)obj;
				
				//Checking the hashcodes is a VERY fast negative test and is almost always true-negative!! :DDD   (rarely it's false-positive, but never false-negative!)
				
				long thisHC = this.hashCode;
				long otherHC = other.hashCode;
				
				boolean thisHashCodeCached = thisHC != 0;
				boolean otherHashCodeCached = otherHC != 0;
				
				int thisHashCode = (int)thisHC;
				int otherHashCode = (int)otherHC;
				
				if (thisHashCodeCached)
				{
					if (otherHashCodeCached)
					{
						if (thisHashCode != otherHashCode)
							return false;
					}
					else
					{
						//Scanning one _$$prim$$_ array is faster than scanning two! :D
						if (thisHashCode != other.hashCode())
							return false;
					}
				}
				else
				{
					if (otherHashCodeCached)
					{
						//Scanning one _$$prim$$_ array is faster than scanning two! :D
						if (this.hashCode() != otherHashCode)
							return false;
					}
					else
					{
						//Then don't make any hash codes X3
					}
				}
				
				return this.equals_$$Prim$$_Array(other.data);
			}
			else
			{
				return false;
			}
		}
		
		
		@Override
		public int compareTo(@ReadonlyValue Immutable_$$Primitive$$_ArrayList other)
		{
			return this.compareTo_$$Prim$$_Array(other.data);
		}
		
		
		public boolean equals_$$Prim$$_Array(@ReadonlyValue _$$prim$$_[] o)
		{
			_$$prim$$_[] ourData = this.getREADONLYLiveWholeArrayBackingUNSAFE();
			_$$prim$$_[] theirData = o;
			
			return Arrays.equals(ourData, theirData);
		}
		
		
		public int compareTo_$$Prim$$_Array(@ReadonlyValue _$$prim$$_[] o)
		{
			_$$prim$$_[] ourData = this.getREADONLYLiveWholeArrayBackingUNSAFE();
			_$$prim$$_[] theirData = o;
			
			return ArrayUtilities.compareLittleEndianLengthsFirst(ourData, theirData);  //lengths-first is more efficient if they're different but hardly any less efficient if they're always the same!  A good default, don't you think? :D
		}
		
		
		
		
		@Override
		public Immutable_$$Primitive$$_ArrayList clone()
		{
			return this;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		// Unallowed Write Methods!
		
		@Override
		public void clear()
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setSize_$$Prim$$_(int newSize, _$$prim$$_ elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void insert_$$Prim$$_(int index, _$$prim$$_ value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void set_$$Prim$$_(int index, _$$prim$$_ value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static _$$Primitive$$_List unmodifiable_$$Primitive$$_List(_$$Primitive$$_List _$$primitive$$_List)
	{
		return _$$primitive$$_List instanceof Unmodifiable_$$Primitive$$_ListWrapper ? _$$primitive$$_List : new Unmodifiable_$$Primitive$$_ListWrapper(_$$primitive$$_List);
	}
	
	public static class Unmodifiable_$$Primitive$$_ListWrapper
	implements _$$Primitive$$_List, KnowsLengthFixedness, TransparentContiguousArrayBackedCollection
	{
		protected final _$$Primitive$$_List underlying;
		
		public Unmodifiable_$$Primitive$$_ListWrapper(_$$Primitive$$_List underlying)
		{
			this.underlying = underlying;
		}
		
		@ImplementationTransparency
		public _$$Primitive$$_List getUnderlying()
		{
			return underlying;
		}
		
		
		
		
		
		@Override
		public Unmodifiable_$$Primitive$$_ListWrapper clone()
		{
			return new Unmodifiable_$$Primitive$$_ListWrapper(underlying.clone());
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.underlying);
		}
		
		@Override
		public boolean isTransparentContiguousArrayBackedCollection()
		{
			return TransparentContiguousArrayBackedCollection.is(underlying);
		}
		
		@Override
		public Slice getLiveContiguousArrayBackingUNSAFE()
		{
			return ((TransparentContiguousArrayBackedCollection)underlying).getLiveContiguousArrayBackingUNSAFE();
		}
		
		
		
		
		
		
		//Use default so they can't get around our readonly-ness!  ;D
		//	public Iterator<_$$Primitive$$_> iterator()
		//	public ListIterator<_$$Primitive$$_> listIterator()
		//	public ListIterator<_$$Primitive$$_> listIterator(int index)
		//	public SimpleIterator<_$$Primitive$$_> simpleIterator()
		//	public Simple_$$Primitive$$_Iterator newSimple_$$Primitive$$_Iterator()
		//	public _$$Primitive$$_List subList(int fromIndex, int toIndex)
		
		
		
		
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeRangeByLength(int start, int length) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void replaceAll(UnaryOperator<_$$Primitive$$_> operator)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeIf(Predicate<? super _$$Primitive$$_> filter)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void sort(Comparator<? super _$$Primitive$$_> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void clear()
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public _$$Primitive$$_ remove(int index)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll_$$Prim$$_s(_$$prim$$_[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll_$$Prim$$_s(Slice<_$$prim$$_[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAll_$$Prim$$_s(_$$prim$$_[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAll_$$Prim$$_s(Slice<_$$prim$$_[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAll_$$Prim$$_s(_$$prim$$_[] a, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void set_$$Prim$$_(int index, _$$prim$$_ value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insert_$$Prim$$_(int index, _$$prim$$_ value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSize_$$Prim$$_(int newSize, _$$prim$$_ elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSize(int newSize, _$$Primitive$$_ elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSize(int newSize)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAll_$$Prim$$_s(int index, _$$prim$$_[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAll_$$Prim$$_s(int index, Slice<_$$prim$$_[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAll_$$Prim$$_s(int index, _$$prim$$_[] array, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAll(int destIndex, List sourceU, int sourceIndex, int amount) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean add_$$Prim$$_(_$$prim$$_ value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll(Collection<? extends _$$Primitive$$_> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll_$$Prim$$_s(_$$prim$$_[] elements, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeByIndex(int index)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public _$$prim$$_ remove_$$Prim$$_ByIndex(int index) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean remove_$$Prim$$_(_$$prim$$_ value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean retainAll(Collection<?> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAll_$$Prim$$_s(int index, _$$prim$$_[] array, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll(int index, Collection<? extends _$$Primitive$$_> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAll_$$Prim$$_s(int index, _$$prim$$_[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAll_$$Prim$$_s(int index, Slice<_$$prim$$_[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public _$$Primitive$$_ set(int index, _$$Primitive$$_ value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void add(int index, _$$Primitive$$_ value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeAll(Collection<?> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean remove(Object o)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean add(_$$Primitive$$_ e)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		
		
		
		
		
		
		
		
		
		public void forEach(Consumer<? super _$$Primitive$$_> action)
		{
			underlying.forEach(action);
		}
		
		public int size()
		{
			return underlying.size();
		}
		
		public boolean equals(Object o)
		{
			return underlying.equals(o);
		}
		
		public int hashCode()
		{
			return underlying.hashCode();
		}
		
		public Spliterator<_$$Primitive$$_> spliterator()
		{
			return underlying.spliterator();
		}
		
		public Stream<_$$Primitive$$_> stream()
		{
			return underlying.stream();
		}
		
		public Stream<_$$Primitive$$_> parallelStream()
		{
			return underlying.parallelStream();
		}
		
		public _$$prim$$_ get_$$Prim$$_(int index)
		{
			return underlying.get_$$Prim$$_(index);
		}
		
		public int indexOf_$$Prim$$_(_$$prim$$_ value)
		{
			return underlying.indexOf_$$Prim$$_(value);
		}
		
		public int lastIndexOf_$$Prim$$_(_$$prim$$_ value)
		{
			return underlying.lastIndexOf_$$Prim$$_(value);
		}
		
		public boolean equivalent(List<_$$Primitive$$_> other)
		{
			return underlying.equivalent(other);
		}
		
		public _$$prim$$_[] to_$$Prim$$_Array()
		{
			return underlying.to_$$Prim$$_Array();
		}
		
		public boolean contains_$$Prim$$_(_$$prim$$_ value)
		{
			return underlying.contains_$$Prim$$_(value);
		}
		
		public _$$Primitive$$_ get(int index)
		{
			return underlying.get(index);
		}
		
		public int indexOf(Object o)
		{
			return underlying.indexOf(o);
		}
		
		public int lastIndexOf(Object o)
		{
			return underlying.lastIndexOf(o);
		}
		
		public boolean containsAll(Collection<?> c)
		{
			return underlying.containsAll(c);
		}
		
		public boolean isEmpty()
		{
			return underlying.isEmpty();
		}
		
		public boolean contains(Object o)
		{
			return underlying.contains(o);
		}
		
		public Object[] toArray()
		{
			return underlying.toArray();
		}
		
		public <T> T[] toArray(T[] a)
		{
			return underlying.toArray(a);
		}
		
		public void storeBoxingIntoArray(Slice<Object[]> array)
		{
			underlying.storeBoxingIntoArray(array);
		}
		
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class _$$Primitive$$_Table
	{
		protected int width;
		protected _$$prim$$_[] data;
		
		public _$$Primitive$$_Table(int width, int height)
		{
			if (width < 0)
				throw new IllegalArgumentException();
			if (height < 0)
				throw new IllegalArgumentException();
			
			data = new _$$prim$$_[width * height];
		}
		
		
		
		@Nonnegative
		public int getNumberOfColumns()
		{
			return width;
		}
		
		@Nonnegative
		public int getNumberOfRows()
		{
			return data.length / width;
		}
		
		
		@Nonnegative
		public int getNumberOfCells()
		{
			return data.length;
		}
		
		
		
		
		
		public _$$prim$$_ getCellContents_$$Primitive$$_(int columnIndex, int rowIndex) throws IndexOutOfBoundsException
		{
			if (columnIndex < 0) throw new IndexOutOfBoundsException();
			if (rowIndex < 0) throw new IndexOutOfBoundsException();
			if (columnIndex >= getNumberOfColumns()) throw new IndexOutOfBoundsException();
			if (rowIndex >= getNumberOfRows()) throw new IndexOutOfBoundsException();
			
			return this.data[rowIndex * this.getNumberOfColumns() + columnIndex];
		}
		
		public void setCellContents_$$Primitive$$_(int columnIndex, int rowIndex, @Nullable _$$prim$$_ newValue) throws IndexOutOfBoundsException
		{
			if (columnIndex < 0) throw new IndexOutOfBoundsException();
			if (rowIndex < 0) throw new IndexOutOfBoundsException();
			if (columnIndex >= getNumberOfColumns()) throw new IndexOutOfBoundsException();
			if (rowIndex >= getNumberOfRows()) throw new IndexOutOfBoundsException();
			
			this.data[rowIndex * this.getNumberOfColumns() + columnIndex] = newValue;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Nullable
		public _$$Primitive$$_ getCellContents(int columnIndex, int rowIndex) throws IndexOutOfBoundsException
		{
			return getCellContents_$$Primitive$$_(columnIndex, rowIndex);
		}
		
		public void setCellContents(int columnIndex, int rowIndex, @Nullable _$$Primitive$$_ newValue) throws IndexOutOfBoundsException
		{
			setCellContents_$$Primitive$$_(columnIndex, rowIndex, newValue);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	 */
	
	
	public static boolean getBoolean(List<Boolean> list, int index)
	{
		if (list instanceof BooleanList)
			return ((BooleanList) list).getBoolean(index);
		else
			return list.get(index);
	}
	
	public static void setBoolean(List<Boolean> list, int index, boolean value)
	{
		if (list instanceof BooleanList)
			((BooleanList) list).setBoolean(index, value);
		else
			list.set(index, value);
	}
	
	
	
	
	
	
	
	public static interface SimpleBooleanIterable
	extends SimpleIterable<Boolean>
	{
		public SimpleBooleanIterator newSimpleBooleanIterator();
		
		public default SimpleIterator<Boolean> simpleIterator()
		{
			SimpleBooleanIterator i = this.newSimpleBooleanIterator();
			return () -> i.nextrpBoolean();
		}
	}
	
	
	public static interface SimpleBooleanIterator
	extends SimpleIterator<Boolean>
	{
		public boolean nextrpBoolean() throws StopIterationReturnPath;
		
		
		@Override
		public default Boolean nextrp() throws StopIterationReturnPath
		{
			return nextrpBoolean();
		}
	}
	
	
	
	@SignalInterface
	public static interface BooleanCollection
	extends PrimitiveCollection<Boolean>, SimpleBooleanIterable
	{
		public boolean addBoolean(boolean value);
		
		public boolean removeBoolean(boolean value);
		
		
		
		
		
		@ThrowAwayValue
		public static boolean[] defaultToBooleanArray(BooleanCollection collection)
		{
			boolean[] array = new boolean[collection.size()];
			SimpleBooleanIterator i = collection.newSimpleBooleanIterator();
			
			int index = 0;
			while (true)
			{
				try
				{
					array[index] = i.nextrpBoolean();
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
				
				index++;
			}
			
			return array;
		}
		
		@ThrowAwayValue
		public default boolean[] toBooleanArray()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(this.size(), u);
				
				if (u.getUnderlying() instanceof boolean[])
				{
					return sliceToNewBooleanArrayOP((Slice<boolean[]>) u);
				}
			}
			
			return defaultToBooleanArray(this);
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public default boolean[] toBooleanArrayPossiblyLive()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				int size = this.size();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				Object und = u.getUnderlying();
				
				if (und instanceof boolean[] && u.getOffset() == 0 && ((boolean[])und).length == size)
				{
					return (boolean[])und;
				}
			}
			
			return defaultToBooleanArray(this);
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public default Slice<boolean[]> toBooleanArraySlicePossiblyLive()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(this.size(), u);
				
				if (u.getUnderlying() instanceof boolean[])
				{
					return (Slice<boolean[]>) u;
				}
			}
			
			return wholeArraySliceBoolean(defaultToBooleanArray(this));
		}
		
		
		
		
		
		
		public default boolean containsBoolean(boolean value)
		{
			SimpleBooleanIterator i = newSimpleBooleanIterator();
			
			while (true)
			{
				try
				{
					if (eqSane(i.nextrpBoolean(), value))
						return true;
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
			}
			
			return false;
		}
		
		
		
		
		
		
		@Override
		public default Iterator<Boolean> iterator()
		{
			return SimpleBooleanIterable.super.iterator();
		}
		
		@Override
		public default boolean isEmpty()
		{
			return size() == 0;
		}
		
		
		
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllBooleans(boolean[] array)
		{
			return addAllBooleans(array, 0, array.length);
		}
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllBooleans(Slice<boolean[]> arraySlice)
		{
			return addAllBooleans(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllBooleans(boolean[] elements, int offset, int length)
		{
			return defaultAddAllBooleans(this, elements, offset, length);
		}
		
		public static boolean defaultAddAllBooleans(BooleanCollection self, boolean[] elements, int offset, int length)
		{
			boolean changedAtAll = false;
			int e = offset + length;
			for (int i = offset; i < e; i++)
				changedAtAll |= self.addBoolean(elements[i]);
			return changedAtAll;
		}
		
		
		
		
		public default void removeAllBooleans(boolean[] array)
		{
			removeAllBooleans(array, 0, array.length);
		}
		
		public default void removeAllBooleans(Slice<boolean[]> arraySlice)
		{
			removeAllBooleans(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public default void removeAllBooleans(boolean[] a, int offset, int length)
		{
			int e = offset + length;
			for (int i = offset; i < e; i++)
				this.removeBoolean(a[i]);
		}
		
		
		
		
		
		
		
		
		
		
		@Override
		public default boolean contains(Object o)
		{
			return o instanceof Boolean && containsBoolean((Boolean)o);
		}
		
		
		@Override
		public default boolean add(Boolean e)
		{
			return addBoolean(e);
		}
		
		
		@Override
		public default boolean remove(Object o)
		{
			return o instanceof Boolean && removeBoolean((Boolean)o);
		}
		
		
		@Override
		public default boolean containsAll(Collection<?> c)
		{
			if (c instanceof BooleanCollection)
			{
				BooleanCollection cc = (BooleanCollection) c;
				
				SimpleBooleanIterator i = cc.newSimpleBooleanIterator();
				while (true)
				{
					try
					{
						if (this.containsBoolean(i.nextrpBoolean()))
							return true;
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return false;
			}
			else
			{
				for (Object e : c)
					if (this.contains(e))
						return true;
				return false;
			}
		}
		
		
		@Override
		public default boolean addAll(Collection<? extends Boolean> c)
		{
			if (c instanceof BooleanCollection)
			{
				boolean changedAtAll = false;
				
				SimpleBooleanIterator i = ((BooleanCollection)c).newSimpleBooleanIterator();
				
				while (true)
				{
					try
					{
						changedAtAll |= this.add(i.nextrpBoolean());
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (Boolean e : c)
				{
					changedAtAll |= this.add(e);
				}
				
				return changedAtAll;
			}
		}
		
		
		
		@Override
		public default boolean removeAll(Collection<?> c)
		{
			if (c instanceof BooleanCollection)
			{
				boolean changedAtAll = false;
				
				SimpleBooleanIterator i = ((BooleanCollection)c).newSimpleBooleanIterator();
				
				while (true)
				{
					try
					{
						changedAtAll |= this.remove(i.nextrpBoolean());
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (Object e : c)
				{
					changedAtAll |= this.remove(e);
				}
				
				return changedAtAll;
			}
		}
		
		
		
		public default String _toString()
		{
			StringBuilder b = new StringBuilder("[");
			
			SimpleBooleanIterator i = newSimpleBooleanIterator();
			
			boolean first = true;
			
			while (true)
			{
				boolean e;
				
				try
				{
					e = i.nextrpBoolean();
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
				
				
				
				//Notice this comes after the possible 'break'!  ^_~
				if (first)
					first = false;
				else
					b.append(", ");
				
				b.append(e);
			}
			
			b.append(']');
			
			return b.toString();
		}
	}
	
	
	
	public static interface DefaultToArraysBooleanCollection
	extends BooleanCollection
	{
		@Override
		public default Object[] toArray()
		{
			Object[] a = new Object[size()];
			storeBoxingIntoArray(wholeArraySliceReference(a));
			return a;
		}
		
		@Override
		public default <T> T[] toArray(T[] a)
		{
			if (a.length >= this.size())
			{
				storeBoxingIntoArray(wholeArraySliceReference(a));
				return a;
			}
			else
			{
				Object[] aa;
				
				if (a.getClass() == Boolean[].class)
				{
					aa = new Boolean[size()];
				}
				else if (a.getClass() == Object[].class)
				{
					aa = new Object[size()];
				}
				else
				{
					throw new StructuredClassCastException(a.getClass());
				}
				
				storeBoxingIntoArray(wholeArraySliceReference(aa));
				
				return (T[])aa;
			}
		}
		
		public void storeBoxingIntoArray(Slice<Object[]> array);
	}
	
	
	
	
	
	
	@SignalInterface
	public static interface BooleanListRO
	{
		public boolean getBoolean(int index);
		
		public int size();
		
		
		
		public default int indexOfBoolean(boolean value)
		{
			int n = this.size();
			for (int i = 0; i < n; i++)
				if (eqSane(this.getBoolean(i), value))
					return i;
			return -1;
		}
		
		public default int lastIndexOfBoolean(boolean value)
		{
			int i = this.size()-1;
			while (i >= 0)
			{
				if (eqSane(this.getBoolean(i), value))
					return i;
				break;
			}
			return i;   // ;D
		}
		
		
		
		public default boolean equivalent(List<Boolean> other)
		{
			int size = this.size();
			if (other.size() != size)
				return false;
			
			
			if (other instanceof BooleanListRO)  //All BooleanLists will implement this interface too so this accounts for them as well :3
			{
				return equivalentFixedRO((BooleanListRO)other);
			}
			else
			{
				for (int i = 0; i < size; i++)
				{
					if (!eqSane(this.getBoolean(i), other.get(i).booleanValue()))
						return false;
				}
			}
			
			return true;
		}
		
		
		
		public default boolean equivalentFixedRO(BooleanListRO other)
		{
			int size = this.size();
			if (other.size() != size)
				return false;
			
			for (int i = 0; i < size; i++)
			{
				if (!eqSane(this.getBoolean(i), other.getBoolean(i)))
					return false;
			}
			
			return true;
		}
	}
	
	
	
	@SignalInterface
	public static interface BooleanListRWFixed
	extends BooleanListRO
	{
		public void setBoolean(int index, boolean value);
	}
	
	
	
	@SignalInterface
	public static interface BooleanList
	extends BooleanCollection, NiceList<Boolean>, BooleanListRO, BooleanListRWFixed, ListWithRemoveRange, DefaultToArraysBooleanCollection, PubliclyCloneable, ListWithSetSize<Boolean>, ListWithSetAll, NonuniformMethodsForBooleanList
	{
		public void insertBoolean(int index, boolean value);
		
		
		
		/**
		 * This should be equivalent to just constructing and returning a new in-memory stock implementation with the same contents  (but can easily be faster, especially for those stock impl.s! XD )
		 * As for whether it's fixed-length or read-only or duplicateless or etc., that should be the same in the clone as it is in the main impl.
		 * If *and only if* it is immutable, this is allowed to return this same instance!  (ie the body of this method being <code>return this;</code>)
		 */
		public BooleanList clone();
		
		
		/**
		 * If size is smaller than the current, then this is equivalent to {@link #removeRange(int, int) removeRange}(newSize, this.{@link #size() size()})  :3
		 * If size is larger, then this appends newSize - this.{@link #size() size()} elementToAddIfGrowing's to the end like that many {@link #addBoolean(boolean)}  :D
		 */
		public void setSizeBoolean(int newSize, boolean elementToAddIfGrowing);
		
		public default void setSize(int newSize, Boolean elementToAddIfGrowing)
		{
			setSizeBoolean(newSize, elementToAddIfGrowing);
		}
		
		public default void setSize(int newSize)
		{
			setSizeBoolean(newSize, false);
		}
		
		
		
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void setAllBooleans(int index, boolean[] array)
		{
			setAllBooleans(index, array, 0, array.length);
		}
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void setAllBooleans(int index, Slice<boolean[]> arraySlice)
		{
			setAllBooleans(index, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		public default void setAllBooleans(int start, boolean[] array, int offset, int length)
		{
			int size = this.size();
			
			rangeCheckIntervalByLength(size, start, length);
			rangeCheckIntervalByLength(array.length, offset, length);
			
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(size, start, length);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				if (u.getUnderlying() instanceof boolean[])
				{
					Slice<boolean[]> s = (Slice<boolean[]>) u;
					
					System.arraycopy(array, offset, s.getUnderlying(), s.getOffset()+start, length);
					
					return;
				}
			}
			
			
			defaultSetAllBooleans(this, start, array, offset, length);
		}
		
		
		public static void defaultSetAllBooleans(BooleanList list, int start, @WritableValue boolean[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				list.setBoolean(start + i, array[offset + i]);
		}
		
		
		
		
		
		@Override
		public default void setAll(int destIndex, List sourceU, int sourceIndex, @Nonnegative int amount) throws IndexOutOfBoundsException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			List<Boolean> source = sourceU;
			BooleanList dest = this;
			
			int sourceSize = source.size();
			int destSize = dest.size();
			rangeCheckIntervalByLength(sourceSize, sourceIndex, amount);
			rangeCheckIntervalByLength(destSize, destIndex, amount);
			
			
			
			if (TransparentContiguousArrayBackedCollection.is(source))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection<?>)source).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(sourceSize, sourceIndex, amount);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(sourceSize, u);
				
				if (u.getUnderlying() instanceof boolean[])
				{
					Slice<boolean[]> s = (Slice<boolean[]>)u;
					this.setAllBooleans(destIndex, s.subslice(sourceIndex, amount));
					return;
				}
			}
			
			
			
			
			if (source instanceof BooleanList)
			{
				BooleanList primSource = (BooleanList) source;
				
				if (destIndex < sourceIndex)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
				{
					for (int i = 0; i < amount; i++)
						dest.setBoolean(destIndex+i, primSource.getBoolean(sourceIndex+i));
				}
				else
				{
					for (int i = amount-1; i >= 0; i--)
						dest.setBoolean(destIndex+i, primSource.getBoolean(sourceIndex+i));
				}
			}
			else
			{
				if (destIndex < sourceIndex)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
				{
					for (int i = 0; i < amount; i++)
						dest.set(destIndex+i, source.get(sourceIndex+i));
				}
				else
				{
					for (int i = amount-1; i >= 0; i--)
						dest.set(destIndex+i, source.get(sourceIndex+i));
				}
			}
		}
		
		
		
		
		
		
		
		/**
		 * Copies <code>array.length</code> elements into <code>array</code> starting with the <code>start</code>th element.<br>
		 */
		public default void getAllBooleans(int start, @WritableValue boolean[] array, int offset, int length)
		{
			int size = this.size();
			
			rangeCheckIntervalByLength(size, start, length);
			rangeCheckIntervalByLength(array.length, offset, length);
			
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(size, start, length);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				if (u.getUnderlying() instanceof boolean[])
				{
					Slice<boolean[]> s = (Slice<boolean[]>) u;
					
					System.arraycopy(s.getUnderlying(), s.getOffset()+start, array, offset, length);
					
					return;
				}
			}
			
			
			defaultGetAllBooleans(this, start, array, offset, length);
		}
		
		
		public static void defaultGetAllBooleans(BooleanList list, int start, @WritableValue boolean[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				array[offset + i] = list.getBoolean(start + i);
		}
		
		
		
		
		
		
		@ThrowAwayValue
		public default boolean[] getAllBooleans(int start, int end)
		{
			rangeCheckInterval(this.size(), start, end);
			
			boolean[] buff = new boolean[end-start];
			getAllBooleans(start, buff, 0, buff.length);
			return buff;
		}
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public default boolean addBoolean(boolean value)
		{
			this.insertBoolean(this.size(), value);
			return true;
		}
		
		@Override
		public default boolean addAll(Collection<? extends Boolean> c)
		{
			this.addAll(this.size(), c);
			return true;
		}
		
		@Override
		public default boolean addAllBooleans(boolean[] elements, int offset, int length)
		{
			insertAllBooleans(this.size(), elements, offset, length);
			return true;
		}
		
		public default void removeByIndex(int index)
		{
			removeRange(index, index+1);
		}
		
		public default boolean removeBooleanByIndex(int index) throws IndexOutOfBoundsException
		{
			boolean v = this.getBoolean(index);
			removeByIndex(index);
			return v;
		}
		
		@Override
		public default boolean removeBoolean(boolean value)
		{
			int i = this.indexOfBoolean(value);
			
			if (i != -1)
			{
				this.removeByIndex(i);
				return true;
			}
			else
			{
				return false;
			}
		}
		
		@Override
		public default boolean retainAll(Collection<?> c)
		{
			if (c instanceof BooleanCollection)
			{
				BooleanCollection cc = (BooleanCollection) c;
				
				boolean changedAtAll = false;
				
				for (int i = size() - 1; i >= 0; i--)
				{
					if (!cc.containsBoolean(getBoolean(i)))
					{
						removeBooleanByIndex(i);
						changedAtAll = true;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (int i = size() - 1; i >= 0; i--)
				{
					if (!c.contains(getBoolean(i)))
					{
						removeBooleanByIndex(i);
						changedAtAll = true;
					}
				}
				
				return changedAtAll;
			}
		}
		
		
		
		
		
		
		
		
		@Override
		public default SimpleBooleanIterator newSimpleBooleanIterator()
		{
			return new SimpleBooleanIterator()
			{
				int index = 0;
				
				public boolean nextrpBoolean() throws StopIterationReturnPath
				{
					int i = index;
					
					if (i >= size())
					{
						throw StopIterationReturnPath.I;
					}
					else
					{
						index = i + 1;
						return getBoolean(i);
					}
				}
			};
		}
		
		
		
		
		
		@Override
		public default boolean containsBoolean(boolean value)
		{
			return indexOfBoolean(value) != -1;
		}
		
		
		public default void insertAllBooleans(int index, boolean[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				insertBoolean(index+i, array[offset+i]);
		}
		
		
		@Override
		public default boolean addAll(int index, Collection<? extends Boolean> c)
		{
			if (c instanceof BooleanCollection)
			{
				BooleanCollection cc = (BooleanCollection)c;
				
				SimpleBooleanIterator i = cc.newSimpleBooleanIterator();
				while (true)
				{
					try
					{
						insertBoolean(index, i.nextrpBoolean());
						index++;  //doesn't matter if this gets executed on the last iteration :3
					}
					catch (StopIterationReturnPath rp)
					{
						break;
					}
				}
			}
			else
			{
				for (Boolean e : c)
				{
					insertBoolean(index, e);
					index++;
				}
			}
			
			return true;
		}
		
		
		
		public default void insertAllBooleans(int index, boolean[] array)
		{
			insertAllBooleans(index, array, 0, array.length);
		}
		
		public default void insertAllBooleans(int index, Slice<boolean[]> arraySlice)
		{
			insertAllBooleans(index, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		
		
		@Override
		public default Boolean get(int index)
		{
			return this.getBoolean(index);
		}
		
		@Override
		public default Boolean set(int index, Boolean value)
		{
			Boolean previous = get(index);
			this.setBoolean(index, value);
			return previous;
		}
		
		
		@Override
		public default void add(int index, Boolean value)
		{
			this.insertBoolean(index, value);
		}
		
		@Override
		public default Boolean remove(int index) throws IndexOutOfBoundsException
		{
			return this.removeBooleanByIndex(index);
		}
		
		
		@Override
		public default int indexOf(Object o)
		{
			return o instanceof Boolean ? indexOfBoolean((Boolean)o) : -1;
		}
		
		@Override
		public default int lastIndexOf(Object o)
		{
			return o instanceof Boolean ? lastIndexOfBoolean((Boolean)o) : -1;
		}
		
		
		
		@Override
		public default ListIterator<Boolean> listIterator()
		{
			return listIterator(0);
		}
		
		@Override
		public default ListIterator<Boolean> listIterator(int index)
		{
			//Todo
			throw new NotYetImplementedException();
		}
		
		@Override
		public default BooleanList subList(int fromIndex, int toIndex)   //Also note that we narrow the return types of this :3
		{
			if (fromIndex == 0 && toIndex == this.size())
				return this;
			
			if (this instanceof Sublist)
			{
				Sublist<Boolean> s = (Sublist<Boolean>)this;
				return new BooleanSublist((BooleanList)s.getUnderlying(), s.getSublistStartingIndex() + fromIndex, toIndex - fromIndex);
			}
			else
			{
				return new BooleanSublist(this, fromIndex, toIndex - fromIndex);
			}
		}
		
		
		
		
		
		
		
		
		
		
		// Multiple-Inheritance conflict resolvers X'3
		@Override
		public default boolean removeAll(Collection<?> c)
		{
			return DefaultToArraysBooleanCollection.super.removeAll(c);
		}
		
		@Override
		public default boolean remove(Object o)
		{
			return DefaultToArraysBooleanCollection.super.remove(o);
		}
		
		@Override
		public default boolean containsAll(Collection<?> c)
		{
			return DefaultToArraysBooleanCollection.super.containsAll(c);
		}
		
		@Override
		public default boolean add(Boolean e)
		{
			return DefaultToArraysBooleanCollection.super.add(e);
		}
		
		@Override
		public default boolean isEmpty()
		{
			return DefaultToArraysBooleanCollection.super.isEmpty();
		}
		
		@Override
		public default boolean contains(Object o)
		{
			return DefaultToArraysBooleanCollection.super.contains(o);
		}
		
		
		
		
		
		@Override
		public default Object[] toArray()
		{
			return DefaultToArraysBooleanCollection.super.toArray();
		}
		
		@Override
		public default <T> T[] toArray(T[] a)
		{
			return DefaultToArraysBooleanCollection.super.toArray(a);
		}
		
		
		
		
		
		
		
		@Override
		public default void storeBoxingIntoArray(Slice<Object[]> array)
		{
			int n = this.size();
			for (int i = 0; i < n; i++)
				ArrayUtilities.setReference((Slice)array, i, this.get(i));
		}
		
		
		
		
		
		
		//Narrow the return types of these :3
		
		@Override
		public default BooleanList subListToEnd(int start)
		{
			return (BooleanList)NiceList.super.subListToEnd(start);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
		
		@Override
		public default BooleanList subListFromBeginning(int lengthWhichIsEndExclusiveInThisCaseXD)
		{
			return (BooleanList)NiceList.super.subListFromBeginning(lengthWhichIsEndExclusiveInThisCaseXD);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
		
		@Override
		public default BooleanList subListByLength(int start, int length)
		{
			return (BooleanList)NiceList.super.subListByLength(start, length);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
	}
	
	
	
	
	
	
	public static interface DefaultShiftingBasedBooleanList
	extends BooleanList
	{
		public default void clear()
		{
			removeRange(0, size());
		}
		
		@Override
		public default void insertBoolean(int index, boolean value)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, 1);
			this.setBoolean(index, value);
		}
		
		@Override
		public default void insertAllBooleans(int index, boolean[] array, int offset, int length)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, length);
			
			this.setAllBooleans(index, array, offset, length);
		}
		
		
		@Override
		public default boolean addAll(int index, Collection<? extends Boolean> c)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			int amount = c.size();  //copy this incase c == this! XD
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, amount);
			
			if (c instanceof List)
			{
				listcopy((List)c, 0, this, index, amount);
			}
			else
			{
				if (c instanceof BooleanCollection)
				{
					SimpleBooleanIterator iterator = ((BooleanCollection)c).newSimpleBooleanIterator();
					
					int i = 0;
					while (true)
					{
						boolean e;
						try
						{
							e = iterator.nextrpBoolean();
						}
						catch (StopIterationReturnPath rp)
						{
							break;
						}
						
						this.setBoolean(index+i, e);
						i++;
					}
				}
				else
				{
					int i = 0;
					for (Boolean e : c)
					{
						this.set(index+i, e);
						i++;
					}
				}
			}
			
			return true;
		}
		
		@Override
		public default void setSizeBoolean(int newSize, boolean elementToAddIfGrowing)
		{
			int oldSize = this.size();
			int amount = newSize - oldSize;
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, oldSize, amount);
			
			//Growing results in *undefined* contents, not necessarily initialized ones!  (eg, it may already have some/enough elements allocated eg, in an underlying array and it just increases the int size field :3 )
			for (int i = oldSize; i < newSize; i++)
				this.setBoolean(i, elementToAddIfGrowing);
		}
		
		
		
		
		@Override
		public default void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			rangeCheckInterval(this.size(), start, pastEnd);
			
			int length = pastEnd - start;
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, pastEnd, -length);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class BooleanSublist
	implements BooleanList, DefaultShiftingBasedBooleanList, Sublist<Boolean>, ShiftableList
	{
		protected final BooleanList underlying;
		protected final int start;
		protected int size;
		
		public BooleanSublist(BooleanList underlying, int start, int initialSize)
		{
			rangeCheckIntervalByLength(underlying.size(), start, initialSize);
			
			this.underlying = underlying;
			this.start = start;
			this.size = initialSize;
		}
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public boolean getBoolean(int index)
		{
			return underlying.getBoolean(index + start);
		}
		
		@Override
		public void setBoolean(int index, boolean value)
		{
			underlying.setBoolean(index + start, value);
		}
		
		@Override
		public void shiftRegionStretchingFromIndexToEndByAmountChangingSize(int start, int amount)
		{
			rangeCheckCursorPoint(size(), start);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(underlying, start, amount);
			
			size += amount;
		}
		
		
		
		@Override
		public BooleanList clone()
		{
			BooleanList c = new BooleanArrayList();
			c.addAll(this);
			return c;
		}
		
		
		
		@Override
		public BooleanList getUnderlying()
		{
			return underlying;
		}
		
		@Override
		public int getSublistStartingIndex()
		{
			return start;
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class BooleanArrayList
	implements DefaultShiftingBasedBooleanList, ListWithSetSize<Boolean>, Trimmable, TransparentContiguousArrayBackedCollection<boolean[]>, KnowsLengthFixedness
	{
		protected int size = 0;
		protected boolean[] data;
		protected GrowerComputationallyUnreducedPurelyRecursive grower;
		
		
		
		
		//In case subclasses don't want to deal with the other things ^^'
		protected BooleanArrayList(Void thisiswhyyoushouldntusejavaconstructorsAlthoughIdontknowhowtomakeinheritanceworkwellwithoutinitmethodsorfactoriesorsomething)
		{
		}
		
		
		/**
		 * Capacity increment is different from Vector's.  It is the percentage of the capacity by which the capacity is increased.
		 * The formula being: <code>newCapacity = capacity + (capacity * capacityIncrement)</code><br>
		 * @param grower This can be anything.  The only downside is performance: all methods will add at least the minimum amount required regardless of what this specifies.
		 */
		public BooleanArrayList(int initialCapacity, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this.data = initialCapacity == 0 ? ArrayUtilities.EmptyBooleanArray : new boolean[initialCapacity];
			this.grower = grower;
		}
		
		public BooleanArrayList(@LiveValue @WritableValue boolean[] LIVEBACKING, int size, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this.data = LIVEBACKING;
			this.grower = grower;
			this.size = size;
		}
		
		public BooleanArrayList(@SnapshotValue @ReadonlyValue Collection<Boolean> c, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this(c.size(), grower);
			this.addAll(c);
		}
		
		
		
		
		
		public BooleanArrayList()
		{
			this(DefaultPrimitiveArrayListInitialCapacity);
		}
		
		public BooleanArrayList(int initialCapacity)
		{
			this(initialCapacity, defaultPrimitiveArrayListGrower(initialCapacity));
		}
		
		public BooleanArrayList(@SnapshotValue @ReadonlyValue Collection<Boolean> c)
		{
			this(c, defaultPrimitiveArrayListGrower(c.size()));
		}
		
		public BooleanArrayList(@LiveValue @WritableValue boolean[] LIVEBACKING, int size)
		{
			this(LIVEBACKING, size, defaultPrimitiveArrayListGrower(LIVEBACKING.length));
		}
		
		public BooleanArrayList(@LiveValue @WritableValue boolean[] LIVEBACKING)
		{
			this(LIVEBACKING, LIVEBACKING.length);
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return false;
		}
		
		
		
		@Override
		public void setSize(int newSize)
		{
			ensureCapacity(newSize);
			this.size = newSize;
			
			// :D
		}
		
		
		
		
		
		//This pattern is nicer for subclassing in any Java class! :D
		protected void setupClone(BooleanArrayList freshClone)
		{
			freshClone.size = this.size;
			
			freshClone.data = new boolean[size];
			System.arraycopy(this.data, 0, freshClone.data, 0, size);
			
			freshClone.grower = this.grower;
		}
		
		@Override
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public BooleanArrayList clone()
		{
			BooleanArrayList clone = new BooleanArrayList();
			this.setupClone(clone);
			return clone;
		}
		
		
		
		
		
		
		
		
		
		
		//This is what unlocks all the System.arraycopy() magic!  8>
		@ImplementationTransparency
		@LiveValue
		public Slice<boolean[]> getLiveContiguousArrayBackingUNSAFE()
		{
			return new Slice(data, 0, size);
		}
		
		
		
		
		
		
		
		
		
		public void setBoolean(int index, boolean value)
		{
			rangeCheckMember(this.size, index);
			data[index] = value;
		}
		
		public boolean getBoolean(int index)
		{
			rangeCheckMember(this.size(), index);
			return data[index];
		}
		
		
		
		
		
		
		
		@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public void clear()
		{
			size = 0;
		}
		
		@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public int size()
		{
			return size;
		}
		
		@ImplementationTransparency
		public void trimToSize()
		{
			boolean[] newdata = new boolean[size];
			System.arraycopy(data, 0, newdata, 0, size);
			this.data = newdata;
		}
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.trimToSize();
			return TrimmableTrimRV.DontKeepInvoking;
		}
		
		
		
		
		
		@ImplementationTransparency
		public int getCapacity()
		{
			return data.length;
		}
		
		@ImplementationTransparency
		public void ensureCapacity(int minCapacity)
		{
			if (data.length < minCapacity)
			{
				//Enlarge
				int newCapacity = getNewCapacity(minCapacity);
				boolean[] newData = new boolean[newCapacity];
				System.arraycopy(data, 0, newData, 0, data.length);
				this.data = newData;
			}
		}
		
		/**
		 * This is also guaranteed not to return a capacity smaller than the provided minimum.
		 */
		@ImplementationTransparency
		public int getNewCapacity(int minCapacity)
		{
			if (minCapacity < 0)
				throw new IllegalArgumentException();
			
			return Math.max(minCapacity, grower.getNewSizeRecursive(data.length));  //The Math.max(min, ...) ensures that the grower can't break anything!  :3
		}
		
		
		
		
		
		
		
		/**
		 * sets the underlying array that backs the array list.
		 * Be careful, though, as updates can cause a new array to be created, obsoleting this one.
		 * Note, also, that only up to {@link #size()} elements will be used.
		 */
		@ImplementationTransparency
		public void setDirectBuffer(@LiveValue boolean[] array)
		{
			if (array.length < size)
				throw new IllegalArgumentException("Array is too small; size="+size+", capacity="+array.length);
			
			this.data = array;
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static class FixedLengthArrayWrapperBooleanList
	implements BooleanList, TransparentContiguousArrayBackedCollection<boolean[]>, KnowsLengthFixedness
	{
		protected final boolean[] underlying;
		protected final int offset, length;
		
		public FixedLengthArrayWrapperBooleanList(boolean[] underlying, int offset, int length)
		{
			rangeCheckIntervalByLength(underlying.length, offset, length);
			
			this.underlying = underlying;
			this.offset = offset;
			this.length = length;
		}
		
		public FixedLengthArrayWrapperBooleanList(boolean[] underlying)
		{
			this(underlying, 0, underlying.length);
		}
		
		public FixedLengthArrayWrapperBooleanList(Slice<boolean[]> underlying)
		{
			this(underlying.getUnderlying(), underlying.getOffset(), underlying.getLength());
		}
		
		@Override
		public BooleanList clone()
		{
			return new FixedLengthArrayWrapperBooleanList(toBooleanArray());
		}
		
		
		
		@Override
		public boolean[] toBooleanArray()
		{
			return sliceToNewBooleanArrayOP(this.getLiveContiguousArrayBackingUNSAFE());
		}
		
		@Override
		public Slice<boolean[]> getLiveContiguousArrayBackingUNSAFE()
		{
			return new Slice<>(underlying, offset, length);
		}
		
		@Override
		public int size()
		{
			return length;
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return true;
		}
		
		
		
		@Override
		public SimpleBooleanIterator newSimpleBooleanIterator()
		{
			return new SimpleBooleanIterator()
			{
				int index = 0;
				
				public boolean nextrpBoolean() throws StopIterationReturnPath
				{
					int i = index;
					
					if (i >= size())
					{
						throw StopIterationReturnPath.I;
					}
					else
					{
						index = i + 1;
						return underlying[offset+i];
					}
				}
			};
		}
		
		@Override
		public BooleanList subList(int fromIndex, int toIndex)
		{
			rangeCheckInterval(this.size(), fromIndex, toIndex);
			return new FixedLengthArrayWrapperBooleanList(this.underlying, fromIndex+offset, toIndex-fromIndex);
		}
		
		@Override
		public boolean getBoolean(int index)
		{
			return underlying[offset + index];
		}
		
		@Override
		public void setBoolean(int index, boolean value)
		{
			underlying[offset + index] = value;
		}
		
		
		
		
		
		
		
		
		
		//Unallowed Dim-altering Methods!
		
		@Override
		public void clear()
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
		public void insertBoolean(int index, boolean value)
		{
			throw new UnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	@ReadonlyValue
	public static BooleanList booleanArrayAsList(@LiveValue @WritableValue boolean[] array, int offset, int length)
	{
		return new FixedLengthArrayWrapperBooleanList(array, offset, length);
	}
	
	@ReadonlyValue
	public static BooleanList booleanArrayAsList(@LiveValue @WritableValue boolean[] array)
	{
		return booleanArrayAsList(array, 0, array.length);
	}
	
	@ReadonlyValue
	public static BooleanList booleanArrayAsList(@LiveValue @WritableValue Slice<boolean[]> arraySlice)
	{
		return booleanArrayAsList(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	
	
	
	@ThrowAwayValue
	public static BooleanList booleanArrayAsMutableList(@SnapshotValue @ReadonlyValue boolean[] array, int offset, int length)
	{
		return new BooleanArrayList(new FixedLengthArrayWrapperBooleanList(array, offset, length));
	}
	
	@ThrowAwayValue
	public static BooleanList booleanArrayAsMutableList(@SnapshotValue @ReadonlyValue boolean[] array)
	{
		return booleanArrayAsMutableList(array, 0, array.length);
	}
	
	@ThrowAwayValue
	public static BooleanList booleanArrayAsMutableList(@SnapshotValue @ReadonlyValue Slice<boolean[]> arraySlice)
	{
		return booleanArrayAsMutableList(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	@Immutable
	public static class ImmutableBooleanArrayList
	implements Serializable, Comparable<ImmutableBooleanArrayList>, BooleanList, KnowsLengthFixedness     //Let's not implement the general interfaces for accessing the underlying data jussssst to help make sure anyone who accesses it *knows* it's meant to be VERY MUCH READONLY!  XD'''
	{
		private static final long serialVersionUID = 1L;
		
		
		protected final boolean[] data;
		
		//Yeah I know it's bad practice for a thread-safe immutable object to cache things, but at best effort is wasted, no constraints are violated!     (If I'm wrong, fire me X'D )
		protected transient long hashCode;     //we cache it in case the booleanarray is large :33
		
		protected ImmutableBooleanArrayList(boolean[] LIVEDATA)
		{
			if (LIVEDATA == null) throw new NullPointerException();
			this.data = LIVEDATA;
		}
		
		
		
		
		
		/**
		 * DO NOT *EVER* MODIFY THE ARRAY YOU PASSED HERE AFTER CALLING THIS X"D
		 * ( THE HASH CODE IS CACHED FOR ONE, AND THIS MAY BE EMBEDDED IN HASHMAPS--THIS IS SUPPOSED TO BE IMMUTABLE AFTER ALL X'DDD )
		 * @see #getREADONLYLiveWholeArrayBackingUNSAFE()
		 */
		@ImplementationTransparency
		public static ImmutableBooleanArrayList newLIVE(@LiveValue boolean[] LIVEDATA)
		{
			return new ImmutableBooleanArrayList(LIVEDATA);
		}
		
		
		@ImplementationTransparency
		public static ImmutableBooleanArrayList newSingleton(boolean singleMember)
		{
			return new ImmutableBooleanArrayList(new boolean[]{singleMember});
		}
		
		
		
		
		public static ImmutableBooleanArrayList newCopying(@SnapshotValue List<Boolean> data)
		{
			if (data instanceof ImmutableBooleanArrayList)
				return (ImmutableBooleanArrayList)data;
			
			if (TransparentContiguousArrayBackedCollection.is(data))
			{
				Slice<Object> s = ((TransparentContiguousArrayBackedCollection)data).getLiveContiguousArrayBackingUNSAFE();
				if (s.getUnderlying() instanceof boolean[])
				{
					return newCopying((Slice)s);
				}
			}
			
			if (data instanceof BooleanList)
			{
				return newLIVE(((BooleanList)data).toBooleanArray());
			}
			else
			{
				int n = data.size();
				boolean[] a = new boolean[n];
				
				int i = 0;
				for (Boolean e : data)
				{
					if (i >= n)
						throw new ImpossibleException();
					
					a[i] = e;
					i++;
				}
				
				if (i != n)
					throw new ImpossibleException();
				
				return newLIVE(a);
			}
		}
		
		public static ImmutableBooleanArrayList newCopying(@SnapshotValue boolean[] data)
		{
			return new ImmutableBooleanArrayList(data.clone());
		}
		
		public static ImmutableBooleanArrayList newCopying(@SnapshotValue boolean[] data, int offset, int length)
		{
			if (offset == 0 && length == data.length)
				return newCopying(data);  //who knows, boolean[].clone() might be faster! \o/   (it certainly shouldn't be slower!!)
			else
			{
				boolean[] newArray = new boolean[length];
				System.arraycopy(data, offset, newArray, 0, length);
				return new ImmutableBooleanArrayList(newArray);
			}
		}
		
		public static ImmutableBooleanArrayList newCopying(@SnapshotValue Slice<boolean[]> data)
		{
			return newCopying(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		
		
		
		
		public static ImmutableBooleanArrayList newLIVEOrCopying(@PossiblySnapshotPossiblyLiveValue boolean[] data, int offset, int length)
		{
			if (offset == 0 && length == data.length)
				return newLIVE(data);
			else
				return newCopying(data, offset, length);
		}
		
		public static ImmutableBooleanArrayList newLIVEOrCopying(@PossiblySnapshotPossiblyLiveValue Slice<boolean[]> data)
		{
			return newLIVEOrCopying(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		
		
		
		
		
		
		public static final ImmutableBooleanArrayList Empty = ImmutableBooleanArrayList.newLIVE(ArrayUtilities.EmptyBooleanArray);
		
		
		
		
		
		
		
		
		@ThrowAwayValue
		@Override
		public boolean[] toBooleanArray()
		{
			return data.clone();
		}
		
		/**
		 * DO NOT MODIFY THIS X"D
		 * ( THE HASH CODE IS CACHED FOR ONE, AND THIS MAY BE EMBEDDED IN HASHMAPS--THIS IS SUPPOSED TO BE IMMUTABLE AFTER ALL X'DDD )
		 */
		@ImplementationTransparency
		@LiveValue
		@ReadonlyValue //!!!!
		public boolean[] getREADONLYLiveWholeArrayBackingUNSAFE()
		{
			return data;
		}
		
		@Override
		public int size()
		{
			return data.length;
		}
		
		@Override
		public boolean getBoolean(int index)
		{
			return data[index];
		}
		
		@Override
		public void getAllBooleans(int offsetInThisSource, @WritableValue boolean[] dest, int offsetInDest, int lengthInBoth)
		{
			System.arraycopy(this.data, offsetInThisSource, dest, offsetInDest, lengthInBoth);
		}
		
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return true;
		}
		
		
		
		
		@Override
		public int hashCode()
		{
			long hashCodeField = this.hashCode;
			
			boolean cached = hashCodeField != 0;
			
			
			if (cached)
			{
				return (int)this.hashCode;
			}
			else
			{
				int hashCode = Arrays.hashCode(this.data);
				this.hashCode = hashCode | 0xFFFF_FFFF_0000_0000l;  //hashCodes can be zero!  Not often XDD   But can be!  So it's not correct unless we account for that :>      (And if it was two fields then it'd be a concurrency bug!)
				return hashCode;
			}
		}
		
		
		
		
		@Override
		public boolean equals(@ReadonlyValue Object obj)
		{
			if (obj == this)
			{
				return true;
			}
			else if (obj instanceof ImmutableBooleanArrayList)
			{
				ImmutableBooleanArrayList other = (ImmutableBooleanArrayList)obj;
				
				//Checking the hashcodes is a VERY fast negative test and is almost always true-negative!! :DDD   (rarely it's false-positive, but never false-negative!)
				
				long thisHC = this.hashCode;
				long otherHC = other.hashCode;
				
				boolean thisHashCodeCached = thisHC != 0;
				boolean otherHashCodeCached = otherHC != 0;
				
				int thisHashCode = (int)thisHC;
				int otherHashCode = (int)otherHC;
				
				if (thisHashCodeCached)
				{
					if (otherHashCodeCached)
					{
						if (thisHashCode != otherHashCode)
							return false;
					}
					else
					{
						//Scanning one boolean array is faster than scanning two! :D
						if (thisHashCode != other.hashCode())
							return false;
					}
				}
				else
				{
					if (otherHashCodeCached)
					{
						//Scanning one boolean array is faster than scanning two! :D
						if (this.hashCode() != otherHashCode)
							return false;
					}
					else
					{
						//Then don't make any hash codes X3
					}
				}
				
				return this.equalsBooleanArray(other.data);
			}
			else
			{
				return false;
			}
		}
		
		
		@Override
		public int compareTo(@ReadonlyValue ImmutableBooleanArrayList other)
		{
			return this.compareToBooleanArray(other.data);
		}
		
		
		public boolean equalsBooleanArray(@ReadonlyValue boolean[] o)
		{
			boolean[] ourData = this.getREADONLYLiveWholeArrayBackingUNSAFE();
			boolean[] theirData = o;
			
			return Arrays.equals(ourData, theirData);
		}
		
		
		public int compareToBooleanArray(@ReadonlyValue boolean[] o)
		{
			boolean[] ourData = this.getREADONLYLiveWholeArrayBackingUNSAFE();
			boolean[] theirData = o;
			
			return ArrayUtilities.compareLittleEndianLengthsFirst(ourData, theirData);  //lengths-first is more efficient if they're different but hardly any less efficient if they're always the same!  A good default, don't you think? :D
		}
		
		
		
		
		@Override
		public ImmutableBooleanArrayList clone()
		{
			return this;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		// Unallowed Write Methods!
		
		@Override
		public void clear()
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setSizeBoolean(int newSize, boolean elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void insertBoolean(int index, boolean value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setBoolean(int index, boolean value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static BooleanList unmodifiableBooleanList(BooleanList booleanList)
	{
		return booleanList instanceof UnmodifiableBooleanListWrapper ? booleanList : new UnmodifiableBooleanListWrapper(booleanList);
	}
	
	public static class UnmodifiableBooleanListWrapper
	implements BooleanList, KnowsLengthFixedness, TransparentContiguousArrayBackedCollection
	{
		protected final BooleanList underlying;
		
		public UnmodifiableBooleanListWrapper(BooleanList underlying)
		{
			this.underlying = underlying;
		}
		
		@ImplementationTransparency
		public BooleanList getUnderlying()
		{
			return underlying;
		}
		
		
		
		
		
		@Override
		public UnmodifiableBooleanListWrapper clone()
		{
			return new UnmodifiableBooleanListWrapper(underlying.clone());
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.underlying);
		}
		
		@Override
		public boolean isTransparentContiguousArrayBackedCollection()
		{
			return TransparentContiguousArrayBackedCollection.is(underlying);
		}
		
		@Override
		public Slice getLiveContiguousArrayBackingUNSAFE()
		{
			return ((TransparentContiguousArrayBackedCollection)underlying).getLiveContiguousArrayBackingUNSAFE();
		}
		
		
		
		
		
		
		//Use default so they can't get around our readonly-ness!  ;D
		//	public Iterator<Boolean> iterator()
		//	public ListIterator<Boolean> listIterator()
		//	public ListIterator<Boolean> listIterator(int index)
		//	public SimpleIterator<Boolean> simpleIterator()
		//	public SimpleBooleanIterator newSimpleBooleanIterator()
		//	public BooleanList subList(int fromIndex, int toIndex)
		
		
		
		
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeRangeByLength(int start, int length) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void replaceAll(UnaryOperator<Boolean> operator)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeIf(Predicate<? super Boolean> filter)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void sort(Comparator<? super Boolean> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void clear()
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public Boolean remove(int index)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllBooleans(boolean[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllBooleans(Slice<boolean[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllBooleans(boolean[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllBooleans(Slice<boolean[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllBooleans(boolean[] a, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setBoolean(int index, boolean value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertBoolean(int index, boolean value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSizeBoolean(int newSize, boolean elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSize(int newSize, Boolean elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSize(int newSize)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllBooleans(int index, boolean[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllBooleans(int index, Slice<boolean[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllBooleans(int index, boolean[] array, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAll(int destIndex, List sourceU, int sourceIndex, int amount) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addBoolean(boolean value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll(Collection<? extends Boolean> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllBooleans(boolean[] elements, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeByIndex(int index)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeBooleanByIndex(int index) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeBoolean(boolean value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean retainAll(Collection<?> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllBooleans(int index, boolean[] array, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll(int index, Collection<? extends Boolean> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllBooleans(int index, boolean[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllBooleans(int index, Slice<boolean[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public Boolean set(int index, Boolean value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void add(int index, Boolean value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeAll(Collection<?> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean remove(Object o)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean add(Boolean e)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		
		
		
		
		
		
		
		
		
		public void forEach(Consumer<? super Boolean> action)
		{
			underlying.forEach(action);
		}
		
		public int size()
		{
			return underlying.size();
		}
		
		public boolean equals(Object o)
		{
			return underlying.equals(o);
		}
		
		public int hashCode()
		{
			return underlying.hashCode();
		}
		
		public Spliterator<Boolean> spliterator()
		{
			return underlying.spliterator();
		}
		
		public Stream<Boolean> stream()
		{
			return underlying.stream();
		}
		
		public Stream<Boolean> parallelStream()
		{
			return underlying.parallelStream();
		}
		
		public boolean getBoolean(int index)
		{
			return underlying.getBoolean(index);
		}
		
		public int indexOfBoolean(boolean value)
		{
			return underlying.indexOfBoolean(value);
		}
		
		public int lastIndexOfBoolean(boolean value)
		{
			return underlying.lastIndexOfBoolean(value);
		}
		
		public boolean equivalent(List<Boolean> other)
		{
			return underlying.equivalent(other);
		}
		
		public boolean[] toBooleanArray()
		{
			return underlying.toBooleanArray();
		}
		
		public boolean containsBoolean(boolean value)
		{
			return underlying.containsBoolean(value);
		}
		
		public Boolean get(int index)
		{
			return underlying.get(index);
		}
		
		public int indexOf(Object o)
		{
			return underlying.indexOf(o);
		}
		
		public int lastIndexOf(Object o)
		{
			return underlying.lastIndexOf(o);
		}
		
		public boolean containsAll(Collection<?> c)
		{
			return underlying.containsAll(c);
		}
		
		public boolean isEmpty()
		{
			return underlying.isEmpty();
		}
		
		public boolean contains(Object o)
		{
			return underlying.contains(o);
		}
		
		public Object[] toArray()
		{
			return underlying.toArray();
		}
		
		public <T> T[] toArray(T[] a)
		{
			return underlying.toArray(a);
		}
		
		public void storeBoxingIntoArray(Slice<Object[]> array)
		{
			underlying.storeBoxingIntoArray(array);
		}
		
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class BooleanTable
	{
		protected int width;
		protected boolean[] data;
		
		public BooleanTable(int width, int height)
		{
			if (width < 0)
				throw new IllegalArgumentException();
			if (height < 0)
				throw new IllegalArgumentException();
			
			data = new boolean[width * height];
		}
		
		
		
		@Nonnegative
		public int getNumberOfColumns()
		{
			return width;
		}
		
		@Nonnegative
		public int getNumberOfRows()
		{
			return data.length / width;
		}
		
		
		@Nonnegative
		public int getNumberOfCells()
		{
			return data.length;
		}
		
		
		
		
		
		public boolean getCellContentsBoolean(int columnIndex, int rowIndex) throws IndexOutOfBoundsException
		{
			if (columnIndex < 0) throw new IndexOutOfBoundsException();
			if (rowIndex < 0) throw new IndexOutOfBoundsException();
			if (columnIndex >= getNumberOfColumns()) throw new IndexOutOfBoundsException();
			if (rowIndex >= getNumberOfRows()) throw new IndexOutOfBoundsException();
			
			return this.data[rowIndex * this.getNumberOfColumns() + columnIndex];
		}
		
		public void setCellContentsBoolean(int columnIndex, int rowIndex, @Nullable boolean newValue) throws IndexOutOfBoundsException
		{
			if (columnIndex < 0) throw new IndexOutOfBoundsException();
			if (rowIndex < 0) throw new IndexOutOfBoundsException();
			if (columnIndex >= getNumberOfColumns()) throw new IndexOutOfBoundsException();
			if (rowIndex >= getNumberOfRows()) throw new IndexOutOfBoundsException();
			
			this.data[rowIndex * this.getNumberOfColumns() + columnIndex] = newValue;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Nullable
		public Boolean getCellContents(int columnIndex, int rowIndex) throws IndexOutOfBoundsException
		{
			return getCellContentsBoolean(columnIndex, rowIndex);
		}
		
		public void setCellContents(int columnIndex, int rowIndex, @Nullable Boolean newValue) throws IndexOutOfBoundsException
		{
			setCellContentsBoolean(columnIndex, rowIndex, newValue);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static byte getByte(List<Byte> list, int index)
	{
		if (list instanceof ByteList)
			return ((ByteList) list).getByte(index);
		else
			return list.get(index);
	}
	
	public static void setByte(List<Byte> list, int index, byte value)
	{
		if (list instanceof ByteList)
			((ByteList) list).setByte(index, value);
		else
			list.set(index, value);
	}
	
	
	
	
	
	
	
	public static interface SimpleByteIterable
	extends SimpleIterable<Byte>
	{
		public SimpleByteIterator newSimpleByteIterator();
		
		public default SimpleIterator<Byte> simpleIterator()
		{
			SimpleByteIterator i = this.newSimpleByteIterator();
			return () -> i.nextrpByte();
		}
	}
	
	
	public static interface SimpleByteIterator
	extends SimpleIterator<Byte>
	{
		public byte nextrpByte() throws StopIterationReturnPath;
		
		
		@Override
		public default Byte nextrp() throws StopIterationReturnPath
		{
			return nextrpByte();
		}
	}
	
	
	
	@SignalInterface
	public static interface ByteCollection
	extends PrimitiveCollection<Byte>, SimpleByteIterable
	{
		public boolean addByte(byte value);
		
		public boolean removeByte(byte value);
		
		
		
		
		
		@ThrowAwayValue
		public static byte[] defaultToByteArray(ByteCollection collection)
		{
			byte[] array = new byte[collection.size()];
			SimpleByteIterator i = collection.newSimpleByteIterator();
			
			int index = 0;
			while (true)
			{
				try
				{
					array[index] = i.nextrpByte();
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
				
				index++;
			}
			
			return array;
		}
		
		@ThrowAwayValue
		public default byte[] toByteArray()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(this.size(), u);
				
				if (u.getUnderlying() instanceof byte[])
				{
					return sliceToNewByteArrayOP((Slice<byte[]>) u);
				}
			}
			
			return defaultToByteArray(this);
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public default byte[] toByteArrayPossiblyLive()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				int size = this.size();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				Object und = u.getUnderlying();
				
				if (und instanceof byte[] && u.getOffset() == 0 && ((byte[])und).length == size)
				{
					return (byte[])und;
				}
			}
			
			return defaultToByteArray(this);
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public default Slice<byte[]> toByteArraySlicePossiblyLive()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(this.size(), u);
				
				if (u.getUnderlying() instanceof byte[])
				{
					return (Slice<byte[]>) u;
				}
			}
			
			return wholeArraySliceByte(defaultToByteArray(this));
		}
		
		
		
		
		
		
		public default boolean containsByte(byte value)
		{
			SimpleByteIterator i = newSimpleByteIterator();
			
			while (true)
			{
				try
				{
					if (eqSane(i.nextrpByte(), value))
						return true;
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
			}
			
			return false;
		}
		
		
		
		
		
		
		@Override
		public default Iterator<Byte> iterator()
		{
			return SimpleByteIterable.super.iterator();
		}
		
		@Override
		public default boolean isEmpty()
		{
			return size() == 0;
		}
		
		
		
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllBytes(byte[] array)
		{
			return addAllBytes(array, 0, array.length);
		}
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllBytes(Slice<byte[]> arraySlice)
		{
			return addAllBytes(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllBytes(byte[] elements, int offset, int length)
		{
			return defaultAddAllBytes(this, elements, offset, length);
		}
		
		public static boolean defaultAddAllBytes(ByteCollection self, byte[] elements, int offset, int length)
		{
			boolean changedAtAll = false;
			int e = offset + length;
			for (int i = offset; i < e; i++)
				changedAtAll |= self.addByte(elements[i]);
			return changedAtAll;
		}
		
		
		
		
		public default void removeAllBytes(byte[] array)
		{
			removeAllBytes(array, 0, array.length);
		}
		
		public default void removeAllBytes(Slice<byte[]> arraySlice)
		{
			removeAllBytes(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public default void removeAllBytes(byte[] a, int offset, int length)
		{
			int e = offset + length;
			for (int i = offset; i < e; i++)
				this.removeByte(a[i]);
		}
		
		
		
		
		
		
		
		
		
		
		@Override
		public default boolean contains(Object o)
		{
			return o instanceof Byte && containsByte((Byte)o);
		}
		
		
		@Override
		public default boolean add(Byte e)
		{
			return addByte(e);
		}
		
		
		@Override
		public default boolean remove(Object o)
		{
			return o instanceof Byte && removeByte((Byte)o);
		}
		
		
		@Override
		public default boolean containsAll(Collection<?> c)
		{
			if (c instanceof ByteCollection)
			{
				ByteCollection cc = (ByteCollection) c;
				
				SimpleByteIterator i = cc.newSimpleByteIterator();
				while (true)
				{
					try
					{
						if (this.containsByte(i.nextrpByte()))
							return true;
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return false;
			}
			else
			{
				for (Object e : c)
					if (this.contains(e))
						return true;
				return false;
			}
		}
		
		
		@Override
		public default boolean addAll(Collection<? extends Byte> c)
		{
			if (c instanceof ByteCollection)
			{
				boolean changedAtAll = false;
				
				SimpleByteIterator i = ((ByteCollection)c).newSimpleByteIterator();
				
				while (true)
				{
					try
					{
						changedAtAll |= this.add(i.nextrpByte());
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (Byte e : c)
				{
					changedAtAll |= this.add(e);
				}
				
				return changedAtAll;
			}
		}
		
		
		
		@Override
		public default boolean removeAll(Collection<?> c)
		{
			if (c instanceof ByteCollection)
			{
				boolean changedAtAll = false;
				
				SimpleByteIterator i = ((ByteCollection)c).newSimpleByteIterator();
				
				while (true)
				{
					try
					{
						changedAtAll |= this.remove(i.nextrpByte());
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (Object e : c)
				{
					changedAtAll |= this.remove(e);
				}
				
				return changedAtAll;
			}
		}
		
		
		
		public default String _toString()
		{
			StringBuilder b = new StringBuilder("[");
			
			SimpleByteIterator i = newSimpleByteIterator();
			
			boolean first = true;
			
			while (true)
			{
				byte e;
				
				try
				{
					e = i.nextrpByte();
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
				
				
				
				//Notice this comes after the possible 'break'!  ^_~
				if (first)
					first = false;
				else
					b.append(", ");
				
				b.append(e);
			}
			
			b.append(']');
			
			return b.toString();
		}
	}
	
	
	
	public static interface DefaultToArraysByteCollection
	extends ByteCollection
	{
		@Override
		public default Object[] toArray()
		{
			Object[] a = new Object[size()];
			storeBoxingIntoArray(wholeArraySliceReference(a));
			return a;
		}
		
		@Override
		public default <T> T[] toArray(T[] a)
		{
			if (a.length >= this.size())
			{
				storeBoxingIntoArray(wholeArraySliceReference(a));
				return a;
			}
			else
			{
				Object[] aa;
				
				if (a.getClass() == Byte[].class)
				{
					aa = new Byte[size()];
				}
				else if (a.getClass() == Object[].class)
				{
					aa = new Object[size()];
				}
				else
				{
					throw new StructuredClassCastException(a.getClass());
				}
				
				storeBoxingIntoArray(wholeArraySliceReference(aa));
				
				return (T[])aa;
			}
		}
		
		public void storeBoxingIntoArray(Slice<Object[]> array);
	}
	
	
	
	
	
	
	@SignalInterface
	public static interface ByteListRO
	{
		public byte getByte(int index);
		
		public int size();
		
		
		
		public default int indexOfByte(byte value)
		{
			int n = this.size();
			for (int i = 0; i < n; i++)
				if (eqSane(this.getByte(i), value))
					return i;
			return -1;
		}
		
		public default int lastIndexOfByte(byte value)
		{
			int i = this.size()-1;
			while (i >= 0)
			{
				if (eqSane(this.getByte(i), value))
					return i;
				break;
			}
			return i;   // ;D
		}
		
		
		
		public default boolean equivalent(List<Byte> other)
		{
			int size = this.size();
			if (other.size() != size)
				return false;
			
			
			if (other instanceof ByteListRO)  //All ByteLists will implement this interface too so this accounts for them as well :3
			{
				return equivalentFixedRO((ByteListRO)other);
			}
			else
			{
				for (int i = 0; i < size; i++)
				{
					if (!eqSane(this.getByte(i), other.get(i).byteValue()))
						return false;
				}
			}
			
			return true;
		}
		
		
		
		public default boolean equivalentFixedRO(ByteListRO other)
		{
			int size = this.size();
			if (other.size() != size)
				return false;
			
			for (int i = 0; i < size; i++)
			{
				if (!eqSane(this.getByte(i), other.getByte(i)))
					return false;
			}
			
			return true;
		}
	}
	
	
	
	@SignalInterface
	public static interface ByteListRWFixed
	extends ByteListRO
	{
		public void setByte(int index, byte value);
	}
	
	
	
	@SignalInterface
	public static interface ByteList
	extends ByteCollection, NiceList<Byte>, ByteListRO, ByteListRWFixed, ListWithRemoveRange, DefaultToArraysByteCollection, PubliclyCloneable, ListWithSetSize<Byte>, ListWithSetAll, NonuniformMethodsForByteList
	{
		public void insertByte(int index, byte value);
		
		
		
		/**
		 * This should be equivalent to just constructing and returning a new in-memory stock implementation with the same contents  (but can easily be faster, especially for those stock impl.s! XD )
		 * As for whether it's fixed-length or read-only or duplicateless or etc., that should be the same in the clone as it is in the main impl.
		 * If *and only if* it is immutable, this is allowed to return this same instance!  (ie the body of this method being <code>return this;</code>)
		 */
		public ByteList clone();
		
		
		/**
		 * If size is smaller than the current, then this is equivalent to {@link #removeRange(int, int) removeRange}(newSize, this.{@link #size() size()})  :3
		 * If size is larger, then this appends newSize - this.{@link #size() size()} elementToAddIfGrowing's to the end like that many {@link #addByte(byte)}  :D
		 */
		public void setSizeByte(int newSize, byte elementToAddIfGrowing);
		
		public default void setSize(int newSize, Byte elementToAddIfGrowing)
		{
			setSizeByte(newSize, elementToAddIfGrowing);
		}
		
		public default void setSize(int newSize)
		{
			setSizeByte(newSize, ((byte)0));
		}
		
		
		
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void setAllBytes(int index, byte[] array)
		{
			setAllBytes(index, array, 0, array.length);
		}
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void setAllBytes(int index, Slice<byte[]> arraySlice)
		{
			setAllBytes(index, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		public default void setAllBytes(int start, byte[] array, int offset, int length)
		{
			int size = this.size();
			
			rangeCheckIntervalByLength(size, start, length);
			rangeCheckIntervalByLength(array.length, offset, length);
			
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(size, start, length);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				if (u.getUnderlying() instanceof byte[])
				{
					Slice<byte[]> s = (Slice<byte[]>) u;
					
					System.arraycopy(array, offset, s.getUnderlying(), s.getOffset()+start, length);
					
					return;
				}
			}
			
			
			defaultSetAllBytes(this, start, array, offset, length);
		}
		
		
		public static void defaultSetAllBytes(ByteList list, int start, @WritableValue byte[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				list.setByte(start + i, array[offset + i]);
		}
		
		
		
		
		
		@Override
		public default void setAll(int destIndex, List sourceU, int sourceIndex, @Nonnegative int amount) throws IndexOutOfBoundsException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			List<Byte> source = sourceU;
			ByteList dest = this;
			
			int sourceSize = source.size();
			int destSize = dest.size();
			rangeCheckIntervalByLength(sourceSize, sourceIndex, amount);
			rangeCheckIntervalByLength(destSize, destIndex, amount);
			
			
			
			if (TransparentContiguousArrayBackedCollection.is(source))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection<?>)source).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(sourceSize, sourceIndex, amount);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(sourceSize, u);
				
				if (u.getUnderlying() instanceof byte[])
				{
					Slice<byte[]> s = (Slice<byte[]>)u;
					this.setAllBytes(destIndex, s.subslice(sourceIndex, amount));
					return;
				}
			}
			
			
			
			
			if (source instanceof ByteList)
			{
				ByteList primSource = (ByteList) source;
				
				if (destIndex < sourceIndex)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
				{
					for (int i = 0; i < amount; i++)
						dest.setByte(destIndex+i, primSource.getByte(sourceIndex+i));
				}
				else
				{
					for (int i = amount-1; i >= 0; i--)
						dest.setByte(destIndex+i, primSource.getByte(sourceIndex+i));
				}
			}
			else
			{
				if (destIndex < sourceIndex)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
				{
					for (int i = 0; i < amount; i++)
						dest.set(destIndex+i, source.get(sourceIndex+i));
				}
				else
				{
					for (int i = amount-1; i >= 0; i--)
						dest.set(destIndex+i, source.get(sourceIndex+i));
				}
			}
		}
		
		
		
		
		
		
		
		/**
		 * Copies <code>array.length</code> elements into <code>array</code> starting with the <code>start</code>th element.<br>
		 */
		public default void getAllBytes(int start, @WritableValue byte[] array, int offset, int length)
		{
			int size = this.size();
			
			rangeCheckIntervalByLength(size, start, length);
			rangeCheckIntervalByLength(array.length, offset, length);
			
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(size, start, length);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				if (u.getUnderlying() instanceof byte[])
				{
					Slice<byte[]> s = (Slice<byte[]>) u;
					
					System.arraycopy(s.getUnderlying(), s.getOffset()+start, array, offset, length);
					
					return;
				}
			}
			
			
			defaultGetAllBytes(this, start, array, offset, length);
		}
		
		
		public static void defaultGetAllBytes(ByteList list, int start, @WritableValue byte[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				array[offset + i] = list.getByte(start + i);
		}
		
		
		
		
		
		
		@ThrowAwayValue
		public default byte[] getAllBytes(int start, int end)
		{
			rangeCheckInterval(this.size(), start, end);
			
			byte[] buff = new byte[end-start];
			getAllBytes(start, buff, 0, buff.length);
			return buff;
		}
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public default boolean addByte(byte value)
		{
			this.insertByte(this.size(), value);
			return true;
		}
		
		@Override
		public default boolean addAll(Collection<? extends Byte> c)
		{
			this.addAll(this.size(), c);
			return true;
		}
		
		@Override
		public default boolean addAllBytes(byte[] elements, int offset, int length)
		{
			insertAllBytes(this.size(), elements, offset, length);
			return true;
		}
		
		public default void removeByIndex(int index)
		{
			removeRange(index, index+1);
		}
		
		public default byte removeByteByIndex(int index) throws IndexOutOfBoundsException
		{
			byte v = this.getByte(index);
			removeByIndex(index);
			return v;
		}
		
		@Override
		public default boolean removeByte(byte value)
		{
			int i = this.indexOfByte(value);
			
			if (i != -1)
			{
				this.removeByIndex(i);
				return true;
			}
			else
			{
				return false;
			}
		}
		
		@Override
		public default boolean retainAll(Collection<?> c)
		{
			if (c instanceof ByteCollection)
			{
				ByteCollection cc = (ByteCollection) c;
				
				boolean changedAtAll = false;
				
				for (int i = size() - 1; i >= 0; i--)
				{
					if (!cc.containsByte(getByte(i)))
					{
						removeByteByIndex(i);
						changedAtAll = true;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (int i = size() - 1; i >= 0; i--)
				{
					if (!c.contains(getByte(i)))
					{
						removeByteByIndex(i);
						changedAtAll = true;
					}
				}
				
				return changedAtAll;
			}
		}
		
		
		
		
		
		
		
		
		@Override
		public default SimpleByteIterator newSimpleByteIterator()
		{
			return new SimpleByteIterator()
			{
				int index = 0;
				
				public byte nextrpByte() throws StopIterationReturnPath
				{
					int i = index;
					
					if (i >= size())
					{
						throw StopIterationReturnPath.I;
					}
					else
					{
						index = i + 1;
						return getByte(i);
					}
				}
			};
		}
		
		
		
		
		
		@Override
		public default boolean containsByte(byte value)
		{
			return indexOfByte(value) != -1;
		}
		
		
		public default void insertAllBytes(int index, byte[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				insertByte(index+i, array[offset+i]);
		}
		
		
		@Override
		public default boolean addAll(int index, Collection<? extends Byte> c)
		{
			if (c instanceof ByteCollection)
			{
				ByteCollection cc = (ByteCollection)c;
				
				SimpleByteIterator i = cc.newSimpleByteIterator();
				while (true)
				{
					try
					{
						insertByte(index, i.nextrpByte());
						index++;  //doesn't matter if this gets executed on the last iteration :3
					}
					catch (StopIterationReturnPath rp)
					{
						break;
					}
				}
			}
			else
			{
				for (Byte e : c)
				{
					insertByte(index, e);
					index++;
				}
			}
			
			return true;
		}
		
		
		
		public default void insertAllBytes(int index, byte[] array)
		{
			insertAllBytes(index, array, 0, array.length);
		}
		
		public default void insertAllBytes(int index, Slice<byte[]> arraySlice)
		{
			insertAllBytes(index, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		
		
		@Override
		public default Byte get(int index)
		{
			return this.getByte(index);
		}
		
		@Override
		public default Byte set(int index, Byte value)
		{
			Byte previous = get(index);
			this.setByte(index, value);
			return previous;
		}
		
		
		@Override
		public default void add(int index, Byte value)
		{
			this.insertByte(index, value);
		}
		
		@Override
		public default Byte remove(int index) throws IndexOutOfBoundsException
		{
			return this.removeByteByIndex(index);
		}
		
		
		@Override
		public default int indexOf(Object o)
		{
			return o instanceof Byte ? indexOfByte((Byte)o) : -1;
		}
		
		@Override
		public default int lastIndexOf(Object o)
		{
			return o instanceof Byte ? lastIndexOfByte((Byte)o) : -1;
		}
		
		
		
		@Override
		public default ListIterator<Byte> listIterator()
		{
			return listIterator(0);
		}
		
		@Override
		public default ListIterator<Byte> listIterator(int index)
		{
			//Todo
			throw new NotYetImplementedException();
		}
		
		@Override
		public default ByteList subList(int fromIndex, int toIndex)   //Also note that we narrow the return types of this :3
		{
			if (fromIndex == 0 && toIndex == this.size())
				return this;
			
			if (this instanceof Sublist)
			{
				Sublist<Byte> s = (Sublist<Byte>)this;
				return new ByteSublist((ByteList)s.getUnderlying(), s.getSublistStartingIndex() + fromIndex, toIndex - fromIndex);
			}
			else
			{
				return new ByteSublist(this, fromIndex, toIndex - fromIndex);
			}
		}
		
		
		
		
		
		
		
		
		
		
		// Multiple-Inheritance conflict resolvers X'3
		@Override
		public default boolean removeAll(Collection<?> c)
		{
			return DefaultToArraysByteCollection.super.removeAll(c);
		}
		
		@Override
		public default boolean remove(Object o)
		{
			return DefaultToArraysByteCollection.super.remove(o);
		}
		
		@Override
		public default boolean containsAll(Collection<?> c)
		{
			return DefaultToArraysByteCollection.super.containsAll(c);
		}
		
		@Override
		public default boolean add(Byte e)
		{
			return DefaultToArraysByteCollection.super.add(e);
		}
		
		@Override
		public default boolean isEmpty()
		{
			return DefaultToArraysByteCollection.super.isEmpty();
		}
		
		@Override
		public default boolean contains(Object o)
		{
			return DefaultToArraysByteCollection.super.contains(o);
		}
		
		
		
		
		
		@Override
		public default Object[] toArray()
		{
			return DefaultToArraysByteCollection.super.toArray();
		}
		
		@Override
		public default <T> T[] toArray(T[] a)
		{
			return DefaultToArraysByteCollection.super.toArray(a);
		}
		
		
		
		
		
		
		
		@Override
		public default void storeBoxingIntoArray(Slice<Object[]> array)
		{
			int n = this.size();
			for (int i = 0; i < n; i++)
				ArrayUtilities.setReference((Slice)array, i, this.get(i));
		}
		
		
		
		
		
		
		//Narrow the return types of these :3
		
		@Override
		public default ByteList subListToEnd(int start)
		{
			return (ByteList)NiceList.super.subListToEnd(start);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
		
		@Override
		public default ByteList subListFromBeginning(int lengthWhichIsEndExclusiveInThisCaseXD)
		{
			return (ByteList)NiceList.super.subListFromBeginning(lengthWhichIsEndExclusiveInThisCaseXD);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
		
		@Override
		public default ByteList subListByLength(int start, int length)
		{
			return (ByteList)NiceList.super.subListByLength(start, length);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
	}
	
	
	
	
	
	
	public static interface DefaultShiftingBasedByteList
	extends ByteList
	{
		public default void clear()
		{
			removeRange(0, size());
		}
		
		@Override
		public default void insertByte(int index, byte value)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, 1);
			this.setByte(index, value);
		}
		
		@Override
		public default void insertAllBytes(int index, byte[] array, int offset, int length)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, length);
			
			this.setAllBytes(index, array, offset, length);
		}
		
		
		@Override
		public default boolean addAll(int index, Collection<? extends Byte> c)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			int amount = c.size();  //copy this incase c == this! XD
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, amount);
			
			if (c instanceof List)
			{
				listcopy((List)c, 0, this, index, amount);
			}
			else
			{
				if (c instanceof ByteCollection)
				{
					SimpleByteIterator iterator = ((ByteCollection)c).newSimpleByteIterator();
					
					int i = 0;
					while (true)
					{
						byte e;
						try
						{
							e = iterator.nextrpByte();
						}
						catch (StopIterationReturnPath rp)
						{
							break;
						}
						
						this.setByte(index+i, e);
						i++;
					}
				}
				else
				{
					int i = 0;
					for (Byte e : c)
					{
						this.set(index+i, e);
						i++;
					}
				}
			}
			
			return true;
		}
		
		@Override
		public default void setSizeByte(int newSize, byte elementToAddIfGrowing)
		{
			int oldSize = this.size();
			int amount = newSize - oldSize;
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, oldSize, amount);
			
			//Growing results in *undefined* contents, not necessarily initialized ones!  (eg, it may already have some/enough elements allocated eg, in an underlying array and it just increases the int size field :3 )
			for (int i = oldSize; i < newSize; i++)
				this.setByte(i, elementToAddIfGrowing);
		}
		
		
		
		
		@Override
		public default void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			rangeCheckInterval(this.size(), start, pastEnd);
			
			int length = pastEnd - start;
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, pastEnd, -length);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ByteSublist
	implements ByteList, DefaultShiftingBasedByteList, Sublist<Byte>, ShiftableList
	{
		protected final ByteList underlying;
		protected final int start;
		protected int size;
		
		public ByteSublist(ByteList underlying, int start, int initialSize)
		{
			rangeCheckIntervalByLength(underlying.size(), start, initialSize);
			
			this.underlying = underlying;
			this.start = start;
			this.size = initialSize;
		}
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public byte getByte(int index)
		{
			return underlying.getByte(index + start);
		}
		
		@Override
		public void setByte(int index, byte value)
		{
			underlying.setByte(index + start, value);
		}
		
		@Override
		public void shiftRegionStretchingFromIndexToEndByAmountChangingSize(int start, int amount)
		{
			rangeCheckCursorPoint(size(), start);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(underlying, start, amount);
			
			size += amount;
		}
		
		
		
		@Override
		public ByteList clone()
		{
			ByteList c = new ByteArrayList();
			c.addAll(this);
			return c;
		}
		
		
		
		@Override
		public ByteList getUnderlying()
		{
			return underlying;
		}
		
		@Override
		public int getSublistStartingIndex()
		{
			return start;
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ByteArrayList
	implements DefaultShiftingBasedByteList, ListWithSetSize<Byte>, Trimmable, TransparentContiguousArrayBackedCollection<byte[]>, KnowsLengthFixedness
	{
		protected int size = 0;
		protected byte[] data;
		protected GrowerComputationallyUnreducedPurelyRecursive grower;
		
		
		
		
		//In case subclasses don't want to deal with the other things ^^'
		protected ByteArrayList(Void thisiswhyyoushouldntusejavaconstructorsAlthoughIdontknowhowtomakeinheritanceworkwellwithoutinitmethodsorfactoriesorsomething)
		{
		}
		
		
		/**
		 * Capacity increment is different from Vector's.  It is the percentage of the capacity by which the capacity is increased.
		 * The formula being: <code>newCapacity = capacity + (capacity * capacityIncrement)</code><br>
		 * @param grower This can be anything.  The only downside is performance: all methods will add at least the minimum amount required regardless of what this specifies.
		 */
		public ByteArrayList(int initialCapacity, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this.data = initialCapacity == 0 ? ArrayUtilities.EmptyByteArray : new byte[initialCapacity];
			this.grower = grower;
		}
		
		public ByteArrayList(@LiveValue @WritableValue byte[] LIVEBACKING, int size, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this.data = LIVEBACKING;
			this.grower = grower;
			this.size = size;
		}
		
		public ByteArrayList(@SnapshotValue @ReadonlyValue Collection<Byte> c, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this(c.size(), grower);
			this.addAll(c);
		}
		
		
		
		
		
		public ByteArrayList()
		{
			this(DefaultPrimitiveArrayListInitialCapacity);
		}
		
		public ByteArrayList(int initialCapacity)
		{
			this(initialCapacity, defaultPrimitiveArrayListGrower(initialCapacity));
		}
		
		public ByteArrayList(@SnapshotValue @ReadonlyValue Collection<Byte> c)
		{
			this(c, defaultPrimitiveArrayListGrower(c.size()));
		}
		
		public ByteArrayList(@LiveValue @WritableValue byte[] LIVEBACKING, int size)
		{
			this(LIVEBACKING, size, defaultPrimitiveArrayListGrower(LIVEBACKING.length));
		}
		
		public ByteArrayList(@LiveValue @WritableValue byte[] LIVEBACKING)
		{
			this(LIVEBACKING, LIVEBACKING.length);
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return false;
		}
		
		
		
		@Override
		public void setSize(int newSize)
		{
			ensureCapacity(newSize);
			this.size = newSize;
			
			// :D
		}
		
		
		
		
		
		//This pattern is nicer for subclassing in any Java class! :D
		protected void setupClone(ByteArrayList freshClone)
		{
			freshClone.size = this.size;
			
			freshClone.data = new byte[size];
			System.arraycopy(this.data, 0, freshClone.data, 0, size);
			
			freshClone.grower = this.grower;
		}
		
		@Override
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public ByteArrayList clone()
		{
			ByteArrayList clone = new ByteArrayList();
			this.setupClone(clone);
			return clone;
		}
		
		
		
		
		
		
		
		
		
		
		//This is what unlocks all the System.arraycopy() magic!  8>
		@ImplementationTransparency
		@LiveValue
		public Slice<byte[]> getLiveContiguousArrayBackingUNSAFE()
		{
			return new Slice(data, 0, size);
		}
		
		
		
		
		
		
		
		
		
		public void setByte(int index, byte value)
		{
			rangeCheckMember(this.size, index);
			data[index] = value;
		}
		
		public byte getByte(int index)
		{
			rangeCheckMember(this.size(), index);
			return data[index];
		}
		
		
		
		
		
		
		
		@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public void clear()
		{
			size = 0;
		}
		
		@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public int size()
		{
			return size;
		}
		
		@ImplementationTransparency
		public void trimToSize()
		{
			byte[] newdata = new byte[size];
			System.arraycopy(data, 0, newdata, 0, size);
			this.data = newdata;
		}
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.trimToSize();
			return TrimmableTrimRV.DontKeepInvoking;
		}
		
		
		
		
		
		@ImplementationTransparency
		public int getCapacity()
		{
			return data.length;
		}
		
		@ImplementationTransparency
		public void ensureCapacity(int minCapacity)
		{
			if (data.length < minCapacity)
			{
				//Enlarge
				int newCapacity = getNewCapacity(minCapacity);
				byte[] newData = new byte[newCapacity];
				System.arraycopy(data, 0, newData, 0, data.length);
				this.data = newData;
			}
		}
		
		/**
		 * This is also guaranteed not to return a capacity smaller than the provided minimum.
		 */
		@ImplementationTransparency
		public int getNewCapacity(int minCapacity)
		{
			if (minCapacity < 0)
				throw new IllegalArgumentException();
			
			return Math.max(minCapacity, grower.getNewSizeRecursive(data.length));  //The Math.max(min, ...) ensures that the grower can't break anything!  :3
		}
		
		
		
		
		
		
		
		/**
		 * sets the underlying array that backs the array list.
		 * Be careful, though, as updates can cause a new array to be created, obsoleting this one.
		 * Note, also, that only up to {@link #size()} elements will be used.
		 */
		@ImplementationTransparency
		public void setDirectBuffer(@LiveValue byte[] array)
		{
			if (array.length < size)
				throw new IllegalArgumentException("Array is too small; size="+size+", capacity="+array.length);
			
			this.data = array;
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static class FixedLengthArrayWrapperByteList
	implements ByteList, TransparentContiguousArrayBackedCollection<byte[]>, KnowsLengthFixedness
	{
		protected final byte[] underlying;
		protected final int offset, length;
		
		public FixedLengthArrayWrapperByteList(byte[] underlying, int offset, int length)
		{
			rangeCheckIntervalByLength(underlying.length, offset, length);
			
			this.underlying = underlying;
			this.offset = offset;
			this.length = length;
		}
		
		public FixedLengthArrayWrapperByteList(byte[] underlying)
		{
			this(underlying, 0, underlying.length);
		}
		
		public FixedLengthArrayWrapperByteList(Slice<byte[]> underlying)
		{
			this(underlying.getUnderlying(), underlying.getOffset(), underlying.getLength());
		}
		
		@Override
		public ByteList clone()
		{
			return new FixedLengthArrayWrapperByteList(toByteArray());
		}
		
		
		
		@Override
		public byte[] toByteArray()
		{
			return sliceToNewByteArrayOP(this.getLiveContiguousArrayBackingUNSAFE());
		}
		
		@Override
		public Slice<byte[]> getLiveContiguousArrayBackingUNSAFE()
		{
			return new Slice<>(underlying, offset, length);
		}
		
		@Override
		public int size()
		{
			return length;
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return true;
		}
		
		
		
		@Override
		public SimpleByteIterator newSimpleByteIterator()
		{
			return new SimpleByteIterator()
			{
				int index = 0;
				
				public byte nextrpByte() throws StopIterationReturnPath
				{
					int i = index;
					
					if (i >= size())
					{
						throw StopIterationReturnPath.I;
					}
					else
					{
						index = i + 1;
						return underlying[offset+i];
					}
				}
			};
		}
		
		@Override
		public ByteList subList(int fromIndex, int toIndex)
		{
			rangeCheckInterval(this.size(), fromIndex, toIndex);
			return new FixedLengthArrayWrapperByteList(this.underlying, fromIndex+offset, toIndex-fromIndex);
		}
		
		@Override
		public byte getByte(int index)
		{
			return underlying[offset + index];
		}
		
		@Override
		public void setByte(int index, byte value)
		{
			underlying[offset + index] = value;
		}
		
		
		
		
		
		
		
		
		
		//Unallowed Dim-altering Methods!
		
		@Override
		public void clear()
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setSizeByte(int newSize, byte elementToAddIfGrowing)
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void insertByte(int index, byte value)
		{
			throw new UnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	@ReadonlyValue
	public static ByteList byteArrayAsList(@LiveValue @WritableValue byte[] array, int offset, int length)
	{
		return new FixedLengthArrayWrapperByteList(array, offset, length);
	}
	
	@ReadonlyValue
	public static ByteList byteArrayAsList(@LiveValue @WritableValue byte[] array)
	{
		return byteArrayAsList(array, 0, array.length);
	}
	
	@ReadonlyValue
	public static ByteList byteArrayAsList(@LiveValue @WritableValue Slice<byte[]> arraySlice)
	{
		return byteArrayAsList(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	
	
	
	@ThrowAwayValue
	public static ByteList byteArrayAsMutableList(@SnapshotValue @ReadonlyValue byte[] array, int offset, int length)
	{
		return new ByteArrayList(new FixedLengthArrayWrapperByteList(array, offset, length));
	}
	
	@ThrowAwayValue
	public static ByteList byteArrayAsMutableList(@SnapshotValue @ReadonlyValue byte[] array)
	{
		return byteArrayAsMutableList(array, 0, array.length);
	}
	
	@ThrowAwayValue
	public static ByteList byteArrayAsMutableList(@SnapshotValue @ReadonlyValue Slice<byte[]> arraySlice)
	{
		return byteArrayAsMutableList(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	@Immutable
	public static class ImmutableByteArrayList
	implements Serializable, Comparable<ImmutableByteArrayList>, ByteList, KnowsLengthFixedness     //Let's not implement the general interfaces for accessing the underlying data jussssst to help make sure anyone who accesses it *knows* it's meant to be VERY MUCH READONLY!  XD'''
	{
		private static final long serialVersionUID = 1L;
		
		
		protected final byte[] data;
		
		//Yeah I know it's bad practice for a thread-safe immutable object to cache things, but at best effort is wasted, no constraints are violated!     (If I'm wrong, fire me X'D )
		protected transient long hashCode;     //we cache it in case the bytearray is large :33
		
		protected ImmutableByteArrayList(byte[] LIVEDATA)
		{
			if (LIVEDATA == null) throw new NullPointerException();
			this.data = LIVEDATA;
		}
		
		
		
		
		
		/**
		 * DO NOT *EVER* MODIFY THE ARRAY YOU PASSED HERE AFTER CALLING THIS X"D
		 * ( THE HASH CODE IS CACHED FOR ONE, AND THIS MAY BE EMBEDDED IN HASHMAPS--THIS IS SUPPOSED TO BE IMMUTABLE AFTER ALL X'DDD )
		 * @see #getREADONLYLiveWholeArrayBackingUNSAFE()
		 */
		@ImplementationTransparency
		public static ImmutableByteArrayList newLIVE(@LiveValue byte[] LIVEDATA)
		{
			return new ImmutableByteArrayList(LIVEDATA);
		}
		
		
		@ImplementationTransparency
		public static ImmutableByteArrayList newSingleton(byte singleMember)
		{
			return new ImmutableByteArrayList(new byte[]{singleMember});
		}
		
		
		
		
		public static ImmutableByteArrayList newCopying(@SnapshotValue List<Byte> data)
		{
			if (data instanceof ImmutableByteArrayList)
				return (ImmutableByteArrayList)data;
			
			if (TransparentContiguousArrayBackedCollection.is(data))
			{
				Slice<Object> s = ((TransparentContiguousArrayBackedCollection)data).getLiveContiguousArrayBackingUNSAFE();
				if (s.getUnderlying() instanceof byte[])
				{
					return newCopying((Slice)s);
				}
			}
			
			if (data instanceof ByteList)
			{
				return newLIVE(((ByteList)data).toByteArray());
			}
			else
			{
				int n = data.size();
				byte[] a = new byte[n];
				
				int i = 0;
				for (Byte e : data)
				{
					if (i >= n)
						throw new ImpossibleException();
					
					a[i] = e;
					i++;
				}
				
				if (i != n)
					throw new ImpossibleException();
				
				return newLIVE(a);
			}
		}
		
		public static ImmutableByteArrayList newCopying(@SnapshotValue byte[] data)
		{
			return new ImmutableByteArrayList(data.clone());
		}
		
		public static ImmutableByteArrayList newCopying(@SnapshotValue byte[] data, int offset, int length)
		{
			if (offset == 0 && length == data.length)
				return newCopying(data);  //who knows, byte[].clone() might be faster! \o/   (it certainly shouldn't be slower!!)
			else
			{
				byte[] newArray = new byte[length];
				System.arraycopy(data, offset, newArray, 0, length);
				return new ImmutableByteArrayList(newArray);
			}
		}
		
		public static ImmutableByteArrayList newCopying(@SnapshotValue Slice<byte[]> data)
		{
			return newCopying(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		
		
		
		
		public static ImmutableByteArrayList newLIVEOrCopying(@PossiblySnapshotPossiblyLiveValue byte[] data, int offset, int length)
		{
			if (offset == 0 && length == data.length)
				return newLIVE(data);
			else
				return newCopying(data, offset, length);
		}
		
		public static ImmutableByteArrayList newLIVEOrCopying(@PossiblySnapshotPossiblyLiveValue Slice<byte[]> data)
		{
			return newLIVEOrCopying(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		
		
		
		
		
		
		public static final ImmutableByteArrayList Empty = ImmutableByteArrayList.newLIVE(ArrayUtilities.EmptyByteArray);
		
		
		
		
		
		
		
		
		@ThrowAwayValue
		@Override
		public byte[] toByteArray()
		{
			return data.clone();
		}
		
		/**
		 * DO NOT MODIFY THIS X"D
		 * ( THE HASH CODE IS CACHED FOR ONE, AND THIS MAY BE EMBEDDED IN HASHMAPS--THIS IS SUPPOSED TO BE IMMUTABLE AFTER ALL X'DDD )
		 */
		@ImplementationTransparency
		@LiveValue
		@ReadonlyValue //!!!!
		public byte[] getREADONLYLiveWholeArrayBackingUNSAFE()
		{
			return data;
		}
		
		@Override
		public int size()
		{
			return data.length;
		}
		
		@Override
		public byte getByte(int index)
		{
			return data[index];
		}
		
		@Override
		public void getAllBytes(int offsetInThisSource, @WritableValue byte[] dest, int offsetInDest, int lengthInBoth)
		{
			System.arraycopy(this.data, offsetInThisSource, dest, offsetInDest, lengthInBoth);
		}
		
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return true;
		}
		
		
		
		
		@Override
		public int hashCode()
		{
			long hashCodeField = this.hashCode;
			
			boolean cached = hashCodeField != 0;
			
			
			if (cached)
			{
				return (int)this.hashCode;
			}
			else
			{
				int hashCode = Arrays.hashCode(this.data);
				this.hashCode = hashCode | 0xFFFF_FFFF_0000_0000l;  //hashCodes can be zero!  Not often XDD   But can be!  So it's not correct unless we account for that :>      (And if it was two fields then it'd be a concurrency bug!)
				return hashCode;
			}
		}
		
		
		
		
		@Override
		public boolean equals(@ReadonlyValue Object obj)
		{
			if (obj == this)
			{
				return true;
			}
			else if (obj instanceof ImmutableByteArrayList)
			{
				ImmutableByteArrayList other = (ImmutableByteArrayList)obj;
				
				//Checking the hashcodes is a VERY fast negative test and is almost always true-negative!! :DDD   (rarely it's false-positive, but never false-negative!)
				
				long thisHC = this.hashCode;
				long otherHC = other.hashCode;
				
				boolean thisHashCodeCached = thisHC != 0;
				boolean otherHashCodeCached = otherHC != 0;
				
				int thisHashCode = (int)thisHC;
				int otherHashCode = (int)otherHC;
				
				if (thisHashCodeCached)
				{
					if (otherHashCodeCached)
					{
						if (thisHashCode != otherHashCode)
							return false;
					}
					else
					{
						//Scanning one byte array is faster than scanning two! :D
						if (thisHashCode != other.hashCode())
							return false;
					}
				}
				else
				{
					if (otherHashCodeCached)
					{
						//Scanning one byte array is faster than scanning two! :D
						if (this.hashCode() != otherHashCode)
							return false;
					}
					else
					{
						//Then don't make any hash codes X3
					}
				}
				
				return this.equalsByteArray(other.data);
			}
			else
			{
				return false;
			}
		}
		
		
		@Override
		public int compareTo(@ReadonlyValue ImmutableByteArrayList other)
		{
			return this.compareToByteArray(other.data);
		}
		
		
		public boolean equalsByteArray(@ReadonlyValue byte[] o)
		{
			byte[] ourData = this.getREADONLYLiveWholeArrayBackingUNSAFE();
			byte[] theirData = o;
			
			return Arrays.equals(ourData, theirData);
		}
		
		
		public int compareToByteArray(@ReadonlyValue byte[] o)
		{
			byte[] ourData = this.getREADONLYLiveWholeArrayBackingUNSAFE();
			byte[] theirData = o;
			
			return ArrayUtilities.compareLittleEndianLengthsFirst(ourData, theirData);  //lengths-first is more efficient if they're different but hardly any less efficient if they're always the same!  A good default, don't you think? :D
		}
		
		
		
		
		@Override
		public ImmutableByteArrayList clone()
		{
			return this;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		// Unallowed Write Methods!
		
		@Override
		public void clear()
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setSizeByte(int newSize, byte elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void insertByte(int index, byte value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setByte(int index, byte value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static ByteList unmodifiableByteList(ByteList byteList)
	{
		return byteList instanceof UnmodifiableByteListWrapper ? byteList : new UnmodifiableByteListWrapper(byteList);
	}
	
	public static class UnmodifiableByteListWrapper
	implements ByteList, KnowsLengthFixedness, TransparentContiguousArrayBackedCollection
	{
		protected final ByteList underlying;
		
		public UnmodifiableByteListWrapper(ByteList underlying)
		{
			this.underlying = underlying;
		}
		
		@ImplementationTransparency
		public ByteList getUnderlying()
		{
			return underlying;
		}
		
		
		
		
		
		@Override
		public UnmodifiableByteListWrapper clone()
		{
			return new UnmodifiableByteListWrapper(underlying.clone());
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.underlying);
		}
		
		@Override
		public boolean isTransparentContiguousArrayBackedCollection()
		{
			return TransparentContiguousArrayBackedCollection.is(underlying);
		}
		
		@Override
		public Slice getLiveContiguousArrayBackingUNSAFE()
		{
			return ((TransparentContiguousArrayBackedCollection)underlying).getLiveContiguousArrayBackingUNSAFE();
		}
		
		
		
		
		
		
		//Use default so they can't get around our readonly-ness!  ;D
		//	public Iterator<Byte> iterator()
		//	public ListIterator<Byte> listIterator()
		//	public ListIterator<Byte> listIterator(int index)
		//	public SimpleIterator<Byte> simpleIterator()
		//	public SimpleByteIterator newSimpleByteIterator()
		//	public ByteList subList(int fromIndex, int toIndex)
		
		
		
		
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeRangeByLength(int start, int length) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void replaceAll(UnaryOperator<Byte> operator)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeIf(Predicate<? super Byte> filter)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void sort(Comparator<? super Byte> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void clear()
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public Byte remove(int index)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllBytes(byte[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllBytes(Slice<byte[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllBytes(byte[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllBytes(Slice<byte[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllBytes(byte[] a, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setByte(int index, byte value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertByte(int index, byte value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSizeByte(int newSize, byte elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSize(int newSize, Byte elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSize(int newSize)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllBytes(int index, byte[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllBytes(int index, Slice<byte[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllBytes(int index, byte[] array, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAll(int destIndex, List sourceU, int sourceIndex, int amount) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addByte(byte value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll(Collection<? extends Byte> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllBytes(byte[] elements, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeByIndex(int index)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public byte removeByteByIndex(int index) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeByte(byte value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean retainAll(Collection<?> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllBytes(int index, byte[] array, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll(int index, Collection<? extends Byte> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllBytes(int index, byte[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllBytes(int index, Slice<byte[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public Byte set(int index, Byte value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void add(int index, Byte value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeAll(Collection<?> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean remove(Object o)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean add(Byte e)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		
		
		
		
		
		
		
		
		
		public void forEach(Consumer<? super Byte> action)
		{
			underlying.forEach(action);
		}
		
		public int size()
		{
			return underlying.size();
		}
		
		public boolean equals(Object o)
		{
			return underlying.equals(o);
		}
		
		public int hashCode()
		{
			return underlying.hashCode();
		}
		
		public Spliterator<Byte> spliterator()
		{
			return underlying.spliterator();
		}
		
		public Stream<Byte> stream()
		{
			return underlying.stream();
		}
		
		public Stream<Byte> parallelStream()
		{
			return underlying.parallelStream();
		}
		
		public byte getByte(int index)
		{
			return underlying.getByte(index);
		}
		
		public int indexOfByte(byte value)
		{
			return underlying.indexOfByte(value);
		}
		
		public int lastIndexOfByte(byte value)
		{
			return underlying.lastIndexOfByte(value);
		}
		
		public boolean equivalent(List<Byte> other)
		{
			return underlying.equivalent(other);
		}
		
		public byte[] toByteArray()
		{
			return underlying.toByteArray();
		}
		
		public boolean containsByte(byte value)
		{
			return underlying.containsByte(value);
		}
		
		public Byte get(int index)
		{
			return underlying.get(index);
		}
		
		public int indexOf(Object o)
		{
			return underlying.indexOf(o);
		}
		
		public int lastIndexOf(Object o)
		{
			return underlying.lastIndexOf(o);
		}
		
		public boolean containsAll(Collection<?> c)
		{
			return underlying.containsAll(c);
		}
		
		public boolean isEmpty()
		{
			return underlying.isEmpty();
		}
		
		public boolean contains(Object o)
		{
			return underlying.contains(o);
		}
		
		public Object[] toArray()
		{
			return underlying.toArray();
		}
		
		public <T> T[] toArray(T[] a)
		{
			return underlying.toArray(a);
		}
		
		public void storeBoxingIntoArray(Slice<Object[]> array)
		{
			underlying.storeBoxingIntoArray(array);
		}
		
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ByteTable
	{
		protected int width;
		protected byte[] data;
		
		public ByteTable(int width, int height)
		{
			if (width < 0)
				throw new IllegalArgumentException();
			if (height < 0)
				throw new IllegalArgumentException();
			
			data = new byte[width * height];
		}
		
		
		
		@Nonnegative
		public int getNumberOfColumns()
		{
			return width;
		}
		
		@Nonnegative
		public int getNumberOfRows()
		{
			return data.length / width;
		}
		
		
		@Nonnegative
		public int getNumberOfCells()
		{
			return data.length;
		}
		
		
		
		
		
		public byte getCellContentsByte(int columnIndex, int rowIndex) throws IndexOutOfBoundsException
		{
			if (columnIndex < 0) throw new IndexOutOfBoundsException();
			if (rowIndex < 0) throw new IndexOutOfBoundsException();
			if (columnIndex >= getNumberOfColumns()) throw new IndexOutOfBoundsException();
			if (rowIndex >= getNumberOfRows()) throw new IndexOutOfBoundsException();
			
			return this.data[rowIndex * this.getNumberOfColumns() + columnIndex];
		}
		
		public void setCellContentsByte(int columnIndex, int rowIndex, @Nullable byte newValue) throws IndexOutOfBoundsException
		{
			if (columnIndex < 0) throw new IndexOutOfBoundsException();
			if (rowIndex < 0) throw new IndexOutOfBoundsException();
			if (columnIndex >= getNumberOfColumns()) throw new IndexOutOfBoundsException();
			if (rowIndex >= getNumberOfRows()) throw new IndexOutOfBoundsException();
			
			this.data[rowIndex * this.getNumberOfColumns() + columnIndex] = newValue;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Nullable
		public Byte getCellContents(int columnIndex, int rowIndex) throws IndexOutOfBoundsException
		{
			return getCellContentsByte(columnIndex, rowIndex);
		}
		
		public void setCellContents(int columnIndex, int rowIndex, @Nullable Byte newValue) throws IndexOutOfBoundsException
		{
			setCellContentsByte(columnIndex, rowIndex, newValue);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static char getChar(List<Character> list, int index)
	{
		if (list instanceof CharacterList)
			return ((CharacterList) list).getChar(index);
		else
			return list.get(index);
	}
	
	public static void setChar(List<Character> list, int index, char value)
	{
		if (list instanceof CharacterList)
			((CharacterList) list).setChar(index, value);
		else
			list.set(index, value);
	}
	
	
	
	
	
	
	
	public static interface SimpleCharacterIterable
	extends SimpleIterable<Character>
	{
		public SimpleCharacterIterator newSimpleCharacterIterator();
		
		public default SimpleIterator<Character> simpleIterator()
		{
			SimpleCharacterIterator i = this.newSimpleCharacterIterator();
			return () -> i.nextrpChar();
		}
	}
	
	
	public static interface SimpleCharacterIterator
	extends SimpleIterator<Character>
	{
		public char nextrpChar() throws StopIterationReturnPath;
		
		
		@Override
		public default Character nextrp() throws StopIterationReturnPath
		{
			return nextrpChar();
		}
	}
	
	
	
	@SignalInterface
	public static interface CharacterCollection
	extends PrimitiveCollection<Character>, SimpleCharacterIterable
	{
		public boolean addChar(char value);
		
		public boolean removeChar(char value);
		
		
		
		
		
		@ThrowAwayValue
		public static char[] defaultToCharArray(CharacterCollection collection)
		{
			char[] array = new char[collection.size()];
			SimpleCharacterIterator i = collection.newSimpleCharacterIterator();
			
			int index = 0;
			while (true)
			{
				try
				{
					array[index] = i.nextrpChar();
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
				
				index++;
			}
			
			return array;
		}
		
		@ThrowAwayValue
		public default char[] toCharArray()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(this.size(), u);
				
				if (u.getUnderlying() instanceof char[])
				{
					return sliceToNewCharArrayOP((Slice<char[]>) u);
				}
			}
			
			return defaultToCharArray(this);
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public default char[] toCharArrayPossiblyLive()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				int size = this.size();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				Object und = u.getUnderlying();
				
				if (und instanceof char[] && u.getOffset() == 0 && ((char[])und).length == size)
				{
					return (char[])und;
				}
			}
			
			return defaultToCharArray(this);
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public default Slice<char[]> toCharArraySlicePossiblyLive()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(this.size(), u);
				
				if (u.getUnderlying() instanceof char[])
				{
					return (Slice<char[]>) u;
				}
			}
			
			return wholeArraySliceChar(defaultToCharArray(this));
		}
		
		
		
		
		
		
		public default boolean containsChar(char value)
		{
			SimpleCharacterIterator i = newSimpleCharacterIterator();
			
			while (true)
			{
				try
				{
					if (eqSane(i.nextrpChar(), value))
						return true;
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
			}
			
			return false;
		}
		
		
		
		
		
		
		@Override
		public default Iterator<Character> iterator()
		{
			return SimpleCharacterIterable.super.iterator();
		}
		
		@Override
		public default boolean isEmpty()
		{
			return size() == 0;
		}
		
		
		
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllChars(char[] array)
		{
			return addAllChars(array, 0, array.length);
		}
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllChars(Slice<char[]> arraySlice)
		{
			return addAllChars(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllChars(char[] elements, int offset, int length)
		{
			return defaultAddAllChars(this, elements, offset, length);
		}
		
		public static boolean defaultAddAllChars(CharacterCollection self, char[] elements, int offset, int length)
		{
			boolean changedAtAll = false;
			int e = offset + length;
			for (int i = offset; i < e; i++)
				changedAtAll |= self.addChar(elements[i]);
			return changedAtAll;
		}
		
		
		
		
		public default void removeAllChars(char[] array)
		{
			removeAllChars(array, 0, array.length);
		}
		
		public default void removeAllChars(Slice<char[]> arraySlice)
		{
			removeAllChars(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public default void removeAllChars(char[] a, int offset, int length)
		{
			int e = offset + length;
			for (int i = offset; i < e; i++)
				this.removeChar(a[i]);
		}
		
		
		
		
		
		
		
		
		
		
		@Override
		public default boolean contains(Object o)
		{
			return o instanceof Character && containsChar((Character)o);
		}
		
		
		@Override
		public default boolean add(Character e)
		{
			return addChar(e);
		}
		
		
		@Override
		public default boolean remove(Object o)
		{
			return o instanceof Character && removeChar((Character)o);
		}
		
		
		@Override
		public default boolean containsAll(Collection<?> c)
		{
			if (c instanceof CharacterCollection)
			{
				CharacterCollection cc = (CharacterCollection) c;
				
				SimpleCharacterIterator i = cc.newSimpleCharacterIterator();
				while (true)
				{
					try
					{
						if (this.containsChar(i.nextrpChar()))
							return true;
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return false;
			}
			else
			{
				for (Object e : c)
					if (this.contains(e))
						return true;
				return false;
			}
		}
		
		
		@Override
		public default boolean addAll(Collection<? extends Character> c)
		{
			if (c instanceof CharacterCollection)
			{
				boolean changedAtAll = false;
				
				SimpleCharacterIterator i = ((CharacterCollection)c).newSimpleCharacterIterator();
				
				while (true)
				{
					try
					{
						changedAtAll |= this.add(i.nextrpChar());
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (Character e : c)
				{
					changedAtAll |= this.add(e);
				}
				
				return changedAtAll;
			}
		}
		
		
		
		@Override
		public default boolean removeAll(Collection<?> c)
		{
			if (c instanceof CharacterCollection)
			{
				boolean changedAtAll = false;
				
				SimpleCharacterIterator i = ((CharacterCollection)c).newSimpleCharacterIterator();
				
				while (true)
				{
					try
					{
						changedAtAll |= this.remove(i.nextrpChar());
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (Object e : c)
				{
					changedAtAll |= this.remove(e);
				}
				
				return changedAtAll;
			}
		}
		
		
		
		public default String _toString()
		{
			StringBuilder b = new StringBuilder("[");
			
			SimpleCharacterIterator i = newSimpleCharacterIterator();
			
			boolean first = true;
			
			while (true)
			{
				char e;
				
				try
				{
					e = i.nextrpChar();
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
				
				
				
				//Notice this comes after the possible 'break'!  ^_~
				if (first)
					first = false;
				else
					b.append(", ");
				
				b.append(e);
			}
			
			b.append(']');
			
			return b.toString();
		}
	}
	
	
	
	public static interface DefaultToArraysCharacterCollection
	extends CharacterCollection
	{
		@Override
		public default Object[] toArray()
		{
			Object[] a = new Object[size()];
			storeBoxingIntoArray(wholeArraySliceReference(a));
			return a;
		}
		
		@Override
		public default <T> T[] toArray(T[] a)
		{
			if (a.length >= this.size())
			{
				storeBoxingIntoArray(wholeArraySliceReference(a));
				return a;
			}
			else
			{
				Object[] aa;
				
				if (a.getClass() == Character[].class)
				{
					aa = new Character[size()];
				}
				else if (a.getClass() == Object[].class)
				{
					aa = new Object[size()];
				}
				else
				{
					throw new StructuredClassCastException(a.getClass());
				}
				
				storeBoxingIntoArray(wholeArraySliceReference(aa));
				
				return (T[])aa;
			}
		}
		
		public void storeBoxingIntoArray(Slice<Object[]> array);
	}
	
	
	
	
	
	
	@SignalInterface
	public static interface CharacterListRO
	{
		public char getChar(int index);
		
		public int size();
		
		
		
		public default int indexOfChar(char value)
		{
			int n = this.size();
			for (int i = 0; i < n; i++)
				if (eqSane(this.getChar(i), value))
					return i;
			return -1;
		}
		
		public default int lastIndexOfChar(char value)
		{
			int i = this.size()-1;
			while (i >= 0)
			{
				if (eqSane(this.getChar(i), value))
					return i;
				break;
			}
			return i;   // ;D
		}
		
		
		
		public default boolean equivalent(List<Character> other)
		{
			int size = this.size();
			if (other.size() != size)
				return false;
			
			
			if (other instanceof CharacterListRO)  //All CharacterLists will implement this interface too so this accounts for them as well :3
			{
				return equivalentFixedRO((CharacterListRO)other);
			}
			else
			{
				for (int i = 0; i < size; i++)
				{
					if (!eqSane(this.getChar(i), other.get(i).charValue()))
						return false;
				}
			}
			
			return true;
		}
		
		
		
		public default boolean equivalentFixedRO(CharacterListRO other)
		{
			int size = this.size();
			if (other.size() != size)
				return false;
			
			for (int i = 0; i < size; i++)
			{
				if (!eqSane(this.getChar(i), other.getChar(i)))
					return false;
			}
			
			return true;
		}
	}
	
	
	
	@SignalInterface
	public static interface CharacterListRWFixed
	extends CharacterListRO
	{
		public void setChar(int index, char value);
	}
	
	
	
	@SignalInterface
	public static interface CharacterList
	extends CharacterCollection, NiceList<Character>, CharacterListRO, CharacterListRWFixed, ListWithRemoveRange, DefaultToArraysCharacterCollection, PubliclyCloneable, ListWithSetSize<Character>, ListWithSetAll, NonuniformMethodsForCharacterList
	{
		public void insertChar(int index, char value);
		
		
		
		/**
		 * This should be equivalent to just constructing and returning a new in-memory stock implementation with the same contents  (but can easily be faster, especially for those stock impl.s! XD )
		 * As for whether it's fixed-length or read-only or duplicateless or etc., that should be the same in the clone as it is in the main impl.
		 * If *and only if* it is immutable, this is allowed to return this same instance!  (ie the body of this method being <code>return this;</code>)
		 */
		public CharacterList clone();
		
		
		/**
		 * If size is smaller than the current, then this is equivalent to {@link #removeRange(int, int) removeRange}(newSize, this.{@link #size() size()})  :3
		 * If size is larger, then this appends newSize - this.{@link #size() size()} elementToAddIfGrowing's to the end like that many {@link #addChar(char)}  :D
		 */
		public void setSizeChar(int newSize, char elementToAddIfGrowing);
		
		public default void setSize(int newSize, Character elementToAddIfGrowing)
		{
			setSizeChar(newSize, elementToAddIfGrowing);
		}
		
		public default void setSize(int newSize)
		{
			setSizeChar(newSize, ((char)0));
		}
		
		
		
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void setAllChars(int index, char[] array)
		{
			setAllChars(index, array, 0, array.length);
		}
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void setAllChars(int index, Slice<char[]> arraySlice)
		{
			setAllChars(index, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		public default void setAllChars(int start, char[] array, int offset, int length)
		{
			int size = this.size();
			
			rangeCheckIntervalByLength(size, start, length);
			rangeCheckIntervalByLength(array.length, offset, length);
			
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(size, start, length);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				if (u.getUnderlying() instanceof char[])
				{
					Slice<char[]> s = (Slice<char[]>) u;
					
					System.arraycopy(array, offset, s.getUnderlying(), s.getOffset()+start, length);
					
					return;
				}
			}
			
			
			defaultSetAllChars(this, start, array, offset, length);
		}
		
		
		public static void defaultSetAllChars(CharacterList list, int start, @WritableValue char[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				list.setChar(start + i, array[offset + i]);
		}
		
		
		
		
		
		@Override
		public default void setAll(int destIndex, List sourceU, int sourceIndex, @Nonnegative int amount) throws IndexOutOfBoundsException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			List<Character> source = sourceU;
			CharacterList dest = this;
			
			int sourceSize = source.size();
			int destSize = dest.size();
			rangeCheckIntervalByLength(sourceSize, sourceIndex, amount);
			rangeCheckIntervalByLength(destSize, destIndex, amount);
			
			
			
			if (TransparentContiguousArrayBackedCollection.is(source))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection<?>)source).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(sourceSize, sourceIndex, amount);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(sourceSize, u);
				
				if (u.getUnderlying() instanceof char[])
				{
					Slice<char[]> s = (Slice<char[]>)u;
					this.setAllChars(destIndex, s.subslice(sourceIndex, amount));
					return;
				}
			}
			
			
			
			
			if (source instanceof CharacterList)
			{
				CharacterList primSource = (CharacterList) source;
				
				if (destIndex < sourceIndex)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
				{
					for (int i = 0; i < amount; i++)
						dest.setChar(destIndex+i, primSource.getChar(sourceIndex+i));
				}
				else
				{
					for (int i = amount-1; i >= 0; i--)
						dest.setChar(destIndex+i, primSource.getChar(sourceIndex+i));
				}
			}
			else
			{
				if (destIndex < sourceIndex)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
				{
					for (int i = 0; i < amount; i++)
						dest.set(destIndex+i, source.get(sourceIndex+i));
				}
				else
				{
					for (int i = amount-1; i >= 0; i--)
						dest.set(destIndex+i, source.get(sourceIndex+i));
				}
			}
		}
		
		
		
		
		
		
		
		/**
		 * Copies <code>array.length</code> elements into <code>array</code> starting with the <code>start</code>th element.<br>
		 */
		public default void getAllChars(int start, @WritableValue char[] array, int offset, int length)
		{
			int size = this.size();
			
			rangeCheckIntervalByLength(size, start, length);
			rangeCheckIntervalByLength(array.length, offset, length);
			
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(size, start, length);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				if (u.getUnderlying() instanceof char[])
				{
					Slice<char[]> s = (Slice<char[]>) u;
					
					System.arraycopy(s.getUnderlying(), s.getOffset()+start, array, offset, length);
					
					return;
				}
			}
			
			
			defaultGetAllChars(this, start, array, offset, length);
		}
		
		
		public static void defaultGetAllChars(CharacterList list, int start, @WritableValue char[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				array[offset + i] = list.getChar(start + i);
		}
		
		
		
		
		
		
		@ThrowAwayValue
		public default char[] getAllChars(int start, int end)
		{
			rangeCheckInterval(this.size(), start, end);
			
			char[] buff = new char[end-start];
			getAllChars(start, buff, 0, buff.length);
			return buff;
		}
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public default boolean addChar(char value)
		{
			this.insertChar(this.size(), value);
			return true;
		}
		
		@Override
		public default boolean addAll(Collection<? extends Character> c)
		{
			this.addAll(this.size(), c);
			return true;
		}
		
		@Override
		public default boolean addAllChars(char[] elements, int offset, int length)
		{
			insertAllChars(this.size(), elements, offset, length);
			return true;
		}
		
		public default void removeByIndex(int index)
		{
			removeRange(index, index+1);
		}
		
		public default char removeCharByIndex(int index) throws IndexOutOfBoundsException
		{
			char v = this.getChar(index);
			removeByIndex(index);
			return v;
		}
		
		@Override
		public default boolean removeChar(char value)
		{
			int i = this.indexOfChar(value);
			
			if (i != -1)
			{
				this.removeByIndex(i);
				return true;
			}
			else
			{
				return false;
			}
		}
		
		@Override
		public default boolean retainAll(Collection<?> c)
		{
			if (c instanceof CharacterCollection)
			{
				CharacterCollection cc = (CharacterCollection) c;
				
				boolean changedAtAll = false;
				
				for (int i = size() - 1; i >= 0; i--)
				{
					if (!cc.containsChar(getChar(i)))
					{
						removeCharByIndex(i);
						changedAtAll = true;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (int i = size() - 1; i >= 0; i--)
				{
					if (!c.contains(getChar(i)))
					{
						removeCharByIndex(i);
						changedAtAll = true;
					}
				}
				
				return changedAtAll;
			}
		}
		
		
		
		
		
		
		
		
		@Override
		public default SimpleCharacterIterator newSimpleCharacterIterator()
		{
			return new SimpleCharacterIterator()
			{
				int index = 0;
				
				public char nextrpChar() throws StopIterationReturnPath
				{
					int i = index;
					
					if (i >= size())
					{
						throw StopIterationReturnPath.I;
					}
					else
					{
						index = i + 1;
						return getChar(i);
					}
				}
			};
		}
		
		
		
		
		
		@Override
		public default boolean containsChar(char value)
		{
			return indexOfChar(value) != -1;
		}
		
		
		public default void insertAllChars(int index, char[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				insertChar(index+i, array[offset+i]);
		}
		
		
		@Override
		public default boolean addAll(int index, Collection<? extends Character> c)
		{
			if (c instanceof CharacterCollection)
			{
				CharacterCollection cc = (CharacterCollection)c;
				
				SimpleCharacterIterator i = cc.newSimpleCharacterIterator();
				while (true)
				{
					try
					{
						insertChar(index, i.nextrpChar());
						index++;  //doesn't matter if this gets executed on the last iteration :3
					}
					catch (StopIterationReturnPath rp)
					{
						break;
					}
				}
			}
			else
			{
				for (Character e : c)
				{
					insertChar(index, e);
					index++;
				}
			}
			
			return true;
		}
		
		
		
		public default void insertAllChars(int index, char[] array)
		{
			insertAllChars(index, array, 0, array.length);
		}
		
		public default void insertAllChars(int index, Slice<char[]> arraySlice)
		{
			insertAllChars(index, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		
		
		@Override
		public default Character get(int index)
		{
			return this.getChar(index);
		}
		
		@Override
		public default Character set(int index, Character value)
		{
			Character previous = get(index);
			this.setChar(index, value);
			return previous;
		}
		
		
		@Override
		public default void add(int index, Character value)
		{
			this.insertChar(index, value);
		}
		
		@Override
		public default Character remove(int index) throws IndexOutOfBoundsException
		{
			return this.removeCharByIndex(index);
		}
		
		
		@Override
		public default int indexOf(Object o)
		{
			return o instanceof Character ? indexOfChar((Character)o) : -1;
		}
		
		@Override
		public default int lastIndexOf(Object o)
		{
			return o instanceof Character ? lastIndexOfChar((Character)o) : -1;
		}
		
		
		
		@Override
		public default ListIterator<Character> listIterator()
		{
			return listIterator(0);
		}
		
		@Override
		public default ListIterator<Character> listIterator(int index)
		{
			//Todo
			throw new NotYetImplementedException();
		}
		
		@Override
		public default CharacterList subList(int fromIndex, int toIndex)   //Also note that we narrow the return types of this :3
		{
			if (fromIndex == 0 && toIndex == this.size())
				return this;
			
			if (this instanceof Sublist)
			{
				Sublist<Character> s = (Sublist<Character>)this;
				return new CharacterSublist((CharacterList)s.getUnderlying(), s.getSublistStartingIndex() + fromIndex, toIndex - fromIndex);
			}
			else
			{
				return new CharacterSublist(this, fromIndex, toIndex - fromIndex);
			}
		}
		
		
		
		
		
		
		
		
		
		
		// Multiple-Inheritance conflict resolvers X'3
		@Override
		public default boolean removeAll(Collection<?> c)
		{
			return DefaultToArraysCharacterCollection.super.removeAll(c);
		}
		
		@Override
		public default boolean remove(Object o)
		{
			return DefaultToArraysCharacterCollection.super.remove(o);
		}
		
		@Override
		public default boolean containsAll(Collection<?> c)
		{
			return DefaultToArraysCharacterCollection.super.containsAll(c);
		}
		
		@Override
		public default boolean add(Character e)
		{
			return DefaultToArraysCharacterCollection.super.add(e);
		}
		
		@Override
		public default boolean isEmpty()
		{
			return DefaultToArraysCharacterCollection.super.isEmpty();
		}
		
		@Override
		public default boolean contains(Object o)
		{
			return DefaultToArraysCharacterCollection.super.contains(o);
		}
		
		
		
		
		
		@Override
		public default Object[] toArray()
		{
			return DefaultToArraysCharacterCollection.super.toArray();
		}
		
		@Override
		public default <T> T[] toArray(T[] a)
		{
			return DefaultToArraysCharacterCollection.super.toArray(a);
		}
		
		
		
		
		
		
		
		@Override
		public default void storeBoxingIntoArray(Slice<Object[]> array)
		{
			int n = this.size();
			for (int i = 0; i < n; i++)
				ArrayUtilities.setReference((Slice)array, i, this.get(i));
		}
		
		
		
		
		
		
		//Narrow the return types of these :3
		
		@Override
		public default CharacterList subListToEnd(int start)
		{
			return (CharacterList)NiceList.super.subListToEnd(start);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
		
		@Override
		public default CharacterList subListFromBeginning(int lengthWhichIsEndExclusiveInThisCaseXD)
		{
			return (CharacterList)NiceList.super.subListFromBeginning(lengthWhichIsEndExclusiveInThisCaseXD);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
		
		@Override
		public default CharacterList subListByLength(int start, int length)
		{
			return (CharacterList)NiceList.super.subListByLength(start, length);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
	}
	
	
	
	
	
	
	public static interface DefaultShiftingBasedCharacterList
	extends CharacterList
	{
		public default void clear()
		{
			removeRange(0, size());
		}
		
		@Override
		public default void insertChar(int index, char value)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, 1);
			this.setChar(index, value);
		}
		
		@Override
		public default void insertAllChars(int index, char[] array, int offset, int length)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, length);
			
			this.setAllChars(index, array, offset, length);
		}
		
		
		@Override
		public default boolean addAll(int index, Collection<? extends Character> c)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			int amount = c.size();  //copy this incase c == this! XD
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, amount);
			
			if (c instanceof List)
			{
				listcopy((List)c, 0, this, index, amount);
			}
			else
			{
				if (c instanceof CharacterCollection)
				{
					SimpleCharacterIterator iterator = ((CharacterCollection)c).newSimpleCharacterIterator();
					
					int i = 0;
					while (true)
					{
						char e;
						try
						{
							e = iterator.nextrpChar();
						}
						catch (StopIterationReturnPath rp)
						{
							break;
						}
						
						this.setChar(index+i, e);
						i++;
					}
				}
				else
				{
					int i = 0;
					for (Character e : c)
					{
						this.set(index+i, e);
						i++;
					}
				}
			}
			
			return true;
		}
		
		@Override
		public default void setSizeChar(int newSize, char elementToAddIfGrowing)
		{
			int oldSize = this.size();
			int amount = newSize - oldSize;
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, oldSize, amount);
			
			//Growing results in *undefined* contents, not necessarily initialized ones!  (eg, it may already have some/enough elements allocated eg, in an underlying array and it just increases the int size field :3 )
			for (int i = oldSize; i < newSize; i++)
				this.setChar(i, elementToAddIfGrowing);
		}
		
		
		
		
		@Override
		public default void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			rangeCheckInterval(this.size(), start, pastEnd);
			
			int length = pastEnd - start;
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, pastEnd, -length);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class CharacterSublist
	implements CharacterList, DefaultShiftingBasedCharacterList, Sublist<Character>, ShiftableList
	{
		protected final CharacterList underlying;
		protected final int start;
		protected int size;
		
		public CharacterSublist(CharacterList underlying, int start, int initialSize)
		{
			rangeCheckIntervalByLength(underlying.size(), start, initialSize);
			
			this.underlying = underlying;
			this.start = start;
			this.size = initialSize;
		}
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public char getChar(int index)
		{
			return underlying.getChar(index + start);
		}
		
		@Override
		public void setChar(int index, char value)
		{
			underlying.setChar(index + start, value);
		}
		
		@Override
		public void shiftRegionStretchingFromIndexToEndByAmountChangingSize(int start, int amount)
		{
			rangeCheckCursorPoint(size(), start);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(underlying, start, amount);
			
			size += amount;
		}
		
		
		
		@Override
		public CharacterList clone()
		{
			CharacterList c = new CharacterArrayList();
			c.addAll(this);
			return c;
		}
		
		
		
		@Override
		public CharacterList getUnderlying()
		{
			return underlying;
		}
		
		@Override
		public int getSublistStartingIndex()
		{
			return start;
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class CharacterArrayList
	implements DefaultShiftingBasedCharacterList, ListWithSetSize<Character>, Trimmable, TransparentContiguousArrayBackedCollection<char[]>, KnowsLengthFixedness
	{
		protected int size = 0;
		protected char[] data;
		protected GrowerComputationallyUnreducedPurelyRecursive grower;
		
		
		
		
		//In case subclasses don't want to deal with the other things ^^'
		protected CharacterArrayList(Void thisiswhyyoushouldntusejavaconstructorsAlthoughIdontknowhowtomakeinheritanceworkwellwithoutinitmethodsorfactoriesorsomething)
		{
		}
		
		
		/**
		 * Capacity increment is different from Vector's.  It is the percentage of the capacity by which the capacity is increased.
		 * The formula being: <code>newCapacity = capacity + (capacity * capacityIncrement)</code><br>
		 * @param grower This can be anything.  The only downside is performance: all methods will add at least the minimum amount required regardless of what this specifies.
		 */
		public CharacterArrayList(int initialCapacity, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this.data = initialCapacity == 0 ? ArrayUtilities.EmptyCharArray : new char[initialCapacity];
			this.grower = grower;
		}
		
		public CharacterArrayList(@LiveValue @WritableValue char[] LIVEBACKING, int size, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this.data = LIVEBACKING;
			this.grower = grower;
			this.size = size;
		}
		
		public CharacterArrayList(@SnapshotValue @ReadonlyValue Collection<Character> c, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this(c.size(), grower);
			this.addAll(c);
		}
		
		
		
		
		
		public CharacterArrayList()
		{
			this(DefaultPrimitiveArrayListInitialCapacity);
		}
		
		public CharacterArrayList(int initialCapacity)
		{
			this(initialCapacity, defaultPrimitiveArrayListGrower(initialCapacity));
		}
		
		public CharacterArrayList(@SnapshotValue @ReadonlyValue Collection<Character> c)
		{
			this(c, defaultPrimitiveArrayListGrower(c.size()));
		}
		
		public CharacterArrayList(@LiveValue @WritableValue char[] LIVEBACKING, int size)
		{
			this(LIVEBACKING, size, defaultPrimitiveArrayListGrower(LIVEBACKING.length));
		}
		
		public CharacterArrayList(@LiveValue @WritableValue char[] LIVEBACKING)
		{
			this(LIVEBACKING, LIVEBACKING.length);
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return false;
		}
		
		
		
		@Override
		public void setSize(int newSize)
		{
			ensureCapacity(newSize);
			this.size = newSize;
			
			// :D
		}
		
		
		
		
		
		//This pattern is nicer for subclassing in any Java class! :D
		protected void setupClone(CharacterArrayList freshClone)
		{
			freshClone.size = this.size;
			
			freshClone.data = new char[size];
			System.arraycopy(this.data, 0, freshClone.data, 0, size);
			
			freshClone.grower = this.grower;
		}
		
		@Override
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public CharacterArrayList clone()
		{
			CharacterArrayList clone = new CharacterArrayList();
			this.setupClone(clone);
			return clone;
		}
		
		
		
		
		
		
		
		
		
		
		//This is what unlocks all the System.arraycopy() magic!  8>
		@ImplementationTransparency
		@LiveValue
		public Slice<char[]> getLiveContiguousArrayBackingUNSAFE()
		{
			return new Slice(data, 0, size);
		}
		
		
		
		
		
		
		
		
		
		public void setChar(int index, char value)
		{
			rangeCheckMember(this.size, index);
			data[index] = value;
		}
		
		public char getChar(int index)
		{
			rangeCheckMember(this.size(), index);
			return data[index];
		}
		
		
		
		
		
		
		
		@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public void clear()
		{
			size = 0;
		}
		
		@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public int size()
		{
			return size;
		}
		
		@ImplementationTransparency
		public void trimToSize()
		{
			char[] newdata = new char[size];
			System.arraycopy(data, 0, newdata, 0, size);
			this.data = newdata;
		}
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.trimToSize();
			return TrimmableTrimRV.DontKeepInvoking;
		}
		
		
		
		
		
		@ImplementationTransparency
		public int getCapacity()
		{
			return data.length;
		}
		
		@ImplementationTransparency
		public void ensureCapacity(int minCapacity)
		{
			if (data.length < minCapacity)
			{
				//Enlarge
				int newCapacity = getNewCapacity(minCapacity);
				char[] newData = new char[newCapacity];
				System.arraycopy(data, 0, newData, 0, data.length);
				this.data = newData;
			}
		}
		
		/**
		 * This is also guaranteed not to return a capacity smaller than the provided minimum.
		 */
		@ImplementationTransparency
		public int getNewCapacity(int minCapacity)
		{
			if (minCapacity < 0)
				throw new IllegalArgumentException();
			
			return Math.max(minCapacity, grower.getNewSizeRecursive(data.length));  //The Math.max(min, ...) ensures that the grower can't break anything!  :3
		}
		
		
		
		
		
		
		
		/**
		 * sets the underlying array that backs the array list.
		 * Be careful, though, as updates can cause a new array to be created, obsoleting this one.
		 * Note, also, that only up to {@link #size()} elements will be used.
		 */
		@ImplementationTransparency
		public void setDirectBuffer(@LiveValue char[] array)
		{
			if (array.length < size)
				throw new IllegalArgumentException("Array is too small; size="+size+", capacity="+array.length);
			
			this.data = array;
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static class FixedLengthArrayWrapperCharacterList
	implements CharacterList, TransparentContiguousArrayBackedCollection<char[]>, KnowsLengthFixedness
	{
		protected final char[] underlying;
		protected final int offset, length;
		
		public FixedLengthArrayWrapperCharacterList(char[] underlying, int offset, int length)
		{
			rangeCheckIntervalByLength(underlying.length, offset, length);
			
			this.underlying = underlying;
			this.offset = offset;
			this.length = length;
		}
		
		public FixedLengthArrayWrapperCharacterList(char[] underlying)
		{
			this(underlying, 0, underlying.length);
		}
		
		public FixedLengthArrayWrapperCharacterList(Slice<char[]> underlying)
		{
			this(underlying.getUnderlying(), underlying.getOffset(), underlying.getLength());
		}
		
		@Override
		public CharacterList clone()
		{
			return new FixedLengthArrayWrapperCharacterList(toCharArray());
		}
		
		
		
		@Override
		public char[] toCharArray()
		{
			return sliceToNewCharArrayOP(this.getLiveContiguousArrayBackingUNSAFE());
		}
		
		@Override
		public Slice<char[]> getLiveContiguousArrayBackingUNSAFE()
		{
			return new Slice<>(underlying, offset, length);
		}
		
		@Override
		public int size()
		{
			return length;
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return true;
		}
		
		
		
		@Override
		public SimpleCharacterIterator newSimpleCharacterIterator()
		{
			return new SimpleCharacterIterator()
			{
				int index = 0;
				
				public char nextrpChar() throws StopIterationReturnPath
				{
					int i = index;
					
					if (i >= size())
					{
						throw StopIterationReturnPath.I;
					}
					else
					{
						index = i + 1;
						return underlying[offset+i];
					}
				}
			};
		}
		
		@Override
		public CharacterList subList(int fromIndex, int toIndex)
		{
			rangeCheckInterval(this.size(), fromIndex, toIndex);
			return new FixedLengthArrayWrapperCharacterList(this.underlying, fromIndex+offset, toIndex-fromIndex);
		}
		
		@Override
		public char getChar(int index)
		{
			return underlying[offset + index];
		}
		
		@Override
		public void setChar(int index, char value)
		{
			underlying[offset + index] = value;
		}
		
		
		
		
		
		
		
		
		
		//Unallowed Dim-altering Methods!
		
		@Override
		public void clear()
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setSizeChar(int newSize, char elementToAddIfGrowing)
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void insertChar(int index, char value)
		{
			throw new UnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	@ReadonlyValue
	public static CharacterList charArrayAsList(@LiveValue @WritableValue char[] array, int offset, int length)
	{
		return new FixedLengthArrayWrapperCharacterList(array, offset, length);
	}
	
	@ReadonlyValue
	public static CharacterList charArrayAsList(@LiveValue @WritableValue char[] array)
	{
		return charArrayAsList(array, 0, array.length);
	}
	
	@ReadonlyValue
	public static CharacterList charArrayAsList(@LiveValue @WritableValue Slice<char[]> arraySlice)
	{
		return charArrayAsList(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	
	
	
	@ThrowAwayValue
	public static CharacterList charArrayAsMutableList(@SnapshotValue @ReadonlyValue char[] array, int offset, int length)
	{
		return new CharacterArrayList(new FixedLengthArrayWrapperCharacterList(array, offset, length));
	}
	
	@ThrowAwayValue
	public static CharacterList charArrayAsMutableList(@SnapshotValue @ReadonlyValue char[] array)
	{
		return charArrayAsMutableList(array, 0, array.length);
	}
	
	@ThrowAwayValue
	public static CharacterList charArrayAsMutableList(@SnapshotValue @ReadonlyValue Slice<char[]> arraySlice)
	{
		return charArrayAsMutableList(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	@Immutable
	public static class ImmutableCharacterArrayList
	implements Serializable, Comparable<ImmutableCharacterArrayList>, CharacterList, KnowsLengthFixedness     //Let's not implement the general interfaces for accessing the underlying data jussssst to help make sure anyone who accesses it *knows* it's meant to be VERY MUCH READONLY!  XD'''
	{
		private static final long serialVersionUID = 1L;
		
		
		protected final char[] data;
		
		//Yeah I know it's bad practice for a thread-safe immutable object to cache things, but at best effort is wasted, no constraints are violated!     (If I'm wrong, fire me X'D )
		protected transient long hashCode;     //we cache it in case the chararray is large :33
		
		protected ImmutableCharacterArrayList(char[] LIVEDATA)
		{
			if (LIVEDATA == null) throw new NullPointerException();
			this.data = LIVEDATA;
		}
		
		
		
		
		
		/**
		 * DO NOT *EVER* MODIFY THE ARRAY YOU PASSED HERE AFTER CALLING THIS X"D
		 * ( THE HASH CODE IS CACHED FOR ONE, AND THIS MAY BE EMBEDDED IN HASHMAPS--THIS IS SUPPOSED TO BE IMMUTABLE AFTER ALL X'DDD )
		 * @see #getREADONLYLiveWholeArrayBackingUNSAFE()
		 */
		@ImplementationTransparency
		public static ImmutableCharacterArrayList newLIVE(@LiveValue char[] LIVEDATA)
		{
			return new ImmutableCharacterArrayList(LIVEDATA);
		}
		
		
		@ImplementationTransparency
		public static ImmutableCharacterArrayList newSingleton(char singleMember)
		{
			return new ImmutableCharacterArrayList(new char[]{singleMember});
		}
		
		
		
		
		public static ImmutableCharacterArrayList newCopying(@SnapshotValue List<Character> data)
		{
			if (data instanceof ImmutableCharacterArrayList)
				return (ImmutableCharacterArrayList)data;
			
			if (TransparentContiguousArrayBackedCollection.is(data))
			{
				Slice<Object> s = ((TransparentContiguousArrayBackedCollection)data).getLiveContiguousArrayBackingUNSAFE();
				if (s.getUnderlying() instanceof char[])
				{
					return newCopying((Slice)s);
				}
			}
			
			if (data instanceof CharacterList)
			{
				return newLIVE(((CharacterList)data).toCharArray());
			}
			else
			{
				int n = data.size();
				char[] a = new char[n];
				
				int i = 0;
				for (Character e : data)
				{
					if (i >= n)
						throw new ImpossibleException();
					
					a[i] = e;
					i++;
				}
				
				if (i != n)
					throw new ImpossibleException();
				
				return newLIVE(a);
			}
		}
		
		public static ImmutableCharacterArrayList newCopying(@SnapshotValue char[] data)
		{
			return new ImmutableCharacterArrayList(data.clone());
		}
		
		public static ImmutableCharacterArrayList newCopying(@SnapshotValue char[] data, int offset, int length)
		{
			if (offset == 0 && length == data.length)
				return newCopying(data);  //who knows, char[].clone() might be faster! \o/   (it certainly shouldn't be slower!!)
			else
			{
				char[] newArray = new char[length];
				System.arraycopy(data, offset, newArray, 0, length);
				return new ImmutableCharacterArrayList(newArray);
			}
		}
		
		public static ImmutableCharacterArrayList newCopying(@SnapshotValue Slice<char[]> data)
		{
			return newCopying(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		
		
		
		
		public static ImmutableCharacterArrayList newLIVEOrCopying(@PossiblySnapshotPossiblyLiveValue char[] data, int offset, int length)
		{
			if (offset == 0 && length == data.length)
				return newLIVE(data);
			else
				return newCopying(data, offset, length);
		}
		
		public static ImmutableCharacterArrayList newLIVEOrCopying(@PossiblySnapshotPossiblyLiveValue Slice<char[]> data)
		{
			return newLIVEOrCopying(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		
		
		
		
		
		
		public static final ImmutableCharacterArrayList Empty = ImmutableCharacterArrayList.newLIVE(ArrayUtilities.EmptyCharArray);
		
		
		
		
		
		
		
		
		@ThrowAwayValue
		@Override
		public char[] toCharArray()
		{
			return data.clone();
		}
		
		/**
		 * DO NOT MODIFY THIS X"D
		 * ( THE HASH CODE IS CACHED FOR ONE, AND THIS MAY BE EMBEDDED IN HASHMAPS--THIS IS SUPPOSED TO BE IMMUTABLE AFTER ALL X'DDD )
		 */
		@ImplementationTransparency
		@LiveValue
		@ReadonlyValue //!!!!
		public char[] getREADONLYLiveWholeArrayBackingUNSAFE()
		{
			return data;
		}
		
		@Override
		public int size()
		{
			return data.length;
		}
		
		@Override
		public char getChar(int index)
		{
			return data[index];
		}
		
		@Override
		public void getAllChars(int offsetInThisSource, @WritableValue char[] dest, int offsetInDest, int lengthInBoth)
		{
			System.arraycopy(this.data, offsetInThisSource, dest, offsetInDest, lengthInBoth);
		}
		
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return true;
		}
		
		
		
		
		@Override
		public int hashCode()
		{
			long hashCodeField = this.hashCode;
			
			boolean cached = hashCodeField != 0;
			
			
			if (cached)
			{
				return (int)this.hashCode;
			}
			else
			{
				int hashCode = Arrays.hashCode(this.data);
				this.hashCode = hashCode | 0xFFFF_FFFF_0000_0000l;  //hashCodes can be zero!  Not often XDD   But can be!  So it's not correct unless we account for that :>      (And if it was two fields then it'd be a concurrency bug!)
				return hashCode;
			}
		}
		
		
		
		
		@Override
		public boolean equals(@ReadonlyValue Object obj)
		{
			if (obj == this)
			{
				return true;
			}
			else if (obj instanceof ImmutableCharacterArrayList)
			{
				ImmutableCharacterArrayList other = (ImmutableCharacterArrayList)obj;
				
				//Checking the hashcodes is a VERY fast negative test and is almost always true-negative!! :DDD   (rarely it's false-positive, but never false-negative!)
				
				long thisHC = this.hashCode;
				long otherHC = other.hashCode;
				
				boolean thisHashCodeCached = thisHC != 0;
				boolean otherHashCodeCached = otherHC != 0;
				
				int thisHashCode = (int)thisHC;
				int otherHashCode = (int)otherHC;
				
				if (thisHashCodeCached)
				{
					if (otherHashCodeCached)
					{
						if (thisHashCode != otherHashCode)
							return false;
					}
					else
					{
						//Scanning one char array is faster than scanning two! :D
						if (thisHashCode != other.hashCode())
							return false;
					}
				}
				else
				{
					if (otherHashCodeCached)
					{
						//Scanning one char array is faster than scanning two! :D
						if (this.hashCode() != otherHashCode)
							return false;
					}
					else
					{
						//Then don't make any hash codes X3
					}
				}
				
				return this.equalsCharArray(other.data);
			}
			else
			{
				return false;
			}
		}
		
		
		@Override
		public int compareTo(@ReadonlyValue ImmutableCharacterArrayList other)
		{
			return this.compareToCharArray(other.data);
		}
		
		
		public boolean equalsCharArray(@ReadonlyValue char[] o)
		{
			char[] ourData = this.getREADONLYLiveWholeArrayBackingUNSAFE();
			char[] theirData = o;
			
			return Arrays.equals(ourData, theirData);
		}
		
		
		public int compareToCharArray(@ReadonlyValue char[] o)
		{
			char[] ourData = this.getREADONLYLiveWholeArrayBackingUNSAFE();
			char[] theirData = o;
			
			return ArrayUtilities.compareLittleEndianLengthsFirst(ourData, theirData);  //lengths-first is more efficient if they're different but hardly any less efficient if they're always the same!  A good default, don't you think? :D
		}
		
		
		
		
		@Override
		public ImmutableCharacterArrayList clone()
		{
			return this;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		// Unallowed Write Methods!
		
		@Override
		public void clear()
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setSizeChar(int newSize, char elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void insertChar(int index, char value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setChar(int index, char value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static CharacterList unmodifiableCharacterList(CharacterList characterList)
	{
		return characterList instanceof UnmodifiableCharacterListWrapper ? characterList : new UnmodifiableCharacterListWrapper(characterList);
	}
	
	public static class UnmodifiableCharacterListWrapper
	implements CharacterList, KnowsLengthFixedness, TransparentContiguousArrayBackedCollection
	{
		protected final CharacterList underlying;
		
		public UnmodifiableCharacterListWrapper(CharacterList underlying)
		{
			this.underlying = underlying;
		}
		
		@ImplementationTransparency
		public CharacterList getUnderlying()
		{
			return underlying;
		}
		
		
		
		
		
		@Override
		public UnmodifiableCharacterListWrapper clone()
		{
			return new UnmodifiableCharacterListWrapper(underlying.clone());
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.underlying);
		}
		
		@Override
		public boolean isTransparentContiguousArrayBackedCollection()
		{
			return TransparentContiguousArrayBackedCollection.is(underlying);
		}
		
		@Override
		public Slice getLiveContiguousArrayBackingUNSAFE()
		{
			return ((TransparentContiguousArrayBackedCollection)underlying).getLiveContiguousArrayBackingUNSAFE();
		}
		
		
		
		
		
		
		//Use default so they can't get around our readonly-ness!  ;D
		//	public Iterator<Character> iterator()
		//	public ListIterator<Character> listIterator()
		//	public ListIterator<Character> listIterator(int index)
		//	public SimpleIterator<Character> simpleIterator()
		//	public SimpleCharacterIterator newSimpleCharacterIterator()
		//	public CharacterList subList(int fromIndex, int toIndex)
		
		
		
		
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeRangeByLength(int start, int length) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void replaceAll(UnaryOperator<Character> operator)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeIf(Predicate<? super Character> filter)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void sort(Comparator<? super Character> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void clear()
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public Character remove(int index)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllChars(char[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllChars(Slice<char[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllChars(char[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllChars(Slice<char[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllChars(char[] a, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setChar(int index, char value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertChar(int index, char value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSizeChar(int newSize, char elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSize(int newSize, Character elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSize(int newSize)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllChars(int index, char[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllChars(int index, Slice<char[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllChars(int index, char[] array, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAll(int destIndex, List sourceU, int sourceIndex, int amount) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addChar(char value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll(Collection<? extends Character> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllChars(char[] elements, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeByIndex(int index)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public char removeCharByIndex(int index) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeChar(char value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean retainAll(Collection<?> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllChars(int index, char[] array, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll(int index, Collection<? extends Character> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllChars(int index, char[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllChars(int index, Slice<char[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public Character set(int index, Character value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void add(int index, Character value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeAll(Collection<?> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean remove(Object o)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean add(Character e)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		
		
		
		
		
		
		
		
		
		public void forEach(Consumer<? super Character> action)
		{
			underlying.forEach(action);
		}
		
		public int size()
		{
			return underlying.size();
		}
		
		public boolean equals(Object o)
		{
			return underlying.equals(o);
		}
		
		public int hashCode()
		{
			return underlying.hashCode();
		}
		
		public Spliterator<Character> spliterator()
		{
			return underlying.spliterator();
		}
		
		public Stream<Character> stream()
		{
			return underlying.stream();
		}
		
		public Stream<Character> parallelStream()
		{
			return underlying.parallelStream();
		}
		
		public char getChar(int index)
		{
			return underlying.getChar(index);
		}
		
		public int indexOfChar(char value)
		{
			return underlying.indexOfChar(value);
		}
		
		public int lastIndexOfChar(char value)
		{
			return underlying.lastIndexOfChar(value);
		}
		
		public boolean equivalent(List<Character> other)
		{
			return underlying.equivalent(other);
		}
		
		public char[] toCharArray()
		{
			return underlying.toCharArray();
		}
		
		public boolean containsChar(char value)
		{
			return underlying.containsChar(value);
		}
		
		public Character get(int index)
		{
			return underlying.get(index);
		}
		
		public int indexOf(Object o)
		{
			return underlying.indexOf(o);
		}
		
		public int lastIndexOf(Object o)
		{
			return underlying.lastIndexOf(o);
		}
		
		public boolean containsAll(Collection<?> c)
		{
			return underlying.containsAll(c);
		}
		
		public boolean isEmpty()
		{
			return underlying.isEmpty();
		}
		
		public boolean contains(Object o)
		{
			return underlying.contains(o);
		}
		
		public Object[] toArray()
		{
			return underlying.toArray();
		}
		
		public <T> T[] toArray(T[] a)
		{
			return underlying.toArray(a);
		}
		
		public void storeBoxingIntoArray(Slice<Object[]> array)
		{
			underlying.storeBoxingIntoArray(array);
		}
		
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class CharacterTable
	{
		protected int width;
		protected char[] data;
		
		public CharacterTable(int width, int height)
		{
			if (width < 0)
				throw new IllegalArgumentException();
			if (height < 0)
				throw new IllegalArgumentException();
			
			data = new char[width * height];
		}
		
		
		
		@Nonnegative
		public int getNumberOfColumns()
		{
			return width;
		}
		
		@Nonnegative
		public int getNumberOfRows()
		{
			return data.length / width;
		}
		
		
		@Nonnegative
		public int getNumberOfCells()
		{
			return data.length;
		}
		
		
		
		
		
		public char getCellContentsCharacter(int columnIndex, int rowIndex) throws IndexOutOfBoundsException
		{
			if (columnIndex < 0) throw new IndexOutOfBoundsException();
			if (rowIndex < 0) throw new IndexOutOfBoundsException();
			if (columnIndex >= getNumberOfColumns()) throw new IndexOutOfBoundsException();
			if (rowIndex >= getNumberOfRows()) throw new IndexOutOfBoundsException();
			
			return this.data[rowIndex * this.getNumberOfColumns() + columnIndex];
		}
		
		public void setCellContentsCharacter(int columnIndex, int rowIndex, @Nullable char newValue) throws IndexOutOfBoundsException
		{
			if (columnIndex < 0) throw new IndexOutOfBoundsException();
			if (rowIndex < 0) throw new IndexOutOfBoundsException();
			if (columnIndex >= getNumberOfColumns()) throw new IndexOutOfBoundsException();
			if (rowIndex >= getNumberOfRows()) throw new IndexOutOfBoundsException();
			
			this.data[rowIndex * this.getNumberOfColumns() + columnIndex] = newValue;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Nullable
		public Character getCellContents(int columnIndex, int rowIndex) throws IndexOutOfBoundsException
		{
			return getCellContentsCharacter(columnIndex, rowIndex);
		}
		
		public void setCellContents(int columnIndex, int rowIndex, @Nullable Character newValue) throws IndexOutOfBoundsException
		{
			setCellContentsCharacter(columnIndex, rowIndex, newValue);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static short getShort(List<Short> list, int index)
	{
		if (list instanceof ShortList)
			return ((ShortList) list).getShort(index);
		else
			return list.get(index);
	}
	
	public static void setShort(List<Short> list, int index, short value)
	{
		if (list instanceof ShortList)
			((ShortList) list).setShort(index, value);
		else
			list.set(index, value);
	}
	
	
	
	
	
	
	
	public static interface SimpleShortIterable
	extends SimpleIterable<Short>
	{
		public SimpleShortIterator newSimpleShortIterator();
		
		public default SimpleIterator<Short> simpleIterator()
		{
			SimpleShortIterator i = this.newSimpleShortIterator();
			return () -> i.nextrpShort();
		}
	}
	
	
	public static interface SimpleShortIterator
	extends SimpleIterator<Short>
	{
		public short nextrpShort() throws StopIterationReturnPath;
		
		
		@Override
		public default Short nextrp() throws StopIterationReturnPath
		{
			return nextrpShort();
		}
	}
	
	
	
	@SignalInterface
	public static interface ShortCollection
	extends PrimitiveCollection<Short>, SimpleShortIterable
	{
		public boolean addShort(short value);
		
		public boolean removeShort(short value);
		
		
		
		
		
		@ThrowAwayValue
		public static short[] defaultToShortArray(ShortCollection collection)
		{
			short[] array = new short[collection.size()];
			SimpleShortIterator i = collection.newSimpleShortIterator();
			
			int index = 0;
			while (true)
			{
				try
				{
					array[index] = i.nextrpShort();
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
				
				index++;
			}
			
			return array;
		}
		
		@ThrowAwayValue
		public default short[] toShortArray()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(this.size(), u);
				
				if (u.getUnderlying() instanceof short[])
				{
					return sliceToNewShortArrayOP((Slice<short[]>) u);
				}
			}
			
			return defaultToShortArray(this);
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public default short[] toShortArrayPossiblyLive()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				int size = this.size();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				Object und = u.getUnderlying();
				
				if (und instanceof short[] && u.getOffset() == 0 && ((short[])und).length == size)
				{
					return (short[])und;
				}
			}
			
			return defaultToShortArray(this);
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public default Slice<short[]> toShortArraySlicePossiblyLive()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(this.size(), u);
				
				if (u.getUnderlying() instanceof short[])
				{
					return (Slice<short[]>) u;
				}
			}
			
			return wholeArraySliceShort(defaultToShortArray(this));
		}
		
		
		
		
		
		
		public default boolean containsShort(short value)
		{
			SimpleShortIterator i = newSimpleShortIterator();
			
			while (true)
			{
				try
				{
					if (eqSane(i.nextrpShort(), value))
						return true;
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
			}
			
			return false;
		}
		
		
		
		
		
		
		@Override
		public default Iterator<Short> iterator()
		{
			return SimpleShortIterable.super.iterator();
		}
		
		@Override
		public default boolean isEmpty()
		{
			return size() == 0;
		}
		
		
		
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllShorts(short[] array)
		{
			return addAllShorts(array, 0, array.length);
		}
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllShorts(Slice<short[]> arraySlice)
		{
			return addAllShorts(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllShorts(short[] elements, int offset, int length)
		{
			return defaultAddAllShorts(this, elements, offset, length);
		}
		
		public static boolean defaultAddAllShorts(ShortCollection self, short[] elements, int offset, int length)
		{
			boolean changedAtAll = false;
			int e = offset + length;
			for (int i = offset; i < e; i++)
				changedAtAll |= self.addShort(elements[i]);
			return changedAtAll;
		}
		
		
		
		
		public default void removeAllShorts(short[] array)
		{
			removeAllShorts(array, 0, array.length);
		}
		
		public default void removeAllShorts(Slice<short[]> arraySlice)
		{
			removeAllShorts(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public default void removeAllShorts(short[] a, int offset, int length)
		{
			int e = offset + length;
			for (int i = offset; i < e; i++)
				this.removeShort(a[i]);
		}
		
		
		
		
		
		
		
		
		
		
		@Override
		public default boolean contains(Object o)
		{
			return o instanceof Short && containsShort((Short)o);
		}
		
		
		@Override
		public default boolean add(Short e)
		{
			return addShort(e);
		}
		
		
		@Override
		public default boolean remove(Object o)
		{
			return o instanceof Short && removeShort((Short)o);
		}
		
		
		@Override
		public default boolean containsAll(Collection<?> c)
		{
			if (c instanceof ShortCollection)
			{
				ShortCollection cc = (ShortCollection) c;
				
				SimpleShortIterator i = cc.newSimpleShortIterator();
				while (true)
				{
					try
					{
						if (this.containsShort(i.nextrpShort()))
							return true;
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return false;
			}
			else
			{
				for (Object e : c)
					if (this.contains(e))
						return true;
				return false;
			}
		}
		
		
		@Override
		public default boolean addAll(Collection<? extends Short> c)
		{
			if (c instanceof ShortCollection)
			{
				boolean changedAtAll = false;
				
				SimpleShortIterator i = ((ShortCollection)c).newSimpleShortIterator();
				
				while (true)
				{
					try
					{
						changedAtAll |= this.add(i.nextrpShort());
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (Short e : c)
				{
					changedAtAll |= this.add(e);
				}
				
				return changedAtAll;
			}
		}
		
		
		
		@Override
		public default boolean removeAll(Collection<?> c)
		{
			if (c instanceof ShortCollection)
			{
				boolean changedAtAll = false;
				
				SimpleShortIterator i = ((ShortCollection)c).newSimpleShortIterator();
				
				while (true)
				{
					try
					{
						changedAtAll |= this.remove(i.nextrpShort());
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (Object e : c)
				{
					changedAtAll |= this.remove(e);
				}
				
				return changedAtAll;
			}
		}
		
		
		
		public default String _toString()
		{
			StringBuilder b = new StringBuilder("[");
			
			SimpleShortIterator i = newSimpleShortIterator();
			
			boolean first = true;
			
			while (true)
			{
				short e;
				
				try
				{
					e = i.nextrpShort();
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
				
				
				
				//Notice this comes after the possible 'break'!  ^_~
				if (first)
					first = false;
				else
					b.append(", ");
				
				b.append(e);
			}
			
			b.append(']');
			
			return b.toString();
		}
	}
	
	
	
	public static interface DefaultToArraysShortCollection
	extends ShortCollection
	{
		@Override
		public default Object[] toArray()
		{
			Object[] a = new Object[size()];
			storeBoxingIntoArray(wholeArraySliceReference(a));
			return a;
		}
		
		@Override
		public default <T> T[] toArray(T[] a)
		{
			if (a.length >= this.size())
			{
				storeBoxingIntoArray(wholeArraySliceReference(a));
				return a;
			}
			else
			{
				Object[] aa;
				
				if (a.getClass() == Short[].class)
				{
					aa = new Short[size()];
				}
				else if (a.getClass() == Object[].class)
				{
					aa = new Object[size()];
				}
				else
				{
					throw new StructuredClassCastException(a.getClass());
				}
				
				storeBoxingIntoArray(wholeArraySliceReference(aa));
				
				return (T[])aa;
			}
		}
		
		public void storeBoxingIntoArray(Slice<Object[]> array);
	}
	
	
	
	
	
	
	@SignalInterface
	public static interface ShortListRO
	{
		public short getShort(int index);
		
		public int size();
		
		
		
		public default int indexOfShort(short value)
		{
			int n = this.size();
			for (int i = 0; i < n; i++)
				if (eqSane(this.getShort(i), value))
					return i;
			return -1;
		}
		
		public default int lastIndexOfShort(short value)
		{
			int i = this.size()-1;
			while (i >= 0)
			{
				if (eqSane(this.getShort(i), value))
					return i;
				break;
			}
			return i;   // ;D
		}
		
		
		
		public default boolean equivalent(List<Short> other)
		{
			int size = this.size();
			if (other.size() != size)
				return false;
			
			
			if (other instanceof ShortListRO)  //All ShortLists will implement this interface too so this accounts for them as well :3
			{
				return equivalentFixedRO((ShortListRO)other);
			}
			else
			{
				for (int i = 0; i < size; i++)
				{
					if (!eqSane(this.getShort(i), other.get(i).shortValue()))
						return false;
				}
			}
			
			return true;
		}
		
		
		
		public default boolean equivalentFixedRO(ShortListRO other)
		{
			int size = this.size();
			if (other.size() != size)
				return false;
			
			for (int i = 0; i < size; i++)
			{
				if (!eqSane(this.getShort(i), other.getShort(i)))
					return false;
			}
			
			return true;
		}
	}
	
	
	
	@SignalInterface
	public static interface ShortListRWFixed
	extends ShortListRO
	{
		public void setShort(int index, short value);
	}
	
	
	
	@SignalInterface
	public static interface ShortList
	extends ShortCollection, NiceList<Short>, ShortListRO, ShortListRWFixed, ListWithRemoveRange, DefaultToArraysShortCollection, PubliclyCloneable, ListWithSetSize<Short>, ListWithSetAll, NonuniformMethodsForShortList
	{
		public void insertShort(int index, short value);
		
		
		
		/**
		 * This should be equivalent to just constructing and returning a new in-memory stock implementation with the same contents  (but can easily be faster, especially for those stock impl.s! XD )
		 * As for whether it's fixed-length or read-only or duplicateless or etc., that should be the same in the clone as it is in the main impl.
		 * If *and only if* it is immutable, this is allowed to return this same instance!  (ie the body of this method being <code>return this;</code>)
		 */
		public ShortList clone();
		
		
		/**
		 * If size is smaller than the current, then this is equivalent to {@link #removeRange(int, int) removeRange}(newSize, this.{@link #size() size()})  :3
		 * If size is larger, then this appends newSize - this.{@link #size() size()} elementToAddIfGrowing's to the end like that many {@link #addShort(short)}  :D
		 */
		public void setSizeShort(int newSize, short elementToAddIfGrowing);
		
		public default void setSize(int newSize, Short elementToAddIfGrowing)
		{
			setSizeShort(newSize, elementToAddIfGrowing);
		}
		
		public default void setSize(int newSize)
		{
			setSizeShort(newSize, ((short)0));
		}
		
		
		
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void setAllShorts(int index, short[] array)
		{
			setAllShorts(index, array, 0, array.length);
		}
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void setAllShorts(int index, Slice<short[]> arraySlice)
		{
			setAllShorts(index, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		public default void setAllShorts(int start, short[] array, int offset, int length)
		{
			int size = this.size();
			
			rangeCheckIntervalByLength(size, start, length);
			rangeCheckIntervalByLength(array.length, offset, length);
			
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(size, start, length);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				if (u.getUnderlying() instanceof short[])
				{
					Slice<short[]> s = (Slice<short[]>) u;
					
					System.arraycopy(array, offset, s.getUnderlying(), s.getOffset()+start, length);
					
					return;
				}
			}
			
			
			defaultSetAllShorts(this, start, array, offset, length);
		}
		
		
		public static void defaultSetAllShorts(ShortList list, int start, @WritableValue short[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				list.setShort(start + i, array[offset + i]);
		}
		
		
		
		
		
		@Override
		public default void setAll(int destIndex, List sourceU, int sourceIndex, @Nonnegative int amount) throws IndexOutOfBoundsException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			List<Short> source = sourceU;
			ShortList dest = this;
			
			int sourceSize = source.size();
			int destSize = dest.size();
			rangeCheckIntervalByLength(sourceSize, sourceIndex, amount);
			rangeCheckIntervalByLength(destSize, destIndex, amount);
			
			
			
			if (TransparentContiguousArrayBackedCollection.is(source))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection<?>)source).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(sourceSize, sourceIndex, amount);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(sourceSize, u);
				
				if (u.getUnderlying() instanceof short[])
				{
					Slice<short[]> s = (Slice<short[]>)u;
					this.setAllShorts(destIndex, s.subslice(sourceIndex, amount));
					return;
				}
			}
			
			
			
			
			if (source instanceof ShortList)
			{
				ShortList primSource = (ShortList) source;
				
				if (destIndex < sourceIndex)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
				{
					for (int i = 0; i < amount; i++)
						dest.setShort(destIndex+i, primSource.getShort(sourceIndex+i));
				}
				else
				{
					for (int i = amount-1; i >= 0; i--)
						dest.setShort(destIndex+i, primSource.getShort(sourceIndex+i));
				}
			}
			else
			{
				if (destIndex < sourceIndex)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
				{
					for (int i = 0; i < amount; i++)
						dest.set(destIndex+i, source.get(sourceIndex+i));
				}
				else
				{
					for (int i = amount-1; i >= 0; i--)
						dest.set(destIndex+i, source.get(sourceIndex+i));
				}
			}
		}
		
		
		
		
		
		
		
		/**
		 * Copies <code>array.length</code> elements into <code>array</code> starting with the <code>start</code>th element.<br>
		 */
		public default void getAllShorts(int start, @WritableValue short[] array, int offset, int length)
		{
			int size = this.size();
			
			rangeCheckIntervalByLength(size, start, length);
			rangeCheckIntervalByLength(array.length, offset, length);
			
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(size, start, length);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				if (u.getUnderlying() instanceof short[])
				{
					Slice<short[]> s = (Slice<short[]>) u;
					
					System.arraycopy(s.getUnderlying(), s.getOffset()+start, array, offset, length);
					
					return;
				}
			}
			
			
			defaultGetAllShorts(this, start, array, offset, length);
		}
		
		
		public static void defaultGetAllShorts(ShortList list, int start, @WritableValue short[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				array[offset + i] = list.getShort(start + i);
		}
		
		
		
		
		
		
		@ThrowAwayValue
		public default short[] getAllShorts(int start, int end)
		{
			rangeCheckInterval(this.size(), start, end);
			
			short[] buff = new short[end-start];
			getAllShorts(start, buff, 0, buff.length);
			return buff;
		}
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public default boolean addShort(short value)
		{
			this.insertShort(this.size(), value);
			return true;
		}
		
		@Override
		public default boolean addAll(Collection<? extends Short> c)
		{
			this.addAll(this.size(), c);
			return true;
		}
		
		@Override
		public default boolean addAllShorts(short[] elements, int offset, int length)
		{
			insertAllShorts(this.size(), elements, offset, length);
			return true;
		}
		
		public default void removeByIndex(int index)
		{
			removeRange(index, index+1);
		}
		
		public default short removeShortByIndex(int index) throws IndexOutOfBoundsException
		{
			short v = this.getShort(index);
			removeByIndex(index);
			return v;
		}
		
		@Override
		public default boolean removeShort(short value)
		{
			int i = this.indexOfShort(value);
			
			if (i != -1)
			{
				this.removeByIndex(i);
				return true;
			}
			else
			{
				return false;
			}
		}
		
		@Override
		public default boolean retainAll(Collection<?> c)
		{
			if (c instanceof ShortCollection)
			{
				ShortCollection cc = (ShortCollection) c;
				
				boolean changedAtAll = false;
				
				for (int i = size() - 1; i >= 0; i--)
				{
					if (!cc.containsShort(getShort(i)))
					{
						removeShortByIndex(i);
						changedAtAll = true;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (int i = size() - 1; i >= 0; i--)
				{
					if (!c.contains(getShort(i)))
					{
						removeShortByIndex(i);
						changedAtAll = true;
					}
				}
				
				return changedAtAll;
			}
		}
		
		
		
		
		
		
		
		
		@Override
		public default SimpleShortIterator newSimpleShortIterator()
		{
			return new SimpleShortIterator()
			{
				int index = 0;
				
				public short nextrpShort() throws StopIterationReturnPath
				{
					int i = index;
					
					if (i >= size())
					{
						throw StopIterationReturnPath.I;
					}
					else
					{
						index = i + 1;
						return getShort(i);
					}
				}
			};
		}
		
		
		
		
		
		@Override
		public default boolean containsShort(short value)
		{
			return indexOfShort(value) != -1;
		}
		
		
		public default void insertAllShorts(int index, short[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				insertShort(index+i, array[offset+i]);
		}
		
		
		@Override
		public default boolean addAll(int index, Collection<? extends Short> c)
		{
			if (c instanceof ShortCollection)
			{
				ShortCollection cc = (ShortCollection)c;
				
				SimpleShortIterator i = cc.newSimpleShortIterator();
				while (true)
				{
					try
					{
						insertShort(index, i.nextrpShort());
						index++;  //doesn't matter if this gets executed on the last iteration :3
					}
					catch (StopIterationReturnPath rp)
					{
						break;
					}
				}
			}
			else
			{
				for (Short e : c)
				{
					insertShort(index, e);
					index++;
				}
			}
			
			return true;
		}
		
		
		
		public default void insertAllShorts(int index, short[] array)
		{
			insertAllShorts(index, array, 0, array.length);
		}
		
		public default void insertAllShorts(int index, Slice<short[]> arraySlice)
		{
			insertAllShorts(index, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		
		
		@Override
		public default Short get(int index)
		{
			return this.getShort(index);
		}
		
		@Override
		public default Short set(int index, Short value)
		{
			Short previous = get(index);
			this.setShort(index, value);
			return previous;
		}
		
		
		@Override
		public default void add(int index, Short value)
		{
			this.insertShort(index, value);
		}
		
		@Override
		public default Short remove(int index) throws IndexOutOfBoundsException
		{
			return this.removeShortByIndex(index);
		}
		
		
		@Override
		public default int indexOf(Object o)
		{
			return o instanceof Short ? indexOfShort((Short)o) : -1;
		}
		
		@Override
		public default int lastIndexOf(Object o)
		{
			return o instanceof Short ? lastIndexOfShort((Short)o) : -1;
		}
		
		
		
		@Override
		public default ListIterator<Short> listIterator()
		{
			return listIterator(0);
		}
		
		@Override
		public default ListIterator<Short> listIterator(int index)
		{
			//Todo
			throw new NotYetImplementedException();
		}
		
		@Override
		public default ShortList subList(int fromIndex, int toIndex)   //Also note that we narrow the return types of this :3
		{
			if (fromIndex == 0 && toIndex == this.size())
				return this;
			
			if (this instanceof Sublist)
			{
				Sublist<Short> s = (Sublist<Short>)this;
				return new ShortSublist((ShortList)s.getUnderlying(), s.getSublistStartingIndex() + fromIndex, toIndex - fromIndex);
			}
			else
			{
				return new ShortSublist(this, fromIndex, toIndex - fromIndex);
			}
		}
		
		
		
		
		
		
		
		
		
		
		// Multiple-Inheritance conflict resolvers X'3
		@Override
		public default boolean removeAll(Collection<?> c)
		{
			return DefaultToArraysShortCollection.super.removeAll(c);
		}
		
		@Override
		public default boolean remove(Object o)
		{
			return DefaultToArraysShortCollection.super.remove(o);
		}
		
		@Override
		public default boolean containsAll(Collection<?> c)
		{
			return DefaultToArraysShortCollection.super.containsAll(c);
		}
		
		@Override
		public default boolean add(Short e)
		{
			return DefaultToArraysShortCollection.super.add(e);
		}
		
		@Override
		public default boolean isEmpty()
		{
			return DefaultToArraysShortCollection.super.isEmpty();
		}
		
		@Override
		public default boolean contains(Object o)
		{
			return DefaultToArraysShortCollection.super.contains(o);
		}
		
		
		
		
		
		@Override
		public default Object[] toArray()
		{
			return DefaultToArraysShortCollection.super.toArray();
		}
		
		@Override
		public default <T> T[] toArray(T[] a)
		{
			return DefaultToArraysShortCollection.super.toArray(a);
		}
		
		
		
		
		
		
		
		@Override
		public default void storeBoxingIntoArray(Slice<Object[]> array)
		{
			int n = this.size();
			for (int i = 0; i < n; i++)
				ArrayUtilities.setReference((Slice)array, i, this.get(i));
		}
		
		
		
		
		
		
		//Narrow the return types of these :3
		
		@Override
		public default ShortList subListToEnd(int start)
		{
			return (ShortList)NiceList.super.subListToEnd(start);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
		
		@Override
		public default ShortList subListFromBeginning(int lengthWhichIsEndExclusiveInThisCaseXD)
		{
			return (ShortList)NiceList.super.subListFromBeginning(lengthWhichIsEndExclusiveInThisCaseXD);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
		
		@Override
		public default ShortList subListByLength(int start, int length)
		{
			return (ShortList)NiceList.super.subListByLength(start, length);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
	}
	
	
	
	
	
	
	public static interface DefaultShiftingBasedShortList
	extends ShortList
	{
		public default void clear()
		{
			removeRange(0, size());
		}
		
		@Override
		public default void insertShort(int index, short value)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, 1);
			this.setShort(index, value);
		}
		
		@Override
		public default void insertAllShorts(int index, short[] array, int offset, int length)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, length);
			
			this.setAllShorts(index, array, offset, length);
		}
		
		
		@Override
		public default boolean addAll(int index, Collection<? extends Short> c)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			int amount = c.size();  //copy this incase c == this! XD
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, amount);
			
			if (c instanceof List)
			{
				listcopy((List)c, 0, this, index, amount);
			}
			else
			{
				if (c instanceof ShortCollection)
				{
					SimpleShortIterator iterator = ((ShortCollection)c).newSimpleShortIterator();
					
					int i = 0;
					while (true)
					{
						short e;
						try
						{
							e = iterator.nextrpShort();
						}
						catch (StopIterationReturnPath rp)
						{
							break;
						}
						
						this.setShort(index+i, e);
						i++;
					}
				}
				else
				{
					int i = 0;
					for (Short e : c)
					{
						this.set(index+i, e);
						i++;
					}
				}
			}
			
			return true;
		}
		
		@Override
		public default void setSizeShort(int newSize, short elementToAddIfGrowing)
		{
			int oldSize = this.size();
			int amount = newSize - oldSize;
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, oldSize, amount);
			
			//Growing results in *undefined* contents, not necessarily initialized ones!  (eg, it may already have some/enough elements allocated eg, in an underlying array and it just increases the int size field :3 )
			for (int i = oldSize; i < newSize; i++)
				this.setShort(i, elementToAddIfGrowing);
		}
		
		
		
		
		@Override
		public default void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			rangeCheckInterval(this.size(), start, pastEnd);
			
			int length = pastEnd - start;
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, pastEnd, -length);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ShortSublist
	implements ShortList, DefaultShiftingBasedShortList, Sublist<Short>, ShiftableList
	{
		protected final ShortList underlying;
		protected final int start;
		protected int size;
		
		public ShortSublist(ShortList underlying, int start, int initialSize)
		{
			rangeCheckIntervalByLength(underlying.size(), start, initialSize);
			
			this.underlying = underlying;
			this.start = start;
			this.size = initialSize;
		}
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public short getShort(int index)
		{
			return underlying.getShort(index + start);
		}
		
		@Override
		public void setShort(int index, short value)
		{
			underlying.setShort(index + start, value);
		}
		
		@Override
		public void shiftRegionStretchingFromIndexToEndByAmountChangingSize(int start, int amount)
		{
			rangeCheckCursorPoint(size(), start);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(underlying, start, amount);
			
			size += amount;
		}
		
		
		
		@Override
		public ShortList clone()
		{
			ShortList c = new ShortArrayList();
			c.addAll(this);
			return c;
		}
		
		
		
		@Override
		public ShortList getUnderlying()
		{
			return underlying;
		}
		
		@Override
		public int getSublistStartingIndex()
		{
			return start;
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ShortArrayList
	implements DefaultShiftingBasedShortList, ListWithSetSize<Short>, Trimmable, TransparentContiguousArrayBackedCollection<short[]>, KnowsLengthFixedness
	{
		protected int size = 0;
		protected short[] data;
		protected GrowerComputationallyUnreducedPurelyRecursive grower;
		
		
		
		
		//In case subclasses don't want to deal with the other things ^^'
		protected ShortArrayList(Void thisiswhyyoushouldntusejavaconstructorsAlthoughIdontknowhowtomakeinheritanceworkwellwithoutinitmethodsorfactoriesorsomething)
		{
		}
		
		
		/**
		 * Capacity increment is different from Vector's.  It is the percentage of the capacity by which the capacity is increased.
		 * The formula being: <code>newCapacity = capacity + (capacity * capacityIncrement)</code><br>
		 * @param grower This can be anything.  The only downside is performance: all methods will add at least the minimum amount required regardless of what this specifies.
		 */
		public ShortArrayList(int initialCapacity, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this.data = initialCapacity == 0 ? ArrayUtilities.EmptyShortArray : new short[initialCapacity];
			this.grower = grower;
		}
		
		public ShortArrayList(@LiveValue @WritableValue short[] LIVEBACKING, int size, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this.data = LIVEBACKING;
			this.grower = grower;
			this.size = size;
		}
		
		public ShortArrayList(@SnapshotValue @ReadonlyValue Collection<Short> c, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this(c.size(), grower);
			this.addAll(c);
		}
		
		
		
		
		
		public ShortArrayList()
		{
			this(DefaultPrimitiveArrayListInitialCapacity);
		}
		
		public ShortArrayList(int initialCapacity)
		{
			this(initialCapacity, defaultPrimitiveArrayListGrower(initialCapacity));
		}
		
		public ShortArrayList(@SnapshotValue @ReadonlyValue Collection<Short> c)
		{
			this(c, defaultPrimitiveArrayListGrower(c.size()));
		}
		
		public ShortArrayList(@LiveValue @WritableValue short[] LIVEBACKING, int size)
		{
			this(LIVEBACKING, size, defaultPrimitiveArrayListGrower(LIVEBACKING.length));
		}
		
		public ShortArrayList(@LiveValue @WritableValue short[] LIVEBACKING)
		{
			this(LIVEBACKING, LIVEBACKING.length);
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return false;
		}
		
		
		
		@Override
		public void setSize(int newSize)
		{
			ensureCapacity(newSize);
			this.size = newSize;
			
			// :D
		}
		
		
		
		
		
		//This pattern is nicer for subclassing in any Java class! :D
		protected void setupClone(ShortArrayList freshClone)
		{
			freshClone.size = this.size;
			
			freshClone.data = new short[size];
			System.arraycopy(this.data, 0, freshClone.data, 0, size);
			
			freshClone.grower = this.grower;
		}
		
		@Override
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public ShortArrayList clone()
		{
			ShortArrayList clone = new ShortArrayList();
			this.setupClone(clone);
			return clone;
		}
		
		
		
		
		
		
		
		
		
		
		//This is what unlocks all the System.arraycopy() magic!  8>
		@ImplementationTransparency
		@LiveValue
		public Slice<short[]> getLiveContiguousArrayBackingUNSAFE()
		{
			return new Slice(data, 0, size);
		}
		
		
		
		
		
		
		
		
		
		public void setShort(int index, short value)
		{
			rangeCheckMember(this.size, index);
			data[index] = value;
		}
		
		public short getShort(int index)
		{
			rangeCheckMember(this.size(), index);
			return data[index];
		}
		
		
		
		
		
		
		
		@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public void clear()
		{
			size = 0;
		}
		
		@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public int size()
		{
			return size;
		}
		
		@ImplementationTransparency
		public void trimToSize()
		{
			short[] newdata = new short[size];
			System.arraycopy(data, 0, newdata, 0, size);
			this.data = newdata;
		}
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.trimToSize();
			return TrimmableTrimRV.DontKeepInvoking;
		}
		
		
		
		
		
		@ImplementationTransparency
		public int getCapacity()
		{
			return data.length;
		}
		
		@ImplementationTransparency
		public void ensureCapacity(int minCapacity)
		{
			if (data.length < minCapacity)
			{
				//Enlarge
				int newCapacity = getNewCapacity(minCapacity);
				short[] newData = new short[newCapacity];
				System.arraycopy(data, 0, newData, 0, data.length);
				this.data = newData;
			}
		}
		
		/**
		 * This is also guaranteed not to return a capacity smaller than the provided minimum.
		 */
		@ImplementationTransparency
		public int getNewCapacity(int minCapacity)
		{
			if (minCapacity < 0)
				throw new IllegalArgumentException();
			
			return Math.max(minCapacity, grower.getNewSizeRecursive(data.length));  //The Math.max(min, ...) ensures that the grower can't break anything!  :3
		}
		
		
		
		
		
		
		
		/**
		 * sets the underlying array that backs the array list.
		 * Be careful, though, as updates can cause a new array to be created, obsoleting this one.
		 * Note, also, that only up to {@link #size()} elements will be used.
		 */
		@ImplementationTransparency
		public void setDirectBuffer(@LiveValue short[] array)
		{
			if (array.length < size)
				throw new IllegalArgumentException("Array is too small; size="+size+", capacity="+array.length);
			
			this.data = array;
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static class FixedLengthArrayWrapperShortList
	implements ShortList, TransparentContiguousArrayBackedCollection<short[]>, KnowsLengthFixedness
	{
		protected final short[] underlying;
		protected final int offset, length;
		
		public FixedLengthArrayWrapperShortList(short[] underlying, int offset, int length)
		{
			rangeCheckIntervalByLength(underlying.length, offset, length);
			
			this.underlying = underlying;
			this.offset = offset;
			this.length = length;
		}
		
		public FixedLengthArrayWrapperShortList(short[] underlying)
		{
			this(underlying, 0, underlying.length);
		}
		
		public FixedLengthArrayWrapperShortList(Slice<short[]> underlying)
		{
			this(underlying.getUnderlying(), underlying.getOffset(), underlying.getLength());
		}
		
		@Override
		public ShortList clone()
		{
			return new FixedLengthArrayWrapperShortList(toShortArray());
		}
		
		
		
		@Override
		public short[] toShortArray()
		{
			return sliceToNewShortArrayOP(this.getLiveContiguousArrayBackingUNSAFE());
		}
		
		@Override
		public Slice<short[]> getLiveContiguousArrayBackingUNSAFE()
		{
			return new Slice<>(underlying, offset, length);
		}
		
		@Override
		public int size()
		{
			return length;
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return true;
		}
		
		
		
		@Override
		public SimpleShortIterator newSimpleShortIterator()
		{
			return new SimpleShortIterator()
			{
				int index = 0;
				
				public short nextrpShort() throws StopIterationReturnPath
				{
					int i = index;
					
					if (i >= size())
					{
						throw StopIterationReturnPath.I;
					}
					else
					{
						index = i + 1;
						return underlying[offset+i];
					}
				}
			};
		}
		
		@Override
		public ShortList subList(int fromIndex, int toIndex)
		{
			rangeCheckInterval(this.size(), fromIndex, toIndex);
			return new FixedLengthArrayWrapperShortList(this.underlying, fromIndex+offset, toIndex-fromIndex);
		}
		
		@Override
		public short getShort(int index)
		{
			return underlying[offset + index];
		}
		
		@Override
		public void setShort(int index, short value)
		{
			underlying[offset + index] = value;
		}
		
		
		
		
		
		
		
		
		
		//Unallowed Dim-altering Methods!
		
		@Override
		public void clear()
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setSizeShort(int newSize, short elementToAddIfGrowing)
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void insertShort(int index, short value)
		{
			throw new UnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	@ReadonlyValue
	public static ShortList shortArrayAsList(@LiveValue @WritableValue short[] array, int offset, int length)
	{
		return new FixedLengthArrayWrapperShortList(array, offset, length);
	}
	
	@ReadonlyValue
	public static ShortList shortArrayAsList(@LiveValue @WritableValue short[] array)
	{
		return shortArrayAsList(array, 0, array.length);
	}
	
	@ReadonlyValue
	public static ShortList shortArrayAsList(@LiveValue @WritableValue Slice<short[]> arraySlice)
	{
		return shortArrayAsList(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	
	
	
	@ThrowAwayValue
	public static ShortList shortArrayAsMutableList(@SnapshotValue @ReadonlyValue short[] array, int offset, int length)
	{
		return new ShortArrayList(new FixedLengthArrayWrapperShortList(array, offset, length));
	}
	
	@ThrowAwayValue
	public static ShortList shortArrayAsMutableList(@SnapshotValue @ReadonlyValue short[] array)
	{
		return shortArrayAsMutableList(array, 0, array.length);
	}
	
	@ThrowAwayValue
	public static ShortList shortArrayAsMutableList(@SnapshotValue @ReadonlyValue Slice<short[]> arraySlice)
	{
		return shortArrayAsMutableList(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	@Immutable
	public static class ImmutableShortArrayList
	implements Serializable, Comparable<ImmutableShortArrayList>, ShortList, KnowsLengthFixedness     //Let's not implement the general interfaces for accessing the underlying data jussssst to help make sure anyone who accesses it *knows* it's meant to be VERY MUCH READONLY!  XD'''
	{
		private static final long serialVersionUID = 1L;
		
		
		protected final short[] data;
		
		//Yeah I know it's bad practice for a thread-safe immutable object to cache things, but at best effort is wasted, no constraints are violated!     (If I'm wrong, fire me X'D )
		protected transient long hashCode;     //we cache it in case the shortarray is large :33
		
		protected ImmutableShortArrayList(short[] LIVEDATA)
		{
			if (LIVEDATA == null) throw new NullPointerException();
			this.data = LIVEDATA;
		}
		
		
		
		
		
		/**
		 * DO NOT *EVER* MODIFY THE ARRAY YOU PASSED HERE AFTER CALLING THIS X"D
		 * ( THE HASH CODE IS CACHED FOR ONE, AND THIS MAY BE EMBEDDED IN HASHMAPS--THIS IS SUPPOSED TO BE IMMUTABLE AFTER ALL X'DDD )
		 * @see #getREADONLYLiveWholeArrayBackingUNSAFE()
		 */
		@ImplementationTransparency
		public static ImmutableShortArrayList newLIVE(@LiveValue short[] LIVEDATA)
		{
			return new ImmutableShortArrayList(LIVEDATA);
		}
		
		
		@ImplementationTransparency
		public static ImmutableShortArrayList newSingleton(short singleMember)
		{
			return new ImmutableShortArrayList(new short[]{singleMember});
		}
		
		
		
		
		public static ImmutableShortArrayList newCopying(@SnapshotValue List<Short> data)
		{
			if (data instanceof ImmutableShortArrayList)
				return (ImmutableShortArrayList)data;
			
			if (TransparentContiguousArrayBackedCollection.is(data))
			{
				Slice<Object> s = ((TransparentContiguousArrayBackedCollection)data).getLiveContiguousArrayBackingUNSAFE();
				if (s.getUnderlying() instanceof short[])
				{
					return newCopying((Slice)s);
				}
			}
			
			if (data instanceof ShortList)
			{
				return newLIVE(((ShortList)data).toShortArray());
			}
			else
			{
				int n = data.size();
				short[] a = new short[n];
				
				int i = 0;
				for (Short e : data)
				{
					if (i >= n)
						throw new ImpossibleException();
					
					a[i] = e;
					i++;
				}
				
				if (i != n)
					throw new ImpossibleException();
				
				return newLIVE(a);
			}
		}
		
		public static ImmutableShortArrayList newCopying(@SnapshotValue short[] data)
		{
			return new ImmutableShortArrayList(data.clone());
		}
		
		public static ImmutableShortArrayList newCopying(@SnapshotValue short[] data, int offset, int length)
		{
			if (offset == 0 && length == data.length)
				return newCopying(data);  //who knows, short[].clone() might be faster! \o/   (it certainly shouldn't be slower!!)
			else
			{
				short[] newArray = new short[length];
				System.arraycopy(data, offset, newArray, 0, length);
				return new ImmutableShortArrayList(newArray);
			}
		}
		
		public static ImmutableShortArrayList newCopying(@SnapshotValue Slice<short[]> data)
		{
			return newCopying(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		
		
		
		
		public static ImmutableShortArrayList newLIVEOrCopying(@PossiblySnapshotPossiblyLiveValue short[] data, int offset, int length)
		{
			if (offset == 0 && length == data.length)
				return newLIVE(data);
			else
				return newCopying(data, offset, length);
		}
		
		public static ImmutableShortArrayList newLIVEOrCopying(@PossiblySnapshotPossiblyLiveValue Slice<short[]> data)
		{
			return newLIVEOrCopying(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		
		
		
		
		
		
		public static final ImmutableShortArrayList Empty = ImmutableShortArrayList.newLIVE(ArrayUtilities.EmptyShortArray);
		
		
		
		
		
		
		
		
		@ThrowAwayValue
		@Override
		public short[] toShortArray()
		{
			return data.clone();
		}
		
		/**
		 * DO NOT MODIFY THIS X"D
		 * ( THE HASH CODE IS CACHED FOR ONE, AND THIS MAY BE EMBEDDED IN HASHMAPS--THIS IS SUPPOSED TO BE IMMUTABLE AFTER ALL X'DDD )
		 */
		@ImplementationTransparency
		@LiveValue
		@ReadonlyValue //!!!!
		public short[] getREADONLYLiveWholeArrayBackingUNSAFE()
		{
			return data;
		}
		
		@Override
		public int size()
		{
			return data.length;
		}
		
		@Override
		public short getShort(int index)
		{
			return data[index];
		}
		
		@Override
		public void getAllShorts(int offsetInThisSource, @WritableValue short[] dest, int offsetInDest, int lengthInBoth)
		{
			System.arraycopy(this.data, offsetInThisSource, dest, offsetInDest, lengthInBoth);
		}
		
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return true;
		}
		
		
		
		
		@Override
		public int hashCode()
		{
			long hashCodeField = this.hashCode;
			
			boolean cached = hashCodeField != 0;
			
			
			if (cached)
			{
				return (int)this.hashCode;
			}
			else
			{
				int hashCode = Arrays.hashCode(this.data);
				this.hashCode = hashCode | 0xFFFF_FFFF_0000_0000l;  //hashCodes can be zero!  Not often XDD   But can be!  So it's not correct unless we account for that :>      (And if it was two fields then it'd be a concurrency bug!)
				return hashCode;
			}
		}
		
		
		
		
		@Override
		public boolean equals(@ReadonlyValue Object obj)
		{
			if (obj == this)
			{
				return true;
			}
			else if (obj instanceof ImmutableShortArrayList)
			{
				ImmutableShortArrayList other = (ImmutableShortArrayList)obj;
				
				//Checking the hashcodes is a VERY fast negative test and is almost always true-negative!! :DDD   (rarely it's false-positive, but never false-negative!)
				
				long thisHC = this.hashCode;
				long otherHC = other.hashCode;
				
				boolean thisHashCodeCached = thisHC != 0;
				boolean otherHashCodeCached = otherHC != 0;
				
				int thisHashCode = (int)thisHC;
				int otherHashCode = (int)otherHC;
				
				if (thisHashCodeCached)
				{
					if (otherHashCodeCached)
					{
						if (thisHashCode != otherHashCode)
							return false;
					}
					else
					{
						//Scanning one short array is faster than scanning two! :D
						if (thisHashCode != other.hashCode())
							return false;
					}
				}
				else
				{
					if (otherHashCodeCached)
					{
						//Scanning one short array is faster than scanning two! :D
						if (this.hashCode() != otherHashCode)
							return false;
					}
					else
					{
						//Then don't make any hash codes X3
					}
				}
				
				return this.equalsShortArray(other.data);
			}
			else
			{
				return false;
			}
		}
		
		
		@Override
		public int compareTo(@ReadonlyValue ImmutableShortArrayList other)
		{
			return this.compareToShortArray(other.data);
		}
		
		
		public boolean equalsShortArray(@ReadonlyValue short[] o)
		{
			short[] ourData = this.getREADONLYLiveWholeArrayBackingUNSAFE();
			short[] theirData = o;
			
			return Arrays.equals(ourData, theirData);
		}
		
		
		public int compareToShortArray(@ReadonlyValue short[] o)
		{
			short[] ourData = this.getREADONLYLiveWholeArrayBackingUNSAFE();
			short[] theirData = o;
			
			return ArrayUtilities.compareLittleEndianLengthsFirst(ourData, theirData);  //lengths-first is more efficient if they're different but hardly any less efficient if they're always the same!  A good default, don't you think? :D
		}
		
		
		
		
		@Override
		public ImmutableShortArrayList clone()
		{
			return this;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		// Unallowed Write Methods!
		
		@Override
		public void clear()
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setSizeShort(int newSize, short elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void insertShort(int index, short value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setShort(int index, short value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static ShortList unmodifiableShortList(ShortList shortList)
	{
		return shortList instanceof UnmodifiableShortListWrapper ? shortList : new UnmodifiableShortListWrapper(shortList);
	}
	
	public static class UnmodifiableShortListWrapper
	implements ShortList, KnowsLengthFixedness, TransparentContiguousArrayBackedCollection
	{
		protected final ShortList underlying;
		
		public UnmodifiableShortListWrapper(ShortList underlying)
		{
			this.underlying = underlying;
		}
		
		@ImplementationTransparency
		public ShortList getUnderlying()
		{
			return underlying;
		}
		
		
		
		
		
		@Override
		public UnmodifiableShortListWrapper clone()
		{
			return new UnmodifiableShortListWrapper(underlying.clone());
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.underlying);
		}
		
		@Override
		public boolean isTransparentContiguousArrayBackedCollection()
		{
			return TransparentContiguousArrayBackedCollection.is(underlying);
		}
		
		@Override
		public Slice getLiveContiguousArrayBackingUNSAFE()
		{
			return ((TransparentContiguousArrayBackedCollection)underlying).getLiveContiguousArrayBackingUNSAFE();
		}
		
		
		
		
		
		
		//Use default so they can't get around our readonly-ness!  ;D
		//	public Iterator<Short> iterator()
		//	public ListIterator<Short> listIterator()
		//	public ListIterator<Short> listIterator(int index)
		//	public SimpleIterator<Short> simpleIterator()
		//	public SimpleShortIterator newSimpleShortIterator()
		//	public ShortList subList(int fromIndex, int toIndex)
		
		
		
		
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeRangeByLength(int start, int length) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void replaceAll(UnaryOperator<Short> operator)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeIf(Predicate<? super Short> filter)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void sort(Comparator<? super Short> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void clear()
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public Short remove(int index)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllShorts(short[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllShorts(Slice<short[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllShorts(short[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllShorts(Slice<short[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllShorts(short[] a, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setShort(int index, short value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertShort(int index, short value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSizeShort(int newSize, short elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSize(int newSize, Short elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSize(int newSize)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllShorts(int index, short[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllShorts(int index, Slice<short[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllShorts(int index, short[] array, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAll(int destIndex, List sourceU, int sourceIndex, int amount) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addShort(short value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll(Collection<? extends Short> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllShorts(short[] elements, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeByIndex(int index)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public short removeShortByIndex(int index) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeShort(short value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean retainAll(Collection<?> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllShorts(int index, short[] array, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll(int index, Collection<? extends Short> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllShorts(int index, short[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllShorts(int index, Slice<short[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public Short set(int index, Short value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void add(int index, Short value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeAll(Collection<?> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean remove(Object o)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean add(Short e)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		
		
		
		
		
		
		
		
		
		public void forEach(Consumer<? super Short> action)
		{
			underlying.forEach(action);
		}
		
		public int size()
		{
			return underlying.size();
		}
		
		public boolean equals(Object o)
		{
			return underlying.equals(o);
		}
		
		public int hashCode()
		{
			return underlying.hashCode();
		}
		
		public Spliterator<Short> spliterator()
		{
			return underlying.spliterator();
		}
		
		public Stream<Short> stream()
		{
			return underlying.stream();
		}
		
		public Stream<Short> parallelStream()
		{
			return underlying.parallelStream();
		}
		
		public short getShort(int index)
		{
			return underlying.getShort(index);
		}
		
		public int indexOfShort(short value)
		{
			return underlying.indexOfShort(value);
		}
		
		public int lastIndexOfShort(short value)
		{
			return underlying.lastIndexOfShort(value);
		}
		
		public boolean equivalent(List<Short> other)
		{
			return underlying.equivalent(other);
		}
		
		public short[] toShortArray()
		{
			return underlying.toShortArray();
		}
		
		public boolean containsShort(short value)
		{
			return underlying.containsShort(value);
		}
		
		public Short get(int index)
		{
			return underlying.get(index);
		}
		
		public int indexOf(Object o)
		{
			return underlying.indexOf(o);
		}
		
		public int lastIndexOf(Object o)
		{
			return underlying.lastIndexOf(o);
		}
		
		public boolean containsAll(Collection<?> c)
		{
			return underlying.containsAll(c);
		}
		
		public boolean isEmpty()
		{
			return underlying.isEmpty();
		}
		
		public boolean contains(Object o)
		{
			return underlying.contains(o);
		}
		
		public Object[] toArray()
		{
			return underlying.toArray();
		}
		
		public <T> T[] toArray(T[] a)
		{
			return underlying.toArray(a);
		}
		
		public void storeBoxingIntoArray(Slice<Object[]> array)
		{
			underlying.storeBoxingIntoArray(array);
		}
		
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ShortTable
	{
		protected int width;
		protected short[] data;
		
		public ShortTable(int width, int height)
		{
			if (width < 0)
				throw new IllegalArgumentException();
			if (height < 0)
				throw new IllegalArgumentException();
			
			data = new short[width * height];
		}
		
		
		
		@Nonnegative
		public int getNumberOfColumns()
		{
			return width;
		}
		
		@Nonnegative
		public int getNumberOfRows()
		{
			return data.length / width;
		}
		
		
		@Nonnegative
		public int getNumberOfCells()
		{
			return data.length;
		}
		
		
		
		
		
		public short getCellContentsShort(int columnIndex, int rowIndex) throws IndexOutOfBoundsException
		{
			if (columnIndex < 0) throw new IndexOutOfBoundsException();
			if (rowIndex < 0) throw new IndexOutOfBoundsException();
			if (columnIndex >= getNumberOfColumns()) throw new IndexOutOfBoundsException();
			if (rowIndex >= getNumberOfRows()) throw new IndexOutOfBoundsException();
			
			return this.data[rowIndex * this.getNumberOfColumns() + columnIndex];
		}
		
		public void setCellContentsShort(int columnIndex, int rowIndex, @Nullable short newValue) throws IndexOutOfBoundsException
		{
			if (columnIndex < 0) throw new IndexOutOfBoundsException();
			if (rowIndex < 0) throw new IndexOutOfBoundsException();
			if (columnIndex >= getNumberOfColumns()) throw new IndexOutOfBoundsException();
			if (rowIndex >= getNumberOfRows()) throw new IndexOutOfBoundsException();
			
			this.data[rowIndex * this.getNumberOfColumns() + columnIndex] = newValue;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Nullable
		public Short getCellContents(int columnIndex, int rowIndex) throws IndexOutOfBoundsException
		{
			return getCellContentsShort(columnIndex, rowIndex);
		}
		
		public void setCellContents(int columnIndex, int rowIndex, @Nullable Short newValue) throws IndexOutOfBoundsException
		{
			setCellContentsShort(columnIndex, rowIndex, newValue);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static float getFloat(List<Float> list, int index)
	{
		if (list instanceof FloatList)
			return ((FloatList) list).getFloat(index);
		else
			return list.get(index);
	}
	
	public static void setFloat(List<Float> list, int index, float value)
	{
		if (list instanceof FloatList)
			((FloatList) list).setFloat(index, value);
		else
			list.set(index, value);
	}
	
	
	
	
	
	
	
	public static interface SimpleFloatIterable
	extends SimpleIterable<Float>
	{
		public SimpleFloatIterator newSimpleFloatIterator();
		
		public default SimpleIterator<Float> simpleIterator()
		{
			SimpleFloatIterator i = this.newSimpleFloatIterator();
			return () -> i.nextrpFloat();
		}
	}
	
	
	public static interface SimpleFloatIterator
	extends SimpleIterator<Float>
	{
		public float nextrpFloat() throws StopIterationReturnPath;
		
		
		@Override
		public default Float nextrp() throws StopIterationReturnPath
		{
			return nextrpFloat();
		}
	}
	
	
	
	@SignalInterface
	public static interface FloatCollection
	extends PrimitiveCollection<Float>, SimpleFloatIterable
	{
		public boolean addFloat(float value);
		
		public boolean removeFloat(float value);
		
		
		
		
		
		@ThrowAwayValue
		public static float[] defaultToFloatArray(FloatCollection collection)
		{
			float[] array = new float[collection.size()];
			SimpleFloatIterator i = collection.newSimpleFloatIterator();
			
			int index = 0;
			while (true)
			{
				try
				{
					array[index] = i.nextrpFloat();
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
				
				index++;
			}
			
			return array;
		}
		
		@ThrowAwayValue
		public default float[] toFloatArray()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(this.size(), u);
				
				if (u.getUnderlying() instanceof float[])
				{
					return sliceToNewFloatArrayOP((Slice<float[]>) u);
				}
			}
			
			return defaultToFloatArray(this);
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public default float[] toFloatArrayPossiblyLive()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				int size = this.size();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				Object und = u.getUnderlying();
				
				if (und instanceof float[] && u.getOffset() == 0 && ((float[])und).length == size)
				{
					return (float[])und;
				}
			}
			
			return defaultToFloatArray(this);
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public default Slice<float[]> toFloatArraySlicePossiblyLive()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(this.size(), u);
				
				if (u.getUnderlying() instanceof float[])
				{
					return (Slice<float[]>) u;
				}
			}
			
			return wholeArraySliceFloat(defaultToFloatArray(this));
		}
		
		
		
		
		
		
		public default boolean containsFloat(float value)
		{
			SimpleFloatIterator i = newSimpleFloatIterator();
			
			while (true)
			{
				try
				{
					if (eqSane(i.nextrpFloat(), value))
						return true;
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
			}
			
			return false;
		}
		
		
		
		
		
		
		@Override
		public default Iterator<Float> iterator()
		{
			return SimpleFloatIterable.super.iterator();
		}
		
		@Override
		public default boolean isEmpty()
		{
			return size() == 0;
		}
		
		
		
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllFloats(float[] array)
		{
			return addAllFloats(array, 0, array.length);
		}
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllFloats(Slice<float[]> arraySlice)
		{
			return addAllFloats(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllFloats(float[] elements, int offset, int length)
		{
			return defaultAddAllFloats(this, elements, offset, length);
		}
		
		public static boolean defaultAddAllFloats(FloatCollection self, float[] elements, int offset, int length)
		{
			boolean changedAtAll = false;
			int e = offset + length;
			for (int i = offset; i < e; i++)
				changedAtAll |= self.addFloat(elements[i]);
			return changedAtAll;
		}
		
		
		
		
		public default void removeAllFloats(float[] array)
		{
			removeAllFloats(array, 0, array.length);
		}
		
		public default void removeAllFloats(Slice<float[]> arraySlice)
		{
			removeAllFloats(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public default void removeAllFloats(float[] a, int offset, int length)
		{
			int e = offset + length;
			for (int i = offset; i < e; i++)
				this.removeFloat(a[i]);
		}
		
		
		
		
		
		
		
		
		
		
		@Override
		public default boolean contains(Object o)
		{
			return o instanceof Float && containsFloat((Float)o);
		}
		
		
		@Override
		public default boolean add(Float e)
		{
			return addFloat(e);
		}
		
		
		@Override
		public default boolean remove(Object o)
		{
			return o instanceof Float && removeFloat((Float)o);
		}
		
		
		@Override
		public default boolean containsAll(Collection<?> c)
		{
			if (c instanceof FloatCollection)
			{
				FloatCollection cc = (FloatCollection) c;
				
				SimpleFloatIterator i = cc.newSimpleFloatIterator();
				while (true)
				{
					try
					{
						if (this.containsFloat(i.nextrpFloat()))
							return true;
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return false;
			}
			else
			{
				for (Object e : c)
					if (this.contains(e))
						return true;
				return false;
			}
		}
		
		
		@Override
		public default boolean addAll(Collection<? extends Float> c)
		{
			if (c instanceof FloatCollection)
			{
				boolean changedAtAll = false;
				
				SimpleFloatIterator i = ((FloatCollection)c).newSimpleFloatIterator();
				
				while (true)
				{
					try
					{
						changedAtAll |= this.add(i.nextrpFloat());
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (Float e : c)
				{
					changedAtAll |= this.add(e);
				}
				
				return changedAtAll;
			}
		}
		
		
		
		@Override
		public default boolean removeAll(Collection<?> c)
		{
			if (c instanceof FloatCollection)
			{
				boolean changedAtAll = false;
				
				SimpleFloatIterator i = ((FloatCollection)c).newSimpleFloatIterator();
				
				while (true)
				{
					try
					{
						changedAtAll |= this.remove(i.nextrpFloat());
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (Object e : c)
				{
					changedAtAll |= this.remove(e);
				}
				
				return changedAtAll;
			}
		}
		
		
		
		public default String _toString()
		{
			StringBuilder b = new StringBuilder("[");
			
			SimpleFloatIterator i = newSimpleFloatIterator();
			
			boolean first = true;
			
			while (true)
			{
				float e;
				
				try
				{
					e = i.nextrpFloat();
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
				
				
				
				//Notice this comes after the possible 'break'!  ^_~
				if (first)
					first = false;
				else
					b.append(", ");
				
				b.append(e);
			}
			
			b.append(']');
			
			return b.toString();
		}
	}
	
	
	
	public static interface DefaultToArraysFloatCollection
	extends FloatCollection
	{
		@Override
		public default Object[] toArray()
		{
			Object[] a = new Object[size()];
			storeBoxingIntoArray(wholeArraySliceReference(a));
			return a;
		}
		
		@Override
		public default <T> T[] toArray(T[] a)
		{
			if (a.length >= this.size())
			{
				storeBoxingIntoArray(wholeArraySliceReference(a));
				return a;
			}
			else
			{
				Object[] aa;
				
				if (a.getClass() == Float[].class)
				{
					aa = new Float[size()];
				}
				else if (a.getClass() == Object[].class)
				{
					aa = new Object[size()];
				}
				else
				{
					throw new StructuredClassCastException(a.getClass());
				}
				
				storeBoxingIntoArray(wholeArraySliceReference(aa));
				
				return (T[])aa;
			}
		}
		
		public void storeBoxingIntoArray(Slice<Object[]> array);
	}
	
	
	
	
	
	
	@SignalInterface
	public static interface FloatListRO
	{
		public float getFloat(int index);
		
		public int size();
		
		
		
		public default int indexOfFloat(float value)
		{
			int n = this.size();
			for (int i = 0; i < n; i++)
				if (eqSane(this.getFloat(i), value))
					return i;
			return -1;
		}
		
		public default int lastIndexOfFloat(float value)
		{
			int i = this.size()-1;
			while (i >= 0)
			{
				if (eqSane(this.getFloat(i), value))
					return i;
				break;
			}
			return i;   // ;D
		}
		
		
		
		public default boolean equivalent(List<Float> other)
		{
			int size = this.size();
			if (other.size() != size)
				return false;
			
			
			if (other instanceof FloatListRO)  //All FloatLists will implement this interface too so this accounts for them as well :3
			{
				return equivalentFixedRO((FloatListRO)other);
			}
			else
			{
				for (int i = 0; i < size; i++)
				{
					if (!eqSane(this.getFloat(i), other.get(i).floatValue()))
						return false;
				}
			}
			
			return true;
		}
		
		
		
		public default boolean equivalentFixedRO(FloatListRO other)
		{
			int size = this.size();
			if (other.size() != size)
				return false;
			
			for (int i = 0; i < size; i++)
			{
				if (!eqSane(this.getFloat(i), other.getFloat(i)))
					return false;
			}
			
			return true;
		}
	}
	
	
	
	@SignalInterface
	public static interface FloatListRWFixed
	extends FloatListRO
	{
		public void setFloat(int index, float value);
	}
	
	
	
	@SignalInterface
	public static interface FloatList
	extends FloatCollection, NiceList<Float>, FloatListRO, FloatListRWFixed, ListWithRemoveRange, DefaultToArraysFloatCollection, PubliclyCloneable, ListWithSetSize<Float>, ListWithSetAll, NonuniformMethodsForFloatList
	{
		public void insertFloat(int index, float value);
		
		
		
		/**
		 * This should be equivalent to just constructing and returning a new in-memory stock implementation with the same contents  (but can easily be faster, especially for those stock impl.s! XD )
		 * As for whether it's fixed-length or read-only or duplicateless or etc., that should be the same in the clone as it is in the main impl.
		 * If *and only if* it is immutable, this is allowed to return this same instance!  (ie the body of this method being <code>return this;</code>)
		 */
		public FloatList clone();
		
		
		/**
		 * If size is smaller than the current, then this is equivalent to {@link #removeRange(int, int) removeRange}(newSize, this.{@link #size() size()})  :3
		 * If size is larger, then this appends newSize - this.{@link #size() size()} elementToAddIfGrowing's to the end like that many {@link #addFloat(float)}  :D
		 */
		public void setSizeFloat(int newSize, float elementToAddIfGrowing);
		
		public default void setSize(int newSize, Float elementToAddIfGrowing)
		{
			setSizeFloat(newSize, elementToAddIfGrowing);
		}
		
		public default void setSize(int newSize)
		{
			setSizeFloat(newSize, 0.0f);
		}
		
		
		
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void setAllFloats(int index, float[] array)
		{
			setAllFloats(index, array, 0, array.length);
		}
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void setAllFloats(int index, Slice<float[]> arraySlice)
		{
			setAllFloats(index, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		public default void setAllFloats(int start, float[] array, int offset, int length)
		{
			int size = this.size();
			
			rangeCheckIntervalByLength(size, start, length);
			rangeCheckIntervalByLength(array.length, offset, length);
			
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(size, start, length);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				if (u.getUnderlying() instanceof float[])
				{
					Slice<float[]> s = (Slice<float[]>) u;
					
					System.arraycopy(array, offset, s.getUnderlying(), s.getOffset()+start, length);
					
					return;
				}
			}
			
			
			defaultSetAllFloats(this, start, array, offset, length);
		}
		
		
		public static void defaultSetAllFloats(FloatList list, int start, @WritableValue float[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				list.setFloat(start + i, array[offset + i]);
		}
		
		
		
		
		
		@Override
		public default void setAll(int destIndex, List sourceU, int sourceIndex, @Nonnegative int amount) throws IndexOutOfBoundsException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			List<Float> source = sourceU;
			FloatList dest = this;
			
			int sourceSize = source.size();
			int destSize = dest.size();
			rangeCheckIntervalByLength(sourceSize, sourceIndex, amount);
			rangeCheckIntervalByLength(destSize, destIndex, amount);
			
			
			
			if (TransparentContiguousArrayBackedCollection.is(source))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection<?>)source).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(sourceSize, sourceIndex, amount);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(sourceSize, u);
				
				if (u.getUnderlying() instanceof float[])
				{
					Slice<float[]> s = (Slice<float[]>)u;
					this.setAllFloats(destIndex, s.subslice(sourceIndex, amount));
					return;
				}
			}
			
			
			
			
			if (source instanceof FloatList)
			{
				FloatList primSource = (FloatList) source;
				
				if (destIndex < sourceIndex)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
				{
					for (int i = 0; i < amount; i++)
						dest.setFloat(destIndex+i, primSource.getFloat(sourceIndex+i));
				}
				else
				{
					for (int i = amount-1; i >= 0; i--)
						dest.setFloat(destIndex+i, primSource.getFloat(sourceIndex+i));
				}
			}
			else
			{
				if (destIndex < sourceIndex)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
				{
					for (int i = 0; i < amount; i++)
						dest.set(destIndex+i, source.get(sourceIndex+i));
				}
				else
				{
					for (int i = amount-1; i >= 0; i--)
						dest.set(destIndex+i, source.get(sourceIndex+i));
				}
			}
		}
		
		
		
		
		
		
		
		/**
		 * Copies <code>array.length</code> elements into <code>array</code> starting with the <code>start</code>th element.<br>
		 */
		public default void getAllFloats(int start, @WritableValue float[] array, int offset, int length)
		{
			int size = this.size();
			
			rangeCheckIntervalByLength(size, start, length);
			rangeCheckIntervalByLength(array.length, offset, length);
			
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(size, start, length);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				if (u.getUnderlying() instanceof float[])
				{
					Slice<float[]> s = (Slice<float[]>) u;
					
					System.arraycopy(s.getUnderlying(), s.getOffset()+start, array, offset, length);
					
					return;
				}
			}
			
			
			defaultGetAllFloats(this, start, array, offset, length);
		}
		
		
		public static void defaultGetAllFloats(FloatList list, int start, @WritableValue float[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				array[offset + i] = list.getFloat(start + i);
		}
		
		
		
		
		
		
		@ThrowAwayValue
		public default float[] getAllFloats(int start, int end)
		{
			rangeCheckInterval(this.size(), start, end);
			
			float[] buff = new float[end-start];
			getAllFloats(start, buff, 0, buff.length);
			return buff;
		}
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public default boolean addFloat(float value)
		{
			this.insertFloat(this.size(), value);
			return true;
		}
		
		@Override
		public default boolean addAll(Collection<? extends Float> c)
		{
			this.addAll(this.size(), c);
			return true;
		}
		
		@Override
		public default boolean addAllFloats(float[] elements, int offset, int length)
		{
			insertAllFloats(this.size(), elements, offset, length);
			return true;
		}
		
		public default void removeByIndex(int index)
		{
			removeRange(index, index+1);
		}
		
		public default float removeFloatByIndex(int index) throws IndexOutOfBoundsException
		{
			float v = this.getFloat(index);
			removeByIndex(index);
			return v;
		}
		
		@Override
		public default boolean removeFloat(float value)
		{
			int i = this.indexOfFloat(value);
			
			if (i != -1)
			{
				this.removeByIndex(i);
				return true;
			}
			else
			{
				return false;
			}
		}
		
		@Override
		public default boolean retainAll(Collection<?> c)
		{
			if (c instanceof FloatCollection)
			{
				FloatCollection cc = (FloatCollection) c;
				
				boolean changedAtAll = false;
				
				for (int i = size() - 1; i >= 0; i--)
				{
					if (!cc.containsFloat(getFloat(i)))
					{
						removeFloatByIndex(i);
						changedAtAll = true;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (int i = size() - 1; i >= 0; i--)
				{
					if (!c.contains(getFloat(i)))
					{
						removeFloatByIndex(i);
						changedAtAll = true;
					}
				}
				
				return changedAtAll;
			}
		}
		
		
		
		
		
		
		
		
		@Override
		public default SimpleFloatIterator newSimpleFloatIterator()
		{
			return new SimpleFloatIterator()
			{
				int index = 0;
				
				public float nextrpFloat() throws StopIterationReturnPath
				{
					int i = index;
					
					if (i >= size())
					{
						throw StopIterationReturnPath.I;
					}
					else
					{
						index = i + 1;
						return getFloat(i);
					}
				}
			};
		}
		
		
		
		
		
		@Override
		public default boolean containsFloat(float value)
		{
			return indexOfFloat(value) != -1;
		}
		
		
		public default void insertAllFloats(int index, float[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				insertFloat(index+i, array[offset+i]);
		}
		
		
		@Override
		public default boolean addAll(int index, Collection<? extends Float> c)
		{
			if (c instanceof FloatCollection)
			{
				FloatCollection cc = (FloatCollection)c;
				
				SimpleFloatIterator i = cc.newSimpleFloatIterator();
				while (true)
				{
					try
					{
						insertFloat(index, i.nextrpFloat());
						index++;  //doesn't matter if this gets executed on the last iteration :3
					}
					catch (StopIterationReturnPath rp)
					{
						break;
					}
				}
			}
			else
			{
				for (Float e : c)
				{
					insertFloat(index, e);
					index++;
				}
			}
			
			return true;
		}
		
		
		
		public default void insertAllFloats(int index, float[] array)
		{
			insertAllFloats(index, array, 0, array.length);
		}
		
		public default void insertAllFloats(int index, Slice<float[]> arraySlice)
		{
			insertAllFloats(index, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		
		
		@Override
		public default Float get(int index)
		{
			return this.getFloat(index);
		}
		
		@Override
		public default Float set(int index, Float value)
		{
			Float previous = get(index);
			this.setFloat(index, value);
			return previous;
		}
		
		
		@Override
		public default void add(int index, Float value)
		{
			this.insertFloat(index, value);
		}
		
		@Override
		public default Float remove(int index) throws IndexOutOfBoundsException
		{
			return this.removeFloatByIndex(index);
		}
		
		
		@Override
		public default int indexOf(Object o)
		{
			return o instanceof Float ? indexOfFloat((Float)o) : -1;
		}
		
		@Override
		public default int lastIndexOf(Object o)
		{
			return o instanceof Float ? lastIndexOfFloat((Float)o) : -1;
		}
		
		
		
		@Override
		public default ListIterator<Float> listIterator()
		{
			return listIterator(0);
		}
		
		@Override
		public default ListIterator<Float> listIterator(int index)
		{
			//Todo
			throw new NotYetImplementedException();
		}
		
		@Override
		public default FloatList subList(int fromIndex, int toIndex)   //Also note that we narrow the return types of this :3
		{
			if (fromIndex == 0 && toIndex == this.size())
				return this;
			
			if (this instanceof Sublist)
			{
				Sublist<Float> s = (Sublist<Float>)this;
				return new FloatSublist((FloatList)s.getUnderlying(), s.getSublistStartingIndex() + fromIndex, toIndex - fromIndex);
			}
			else
			{
				return new FloatSublist(this, fromIndex, toIndex - fromIndex);
			}
		}
		
		
		
		
		
		
		
		
		
		
		// Multiple-Inheritance conflict resolvers X'3
		@Override
		public default boolean removeAll(Collection<?> c)
		{
			return DefaultToArraysFloatCollection.super.removeAll(c);
		}
		
		@Override
		public default boolean remove(Object o)
		{
			return DefaultToArraysFloatCollection.super.remove(o);
		}
		
		@Override
		public default boolean containsAll(Collection<?> c)
		{
			return DefaultToArraysFloatCollection.super.containsAll(c);
		}
		
		@Override
		public default boolean add(Float e)
		{
			return DefaultToArraysFloatCollection.super.add(e);
		}
		
		@Override
		public default boolean isEmpty()
		{
			return DefaultToArraysFloatCollection.super.isEmpty();
		}
		
		@Override
		public default boolean contains(Object o)
		{
			return DefaultToArraysFloatCollection.super.contains(o);
		}
		
		
		
		
		
		@Override
		public default Object[] toArray()
		{
			return DefaultToArraysFloatCollection.super.toArray();
		}
		
		@Override
		public default <T> T[] toArray(T[] a)
		{
			return DefaultToArraysFloatCollection.super.toArray(a);
		}
		
		
		
		
		
		
		
		@Override
		public default void storeBoxingIntoArray(Slice<Object[]> array)
		{
			int n = this.size();
			for (int i = 0; i < n; i++)
				ArrayUtilities.setReference((Slice)array, i, this.get(i));
		}
		
		
		
		
		
		
		//Narrow the return types of these :3
		
		@Override
		public default FloatList subListToEnd(int start)
		{
			return (FloatList)NiceList.super.subListToEnd(start);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
		
		@Override
		public default FloatList subListFromBeginning(int lengthWhichIsEndExclusiveInThisCaseXD)
		{
			return (FloatList)NiceList.super.subListFromBeginning(lengthWhichIsEndExclusiveInThisCaseXD);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
		
		@Override
		public default FloatList subListByLength(int start, int length)
		{
			return (FloatList)NiceList.super.subListByLength(start, length);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
	}
	
	
	
	
	
	
	public static interface DefaultShiftingBasedFloatList
	extends FloatList
	{
		public default void clear()
		{
			removeRange(0, size());
		}
		
		@Override
		public default void insertFloat(int index, float value)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, 1);
			this.setFloat(index, value);
		}
		
		@Override
		public default void insertAllFloats(int index, float[] array, int offset, int length)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, length);
			
			this.setAllFloats(index, array, offset, length);
		}
		
		
		@Override
		public default boolean addAll(int index, Collection<? extends Float> c)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			int amount = c.size();  //copy this incase c == this! XD
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, amount);
			
			if (c instanceof List)
			{
				listcopy((List)c, 0, this, index, amount);
			}
			else
			{
				if (c instanceof FloatCollection)
				{
					SimpleFloatIterator iterator = ((FloatCollection)c).newSimpleFloatIterator();
					
					int i = 0;
					while (true)
					{
						float e;
						try
						{
							e = iterator.nextrpFloat();
						}
						catch (StopIterationReturnPath rp)
						{
							break;
						}
						
						this.setFloat(index+i, e);
						i++;
					}
				}
				else
				{
					int i = 0;
					for (Float e : c)
					{
						this.set(index+i, e);
						i++;
					}
				}
			}
			
			return true;
		}
		
		@Override
		public default void setSizeFloat(int newSize, float elementToAddIfGrowing)
		{
			int oldSize = this.size();
			int amount = newSize - oldSize;
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, oldSize, amount);
			
			//Growing results in *undefined* contents, not necessarily initialized ones!  (eg, it may already have some/enough elements allocated eg, in an underlying array and it just increases the int size field :3 )
			for (int i = oldSize; i < newSize; i++)
				this.setFloat(i, elementToAddIfGrowing);
		}
		
		
		
		
		@Override
		public default void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			rangeCheckInterval(this.size(), start, pastEnd);
			
			int length = pastEnd - start;
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, pastEnd, -length);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class FloatSublist
	implements FloatList, DefaultShiftingBasedFloatList, Sublist<Float>, ShiftableList
	{
		protected final FloatList underlying;
		protected final int start;
		protected int size;
		
		public FloatSublist(FloatList underlying, int start, int initialSize)
		{
			rangeCheckIntervalByLength(underlying.size(), start, initialSize);
			
			this.underlying = underlying;
			this.start = start;
			this.size = initialSize;
		}
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public float getFloat(int index)
		{
			return underlying.getFloat(index + start);
		}
		
		@Override
		public void setFloat(int index, float value)
		{
			underlying.setFloat(index + start, value);
		}
		
		@Override
		public void shiftRegionStretchingFromIndexToEndByAmountChangingSize(int start, int amount)
		{
			rangeCheckCursorPoint(size(), start);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(underlying, start, amount);
			
			size += amount;
		}
		
		
		
		@Override
		public FloatList clone()
		{
			FloatList c = new FloatArrayList();
			c.addAll(this);
			return c;
		}
		
		
		
		@Override
		public FloatList getUnderlying()
		{
			return underlying;
		}
		
		@Override
		public int getSublistStartingIndex()
		{
			return start;
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class FloatArrayList
	implements DefaultShiftingBasedFloatList, ListWithSetSize<Float>, Trimmable, TransparentContiguousArrayBackedCollection<float[]>, KnowsLengthFixedness
	{
		protected int size = 0;
		protected float[] data;
		protected GrowerComputationallyUnreducedPurelyRecursive grower;
		
		
		
		
		//In case subclasses don't want to deal with the other things ^^'
		protected FloatArrayList(Void thisiswhyyoushouldntusejavaconstructorsAlthoughIdontknowhowtomakeinheritanceworkwellwithoutinitmethodsorfactoriesorsomething)
		{
		}
		
		
		/**
		 * Capacity increment is different from Vector's.  It is the percentage of the capacity by which the capacity is increased.
		 * The formula being: <code>newCapacity = capacity + (capacity * capacityIncrement)</code><br>
		 * @param grower This can be anything.  The only downside is performance: all methods will add at least the minimum amount required regardless of what this specifies.
		 */
		public FloatArrayList(int initialCapacity, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this.data = initialCapacity == 0 ? ArrayUtilities.EmptyFloatArray : new float[initialCapacity];
			this.grower = grower;
		}
		
		public FloatArrayList(@LiveValue @WritableValue float[] LIVEBACKING, int size, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this.data = LIVEBACKING;
			this.grower = grower;
			this.size = size;
		}
		
		public FloatArrayList(@SnapshotValue @ReadonlyValue Collection<Float> c, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this(c.size(), grower);
			this.addAll(c);
		}
		
		
		
		
		
		public FloatArrayList()
		{
			this(DefaultPrimitiveArrayListInitialCapacity);
		}
		
		public FloatArrayList(int initialCapacity)
		{
			this(initialCapacity, defaultPrimitiveArrayListGrower(initialCapacity));
		}
		
		public FloatArrayList(@SnapshotValue @ReadonlyValue Collection<Float> c)
		{
			this(c, defaultPrimitiveArrayListGrower(c.size()));
		}
		
		public FloatArrayList(@LiveValue @WritableValue float[] LIVEBACKING, int size)
		{
			this(LIVEBACKING, size, defaultPrimitiveArrayListGrower(LIVEBACKING.length));
		}
		
		public FloatArrayList(@LiveValue @WritableValue float[] LIVEBACKING)
		{
			this(LIVEBACKING, LIVEBACKING.length);
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return false;
		}
		
		
		
		@Override
		public void setSize(int newSize)
		{
			ensureCapacity(newSize);
			this.size = newSize;
			
			// :D
		}
		
		
		
		
		
		//This pattern is nicer for subclassing in any Java class! :D
		protected void setupClone(FloatArrayList freshClone)
		{
			freshClone.size = this.size;
			
			freshClone.data = new float[size];
			System.arraycopy(this.data, 0, freshClone.data, 0, size);
			
			freshClone.grower = this.grower;
		}
		
		@Override
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public FloatArrayList clone()
		{
			FloatArrayList clone = new FloatArrayList();
			this.setupClone(clone);
			return clone;
		}
		
		
		
		
		
		
		
		
		
		
		//This is what unlocks all the System.arraycopy() magic!  8>
		@ImplementationTransparency
		@LiveValue
		public Slice<float[]> getLiveContiguousArrayBackingUNSAFE()
		{
			return new Slice(data, 0, size);
		}
		
		
		
		
		
		
		
		
		
		public void setFloat(int index, float value)
		{
			rangeCheckMember(this.size, index);
			data[index] = value;
		}
		
		public float getFloat(int index)
		{
			rangeCheckMember(this.size(), index);
			return data[index];
		}
		
		
		
		
		
		
		
		@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public void clear()
		{
			size = 0;
		}
		
		@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public int size()
		{
			return size;
		}
		
		@ImplementationTransparency
		public void trimToSize()
		{
			float[] newdata = new float[size];
			System.arraycopy(data, 0, newdata, 0, size);
			this.data = newdata;
		}
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.trimToSize();
			return TrimmableTrimRV.DontKeepInvoking;
		}
		
		
		
		
		
		@ImplementationTransparency
		public int getCapacity()
		{
			return data.length;
		}
		
		@ImplementationTransparency
		public void ensureCapacity(int minCapacity)
		{
			if (data.length < minCapacity)
			{
				//Enlarge
				int newCapacity = getNewCapacity(minCapacity);
				float[] newData = new float[newCapacity];
				System.arraycopy(data, 0, newData, 0, data.length);
				this.data = newData;
			}
		}
		
		/**
		 * This is also guaranteed not to return a capacity smaller than the provided minimum.
		 */
		@ImplementationTransparency
		public int getNewCapacity(int minCapacity)
		{
			if (minCapacity < 0)
				throw new IllegalArgumentException();
			
			return Math.max(minCapacity, grower.getNewSizeRecursive(data.length));  //The Math.max(min, ...) ensures that the grower can't break anything!  :3
		}
		
		
		
		
		
		
		
		/**
		 * sets the underlying array that backs the array list.
		 * Be careful, though, as updates can cause a new array to be created, obsoleting this one.
		 * Note, also, that only up to {@link #size()} elements will be used.
		 */
		@ImplementationTransparency
		public void setDirectBuffer(@LiveValue float[] array)
		{
			if (array.length < size)
				throw new IllegalArgumentException("Array is too small; size="+size+", capacity="+array.length);
			
			this.data = array;
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static class FixedLengthArrayWrapperFloatList
	implements FloatList, TransparentContiguousArrayBackedCollection<float[]>, KnowsLengthFixedness
	{
		protected final float[] underlying;
		protected final int offset, length;
		
		public FixedLengthArrayWrapperFloatList(float[] underlying, int offset, int length)
		{
			rangeCheckIntervalByLength(underlying.length, offset, length);
			
			this.underlying = underlying;
			this.offset = offset;
			this.length = length;
		}
		
		public FixedLengthArrayWrapperFloatList(float[] underlying)
		{
			this(underlying, 0, underlying.length);
		}
		
		public FixedLengthArrayWrapperFloatList(Slice<float[]> underlying)
		{
			this(underlying.getUnderlying(), underlying.getOffset(), underlying.getLength());
		}
		
		@Override
		public FloatList clone()
		{
			return new FixedLengthArrayWrapperFloatList(toFloatArray());
		}
		
		
		
		@Override
		public float[] toFloatArray()
		{
			return sliceToNewFloatArrayOP(this.getLiveContiguousArrayBackingUNSAFE());
		}
		
		@Override
		public Slice<float[]> getLiveContiguousArrayBackingUNSAFE()
		{
			return new Slice<>(underlying, offset, length);
		}
		
		@Override
		public int size()
		{
			return length;
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return true;
		}
		
		
		
		@Override
		public SimpleFloatIterator newSimpleFloatIterator()
		{
			return new SimpleFloatIterator()
			{
				int index = 0;
				
				public float nextrpFloat() throws StopIterationReturnPath
				{
					int i = index;
					
					if (i >= size())
					{
						throw StopIterationReturnPath.I;
					}
					else
					{
						index = i + 1;
						return underlying[offset+i];
					}
				}
			};
		}
		
		@Override
		public FloatList subList(int fromIndex, int toIndex)
		{
			rangeCheckInterval(this.size(), fromIndex, toIndex);
			return new FixedLengthArrayWrapperFloatList(this.underlying, fromIndex+offset, toIndex-fromIndex);
		}
		
		@Override
		public float getFloat(int index)
		{
			return underlying[offset + index];
		}
		
		@Override
		public void setFloat(int index, float value)
		{
			underlying[offset + index] = value;
		}
		
		
		
		
		
		
		
		
		
		//Unallowed Dim-altering Methods!
		
		@Override
		public void clear()
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setSizeFloat(int newSize, float elementToAddIfGrowing)
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void insertFloat(int index, float value)
		{
			throw new UnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	@ReadonlyValue
	public static FloatList floatArrayAsList(@LiveValue @WritableValue float[] array, int offset, int length)
	{
		return new FixedLengthArrayWrapperFloatList(array, offset, length);
	}
	
	@ReadonlyValue
	public static FloatList floatArrayAsList(@LiveValue @WritableValue float[] array)
	{
		return floatArrayAsList(array, 0, array.length);
	}
	
	@ReadonlyValue
	public static FloatList floatArrayAsList(@LiveValue @WritableValue Slice<float[]> arraySlice)
	{
		return floatArrayAsList(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	
	
	
	@ThrowAwayValue
	public static FloatList floatArrayAsMutableList(@SnapshotValue @ReadonlyValue float[] array, int offset, int length)
	{
		return new FloatArrayList(new FixedLengthArrayWrapperFloatList(array, offset, length));
	}
	
	@ThrowAwayValue
	public static FloatList floatArrayAsMutableList(@SnapshotValue @ReadonlyValue float[] array)
	{
		return floatArrayAsMutableList(array, 0, array.length);
	}
	
	@ThrowAwayValue
	public static FloatList floatArrayAsMutableList(@SnapshotValue @ReadonlyValue Slice<float[]> arraySlice)
	{
		return floatArrayAsMutableList(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	@Immutable
	public static class ImmutableFloatArrayList
	implements Serializable, Comparable<ImmutableFloatArrayList>, FloatList, KnowsLengthFixedness     //Let's not implement the general interfaces for accessing the underlying data jussssst to help make sure anyone who accesses it *knows* it's meant to be VERY MUCH READONLY!  XD'''
	{
		private static final long serialVersionUID = 1L;
		
		
		protected final float[] data;
		
		//Yeah I know it's bad practice for a thread-safe immutable object to cache things, but at best effort is wasted, no constraints are violated!     (If I'm wrong, fire me X'D )
		protected transient long hashCode;     //we cache it in case the floatarray is large :33
		
		protected ImmutableFloatArrayList(float[] LIVEDATA)
		{
			if (LIVEDATA == null) throw new NullPointerException();
			this.data = LIVEDATA;
		}
		
		
		
		
		
		/**
		 * DO NOT *EVER* MODIFY THE ARRAY YOU PASSED HERE AFTER CALLING THIS X"D
		 * ( THE HASH CODE IS CACHED FOR ONE, AND THIS MAY BE EMBEDDED IN HASHMAPS--THIS IS SUPPOSED TO BE IMMUTABLE AFTER ALL X'DDD )
		 * @see #getREADONLYLiveWholeArrayBackingUNSAFE()
		 */
		@ImplementationTransparency
		public static ImmutableFloatArrayList newLIVE(@LiveValue float[] LIVEDATA)
		{
			return new ImmutableFloatArrayList(LIVEDATA);
		}
		
		
		@ImplementationTransparency
		public static ImmutableFloatArrayList newSingleton(float singleMember)
		{
			return new ImmutableFloatArrayList(new float[]{singleMember});
		}
		
		
		
		
		public static ImmutableFloatArrayList newCopying(@SnapshotValue List<Float> data)
		{
			if (data instanceof ImmutableFloatArrayList)
				return (ImmutableFloatArrayList)data;
			
			if (TransparentContiguousArrayBackedCollection.is(data))
			{
				Slice<Object> s = ((TransparentContiguousArrayBackedCollection)data).getLiveContiguousArrayBackingUNSAFE();
				if (s.getUnderlying() instanceof float[])
				{
					return newCopying((Slice)s);
				}
			}
			
			if (data instanceof FloatList)
			{
				return newLIVE(((FloatList)data).toFloatArray());
			}
			else
			{
				int n = data.size();
				float[] a = new float[n];
				
				int i = 0;
				for (Float e : data)
				{
					if (i >= n)
						throw new ImpossibleException();
					
					a[i] = e;
					i++;
				}
				
				if (i != n)
					throw new ImpossibleException();
				
				return newLIVE(a);
			}
		}
		
		public static ImmutableFloatArrayList newCopying(@SnapshotValue float[] data)
		{
			return new ImmutableFloatArrayList(data.clone());
		}
		
		public static ImmutableFloatArrayList newCopying(@SnapshotValue float[] data, int offset, int length)
		{
			if (offset == 0 && length == data.length)
				return newCopying(data);  //who knows, float[].clone() might be faster! \o/   (it certainly shouldn't be slower!!)
			else
			{
				float[] newArray = new float[length];
				System.arraycopy(data, offset, newArray, 0, length);
				return new ImmutableFloatArrayList(newArray);
			}
		}
		
		public static ImmutableFloatArrayList newCopying(@SnapshotValue Slice<float[]> data)
		{
			return newCopying(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		
		
		
		
		public static ImmutableFloatArrayList newLIVEOrCopying(@PossiblySnapshotPossiblyLiveValue float[] data, int offset, int length)
		{
			if (offset == 0 && length == data.length)
				return newLIVE(data);
			else
				return newCopying(data, offset, length);
		}
		
		public static ImmutableFloatArrayList newLIVEOrCopying(@PossiblySnapshotPossiblyLiveValue Slice<float[]> data)
		{
			return newLIVEOrCopying(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		
		
		
		
		
		
		public static final ImmutableFloatArrayList Empty = ImmutableFloatArrayList.newLIVE(ArrayUtilities.EmptyFloatArray);
		
		
		
		
		
		
		
		
		@ThrowAwayValue
		@Override
		public float[] toFloatArray()
		{
			return data.clone();
		}
		
		/**
		 * DO NOT MODIFY THIS X"D
		 * ( THE HASH CODE IS CACHED FOR ONE, AND THIS MAY BE EMBEDDED IN HASHMAPS--THIS IS SUPPOSED TO BE IMMUTABLE AFTER ALL X'DDD )
		 */
		@ImplementationTransparency
		@LiveValue
		@ReadonlyValue //!!!!
		public float[] getREADONLYLiveWholeArrayBackingUNSAFE()
		{
			return data;
		}
		
		@Override
		public int size()
		{
			return data.length;
		}
		
		@Override
		public float getFloat(int index)
		{
			return data[index];
		}
		
		@Override
		public void getAllFloats(int offsetInThisSource, @WritableValue float[] dest, int offsetInDest, int lengthInBoth)
		{
			System.arraycopy(this.data, offsetInThisSource, dest, offsetInDest, lengthInBoth);
		}
		
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return true;
		}
		
		
		
		
		@Override
		public int hashCode()
		{
			long hashCodeField = this.hashCode;
			
			boolean cached = hashCodeField != 0;
			
			
			if (cached)
			{
				return (int)this.hashCode;
			}
			else
			{
				int hashCode = Arrays.hashCode(this.data);
				this.hashCode = hashCode | 0xFFFF_FFFF_0000_0000l;  //hashCodes can be zero!  Not often XDD   But can be!  So it's not correct unless we account for that :>      (And if it was two fields then it'd be a concurrency bug!)
				return hashCode;
			}
		}
		
		
		
		
		@Override
		public boolean equals(@ReadonlyValue Object obj)
		{
			if (obj == this)
			{
				return true;
			}
			else if (obj instanceof ImmutableFloatArrayList)
			{
				ImmutableFloatArrayList other = (ImmutableFloatArrayList)obj;
				
				//Checking the hashcodes is a VERY fast negative test and is almost always true-negative!! :DDD   (rarely it's false-positive, but never false-negative!)
				
				long thisHC = this.hashCode;
				long otherHC = other.hashCode;
				
				boolean thisHashCodeCached = thisHC != 0;
				boolean otherHashCodeCached = otherHC != 0;
				
				int thisHashCode = (int)thisHC;
				int otherHashCode = (int)otherHC;
				
				if (thisHashCodeCached)
				{
					if (otherHashCodeCached)
					{
						if (thisHashCode != otherHashCode)
							return false;
					}
					else
					{
						//Scanning one float array is faster than scanning two! :D
						if (thisHashCode != other.hashCode())
							return false;
					}
				}
				else
				{
					if (otherHashCodeCached)
					{
						//Scanning one float array is faster than scanning two! :D
						if (this.hashCode() != otherHashCode)
							return false;
					}
					else
					{
						//Then don't make any hash codes X3
					}
				}
				
				return this.equalsFloatArray(other.data);
			}
			else
			{
				return false;
			}
		}
		
		
		@Override
		public int compareTo(@ReadonlyValue ImmutableFloatArrayList other)
		{
			return this.compareToFloatArray(other.data);
		}
		
		
		public boolean equalsFloatArray(@ReadonlyValue float[] o)
		{
			float[] ourData = this.getREADONLYLiveWholeArrayBackingUNSAFE();
			float[] theirData = o;
			
			return Arrays.equals(ourData, theirData);
		}
		
		
		public int compareToFloatArray(@ReadonlyValue float[] o)
		{
			float[] ourData = this.getREADONLYLiveWholeArrayBackingUNSAFE();
			float[] theirData = o;
			
			return ArrayUtilities.compareLittleEndianLengthsFirst(ourData, theirData);  //lengths-first is more efficient if they're different but hardly any less efficient if they're always the same!  A good default, don't you think? :D
		}
		
		
		
		
		@Override
		public ImmutableFloatArrayList clone()
		{
			return this;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		// Unallowed Write Methods!
		
		@Override
		public void clear()
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setSizeFloat(int newSize, float elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void insertFloat(int index, float value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setFloat(int index, float value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static FloatList unmodifiableFloatList(FloatList floatList)
	{
		return floatList instanceof UnmodifiableFloatListWrapper ? floatList : new UnmodifiableFloatListWrapper(floatList);
	}
	
	public static class UnmodifiableFloatListWrapper
	implements FloatList, KnowsLengthFixedness, TransparentContiguousArrayBackedCollection
	{
		protected final FloatList underlying;
		
		public UnmodifiableFloatListWrapper(FloatList underlying)
		{
			this.underlying = underlying;
		}
		
		@ImplementationTransparency
		public FloatList getUnderlying()
		{
			return underlying;
		}
		
		
		
		
		
		@Override
		public UnmodifiableFloatListWrapper clone()
		{
			return new UnmodifiableFloatListWrapper(underlying.clone());
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.underlying);
		}
		
		@Override
		public boolean isTransparentContiguousArrayBackedCollection()
		{
			return TransparentContiguousArrayBackedCollection.is(underlying);
		}
		
		@Override
		public Slice getLiveContiguousArrayBackingUNSAFE()
		{
			return ((TransparentContiguousArrayBackedCollection)underlying).getLiveContiguousArrayBackingUNSAFE();
		}
		
		
		
		
		
		
		//Use default so they can't get around our readonly-ness!  ;D
		//	public Iterator<Float> iterator()
		//	public ListIterator<Float> listIterator()
		//	public ListIterator<Float> listIterator(int index)
		//	public SimpleIterator<Float> simpleIterator()
		//	public SimpleFloatIterator newSimpleFloatIterator()
		//	public FloatList subList(int fromIndex, int toIndex)
		
		
		
		
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeRangeByLength(int start, int length) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void replaceAll(UnaryOperator<Float> operator)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeIf(Predicate<? super Float> filter)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void sort(Comparator<? super Float> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void clear()
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public Float remove(int index)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllFloats(float[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllFloats(Slice<float[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllFloats(float[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllFloats(Slice<float[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllFloats(float[] a, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setFloat(int index, float value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertFloat(int index, float value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSizeFloat(int newSize, float elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSize(int newSize, Float elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSize(int newSize)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllFloats(int index, float[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllFloats(int index, Slice<float[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllFloats(int index, float[] array, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAll(int destIndex, List sourceU, int sourceIndex, int amount) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addFloat(float value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll(Collection<? extends Float> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllFloats(float[] elements, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeByIndex(int index)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public float removeFloatByIndex(int index) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeFloat(float value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean retainAll(Collection<?> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllFloats(int index, float[] array, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll(int index, Collection<? extends Float> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllFloats(int index, float[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllFloats(int index, Slice<float[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public Float set(int index, Float value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void add(int index, Float value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeAll(Collection<?> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean remove(Object o)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean add(Float e)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		
		
		
		
		
		
		
		
		
		public void forEach(Consumer<? super Float> action)
		{
			underlying.forEach(action);
		}
		
		public int size()
		{
			return underlying.size();
		}
		
		public boolean equals(Object o)
		{
			return underlying.equals(o);
		}
		
		public int hashCode()
		{
			return underlying.hashCode();
		}
		
		public Spliterator<Float> spliterator()
		{
			return underlying.spliterator();
		}
		
		public Stream<Float> stream()
		{
			return underlying.stream();
		}
		
		public Stream<Float> parallelStream()
		{
			return underlying.parallelStream();
		}
		
		public float getFloat(int index)
		{
			return underlying.getFloat(index);
		}
		
		public int indexOfFloat(float value)
		{
			return underlying.indexOfFloat(value);
		}
		
		public int lastIndexOfFloat(float value)
		{
			return underlying.lastIndexOfFloat(value);
		}
		
		public boolean equivalent(List<Float> other)
		{
			return underlying.equivalent(other);
		}
		
		public float[] toFloatArray()
		{
			return underlying.toFloatArray();
		}
		
		public boolean containsFloat(float value)
		{
			return underlying.containsFloat(value);
		}
		
		public Float get(int index)
		{
			return underlying.get(index);
		}
		
		public int indexOf(Object o)
		{
			return underlying.indexOf(o);
		}
		
		public int lastIndexOf(Object o)
		{
			return underlying.lastIndexOf(o);
		}
		
		public boolean containsAll(Collection<?> c)
		{
			return underlying.containsAll(c);
		}
		
		public boolean isEmpty()
		{
			return underlying.isEmpty();
		}
		
		public boolean contains(Object o)
		{
			return underlying.contains(o);
		}
		
		public Object[] toArray()
		{
			return underlying.toArray();
		}
		
		public <T> T[] toArray(T[] a)
		{
			return underlying.toArray(a);
		}
		
		public void storeBoxingIntoArray(Slice<Object[]> array)
		{
			underlying.storeBoxingIntoArray(array);
		}
		
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class FloatTable
	{
		protected int width;
		protected float[] data;
		
		public FloatTable(int width, int height)
		{
			if (width < 0)
				throw new IllegalArgumentException();
			if (height < 0)
				throw new IllegalArgumentException();
			
			data = new float[width * height];
		}
		
		
		
		@Nonnegative
		public int getNumberOfColumns()
		{
			return width;
		}
		
		@Nonnegative
		public int getNumberOfRows()
		{
			return data.length / width;
		}
		
		
		@Nonnegative
		public int getNumberOfCells()
		{
			return data.length;
		}
		
		
		
		
		
		public float getCellContentsFloat(int columnIndex, int rowIndex) throws IndexOutOfBoundsException
		{
			if (columnIndex < 0) throw new IndexOutOfBoundsException();
			if (rowIndex < 0) throw new IndexOutOfBoundsException();
			if (columnIndex >= getNumberOfColumns()) throw new IndexOutOfBoundsException();
			if (rowIndex >= getNumberOfRows()) throw new IndexOutOfBoundsException();
			
			return this.data[rowIndex * this.getNumberOfColumns() + columnIndex];
		}
		
		public void setCellContentsFloat(int columnIndex, int rowIndex, @Nullable float newValue) throws IndexOutOfBoundsException
		{
			if (columnIndex < 0) throw new IndexOutOfBoundsException();
			if (rowIndex < 0) throw new IndexOutOfBoundsException();
			if (columnIndex >= getNumberOfColumns()) throw new IndexOutOfBoundsException();
			if (rowIndex >= getNumberOfRows()) throw new IndexOutOfBoundsException();
			
			this.data[rowIndex * this.getNumberOfColumns() + columnIndex] = newValue;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Nullable
		public Float getCellContents(int columnIndex, int rowIndex) throws IndexOutOfBoundsException
		{
			return getCellContentsFloat(columnIndex, rowIndex);
		}
		
		public void setCellContents(int columnIndex, int rowIndex, @Nullable Float newValue) throws IndexOutOfBoundsException
		{
			setCellContentsFloat(columnIndex, rowIndex, newValue);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int getInt(List<Integer> list, int index)
	{
		if (list instanceof IntegerList)
			return ((IntegerList) list).getInt(index);
		else
			return list.get(index);
	}
	
	public static void setInt(List<Integer> list, int index, int value)
	{
		if (list instanceof IntegerList)
			((IntegerList) list).setInt(index, value);
		else
			list.set(index, value);
	}
	
	
	
	
	
	
	
	public static interface SimpleIntegerIterable
	extends SimpleIterable<Integer>
	{
		public SimpleIntegerIterator newSimpleIntegerIterator();
		
		public default SimpleIterator<Integer> simpleIterator()
		{
			SimpleIntegerIterator i = this.newSimpleIntegerIterator();
			return () -> i.nextrpInt();
		}
	}
	
	
	public static interface SimpleIntegerIterator
	extends SimpleIterator<Integer>
	{
		public int nextrpInt() throws StopIterationReturnPath;
		
		
		@Override
		public default Integer nextrp() throws StopIterationReturnPath
		{
			return nextrpInt();
		}
	}
	
	
	
	@SignalInterface
	public static interface IntegerCollection
	extends PrimitiveCollection<Integer>, SimpleIntegerIterable
	{
		public boolean addInt(int value);
		
		public boolean removeInt(int value);
		
		
		
		
		
		@ThrowAwayValue
		public static int[] defaultToIntArray(IntegerCollection collection)
		{
			int[] array = new int[collection.size()];
			SimpleIntegerIterator i = collection.newSimpleIntegerIterator();
			
			int index = 0;
			while (true)
			{
				try
				{
					array[index] = i.nextrpInt();
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
				
				index++;
			}
			
			return array;
		}
		
		@ThrowAwayValue
		public default int[] toIntArray()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(this.size(), u);
				
				if (u.getUnderlying() instanceof int[])
				{
					return sliceToNewIntArrayOP((Slice<int[]>) u);
				}
			}
			
			return defaultToIntArray(this);
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public default int[] toIntArrayPossiblyLive()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				int size = this.size();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				Object und = u.getUnderlying();
				
				if (und instanceof int[] && u.getOffset() == 0 && ((int[])und).length == size)
				{
					return (int[])und;
				}
			}
			
			return defaultToIntArray(this);
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public default Slice<int[]> toIntArraySlicePossiblyLive()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(this.size(), u);
				
				if (u.getUnderlying() instanceof int[])
				{
					return (Slice<int[]>) u;
				}
			}
			
			return wholeArraySliceInt(defaultToIntArray(this));
		}
		
		
		
		
		
		
		public default boolean containsInt(int value)
		{
			SimpleIntegerIterator i = newSimpleIntegerIterator();
			
			while (true)
			{
				try
				{
					if (eqSane(i.nextrpInt(), value))
						return true;
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
			}
			
			return false;
		}
		
		
		
		
		
		
		@Override
		public default Iterator<Integer> iterator()
		{
			return SimpleIntegerIterable.super.iterator();
		}
		
		@Override
		public default boolean isEmpty()
		{
			return size() == 0;
		}
		
		
		
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllInts(int[] array)
		{
			return addAllInts(array, 0, array.length);
		}
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllInts(Slice<int[]> arraySlice)
		{
			return addAllInts(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllInts(int[] elements, int offset, int length)
		{
			return defaultAddAllInts(this, elements, offset, length);
		}
		
		public static boolean defaultAddAllInts(IntegerCollection self, int[] elements, int offset, int length)
		{
			boolean changedAtAll = false;
			int e = offset + length;
			for (int i = offset; i < e; i++)
				changedAtAll |= self.addInt(elements[i]);
			return changedAtAll;
		}
		
		
		
		
		public default void removeAllInts(int[] array)
		{
			removeAllInts(array, 0, array.length);
		}
		
		public default void removeAllInts(Slice<int[]> arraySlice)
		{
			removeAllInts(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public default void removeAllInts(int[] a, int offset, int length)
		{
			int e = offset + length;
			for (int i = offset; i < e; i++)
				this.removeInt(a[i]);
		}
		
		
		
		
		
		
		
		
		
		
		@Override
		public default boolean contains(Object o)
		{
			return o instanceof Integer && containsInt((Integer)o);
		}
		
		
		@Override
		public default boolean add(Integer e)
		{
			return addInt(e);
		}
		
		
		@Override
		public default boolean remove(Object o)
		{
			return o instanceof Integer && removeInt((Integer)o);
		}
		
		
		@Override
		public default boolean containsAll(Collection<?> c)
		{
			if (c instanceof IntegerCollection)
			{
				IntegerCollection cc = (IntegerCollection) c;
				
				SimpleIntegerIterator i = cc.newSimpleIntegerIterator();
				while (true)
				{
					try
					{
						if (this.containsInt(i.nextrpInt()))
							return true;
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return false;
			}
			else
			{
				for (Object e : c)
					if (this.contains(e))
						return true;
				return false;
			}
		}
		
		
		@Override
		public default boolean addAll(Collection<? extends Integer> c)
		{
			if (c instanceof IntegerCollection)
			{
				boolean changedAtAll = false;
				
				SimpleIntegerIterator i = ((IntegerCollection)c).newSimpleIntegerIterator();
				
				while (true)
				{
					try
					{
						changedAtAll |= this.add(i.nextrpInt());
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (Integer e : c)
				{
					changedAtAll |= this.add(e);
				}
				
				return changedAtAll;
			}
		}
		
		
		
		@Override
		public default boolean removeAll(Collection<?> c)
		{
			if (c instanceof IntegerCollection)
			{
				boolean changedAtAll = false;
				
				SimpleIntegerIterator i = ((IntegerCollection)c).newSimpleIntegerIterator();
				
				while (true)
				{
					try
					{
						changedAtAll |= this.remove(i.nextrpInt());
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (Object e : c)
				{
					changedAtAll |= this.remove(e);
				}
				
				return changedAtAll;
			}
		}
		
		
		
		public default String _toString()
		{
			StringBuilder b = new StringBuilder("[");
			
			SimpleIntegerIterator i = newSimpleIntegerIterator();
			
			boolean first = true;
			
			while (true)
			{
				int e;
				
				try
				{
					e = i.nextrpInt();
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
				
				
				
				//Notice this comes after the possible 'break'!  ^_~
				if (first)
					first = false;
				else
					b.append(", ");
				
				b.append(e);
			}
			
			b.append(']');
			
			return b.toString();
		}
	}
	
	
	
	public static interface DefaultToArraysIntegerCollection
	extends IntegerCollection
	{
		@Override
		public default Object[] toArray()
		{
			Object[] a = new Object[size()];
			storeBoxingIntoArray(wholeArraySliceReference(a));
			return a;
		}
		
		@Override
		public default <T> T[] toArray(T[] a)
		{
			if (a.length >= this.size())
			{
				storeBoxingIntoArray(wholeArraySliceReference(a));
				return a;
			}
			else
			{
				Object[] aa;
				
				if (a.getClass() == Integer[].class)
				{
					aa = new Integer[size()];
				}
				else if (a.getClass() == Object[].class)
				{
					aa = new Object[size()];
				}
				else
				{
					throw new StructuredClassCastException(a.getClass());
				}
				
				storeBoxingIntoArray(wholeArraySliceReference(aa));
				
				return (T[])aa;
			}
		}
		
		public void storeBoxingIntoArray(Slice<Object[]> array);
	}
	
	
	
	
	
	
	@SignalInterface
	public static interface IntegerListRO
	{
		public int getInt(int index);
		
		public int size();
		
		
		
		public default int indexOfInt(int value)
		{
			int n = this.size();
			for (int i = 0; i < n; i++)
				if (eqSane(this.getInt(i), value))
					return i;
			return -1;
		}
		
		public default int lastIndexOfInt(int value)
		{
			int i = this.size()-1;
			while (i >= 0)
			{
				if (eqSane(this.getInt(i), value))
					return i;
				break;
			}
			return i;   // ;D
		}
		
		
		
		public default boolean equivalent(List<Integer> other)
		{
			int size = this.size();
			if (other.size() != size)
				return false;
			
			
			if (other instanceof IntegerListRO)  //All IntegerLists will implement this interface too so this accounts for them as well :3
			{
				return equivalentFixedRO((IntegerListRO)other);
			}
			else
			{
				for (int i = 0; i < size; i++)
				{
					if (!eqSane(this.getInt(i), other.get(i).intValue()))
						return false;
				}
			}
			
			return true;
		}
		
		
		
		public default boolean equivalentFixedRO(IntegerListRO other)
		{
			int size = this.size();
			if (other.size() != size)
				return false;
			
			for (int i = 0; i < size; i++)
			{
				if (!eqSane(this.getInt(i), other.getInt(i)))
					return false;
			}
			
			return true;
		}
	}
	
	
	
	@SignalInterface
	public static interface IntegerListRWFixed
	extends IntegerListRO
	{
		public void setInt(int index, int value);
	}
	
	
	
	@SignalInterface
	public static interface IntegerList
	extends IntegerCollection, NiceList<Integer>, IntegerListRO, IntegerListRWFixed, ListWithRemoveRange, DefaultToArraysIntegerCollection, PubliclyCloneable, ListWithSetSize<Integer>, ListWithSetAll, NonuniformMethodsForIntegerList
	{
		public void insertInt(int index, int value);
		
		
		
		/**
		 * This should be equivalent to just constructing and returning a new in-memory stock implementation with the same contents  (but can easily be faster, especially for those stock impl.s! XD )
		 * As for whether it's fixed-length or read-only or duplicateless or etc., that should be the same in the clone as it is in the main impl.
		 * If *and only if* it is immutable, this is allowed to return this same instance!  (ie the body of this method being <code>return this;</code>)
		 */
		public IntegerList clone();
		
		
		/**
		 * If size is smaller than the current, then this is equivalent to {@link #removeRange(int, int) removeRange}(newSize, this.{@link #size() size()})  :3
		 * If size is larger, then this appends newSize - this.{@link #size() size()} elementToAddIfGrowing's to the end like that many {@link #addInt(int)}  :D
		 */
		public void setSizeInt(int newSize, int elementToAddIfGrowing);
		
		public default void setSize(int newSize, Integer elementToAddIfGrowing)
		{
			setSizeInt(newSize, elementToAddIfGrowing);
		}
		
		public default void setSize(int newSize)
		{
			setSizeInt(newSize, 0);
		}
		
		
		
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void setAllInts(int index, int[] array)
		{
			setAllInts(index, array, 0, array.length);
		}
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void setAllInts(int index, Slice<int[]> arraySlice)
		{
			setAllInts(index, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		public default void setAllInts(int start, int[] array, int offset, int length)
		{
			int size = this.size();
			
			rangeCheckIntervalByLength(size, start, length);
			rangeCheckIntervalByLength(array.length, offset, length);
			
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(size, start, length);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				if (u.getUnderlying() instanceof int[])
				{
					Slice<int[]> s = (Slice<int[]>) u;
					
					System.arraycopy(array, offset, s.getUnderlying(), s.getOffset()+start, length);
					
					return;
				}
			}
			
			
			defaultSetAllInts(this, start, array, offset, length);
		}
		
		
		public static void defaultSetAllInts(IntegerList list, int start, @WritableValue int[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				list.setInt(start + i, array[offset + i]);
		}
		
		
		
		
		
		@Override
		public default void setAll(int destIndex, List sourceU, int sourceIndex, @Nonnegative int amount) throws IndexOutOfBoundsException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			List<Integer> source = sourceU;
			IntegerList dest = this;
			
			int sourceSize = source.size();
			int destSize = dest.size();
			rangeCheckIntervalByLength(sourceSize, sourceIndex, amount);
			rangeCheckIntervalByLength(destSize, destIndex, amount);
			
			
			
			if (TransparentContiguousArrayBackedCollection.is(source))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection<?>)source).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(sourceSize, sourceIndex, amount);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(sourceSize, u);
				
				if (u.getUnderlying() instanceof int[])
				{
					Slice<int[]> s = (Slice<int[]>)u;
					this.setAllInts(destIndex, s.subslice(sourceIndex, amount));
					return;
				}
			}
			
			
			
			
			if (source instanceof IntegerList)
			{
				IntegerList primSource = (IntegerList) source;
				
				if (destIndex < sourceIndex)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
				{
					for (int i = 0; i < amount; i++)
						dest.setInt(destIndex+i, primSource.getInt(sourceIndex+i));
				}
				else
				{
					for (int i = amount-1; i >= 0; i--)
						dest.setInt(destIndex+i, primSource.getInt(sourceIndex+i));
				}
			}
			else
			{
				if (destIndex < sourceIndex)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
				{
					for (int i = 0; i < amount; i++)
						dest.set(destIndex+i, source.get(sourceIndex+i));
				}
				else
				{
					for (int i = amount-1; i >= 0; i--)
						dest.set(destIndex+i, source.get(sourceIndex+i));
				}
			}
		}
		
		
		
		
		
		
		
		/**
		 * Copies <code>array.length</code> elements into <code>array</code> starting with the <code>start</code>th element.<br>
		 */
		public default void getAllInts(int start, @WritableValue int[] array, int offset, int length)
		{
			int size = this.size();
			
			rangeCheckIntervalByLength(size, start, length);
			rangeCheckIntervalByLength(array.length, offset, length);
			
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(size, start, length);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				if (u.getUnderlying() instanceof int[])
				{
					Slice<int[]> s = (Slice<int[]>) u;
					
					System.arraycopy(s.getUnderlying(), s.getOffset()+start, array, offset, length);
					
					return;
				}
			}
			
			
			defaultGetAllInts(this, start, array, offset, length);
		}
		
		
		public static void defaultGetAllInts(IntegerList list, int start, @WritableValue int[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				array[offset + i] = list.getInt(start + i);
		}
		
		
		
		
		
		
		@ThrowAwayValue
		public default int[] getAllInts(int start, int end)
		{
			rangeCheckInterval(this.size(), start, end);
			
			int[] buff = new int[end-start];
			getAllInts(start, buff, 0, buff.length);
			return buff;
		}
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public default boolean addInt(int value)
		{
			this.insertInt(this.size(), value);
			return true;
		}
		
		@Override
		public default boolean addAll(Collection<? extends Integer> c)
		{
			this.addAll(this.size(), c);
			return true;
		}
		
		@Override
		public default boolean addAllInts(int[] elements, int offset, int length)
		{
			insertAllInts(this.size(), elements, offset, length);
			return true;
		}
		
		public default void removeByIndex(int index)
		{
			removeRange(index, index+1);
		}
		
		public default int removeIntByIndex(int index) throws IndexOutOfBoundsException
		{
			int v = this.getInt(index);
			removeByIndex(index);
			return v;
		}
		
		@Override
		public default boolean removeInt(int value)
		{
			int i = this.indexOfInt(value);
			
			if (i != -1)
			{
				this.removeByIndex(i);
				return true;
			}
			else
			{
				return false;
			}
		}
		
		@Override
		public default boolean retainAll(Collection<?> c)
		{
			if (c instanceof IntegerCollection)
			{
				IntegerCollection cc = (IntegerCollection) c;
				
				boolean changedAtAll = false;
				
				for (int i = size() - 1; i >= 0; i--)
				{
					if (!cc.containsInt(getInt(i)))
					{
						removeIntByIndex(i);
						changedAtAll = true;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (int i = size() - 1; i >= 0; i--)
				{
					if (!c.contains(getInt(i)))
					{
						removeIntByIndex(i);
						changedAtAll = true;
					}
				}
				
				return changedAtAll;
			}
		}
		
		
		
		
		
		
		
		
		@Override
		public default SimpleIntegerIterator newSimpleIntegerIterator()
		{
			return new SimpleIntegerIterator()
			{
				int index = 0;
				
				public int nextrpInt() throws StopIterationReturnPath
				{
					int i = index;
					
					if (i >= size())
					{
						throw StopIterationReturnPath.I;
					}
					else
					{
						index = i + 1;
						return getInt(i);
					}
				}
			};
		}
		
		
		
		
		
		@Override
		public default boolean containsInt(int value)
		{
			return indexOfInt(value) != -1;
		}
		
		
		public default void insertAllInts(int index, int[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				insertInt(index+i, array[offset+i]);
		}
		
		
		@Override
		public default boolean addAll(int index, Collection<? extends Integer> c)
		{
			if (c instanceof IntegerCollection)
			{
				IntegerCollection cc = (IntegerCollection)c;
				
				SimpleIntegerIterator i = cc.newSimpleIntegerIterator();
				while (true)
				{
					try
					{
						insertInt(index, i.nextrpInt());
						index++;  //doesn't matter if this gets executed on the last iteration :3
					}
					catch (StopIterationReturnPath rp)
					{
						break;
					}
				}
			}
			else
			{
				for (Integer e : c)
				{
					insertInt(index, e);
					index++;
				}
			}
			
			return true;
		}
		
		
		
		public default void insertAllInts(int index, int[] array)
		{
			insertAllInts(index, array, 0, array.length);
		}
		
		public default void insertAllInts(int index, Slice<int[]> arraySlice)
		{
			insertAllInts(index, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		
		
		@Override
		public default Integer get(int index)
		{
			return this.getInt(index);
		}
		
		@Override
		public default Integer set(int index, Integer value)
		{
			Integer previous = get(index);
			this.setInt(index, value);
			return previous;
		}
		
		
		@Override
		public default void add(int index, Integer value)
		{
			this.insertInt(index, value);
		}
		
		@Override
		public default Integer remove(int index) throws IndexOutOfBoundsException
		{
			return this.removeIntByIndex(index);
		}
		
		
		@Override
		public default int indexOf(Object o)
		{
			return o instanceof Integer ? indexOfInt((Integer)o) : -1;
		}
		
		@Override
		public default int lastIndexOf(Object o)
		{
			return o instanceof Integer ? lastIndexOfInt((Integer)o) : -1;
		}
		
		
		
		@Override
		public default ListIterator<Integer> listIterator()
		{
			return listIterator(0);
		}
		
		@Override
		public default ListIterator<Integer> listIterator(int index)
		{
			//Todo
			throw new NotYetImplementedException();
		}
		
		@Override
		public default IntegerList subList(int fromIndex, int toIndex)   //Also note that we narrow the return types of this :3
		{
			if (fromIndex == 0 && toIndex == this.size())
				return this;
			
			if (this instanceof Sublist)
			{
				Sublist<Integer> s = (Sublist<Integer>)this;
				return new IntegerSublist((IntegerList)s.getUnderlying(), s.getSublistStartingIndex() + fromIndex, toIndex - fromIndex);
			}
			else
			{
				return new IntegerSublist(this, fromIndex, toIndex - fromIndex);
			}
		}
		
		
		
		
		
		
		
		
		
		
		// Multiple-Inheritance conflict resolvers X'3
		@Override
		public default boolean removeAll(Collection<?> c)
		{
			return DefaultToArraysIntegerCollection.super.removeAll(c);
		}
		
		@Override
		public default boolean remove(Object o)
		{
			return DefaultToArraysIntegerCollection.super.remove(o);
		}
		
		@Override
		public default boolean containsAll(Collection<?> c)
		{
			return DefaultToArraysIntegerCollection.super.containsAll(c);
		}
		
		@Override
		public default boolean add(Integer e)
		{
			return DefaultToArraysIntegerCollection.super.add(e);
		}
		
		@Override
		public default boolean isEmpty()
		{
			return DefaultToArraysIntegerCollection.super.isEmpty();
		}
		
		@Override
		public default boolean contains(Object o)
		{
			return DefaultToArraysIntegerCollection.super.contains(o);
		}
		
		
		
		
		
		@Override
		public default Object[] toArray()
		{
			return DefaultToArraysIntegerCollection.super.toArray();
		}
		
		@Override
		public default <T> T[] toArray(T[] a)
		{
			return DefaultToArraysIntegerCollection.super.toArray(a);
		}
		
		
		
		
		
		
		
		@Override
		public default void storeBoxingIntoArray(Slice<Object[]> array)
		{
			int n = this.size();
			for (int i = 0; i < n; i++)
				ArrayUtilities.setReference((Slice)array, i, this.get(i));
		}
		
		
		
		
		
		
		//Narrow the return types of these :3
		
		@Override
		public default IntegerList subListToEnd(int start)
		{
			return (IntegerList)NiceList.super.subListToEnd(start);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
		
		@Override
		public default IntegerList subListFromBeginning(int lengthWhichIsEndExclusiveInThisCaseXD)
		{
			return (IntegerList)NiceList.super.subListFromBeginning(lengthWhichIsEndExclusiveInThisCaseXD);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
		
		@Override
		public default IntegerList subListByLength(int start, int length)
		{
			return (IntegerList)NiceList.super.subListByLength(start, length);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
	}
	
	
	
	
	
	
	public static interface DefaultShiftingBasedIntegerList
	extends IntegerList
	{
		public default void clear()
		{
			removeRange(0, size());
		}
		
		@Override
		public default void insertInt(int index, int value)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, 1);
			this.setInt(index, value);
		}
		
		@Override
		public default void insertAllInts(int index, int[] array, int offset, int length)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, length);
			
			this.setAllInts(index, array, offset, length);
		}
		
		
		@Override
		public default boolean addAll(int index, Collection<? extends Integer> c)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			int amount = c.size();  //copy this incase c == this! XD
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, amount);
			
			if (c instanceof List)
			{
				listcopy((List)c, 0, this, index, amount);
			}
			else
			{
				if (c instanceof IntegerCollection)
				{
					SimpleIntegerIterator iterator = ((IntegerCollection)c).newSimpleIntegerIterator();
					
					int i = 0;
					while (true)
					{
						int e;
						try
						{
							e = iterator.nextrpInt();
						}
						catch (StopIterationReturnPath rp)
						{
							break;
						}
						
						this.setInt(index+i, e);
						i++;
					}
				}
				else
				{
					int i = 0;
					for (Integer e : c)
					{
						this.set(index+i, e);
						i++;
					}
				}
			}
			
			return true;
		}
		
		@Override
		public default void setSizeInt(int newSize, int elementToAddIfGrowing)
		{
			int oldSize = this.size();
			int amount = newSize - oldSize;
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, oldSize, amount);
			
			//Growing results in *undefined* contents, not necessarily initialized ones!  (eg, it may already have some/enough elements allocated eg, in an underlying array and it just increases the int size field :3 )
			for (int i = oldSize; i < newSize; i++)
				this.setInt(i, elementToAddIfGrowing);
		}
		
		
		
		
		@Override
		public default void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			rangeCheckInterval(this.size(), start, pastEnd);
			
			int length = pastEnd - start;
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, pastEnd, -length);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class IntegerSublist
	implements IntegerList, DefaultShiftingBasedIntegerList, Sublist<Integer>, ShiftableList
	{
		protected final IntegerList underlying;
		protected final int start;
		protected int size;
		
		public IntegerSublist(IntegerList underlying, int start, int initialSize)
		{
			rangeCheckIntervalByLength(underlying.size(), start, initialSize);
			
			this.underlying = underlying;
			this.start = start;
			this.size = initialSize;
		}
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public int getInt(int index)
		{
			return underlying.getInt(index + start);
		}
		
		@Override
		public void setInt(int index, int value)
		{
			underlying.setInt(index + start, value);
		}
		
		@Override
		public void shiftRegionStretchingFromIndexToEndByAmountChangingSize(int start, int amount)
		{
			rangeCheckCursorPoint(size(), start);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(underlying, start, amount);
			
			size += amount;
		}
		
		
		
		@Override
		public IntegerList clone()
		{
			IntegerList c = new IntegerArrayList();
			c.addAll(this);
			return c;
		}
		
		
		
		@Override
		public IntegerList getUnderlying()
		{
			return underlying;
		}
		
		@Override
		public int getSublistStartingIndex()
		{
			return start;
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class IntegerArrayList
	implements DefaultShiftingBasedIntegerList, ListWithSetSize<Integer>, Trimmable, TransparentContiguousArrayBackedCollection<int[]>, KnowsLengthFixedness
	{
		protected int size = 0;
		protected int[] data;
		protected GrowerComputationallyUnreducedPurelyRecursive grower;
		
		
		
		
		//In case subclasses don't want to deal with the other things ^^'
		protected IntegerArrayList(Void thisiswhyyoushouldntusejavaconstructorsAlthoughIdontknowhowtomakeinheritanceworkwellwithoutinitmethodsorfactoriesorsomething)
		{
		}
		
		
		/**
		 * Capacity increment is different from Vector's.  It is the percentage of the capacity by which the capacity is increased.
		 * The formula being: <code>newCapacity = capacity + (capacity * capacityIncrement)</code><br>
		 * @param grower This can be anything.  The only downside is performance: all methods will add at least the minimum amount required regardless of what this specifies.
		 */
		public IntegerArrayList(int initialCapacity, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this.data = initialCapacity == 0 ? ArrayUtilities.EmptyIntArray : new int[initialCapacity];
			this.grower = grower;
		}
		
		public IntegerArrayList(@LiveValue @WritableValue int[] LIVEBACKING, int size, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this.data = LIVEBACKING;
			this.grower = grower;
			this.size = size;
		}
		
		public IntegerArrayList(@SnapshotValue @ReadonlyValue Collection<Integer> c, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this(c.size(), grower);
			this.addAll(c);
		}
		
		
		
		
		
		public IntegerArrayList()
		{
			this(DefaultPrimitiveArrayListInitialCapacity);
		}
		
		public IntegerArrayList(int initialCapacity)
		{
			this(initialCapacity, defaultPrimitiveArrayListGrower(initialCapacity));
		}
		
		public IntegerArrayList(@SnapshotValue @ReadonlyValue Collection<Integer> c)
		{
			this(c, defaultPrimitiveArrayListGrower(c.size()));
		}
		
		public IntegerArrayList(@LiveValue @WritableValue int[] LIVEBACKING, int size)
		{
			this(LIVEBACKING, size, defaultPrimitiveArrayListGrower(LIVEBACKING.length));
		}
		
		public IntegerArrayList(@LiveValue @WritableValue int[] LIVEBACKING)
		{
			this(LIVEBACKING, LIVEBACKING.length);
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return false;
		}
		
		
		
		@Override
		public void setSize(int newSize)
		{
			ensureCapacity(newSize);
			this.size = newSize;
			
			// :D
		}
		
		
		
		
		
		//This pattern is nicer for subclassing in any Java class! :D
		protected void setupClone(IntegerArrayList freshClone)
		{
			freshClone.size = this.size;
			
			freshClone.data = new int[size];
			System.arraycopy(this.data, 0, freshClone.data, 0, size);
			
			freshClone.grower = this.grower;
		}
		
		@Override
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public IntegerArrayList clone()
		{
			IntegerArrayList clone = new IntegerArrayList();
			this.setupClone(clone);
			return clone;
		}
		
		
		
		
		
		
		
		
		
		
		//This is what unlocks all the System.arraycopy() magic!  8>
		@ImplementationTransparency
		@LiveValue
		public Slice<int[]> getLiveContiguousArrayBackingUNSAFE()
		{
			return new Slice(data, 0, size);
		}
		
		
		
		
		
		
		
		
		
		public void setInt(int index, int value)
		{
			rangeCheckMember(this.size, index);
			data[index] = value;
		}
		
		public int getInt(int index)
		{
			rangeCheckMember(this.size(), index);
			return data[index];
		}
		
		
		
		
		
		
		
		@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public void clear()
		{
			size = 0;
		}
		
		@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public int size()
		{
			return size;
		}
		
		@ImplementationTransparency
		public void trimToSize()
		{
			int[] newdata = new int[size];
			System.arraycopy(data, 0, newdata, 0, size);
			this.data = newdata;
		}
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.trimToSize();
			return TrimmableTrimRV.DontKeepInvoking;
		}
		
		
		
		
		
		@ImplementationTransparency
		public int getCapacity()
		{
			return data.length;
		}
		
		@ImplementationTransparency
		public void ensureCapacity(int minCapacity)
		{
			if (data.length < minCapacity)
			{
				//Enlarge
				int newCapacity = getNewCapacity(minCapacity);
				int[] newData = new int[newCapacity];
				System.arraycopy(data, 0, newData, 0, data.length);
				this.data = newData;
			}
		}
		
		/**
		 * This is also guaranteed not to return a capacity smaller than the provided minimum.
		 */
		@ImplementationTransparency
		public int getNewCapacity(int minCapacity)
		{
			if (minCapacity < 0)
				throw new IllegalArgumentException();
			
			return Math.max(minCapacity, grower.getNewSizeRecursive(data.length));  //The Math.max(min, ...) ensures that the grower can't break anything!  :3
		}
		
		
		
		
		
		
		
		/**
		 * sets the underlying array that backs the array list.
		 * Be careful, though, as updates can cause a new array to be created, obsoleting this one.
		 * Note, also, that only up to {@link #size()} elements will be used.
		 */
		@ImplementationTransparency
		public void setDirectBuffer(@LiveValue int[] array)
		{
			if (array.length < size)
				throw new IllegalArgumentException("Array is too small; size="+size+", capacity="+array.length);
			
			this.data = array;
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static class FixedLengthArrayWrapperIntegerList
	implements IntegerList, TransparentContiguousArrayBackedCollection<int[]>, KnowsLengthFixedness
	{
		protected final int[] underlying;
		protected final int offset, length;
		
		public FixedLengthArrayWrapperIntegerList(int[] underlying, int offset, int length)
		{
			rangeCheckIntervalByLength(underlying.length, offset, length);
			
			this.underlying = underlying;
			this.offset = offset;
			this.length = length;
		}
		
		public FixedLengthArrayWrapperIntegerList(int[] underlying)
		{
			this(underlying, 0, underlying.length);
		}
		
		public FixedLengthArrayWrapperIntegerList(Slice<int[]> underlying)
		{
			this(underlying.getUnderlying(), underlying.getOffset(), underlying.getLength());
		}
		
		@Override
		public IntegerList clone()
		{
			return new FixedLengthArrayWrapperIntegerList(toIntArray());
		}
		
		
		
		@Override
		public int[] toIntArray()
		{
			return sliceToNewIntArrayOP(this.getLiveContiguousArrayBackingUNSAFE());
		}
		
		@Override
		public Slice<int[]> getLiveContiguousArrayBackingUNSAFE()
		{
			return new Slice<>(underlying, offset, length);
		}
		
		@Override
		public int size()
		{
			return length;
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return true;
		}
		
		
		
		@Override
		public SimpleIntegerIterator newSimpleIntegerIterator()
		{
			return new SimpleIntegerIterator()
			{
				int index = 0;
				
				public int nextrpInt() throws StopIterationReturnPath
				{
					int i = index;
					
					if (i >= size())
					{
						throw StopIterationReturnPath.I;
					}
					else
					{
						index = i + 1;
						return underlying[offset+i];
					}
				}
			};
		}
		
		@Override
		public IntegerList subList(int fromIndex, int toIndex)
		{
			rangeCheckInterval(this.size(), fromIndex, toIndex);
			return new FixedLengthArrayWrapperIntegerList(this.underlying, fromIndex+offset, toIndex-fromIndex);
		}
		
		@Override
		public int getInt(int index)
		{
			return underlying[offset + index];
		}
		
		@Override
		public void setInt(int index, int value)
		{
			underlying[offset + index] = value;
		}
		
		
		
		
		
		
		
		
		
		//Unallowed Dim-altering Methods!
		
		@Override
		public void clear()
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setSizeInt(int newSize, int elementToAddIfGrowing)
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void insertInt(int index, int value)
		{
			throw new UnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	@ReadonlyValue
	public static IntegerList intArrayAsList(@LiveValue @WritableValue int[] array, int offset, int length)
	{
		return new FixedLengthArrayWrapperIntegerList(array, offset, length);
	}
	
	@ReadonlyValue
	public static IntegerList intArrayAsList(@LiveValue @WritableValue int[] array)
	{
		return intArrayAsList(array, 0, array.length);
	}
	
	@ReadonlyValue
	public static IntegerList intArrayAsList(@LiveValue @WritableValue Slice<int[]> arraySlice)
	{
		return intArrayAsList(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	
	
	
	@ThrowAwayValue
	public static IntegerList intArrayAsMutableList(@SnapshotValue @ReadonlyValue int[] array, int offset, int length)
	{
		return new IntegerArrayList(new FixedLengthArrayWrapperIntegerList(array, offset, length));
	}
	
	@ThrowAwayValue
	public static IntegerList intArrayAsMutableList(@SnapshotValue @ReadonlyValue int[] array)
	{
		return intArrayAsMutableList(array, 0, array.length);
	}
	
	@ThrowAwayValue
	public static IntegerList intArrayAsMutableList(@SnapshotValue @ReadonlyValue Slice<int[]> arraySlice)
	{
		return intArrayAsMutableList(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	@Immutable
	public static class ImmutableIntegerArrayList
	implements Serializable, Comparable<ImmutableIntegerArrayList>, IntegerList, KnowsLengthFixedness     //Let's not implement the general interfaces for accessing the underlying data jussssst to help make sure anyone who accesses it *knows* it's meant to be VERY MUCH READONLY!  XD'''
	{
		private static final long serialVersionUID = 1L;
		
		
		protected final int[] data;
		
		//Yeah I know it's bad practice for a thread-safe immutable object to cache things, but at best effort is wasted, no constraints are violated!     (If I'm wrong, fire me X'D )
		protected transient long hashCode;     //we cache it in case the intarray is large :33
		
		protected ImmutableIntegerArrayList(int[] LIVEDATA)
		{
			if (LIVEDATA == null) throw new NullPointerException();
			this.data = LIVEDATA;
		}
		
		
		
		
		
		/**
		 * DO NOT *EVER* MODIFY THE ARRAY YOU PASSED HERE AFTER CALLING THIS X"D
		 * ( THE HASH CODE IS CACHED FOR ONE, AND THIS MAY BE EMBEDDED IN HASHMAPS--THIS IS SUPPOSED TO BE IMMUTABLE AFTER ALL X'DDD )
		 * @see #getREADONLYLiveWholeArrayBackingUNSAFE()
		 */
		@ImplementationTransparency
		public static ImmutableIntegerArrayList newLIVE(@LiveValue int[] LIVEDATA)
		{
			return new ImmutableIntegerArrayList(LIVEDATA);
		}
		
		
		@ImplementationTransparency
		public static ImmutableIntegerArrayList newSingleton(int singleMember)
		{
			return new ImmutableIntegerArrayList(new int[]{singleMember});
		}
		
		
		
		
		public static ImmutableIntegerArrayList newCopying(@SnapshotValue List<Integer> data)
		{
			if (data instanceof ImmutableIntegerArrayList)
				return (ImmutableIntegerArrayList)data;
			
			if (TransparentContiguousArrayBackedCollection.is(data))
			{
				Slice<Object> s = ((TransparentContiguousArrayBackedCollection)data).getLiveContiguousArrayBackingUNSAFE();
				if (s.getUnderlying() instanceof int[])
				{
					return newCopying((Slice)s);
				}
			}
			
			if (data instanceof IntegerList)
			{
				return newLIVE(((IntegerList)data).toIntArray());
			}
			else
			{
				int n = data.size();
				int[] a = new int[n];
				
				int i = 0;
				for (Integer e : data)
				{
					if (i >= n)
						throw new ImpossibleException();
					
					a[i] = e;
					i++;
				}
				
				if (i != n)
					throw new ImpossibleException();
				
				return newLIVE(a);
			}
		}
		
		public static ImmutableIntegerArrayList newCopying(@SnapshotValue int[] data)
		{
			return new ImmutableIntegerArrayList(data.clone());
		}
		
		public static ImmutableIntegerArrayList newCopying(@SnapshotValue int[] data, int offset, int length)
		{
			if (offset == 0 && length == data.length)
				return newCopying(data);  //who knows, int[].clone() might be faster! \o/   (it certainly shouldn't be slower!!)
			else
			{
				int[] newArray = new int[length];
				System.arraycopy(data, offset, newArray, 0, length);
				return new ImmutableIntegerArrayList(newArray);
			}
		}
		
		public static ImmutableIntegerArrayList newCopying(@SnapshotValue Slice<int[]> data)
		{
			return newCopying(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		
		
		
		
		public static ImmutableIntegerArrayList newLIVEOrCopying(@PossiblySnapshotPossiblyLiveValue int[] data, int offset, int length)
		{
			if (offset == 0 && length == data.length)
				return newLIVE(data);
			else
				return newCopying(data, offset, length);
		}
		
		public static ImmutableIntegerArrayList newLIVEOrCopying(@PossiblySnapshotPossiblyLiveValue Slice<int[]> data)
		{
			return newLIVEOrCopying(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		
		
		
		
		
		
		public static final ImmutableIntegerArrayList Empty = ImmutableIntegerArrayList.newLIVE(ArrayUtilities.EmptyIntArray);
		
		
		
		
		
		
		
		
		@ThrowAwayValue
		@Override
		public int[] toIntArray()
		{
			return data.clone();
		}
		
		/**
		 * DO NOT MODIFY THIS X"D
		 * ( THE HASH CODE IS CACHED FOR ONE, AND THIS MAY BE EMBEDDED IN HASHMAPS--THIS IS SUPPOSED TO BE IMMUTABLE AFTER ALL X'DDD )
		 */
		@ImplementationTransparency
		@LiveValue
		@ReadonlyValue //!!!!
		public int[] getREADONLYLiveWholeArrayBackingUNSAFE()
		{
			return data;
		}
		
		@Override
		public int size()
		{
			return data.length;
		}
		
		@Override
		public int getInt(int index)
		{
			return data[index];
		}
		
		@Override
		public void getAllInts(int offsetInThisSource, @WritableValue int[] dest, int offsetInDest, int lengthInBoth)
		{
			System.arraycopy(this.data, offsetInThisSource, dest, offsetInDest, lengthInBoth);
		}
		
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return true;
		}
		
		
		
		
		@Override
		public int hashCode()
		{
			long hashCodeField = this.hashCode;
			
			boolean cached = hashCodeField != 0;
			
			
			if (cached)
			{
				return (int)this.hashCode;
			}
			else
			{
				int hashCode = Arrays.hashCode(this.data);
				this.hashCode = hashCode | 0xFFFF_FFFF_0000_0000l;  //hashCodes can be zero!  Not often XDD   But can be!  So it's not correct unless we account for that :>      (And if it was two fields then it'd be a concurrency bug!)
				return hashCode;
			}
		}
		
		
		
		
		@Override
		public boolean equals(@ReadonlyValue Object obj)
		{
			if (obj == this)
			{
				return true;
			}
			else if (obj instanceof ImmutableIntegerArrayList)
			{
				ImmutableIntegerArrayList other = (ImmutableIntegerArrayList)obj;
				
				//Checking the hashcodes is a VERY fast negative test and is almost always true-negative!! :DDD   (rarely it's false-positive, but never false-negative!)
				
				long thisHC = this.hashCode;
				long otherHC = other.hashCode;
				
				boolean thisHashCodeCached = thisHC != 0;
				boolean otherHashCodeCached = otherHC != 0;
				
				int thisHashCode = (int)thisHC;
				int otherHashCode = (int)otherHC;
				
				if (thisHashCodeCached)
				{
					if (otherHashCodeCached)
					{
						if (thisHashCode != otherHashCode)
							return false;
					}
					else
					{
						//Scanning one int array is faster than scanning two! :D
						if (thisHashCode != other.hashCode())
							return false;
					}
				}
				else
				{
					if (otherHashCodeCached)
					{
						//Scanning one int array is faster than scanning two! :D
						if (this.hashCode() != otherHashCode)
							return false;
					}
					else
					{
						//Then don't make any hash codes X3
					}
				}
				
				return this.equalsIntArray(other.data);
			}
			else
			{
				return false;
			}
		}
		
		
		@Override
		public int compareTo(@ReadonlyValue ImmutableIntegerArrayList other)
		{
			return this.compareToIntArray(other.data);
		}
		
		
		public boolean equalsIntArray(@ReadonlyValue int[] o)
		{
			int[] ourData = this.getREADONLYLiveWholeArrayBackingUNSAFE();
			int[] theirData = o;
			
			return Arrays.equals(ourData, theirData);
		}
		
		
		public int compareToIntArray(@ReadonlyValue int[] o)
		{
			int[] ourData = this.getREADONLYLiveWholeArrayBackingUNSAFE();
			int[] theirData = o;
			
			return ArrayUtilities.compareLittleEndianLengthsFirst(ourData, theirData);  //lengths-first is more efficient if they're different but hardly any less efficient if they're always the same!  A good default, don't you think? :D
		}
		
		
		
		
		@Override
		public ImmutableIntegerArrayList clone()
		{
			return this;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		// Unallowed Write Methods!
		
		@Override
		public void clear()
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setSizeInt(int newSize, int elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void insertInt(int index, int value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setInt(int index, int value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static IntegerList unmodifiableIntegerList(IntegerList integerList)
	{
		return integerList instanceof UnmodifiableIntegerListWrapper ? integerList : new UnmodifiableIntegerListWrapper(integerList);
	}
	
	public static class UnmodifiableIntegerListWrapper
	implements IntegerList, KnowsLengthFixedness, TransparentContiguousArrayBackedCollection
	{
		protected final IntegerList underlying;
		
		public UnmodifiableIntegerListWrapper(IntegerList underlying)
		{
			this.underlying = underlying;
		}
		
		@ImplementationTransparency
		public IntegerList getUnderlying()
		{
			return underlying;
		}
		
		
		
		
		
		@Override
		public UnmodifiableIntegerListWrapper clone()
		{
			return new UnmodifiableIntegerListWrapper(underlying.clone());
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.underlying);
		}
		
		@Override
		public boolean isTransparentContiguousArrayBackedCollection()
		{
			return TransparentContiguousArrayBackedCollection.is(underlying);
		}
		
		@Override
		public Slice getLiveContiguousArrayBackingUNSAFE()
		{
			return ((TransparentContiguousArrayBackedCollection)underlying).getLiveContiguousArrayBackingUNSAFE();
		}
		
		
		
		
		
		
		//Use default so they can't get around our readonly-ness!  ;D
		//	public Iterator<Integer> iterator()
		//	public ListIterator<Integer> listIterator()
		//	public ListIterator<Integer> listIterator(int index)
		//	public SimpleIterator<Integer> simpleIterator()
		//	public SimpleIntegerIterator newSimpleIntegerIterator()
		//	public IntegerList subList(int fromIndex, int toIndex)
		
		
		
		
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeRangeByLength(int start, int length) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void replaceAll(UnaryOperator<Integer> operator)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeIf(Predicate<? super Integer> filter)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void sort(Comparator<? super Integer> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void clear()
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public Integer remove(int index)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllInts(int[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllInts(Slice<int[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllInts(int[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllInts(Slice<int[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllInts(int[] a, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setInt(int index, int value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertInt(int index, int value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSizeInt(int newSize, int elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSize(int newSize, Integer elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSize(int newSize)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllInts(int index, int[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllInts(int index, Slice<int[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllInts(int index, int[] array, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAll(int destIndex, List sourceU, int sourceIndex, int amount) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addInt(int value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll(Collection<? extends Integer> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllInts(int[] elements, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeByIndex(int index)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public int removeIntByIndex(int index) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeInt(int value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean retainAll(Collection<?> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllInts(int index, int[] array, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll(int index, Collection<? extends Integer> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllInts(int index, int[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllInts(int index, Slice<int[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public Integer set(int index, Integer value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void add(int index, Integer value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeAll(Collection<?> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean remove(Object o)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean add(Integer e)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		
		
		
		
		
		
		
		
		
		public void forEach(Consumer<? super Integer> action)
		{
			underlying.forEach(action);
		}
		
		public int size()
		{
			return underlying.size();
		}
		
		public boolean equals(Object o)
		{
			return underlying.equals(o);
		}
		
		public int hashCode()
		{
			return underlying.hashCode();
		}
		
		public Spliterator<Integer> spliterator()
		{
			return underlying.spliterator();
		}
		
		public Stream<Integer> stream()
		{
			return underlying.stream();
		}
		
		public Stream<Integer> parallelStream()
		{
			return underlying.parallelStream();
		}
		
		public int getInt(int index)
		{
			return underlying.getInt(index);
		}
		
		public int indexOfInt(int value)
		{
			return underlying.indexOfInt(value);
		}
		
		public int lastIndexOfInt(int value)
		{
			return underlying.lastIndexOfInt(value);
		}
		
		public boolean equivalent(List<Integer> other)
		{
			return underlying.equivalent(other);
		}
		
		public int[] toIntArray()
		{
			return underlying.toIntArray();
		}
		
		public boolean containsInt(int value)
		{
			return underlying.containsInt(value);
		}
		
		public Integer get(int index)
		{
			return underlying.get(index);
		}
		
		public int indexOf(Object o)
		{
			return underlying.indexOf(o);
		}
		
		public int lastIndexOf(Object o)
		{
			return underlying.lastIndexOf(o);
		}
		
		public boolean containsAll(Collection<?> c)
		{
			return underlying.containsAll(c);
		}
		
		public boolean isEmpty()
		{
			return underlying.isEmpty();
		}
		
		public boolean contains(Object o)
		{
			return underlying.contains(o);
		}
		
		public Object[] toArray()
		{
			return underlying.toArray();
		}
		
		public <T> T[] toArray(T[] a)
		{
			return underlying.toArray(a);
		}
		
		public void storeBoxingIntoArray(Slice<Object[]> array)
		{
			underlying.storeBoxingIntoArray(array);
		}
		
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class IntegerTable
	{
		protected int width;
		protected int[] data;
		
		public IntegerTable(int width, int height)
		{
			if (width < 0)
				throw new IllegalArgumentException();
			if (height < 0)
				throw new IllegalArgumentException();
			
			data = new int[width * height];
		}
		
		
		
		@Nonnegative
		public int getNumberOfColumns()
		{
			return width;
		}
		
		@Nonnegative
		public int getNumberOfRows()
		{
			return data.length / width;
		}
		
		
		@Nonnegative
		public int getNumberOfCells()
		{
			return data.length;
		}
		
		
		
		
		
		public int getCellContentsInteger(int columnIndex, int rowIndex) throws IndexOutOfBoundsException
		{
			if (columnIndex < 0) throw new IndexOutOfBoundsException();
			if (rowIndex < 0) throw new IndexOutOfBoundsException();
			if (columnIndex >= getNumberOfColumns()) throw new IndexOutOfBoundsException();
			if (rowIndex >= getNumberOfRows()) throw new IndexOutOfBoundsException();
			
			return this.data[rowIndex * this.getNumberOfColumns() + columnIndex];
		}
		
		public void setCellContentsInteger(int columnIndex, int rowIndex, @Nullable int newValue) throws IndexOutOfBoundsException
		{
			if (columnIndex < 0) throw new IndexOutOfBoundsException();
			if (rowIndex < 0) throw new IndexOutOfBoundsException();
			if (columnIndex >= getNumberOfColumns()) throw new IndexOutOfBoundsException();
			if (rowIndex >= getNumberOfRows()) throw new IndexOutOfBoundsException();
			
			this.data[rowIndex * this.getNumberOfColumns() + columnIndex] = newValue;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Nullable
		public Integer getCellContents(int columnIndex, int rowIndex) throws IndexOutOfBoundsException
		{
			return getCellContentsInteger(columnIndex, rowIndex);
		}
		
		public void setCellContents(int columnIndex, int rowIndex, @Nullable Integer newValue) throws IndexOutOfBoundsException
		{
			setCellContentsInteger(columnIndex, rowIndex, newValue);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static double getDouble(List<Double> list, int index)
	{
		if (list instanceof DoubleList)
			return ((DoubleList) list).getDouble(index);
		else
			return list.get(index);
	}
	
	public static void setDouble(List<Double> list, int index, double value)
	{
		if (list instanceof DoubleList)
			((DoubleList) list).setDouble(index, value);
		else
			list.set(index, value);
	}
	
	
	
	
	
	
	
	public static interface SimpleDoubleIterable
	extends SimpleIterable<Double>
	{
		public SimpleDoubleIterator newSimpleDoubleIterator();
		
		public default SimpleIterator<Double> simpleIterator()
		{
			SimpleDoubleIterator i = this.newSimpleDoubleIterator();
			return () -> i.nextrpDouble();
		}
	}
	
	
	public static interface SimpleDoubleIterator
	extends SimpleIterator<Double>
	{
		public double nextrpDouble() throws StopIterationReturnPath;
		
		
		@Override
		public default Double nextrp() throws StopIterationReturnPath
		{
			return nextrpDouble();
		}
	}
	
	
	
	@SignalInterface
	public static interface DoubleCollection
	extends PrimitiveCollection<Double>, SimpleDoubleIterable
	{
		public boolean addDouble(double value);
		
		public boolean removeDouble(double value);
		
		
		
		
		
		@ThrowAwayValue
		public static double[] defaultToDoubleArray(DoubleCollection collection)
		{
			double[] array = new double[collection.size()];
			SimpleDoubleIterator i = collection.newSimpleDoubleIterator();
			
			int index = 0;
			while (true)
			{
				try
				{
					array[index] = i.nextrpDouble();
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
				
				index++;
			}
			
			return array;
		}
		
		@ThrowAwayValue
		public default double[] toDoubleArray()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(this.size(), u);
				
				if (u.getUnderlying() instanceof double[])
				{
					return sliceToNewDoubleArrayOP((Slice<double[]>) u);
				}
			}
			
			return defaultToDoubleArray(this);
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public default double[] toDoubleArrayPossiblyLive()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				int size = this.size();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				Object und = u.getUnderlying();
				
				if (und instanceof double[] && u.getOffset() == 0 && ((double[])und).length == size)
				{
					return (double[])und;
				}
			}
			
			return defaultToDoubleArray(this);
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public default Slice<double[]> toDoubleArraySlicePossiblyLive()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(this.size(), u);
				
				if (u.getUnderlying() instanceof double[])
				{
					return (Slice<double[]>) u;
				}
			}
			
			return wholeArraySliceDouble(defaultToDoubleArray(this));
		}
		
		
		
		
		
		
		public default boolean containsDouble(double value)
		{
			SimpleDoubleIterator i = newSimpleDoubleIterator();
			
			while (true)
			{
				try
				{
					if (eqSane(i.nextrpDouble(), value))
						return true;
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
			}
			
			return false;
		}
		
		
		
		
		
		
		@Override
		public default Iterator<Double> iterator()
		{
			return SimpleDoubleIterable.super.iterator();
		}
		
		@Override
		public default boolean isEmpty()
		{
			return size() == 0;
		}
		
		
		
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllDoubles(double[] array)
		{
			return addAllDoubles(array, 0, array.length);
		}
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllDoubles(Slice<double[]> arraySlice)
		{
			return addAllDoubles(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllDoubles(double[] elements, int offset, int length)
		{
			return defaultAddAllDoubles(this, elements, offset, length);
		}
		
		public static boolean defaultAddAllDoubles(DoubleCollection self, double[] elements, int offset, int length)
		{
			boolean changedAtAll = false;
			int e = offset + length;
			for (int i = offset; i < e; i++)
				changedAtAll |= self.addDouble(elements[i]);
			return changedAtAll;
		}
		
		
		
		
		public default void removeAllDoubles(double[] array)
		{
			removeAllDoubles(array, 0, array.length);
		}
		
		public default void removeAllDoubles(Slice<double[]> arraySlice)
		{
			removeAllDoubles(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public default void removeAllDoubles(double[] a, int offset, int length)
		{
			int e = offset + length;
			for (int i = offset; i < e; i++)
				this.removeDouble(a[i]);
		}
		
		
		
		
		
		
		
		
		
		
		@Override
		public default boolean contains(Object o)
		{
			return o instanceof Double && containsDouble((Double)o);
		}
		
		
		@Override
		public default boolean add(Double e)
		{
			return addDouble(e);
		}
		
		
		@Override
		public default boolean remove(Object o)
		{
			return o instanceof Double && removeDouble((Double)o);
		}
		
		
		@Override
		public default boolean containsAll(Collection<?> c)
		{
			if (c instanceof DoubleCollection)
			{
				DoubleCollection cc = (DoubleCollection) c;
				
				SimpleDoubleIterator i = cc.newSimpleDoubleIterator();
				while (true)
				{
					try
					{
						if (this.containsDouble(i.nextrpDouble()))
							return true;
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return false;
			}
			else
			{
				for (Object e : c)
					if (this.contains(e))
						return true;
				return false;
			}
		}
		
		
		@Override
		public default boolean addAll(Collection<? extends Double> c)
		{
			if (c instanceof DoubleCollection)
			{
				boolean changedAtAll = false;
				
				SimpleDoubleIterator i = ((DoubleCollection)c).newSimpleDoubleIterator();
				
				while (true)
				{
					try
					{
						changedAtAll |= this.add(i.nextrpDouble());
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (Double e : c)
				{
					changedAtAll |= this.add(e);
				}
				
				return changedAtAll;
			}
		}
		
		
		
		@Override
		public default boolean removeAll(Collection<?> c)
		{
			if (c instanceof DoubleCollection)
			{
				boolean changedAtAll = false;
				
				SimpleDoubleIterator i = ((DoubleCollection)c).newSimpleDoubleIterator();
				
				while (true)
				{
					try
					{
						changedAtAll |= this.remove(i.nextrpDouble());
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (Object e : c)
				{
					changedAtAll |= this.remove(e);
				}
				
				return changedAtAll;
			}
		}
		
		
		
		public default String _toString()
		{
			StringBuilder b = new StringBuilder("[");
			
			SimpleDoubleIterator i = newSimpleDoubleIterator();
			
			boolean first = true;
			
			while (true)
			{
				double e;
				
				try
				{
					e = i.nextrpDouble();
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
				
				
				
				//Notice this comes after the possible 'break'!  ^_~
				if (first)
					first = false;
				else
					b.append(", ");
				
				b.append(e);
			}
			
			b.append(']');
			
			return b.toString();
		}
	}
	
	
	
	public static interface DefaultToArraysDoubleCollection
	extends DoubleCollection
	{
		@Override
		public default Object[] toArray()
		{
			Object[] a = new Object[size()];
			storeBoxingIntoArray(wholeArraySliceReference(a));
			return a;
		}
		
		@Override
		public default <T> T[] toArray(T[] a)
		{
			if (a.length >= this.size())
			{
				storeBoxingIntoArray(wholeArraySliceReference(a));
				return a;
			}
			else
			{
				Object[] aa;
				
				if (a.getClass() == Double[].class)
				{
					aa = new Double[size()];
				}
				else if (a.getClass() == Object[].class)
				{
					aa = new Object[size()];
				}
				else
				{
					throw new StructuredClassCastException(a.getClass());
				}
				
				storeBoxingIntoArray(wholeArraySliceReference(aa));
				
				return (T[])aa;
			}
		}
		
		public void storeBoxingIntoArray(Slice<Object[]> array);
	}
	
	
	
	
	
	
	@SignalInterface
	public static interface DoubleListRO
	{
		public double getDouble(int index);
		
		public int size();
		
		
		
		public default int indexOfDouble(double value)
		{
			int n = this.size();
			for (int i = 0; i < n; i++)
				if (eqSane(this.getDouble(i), value))
					return i;
			return -1;
		}
		
		public default int lastIndexOfDouble(double value)
		{
			int i = this.size()-1;
			while (i >= 0)
			{
				if (eqSane(this.getDouble(i), value))
					return i;
				break;
			}
			return i;   // ;D
		}
		
		
		
		public default boolean equivalent(List<Double> other)
		{
			int size = this.size();
			if (other.size() != size)
				return false;
			
			
			if (other instanceof DoubleListRO)  //All DoubleLists will implement this interface too so this accounts for them as well :3
			{
				return equivalentFixedRO((DoubleListRO)other);
			}
			else
			{
				for (int i = 0; i < size; i++)
				{
					if (!eqSane(this.getDouble(i), other.get(i).doubleValue()))
						return false;
				}
			}
			
			return true;
		}
		
		
		
		public default boolean equivalentFixedRO(DoubleListRO other)
		{
			int size = this.size();
			if (other.size() != size)
				return false;
			
			for (int i = 0; i < size; i++)
			{
				if (!eqSane(this.getDouble(i), other.getDouble(i)))
					return false;
			}
			
			return true;
		}
	}
	
	
	
	@SignalInterface
	public static interface DoubleListRWFixed
	extends DoubleListRO
	{
		public void setDouble(int index, double value);
	}
	
	
	
	@SignalInterface
	public static interface DoubleList
	extends DoubleCollection, NiceList<Double>, DoubleListRO, DoubleListRWFixed, ListWithRemoveRange, DefaultToArraysDoubleCollection, PubliclyCloneable, ListWithSetSize<Double>, ListWithSetAll, NonuniformMethodsForDoubleList
	{
		public void insertDouble(int index, double value);
		
		
		
		/**
		 * This should be equivalent to just constructing and returning a new in-memory stock implementation with the same contents  (but can easily be faster, especially for those stock impl.s! XD )
		 * As for whether it's fixed-length or read-only or duplicateless or etc., that should be the same in the clone as it is in the main impl.
		 * If *and only if* it is immutable, this is allowed to return this same instance!  (ie the body of this method being <code>return this;</code>)
		 */
		public DoubleList clone();
		
		
		/**
		 * If size is smaller than the current, then this is equivalent to {@link #removeRange(int, int) removeRange}(newSize, this.{@link #size() size()})  :3
		 * If size is larger, then this appends newSize - this.{@link #size() size()} elementToAddIfGrowing's to the end like that many {@link #addDouble(double)}  :D
		 */
		public void setSizeDouble(int newSize, double elementToAddIfGrowing);
		
		public default void setSize(int newSize, Double elementToAddIfGrowing)
		{
			setSizeDouble(newSize, elementToAddIfGrowing);
		}
		
		public default void setSize(int newSize)
		{
			setSizeDouble(newSize, 0.0d);
		}
		
		
		
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void setAllDoubles(int index, double[] array)
		{
			setAllDoubles(index, array, 0, array.length);
		}
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void setAllDoubles(int index, Slice<double[]> arraySlice)
		{
			setAllDoubles(index, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		public default void setAllDoubles(int start, double[] array, int offset, int length)
		{
			int size = this.size();
			
			rangeCheckIntervalByLength(size, start, length);
			rangeCheckIntervalByLength(array.length, offset, length);
			
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(size, start, length);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				if (u.getUnderlying() instanceof double[])
				{
					Slice<double[]> s = (Slice<double[]>) u;
					
					System.arraycopy(array, offset, s.getUnderlying(), s.getOffset()+start, length);
					
					return;
				}
			}
			
			
			defaultSetAllDoubles(this, start, array, offset, length);
		}
		
		
		public static void defaultSetAllDoubles(DoubleList list, int start, @WritableValue double[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				list.setDouble(start + i, array[offset + i]);
		}
		
		
		
		
		
		@Override
		public default void setAll(int destIndex, List sourceU, int sourceIndex, @Nonnegative int amount) throws IndexOutOfBoundsException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			List<Double> source = sourceU;
			DoubleList dest = this;
			
			int sourceSize = source.size();
			int destSize = dest.size();
			rangeCheckIntervalByLength(sourceSize, sourceIndex, amount);
			rangeCheckIntervalByLength(destSize, destIndex, amount);
			
			
			
			if (TransparentContiguousArrayBackedCollection.is(source))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection<?>)source).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(sourceSize, sourceIndex, amount);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(sourceSize, u);
				
				if (u.getUnderlying() instanceof double[])
				{
					Slice<double[]> s = (Slice<double[]>)u;
					this.setAllDoubles(destIndex, s.subslice(sourceIndex, amount));
					return;
				}
			}
			
			
			
			
			if (source instanceof DoubleList)
			{
				DoubleList primSource = (DoubleList) source;
				
				if (destIndex < sourceIndex)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
				{
					for (int i = 0; i < amount; i++)
						dest.setDouble(destIndex+i, primSource.getDouble(sourceIndex+i));
				}
				else
				{
					for (int i = amount-1; i >= 0; i--)
						dest.setDouble(destIndex+i, primSource.getDouble(sourceIndex+i));
				}
			}
			else
			{
				if (destIndex < sourceIndex)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
				{
					for (int i = 0; i < amount; i++)
						dest.set(destIndex+i, source.get(sourceIndex+i));
				}
				else
				{
					for (int i = amount-1; i >= 0; i--)
						dest.set(destIndex+i, source.get(sourceIndex+i));
				}
			}
		}
		
		
		
		
		
		
		
		/**
		 * Copies <code>array.length</code> elements into <code>array</code> starting with the <code>start</code>th element.<br>
		 */
		public default void getAllDoubles(int start, @WritableValue double[] array, int offset, int length)
		{
			int size = this.size();
			
			rangeCheckIntervalByLength(size, start, length);
			rangeCheckIntervalByLength(array.length, offset, length);
			
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(size, start, length);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				if (u.getUnderlying() instanceof double[])
				{
					Slice<double[]> s = (Slice<double[]>) u;
					
					System.arraycopy(s.getUnderlying(), s.getOffset()+start, array, offset, length);
					
					return;
				}
			}
			
			
			defaultGetAllDoubles(this, start, array, offset, length);
		}
		
		
		public static void defaultGetAllDoubles(DoubleList list, int start, @WritableValue double[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				array[offset + i] = list.getDouble(start + i);
		}
		
		
		
		
		
		
		@ThrowAwayValue
		public default double[] getAllDoubles(int start, int end)
		{
			rangeCheckInterval(this.size(), start, end);
			
			double[] buff = new double[end-start];
			getAllDoubles(start, buff, 0, buff.length);
			return buff;
		}
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public default boolean addDouble(double value)
		{
			this.insertDouble(this.size(), value);
			return true;
		}
		
		@Override
		public default boolean addAll(Collection<? extends Double> c)
		{
			this.addAll(this.size(), c);
			return true;
		}
		
		@Override
		public default boolean addAllDoubles(double[] elements, int offset, int length)
		{
			insertAllDoubles(this.size(), elements, offset, length);
			return true;
		}
		
		public default void removeByIndex(int index)
		{
			removeRange(index, index+1);
		}
		
		public default double removeDoubleByIndex(int index) throws IndexOutOfBoundsException
		{
			double v = this.getDouble(index);
			removeByIndex(index);
			return v;
		}
		
		@Override
		public default boolean removeDouble(double value)
		{
			int i = this.indexOfDouble(value);
			
			if (i != -1)
			{
				this.removeByIndex(i);
				return true;
			}
			else
			{
				return false;
			}
		}
		
		@Override
		public default boolean retainAll(Collection<?> c)
		{
			if (c instanceof DoubleCollection)
			{
				DoubleCollection cc = (DoubleCollection) c;
				
				boolean changedAtAll = false;
				
				for (int i = size() - 1; i >= 0; i--)
				{
					if (!cc.containsDouble(getDouble(i)))
					{
						removeDoubleByIndex(i);
						changedAtAll = true;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (int i = size() - 1; i >= 0; i--)
				{
					if (!c.contains(getDouble(i)))
					{
						removeDoubleByIndex(i);
						changedAtAll = true;
					}
				}
				
				return changedAtAll;
			}
		}
		
		
		
		
		
		
		
		
		@Override
		public default SimpleDoubleIterator newSimpleDoubleIterator()
		{
			return new SimpleDoubleIterator()
			{
				int index = 0;
				
				public double nextrpDouble() throws StopIterationReturnPath
				{
					int i = index;
					
					if (i >= size())
					{
						throw StopIterationReturnPath.I;
					}
					else
					{
						index = i + 1;
						return getDouble(i);
					}
				}
			};
		}
		
		
		
		
		
		@Override
		public default boolean containsDouble(double value)
		{
			return indexOfDouble(value) != -1;
		}
		
		
		public default void insertAllDoubles(int index, double[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				insertDouble(index+i, array[offset+i]);
		}
		
		
		@Override
		public default boolean addAll(int index, Collection<? extends Double> c)
		{
			if (c instanceof DoubleCollection)
			{
				DoubleCollection cc = (DoubleCollection)c;
				
				SimpleDoubleIterator i = cc.newSimpleDoubleIterator();
				while (true)
				{
					try
					{
						insertDouble(index, i.nextrpDouble());
						index++;  //doesn't matter if this gets executed on the last iteration :3
					}
					catch (StopIterationReturnPath rp)
					{
						break;
					}
				}
			}
			else
			{
				for (Double e : c)
				{
					insertDouble(index, e);
					index++;
				}
			}
			
			return true;
		}
		
		
		
		public default void insertAllDoubles(int index, double[] array)
		{
			insertAllDoubles(index, array, 0, array.length);
		}
		
		public default void insertAllDoubles(int index, Slice<double[]> arraySlice)
		{
			insertAllDoubles(index, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		
		
		@Override
		public default Double get(int index)
		{
			return this.getDouble(index);
		}
		
		@Override
		public default Double set(int index, Double value)
		{
			Double previous = get(index);
			this.setDouble(index, value);
			return previous;
		}
		
		
		@Override
		public default void add(int index, Double value)
		{
			this.insertDouble(index, value);
		}
		
		@Override
		public default Double remove(int index) throws IndexOutOfBoundsException
		{
			return this.removeDoubleByIndex(index);
		}
		
		
		@Override
		public default int indexOf(Object o)
		{
			return o instanceof Double ? indexOfDouble((Double)o) : -1;
		}
		
		@Override
		public default int lastIndexOf(Object o)
		{
			return o instanceof Double ? lastIndexOfDouble((Double)o) : -1;
		}
		
		
		
		@Override
		public default ListIterator<Double> listIterator()
		{
			return listIterator(0);
		}
		
		@Override
		public default ListIterator<Double> listIterator(int index)
		{
			//Todo
			throw new NotYetImplementedException();
		}
		
		@Override
		public default DoubleList subList(int fromIndex, int toIndex)   //Also note that we narrow the return types of this :3
		{
			if (fromIndex == 0 && toIndex == this.size())
				return this;
			
			if (this instanceof Sublist)
			{
				Sublist<Double> s = (Sublist<Double>)this;
				return new DoubleSublist((DoubleList)s.getUnderlying(), s.getSublistStartingIndex() + fromIndex, toIndex - fromIndex);
			}
			else
			{
				return new DoubleSublist(this, fromIndex, toIndex - fromIndex);
			}
		}
		
		
		
		
		
		
		
		
		
		
		// Multiple-Inheritance conflict resolvers X'3
		@Override
		public default boolean removeAll(Collection<?> c)
		{
			return DefaultToArraysDoubleCollection.super.removeAll(c);
		}
		
		@Override
		public default boolean remove(Object o)
		{
			return DefaultToArraysDoubleCollection.super.remove(o);
		}
		
		@Override
		public default boolean containsAll(Collection<?> c)
		{
			return DefaultToArraysDoubleCollection.super.containsAll(c);
		}
		
		@Override
		public default boolean add(Double e)
		{
			return DefaultToArraysDoubleCollection.super.add(e);
		}
		
		@Override
		public default boolean isEmpty()
		{
			return DefaultToArraysDoubleCollection.super.isEmpty();
		}
		
		@Override
		public default boolean contains(Object o)
		{
			return DefaultToArraysDoubleCollection.super.contains(o);
		}
		
		
		
		
		
		@Override
		public default Object[] toArray()
		{
			return DefaultToArraysDoubleCollection.super.toArray();
		}
		
		@Override
		public default <T> T[] toArray(T[] a)
		{
			return DefaultToArraysDoubleCollection.super.toArray(a);
		}
		
		
		
		
		
		
		
		@Override
		public default void storeBoxingIntoArray(Slice<Object[]> array)
		{
			int n = this.size();
			for (int i = 0; i < n; i++)
				ArrayUtilities.setReference((Slice)array, i, this.get(i));
		}
		
		
		
		
		
		
		//Narrow the return types of these :3
		
		@Override
		public default DoubleList subListToEnd(int start)
		{
			return (DoubleList)NiceList.super.subListToEnd(start);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
		
		@Override
		public default DoubleList subListFromBeginning(int lengthWhichIsEndExclusiveInThisCaseXD)
		{
			return (DoubleList)NiceList.super.subListFromBeginning(lengthWhichIsEndExclusiveInThisCaseXD);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
		
		@Override
		public default DoubleList subListByLength(int start, int length)
		{
			return (DoubleList)NiceList.super.subListByLength(start, length);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
	}
	
	
	
	
	
	
	public static interface DefaultShiftingBasedDoubleList
	extends DoubleList
	{
		public default void clear()
		{
			removeRange(0, size());
		}
		
		@Override
		public default void insertDouble(int index, double value)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, 1);
			this.setDouble(index, value);
		}
		
		@Override
		public default void insertAllDoubles(int index, double[] array, int offset, int length)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, length);
			
			this.setAllDoubles(index, array, offset, length);
		}
		
		
		@Override
		public default boolean addAll(int index, Collection<? extends Double> c)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			int amount = c.size();  //copy this incase c == this! XD
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, amount);
			
			if (c instanceof List)
			{
				listcopy((List)c, 0, this, index, amount);
			}
			else
			{
				if (c instanceof DoubleCollection)
				{
					SimpleDoubleIterator iterator = ((DoubleCollection)c).newSimpleDoubleIterator();
					
					int i = 0;
					while (true)
					{
						double e;
						try
						{
							e = iterator.nextrpDouble();
						}
						catch (StopIterationReturnPath rp)
						{
							break;
						}
						
						this.setDouble(index+i, e);
						i++;
					}
				}
				else
				{
					int i = 0;
					for (Double e : c)
					{
						this.set(index+i, e);
						i++;
					}
				}
			}
			
			return true;
		}
		
		@Override
		public default void setSizeDouble(int newSize, double elementToAddIfGrowing)
		{
			int oldSize = this.size();
			int amount = newSize - oldSize;
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, oldSize, amount);
			
			//Growing results in *undefined* contents, not necessarily initialized ones!  (eg, it may already have some/enough elements allocated eg, in an underlying array and it just increases the int size field :3 )
			for (int i = oldSize; i < newSize; i++)
				this.setDouble(i, elementToAddIfGrowing);
		}
		
		
		
		
		@Override
		public default void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			rangeCheckInterval(this.size(), start, pastEnd);
			
			int length = pastEnd - start;
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, pastEnd, -length);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class DoubleSublist
	implements DoubleList, DefaultShiftingBasedDoubleList, Sublist<Double>, ShiftableList
	{
		protected final DoubleList underlying;
		protected final int start;
		protected int size;
		
		public DoubleSublist(DoubleList underlying, int start, int initialSize)
		{
			rangeCheckIntervalByLength(underlying.size(), start, initialSize);
			
			this.underlying = underlying;
			this.start = start;
			this.size = initialSize;
		}
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public double getDouble(int index)
		{
			return underlying.getDouble(index + start);
		}
		
		@Override
		public void setDouble(int index, double value)
		{
			underlying.setDouble(index + start, value);
		}
		
		@Override
		public void shiftRegionStretchingFromIndexToEndByAmountChangingSize(int start, int amount)
		{
			rangeCheckCursorPoint(size(), start);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(underlying, start, amount);
			
			size += amount;
		}
		
		
		
		@Override
		public DoubleList clone()
		{
			DoubleList c = new DoubleArrayList();
			c.addAll(this);
			return c;
		}
		
		
		
		@Override
		public DoubleList getUnderlying()
		{
			return underlying;
		}
		
		@Override
		public int getSublistStartingIndex()
		{
			return start;
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class DoubleArrayList
	implements DefaultShiftingBasedDoubleList, ListWithSetSize<Double>, Trimmable, TransparentContiguousArrayBackedCollection<double[]>, KnowsLengthFixedness
	{
		protected int size = 0;
		protected double[] data;
		protected GrowerComputationallyUnreducedPurelyRecursive grower;
		
		
		
		
		//In case subclasses don't want to deal with the other things ^^'
		protected DoubleArrayList(Void thisiswhyyoushouldntusejavaconstructorsAlthoughIdontknowhowtomakeinheritanceworkwellwithoutinitmethodsorfactoriesorsomething)
		{
		}
		
		
		/**
		 * Capacity increment is different from Vector's.  It is the percentage of the capacity by which the capacity is increased.
		 * The formula being: <code>newCapacity = capacity + (capacity * capacityIncrement)</code><br>
		 * @param grower This can be anything.  The only downside is performance: all methods will add at least the minimum amount required regardless of what this specifies.
		 */
		public DoubleArrayList(int initialCapacity, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this.data = initialCapacity == 0 ? ArrayUtilities.EmptyDoubleArray : new double[initialCapacity];
			this.grower = grower;
		}
		
		public DoubleArrayList(@LiveValue @WritableValue double[] LIVEBACKING, int size, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this.data = LIVEBACKING;
			this.grower = grower;
			this.size = size;
		}
		
		public DoubleArrayList(@SnapshotValue @ReadonlyValue Collection<Double> c, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this(c.size(), grower);
			this.addAll(c);
		}
		
		
		
		
		
		public DoubleArrayList()
		{
			this(DefaultPrimitiveArrayListInitialCapacity);
		}
		
		public DoubleArrayList(int initialCapacity)
		{
			this(initialCapacity, defaultPrimitiveArrayListGrower(initialCapacity));
		}
		
		public DoubleArrayList(@SnapshotValue @ReadonlyValue Collection<Double> c)
		{
			this(c, defaultPrimitiveArrayListGrower(c.size()));
		}
		
		public DoubleArrayList(@LiveValue @WritableValue double[] LIVEBACKING, int size)
		{
			this(LIVEBACKING, size, defaultPrimitiveArrayListGrower(LIVEBACKING.length));
		}
		
		public DoubleArrayList(@LiveValue @WritableValue double[] LIVEBACKING)
		{
			this(LIVEBACKING, LIVEBACKING.length);
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return false;
		}
		
		
		
		@Override
		public void setSize(int newSize)
		{
			ensureCapacity(newSize);
			this.size = newSize;
			
			// :D
		}
		
		
		
		
		
		//This pattern is nicer for subclassing in any Java class! :D
		protected void setupClone(DoubleArrayList freshClone)
		{
			freshClone.size = this.size;
			
			freshClone.data = new double[size];
			System.arraycopy(this.data, 0, freshClone.data, 0, size);
			
			freshClone.grower = this.grower;
		}
		
		@Override
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public DoubleArrayList clone()
		{
			DoubleArrayList clone = new DoubleArrayList();
			this.setupClone(clone);
			return clone;
		}
		
		
		
		
		
		
		
		
		
		
		//This is what unlocks all the System.arraycopy() magic!  8>
		@ImplementationTransparency
		@LiveValue
		public Slice<double[]> getLiveContiguousArrayBackingUNSAFE()
		{
			return new Slice(data, 0, size);
		}
		
		
		
		
		
		
		
		
		
		public void setDouble(int index, double value)
		{
			rangeCheckMember(this.size, index);
			data[index] = value;
		}
		
		public double getDouble(int index)
		{
			rangeCheckMember(this.size(), index);
			return data[index];
		}
		
		
		
		
		
		
		
		@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public void clear()
		{
			size = 0;
		}
		
		@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public int size()
		{
			return size;
		}
		
		@ImplementationTransparency
		public void trimToSize()
		{
			double[] newdata = new double[size];
			System.arraycopy(data, 0, newdata, 0, size);
			this.data = newdata;
		}
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.trimToSize();
			return TrimmableTrimRV.DontKeepInvoking;
		}
		
		
		
		
		
		@ImplementationTransparency
		public int getCapacity()
		{
			return data.length;
		}
		
		@ImplementationTransparency
		public void ensureCapacity(int minCapacity)
		{
			if (data.length < minCapacity)
			{
				//Enlarge
				int newCapacity = getNewCapacity(minCapacity);
				double[] newData = new double[newCapacity];
				System.arraycopy(data, 0, newData, 0, data.length);
				this.data = newData;
			}
		}
		
		/**
		 * This is also guaranteed not to return a capacity smaller than the provided minimum.
		 */
		@ImplementationTransparency
		public int getNewCapacity(int minCapacity)
		{
			if (minCapacity < 0)
				throw new IllegalArgumentException();
			
			return Math.max(minCapacity, grower.getNewSizeRecursive(data.length));  //The Math.max(min, ...) ensures that the grower can't break anything!  :3
		}
		
		
		
		
		
		
		
		/**
		 * sets the underlying array that backs the array list.
		 * Be careful, though, as updates can cause a new array to be created, obsoleting this one.
		 * Note, also, that only up to {@link #size()} elements will be used.
		 */
		@ImplementationTransparency
		public void setDirectBuffer(@LiveValue double[] array)
		{
			if (array.length < size)
				throw new IllegalArgumentException("Array is too small; size="+size+", capacity="+array.length);
			
			this.data = array;
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static class FixedLengthArrayWrapperDoubleList
	implements DoubleList, TransparentContiguousArrayBackedCollection<double[]>, KnowsLengthFixedness
	{
		protected final double[] underlying;
		protected final int offset, length;
		
		public FixedLengthArrayWrapperDoubleList(double[] underlying, int offset, int length)
		{
			rangeCheckIntervalByLength(underlying.length, offset, length);
			
			this.underlying = underlying;
			this.offset = offset;
			this.length = length;
		}
		
		public FixedLengthArrayWrapperDoubleList(double[] underlying)
		{
			this(underlying, 0, underlying.length);
		}
		
		public FixedLengthArrayWrapperDoubleList(Slice<double[]> underlying)
		{
			this(underlying.getUnderlying(), underlying.getOffset(), underlying.getLength());
		}
		
		@Override
		public DoubleList clone()
		{
			return new FixedLengthArrayWrapperDoubleList(toDoubleArray());
		}
		
		
		
		@Override
		public double[] toDoubleArray()
		{
			return sliceToNewDoubleArrayOP(this.getLiveContiguousArrayBackingUNSAFE());
		}
		
		@Override
		public Slice<double[]> getLiveContiguousArrayBackingUNSAFE()
		{
			return new Slice<>(underlying, offset, length);
		}
		
		@Override
		public int size()
		{
			return length;
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return true;
		}
		
		
		
		@Override
		public SimpleDoubleIterator newSimpleDoubleIterator()
		{
			return new SimpleDoubleIterator()
			{
				int index = 0;
				
				public double nextrpDouble() throws StopIterationReturnPath
				{
					int i = index;
					
					if (i >= size())
					{
						throw StopIterationReturnPath.I;
					}
					else
					{
						index = i + 1;
						return underlying[offset+i];
					}
				}
			};
		}
		
		@Override
		public DoubleList subList(int fromIndex, int toIndex)
		{
			rangeCheckInterval(this.size(), fromIndex, toIndex);
			return new FixedLengthArrayWrapperDoubleList(this.underlying, fromIndex+offset, toIndex-fromIndex);
		}
		
		@Override
		public double getDouble(int index)
		{
			return underlying[offset + index];
		}
		
		@Override
		public void setDouble(int index, double value)
		{
			underlying[offset + index] = value;
		}
		
		
		
		
		
		
		
		
		
		//Unallowed Dim-altering Methods!
		
		@Override
		public void clear()
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setSizeDouble(int newSize, double elementToAddIfGrowing)
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void insertDouble(int index, double value)
		{
			throw new UnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	@ReadonlyValue
	public static DoubleList doubleArrayAsList(@LiveValue @WritableValue double[] array, int offset, int length)
	{
		return new FixedLengthArrayWrapperDoubleList(array, offset, length);
	}
	
	@ReadonlyValue
	public static DoubleList doubleArrayAsList(@LiveValue @WritableValue double[] array)
	{
		return doubleArrayAsList(array, 0, array.length);
	}
	
	@ReadonlyValue
	public static DoubleList doubleArrayAsList(@LiveValue @WritableValue Slice<double[]> arraySlice)
	{
		return doubleArrayAsList(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	
	
	
	@ThrowAwayValue
	public static DoubleList doubleArrayAsMutableList(@SnapshotValue @ReadonlyValue double[] array, int offset, int length)
	{
		return new DoubleArrayList(new FixedLengthArrayWrapperDoubleList(array, offset, length));
	}
	
	@ThrowAwayValue
	public static DoubleList doubleArrayAsMutableList(@SnapshotValue @ReadonlyValue double[] array)
	{
		return doubleArrayAsMutableList(array, 0, array.length);
	}
	
	@ThrowAwayValue
	public static DoubleList doubleArrayAsMutableList(@SnapshotValue @ReadonlyValue Slice<double[]> arraySlice)
	{
		return doubleArrayAsMutableList(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	@Immutable
	public static class ImmutableDoubleArrayList
	implements Serializable, Comparable<ImmutableDoubleArrayList>, DoubleList, KnowsLengthFixedness     //Let's not implement the general interfaces for accessing the underlying data jussssst to help make sure anyone who accesses it *knows* it's meant to be VERY MUCH READONLY!  XD'''
	{
		private static final long serialVersionUID = 1L;
		
		
		protected final double[] data;
		
		//Yeah I know it's bad practice for a thread-safe immutable object to cache things, but at best effort is wasted, no constraints are violated!     (If I'm wrong, fire me X'D )
		protected transient long hashCode;     //we cache it in case the doublearray is large :33
		
		protected ImmutableDoubleArrayList(double[] LIVEDATA)
		{
			if (LIVEDATA == null) throw new NullPointerException();
			this.data = LIVEDATA;
		}
		
		
		
		
		
		/**
		 * DO NOT *EVER* MODIFY THE ARRAY YOU PASSED HERE AFTER CALLING THIS X"D
		 * ( THE HASH CODE IS CACHED FOR ONE, AND THIS MAY BE EMBEDDED IN HASHMAPS--THIS IS SUPPOSED TO BE IMMUTABLE AFTER ALL X'DDD )
		 * @see #getREADONLYLiveWholeArrayBackingUNSAFE()
		 */
		@ImplementationTransparency
		public static ImmutableDoubleArrayList newLIVE(@LiveValue double[] LIVEDATA)
		{
			return new ImmutableDoubleArrayList(LIVEDATA);
		}
		
		
		@ImplementationTransparency
		public static ImmutableDoubleArrayList newSingleton(double singleMember)
		{
			return new ImmutableDoubleArrayList(new double[]{singleMember});
		}
		
		
		
		
		public static ImmutableDoubleArrayList newCopying(@SnapshotValue List<Double> data)
		{
			if (data instanceof ImmutableDoubleArrayList)
				return (ImmutableDoubleArrayList)data;
			
			if (TransparentContiguousArrayBackedCollection.is(data))
			{
				Slice<Object> s = ((TransparentContiguousArrayBackedCollection)data).getLiveContiguousArrayBackingUNSAFE();
				if (s.getUnderlying() instanceof double[])
				{
					return newCopying((Slice)s);
				}
			}
			
			if (data instanceof DoubleList)
			{
				return newLIVE(((DoubleList)data).toDoubleArray());
			}
			else
			{
				int n = data.size();
				double[] a = new double[n];
				
				int i = 0;
				for (Double e : data)
				{
					if (i >= n)
						throw new ImpossibleException();
					
					a[i] = e;
					i++;
				}
				
				if (i != n)
					throw new ImpossibleException();
				
				return newLIVE(a);
			}
		}
		
		public static ImmutableDoubleArrayList newCopying(@SnapshotValue double[] data)
		{
			return new ImmutableDoubleArrayList(data.clone());
		}
		
		public static ImmutableDoubleArrayList newCopying(@SnapshotValue double[] data, int offset, int length)
		{
			if (offset == 0 && length == data.length)
				return newCopying(data);  //who knows, double[].clone() might be faster! \o/   (it certainly shouldn't be slower!!)
			else
			{
				double[] newArray = new double[length];
				System.arraycopy(data, offset, newArray, 0, length);
				return new ImmutableDoubleArrayList(newArray);
			}
		}
		
		public static ImmutableDoubleArrayList newCopying(@SnapshotValue Slice<double[]> data)
		{
			return newCopying(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		
		
		
		
		public static ImmutableDoubleArrayList newLIVEOrCopying(@PossiblySnapshotPossiblyLiveValue double[] data, int offset, int length)
		{
			if (offset == 0 && length == data.length)
				return newLIVE(data);
			else
				return newCopying(data, offset, length);
		}
		
		public static ImmutableDoubleArrayList newLIVEOrCopying(@PossiblySnapshotPossiblyLiveValue Slice<double[]> data)
		{
			return newLIVEOrCopying(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		
		
		
		
		
		
		public static final ImmutableDoubleArrayList Empty = ImmutableDoubleArrayList.newLIVE(ArrayUtilities.EmptyDoubleArray);
		
		
		
		
		
		
		
		
		@ThrowAwayValue
		@Override
		public double[] toDoubleArray()
		{
			return data.clone();
		}
		
		/**
		 * DO NOT MODIFY THIS X"D
		 * ( THE HASH CODE IS CACHED FOR ONE, AND THIS MAY BE EMBEDDED IN HASHMAPS--THIS IS SUPPOSED TO BE IMMUTABLE AFTER ALL X'DDD )
		 */
		@ImplementationTransparency
		@LiveValue
		@ReadonlyValue //!!!!
		public double[] getREADONLYLiveWholeArrayBackingUNSAFE()
		{
			return data;
		}
		
		@Override
		public int size()
		{
			return data.length;
		}
		
		@Override
		public double getDouble(int index)
		{
			return data[index];
		}
		
		@Override
		public void getAllDoubles(int offsetInThisSource, @WritableValue double[] dest, int offsetInDest, int lengthInBoth)
		{
			System.arraycopy(this.data, offsetInThisSource, dest, offsetInDest, lengthInBoth);
		}
		
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return true;
		}
		
		
		
		
		@Override
		public int hashCode()
		{
			long hashCodeField = this.hashCode;
			
			boolean cached = hashCodeField != 0;
			
			
			if (cached)
			{
				return (int)this.hashCode;
			}
			else
			{
				int hashCode = Arrays.hashCode(this.data);
				this.hashCode = hashCode | 0xFFFF_FFFF_0000_0000l;  //hashCodes can be zero!  Not often XDD   But can be!  So it's not correct unless we account for that :>      (And if it was two fields then it'd be a concurrency bug!)
				return hashCode;
			}
		}
		
		
		
		
		@Override
		public boolean equals(@ReadonlyValue Object obj)
		{
			if (obj == this)
			{
				return true;
			}
			else if (obj instanceof ImmutableDoubleArrayList)
			{
				ImmutableDoubleArrayList other = (ImmutableDoubleArrayList)obj;
				
				//Checking the hashcodes is a VERY fast negative test and is almost always true-negative!! :DDD   (rarely it's false-positive, but never false-negative!)
				
				long thisHC = this.hashCode;
				long otherHC = other.hashCode;
				
				boolean thisHashCodeCached = thisHC != 0;
				boolean otherHashCodeCached = otherHC != 0;
				
				int thisHashCode = (int)thisHC;
				int otherHashCode = (int)otherHC;
				
				if (thisHashCodeCached)
				{
					if (otherHashCodeCached)
					{
						if (thisHashCode != otherHashCode)
							return false;
					}
					else
					{
						//Scanning one double array is faster than scanning two! :D
						if (thisHashCode != other.hashCode())
							return false;
					}
				}
				else
				{
					if (otherHashCodeCached)
					{
						//Scanning one double array is faster than scanning two! :D
						if (this.hashCode() != otherHashCode)
							return false;
					}
					else
					{
						//Then don't make any hash codes X3
					}
				}
				
				return this.equalsDoubleArray(other.data);
			}
			else
			{
				return false;
			}
		}
		
		
		@Override
		public int compareTo(@ReadonlyValue ImmutableDoubleArrayList other)
		{
			return this.compareToDoubleArray(other.data);
		}
		
		
		public boolean equalsDoubleArray(@ReadonlyValue double[] o)
		{
			double[] ourData = this.getREADONLYLiveWholeArrayBackingUNSAFE();
			double[] theirData = o;
			
			return Arrays.equals(ourData, theirData);
		}
		
		
		public int compareToDoubleArray(@ReadonlyValue double[] o)
		{
			double[] ourData = this.getREADONLYLiveWholeArrayBackingUNSAFE();
			double[] theirData = o;
			
			return ArrayUtilities.compareLittleEndianLengthsFirst(ourData, theirData);  //lengths-first is more efficient if they're different but hardly any less efficient if they're always the same!  A good default, don't you think? :D
		}
		
		
		
		
		@Override
		public ImmutableDoubleArrayList clone()
		{
			return this;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		// Unallowed Write Methods!
		
		@Override
		public void clear()
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setSizeDouble(int newSize, double elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void insertDouble(int index, double value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setDouble(int index, double value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static DoubleList unmodifiableDoubleList(DoubleList doubleList)
	{
		return doubleList instanceof UnmodifiableDoubleListWrapper ? doubleList : new UnmodifiableDoubleListWrapper(doubleList);
	}
	
	public static class UnmodifiableDoubleListWrapper
	implements DoubleList, KnowsLengthFixedness, TransparentContiguousArrayBackedCollection
	{
		protected final DoubleList underlying;
		
		public UnmodifiableDoubleListWrapper(DoubleList underlying)
		{
			this.underlying = underlying;
		}
		
		@ImplementationTransparency
		public DoubleList getUnderlying()
		{
			return underlying;
		}
		
		
		
		
		
		@Override
		public UnmodifiableDoubleListWrapper clone()
		{
			return new UnmodifiableDoubleListWrapper(underlying.clone());
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.underlying);
		}
		
		@Override
		public boolean isTransparentContiguousArrayBackedCollection()
		{
			return TransparentContiguousArrayBackedCollection.is(underlying);
		}
		
		@Override
		public Slice getLiveContiguousArrayBackingUNSAFE()
		{
			return ((TransparentContiguousArrayBackedCollection)underlying).getLiveContiguousArrayBackingUNSAFE();
		}
		
		
		
		
		
		
		//Use default so they can't get around our readonly-ness!  ;D
		//	public Iterator<Double> iterator()
		//	public ListIterator<Double> listIterator()
		//	public ListIterator<Double> listIterator(int index)
		//	public SimpleIterator<Double> simpleIterator()
		//	public SimpleDoubleIterator newSimpleDoubleIterator()
		//	public DoubleList subList(int fromIndex, int toIndex)
		
		
		
		
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeRangeByLength(int start, int length) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void replaceAll(UnaryOperator<Double> operator)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeIf(Predicate<? super Double> filter)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void sort(Comparator<? super Double> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void clear()
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public Double remove(int index)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllDoubles(double[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllDoubles(Slice<double[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllDoubles(double[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllDoubles(Slice<double[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllDoubles(double[] a, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setDouble(int index, double value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertDouble(int index, double value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSizeDouble(int newSize, double elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSize(int newSize, Double elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSize(int newSize)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllDoubles(int index, double[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllDoubles(int index, Slice<double[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllDoubles(int index, double[] array, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAll(int destIndex, List sourceU, int sourceIndex, int amount) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addDouble(double value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll(Collection<? extends Double> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllDoubles(double[] elements, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeByIndex(int index)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public double removeDoubleByIndex(int index) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeDouble(double value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean retainAll(Collection<?> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllDoubles(int index, double[] array, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll(int index, Collection<? extends Double> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllDoubles(int index, double[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllDoubles(int index, Slice<double[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public Double set(int index, Double value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void add(int index, Double value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeAll(Collection<?> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean remove(Object o)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean add(Double e)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		
		
		
		
		
		
		
		
		
		public void forEach(Consumer<? super Double> action)
		{
			underlying.forEach(action);
		}
		
		public int size()
		{
			return underlying.size();
		}
		
		public boolean equals(Object o)
		{
			return underlying.equals(o);
		}
		
		public int hashCode()
		{
			return underlying.hashCode();
		}
		
		public Spliterator<Double> spliterator()
		{
			return underlying.spliterator();
		}
		
		public Stream<Double> stream()
		{
			return underlying.stream();
		}
		
		public Stream<Double> parallelStream()
		{
			return underlying.parallelStream();
		}
		
		public double getDouble(int index)
		{
			return underlying.getDouble(index);
		}
		
		public int indexOfDouble(double value)
		{
			return underlying.indexOfDouble(value);
		}
		
		public int lastIndexOfDouble(double value)
		{
			return underlying.lastIndexOfDouble(value);
		}
		
		public boolean equivalent(List<Double> other)
		{
			return underlying.equivalent(other);
		}
		
		public double[] toDoubleArray()
		{
			return underlying.toDoubleArray();
		}
		
		public boolean containsDouble(double value)
		{
			return underlying.containsDouble(value);
		}
		
		public Double get(int index)
		{
			return underlying.get(index);
		}
		
		public int indexOf(Object o)
		{
			return underlying.indexOf(o);
		}
		
		public int lastIndexOf(Object o)
		{
			return underlying.lastIndexOf(o);
		}
		
		public boolean containsAll(Collection<?> c)
		{
			return underlying.containsAll(c);
		}
		
		public boolean isEmpty()
		{
			return underlying.isEmpty();
		}
		
		public boolean contains(Object o)
		{
			return underlying.contains(o);
		}
		
		public Object[] toArray()
		{
			return underlying.toArray();
		}
		
		public <T> T[] toArray(T[] a)
		{
			return underlying.toArray(a);
		}
		
		public void storeBoxingIntoArray(Slice<Object[]> array)
		{
			underlying.storeBoxingIntoArray(array);
		}
		
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class DoubleTable
	{
		protected int width;
		protected double[] data;
		
		public DoubleTable(int width, int height)
		{
			if (width < 0)
				throw new IllegalArgumentException();
			if (height < 0)
				throw new IllegalArgumentException();
			
			data = new double[width * height];
		}
		
		
		
		@Nonnegative
		public int getNumberOfColumns()
		{
			return width;
		}
		
		@Nonnegative
		public int getNumberOfRows()
		{
			return data.length / width;
		}
		
		
		@Nonnegative
		public int getNumberOfCells()
		{
			return data.length;
		}
		
		
		
		
		
		public double getCellContentsDouble(int columnIndex, int rowIndex) throws IndexOutOfBoundsException
		{
			if (columnIndex < 0) throw new IndexOutOfBoundsException();
			if (rowIndex < 0) throw new IndexOutOfBoundsException();
			if (columnIndex >= getNumberOfColumns()) throw new IndexOutOfBoundsException();
			if (rowIndex >= getNumberOfRows()) throw new IndexOutOfBoundsException();
			
			return this.data[rowIndex * this.getNumberOfColumns() + columnIndex];
		}
		
		public void setCellContentsDouble(int columnIndex, int rowIndex, @Nullable double newValue) throws IndexOutOfBoundsException
		{
			if (columnIndex < 0) throw new IndexOutOfBoundsException();
			if (rowIndex < 0) throw new IndexOutOfBoundsException();
			if (columnIndex >= getNumberOfColumns()) throw new IndexOutOfBoundsException();
			if (rowIndex >= getNumberOfRows()) throw new IndexOutOfBoundsException();
			
			this.data[rowIndex * this.getNumberOfColumns() + columnIndex] = newValue;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Nullable
		public Double getCellContents(int columnIndex, int rowIndex) throws IndexOutOfBoundsException
		{
			return getCellContentsDouble(columnIndex, rowIndex);
		}
		
		public void setCellContents(int columnIndex, int rowIndex, @Nullable Double newValue) throws IndexOutOfBoundsException
		{
			setCellContentsDouble(columnIndex, rowIndex, newValue);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static long getLong(List<Long> list, int index)
	{
		if (list instanceof LongList)
			return ((LongList) list).getLong(index);
		else
			return list.get(index);
	}
	
	public static void setLong(List<Long> list, int index, long value)
	{
		if (list instanceof LongList)
			((LongList) list).setLong(index, value);
		else
			list.set(index, value);
	}
	
	
	
	
	
	
	
	public static interface SimpleLongIterable
	extends SimpleIterable<Long>
	{
		public SimpleLongIterator newSimpleLongIterator();
		
		public default SimpleIterator<Long> simpleIterator()
		{
			SimpleLongIterator i = this.newSimpleLongIterator();
			return () -> i.nextrpLong();
		}
	}
	
	
	public static interface SimpleLongIterator
	extends SimpleIterator<Long>
	{
		public long nextrpLong() throws StopIterationReturnPath;
		
		
		@Override
		public default Long nextrp() throws StopIterationReturnPath
		{
			return nextrpLong();
		}
	}
	
	
	
	@SignalInterface
	public static interface LongCollection
	extends PrimitiveCollection<Long>, SimpleLongIterable
	{
		public boolean addLong(long value);
		
		public boolean removeLong(long value);
		
		
		
		
		
		@ThrowAwayValue
		public static long[] defaultToLongArray(LongCollection collection)
		{
			long[] array = new long[collection.size()];
			SimpleLongIterator i = collection.newSimpleLongIterator();
			
			int index = 0;
			while (true)
			{
				try
				{
					array[index] = i.nextrpLong();
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
				
				index++;
			}
			
			return array;
		}
		
		@ThrowAwayValue
		public default long[] toLongArray()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(this.size(), u);
				
				if (u.getUnderlying() instanceof long[])
				{
					return sliceToNewLongArrayOP((Slice<long[]>) u);
				}
			}
			
			return defaultToLongArray(this);
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public default long[] toLongArrayPossiblyLive()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				int size = this.size();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				Object und = u.getUnderlying();
				
				if (und instanceof long[] && u.getOffset() == 0 && ((long[])und).length == size)
				{
					return (long[])und;
				}
			}
			
			return defaultToLongArray(this);
		}
		
		@PossiblySnapshotPossiblyLiveValue
		public default Slice<long[]> toLongArraySlicePossiblyLive()
		{
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(this.size(), u);
				
				if (u.getUnderlying() instanceof long[])
				{
					return (Slice<long[]>) u;
				}
			}
			
			return wholeArraySliceLong(defaultToLongArray(this));
		}
		
		
		
		
		
		
		public default boolean containsLong(long value)
		{
			SimpleLongIterator i = newSimpleLongIterator();
			
			while (true)
			{
				try
				{
					if (eqSane(i.nextrpLong(), value))
						return true;
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
			}
			
			return false;
		}
		
		
		
		
		
		
		@Override
		public default Iterator<Long> iterator()
		{
			return SimpleLongIterable.super.iterator();
		}
		
		@Override
		public default boolean isEmpty()
		{
			return size() == 0;
		}
		
		
		
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllLongs(long[] array)
		{
			return addAllLongs(array, 0, array.length);
		}
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllLongs(Slice<long[]> arraySlice)
		{
			return addAllLongs(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public default boolean addAllLongs(long[] elements, int offset, int length)
		{
			return defaultAddAllLongs(this, elements, offset, length);
		}
		
		public static boolean defaultAddAllLongs(LongCollection self, long[] elements, int offset, int length)
		{
			boolean changedAtAll = false;
			int e = offset + length;
			for (int i = offset; i < e; i++)
				changedAtAll |= self.addLong(elements[i]);
			return changedAtAll;
		}
		
		
		
		
		public default void removeAllLongs(long[] array)
		{
			removeAllLongs(array, 0, array.length);
		}
		
		public default void removeAllLongs(Slice<long[]> arraySlice)
		{
			removeAllLongs(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public default void removeAllLongs(long[] a, int offset, int length)
		{
			int e = offset + length;
			for (int i = offset; i < e; i++)
				this.removeLong(a[i]);
		}
		
		
		
		
		
		
		
		
		
		
		@Override
		public default boolean contains(Object o)
		{
			return o instanceof Long && containsLong((Long)o);
		}
		
		
		@Override
		public default boolean add(Long e)
		{
			return addLong(e);
		}
		
		
		@Override
		public default boolean remove(Object o)
		{
			return o instanceof Long && removeLong((Long)o);
		}
		
		
		@Override
		public default boolean containsAll(Collection<?> c)
		{
			if (c instanceof LongCollection)
			{
				LongCollection cc = (LongCollection) c;
				
				SimpleLongIterator i = cc.newSimpleLongIterator();
				while (true)
				{
					try
					{
						if (this.containsLong(i.nextrpLong()))
							return true;
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return false;
			}
			else
			{
				for (Object e : c)
					if (this.contains(e))
						return true;
				return false;
			}
		}
		
		
		@Override
		public default boolean addAll(Collection<? extends Long> c)
		{
			if (c instanceof LongCollection)
			{
				boolean changedAtAll = false;
				
				SimpleLongIterator i = ((LongCollection)c).newSimpleLongIterator();
				
				while (true)
				{
					try
					{
						changedAtAll |= this.add(i.nextrpLong());
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (Long e : c)
				{
					changedAtAll |= this.add(e);
				}
				
				return changedAtAll;
			}
		}
		
		
		
		@Override
		public default boolean removeAll(Collection<?> c)
		{
			if (c instanceof LongCollection)
			{
				boolean changedAtAll = false;
				
				SimpleLongIterator i = ((LongCollection)c).newSimpleLongIterator();
				
				while (true)
				{
					try
					{
						changedAtAll |= this.remove(i.nextrpLong());
					}
					catch (StopIterationReturnPath exc)
					{
						break;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (Object e : c)
				{
					changedAtAll |= this.remove(e);
				}
				
				return changedAtAll;
			}
		}
		
		
		
		public default String _toString()
		{
			StringBuilder b = new StringBuilder("[");
			
			SimpleLongIterator i = newSimpleLongIterator();
			
			boolean first = true;
			
			while (true)
			{
				long e;
				
				try
				{
					e = i.nextrpLong();
				}
				catch (StopIterationReturnPath exc)
				{
					break;
				}
				
				
				
				//Notice this comes after the possible 'break'!  ^_~
				if (first)
					first = false;
				else
					b.append(", ");
				
				b.append(e);
			}
			
			b.append(']');
			
			return b.toString();
		}
	}
	
	
	
	public static interface DefaultToArraysLongCollection
	extends LongCollection
	{
		@Override
		public default Object[] toArray()
		{
			Object[] a = new Object[size()];
			storeBoxingIntoArray(wholeArraySliceReference(a));
			return a;
		}
		
		@Override
		public default <T> T[] toArray(T[] a)
		{
			if (a.length >= this.size())
			{
				storeBoxingIntoArray(wholeArraySliceReference(a));
				return a;
			}
			else
			{
				Object[] aa;
				
				if (a.getClass() == Long[].class)
				{
					aa = new Long[size()];
				}
				else if (a.getClass() == Object[].class)
				{
					aa = new Object[size()];
				}
				else
				{
					throw new StructuredClassCastException(a.getClass());
				}
				
				storeBoxingIntoArray(wholeArraySliceReference(aa));
				
				return (T[])aa;
			}
		}
		
		public void storeBoxingIntoArray(Slice<Object[]> array);
	}
	
	
	
	
	
	
	@SignalInterface
	public static interface LongListRO
	{
		public long getLong(int index);
		
		public int size();
		
		
		
		public default int indexOfLong(long value)
		{
			int n = this.size();
			for (int i = 0; i < n; i++)
				if (eqSane(this.getLong(i), value))
					return i;
			return -1;
		}
		
		public default int lastIndexOfLong(long value)
		{
			int i = this.size()-1;
			while (i >= 0)
			{
				if (eqSane(this.getLong(i), value))
					return i;
				break;
			}
			return i;   // ;D
		}
		
		
		
		public default boolean equivalent(List<Long> other)
		{
			int size = this.size();
			if (other.size() != size)
				return false;
			
			
			if (other instanceof LongListRO)  //All LongLists will implement this interface too so this accounts for them as well :3
			{
				return equivalentFixedRO((LongListRO)other);
			}
			else
			{
				for (int i = 0; i < size; i++)
				{
					if (!eqSane(this.getLong(i), other.get(i).longValue()))
						return false;
				}
			}
			
			return true;
		}
		
		
		
		public default boolean equivalentFixedRO(LongListRO other)
		{
			int size = this.size();
			if (other.size() != size)
				return false;
			
			for (int i = 0; i < size; i++)
			{
				if (!eqSane(this.getLong(i), other.getLong(i)))
					return false;
			}
			
			return true;
		}
	}
	
	
	
	@SignalInterface
	public static interface LongListRWFixed
	extends LongListRO
	{
		public void setLong(int index, long value);
	}
	
	
	
	@SignalInterface
	public static interface LongList
	extends LongCollection, NiceList<Long>, LongListRO, LongListRWFixed, ListWithRemoveRange, DefaultToArraysLongCollection, PubliclyCloneable, ListWithSetSize<Long>, ListWithSetAll, NonuniformMethodsForLongList
	{
		public void insertLong(int index, long value);
		
		
		
		/**
		 * This should be equivalent to just constructing and returning a new in-memory stock implementation with the same contents  (but can easily be faster, especially for those stock impl.s! XD )
		 * As for whether it's fixed-length or read-only or duplicateless or etc., that should be the same in the clone as it is in the main impl.
		 * If *and only if* it is immutable, this is allowed to return this same instance!  (ie the body of this method being <code>return this;</code>)
		 */
		public LongList clone();
		
		
		/**
		 * If size is smaller than the current, then this is equivalent to {@link #removeRange(int, int) removeRange}(newSize, this.{@link #size() size()})  :3
		 * If size is larger, then this appends newSize - this.{@link #size() size()} elementToAddIfGrowing's to the end like that many {@link #addLong(long)}  :D
		 */
		public void setSizeLong(int newSize, long elementToAddIfGrowing);
		
		public default void setSize(int newSize, Long elementToAddIfGrowing)
		{
			setSizeLong(newSize, elementToAddIfGrowing);
		}
		
		public default void setSize(int newSize)
		{
			setSizeLong(newSize, 0l);
		}
		
		
		
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void setAllLongs(int index, long[] array)
		{
			setAllLongs(index, array, 0, array.length);
		}
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void setAllLongs(int index, Slice<long[]> arraySlice)
		{
			setAllLongs(index, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		public default void setAllLongs(int start, long[] array, int offset, int length)
		{
			int size = this.size();
			
			rangeCheckIntervalByLength(size, start, length);
			rangeCheckIntervalByLength(array.length, offset, length);
			
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(size, start, length);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				if (u.getUnderlying() instanceof long[])
				{
					Slice<long[]> s = (Slice<long[]>) u;
					
					System.arraycopy(array, offset, s.getUnderlying(), s.getOffset()+start, length);
					
					return;
				}
			}
			
			
			defaultSetAllLongs(this, start, array, offset, length);
		}
		
		
		public static void defaultSetAllLongs(LongList list, int start, @WritableValue long[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				list.setLong(start + i, array[offset + i]);
		}
		
		
		
		
		
		@Override
		public default void setAll(int destIndex, List sourceU, int sourceIndex, @Nonnegative int amount) throws IndexOutOfBoundsException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			List<Long> source = sourceU;
			LongList dest = this;
			
			int sourceSize = source.size();
			int destSize = dest.size();
			rangeCheckIntervalByLength(sourceSize, sourceIndex, amount);
			rangeCheckIntervalByLength(destSize, destIndex, amount);
			
			
			
			if (TransparentContiguousArrayBackedCollection.is(source))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection<?>)source).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(sourceSize, sourceIndex, amount);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(sourceSize, u);
				
				if (u.getUnderlying() instanceof long[])
				{
					Slice<long[]> s = (Slice<long[]>)u;
					this.setAllLongs(destIndex, s.subslice(sourceIndex, amount));
					return;
				}
			}
			
			
			
			
			if (source instanceof LongList)
			{
				LongList primSource = (LongList) source;
				
				if (destIndex < sourceIndex)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
				{
					for (int i = 0; i < amount; i++)
						dest.setLong(destIndex+i, primSource.getLong(sourceIndex+i));
				}
				else
				{
					for (int i = amount-1; i >= 0; i--)
						dest.setLong(destIndex+i, primSource.getLong(sourceIndex+i));
				}
			}
			else
			{
				if (destIndex < sourceIndex)  //do it safely always (even if source != dest) just in case, say, the source and dest are actually views of the same array or something!  (even though this isn't proper even doing it this way since they could be using different offsets ^^''')
				{
					for (int i = 0; i < amount; i++)
						dest.set(destIndex+i, source.get(sourceIndex+i));
				}
				else
				{
					for (int i = amount-1; i >= 0; i--)
						dest.set(destIndex+i, source.get(sourceIndex+i));
				}
			}
		}
		
		
		
		
		
		
		
		/**
		 * Copies <code>array.length</code> elements into <code>array</code> starting with the <code>start</code>th element.<br>
		 */
		public default void getAllLongs(int start, @WritableValue long[] array, int offset, int length)
		{
			int size = this.size();
			
			rangeCheckIntervalByLength(size, start, length);
			rangeCheckIntervalByLength(array.length, offset, length);
			
			if (TransparentContiguousArrayBackedCollection.is(this))
			{
				Slice<?> u = ((TransparentContiguousArrayBackedCollection)this).getLiveContiguousArrayBackingUNSAFE();
				
				//Double-check that the underlying slice matches the exposed length/size!
				//  To make sure this we did above was all we needed:  rangeCheckIntervalByLength(size, start, length);
				TransparentContiguousArrayBackedCollection.checkUnderlyingLengthAndExposedSizeMatch(size, u);
				
				if (u.getUnderlying() instanceof long[])
				{
					Slice<long[]> s = (Slice<long[]>) u;
					
					System.arraycopy(s.getUnderlying(), s.getOffset()+start, array, offset, length);
					
					return;
				}
			}
			
			
			defaultGetAllLongs(this, start, array, offset, length);
		}
		
		
		public static void defaultGetAllLongs(LongList list, int start, @WritableValue long[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				array[offset + i] = list.getLong(start + i);
		}
		
		
		
		
		
		
		@ThrowAwayValue
		public default long[] getAllLongs(int start, int end)
		{
			rangeCheckInterval(this.size(), start, end);
			
			long[] buff = new long[end-start];
			getAllLongs(start, buff, 0, buff.length);
			return buff;
		}
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public default boolean addLong(long value)
		{
			this.insertLong(this.size(), value);
			return true;
		}
		
		@Override
		public default boolean addAll(Collection<? extends Long> c)
		{
			this.addAll(this.size(), c);
			return true;
		}
		
		@Override
		public default boolean addAllLongs(long[] elements, int offset, int length)
		{
			insertAllLongs(this.size(), elements, offset, length);
			return true;
		}
		
		public default void removeByIndex(int index)
		{
			removeRange(index, index+1);
		}
		
		public default long removeLongByIndex(int index) throws IndexOutOfBoundsException
		{
			long v = this.getLong(index);
			removeByIndex(index);
			return v;
		}
		
		@Override
		public default boolean removeLong(long value)
		{
			int i = this.indexOfLong(value);
			
			if (i != -1)
			{
				this.removeByIndex(i);
				return true;
			}
			else
			{
				return false;
			}
		}
		
		@Override
		public default boolean retainAll(Collection<?> c)
		{
			if (c instanceof LongCollection)
			{
				LongCollection cc = (LongCollection) c;
				
				boolean changedAtAll = false;
				
				for (int i = size() - 1; i >= 0; i--)
				{
					if (!cc.containsLong(getLong(i)))
					{
						removeLongByIndex(i);
						changedAtAll = true;
					}
				}
				
				return changedAtAll;
			}
			else
			{
				boolean changedAtAll = false;
				
				for (int i = size() - 1; i >= 0; i--)
				{
					if (!c.contains(getLong(i)))
					{
						removeLongByIndex(i);
						changedAtAll = true;
					}
				}
				
				return changedAtAll;
			}
		}
		
		
		
		
		
		
		
		
		@Override
		public default SimpleLongIterator newSimpleLongIterator()
		{
			return new SimpleLongIterator()
			{
				int index = 0;
				
				public long nextrpLong() throws StopIterationReturnPath
				{
					int i = index;
					
					if (i >= size())
					{
						throw StopIterationReturnPath.I;
					}
					else
					{
						index = i + 1;
						return getLong(i);
					}
				}
			};
		}
		
		
		
		
		
		@Override
		public default boolean containsLong(long value)
		{
			return indexOfLong(value) != -1;
		}
		
		
		public default void insertAllLongs(int index, long[] array, int offset, int length)
		{
			for (int i = 0; i < length; i++)
				insertLong(index+i, array[offset+i]);
		}
		
		
		@Override
		public default boolean addAll(int index, Collection<? extends Long> c)
		{
			if (c instanceof LongCollection)
			{
				LongCollection cc = (LongCollection)c;
				
				SimpleLongIterator i = cc.newSimpleLongIterator();
				while (true)
				{
					try
					{
						insertLong(index, i.nextrpLong());
						index++;  //doesn't matter if this gets executed on the last iteration :3
					}
					catch (StopIterationReturnPath rp)
					{
						break;
					}
				}
			}
			else
			{
				for (Long e : c)
				{
					insertLong(index, e);
					index++;
				}
			}
			
			return true;
		}
		
		
		
		public default void insertAllLongs(int index, long[] array)
		{
			insertAllLongs(index, array, 0, array.length);
		}
		
		public default void insertAllLongs(int index, Slice<long[]> arraySlice)
		{
			insertAllLongs(index, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
		}
		
		
		
		@Override
		public default Long get(int index)
		{
			return this.getLong(index);
		}
		
		@Override
		public default Long set(int index, Long value)
		{
			Long previous = get(index);
			this.setLong(index, value);
			return previous;
		}
		
		
		@Override
		public default void add(int index, Long value)
		{
			this.insertLong(index, value);
		}
		
		@Override
		public default Long remove(int index) throws IndexOutOfBoundsException
		{
			return this.removeLongByIndex(index);
		}
		
		
		@Override
		public default int indexOf(Object o)
		{
			return o instanceof Long ? indexOfLong((Long)o) : -1;
		}
		
		@Override
		public default int lastIndexOf(Object o)
		{
			return o instanceof Long ? lastIndexOfLong((Long)o) : -1;
		}
		
		
		
		@Override
		public default ListIterator<Long> listIterator()
		{
			return listIterator(0);
		}
		
		@Override
		public default ListIterator<Long> listIterator(int index)
		{
			//Todo
			throw new NotYetImplementedException();
		}
		
		@Override
		public default LongList subList(int fromIndex, int toIndex)   //Also note that we narrow the return types of this :3
		{
			if (fromIndex == 0 && toIndex == this.size())
				return this;
			
			if (this instanceof Sublist)
			{
				Sublist<Long> s = (Sublist<Long>)this;
				return new LongSublist((LongList)s.getUnderlying(), s.getSublistStartingIndex() + fromIndex, toIndex - fromIndex);
			}
			else
			{
				return new LongSublist(this, fromIndex, toIndex - fromIndex);
			}
		}
		
		
		
		
		
		
		
		
		
		
		// Multiple-Inheritance conflict resolvers X'3
		@Override
		public default boolean removeAll(Collection<?> c)
		{
			return DefaultToArraysLongCollection.super.removeAll(c);
		}
		
		@Override
		public default boolean remove(Object o)
		{
			return DefaultToArraysLongCollection.super.remove(o);
		}
		
		@Override
		public default boolean containsAll(Collection<?> c)
		{
			return DefaultToArraysLongCollection.super.containsAll(c);
		}
		
		@Override
		public default boolean add(Long e)
		{
			return DefaultToArraysLongCollection.super.add(e);
		}
		
		@Override
		public default boolean isEmpty()
		{
			return DefaultToArraysLongCollection.super.isEmpty();
		}
		
		@Override
		public default boolean contains(Object o)
		{
			return DefaultToArraysLongCollection.super.contains(o);
		}
		
		
		
		
		
		@Override
		public default Object[] toArray()
		{
			return DefaultToArraysLongCollection.super.toArray();
		}
		
		@Override
		public default <T> T[] toArray(T[] a)
		{
			return DefaultToArraysLongCollection.super.toArray(a);
		}
		
		
		
		
		
		
		
		@Override
		public default void storeBoxingIntoArray(Slice<Object[]> array)
		{
			int n = this.size();
			for (int i = 0; i < n; i++)
				ArrayUtilities.setReference((Slice)array, i, this.get(i));
		}
		
		
		
		
		
		
		//Narrow the return types of these :3
		
		@Override
		public default LongList subListToEnd(int start)
		{
			return (LongList)NiceList.super.subListToEnd(start);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
		
		@Override
		public default LongList subListFromBeginning(int lengthWhichIsEndExclusiveInThisCaseXD)
		{
			return (LongList)NiceList.super.subListFromBeginning(lengthWhichIsEndExclusiveInThisCaseXD);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
		
		@Override
		public default LongList subListByLength(int start, int length)
		{
			return (LongList)NiceList.super.subListByLength(start, length);   //It's okay to cast, because it just passes through from subList(int,int) :3
		}
	}
	
	
	
	
	
	
	public static interface DefaultShiftingBasedLongList
	extends LongList
	{
		public default void clear()
		{
			removeRange(0, size());
		}
		
		@Override
		public default void insertLong(int index, long value)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, 1);
			this.setLong(index, value);
		}
		
		@Override
		public default void insertAllLongs(int index, long[] array, int offset, int length)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, length);
			
			this.setAllLongs(index, array, offset, length);
		}
		
		
		@Override
		public default boolean addAll(int index, Collection<? extends Long> c)
		{
			rangeCheckCursorPoint(this.size(), index);
			
			int amount = c.size();  //copy this incase c == this! XD
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, index, amount);
			
			if (c instanceof List)
			{
				listcopy((List)c, 0, this, index, amount);
			}
			else
			{
				if (c instanceof LongCollection)
				{
					SimpleLongIterator iterator = ((LongCollection)c).newSimpleLongIterator();
					
					int i = 0;
					while (true)
					{
						long e;
						try
						{
							e = iterator.nextrpLong();
						}
						catch (StopIterationReturnPath rp)
						{
							break;
						}
						
						this.setLong(index+i, e);
						i++;
					}
				}
				else
				{
					int i = 0;
					for (Long e : c)
					{
						this.set(index+i, e);
						i++;
					}
				}
			}
			
			return true;
		}
		
		@Override
		public default void setSizeLong(int newSize, long elementToAddIfGrowing)
		{
			int oldSize = this.size();
			int amount = newSize - oldSize;
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, oldSize, amount);
			
			//Growing results in *undefined* contents, not necessarily initialized ones!  (eg, it may already have some/enough elements allocated eg, in an underlying array and it just increases the int size field :3 )
			for (int i = oldSize; i < newSize; i++)
				this.setLong(i, elementToAddIfGrowing);
		}
		
		
		
		
		@Override
		public default void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			rangeCheckInterval(this.size(), start, pastEnd);
			
			int length = pastEnd - start;
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(this, pastEnd, -length);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class LongSublist
	implements LongList, DefaultShiftingBasedLongList, Sublist<Long>, ShiftableList
	{
		protected final LongList underlying;
		protected final int start;
		protected int size;
		
		public LongSublist(LongList underlying, int start, int initialSize)
		{
			rangeCheckIntervalByLength(underlying.size(), start, initialSize);
			
			this.underlying = underlying;
			this.start = start;
			this.size = initialSize;
		}
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public long getLong(int index)
		{
			return underlying.getLong(index + start);
		}
		
		@Override
		public void setLong(int index, long value)
		{
			underlying.setLong(index + start, value);
		}
		
		@Override
		public void shiftRegionStretchingFromIndexToEndByAmountChangingSize(int start, int amount)
		{
			rangeCheckCursorPoint(size(), start);
			
			shiftRegionStretchingFromIndexToEndByAmountChangingSizeGrandfathering(underlying, start, amount);
			
			size += amount;
		}
		
		
		
		@Override
		public LongList clone()
		{
			LongList c = new LongArrayList();
			c.addAll(this);
			return c;
		}
		
		
		
		@Override
		public LongList getUnderlying()
		{
			return underlying;
		}
		
		@Override
		public int getSublistStartingIndex()
		{
			return start;
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class LongArrayList
	implements DefaultShiftingBasedLongList, ListWithSetSize<Long>, Trimmable, TransparentContiguousArrayBackedCollection<long[]>, KnowsLengthFixedness
	{
		protected int size = 0;
		protected long[] data;
		protected GrowerComputationallyUnreducedPurelyRecursive grower;
		
		
		
		
		//In case subclasses don't want to deal with the other things ^^'
		protected LongArrayList(Void thisiswhyyoushouldntusejavaconstructorsAlthoughIdontknowhowtomakeinheritanceworkwellwithoutinitmethodsorfactoriesorsomething)
		{
		}
		
		
		/**
		 * Capacity increment is different from Vector's.  It is the percentage of the capacity by which the capacity is increased.
		 * The formula being: <code>newCapacity = capacity + (capacity * capacityIncrement)</code><br>
		 * @param grower This can be anything.  The only downside is performance: all methods will add at least the minimum amount required regardless of what this specifies.
		 */
		public LongArrayList(int initialCapacity, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this.data = initialCapacity == 0 ? ArrayUtilities.EmptyLongArray : new long[initialCapacity];
			this.grower = grower;
		}
		
		public LongArrayList(@LiveValue @WritableValue long[] LIVEBACKING, int size, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this.data = LIVEBACKING;
			this.grower = grower;
			this.size = size;
		}
		
		public LongArrayList(@SnapshotValue @ReadonlyValue Collection<Long> c, GrowerComputationallyUnreducedPurelyRecursive grower)
		{
			this(c.size(), grower);
			this.addAll(c);
		}
		
		
		
		
		
		public LongArrayList()
		{
			this(DefaultPrimitiveArrayListInitialCapacity);
		}
		
		public LongArrayList(int initialCapacity)
		{
			this(initialCapacity, defaultPrimitiveArrayListGrower(initialCapacity));
		}
		
		public LongArrayList(@SnapshotValue @ReadonlyValue Collection<Long> c)
		{
			this(c, defaultPrimitiveArrayListGrower(c.size()));
		}
		
		public LongArrayList(@LiveValue @WritableValue long[] LIVEBACKING, int size)
		{
			this(LIVEBACKING, size, defaultPrimitiveArrayListGrower(LIVEBACKING.length));
		}
		
		public LongArrayList(@LiveValue @WritableValue long[] LIVEBACKING)
		{
			this(LIVEBACKING, LIVEBACKING.length);
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return false;
		}
		
		
		
		@Override
		public void setSize(int newSize)
		{
			ensureCapacity(newSize);
			this.size = newSize;
			
			// :D
		}
		
		
		
		
		
		//This pattern is nicer for subclassing in any Java class! :D
		protected void setupClone(LongArrayList freshClone)
		{
			freshClone.size = this.size;
			
			freshClone.data = new long[size];
			System.arraycopy(this.data, 0, freshClone.data, 0, size);
			
			freshClone.grower = this.grower;
		}
		
		@Override
		@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
		public LongArrayList clone()
		{
			LongArrayList clone = new LongArrayList();
			this.setupClone(clone);
			return clone;
		}
		
		
		
		
		
		
		
		
		
		
		//This is what unlocks all the System.arraycopy() magic!  8>
		@ImplementationTransparency
		@LiveValue
		public Slice<long[]> getLiveContiguousArrayBackingUNSAFE()
		{
			return new Slice(data, 0, size);
		}
		
		
		
		
		
		
		
		
		
		public void setLong(int index, long value)
		{
			rangeCheckMember(this.size, index);
			data[index] = value;
		}
		
		public long getLong(int index)
		{
			rangeCheckMember(this.size(), index);
			return data[index];
		}
		
		
		
		
		
		
		
		@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public void clear()
		{
			size = 0;
		}
		
		@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public int size()
		{
			return size;
		}
		
		@ImplementationTransparency
		public void trimToSize()
		{
			long[] newdata = new long[size];
			System.arraycopy(data, 0, newdata, 0, size);
			this.data = newdata;
		}
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.trimToSize();
			return TrimmableTrimRV.DontKeepInvoking;
		}
		
		
		
		
		
		@ImplementationTransparency
		public int getCapacity()
		{
			return data.length;
		}
		
		@ImplementationTransparency
		public void ensureCapacity(int minCapacity)
		{
			if (data.length < minCapacity)
			{
				//Enlarge
				int newCapacity = getNewCapacity(minCapacity);
				long[] newData = new long[newCapacity];
				System.arraycopy(data, 0, newData, 0, data.length);
				this.data = newData;
			}
		}
		
		/**
		 * This is also guaranteed not to return a capacity smaller than the provided minimum.
		 */
		@ImplementationTransparency
		public int getNewCapacity(int minCapacity)
		{
			if (minCapacity < 0)
				throw new IllegalArgumentException();
			
			return Math.max(minCapacity, grower.getNewSizeRecursive(data.length));  //The Math.max(min, ...) ensures that the grower can't break anything!  :3
		}
		
		
		
		
		
		
		
		/**
		 * sets the underlying array that backs the array list.
		 * Be careful, though, as updates can cause a new array to be created, obsoleting this one.
		 * Note, also, that only up to {@link #size()} elements will be used.
		 */
		@ImplementationTransparency
		public void setDirectBuffer(@LiveValue long[] array)
		{
			if (array.length < size)
				throw new IllegalArgumentException("Array is too small; size="+size+", capacity="+array.length);
			
			this.data = array;
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static class FixedLengthArrayWrapperLongList
	implements LongList, TransparentContiguousArrayBackedCollection<long[]>, KnowsLengthFixedness
	{
		protected final long[] underlying;
		protected final int offset, length;
		
		public FixedLengthArrayWrapperLongList(long[] underlying, int offset, int length)
		{
			rangeCheckIntervalByLength(underlying.length, offset, length);
			
			this.underlying = underlying;
			this.offset = offset;
			this.length = length;
		}
		
		public FixedLengthArrayWrapperLongList(long[] underlying)
		{
			this(underlying, 0, underlying.length);
		}
		
		public FixedLengthArrayWrapperLongList(Slice<long[]> underlying)
		{
			this(underlying.getUnderlying(), underlying.getOffset(), underlying.getLength());
		}
		
		@Override
		public LongList clone()
		{
			return new FixedLengthArrayWrapperLongList(toLongArray());
		}
		
		
		
		@Override
		public long[] toLongArray()
		{
			return sliceToNewLongArrayOP(this.getLiveContiguousArrayBackingUNSAFE());
		}
		
		@Override
		public Slice<long[]> getLiveContiguousArrayBackingUNSAFE()
		{
			return new Slice<>(underlying, offset, length);
		}
		
		@Override
		public int size()
		{
			return length;
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return true;
		}
		
		
		
		@Override
		public SimpleLongIterator newSimpleLongIterator()
		{
			return new SimpleLongIterator()
			{
				int index = 0;
				
				public long nextrpLong() throws StopIterationReturnPath
				{
					int i = index;
					
					if (i >= size())
					{
						throw StopIterationReturnPath.I;
					}
					else
					{
						index = i + 1;
						return underlying[offset+i];
					}
				}
			};
		}
		
		@Override
		public LongList subList(int fromIndex, int toIndex)
		{
			rangeCheckInterval(this.size(), fromIndex, toIndex);
			return new FixedLengthArrayWrapperLongList(this.underlying, fromIndex+offset, toIndex-fromIndex);
		}
		
		@Override
		public long getLong(int index)
		{
			return underlying[offset + index];
		}
		
		@Override
		public void setLong(int index, long value)
		{
			underlying[offset + index] = value;
		}
		
		
		
		
		
		
		
		
		
		//Unallowed Dim-altering Methods!
		
		@Override
		public void clear()
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void setSizeLong(int newSize, long elementToAddIfGrowing)
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void insertLong(int index, long value)
		{
			throw new UnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	@ReadonlyValue
	public static LongList longArrayAsList(@LiveValue @WritableValue long[] array, int offset, int length)
	{
		return new FixedLengthArrayWrapperLongList(array, offset, length);
	}
	
	@ReadonlyValue
	public static LongList longArrayAsList(@LiveValue @WritableValue long[] array)
	{
		return longArrayAsList(array, 0, array.length);
	}
	
	@ReadonlyValue
	public static LongList longArrayAsList(@LiveValue @WritableValue Slice<long[]> arraySlice)
	{
		return longArrayAsList(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	
	
	
	@ThrowAwayValue
	public static LongList longArrayAsMutableList(@SnapshotValue @ReadonlyValue long[] array, int offset, int length)
	{
		return new LongArrayList(new FixedLengthArrayWrapperLongList(array, offset, length));
	}
	
	@ThrowAwayValue
	public static LongList longArrayAsMutableList(@SnapshotValue @ReadonlyValue long[] array)
	{
		return longArrayAsMutableList(array, 0, array.length);
	}
	
	@ThrowAwayValue
	public static LongList longArrayAsMutableList(@SnapshotValue @ReadonlyValue Slice<long[]> arraySlice)
	{
		return longArrayAsMutableList(arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	
	
	
	
	
	
	
	
	
	
	@Immutable
	public static class ImmutableLongArrayList
	implements Serializable, Comparable<ImmutableLongArrayList>, LongList, KnowsLengthFixedness     //Let's not implement the general interfaces for accessing the underlying data jussssst to help make sure anyone who accesses it *knows* it's meant to be VERY MUCH READONLY!  XD'''
	{
		private static final long serialVersionUID = 1L;
		
		
		protected final long[] data;
		
		//Yeah I know it's bad practice for a thread-safe immutable object to cache things, but at best effort is wasted, no constraints are violated!     (If I'm wrong, fire me X'D )
		protected transient long hashCode;     //we cache it in case the longarray is large :33
		
		protected ImmutableLongArrayList(long[] LIVEDATA)
		{
			if (LIVEDATA == null) throw new NullPointerException();
			this.data = LIVEDATA;
		}
		
		
		
		
		
		/**
		 * DO NOT *EVER* MODIFY THE ARRAY YOU PASSED HERE AFTER CALLING THIS X"D
		 * ( THE HASH CODE IS CACHED FOR ONE, AND THIS MAY BE EMBEDDED IN HASHMAPS--THIS IS SUPPOSED TO BE IMMUTABLE AFTER ALL X'DDD )
		 * @see #getREADONLYLiveWholeArrayBackingUNSAFE()
		 */
		@ImplementationTransparency
		public static ImmutableLongArrayList newLIVE(@LiveValue long[] LIVEDATA)
		{
			return new ImmutableLongArrayList(LIVEDATA);
		}
		
		
		@ImplementationTransparency
		public static ImmutableLongArrayList newSingleton(long singleMember)
		{
			return new ImmutableLongArrayList(new long[]{singleMember});
		}
		
		
		
		
		public static ImmutableLongArrayList newCopying(@SnapshotValue List<Long> data)
		{
			if (data instanceof ImmutableLongArrayList)
				return (ImmutableLongArrayList)data;
			
			if (TransparentContiguousArrayBackedCollection.is(data))
			{
				Slice<Object> s = ((TransparentContiguousArrayBackedCollection)data).getLiveContiguousArrayBackingUNSAFE();
				if (s.getUnderlying() instanceof long[])
				{
					return newCopying((Slice)s);
				}
			}
			
			if (data instanceof LongList)
			{
				return newLIVE(((LongList)data).toLongArray());
			}
			else
			{
				int n = data.size();
				long[] a = new long[n];
				
				int i = 0;
				for (Long e : data)
				{
					if (i >= n)
						throw new ImpossibleException();
					
					a[i] = e;
					i++;
				}
				
				if (i != n)
					throw new ImpossibleException();
				
				return newLIVE(a);
			}
		}
		
		public static ImmutableLongArrayList newCopying(@SnapshotValue long[] data)
		{
			return new ImmutableLongArrayList(data.clone());
		}
		
		public static ImmutableLongArrayList newCopying(@SnapshotValue long[] data, int offset, int length)
		{
			if (offset == 0 && length == data.length)
				return newCopying(data);  //who knows, long[].clone() might be faster! \o/   (it certainly shouldn't be slower!!)
			else
			{
				long[] newArray = new long[length];
				System.arraycopy(data, offset, newArray, 0, length);
				return new ImmutableLongArrayList(newArray);
			}
		}
		
		public static ImmutableLongArrayList newCopying(@SnapshotValue Slice<long[]> data)
		{
			return newCopying(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		
		
		
		
		public static ImmutableLongArrayList newLIVEOrCopying(@PossiblySnapshotPossiblyLiveValue long[] data, int offset, int length)
		{
			if (offset == 0 && length == data.length)
				return newLIVE(data);
			else
				return newCopying(data, offset, length);
		}
		
		public static ImmutableLongArrayList newLIVEOrCopying(@PossiblySnapshotPossiblyLiveValue Slice<long[]> data)
		{
			return newLIVEOrCopying(data.getUnderlying(), data.getOffset(), data.getLength());
		}
		
		
		
		
		
		
		public static final ImmutableLongArrayList Empty = ImmutableLongArrayList.newLIVE(ArrayUtilities.EmptyLongArray);
		
		
		
		
		
		
		
		
		@ThrowAwayValue
		@Override
		public long[] toLongArray()
		{
			return data.clone();
		}
		
		/**
		 * DO NOT MODIFY THIS X"D
		 * ( THE HASH CODE IS CACHED FOR ONE, AND THIS MAY BE EMBEDDED IN HASHMAPS--THIS IS SUPPOSED TO BE IMMUTABLE AFTER ALL X'DDD )
		 */
		@ImplementationTransparency
		@LiveValue
		@ReadonlyValue //!!!!
		public long[] getREADONLYLiveWholeArrayBackingUNSAFE()
		{
			return data;
		}
		
		@Override
		public int size()
		{
			return data.length;
		}
		
		@Override
		public long getLong(int index)
		{
			return data[index];
		}
		
		@Override
		public void getAllLongs(int offsetInThisSource, @WritableValue long[] dest, int offsetInDest, int lengthInBoth)
		{
			System.arraycopy(this.data, offsetInThisSource, dest, offsetInDest, lengthInBoth);
		}
		
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return true;
		}
		
		
		
		
		@Override
		public int hashCode()
		{
			long hashCodeField = this.hashCode;
			
			boolean cached = hashCodeField != 0;
			
			
			if (cached)
			{
				return (int)this.hashCode;
			}
			else
			{
				int hashCode = Arrays.hashCode(this.data);
				this.hashCode = hashCode | 0xFFFF_FFFF_0000_0000l;  //hashCodes can be zero!  Not often XDD   But can be!  So it's not correct unless we account for that :>      (And if it was two fields then it'd be a concurrency bug!)
				return hashCode;
			}
		}
		
		
		
		
		@Override
		public boolean equals(@ReadonlyValue Object obj)
		{
			if (obj == this)
			{
				return true;
			}
			else if (obj instanceof ImmutableLongArrayList)
			{
				ImmutableLongArrayList other = (ImmutableLongArrayList)obj;
				
				//Checking the hashcodes is a VERY fast negative test and is almost always true-negative!! :DDD   (rarely it's false-positive, but never false-negative!)
				
				long thisHC = this.hashCode;
				long otherHC = other.hashCode;
				
				boolean thisHashCodeCached = thisHC != 0;
				boolean otherHashCodeCached = otherHC != 0;
				
				int thisHashCode = (int)thisHC;
				int otherHashCode = (int)otherHC;
				
				if (thisHashCodeCached)
				{
					if (otherHashCodeCached)
					{
						if (thisHashCode != otherHashCode)
							return false;
					}
					else
					{
						//Scanning one long array is faster than scanning two! :D
						if (thisHashCode != other.hashCode())
							return false;
					}
				}
				else
				{
					if (otherHashCodeCached)
					{
						//Scanning one long array is faster than scanning two! :D
						if (this.hashCode() != otherHashCode)
							return false;
					}
					else
					{
						//Then don't make any hash codes X3
					}
				}
				
				return this.equalsLongArray(other.data);
			}
			else
			{
				return false;
			}
		}
		
		
		@Override
		public int compareTo(@ReadonlyValue ImmutableLongArrayList other)
		{
			return this.compareToLongArray(other.data);
		}
		
		
		public boolean equalsLongArray(@ReadonlyValue long[] o)
		{
			long[] ourData = this.getREADONLYLiveWholeArrayBackingUNSAFE();
			long[] theirData = o;
			
			return Arrays.equals(ourData, theirData);
		}
		
		
		public int compareToLongArray(@ReadonlyValue long[] o)
		{
			long[] ourData = this.getREADONLYLiveWholeArrayBackingUNSAFE();
			long[] theirData = o;
			
			return ArrayUtilities.compareLittleEndianLengthsFirst(ourData, theirData);  //lengths-first is more efficient if they're different but hardly any less efficient if they're always the same!  A good default, don't you think? :D
		}
		
		
		
		
		@Override
		public ImmutableLongArrayList clone()
		{
			return this;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		// Unallowed Write Methods!
		
		@Override
		public void clear()
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setSizeLong(int newSize, long elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void insertLong(int index, long value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setLong(int index, long value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static LongList unmodifiableLongList(LongList longList)
	{
		return longList instanceof UnmodifiableLongListWrapper ? longList : new UnmodifiableLongListWrapper(longList);
	}
	
	public static class UnmodifiableLongListWrapper
	implements LongList, KnowsLengthFixedness, TransparentContiguousArrayBackedCollection
	{
		protected final LongList underlying;
		
		public UnmodifiableLongListWrapper(LongList underlying)
		{
			this.underlying = underlying;
		}
		
		@ImplementationTransparency
		public LongList getUnderlying()
		{
			return underlying;
		}
		
		
		
		
		
		@Override
		public UnmodifiableLongListWrapper clone()
		{
			return new UnmodifiableLongListWrapper(underlying.clone());
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.underlying);
		}
		
		@Override
		public boolean isTransparentContiguousArrayBackedCollection()
		{
			return TransparentContiguousArrayBackedCollection.is(underlying);
		}
		
		@Override
		public Slice getLiveContiguousArrayBackingUNSAFE()
		{
			return ((TransparentContiguousArrayBackedCollection)underlying).getLiveContiguousArrayBackingUNSAFE();
		}
		
		
		
		
		
		
		//Use default so they can't get around our readonly-ness!  ;D
		//	public Iterator<Long> iterator()
		//	public ListIterator<Long> listIterator()
		//	public ListIterator<Long> listIterator(int index)
		//	public SimpleIterator<Long> simpleIterator()
		//	public SimpleLongIterator newSimpleLongIterator()
		//	public LongList subList(int fromIndex, int toIndex)
		
		
		
		
		public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeRangeByLength(int start, int length) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void replaceAll(UnaryOperator<Long> operator)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeIf(Predicate<? super Long> filter)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void sort(Comparator<? super Long> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void clear()
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public Long remove(int index)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllLongs(long[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllLongs(Slice<long[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllLongs(long[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllLongs(Slice<long[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeAllLongs(long[] a, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setLong(int index, long value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertLong(int index, long value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSizeLong(int newSize, long elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSize(int newSize, Long elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setSize(int newSize)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllLongs(int index, long[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllLongs(int index, Slice<long[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAllLongs(int index, long[] array, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void setAll(int destIndex, List sourceU, int sourceIndex, int amount) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addLong(long value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll(Collection<? extends Long> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAllLongs(long[] elements, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void removeByIndex(int index)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public long removeLongByIndex(int index) throws IndexOutOfBoundsException
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeLong(long value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean retainAll(Collection<?> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllLongs(int index, long[] array, int offset, int length)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean addAll(int index, Collection<? extends Long> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllLongs(int index, long[] array)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void insertAllLongs(int index, Slice<long[]> arraySlice)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public Long set(int index, Long value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public void add(int index, Long value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean removeAll(Collection<?> c)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean remove(Object o)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		public boolean add(Long e)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		
		
		
		
		
		
		
		
		
		public void forEach(Consumer<? super Long> action)
		{
			underlying.forEach(action);
		}
		
		public int size()
		{
			return underlying.size();
		}
		
		public boolean equals(Object o)
		{
			return underlying.equals(o);
		}
		
		public int hashCode()
		{
			return underlying.hashCode();
		}
		
		public Spliterator<Long> spliterator()
		{
			return underlying.spliterator();
		}
		
		public Stream<Long> stream()
		{
			return underlying.stream();
		}
		
		public Stream<Long> parallelStream()
		{
			return underlying.parallelStream();
		}
		
		public long getLong(int index)
		{
			return underlying.getLong(index);
		}
		
		public int indexOfLong(long value)
		{
			return underlying.indexOfLong(value);
		}
		
		public int lastIndexOfLong(long value)
		{
			return underlying.lastIndexOfLong(value);
		}
		
		public boolean equivalent(List<Long> other)
		{
			return underlying.equivalent(other);
		}
		
		public long[] toLongArray()
		{
			return underlying.toLongArray();
		}
		
		public boolean containsLong(long value)
		{
			return underlying.containsLong(value);
		}
		
		public Long get(int index)
		{
			return underlying.get(index);
		}
		
		public int indexOf(Object o)
		{
			return underlying.indexOf(o);
		}
		
		public int lastIndexOf(Object o)
		{
			return underlying.lastIndexOf(o);
		}
		
		public boolean containsAll(Collection<?> c)
		{
			return underlying.containsAll(c);
		}
		
		public boolean isEmpty()
		{
			return underlying.isEmpty();
		}
		
		public boolean contains(Object o)
		{
			return underlying.contains(o);
		}
		
		public Object[] toArray()
		{
			return underlying.toArray();
		}
		
		public <T> T[] toArray(T[] a)
		{
			return underlying.toArray(a);
		}
		
		public void storeBoxingIntoArray(Slice<Object[]> array)
		{
			underlying.storeBoxingIntoArray(array);
		}
		
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class LongTable
	{
		protected int width;
		protected long[] data;
		
		public LongTable(int width, int height)
		{
			if (width < 0)
				throw new IllegalArgumentException();
			if (height < 0)
				throw new IllegalArgumentException();
			
			data = new long[width * height];
		}
		
		
		
		@Nonnegative
		public int getNumberOfColumns()
		{
			return width;
		}
		
		@Nonnegative
		public int getNumberOfRows()
		{
			return data.length / width;
		}
		
		
		@Nonnegative
		public int getNumberOfCells()
		{
			return data.length;
		}
		
		
		
		
		
		public long getCellContentsLong(int columnIndex, int rowIndex) throws IndexOutOfBoundsException
		{
			if (columnIndex < 0) throw new IndexOutOfBoundsException();
			if (rowIndex < 0) throw new IndexOutOfBoundsException();
			if (columnIndex >= getNumberOfColumns()) throw new IndexOutOfBoundsException();
			if (rowIndex >= getNumberOfRows()) throw new IndexOutOfBoundsException();
			
			return this.data[rowIndex * this.getNumberOfColumns() + columnIndex];
		}
		
		public void setCellContentsLong(int columnIndex, int rowIndex, @Nullable long newValue) throws IndexOutOfBoundsException
		{
			if (columnIndex < 0) throw new IndexOutOfBoundsException();
			if (rowIndex < 0) throw new IndexOutOfBoundsException();
			if (columnIndex >= getNumberOfColumns()) throw new IndexOutOfBoundsException();
			if (rowIndex >= getNumberOfRows()) throw new IndexOutOfBoundsException();
			
			this.data[rowIndex * this.getNumberOfColumns() + columnIndex] = newValue;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Nullable
		public Long getCellContents(int columnIndex, int rowIndex) throws IndexOutOfBoundsException
		{
			return getCellContentsLong(columnIndex, rowIndex);
		}
		
		public void setCellContents(int columnIndex, int rowIndex, @Nullable Long newValue) throws IndexOutOfBoundsException
		{
			setCellContentsLong(columnIndex, rowIndex, newValue);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//>>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
primxp
_$$primxpconf:noboolean$$_

	/**
	 * A set of primitives with the constraint that no two elements are duplicates and that
	 * the collection is always sorted (ascending).
	 ⎋a/
	public static class Sorted_$$Primitive$$_SetBackedByList
	implements DefaultToArrays_$$Primitive$$_Collection, UnderlyingInstanceAccessible<_$$Primitive$$_List>, KnowsLengthFixedness
	{
		protected _$$Primitive$$_List underlying;
		
		public Sorted_$$Primitive$$_SetBackedByList()
		{
			this(new _$$Primitive$$_ArrayList());
		}
		
		/**
	 * NOTE THAT IF IT'S NOT EMPTY IT MUST ALREADY BE SORTED!!
		 ⎋a/
		public Sorted_$$Primitive$$_SetBackedByList(@LiveValue _$$Primitive$$_List presortedOrEmptyUnderlying)
		{
			this.underlying = requireNonNull(presortedOrEmptyUnderlying);
		}
		
		
		
		
		
		@LiveValue
		@Override
		public _$$Primitive$$_List getUnderlying()
		{
			return underlying;
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.getUnderlying());
		}
		
		
		
		
		
		
		
		//No easy way to add elements in bulk (that I'm aware of, or have the time to implement)
		
		@Override
		public boolean add_$$Prim$$_(_$$prim$$_ value)
		{
			int insertionPoint = SortingUtilities.findInsertionPointInSet(underlying, 0, size(), value);
			
			if (insertionPoint < 0)
			{
				//Duplicate
				return false;
			}
			else
			{
				underlying.insert_$$Prim$$_(insertionPoint, value);
				return true;
			}
		}
		
		public int indexOf_$$Prim$$_(_$$prim$$_ value)
		{
			//Better algorithm, no difference in API
			return SortingUtilities.findIndexForValueInSortedSet(underlying, 0, size(), value);
		}
		
		
		
		
		
		////  Use the better impl.s! :D  ////
		
		@Override
		public boolean remove_$$Prim$$_(_$$prim$$_ value)
		{
			int i = indexOf_$$Prim$$_(value);
			
			if (i == -1)
			{
				return false;
			}
			else
			{
				this.removeByIndex(i);
				return true;
			}
		}
		
		@Override
		public boolean contains_$$Prim$$_(_$$prim$$_ value)
		{
			return indexOf_$$Prim$$_(value) != -1;
		}
		
		/**
	 * A MUCH faster implementation (practically in every case and asymptotically! :D )
		 ⎋a/
		public boolean equivalent(Sorted_$$Primitive$$_SetBackedByList other)
		{
			return this.underlying.equivalent(other.getUnderlying());
		}
		
		
		
		
		
		
		
		
		
		@Override
		public _$$prim$$_[] to_$$Prim$$_Array()
		{
			return toSorted_$$Prim$$_Array();
		}
		
		/**
	 * This is a synonym to {@link #to_$$Prim$$_Array()} which should probably be used in preference to {@link #to_$$Prim$$_Array()} if your code relies on its sorted-ness, at least for clarity :>
	 * (Useful if someone else comes by and switches the instantiation to, say, Set&lt;Integer&gt; X'D )
		 ⎋a/
		@ThrowAwayValue
		public _$$prim$$_[] toSorted_$$Prim$$_Array()
		{
			return underlying.to_$$Prim$$_Array();
		}
		
		
		@Override
		public boolean retainAll(Collection<?> c)
		{
			//Can you think of a better way that relies on using this.contains() instead of c.contains() ??
			return underlying.retainAll(c);
		}
		
		
		
		//NOTE!  There is no set _$$prim$$_ (by index)!  It could easily de-sort the elements! XD      Remove it then add it again :3
		
		
		
		//// Simple delegates :3 ////equivalent
		
		@Override
		public int size()
		{
			return underlying.size();
		}
		
		public _$$prim$$_ remove_$$Prim$$_ByIndex(int index)
		{
			return underlying.remove_$$Prim$$_ByIndex(index);
		}
		
		public void removeByIndex(int index)
		{
			underlying.removeByIndex(index);
		}
		
		/**
	 * Remove all elements in the range (exclusive, like String).<br>
	 * Then shifts all elements at- and to the right of- the <code>end</code>'th element left by the size() of the block removed to 'fill the void'.<br>
		 ⎋a/
		public void removeRange(int start, int end)
		{
			underlying.removeRange(start, end);
		}
		
		@Override
		public void clear()
		{
			underlying.clear();
		}
		
		public _$$prim$$_ get_$$Prim$$_(int index)
		{
			return underlying.get_$$Prim$$_(index);
		}
		
		@Override
		public void storeBoxingIntoArray(Slice<Object[]> array)
		{
			underlying.storeBoxingIntoArray(array);
		}
		
		@Override
		public Simple_$$Primitive$$_Iterator newSimple_$$Primitive$$_Iterator()
		{
			return underlying.newSimple_$$Primitive$$_Iterator();
		}
		
		@Override
		public Sorted_$$Primitive$$_SetBackedByList clone()
		{
			return new Sorted_$$Primitive$$_SetBackedByList(underlying.clone());
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	 */
	
	
	/**
	 * A set of primitives with the constraint that no two elements are duplicates and that
	 * the collection is always sorted (ascending).
	 */
	public static class SortedByteSetBackedByList
	implements DefaultToArraysByteCollection, UnderlyingInstanceAccessible<ByteList>, KnowsLengthFixedness
	{
		protected ByteList underlying;
		
		public SortedByteSetBackedByList()
		{
			this(new ByteArrayList());
		}
		
		/**
		 * NOTE THAT IF IT'S NOT EMPTY IT MUST ALREADY BE SORTED!!
		 */
		public SortedByteSetBackedByList(@LiveValue ByteList presortedOrEmptyUnderlying)
		{
			this.underlying = requireNonNull(presortedOrEmptyUnderlying);
		}
		
		
		
		
		
		@LiveValue
		@Override
		public ByteList getUnderlying()
		{
			return underlying;
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.getUnderlying());
		}
		
		
		
		
		
		
		
		//No easy way to add elements in bulk (that I'm aware of, or have the time to implement)
		
		@Override
		public boolean addByte(byte value)
		{
			int insertionPoint = SortingUtilities.findInsertionPointInSet(underlying, 0, size(), value);
			
			if (insertionPoint < 0)
			{
				//Duplicate
				return false;
			}
			else
			{
				underlying.insertByte(insertionPoint, value);
				return true;
			}
		}
		
		public int indexOfByte(byte value)
		{
			//Better algorithm, no difference in API
			return SortingUtilities.findIndexForValueInSortedSet(underlying, 0, size(), value);
		}
		
		
		
		
		
		////  Use the better impl.s! :D  ////
		
		@Override
		public boolean removeByte(byte value)
		{
			int i = indexOfByte(value);
			
			if (i == -1)
			{
				return false;
			}
			else
			{
				this.removeByIndex(i);
				return true;
			}
		}
		
		@Override
		public boolean containsByte(byte value)
		{
			return indexOfByte(value) != -1;
		}
		
		/**
		 * A MUCH faster implementation (practically in every case and asymptotically! :D )
		 */
		public boolean equivalent(SortedByteSetBackedByList other)
		{
			return this.underlying.equivalent(other.getUnderlying());
		}
		
		
		
		
		
		
		
		
		
		@Override
		public byte[] toByteArray()
		{
			return toSortedByteArray();
		}
		
		/**
		 * This is a synonym to {@link #toByteArray()} which should probably be used in preference to {@link #toByteArray()} if your code relies on its sorted-ness, at least for clarity :>
		 * (Useful if someone else comes by and switches the instantiation to, say, Set&lt;Integer&gt; X'D )
		 */
		@ThrowAwayValue
		public byte[] toSortedByteArray()
		{
			return underlying.toByteArray();
		}
		
		
		@Override
		public boolean retainAll(Collection<?> c)
		{
			//Can you think of a better way that relies on using this.contains() instead of c.contains() ??
			return underlying.retainAll(c);
		}
		
		
		
		//NOTE!  There is no set byte (by index)!  It could easily de-sort the elements! XD      Remove it then add it again :3
		
		
		
		//// Simple delegates :3 ////equivalent
		
		@Override
		public int size()
		{
			return underlying.size();
		}
		
		public byte removeByteByIndex(int index)
		{
			return underlying.removeByteByIndex(index);
		}
		
		public void removeByIndex(int index)
		{
			underlying.removeByIndex(index);
		}
		
		/**
		 * Remove all elements in the range (exclusive, like String).<br>
		 * Then shifts all elements at- and to the right of- the <code>end</code>'th element left by the size() of the block removed to 'fill the void'.<br>
		 */
		public void removeRange(int start, int end)
		{
			underlying.removeRange(start, end);
		}
		
		@Override
		public void clear()
		{
			underlying.clear();
		}
		
		public byte getByte(int index)
		{
			return underlying.getByte(index);
		}
		
		@Override
		public void storeBoxingIntoArray(Slice<Object[]> array)
		{
			underlying.storeBoxingIntoArray(array);
		}
		
		@Override
		public SimpleByteIterator newSimpleByteIterator()
		{
			return underlying.newSimpleByteIterator();
		}
		
		@Override
		public SortedByteSetBackedByList clone()
		{
			return new SortedByteSetBackedByList(underlying.clone());
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * A set of primitives with the constraint that no two elements are duplicates and that
	 * the collection is always sorted (ascending).
	 */
	public static class SortedCharacterSetBackedByList
	implements DefaultToArraysCharacterCollection, UnderlyingInstanceAccessible<CharacterList>, KnowsLengthFixedness
	{
		protected CharacterList underlying;
		
		public SortedCharacterSetBackedByList()
		{
			this(new CharacterArrayList());
		}
		
		/**
		 * NOTE THAT IF IT'S NOT EMPTY IT MUST ALREADY BE SORTED!!
		 */
		public SortedCharacterSetBackedByList(@LiveValue CharacterList presortedOrEmptyUnderlying)
		{
			this.underlying = requireNonNull(presortedOrEmptyUnderlying);
		}
		
		
		
		
		
		@LiveValue
		@Override
		public CharacterList getUnderlying()
		{
			return underlying;
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.getUnderlying());
		}
		
		
		
		
		
		
		
		//No easy way to add elements in bulk (that I'm aware of, or have the time to implement)
		
		@Override
		public boolean addChar(char value)
		{
			int insertionPoint = SortingUtilities.findInsertionPointInSet(underlying, 0, size(), value);
			
			if (insertionPoint < 0)
			{
				//Duplicate
				return false;
			}
			else
			{
				underlying.insertChar(insertionPoint, value);
				return true;
			}
		}
		
		public int indexOfChar(char value)
		{
			//Better algorithm, no difference in API
			return SortingUtilities.findIndexForValueInSortedSet(underlying, 0, size(), value);
		}
		
		
		
		
		
		////  Use the better impl.s! :D  ////
		
		@Override
		public boolean removeChar(char value)
		{
			int i = indexOfChar(value);
			
			if (i == -1)
			{
				return false;
			}
			else
			{
				this.removeByIndex(i);
				return true;
			}
		}
		
		@Override
		public boolean containsChar(char value)
		{
			return indexOfChar(value) != -1;
		}
		
		/**
		 * A MUCH faster implementation (practically in every case and asymptotically! :D )
		 */
		public boolean equivalent(SortedCharacterSetBackedByList other)
		{
			return this.underlying.equivalent(other.getUnderlying());
		}
		
		
		
		
		
		
		
		
		
		@Override
		public char[] toCharArray()
		{
			return toSortedCharArray();
		}
		
		/**
		 * This is a synonym to {@link #toCharArray()} which should probably be used in preference to {@link #toCharArray()} if your code relies on its sorted-ness, at least for clarity :>
		 * (Useful if someone else comes by and switches the instantiation to, say, Set&lt;Integer&gt; X'D )
		 */
		@ThrowAwayValue
		public char[] toSortedCharArray()
		{
			return underlying.toCharArray();
		}
		
		
		@Override
		public boolean retainAll(Collection<?> c)
		{
			//Can you think of a better way that relies on using this.contains() instead of c.contains() ??
			return underlying.retainAll(c);
		}
		
		
		
		//NOTE!  There is no set char (by index)!  It could easily de-sort the elements! XD      Remove it then add it again :3
		
		
		
		//// Simple delegates :3 ////equivalent
		
		@Override
		public int size()
		{
			return underlying.size();
		}
		
		public char removeCharByIndex(int index)
		{
			return underlying.removeCharByIndex(index);
		}
		
		public void removeByIndex(int index)
		{
			underlying.removeByIndex(index);
		}
		
		/**
		 * Remove all elements in the range (exclusive, like String).<br>
		 * Then shifts all elements at- and to the right of- the <code>end</code>'th element left by the size() of the block removed to 'fill the void'.<br>
		 */
		public void removeRange(int start, int end)
		{
			underlying.removeRange(start, end);
		}
		
		@Override
		public void clear()
		{
			underlying.clear();
		}
		
		public char getChar(int index)
		{
			return underlying.getChar(index);
		}
		
		@Override
		public void storeBoxingIntoArray(Slice<Object[]> array)
		{
			underlying.storeBoxingIntoArray(array);
		}
		
		@Override
		public SimpleCharacterIterator newSimpleCharacterIterator()
		{
			return underlying.newSimpleCharacterIterator();
		}
		
		@Override
		public SortedCharacterSetBackedByList clone()
		{
			return new SortedCharacterSetBackedByList(underlying.clone());
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * A set of primitives with the constraint that no two elements are duplicates and that
	 * the collection is always sorted (ascending).
	 */
	public static class SortedShortSetBackedByList
	implements DefaultToArraysShortCollection, UnderlyingInstanceAccessible<ShortList>, KnowsLengthFixedness
	{
		protected ShortList underlying;
		
		public SortedShortSetBackedByList()
		{
			this(new ShortArrayList());
		}
		
		/**
		 * NOTE THAT IF IT'S NOT EMPTY IT MUST ALREADY BE SORTED!!
		 */
		public SortedShortSetBackedByList(@LiveValue ShortList presortedOrEmptyUnderlying)
		{
			this.underlying = requireNonNull(presortedOrEmptyUnderlying);
		}
		
		
		
		
		
		@LiveValue
		@Override
		public ShortList getUnderlying()
		{
			return underlying;
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.getUnderlying());
		}
		
		
		
		
		
		
		
		//No easy way to add elements in bulk (that I'm aware of, or have the time to implement)
		
		@Override
		public boolean addShort(short value)
		{
			int insertionPoint = SortingUtilities.findInsertionPointInSet(underlying, 0, size(), value);
			
			if (insertionPoint < 0)
			{
				//Duplicate
				return false;
			}
			else
			{
				underlying.insertShort(insertionPoint, value);
				return true;
			}
		}
		
		public int indexOfShort(short value)
		{
			//Better algorithm, no difference in API
			return SortingUtilities.findIndexForValueInSortedSet(underlying, 0, size(), value);
		}
		
		
		
		
		
		////  Use the better impl.s! :D  ////
		
		@Override
		public boolean removeShort(short value)
		{
			int i = indexOfShort(value);
			
			if (i == -1)
			{
				return false;
			}
			else
			{
				this.removeByIndex(i);
				return true;
			}
		}
		
		@Override
		public boolean containsShort(short value)
		{
			return indexOfShort(value) != -1;
		}
		
		/**
		 * A MUCH faster implementation (practically in every case and asymptotically! :D )
		 */
		public boolean equivalent(SortedShortSetBackedByList other)
		{
			return this.underlying.equivalent(other.getUnderlying());
		}
		
		
		
		
		
		
		
		
		
		@Override
		public short[] toShortArray()
		{
			return toSortedShortArray();
		}
		
		/**
		 * This is a synonym to {@link #toShortArray()} which should probably be used in preference to {@link #toShortArray()} if your code relies on its sorted-ness, at least for clarity :>
		 * (Useful if someone else comes by and switches the instantiation to, say, Set&lt;Integer&gt; X'D )
		 */
		@ThrowAwayValue
		public short[] toSortedShortArray()
		{
			return underlying.toShortArray();
		}
		
		
		@Override
		public boolean retainAll(Collection<?> c)
		{
			//Can you think of a better way that relies on using this.contains() instead of c.contains() ??
			return underlying.retainAll(c);
		}
		
		
		
		//NOTE!  There is no set short (by index)!  It could easily de-sort the elements! XD      Remove it then add it again :3
		
		
		
		//// Simple delegates :3 ////equivalent
		
		@Override
		public int size()
		{
			return underlying.size();
		}
		
		public short removeShortByIndex(int index)
		{
			return underlying.removeShortByIndex(index);
		}
		
		public void removeByIndex(int index)
		{
			underlying.removeByIndex(index);
		}
		
		/**
		 * Remove all elements in the range (exclusive, like String).<br>
		 * Then shifts all elements at- and to the right of- the <code>end</code>'th element left by the size() of the block removed to 'fill the void'.<br>
		 */
		public void removeRange(int start, int end)
		{
			underlying.removeRange(start, end);
		}
		
		@Override
		public void clear()
		{
			underlying.clear();
		}
		
		public short getShort(int index)
		{
			return underlying.getShort(index);
		}
		
		@Override
		public void storeBoxingIntoArray(Slice<Object[]> array)
		{
			underlying.storeBoxingIntoArray(array);
		}
		
		@Override
		public SimpleShortIterator newSimpleShortIterator()
		{
			return underlying.newSimpleShortIterator();
		}
		
		@Override
		public SortedShortSetBackedByList clone()
		{
			return new SortedShortSetBackedByList(underlying.clone());
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * A set of primitives with the constraint that no two elements are duplicates and that
	 * the collection is always sorted (ascending).
	 */
	public static class SortedFloatSetBackedByList
	implements DefaultToArraysFloatCollection, UnderlyingInstanceAccessible<FloatList>, KnowsLengthFixedness
	{
		protected FloatList underlying;
		
		public SortedFloatSetBackedByList()
		{
			this(new FloatArrayList());
		}
		
		/**
		 * NOTE THAT IF IT'S NOT EMPTY IT MUST ALREADY BE SORTED!!
		 */
		public SortedFloatSetBackedByList(@LiveValue FloatList presortedOrEmptyUnderlying)
		{
			this.underlying = requireNonNull(presortedOrEmptyUnderlying);
		}
		
		
		
		
		
		@LiveValue
		@Override
		public FloatList getUnderlying()
		{
			return underlying;
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.getUnderlying());
		}
		
		
		
		
		
		
		
		//No easy way to add elements in bulk (that I'm aware of, or have the time to implement)
		
		@Override
		public boolean addFloat(float value)
		{
			int insertionPoint = SortingUtilities.findInsertionPointInSet(underlying, 0, size(), value);
			
			if (insertionPoint < 0)
			{
				//Duplicate
				return false;
			}
			else
			{
				underlying.insertFloat(insertionPoint, value);
				return true;
			}
		}
		
		public int indexOfFloat(float value)
		{
			//Better algorithm, no difference in API
			return SortingUtilities.findIndexForValueInSortedSet(underlying, 0, size(), value);
		}
		
		
		
		
		
		////  Use the better impl.s! :D  ////
		
		@Override
		public boolean removeFloat(float value)
		{
			int i = indexOfFloat(value);
			
			if (i == -1)
			{
				return false;
			}
			else
			{
				this.removeByIndex(i);
				return true;
			}
		}
		
		@Override
		public boolean containsFloat(float value)
		{
			return indexOfFloat(value) != -1;
		}
		
		/**
		 * A MUCH faster implementation (practically in every case and asymptotically! :D )
		 */
		public boolean equivalent(SortedFloatSetBackedByList other)
		{
			return this.underlying.equivalent(other.getUnderlying());
		}
		
		
		
		
		
		
		
		
		
		@Override
		public float[] toFloatArray()
		{
			return toSortedFloatArray();
		}
		
		/**
		 * This is a synonym to {@link #toFloatArray()} which should probably be used in preference to {@link #toFloatArray()} if your code relies on its sorted-ness, at least for clarity :>
		 * (Useful if someone else comes by and switches the instantiation to, say, Set&lt;Integer&gt; X'D )
		 */
		@ThrowAwayValue
		public float[] toSortedFloatArray()
		{
			return underlying.toFloatArray();
		}
		
		
		@Override
		public boolean retainAll(Collection<?> c)
		{
			//Can you think of a better way that relies on using this.contains() instead of c.contains() ??
			return underlying.retainAll(c);
		}
		
		
		
		//NOTE!  There is no set float (by index)!  It could easily de-sort the elements! XD      Remove it then add it again :3
		
		
		
		//// Simple delegates :3 ////equivalent
		
		@Override
		public int size()
		{
			return underlying.size();
		}
		
		public float removeFloatByIndex(int index)
		{
			return underlying.removeFloatByIndex(index);
		}
		
		public void removeByIndex(int index)
		{
			underlying.removeByIndex(index);
		}
		
		/**
		 * Remove all elements in the range (exclusive, like String).<br>
		 * Then shifts all elements at- and to the right of- the <code>end</code>'th element left by the size() of the block removed to 'fill the void'.<br>
		 */
		public void removeRange(int start, int end)
		{
			underlying.removeRange(start, end);
		}
		
		@Override
		public void clear()
		{
			underlying.clear();
		}
		
		public float getFloat(int index)
		{
			return underlying.getFloat(index);
		}
		
		@Override
		public void storeBoxingIntoArray(Slice<Object[]> array)
		{
			underlying.storeBoxingIntoArray(array);
		}
		
		@Override
		public SimpleFloatIterator newSimpleFloatIterator()
		{
			return underlying.newSimpleFloatIterator();
		}
		
		@Override
		public SortedFloatSetBackedByList clone()
		{
			return new SortedFloatSetBackedByList(underlying.clone());
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * A set of primitives with the constraint that no two elements are duplicates and that
	 * the collection is always sorted (ascending).
	 */
	public static class SortedIntegerSetBackedByList
	implements DefaultToArraysIntegerCollection, UnderlyingInstanceAccessible<IntegerList>, KnowsLengthFixedness
	{
		protected IntegerList underlying;
		
		public SortedIntegerSetBackedByList()
		{
			this(new IntegerArrayList());
		}
		
		/**
		 * NOTE THAT IF IT'S NOT EMPTY IT MUST ALREADY BE SORTED!!
		 */
		public SortedIntegerSetBackedByList(@LiveValue IntegerList presortedOrEmptyUnderlying)
		{
			this.underlying = requireNonNull(presortedOrEmptyUnderlying);
		}
		
		
		
		
		
		@LiveValue
		@Override
		public IntegerList getUnderlying()
		{
			return underlying;
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.getUnderlying());
		}
		
		
		
		
		
		
		
		//No easy way to add elements in bulk (that I'm aware of, or have the time to implement)
		
		@Override
		public boolean addInt(int value)
		{
			int insertionPoint = SortingUtilities.findInsertionPointInSet(underlying, 0, size(), value);
			
			if (insertionPoint < 0)
			{
				//Duplicate
				return false;
			}
			else
			{
				underlying.insertInt(insertionPoint, value);
				return true;
			}
		}
		
		public int indexOfInt(int value)
		{
			//Better algorithm, no difference in API
			return SortingUtilities.findIndexForValueInSortedSet(underlying, 0, size(), value);
		}
		
		
		
		
		
		////  Use the better impl.s! :D  ////
		
		@Override
		public boolean removeInt(int value)
		{
			int i = indexOfInt(value);
			
			if (i == -1)
			{
				return false;
			}
			else
			{
				this.removeByIndex(i);
				return true;
			}
		}
		
		@Override
		public boolean containsInt(int value)
		{
			return indexOfInt(value) != -1;
		}
		
		/**
		 * A MUCH faster implementation (practically in every case and asymptotically! :D )
		 */
		public boolean equivalent(SortedIntegerSetBackedByList other)
		{
			return this.underlying.equivalent(other.getUnderlying());
		}
		
		
		
		
		
		
		
		
		
		@Override
		public int[] toIntArray()
		{
			return toSortedIntArray();
		}
		
		/**
		 * This is a synonym to {@link #toIntArray()} which should probably be used in preference to {@link #toIntArray()} if your code relies on its sorted-ness, at least for clarity :>
		 * (Useful if someone else comes by and switches the instantiation to, say, Set&lt;Integer&gt; X'D )
		 */
		@ThrowAwayValue
		public int[] toSortedIntArray()
		{
			return underlying.toIntArray();
		}
		
		
		@Override
		public boolean retainAll(Collection<?> c)
		{
			//Can you think of a better way that relies on using this.contains() instead of c.contains() ??
			return underlying.retainAll(c);
		}
		
		
		
		//NOTE!  There is no set int (by index)!  It could easily de-sort the elements! XD      Remove it then add it again :3
		
		
		
		//// Simple delegates :3 ////equivalent
		
		@Override
		public int size()
		{
			return underlying.size();
		}
		
		public int removeIntByIndex(int index)
		{
			return underlying.removeIntByIndex(index);
		}
		
		public void removeByIndex(int index)
		{
			underlying.removeByIndex(index);
		}
		
		/**
		 * Remove all elements in the range (exclusive, like String).<br>
		 * Then shifts all elements at- and to the right of- the <code>end</code>'th element left by the size() of the block removed to 'fill the void'.<br>
		 */
		public void removeRange(int start, int end)
		{
			underlying.removeRange(start, end);
		}
		
		@Override
		public void clear()
		{
			underlying.clear();
		}
		
		public int getInt(int index)
		{
			return underlying.getInt(index);
		}
		
		@Override
		public void storeBoxingIntoArray(Slice<Object[]> array)
		{
			underlying.storeBoxingIntoArray(array);
		}
		
		@Override
		public SimpleIntegerIterator newSimpleIntegerIterator()
		{
			return underlying.newSimpleIntegerIterator();
		}
		
		@Override
		public SortedIntegerSetBackedByList clone()
		{
			return new SortedIntegerSetBackedByList(underlying.clone());
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * A set of primitives with the constraint that no two elements are duplicates and that
	 * the collection is always sorted (ascending).
	 */
	public static class SortedDoubleSetBackedByList
	implements DefaultToArraysDoubleCollection, UnderlyingInstanceAccessible<DoubleList>, KnowsLengthFixedness
	{
		protected DoubleList underlying;
		
		public SortedDoubleSetBackedByList()
		{
			this(new DoubleArrayList());
		}
		
		/**
		 * NOTE THAT IF IT'S NOT EMPTY IT MUST ALREADY BE SORTED!!
		 */
		public SortedDoubleSetBackedByList(@LiveValue DoubleList presortedOrEmptyUnderlying)
		{
			this.underlying = requireNonNull(presortedOrEmptyUnderlying);
		}
		
		
		
		
		
		@LiveValue
		@Override
		public DoubleList getUnderlying()
		{
			return underlying;
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.getUnderlying());
		}
		
		
		
		
		
		
		
		//No easy way to add elements in bulk (that I'm aware of, or have the time to implement)
		
		@Override
		public boolean addDouble(double value)
		{
			int insertionPoint = SortingUtilities.findInsertionPointInSet(underlying, 0, size(), value);
			
			if (insertionPoint < 0)
			{
				//Duplicate
				return false;
			}
			else
			{
				underlying.insertDouble(insertionPoint, value);
				return true;
			}
		}
		
		public int indexOfDouble(double value)
		{
			//Better algorithm, no difference in API
			return SortingUtilities.findIndexForValueInSortedSet(underlying, 0, size(), value);
		}
		
		
		
		
		
		////  Use the better impl.s! :D  ////
		
		@Override
		public boolean removeDouble(double value)
		{
			int i = indexOfDouble(value);
			
			if (i == -1)
			{
				return false;
			}
			else
			{
				this.removeByIndex(i);
				return true;
			}
		}
		
		@Override
		public boolean containsDouble(double value)
		{
			return indexOfDouble(value) != -1;
		}
		
		/**
		 * A MUCH faster implementation (practically in every case and asymptotically! :D )
		 */
		public boolean equivalent(SortedDoubleSetBackedByList other)
		{
			return this.underlying.equivalent(other.getUnderlying());
		}
		
		
		
		
		
		
		
		
		
		@Override
		public double[] toDoubleArray()
		{
			return toSortedDoubleArray();
		}
		
		/**
		 * This is a synonym to {@link #toDoubleArray()} which should probably be used in preference to {@link #toDoubleArray()} if your code relies on its sorted-ness, at least for clarity :>
		 * (Useful if someone else comes by and switches the instantiation to, say, Set&lt;Integer&gt; X'D )
		 */
		@ThrowAwayValue
		public double[] toSortedDoubleArray()
		{
			return underlying.toDoubleArray();
		}
		
		
		@Override
		public boolean retainAll(Collection<?> c)
		{
			//Can you think of a better way that relies on using this.contains() instead of c.contains() ??
			return underlying.retainAll(c);
		}
		
		
		
		//NOTE!  There is no set double (by index)!  It could easily de-sort the elements! XD      Remove it then add it again :3
		
		
		
		//// Simple delegates :3 ////equivalent
		
		@Override
		public int size()
		{
			return underlying.size();
		}
		
		public double removeDoubleByIndex(int index)
		{
			return underlying.removeDoubleByIndex(index);
		}
		
		public void removeByIndex(int index)
		{
			underlying.removeByIndex(index);
		}
		
		/**
		 * Remove all elements in the range (exclusive, like String).<br>
		 * Then shifts all elements at- and to the right of- the <code>end</code>'th element left by the size() of the block removed to 'fill the void'.<br>
		 */
		public void removeRange(int start, int end)
		{
			underlying.removeRange(start, end);
		}
		
		@Override
		public void clear()
		{
			underlying.clear();
		}
		
		public double getDouble(int index)
		{
			return underlying.getDouble(index);
		}
		
		@Override
		public void storeBoxingIntoArray(Slice<Object[]> array)
		{
			underlying.storeBoxingIntoArray(array);
		}
		
		@Override
		public SimpleDoubleIterator newSimpleDoubleIterator()
		{
			return underlying.newSimpleDoubleIterator();
		}
		
		@Override
		public SortedDoubleSetBackedByList clone()
		{
			return new SortedDoubleSetBackedByList(underlying.clone());
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * A set of primitives with the constraint that no two elements are duplicates and that
	 * the collection is always sorted (ascending).
	 */
	public static class SortedLongSetBackedByList
	implements DefaultToArraysLongCollection, UnderlyingInstanceAccessible<LongList>, KnowsLengthFixedness
	{
		protected LongList underlying;
		
		public SortedLongSetBackedByList()
		{
			this(new LongArrayList());
		}
		
		/**
		 * NOTE THAT IF IT'S NOT EMPTY IT MUST ALREADY BE SORTED!!
		 */
		public SortedLongSetBackedByList(@LiveValue LongList presortedOrEmptyUnderlying)
		{
			this.underlying = requireNonNull(presortedOrEmptyUnderlying);
		}
		
		
		
		
		
		@LiveValue
		@Override
		public LongList getUnderlying()
		{
			return underlying;
		}
		
		@Override
		public Boolean isFixedLengthNotVariableLength()
		{
			return PolymorphicCollectionUtilities.isFixedLengthNotVariableLength(this.getUnderlying());
		}
		
		
		
		
		
		
		
		//No easy way to add elements in bulk (that I'm aware of, or have the time to implement)
		
		@Override
		public boolean addLong(long value)
		{
			int insertionPoint = SortingUtilities.findInsertionPointInSet(underlying, 0, size(), value);
			
			if (insertionPoint < 0)
			{
				//Duplicate
				return false;
			}
			else
			{
				underlying.insertLong(insertionPoint, value);
				return true;
			}
		}
		
		public int indexOfLong(long value)
		{
			//Better algorithm, no difference in API
			return SortingUtilities.findIndexForValueInSortedSet(underlying, 0, size(), value);
		}
		
		
		
		
		
		////  Use the better impl.s! :D  ////
		
		@Override
		public boolean removeLong(long value)
		{
			int i = indexOfLong(value);
			
			if (i == -1)
			{
				return false;
			}
			else
			{
				this.removeByIndex(i);
				return true;
			}
		}
		
		@Override
		public boolean containsLong(long value)
		{
			return indexOfLong(value) != -1;
		}
		
		/**
		 * A MUCH faster implementation (practically in every case and asymptotically! :D )
		 */
		public boolean equivalent(SortedLongSetBackedByList other)
		{
			return this.underlying.equivalent(other.getUnderlying());
		}
		
		
		
		
		
		
		
		
		
		@Override
		public long[] toLongArray()
		{
			return toSortedLongArray();
		}
		
		/**
		 * This is a synonym to {@link #toLongArray()} which should probably be used in preference to {@link #toLongArray()} if your code relies on its sorted-ness, at least for clarity :>
		 * (Useful if someone else comes by and switches the instantiation to, say, Set&lt;Integer&gt; X'D )
		 */
		@ThrowAwayValue
		public long[] toSortedLongArray()
		{
			return underlying.toLongArray();
		}
		
		
		@Override
		public boolean retainAll(Collection<?> c)
		{
			//Can you think of a better way that relies on using this.contains() instead of c.contains() ??
			return underlying.retainAll(c);
		}
		
		
		
		//NOTE!  There is no set long (by index)!  It could easily de-sort the elements! XD      Remove it then add it again :3
		
		
		
		//// Simple delegates :3 ////equivalent
		
		@Override
		public int size()
		{
			return underlying.size();
		}
		
		public long removeLongByIndex(int index)
		{
			return underlying.removeLongByIndex(index);
		}
		
		public void removeByIndex(int index)
		{
			underlying.removeByIndex(index);
		}
		
		/**
		 * Remove all elements in the range (exclusive, like String).<br>
		 * Then shifts all elements at- and to the right of- the <code>end</code>'th element left by the size() of the block removed to 'fill the void'.<br>
		 */
		public void removeRange(int start, int end)
		{
			underlying.removeRange(start, end);
		}
		
		@Override
		public void clear()
		{
			underlying.clear();
		}
		
		public long getLong(int index)
		{
			return underlying.getLong(index);
		}
		
		@Override
		public void storeBoxingIntoArray(Slice<Object[]> array)
		{
			underlying.storeBoxingIntoArray(array);
		}
		
		@Override
		public SimpleLongIterator newSimpleLongIterator()
		{
			return underlying.newSimpleLongIterator();
		}
		
		@Override
		public SortedLongSetBackedByList clone()
		{
			return new SortedLongSetBackedByList(underlying.clone());
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static byte scintervalByte(int x)
	{
		return safeCastS32toS8(x);
	}
	
	private static short scintervalShort(int x)
	{
		return safeCastS32toS16(x);
	}
	
	private static char scintervalChar(int x)
	{
		return safeCastS32toU16(x);
	}
	
	private static int scintervalInt(int x)
	{
		return x;
	}
	
	private static long scintervalLong(long x)
	{
		return x;
	}
	
	
	
	
	/* <<<
primxp
_$$primxpconf:intsonly$$_
	
	
	
	
	// Todo: Recognition in bulk operations!  Eg, containsAll()! ^^'''
	@SignalInterface
	public static interface _$$Primitive$$_IntervalCollection
	extends _$$Primitive$$_Collection
	{
		@Override
		public int size();
		
		public _$$prim$$_ getLowerBoundInclusive();
	}
	
	
	
	@ReadonlyValue
	public static Sorted_$$Primitive$$_SetBackedByList newSorted_$$Primitive$$_SetForRange(_$$prim$$_ inclusiveLowBound, int count)
	{
		return new Sorted_$$Primitive$$_SetBackedByList(interval_$$Primitive$$_sList(inclusiveLowBound, count));
	}
	
	@ThrowAwayValue
	public static Sorted_$$Primitive$$_SetBackedByList newSorted_$$Primitive$$_SetForRangeMutable(_$$prim$$_ inclusiveLowBound, int count)
	{
		return new Sorted_$$Primitive$$_SetBackedByList(new _$$Primitive$$_ArrayList(interval_$$Primitive$$_sArray(inclusiveLowBound, count)));
	}
	
	
	
	public static interface Default_$$Primitive$$_IntervalCollection
	extends _$$Primitive$$_IntervalCollection, SimpleIterable<_$$Primitive$$_>
	{
		@Override
		public default boolean contains_$$Prim$$_(_$$prim$$_ i)
		{
			return i >= getLowerBoundInclusive() && i < getLowerBoundInclusive()+size();
		}
		
		
		
		@Override
		public default Iterator<_$$Primitive$$_> iterator()
		{
			final _$$prim$$_ lb = (_$$prim$$_)getLowerBoundInclusive();
			final int s = this.size();
			
			return new Iterator<_$$Primitive$$_>()
			{
				int next = 0;
				
				@Override
				public _$$Primitive$$_ next()
				{
					int next = this.next;
					
					if (next >= s)
						throw new NoSuchElementException();
					
					this.next = next + 1;
					
					return (_$$prim$$_)(next + lb);
				}
				
				@Override
				public boolean hasNext()
				{
					return this.next < size();
				}
			};
		}
		
		
		@Override
		public default SimpleIterator<_$$Primitive$$_> simpleIterator()
		{
			final _$$prim$$_ lb = getLowerBoundInclusive();
			final int s = size();
			
			return new SimpleIterator<_$$Primitive$$_>
			()
			{
				int next = 0;
				
				@Override
				public _$$Primitive$$_ nextrp() throws StopIterationReturnPath
				{
					int next = this.next;
					
					if (next >= s)
						throw StopIterationReturnPath.I;
					
					this.next = next + 1;
					
					return (_$$prim$$_)(next + lb);
				}
			};
		}
		
		
		@Override
		public default Simple_$$Primitive$$_Iterator newSimple_$$Primitive$$_Iterator()
		{
			final _$$prim$$_ lb = getLowerBoundInclusive();
			final int s = size();
			
			return new Simple_$$Primitive$$_Iterator
			()
			{
				int next = 0;
				
				@Override
				public _$$prim$$_ nextrp_$$Prim$$_() throws StopIterationReturnPath
				{
					int next = this.next;
					
					if (next >= s)
						throw StopIterationReturnPath.I;
					
					this.next = next + 1;
					
					return (_$$prim$$_)(next + lb);
				}
			};
		}
		
		
		
		@Override
		public default _$$prim$$_[] to_$$Prim$$_Array()
		{
			return interval_$$Primitive$$_sArray(this.getLowerBoundInclusive(), this.size());
		}
	}
	
	
	
	
	
	
	public static class Immutable_$$Primitive$$_IntervalSet
	extends AbstractReadonlySet<_$$Primitive$$_>
	implements StaticallyConcurrentlyImmutable, Default_$$Primitive$$_IntervalCollection
	{
		protected final _$$prim$$_ lowerBoundInclusive;
		protected final int size;
		
		public Immutable_$$Primitive$$_IntervalSet(_$$prim$$_ lowerBoundInclusive, @Nonnegative int size)
		{
			if (size < 0)  throw new IllegalArgumentException("negative size: "+size);
			if (doesIntegerIntervalOverflow_$$Prim$$_WithIntSize(lowerBoundInclusive, size))  throw new OverflowException();
			
			this.lowerBoundInclusive = lowerBoundInclusive;
			this.size = size;
		}
		
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public _$$prim$$_ getLowerBoundInclusive()
		{
			return lowerBoundInclusive;
		}
		
		
		
		
		
		@Override
		public boolean isEmpty()
		{
			return Default_$$Primitive$$_IntervalCollection.super.isEmpty();
		}
		
		@Override
		public boolean contains(Object o)
		{
			return Default_$$Primitive$$_IntervalCollection.super.contains(o);
		}
		
		@Override
		public SimpleIterator<_$$Primitive$$_> simpleIterator()
		{
			return Default_$$Primitive$$_IntervalCollection.super.simpleIterator();
		}
		
		@Override
		public Iterator<_$$Primitive$$_> iterator()
		{
			return Default_$$Primitive$$_IntervalCollection.super.iterator();
		}
		
		@Override
		public boolean add_$$Prim$$_(_$$prim$$_ value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public boolean remove_$$Prim$$_(_$$prim$$_ value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	public static class Immutable_$$Primitive$$_IntervalList
	extends AbstractReadonlyList<_$$Primitive$$_>
	implements StaticallyConcurrentlyImmutable, Default_$$Primitive$$_IntervalCollection, _$$Primitive$$_List
	{
		protected final _$$prim$$_ lowerBoundInclusive;
		protected final int size;
		
		public Immutable_$$Primitive$$_IntervalList(_$$prim$$_ lowerBoundInclusive, @Nonnegative int size)
		{
			if (size < 0)  throw new IllegalArgumentException("negative size: "+size);
			if (doesIntegerIntervalOverflow_$$Prim$$_WithIntSize(lowerBoundInclusive, size))  throw new OverflowException();
			
			this.lowerBoundInclusive = lowerBoundInclusive;
			this.size = size;
		}
		
		
		@Override
		public _$$Primitive$$_List subList(int fromIndex, int toIndex)
		{
			rangeCheckInterval(this.size(), fromIndex, toIndex);
			
			_$$prim$$_ l = this.lowerBoundInclusive;
			return new Immutable_$$Primitive$$_IntervalList(scinterval_$$Prim$$_(l+fromIndex), toIndex-fromIndex);
		}
		
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public _$$prim$$_ getLowerBoundInclusive()
		{
			return lowerBoundInclusive;
		}
		
		@Override
		public _$$Primitive$$_List clone()
		{
			return this;
		}
		
		
		
		
		
		@Override
		public _$$prim$$_[] to_$$Prim$$_Array()
		{
			return Default_$$Primitive$$_IntervalCollection.super.to_$$Prim$$_Array();   //this is a better impl than the default IntegerList one :D
		}
		
		@Override
		public Simple_$$Primitive$$_Iterator newSimple_$$Primitive$$_Iterator()
		{
			return Default_$$Primitive$$_IntervalCollection.super.newSimple_$$Primitive$$_Iterator();   //this is a better impl than the default IntegerList one :D
		}
		
		@Override
		public boolean contains_$$Prim$$_(_$$prim$$_ i)
		{
			return Default_$$Primitive$$_IntervalCollection.super.contains_$$Prim$$_(i);   //this is a better impl than the default IntegerList one :D
		}
		
		
		
		
		@Override
		public _$$prim$$_ get_$$Prim$$_(int index)
		{
			if (index < 0)  throw new IndexOutOfBoundsException();
			if (index >= size)  throw new IndexOutOfBoundsException();
			return (_$$prim$$_)(index + lowerBoundInclusive);
		}
		
		@Override
		public _$$Primitive$$_ get(int index)
		{
			return _$$Primitive$$_List.super.get(index);
		}
		
		
		@Override
		public int indexOf(Object o)
		{
			//For longs, since the size is an int >= 0, if it contains() a value, then that value's index will be a valid S32 integer, so no need for overflow checking here :>
			return contains(o) ? (int)(((_$$Primitive$$_)o) - lowerBoundInclusive) : -1;
		}
		
		@Override
		public int lastIndexOf(Object o)
		{
			return indexOf(o);
		}
		
		
		
		
		
		@Override
		public SimpleIterator<_$$Primitive$$_> simpleIterator()
		{
			return Default_$$Primitive$$_IntervalCollection.super.simpleIterator();
		}
		
		@Override
		public Iterator<_$$Primitive$$_> iterator()
		{
			return Default_$$Primitive$$_IntervalCollection.super.iterator();
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public boolean add_$$Prim$$_(_$$prim$$_ value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void insert_$$Prim$$_(int index, _$$prim$$_ value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public boolean remove_$$Prim$$_(_$$prim$$_ value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int fromIndex, int toIndex)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void set_$$Prim$$_(int index, _$$prim$$_ value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setSize_$$Prim$$_(int newSize, _$$prim$$_ elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	 */
	
	
	
	
	
	// Todo: Recognition in bulk operations!  Eg, containsAll()! ^^'''
	@SignalInterface
	public static interface ByteIntervalCollection
	extends ByteCollection
	{
		@Override
		public int size();
		
		public byte getLowerBoundInclusive();
	}
	
	
	
	@ReadonlyValue
	public static SortedByteSetBackedByList newSortedByteSetForRange(byte inclusiveLowBound, int count)
	{
		return new SortedByteSetBackedByList(intervalBytesList(inclusiveLowBound, count));
	}
	
	@ThrowAwayValue
	public static SortedByteSetBackedByList newSortedByteSetForRangeMutable(byte inclusiveLowBound, int count)
	{
		return new SortedByteSetBackedByList(new ByteArrayList(intervalBytesArray(inclusiveLowBound, count)));
	}
	
	
	
	public static interface DefaultByteIntervalCollection
	extends ByteIntervalCollection, SimpleIterable<Byte>
	{
		@Override
		public default boolean containsByte(byte i)
		{
			return i >= getLowerBoundInclusive() && i < getLowerBoundInclusive()+size();
		}
		
		
		
		@Override
		public default Iterator<Byte> iterator()
		{
			final byte lb = (byte)getLowerBoundInclusive();
			final int s = this.size();
			
			return new Iterator<Byte>()
			{
				int next = 0;
				
				@Override
				public Byte next()
				{
					int next = this.next;
					
					if (next >= s)
						throw new NoSuchElementException();
					
					this.next = next + 1;
					
					return (byte)(next + lb);
				}
				
				@Override
				public boolean hasNext()
				{
					return this.next < size();
				}
			};
		}
		
		
		@Override
		public default SimpleIterator<Byte> simpleIterator()
		{
			final byte lb = getLowerBoundInclusive();
			final int s = size();
			
			return new SimpleIterator<Byte>
			()
			{
				int next = 0;
				
				@Override
				public Byte nextrp() throws StopIterationReturnPath
				{
					int next = this.next;
					
					if (next >= s)
						throw StopIterationReturnPath.I;
					
					this.next = next + 1;
					
					return (byte)(next + lb);
				}
			};
		}
		
		
		@Override
		public default SimpleByteIterator newSimpleByteIterator()
		{
			final byte lb = getLowerBoundInclusive();
			final int s = size();
			
			return new SimpleByteIterator
			()
			{
				int next = 0;
				
				@Override
				public byte nextrpByte() throws StopIterationReturnPath
				{
					int next = this.next;
					
					if (next >= s)
						throw StopIterationReturnPath.I;
					
					this.next = next + 1;
					
					return (byte)(next + lb);
				}
			};
		}
		
		
		
		@Override
		public default byte[] toByteArray()
		{
			return intervalBytesArray(this.getLowerBoundInclusive(), this.size());
		}
	}
	
	
	
	
	
	
	public static class ImmutableByteIntervalSet
	extends AbstractReadonlySet<Byte>
	implements StaticallyConcurrentlyImmutable, DefaultByteIntervalCollection
	{
		protected final byte lowerBoundInclusive;
		protected final int size;
		
		public ImmutableByteIntervalSet(byte lowerBoundInclusive, @Nonnegative int size)
		{
			if (size < 0)  throw new IllegalArgumentException("negative size: "+size);
			if (doesIntegerIntervalOverflowByteWithIntSize(lowerBoundInclusive, size))  throw new OverflowException();
			
			this.lowerBoundInclusive = lowerBoundInclusive;
			this.size = size;
		}
		
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public byte getLowerBoundInclusive()
		{
			return lowerBoundInclusive;
		}
		
		
		
		
		
		@Override
		public boolean isEmpty()
		{
			return DefaultByteIntervalCollection.super.isEmpty();
		}
		
		@Override
		public boolean contains(Object o)
		{
			return DefaultByteIntervalCollection.super.contains(o);
		}
		
		@Override
		public SimpleIterator<Byte> simpleIterator()
		{
			return DefaultByteIntervalCollection.super.simpleIterator();
		}
		
		@Override
		public Iterator<Byte> iterator()
		{
			return DefaultByteIntervalCollection.super.iterator();
		}
		
		@Override
		public boolean addByte(byte value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public boolean removeByte(byte value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	public static class ImmutableByteIntervalList
	extends AbstractReadonlyList<Byte>
	implements StaticallyConcurrentlyImmutable, DefaultByteIntervalCollection, ByteList
	{
		protected final byte lowerBoundInclusive;
		protected final int size;
		
		public ImmutableByteIntervalList(byte lowerBoundInclusive, @Nonnegative int size)
		{
			if (size < 0)  throw new IllegalArgumentException("negative size: "+size);
			if (doesIntegerIntervalOverflowByteWithIntSize(lowerBoundInclusive, size))  throw new OverflowException();
			
			this.lowerBoundInclusive = lowerBoundInclusive;
			this.size = size;
		}
		
		
		@Override
		public ByteList subList(int fromIndex, int toIndex)
		{
			rangeCheckInterval(this.size(), fromIndex, toIndex);
			
			byte l = this.lowerBoundInclusive;
			return new ImmutableByteIntervalList(scintervalByte(l+fromIndex), toIndex-fromIndex);
		}
		
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public byte getLowerBoundInclusive()
		{
			return lowerBoundInclusive;
		}
		
		@Override
		public ByteList clone()
		{
			return this;
		}
		
		
		
		
		
		@Override
		public byte[] toByteArray()
		{
			return DefaultByteIntervalCollection.super.toByteArray();   //this is a better impl than the default IntegerList one :D
		}
		
		@Override
		public SimpleByteIterator newSimpleByteIterator()
		{
			return DefaultByteIntervalCollection.super.newSimpleByteIterator();   //this is a better impl than the default IntegerList one :D
		}
		
		@Override
		public boolean containsByte(byte i)
		{
			return DefaultByteIntervalCollection.super.containsByte(i);   //this is a better impl than the default IntegerList one :D
		}
		
		
		
		
		@Override
		public byte getByte(int index)
		{
			if (index < 0)  throw new IndexOutOfBoundsException();
			if (index >= size)  throw new IndexOutOfBoundsException();
			return (byte)(index + lowerBoundInclusive);
		}
		
		@Override
		public Byte get(int index)
		{
			return ByteList.super.get(index);
		}
		
		
		@Override
		public int indexOf(Object o)
		{
			//For longs, since the size is an int >= 0, if it contains() a value, then that value's index will be a valid S32 integer, so no need for overflow checking here :>
			return contains(o) ? (int)(((Byte)o) - lowerBoundInclusive) : -1;
		}
		
		@Override
		public int lastIndexOf(Object o)
		{
			return indexOf(o);
		}
		
		
		
		
		
		@Override
		public SimpleIterator<Byte> simpleIterator()
		{
			return DefaultByteIntervalCollection.super.simpleIterator();
		}
		
		@Override
		public Iterator<Byte> iterator()
		{
			return DefaultByteIntervalCollection.super.iterator();
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public boolean addByte(byte value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void insertByte(int index, byte value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public boolean removeByte(byte value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int fromIndex, int toIndex)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setByte(int index, byte value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setSizeByte(int newSize, byte elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// Todo: Recognition in bulk operations!  Eg, containsAll()! ^^'''
	@SignalInterface
	public static interface CharacterIntervalCollection
	extends CharacterCollection
	{
		@Override
		public int size();
		
		public char getLowerBoundInclusive();
	}
	
	
	
	@ReadonlyValue
	public static SortedCharacterSetBackedByList newSortedCharacterSetForRange(char inclusiveLowBound, int count)
	{
		return new SortedCharacterSetBackedByList(intervalCharactersList(inclusiveLowBound, count));
	}
	
	@ThrowAwayValue
	public static SortedCharacterSetBackedByList newSortedCharacterSetForRangeMutable(char inclusiveLowBound, int count)
	{
		return new SortedCharacterSetBackedByList(new CharacterArrayList(intervalCharactersArray(inclusiveLowBound, count)));
	}
	
	
	
	public static interface DefaultCharacterIntervalCollection
	extends CharacterIntervalCollection, SimpleIterable<Character>
	{
		@Override
		public default boolean containsChar(char i)
		{
			return i >= getLowerBoundInclusive() && i < getLowerBoundInclusive()+size();
		}
		
		
		
		@Override
		public default Iterator<Character> iterator()
		{
			final char lb = (char)getLowerBoundInclusive();
			final int s = this.size();
			
			return new Iterator<Character>()
			{
				int next = 0;
				
				@Override
				public Character next()
				{
					int next = this.next;
					
					if (next >= s)
						throw new NoSuchElementException();
					
					this.next = next + 1;
					
					return (char)(next + lb);
				}
				
				@Override
				public boolean hasNext()
				{
					return this.next < size();
				}
			};
		}
		
		
		@Override
		public default SimpleIterator<Character> simpleIterator()
		{
			final char lb = getLowerBoundInclusive();
			final int s = size();
			
			return new SimpleIterator<Character>
			()
			{
				int next = 0;
				
				@Override
				public Character nextrp() throws StopIterationReturnPath
				{
					int next = this.next;
					
					if (next >= s)
						throw StopIterationReturnPath.I;
					
					this.next = next + 1;
					
					return (char)(next + lb);
				}
			};
		}
		
		
		@Override
		public default SimpleCharacterIterator newSimpleCharacterIterator()
		{
			final char lb = getLowerBoundInclusive();
			final int s = size();
			
			return new SimpleCharacterIterator
			()
			{
				int next = 0;
				
				@Override
				public char nextrpChar() throws StopIterationReturnPath
				{
					int next = this.next;
					
					if (next >= s)
						throw StopIterationReturnPath.I;
					
					this.next = next + 1;
					
					return (char)(next + lb);
				}
			};
		}
		
		
		
		@Override
		public default char[] toCharArray()
		{
			return intervalCharactersArray(this.getLowerBoundInclusive(), this.size());
		}
	}
	
	
	
	
	
	
	public static class ImmutableCharacterIntervalSet
	extends AbstractReadonlySet<Character>
	implements StaticallyConcurrentlyImmutable, DefaultCharacterIntervalCollection
	{
		protected final char lowerBoundInclusive;
		protected final int size;
		
		public ImmutableCharacterIntervalSet(char lowerBoundInclusive, @Nonnegative int size)
		{
			if (size < 0)  throw new IllegalArgumentException("negative size: "+size);
			if (doesIntegerIntervalOverflowCharWithIntSize(lowerBoundInclusive, size))  throw new OverflowException();
			
			this.lowerBoundInclusive = lowerBoundInclusive;
			this.size = size;
		}
		
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public char getLowerBoundInclusive()
		{
			return lowerBoundInclusive;
		}
		
		
		
		
		
		@Override
		public boolean isEmpty()
		{
			return DefaultCharacterIntervalCollection.super.isEmpty();
		}
		
		@Override
		public boolean contains(Object o)
		{
			return DefaultCharacterIntervalCollection.super.contains(o);
		}
		
		@Override
		public SimpleIterator<Character> simpleIterator()
		{
			return DefaultCharacterIntervalCollection.super.simpleIterator();
		}
		
		@Override
		public Iterator<Character> iterator()
		{
			return DefaultCharacterIntervalCollection.super.iterator();
		}
		
		@Override
		public boolean addChar(char value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public boolean removeChar(char value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	public static class ImmutableCharacterIntervalList
	extends AbstractReadonlyList<Character>
	implements StaticallyConcurrentlyImmutable, DefaultCharacterIntervalCollection, CharacterList
	{
		protected final char lowerBoundInclusive;
		protected final int size;
		
		public ImmutableCharacterIntervalList(char lowerBoundInclusive, @Nonnegative int size)
		{
			if (size < 0)  throw new IllegalArgumentException("negative size: "+size);
			if (doesIntegerIntervalOverflowCharWithIntSize(lowerBoundInclusive, size))  throw new OverflowException();
			
			this.lowerBoundInclusive = lowerBoundInclusive;
			this.size = size;
		}
		
		
		@Override
		public CharacterList subList(int fromIndex, int toIndex)
		{
			rangeCheckInterval(this.size(), fromIndex, toIndex);
			
			char l = this.lowerBoundInclusive;
			return new ImmutableCharacterIntervalList(scintervalChar(l+fromIndex), toIndex-fromIndex);
		}
		
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public char getLowerBoundInclusive()
		{
			return lowerBoundInclusive;
		}
		
		@Override
		public CharacterList clone()
		{
			return this;
		}
		
		
		
		
		
		@Override
		public char[] toCharArray()
		{
			return DefaultCharacterIntervalCollection.super.toCharArray();   //this is a better impl than the default IntegerList one :D
		}
		
		@Override
		public SimpleCharacterIterator newSimpleCharacterIterator()
		{
			return DefaultCharacterIntervalCollection.super.newSimpleCharacterIterator();   //this is a better impl than the default IntegerList one :D
		}
		
		@Override
		public boolean containsChar(char i)
		{
			return DefaultCharacterIntervalCollection.super.containsChar(i);   //this is a better impl than the default IntegerList one :D
		}
		
		
		
		
		@Override
		public char getChar(int index)
		{
			if (index < 0)  throw new IndexOutOfBoundsException();
			if (index >= size)  throw new IndexOutOfBoundsException();
			return (char)(index + lowerBoundInclusive);
		}
		
		@Override
		public Character get(int index)
		{
			return CharacterList.super.get(index);
		}
		
		
		@Override
		public int indexOf(Object o)
		{
			//For longs, since the size is an int >= 0, if it contains() a value, then that value's index will be a valid S32 integer, so no need for overflow checking here :>
			return contains(o) ? (int)(((Character)o) - lowerBoundInclusive) : -1;
		}
		
		@Override
		public int lastIndexOf(Object o)
		{
			return indexOf(o);
		}
		
		
		
		
		
		@Override
		public SimpleIterator<Character> simpleIterator()
		{
			return DefaultCharacterIntervalCollection.super.simpleIterator();
		}
		
		@Override
		public Iterator<Character> iterator()
		{
			return DefaultCharacterIntervalCollection.super.iterator();
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public boolean addChar(char value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void insertChar(int index, char value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public boolean removeChar(char value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int fromIndex, int toIndex)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setChar(int index, char value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setSizeChar(int newSize, char elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// Todo: Recognition in bulk operations!  Eg, containsAll()! ^^'''
	@SignalInterface
	public static interface ShortIntervalCollection
	extends ShortCollection
	{
		@Override
		public int size();
		
		public short getLowerBoundInclusive();
	}
	
	
	
	@ReadonlyValue
	public static SortedShortSetBackedByList newSortedShortSetForRange(short inclusiveLowBound, int count)
	{
		return new SortedShortSetBackedByList(intervalShortsList(inclusiveLowBound, count));
	}
	
	@ThrowAwayValue
	public static SortedShortSetBackedByList newSortedShortSetForRangeMutable(short inclusiveLowBound, int count)
	{
		return new SortedShortSetBackedByList(new ShortArrayList(intervalShortsArray(inclusiveLowBound, count)));
	}
	
	
	
	public static interface DefaultShortIntervalCollection
	extends ShortIntervalCollection, SimpleIterable<Short>
	{
		@Override
		public default boolean containsShort(short i)
		{
			return i >= getLowerBoundInclusive() && i < getLowerBoundInclusive()+size();
		}
		
		
		
		@Override
		public default Iterator<Short> iterator()
		{
			final short lb = (short)getLowerBoundInclusive();
			final int s = this.size();
			
			return new Iterator<Short>()
			{
				int next = 0;
				
				@Override
				public Short next()
				{
					int next = this.next;
					
					if (next >= s)
						throw new NoSuchElementException();
					
					this.next = next + 1;
					
					return (short)(next + lb);
				}
				
				@Override
				public boolean hasNext()
				{
					return this.next < size();
				}
			};
		}
		
		
		@Override
		public default SimpleIterator<Short> simpleIterator()
		{
			final short lb = getLowerBoundInclusive();
			final int s = size();
			
			return new SimpleIterator<Short>
			()
			{
				int next = 0;
				
				@Override
				public Short nextrp() throws StopIterationReturnPath
				{
					int next = this.next;
					
					if (next >= s)
						throw StopIterationReturnPath.I;
					
					this.next = next + 1;
					
					return (short)(next + lb);
				}
			};
		}
		
		
		@Override
		public default SimpleShortIterator newSimpleShortIterator()
		{
			final short lb = getLowerBoundInclusive();
			final int s = size();
			
			return new SimpleShortIterator
			()
			{
				int next = 0;
				
				@Override
				public short nextrpShort() throws StopIterationReturnPath
				{
					int next = this.next;
					
					if (next >= s)
						throw StopIterationReturnPath.I;
					
					this.next = next + 1;
					
					return (short)(next + lb);
				}
			};
		}
		
		
		
		@Override
		public default short[] toShortArray()
		{
			return intervalShortsArray(this.getLowerBoundInclusive(), this.size());
		}
	}
	
	
	
	
	
	
	public static class ImmutableShortIntervalSet
	extends AbstractReadonlySet<Short>
	implements StaticallyConcurrentlyImmutable, DefaultShortIntervalCollection
	{
		protected final short lowerBoundInclusive;
		protected final int size;
		
		public ImmutableShortIntervalSet(short lowerBoundInclusive, @Nonnegative int size)
		{
			if (size < 0)  throw new IllegalArgumentException("negative size: "+size);
			if (doesIntegerIntervalOverflowShortWithIntSize(lowerBoundInclusive, size))  throw new OverflowException();
			
			this.lowerBoundInclusive = lowerBoundInclusive;
			this.size = size;
		}
		
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public short getLowerBoundInclusive()
		{
			return lowerBoundInclusive;
		}
		
		
		
		
		
		@Override
		public boolean isEmpty()
		{
			return DefaultShortIntervalCollection.super.isEmpty();
		}
		
		@Override
		public boolean contains(Object o)
		{
			return DefaultShortIntervalCollection.super.contains(o);
		}
		
		@Override
		public SimpleIterator<Short> simpleIterator()
		{
			return DefaultShortIntervalCollection.super.simpleIterator();
		}
		
		@Override
		public Iterator<Short> iterator()
		{
			return DefaultShortIntervalCollection.super.iterator();
		}
		
		@Override
		public boolean addShort(short value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public boolean removeShort(short value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	public static class ImmutableShortIntervalList
	extends AbstractReadonlyList<Short>
	implements StaticallyConcurrentlyImmutable, DefaultShortIntervalCollection, ShortList
	{
		protected final short lowerBoundInclusive;
		protected final int size;
		
		public ImmutableShortIntervalList(short lowerBoundInclusive, @Nonnegative int size)
		{
			if (size < 0)  throw new IllegalArgumentException("negative size: "+size);
			if (doesIntegerIntervalOverflowShortWithIntSize(lowerBoundInclusive, size))  throw new OverflowException();
			
			this.lowerBoundInclusive = lowerBoundInclusive;
			this.size = size;
		}
		
		
		@Override
		public ShortList subList(int fromIndex, int toIndex)
		{
			rangeCheckInterval(this.size(), fromIndex, toIndex);
			
			short l = this.lowerBoundInclusive;
			return new ImmutableShortIntervalList(scintervalShort(l+fromIndex), toIndex-fromIndex);
		}
		
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public short getLowerBoundInclusive()
		{
			return lowerBoundInclusive;
		}
		
		@Override
		public ShortList clone()
		{
			return this;
		}
		
		
		
		
		
		@Override
		public short[] toShortArray()
		{
			return DefaultShortIntervalCollection.super.toShortArray();   //this is a better impl than the default IntegerList one :D
		}
		
		@Override
		public SimpleShortIterator newSimpleShortIterator()
		{
			return DefaultShortIntervalCollection.super.newSimpleShortIterator();   //this is a better impl than the default IntegerList one :D
		}
		
		@Override
		public boolean containsShort(short i)
		{
			return DefaultShortIntervalCollection.super.containsShort(i);   //this is a better impl than the default IntegerList one :D
		}
		
		
		
		
		@Override
		public short getShort(int index)
		{
			if (index < 0)  throw new IndexOutOfBoundsException();
			if (index >= size)  throw new IndexOutOfBoundsException();
			return (short)(index + lowerBoundInclusive);
		}
		
		@Override
		public Short get(int index)
		{
			return ShortList.super.get(index);
		}
		
		
		@Override
		public int indexOf(Object o)
		{
			//For longs, since the size is an int >= 0, if it contains() a value, then that value's index will be a valid S32 integer, so no need for overflow checking here :>
			return contains(o) ? (int)(((Short)o) - lowerBoundInclusive) : -1;
		}
		
		@Override
		public int lastIndexOf(Object o)
		{
			return indexOf(o);
		}
		
		
		
		
		
		@Override
		public SimpleIterator<Short> simpleIterator()
		{
			return DefaultShortIntervalCollection.super.simpleIterator();
		}
		
		@Override
		public Iterator<Short> iterator()
		{
			return DefaultShortIntervalCollection.super.iterator();
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public boolean addShort(short value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void insertShort(int index, short value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public boolean removeShort(short value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int fromIndex, int toIndex)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setShort(int index, short value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setSizeShort(int newSize, short elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// Todo: Recognition in bulk operations!  Eg, containsAll()! ^^'''
	@SignalInterface
	public static interface IntegerIntervalCollection
	extends IntegerCollection
	{
		@Override
		public int size();
		
		public int getLowerBoundInclusive();
	}
	
	
	
	@ReadonlyValue
	public static SortedIntegerSetBackedByList newSortedIntegerSetForRange(int inclusiveLowBound, int count)
	{
		return new SortedIntegerSetBackedByList(intervalIntegersList(inclusiveLowBound, count));
	}
	
	@ThrowAwayValue
	public static SortedIntegerSetBackedByList newSortedIntegerSetForRangeMutable(int inclusiveLowBound, int count)
	{
		return new SortedIntegerSetBackedByList(new IntegerArrayList(intervalIntegersArray(inclusiveLowBound, count)));
	}
	
	
	
	public static interface DefaultIntegerIntervalCollection
	extends IntegerIntervalCollection, SimpleIterable<Integer>
	{
		@Override
		public default boolean containsInt(int i)
		{
			return i >= getLowerBoundInclusive() && i < getLowerBoundInclusive()+size();
		}
		
		
		
		@Override
		public default Iterator<Integer> iterator()
		{
			final int lb = (int)getLowerBoundInclusive();
			final int s = this.size();
			
			return new Iterator<Integer>()
			{
				int next = 0;
				
				@Override
				public Integer next()
				{
					int next = this.next;
					
					if (next >= s)
						throw new NoSuchElementException();
					
					this.next = next + 1;
					
					return (int)(next + lb);
				}
				
				@Override
				public boolean hasNext()
				{
					return this.next < size();
				}
			};
		}
		
		
		@Override
		public default SimpleIterator<Integer> simpleIterator()
		{
			final int lb = getLowerBoundInclusive();
			final int s = size();
			
			return new SimpleIterator<Integer>
			()
			{
				int next = 0;
				
				@Override
				public Integer nextrp() throws StopIterationReturnPath
				{
					int next = this.next;
					
					if (next >= s)
						throw StopIterationReturnPath.I;
					
					this.next = next + 1;
					
					return (int)(next + lb);
				}
			};
		}
		
		
		@Override
		public default SimpleIntegerIterator newSimpleIntegerIterator()
		{
			final int lb = getLowerBoundInclusive();
			final int s = size();
			
			return new SimpleIntegerIterator
			()
			{
				int next = 0;
				
				@Override
				public int nextrpInt() throws StopIterationReturnPath
				{
					int next = this.next;
					
					if (next >= s)
						throw StopIterationReturnPath.I;
					
					this.next = next + 1;
					
					return (int)(next + lb);
				}
			};
		}
		
		
		
		@Override
		public default int[] toIntArray()
		{
			return intervalIntegersArray(this.getLowerBoundInclusive(), this.size());
		}
	}
	
	
	
	
	
	
	public static class ImmutableIntegerIntervalSet
	extends AbstractReadonlySet<Integer>
	implements StaticallyConcurrentlyImmutable, DefaultIntegerIntervalCollection
	{
		protected final int lowerBoundInclusive;
		protected final int size;
		
		public ImmutableIntegerIntervalSet(int lowerBoundInclusive, @Nonnegative int size)
		{
			if (size < 0)  throw new IllegalArgumentException("negative size: "+size);
			if (doesIntegerIntervalOverflowIntWithIntSize(lowerBoundInclusive, size))  throw new OverflowException();
			
			this.lowerBoundInclusive = lowerBoundInclusive;
			this.size = size;
		}
		
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public int getLowerBoundInclusive()
		{
			return lowerBoundInclusive;
		}
		
		
		
		
		
		@Override
		public boolean isEmpty()
		{
			return DefaultIntegerIntervalCollection.super.isEmpty();
		}
		
		@Override
		public boolean contains(Object o)
		{
			return DefaultIntegerIntervalCollection.super.contains(o);
		}
		
		@Override
		public SimpleIterator<Integer> simpleIterator()
		{
			return DefaultIntegerIntervalCollection.super.simpleIterator();
		}
		
		@Override
		public Iterator<Integer> iterator()
		{
			return DefaultIntegerIntervalCollection.super.iterator();
		}
		
		@Override
		public boolean addInt(int value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public boolean removeInt(int value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	public static class ImmutableIntegerIntervalList
	extends AbstractReadonlyList<Integer>
	implements StaticallyConcurrentlyImmutable, DefaultIntegerIntervalCollection, IntegerList
	{
		protected final int lowerBoundInclusive;
		protected final int size;
		
		public ImmutableIntegerIntervalList(int lowerBoundInclusive, @Nonnegative int size)
		{
			if (size < 0)  throw new IllegalArgumentException("negative size: "+size);
			if (doesIntegerIntervalOverflowIntWithIntSize(lowerBoundInclusive, size))  throw new OverflowException();
			
			this.lowerBoundInclusive = lowerBoundInclusive;
			this.size = size;
		}
		
		
		@Override
		public IntegerList subList(int fromIndex, int toIndex)
		{
			rangeCheckInterval(this.size(), fromIndex, toIndex);
			
			int l = this.lowerBoundInclusive;
			return new ImmutableIntegerIntervalList(scintervalInt(l+fromIndex), toIndex-fromIndex);
		}
		
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public int getLowerBoundInclusive()
		{
			return lowerBoundInclusive;
		}
		
		@Override
		public IntegerList clone()
		{
			return this;
		}
		
		
		
		
		
		@Override
		public int[] toIntArray()
		{
			return DefaultIntegerIntervalCollection.super.toIntArray();   //this is a better impl than the default IntegerList one :D
		}
		
		@Override
		public SimpleIntegerIterator newSimpleIntegerIterator()
		{
			return DefaultIntegerIntervalCollection.super.newSimpleIntegerIterator();   //this is a better impl than the default IntegerList one :D
		}
		
		@Override
		public boolean containsInt(int i)
		{
			return DefaultIntegerIntervalCollection.super.containsInt(i);   //this is a better impl than the default IntegerList one :D
		}
		
		
		
		
		@Override
		public int getInt(int index)
		{
			if (index < 0)  throw new IndexOutOfBoundsException();
			if (index >= size)  throw new IndexOutOfBoundsException();
			return (int)(index + lowerBoundInclusive);
		}
		
		@Override
		public Integer get(int index)
		{
			return IntegerList.super.get(index);
		}
		
		
		@Override
		public int indexOf(Object o)
		{
			//For longs, since the size is an int >= 0, if it contains() a value, then that value's index will be a valid S32 integer, so no need for overflow checking here :>
			return contains(o) ? (int)(((Integer)o) - lowerBoundInclusive) : -1;
		}
		
		@Override
		public int lastIndexOf(Object o)
		{
			return indexOf(o);
		}
		
		
		
		
		
		@Override
		public SimpleIterator<Integer> simpleIterator()
		{
			return DefaultIntegerIntervalCollection.super.simpleIterator();
		}
		
		@Override
		public Iterator<Integer> iterator()
		{
			return DefaultIntegerIntervalCollection.super.iterator();
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public boolean addInt(int value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void insertInt(int index, int value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public boolean removeInt(int value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int fromIndex, int toIndex)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setInt(int index, int value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setSizeInt(int newSize, int elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// Todo: Recognition in bulk operations!  Eg, containsAll()! ^^'''
	@SignalInterface
	public static interface LongIntervalCollection
	extends LongCollection
	{
		@Override
		public int size();
		
		public long getLowerBoundInclusive();
	}
	
	
	
	@ReadonlyValue
	public static SortedLongSetBackedByList newSortedLongSetForRange(long inclusiveLowBound, int count)
	{
		return new SortedLongSetBackedByList(intervalLongsList(inclusiveLowBound, count));
	}
	
	@ThrowAwayValue
	public static SortedLongSetBackedByList newSortedLongSetForRangeMutable(long inclusiveLowBound, int count)
	{
		return new SortedLongSetBackedByList(new LongArrayList(intervalLongsArray(inclusiveLowBound, count)));
	}
	
	
	
	public static interface DefaultLongIntervalCollection
	extends LongIntervalCollection, SimpleIterable<Long>
	{
		@Override
		public default boolean containsLong(long i)
		{
			return i >= getLowerBoundInclusive() && i < getLowerBoundInclusive()+size();
		}
		
		
		
		@Override
		public default Iterator<Long> iterator()
		{
			final long lb = (long)getLowerBoundInclusive();
			final int s = this.size();
			
			return new Iterator<Long>()
			{
				int next = 0;
				
				@Override
				public Long next()
				{
					int next = this.next;
					
					if (next >= s)
						throw new NoSuchElementException();
					
					this.next = next + 1;
					
					return (long)(next + lb);
				}
				
				@Override
				public boolean hasNext()
				{
					return this.next < size();
				}
			};
		}
		
		
		@Override
		public default SimpleIterator<Long> simpleIterator()
		{
			final long lb = getLowerBoundInclusive();
			final int s = size();
			
			return new SimpleIterator<Long>
			()
			{
				int next = 0;
				
				@Override
				public Long nextrp() throws StopIterationReturnPath
				{
					int next = this.next;
					
					if (next >= s)
						throw StopIterationReturnPath.I;
					
					this.next = next + 1;
					
					return (long)(next + lb);
				}
			};
		}
		
		
		@Override
		public default SimpleLongIterator newSimpleLongIterator()
		{
			final long lb = getLowerBoundInclusive();
			final int s = size();
			
			return new SimpleLongIterator
			()
			{
				int next = 0;
				
				@Override
				public long nextrpLong() throws StopIterationReturnPath
				{
					int next = this.next;
					
					if (next >= s)
						throw StopIterationReturnPath.I;
					
					this.next = next + 1;
					
					return (long)(next + lb);
				}
			};
		}
		
		
		
		@Override
		public default long[] toLongArray()
		{
			return intervalLongsArray(this.getLowerBoundInclusive(), this.size());
		}
	}
	
	
	
	
	
	
	public static class ImmutableLongIntervalSet
	extends AbstractReadonlySet<Long>
	implements StaticallyConcurrentlyImmutable, DefaultLongIntervalCollection
	{
		protected final long lowerBoundInclusive;
		protected final int size;
		
		public ImmutableLongIntervalSet(long lowerBoundInclusive, @Nonnegative int size)
		{
			if (size < 0)  throw new IllegalArgumentException("negative size: "+size);
			if (doesIntegerIntervalOverflowLongWithIntSize(lowerBoundInclusive, size))  throw new OverflowException();
			
			this.lowerBoundInclusive = lowerBoundInclusive;
			this.size = size;
		}
		
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public long getLowerBoundInclusive()
		{
			return lowerBoundInclusive;
		}
		
		
		
		
		
		@Override
		public boolean isEmpty()
		{
			return DefaultLongIntervalCollection.super.isEmpty();
		}
		
		@Override
		public boolean contains(Object o)
		{
			return DefaultLongIntervalCollection.super.contains(o);
		}
		
		@Override
		public SimpleIterator<Long> simpleIterator()
		{
			return DefaultLongIntervalCollection.super.simpleIterator();
		}
		
		@Override
		public Iterator<Long> iterator()
		{
			return DefaultLongIntervalCollection.super.iterator();
		}
		
		@Override
		public boolean addLong(long value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public boolean removeLong(long value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	public static class ImmutableLongIntervalList
	extends AbstractReadonlyList<Long>
	implements StaticallyConcurrentlyImmutable, DefaultLongIntervalCollection, LongList
	{
		protected final long lowerBoundInclusive;
		protected final int size;
		
		public ImmutableLongIntervalList(long lowerBoundInclusive, @Nonnegative int size)
		{
			if (size < 0)  throw new IllegalArgumentException("negative size: "+size);
			if (doesIntegerIntervalOverflowLongWithIntSize(lowerBoundInclusive, size))  throw new OverflowException();
			
			this.lowerBoundInclusive = lowerBoundInclusive;
			this.size = size;
		}
		
		
		@Override
		public LongList subList(int fromIndex, int toIndex)
		{
			rangeCheckInterval(this.size(), fromIndex, toIndex);
			
			long l = this.lowerBoundInclusive;
			return new ImmutableLongIntervalList(scintervalLong(l+fromIndex), toIndex-fromIndex);
		}
		
		
		@Override
		public int size()
		{
			return size;
		}
		
		@Override
		public long getLowerBoundInclusive()
		{
			return lowerBoundInclusive;
		}
		
		@Override
		public LongList clone()
		{
			return this;
		}
		
		
		
		
		
		@Override
		public long[] toLongArray()
		{
			return DefaultLongIntervalCollection.super.toLongArray();   //this is a better impl than the default IntegerList one :D
		}
		
		@Override
		public SimpleLongIterator newSimpleLongIterator()
		{
			return DefaultLongIntervalCollection.super.newSimpleLongIterator();   //this is a better impl than the default IntegerList one :D
		}
		
		@Override
		public boolean containsLong(long i)
		{
			return DefaultLongIntervalCollection.super.containsLong(i);   //this is a better impl than the default IntegerList one :D
		}
		
		
		
		
		@Override
		public long getLong(int index)
		{
			if (index < 0)  throw new IndexOutOfBoundsException();
			if (index >= size)  throw new IndexOutOfBoundsException();
			return (long)(index + lowerBoundInclusive);
		}
		
		@Override
		public Long get(int index)
		{
			return LongList.super.get(index);
		}
		
		
		@Override
		public int indexOf(Object o)
		{
			//For longs, since the size is an int >= 0, if it contains() a value, then that value's index will be a valid S32 integer, so no need for overflow checking here :>
			return contains(o) ? (int)(((Long)o) - lowerBoundInclusive) : -1;
		}
		
		@Override
		public int lastIndexOf(Object o)
		{
			return indexOf(o);
		}
		
		
		
		
		
		@Override
		public SimpleIterator<Long> simpleIterator()
		{
			return DefaultLongIntervalCollection.super.simpleIterator();
		}
		
		@Override
		public Iterator<Long> iterator()
		{
			return DefaultLongIntervalCollection.super.iterator();
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		@Override
		public boolean addLong(long value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void insertLong(int index, long value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public boolean removeLong(long value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void removeRange(int fromIndex, int toIndex)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setLong(int index, long value)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		@Override
		public void setSizeLong(int newSize, long elementToAddIfGrowing)
		{
			throw new ReadonlyUnsupportedOperationException();
		}
		
		
		@Override
		public String toString()
		{
			return this._toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	// >>>
}










































///////////////////////////////   Basement/Closet   ///////////////////////////////






/*
	public static class _$$Primitive$$_ArrayListFreezable
	extends _$$Primitive$$_ArrayCollection
	implements PubliclyCloneable, RuntimeReadabilityCollection, RuntimeWriteabilityCollection
	{
		protected boolean immutable;
		protected transient int hashCodeCacheIfImmutable;
		protected transient boolean hasHashcodeCacheIfImmutable;
		
		
		//@Deprecated  //maybe nottt??
		public _$$Primitive$$_ArrayListFreezable()
		{
			this(16);
			this.immutable = false;
		}
		
		//@Deprecated  //maybe nottt??
		public _$$Primitive$$_ArrayListFreezable(int initialCapacity)
		{
			super(initialCapacity);
			this.immutable = false;
		}
		
		/**
 * Capacity increment is different from Vector's.  It is the percentage of the capacity by which the capacity is increased.
 * The formula being: <code>newCapacity = capacity + (capacity * capacityIncrement)</code><br>
 * @param capacityIncrement This can be anything (aside from NaN and ∞) the only downside is performance; all methods will add at least the minimum amount required regardless of capacityIncrement
		 ⎋a/
		//@Deprecated  //maybe nottt??
		public _$$Primitive$$_ArrayListFreezable(int initialCapacity, float capacityIncrement)
		{
			super(initialCapacity, capacityIncrement);
			this.immutable = false;
		}
		
		
		protected _$$Primitive$$_ArrayListFreezable(boolean immutable)
		{
			super((Void)null);
			this.immutable = immutable;
		}
		
		
		
		
		public static _$$Primitive$$_ArrayListFreezable newMutable()
		{
			return newMutable(16);
		}
		
		public static _$$Primitive$$_ArrayListFreezable newMutable(int initialCapacity)
		{
			return newMutable(initialCapacity, 16);
		}
		
		public static _$$Primitive$$_ArrayListFreezable newMutable(int initialCapacity, float capacityIncrement)
		{
			return new _$$Primitive$$_ArrayListFreezable(initialCapacity, capacityIncrement);
		}
		
		
		
		
		
		
		public static _$$Primitive$$_ArrayListFreezable newImmutable(@SnapshotValue @ReadonlyValue _$$prim$$_[] array)
		{
			return newImmutable(array, 0, array.length);
		}
		
		public static _$$Primitive$$_ArrayListFreezable newImmutable(@SnapshotValue @ReadonlyValue _$$prim$$_[] array, int offset, int length)
		{
			_$$prim$$_[] c;
			{
				if (offset == 0 && length == array.length)
					c = array.clone();
				else
				{
					c = new _$$prim$$_[length];
					System.arraycopy(array, offset, c, 0, length);
				}
			}
			
			return newImmutableUNSAFELIVE(c);
		}
		
		
		
		public static _$$Primitive$$_ArrayListFreezable newImmutableUNSAFELIVE(@TreatAsImmutableValue _$$prim$$_[] array)
		{
			return newImmutableUNSAFELIVE(array, 0, array.length);
		}
		
		public static _$$Primitive$$_ArrayListFreezable newImmutableUNSAFELIVE(@TreatAsImmutableValue _$$prim$$_[] array, int offset, int length)
		{
			if (offset < 0) throw new IndexOutOfBoundsException();
			if (length < 0) throw new IllegalArgumentException();
			if (length + offset > array.length) throw new IndexOutOfBoundsException();
			
			if (offset != 0)
				return newImmutable(array, offset, length);
			else
			{
				_$$Primitive$$_ArrayListFreezable l = new _$$Primitive$$_ArrayListFreezable(true);
				l.data = array;
				l.size = length;
				return l;
			}
		}
		
		
		
		
		
		@Override
		public boolean isReadableCollection()
		{
			return true;
		}
		
		@Override
		public boolean isWritableCollection()
		{
			return !immutable;
		}
		
		
		
		
		@Override
		public int hashCode()
		{
			if (immutable)
			{
				if (hasHashcodeCacheIfImmutable)
				{
					return hashCodeCacheIfImmutable;
				}
				else
				{
					int hashCode = 1;
					
					int n = this.size();
					for (int i = 0; i < n; i++)
					{
						hashCode = 31*hashCode + _$$Primitive$$_.hashCode(this.get_$$Prim$$_(i));
					}
					
					this.hashCodeCacheIfImmutable = hashCode;
					this.hasHashcodeCacheIfImmutable = true;
					
					return hashCode;
				}
			}
			else
			{
				return super.hashCode();  //hashcodes must be immutable!  use freeze() if you need to switch from mutable to immutable! ;D
			}
		}
		
		
		
		public void freeze()
		{
			this.immutable = true;
			//hashcode/etc. caches will be already set to their needed defaults :333
		}
		
		
		
		
		@Override
		public boolean add_$$Prim$$_(_$$prim$$_ v)
		{
			if (immutable)
				throw new ReadonlyUnsupportedOperationException();
			return super.add_$$Prim$$_(v);
		}
		
		@Override
		public void addAll_$$Prim$$_s(_$$Primitive$$_ArrayCollection collection)
		{
			if (immutable)
				throw new ReadonlyUnsupportedOperationException();
			super.addAll_$$Prim$$_s(collection);
		}
		
		@Override
		public void addAll_$$Prim$$_s(_$$prim$$_[] array)
		{
			if (immutable)
				throw new ReadonlyUnsupportedOperationException();
			addAll_$$Prim$$_s(array, 0, array.length);
		}
		
		@Override
		public void addAll_$$Prim$$_s(_$$prim$$_[] array, int offset, int length)
		{
			if (immutable)
				throw new ReadonlyUnsupportedOperationException();
			super.addAll_$$Prim$$_s(array, 0, array.length);
		}
		
		@Override
		public void addAll_$$Prim$$_s(Slice<_$$prim$$_[]> arraySlice)
		{
			if (immutable)
				throw new ReadonlyUnsupportedOperationException();
			super.addAll_$$Prim$$_s(arraySlice);
		}
		
		@Override
		public void set_$$Prim$$_(int index, _$$prim$$_ value)
		{
			if (immutable)
				throw new ReadonlyUnsupportedOperationException();
			super.set_$$Prim$$_(index, value);
		}
		
		@Override
		public void setAll_$$Prim$$_s(int index, _$$prim$$_[] array)
		{
			if (immutable)
				throw new ReadonlyUnsupportedOperationException();
			super.setAll_$$Prim$$_s(index, array);
		}
		
		/**
 * Truncate to shrink, or expand with 0's to expand.
		 ⎋a/
		@Override
		public void setSize(int newSize)
		{
			if (immutable)
				throw new ReadonlyUnsupportedOperationException();
			super.setSize(newSize);
		}
		
		
		/**
 * @return The removed element
		 ⎋a/
		@Override
		public _$$prim$$_ remove_$$Prim$$_ByIndex(int index)
		{
			if (immutable)
				throw new ReadonlyUnsupportedOperationException();
			return super.remove_$$Prim$$_ByIndex(index);
		}
		
		/**
 * Remove all elements in the range (exclusive, like String).<br>
 * Then shifts all elements at- and to the right of- the <code>end</code>'th element left by the size of the block removed to 'fill the void'.<br>
		 ⎋a/
		@Override
		public void removeRange(int start, int end)
		{
			if (immutable)
				throw new ReadonlyUnsupportedOperationException();
			super.removeRange(start, end);
		}
		
		
		@Override
		public void clear()
		{
			if (immutable)
				throw new ReadonlyUnsupportedOperationException();
			super.clear();
		}
		
		@Override
		public void trimToSize()
		{
			super.trimToSize();
		}
		
		@Override
		public _$$prim$$_ get_$$Prim$$_(int index)
		{
			return super.get_$$Prim$$_(index);
		}
		
		@Override
		public _$$prim$$_[] getAll_$$Prim$$_s(int start, int end)
		{
			return super.getAll_$$Prim$$_s(start, end);
		}
		
		/**
 * Copies <code>array.length</code> elements into <code>array</code> starting with the <code>start</code>th element.<br>
		 ⎋a/
		@Override
		public void getAll_$$Prim$$_s(int start, _$$prim$$_[] array, int offset, int length)
		{
			super.getAll_$$Prim$$_s(start, array, offset, length);
		}
		
		@Override
		public int getCapacity()
		{
			return super.getCapacity();
		}
		
		@ThrowAwayValue
		@Override
		public _$$prim$$_[] to_$$Prim$$_Array()
		{
			return getAll_$$Prim$$_s(0, size);
		}
		
		@Override
		public int indexOf_$$Prim$$_(_$$prim$$_ value)
		{
			return super.indexOf_$$Prim$$_(value);
		}
		
		public boolean contains_$$Prim$$_(_$$prim$$_ value)
		{
			return super.contains_$$Prim$$_(value);
		}
		
		
		/**
 * sets the underlying array that backs the array list.
 * Be careful, though, as updates can cause a new array to be creates, obsoleting this one.
 * Note, also, that only up to {@link #size()} elements will be used.
 * @throws IllegalArgumentException if the provided array is too small!  (array.length < {@link #size()})
		 ⎋a/
		public void setLiveContiguousArrayBacking(_$$prim$$_[] array) throws IllegalArgumentException
		{
			if (array.length < size)
				throw new IllegalArgumentException("Array is too small; size="+size+", capacity="+array.length);
			
			this.data = array;
		}
		
		
		
		@Override
		public _$$Primitive$$_ArrayListFreezable clone()
		{
			_$$Primitive$$_ArrayListFreezable clone = new _$$Primitive$$_ArrayListFreezable();
			super.setupClone(clone);
			return clone;
		}
	}
 */

















/* << <
primxp
_$$primxpconf:noboolean$$_

/**
 * An ArrayCollection of primitives with the constraint that no two elements are duplicates and that
 * the collection is always sorted (ascending).<br>
 ⎋a/
public static class Sorted_$$Primitive$$_ArraySet
extends _$$Primitive$$_ArrayCollection
{
	public Sorted_$$Primitive$$_ArraySet()
	{
		this(16);
	}
	
	public Sorted_$$Primitive$$_ArraySet(int initialCapacity)
	{
		super(initialCapacity);
	}
	
	/**
 * Capacity increment is different from Vector's.  It is the percentage of the capacity by which the capacity is increased.
 * The formula being: <code>newCapacity = capacity + (capacity * capacityIncrement)</code><br>
 * @param capacityIncrement This can be anything (aside from NaN and ∞) the only downside is performance; all methods will add at least the minimum amount required regardless of capacityIncrement
	 ⎋a/
	public Sorted_$$Primitive$$_ArraySet(int initialCapacity, float capacityIncrement)
	{
		super(initialCapacity, capacityIncrement);
	}
	
	
	
	
	
	public static Sorted_$$Primitive$$_ArraySet newByPresortedLiveBackingUNSAFE(@LiveValue _$$prim$$_[] backing, int size)
	{
		Sorted_$$Primitive$$_ArraySet set = new Sorted_$$Primitive$$_ArraySet(0);
		set.data = backing;
		set.size = size;
		return set;
	}
	
	
	public static Sorted_$$Primitive$$_ArraySet newForRange(int inclusiveLowBound, int size)
	{
		_$$prim$$_[] d = new _$$prim$$_[size];
		
		for (int i = 0; i < size; i++)
			d[i] = (_$$prim$$_)(i + inclusiveLowBound);
		
		return newByPresortedLiveBackingUNSAFE(d, size);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	//The super implementations of addAll() will only delegate to either add_$$Prim$$_() or to this!   So these are the only two we need to override to cause Set behavior! :3

	
	@Override
	public boolean addAll_$$Prim$$_s(_$$prim$$_[] elements, int offset, int length)
	{
		//No easy way to add elements in bulk (that I'm aware of, or have the time to implement)
		
		return _$$Primitive$$_Collection.defaultAddAll_$$Prim$$_s(this, elements, offset, length);
	}
	
	@Override
	public boolean add_$$Prim$$_(_$$prim$$_ value)
	{
		int insertionPoint = SortingUtilities.findInsertionPointInSet(data, 0, size, value);
		
		if (insertionPoint < 0)
			//Duplicate
			return false;
		
		if (data.length < size+1)
		{
			_$$prim$$_[] newdata = new _$$prim$$_[getNewCapacity(size+1)];
			System.arraycopy(data, 0, newdata, 0, insertionPoint);
			newdata[insertionPoint] = value;
			System.arraycopy(data, insertionPoint, newdata, insertionPoint+1, size-insertionPoint);
			this.data = newdata;
		}
		else
		{
			System.arraycopy(data, insertionPoint, data, insertionPoint+1, size-insertionPoint);
			data[insertionPoint] = value;
		}
		
		size++;
		
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public _$$prim$$_ remove_$$Prim$$_ByIndex(int index)
	{
		return super.remove_$$Prim$$_ByIndex(index);
	}
	
	/**
 * Remove all elements in the range (exclusive, like String).<br>
 * Then shifts all elements at- and to the right of- the <code>end</code>'th element left by the size of the block removed to 'fill the void'.<br>
	 ⎋a/
	@Override
	public void removeRange(int start, int end)
	{
		super.removeRange(start, end);
	}
	
	
	@Override
	public void clear()
	{
		super.clear();
	}
	
	@Override
	public void trimToSize()
	{
		super.trimToSize();
	}
	
	@Override
	public _$$prim$$_ get_$$Prim$$_(int index)
	{
		return super.get_$$Prim$$_(index);
	}
	
	@Override
	public _$$prim$$_[] getAll_$$Prim$$_s(int start, int end)
	{
		return super.getAll_$$Prim$$_s(start, end);
	}
	
	@Override
	public void getAll_$$Prim$$_s(int start, _$$prim$$_[] array, int offset, int length)
	{
		super.getAll_$$Prim$$_s(start, array, offset, length);
	}
	
	@Override
	public int getCapacity()
	{
		return super.getCapacity();
	}
	
	@Override
	public boolean isEmpty()
	{
		return super.isEmpty();
	}
	
	@Override
	public int indexOf_$$Prim$$_(_$$prim$$_ value)
	{
		//Better algorithm, no difference in API
		return SortingUtilities.findIndexForValueInSortedSet(data, 0, size, value);
	}
	
	@Override
	public boolean contains_$$Prim$$_(_$$prim$$_ value)
	{
		return super.contains_$$Prim$$_(value);
	}
	
	
	@Override
	public _$$prim$$_[] to_$$Prim$$_Array()
	{
		return getAll_$$Prim$$_s(0, size);
	}
	
	/**
 * This is a synonym to {@link #to_$$Prim$$_Array()} which should probably be used in preference to {@link #to_$$Prim$$_Array()} if your code relies on its sorted-ness, at least for clarity :>
 * (Useful if someone else comes by and switches the instantiation to, say, Set&lt;Integer&gt; X'D )
	 ⎋a/
	@ThrowAwayValue
	public _$$prim$$_[] toSorted_$$Prim$$_Array()
	{
		return getAll_$$Prim$$_s(0, size);
	}
	
	
	
	
	
	@Override
	public Sorted_$$Primitive$$_ArraySet clone()
	{
		Sorted_$$Primitive$$_ArraySet clone = new Sorted_$$Primitive$$_ArraySet();
		setupClone(clone);
		return clone;
	}
}


















 */



































/*  Old way with ArrayCollection that was basically an array list, but didn't implement List (eg, for Sets or just bare Collections and etc.)   (and thus duplicated a bunch of code with the List defaults! X'D)      Especially unnecessary given in Java any List can easily function as a Collection! XD



/**
 * This is a utility base class.  All the methods here are protected, the subclass will utilize what it wants, and not publish the rest.<br>
 * @author RProgrammer
 ⎋a/
public static class _$$Primitive$$_ArrayCollection
implements DefaultToArrays_$$Primitive$$_Collection, Trimmable, TransparentContiguousArrayBackedCollection<_$$prim$$_[]>
{
	protected int size;
	protected _$$prim$$_[] data;
	protected float capacityIncrement;
	
	protected _$$Primitive$$_ArrayCollection()
	{
		this(16);
	}
	
	protected _$$Primitive$$_ArrayCollection(int initialCapacity)
	{
		size = 0;
		data = initialCapacity == 0 ? ArrayUtilities.Empty_$$Prim$$_Array : new _$$prim$$_[initialCapacity];
		capacityIncrement = 0.10f; //10%
	}
	
	/**
 * Capacity increment is different from Vector's.  It is the percentage of the capacity by which the capacity is increased.
 * The formula being: <code>newCapacity = capacity + (capacity * capacityIncrement)</code><br>
 * @param capacityIncrement This can be anything (aside from NaN and ∞) the only downside is performance; all methods will add at least the minimum amount required regardless of capacityIncrement
 	 ⎋a/
	protected _$$Primitive$$_ArrayCollection(int initialCapacity, float capacityIncrement)
	{
		size = 0;
		data = initialCapacity == 0 ? ArrayUtilities.Empty_$$Prim$$_Array : new _$$prim$$_[initialCapacity];
		this.capacityIncrement = capacityIncrement;
	}
	
	protected _$$Primitive$$_ArrayCollection(Void thisiswhyyoushouldntusejavaconstructorsAlthoughIdontknowhowtomakeinheritanceworkwellwithoutinitmethodsorfactoriesorsomething)
	{
	}
	
	
	
	
	
	
	@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
	public boolean add_$$Prim$$_(_$$prim$$_ v)
	{
		ensureCapacity(size+1);
		data[size] = v;
		size++;
		return true;
	}
	
	@Override
	public boolean addAll_$$Prim$$_s(_$$prim$$_[] array, int offset, int length)
	{
		ensureCapacity(size+length);
		System.arraycopy(array, offset, data, size, length);
		size += length;
		return true;
	}
	
	@Override
	public boolean addAll(Collection<? extends _$$Primitive$$_> c)
	{
		if (TransparentContiguousArrayBackedCollection.is(c))
		{
			Slice<?> u = ((TransparentContiguousArrayBackedCollection<?>)c).getLiveContiguousArrayBackingUNSAFE();
			
			if (u.getUnderlying() instanceof _$$prim$$_[])
			{
				return this.addAll_$$Prim$$_s((Slice<_$$prim$$_[]>)u);
			}
		}
		
		//else else
		{
			return DefaultToArrays_$$Primitive$$_Collection.super.addAll(c);
		}
	}
	
	
	
	
	
	
	
	
	
	
	protected void setupClone(_$$Primitive$$_ArrayCollection freshClone)
	{
		freshClone.size = this.size;
		
		freshClone.capacityIncrement = this.capacityIncrement;
		
		freshClone.data = new _$$prim$$_[size];
		System.arraycopy(this.data, 0, freshClone.data, 0, size);
	}





	@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
	@Override
	public void clear()
	{
		size = 0;
	}
	
	@ImplementationTransparency
	public void trimToSize()
	{
		_$$prim$$_[] newdata = new _$$prim$$_[size];
		System.arraycopy(data, 0, newdata, 0, size);
		this.data = newdata;
	}
	
	
	@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
	@Override
	public int size()
	{
		return size;
	}
	
	
	
	@ImplementationTransparency
	@LiveValue
	public Slice<_$$prim$$_[]> getLiveContiguousArrayBackingUNSAFE()
	{
		return new Slice(data, 0, size);
	}
	
	@ThrowAwayValue
	@Override
	public _$$prim$$_[] to_$$Prim$$_Array()
	{
		return getAll_$$Prim$$_s(0, size);
	}
	
	@Override
	public boolean contains_$$Prim$$_(_$$prim$$_ value)
	{
		return indexOf_$$Prim$$_(value) != -1;
	}
	
	
	
	@Override
	public Simple_$$Primitive$$_Iterator newSimple_$$Primitive$$_Iterator()
	{
		return new Simple_$$Primitive$$_Iterator()
		{
			int index = 0;
			
			public _$$prim$$_ nextrp_$$Prim$$_() throws StopIterationReturnPath
			{
				int i = index;
				
				if (i >= size)
				{
					throw StopIterationReturnPath.I;
				}
				else
				{
					index = i + 1;
					return data[i];
				}
			}
		};
	}
	
	
	
	
	@Override
	public Iterator<_$$Primitive$$_> iterator()
	{
		return new Iterator<_$$Primitive$$_>()
		{
			int index = 0;
			
			@Override
			public boolean hasNext()
			{
				return index < size;
			}
			
			@Override
			public _$$Primitive$$_ next()
			{
				int i = index;
				
				if (i >= size)
				{
					throw new NoSuchElementException();
				}
				else
				{
					index = i + 1;
					return data[i];
				}
			}
		};
	}
	
	
	
	@Override
	public void storeBoxingIntoArray(Slice<Object[]> array)
	{
		int n = this.size();
		for (int i = 0; i < n; i++)
			ArrayUtilities.set_$$Prim$$_((Slice)array, i, this.get_$$Prim$$_(i));
	}
	
	
	@Override
	public boolean remove_$$Prim$$_(_$$prim$$_ value)
	{
		int i = this.indexOf_$$Prim$$_(value);
		
		if (i != -1)
		{
			this.remove_$$Prim$$_ByIndex(i);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	
	
	
	@Override
	public boolean removeAll(Collection<?> c)
	{
		if (c instanceof _$$Primitive$$_Collection)
		{
			_$$Primitive$$_Collection cc = (_$$Primitive$$_Collection) c;
			
			boolean changedAtAll = false;
			
			for (int i = size() - 1; i >= 0; i--)
			{
				if (cc.contains_$$Prim$$_(data[i]))
				{
					remove_$$Prim$$_ByIndex(i);
					changedAtAll = true;
				}
			}
			
			return changedAtAll;
		}
		else
		{
			boolean changedAtAll = false;
			
			for (int i = size() - 1; i >= 0; i--)
			{
				if (c.contains(data[i]))
				{
					remove_$$Prim$$_ByIndex(i);
					changedAtAll = true;
				}
			}
			
			return changedAtAll;
		}
	}
	
	
	@Override
	public boolean retainAll(Collection<?> c)
	{
		if (c instanceof _$$Primitive$$_Collection)
		{
			_$$Primitive$$_Collection cc = (_$$Primitive$$_Collection) c;
			
			boolean changedAtAll = false;
			
			for (int i = size() - 1; i >= 0; i--)
			{
				if (!cc.contains_$$Prim$$_(data[i]))
				{
					remove_$$Prim$$_ByIndex(i);
					changedAtAll = true;
				}
			}
			
			return changedAtAll;
		}
		else
		{
			boolean changedAtAll = false;
			
			for (int i = size() - 1; i >= 0; i--)
			{
				if (!c.contains(data[i]))
				{
					remove_$$Prim$$_ByIndex(i);
					changedAtAll = true;
				}
			}
			
			return changedAtAll;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//List-like things that aren't exposed publicly except in actual List impl.s, but might be useful at least for internal workings even for non-List impl.s  :D
	
	protected int indexOf_$$Prim$$_(_$$prim$$_ value)
	{
		for (int i = 0; i < size; i++)
			if (eqSane(data[i], value))
				return i;
		return -1;
	}
	
	protected int lastIndexOf_$$Prim$$_(_$$prim$$_ value)
	{
		int i = size-1;
		while (i >= 0)
		{
			if (eqSane(data[i], value))
				return i;
			break;
		}
		return i;   // ;D
	}
	
	
	@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
	protected void insert_$$Prim$$_(int index, _$$prim$$_ v)
	{
		rangeCheckCursorPoint(this.size, index);
		
		if (index != size)
		{
			ensureCapacity(size+1);  //Todo more efficient expansion that minimizes the number of copies? ^^'
			System.arraycopy(data, index, data, index+1, size-index);
		}
		else
		{
			ensureCapacity(size+1);  //Todo more efficient expansion that minimizes the number of copies? ^^'
		}
		
		data[index] = v;
		size++;
	}
	
	protected void insertAll_$$Prim$$_s(int index, _$$prim$$_[] array, int offset, int length)
	{
		rangeCheckCursorPoint(this.size, index);
		
		if (index != size)
		{
			ensureCapacity(size+length);  //Todo more efficient expansion that minimizes the number of copies? ^^'
			System.arraycopy(data, index, data, index+length, size-index);
		}
		else
		{
			ensureCapacity(size+length);  //Todo more efficient expansion that minimizes the number of copies? ^^'
		}
		
		System.arraycopy(array, offset, data, index, length);
		size += length;
	}
	
	protected void set_$$Prim$$_(int index, _$$prim$$_ value)
	{
		rangeCheckMember(this.size, index);
		data[index] = value;
	}
	
	protected void setAll_$$Prim$$_s(int index, _$$prim$$_[] array)
	{
		setAll_$$Prim$$_s(index, array, 0, array.length);
	}
	
	protected void setAll_$$Prim$$_s(int index, _$$prim$$_[] array, int offset, int length)
	{
		rangeCheckIntervalByLength(this.size(), index, length);
		rangeCheckIntervalByLength(array.length, offset, length);
		
		System.arraycopy(array, offset, data, index, length);
	}
	
	protected void setSize_$$Prim$$_(int newSize, _$$prim$$_ elementToAddIfGrowing)
	{
		if (newSize < 0)
			throw new IllegalArgumentException("Negative size!: "+newSize);
		
		int oldSize = this.size;
		
		if (newSize == oldSize)
			return;
		
		if (newSize > data.length)
			ensureCapacity(newSize);
		
		size = newSize;
		
		if (newSize > oldSize)
		{
			for (int i = oldSize; i < newSize; i++)
				this.data[i] = elementToAddIfGrowing;
		}
	}
	
	/**
 * @return The removed element
 	 ⎋a/
	protected _$$prim$$_ remove_$$Prim$$_ByIndex(int index)
	{
		rangeCheckMember(this.size(), index);
		
		_$$prim$$_ value = get_$$Prim$$_(index);
		if (index+1 != size)
			System.arraycopy(data, index+1, data, index, size-(index+1));
		size--;
		return value;
	}
	
	/**
 * Remove all elements in the range (exclusive, like String).<br>
 * Then shifts all elements at- and to the right of- the <code>end</code>'th element left by the size of the block removed to 'fill the void'.<br>
 	 ⎋a/
	protected void removeRange(int start, int end)  //This doesn't have a _$$Prim$$_ in it because neither the method signature nor semantics depends on the type! :D
	{
		rangeCheckInterval(this.size, start, end);
		
		if (start == end)
			return;
		if (end != size)
			System.arraycopy(data, end, data, start, size-end);
		size -= end - start;
	}
	
	
	protected _$$prim$$_ get_$$Prim$$_(int index)
	{
		rangeCheckMember(this.size(), index);
		return data[index];
	}
	
	@ThrowAwayValue
	protected _$$prim$$_[] getAll_$$Prim$$_s(int start, int end)
	{
		rangeCheckInterval(this.size, start, end);
		
		_$$prim$$_[] buff = new _$$prim$$_[end-start];
		System.arraycopy(data, start, buff, 0, end-start);
		return buff;
	}
	
	/**
 * Copies <code>array.length</code> elements into <code>array</code> starting with the <code>start</code>th element.<br>
 	 ⎋a/
	protected void getAll_$$Prim$$_s(int start, _$$prim$$_[] array, int offset, int length)
	{
		rangeCheckInterval(this.size, start, start+length);
		
		System.arraycopy(data, start, array, offset, length);
	}
	
	protected int getCapacity()
	{
		return data.length;
	}
	
	
	
	
	
	protected void ensureCapacity(int minCapacity)
	{
		if (data.length < minCapacity)
		{
			//Enlarge
			int newCapacity = getNewCapacity(minCapacity);
			_$$prim$$_[] newData = new _$$prim$$_[newCapacity];
			System.arraycopy(data, 0, newData, 0, data.length);
			this.data = newData;
		}
	}
	
	
	/**
 * Applies capacity increment rules to an enlargement in capacity.
 * This is also guaranteed not to return a capacity smaller than the provided minimum.
 	 ⎋a/
	protected int getNewCapacity(int minCapacity)
	{
		return Math.max(minCapacity, data.length + (int)(data.length*capacityIncrement)); //Math.max ensures that the capacity increment can't break anything
	}
	
	
	
	
	
	@Override
	public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
	{
		this.trimToSize();
		return TrimmableTrimRV.DontKeepInvoking;
	}
}

































public static class _$$Primitive$$_ArrayList
extends _$$Primitive$$_ArrayCollection
implements _$$Primitive$$_List
{
	public _$$Primitive$$_ArrayList()
	{
		this(16);
	}
	
	public _$$Primitive$$_ArrayList(int initialCapacity)
	{
		super(initialCapacity);
	}
	
	/**
 * Capacity increment is different from Vector's.  It is the percentage of the capacity by which the capacity is increased.
 * The formula being: <code>newCapacity = capacity + (capacity * capacityIncrement)</code><br>
 * @param capacityIncrement This can be anything (aside from NaN and ∞) the only downside is performance; all methods will add at least the minimum amount required regardless of capacityIncrement
	 ⎋a/
	public _$$Primitive$$_ArrayList(int initialCapacity, float capacityIncrement)
	{
		super(initialCapacity, capacityIncrement);
	}
	
	
	
	
	
	
	@Override
	public void setAll_$$Prim$$_s(int index, _$$prim$$_[] array, int offset, int length)
	{
		super.setAll_$$Prim$$_s(index, array, offset, length);
	}
	
	@Override
	public void setAll(int destIndex, List source, int sourceIndex, int amount) throws IndexOutOfBoundsException
	{
		if (TransparentContiguousArrayBackedCollection.is(source))
		{
			Slice<?> u = ((TransparentContiguousArrayBackedCollection<?>)source).getLiveContiguousArrayBackingUNSAFE();
			
			if (u.getUnderlying() instanceof _$$prim$$_[])
			{
				rangeCheckIntervalByLength(source.size(), sourceIndex, amount);
				rangeCheckIntervalByLength(this.size(), destIndex, amount);
				FixedLengthArrayWrapper_$$Primitive$$_MutableList
				this.setAll_$$Prim$$_s(destIndex, (Slice<_$$prim$$_[]>)u);
				return;
			}
		}
		
		_$$Primitive$$_List.super.setAll(destIndex, source, sourceIndex, amount);
	}
	
	
	
	
	
	
	
	
	
	@Override
	public void insertAll_$$Prim$$_s(int index, _$$prim$$_[] array, int offset, int length)
	{
		super.insertAll_$$Prim$$_s(index, array, offset, length);
	}
	
	
	
	
	
	
	
	@Override
	public int indexOf_$$Prim$$_(_$$prim$$_ value)
	{
		return super.indexOf_$$Prim$$_(value);  //equivalent but takes less JITting than the superinterface's default, so perhaps slightly better? ^^'
	}
	
	@Override
	public int lastIndexOf_$$Prim$$_(_$$prim$$_ value)
	{
		return super.lastIndexOf_$$Prim$$_(value);  //equivalent but takes less JITting than the superinterface's default, so perhaps slightly better? ^^'
	}
	
	@Override
	public Simple_$$Primitive$$_Iterator newSimple_$$Primitive$$_Iterator()
	{
		return super.newSimple_$$Primitive$$_Iterator();  //equivalent but takes less JITting than the superinterface's default, so perhaps slightly better? ^^'
	}
	
	
	@Override
	public void insert_$$Prim$$_(int index, _$$prim$$_ v)
	{
		super.insert_$$Prim$$_(index, v);
	}
	
	
	@Override
	public void set_$$Prim$$_(int index, _$$prim$$_ value)
	{
		super.set_$$Prim$$_(index, value);
	}
	
	@Override
	public void setAll_$$Prim$$_s(int index, _$$prim$$_[] array)
	{
		super.setAll_$$Prim$$_s(index, array);
	}
	
	@Override
	public void setSize_$$Prim$$_(int newSize, _$$prim$$_ elementToAddIfGrowing)
	{
		super.setSize_$$Prim$$_(newSize, elementToAddIfGrowing);
	}
	
	
	/**
 * @return The removed element
	 ⎋a/
	@Override
	public _$$prim$$_ remove_$$Prim$$_ByIndex(int index)
	{
		return super.remove_$$Prim$$_ByIndex(index);
	}
	
	/**
 * Remove all elements in the range (exclusive, like String).<br>
 * Then shifts all elements at- and to the right of- the <code>end</code>'th element left by the size of the block removed to 'fill the void'.<br>
	 ⎋a/
	@Override
	public void removeRange(int start, int pastEnd)
	{
		super.removeRange(start, pastEnd);
	}
	
	
	@Override
	public _$$prim$$_ get_$$Prim$$_(int index)
	{
		return super.get_$$Prim$$_(index);
	}
	
	@Override
	public _$$prim$$_[] getAll_$$Prim$$_s(int start, int end)
	{
		return super.getAll_$$Prim$$_s(start, end);
	}
	
	/**
 * Copies <code>array.length</code> elements into <code>array</code> starting with the <code>start</code>th element.<br>
	 ⎋a/
	@Override
	public void getAll_$$Prim$$_s(int start, _$$prim$$_[] array, int offset, int length)
	{
		super.getAll_$$Prim$$_s(start, array, offset, length);
	}
	
	@Override
	public int getCapacity()
	{
		return super.getCapacity();
	}
	
	
	/**
 * returns the underlying array that backs the array list.
 * Be careful, though, as updates can cause a new array to be created, obsoleting this one.
 * Note, also, that only up to {@link #size()} elements should be used.
	 ⎋a/
	public _$$prim$$_[] getDirectBuffer()
	{
		return this.data;
	}
	
	/**
 * sets the underlying array that backs the array list.
 * Be careful, though, as updates can cause a new array to be creates, obsoleting this one.
 * Note, also, that only up to {@link #size()} elements will be used.
	 ⎋a/
	public void setDirectBuffer(@LiveValue _$$prim$$_[] array)
	{
		if (array.length < size)
			throw new IllegalArgumentException("Array is too small; size="+size+", capacity="+array.length);
		
		this.data = array;
	}
	
	
	
	@Override
	public _$$Primitive$$_ArrayList clone()
	{
		_$$Primitive$$_ArrayList clone = new _$$Primitive$$_ArrayList();
		super.setupClone(clone);
		return clone;
	}
}
 */
















/*
		public default String _toString()
		{
			StringBuilder b = new StringBuilder("[");
			
			int size = this.size();
			
			for (int i = 0; i < size; i++)
			{
				b.append(this.get_$$Prim$$_(i));
				
				if (i < size-1)
					b.append(", ");
			}
			
			b.append(']');
			
			return b.toString();
		}
 */
