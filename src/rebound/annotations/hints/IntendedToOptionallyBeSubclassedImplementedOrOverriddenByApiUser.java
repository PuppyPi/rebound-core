/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.hints;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * :>
 * 
 * @see IntendedToBeSubclassedImplementedOrOverriddenByApiUser
 * @author Puppy Pie ^_^
 */
@Documented  //important for this one! XD
@Retention(RetentionPolicy.RUNTIME)
public @interface IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
{
}
