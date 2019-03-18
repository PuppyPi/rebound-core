package rebound.exceptions;

import rebound.exceptions.ReturnPath.SingletonReturnPath;

public class NonReverseInjectiveMapReturnPath
extends SingletonReturnPath
{
	private static final long serialVersionUID = 1L;
	
	public static final NonReverseInjectiveMapReturnPath I = new NonReverseInjectiveMapReturnPath();
	protected NonReverseInjectiveMapReturnPath() {}
	
	
	@Override
	public NonReverseInjectiveMapException toException()
	{
		return new NonReverseInjectiveMapException();
	}
}
