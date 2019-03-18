/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.hints;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Opposite; in case you want to be explicits! :D
 * (this should prolly be the default, ne? x> )
 * 
 * @see IntendedToBeSubclassedImplementedOrOverriddenByApiUser
 * @author Puppy Pie ^_^
 */
@Documented  //important for this one! XD
@Retention(RetentionPolicy.RUNTIME)
public @interface IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
{
}
