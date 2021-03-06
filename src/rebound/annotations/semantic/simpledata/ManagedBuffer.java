/*
 * Created on Apr 14, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.simpledata;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.Buffer;

/**
 * The native memory WILL be free()'d (or the mmapped memory munmap()'d!) when the {@link Buffer} becomes garbage collected!
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ManagedBuffer
{
}
