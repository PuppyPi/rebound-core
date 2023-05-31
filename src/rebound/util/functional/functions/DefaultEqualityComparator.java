package rebound.util.functional.functions;

import java.util.Objects;
import rebound.annotations.semantic.SignalType;
import rebound.util.functional.EqualityComparator;
import rebound.util.objectutil.BasicObjectUtilities;

/**
 * Interfaces for inspecting and rearranging/reimplementing/optimizing predicate trees! :D
 * 
 * ie, {@link Object#equals(Object)} ^_^
 * 
 * @author Puppy Pie ^_^
 * @see Object#equals(Object)
 * @see Objects#equals(Object, Object)
 * @see BasicObjectUtilities#eq(Object, Object)
 */
@SignalType
public enum DefaultEqualityComparator
implements EqualityComparator
{
	I;
	
	@Override
	public boolean equals(Object a, Object b)
	{
		return Objects.equals(a, b);
	}
	
	
	public static <E> EqualityComparator<E> value()
	{
		return I;
	}
}
