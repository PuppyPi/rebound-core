/*
 * Created on Mar 6, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util;

/**
 * Eg, indexOf/lastIndexOf, split/rsplit, etc. :>
 * Anything which is always forward/increasing-index or backwards/decreasing-index, but never zero (like derivatives in calculus) so only two values make sense.
 * (and an enum is of course WAY more clear than using a boolean! XD )
 * 
 * @author Puppy Pie ^_^
 */
public enum ScanDirection
{
	Forward,
	Reverse,
}
