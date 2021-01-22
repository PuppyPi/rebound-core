/*
 * Created on Feb 25, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections;

import rebound.util.functional.EqualityComparator;

public interface CollectionWithBoundEqualityComparator<E>
{
	public EqualityComparator<E> getEqualityComparator();
	
	
	public interface CollectionWithChangeableBoundEqualityComparator<E>
	{
		public void setEqualityComparator(EqualityComparator<E> equalityComparator);
	}
}
