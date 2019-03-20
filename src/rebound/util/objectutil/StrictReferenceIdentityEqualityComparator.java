package rebound.util.objectutil;

import rebound.annotations.semantic.SignalType;

/**
 * Interfaces for inspecting and rearranging/reimplementing/optimizing predicate trees! :D
 * 
 * @author Puppy Pie ^_^
 */
@SignalType
public enum StrictReferenceIdentityEqualityComparator
implements EqualityComparator
{
	I;
	
	@Override
	public boolean equals(Object a, Object b)
	{
		return a == b;
	}
}
