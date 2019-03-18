/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.reachability;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import rebound.annotations.semantic.allowedoperations.WritableValue;

//TODO replace these with more generic ones about escaping references and such??

/**
 * This is a special type of {@link WritableValue} / {@link SnapshotValue},
 * that means the thing is not referenced/used anywhere else,
 * and is truly just made for this specific method call/etc.,
 * so you can "mark all over" the return value and everything's fine XD
 * :>
 * 
 * @see WritableValue
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ThrowAwayValue
{
	int[] depth() default {1};
}
