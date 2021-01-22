/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.reachability;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//TODO replace these with more generic ones about escaping references and such??

/**
 * Indicates this is a snapshot of something else (eg, the input parameter),
 * as opposed to a live value!
 * 
 * @see LiveValue (the opposite! ^_^ )
 * @see PossiblySnapshotPossiblyLiveValue (the unknown! ^_^ )
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface SnapshotValue
{
	int[] depth() default {1};
}
