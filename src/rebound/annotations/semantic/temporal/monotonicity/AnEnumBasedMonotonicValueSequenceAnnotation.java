/*
 * Created on
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.temporal.monotonicity;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to tag an annotation like {@link MonotonicValueIntegerSequence}, {@link MonotonicValueIntegerSequence}, etc., but for your given enum class! :D
 * 
 * @see MonotonicValueGeneric
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface AnEnumBasedMonotonicValueSequenceAnnotation
{
	Class sequenceValuesType();
	String sequenceValuesAnnotationParameterName() default "value";
	String depthAnnotationParameterName() default "depth";
}
