/*
 * Created on Jun 13, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.objectutil;

//Todo ties to grandthings ._.

/**
 * For when you don't need greater than / less than! :>
 * @author RProgrammer
 */
@FunctionalInterface
public interface EqualityComparator<T>
{
	public boolean equals(T a, T b);
}
