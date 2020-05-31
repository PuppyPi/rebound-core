/*
 * Created on
 * 	by the wonderful Eclipse(c)
 */
package rebound.annotations.semantic.temporal;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies that after a method returns (and thus doesn't throw anything), the instance is functionally equivalent to a brand new instance—ie, this transitions the instance to the state it was in after the initial constructor returned.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
//Now with Java 8, fields and etc. can really be methods, so we removed the @Target(ElementType.METHOD) restriction! ^wwwwwww^
public @interface Resets
{
}
