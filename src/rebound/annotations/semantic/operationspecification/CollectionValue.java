/*
 * Created on
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.operationspecification;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates the {@link Object} should be an array, a {@link java.util.List}, a {@link java.util.Collection}, an {@link Iterable}, a {@link String}, a {@link java.lang.CharSequence}, a {@link java.nio.Buffer}, etc.c. :>
 * + Note that this doesn't speak to ordering (eg, List vs Set), duplicateness (eg, Set vs Collection), or anything about the component type!
 * + And it is *not* mutually exclusive with {@link MapValue}!  For example, a random access list can be thought of as a map with integer keys! ^^
 * 
 * @see rebound.util.collections.CollectionUtilities#toList(Object)
 * @see MapValue
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface CollectionValue
{
}
