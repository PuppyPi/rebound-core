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
 * The opposite of {@link SignalType}, for being explicits! ^,^
 * 
 * Ie, implementing this interface or not implementing it conveys no information,
 * (the information it would represent often (but not always!) will be conveyed through a method, like {@link java.io.InputStream#markSupported()} ;> )
 * 
 * Using {@link FunctionalityType}s helps generic implementations to implement
 * tons of interfaces, such as decorators or wrappers or etc., whose actual
 * supporting of a given thing depends on the object they're delegating to!
 * 
 * (and this should prolly be the default assumption for most interfaces, most of the times ;3 )
 * 
 * + Note that {@link #traitPredicate()} and {@link #equivalentBehavior()} should never both be given!
 * 
 * + Update: We've added {@link StaticTraitPredicate} now that Java 8 supports static methods in interfaces! :D!!
 * 		So..:  If you're working in Java 8, <b>*all uses of {@link FunctionalityType#traitPredicate()} should use {@link StaticTraitPredicate} instead!!*</b>  :D!
 * 
 * @see SignalType
 * @see TraitPredicate
 * @see StaticTraitPredicate
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface FunctionalityType
{
	/**
	 * An expression for how to tell if the interface is *really* implemented ;>
	 * @see TraitPredicate
	 */
	String traitPredicate() default "";
	
	/**
	 * If there is no {@link #traitPredicate() trait predicate expression}, then just tell what to do if the interface isn't *really* implemented :P
	 */
	String equivalentBehavior() default "";
}
