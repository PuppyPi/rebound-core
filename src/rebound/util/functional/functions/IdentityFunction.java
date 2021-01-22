package rebound.util.functional.functions;

import rebound.util.functional.FunctionInterfaces.UnaryFunction;

public enum IdentityFunction
implements UnaryFunction
{
	I;
	
	@Override
	public Object f(Object input)
	{
		return input;
	}
}
