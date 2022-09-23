/*
 * Created on Feb 27, 2007
 * 	by the wonderful Eclipse(c)
 */
package rebound.concurrency.blocks;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Signifies that a method doesn't offload/queue its heavy parts (namely I/O), but does them right now before returning!
 * Aka "synchronous"
 * @see Nonblocking
 */
@Documented
@Retention(RetentionPolicy.CLASS)
public @interface Blocking
{
}
