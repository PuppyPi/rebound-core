package rebound.util.functional.predicates;

import java.io.Serializable;
import java.util.function.Predicate;
import rebound.concurrency.immutability.StaticallyConcurrentlyImmutable;
import rebound.util.objectutil.StaticallyIdentityless;

/**
 * A NOT gate! :D
 * 
 * @author Puppy Pie ^_^
 */
public class NotPredicate<E>
implements Predicate<E>, StaticallyConcurrentlyImmutable, StaticallyIdentityless, Serializable
{
	private static final long serialVersionUID = 1L;
	
	
	protected final Predicate<E> underlyingFunction;
	
	public NotPredicate(Predicate<E> underlyingPredicate)
	{
		this.underlyingFunction = underlyingPredicate;
	}
	
	
	/**
	 * @return either a new {@link NotPredicate}, or the underlying function of the provided {@link NotPredicate} (if that's what is provided!)  ;D!
	 */
	public static final <E> Predicate<E> not(Predicate<E> underlyingPredicate)
	{
		if (underlyingPredicate instanceof NotPredicate)
		{
			return ((NotPredicate)underlyingPredicate).getUnderlyingFunction();
		}
		else
		{
			return new NotPredicate<E>(underlyingPredicate);
		}
	}
	
	
	
	public Predicate<E> getUnderlyingFunction()
	{
		return this.underlyingFunction;
	}
	
	
	@Override
	public boolean test(E input)
	{
		return !getUnderlyingFunction().test(input);
	}
}
