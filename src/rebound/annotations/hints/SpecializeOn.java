/*
 * Created on
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.hints;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This hints that a method should be specialized on certain parameters annotated with this (ie, entirely different versions of the method created which have certain {@link RuntimeTypeGuaranteed known runtime types} for the parameters tagged with this, enabling massive optimizations! :D )
 * Or if this is on a field, then the entire class should be specialized on these given to the constructor (eg, the field is a Java <code>interface</code> for an algorithm like a sort algorithm, but it would be better if (some level of) compiler could automatically create specialized versions of the class that statically invoked various sort algorithms, instead of having to manually rewrite this whole class just to invoke different sort() static methods X'D  :D )
 */
@Documented
@PerformanceHint
@Retention(RetentionPolicy.RUNTIME)
public @interface SpecializeOn
{
	/**
	 * If this is not empty, this is a list of suggestions about what {@link RuntimeTypeGuaranteed runtime type[s]} the variable/parameter/field/returnvalue would likely take on :>
	 */
	Class[] value() default {};
}
