/*
 * Created on Mar 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.objectutil;


/**
 * Specifies that not only does this have identity, it's supposed to be the *only*
 * thing with that identity!!  (and thus can't be cloned/duplicated!!)
 * 
 * @author Puppy Pie ^_^
 */
public interface UnduplicateableSingleton
extends StaticallyIdentityful
{
}
