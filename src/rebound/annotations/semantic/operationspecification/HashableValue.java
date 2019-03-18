/*
 * Created on May 18, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.operationspecification;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;

/**
 * Indicates that the [return] value of this method/field must be {@link HashableType}  ^_^
 * (compare to {@link ReadonlyValue}, but for {@link HashableType}-ness :> )
 * 
 * @see HashableType
 * @see IdentityHashableType
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface HashableValue
{
	int[] depth() default {1};
}
