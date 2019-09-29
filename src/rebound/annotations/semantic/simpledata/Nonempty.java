/*
 * Created on Apr 14, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.simpledata;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nonnull;

/**
 * Like {@link Nonnull} but for {@link Collection}s and {@link String}s and {@link Map}s and such being empty or not :>
 * @see Emptyable
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Nonempty
{
}
