/*
 * Created on Jan 27, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.temporal;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * NOTE: In Java (for practical reasons), this does NOT mean that hashCode()/equals() will act as if the thing is immutable!  Equals() may still represent identity not equivalence!
 * You're just supposed to never write to it (which may or may not actually throw an {@link Exception} if you tried), and you can rely on it never changing.
 * 
 * Applied to a 'getter' method, a field..or something similar.
 * The return value of this method may or may not change (see {@link ConstantReturnValue}),
 * but whatever is returned will be (or must be used as) an immutable object.
 * In other words, the object that's returned..is for READING ONLY!  (whether or not it actually prevents you from mutating it, like a read-only List/Map/etc.)
 * 
 * Note: some things don't really need this annotation as it's implied; like primitives, Strings, etc.
 * For other things, it is helpful to remind that any write/mutate operations will fail (eg, remove() on an Iterator)
 * And for some things it is vital to convey this information (through docs or an annotation like this), as you _can_ alter the object but are not _supposed to_ .
 * @author RProgrammer
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ImmutableValue
{
}
