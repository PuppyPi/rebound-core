/*
 * Created on
 * 	by the wonderful Eclipse(c)
 */
package rebound.annotations.semantic.temporal;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies that a procedure won't have any further effect if called after the first time, such as {@link java.io.Closeable#close()} (see {@link java.lang.AutoCloseable} for a discussion and another use of the term "idempotent" ^_^ )
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
//Now with Java 8, fields and etc. can really be methods, so we removed the @Target(ElementType.METHOD) restriction! ^wwwwwww^
public @interface IdempotentOperation
{
}
