/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.purelyforhumans;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Like {@link Deprecated}, but specifies its replacement for the simple cases where there's a specific replacement ^w^
 * 
 * (eg, {@link java.util.Timer} is @{@link DeprecatedInFavorOfClass}({@link java.util.concurrent.ScheduledThreadPoolExecutor})  ^_^ )
 * (eg, {@link java.util.Vector} is @{@link DeprecatedInFavorOf}("java.util.Collections.synchronizedList(new java.util.ArrayList())")  :> )
 * 
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DeprecatedInFavorOf
{
	String value();
}
