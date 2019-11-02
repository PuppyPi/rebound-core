/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.allowedoperations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Explicit opposite of {@link EmbeddableMain}
 * It does (at least sometimes) call {@link System#exit(int)}!!
 * 
 * @see EmbeddableMain
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface UnembeddableMain
{
}
