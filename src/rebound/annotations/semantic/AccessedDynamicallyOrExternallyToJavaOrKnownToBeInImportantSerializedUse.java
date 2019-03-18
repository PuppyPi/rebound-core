/*
 * Created on May 19, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This shows that the given member or type is connected to things which you can't
 * possibly see by static analysis of Java code!
 * 
 * SO BE CAREFUL REFACTORING OR RENAMING! X'D!!
 * 
 * 
 * + Note: the accessor may be through Java reflection, Java {@link java.lang.invoke.MethodHandle}s, dynamically generated Java bytecode,
 * or it may be something that *may be static*, but is not in Java; eg, JNI!
 * 
 * + Note: technically, <code>public static void main(String[] args)</code> methods that are actually intended to be used to start programs are always this..but we can just leave that as understood ^_~ XD
 * 
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse
{
	String by() default "";
}
