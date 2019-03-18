package rebound.util;

import rebound.annotations.semantic.temporal.NeverReturns;
import rebound.exceptions.StructuredClassCastException;
import rebound.exceptions.UnexpectedHardcodedEnumValueException;
import rebound.exceptions.WrappedThrowableRuntimeException;

public class BasicExceptionUtilities
{
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
