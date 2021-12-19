package rebound.db;

import rebound.annotations.semantic.FunctionalityType;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;

@FunctionalityType
public interface TransientTransactionalExceptionType
{
	//<<< tp TransientTransactionalException
	@TraitPredicate
	public default boolean isTransientTransactionalException()
	{
		return true;
	}
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof TransientTransactionalExceptionType && ((TransientTransactionalExceptionType)x).isTransientTransactionalException();
	}
	//>>>
}
