/*
 * Created on Apr 14, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.simpledata;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Explicitly all three are allowed
 * 
 * Constrast with {@link Finite}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface FiniteOrInfinityOrNaN
{
}
