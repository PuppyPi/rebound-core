/*
 * Created on Jan 25, 2008
 * 	by the wonderful Eclipse(c)
 */
package rebound.concurrency.blocks;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies a method that responds to interrupts, and should return false if it was interrupted.<br>
 * @author Sean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Interruptable
{
}
