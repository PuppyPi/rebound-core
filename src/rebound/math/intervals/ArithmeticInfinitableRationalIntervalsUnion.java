package rebound.math.intervals;

import static java.util.Collections.*;
import static java.util.Objects.*;
import static rebound.math.MathUtilities.*;
import static rebound.util.collections.CollectionUtilities.*;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import rebound.annotations.semantic.simpledata.Emptyable;

public class ArithmeticInfinitableRationalIntervalsUnion
{
	public static final ArithmeticInfinitableRationalIntervalsUnion
	Empty = new ArithmeticInfinitableRationalIntervalsUnion(emptyList()),
	Universe = new ArithmeticInfinitableRationalIntervalsUnion(singletonList(NonemptyArithmeticInfinitableRationalInterval.Universe)),
	Zero = new ArithmeticInfinitableRationalIntervalsUnion(singletonList(NonemptyArithmeticInfinitableRationalInterval.Zero)),
	Positive = new ArithmeticInfinitableRationalIntervalsUnion(singletonList(NonemptyArithmeticInfinitableRationalInterval.Positive)),
	Negative = new ArithmeticInfinitableRationalIntervalsUnion(singletonList(NonemptyArithmeticInfinitableRationalInterval.Negative)),
	Nonpositive = new ArithmeticInfinitableRationalIntervalsUnion(singletonList(NonemptyArithmeticInfinitableRationalInterval.Nonpositive)),
	Nonnegative = new ArithmeticInfinitableRationalIntervalsUnion(singletonList(NonemptyArithmeticInfinitableRationalInterval.Nonnegative)),
	Nonzero = new ArithmeticInfinitableRationalIntervalsUnion(Arrays.asList(NonemptyArithmeticInfinitableRationalInterval.Negative, NonemptyArithmeticInfinitableRationalInterval.Positive));
	
	
	
	/**
	 * These must be in order with no overlaps or touching-that-could-be-combined-to-one-bigger-interval!!
	 * + This means only the first one can have -∞ and the last one can have +∞ !
	 * + This list being empty is the unique way to encode the Empty Set! :D
	 */
	protected final @Emptyable @Nonnull List<NonemptyArithmeticInfinitableRationalInterval> intervals;
	
	public ArithmeticInfinitableRationalIntervalsUnion(List<NonemptyArithmeticInfinitableRationalInterval> intervals)
	{
		this.intervals = requireNonNullElements(requireNonNull(intervals));
		
		if (intervals.size() >= 2)
		{
			NonemptyArithmeticInfinitableRationalInterval prev = null;
			
			for (NonemptyArithmeticInfinitableRationalInterval e : intervals)
			{
				boolean first = prev == null;
				
				if (!first)
				{
					if (!areIntervalsConsecutiveAndNonOverlapping(prev, e))
						throw new IllegalArgumentException("The interval "+prev+" does not wholly come before "+e+" without touching inclusive bounds!!");
				}
				
				prev = e;
			}
		}
	}
	
	protected static boolean areIntervalsConsecutiveAndNonOverlapping(NonemptyArithmeticInfinitableRationalInterval preceding, NonemptyArithmeticInfinitableRationalInterval succeeding)
	{
		if (succeeding.getStart() == null)
			return false;
		
		if (preceding.getEnd() == null)
			return false;
		
		int s = mathcmp(preceding.getEnd(), succeeding.getStart());
		
		if (s < 0)
			return true;  //Note that if the high bound of a is less than the low bound of b, then both bounds of a are less than both bounds of b, by Transitivity because NonemptyArithmeticInfinitableRationalInterval already checked its own bounds! :D
		else if (s > 0)
			return false;
		else
			return !preceding.isEndInclusive() && !succeeding.isStartInclusive();  //The intervals must not be compactable into one interval and produce the same Set (Subset of the Rationals)!  This is only possible if they're conspiring to exclude a single value—an infinitescimally small region! :3
	}
	
	
	
	public List<NonemptyArithmeticInfinitableRationalInterval> getIntervals()
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
		ArithmeticInfinitableRationalIntervalsUnion other = (ArithmeticInfinitableRationalIntervalsUnion) obj;
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
		
		for (NonemptyArithmeticInfinitableRationalInterval i : intervals)
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
