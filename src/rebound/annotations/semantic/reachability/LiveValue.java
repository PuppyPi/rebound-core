/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.reachability;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//TODO replace these with more generic ones about escaping references and such??

/**
 * Indicates this is a live view of something else (eg, the input parameter),
 * as opposed to a snapshot value!
 * 
 * Ie, if the underlying thing changes, the live view will appear to change as well!
 * And if the live view is modified, the underlying value may be modified as well!!
 * 
 * 
 * 
 * Examples! :D
 * 
 * <code>
 * 	@{@link LiveValue}
 * 	public static List tolist(Object[] array)
 * 	{
 * 		return Arrays.asList(array);
 * 	}
 * </code>
 * 
 * <code>
 * 	@{@link SnapshotValue}
 * 	public static List tolist(Object[] array)
 * 	{
 * 		ArrayList l = new ArrayList();
 * 		l.addAll(Arrays.asList(array));
 * 		return l;
 * 	}
 * </code>
 * 
 * @see SnapshotValue (the opposite! ^_^ )
 * @see PossiblySnapshotPossiblyLiveValue (the unknown! ^_^ )
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.FIELD, ElementType.METHOD})
public @interface LiveValue
{
	int[] depth() default {1};
}
