package rebound.math;

import javax.annotation.concurrent.Immutable;
import rebound.annotations.semantic.operationspecification.UnhashableType;

/**
 * This is meant to be a *very* general datatype!
 * So note that you'll have to use an equals() and hashCode() commensurate with what you put in the start and end!
 * For example, if you use {@link RealNumeric} values then use {@link MathUtilities#gintervalEquals(ArithmeticGenericInterval, ArithmeticGenericInterval)} / {@link MathUtilities#gintervalHashCode(ArithmeticGenericInterval)}
 */
@UnhashableType  //because how do we know what things are in start and end!?
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
}
