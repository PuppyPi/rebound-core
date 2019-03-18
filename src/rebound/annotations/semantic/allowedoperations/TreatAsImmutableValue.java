package rebound.annotations.semantic.allowedoperations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import rebound.annotations.semantic.reachability.LiveValue;

/**
 * Kind of the opposite of {@link LiveValue} :3
 * 
 * @author RP
 */
@Target({ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.FIELD, ElementType.METHOD})
public @interface TreatAsImmutableValue
{
}
