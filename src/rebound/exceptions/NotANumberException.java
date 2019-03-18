/*
 * Created on Jan 9, 2009
 * 	by the great Eclipse(c)
 */
package rebound.exceptions;

/**
 * You know, for when a NaN crops up..and that's not okay XD
 * 
 * @see InfinityException
 * @see NonfiniteException
 */
public class NotANumberException
extends ArithmeticException
{
	private static final long serialVersionUID = 1L;
	
	public NotANumberException()
	{
		super("Not-a-Number!");
	}
}
