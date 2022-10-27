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
 * The method tagged with this is (subjectively) like a <code>public static void main(String[] args)</code> method (whether it has that signature/name or not)
 * But it *will not call {@link System#exit(int)}!!*
 * 
 * So it's safe to embed into other applications :>
 * 
 * (Really the only objective meaning of this is that neither it nor anything it calls will explicitly call {@link System#exit(int)})
 * (At least not any more than any code X3' )
 * 
 * Contrast with {@link UnembeddableMain}.
 * 
 * @see UnembeddableMain
 * @see UnembeddableMainOnlyOnError
 * @see NeverReturns
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface EmbeddableMain
{
}
