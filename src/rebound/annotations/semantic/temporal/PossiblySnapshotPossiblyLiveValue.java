/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.temporal;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.annotations.semantic.reachability.SnapshotValue;

/**
 * Either {@link SnapshotValue} or {@link LiveValue}, but no telling which!
 * (or, it's complicated to tell which! eg, based on input data!)
 * 
 * @see LiveValue
 * @see SnapshotValue
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface PossiblySnapshotPossiblyLiveValue
{
	int[] depth() default {1};
}
