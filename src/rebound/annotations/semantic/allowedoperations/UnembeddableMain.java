/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.allowedoperations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import rebound.annotations.semantic.temporal.NeverReturns;

/**
 * Explicit opposite of {@link EmbeddableMain}
 * It does (at least sometimes) call {@link System#exit(int)} in normal or error-handling operation!!
 * 
 * If the method is also tagged with {@link NeverReturns}, then it..well, never returns XD
 * (This might be because it throws error, but that's meant to end the program)
 * 
 * @see EmbeddableMain
 * @see UnembeddableMainOnlyOnError
 * @see NeverReturns
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface UnembeddableMain
{
}
