package rebound.db;

import rebound.annotations.semantic.FunctionalityType;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;
import rebound.exceptions.ClosedExceptionType;

/**
 * Compare to {@link ClosedExceptionType}
 */
@FunctionalityType
public interface TransientTransactionalExceptionType
{
	//<<< tp TransientTransactionalExceptionType
	@TraitPredicate
	public default boolean isTransientTransactionalExceptionType()
	{
		return true;
	}
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof TransientTransactionalExceptionType && ((TransientTransactionalExceptionType)x).isTransientTransactionalExceptionType();
	}
	//>>>
}
