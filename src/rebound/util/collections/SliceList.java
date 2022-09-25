package rebound.util.collections;

import static java.util.Objects.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.util.collections.BasicCollectionUtilities.*;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Using {@link Slice}&lt;byte[]&gt; instead of byte[] everywhere greatly improves performance decoding header-payload type binary formats (which is almost all binary formats) because you don't need to perform a {@link System#arraycopy(Object, int, Object, int, int)} or equivalent to copy the payload out.
 * But during *en*coding that doesn't help :P
 * That's where this class comes in! :D
 * If you use this as the general data format, then you can tack headers on at the front *without copying any bytes*! :D
 * 
 * And remember, {@link System#arraycopy(Object, int, Object, int, int)} isn't just slow because it has to copy lots of bytes,
 * but when things are specialized and inlined and stack-allocated and optimized by the JIT/AOT compiler, it's probably MUCH harder to turn two arraycopy() calls into one than it is to just..do nothing on top of the stack-allocating already done to enable that (which would also apply to instances of this class ;D )
 */
public final class SliceList<A>
{
	protected final @Nonnull A underlyingForThisSlice;
	protected final int offsetForThisSlice;
	protected final int lengthForThisSlice;  //this can only be 0 if next == null
	
	protected final @Nullable SliceList<A> next;
	protected final int lengthTotal;
	
	protected SliceList(@Nonnull A underlyingForThisSlice, @Nonnegative int offsetForThisSlice, @Nonnegative int lengthForThisSlice, @Nullable SliceList<A> next, @Nonnegative int lengthTotal)
	{
		this.underlyingForThisSlice = requireNonNull(underlyingForThisSlice);
		this.offsetForThisSlice = requireNonNegative(offsetForThisSlice);
		this.lengthForThisSlice = requireNonNegative(lengthForThisSlice);
		this.next = next;
		this.lengthTotal = requireNonNegative(lengthTotal);  //this helps find integer overflow in the addition performed in the constructor below :3
		
		if (lengthForThisSlice == 0 && next != null)
			throw new IllegalArgumentException("Empty slices are not allowed in the list except for encoding The Empty List!");
	}
	
	
	
	public static <A> SliceList<A> empty(@Nonnull A underlyingForThisSlice)
	{
		return base(underlyingForThisSlice, 0, 0);
	}
	
	public static <A> SliceList<A> base(@Nonnull A underlyingForThisSlice, @Nonnegative int offsetForThisSlice, @Nonnegative int lengthForThisSlice)
	{
		return new SliceList<>(underlyingForThisSlice, offsetForThisSlice, lengthForThisSlice, null, lengthForThisSlice);
	}
	public static <A> SliceList<A> base(@Nonnull Slice<A> thisSlice)
	{
		return base(thisSlice.getUnderlying(), thisSlice.getOffset(), thisSlice.getLength());
	}
	
	public static <A> SliceList<A> prepend(@Nonnull A underlyingForThisSlice, @Nonnegative int offsetForThisSlice, @Nonnegative int lengthForThisSlice, @Nullable SliceList<A> next)
	{
		if (next == null)
			return base(underlyingForThisSlice, offsetForThisSlice, lengthForThisSlice);
		else
			return lengthForThisSlice == 0 ? next : new SliceList<>(underlyingForThisSlice, offsetForThisSlice, lengthForThisSlice, next, lengthForThisSlice + next.getLengthTotal());
	}
	public static <A> SliceList<A> prepend(@Nonnull Slice<A> thisSlice, @Nullable SliceList<A> next)
	{
		return prepend(thisSlice.getUnderlying(), thisSlice.getOffset(), thisSlice.getLength(), next);
	}
	
	
	
	
	public A getUnderlyingForThisSlice()
	{
		return underlyingForThisSlice;
	}
	
	@Nonnegative
	public int getOffsetForThisSlice()
	{
		return offsetForThisSlice;
	}
	
	@Nonnegative
	public int getLengthForThisSlice()
	{
		return lengthForThisSlice;
	}
	
	public Slice<A> getThisSlice()
	{
		return new Slice<>(underlyingForThisSlice, offsetForThisSlice, lengthForThisSlice);
	}
	
	
	
	public SliceList<A> getNext()
	{
		return next;
	}
	
	@Nonnegative
	public int getLengthTotal()
	{
		return lengthTotal;
	}
	
	
	
	
	
	
	
	
	public boolean isEmpty()
	{
		return getLengthTotal() == 0;
	}
	
	
	/**
	 * + Note that if absoluteIndex == {@link #getLengthTotal()} then (empty(), 0) is returned (which is the *only* time the relative index is out of bounds in the top-slice of the list returned XD' )
	 * @return (the list of all things at the index and after it, and the relative index within its {@link #getThisSlice() first-slice} :3 )
	 */
	public PairOrdered<SliceList<A>, Integer> find(@Nonnegative int absoluteIndex)
	{
		if (absoluteIndex < 0)
			throw new IndexOutOfBoundsException();
		else if (absoluteIndex == 0)
			return pair(this, 0);
		else
		{
			int l = getLengthTotal();
			if (absoluteIndex == l)
				return pair(empty(getUnderlyingForThisSlice()), 0);
			else if (absoluteIndex > l)
				throw new IndexOutOfBoundsException();
			else
			{
				int ll = getLengthForThisSlice();
				
				if (absoluteIndex < getLengthForThisSlice())
					return pair(this, absoluteIndex);
				else
				{
					//+ This could be a big slow traversal of the whole list but that's what it has to be!
					//+ We can safely call getNext() here because if it was null then absoluteIndex would either be within this slice or out of bounds and we already checked for that :>
					//+ Yayyyyy tail-call recursion optimization! :D
					return getNext().find(absoluteIndex - ll);
				}
			}
		}
	}
	
	
	
	
	
	public SliceList<A> subsliceByExclusiveBound(@Nonnegative int start, @Nonnegative int end)
	{
		return subslice(start, end - start);
	}
	
	
	public SliceList<A> subsliceToEnd(@Nonnegative int offset)
	{
		if (offset < 0)
		{
			throw new IndexOutOfBoundsException();
		}
		else if (offset == 0)
		{
			return this;
		}
		else
		{
			int l = getLengthForThisSlice();
			
			if (offset == l)
			{
				return getNext() == null ? empty(getUnderlyingForThisSlice()) : getNext();
			}
			else if (offset < l)
			{
				return prepend(this.getThisSlice().subsliceToEnd(offset), this.getNext());
			}
			else
			{
				//+ This could be a big slow traversal of the whole list but that's what it has to be!
				//+ Yayyyyy tail-call recursion optimization! :D
				return subsliceToEnd(offset - l);
			}
		}
	}
	
	
	public SliceList<A> subsliceFromBeginning(@Nonnegative int lengthOrExclusiveEndingBound)
	{
		if (lengthOrExclusiveEndingBound == 0)
			return empty(getUnderlyingForThisSlice());
		
		else if (lengthOrExclusiveEndingBound < 0)
			throw new IllegalArgumentException();
		
		else if (lengthOrExclusiveEndingBound > getLengthTotal())
			throw new IndexOutOfBoundsException();
		
		else
		{
			int l = getLengthForThisSlice();
			if (lengthOrExclusiveEndingBound < l)
			{
				return base(getThisSlice().subsliceFromBeginning(lengthOrExclusiveEndingBound));
			}
			else if (lengthOrExclusiveEndingBound == l)
			{
				return base(getThisSlice());
			}
			else
			{
				//+ This could be a big slow traversal of the whole list but that's what it has to be!
				//+ This could be a big slow *recreation of most of the whole list* but that's what it has to be.
				//+ We can safely call getNext() here because if it was null then lengthOrExclusiveEndingBound would either be > or = this.getLengthTotal() and we already checked for that :>
				//+ Yayyyyy tail-call recursion optimization! :D
				return prepend(getThisSlice(), getNext().subsliceFromBeginning(lengthOrExclusiveEndingBound - this.getLengthForThisSlice()));
			}
		}
	}
	
	
	
	public SliceList<A> subslice(@Nonnegative int offset, @Nonnegative int length)
	{
		if (length == getLengthTotal())
		{
			if (offset == 0)
				return this;
			else
				return subsliceToEnd(offset);
		}
		else
		{
			if (offset == 0)
				return subsliceFromBeginning(length);
			else
				return subsliceFromBeginning(length).subsliceToEnd(offset);  //Muahahaha that's easy 8>    And performant too!  Because singly-linked lists (like the one possibly implicitly created here) are easy to stack-allocate and elide, and also, this order (not the opposite), works best because fromBeginning traverses as little as possible and makes only one more list-node, and toEnd always has to recreate an entire list X'D
		}
	}
}
