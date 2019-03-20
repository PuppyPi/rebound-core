package rebound.util.objectutil;

import java.util.Objects;
import rebound.annotations.semantic.SignalType;

/**
 * Interfaces for inspecting and rearranging/reimplementing/optimizing predicate trees! :D
 * 
 * ie, {@link Object#equals(Object)} ^_^
 * 
 * @author Puppy Pie ^_^
 */
@SignalType
public enum NaturalEqualityComparator
implements EqualityComparator
{
	I;
	
	@Override
	public boolean equals(Object a, Object b)
	{
		return Objects.equals(a, b);
	}
}