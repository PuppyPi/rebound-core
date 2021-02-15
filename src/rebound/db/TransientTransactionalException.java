package rebound.db;

import rebound.annotations.semantic.FunctionalityType;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;

@FunctionalityType
public interface TransientTransactionalException
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
		return x instanceof TransientTransactionalException && ((TransientTransactionalException)x).isTransientTransactionalException();
	}
	//>>>
}
