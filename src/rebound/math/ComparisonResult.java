package rebound.math;

import java.util.Comparator;

public enum ComparisonResult
{
	FirstIsLessThanSecond (-1),
	Equal (0),
	FirstIsGreaterThanSecond (+1),
	;
	
	
	private final int differenceSignumValue;
	
	private ComparisonResult(int differenceSignumValue)
	{
		this.differenceSignumValue = differenceSignumValue;
	}
	
	
	
	/**
	 * @return what you'd get from {@link Comparator#compare(Object, Object)}, except it's only ever -1 or +1 (or 0), not just any negative and positive numbers :3
	 */
	public int getDifferenceSignumValue()
	{
		return differenceSignumValue;
	}
	
	
	public static ComparisonResult forDifferenceSignumValue(int v)
	{
		return v < 0 ? FirstIsLessThanSecond : (v > 0 ? FirstIsGreaterThanSecond : Equal);
	}
}
