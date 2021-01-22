package rebound.util.functional.functions;

import static rebound.math.MathUtilities.*;
import static rebound.util.objectutil.BasicObjectUtilities.*;
import java.util.Comparator;
import rebound.math.MathUtilities;
import rebound.util.functional.ComparatorWithEquality;
import rebound.util.functional.EqualityComparator;

/**
 * Just like {@link Comparator#naturalOrder()} but uses {@link MathUtilities#mathcmp(Object, Object)} if they're numbers—that way (Byte)42 = (Integer)42 and etc. :3
 */
public enum DefaultComparisonNumericallyAbstract
implements ComparatorWithEquality
{
	I;
	
	@Override
	public EqualityComparator equalityComparator()
	{
		return DefaultEqualityNumericallyAbstract.I;
	}
	
	@Override
	public int compare(Object a, Object b)
	{
		return isRealNumber(a) || isRealNumber(b) ? mathcmp(a, b) : cmp2(a, b);
	}
}
