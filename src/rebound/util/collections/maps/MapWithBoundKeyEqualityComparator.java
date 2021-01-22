/*
 * Created on Feb 25, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections.maps;

import rebound.util.functional.EqualityComparator;

public interface MapWithBoundKeyEqualityComparator<K>
{
	public EqualityComparator<K> getKeyEqualityComparator();
	
	public interface MapWithChangeableBoundKeyEqualityComparator<K>
	{
		public void setKeyEqualityComparator(EqualityComparator<K> equalityComparator);
	}
}
