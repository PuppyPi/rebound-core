package rebound.util.collections;

import javax.annotation.Nonnull;

public final class Slice<A>
extends Interval<Slice<A>>
{
	protected final @Nonnull A underlying;
	
	
	public Slice(@Nonnull A underlying, int offset, int length)
	{
		super(offset, length);
		
		if (underlying == null)  //If you wanted to set it to null, use Interval instead :>
			throw new NullPointerException();
		
		this.underlying = underlying;
	}
	
	public static <E> Slice<E> fromRange(E underlying, int startInclusive, int endExclusive)
	{
		return new Slice<E>(underlying, startInclusive, endExclusive - startInclusive);
	}
	
	
	
	@Nonnull
	public A getUnderlying()
	{
		return this.underlying;
	}
	
	
	
	
	
	
	@Override
	protected Slice<A> subslice0(int offset, int length)
	{
		return new Slice<A>(this.underlying, this.offset + offset, length);
	}
}
