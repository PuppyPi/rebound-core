package rebound.exceptions;

import rebound.exceptions.ReturnPath.SingletonReturnPath;

public class NonForwardInjectiveMapReturnPath
extends SingletonReturnPath
{
	private static final long serialVersionUID = 1L;
	
	public static final NonForwardInjectiveMapReturnPath I = new NonForwardInjectiveMapReturnPath();
	protected NonForwardInjectiveMapReturnPath() {}
	
	
	@Override
	public NonForwardInjectiveMapException toException()
	{
		return new NonForwardInjectiveMapException();
	}
}
