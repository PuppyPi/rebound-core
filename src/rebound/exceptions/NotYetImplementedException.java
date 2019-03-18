/*
 * Created on Apr 28, 2008
 * 	by the wonderful Eclipse(c)
 */
package rebound.exceptions;

/**
 * Essentially, this exception means (and differs from {@link UnsupportedOperationException} in that) some code has been invoked that is supposed to be valid but hasn't actually been written yet.<br>
 * <br>
 * This is very valuable as a way to write all you need of a method, but not all and not have to worry too much about forgetting to implement the rest of it when you need it later.<br>
 * @author RProgrammer
 */
public class NotYetImplementedException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public NotYetImplementedException()
	{
		super();
	}
	
	public NotYetImplementedException(String message)
	{
		super(message);
	}
}
