/*
 * Created on Apr 14, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.simpledata;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Eg, the double array in many methods of java.awt.geom.AffineTransform would be tagged with @{@link StaticLength}(6)  :>
 * (though that's a bad example since some cases actually accept either 4 or 6 elements ^^''   maybe we'll make this @@Repeatable someday..? :>> )
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface StaticLength
{
	int value();
}
