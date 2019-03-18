/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.allowedoperations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import rebound.annotations.semantic.reachability.ThrowAwayValue;

/**
 * This specifies that the *value* is not to be written to or modified,
 * as opposed to the container (variable/field) that holds it,
 * wherein "final" applies ^_^
 * 
 * Classic example: an array.  :>
 * Say a method returns or accepts and array, or a class has an array field.
 * This specifies that it's not to be written to, but only read from! :>
 * 
 * 
 * Application:
 * 
 * If this is applied to a field or local variable (a container), it means its
 * contents is to be readonly.  It doesn't need to apply to primitives, since those
 * are already *immutable* :>
 * 
 * If this is applied to a method parameter (an input), it means the method
 * implementation is not to write to the object in the parameter :>
 * (if applied to a varargs parameter, *it means the varargs array* not the values inside it!)
 * (this replaces earlier annotations about the varargs array being readonly or not ^_^ )
 * 
 * If applied to a method, *it means the return value*! (an output :> )
 * And that no users/callers of the method are to write to that value :>
 * 
 * 
 * This can be used (esp. with arrays!) to make higher-performance, albeit more
 * dangerous and programmer-trusting systems clearer..and consequently perhaps a bit
 * less dangerous xD'
 * 
 * 
 * The depth parameter is a verrry basic/crude way of specifying how far down to go
 * in the object graph this applies :>
 * 
 * Depth	Meaning
 * ----------------
 * 0		'final' (for fields) XD
 * 1		the value object's internal data itself
 * 2		values referenced by non-primitive properties/fields in the value object (eg, the members of it if it's a list or array)  :>
 * ...		etc...
 * -1		alllll the rest of the depths XD :>
 * 
 * Specify more than one value to specify this applies at more than one level! ^_^
 * 
 * 
 * :>
 * 
 * @see WritableValue (the opposite! ^_^ )
 * @see ThrowAwayValue
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadonlyValue
{
	int[] depth() default {1};
}
