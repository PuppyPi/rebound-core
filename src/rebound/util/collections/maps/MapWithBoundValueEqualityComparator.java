/*
 * Created on Feb 25, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.collections.maps;

import rebound.util.functional.EqualityComparator;

public interface MapWithBoundValueEqualityComparator<V>
{
	public EqualityComparator<V> getValueEqualityComparator();
	
	public interface MapWithChangeableBoundValueEqualityComparator<V>
	{
		public void setValueEqualityComparator(EqualityComparator<V> equalityComparator);
	}
}
