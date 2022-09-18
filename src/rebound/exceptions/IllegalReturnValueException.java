/*
 * Created on Nov 3, 2010
 * 	by the great Eclipse(c)
 */
package rebound.exceptions;

/**
 * Like {@link ImpossibleException} and {@link IllegalArgumentException}, this should only
 * come up when there are flaws in code and so is a {@link RuntimeException}.
 * As the counterpart to {@link IllegalArgumentException}
 * this is thrown by the alterone code checking the return value,
 * rather than the subterone code checking the arguments.
 * @author RProgrammer
 */
public class IllegalReturnValueException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public IllegalReturnValueException()
	{
		super();
	}
	
	public IllegalReturnValueException(String message)
	{
		super(message);
	}
	
	public IllegalReturnValueException(Throwable cause)
	{
		super(cause);
	}
	
	public IllegalReturnValueException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
