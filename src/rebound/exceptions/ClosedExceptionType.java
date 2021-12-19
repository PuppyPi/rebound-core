package rebound.exceptions;

import rebound.annotations.semantic.FunctionalityType;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;
import rebound.db.TransientTransactionalExceptionType;

/**
 * Make sure to grandfather in things that need grandfathering in!
 * 
 * Compare to {@link TransientTransactionalExceptionType}
 */
@FunctionalityType
public interface ClosedExceptionType
{
	//<<< tp ClosedDatabaseException
	@TraitPredicate
	public default boolean isClosedDatabaseException()
	{
		return true;
	}
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof ClosedExceptionType && ((ClosedExceptionType)x).isClosedDatabaseException();
	}
	//>>>
}
