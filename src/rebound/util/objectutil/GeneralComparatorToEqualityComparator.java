package rebound.util.objectutil;

import java.util.Comparator;
import rebound.annotations.semantic.SignalType;
import rebound.util.functional.EqualityComparator;

@SignalType
public interface GeneralComparatorToEqualityComparator<T>
extends EqualityComparator<T>
{
	public Comparator<T> getUnderlyingGeneralComparator();
}
