package rebound.util.functional.predicates;

import static java.util.Objects.*;
import static rebound.util.CodeHinting.*;
import java.util.Comparator;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.util.functional.FunctionalUtilities;

/**
 * Null here = infinity/unbounded in that side :3
 * 
 * + To encode an empty bound with inclusive predicates, you can just use {@link AlwaysFalsePredicate} instead of this X3
 * + To encode a complete bound on a finite type (eg, Byte) with exclusive predicate[s], you can just use {@link AlwaysTruePredicate} instead of this X3
 */
public class BoundsPredicate<E>
implements Predicate<E>
{
	protected final E lower, upper;
	protected final boolean lowerExclusive, upperExclusive;
	protected final Comparator<E> comparison;
	
	
	/**
	 * Consider using {@link #boundsPredicate(Object, boolean, Object, boolean, Comparator)} instead, since it will return {@link AlwaysFalsePredicate} and {@link ExactlyThisPredicate} and such as appropriate :3
	 * 
	 * @param lowerExclusive unused if lower == null (unbounded)
	 * @param upperExclusive unused if upper == null (unbounded)
	 */
	public BoundsPredicate(@Nullable E lower, @Nullable E upper, boolean lowerExclusive, boolean upperExclusive, @Nonnull Comparator<E> comparison)
	{
		this.lower = lower;
		this.upper = upper;
		this.lowerExclusive = lowerExclusive;
		this.upperExclusive = upperExclusive;
		this.comparison = requireNonNull(comparison);
	}
	
	
	
	public static <E> Predicate<E> boundsPredicate(E lower, boolean lowerExclusive, E upper, boolean upperExclusive)
	{
		return boundsPredicate(lower, lowerExclusive, upper, upperExclusive, (Comparator)Comparator.naturalOrder());
	}
	
	public static <E> Predicate<E> boundsPredicate(E lower, boolean lowerExclusive, E upper, boolean upperExclusive,  @Nonnull Comparator<E> comparison)
	{
		if (lower == null)
		{
			if (upper == null)
			{
				return AlwaysTruePredicate.I;
			}
			else
			{
				//lowerExclusive is unused if infinite X3
				if (upperExclusive)
				{
					//Todo check if upper is the largest value and return AlwaysFalse if so!
				}
				else
				{
					//Todo check if upper is the largest value and return ExactlyThis if so!
				}
			}
		}
		else
		{
			if (upper == null)
			{
				//upperExclusive is unused if infinite X3
				if (lowerExclusive)
				{
					//Todo check if lower is the smallest value and return AlwaysFalse if so!
				}
				else
				{
					//Todo check if lower is the smallest value and return ExactlyThis if so!
				}
			}
			else
			{
				//Todo check for largest/smallest values here too ^^'
				
				if (comparison.compare(lower, upper) > 0)
					throw new IllegalArgumentException("Lower bound > Upper bound!!  (use AlwaysFalse to encode empty bounds!)");
				
				if (lowerExclusive)
				{
					if (upperExclusive)
					{
						//Todo if consecutive values, return AlwaysFalse
						if (comparison.compare(lower, upper) == 0)
							return AlwaysFalsePredicate.I;
					}
					else
					{
						//Todo if consecutive values, return ExactlyThis
						if (comparison.compare(lower, upper) == 0)
							return AlwaysFalsePredicate.I;
					}
				}
				else
				{
					if (upperExclusive)
					{
						//Todo if consecutive values, return ExactlyThis
						if (comparison.compare(lower, upper) == 0)
							return AlwaysFalsePredicate.I;
					}
					else
					{
						if (comparison.compare(lower, upper) == 0)
							return new ExactlyThisPredicate<E>(arbitrary(lower, upper), FunctionalUtilities.equalityFromComparison(comparison));
					}
				}
			}
		}
		
		
		return new BoundsPredicate<E>(lower, upper, lowerExclusive, upperExclusive, comparison);
	}
	
	
	
	
	public static <E> Predicate<E> inlowerInupper(E lower, E upper)
	{
		return boundsPredicate(lower, false, upper, false);
	}
	
	public static <E> Predicate<E> inlowerExupper(E lower, E upper)
	{
		return boundsPredicate(lower, false, upper, true);
	}
	
	public static <E> Predicate<E> exlowerInupper(E lower, E upper)
	{
		return boundsPredicate(lower, true, upper, false);
	}
	
	public static <E> Predicate<E> exlowerExupper(E lower, E upper)
	{
		return boundsPredicate(lower, true, upper, true);
	}
	
	
	
	public static <E> Predicate<E> greaterThan(E lower)
	{
		return boundsPredicate(lower, true, null, false);
	}
	
	public static <E> Predicate<E> greaterThanEquals(E lower)
	{
		return boundsPredicate(lower, false, null, false);
	}
	
	public static <E> Predicate<E> lessThan(E lower)
	{
		return boundsPredicate(null, false, lower, true);
	}
	
	public static <E> Predicate<E> lessThanEquals(E lower)
	{
		return boundsPredicate(null, false, lower, false);
	}
	
	
	
	
	public static <E> Predicate<E> inlowerInupper(E lower, E upper, Comparator<E> comparison)
	{
		return boundsPredicate(lower, false, upper, false, comparison);
	}
	
	public static <E> Predicate<E> inlowerExupper(E lower, E upper, Comparator<E> comparison)
	{
		return boundsPredicate(lower, false, upper, true, comparison);
	}
	
	public static <E> Predicate<E> exlowerInupper(E lower, E upper, Comparator<E> comparison)
	{
		return boundsPredicate(lower, true, upper, false, comparison);
	}
	
	public static <E> Predicate<E> exlowerExupper(E lower, E upper, Comparator<E> comparison)
	{
		return boundsPredicate(lower, true, upper, true, comparison);
	}
	
	
	
	public static <E> Predicate<E> greaterThan(E lower, Comparator<E> comparison)
	{
		return boundsPredicate(lower, true, null, false, comparison);
	}
	
	public static <E> Predicate<E> greaterThanEquals(E lower, Comparator<E> comparison)
	{
		return boundsPredicate(lower, false, null, false, comparison);
	}
	
	public static <E> Predicate<E> lessThan(E lower, Comparator<E> comparison)
	{
		return boundsPredicate(null, false, lower, true, comparison);
	}
	
	public static <E> Predicate<E> lessThanEquals(E lower, Comparator<E> comparison)
	{
		return boundsPredicate(null, false, lower, false, comparison);
	}
	
	
	
	
	
	
	
	
	
	public @Nullable E getLower()
	{
		return lower;
	}
	
	public @Nullable E getUpper()
	{
		return upper;
	}
	
	public boolean isLowerExclusive()
	{
		return lowerExclusive;
	}
	
	public boolean isUpperExclusive()
	{
		return upperExclusive;
	}
	
	public boolean isLowerInfinite()
	{
		return getLower() == null;
	}
	
	public boolean isUpperInfinite()
	{
		return getUpper() == null;
	}
	
	public @Nonnull Comparator<E> getComparison()
	{
		return comparison;
	}
	
	
	
	
	
	@Override
	public boolean test(E t)
	{
		if (!isLowerInfinite())
		{
			int c = comparison.compare(t, lower);
			
			if (isLowerExclusive())
			{
				if (c <= 0)  //t <= lower
					return false;
			}
			else
			{
				if (c < 0)  //t < lower
					return false;
			}
		}
		
		if (!isUpperInfinite())
		{
			int c = comparison.compare(t, upper);
			
			if (isUpperExclusive())
			{
				if (c >= 0)  //t >= upper
					return false;
			}
			else
			{
				if (c > 0)  //t > upper
					return false;
			}
		}
		
		return true;
	}
}
