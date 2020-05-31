/*
 * Created on May 19, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The highlevel and lowlevel forms are bijective: every valid value of one corresponds to a value in the other -- ie, information is never lost in a successful conversion from one form to the other.
 * (It may not be surjective though, meaning there may be invalid values (eg, a Java int in the highlevel form representing a 24-bit integer in the lowlevel form))
 * 
 * @see CompleteNonDoublyinjectiveTranscoding
 * @see IncompleteNonDoublyinjectiveTranscoding
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DoublyInjectiveTranscoding
{
}
