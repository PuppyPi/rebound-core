package rebound.testing;

import static rebound.GlobalCodeMetastuffContext.*;
import static rebound.util.objectutil.ObjectUtilities.*;
import rebound.annotations.semantic.temporal.NeverReturns;
import rebound.util.functional.FunctionInterfaces.NullaryFunction;

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
	
	public static void asrt(boolean condition, String message) throws AssertionError
	{
		if (!condition)
			throw new AssertionError(message);
	}
	
	public static void asrt(boolean condition, NullaryFunction<?> detailMessageClosure) throws AssertionError
	{
		if (!condition)
		{
			Object msg = detailMessageClosure.f();
			throw new AssertionError(msg);
		}
	}
	
	
	
	
	public static void expect(boolean condition) throws AssertionError
	{
		if (!condition)
			logBug();
	}
	
	public static void expect(boolean condition, String message) throws AssertionError
	{
		if (!condition)
			logBug(message);
	}
	
	public static void expect(boolean condition, NullaryFunction<?> detailMessageClosure) throws AssertionError
	{
		if (!condition)
		{
			Object msg = detailMessageClosure.f();
			logBug(toStringNT(msg));
		}
	}
	
	public static <E> E expectNonNull(E x)
	{
		expect(x != null);
		return x;
	}
	
	public static <E> E expectNonNull(E x, E valueIfNull)
	{
		if (x == null)
		{
			logBug();
			return valueIfNull;
		}
		else
		{
			return x;
		}
	}
	
	
	
	public static void casrt(boolean hard, boolean condition) throws AssertionError
	{
		if (hard)
			asrt(condition);
		else
			expect(condition);
	}
	
	public static void casrt(boolean hard, boolean condition, String message) throws AssertionError
	{
		if (hard)
			asrt(condition, message);
		else
			expect(condition, message);
	}
	
	public static void casrt(boolean hard, boolean condition, NullaryFunction<?> detailMessageClosure) throws AssertionError
	{
		if (hard)
			asrt(condition, detailMessageClosure);
		else
			expect(condition, detailMessageClosure);
	}
	
	
	
	
	
	
	
	/**
	 * Simple, easy failure.  (Throw an AssertionError)
	 */
	@NeverReturns
	public static RuntimeException fail() throws AssertionError
	{
		throw new AssertionError();
	}
	
	/**
	 * Simple, easy failure.  (Throw an AssertionError)
	 */
	@NeverReturns
	public static RuntimeException fail(String message) throws AssertionError
	{
		throw new AssertionError(message);
	}
	
	
	
	
	
	
	
	
	/**
	 * Useful eg, in a conditional expression (like x != null ? x.getFoo() : error())
	 */
	@NeverReturns
	public static <E> E error()
	{
		throw new RuntimeException();
	}
	
	
	/* <<<
	primxp
	
	@NeverReturns
	public static _$$prim$$_ error_$$Prim$$_()
	{
		throw new RuntimeException();
	}
	 */
	
	@NeverReturns
	public static boolean errorBoolean()
	{
		throw new RuntimeException();
	}
	
	@NeverReturns
	public static byte errorByte()
	{
		throw new RuntimeException();
	}
	
	@NeverReturns
	public static char errorChar()
	{
		throw new RuntimeException();
	}
	
	@NeverReturns
	public static short errorShort()
	{
		throw new RuntimeException();
	}
	
	@NeverReturns
	public static float errorFloat()
	{
		throw new RuntimeException();
	}
	
	@NeverReturns
	public static int errorInt()
	{
		throw new RuntimeException();
	}
	
	@NeverReturns
	public static double errorDouble()
	{
		throw new RuntimeException();
	}
	
	@NeverReturns
	public static long errorLong()
	{
		throw new RuntimeException();
	}
	// >>>
}
