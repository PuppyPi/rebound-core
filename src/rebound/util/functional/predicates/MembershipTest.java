package rebound.util.functional.predicates;

import java.util.Set;
import java.util.function.Predicate;
import rebound.concurrency.immutability.StaticallyConcurrentlyImmutable;
import rebound.util.collections.CollectionUtilities;
import rebound.util.functional.EqualityComparator;
import rebound.util.objectutil.StaticallyIdentityless;

/**
 * These in this package are all for inspecting and rearranging/reimplementing/optimizing predicate trees! :D
 * 
 * @author Puppy Pie ^_^
 */
public class MembershipTest<E>
implements Predicate<E>, StaticallyConcurrentlyImmutable, StaticallyIdentityless
{
	protected final Set<E> set;
	
	public MembershipTest(Set<E> set)
	{
		this.set = set;
	}
	
	public Set<E> getSet()
	{
		return this.set;
	}
	
	public EqualityComparator<E> getEqualityComparator()
	{
		return CollectionUtilities.getBoundCollectionEqualityComparator(this.set);
	}
	
	
	@Override
	public boolean test(E input)
	{
		return set.contains(input);
	}
}
