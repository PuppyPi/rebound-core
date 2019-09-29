package rebound.testing;

import rebound.annotations.semantic.temporal.NeverReturns;

public class WidespreadTestingUtilities
{
	/**
	 * Note that these *must never be disabled!!*
	 * That's kind of the whole point of it as opposed to a normal Java "assert"  X3
	 */
	public static void asrt(boolean condition) throws AssertionError
	{
		if (!condition)
			throw new AssertionError();
	}
	
	@NeverReturns
	public static RuntimeException fail()
	{
		throw new AssertionError();
	}
}
