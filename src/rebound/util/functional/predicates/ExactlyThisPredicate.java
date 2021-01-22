package rebound.util.functional.predicates;

import static java.util.Objects.*;
import java.math.BigInteger;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.util.functional.EqualityComparator;
import rebound.util.functional.functions.DefaultEqualityComparator;

/**
 * An override for the equality is used to support, eg, unifying boxed integer types with each other and/or with {@link BigInteger}!
 * @param <E>
 */
public class ExactlyThisPredicate<E>
implements Predicate<E>
{
	protected final E value;
	protected final EqualityComparator<E> equality;
	
	public ExactlyThisPredicate(@Nullable E value)
	{
		this.value = value;
		this.equality = DefaultEqualityComparator.I;
	}
	
	public ExactlyThisPredicate(@Nullable E value, @Nonnull EqualityComparator<E> equality)
	{
		this.value = value;
		this.equality = requireNonNull(equality);
	}
	
	public E getValue()
	{
		return value;
	}
	
	public EqualityComparator<E> getEquality()
	{
		return equality;
	}
	
	
	@Override
	public boolean test(E t)
	{
		return equality.equals(t, value);
	}
}
