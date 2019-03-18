/*
 * Created on Jan 9, 2009
 * 	by the great Eclipse(c)
 */
package rebound.exceptions;

public class DivisionByZeroException
extends ArithmeticException
{
	private static final long serialVersionUID = 1L;
	
	public DivisionByZeroException()
	{
		super("Division by zero");
	}
}
