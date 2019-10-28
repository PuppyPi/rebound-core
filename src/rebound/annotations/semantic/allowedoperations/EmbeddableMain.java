/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.allowedoperations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The method tagged with this is like a <code>public static void main(String[] args)</code> method (whether it has that signature/name or not)
 * But it *will not call System.exit()!!*
 * 
 * So it's safe to embed into other applications :>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface EmbeddableMain
{
}
