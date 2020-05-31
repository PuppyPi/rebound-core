/*
 * Created on May 19, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util;

import java.lang.reflect.InvocationTargetException;
import rebound.annotations.purelyforhumans.DeprecatedInFavorOfMember;
import rebound.annotations.semantic.AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse;
import rebound.annotations.semantic.temporal.NeverReturns;
import rebound.exceptions.WrappedThrowableRuntimeException;
import rebound.util.objectutil.JavaNamespace;

/**
 * Note!  I updated this niftily to make it so some of the functions have a return type, though they {@link NeverReturns NeverReturn}, THAT YOU CAN THROW IN YOUR CODE to avoid any need to account for dealing with unreachable conditions!  (ie, putting the constraint satisfaction responsibility on this code instead of EVERY USAGE OF IT XD')      ..a bit kludgey XD''    But a nifty hack, don't you think? :>     (at least since we're already dealing with exceptions, and they're supposed to throw something anyways X3 )
 * 
 * @author Puppy Pie ^_^
 */
@AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse
public class ExceptionUtilities
implements JavaNamespace
{
	/**
	 * Note: BE CAREFUL WITH UNVERIFIED THROWS!
	 * (but in practice, I don't really see how a RuntimeException subclass is intrinsically less problem-causing than an unverified exception xD )
	 */
	@NeverReturns
	@AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse  //if Escape wasn't using it, I'd like to rename it to rethrowUnsafe()  ^^'
	public static RuntimeException throwGeneralThrowableAttemptingUnverifiedThrow(Throwable target)
	{
		if (target instanceof RuntimeException)
			throw (RuntimeException)target;
		else if (target instanceof Error)
			throw (Error)target;
		else
		{
			//Ehhhhh maybe we don't do this XD ^^'''
			//			if (UnsafePlatformExceptionUtilities.canThrowUnchecked())
			//			{
			//				UnsafePlatformExceptionUtilities.throwUnchecked(target);
			//			}
			//
			//			else
			
			{
				//else, just wrap it with one of these; ahwell :>
				throw new WrappedThrowableRuntimeException(target);
			}
		}
		
		
		//throw new UnreachableCodeException();
	}
	
	
	
	
	//	@NeverReturns
	//	public static RuntimeException throwUnchecked(Throwable target)
	//	{
	//		return UnsafePlatformExceptionUtilities.throwUnchecked(target);
	//	}
	
	
	
	
	
	//	/**
	//	 * Note: BE CAREFUL WITH UNVERIFIED THROWS!
	//	 * (but in practice, I don't really see how a RuntimeException subclass is intrinsically less problem-causing than an unverified exception xD )
	//	 */
	//	@NeverReturns
	//	public static RuntimeException unwrapAndThrowTargetOfInvocationTargetExceptionAttemptingUnverifiedThrow(InvocationTargetException invocationTargetException)
	//	{
	//		Throwable target = invocationTargetException.getTargetException(); //same as getCause() as of Java 4
	//		throw throwGeneralThrowableAttemptingUnverifiedThrow(target);
	//	}
	
	
	
	
	
	
	
	public static void throwIfFatalError(Throwable t)
	{
		if (BasicExceptionUtilities.isFatalError(t))
			throwGeneralThrowableAttemptingUnverifiedThrow(t);
	}
	
	public static void defaultThrowableHandling(Throwable t)
	{
		throwIfFatalError(t);
		t.printStackTrace();
	}
	
	
	
	public static WrappedThrowableRuntimeException rewrapToUnchecked(InvocationTargetException exc)
	{
		return new WrappedThrowableRuntimeException(exc.getCause());
	}
	
	
	
	
	
	
	
	
	
	@Deprecated
	@DeprecatedInFavorOfMember(cls=BasicExceptionUtilities.class, member="rethrowSafe")
	@NeverReturns
	@AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse
	public static RuntimeException throwGeneralThrowableIfPossible(Throwable target) throws RuntimeException, Error
	{
		return BasicExceptionUtilities.rethrowSafe(target);
	}
}
