package rebound.annotations.semantic.simpledata;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Signed;

/**
 * Like {@link ActuallyUnsigned} but means signed :>
 * This is really only useful if you provide a non-standard {@link #value() bitlength} XD
 * Eg, for signed 24-bit integers in graphics or audio data! :D
 * (Wherein it will be sign-extended to whatever number of bits it's actually stored in :3 )
 * 
 * Note: don't rename this Signed or it will conflict with {@link Signed} X'D
 * 
 * @see ActuallyUnsigned
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.PARAMETER})
public @interface ActuallySigned
{
	/**
	 * The bitlength! :D
	 * If 0, it means 'the bitlength of the java primitive it's attached to'  :3
	 */
	int value() default 0;
}
