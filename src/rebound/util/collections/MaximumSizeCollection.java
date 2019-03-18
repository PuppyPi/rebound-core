/*
 * Created on May 15, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections;

public interface MaximumSizeCollection
{
	/**
	 * @see FixedSizeCollection#hasFixedSize()
	 */
	public boolean hasMaximumSize();
	
	public int getMaximumSize();
}
