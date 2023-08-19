package rebound.util;

public enum Sign
{
	Negative,
	Positive,
	;
	
	
	public Sign opposite()
	{
		return this == Negative ? Positive : Negative;
	}
	
	public static Sign multiplication(Sign a, Sign b)
	{
		return a == b ? Positive : Negative;
	}
}
