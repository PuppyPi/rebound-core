/*
 * Created on
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.temporal;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For {@link OnlyOnStates} type annotations, but which allow for specifying explicitly typed values! :D!
 * 
 * @see OnlyOnStates
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface AnEnumBasedOnlyOnStatesAnnotation
{
	Class statesType();
	String sequenceValuesAnnotationParameterName() default "value";
	String depthAnnotationParameterName() default "depth";
}
