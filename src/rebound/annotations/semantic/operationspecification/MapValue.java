/*
 * Created on
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.operationspecification;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates the thing should be a map-type value :3
 * + Note that this doesn't speak to keys being ordered, duplicate values being allowed (value set-ness), or anything about the component key and value types!
 * + And note that it is *not* mutually exclusive with {@link CollectionValue}!  For example, a random access list can be thought of as a map with integer keys! ^^
 * 
 * @see CollectionValue
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface MapValue
{
}
