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
 * The field/property would be <code>final</code> save for eg, {@link java.io.Externalizable#readExternal(java.io.ObjectInput)} or some init() method!
 * (init() methods are (usually) initializers, just not official Java ones (ie, "constructors") :3 )
 * 
 * @author Puppy Pie ^_^
 */
@Documented  //important for this one! XD
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
{
}
