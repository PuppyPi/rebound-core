package rebound.util.objectutil;

import rebound.annotations.semantic.temporal.ConstantReturnValue;

public class LintingCircumvinting
{
	@ConstantReturnValue
	public static boolean pass(boolean x)
	{
		return x;
	}
	
	@ConstantReturnValue
	public static boolean never()
	{
		return false;
	}
	
	@ConstantReturnValue
	public static boolean always()
	{
		return true;
	}
}
