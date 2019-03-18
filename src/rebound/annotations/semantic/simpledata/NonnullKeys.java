/*
 * Created on
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.simpledata;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Like {@link Nonnull} but for keys of a map! ^w^
 * 
 * @see Nonnull
 * @see Nullable
 * @see NonnullValues
 * @see NullableValues
 * @see NonnullKeys
 * @see NullableKeys
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface NonnullKeys
{
	int[] depth() default {1};
}
