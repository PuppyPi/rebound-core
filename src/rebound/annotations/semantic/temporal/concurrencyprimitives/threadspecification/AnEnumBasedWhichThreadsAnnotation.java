/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.temporal.concurrencyprimitives.threadspecification;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Combined with {@link AWhichThreadsAnnotation}, for which-threads type annotations
 * that explicitly use enums to specify threads! :D
 * 
 * Which require their own annotation; sorries ._.
 * 
 * But allow properly managed enums token-sets for specifying threads rather than
 * arbitrary text strings! :D!
 *
 * @see WhichThreads
 * @see AWhichThreadsAnnotation
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface AnEnumBasedWhichThreadsAnnotation
{
	Class threadSpecifierType();
}
