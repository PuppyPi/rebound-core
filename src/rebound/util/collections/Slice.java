package rebound.util.collections;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public final class Slice<A>
extends Interval<Slice<A>>
{
	protected final @Nonnull A underlying;
	
	
	public Slice(@Nonnull A underlying, @Nonnegative int offset, @Nonnegative int length)
	{
		super(offset, length);
		
		if (underlying == null)  //If you wanted to set it to null, use Interval instead :>
			throw new NullPointerException();
		
		this.underlying = underlying;
	}
	
	public static <E> Slice<E> fromRange(@Nonnull E underlying, @Nonnegative int startInclusive, @Nonnegative int endExclusive)
	{
		return new Slice<E>(underlying, startInclusive, endExclusive - startInclusive);
	}
	
	
	
	@Nonnull
	public A getUnderlying()
	{
		return this.underlying;
	}
	
	
	
	
	
	
	@Override
	protected Slice<A> subslice0(@Nonnegative int offset, @Nonnegative int length)
	{
		return new Slice<A>(this.underlying, this.offset + offset, length);
	}
	
	
	
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((underlying == null) ? 0 : underlying.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Slice other = (Slice) obj;
		if (underlying == null)
		{
			if (other.underlying != null)
				return false;
		}
		else if (!underlying.equals(other.underlying))
			return false;
		return true;
	}
}
