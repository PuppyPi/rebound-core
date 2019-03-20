/*
 * Created on May 19, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * In Java 8, interfaces can have static methods.
 * ...
 * ...
 * SO WE CAN JUST ADD A STATIC BOOLEAN METHOD TAKING AN OBJECT PARAMETER
 * AND THEN SUPPORT ARBITRARY CODE AS TRAIT PREDICATES!! :D!!!
 * 
 * 
 * + This works along with {@link TraitPredicate}--that being the mechanism by which the {@link StaticTraitPredicate} function determines if a given object has the trait ;D
 * 
 * + This must tag a static method which takes exactly one <code>java.lang.Object</code> parameter, and returns <code>boolean</code>
 * + The method is usually named "is", but that's not necessary with this annotation ^_~
 * + No more than one method per class/interface must be tagged with this :P
 * 
 * + If you're working in Java 8, <b>*all uses of {@link FunctionalityType#traitPredicate()} should use this instead!!*</b>
 * 
 * 
 * @see FunctionalityType
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface StaticTraitPredicate
{
}
