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
 * An explicit opposite of {@link EmbeddableMain}; implies {@link UnembeddableMain}
 * It does (only sometimes) call {@link System#exit(int)} in error-handling operation!!
 * 
 * It's an error for this to go along with {@link NeverReturns} because this implies {@link UnembeddableMain} but which *does* definitely return on success.
 * What it does on error is not defined; it *may* call {@link System#exit(int)}, but that's not guaranteed.  If it throws an error itself, just like {@link UnembeddableMain}, it's meant to terminate the program.
 * 
 * @see EmbeddableMain
 * @see UnembeddableMain
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface UnembeddableMainOnlyOnError
{
}
