/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.hints;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This can easily be more than just a nice note!!
 * 
 * Take this example:
 * 		A method f(x) just delegates to g(x, null) in a superclass or a default method of an interface (or maybe it's abstract not default but it's expected to be Functionally Equivalent to g(x, null) anyway)
 * 		A decorator knowing this can intercept calls to both its own f(x) and g(x, y) and (for simplicity), send both to its underlying decorated object's g() method instead of sending f() calls to the f() method.
 * 		
 * 		This means that, if there's some special thing you can do in the f() method, you should NOT do that!!  (ie, it's IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser XD )
 * 		and instead, just put an if/then first thing in the implementation of g() to check if (y == null) then do the special thing.
 * 		That would probably be better anyway, since, then, people could use either method even if it wasn't decorated and not have to worry X3
 * 		But with decorators if can become *impossible* to use your super-efficient algorithm if it's stored in an override of f() instead of g()!
 * 
 * :3
 * 
 * 
 * + In Lisp/Escape-style dispatching languages, this annotation is unnecessary because a function can just be declared "non-overridable/dispatchable" and it has the same effect X3    (since there's only one kind of function: static functions and you opt them in to object-orientation instead of opting out XD )
 * 		You could use "final" for that in Java!..but that doesn't work for interface default implementations X3
 * 
 * 
 * @see IntendedToBeSubclassedImplementedOrOverriddenByApiUser
 * @author Puppy Pie ^_^
 */
@Documented  //important for this one! XD
@Retention(RetentionPolicy.RUNTIME)
public @interface IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
{
}
