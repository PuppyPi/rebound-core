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
	
	
	
	
	public static void softasrt(boolean condition) throws AssertionError
	{
		if (!condition)
			logBug();
	}
	
	public static void softasrt(boolean condition, String message) throws AssertionError
	{
		if (!condition)
			logBug(message);
	}
	
	public static void softasrt(boolean condition, NullaryFunction<?> detailMessageClosure) throws AssertionError
	{
		if (!condition)
		{
			Object msg = detailMessageClosure.f();
			logBug(toStringNT(msg));
		}
	}
	
	public static <E> E expectNonNull(E x)
	{
		//expect(x != null);
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
			softasrt(condition);
	}
	
	public static void casrt(boolean hard, boolean condition, String message) throws AssertionError
	{
		if (hard)
			asrt(condition, message);
		else
			softasrt(condition, message);
	}
	
	public static void casrt(boolean hard, boolean condition, NullaryFunction<?> detailMessageClosure) throws AssertionError
	{
		if (hard)
			asrt(condition, detailMessageClosure);
		else
			softasrt(condition, detailMessageClosure);
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
}
