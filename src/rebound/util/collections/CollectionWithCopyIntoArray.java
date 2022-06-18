package rebound.util.collections;

import java.util.Collection;
import rebound.annotations.semantic.SignalType;

@SignalType
public interface CollectionWithCopyIntoArray
{
	/**
	 * @return the number of elements copied :>    (only really useful (given you can just call {@link Collection#size()} x3 ) if it's a weak-reference collection, and the size may shrink during usage! (even in single-threaded programs!) )
	 */
	public int copyIntoArray(Object[] dest, int destStart);
}