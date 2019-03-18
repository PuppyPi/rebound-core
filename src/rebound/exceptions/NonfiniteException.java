/*
 * Created on
 * 	by the great Eclipse(c)
 */
package rebound.exceptions;

/**
 * You know, for when an infinity or a NaN crops up..and that's not okay XD
 * 
 * @see InfinityException
 * @see NotANumberException
 */
public class NonfiniteException
extends ArithmeticException
{
	private static final long serialVersionUID = 1L;
	
	public NonfiniteException()
	{
		super("Infinity or NaN!");
	}
}
