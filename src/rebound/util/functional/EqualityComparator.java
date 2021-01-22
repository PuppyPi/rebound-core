/*
 * Created on Jun 13, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.functional;

import java.util.Objects;
import rebound.util.objectutil.BasicObjectUtilities;

//Todo ties to grandfathering/grandmothering things ._.

/**
 * For when you don't need greater than / less than! :>
 * @author RProgrammer
 * @see FunctionalUtilities#equalityFromComparison(java.util.Comparator)
 * @see Object#equals(Object)
 * @see Objects#equals(Object, Object)
 * @see BasicObjectUtilities#eq(Object, Object)
 */
@FunctionalInterface
public interface EqualityComparator<T>
{
	public boolean equals(T a, T b);
}
