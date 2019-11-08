/*
 * Created on Apr 14, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.simpledata;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Like {@link ActuallyUnsigned} or {@link ActuallySigned}, but for when the signedness doesn't matter XD
 * 
 * Eg, {@link OutputStream#write(int)} would be {@link ActuallyBits}(8)
 * But {@link InputStream#read()} would be {@link ActuallyUnsigned}(8) since it's upcasted without sign extension, except that it can return 257 values (-1 for EOF) so it's not really XD
 * 
 * @see ActuallySigned
 * @author RProgrammer
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.PARAMETER})
public @interface ActuallyBits
{
	/**
	 * The bitlength! :D
	 * If 0, it means 'the bitlength of the java primitive it's attached to'  :3
	 */
	int value() default 0;
}
