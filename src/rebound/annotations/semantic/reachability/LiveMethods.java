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
 * All methods are {@link LiveMethod} :3
 * 
 * @see LiveMethod
 * @author Puppy Pie ^_^
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface LiveMethods
{
}
