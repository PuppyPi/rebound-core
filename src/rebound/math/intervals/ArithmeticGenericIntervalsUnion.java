package rebound.math.intervals;

import static java.util.Objects.*;
import static rebound.util.collections.CollectionUtilities.*;
import java.util.List;
import javax.annotation.Nonnull;
import rebound.annotations.semantic.simpledata.Emptyable;

public class ArithmeticGenericIntervalsUnion<N>
{
	/**
	 * These must be in order with no overlaps or touching-that-could-be-combined-to-one-bigger-interval!!
	 * + This means only the first one can have -∞ and the last one can have +∞ !
	 * + None of the internal intervals may be empty!
	 */
	protected final @Emptyable @Nonnull List<ArithmeticGenericInterval<N>> intervals;
	
	public ArithmeticGenericIntervalsUnion(List<ArithmeticGenericInterval<N>> intervals)
	{
		this.intervals = requireNonNullElements(requireNonNull(intervals));
	}
	
	public List<ArithmeticGenericInterval<N>> getIntervals()
	{
		return intervals;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((intervals == null) ? 0 : intervals.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArithmeticGenericIntervalsUnion other = (ArithmeticGenericIntervalsUnion) obj;
		if (intervals == null)
		{
			if (other.intervals != null)
				return false;
		}
		else if (!intervals.equals(other.intervals))
			return false;
		return true;
	}
	
	
	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder();
		boolean first = true;
		
		for (ArithmeticGenericInterval<N> i : intervals)
		{
			if (first)
				first = false;
			else
				b.append(" ∪ ");
			
			b.append(i);
		}
		
		if (first)
			return "∅";
			
		return b.toString();
	}
}
