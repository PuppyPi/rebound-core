package rebound.math.intervals;

import static rebound.math.MathUtilities.*;
import static rebound.util.CodeHinting.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import rebound.math.RationalOrInteger;

@Immutable
public class NonemptyArithmeticInfinitableRationalInterval
{
	public static final NonemptyArithmeticInfinitableRationalInterval
	Universe = new NonemptyArithmeticInfinitableRationalInterval(null, false, null, false),
	Zero = new NonemptyArithmeticInfinitableRationalInterval(0, true, 0, true),
	Positive = new NonemptyArithmeticInfinitableRationalInterval(0, false, null, false),
	Negative = new NonemptyArithmeticInfinitableRationalInterval(null, false, 0, false),
	Nonnegative = new NonemptyArithmeticInfinitableRationalInterval(0, true, null, false),
	Nonpositive = new NonemptyArithmeticInfinitableRationalInterval(null, false, 0, true);
	
	
	
	
	protected final @RationalOrInteger @Nullable Object start;
	protected final boolean startInclusive;
	protected final @RationalOrInteger @Nullable Object end;
	protected final boolean endInclusive;
	
	
	/**
	 * Note how it's fundamentally impossible to even encode an empty interval with two inclusive bounds ;3
	 * Making them be the same and both inclusive is the specific and correct way to encode a singleton set though! :D
	 * If the bounds are the same value, they *must both be inclusive!*
	 * If one was inclusive and the other wasn't, then it would be a Set (a Subset of the Rationals) which both included that element as a member of the set and didn't!  A logical contradiction!
	 * And if they're both exclusive, then it's an empty interval and this is simply defined as a nonempty type, so that's not allowed either X3
	 * 
	 * And note how it's impossible to encode the wrong kind of infinity here ;3
	 * 
	 * @param start  inclusive lower bound; null = -∞
	 * @param startInclusive  must be false if start is -∞!
	 * @param end  inclusive upper bound; null = +∞
	 * @param endInclusive  must be false if end is +∞!
	 */
	public NonemptyArithmeticInfinitableRationalInterval(@RationalOrInteger @Nullable Object start, boolean startInclusive, @RationalOrInteger @Nullable Object end, boolean endInclusive)
	{
		if (start == null && startInclusive)
			throw new IllegalArgumentException("Cannot include -∞!!");
		
		if (end == null && endInclusive)
			throw new IllegalArgumentException("Cannot include +∞!!");
		
		this.start = start == null ? null : requireRationalOrInteger(start);
		this.startInclusive = startInclusive;
		this.end = end == null ? null : requireRationalOrInteger(end);
		this.endInclusive = endInclusive;
		
		if (start != null && end != null)
		{
			int s = mathcmp(start, end);
			
			if (s > 0)
				throw new IllegalArgumentException("Lower bound ("+start+") is > Upper bound! ("+end+")");
			else if (s == 0)
			{
				@RationalOrInteger @Nonnull Object value = arbitrary(start, end);
				
				if (startInclusive)
				{
					if (!endInclusive)
						throw new IllegalArgumentException("Lower and upper bounds cannot be equal ("+value+") and one be inclusive but the other not!  That's a logical contradiction!  The interval as a Set can't both contain and not contain "+value);
				}
				else
				{
					if (endInclusive)
						throw new IllegalArgumentException("Lower and upper bounds cannot be equal ("+value+") and one be inclusive but the other not!  That's a logical contradiction!  The interval as a Set can't both contain and not contain "+value);
					else
						throw new IllegalArgumentException("This class does not support empty intervals.");
				}
			}
		}
	}
	
	
	public @Nullable @RationalOrInteger Object getStart()
	{
		return this.start;
	}
	
	public boolean isStartInclusive()
	{
		return startInclusive;
	}
	
	public @Nullable @RationalOrInteger Object getEnd()
	{
		return this.end;
	}
	
	public boolean isEndInclusive()
	{
		return endInclusive;
	}
	
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + (endInclusive ? 1231 : 1237);
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + (startInclusive ? 1231 : 1237);
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
		NonemptyArithmeticInfinitableRationalInterval other = (NonemptyArithmeticInfinitableRationalInterval)obj;
		if (end == null)
		{
			if (other.end != null)
				return false;
		}
		else if (!end.equals(other.end))
			return false;
		if (endInclusive != other.endInclusive)
			return false;
		if (start == null)
		{
			if (other.start != null)
				return false;
		}
		else if (!start.equals(other.start))
			return false;
		if (startInclusive != other.startInclusive)
			return false;
		return true;
	}
	
	
	@Override
	public String toString()
	{
		return (startInclusive ? "[" : "(")+(start == null ? "-∞" : start)+", "+(end == null ? "+∞" : end)+(endInclusive ? "]" : ")");
	}
}
