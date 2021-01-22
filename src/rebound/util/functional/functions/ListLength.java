package rebound.util.functional.functions;

import java.util.List;
import rebound.util.functional.FunctionInterfaces.UnaryFunction;
import rebound.util.functional.predicates.BoundsPredicate;

/**
 * Usefully combined with {@link BoundsPredicate} :3
 */
public enum ListLength
implements UnaryFunction<List, Integer>
{
	I;
	
	
	@Override
	public Integer f(List input)
	{
		return input.size();
	}
}
