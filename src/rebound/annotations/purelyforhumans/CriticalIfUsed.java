/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.purelyforhumans;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * signififies that whatever is tagged with this is dangerous and code using it is {@link Critical}!
 * 
 * Critical aka Unsafe
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface CriticalIfUsed
{
}
