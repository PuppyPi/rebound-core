/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.purelyforhumans;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @see DeprecatedInFavorOf
 * @see DeprecatedInFavorOfClass
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DeprecatedInFavorOfMember
{
	/**
	 * @return default is the class of what is being annotated!
	 */
	Class cls() default void.class;
	
	String member();
}
