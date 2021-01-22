package rebound.util.functional.predicates;

import java.util.function.Predicate;

//Enums are automatically serializable ;D!!  (so long as their classname+fieldnames don't change! ^^''' )
public enum AlwaysTruePredicate
implements Predicate
{
	I;
	
	
	public boolean test(Object t)
	{
		return true;
	}
	
	public String toString()
	{
		return "true";
	}
	
	
	
	
	
	private static final long serialVersionUID = 1L;
}
