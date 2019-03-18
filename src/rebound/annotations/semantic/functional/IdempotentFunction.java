/*
 * Created on
 * 	by the wonderful Eclipse(c)
 */
package rebound.annotations.semantic.functional;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies that you can recursively call a function on its output, and further calls won't change the data passing through it, such as {@link Math#abs(long)} ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
//Now with Java 8, fields and etc. can really be methods, so we removed the @Target(ElementType.METHOD) restriction! ^wwwwwww^
public @interface IdempotentFunction
{
}
