package rebound.math;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ArithmeticGenericInterval<N>
{
	protected final N start;
	protected final boolean startInclusive;
	protected final N end;
	protected final boolean endInclusive;
	
	public ArithmeticGenericInterval(N start, boolean startInclusive, N end, boolean endInclusive)
	{
		this.start = start;
		this.startInclusive = startInclusive;
		this.end = end;
		this.endInclusive = endInclusive;
	}
	
	public N getStart()
	{
		return start;
	}
	
	public boolean isStartInclusive()
	{
		return startInclusive;
	}
	
	public N getEnd()
	{
		return end;
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
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + (startInclusive ? 1231 : 1237);
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + (endInclusive ? 1231 : 1237);
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
		ArithmeticGenericInterval other = (ArithmeticGenericInterval) obj;
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
}
