/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.hints;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This is something which is technically accessible (eg, public or protected),
 * but probably shouldn't be used unless you want to do something really clever >;)
 * 
 * Compare to things starting with underscores (esp. in Python!)
 * 
 * ^_^
 * 
 * 
 * @author Puppy Pie ^_^
 */
@Documented  //important for this one! XD
@Retention(RetentionPolicy.RUNTIME)
public @interface ImplementationTransparency
{
}
