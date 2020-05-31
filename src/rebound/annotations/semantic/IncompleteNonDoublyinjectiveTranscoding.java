/*
 * Created on May 19, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Like {@link CompleteNonDoublyinjectiveTranscoding}, except even values that are valid according to the official specification/datasheet/docs are unencodable/decodable!
 * 
 * Ie, {@link DoublyInjectiveTranscoding} means every <i>possible</i> value is transcodable, even "unused" or "reserved" ones
 * Ie, {@link CompleteNonDoublyinjectiveTranscoding} means every <i>valid</i> value is transcodable (valid according to specs/docs).
 * Ie, {@link IncompleteNonDoublyinjectiveTranscoding} means not even all of those are transcodable (eg, it's a work-in-progress intentionally leaves out rarely used / obscure things or the author just didn't feel like doing all of it XD )
 * 
 * @see DoublyInjectiveTranscoding
 * @see CompleteNonDoublyinjectiveTranscoding
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface IncompleteNonDoublyinjectiveTranscoding
{
}
