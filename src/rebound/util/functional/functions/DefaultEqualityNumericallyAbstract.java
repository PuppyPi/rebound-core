package rebound.util.functional.functions;

import static rebound.math.MathUtilities.*;
import static rebound.util.objectutil.BasicObjectUtilities.*;
import rebound.math.MathUtilities;
import rebound.util.functional.EqualityComparator;

/**
 * Just like {@link Object#equals(Object)} but uses {@link MathUtilities#matheq(Object, Object)} if they're numbers—that way (Byte)42 = (Integer)42 and etc. :3
 */
public enum DefaultEqualityNumericallyAbstract
implements EqualityComparator
{
	I;
	
	@Override
	public boolean equals(Object a, Object b)
	{
		return isRealNumber(a) || isRealNumber(b) ? matheq(a, b) : eq(a, b);
	}
}
