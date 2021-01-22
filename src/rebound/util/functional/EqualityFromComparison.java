package rebound.util.functional;

import static java.util.Objects.*;
import java.util.Comparator;
import javax.annotation.Nonnull;

public class EqualityFromComparison<T>
implements EqualityComparator<T>
{
	protected @Nonnull Comparator<T> comparison;
	
	public EqualityFromComparison(@Nonnull Comparator<T> comparison)
	{
		this.comparison = requireNonNull(comparison);
	}
	
	public Comparator<T> getComparison()
	{
		return comparison;
	}
	
	@Override
	public boolean equals(T a, T b)
	{
		return comparison.compare(a, b) == 0;
	}
}
