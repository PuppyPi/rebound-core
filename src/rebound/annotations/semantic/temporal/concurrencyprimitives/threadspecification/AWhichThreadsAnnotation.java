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
 * This specifies that the given annotation a kind of {@link WhichThreads} annotation,
 * which allows specifying which threads/actors may access a given member/type ^w^
 * 
 * + The annotation should specify a singleton or array of threads it could be (eg, an array of some enum ;> ), unless it itself specifies exactly which thread or threads a piece of code may be called from (with no need of parameters), in which case {@link #threadsSpecifierAnnotationParameterName()} should be ""  ^_^
 * + And should specify a int[] depth() parameter :>
 * 		+ A depth of 0 means the actual field/method/instances-of-this-type
 * 		+ A depth of 1 means accessing members of the object *in* the field or return value of the method/constructor!  (or all members of the annotated Class!)
 * 		+ Etc. etc.  ^w^
 * 
 * @see WhichThreads
 * @see AnEnumBasedWhichThreadsAnnotation
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface AWhichThreadsAnnotation
{
	String threadsSpecifierAnnotationParameterName() default "value";
	String depthAnnotationParameterName() default "depth";
}
