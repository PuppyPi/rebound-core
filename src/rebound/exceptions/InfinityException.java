/*
 * Created on
 * 	by the great Eclipse(c)
 */
package rebound.exceptions;

/**
 * You know, for when an infinity crops up..and that's not okay XD
 * 
 * @see NotANumberException
 * @see NonfiniteException
 */
public class InfinityException
extends ArithmeticException
{
	private static final long serialVersionUID = 1L;
	
	public InfinityException()
	{
		super("Infinity!");
	}
}
