package rebound.util;

import rebound.annotations.semantic.temporal.NeverReturns;
import rebound.exceptions.StructuredClassCastException;
import rebound.exceptions.UnexpectedHardcodedEnumValueException;
import rebound.exceptions.WrappedThrowableRuntimeException;
import rebound.util.functional.FunctionInterfaces.UnaryProcedureBoolean;

public class BasicExceptionUtilities
{
	public static void tryelse(Runnable tryBody, UnaryProcedureBoolean resolutionBody)
	{
		boolean success = false;
		
		try
		{
			tryBody.run();
			success = true;
		}
		finally
		{
			resolutionBody.f(success);
		}
	}
	
	public static void tryelse(Runnable tryBody, Runnable successBody, Runnable failureBody)
	{
		tryelse(tryBody, success ->
		{
			if (success)
				successBody.run();
			else
				failureBody.run();
		});
	}
	
	
	
	
	
	
	
	
	
	public static RuntimeException newClassCastExceptionOrNullPointerException(Object o)
	{
		if (o == null)
			return new NullPointerException();
		else
			return new StructuredClassCastException(o.getClass());
	}
	
	public static RuntimeException newClassCastExceptionOrNullPointerException(Object o, String message)
	{
		if (o == null)
			return new NullPointerException(message);
		else
			return new StructuredClassCastException(message, o.getClass());
	}
	
	public static RuntimeException newClassCastExceptionOrNullPointerException(Object o, Class classItWasSupposedToBe)
	{
		if (o == null)
			return new NullPointerException();
		else
			return new StructuredClassCastException(o.getClass(), classItWasSupposedToBe);
	}
	
	/**
	 * @param o  this is not typed as {@link Enum} so that this works for "enums" that aren't official java 5.0 enums :3
	 */
	public static RuntimeException newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(Object o)
	{
		if (o == null)
			return new NullPointerException();
		else
			return new UnexpectedHardcodedEnumValueException(o);
	}
	
	
	
	
	
	
	
	
	@NeverReturns
	public static RuntimeException throwGeneralThrowableIfPossible(Throwable target) throws RuntimeException, Error
	{
		if (target instanceof RuntimeException)
			throw (RuntimeException)target;
		else if (target instanceof Error)
			throw (Error)target;
		else
			throw new WrappedThrowableRuntimeException(target);
	}
	
	public static boolean isFatalError(Throwable t)
	{
		if (t instanceof Error)
		{
			if (t instanceof AssertionError || t instanceof LinkageError)
				return false;
			else
				return true;
		}
		else
		{
			return false;
		}
	}
}
