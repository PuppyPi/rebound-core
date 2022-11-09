/*
 * Created on
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.annotations.semantic.reachability;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//TODO replace these with more generic ones about escaping references and such??

/**
 * Method of a {@link LiveValue} object.
 * 
 * Great example!:
 * {@link java.util.Map.Entry#setValue(Object)}!  ^w^
 * 
 * 
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface LiveMethod
{
}
