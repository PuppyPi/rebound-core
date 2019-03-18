/*
 * Created on Feb 25, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.functional;

import java.io.Serializable;
import java.util.Set;
import java.util.function.Predicate;
import rebound.concurrency.immutability.StaticallyConcurrentlyImmutable;
import rebound.concurrency.immutability.StaticallyMutable;
import rebound.util.collections.CollectionUtilities;
import rebound.util.functional.FunctionalUtilities.UnaryUnderliedFunctionR;
import rebound.util.functional.FunctionalUtilities.UnaryUnderliedFunctionW;
import rebound.util.objectutil.EqualityComparator;
import rebound.util.objectutil.JavaNamespace;
import rebound.util.objectutil.StaticallyIdentityless;

public class Predicates
implements JavaNamespace
{
	/**
	 * Interfaces for inspecting and rearranging/reimplementing/optimizing predicate trees! :D
	 * 
	 * @author Puppy Pie ^_^
	 */
	public static interface BooleanInverter<Input>
	extends Predicate<Input>, UnaryUnderliedFunctionR<Predicate<Input>>
	{
	}
	
	
	/**
	 * Interfaces for inspecting and rearranging/reimplementing/optimizing predicate trees! :D
	 * 
	 * @author Puppy Pie ^_^
	 */
	public static interface MembershipTest<E>
	extends Predicate<E>
	{
		public Set<E> getSet();
		
		public EqualityComparator<E> getEqualityComparator();
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * A NOT gate! :D
	 * 
	 * @author Puppy Pie ^_^
	 */
	public static class BooleanInverterImmutable<Input>
	implements BooleanInverter<Input>, StaticallyConcurrentlyImmutable, StaticallyIdentityless, Serializable
	{
		private static final long serialVersionUID = 5339657331618331094L;
		
		
		protected final Predicate<Input> underlyingFunction;
		
		public BooleanInverterImmutable(Predicate<Input> underlyingPredicate)
		{
			this.underlyingFunction = underlyingPredicate;
		}
		
		
		@Override
		public boolean test(Input input)
		{
			return !getUnderlyingFunction().test(input);
		}
		
		
		@Override
		public Predicate<Input> getUnderlyingFunction()
		{
			return this.underlyingFunction;
		}
		
		
		
		/**
		 * @return either a new {@link BooleanInverterImmutable}, or the underlying function of the provided {@link BooleanInverterImmutable} (if that's what is provided!)  ;D!
		 */
		public static final <Input> Predicate<Input> Not(Predicate<Input> underlyingPredicate)
		{
			if (underlyingPredicate instanceof BooleanInverterImmutable)
			{
				return ((BooleanInverterImmutable)underlyingPredicate).getUnderlyingFunction();
			}
			else
			{
				return new BooleanInverterImmutable<Input>(underlyingPredicate);
			}
		}
	}
	
	
	
	
	
	/**
	 * A (rewireable) NOT gate! :D
	 * 
	 * @author Puppy Pie ^_^
	 */
	public static class BooleanInverterMutable<Input>
	implements BooleanInverter<Input>, StaticallyMutable, UnaryUnderliedFunctionW<Predicate<Input>>, Serializable
	{
		private static final long serialVersionUID = -5358881061200520707L;
		
		
		protected Predicate<Input> underlyingFunction;
		
		public BooleanInverterMutable()
		{
		}
		
		public BooleanInverterMutable(Predicate<Input> underlyingPredicate)
		{
			this.underlyingFunction = underlyingPredicate;
		}
		
		
		@Override
		public boolean test(Input input)
		{
			return !getUnderlyingFunction().test(input);
		}
		
		
		@Override
		public Predicate<Input> getUnderlyingFunction()
		{
			return this.underlyingFunction;
		}
		
		@Override
		public void setUnderlyingFunction(Predicate<Input> underlyingPredicate)
		{
			this.underlyingFunction = underlyingPredicate;
		}
	}
	
	
	
	
	
	
	
	
	public static class MembershipTestImmutable<E>
	implements MembershipTest<E>, StaticallyConcurrentlyImmutable, StaticallyIdentityless
	{
		protected final Set<E> set;
		
		public MembershipTestImmutable(Set<E> set)
		{
			this.set = set;
		}
		
		@Override
		public boolean test(E input)
		{
			return this.set.contains(input);
		}
		
		@Override
		public Set<E> getSet()
		{
			return this.set;
		}
		
		@Override
		public EqualityComparator<E> getEqualityComparator()
		{
			return CollectionUtilities.getBoundCollectionEqualityComparator(this.set);
		}
	}
	
	
	
	public static class MembershipTestMutable<E>
	implements MembershipTest<E>, StaticallyMutable
	{
		protected Set<E> set;
		
		public MembershipTestMutable()
		{
		}
		
		public MembershipTestMutable(Set<E> set)
		{
			this.set = set;
		}
		
		@Override
		public boolean test(E input)
		{
			return this.set.contains(input);
		}
		
		@Override
		public Set<E> getSet()
		{
			return this.set;
		}
		
		@Override
		public EqualityComparator<E> getEqualityComparator()
		{
			return CollectionUtilities.getBoundCollectionEqualityComparator(this.set);
		}
		
		public void setSet(Set<E> set)
		{
			this.set = set;
		}
	}
}
