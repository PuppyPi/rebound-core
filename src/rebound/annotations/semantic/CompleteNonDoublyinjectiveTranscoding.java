/*
 * Created on May 19, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The highlevel and lowlevel forms are not doubly-injective, meaning multiple values in one correspond to another -- ie, information can be lost when decoding or encoding (probably not important information, but still XD ).
 * 
 * Example: in the lowlevel form there are 8 bits in a particular field and thus 8 integer values, but the last three are functionally equivalent (or the last two are Reserved), and only 5 values in the Java enum of the highlevel form.
 * If the Java enum contained Reserved0, Reserved1 or something, then it would count as {@link DoublyInjectiveTranscoding}
 * 
 * When decoding then re-encoding with a transform like this, it might be possible to end up changing the lowlevel value even if the highlevel isn't, like you just do set(get())!
 * Ie, information can be lost going from one to another.
 * 
 * @see DoublyInjectiveTranscoding
 * @see IncompleteNonDoublyinjectiveTranscoding
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface CompleteNonDoublyinjectiveTranscoding
{
}
