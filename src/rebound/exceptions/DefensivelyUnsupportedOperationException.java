package rebound.exceptions;

import rebound.util.collections.InfiniteIterable;

/**
 * It is actually supported..but it's probably not what you meant!!
 * Like {@link InfiniteIterable#iterator()}  XD
 * So an explicit alternative is provided for things that really do mean that :3
 */
public class DefensivelyUnsupportedOperationException
extends UnsupportedOperationException
{
	private static final long serialVersionUID = 1L;
	
	public DefensivelyUnsupportedOperationException()
	{
		super();
	}
	
	public DefensivelyUnsupportedOperationException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public DefensivelyUnsupportedOperationException(String message)
	{
		super(message);
	}
	
	public DefensivelyUnsupportedOperationException(Throwable cause)
	{
		super(cause);
	}
}
