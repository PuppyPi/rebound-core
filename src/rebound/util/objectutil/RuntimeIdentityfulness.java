package rebound.util.objectutil;

import rebound.annotations.semantic.FunctionalityInterface;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;

@FunctionalityInterface
public interface RuntimeIdentityfulness
{
	@TraitPredicate
	public boolean hasIdentity();
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof RuntimeIdentityfulness && ((RuntimeIdentityfulness)x).hasIdentity();
	}
}
