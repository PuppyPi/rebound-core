package rebound.util.functional.predicates;

import java.util.function.Predicate;

//Enums are automatically serializable ;D!!  (so long as their classname+fieldnames don't change! ^^''' )
public enum AlwaysFalsePredicate
implements Predicate
{
	I;
	
	
	public boolean test(Object t)
	{
		return false;
	}
	
	public String toString()
	{
		return "false";
	}
	
	
	public static <E> Predicate<E> value()
	{
		return I;
	}
	
	
	
	
	
	private static final long serialVersionUID = 1L;
}
