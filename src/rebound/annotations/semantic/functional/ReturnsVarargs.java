/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.functional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.reachability.EscapesVarargs;

//Todo make more general ReturnsParameter! :D

/**
 * If the varargs array instance is *exactly* the return value of this method! :>
 * (probably usually combined with {@link WritableValue} on the varargs, because what purpose would that have other than API compatibility or something; but any combination is possible! XD )
 * Implies {@link EscapesVarargs} :>
 * 
 * If this is present with {@link EscapesVarargs}, then it means it escapes into the return value, AND elsewhere.
 * If this alone is present, it means it only escapes into the return value (which may be safer / easier to deal with :> )
 * ^_^
 * 
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ReturnsVarargs
{
}
