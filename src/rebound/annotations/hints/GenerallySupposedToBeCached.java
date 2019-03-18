/*
 * Created on
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.hints;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@PerformanceHint
@Retention(RetentionPolicy.RUNTIME)
public @interface GenerallySupposedToBeCached
{
}
