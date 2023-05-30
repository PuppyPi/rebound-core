/*
 * Created on Jun 13, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.functional;

/**
 * For "equality" in some unusual sense.  Perhaps "equivalence" is better, but that also already means something specific; oh well XD
 */
@FunctionalInterface
public interface AsymmetricalEqualityComparator<A, B>
{
	public boolean equals(A a, B b);
}
