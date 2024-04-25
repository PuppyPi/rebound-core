package rebound.math.intervals;

import static java.util.Collections.*;
import static java.util.Objects.*;
import static rebound.math.MathUtilities.*;
import static rebound.util.collections.CollectionUtilities.*;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import rebound.annotations.semantic.simpledata.Emptyable;

public class ArithmeticInfinitableIntegerIntervalsUnion
{
	public static final ArithmeticInfinitableIntegerIntervalsUnion
	Empty = new ArithmeticInfinitableIntegerIntervalsUnion(emptyList()),
	Universe = new ArithmeticInfinitableIntegerIntervalsUnion(singletonList(NonemptyArithmeticInfinitableIntegerInterval.Universe)),
	Zero = new ArithmeticInfinitableIntegerIntervalsUnion(singletonList(NonemptyArithmeticInfinitableIntegerInterval.Zero)),
	Positive = new ArithmeticInfinitableIntegerIntervalsUnion(singletonList(NonemptyArithmeticInfinitableIntegerInterval.Positive)),
	Negative = new ArithmeticInfinitableIntegerIntervalsUnion(singletonList(NonemptyArithmeticInfinitableIntegerInterval.Negative)),
	Nonpositive = new ArithmeticInfinitableIntegerIntervalsUnion(singletonList(NonemptyArithmeticInfinitableIntegerInterval.Nonpositive)),
	Nonnegative = new ArithmeticInfinitableIntegerIntervalsUnion(singletonList(NonemptyArithmeticInfinitableIntegerInterval.Nonnegative)),
	Nonzero = new ArithmeticInfinitableIntegerIntervalsUnion(Arrays.asList(NonemptyArithmeticInfinitableIntegerInterval.Negative, NonemptyArithmeticInfinitableIntegerInterval.Positive));
	
	
	
	/**
	 * These must be in order with no overlaps or touching-that-could-be-combined-to-one-bigger-interval!!
	 * + This means only the first one can have -∞ and the last one can have +∞ !
	 * + This list being empty is the unique way to encode the Empty Set! :D
	 */
	protected final @Emptyable @Nonnull List<NonemptyArithmeticInfinitableIntegerInterval> intervals;
	
	public ArithmeticInfinitableIntegerIntervalsUnion(List<NonemptyArithmeticInfinitableIntegerInterval> intervals)
	{
		this.intervals = requireNonNullElements(requireNonNull(intervals));
		
		if (intervals.size() >= 2)
		{
			NonemptyArithmeticInfinitableIntegerInterval prev = null;
			
			for (NonemptyArithmeticInfinitableIntegerInterval e : intervals)
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
	
	protected static boolean areIntervalsConsecutiveAndNonOverlapping(NonemptyArithmeticInfinitableIntegerInterval preceding, NonemptyArithmeticInfinitableIntegerInterval succeeding)
	{
		if (succeeding.getStart() == null)
			return false;
		
		if (preceding.getEnd() == null)
			return false;
		
		return mathcmp(preceding.getEnd(), succeeding.getStart()) < 0;  //Note that if the high bound of a is less than the low bound of b, then both bounds of a are less than both bounds of b, by Transitivity because NonemptyArithmeticInfinitableIntegerInterval already checked its own bounds! :D
	}
	
	
	
	public List<NonemptyArithmeticInfinitableIntegerInterval> getIntervals()
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
		ArithmeticInfinitableIntegerIntervalsUnion other = (ArithmeticInfinitableIntegerIntervalsUnion) obj;
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
		
		for (NonemptyArithmeticInfinitableIntegerInterval i : intervals)
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
