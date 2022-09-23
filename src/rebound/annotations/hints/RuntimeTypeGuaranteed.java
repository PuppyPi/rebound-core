/*
 * Created on
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.hints;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This should cause a warning if the compiler can't determine that the runtime type of a field or local variable or method return value or parameter is known to be {@link #value()}.
 * If the runtime type *can* be known at compile time..hoo boy a *truckload* of optimizations are possible! 8>
 * 
 * (I guess this could tag a class but..you could just declare if "final" instead, in Java XD )
 * (Maybe it could be useful to subclass a class only for testing, but say with an annotation that it shouldn't be done in normal code? idk)
 * (Honestly the point of this class really is to let the *users* of classes expect their fields/variables will be a given runtime type so classes don't *have* to be restrictively <code>final</code> XD )
 * 
 * This enables the {@link ExplosionAllocate} and {@link Inline} optimizations *at compile time!*  :>
 * (Compile as in GraalVM compile-to-native-image or JIT compile, not compile-to-bytecode compile, that is XD' )
 */
@Documented
@PerformanceHint
@Retention(RetentionPolicy.RUNTIME)
public @interface RuntimeTypeGuaranteed
{
	Class value();
}
