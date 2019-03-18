/*
 * Created on May 19, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.objectutil;

import javax.annotation.Nonnull;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.reachability.SnapshotValue;

/**
 * Kind of like clone(), but to copy an existing//pre-allocated instance into <code>this</code>.
 * think:
 * 		this.everyfield = source.everyfield;
 * :>
 * 
 * @author RProgrammer
 */
public interface Copyable
{
	public void setFrom(@Nonnull @ReadonlyValue @SnapshotValue Object source) throws ClassCastException;
}



//Both directions?
//
//Immutable.java: * It should be some kind of error for anything to implement {@link Copyable}<(something that implements Immutable)>
//
///**
// * The distinguishing of different directions of copying are useful (only?) when the two things are different runtime types, and one doesn't support the other.
// * @author RProgrammer
// */
//public static interface CopyIntoAble??
//{
//	public void copyInto(T existingInstance/dest);
//}
//
