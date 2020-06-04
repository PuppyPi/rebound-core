/*
 * Created on Jun 2, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections;

import java.util.Collection;
import rebound.annotations.semantic.FunctionalityType;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;

/**
 * Note that weak-reference implementations may always appear to have less elements
 * than they actually do.  Even if they purged themselves of lost references
 * immediately prior to each operation, it's still always possible that references
 * will become lost between when the operation returns and the caller's next instruction!
 * 
 * So, it is very likely that weak collections/maps won't purge themselves terribly often
 * and may even present as containing members they could have recognized as lost
 *  *before* the operation began, had they purged themselves!
 * (for example, purging only on writes, not reads :P )
 * This is especially true of {@link Collection#size()} / {@link Collection#isEmpty()}
 * 
 * @author Puppy Pie ^_^
 */
@FunctionalityType
public interface WeakCollection
{
	//<<< tp WeakCollection
	@TraitPredicate
	public default boolean isWeakCollection()
	{
		return true;
	}
	
	@StaticTraitPredicate
	public static boolean is(Object x)
	{
		return x instanceof WeakCollection && ((WeakCollection)x).isWeakCollection();
	}
	//>>>
}
