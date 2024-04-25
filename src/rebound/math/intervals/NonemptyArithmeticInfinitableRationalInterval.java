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
	protected final @RationalOrInteger @Nullable Object start;
	protected final boolean startInclusive;
	protected final @RationalOrInteger @Nullable Object end;
	protected final boolean endInclusive;
	
	
	/**
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
		
		this.start = start;
		this.startInclusive = startInclusive;
		this.end = end;
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
