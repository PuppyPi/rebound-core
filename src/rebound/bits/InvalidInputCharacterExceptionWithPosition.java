package rebound.bits;

import rebound.GlobalCodeMetastuffContext;

public class InvalidInputCharacterExceptionWithPosition
extends InvalidInputCharacterException
{
	private static final long serialVersionUID = 1L;
	
	
	protected final int positionInInput;
	
	public InvalidInputCharacterExceptionWithPosition(int positionInInput)
	{
		if (positionInInput < 0)
			GlobalCodeMetastuffContext.logBug();
		
		this.positionInInput = positionInInput;
	}
	
	public int getPositionInInput()
	{
		return this.positionInInput;
	}
}
