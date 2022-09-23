/*
 * Created on Feb 27, 2007
 * 	by the wonderful Eclipse(c)
 */
package rebound.concurrency.blocks;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Signifies that a method offloads/queues its heavy parts (namely I/O)
 * Aka "asynchronous"
 * @see Blocking
 */
@Documented
@Retention(RetentionPolicy.CLASS)
public @interface Nonblocking
{
}
