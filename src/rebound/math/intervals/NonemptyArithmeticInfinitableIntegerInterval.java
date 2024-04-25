package rebound.math.intervals;

import static rebound.math.MathUtilities.*;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import rebound.math.PolyInteger;

@Immutable
public class NonemptyArithmeticInfinitableIntegerInterval
{
	protected final @PolyInteger @Nullable Object start;
	protected final @PolyInteger @Nullable Object end;
	
	
	/**
	 * Note how it's fundamentally impossible to even encode an empty interval with two inclusive bounds ;3
	 * Making them be the same is the specific and correct way to encode a singleton set though! :D
	 * 
	 * And note how it's impossible to encode the wrong kind of infinity here ;3
	 * 
	 * @param start  inclusive lower bound; null = -∞
	 * @param end  inclusive upper bound; null = +∞
	 */
	public NonemptyArithmeticInfinitableIntegerInterval(@PolyInteger @Nullable Object start, @PolyInteger @Nullable Object end)
	{
		this.start = start == null ? null : requireInteger(start);
		this.end = end == null ? null : requireInteger(end);
		
		if (start != null && end != null && mathcmp(start, end) > 0)
			throw new IllegalArgumentException("Lower bound ("+start+") is > Upper bound! ("+end+")");
	}
	
	
	public @Nullable @PolyInteger Object getStart()
	{
		return this.start;
	}
	
	public @Nullable @PolyInteger Object getEnd()
	{
		return this.end;
	}
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
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
		NonemptyArithmeticInfinitableIntegerInterval other = (NonemptyArithmeticInfinitableIntegerInterval)obj;
		if (end == null)
		{
			if (other.end != null)
				return false;
		}
		else if (!end.equals(other.end))
			return false;
		if (start == null)
		{
			if (other.start != null)
				return false;
		}
		else if (!start.equals(other.start))
			return false;
		return true;
	}
	
	
	@Override
	public String toString()
	{
		return "["+(start == null ? "-∞" : start)+", "+(end == null ? "+∞" : end)+"]";
	}
}
