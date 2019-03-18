/*
 * Created on Feb 17, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.hints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Eg, if you're {@link java.io.Externalizable} and have to give a public, no-args constructor, but which should only be used if {@link java.io.Externalizable#writeExternal(java.io.ObjectOutput)} is called immediately after!!  (and you don't [yet] have an init() method to call after the no-args constructor when you're not deserializing ^^' )
 * 
 * This should be seen as a specific type of {@link ImplementationTransparency} :3
 * 
 * 
 * @author Puppy Pie ^_^
 */
@Documented  //important for this one! XD
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface ConstructorOnlyForDeserialization
{
}
