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
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.functional.ReturnsVarargs;

//TODO replace these with more generic ones about escaping references and such??

/**
 * If the varargs array reference escapes from the method!
 * (eg, passed to another method whose escapedness is not known)
 * See {@link NotEscapedVarargs} for opposite :>
 * See {@link ReturnsVarargs} for a more informational version of this :>
 * 
 * Escapedness is orthogonal to Readonlyness ({@link ReadonlyValue}/{@link WritableValue})
 * Readonlyness means whether it's written to.
 * Escapedness means whether the array reference gets out of the method.
 * They can come in any combination :>
 * 
 * Oh and remember, {@link SafeVarargs} refers to a Java generic types vs. Array component type dilemma X3
 * :>
 * 
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface EscapesVarargs
{
}
