package rebound.annotations.semantic.simpledata;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import rebound.math.RealInfinity;

/**
 * Something tagged with this *may* convert some primitive wrappers into equivalent wrappers of some wider types!
 * Eg, byte/short -> int/long, or float -> double
 * 
 * ^w^
 * 
 * 
 * + Note: if you only do this internally, and the normalization is unseen and functionally affects nothing (eg, the normalized value doesn't get returned), you don't have to tag it with this annotation :3
 * 
 * + Note: Float and Double infinities may also be converted to {@link RealInfinity}!  (NaN will usually produce undefined behavior or throw errors though)
 * 
 * @author RP
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface MayNormalizePrimitives
{
}
