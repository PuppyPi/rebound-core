/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.purelyforhumans;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This code could allow security vulnerabilities if not done perfectly!!
 * (Eg, interfacing with native libraries).
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Critical
{
}
