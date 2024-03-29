package rebound.util;

public enum Signum
{
	Negative,
	Zero,
	Positive,
	;
	
	
	public int toIntegerValue()
	{
		return this == Positive ? 1 : (this == Zero ? 0 : -1);
	}
	
	public static Signum fromIntegerValue(int v)
	{
		return v > 0 ? Positive : (v < 0 ? Negative : Zero);
	}
	
	
	
	
	/* <<<
primxp
_$$primxpconf:noboolean$$_
	
	public static Signum signumSemantic(_$$prim$$_ x)
	{
		return x < 0 ? Negative : (x > 0 ? Positive : Zero);
	}
	 */
	
	
	public static Signum signumSemantic(byte x)
	{
		return x < 0 ? Negative : (x > 0 ? Positive : Zero);
	}
	
	
	public static Signum signumSemantic(char x)
	{
		return x < 0 ? Negative : (x > 0 ? Positive : Zero);
	}
	
	
	public static Signum signumSemantic(short x)
	{
		return x < 0 ? Negative : (x > 0 ? Positive : Zero);
	}
	
	
	public static Signum signumSemantic(float x)
	{
		return x < 0 ? Negative : (x > 0 ? Positive : Zero);
	}
	
	
	public static Signum signumSemantic(int x)
	{
		return x < 0 ? Negative : (x > 0 ? Positive : Zero);
	}
	
	
	public static Signum signumSemantic(double x)
	{
		return x < 0 ? Negative : (x > 0 ? Positive : Zero);
	}
	
	
	public static Signum signumSemantic(long x)
	{
		return x < 0 ? Negative : (x > 0 ? Positive : Zero);
	}
	// >>>
}
